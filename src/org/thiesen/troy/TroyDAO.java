/*
 * (c) Copyright 2010 Marcus Thiesen (marcus@thiesen.org)
 *
 *  This file is part of TroyORM.
 *
 *  TroyORM is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  TroyORM is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with TroyORM.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.thiesen.troy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.thiesen.troy.annotations.TroyId;
import org.thiesen.troy.annotations.TroyKey;
import org.thiesen.troy.annotations.TroyTransient;
import org.thiesen.troy.conversion.TypeConversionMap;

import sun.reflect.ReflectionFactory;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

public class TroyDAO<T> {

	public static final String ID_KEY = "_id";

	private static final Object UPDATER_LOCK = new Object();

	static class ConvertToDBObject<U> implements Function<U, DBObject> {

		private final Map<String, Field> _objectFieldsByKey;
		private TypeConversionMap _conversionMap;
		
		private ConvertToDBObject( Map<String, Field> objectFieldsByKey, TypeConversionMap conversionMap) {
			super();
			_objectFieldsByKey = objectFieldsByKey;
			_conversionMap = conversionMap;
		}

		@Override
		public DBObject apply( U value ) {
			final DBObject retval = new BasicDBObject();

			for ( final Entry<String, Field> entry : _objectFieldsByKey.entrySet() ) {
				try {
					final Class<?> type = entry.getValue().getType();
					
					retval.put( entry.getKey(), _conversionMap.convertIfPossibleFromField( type, entry.getValue().get( value ) ) );
				} catch (IllegalArgumentException e) {
					throw new RuntimeException( e );
				} catch (IllegalAccessException e) {
					throw new RuntimeException( e );
				}
			}

			return retval;
		}
		
	}
	
	private final DBCollection _collection;
	private final ImmutableMap<String, Field> _fieldsByKey;
	private final Query<T> _query;
	private final Map<Class<?>, Updater<?>> updaters = Maps.newHashMap();
	private final ConvertToDBObject<T> _convertFunction;
	private final TypeConversionMap _conversionMap;

	@SuppressWarnings("unchecked")
	TroyDAO(Class<T> clz, DBCollection collection, TypeConversionMap conversionMap )  {
		_collection = collection;

		_fieldsByKey = classToFieldMap(clz);
		_conversionMap = conversionMap;
		_convertFunction = new ConvertToDBObject<T>( _fieldsByKey, conversionMap );
		
		
		Constructor<T> objectConstructor;
		try {
			objectConstructor = clz.getConstructor();
		} catch ( NoSuchMethodException e ) {
			// silent catch, we do it the other way
			ReflectionFactory rf =
				ReflectionFactory.getReflectionFactory();
			Constructor<Object> objDef;
			try {
				objDef = Object.class.getDeclaredConstructor();
			} catch (SecurityException e2) {
				throw new RuntimeException( e2 );
			} catch (NoSuchMethodException e2) {
				throw new RuntimeException( e2 );
			}

			objectConstructor = rf.newConstructorForSerialization( clz, objDef );
		}
		
		_query = new Query<T>( collection, objectConstructor, _fieldsByKey, conversionMap );


	}

	static <T> ImmutableMap<String, Field> classToFieldMap(Class<T> clz) {
		final Field[] fields = clz.getDeclaredFields();
		final Builder<String, Field> fieldMapBuilder = ImmutableMap.<String, Field>builder(); 

		for ( final Field field : fields ) {
			if ( field.isAnnotationPresent( TroyTransient.class ) ) {
				continue;
			}

			field.setAccessible( true );
			fieldMapBuilder.put( extractKeyName( field ), field );

		}

		return fieldMapBuilder.build();
	}

	private static String extractKeyName(Field field) {
		if ( field.isAnnotationPresent( TroyKey.class ) ) {
			return field.getAnnotation( TroyKey.class ).value();
		}
		if ( field.isAnnotationPresent( TroyId.class ) ) {
			return ID_KEY;
		}

		return field.getName().replaceFirst("^_", "" );
	}

	public WriteResult insert( T... values ) {
		return _collection.insert( toDBObject( Arrays.asList( values ) ) );
	}

	public WriteResult insert( List<T> values ) {
		return _collection.insert( toDBObject( values ) );
	}

	public WriteResult update( final T value ) {
		return _collection.update( TroyUtils.getId( _fieldsByKey, value ), _convertFunction.apply( value ) );
	}

	private List<DBObject> toDBObject(List<T> values) {
		return Lists.transform( values, _convertFunction );
	}

	public Query<T> query() {
		return _query;
	}

	@SuppressWarnings("unchecked")
	public <U> Updater<U> updaterFor(Class<U> updaterClass ) {
		synchronized ( UPDATER_LOCK ) {
			if ( updaters .containsKey( updaterClass ) ) {
				return (Updater<U>) updaters.get( updaterClass );
			}
			
			final ImmutableMap<String, Field> classAsFieldMap = classToFieldMap( updaterClass );
			updaters.put( updaterClass, new Updater<U>( _collection, classAsFieldMap, new ConvertToDBObject<U>( classAsFieldMap, _conversionMap ) ) );
			
			return (Updater<U>) updaters.get( updaterClass );
		}
	}

}

/*
 * (c) Copyright 2010 Marcus Thiesen (marcus@thiesen.org)
 *
 *  This file is part of jiffs.
 *
 *  jiffs is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  jiffs is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with jiffs.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.thiesn.troy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.thiesn.troy.annotations.TroyId;
import org.thiesn.troy.annotations.TroyKey;
import org.thiesn.troy.annotations.TroyTransient;

import sun.reflect.ReflectionFactory;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

public class TroyDAO<T> {

	public static final String ID_KEY = "_id";

	private final Function<T, DBObject> TO_DB_OBJECT = new Function<T,DBObject>() {

		@Override
		public DBObject apply(T value) {
			final DBObject retval = new BasicDBObject();

			for ( final Entry<String, Field> entry : _fieldsByKey.entrySet() ) {
				try {
					retval.put( entry.getKey(), entry.getValue().get( value ) );
				} catch (IllegalArgumentException e) {
					throw new RuntimeException( e );
				} catch (IllegalAccessException e) {
					throw new RuntimeException( e );
				}
			}

			return retval;
		}

	};

	private Class<T> _clz;
	private DBCollection _collection;
	private ImmutableMap<String, Field> _fieldsByKey;
	private Query<T> _query;

	@SuppressWarnings("unchecked")
	public TroyDAO(Class<T> clz, DBCollection collection )  {
		_clz = clz;
		_collection = collection;

		final Field[] fields = _clz.getDeclaredFields();
		final Builder<String, Field> fieldMapBuilder = ImmutableMap.<String, Field>builder(); 

		for ( final Field field : fields ) {
			if ( field.isAnnotationPresent( TroyTransient.class ) ) {
				continue;
			}

			field.setAccessible( true );
			fieldMapBuilder.put( extractKeyName( field ), field );

		}

		_fieldsByKey = fieldMapBuilder.build();

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
		
		_query = new Query<T>( collection, objectConstructor, _fieldsByKey );


	}

	private String extractKeyName(Field field) {
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
		return _collection.update( getId( value ), TO_DB_OBJECT.apply( value ) );
	}

	private DBObject getId(T value) {
		return new BasicDBObject( ID_KEY, accessField( "_id", value ) );

	}

	private Object accessField( final String key, final T object ) {
		try {
			return _fieldsByKey.get( key ).get( object );

		} catch (IllegalArgumentException e) {
			throw new RuntimeException( e );
		} catch (IllegalAccessException e) {
			throw new RuntimeException( e );
		}

	}

	private List<DBObject> toDBObject(List<T> values) {
		return Lists.transform( values, TO_DB_OBJECT );
	}

	public Query<T> query() {
		return _query;
	}

}

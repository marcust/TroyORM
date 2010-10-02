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
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map.Entry;

import org.thiesen.troy.conversion.TypeConversionMap;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class Query<T> {

	private class ToObjectFunction implements Function<DBObject, T> {

		@Override
		public T apply(DBObject dbObject) {
			try {
				if ( dbObject == null ) {
					return null;
				}
				final T retval = _objectConstructor.newInstance();
				
				for ( final Entry<String, Field> entry : _fieldsByKey.entrySet() ) {
					final String key = entry.getKey();
					if ( dbObject.containsField( key ) ) {
						final Class<?> type = entry.getValue().getType();
						
						entry.getValue().set( retval , _typeConversionMap.convertIfPossibleFromDb( type, dbObject.get( key ) ) );
					} else {
						entry.getValue().set( retval, null );
					}
				}

				return retval;
				
			} catch (IllegalArgumentException e) {
				throw new RuntimeException( e );
			} catch (InstantiationException e) {
				throw new RuntimeException( e );
			} catch (IllegalAccessException e) {
				throw new RuntimeException( e );
			} catch (InvocationTargetException e) {
				throw new RuntimeException( e );
			}
		}
		
	};
	private final DBCollection _collection;
	private final Constructor<T> _objectConstructor;
	private final ImmutableMap<String, Field> _fieldsByKey;
	private final TypeConversionMap _typeConversionMap;

	Query(DBCollection collection, Constructor<T> objectConstructor, ImmutableMap<String, Field> fieldsByKey,
			TypeConversionMap conversionMap) {
		_collection = collection;
		_objectConstructor = objectConstructor;
		_fieldsByKey = fieldsByKey;
		_typeConversionMap = conversionMap; 
	}

	public Iterable<T> find( final DBObject query ) {
		final DBCursor cur = _collection.find(query);

		return Iterables.transform( cur, new ToObjectFunction() );
	}
	
	protected long count( final DBObject query ) {
		return _collection.count(query);
	}
	
	public Iterable<T> findAll() {
		final DBCursor cur = _collection.find();
		return Iterables.transform( cur, new ToObjectFunction() );
	}
	
	public long countAll() {
		return _collection.count();
	}
	
	protected T findOne( final DBObject query ) {
		return new ToObjectFunction().apply( _collection.findOne( query ) );
	}

	protected DBObject exists() {
		return exists( Boolean.TRUE );
	}

	protected DBObject doesNotExist() {
		return exists( Boolean.FALSE );
	}
	
	private DBObject exists(Boolean exists ) {
		return new BasicDBObject( "$exists", exists );
	}
	

	protected Iterable<T> findWithoutKey( final String property ) {
		return find( new BasicDBObject( property, doesNotExist() ) );
	}
	
	protected Iterable<T> findWithKey( final String property ) {
		return find( new BasicDBObject( property, exists() ) );
	}

	public T byId(String key) {
		return findOne( new BasicDBObject( TroyDAO.ID_KEY, key ) );
	}

	public Iterable<T> in(String property, List<String> values ) {
		return find( new BasicDBObject( property, new BasicDBObject( "$in", values ) ) );
	}
	
	
}



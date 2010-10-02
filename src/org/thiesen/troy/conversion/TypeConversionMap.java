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
package org.thiesen.troy.conversion;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class TypeConversionMap {

	private final Map<Class<?>,TypeConverter<?,?>> _typeMap;
	
	private final static TypeConversionMap EMPTY_MAP = new TypeConversionMap( Maps.<Class<?>, TypeConverter<?, ?>>newHashMap() );
	private final static TypeConversionMap DEFAULT_MAP = EMPTY_MAP.with( new URIConverter() );
	
	public TypeConversionMap (
			Map<Class<?>, TypeConverter<?, ?>> newMap) {
		_typeMap = ImmutableMap.<Class<?>, TypeConverter<?,?>>copyOf( newMap );
	}

	public TypeConversionMap with( TypeConverter<?, ?>... converter ) {
		final Map<Class<?>,TypeConverter<?,?>> newMap = Maps.newHashMap( _typeMap );
		for ( final TypeConverter<?, ?> conv : converter ) {
			newMap.put( conv.getFieldType(),  conv );
		}

		return new TypeConversionMap( newMap );
	}

	
	public static TypeConversionMap defaultMap() {
		return DEFAULT_MAP;
	}

	public static TypeConversionMap emptyMap() {
		return EMPTY_MAP;
	}

	public Object convertIfPossibleFromField(Class<?> type, Object value ) {
		if ( _typeMap.containsKey( type ) ) {
			 @SuppressWarnings("unchecked")
			 final TypeConverter<Object, Object> typeConverter = (TypeConverter<Object, Object>) _typeMap.get( type );
			 return typeConverter.convertFieldValue( value );
		}
		
		return value;
 	
	}

	public Object convertIfPossibleFromDb(Class<?> type, Object value ) {
		if ( _typeMap.containsKey( type ) ) {
			 @SuppressWarnings("unchecked")
			 final TypeConverter<Object, Object> typeConverter = (TypeConverter<Object, Object>) _typeMap.get( type );
			 return typeConverter.convertDatabaseValue( value );
		}
		
		return value;
	}
}

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

package org.thiesn.troy;

import java.lang.reflect.Field;

import org.thiesn.troy.TroyDAO.ConvertToDBObject;

import com.google.common.collect.ImmutableMap;
import com.mongodb.DBCollection;
import com.mongodb.WriteResult;

public class Updater<U> {

	private final DBCollection _collection;
	private final ConvertToDBObject<U> _convertFunction;
	private final ImmutableMap<String, Field> _classAsFieldMap;

	Updater( final DBCollection collection, ImmutableMap<String, Field> classAsFieldMap, ConvertToDBObject<U> convertToDBObject) {
		_collection = collection;
		_classAsFieldMap = classAsFieldMap;
		_convertFunction = convertToDBObject;
	}

	public WriteResult update( final U value ) {
		return _collection.update( TroyUtils.getId( _classAsFieldMap, value ), _convertFunction.apply( value ) );
	}

}

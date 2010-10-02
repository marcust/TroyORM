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

import java.lang.reflect.Field;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

class TroyUtils {

	private TroyUtils() {
		// do nothing, utility class
	}
	
	static DBObject getId( final Map<String, Field> fieldsByKey, Object value) {
		return new BasicDBObject( TroyDAO.ID_KEY, accessField( fieldsByKey, TroyDAO.ID_KEY, value ) );
	}

	static Object accessField( final Map<String, Field> fieldsByKey, final String key, final Object object ) {
		try {
			return fieldsByKey.get( key ).get( object );

		} catch (IllegalArgumentException e) {
			throw new RuntimeException( e );
		} catch (IllegalAccessException e) {
			throw new RuntimeException( e );
		}
	}
	
	
}

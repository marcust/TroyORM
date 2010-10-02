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

import java.net.URI;

public class URIConverter implements TypeConverter<URI, String> {

	@Override
	public Class<URI> getFieldType() {
		return URI.class;
	}

	@Override
	public Class<String> getConvertedType() {
		return String.class;
	}

	@Override
	public String convertFieldValue(URI source) {
		return source.toString();
	}

	@Override
	public URI convertDatabaseValue(String dbValue) {
		return URI.create( dbValue );
	}


}

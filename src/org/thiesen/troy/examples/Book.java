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

package org.thiesen.troy.examples;

import org.thiesen.troy.annotations.TroyCollectionName;
import org.thiesen.troy.annotations.TroyId;
import org.thiesen.troy.annotations.TroyKey;
import org.thiesen.troy.annotations.TroyTransient;

// Optional name for collection to be used, otherwise the class name will be used
@TroyCollectionName("book")
public class Book {
	
	@TroyId
	private final String _isbn;
	
	// optional mapping of field to different key in mongo
	@TroyKey("titleOfBook")
	private final String _title;
	
	// will be stored as author
	private final String _author;
	
	@TroyTransient
	private final String _dontWantToStoreThis = "foo";
	

	private Book(String isbn, String title, String author) {
		super();
		_isbn = isbn;
		_title = title;
		_author = author;
	}

	public static Book create( final String isbn, final String title, final String author ) {
		return new Book( isbn, title, author );
	}
	
	public String getIsbn() {
		return _isbn;
	}

	public String getTitle() {
		return _title;
	}

	public String getAuthor() {
		return _author;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Book [_isbn=");
		builder.append(_isbn);
		builder.append(", _title=");
		builder.append(_title);
		builder.append(", _author=");
		builder.append(_author);
		builder.append(", _dontWantToStoreThis=");
		builder.append(_dontWantToStoreThis);
		builder.append("]");
		return builder.toString();
	}
	
	
	
	
	
	
	
}

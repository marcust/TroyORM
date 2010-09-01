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

package org.thiesn.troy.examples;


class MutableBook extends Book {

	private String _isbn;
	
	private String _title;
	
	private String _author;

	public MutableBook(String isbn, String title, String author) {
		super(isbn, title, author);
	}

	@Override
	public String getIsbn() {
		return _isbn;
	}

	public void setIsbn(String isbn) {
		_isbn = isbn;
	}

	@Override
	public String getTitle() {
		return _title;
	}

	public void setTitle(String title) {
		_title = title;
	}

	@Override
	public String getAuthor() {
		return _author;
	}

	public void setAuthor(String author) {
		_author = author;
	}
	
	
	
	
	
	
	
}

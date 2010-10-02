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

import java.net.UnknownHostException;

import org.thiesen.troy.TroyDAO;
import org.thiesen.troy.TroyORMDAOFactory;

import com.mongodb.MongoException;

public class Main {

	public static void main( String... args ) throws UnknownHostException, MongoException {
		final TroyORMDAOFactory factory = TroyORMDAOFactory.create("troy-test");
		
		final TroyDAO<Book> bookDao = factory.makeDaoForClass( Book.class );
		
		final String key = "1234" + System.currentTimeMillis();
		final Book book = Book.create( key, "MongoDB & Troy", "Marcus Thiesen et al.");
		
		bookDao.insert(book);
		
		System.out.println("Created book with key " + key );
		
		final Book loadedBook = bookDao.query().byId( key );
		
		System.out.println( "Loaded " + loadedBook.toString() );
		
		final BookTitleRefresh refresh = BookTitleRefresh.create( key, "MongoDB & TroyORM" );
		
		bookDao.updaterFor( BookTitleRefresh.class ).update( refresh );
		
		System.out.println("Updated Book");
		
		final Book loadedModifiedBook = bookDao.query().byId( key );
		
		System.out.println( "Loaded Modified " + loadedModifiedBook.toString() );
		
	}
	
}

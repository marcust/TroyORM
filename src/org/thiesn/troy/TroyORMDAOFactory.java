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

import java.net.UnknownHostException;

import org.thiesn.troy.annotations.TroyCollectionName;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class TroyORMDAOFactory {

	private final static int DEFAULT_PORT = 27017;
	private final static String DEFAULT_HOST = "localhost";
	private DB _dbConnection;
	

	private TroyORMDAOFactory( final String database, final String host, final int port) throws UnknownHostException, MongoException {
		super();
		final Mongo m = new Mongo( host, port );
		
		_dbConnection = m.getDB( database );
	}
	
	public static TroyORMDAOFactory create( final String database ) throws UnknownHostException, MongoException {
		return create( database, DEFAULT_HOST, DEFAULT_PORT );
	}
	
	public static TroyORMDAOFactory create( final String database, final String host ) throws UnknownHostException, MongoException {
		return create( database, host, DEFAULT_PORT );
	}
	
	public static TroyORMDAOFactory create( final String database, final String host, final int port ) throws UnknownHostException, MongoException {
		return new TroyORMDAOFactory( database, host, port );
	}
	
    public <T> TroyDAO<T> makeDaoForClass( final Class<T> clz ) {
    	final DBCollection collection = _dbConnection.getCollection( extractCollectionName( clz ) );
    	
    	return new TroyDAO<T>( clz, collection );
    }

    private static String extractCollectionName( final Class<?> clz ) {
    	if ( clz.isAnnotationPresent( TroyCollectionName.class ) ) {
    		TroyCollectionName annotation = clz.getAnnotation(TroyCollectionName.class);
    		
    		return annotation.value();
    	}

    	final String name = clz.getSimpleName(); 
    	return Character.toLowerCase(name.charAt(0)) + (name.length() > 1 ? name.substring(1) : "" );
    }
	
	
}

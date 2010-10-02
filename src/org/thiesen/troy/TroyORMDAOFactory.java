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

import java.net.UnknownHostException;

import org.thiesen.troy.annotations.TroyCollectionName;
import org.thiesen.troy.conversion.TypeConversionMap;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class TroyORMDAOFactory {

	private final static int DEFAULT_PORT = 27017;
	private final static String DEFAULT_HOST = "localhost";
	private DB _dbConnection;
	private TypeConversionMap _conversionMap;
	
	private final static TypeConversionMap DEFAULT_CONVERSION_MAP = TypeConversionMap.defaultMap(); 

	private TroyORMDAOFactory( final String database, final String host, final int port, final TypeConversionMap conversionMap ) throws UnknownHostException, MongoException {
		super();
		final Mongo m = new Mongo( host, port );
		
		_dbConnection = m.getDB( database );
		_conversionMap = conversionMap;
	}
	
	public static TroyORMDAOFactory create( final String database ) throws UnknownHostException, MongoException {
		return create( database, DEFAULT_HOST, DEFAULT_PORT );
	}
	
	public static TroyORMDAOFactory create( final String database, final String host ) throws UnknownHostException, MongoException {
		return create( database, host, DEFAULT_PORT );
	}
	
	public static TroyORMDAOFactory create( final String database, final String host, final int port ) throws UnknownHostException, MongoException {
		return new TroyORMDAOFactory( database, host, port, DEFAULT_CONVERSION_MAP );
	}
	
	public static TroyORMDAOFactory create( final String database, final TypeConversionMap conversionMap ) throws UnknownHostException, MongoException {
		return create( database, DEFAULT_HOST, DEFAULT_PORT, conversionMap );
	}
	
	public static TroyORMDAOFactory create( final String database, final String host, final TypeConversionMap conversionMap ) throws UnknownHostException, MongoException {
		return create( database, host, DEFAULT_PORT, conversionMap );
	}
	
	public static TroyORMDAOFactory create( final String database, final String host, final int port, final TypeConversionMap conversionMap ) throws UnknownHostException, MongoException {
		return new TroyORMDAOFactory( database, host, port, conversionMap );
	}
	
	
    public <T> TroyDAO<T> makeDaoForClass( final Class<T> clz ) {
    	final DBCollection collection = _dbConnection.getCollection( extractCollectionName( clz ) );
    	
    	return new TroyDAO<T>( clz, collection, _conversionMap );
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

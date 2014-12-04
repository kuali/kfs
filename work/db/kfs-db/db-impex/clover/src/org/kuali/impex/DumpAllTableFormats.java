/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.impex;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;

public class DumpAllTableFormats {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Connection con = ETLHelper.connectToDatabase( args[0] );

		String schema = args[1];
		String destinationDirectory = args[2];
		ResultSet tables = con.getMetaData().getTables(null, schema, null, new String[] { "TABLE" } );
		
		while ( tables.next() ) {
			String tableName = tables.getString( "TABLE_NAME" );
			// filter out oracle control tables 
			// and sequences when implemented as tables (as in MySQL)
			if ( !ETLHelper.isValidTableType(tableName) ) {
				continue;
			}
			String tableFormat = DbMetadataToFormat.getFormatFile( schema.toUpperCase(), tableName, 
					DbMetadataToFormat.createFieldInfoFromMetadata(con, schema.toUpperCase(), tableName ) );
			
			File outFile = new File( destinationDirectory + "/" + tableName.toLowerCase() + ".fmt.xml" );
			System.out.println( "Writing to output file: "  + outFile.getAbsolutePath() );
			BufferedWriter out = new BufferedWriter( new FileWriter( outFile ) );
			out.write( tableFormat );
			out.close();
			
		}
		tables.close();
		con.close();
	}

}

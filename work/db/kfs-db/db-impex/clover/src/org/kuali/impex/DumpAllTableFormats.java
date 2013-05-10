/*
 * Copyright 2012 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

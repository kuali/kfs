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

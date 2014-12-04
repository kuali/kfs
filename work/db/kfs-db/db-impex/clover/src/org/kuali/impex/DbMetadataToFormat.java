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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class DbMetadataToFormat {

	
	public static void main(String[] args) throws Exception {
		if ( args.length == 0 ) {
			System.err.println( "usage: db-to-format <database.cfg file> <table schema> <table name> <format file name>" );
		}
		
		Connection con = ETLHelper.connectToDatabase( args[0] );
		
		String tableFormat = getFormatFile( args[1], args[2], createFieldInfoFromMetadata( con, args[1], args[2]) );

		con.close();
		
		System.out.println( tableFormat );
		File outFile = new File( args[3] );
		System.out.println( "Writing to output file: "  + outFile.getAbsolutePath() );
		BufferedWriter out = new BufferedWriter( new FileWriter( outFile ) );
		out.write( tableFormat );
		out.close();
	}
	
	public static List<FieldInfo> createFieldInfoFromMetadata( Connection con, String schema, String tableName ) throws Exception {
		ResultSet cols = con.getMetaData().getColumns(null, schema.toUpperCase(), tableName, null);
		List<FieldInfo> fields = new ArrayList<FieldInfo>();
		while ( cols.next() ) {
			fields.add( new FieldInfo(cols.getString( "COLUMN_NAME" ),ETLHelper.getCloverTypeFromJdbcType( cols.getInt( "DATA_TYPE" ) ), cols.getInt("ORDINAL_POSITION")));
		}
		cols.close();
		return fields;
	}
	
	
	public static String getFormatFile( String schema, String tableName, List<FieldInfo> fields ) throws SQLException {
		System.out.println( "Dumping Database Table Format: " + schema +  "." + tableName );
		
		int colCount = fields.size();
		StringBuffer sb = new StringBuffer( 2000 );		
//		sb.append( "<Record name=\"").append( tableName.toLowerCase() ).append( "\" type=\"delimited\" recordDelimiter=\"\\n\" fieldDelimiter=\"").append( ETLHelper.COLUMN_DELIMITER ).append( "\" >\n" );
		sb.append( "<Record name=\"").append( tableName.toLowerCase() ).append( "\" type=\"delimited\">\n" );
		int currCol = 0;
		for ( FieldInfo field : fields ) {
			currCol++;
			sb.append( "  " );
			//System.out.println( cols.getString( "COLUMN_NAME" ) + "-" + getCloverTypeFromJdbcType( cols.getInt( "DATA_TYPE" ) ) + "-" + cols.getBoolean( "NULLABLE" ) );
			sb.append( "<Field name=\"").append( field.getColumnName().toLowerCase() ).append( "\"" );
			for ( int i = field.getColumnName().length(); i < 31; i++ ) {
				sb.append( ' ' );
			}
			sb.append( " type=\"" ).append( field.getCloverFieldType() ).append( "\"" );
			for ( int i = field.getCloverFieldType().length(); i < 10; i++ ) {
				sb.append( ' ' );
			}
			sb.append( " nullable=\"true\" " );
			sb.append( " trim=\"false\"" );
			if ( currCol == colCount ) {	
				sb.append( " delimiter=\"|\\r\\n\"" );
			} else {
				sb.append( " delimiter=\"" ).append( ETLHelper.COLUMN_DELIMITER ).append( "\" " );				
			}
			if ( field.getCloverFieldType().equals( "date" ) ) {
				sb.append( " format=\"yyyyMMddHHmmss\"" );
			}
			
			sb.append( " />\n" );
		}
		sb.append( "</Record>" );	
		return sb.toString();
	}
	
}

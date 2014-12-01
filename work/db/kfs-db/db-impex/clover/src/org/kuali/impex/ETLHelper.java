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

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Properties;

public class ETLHelper {

	public static final String COLUMN_DELIMITER = "|";
	public static final String COLUMN_DELIMITER_REPLACEMENT = "~!~";
	
	public static Connection connectToDatabase( String propertiesFileName ) throws Exception {
		Properties dbProps = new Properties();
		Properties dumpProps = new Properties();
		dbProps.load( new FileInputStream( propertiesFileName) );
		dumpProps.load( new FileInputStream( propertiesFileName) );
		System.out.println( "Database Properties" );		
		dumpProps.remove("password");
		dumpProps.list( System.out );
		System.out.println( "Loading Driver");
		Class.forName( dbProps.getProperty("dbDriver") );
		
		System.out.println( "Connecting to Database..." );
		Connection con = DriverManager.getConnection( dbProps.getProperty("dbURL"), dbProps);
		System.out.println( "Connected" );
		return con;
	}
	
	public static String getCloverTypeFromJdbcType( int jdbcDataType ) {
		switch ( jdbcDataType ) {
			case Types.DATE :
			case Types.TIME :
			case Types.TIMESTAMP :
				return "date";
			case Types.ARRAY :
			case Types.BINARY :
			case Types.DATALINK :
			case Types.BLOB :
			case Types.DISTINCT :
			case Types.JAVA_OBJECT :
			case Types.NULL :
			case Types.OTHER :
			case Types.REF :
			case Types.STRUCT :
			case Types.VARBINARY :
			case Types.LONGVARBINARY :
				System.out.println( "Outputting cbyte for Type: " + jdbcDataType );
				return "cbyte";
			case Types.BIT :
			case Types.BOOLEAN :
				return "boolean";
			case Types.DECIMAL :
			case Types.DOUBLE :
			case Types.FLOAT :
			case Types.NUMERIC :
			case Types.REAL :
				return "numeric";
			case Types.INTEGER :
			case Types.SMALLINT :
				return "integer";
			case Types.BIGINT :
				return "long";
			case Types.CHAR :
			case Types.VARCHAR :
			case Types.CLOB :
			case Types.LONGVARCHAR :
				return "string";
		}
		System.out.println( "Outputting string for unknown Type: " + jdbcDataType );
		return "string";
	}

	/** method to filter out "special" tables seen by the JDBC driver metadata layer. */
	public static boolean isValidTableType( String tableName ) {
		return !( tableName.startsWith("MLOG$") 
				|| tableName.startsWith("RUPD$")
				|| tableName.startsWith("BIN$")
				|| tableName.endsWith("_seq")
				|| tableName.endsWith("_s")
				|| tableName.startsWith("seq_")
				|| tableName.startsWith("sequence_")
				|| tableName.endsWith("_id")
				);
	}
	
	public static boolean isTableEmpty( Connection con, String schemaName, String tableName ) {
		boolean result = false;
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT COUNT(*) FROM " + schemaName + "." + tableName );
			rs.next();
			int rowCount = rs.getInt(1);
			if ( rowCount == 0 ) {
				result = true;
			}
			rs.close();
			stmt.close();
		} catch (SQLException ex) {
			System.err.println( "Error checking table row count ("  + schemaName + "." + tableName + ") : " + ex.getMessage() );
		}
		return result;
	}
}

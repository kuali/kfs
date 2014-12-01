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
import java.util.List;


public class CreateImportGraph {




	/**
	 * @param args[0] db connection name.
	 * @param args[1] db schema name
	 * @param args[2] table name
	 * @param args[3] output graph file name
	 * @param args[4] optional true/false indicating if truncate table should be performed before the talbe import.
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("args.length=" + args.length );
		for ( int i=0; i < args.length; ++i )
		{
			System.out.println("arg["+i +"]=" + args[i]);
		}
		if ( args.length < 4 ) {
			System.err.println( "usage: gengraph <database.cfg file> <table schema> <table name> <graph file name> " );
			System.err.println( "       Optional 5th true/false parameter to indicate if truncate table should be performed before the import.  Default is no." );			
		}

		Connection con = ETLHelper.connectToDatabase( args[0] );

		boolean truncateTable = false;
		if ( args.length > 4 )
		{
			truncateTable = Boolean.parseBoolean(args[4]);
		}
		System.out.println("truncateTable=" + truncateTable );
		
		List<FieldInfo> fields = DbMetadataToFormat.createFieldInfoFromMetadata(con, args[1], args[2]);
		
		StringBuffer sb = getImportGraph( args[1], args[2], fields, "", "", false, truncateTable );

		con.close();

		System.out.println( sb );
		File outFile = new File( args[3] );
		System.out.println( "Writing to output file: "  + outFile.getAbsolutePath() );
		BufferedWriter out = new BufferedWriter( new FileWriter( outFile ) );
		out.write( sb.toString() );
		out.close();
	}

	public static StringBuffer getImportGraph( String schemaName, String tableName, List<FieldInfo> fieldInfo, String inputFormatDir, String dataDir, boolean includeDebugDump, boolean truncateTable ) throws Exception {
		System.out.println( "Dumping Database Table Import Graph: " + schemaName+  "." + tableName );
		StringBuffer sb = new StringBuffer( 2000 );
		sb.append( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" );
		sb.append( "<Graph name=\"Export " ).append( tableName ).append( "\" revision=\"1.0\">\r\n" );
		sb.append( "  <Global>\r\n" );
		sb.append( "    <Property fileURL=\"workspace.prm\" id=\"WorkspaceParameters\" />\r\n" );
		sb.append( "    <Connection dbConfig=\"${DATABASE}\" id=\"TargetDB\" type=\"JDBC\" />\r\n" );
		sb.append( "    <Metadata id=\"OutputFileFormat\" fileURL=\"${FORMAT_DIR}" ).append( inputFormatDir ).append( '/' ).append( tableName.toLowerCase() ).append( ".fmt.xml\" />\r\n" );
		sb.append( "  </Global>\r\n" );
		
		int phaseNumber = 0;
		if ( truncateTable )
		{
			sb.append("   <Phase number=\"").append(phaseNumber++).append("\">\r\n");
			sb.append("     <Node id=\"DATABASE_RUN\"\r\n");
			sb.append("           type=\"DB_EXECUTE\"\r\n");
			sb.append("           dbConnection=\"TargetDB\"\r\n");
			sb.append("           sqlQuery=\"TRUNCATE TABLE ").append(tableName).append("\" />\r\n");
			sb.append("   </Phase>\r\n");
		}

		sb.append("   <Phase number=\"").append(phaseNumber++).append("\">\r\n");	
		sb.append( "    <Node id=\"INPUT\" type=\"DATA_READER\" skipLeadingBlanks=\"false\" fileURL=\"${DATA_DIR}/" ).append( tableName.toLowerCase() ).append( ".txt\" skipFirstLine=\"true\" dataPolicy=\"strict\" charset=\"UTF-8\" quotedStrings=\"false\" trim=\"false\" treatMultipleDelimitersAsOne=\"false\" />\r\n" );
		sb.append( "    <Edge fromNode=\"INPUT:0\" toNode=\"FIX_DELIM:0\" id=\"INPUT_TO_FIX_DELIM\" metadata=\"OutputFileFormat\" />\r\n" );
		sb.append( "    <Node id=\"FIX_DELIM\" type=\"REFORMAT\">\r\n" );
		sb.append( "      <attr name=\"transform\"><![CDATA[\r\n//#TL\r\n" );
		sb.append( "    function transform() {\r\n" );
		// loop over fields
		for ( FieldInfo field : fieldInfo ) {
			if ( field.getCloverFieldType().equals("string") ) {
				sb.append( "        $0.").append( field.getColumnName().toLowerCase() )
				.append( " := replace(nvl($" ).append( field.getColumnName().toLowerCase() )
				.append( ", \"\"), \"" ).append( ETLHelper.COLUMN_DELIMITER_REPLACEMENT ).append( "\", \"" ).append( ETLHelper.COLUMN_DELIMITER ).append( "\" );\r\n" );
			} else {
				sb.append( "        $0.").append( field.getColumnName().toLowerCase() )
				.append( " := $" ).append( field.getColumnName().toLowerCase() ).append( ";\r\n" );
			}
		}
		sb.append( "   }\r\n" );
		sb.append( "      ]]></attr>\r\n" );
		sb.append( "    </Node>\r\n" );
		if ( includeDebugDump ) {
			sb.append( "    <Edge fromNode=\"FIX_DELIM:0\" toNode=\"COPY:0\" id=\"TO_COPY\"  metadata=\"OutputFileFormat\" />\r\n" );
			sb.append( "    <Node id=\"COPY\" type=\"SIMPLE_COPY\" />\r\n" );
			sb.append( "    <Edge fromNode=\"COPY:0\" toNode=\"DB_OUTPUT:0\" id=\"TO_OUTPUT\"  metadata=\"OutputFileFormat\" />\r\n" );
			sb.append( "    <Edge fromNode=\"COPY:1\" toNode=\"DEBUG_DUMP:0\" id=\"TO_DUMP\"  metadata=\"OutputFileFormat\" />\r\n" );
			sb.append( "    <Node id=\"DEBUG_DUMP\" type=\"TRASH\" debugPrint=\"false\" />\r\n" );
		} else {
			sb.append( "    <Edge fromNode=\"FIX_DELIM:0\" toNode=\"DB_OUTPUT:0\" id=\"TO_OUTPUT\"  metadata=\"OutputFileFormat\" />\r\n" );		
		}
		sb.append( "    <Node id=\"DB_OUTPUT\" type=\"KUL_DB_OUTPUT_TABLE\" metadata=\"OutputFileFormat\" dbConnection=\"TargetDB\" dbTable=\"" ).append( tableName.toLowerCase() ).append( "\" batchMode=\"true\" errorAction=\"ROLLBACK\" />\r\n" );
		sb.append( "  </Phase>\r\n" );
		sb.append( "</Graph>\r\n" );
		sb.append( "  \r\n" );

		sb.append( "  \r\n" );

		return sb;
	}

}

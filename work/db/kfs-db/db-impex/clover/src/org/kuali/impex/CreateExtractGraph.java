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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class CreateExtractGraph {




	public static void main(String[] args) throws Exception {
		if ( args.length == 0 ) {
			System.err.println( "usage: gengraph <database.cfg file> <table schema> <table name> <graph file name>" );
		}

		Connection con = ETLHelper.connectToDatabase( args[0] );

		List<FieldInfo> fields = DbMetadataToFormat.createFieldInfoFromMetadata(con, args[1], args[2]);
		String graphString = createExportGraph( con, args[1], args[2], fields, "", "", "data", "data", false, false, false );

		con.close();

		System.out.println( graphString );
		File outFile = new File( args[3] );
		BufferedWriter out = new BufferedWriter( new FileWriter( outFile ) );
		out.write( graphString );
		out.close();
		System.out.println( "Wrote to output file: "  + outFile.getAbsolutePath() );
	}

	public static String createExportGraph( Connection con, String schema, String tableName, List<FieldInfo> fieldInfo, String inputFormatDir, String outputFormatDir, String dataDir, String dumpDir, boolean addKfsFields, boolean includeMappingTransformStep, boolean includeDebugDump ) throws SQLException {
		System.out.println( "Dumping Table Export Graph for: " + schema +  "." + tableName );
		StringBuffer sb = new StringBuffer( 2000 );
		sb.append( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
		sb.append( "<Graph name=\"Export " ).append( tableName ).append( "\" revision=\"1.0\">\r\n" );
		sb.append( "  <Global>\r\n" );
		sb.append( "    <Property fileURL=\"workspace.prm\" id=\"WorkspaceParameters\" />\r\n" );
		sb.append( "    <Property id=\"WHERE_CLAUSE\" name=\"").append( tableName.toUpperCase() ).append( "_WHERE_CLAUSE\" value=\"\" />\r\n" );
		sb.append( "    <!-- points to a properties file that contains the connection parameters for the database -->\r\n" );
		sb.append( "    <Connection dbConfig=\"${DATABASE}\" id=\"SourceDB\" type=\"JDBC\" />\r\n" );
		sb.append( "    <Metadata id=\"InputFileFormat\" fileURL=\"${FORMAT_DIR}" ).append( inputFormatDir ).append( '/' ).append( tableName.toLowerCase() ).append( ".fmt.xml\" />\r\n" );
		sb.append( "    <Metadata id=\"OutputFileFormat\" fileURL=\"${FORMAT_DIR}" ).append( outputFormatDir ).append( '/' ).append( tableName.toLowerCase() ).append( ".fmt.xml\" />\r\n" );
		if ( addKfsFields ) {
			sb.append( "    <Sequence id=\"ObjId\" type=\"SIMPLE_SEQUENCE\" name=\"Object ID\" fileURL=\"${DATA_DIR}/objid.seq\" start=\"100000000\" step=\"1\" cached=\"1000\" />\r\n" );
		}
		sb.append( "  </Global>\r\n" );
		sb.append( "  <Phase number=\"0\">\r\n" );
		sb.append( "    <!-- Performs the extraction of all the data from the database. -->\r\n" );
		sb.append( "    <!-- With the default command below, the fields in the format file must be the same order as the columns in the database. -->\r\n" );
		sb.append( "    <Node dbConnection=\"SourceDB\" id=\"INPUT\" type=\"DB_INPUT_TABLE\">\r\n" );
		sb.append( "      <attr name=\"sqlQuery\">\r\n" );
		sb.append( "        SELECT * FROM " ).append(schema).append('.').append( tableName).append( "\r\n" ).append( "${" ).append( tableName.toUpperCase() ).append( "_WHERE_CLAUSE}\r\n" );
		sb.append( "      </attr>\r\n" );
		sb.append( "    </Node>\r\n" );
		sb.append( "    <Edge fromNode=\"INPUT:0\" toNode=\"SORT:0\" id=\"SORT_INPUT\" metadata=\"InputFileFormat\" />\r\n" );
		sb.append( "    <!-- sort the results by the primary key -->\r\n" );
		// get primary key fields
		ResultSet pks = con.getMetaData().getPrimaryKeys(null, schema.toUpperCase(), tableName);
		TreeMap<Integer, String> pkMap = new TreeMap<Integer, String>();
		while ( pks.next() ) {
			pkMap.put(pks.getInt("KEY_SEQ"), pks.getString("COLUMN_NAME").toLowerCase() );
		}
		pks.close();
		// check if there are any PK fields - if not, use the entire table
		if ( pkMap.isEmpty() ) {
			for ( FieldInfo field : fieldInfo ) {
				pkMap.put(field.getColumnPosition(), field.getColumnName().toLowerCase() );
			}
		}
		sb.append( "    <Node id=\"SORT\" type=\"EXT_SORT\" sortKey=\"" );
		for ( Map.Entry<Integer,String> col : pkMap.entrySet() ) {
			sb.append( col.getValue() );
			if ( !col.getKey().equals(pkMap.lastKey() ) ) {
				sb.append( ';' );
			}
		}
		sb.append( "\" />\r\n" );

		pks.close();
		if ( includeMappingTransformStep ) {
			sb.append( "    <Edge fromNode=\"SORT:0\" toNode=\"TRANSFORM_1:0\" id=\"SORT_TO_TRANSFORM_1\" metadata=\"InputFileFormat\" />\r\n" );
			sb.append( "    <!-- copy the values from the input records to the output records -->\r\n" );
			sb.append( "    <!-- Where the columns differ between the input and output, this section will need to be adapted -->\r\n" );
			sb.append( "    <Node id=\"TRANSFORM_1\" type=\"REFORMAT\">\r\n" );
			sb.append( "      <attr name=\"transform\"><![CDATA[\r\n//#TL\r\n" );
			sb.append( "    function transform() {\r\n" );
			// loop over fields
			for ( FieldInfo field : fieldInfo ) {
				if ( !addKfsFields
						|| !(field.getColumnName().equalsIgnoreCase("OBJ_ID") || field.getColumnName().equalsIgnoreCase("VER_NBR" ) ) ) {
					sb.append( "        $0.").append( field.getColumnName().toLowerCase() )
							.append( " := $" ).append( field.getColumnName().toLowerCase() ).append( ";\r\n" );
				}
			}
			if ( addKfsFields ) {
				sb.append( "        $0.obj_id := sequence(ObjId,string).next;\r\n" );
				sb.append( "        $0.ver_nbr := 1;\r\n" );
			}
			sb.append( "   }\r\n" );
			sb.append( "      ]]></attr>\r\n" );
			sb.append( "    </Node>\r\n" );
			sb.append( "    <Edge fromNode=\"TRANSFORM_1:0\" toNode=\"REPLACE_DELIMS:0\" id=\"T1_REPLACE_DELIMS\" metadata=\"OutputFileFormat\" />\r\n" );
		} else {
			sb.append( "    <Edge fromNode=\"SORT:0\" toNode=\"REPLACE_DELIMS:0\" id=\"SORT_TO_REPLACE_DELIMS\" metadata=\"OutputFileFormat\" />\r\n" );
		}
		sb.append( "    <!-- This node escapes all occurences of the column delimiter character in string columns -->\r\n" );
		sb.append( "    <!-- This section will need to be adapted to match the output record columns. -->\r\n" );
		sb.append( "    <Node id=\"REPLACE_DELIMS\" type=\"REFORMAT\">\r\n" );
		sb.append( "      <attr name=\"transform\"><![CDATA[\r\n//#TL\r\n" );
		sb.append( "    function transform() {\r\n" );
		// loop over fields
		for ( FieldInfo field : fieldInfo ) {
			if ( !addKfsFields
					|| !(field.getColumnName().equalsIgnoreCase("OBJ_ID") || field.getColumnName().equalsIgnoreCase("VER_NBR" ) ) ) {
				if ( field.getCloverFieldType().equals( "string") ) {
					sb.append( "        $0.").append( field.getColumnName().toLowerCase() )
							.append( " := replace(nvl($" ).append( field.getColumnName().toLowerCase() )
							.append( ",\"\"),\"\\\\" ).append( ETLHelper.COLUMN_DELIMITER ).append( "\", \"" ).append( ETLHelper.COLUMN_DELIMITER_REPLACEMENT ).append( "\" );\r\n" );
				} else {
					sb.append( "        $0.").append( field.getColumnName().toLowerCase() )
							.append( " := $" ).append( field.getColumnName().toLowerCase() ).append( ";\r\n" );
				}
			}
		}
		if ( addKfsFields ) {
			sb.append( "        $0.obj_id := $obj_id;\r\n" );
			sb.append( "        $0.ver_nbr := $ver_nbr;\r\n" );
		}
		sb.append( "   }\r\n" );
		sb.append( "      ]]></attr>\r\n" );
		sb.append( "    </Node>\r\n" );
		if ( includeDebugDump ) {
			sb.append( "    <Edge fromNode=\"REPLACE_DELIMS:0\" toNode=\"COPY:0\" id=\"TO_COPY\" metadata=\"OutputFileFormat\" />\r\n" );
			sb.append( "    <!-- The copy node allows the results to be passed to both the debug dump and the main output file. -->\r\n" );
			sb.append( "    <Node id=\"COPY\" type=\"SIMPLE_COPY\" />\r\n" );
			sb.append( "    <Edge fromNode=\"COPY:0\" toNode=\"DEBUG_DUMP:0\" id=\"TO_DEBUG_DUMP\" metadata=\"OutputFileFormat\" />\r\n" );
			sb.append( "    <Node id=\"DEBUG_DUMP\" type=\"TRASH\" debugPrint=\"${OUTPUT_DUMPS}\" debugFilename=\"${DUMP_DIR}/" ).append( tableName.toLowerCase() ).append( "_dump.txt\" />\r\n" );
			sb.append( "    <Edge fromNode=\"COPY:1\" toNode=\"OUTPUT:0\" id=\"TO_OUTPUT\" metadata=\"OutputFileFormat\" />\r\n" );
		} else {
			sb.append( "    <Edge fromNode=\"REPLACE_DELIMS:0\" toNode=\"OUTPUT:0\" id=\"TO_OUTPUT\" metadata=\"OutputFileFormat\" />\r\n" );
		}
		sb.append( "    <Node id=\"OUTPUT\" type=\"DATA_WRITER\" fileURL=\"${DATA_DIR}/" ).append( tableName.toLowerCase() ).append( ".txt\" outputFieldNames=\"true\" charset=\"UTF-8\" />\r\n" );
		sb.append( "  </Phase>\r\n" );
		sb.append( "</Graph>\r\n" );
		sb.append( "  \r\n" );

		sb.append( "  \r\n" );

		return sb.toString();
	}
}

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
package org.kuali.impex.ant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;

import org.apache.tools.ant.BuildException;
import org.kuali.impex.DbMetadataToFormat;
import org.kuali.impex.ETLHelper;


public class CreateTableFormatsTask extends EtlJdbcTask {

	private boolean createIfTableEmpty = true;
	
	@Override
	public void execute() throws BuildException {
		Connection con = getConnection();
		File formatDir = new File( getExportBaseDir(), getFormatDir() );
		formatDir.mkdirs();
		try {
			ResultSet tables = con.getMetaData().getTables(null, getSchemaName().toUpperCase(), getTableName(), new String[] { "TABLE" } );
			
			while ( tables.next() ) {
				String tableName = tables.getString( "TABLE_NAME" );
				// filter out oracle control tables 
				// and sequences when implemented as tables (as in MySQL)
				if ( !ETLHelper.isValidTableType(tableName) ) {
					continue;
				}
				if ( !isCreateIfTableEmpty() && ETLHelper.isTableEmpty(con, getSchemaName(), tableName) ) {
					continue;
				}
				String tableFormat = DbMetadataToFormat.getFormatFile( getSchemaName().toUpperCase(), tableName, 
						DbMetadataToFormat.createFieldInfoFromMetadata(con, getSchemaName().toUpperCase(), tableName ) );
				
				File outFile = new File( formatDir, tableName.toLowerCase() + ".fmt.xml" );
				log( "Writing to output file: "  + outFile.getAbsolutePath() );
				BufferedWriter out = new BufferedWriter( new FileWriter( outFile ) );
				out.write( tableFormat );
				out.close();
			}
			tables.close();
		} catch ( Exception ex ) {
			throw new BuildException( ex );
		}
	}

	/**
	 * @return the createIfTableEmpty
	 */
	public boolean isCreateIfTableEmpty() {
		return createIfTableEmpty;
	}

	/**
	 * @param createIfTableEmpty the createIfTableEmpty to set
	 */
	public void setCreateIfTableEmpty(boolean createIfTableEmpty) {
		this.createIfTableEmpty = createIfTableEmpty;
	}
}

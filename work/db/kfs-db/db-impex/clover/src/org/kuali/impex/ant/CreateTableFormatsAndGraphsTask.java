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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.kuali.impex.CreateExtractGraph;
import org.kuali.impex.CreateImportGraph;
import org.kuali.impex.DbMetadataToFormat;
import org.kuali.impex.ETLHelper;
import org.kuali.impex.FieldInfo;


public class CreateTableFormatsAndGraphsTask extends EtlJdbcTask {

	private boolean addKfsFields = false;
	private boolean includeMappingTransformStep = false;
	private boolean createIfTableEmpty = true;
	private boolean truncateTable = false;

	@Override
	public void execute() throws BuildException {
		Connection con = getConnection();
		File graphDir = new File( getExportBaseDir(), getGraphDir() );
		File formatDir = new File( getExportBaseDir(), "formats" );
		graphDir.mkdirs();
		formatDir.mkdirs();
		try {
			ResultSet tables = con.getMetaData().getTables(null, getSchemaName().toUpperCase(), getTableName(), new String[] { "TABLE" } );
			ResultSet materializedViews = con.getMetaData().getTables(null, getSchemaName().toUpperCase(), getTableName(), new String[] { "MATERIALIZED VIEW" } );
			Set<String> mvs = new HashSet<String>();
			while ( materializedViews.next() ) {
				String tableName = materializedViews.getString( "TABLE_NAME" );
				mvs.add(tableName);
			}
			log( "Found Materialized Views: " + mvs);

			while ( tables.next() ) {
				String tableName = tables.getString( "TABLE_NAME" );
				if ( mvs.contains( tableName ) ) {
					log( "Skipping materialized view: " + tableName);
					continue;
				}
				if ( !getTableNameRegexPattern().matcher( tableName ).matches() ) {
					log( "Skipping table: " + tableName);
					continue;
				}
                if ( getTableNameExcludeRegexPattern() != null && 
                        getTableNameExcludeRegexPattern().matcher( tableName ).matches() ) {
                    log( "Skipping table: " + tableName);
                    continue;
                }
				// filter out oracle control tables
				// and sequences when implemented as tables (as in MySQL)
				if ( !ETLHelper.isValidTableType(tableName) ) {
					continue;
				}
				if ( !isCreateIfTableEmpty() && ETLHelper.isTableEmpty(con, getSchemaName(), tableName) ) {
					continue;
				}
				List<FieldInfo> fields = DbMetadataToFormat.createFieldInfoFromMetadata(con, getSchemaName(), tableName);
				String exportGraph = CreateExtractGraph.createExportGraph(con,getSchemaName().toUpperCase(), tableName, fields, getFormatDir(), getFormatDir(), getDataDir(), getDumpDir(), isAddKfsFields(), isIncludeMappingTransformStep(), isIncludeDebugDumps() );
				File outFile = new File( graphDir, tableName.toLowerCase() + "_export.grf.xml" );
				log( "Writing to output file: "  + outFile.getAbsolutePath() );
				BufferedWriter out = new BufferedWriter( new FileWriter( outFile ) );
				out.write( exportGraph );
				out.close();

				String importGraph = CreateImportGraph.getImportGraph(getSchemaName().toUpperCase(), tableName, fields, getFormatDir(), getDataDir(), isIncludeDebugDumps(), isTruncateTable() ).toString();
				outFile = new File( graphDir, tableName.toLowerCase() + "_import.grf.xml" );
				log( "Writing to output file: "  + outFile.getAbsolutePath() );
				out = new BufferedWriter( new FileWriter( outFile ) );
				out.write( importGraph );
				out.close();

				String tableFormat = DbMetadataToFormat.getFormatFile(getSchemaName().toUpperCase(), tableName, fields );

				outFile = new File( formatDir, tableName.toLowerCase() + ".fmt.xml" );
				log( "Writing to output file: "  + outFile.getAbsolutePath() );
				out = new BufferedWriter( new FileWriter( outFile ) );
				out.write( tableFormat );
				out.close();

			}
			tables.close();
		} catch ( Exception ex ) {
			throw new BuildException( ex );
		}
	}

	/**
	 * @return the addKfsFields
	 */
	public boolean isAddKfsFields() {
		return addKfsFields;
	}

	/**
	 * @param addKfsFields the addKfsFields to set
	 */
	public void setAddKfsFields(boolean addKfsFields) {
		this.addKfsFields = addKfsFields;
	}

	/**
	 * @return the includeMappingTransformStep
	 */
	public boolean isIncludeMappingTransformStep() {
		return includeMappingTransformStep;
	}

	/**
	 * @param includeMappingTransformStep the includeMappingTransformStep to set
	 */
	public void setIncludeMappingTransformStep(boolean includeMappingTransformStep) {
		this.includeMappingTransformStep = includeMappingTransformStep;
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

	public boolean isTruncateTable() {
		return truncateTable;
	}

	public void setTruncateTable(boolean truncateTable) {
		this.truncateTable = truncateTable;
	}
}

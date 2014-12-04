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
package org.kuali.impex.ant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.kuali.impex.CreateExtractGraph;
import org.kuali.impex.ETLHelper;
import org.kuali.impex.FieldInfo;


public class CreateTableExportGraphsTask extends EtlJdbcTask {
	
	private boolean addKfsFields = false;
	private boolean includeMappingTransformStep = false;
	private boolean createIfTableEmpty = true;
	
	@Override
	public void execute() throws BuildException {
		Connection con = getConnection();
		File graphDir = new File( getExportBaseDir(), getGraphDir() ); 
		graphDir.mkdirs();
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
				ResultSet cols = con.getMetaData().getColumns(null, getSchemaName().toUpperCase(), tableName, null);
				List<FieldInfo> fields = new ArrayList<FieldInfo>();
				while ( cols.next() ) {
					fields.add( new FieldInfo(cols.getString( "COLUMN_NAME" ),ETLHelper.getCloverTypeFromJdbcType( cols.getInt( "DATA_TYPE" ) ), cols.getInt("ORDINAL_POSITION")));
				}
				cols.close();
				String exportGraph = CreateExtractGraph.createExportGraph(con, getSchemaName().toUpperCase(), tableName, fields, getFormatDir(), getFormatDir(), getDataDir(), getDumpDir(), isAddKfsFields(), isIncludeMappingTransformStep(), isIncludeDebugDumps() );
				
				File outFile = new File( graphDir, tableName.toLowerCase() + "_export.grf.xml" );
				log( "Writing to output file: "  + outFile.getAbsolutePath() );
				BufferedWriter out = new BufferedWriter( new FileWriter( outFile ) );
				out.write( exportGraph );
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
}

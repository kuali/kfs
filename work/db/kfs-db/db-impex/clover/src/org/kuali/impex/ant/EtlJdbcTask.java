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

import java.io.File;
import java.util.regex.Pattern;

import org.apache.tools.ant.taskdefs.JDBCTask;

public class EtlJdbcTask extends JDBCTask {

	private String schemaName;
	private String tableName;
	private File exportBaseDir;
	private String formatDir = "";
	private String graphDir = "graphs";
	private String dataDir = "data";
	private String dumpDir = "dump";
	private boolean includeDebugDumps = false;
	private String tableNameRegex = ".*";
    private String tableNameExcludeRegex = "";
	private Pattern tableNameRegexPattern = Pattern.compile(tableNameRegex);
    private Pattern tableNameExcludeRegexPattern = null;

	public File getExportBaseDir() {
		return exportBaseDir;
	}
	public void setExportBaseDir(File exportBaseDir) {
		this.exportBaseDir = exportBaseDir;
	}
	public String getFormatDir() {
		return formatDir;
	}
	public void setFormatDir(String formatDir) {
		this.formatDir = formatDir;
	}
	public String getGraphDir() {
		return graphDir;
	}
	public void setGraphDir(String graphDir) {
		this.graphDir = graphDir;
	}
	public String getDataDir() {
		return dataDir;
	}
	public void setDataDir(String dataDir) {
		this.dataDir = dataDir;
	}
	public String getDumpDir() {
		return dumpDir;
	}
	public void setDumpDir(String dumpDir) {
		this.dumpDir = dumpDir;
	}
	public String getSchemaName() {
		return schemaName;
	}
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * @return the includeDebugDumps
	 */
	public boolean isIncludeDebugDumps() {
		return includeDebugDumps;
	}
	/**
	 * @param includeDebugDumps the includeDebugDumps to set
	 */
	public void setIncludeDebugDumps(boolean includeDebugDumps) {
		this.includeDebugDumps = includeDebugDumps;
	}
	public String getTableNameRegex() {
		return tableNameRegex;
	}
	public void setTableNameRegex(String tableNameRegex) {
		this.tableNameRegex = tableNameRegex;
		tableNameRegexPattern = Pattern.compile(tableNameRegex);
	}
	public Pattern getTableNameRegexPattern() {
		return tableNameRegexPattern;
	}

    public String getTableNameExcludeRegex() {
        return tableNameExcludeRegex;
    }
    public void setTableNameExcludeRegex(String tableNameExcludeRegex) {
        this.tableNameExcludeRegex = tableNameExcludeRegex;
        tableNameExcludeRegexPattern = Pattern.compile(tableNameExcludeRegex);
    }
    public Pattern getTableNameExcludeRegexPattern() {
        return tableNameExcludeRegexPattern;
    }
	
}

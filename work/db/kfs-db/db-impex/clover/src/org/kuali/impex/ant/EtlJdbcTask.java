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

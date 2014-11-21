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
package org.kuali.kfs.sys.dataaccess;

import java.util.List;

import org.kuali.rice.core.framework.persistence.platform.DatabasePlatform;

/**
 * 
 *         before testing and to read data after testing.
 */
public interface UnitTestSqlDao {
    /**
     * Run an insert, delete or update sql command
     * 
     * @param sql SQL to run
     * @return row count of rows affected
     */
    public int sqlCommand(String sql);

    /**
     * Run a select sql command
     * 
     * @param sql SQL to run
     * @return List of Map objects where the key is the column name and the value is the value
     */
    public List sqlSelect(String sql);

    /**
     * Clear the OJB cache
     * 
     */
    public void clearCache();
    
    public DatabasePlatform getDbPlatform();
}

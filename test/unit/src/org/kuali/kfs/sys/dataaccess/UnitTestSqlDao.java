/*
 * Copyright 2006 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.core.util;

import java.util.List;

import org.kuali.core.dbplatform.KualiDBPlatform;

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
    
    public KualiDBPlatform getDbPlatform();
}

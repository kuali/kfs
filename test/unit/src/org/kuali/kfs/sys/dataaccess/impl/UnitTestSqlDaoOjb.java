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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;

/**
 * NOTE: Do NOT use this code in production. It is only there for testing purposes.
 */
public class UnitTestSqlDaoOjb extends PlatformAwareDaoBaseOjb implements UnitTestSqlDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UnitTestSqlDaoOjb.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.gl.dao.UnitTestSqlDao#sqlCommand(java.lang.String)
     */
    public int sqlCommand(String sql) {
        LOG.debug("sqlCommand() started: " + sql);

        Statement stmt = null;

        try {
            Connection c = getPersistenceBroker(false).serviceConnectionManager().getConnection();
            stmt = c.createStatement();
            return stmt.executeUpdate(sql);
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to execute: " + e.getMessage());
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch (Exception e) {
                throw new RuntimeException("Unable to close connection: " + e.getMessage());
            }
        }
    }

    /**
     * Given a sql select statement, return a list that contains all the rows returned. Each item in the list will be a map. For
     * each map, the key will be the field name (uppercase) and the value will be the object that jdbc returned that has the value
     * of the field.
     */
    public List sqlSelect(String sql) {
        LOG.debug("sqlSelect() started");

        Statement stmt = null;

        try {
            Connection c = getPersistenceBroker(false).serviceConnectionManager().getConnection();
            stmt = c.createStatement();

            ResultSet rs = stmt.executeQuery(sql);
            List result = new ArrayList();
            while (rs.next()) {
                Map row = new HashMap();
                int numColumns = rs.getMetaData().getColumnCount();
                for (int i = 1; i <= numColumns; i++) {
                    row.put(rs.getMetaData().getColumnName(i).toUpperCase(), rs.getObject(i));
                }
                result.add(row);
            }
            return result;
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to execute: " + e.getMessage());
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch (Exception e) {
                throw new RuntimeException("Unable to close connection: " + e.getMessage());
            }
        }
    }

    public void clearCache() {
        LOG.debug("clearCache() started");

        getPersistenceBroker(false).clearCache();
    }
}

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
package org.kuali.kfs.sys.dataaccess.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.dataaccess.UnitTestSqlDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * NOTE: Do NOT use this code in production. It is only there for testing purposes.
 */
public class UnitTestSqlDaoOjb extends PlatformAwareDaoBaseOjb implements UnitTestSqlDao {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UnitTestSqlDaoOjb.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.gl.dao.UnitTestSqlDao#sqlCommand(java.lang.String)
     */
    public int sqlCommand(String sql) {
        LOG.debug("sqlCommand() started: " + sql);

        Statement stmt = null;

        try {
            Connection c = getPersistenceBroker(true).serviceConnectionManager().getConnection();
            stmt = c.createStatement();
            return stmt.executeUpdate(sql);
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to execute: " + sql, e);
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch (Exception e) {
                throw new RuntimeException("Unable to close connection: " + sql, e);
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
            Connection c = getPersistenceBroker(true).serviceConnectionManager().getConnection();
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
            throw new RuntimeException("Unable to execute: " + sql, e);
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch (Exception e) {
                throw new RuntimeException("Unable to close connection: " + sql, e);
            }
        }
    }

    public void clearCache() {
        LOG.debug("clearCache() started");

        getPersistenceBroker(false).clearCache();
    }
}

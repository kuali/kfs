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
package org.kuali.kfs.gl.dataaccess.impl;

import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;

/**
 * A base class to support the JDBC operations done for AccountBalance inquiries
 */
public class AccountBalanceDaoJdbcBase extends PlatformAwareDaoBaseJdbc {


    /**
     * Creates a String bounded with parantheses with count number of question marks, like this: (?, ?, ?) if count is 3. Right, for
     * creating the SQL queries
     * 
     * @param count the count of question marks
     * @return the resulting String
     */
    protected String inString(int count) {
        StringBuffer sb = new StringBuffer("(");
        for (int i = 0; i < count; i++) {
            sb.append('?');
            if (i < count - 1) {
                sb.append(',');
            }
        }
        sb.append(')');
        return sb.toString();
    }

    /**
     * Removes all cost share entries from the temporary holding table for this unique inquiry
     * 
     * @param tableName the name of the temporary table to remove cost share entries from
     * @param sessionIdColumn the name of the column in the temporary table that holds the unique id of the inquiry
     * @param sessionId the unique id of the web session of the inquiring user
     */
    protected void purgeCostShareEntries(String tableName, String sessionIdColumn, String sessionId) {
        getSimpleJdbcTemplate().update("DELETE FROM " + tableName + " WHERE " + sessionIdColumn + " = ? " + " AND EXISTS (SELECT 1 FROM CA_A21_SUB_ACCT_T a " + " WHERE a.fin_coa_cd = " + tableName + ".fin_coa_cd AND a.account_nbr = " + tableName + ".account_nbr AND a.sub_acct_nbr = " + tableName + ".sub_acct_nbr AND a.sub_acct_typ_cd = 'CS')", sessionId);
    }

    /**
     * Determines if the currently inquiring user has associated temporary pending entries in the temporary pending entry table
     * 
     * @param sessionId the unique web id of the inquiring user
     * @return true if this inquiring user has temporary pending entries, false otherwise
     */
    protected boolean hasEntriesInPendingTable(String sessionId) {
        return getSimpleJdbcTemplate().queryForInt("select count(*) as COUNT from GL_PENDING_ENTRY_MT WHERE sesid = ?", sessionId) != 0;
    }

    /**
     * Updates the fiscal year and account numbers of temporary pending entries for display
     * 
     * @param universityFiscalYear the fiscal year to update all the temporary pending entries of this inquiry to
     * @param sessionId the unique web id of the inquiring user
     */
    protected void fixPendingEntryDisplay(Integer universityFiscalYear, String sessionId) {
        getSimpleJdbcTemplate().update("update GL_PENDING_ENTRY_MT set univ_fiscal_yr = ? where SESID = ?", universityFiscalYear, sessionId);
        getSimpleJdbcTemplate().update("update GL_PENDING_ENTRY_MT set SUB_ACCT_NBR = '-----' where (SUB_ACCT_NBR is null or SUB_ACCT_NBR = '     ')");
    }

    /**
     * Deletes all entries in the temporary table for the given unique user
     * 
     * @param tableName the table name to purge data from
     * @param sessionIdColumn the name of the unique field on that table
     * @param sessionId the unique value of the inquiry; basically, the unique web session id of the inquiring user
     */
    protected void clearTempTable(String tableName, String sessionIdColumn, String sessionId) {
        getSimpleJdbcTemplate().update("DELETE from " + tableName + " WHERE " + sessionIdColumn + " = ?", sessionId);
    }
}

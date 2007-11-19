/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.gl.dao.jdbc;

import org.kuali.core.dao.jdbc.PlatformAwareDaoBaseJdbc;
import org.kuali.core.dbplatform.RawSQL;
import org.kuali.kfs.service.OptionsService;
import org.kuali.module.financial.service.UniversityDateService;

/**
 * A base class to support the JDBC operations done for AccountBalance inquiries
 */
@RawSQL
public class AccountBalanceDaoJdbcBase extends PlatformAwareDaoBaseJdbc {
    protected OptionsService optionsService;
    protected UniversityDateService universityDateService;

    public OptionsService getOptionsService() {
        return optionsService;
    }

    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }


    /**
     * Creates a String bounded with parantheses with count number of question marks, like this:
     * (?, ?, ?) if count is 3.  Right, for creating the SQL queries
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
    @RawSQL
    protected void purgeCostShareEntries(String tableName, String sessionIdColumn, String sessionId) {
        getSimpleJdbcTemplate().update("DELETE FROM " + tableName + " WHERE " + sessionIdColumn + " = ? " + " AND EXISTS (SELECT 1 FROM ca_a21_sub_acct_t a " + " WHERE a.fin_coa_cd = " + tableName + ".fin_coa_cd AND a.account_nbr = " + tableName + ".account_nbr AND a.sub_acct_nbr = " + tableName + ".sub_acct_nbr AND a.sub_acct_typ_cd = 'CS')", sessionId);
    }

    /**
     * Determines if the currently inquiring user has associated temporary pending entries in the temporary pending entry table
     * 
     * @param sessionId the unique web id of the inquiring user
     * @return true if this inquiring user has temporary pending entries, false otherwise
     */
    @RawSQL
    protected boolean hasEntriesInPendingTable(String sessionId) {
        return getSimpleJdbcTemplate().queryForInt("select count(*) as COUNT from gl_pending_entry_mt WHERE sesid = ?", sessionId) != 0;
    }

    /**
     * Updates the fiscal year and account numbers of temporary pending entries for display
     * 
     * @param universityFiscalYear the fiscal year to update all the temporary pending entries of this inquiry to
     * @param sessionId the unique web id of the inquiring user
     */
    @RawSQL
    protected void fixPendingEntryDisplay(Integer universityFiscalYear, String sessionId) {
        getSimpleJdbcTemplate().update("update GL_PENDING_ENTRY_MT set univ_fiscal_yr = ? where SESID = ?", universityFiscalYear, sessionId);
        getSimpleJdbcTemplate().update("update gl_pending_entry_mt set SUB_ACCT_NBR = '-----' where (SUB_ACCT_NBR is null or SUB_ACCT_NBR = '     ')");
    }

    /**
     * Deletes all entries in the temporary table for the given unique user
     * 
     * @param tableName the table name to purge data from
     * @param sessionIdColumn the name of the unique field on that table
     * @param sessionId the unique value of the inquiry; basically, the unique web session id of the inquiring user
     */
    @RawSQL
    protected void clearTempTable(String tableName, String sessionIdColumn, String sessionId) {
        getSimpleJdbcTemplate().update("DELETE from " + tableName + " WHERE " + sessionIdColumn + " = ?", sessionId);
    }
}

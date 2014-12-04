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
package org.kuali.kfs.module.bc.document.dataaccess.impl;

import org.kuali.kfs.module.bc.document.dataaccess.ReportDumpDao;

/**
 * JCBC implementation of ReportDumpDao
 * 
 * @see org.kuali.kfs.module.bc.document.dataaccess.ReportDumpDao
 */
public class ReportDumpDaoJdbc extends BudgetConstructionDaoJdbcBase implements ReportDumpDao {
    protected static String updateAccountDump = new String();

    public ReportDumpDaoJdbc() {
        super();

        // SQL to update account dump table from control list and sub-fund selections
        StringBuilder sqlText = new StringBuilder(500);
        sqlText.append("INSERT INTO LD_BCN_ACCT_DUMP_T\n");
        sqlText.append("SELECT ?, ctrl.univ_fiscal_yr, ctrl.fin_coa_cd, ctrl.account_nbr, ctrl.sub_acct_nbr, ctrl.ver_nbr \n");
        sqlText.append("FROM LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T pick \n");
        sqlText.append("WHERE ctrl.person_unvl_id = ? \n");
        sqlText.append("AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append("AND pick.sub_fund_grp_cd = ctrl.sel_sub_fund_grp \n");
        sqlText.append("AND pick.report_flag > 0 \n");

        updateAccountDump = sqlText.toString();
        sqlText.delete(0, sqlText.length());
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.ReportDumpDao#cleanAccountDump(java.lang.String)
     */
    public void cleanAccountDump(String principalName) {
        // clear out previous account dump data for user
        clearTempTableByUnvlId("LD_BCN_ACCT_DUMP_T", "PERSON_UNVL_ID", principalName);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.ReportDumpDao#updateAccountDump(java.lang.String)
     */
    public void updateAccountDump(String principalId) {
        cleanAccountDump(principalId);
        
        // rebuild account dump table
        getSimpleJdbcTemplate().update(updateAccountDump, principalId, principalId);
    }

}


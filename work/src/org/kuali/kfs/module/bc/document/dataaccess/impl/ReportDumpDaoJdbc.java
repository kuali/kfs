/*
 * Copyright 2008 The Kuali Foundation
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


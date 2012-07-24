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

import java.util.ArrayList;

import org.kuali.kfs.module.bc.batch.dataaccess.impl.SQLForStep;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionAccountFundingDetailReportDao;

/**
 * 
 */

public class BudgetConstructionAccountFundingDetailReportDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetConstructionAccountFundingDetailReportDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionAccountFundingDetailReportDaoJdbc.class);

    protected static ArrayList<SQLForStep> updateReportsAccountFundingDetailTable = new ArrayList<SQLForStep>(1);

    public BudgetConstructionAccountFundingDetailReportDaoJdbc() {

        /* get accounts for selected orgs and objects */
        StringBuilder sqlText = new StringBuilder(500);
        sqlText.append("INSERT INTO LD_BCN_OBJT_DUMP_T \n");
        sqlText.append(" (PERSON_UNVL_ID, ORG_FIN_COA_CD, ORG_CD, SUB_FUND_GRP_CD, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD)\n");
        sqlText.append("SELECT DISTINCT \n");
        sqlText.append(" ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_sub_fund_grp, ctrl.univ_fiscal_yr, ctrl.fin_coa_cd, \n");
        sqlText.append(" ctrl.account_nbr, ctrl.sub_acct_nbr, pick.fin_object_cd \n");
        sqlText.append("FROM LD_PNDBC_APPTFND_T af, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_OBJ_PICK_T pick \n");
        sqlText.append("WHERE ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND af.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND af.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND af.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND af.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND pick.fin_object_cd = af.fin_object_cd \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pick.select_flag > 0 \n");

        updateReportsAccountFundingDetailTable.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());
    }

    public void cleanReportsAccountFundingDetailTable(String principalName) {
        clearTempTableByUnvlId("LD_BCN_OBJT_DUMP_T", "PERSON_UNVL_ID", principalName);
    }

    public void updateReportsAccountFundingDetailTable(String principalName) {
        cleanReportsAccountFundingDetailTable(principalName);
        getSimpleJdbcTemplate().update(updateReportsAccountFundingDetailTable.get(0).getSQL(), principalName, principalName);
    }

}

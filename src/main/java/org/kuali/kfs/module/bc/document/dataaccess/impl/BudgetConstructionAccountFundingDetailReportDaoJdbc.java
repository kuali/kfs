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

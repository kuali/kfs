/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.budget.dao.jdbc;

import java.util.List;

import org.kuali.core.dbplatform.RawSQL;
import org.kuali.module.budget.dao.BudgetReportsControlListDao;

/**
 * database queries needed to get valid data for OrganizationReportSelection screen
 */
public class BudgetReportsControlListDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetReportsControlListDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationBCDocumentSearchDaoJdbc.class);

    private static String[] updateReportsControlList = new String[3];
    private static String changeFlagOrganizationAndChartOfAccountCodeSelection = new String();
    private static String updateReportsSubFundGroupSelectList = new String();
    private static String updateReportsSelectedSubFundGroupFlags = new String();

    @RawSQL
    public BudgetReportsControlListDaoJdbc() {
        // populate ld_bcn_ctrl_list_t based on a join to one of three sources
        // the choice of sources is controlled by the cl_disp parameter where
        // cl_disp=1 pending budget GL
        // cl_disp=2 monthly budgets
        // cl_disp=3 bcn appointment funding

        // get the accounts for the selected org(s)
        StringBuilder sqlText = new StringBuilder(500);
        sqlText.append("INSERT INTO LD_BCN_BUILD_CTRL_LIST01_MT \n");
        sqlText.append(" (SESID, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, \n");
        sqlText.append("  ORG_LEVEL_CD, ORG_FIN_COA_CD, ORG_CD, PULL_FLAG) \n");
        sqlText.append("SELECT ?, hier.univ_fiscal_yr, hier.fin_coa_cd, hier.account_nbr, \n");
        sqlText.append(" hier.org_level_cd, hier.org_fin_coa_cd, hier.org_cd, pull.pull_flag \n");
        sqlText.append("FROM LD_BCN_PULLUP_T pull, LD_BCN_ACCT_ORG_HIER_T hier  \n");
        sqlText.append("WHERE pull.pull_flag > 0  \n");
        sqlText.append("  AND pull.person_unvl_id = ? \n");
        sqlText.append("  AND hier.univ_fiscal_yr = ? \n");
        sqlText.append("  AND hier.org_fin_coa_cd = pull.fin_coa_cd \n");
        sqlText.append("  AND hier.org_cd = pull.org_cd \n");

        updateReportsControlList[0] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // get the list of account headers accessible to the user
        sqlText.append("INSERT INTO LD_BCN_BUILD_CTRL_LIST02_MT \n");
        sqlText.append(" (SESID, PERSON_UNVL_ID, FDOC_NBR, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, \n");
        sqlText.append("  SUB_ACCT_NBR, HIER_ORG_LVL_CD, SEL_ORG_LVL_CD, SEL_ORG_FIN_COA, SEL_ORG_CD, SEL_PULL_FLAG) \n");
        sqlText.append("SELECT DISTINCT ?, ?, head.fdoc_nbr, head.univ_fiscal_yr, head.fin_coa_cd, head.account_nbr, \n");
        sqlText.append(" head.sub_acct_nbr, hier.org_level_cd, sel.org_level_cd, sel.org_fin_coa_cd, sel.org_cd, sel.pull_flag \n");
        sqlText.append("FROM LD_BCN_ACCT_ORG_HIER_T hier, LD_BCNSTR_HDR_T head, LD_BCN_BUILD_CTRL_LIST01_MT sel  \n");
        sqlText.append("WHERE hier.org_fin_coa_cd = ?  \n");
        sqlText.append("  AND hier.org_cd = ? \n");
        sqlText.append("  AND hier.univ_fiscal_yr = sel.univ_fiscal_yr \n");
        sqlText.append("  AND hier.fin_coa_cd = sel.fin_coa_cd \n");
        sqlText.append("  AND hier.account_nbr = sel.account_nbr \n");
        sqlText.append("  AND head.org_level_cd <= hier.org_level_cd \n");
        sqlText.append("  AND hier.univ_fiscal_yr = head.univ_fiscal_yr \n");
        sqlText.append("  AND hier.fin_coa_cd = head.fin_coa_cd \n");
        sqlText.append("  AND hier.account_nbr = head.account_nbr \n");
        sqlText.append("  AND sel.sesid = ? \n");

        updateReportsControlList[1] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // now insert based on the desired view
        sqlText.append("insert into LD_BCN_CTRL_LIST_T \n");
        sqlText.append(" (PERSON_UNVL_ID, FDOC_NBR, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, \n");
        sqlText.append("  HIER_ORG_LVL_CD, SEL_ORG_LVL_CD, SEL_ORG_FIN_COA, SEL_ORG_CD, SEL_PULL_FLAG, SEL_SUB_FUND_GRP) \n");
        sqlText.append("select ctrl.person_unvl_id, ctrl.fdoc_nbr, ctrl.univ_fiscal_yr, ctrl.fin_coa_cd, ctrl.account_nbr, ctrl.sub_acct_nbr, \n");
        sqlText.append(" ctrl.hier_org_lvl_cd, ctrl.sel_org_lvl_cd, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_pull_flag, acct.sub_fund_grp_cd \n");
        sqlText.append("from LD_BCN_BUILD_CTRL_LIST02_MT ctrl, CA_ACCOUNT_T acct \n");
        sqlText.append("where ctrl.sesid = ? \n");
        sqlText.append("  AND acct.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append("  AND acct.account_nbr = ctrl.account_nbr \n");
        sqlText.append("  and exists \n");
        sqlText.append("  (select * from LD_PND_BCNSTR_GL_T pbgl \n");
        sqlText.append("where pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append("  and pbgl.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append("  and pbgl.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append("  and pbgl.account_nbr = ctrl.account_nbr \n");
        sqlText.append("  and pbgl.sub_acct_nbr = ctrl.sub_acct_nbr) \n");

        updateReportsControlList[2] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // change flag in LD_BCN_PULLUP_T
        sqlText.append("UPDATE ld_bcn_pullup_t \n");
        sqlText.append("SET pull_flag = 1 \n");
        sqlText.append("WHERE person_unvl_id = ? \n");
        sqlText.append("  AND fin_coa_cd = ? \n");
        sqlText.append("  AND org_cd = ? \n");

        changeFlagOrganizationAndChartOfAccountCodeSelection = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // takes a subset of the control table to build the subfund pick list
        sqlText.append("INSERT INTO LD_BCN_SUBFUND_PICK_T (PERSON_UNVL_ID, SUB_FUND_GRP_CD, REPORT_FLAG)\n");
        sqlText.append("SELECT DISTINCT  ?, ctrl.sel_sub_fund_grp, 0 \n");
        sqlText.append("FROM LD_BCN_CTRL_LIST_T ctrl \n");
        sqlText.append("WHERE ctrl.person_unvl_id = ? \n");

        updateReportsSubFundGroupSelectList = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // change flag in UPDATE LD_BCN_SUBFUND_PICK_T
        sqlText.append("UPDATE LD_BCN_SUBFUND_PICK_T \n");
        sqlText.append("SET report_flag = 1 \n");
        sqlText.append("WHERE person_unvl_id = ? \n");
        sqlText.append("  AND sub_fund_grp_cd = ? \n");

        updateReportsSelectedSubFundGroupFlags = sqlText.toString();
    }

    /**
     * @see org.kuali.module.budget.dao.BudgetReportsControlListDao#updateReportsControlListpart1(java.lang.String,
     *      java.lang.String, java.lang.Integer)
     */
    public void updateReportsControlListpart1(String idForSession, String personUserIdentifier, Integer universityFiscalYear) {
        getSimpleJdbcTemplate().update(updateReportsControlList[0], idForSession, personUserIdentifier, universityFiscalYear);
    }

    /**
     * @see org.kuali.module.budget.dao.BudgetReportsControlListDao#updateReportsControlListpart2(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public void updateReportsControlListpart2(String idForSession, String personUserIdentifier, String chartOfAccountsCode, String organizationCode) {
        getSimpleJdbcTemplate().update(updateReportsControlList[1], idForSession, personUserIdentifier, chartOfAccountsCode, organizationCode, idForSession);
    }

    /**
     * @see org.kuali.module.budget.dao.BudgetReportsControlListDao#updateReportsControlListDisp1(java.lang.String)
     */
    public void updateReportsControlListDisp1(String idForSession) {
        getSimpleJdbcTemplate().update(updateReportsControlList[2], idForSession);
    }

    /**
     * @see org.kuali.module.budget.dao.BudgetReportsControlListDao#changeFlagOrganizationAndChartOfAccountCodeSelection(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void changeFlagOrganizationAndChartOfAccountCodeSelection(String personUserIdentifier, String chartOfAccountsCode, String organizationCode) {
        getSimpleJdbcTemplate().update(changeFlagOrganizationAndChartOfAccountCodeSelection, personUserIdentifier, chartOfAccountsCode, organizationCode);
    }

    /**
     * @see org.kuali.module.budget.dao.BudgetReportsControlListDao#updateReportsSubFundGroupSelectList(java.lang.String)
     */
    public void updateReportsSubFundGroupSelectList(String personUserIdentifier) {
        getSimpleJdbcTemplate().update(updateReportsSubFundGroupSelectList, personUserIdentifier, personUserIdentifier);
    }

    /**
     * @see org.kuali.module.budget.dao.BudgetReportsControlListDao#updateReportsSelectedSubFundGroupFlags(java.lang.String,
     *      java.lang.String)
     */
    public void updateReportsSelectedSubFundGroupFlags(String personUserIdentifier, String subfundGroupCode) {
        getSimpleJdbcTemplate().update(updateReportsSelectedSubFundGroupFlags, personUserIdentifier, subfundGroupCode);
    }


    /**
     * @see org.kuali.module.budget.dao.OrganizationBCDocumentSearchDao#cleanAccountSelectPullList(java.lang.String)
     */
    @RawSQL
    public void cleanReportsSubFundGroupSelectList(String personUserIdentifier) {
        clearTempTableByUnvlId("LD_BCN_SUBFUND_PICK_T", "PERSON_UNVL_ID", personUserIdentifier);
    }

    /**
     * @see org.kuali.module.budget.dao.BudgetReportsControlListDao#cleanReportsControlList(java.lang.String)
     */
    @RawSQL
    public void cleanReportsControlList(String personUserIdentifier) {
        clearTempTableByUnvlId("LD_BCN_CTRL_LIST_T", "PERSON_UNVL_ID", personUserIdentifier);
    }

    /**
     * @see org.kuali.module.budget.dao.BudgetReportsControlListDao#cleanReportsControlListPart1(java.lang.String)
     */
    @RawSQL
    public void cleanReportsControlListPart1(String idForSession) {
        clearTempTableBySesId("LD_BCN_BUILD_CTRL_LIST01_MT", "SESID", idForSession);
    }

    /**
     * @see org.kuali.module.budget.dao.BudgetReportsControlListDao#cleanReportsControlListPart2(java.lang.String)
     */
    @RawSQL
    public void cleanReportsControlListPart2(String idForSession) {
        clearTempTableBySesId("LD_BCN_BUILD_CTRL_LIST02_MT", "SESID", idForSession);
    }
}

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

import org.kuali.core.dbplatform.RawSQL;
import org.kuali.module.budget.dao.BudgetReportsControlListDao;

public class BudgetReportsControlListDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetReportsControlListDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationBCDocumentSearchDaoJdbc.class);

    private static String[] buildSubFundSelectListTemplates = new String[1];

    @RawSQL
    public BudgetReportsControlListDaoJdbc() {


    }

    /**
     * @see org.kuali.module.budget.dao.OrganizationBCDocumentSearchDao#buildAccountSelectPullList(java.lang.String,
     *      java.lang.Integer)
     */
    @RawSQL
    public void buildSubFundSelectList() {

        LOG.debug("buildSubFundSelectList() started");

        StringBuilder sqlText = new StringBuilder(500);

        
        // getSimpleJdbcTemplate().update(buildAccountSelectPullListTemplates[0], personUserIdentifier, universityFiscalYear,
        // personUserIdentifier, universityFiscalYear);
    }

    
    public void updateReportsControlListpart1(String idForSession, String personUserIdentifier, Integer universityFiscalYear) {

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

        String buildReportsControlListPart1 = sqlText.toString();

        getSimpleJdbcTemplate().update(buildReportsControlListPart1, idForSession, personUserIdentifier, universityFiscalYear);


        /*
         * INSERT INTO LD_BCN_BUILD_CTRL_LIST01_MT (SESID, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, ORG_LEVEL_CD, ORG_FIN_COA_CD,
         * ORG_CD, PULL_FLAG) SELECT '123456789', hier.univ_fiscal_yr, hier.fin_coa_cd, hier.account_nbr, hier.org_level_cd,
         * hier.org_fin_coa_cd, hier.org_cd, pull.pull_flag FROM LD_BCN_PULLUP_T pull, LD_BCN_ACCT_ORG_HIER_T hier WHERE
         * pull.pull_flag > 0 AND pull.person_unvl_id = '1234567890' AND hier.univ_fiscal_yr = 2007 AND hier.org_fin_coa_cd =
         * pull.fin_coa_cd AND hier.org_cd = pull.org_cd;
         */
    }

    public void updateReportsControlListpart2(String idForSession, String personUserIdentifier, String chartOfAccountsCode, String organizationCode ) {

        StringBuilder sqlText = new StringBuilder(500);

        sqlText.append("INSERT INTO LD_BCN_BUILD_CTRL_LIST02_MT \n");
        sqlText.append(" (SESID, PERSON_UNVL_ID, FDOC_NBR, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, \n");
        sqlText.append("  SUB_ACCT_NBR, HIER_ORG_LVL_CD, SEL_ORG_LVL_CD, SEL_ORG_FIN_COA, SEL_ORG_CD, SEL_PULL_FLAG) \n");
        sqlText.append("SELECT DISTINCT ?, ?, head.fdoc_nbr, head.univ_fiscal_yr, head.fin_coa_cd, head.account_nbr, \n");
        sqlText.append(" head.sub_acct_nbr, hier.org_level_cd, sel.org_level_cd, sel.org_fin_coa_cd, sel.org_cd, sel.pull_flag \n");
        sqlText.append(" hier.org_level_cd, hier.org_fin_coa_cd, hier.org_cd, pull.pull_flag \n");
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
        

        String buildReportsControlListPart2 = sqlText.toString();

        getSimpleJdbcTemplate().update(buildReportsControlListPart2, idForSession, personUserIdentifier, chartOfAccountsCode, organizationCode, idForSession);


        /*
         * INSERT INTO LD_BCN_BUILD_CTRL_LIST02_MT (SESID, PERSON_UNVL_ID, FDOC_NBR, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR,
         * SUB_ACCT_NBR, HIER_ORG_LVL_CD, SEL_ORG_LVL_CD, SEL_ORG_FIN_COA, SEL_ORG_CD, SEL_PULL_FLAG) 
         * SELECT DISTINCT 'SESSIONID', 'testid', head.fdoc_nbr, head.univ_fiscal_yr, head.fin_coa_cd, head.account_nbr, head.sub_acct_nbr, hier.org_level_cd,
         * sel.org_level_cd, sel.org_fin_coa_cd, sel.org_cd, sel.pull_flag 
         * FROM LD_BCN_ACCT_ORG_HIER_T hier, LD_BCNSTR_HDR_T head, LD_BCN_BUILD_CTRL_LIST01_MT sel 
         * WHERE hier.org_fin_coa_cd = 'BL' 
         * AND hier.org_cd = 'BL' 
         * AND hier.univ_fiscal_yr = sel.univ_fiscal_yr 
         * AND hier.fin_coa_cd = sel.fin_coa_cd 
         * AND hier.account_nbr = sel.account_nbr 
         * AND head.org_level_cd <= hier.org_level_cd 
         * AND hier.univ_fiscal_yr = head.univ_fiscal_yr 
         * AND hier.fin_coa_cd = head.fin_coa_cd 
         * AND hier.account_nbr = head.account_nbr 
         * AND sel.sesid = 'SESSIONID'
         */
    }

    public void updateReportsControlListDisp1(String idForSession) {

        StringBuilder sqlText = new StringBuilder(500);

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
        sqlText.append("  and pbgl.sub_acct_nbr = ctrl.sub_acct_nbr); \n");
        
        
        String buildReportsControlListDisp1 = sqlText.toString();

        getSimpleJdbcTemplate().update(buildReportsControlListDisp1, idForSession);


        /*
         * insert into LD_BCN_CTRL_LIST_T (PERSON_UNVL_ID, FDOC_NBR, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR,
         * HIER_ORG_LVL_CD, SEL_ORG_LVL_CD, SEL_ORG_FIN_COA, SEL_ORG_CD, SEL_PULL_FLAG, SEL_SUB_FUND_GRP) 
         * select ctrl.person_unvl_id, ctrl.fdoc_nbr, ctrl.univ_fiscal_yr, ctrl.fin_coa_cd, ctrl.account_nbr, ctrl.sub_acct_nbr,
         * ctrl.hier_org_lvl_cd, ctrl.sel_org_lvl_cd, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_pull_flag, acct.sub_fund_grp_cd 
         * from LD_BCN_BUILD_CTRL_LIST02_MT ctrl, CA_ACCOUNT_T acct 
         * where ctrl.sesid = 'SESSIONID' 
         * and acct.fin_coa_cd = ctrl.fin_coa_cd 
         * and acct.account_nbr = ctrl.account_nbr 
         * and exists 
         * (select * from LD_PND_BCNSTR_GL_T pbgl 
         * where pbgl.fdoc_nbr = ctrl.fdoc_nbr 
         * and pbgl.univ_fiscal_yr = ctrl.univ_fiscal_yr 
         * and pbgl.fin_coa_cd = ctrl.fin_coa_cd 
         * and pbgl.account_nbr = ctrl.account_nbr 
         * and pbgl.sub_acct_nbr = ctrl.sub_acct_nbr);
         */
    }
  
    /**
     * @see org.kuali.module.budget.dao.OrganizationBCDocumentSearchDao#cleanAccountSelectPullList(java.lang.String)
     */
    public void cleanSubFundSelectList(String personUserIdentifier) {
        // String sqlString = new String("DELETE FROM LD_BCN_ADM_POST_T WHERE (UNIV_FISCAL_YR = ?)\n");
        // getSimpleJdbcTemplate().update(sqlString,requestFiscalYear);
    }

    public void cleanReportsControlList(String personUserIdentifier) {
        clearTempTableByUnvlId("LD_BCN_CTRL_LIST_T", "PERSON_UNVL_ID", personUserIdentifier);
    }

    public void cleanReportsControlListPart1(String idForSession) {
        clearTempTableBySesId("LD_BCN_BUILD_CTRL_LIST01_MT", "SESID", idForSession);
    }

    public void cleanReportsControlListPart2(String idForSession) {
        clearTempTableBySesId("LD_BCN_BUILD_CTRL_LIST02_MT", "SESID", idForSession);
    }

}

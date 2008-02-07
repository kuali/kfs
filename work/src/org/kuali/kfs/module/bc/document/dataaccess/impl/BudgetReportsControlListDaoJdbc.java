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
 * A class to do the database queries needed to get valid data for OrganizationReportSelection screen
 */
public class BudgetReportsControlListDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetReportsControlListDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationBCDocumentSearchDaoJdbc.class);

    /**
     * @see org.kuali.module.budget.dao.BudgetReportsControlListDao#updateReportsControlListpart1(java.lang.String,
     *      java.lang.String, java.lang.Integer)
     */
    @RawSQL
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
    }

    /**
     * @see org.kuali.module.budget.dao.BudgetReportsControlListDao#updateReportsControlListpart2(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    @RawSQL
    public void updateReportsControlListpart2(String idForSession, String personUserIdentifier, String chartOfAccountsCode, String organizationCode) {
        StringBuilder sqlText = new StringBuilder(500);
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

        String buildReportsControlListPart2 = sqlText.toString();
        getSimpleJdbcTemplate().update(buildReportsControlListPart2, idForSession, personUserIdentifier, chartOfAccountsCode, organizationCode, idForSession);
    }

    /**
     * @see org.kuali.module.budget.dao.BudgetReportsControlListDao#updateReportsControlListDisp1(java.lang.String)
     */
    @RawSQL
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
        sqlText.append("  and pbgl.sub_acct_nbr = ctrl.sub_acct_nbr) \n");

        String buildReportsControlListDisp1 = sqlText.toString();
        getSimpleJdbcTemplate().update(buildReportsControlListDisp1, idForSession);
    }

    /**
     * @see org.kuali.module.budget.dao.BudgetReportsControlListDao#changeFlagOrganizationAndChartOfAccountCodeSelection(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @RawSQL
    public void changeFlagOrganizationAndChartOfAccountCodeSelection(String personUserIdentifier, String chartOfAccountsCode, String organizationCode) {
        StringBuilder sqlText = new StringBuilder(500);
        sqlText.append("UPDATE ld_bcn_pullup_t \n");
        sqlText.append("SET pull_flag = 1 \n");
        sqlText.append("WHERE person_unvl_id = ? \n");
        sqlText.append("  AND fin_coa_cd = ? \n");
        sqlText.append("  AND org_cd = ? \n");

        String flagToNonZero = sqlText.toString();
        getSimpleJdbcTemplate().update(flagToNonZero, personUserIdentifier, chartOfAccountsCode, organizationCode);
    }

    /**
     * @see org.kuali.module.budget.dao.BudgetReportsControlListDao#updateReportsSubFundGroupSelectList(java.lang.String)
     */
    @RawSQL
    public void updateReportsSubFundGroupSelectList(String personUserIdentifier) {
        StringBuilder sqlText = new StringBuilder(500);
        sqlText.append("INSERT INTO LD_BCN_SUBFUND_PICK_T (PERSON_UNVL_ID, SUB_FUND_GRP_CD, REPORT_FLAG)\n");
        sqlText.append("SELECT DISTINCT  ?, ctrl.sel_sub_fund_grp, 0 \n");
        sqlText.append("FROM LD_BCN_CTRL_LIST_T ctrl \n");
        sqlText.append("WHERE ctrl.person_unvl_id = ? \n");

        String subFundList = sqlText.toString();
        getSimpleJdbcTemplate().update(subFundList, personUserIdentifier, personUserIdentifier);
  }

    /**
     * @see org.kuali.module.budget.dao.BudgetReportsControlListDao#updateReportsSelectedSubFundGroupFlags(java.lang.String,
     *      java.lang.String)
     */
    @RawSQL
    public void updateReportsSelectedSubFundGroupFlags(String personUserIdentifier, String subfundGroupCode) {
        StringBuilder sqlText = new StringBuilder(500);
        sqlText.append("UPDATE LD_BCN_SUBFUND_PICK_T \n");
        sqlText.append("SET report_flag = 1 \n");
        sqlText.append("WHERE person_unvl_id = ? \n");
        sqlText.append("  AND sub_fund_grp_cd = ? \n");

        String subFundSelectListFlags = sqlText.toString();
        getSimpleJdbcTemplate().update(subFundSelectListFlags, personUserIdentifier, subfundGroupCode);
    }

    /**
     * @see org.kuali.module.budget.dao.BudgetReportsControlListDao#cleanReportsAccountSummaryTable(java.lang.String)
     */
    @RawSQL
    public void cleanReportsAccountSummaryTable(String personUserIdentifier) {
        clearTempTableByUnvlId("LD_BCN_ACCT_SUMM_T", "PERSON_UNVL_ID", personUserIdentifier);
    }

    /**
     * @see org.kuali.module.budget.dao.BudgetReportsControlListDao#updateRepotsAccountSummaryTable(java.lang.String)
     */
    @RawSQL
    public void updateRepotsAccountSummaryTable(String personUserIdentifier) {
        StringBuilder sqlText = new StringBuilder(500);
        sqlText.append("INSERT INTO LD_BCN_ACCT_SUMM_T (PERSON_UNVL_ID, ORG_FIN_COA_CD, ORG_CD, FIN_COA_CD, FUND_GRP_CD, SUB_FUND_GRP_CD, \n");
        sqlText.append("  ACCOUNT_NBR, SUB_ACCT_NBR, INC_EXP_CD, ACLN_ANNL_BAL_AMT, FIN_BEG_BAL_LN_AMT, SUB_FUND_SORT_CD) \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp, \n");
        sqlText.append(" ctrl.account_nbr, ctrl.sub_acct_nbr, 'A', sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt), sf.fin_report_sort_cd\n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T pbgl, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T pick, CA_SUB_FUND_GRP_T sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ('IN','IC','CH','AS') \n");
        sqlText.append(" AND ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND ctrl.person_unvl_id = pick.person_unvl_id \n");
        sqlText.append(" AND ctrl.sel_sub_fund_grp = pick.sub_fund_grp_cd \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = sf.sub_fund_grp_cd \n");
        sqlText.append(" AND pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append(" AND pbgl.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND pbgl.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND pbgl.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND pbgl.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fin_report_sort_cd, \n");
        sqlText.append(" sf.fund_grp_cd, ctrl.sel_sub_fund_grp, ctrl.account_nbr, ctrl.sub_acct_nbr \n");
        sqlText.append("UNION \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp, \n");
        sqlText.append(" ctrl.account_nbr, ctrl.sub_acct_nbr, 'E', sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt), sf.fin_report_sort_cd \n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T pbgl, CA_OBJECT_CODE_T o, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T pick, CA_SUB_FUND_GRP_T sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ('EE','ES','EX','LI')\n");
        sqlText.append(" AND ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND ctrl.person_unvl_id = pick.person_unvl_id \n");
        sqlText.append(" AND ctrl.sel_sub_fund_grp = pick.sub_fund_grp_cd \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = sf.sub_fund_grp_cd \n");
        sqlText.append(" AND pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append(" AND pbgl.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND pbgl.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND pbgl.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND pbgl.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND o.univ_fiscal_yr = pbgl.univ_fiscal_yr \n");
        sqlText.append(" AND o.fin_coa_cd = pbgl.fin_coa_cd \n");
        sqlText.append(" AND o.fin_object_cd = pbgl.fin_object_cd \n");
        sqlText.append(" AND o.fin_obj_level_cd not in ('CORI','TRIN') \n");
        sqlText.append(" AND EXISTS (SELECT * FROM CA_OBJECT_CODE_T o1, LD_PND_BCNSTR_GL_T pb \n");
        sqlText.append("WHERE pb.fdoc_nbr = pbgl.fdoc_nbr \n");
        sqlText.append(" AND pb.univ_fiscal_yr = pbgl.univ_fiscal_yr \n");
        sqlText.append(" AND pb.fin_coa_cd = pbgl.fin_coa_cd \n");
        sqlText.append(" AND pb.account_nbr = pbgl.account_nbr \n");
        sqlText.append(" AND pb.sub_acct_nbr = pbgl.sub_acct_nbr \n");
        sqlText.append(" AND o1.univ_fiscal_yr = pb.univ_fiscal_yr \n");
        sqlText.append(" AND o1.fin_coa_cd = pb.fin_coa_cd \n");
        sqlText.append(" AND o1.fin_object_cd = pb.fin_object_cd \n");
        sqlText.append(" AND o1.fin_obj_level_cd in ('CORI','TRIN')) \n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fin_report_sort_cd, sf.fund_grp_cd,\n");
        sqlText.append(" ctrl.sel_sub_fund_grp, ctrl.account_nbr, ctrl.sub_acct_nbr \n");
        sqlText.append("UNION\n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp, \n");
        sqlText.append(" ctrl.account_nbr, ctrl.sub_acct_nbr, 'T', sum(pbgl.acln_annl_bal_amt),sum(pbgl.fin_beg_bal_ln_amt), sf.fin_report_sort_cd \n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T pbgl, CA_OBJECT_CODE_T o, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T pick, CA_SUB_FUND_GRP_T sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ('EE','ES','EX','LI') \n");
        sqlText.append(" AND ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND ctrl.person_unvl_id = pick.person_unvl_id \n");
        sqlText.append(" AND ctrl.sel_sub_fund_grp = pick.sub_fund_grp_cd \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = sf.sub_fund_grp_cd \n");
        sqlText.append(" AND pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append(" AND pbgl.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND pbgl.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND pbgl.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND pbgl.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND o.univ_fiscal_yr = pbgl.univ_fiscal_yr \n");
        sqlText.append(" AND o.fin_coa_cd = pbgl.fin_coa_cd \n");
        sqlText.append(" AND o.fin_object_cd = pbgl.fin_object_cd \n");
        sqlText.append(" AND o.fin_obj_level_cd in ('CORI','TRIN') \n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fin_report_sort_cd, sf.fund_grp_cd, \n");
        sqlText.append(" ctrl.sel_sub_fund_grp, ctrl.account_nbr, ctrl.sub_acct_nbr \n");
        sqlText.append("UNION \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp, \n");
        sqlText.append(" ctrl.account_nbr, ctrl.sub_acct_nbr, 'X', sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt), sf.fin_report_sort_cd \n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T pbgl, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T pick, CA_SUB_FUND_GRP_T sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ('EE','ES','EX','LI') \n");
        sqlText.append(" AND ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND ctrl.person_unvl_id = pick.person_unvl_id \n");
        sqlText.append(" AND ctrl.sel_sub_fund_grp = pick.sub_fund_grp_cd \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = sf.sub_fund_grp_cd \n");
        sqlText.append(" AND pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append(" AND pbgl.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND pbgl.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND pbgl.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND pbgl.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fin_report_sort_cd, sf.fund_grp_cd,\n");
        sqlText.append(" ctrl.sel_sub_fund_grp, ctrl.account_nbr, ctrl.sub_acct_nbr \n");

        String repotsAccountSummary = sqlText.toString();
        getSimpleJdbcTemplate().update(repotsAccountSummary, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier);
    }


    /**
     * @see org.kuali.module.budget.dao.BudgetReportsControlListDao#updateRepotsAccountSummaryTable(java.lang.String)
     */
    @RawSQL
    public void updateRepotsAccountSummaryTableWithConsolidation(String personUserIdentifier) {
        StringBuilder sqlText = new StringBuilder(500);
        sqlText.append("INSERT INTO ld_bcn_acct_summ_t (PERSON_UNVL_ID, ORG_FIN_COA_CD, ORG_CD, FIN_COA_CD, FUND_GRP_CD, SUB_FUND_GRP_CD, \n");
        sqlText.append(" ACCOUNT_NBR, SUB_ACCT_NBR, INC_EXP_CD, ACLN_ANNL_BAL_AMT, FIN_BEG_BAL_LN_AMT, SUB_FUND_SORT_CD) \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp, \n");
        sqlText.append(" ctrl.account_nbr, '-----', 'A', sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt), sf.fin_report_sort_cd \n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T  pbgl, \n");
        sqlText.append(" LD_BCN_CTRL_LIST_T  ctrl, \n");
        sqlText.append(" LD_BCN_SUBFUND_PICK_T  pick, \n");
        sqlText.append(" CA_SUB_FUND_GRP_T  sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ('IN','IC','CH','AS') \n");
        sqlText.append(" AND ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND ctrl.person_unvl_id = pick.person_unvl_id \n");
        sqlText.append(" AND ctrl.sel_sub_fund_grp = pick.sub_fund_grp_cd \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = sf.sub_fund_grp_cd \n");
        sqlText.append(" AND pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append(" AND pbgl.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND pbgl.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND pbgl.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND pbgl.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, \n");
        sqlText.append(" ctrl.sel_org_cd, \n");
        sqlText.append(" ctrl.fin_coa_cd, \n");
        sqlText.append(" sf.fin_report_sort_cd, \n");
        sqlText.append(" sf.fund_grp_cd, \n");
        sqlText.append(" ctrl.sel_sub_fund_grp, \n");
        sqlText.append(" ctrl.account_nbr \n");
        sqlText.append("UNION \n");
        sqlText.append(" SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp, ctrl.account_nbr, '-----', \n");
        sqlText.append(" 'E', sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt), sf.fin_report_sort_cd \n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T pbgl, CA_OBJECT_CODE_T o, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T pick, CA_SUB_FUND_GRP_T sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ('EE','ES','EX','LI') \n");
        sqlText.append(" AND ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND ctrl.person_unvl_id = pick.person_unvl_id \n");
        sqlText.append(" AND ctrl.sel_sub_fund_grp = pick.sub_fund_grp_cd \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = sf.sub_fund_grp_cd \n");
        sqlText.append(" AND pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append(" AND pbgl.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND pbgl.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND pbgl.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND pbgl.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND o.univ_fiscal_yr = pbgl.univ_fiscal_yr \n");
        sqlText.append(" AND o.fin_coa_cd = pbgl.fin_coa_cd \n");
        sqlText.append(" AND o.fin_object_cd = pbgl.fin_object_cd \n");
        sqlText.append(" AND o.fin_obj_level_cd not in ('CORI','TRIN') \n");
        sqlText.append(" AND EXISTS \n");
        sqlText.append(" (SELECT * \n");
        sqlText.append(" FROM CA_OBJECT_CODE_T o1, LD_PND_BCNSTR_GL_T pb \n");
        sqlText.append(" WHERE pb.univ_fiscal_yr = pbgl.univ_fiscal_yr \n");
        sqlText.append("  AND pb.fin_coa_cd = pbgl.fin_coa_cd \n");
        sqlText.append("  AND pb.account_nbr = pbgl.account_nbr \n");
        sqlText.append("  AND o1.univ_fiscal_yr = pb.univ_fiscal_yr \n");
        sqlText.append("  AND o1.fin_coa_cd = pb.fin_coa_cd \n");
        sqlText.append("  AND o1.fin_object_cd = pb.fin_object_cd \n");
        sqlText.append("  AND o1.fin_obj_level_cd in ('CORI','TRIN')) \n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fin_report_sort_cd, \n");
        sqlText.append(" sf.fund_grp_cd, ctrl.sel_sub_fund_grp, ctrl.account_nbr \n");
        sqlText.append("UNION \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp, \n");
        sqlText.append(" ctrl.account_nbr, '-----', 'T', sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt),sf.fin_report_sort_cd \n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T pbgl, CA_OBJECT_CODE_T o, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T pick, CA_SUB_FUND_GRP_T sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ('EE','ES','EX','LI') \n");
        sqlText.append(" AND ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND ctrl.person_unvl_id = pick.person_unvl_id \n");
        sqlText.append(" AND ctrl.sel_sub_fund_grp = pick.sub_fund_grp_cd \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = sf.sub_fund_grp_cd \n");
        sqlText.append(" AND pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append(" AND pbgl.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND pbgl.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND pbgl.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND pbgl.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND o.univ_fiscal_yr = pbgl.univ_fiscal_yr \n");
        sqlText.append(" AND o.fin_coa_cd = pbgl.fin_coa_cd \n");
        sqlText.append(" AND o.fin_object_cd = pbgl.fin_object_cd \n");
        sqlText.append(" AND o.fin_obj_level_cd in ('CORI','TRIN') \n");
        sqlText.append(" GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fin_report_sort_cd, \n");
        sqlText.append(" sf.fund_grp_cd, ctrl.sel_sub_fund_grp, ctrl.account_nbr \n");
        sqlText.append("UNION \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp,  \n");
        sqlText.append(" ctrl.account_nbr, '-----', 'X', sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt), sf.fin_report_sort_cd \n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T pbgl, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T pick, CA_SUB_FUND_GRP_T sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ('EE','ES','EX','LI') \n");
        sqlText.append(" AND ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND ctrl.person_unvl_id = pick.person_unvl_id \n");
        sqlText.append(" AND ctrl.sel_sub_fund_grp = pick.sub_fund_grp_cd \n");
        sqlText.append(" AND pick.report_flag > 0 \n");
        sqlText.append(" AND pick.sub_fund_grp_cd = sf.sub_fund_grp_cd \n");
        sqlText.append(" AND pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append(" AND pbgl.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND pbgl.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND pbgl.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND pbgl.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fin_report_sort_cd, \n");
        sqlText.append(" sf.fund_grp_cd, ctrl.sel_sub_fund_grp, ctrl.account_nbr \n");

        String repotsAccountSummary = sqlText.toString();
        getSimpleJdbcTemplate().update(repotsAccountSummary, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier);

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

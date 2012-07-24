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

import org.kuali.kfs.module.bc.BCConstants.Report;
import org.kuali.kfs.module.bc.batch.dataaccess.impl.SQLForStep;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionAccountSummaryReportDao;

/**
 * builds rows for the general ledger summary report. allows three different levels of aggregation: account/sub-account, account,
 * and subfund
 */

public class BudgetConstructionAccountSummaryReportDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetConstructionAccountSummaryReportDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionAccountSummaryReportDaoJdbc.class);

    protected static ArrayList<SQLForStep> updateReportsAccountSummaryTable = new ArrayList<SQLForStep>(1);

    protected static ArrayList<SQLForStep> updateReportsAccountSummaryTableWithConsolidation = new ArrayList<SQLForStep>(1);

    protected static ArrayList<SQLForStep> updateSubFundSummaryReport = new ArrayList<SQLForStep>(1);

    public BudgetConstructionAccountSummaryReportDaoJdbc() {

        // builds and updates AccountSummaryReports

        ArrayList<Integer> insertionPoints = new ArrayList<Integer>(10);

        // report the detail
        StringBuilder sqlText = new StringBuilder(10000);
        sqlText.append("INSERT INTO LD_BCN_ACCT_SUMM_T (PERSON_UNVL_ID, ORG_FIN_COA_CD, ORG_CD, FIN_COA_CD, FUND_GRP_CD, SUB_FUND_GRP_CD, \n");
        sqlText.append("  ACCOUNT_NBR, SUB_ACCT_NBR, INC_EXP_CD, ACLN_ANNL_BAL_AMT, FIN_BEG_BAL_LN_AMT, SUB_FUND_SORT_CD) \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp, \n");
        sqlText.append(" ctrl.account_nbr, ctrl.sub_acct_nbr, '");
        // INCOME_EXP_TYPE_A
        insertionPoints.add(sqlText.length());
        sqlText.append("', sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt), sf.fin_report_sort_cd\n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T pbgl, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T pick, CA_SUB_FUND_GRP_T sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ");
        // IN list of object types for revenue
        insertionPoints.add(sqlText.length());
        sqlText.append(" \n");
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
        sqlText.append("UNION ALL\n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp, \n");
        sqlText.append(" ctrl.account_nbr, ctrl.sub_acct_nbr, '");
        // INCOME_EXP_TYPE_E
        insertionPoints.add(sqlText.length());
        sqlText.append("', sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt), sf.fin_report_sort_cd \n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T pbgl, CA_OBJECT_CODE_T o, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T pick, CA_SUB_FUND_GRP_T sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ");
        // IN list of object types for expenditure
        insertionPoints.add(sqlText.length());
        sqlText.append("\n");
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
        sqlText.append(" AND EXISTS (SELECT 1 FROM CA_OBJECT_CODE_T o1, LD_PND_BCNSTR_GL_T pb \n");
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
        sqlText.append("UNION ALL\n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp, \n");
        sqlText.append(" ctrl.account_nbr, ctrl.sub_acct_nbr, '");
        // INCOME_EXP_TYPE_T
        insertionPoints.add(sqlText.length());
        sqlText.append("', sum(pbgl.acln_annl_bal_amt),sum(pbgl.fin_beg_bal_ln_amt), sf.fin_report_sort_cd \n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T pbgl, CA_OBJECT_CODE_T o, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T pick, CA_SUB_FUND_GRP_T sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ");
        // IN list of expenditure object types
        insertionPoints.add(sqlText.length());
        sqlText.append(" \n");
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
        sqlText.append("UNION ALL\n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp, \n");
        sqlText.append(" ctrl.account_nbr, ctrl.sub_acct_nbr, '");
        // INCOME_EXP_TYPE_X
        insertionPoints.add(sqlText.length());
        sqlText.append("', sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt), sf.fin_report_sort_cd \n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T pbgl, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T pick, CA_SUB_FUND_GRP_T sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ");
        // IN list of expenditure object types
        insertionPoints.add(sqlText.length());
        sqlText.append(" \n");
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

        updateReportsAccountSummaryTable.add(new SQLForStep(sqlText, insertionPoints));
        sqlText.delete(0, sqlText.length());
        insertionPoints.clear();

        // report at the account level
        sqlText.append("INSERT INTO LD_BCN_ACCT_SUMM_T (PERSON_UNVL_ID, ORG_FIN_COA_CD, ORG_CD, FIN_COA_CD, FUND_GRP_CD, SUB_FUND_GRP_CD, \n");
        sqlText.append(" ACCOUNT_NBR, SUB_ACCT_NBR, INC_EXP_CD, ACLN_ANNL_BAL_AMT, FIN_BEG_BAL_LN_AMT, SUB_FUND_SORT_CD) \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp, \n");
        sqlText.append(" ctrl.account_nbr, '-----', '");
        // INCOME_EXP_TYPE_A
        insertionPoints.add(sqlText.length());
        sqlText.append("', sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt), sf.fin_report_sort_cd \n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T  pbgl, \n");
        sqlText.append(" LD_BCN_CTRL_LIST_T  ctrl, \n");
        sqlText.append(" LD_BCN_SUBFUND_PICK_T  pick, \n");
        sqlText.append(" CA_SUB_FUND_GRP_T  sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ");
        insertionPoints.add(sqlText.length());
        // IN list of revenue object types
        sqlText.append(" \n");
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
        sqlText.append("UNION ALL\n");
        sqlText.append(" SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp, ctrl.account_nbr, '-----', \n");
        sqlText.append(" '");
        // INCOME_EXP_TYPE_E
        insertionPoints.add(sqlText.length());
        sqlText.append("', sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt), sf.fin_report_sort_cd \n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T pbgl, CA_OBJECT_CODE_T o, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T pick, CA_SUB_FUND_GRP_T sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ");
        // IN list of expenditure object types
        insertionPoints.add(sqlText.length());
        sqlText.append(" \n");
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
        sqlText.append(" (SELECT 1 \n");
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
        sqlText.append("UNION ALL\n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp, \n");
        sqlText.append(" ctrl.account_nbr, '-----', '");
        // INCOME_EXP_TYPE_T
        insertionPoints.add(sqlText.length());
        sqlText.append("', sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt),sf.fin_report_sort_cd \n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T pbgl, CA_OBJECT_CODE_T o, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T pick, CA_SUB_FUND_GRP_T sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ");
        // IN list of expenditure object types
        insertionPoints.add(sqlText.length());
        sqlText.append(" \n");
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
        sqlText.append("UNION ALL\n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp,  \n");
        sqlText.append(" ctrl.account_nbr, '-----', '");
        // INCOME_EXP_TYPE_X
        insertionPoints.add(sqlText.length());
        sqlText.append("', sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt), sf.fin_report_sort_cd \n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T pbgl, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T pick, CA_SUB_FUND_GRP_T sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ");
        // IN list of expenditure object types
        insertionPoints.add(sqlText.length());
        sqlText.append(" \n");
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

        updateReportsAccountSummaryTableWithConsolidation.add(new SQLForStep(sqlText, insertionPoints));
        sqlText.delete(0, sqlText.length());
        insertionPoints.clear();

        // builds and updates SubFundSummaryReports
        sqlText.append("INSERT INTO LD_BCN_ACCT_SUMM_T(PERSON_UNVL_ID, ORG_FIN_COA_CD, ORG_CD, FIN_COA_CD, FUND_GRP_CD, SUB_FUND_GRP_CD,  \n");
        sqlText.append(" ACCOUNT_NBR, SUB_ACCT_NBR, INC_EXP_CD, ACLN_ANNL_BAL_AMT, FIN_BEG_BAL_LN_AMT, SUB_FUND_SORT_CD) \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp, \n");
        sqlText.append(" '-------', '-----', '");
        // INCOME_EXP_TYPE_A
        insertionPoints.add(sqlText.length());
        sqlText.append("', sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt), sf.fin_report_sort_cd \n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T pbgl, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T pick, CA_SUB_FUND_GRP_T sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ");
        // IN list of revenue object types
        insertionPoints.add(sqlText.length());
        sqlText.append(" \n");
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
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fin_report_sort_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp \n");
        sqlText.append("UNION ALL\n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp, \n");
        sqlText.append("'-------', '-----', '");
        // INCOME_EXP_TYPE_E
        insertionPoints.add(sqlText.length());
        sqlText.append("', sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt), sf.fin_report_sort_cd \n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T pbgl, CA_OBJECT_CODE_T o, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T  pick, CA_SUB_FUND_GRP_T sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ");
        // IN list of expenditure object types
        insertionPoints.add(sqlText.length());
        sqlText.append(" \n");
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
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fin_report_sort_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp \n");
        sqlText.append("UNION ALL\n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp, \n");
        sqlText.append("    '-------', '-----', '");
        // INCOME_EXP_TYPE_T
        insertionPoints.add(sqlText.length());
        sqlText.append("', sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt), sf.fin_report_sort_cd \n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T pbgl, CA_OBJECT_CODE_T o, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T pick, CA_SUB_FUND_GRP_T sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ");
        insertionPoints.add(sqlText.length());
        sqlText.append(" \n");
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
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fin_report_sort_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp \n");
        sqlText.append("UNION ALL\n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp, \n");
        sqlText.append(" '-------', '-----', '");
        // INCOME_EXP_TYPE_X
        insertionPoints.add(sqlText.length());
        sqlText.append("', sum(pbgl.acln_annl_bal_amt), sum(pbgl.fin_beg_bal_ln_amt), sf.fin_report_sort_cd \n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T pbgl, LD_BCN_CTRL_LIST_T ctrl, LD_BCN_SUBFUND_PICK_T  pick, CA_SUB_FUND_GRP_T sf \n");
        sqlText.append("WHERE pbgl.fin_obj_typ_cd in ");
        // IN list for expenditure
        insertionPoints.add(sqlText.length());
        sqlText.append(" \n");
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
        sqlText.append("GROUP BY ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, sf.fin_report_sort_cd, sf.fund_grp_cd, ctrl.sel_sub_fund_grp \n");

        updateSubFundSummaryReport.add(new SQLForStep(sqlText, insertionPoints));
        sqlText.delete(0, sqlText.length());
        insertionPoints.clear();
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetReportsControlListDao#cleanReportsAccountSummaryTable(java.lang.String)
     */
    public void cleanReportsAccountSummaryTable(String principalName) {
        clearTempTableByUnvlId("LD_BCN_ACCT_SUMM_T", "PERSON_UNVL_ID", principalName);
    }

    
    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionAccountSummaryReportDao#updateReportsAccountSummaryTable(java.lang.String, java.lang.String, java.lang.String)
     */
    public void updateReportsAccountSummaryTable(String principalName, String revenueINList, String expenditureINList) {
        ArrayList<String> stringsToInsert = new ArrayList<String>(8);
        stringsToInsert.add(Report.INCOME_EXP_TYPE_A);
        stringsToInsert.add(revenueINList);
        stringsToInsert.add(Report.INCOME_EXP_TYPE_E);
        stringsToInsert.add(expenditureINList);
        stringsToInsert.add(Report.INCOME_EXP_TYPE_T);
        stringsToInsert.add(expenditureINList);
        stringsToInsert.add(Report.INCOME_EXP_TYPE_X);
        stringsToInsert.add(expenditureINList);
        // run the SQL after inserting the constant strings
        getSimpleJdbcTemplate().update(updateReportsAccountSummaryTable.get(0).getSQL(stringsToInsert), principalName, principalName, principalName, principalName, principalName, principalName, principalName, principalName);
    }


    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionAccountSummaryReportDao#updateReportsAccountSummaryTableWithConsolidation(java.lang.String, java.lang.String, java.lang.String)
     */
    public void updateReportsAccountSummaryTableWithConsolidation(String principalName, String revenueINList, String expenditureINList) {

        ArrayList<String> stringsToInsert = new ArrayList<String>(8);
        stringsToInsert.add(Report.INCOME_EXP_TYPE_A);
        stringsToInsert.add(revenueINList);
        stringsToInsert.add(Report.INCOME_EXP_TYPE_E);
        stringsToInsert.add(expenditureINList);
        stringsToInsert.add(Report.INCOME_EXP_TYPE_T);
        stringsToInsert.add(expenditureINList);
        stringsToInsert.add(Report.INCOME_EXP_TYPE_X);
        stringsToInsert.add(expenditureINList);
        // run the SQL after inserting the constant strings
        getSimpleJdbcTemplate().update(updateReportsAccountSummaryTableWithConsolidation.get(0).getSQL(stringsToInsert), principalName, principalName, principalName, principalName, principalName, principalName, principalName, principalName);
    }

    
    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionAccountSummaryReportDao#updateSubFundSummaryReport(java.lang.String, java.lang.String, java.lang.String)
     */
    public void updateSubFundSummaryReport(String principalName, String revenueINList, String expenditureINList) {

        ArrayList<String> stringsToInsert = new ArrayList<String>(8);
        stringsToInsert.add(Report.INCOME_EXP_TYPE_A);
        stringsToInsert.add(revenueINList);
        stringsToInsert.add(Report.INCOME_EXP_TYPE_E);
        stringsToInsert.add(expenditureINList);
        stringsToInsert.add(Report.INCOME_EXP_TYPE_T);
        stringsToInsert.add(expenditureINList);
        stringsToInsert.add(Report.INCOME_EXP_TYPE_X);
        stringsToInsert.add(expenditureINList);
        // run the SQL after inserting the constant strings
        getSimpleJdbcTemplate().update(updateSubFundSummaryReport.get(0).getSQL(stringsToInsert), principalName, principalName, principalName, principalName, principalName, principalName, principalName, principalName);
    }

}

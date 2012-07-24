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

import java.util.List;
import java.util.UUID;

import org.kuali.kfs.module.bc.BCConstants.Report.BuildMode;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionObjectPick;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionReasonCodePick;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionSubFundPick;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetReportsControlListDao;

/**
 * JCBC implementation of BudgetReportsControlListDaoJdbc
 *
 * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetReportsControlListDao
 */
public class BudgetReportsControlListDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetReportsControlListDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetReportsControlListDaoJdbc.class);

    protected static String[] updateReportsControlList = new String[5];
    protected static String updateReportsSubFundGroupSelectList = new String();
    protected static String updateReportsObjectCodeSelectList = new String();
    protected static String updateReportsReasonCodeSelectList = new String();
    protected static String updateReportsSelectedSubFundGroupFlags = new String();
    protected static String updateReportsSelectedObjectCodeFlags = new String();
    protected static String updateReportsSelectedReasonCodeFlags = new String();

    public BudgetReportsControlListDaoJdbc() {
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

        // constrain accounts to GL pending budget
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

        // constrain accounts to monthly budget
        sqlText.append("INSERT INTO LD_BCN_CTRL_LIST_T \n");
        sqlText.append("  (PERSON_UNVL_ID, FDOC_NBR, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, \n");
        sqlText.append("  HIER_ORG_LVL_CD, SEL_ORG_LVL_CD, SEL_ORG_FIN_COA, SEL_ORG_CD, SEL_PULL_FLAG, SEL_SUB_FUND_GRP) \n");
        sqlText.append("SELECT ctrl.person_unvl_id, ctrl.fdoc_nbr, ctrl.univ_fiscal_yr, ctrl.fin_coa_cd, ctrl.account_nbr, ctrl.sub_acct_nbr, \n");
        sqlText.append("    ctrl.hier_org_lvl_cd, ctrl.sel_org_lvl_cd, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_pull_flag, acct.sub_fund_grp_cd \n");
        sqlText.append("FROM LD_BCN_BUILD_CTRL_LIST02_MT ctrl, CA_ACCOUNT_T acct \n");
        sqlText.append("WHERE ctrl.sesid = ? \n");
        sqlText.append("  AND acct.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append("  AND acct.account_nbr = ctrl.account_nbr \n");
        sqlText.append("  AND exists (SELECT * FROM LD_BCNSTR_MONTH_T bmth \n");
        sqlText.append("               WHERE bmth.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append("               AND bmth.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append("               AND bmth.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append("               AND bmth.account_nbr = ctrl.account_nbr \n");
        sqlText.append("               AND bmth.sub_acct_nbr = ctrl.sub_acct_nbr) \n");

        updateReportsControlList[3] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // constrain accounts to bcn appointment funding
        sqlText.append("INSERT INTO LD_BCN_CTRL_LIST_T \n");
        sqlText.append("  (PERSON_UNVL_ID, FDOC_NBR, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, \n");
        sqlText.append("  HIER_ORG_LVL_CD, SEL_ORG_LVL_CD, SEL_ORG_FIN_COA, SEL_ORG_CD, SEL_PULL_FLAG, SEL_SUB_FUND_GRP) \n");
        sqlText.append("SELECT ctrl.person_unvl_id, ctrl.fdoc_nbr, ctrl.univ_fiscal_yr, ctrl.fin_coa_cd, ctrl.account_nbr, ctrl.sub_acct_nbr, \n");
        sqlText.append("    ctrl.hier_org_lvl_cd, ctrl.sel_org_lvl_cd, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.sel_pull_flag, acct.sub_fund_grp_cd \n");
        sqlText.append("FROM LD_BCN_BUILD_CTRL_LIST02_MT ctrl, CA_ACCOUNT_T acct \n");
        sqlText.append("WHERE ctrl.sesid = ? \n");
        sqlText.append("  AND acct.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append("  AND acct.account_nbr = ctrl.account_nbr \n");
        sqlText.append("  AND exists (SELECT * FROM LD_PNDBC_APPTFND_T bcaf \n");
        sqlText.append("             WHERE bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append("               AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append("               AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append("               AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr) \n");

        updateReportsControlList[4] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // build the sub fund list for selection from the control list accounts
        sqlText.append("INSERT INTO LD_BCN_SUBFUND_PICK_T (PERSON_UNVL_ID, SUB_FUND_GRP_CD, REPORT_FLAG)\n");
        sqlText.append("SELECT DISTINCT  ?, ctrl.sel_sub_fund_grp, 0 \n");
        sqlText.append("FROM LD_BCN_CTRL_LIST_T ctrl \n");
        sqlText.append("WHERE ctrl.person_unvl_id = ? \n");

        updateReportsSubFundGroupSelectList = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // build the object code list for selection from the control list accounts and appointment funding table
        sqlText.append("INSERT INTO LD_BCN_OBJ_PICK_T (PERSON_UNVL_ID, FIN_OBJECT_CD, SELECT_FLAG) \n");
        sqlText.append("SELECT DISTINCT ?, bcaf.fin_object_cd, 0 \n");
        sqlText.append("FROM LD_BCN_CTRL_LIST_T ctrl, LD_PNDBC_APPTFND_T bcaf \n");
        sqlText.append("WHERE ctrl.person_unvl_id = ? \n");
        sqlText.append("  AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append("  AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append("  AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append("  AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");

        updateReportsObjectCodeSelectList = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // build the reason code list for selection from the account control table, object code control table, and reason code table
        sqlText.append("INSERT INTO LD_BCN_RSN_CD_PK_T (PERSON_UNVL_ID, APPT_FND_REASON_CD, SELECT_FLAG) \n");
        sqlText.append("SELECT DISTINCT  ?, brsn.appt_fnd_reason_cd, 0 \n");
        sqlText.append("FROM LD_BCN_CTRL_LIST_T ctrl, LD_BCN_OBJ_PICK_T opk, LD_BCN_AF_REASON_T brsn \n");
        sqlText.append("WHERE ctrl.person_unvl_id = ? \n");
        sqlText.append("  AND brsn.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append("  AND brsn.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append("  AND brsn.account_nbr = ctrl.account_nbr \n");
        sqlText.append("  AND brsn.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append("  AND brsn.fin_object_cd = opk.fin_object_cd \n");
        sqlText.append("  AND brsn.emplid != 'VACANT' \n");
        sqlText.append("  AND ctrl.person_unvl_id = opk.person_unvl_id \n");
        sqlText.append("  AND opk.select_flag > 0 \n");

        updateReportsReasonCodeSelectList = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // sql to update the select flag of a sub fund option
        sqlText.append("UPDATE LD_BCN_SUBFUND_PICK_T \n");
        sqlText.append("SET report_flag = ? \n");
        sqlText.append("WHERE person_unvl_id = ? \n");
        sqlText.append("  AND sub_fund_grp_cd = ? \n");

        updateReportsSelectedSubFundGroupFlags = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // sql to update the select flag of a sub fund option
        sqlText.append("UPDATE LD_BCN_OBJ_PICK_T \n");
        sqlText.append("SET SELECT_FLAG = ? \n");
        sqlText.append("WHERE person_unvl_id = ? \n");
        sqlText.append("  AND fin_object_cd = ? \n");

        updateReportsSelectedObjectCodeFlags = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // sql to update the select flag of a sub fund option
        sqlText.append("UPDATE LD_BCN_RSN_CD_PK_T \n");
        sqlText.append("SET SELECT_FLAG = ? \n");
        sqlText.append("WHERE person_unvl_id = ? \n");
        sqlText.append("  AND APPT_FND_REASON_CD = ? \n");

        updateReportsSelectedReasonCodeFlags = sqlText.toString();
        sqlText.delete(0, sqlText.length());
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetReportsControlListDao#updateReportControlList(java.lang.String, java.lang.Integer,
     *      java.lang.String, java.lang.String, org.kuali.kfs.module.bc.BCConstants.Report.BuildMode)
     */
    @Override
    public void updateReportControlList(String principalName, Integer universityFiscalYear, String chartOfAccountsCode, String organizationCode, BuildMode buildMode) {
        // clear out previous data for user
        clearTempTableByUnvlId("LD_BCN_CTRL_LIST_T", "PERSON_UNVL_ID", principalName);

        String idForSession = UUID.randomUUID().toString();

        // build 1st temp table with list of accounts for the selected organizations
        getSimpleJdbcTemplate().update(updateReportsControlList[0], idForSession.toString(), principalName, universityFiscalYear);

        // build 2nd temp table with list of accounts from 1 temp table that are also contained in user's point of view
        getSimpleJdbcTemplate().update(updateReportsControlList[1], idForSession.toString(), principalName, chartOfAccountsCode, organizationCode, idForSession.toString());

        // constrain account list further based on buildMode
        switch (buildMode) {
            case PBGL:
                getSimpleJdbcTemplate().update(updateReportsControlList[2], idForSession.toString());
                break;
            case MONTH:
                getSimpleJdbcTemplate().update(updateReportsControlList[3], idForSession.toString());
                break;
            case BCAF:
                getSimpleJdbcTemplate().update(updateReportsControlList[4], idForSession.toString());
                break;
        }

        // clear out temp tables
        clearTempTableBySesId("LD_BCN_BUILD_CTRL_LIST01_MT", "SESID", idForSession.toString());
        clearTempTableBySesId("LD_BCN_BUILD_CTRL_LIST02_MT", "SESID", idForSession.toString());
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetReportsControlListDao#updateReportsSubFundGroupSelectList(java.lang.String)
     */
    @Override
    public void updateReportsSubFundGroupSelectList(String principalName) {
        // clear out previous sub-fund list for user
        clearTempTableByUnvlId("LD_BCN_SUBFUND_PICK_T", "PERSON_UNVL_ID", principalName);

        // rebuild sub-fund list
        getSimpleJdbcTemplate().update(updateReportsSubFundGroupSelectList, principalName, principalName);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetReportsControlListDao#updateReportsObjectCodeSelectList(java.lang.String)
     */
    @Override
    public void updateReportsObjectCodeSelectList(String principalName) {
        // clear out previous object code list for user
        clearTempTableByUnvlId("LD_BCN_OBJ_PICK_T", "PERSON_UNVL_ID", principalName);

        // rebuild object code list
        getSimpleJdbcTemplate().update(updateReportsObjectCodeSelectList, principalName, principalName);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetReportsControlListDao#updateReportsReasonCodeSelectList(java.lang.String)
     */
    @Override
    public void updateReportsReasonCodeSelectList(String principalName) {
        // clear out previous reason code list for user
        clearTempTableByUnvlId("LD_BCN_RSN_CD_PK_T", "PERSON_UNVL_ID", principalName);

        // rebuild reason code list
        getSimpleJdbcTemplate().update(updateReportsReasonCodeSelectList, principalName, principalName);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetReportsControlListDao#updateObjectCodeSelectFlags(java.util.List)
     */
    @Override
    public void updateObjectCodeSelectFlags(List<BudgetConstructionObjectPick> objectCodePickList) {
        for (BudgetConstructionObjectPick budgetConstructionObjectPick : objectCodePickList) {
            getSimpleJdbcTemplate().update(updateReportsSelectedObjectCodeFlags, budgetConstructionObjectPick.getSelectFlag().intValue(), budgetConstructionObjectPick.getPrincipalId(), budgetConstructionObjectPick.getFinancialObjectCode());
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetReportsControlListDao#updateReasonCodeSelectFlags(java.util.List)
     */
    @Override
    public void updateReasonCodeSelectFlags(List<BudgetConstructionReasonCodePick> reasonCodePickList) {
        for (BudgetConstructionReasonCodePick budgetConstructionReasonCodePick : reasonCodePickList) {
            getSimpleJdbcTemplate().update(updateReportsSelectedReasonCodeFlags, budgetConstructionReasonCodePick.getSelectFlag().intValue(), budgetConstructionReasonCodePick.getPrincipalId(), budgetConstructionReasonCodePick.getAppointmentFundingReasonCode());
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetReportsControlListDao#updateSubFundSelectFlags(java.util.List)
     */
    @Override
    public void updateSubFundSelectFlags(List<BudgetConstructionSubFundPick> subFundPickList) {
        for (BudgetConstructionSubFundPick budgetConstructionSubFundPick : subFundPickList) {
            getSimpleJdbcTemplate().update(updateReportsSelectedSubFundGroupFlags, budgetConstructionSubFundPick.getReportFlag().intValue(), budgetConstructionSubFundPick.getPrincipalId(), budgetConstructionSubFundPick.getSubFundGroupCode());
        }
    }
}


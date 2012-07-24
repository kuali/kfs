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
import java.util.UUID;

import org.kuali.kfs.module.bc.batch.dataaccess.impl.SQLForStep;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionPositionFundingDetailReportDao;
import org.kuali.rice.core.api.util.type.KualiDecimal;


/**
 * populates the report table for positon funding detail for a given user
 */

public class BudgetConstructionPositionFundingDetailReportDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetConstructionPositionFundingDetailReportDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionPositionFundingDetailReportDaoJdbc.class);

    protected static ArrayList<SQLForStep> updateReportsPositionFundingDetailTable = new ArrayList<SQLForStep>(5);

    public BudgetConstructionPositionFundingDetailReportDaoJdbc() {

        /* populate the table based on threshold settings */
        /* sum the request and base by ssn */
        StringBuilder sqlText = new StringBuilder(1500);
        sqlText.append("INSERT INTO LD_BCN_BUILD_POSLIST01_MT \n");
        sqlText.append("(SESID, EMPLID, POS_CSF_AMT, POS_CSF_FTE_QTY, APPT_RQST_AMT, APPT_RQST_FTE_QTY) \n");
        sqlText.append("SELECT ?, bcaf.emplid, SUM(COALESCE(bcsf.pos_csf_amt,0)), SUM(COALESCE(bcsf.pos_csf_fte_qty,0)), SUM(bcaf.appt_rqst_amt), SUM(bcaf.appt_rqst_fte_qty) \n");
        sqlText.append("FROM (LD_PNDBC_APPTFND_T bcaf LEFT OUTER JOIN LD_BCN_CSF_TRCKR_T bcsf ON \n");
        sqlText.append(" ((bcaf.univ_fiscal_yr = bcsf.univ_fiscal_yr) AND \n");
        sqlText.append(" (bcaf.fin_coa_cd = bcsf.fin_coa_cd) AND \n");
        sqlText.append(" (bcaf.account_nbr = bcsf.account_nbr) AND \n");
        sqlText.append(" (bcaf.sub_acct_nbr = bcsf.sub_acct_nbr) AND \n");
        sqlText.append(" (bcaf.fin_object_cd = bcsf.fin_object_cd) AND \n");
        sqlText.append(" (bcaf.fin_sub_obj_cd = bcsf.fin_sub_obj_cd) AND \n");
        sqlText.append(" (bcaf.position_nbr = bcsf.position_nbr) AND \n");
        sqlText.append(" (bcaf.emplid = bcsf.emplid))), LD_BCN_CTRL_LIST_T ctrl, LD_BCN_OBJ_PICK_T pick \n");
        sqlText.append("WHERE ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.appt_rqst_amt <> 0 \n");
        sqlText.append(" AND bcaf.fin_object_cd = pick.fin_object_cd \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pick.select_flag > 0 \n");
        sqlText.append("GROUP BY bcaf.emplid \n");

        updateReportsPositionFundingDetailTable.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

        /* get the set where percent change (fraction * 100) in the salary exceeds the threshold */
        sqlText.append("INSERT INTO LD_BCN_POS_FND_T \n");
        sqlText.append(" (PERSON_UNVL_ID, SEL_ORG_FIN_COA, SEL_ORG_CD, PERSON_NM, EMPLID, POSITION_NBR, UNIV_FISCAL_YR, \n");
        sqlText.append(" FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD) \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, COALESCE(iinc.person_nm,'VACANT'), bcaf.emplid, bcaf.position_nbr, bcaf.univ_fiscal_yr,\n");
        sqlText.append(" bcaf.fin_coa_cd, bcaf.account_nbr, bcaf.sub_acct_nbr, bcaf.fin_object_cd, bcaf.fin_sub_obj_cd \n");
        sqlText.append("FROM (LD_PNDBC_APPTFND_T bcaf LEFT OUTER JOIN LD_BCN_INTINCBNT_T iinc \n");
        sqlText.append(" ON (bcaf.emplid = iinc.emplid)), LD_BCN_CTRL_LIST_T ctrl, LD_BCN_OBJ_PICK_T pick, LD_BCN_BUILD_POSLIST01_MT tssn \n");
        sqlText.append("WHERE ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.fin_object_cd = pick.fin_object_cd \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pick.select_flag > 0 \n");
        sqlText.append(" AND bcaf.emplid = tssn.emplid \n");
        sqlText.append(" AND tssn.appt_rqst_fte_qty = tssn.pos_csf_fte_qty \n");
        sqlText.append(" AND ROUND((((tssn.appt_rqst_amt - tssn.pos_csf_amt) / tssn.pos_csf_amt) * 100),1) >= ? \n");
        sqlText.append(" AND tssn.pos_csf_amt <> 0 \n");
        sqlText.append(" AND tssn.sesid = ? \n");

        updateReportsPositionFundingDetailTable.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

        /* get the set where the percent change (fraction * 100) in the salary is less than or equal to the threshold */
        sqlText.append("INSERT INTO LD_BCN_POS_FND_T \n");
        sqlText.append(" (PERSON_UNVL_ID, SEL_ORG_FIN_COA, SEL_ORG_CD, PERSON_NM, EMPLID, POSITION_NBR, UNIV_FISCAL_YR, \n");
        sqlText.append(" FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD) \n");
        sqlText.append(" SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, COALESCE(iinc.person_nm,'VACANT'), bcaf.emplid, bcaf.position_nbr, bcaf.univ_fiscal_yr, \n");
        sqlText.append(" bcaf.fin_coa_cd, bcaf.account_nbr, bcaf.sub_acct_nbr, bcaf.fin_object_cd, bcaf.fin_sub_obj_cd \n");
        sqlText.append("FROM (LD_PNDBC_APPTFND_T bcaf LEFT OUTER JOIN LD_BCN_INTINCBNT_T iinc ON (bcaf.emplid = iinc.emplid)), \n");
        sqlText.append(" LD_BCN_CTRL_LIST_T ctrl, LD_BCN_OBJ_PICK_T pick, LD_BCN_BUILD_POSLIST01_MT tssn \n");
        sqlText.append("WHERE ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.fin_object_cd = pick.fin_object_cd \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pick.select_flag > 0 \n");
        sqlText.append(" AND bcaf.emplid = tssn.emplid \n");
        sqlText.append(" AND tssn.appt_rqst_fte_qty = tssn.pos_csf_fte_qty \n");
        sqlText.append(" AND ROUND((((tssn.appt_rqst_amt - tssn.pos_csf_amt) / tssn.pos_csf_amt) * 100),1) <= ? \n");
        sqlText.append(" AND tssn.pos_csf_amt <> 0 \n");
        sqlText.append(" AND tssn.sesid = ? \n");

        updateReportsPositionFundingDetailTable.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());

        /* populate the table using the full set--no check on the percent change in the salary */
        sqlText.append("INSERT INTO LD_BCN_POS_FND_T \n");
        sqlText.append(" (PERSON_UNVL_ID, SEL_ORG_FIN_COA, SEL_ORG_CD, PERSON_NM, EMPLID, POSITION_NBR, UNIV_FISCAL_YR, \n");
        sqlText.append(" FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD) \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, COALESCE(iinc.person_nm,'VACANT'), bcaf.emplid, bcaf.position_nbr, bcaf.univ_fiscal_yr, \n");
        sqlText.append(" bcaf.fin_coa_cd, bcaf.account_nbr, bcaf.sub_acct_nbr, bcaf.fin_object_cd, bcaf.fin_sub_obj_cd \n");
        sqlText.append("FROM (LD_PNDBC_APPTFND_T bcaf LEFT OUTER JOIN LD_BCN_INTINCBNT_T iinc \n");
        sqlText.append(" ON (bcaf.emplid = iinc.emplid)), LD_BCN_CTRL_LIST_T ctrl, LD_BCN_OBJ_PICK_T pick \n");
        sqlText.append("WHERE ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.fin_object_cd = pick.fin_object_cd \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pick.select_flag > 0 \n");

        updateReportsPositionFundingDetailTable.add(new SQLForStep(sqlText));
        sqlText.delete(0, sqlText.length());
    }

    @Override
    public void cleanReportsPositionFundingDetailTable(String principalName) {
        clearTempTableByUnvlId("LD_BCN_POS_FND_T", "PERSON_UNVL_ID", principalName);
    }

    /**
     * build a list of people with salaries at or above the threshold
     *
     * @param principalName--the user requesting the list
     * @param thresholdPercent--the percent marking the threshold
     */
    protected void updateReportsPositionFundingDetailTableAboveThreshold(String principalName, KualiDecimal thresholdPercent) {
        String idForSession = UUID.randomUUID().toString();
        // get rid of any previous reporting data from this user
        cleanReportsPositionFundingDetailTable(principalName);
        // sum the FTE and amounts into a temporary table
        getSimpleJdbcTemplate().update(updateReportsPositionFundingDetailTable.get(0).getSQL(), idForSession, principalName);
        // fill the reporting table with only those people who are at or above the threshold
        // (jdbcTemplate will apparenlty not accept a parameter of type KualiDecimal, and a cast when we pass the parameter doesn't
        // help: 04/09/2008)
        // (apparently, creating a new value from a cast doesn't help either)
        Number thresholdValue = thresholdPercent.floatValue();
        getSimpleJdbcTemplate().update(updateReportsPositionFundingDetailTable.get(1).getSQL(), principalName, principalName, thresholdValue, idForSession);
        // remove the data for this user's session from the temporary table for total amounts and FTE
        this.clearTempTableBySesId("LD_BCN_BUILD_POSLIST01_MT","SESID",idForSession);
    }

    /**
     * build a list of people with salaries at or below the threshhold
     *
     * @param principalName--the user requesting the list
     * @param thresholdPercent--the percent marking the threshold
     */
    protected void updateReportsPositionFundingDetailTableBelowThreshold(String principalName, KualiDecimal thresholdPercent) {
        String idForSession = UUID.randomUUID().toString();
        // get rid of any previous reporting data from this user
        cleanReportsPositionFundingDetailTable(principalName);
        // sum the FTE and amounts into a temporary table
        getSimpleJdbcTemplate().update(updateReportsPositionFundingDetailTable.get(0).getSQL(), idForSession, principalName);
        // fill the reporting table with only those people who are at or below the threshold
        // (jdbcTemplate will apparenlty not accept a parameter of type KualiDecimal, and a cast when we pass the parameter doesn't
        // help: 04/09/2008)
        // (apparently, creating a new value from a cast doesn't help either)
        Number thresholdValue = thresholdPercent.floatValue();
        getSimpleJdbcTemplate().update(updateReportsPositionFundingDetailTable.get(2).getSQL(), principalName, principalName, thresholdValue, idForSession);
        // remove the data for this user's session from the temporary table for total amounts and FTE
        this.clearTempTableBySesId("LD_BCN_BUILD_POSLIST01_MT","SESID",idForSession);
    }

    /**
     * build a list of all salaries which this user can see
     *
     * @param principalName--the user requesting the list
     */
    protected void updateReportsPositionFundingDetailTableWithAllData(String principalName) {
        // get rid of any previous reporting data from this user
        cleanReportsPositionFundingDetailTable(principalName);
        // dump all the data this user is authorized to report on into the reporting table
        getSimpleJdbcTemplate().update(updateReportsPositionFundingDetailTable.get(3).getSQL(), principalName, principalName);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionPositionFundingDetailReportDao#updateReportsPositionFundingDetailTable(java.lang.String,
     *      boolean, boolean, java.lang.Number)
     */
    @Override
    public void updateReportsPositionFundingDetailTable(String principalName, boolean applyAThreshold, boolean selectOnlyGreaterThanOrEqualToThreshold, KualiDecimal thresholdPercent) {
        // if there is no threshold, just dump everything in and return
        if (!applyAThreshold) {
            updateReportsPositionFundingDetailTableWithAllData(principalName);
            return;
        }
        // the user wants a threshold--list above or below?
        if (selectOnlyGreaterThanOrEqualToThreshold) {
            updateReportsPositionFundingDetailTableAboveThreshold(principalName, thresholdPercent);
        }
        else {
            updateReportsPositionFundingDetailTableBelowThreshold(principalName, thresholdPercent);
        }
    }

}

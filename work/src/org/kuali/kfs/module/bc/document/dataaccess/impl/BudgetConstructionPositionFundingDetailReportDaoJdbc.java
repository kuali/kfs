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
import org.kuali.module.budget.dao.BudgetConstructionPositionFundingDetailReportDao;

/**
 * A class to do the database queries needed to get valid data for 
 */

public class BudgetConstructionPositionFundingDetailReportDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetConstructionPositionFundingDetailReportDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionPositionFundingDetailReportDaoJdbc.class);

    private static String[] updateReportsPositionFundingDetailTable = new String[5];
    
    @RawSQL
    public BudgetConstructionPositionFundingDetailReportDaoJdbc() {
        
        /* populate the table based on threshold settings */
        /* sum the request and base by ssn */
        StringBuilder sqlText = new StringBuilder(500);
        sqlText.append("INSERT INTO ld_bcn_build_poslist01_mt \n");
        sqlText.append("(SESID, EMPLID, POS_CSF_AMT, POS_CSF_FTE_QTY, APPT_RQST_AMT, APPT_RQST_FTE_QTY) \n");
        sqlText.append("SELECT '12345', bcaf.emplid, SUM(COALESCE(bcsf.pos_csf_amt,0)), SUM(COALESCE(bcsf.pos_csf_fte_qty,0)), SUM(bcaf.appt_rqst_amt), SUM(bcaf.appt_rqst_fte_qty) \n");
        sqlText.append("FROM (ld_pndbc_apptfnd_t bcaf LEFT OUTER JOIN ld_bcn_csf_trckr_t bcsf ON \n");
        sqlText.append(" ((bcaf.univ_fiscal_yr = bcsf.univ_fiscal_yr) AND \n");
        sqlText.append(" (bcaf.fin_coa_cd = bcsf.fin_coa_cd) AND \n");
        sqlText.append(" (bcaf.account_nbr = bcsf.account_nbr) AND \n");
        sqlText.append(" (bcaf.sub_acct_nbr = bcsf.sub_acct_nbr) AND \n");
        sqlText.append(" (bcaf.fin_object_cd = bcsf.fin_object_cd) AND \n");
        sqlText.append(" (bcaf.fin_sub_obj_cd = bcsf.fin_sub_obj_cd) AND \n");
        sqlText.append(" (bcaf.position_nbr = bcsf.position_nbr) AND \n");
        sqlText.append(" (bcaf.emplid = bcsf.emplid))), ld_bcn_ctrl_list_t ctrl, ld_bcn_obj_pick_t pick \n");
        sqlText.append("WHERE ctrl.person_unvl_id = '1234567' \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.appt_rqst_amt <> 0 \n");
        sqlText.append(" AND bcaf.fin_object_cd = pick.fin_object_cd \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pick.select_flag > 0 \n");
        sqlText.append("GROUP BY bcaf.emplid \n");          

        updateReportsPositionFundingDetailTable[0] = sqlText.toString();
        sqlText.delete(0, sqlText.length());
        
        /* get the set where diff >= threshold */
        sqlText.append("INSERT INTO ld_bcn_pos_fnd_t \n");
        sqlText.append(" (PERSON_UNVL_ID, SEL_ORG_FIN_COA, SEL_ORG_CD, PERSON_NM, EMPLID, POSITION_NBR, UNIV_FISCAL_YR, \n");
        sqlText.append(" FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD) \n");
        sqlText.append("SELECT '1234567', ctrl.sel_org_fin_coa, ctrl.sel_org_cd, COALESCE(iinc.person_nm,'VACANT'), bcaf.emplid, bcaf.position_nbr, bcaf.univ_fiscal_yr,\n");
        sqlText.append(" bcaf.fin_coa_cd, bcaf.account_nbr, bcaf.sub_acct_nbr, bcaf.fin_object_cd, bcaf.fin_sub_obj_cd \n");
        sqlText.append("FROM (ld_pndbc_apptfnd_t bcaf LEFT OUTER JOIN ld_bcn_intincbnt_t iinc \n");
        sqlText.append(" ON (bcaf.emplid = iinc.emplid)), ld_bcn_ctrl_list_t ctrl, ld_bcn_obj_pick_t pick, ld_bcn_build_poslist01_mt tssn \n");
        sqlText.append("WHERE ctrl.person_unvl_id = '1234567' \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.fin_object_cd = pick.fin_object_cd \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pick.select_flag > 0 \n");
        sqlText.append(" AND bcaf.emplid = tssn.emplid \n");
        sqlText.append(" AND tssn.appt_rqst_fte_qty = tssn.pos_csf_fte_qty \n");
        sqlText.append(" AND ROUND((((tssn.appt_rqst_amt - tssn.pos_csf_amt) / tssn.pos_csf_amt) * 100),1) >= '5' \n");
        sqlText.append(" AND tssn.pos_csf_amt <> 0 \n");
        sqlText.append(" AND tssn.sesid = '12345' \n");
                                            
        updateReportsPositionFundingDetailTable[1] = sqlText.toString();
        sqlText.delete(0, sqlText.length());
        
        /* get the set where diff <= threshold */        
        sqlText.append("INSERT INTO ld_bcn_pos_fnd_t \n");
        sqlText.append(" (PERSON_UNVL_ID, SEL_ORG_FIN_COA, SEL_ORG_CD, PERSON_NM, EMPLID, POSITION_NBR, UNIV_FISCAL_YR, \n");
        sqlText.append(" FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD) \n");
        sqlText.append(" SELECT '1234567', ctrl.sel_org_fin_coa, ctrl.sel_org_cd, COALESCE(iinc.person_nm,'VACANT'), bcaf.emplid, bcaf.position_nbr, bcaf.univ_fiscal_yr, \n");
        sqlText.append(" bcaf.fin_coa_cd, bcaf.account_nbr, bcaf.sub_acct_nbr, bcaf.fin_object_cd, bcaf.fin_sub_obj_cd \n");
        sqlText.append("FROM (ld_pndbc_apptfnd_t bcaf LEFT OUTER JOIN ld_bcn_intincbnt_t iinc ON (bcaf.emplid = iinc.emplid)), \n");
        sqlText.append(" ld_bcn_ctrl_list_t ctrl, ld_bcn_obj_pick_t pick, ld_bcn_build_poslist01_mt tssn \n");
        sqlText.append("WHERE ctrl.person_unvl_id = '1234567' \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.fin_object_cd = pick.fin_object_cd \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pick.select_flag > 0 \n");
        sqlText.append(" AND bcaf.emplid = tssn.emplid \n");
        sqlText.append(" AND tssn.appt_rqst_fte_qty = tssn.pos_csf_fte_qty \n");
        sqlText.append(" AND ROUND((((tssn.appt_rqst_amt - tssn.pos_csf_amt) / tssn.pos_csf_amt) * 100),1) <= '5' \n");
        sqlText.append(" AND tssn.pos_csf_amt <> 0 \n");
        sqlText.append(" AND tssn.sesid = '12345' \n");
        
        updateReportsPositionFundingDetailTable[2] = sqlText.toString();
        sqlText.delete(0, sqlText.length());
        
        /* populate the table using the full set */
        sqlText.append("INSERT INTO ld_bcn_pos_fnd_t \n");
        sqlText.append(" (PERSON_UNVL_ID, SEL_ORG_FIN_COA, SEL_ORG_CD, PERSON_NM, EMPLID, POSITION_NBR, UNIV_FISCAL_YR, \n");
        sqlText.append(" FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD) \n");
        sqlText.append("SELECT '1234567', ctrl.sel_org_fin_coa, ctrl.sel_org_cd, COALESCE(iinc.person_nm,'VACANT'), bcaf.emplid, bcaf.position_nbr, bcaf.univ_fiscal_yr, \n");
        sqlText.append(" bcaf.fin_coa_cd, bcaf.account_nbr, bcaf.sub_acct_nbr, bcaf.fin_object_cd, bcaf.fin_sub_obj_cd \n");
        sqlText.append("FROM (ld_pndbc_apptfnd_t bcaf LEFT OUTER JOIN ld_bcn_intincbnt_t iinc ON \n");
        sqlText.append(" (bcaf.emplid = iinc.emplid)), ld_bcn_ctrl_list_t ctrl, ld_bcn_obj_pick_t pick \n");
        sqlText.append("WHERE ctrl.person_unvl_id = '1234567' \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.fin_object_cd = pick.fin_object_cd \n");
        sqlText.append(" AND pick.person_unvl_id = ctrl.person_unvl_id \n");
        sqlText.append(" AND pick.select_flag > 0 \n");
        
        updateReportsPositionFundingDetailTable[3] = sqlText.toString();
        sqlText.delete(0, sqlText.length());
    }
    
    public void cleanReportsPositionFundingDetailTable(String personUserIdentifier) {
        clearTempTableByUnvlId("ld_bcn_pos_fnd_t", "PERSON_UNVL_ID", personUserIdentifier);
    }

    public void updateReportsPositionFundingDetailTable(String personUserIdentifier) {
        //getSimpleJdbcTemplate().update(updateReportsAccountSummaryTable[0], personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier, personUserIdentifier);
    }


}

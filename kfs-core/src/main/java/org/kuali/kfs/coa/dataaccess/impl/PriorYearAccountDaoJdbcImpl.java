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
package org.kuali.kfs.coa.dataaccess.impl;

import org.kuali.kfs.coa.dataaccess.PriorYearAccountDao;
import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;

/**
 * This class performs actions against the database through direct SQL command calls.
 */
public class PriorYearAccountDaoJdbcImpl extends PlatformAwareDaoBaseJdbc implements PriorYearAccountDao {

    /** Constant used to retrieve row counts for tables. Obj_Id value exists in all tables in DB. */
    private static final String OBJ_ID = "OBJ_ID";


    /**
     * @see org.kuali.kfs.coa.dataaccess.PriorYearAccountDaoJdbc#purgePriorYearAccounts(java.lang.String)
     */
    public int purgePriorYearAccounts(String priorYrAcctTableName) {

        // 1. Count how many rows are currently in the prior year acct table
        int count = getSimpleJdbcTemplate().queryForInt("SELECT COUNT(1) FROM " + priorYrAcctTableName);

        // 2. Purge all the rows from the prior year acct table
        getSimpleJdbcTemplate().update("DELETE FROM " + priorYrAcctTableName);

        return count;
    }

    /**
     * @see org.kuali.kfs.coa.dataaccess.PriorYearAccountDaoJdbc#copyCurrentAccountsToPriorYearTable(java.lang.String,
     *      java.lang.String)
     */
    public int copyCurrentAccountsToPriorYearTable(String priorYrAcctTableName, String acctTableName) {

        // 1. Copy all the rows from the current org table to the prior year acct table
        getSimpleJdbcTemplate().update(
                "INSERT INTO " + priorYrAcctTableName + "(FIN_COA_CD, ACCOUNT_NBR, OBJ_ID, VER_NBR, ACCOUNT_NM, ACCT_FSC_OFC_UID, ACCT_SPVSR_UNVL_ID, ACCT_MGR_UNVL_ID, ORG_CD, ACCT_TYP_CD, ACCT_PHYS_CMP_CD, SUB_FUND_GRP_CD, ACCT_FRNG_BNFT_CD, FIN_HGH_ED_FUNC_CD, ACCT_RSTRC_STAT_CD, ACCT_RSTRC_STAT_DT, ACCT_CITY_NM, ACCT_STATE_CD, ACCT_STREET_ADDR, ACCT_ZIP_CD, RPTS_TO_FIN_COA_CD, RPTS_TO_ACCT_NBR, ACCT_CREATE_DT, ACCT_EFFECT_DT, ACCT_EXPIRATION_DT, CONT_FIN_COA_CD, CONT_ACCOUNT_NBR, ENDOW_FIN_COA_CD, ENDOW_ACCOUNT_NBR, CONTR_CTRL_FCOA_CD, CONTR_CTRLACCT_NBR, INCOME_FIN_COA_CD, INCOME_ACCOUNT_NBR, ACCT_ICR_TYP_CD, AC_CSTM_ICREXCL_CD, FIN_SERIES_ID, ACCT_IN_FP_CD, BDGT_REC_LVL_CD, ACCT_SF_CD, ACCT_PND_SF_CD, FIN_EXT_ENC_SF_CD, FIN_INT_ENC_SF_CD, FIN_PRE_ENC_SF_CD, FIN_OBJ_PRSCTRL_CD, CG_CFDA_NBR, ACCT_OFF_CMP_IND, ACCT_CLOSED_IND) "
                        + " SELECT FIN_COA_CD, ACCOUNT_NBR, OBJ_ID, VER_NBR, ACCOUNT_NM, ACCT_FSC_OFC_UID, ACCT_SPVSR_UNVL_ID, ACCT_MGR_UNVL_ID, ORG_CD, ACCT_TYP_CD, ACCT_PHYS_CMP_CD, SUB_FUND_GRP_CD, ACCT_FRNG_BNFT_CD, FIN_HGH_ED_FUNC_CD, ACCT_RSTRC_STAT_CD, ACCT_RSTRC_STAT_DT, ACCT_CITY_NM, ACCT_STATE_CD, ACCT_STREET_ADDR, ACCT_ZIP_CD, RPTS_TO_FIN_COA_CD, RPTS_TO_ACCT_NBR, ACCT_CREATE_DT, ACCT_EFFECT_DT, ACCT_EXPIRATION_DT, CONT_FIN_COA_CD, CONT_ACCOUNT_NBR, ENDOW_FIN_COA_CD, ENDOW_ACCOUNT_NBR, CONTR_CTRL_FCOA_CD, CONTR_CTRLACCT_NBR, INCOME_FIN_COA_CD, INCOME_ACCOUNT_NBR, ACCT_ICR_TYP_CD, AC_CSTM_ICREXCL_CD, FIN_SERIES_ID, ACCT_IN_FP_CD, BDGT_REC_LVL_CD, ACCT_SF_CD, ACCT_PND_SF_CD, FIN_EXT_ENC_SF_CD, FIN_INT_ENC_SF_CD, FIN_PRE_ENC_SF_CD, FIN_OBJ_PRSCTRL_CD, CG_CFDA_NBR, ACCT_OFF_CMP_IND, ACCT_CLOSED_IND " + " FROM " + acctTableName);

//        "INSERT INTO " + priorYrAcctTableName + "(FIN_COA_CD, ACCOUNT_NBR, OBJ_ID, VER_NBR, ACCOUNT_NM, ACCT_FSC_OFC_UID, ACCT_SPVSR_UNVL_ID, ACCT_MGR_UNVL_ID, ORG_CD, ACCT_TYP_CD, ACCT_PHYS_CMP_CD, SUB_FUND_GRP_CD, ACCT_FRNG_BNFT_CD, FIN_HGH_ED_FUNC_CD, ACCT_RSTRC_STAT_CD, ACCT_RSTRC_STAT_DT, ACCT_CITY_NM, ACCT_STATE_CD, ACCT_STREET_ADDR, ACCT_ZIP_CD, RPTS_TO_FIN_COA_CD, RPTS_TO_ACCT_NBR, ACCT_CREATE_DT, ACCT_EFFECT_DT, ACCT_EXPIRATION_DT, CONT_FIN_COA_CD, CONT_ACCOUNT_NBR, ENDOW_FIN_COA_CD, ENDOW_ACCOUNT_NBR, CONTR_CTRL_FCOA_CD, CONTR_CTRLACCT_NBR, INCOME_FIN_COA_CD, INCOME_ACCOUNT_NBR, ACCT_ICR_TYP_CD, AC_CSTM_ICREXCL_CD, FIN_SERIES_ID, ICR_FIN_COA_CD, ICR_ACCOUNT_NBR, ACCT_IN_FP_CD, BDGT_REC_LVL_CD, ACCT_SF_CD, ACCT_PND_SF_CD, FIN_EXT_ENC_SF_CD, FIN_INT_ENC_SF_CD, FIN_PRE_ENC_SF_CD, FIN_OBJ_PRSCTRL_CD, CG_CFDA_NBR, ACCT_OFF_CMP_IND, ACCT_CLOSED_IND) "
//        + " SELECT FIN_COA_CD, ACCOUNT_NBR, OBJ_ID, VER_NBR, ACCOUNT_NM, ACCT_FSC_OFC_UID, ACCT_SPVSR_UNVL_ID, ACCT_MGR_UNVL_ID, ORG_CD, ACCT_TYP_CD, ACCT_PHYS_CMP_CD, SUB_FUND_GRP_CD, ACCT_FRNG_BNFT_CD, FIN_HGH_ED_FUNC_CD, ACCT_RSTRC_STAT_CD, ACCT_RSTRC_STAT_DT, ACCT_CITY_NM, ACCT_STATE_CD, ACCT_STREET_ADDR, ACCT_ZIP_CD, RPTS_TO_FIN_COA_CD, RPTS_TO_ACCT_NBR, ACCT_CREATE_DT, ACCT_EFFECT_DT, ACCT_EXPIRATION_DT, CONT_FIN_COA_CD, CONT_ACCOUNT_NBR, ENDOW_FIN_COA_CD, ENDOW_ACCOUNT_NBR, CONTR_CTRL_FCOA_CD, CONTR_CTRLACCT_NBR, INCOME_FIN_COA_CD, INCOME_ACCOUNT_NBR, ACCT_ICR_TYP_CD, AC_CSTM_ICREXCL_CD, FIN_SERIES_ID, ICR_FIN_COA_CD, ICR_ACCOUNT_NBR, ACCT_IN_FP_CD, BDGT_REC_LVL_CD, ACCT_SF_CD, ACCT_PND_SF_CD, FIN_EXT_ENC_SF_CD, FIN_INT_ENC_SF_CD, FIN_PRE_ENC_SF_CD, FIN_OBJ_PRSCTRL_CD, CG_CFDA_NBR, ACCT_OFF_CMP_IND, ACCT_CLOSED_IND " + " FROM " + acctTableName);
        
        // 2. Count how many rows are currently in the prior year acct table
        return getSimpleJdbcTemplate().queryForInt("SELECT COUNT(1) FROM " + priorYrAcctTableName);
    }

    /**
     * @see org.kuali.kfs.coa.dataaccess.PriorYearAccountDaoJdbc#copyCurrentICRAccountsToPriorYearTable(java.lang.String, java.lang.String)
     */
    public int copyCurrentICRAccountsToPriorYearTable(String priorYrAcctTableName, String acctTableName) {

        // 1. Copy all the rows from the current org table to the prior year acct table
        getSimpleJdbcTemplate().update(
                "INSERT INTO " + priorYrAcctTableName + "(CA_PRIOR_YR_ICR_ACCT_GNRTD_ID, OBJ_ID, VER_NBR, FIN_COA_CD, ACCOUNT_NBR, ICR_FIN_COA_CD, ICR_FIN_ACCT_NBR, ACLN_PCT, DOBJ_MAINT_CD_ACTV_IND)"  
                        + " SELECT CA_ICR_ACCT_GNRTD_ID, OBJ_ID, VER_NBR, FIN_COA_CD, ACCOUNT_NBR, ICR_FIN_COA_CD, ICR_FIN_ACCT_NBR, ACLN_PCT, DOBJ_MAINT_CD_ACTV_IND " + " FROM " + acctTableName);

        // 2. Count how many rows are currently in the prior year acct table
        return getSimpleJdbcTemplate().queryForInt("SELECT COUNT(1) FROM " + priorYrAcctTableName);
    }

}

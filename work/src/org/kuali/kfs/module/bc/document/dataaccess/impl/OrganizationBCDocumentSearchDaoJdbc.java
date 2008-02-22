/*
 * Copyright 2007 The Kuali Foundation.
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
import org.kuali.module.budget.dao.OrganizationBCDocumentSearchDao;

/**
 * This class...
 */
public class OrganizationBCDocumentSearchDaoJdbc extends BudgetConstructionDaoJdbcBase implements OrganizationBCDocumentSearchDao {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationBCDocumentSearchDaoJdbc.class);
    
    private static String[] buildAccountSelectPullListTemplates = new String[1];
    
    private static String[] buildBudgetedAccountsAbovePointsOfView = new String[1];
    
    @RawSQL
    public OrganizationBCDocumentSearchDaoJdbc() {
        
        StringBuilder sqlText = new StringBuilder(500);

        sqlText.append("INSERT INTO ld_bcn_acctsel_t \n");
        sqlText.append(" (PERSON_UNVL_ID,UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FDOC_NBR, \n");
        sqlText.append("  ORG_LEVEL_CD,ORG_FIN_COA_CD,ORG_CD,FDOC_STATUS_CD,FDOC_CREATE_DT) \n");
        sqlText.append("SELECT pull.person_unvl_id, head.univ_fiscal_yr, head.fin_coa_cd, head.account_nbr, head.sub_acct_nbr,head.fdoc_nbr, \n");
        sqlText.append(" head.org_level_cd, hier2.org_fin_coa_cd, hier2.org_cd, fphd.fdoc_status_cd, fphd.temp_doc_fnl_dt \n");
        sqlText.append("FROM ld_bcn_pullup_t pull, ld_bcn_acct_org_hier_t hier,  ld_bcn_acct_org_hier_t hier2, \n");
        sqlText.append("     ld_bcnstr_hdr_t head, fp_doc_header_t fphd \n");
        sqlText.append("WHERE pull.pull_flag > 0 \n");
        sqlText.append("  AND pull.person_unvl_id = ? \n");
        sqlText.append("  AND hier.univ_fiscal_yr = ? \n");
        sqlText.append("  AND hier.org_fin_coa_cd = pull.fin_coa_cd \n");
        sqlText.append("  AND hier.org_cd = pull.org_cd \n");
        sqlText.append("  AND hier2.univ_fiscal_yr = hier.univ_fiscal_yr \n");
        sqlText.append("  AND hier2.fin_coa_cd = hier.fin_coa_cd \n");
        sqlText.append("  AND hier2.account_nbr = hier.account_nbr \n");
        sqlText.append("  AND head.univ_fiscal_yr = hier2.univ_fiscal_yr \n");
        sqlText.append("  AND head.fin_coa_cd = hier2.fin_coa_cd \n");
        sqlText.append("  AND head.account_nbr = hier2.account_nbr \n");
        sqlText.append("  AND head.org_level_cd = hier2.org_level_cd \n");
        sqlText.append("  AND fphd.fdoc_nbr = head.fdoc_nbr \n");
        sqlText.append("UNION \n");
        sqlText.append("SELECT pull.person_unvl_id, head.univ_fiscal_yr, head.fin_coa_cd, head.account_nbr, head.sub_acct_nbr, head.fdoc_nbr, \n");
        sqlText.append(" head.org_level_cd, hier2.org_fin_coa_cd, hier2.org_cd, fphd.fdoc_status_cd, fphd.temp_doc_fnl_dt \n");
        sqlText.append("FROM ld_bcn_pullup_t pull, ld_bcn_acct_org_hier_t hier, ld_bcn_acct_org_hier_t hier2, \n");
        sqlText.append("     ld_bcnstr_hdr_t head, fp_doc_header_t fphd \n");
        sqlText.append("WHERE pull.pull_flag > 0 \n");
        sqlText.append("  AND pull.person_unvl_id = ? \n");
        sqlText.append("  AND hier.univ_fiscal_yr = ? \n");
        sqlText.append("  AND hier.org_fin_coa_cd = pull.fin_coa_cd \n");
        sqlText.append("  AND hier.org_cd = pull.org_cd \n");
        sqlText.append("  AND hier2.univ_fiscal_yr = hier.univ_fiscal_yr \n");
        sqlText.append("  AND hier2.fin_coa_cd = hier.fin_coa_cd \n");
        sqlText.append("  AND hier2.account_nbr = hier.account_nbr \n");
        sqlText.append("  AND hier2.org_level_cd = 1 \n");
        sqlText.append("  AND head.univ_fiscal_yr = hier2.univ_fiscal_yr \n");
        sqlText.append("  AND head.fin_coa_cd = hier2.fin_coa_cd \n");
        sqlText.append("  AND head.account_nbr = hier2.account_nbr \n");
        sqlText.append("  AND head.org_level_cd = 0 \n");
        sqlText.append("  AND fphd.fdoc_nbr = head.fdoc_nbr\n");
        buildAccountSelectPullListTemplates[0] = sqlText.toString();
        sqlText.delete(0, sqlText.length());
                
        sqlText.append("INSERT INTO LD_BCN_ACCTSEL_T \n");
        sqlText.append(" (PERSON_UNVL_ID, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FDOC_NBR, \n");
        sqlText.append(" ORG_LEVEL_CD, ORG_FIN_COA_CD, ORG_CD, FDOC_STATUS_CD, FDOC_CREATE_DT) \n");
        sqlText.append("SELECT  ?, \n");
        sqlText.append(" head.univ_fiscal_yr, \n");
        sqlText.append(" head.fin_coa_cd, \n");
        sqlText.append(" head.account_nbr, \n");
        sqlText.append(" head.sub_acct_nbr, \n");
        sqlText.append(" head.fdoc_nbr, \n");
        sqlText.append(" head.org_level_cd, \n");
        sqlText.append(" ah.org_fin_coa_cd, \n");
        sqlText.append(" ah.org_cd, \n");
        sqlText.append(" fphd.fdoc_status_cd, \n");
        sqlText.append(" fphd.TEMP_DOC_FNL_DT \n");
        sqlText.append("FROM ld_bcn_pullup_t pull, \n");
        sqlText.append(" LD_BCNSTR_HDR_T head, \n");
        sqlText.append(" fp_doc_header_t fphd, \n");
        sqlText.append(" LD_BCN_ACCT_ORG_HIER_T sh, \n");
        sqlText.append(" LD_BCN_ACCT_ORG_HIER_T ph, \n");
        sqlText.append(" LD_BCN_ACCT_ORG_HIER_T ah \n");
        sqlText.append("WHERE pull.pull_flag > 0 \n");
        sqlText.append(" AND pull.person_unvl_id = ? \n");
        sqlText.append(" AND sh.org_fin_coa_cd = pull.fin_coa_cd  \n");
        sqlText.append(" AND sh.org_cd = pull.org_cd \n");
        sqlText.append(" AND sh.univ_fiscal_yr = ?  \n");
        sqlText.append(" AND ph.univ_fiscal_yr = sh.univ_fiscal_yr  \n");
        sqlText.append(" AND ph.fin_coa_cd = sh.fin_coa_cd  \n");
        sqlText.append(" AND ph.account_nbr = sh.account_nbr \n");
        sqlText.append(" AND ph.org_fin_coa_cd = ? \n");
        sqlText.append(" AND ph.org_cd = ? \n");
        sqlText.append(" AND head.univ_fiscal_yr = ph.univ_fiscal_yr \n");
        sqlText.append(" AND head.fin_coa_cd = ph.fin_coa_cd \n");
        sqlText.append(" AND head.account_nbr = ph.account_nbr \n");
        sqlText.append(" AND head.org_level_cd > ph.org_level_cd \n");
        sqlText.append(" AND fphd.fdoc_nbr = head.fdoc_nbr \n");
        sqlText.append(" AND ah.univ_fiscal_yr = head.univ_fiscal_yr \n");
        sqlText.append(" AND ah.fin_coa_cd = head.fin_coa_cd \n");
        sqlText.append(" AND ah.account_nbr = head.account_nbr \n");
        sqlText.append(" AND ah.org_level_cd = head.org_level_cd \n");
        buildBudgetedAccountsAbovePointsOfView[0] = sqlText.toString();
            
    }

    /**
     * @see org.kuali.module.budget.dao.OrganizationBCDocumentSearchDao#buildAccountSelectPullList(java.lang.String,
     *      java.lang.Integer)
     */
    @RawSQL
    public int buildAccountSelectPullList(String personUserIdentifier, Integer universityFiscalYear) {

        LOG.debug("buildAccountSelectPullList() started");

        int rowsAffected = getSimpleJdbcTemplate().update(buildAccountSelectPullListTemplates[0], personUserIdentifier, universityFiscalYear, personUserIdentifier, universityFiscalYear);
        return rowsAffected;
    }
    
    @RawSQL
    public void buildBudgetedAccountsAbovePointsOfView(String personUserIdentifier, Integer universityFiscalYear, String chartOfAccountsCode, String organizationCode) {

        LOG.debug("buildBudgetedAccountsAbovePointsOfView() started");

        getSimpleJdbcTemplate().update(buildBudgetedAccountsAbovePointsOfView[0], personUserIdentifier, personUserIdentifier, universityFiscalYear, chartOfAccountsCode, organizationCode);
    }

    

    /**
     * @see org.kuali.module.budget.dao.OrganizationBCDocumentSearchDao#cleanAccountSelectPullList(java.lang.String)
     */
    public void cleanAccountSelectPullList(String personUserIdentifier) {

        clearTempTableByUnvlId("ld_bcn_acctsel_t", "PERSON_UNVL_ID", personUserIdentifier);
    }

}

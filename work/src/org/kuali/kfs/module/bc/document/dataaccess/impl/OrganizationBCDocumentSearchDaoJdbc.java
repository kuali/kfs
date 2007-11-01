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

import org.kuali.module.budget.dao.OrganizationBCDocumentSearchDao;

/**
 * This class...
 */
public class OrganizationBCDocumentSearchDaoJdbc extends BudgetConstructionDaoJdbcBase implements OrganizationBCDocumentSearchDao {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationBCDocumentSearchDaoJdbc.class);

    /**
     * @see org.kuali.module.budget.dao.OrganizationBCDocumentSearchDao#buildAccountSelectPullList(java.lang.String,
     *      java.lang.Integer)
     */
    public void buildAccountSelectPullList(String personUserIdentifier, Integer universityFiscalYear) {

        LOG.debug("buildAccountSelectPullList() started");

        getSimpleJdbcTemplate().update(
                "INSERT INTO ld_bcn_acctsel_t " + " (PERSON_UNVL_ID,UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FDOC_NBR, " + "  ORG_LEVEL_CD,ORG_FIN_COA_CD,ORG_CD,FDOC_STATUS_CD,FDOC_CREATE_DT) " + "SELECT pull.person_unvl_id, head.univ_fiscal_yr, head.fin_coa_cd, head.account_nbr, head.sub_acct_nbr,head.fdoc_nbr, " + " head.org_level_cd, hier2.org_fin_coa_cd, hier2.org_cd, fphd.fdoc_status_cd, fphd.temp_doc_fnl_dt " + "FROM ld_bcn_pullup_t pull, ld_bcn_acct_org_hier_t hier,  ld_bcn_acct_org_hier_t hier2, " + "     ld_bcnstr_hdr_t head, fp_doc_header_t fphd " + "WHERE pull.pull_flag > 0 " + "  AND pull.person_unvl_id = ? " + "  AND hier.univ_fiscal_yr = ? " + "  AND hier.org_fin_coa_cd = pull.fin_coa_cd " + "  AND hier.org_cd = pull.org_cd " + "  AND hier2.univ_fiscal_yr = hier.univ_fiscal_yr " + "  AND hier2.fin_coa_cd = hier.fin_coa_cd " + "  AND hier2.account_nbr = hier.account_nbr " + "  AND head.univ_fiscal_yr = hier2.univ_fiscal_yr "
                        + "  AND head.fin_coa_cd = hier2.fin_coa_cd " + "  AND head.account_nbr = hier2.account_nbr " + "  AND head.org_level_cd = hier2.org_level_cd " + "  AND fphd.fdoc_nbr = head.fdoc_nbr " + "UNION " + "SELECT pull.person_unvl_id, head.univ_fiscal_yr, head.fin_coa_cd, head.account_nbr, head.sub_acct_nbr, head.fdoc_nbr, " + " head.org_level_cd, hier2.org_fin_coa_cd, hier2.org_cd, fphd.fdoc_status_cd, fphd.temp_doc_fnl_dt " + "FROM ld_bcn_pullup_t pull, ld_bcn_acct_org_hier_t hier, ld_bcn_acct_org_hier_t hier2, " + "     ld_bcnstr_hdr_t head, fp_doc_header_t fphd " + "WHERE pull.pull_flag > 0 " + "  AND pull.person_unvl_id = ? " + "  AND hier.univ_fiscal_yr = ? " + "  AND hier.org_fin_coa_cd = pull.fin_coa_cd " + "  AND hier.org_cd = pull.org_cd " + "  AND hier2.univ_fiscal_yr = hier.univ_fiscal_yr " + "  AND hier2.fin_coa_cd = hier.fin_coa_cd " + "  AND hier2.account_nbr = hier.account_nbr " + "  AND hier2.org_level_cd = 1 "
                        + "  AND head.univ_fiscal_yr = hier2.univ_fiscal_yr " + "  AND head.fin_coa_cd = hier2.fin_coa_cd " + "  AND head.account_nbr = hier2.account_nbr " + "  AND head.org_level_cd = 0 " + "  AND fphd.fdoc_nbr = head.fdoc_nbr", personUserIdentifier, universityFiscalYear, personUserIdentifier, universityFiscalYear);

    }

    /**
     * @see org.kuali.module.budget.dao.OrganizationBCDocumentSearchDao#cleanAccountSelectPullList(java.lang.String)
     */
    public void cleanAccountSelectPullList(String personUserIdentifier) {

        clearTempTableByUnvlId("ld_bcn_acctsel_t", "PERSON_UNVL_ID", personUserIdentifier);
    }

}

/*
 * Copyright 2007 The Kuali Foundation
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

import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.document.dataaccess.OrganizationBCDocumentSearchDao;
import org.kuali.kfs.sys.KFSConstants;

/**
 * This class...
 */
public class OrganizationBCDocumentSearchDaoJdbc extends BudgetConstructionDaoJdbcBase implements OrganizationBCDocumentSearchDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationBCDocumentSearchDaoJdbc.class);

    protected static String[] buildAccountSelectPullListTemplates = new String[1];
    protected static String[] buildBudgetedAccountsAbovePointsOfView = new String[1];
    protected static String[] buildAccountManagerDelegateListTemplates = new String[3];

    public OrganizationBCDocumentSearchDaoJdbc() {

        StringBuilder sqlText = new StringBuilder(500);

        sqlText.append("INSERT INTO LD_BCN_ACCTSEL_T \n");
        sqlText.append(" (PERSON_UNVL_ID,UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FDOC_NBR, \n");
        sqlText.append("  ORG_LEVEL_CD,ORG_FIN_COA_CD,ORG_CD,FDOC_STATUS_CD) \n");
        sqlText.append("SELECT pull.person_unvl_id, head.univ_fiscal_yr, head.fin_coa_cd, head.account_nbr, head.sub_acct_nbr,head.fdoc_nbr, \n");
        sqlText.append(" head.org_level_cd, hier2.org_fin_coa_cd, hier2.org_cd, fshd.fdoc_status_cd \n");
        sqlText.append("FROM LD_BCN_PULLUP_T pull, LD_BCN_ACCT_ORG_HIER_T hier,  LD_BCN_ACCT_ORG_HIER_T hier2, \n");
        sqlText.append("     LD_BCNSTR_HDR_T head, FS_DOC_HEADER_T fshd \n");
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
        sqlText.append("  AND fshd.fdoc_nbr = head.fdoc_nbr \n");
        sqlText.append("UNION \n");
        sqlText.append("SELECT pull.person_unvl_id, head.univ_fiscal_yr, head.fin_coa_cd, head.account_nbr, head.sub_acct_nbr, head.fdoc_nbr, \n");
        sqlText.append(" head.org_level_cd, hier2.org_fin_coa_cd, hier2.org_cd, fshd.fdoc_status_cd \n");
        sqlText.append("FROM LD_BCN_PULLUP_T pull, LD_BCN_ACCT_ORG_HIER_T hier, LD_BCN_ACCT_ORG_HIER_T hier2, \n");
        sqlText.append("     LD_BCNSTR_HDR_T head, FS_DOC_HEADER_T fshd \n");
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
        sqlText.append("  AND fshd.fdoc_nbr = head.fdoc_nbr\n");
        buildAccountSelectPullListTemplates[0] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        sqlText.append("INSERT INTO LD_BCN_ACCTSEL_T \n");
        sqlText.append(" (PERSON_UNVL_ID, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FDOC_NBR, \n");
        sqlText.append(" ORG_LEVEL_CD, ORG_FIN_COA_CD, ORG_CD, FDOC_STATUS_CD) \n");
        sqlText.append("SELECT  ?, \n");
        sqlText.append(" head.univ_fiscal_yr, \n");
        sqlText.append(" head.fin_coa_cd, \n");
        sqlText.append(" head.account_nbr, \n");
        sqlText.append(" head.sub_acct_nbr, \n");
        sqlText.append(" head.fdoc_nbr, \n");
        sqlText.append(" head.org_level_cd, \n");
        sqlText.append(" ah.org_fin_coa_cd, \n");
        sqlText.append(" ah.org_cd, \n");
        sqlText.append(" fshd.fdoc_status_cd \n");
        sqlText.append("FROM LD_BCN_PULLUP_T pull, \n");
        sqlText.append(" LD_BCNSTR_HDR_T head, \n");
        sqlText.append(" FS_DOC_HEADER_T fshd, \n");
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
        sqlText.append(" AND fshd.fdoc_nbr = head.fdoc_nbr \n");
        sqlText.append(" AND ah.univ_fiscal_yr = head.univ_fiscal_yr \n");
        sqlText.append(" AND ah.fin_coa_cd = head.fin_coa_cd \n");
        sqlText.append(" AND ah.account_nbr = head.account_nbr \n");
        sqlText.append(" AND ah.org_level_cd = head.org_level_cd \n");
        buildBudgetedAccountsAbovePointsOfView[0] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // build list of accounts where user is fiscal officer or delegate
        sqlText.append("INSERT INTO LD_BCN_ACCTSEL_T \n");
        sqlText.append("SELECT ?, \n");
        sqlText.append("    head.univ_fiscal_yr, \n");
        sqlText.append("    head.fin_coa_cd, \n");
        sqlText.append("    head.account_nbr, \n");
        sqlText.append("    head.sub_acct_nbr, \n");
        sqlText.append("    head.fdoc_nbr, \n");
        sqlText.append("    1, \n");
        sqlText.append("    head.org_level_cd, \n");
        sqlText.append("    NULL, \n");
        sqlText.append("    NULL, \n");
        sqlText.append("    fshd.fdoc_status_cd, \n");
        sqlText.append("    '', \n");
        sqlText.append("    NULL \n");
        sqlText.append("FROM LD_BCNSTR_HDR_T head, \n");
        sqlText.append("    CA_ACCT_DELEGATE_T adel, \n");
        sqlText.append("    FS_DOC_HEADER_T fshd \n");
        sqlText.append("WHERE head.univ_fiscal_yr = ? \n");
        sqlText.append("  AND adel.acct_dlgt_unvl_id = ? \n");
        sqlText.append("  AND adel.acct_dlgt_actv_cd = 'Y' \n");
        sqlText.append("  AND adel.fdoc_typ_cd in (?, ?)  \n");
        sqlText.append("  AND head.fin_coa_cd = adel.fin_coa_cd \n");
        sqlText.append("  AND head.account_nbr = adel.account_nbr \n");
        sqlText.append("  AND fshd.fdoc_nbr = head.fdoc_nbr \n");
        sqlText.append("UNION \n");
        sqlText.append("SELECT ?, \n");
        sqlText.append("    head.univ_fiscal_yr, \n");
        sqlText.append("    head.fin_coa_cd, \n");
        sqlText.append("    head.account_nbr, \n");
        sqlText.append("    head.sub_acct_nbr, \n");
        sqlText.append("    head.fdoc_nbr, \n");
        sqlText.append("    1, \n");
        sqlText.append("    head.org_level_cd, \n");
        sqlText.append("    NULL, \n");
        sqlText.append("    NULL, \n");
        sqlText.append("    fshd.fdoc_status_cd, \n");
        sqlText.append("    '', \n");
        sqlText.append("    NULL \n");
        sqlText.append("FROM LD_BCNSTR_HDR_T head, \n");
        sqlText.append("    CA_ACCOUNT_T acct, \n");
        sqlText.append("    FS_DOC_HEADER_T fshd \n");
        sqlText.append("WHERE head.univ_fiscal_yr = ? \n");
        sqlText.append("  AND acct.acct_fsc_ofc_uid = ? \n");
        sqlText.append("  AND head.fin_coa_cd = acct.fin_coa_cd \n");
        sqlText.append("  AND head.account_nbr = acct.account_nbr \n");
        sqlText.append("  AND fshd.fdoc_nbr = head.fdoc_nbr \n");

        buildAccountManagerDelegateListTemplates[0] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // assign org for the account's current level
        sqlText.append("UPDATE LD_BCN_ACCTSEL_T asel \n");
        sqlText.append("SET org_fin_coa_cd =  \n");
        sqlText.append("    (SELECT h1.org_fin_coa_cd \n");
        sqlText.append("    FROM LD_BCN_ACCT_ORG_HIER_T h1 \n");
        sqlText.append("    WHERE asel.univ_fiscal_yr = h1.univ_fiscal_yr \n");
        sqlText.append("      AND asel.fin_coa_cd = h1.fin_coa_cd \n");
        sqlText.append("      AND asel.account_nbr = h1.account_nbr \n");
        sqlText.append("      AND asel.org_level_cd = h1.org_level_cd), \n");
        sqlText.append("    org_cd =  \n");
        sqlText.append("    (SELECT h1.org_cd \n");
        sqlText.append("    FROM LD_BCN_ACCT_ORG_HIER_T h1 \n");
        sqlText.append("    WHERE asel.univ_fiscal_yr = h1.univ_fiscal_yr \n");
        sqlText.append("      AND asel.fin_coa_cd = h1.fin_coa_cd \n");
        sqlText.append("      AND asel.account_nbr = h1.account_nbr \n");
        sqlText.append("      AND asel.org_level_cd = h1.org_level_cd) \n");
        sqlText.append("WHERE asel.person_unvl_id = ? \n");
        sqlText.append("AND EXISTS (SELECT * \n");
        sqlText.append("    FROM LD_BCN_ACCT_ORG_HIER_T h2 \n");
        sqlText.append("    WHERE asel.univ_fiscal_yr = h2.univ_fiscal_yr \n");
        sqlText.append("      AND asel.fin_coa_cd = h2.fin_coa_cd \n");
        sqlText.append("      AND asel.account_nbr = h2.account_nbr \n");
        sqlText.append("      AND asel.org_level_cd = h2.org_level_cd) \n");

        buildAccountManagerDelegateListTemplates[1] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // assign org for accounts at level 0
        sqlText.append("UPDATE LD_BCN_ACCTSEL_T asel \n");
        sqlText.append("SET org_fin_coa_cd =  \n");
        sqlText.append("    (SELECT r1.rpts_to_fin_coa_cd \n");
        sqlText.append("    FROM LD_BCN_ACCT_RPTS_T r1 \n");
        sqlText.append("    WHERE asel.fin_coa_cd = r1.fin_coa_cd \n");
        sqlText.append("      AND asel.account_nbr = r1.account_nbr), \n");
        sqlText.append("    org_cd =  \n");
        sqlText.append("    (SELECT r1.rpts_to_org_cd \n");
        sqlText.append("    FROM LD_BCN_ACCT_RPTS_T r1 \n");
        sqlText.append("    WHERE asel.fin_coa_cd = r1.fin_coa_cd \n");
        sqlText.append("      AND asel.account_nbr = r1.account_nbr) \n");
        sqlText.append("WHERE asel.person_unvl_id = ? \n");
        sqlText.append("  AND asel.org_level_cd = 0 \n");
        sqlText.append("  AND EXISTS (select * \n");
        sqlText.append("    FROM LD_BCN_ACCT_RPTS_T r2 \n");
        sqlText.append("    WHERE asel.fin_coa_cd = r2.fin_coa_cd \n");
        sqlText.append("      AND asel.account_nbr = r2.account_nbr)  \n");

        buildAccountManagerDelegateListTemplates[2] = sqlText.toString();
        sqlText.delete(0, sqlText.length());
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.OrganizationBCDocumentSearchDao#buildAccountSelectPullList(java.lang.String,
     *      java.lang.Integer)
     */
    public int buildAccountSelectPullList(String principalName, Integer universityFiscalYear) {
        LOG.debug("buildAccountSelectPullList() started");

        int rowsAffected = getSimpleJdbcTemplate().update(buildAccountSelectPullListTemplates[0], principalName, universityFiscalYear, principalName, universityFiscalYear);
        return rowsAffected;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.OrganizationBCDocumentSearchDao#buildBudgetedAccountsAbovePointsOfView(java.lang.String,
     *      java.lang.Integer, java.lang.String, java.lang.String)
     */
    public int buildBudgetedAccountsAbovePointsOfView(String principalName, Integer universityFiscalYear, String chartOfAccountsCode, String organizationCode) {
        LOG.debug("buildBudgetedAccountsAbovePointsOfView() started");

        int rowsAffected = getSimpleJdbcTemplate().update(buildBudgetedAccountsAbovePointsOfView[0], principalName, principalName, universityFiscalYear, chartOfAccountsCode, organizationCode);
        return rowsAffected;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.OrganizationBCDocumentSearchDao#buildAccountManagerDelegateList(java.lang.String,
     *      java.lang.Integer)
     */
    public int buildAccountManagerDelegateList(String principalName, Integer universityFiscalYear) {
        int rowsAffected = getSimpleJdbcTemplate().update(buildAccountManagerDelegateListTemplates[0], principalName, universityFiscalYear, principalName, KFSConstants.FinancialDocumentTypeCodes.BUDGET_CONSTRUCTION, BCConstants.DOCUMENT_TYPE_CODE_ALL, principalName, universityFiscalYear, principalName);

        // update level chart and org
        getSimpleJdbcTemplate().update(buildAccountManagerDelegateListTemplates[1], principalName);
        getSimpleJdbcTemplate().update(buildAccountManagerDelegateListTemplates[2], principalName);

        return rowsAffected;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.OrganizationBCDocumentSearchDao#cleanAccountSelectPullList(java.lang.String)
     */
    public void cleanAccountSelectPullList(String principalName) {
        clearTempTableByUnvlId("LD_BCN_ACCTSEL_T", "PERSON_UNVL_ID", principalName);
    }

}

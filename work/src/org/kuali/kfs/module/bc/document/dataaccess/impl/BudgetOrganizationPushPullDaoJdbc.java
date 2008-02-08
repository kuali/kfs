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
import org.kuali.core.util.Guid;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.dao.BudgetOrganizationPushPullDao;

/**
 * Implements BudgetOrganizationPushPullDao using raw SQL and populating temporary tables with the potential set of documents to
 * push down or pull up. The temporary tables are then used to drive the entire push down or pull up process. First, an attempt is
 * made to place budget locks on each document. Successfully locked documents are then pushed down or pulled up by setting the
 * associated BudgetConstructionHeader (ld_bcnstr_hdr_t) row with the appropriate level attribute values and releasing the locks.
 */
public class BudgetOrganizationPushPullDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetOrganizationPushPullDao {

    private static String[] pullupSelectedOrganizationDocumentsTemplates = new String[8];

    // private static String[] pushdownSelectedOrganizationDocumentsTemplates = new String[2];

    @RawSQL
    public BudgetOrganizationPushPullDaoJdbc() {

        // get accounts for selected orgs and attach the pull_flag setting
        StringBuilder sqlText = new StringBuilder(1000);
        sqlText.append("INSERT INTO ld_bcn_doc_pullup01_mt \n");
        sqlText.append(" (SESID, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SORG_FIN_COA_CD, SORG_CD, PULL_FLAG) \n");
        sqlText.append("SELECT  ?, hier.univ_fiscal_yr, hier.fin_coa_cd, hier.account_nbr, \n");
        sqlText.append("    hier.org_fin_coa_cd, hier.org_cd, pull.pull_flag \n");
        sqlText.append("FROM ld_bcn_pullup_t pull, ld_bcn_acct_org_hier_t hier  \n");
        sqlText.append("WHERE pull.pull_flag > 0 \n");
        sqlText.append("  AND pull.person_unvl_id = ? \n");
        sqlText.append("  AND hier.univ_fiscal_yr = ? \n");
        sqlText.append("  AND hier.org_fin_coa_cd = pull.fin_coa_cd \n");
        sqlText.append("  AND hier.org_cd = pull.org_cd \n");
        pullupSelectedOrganizationDocumentsTemplates[0] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // get the point of view record for each account and attach pull_flag again
        sqlText.append("INSERT INTO ld_bcn_doc_pullup02_mt \n");
        sqlText.append("  (SESID, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, ORG_LEVEL_CD, \n");
        sqlText.append("   ORG_FIN_COA_CD, ORG_CD, SORG_FIN_COA_CD, SORG_CD, PULL_FLAG) \n");
        sqlText.append("SELECT  ?, hier.univ_fiscal_yr, hier.fin_coa_cd, hier.account_nbr, hier.org_level_cd, \n");
        sqlText.append("        hier.org_fin_coa_cd, hier.org_cd, sel.sorg_fin_coa_cd, sel.sorg_cd, sel.pull_flag \n");
        sqlText.append("FROM ld_bcn_acct_org_hier_t hier, ld_bcn_doc_pullup01_mt sel \n");
        sqlText.append("WHERE sel.SESID  = ? \n");
        sqlText.append("  AND hier.org_fin_coa_cd = ? \n");
        sqlText.append("  AND hier.org_cd = ? \n");
        sqlText.append("  AND hier.univ_fiscal_yr = sel.univ_fiscal_yr \n");
        sqlText.append("  AND hier.fin_coa_cd = sel.fin_coa_cd \n");
        sqlText.append("  AND hier.account_nbr = sel.account_nbr \n");
        pullupSelectedOrganizationDocumentsTemplates[1] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // populate list of accounts to update based on pull_flag setting
        // doc numbers and acct,subacct pairs are candidate keys
        // build list with doc numbers since it starts the clustered index

        // get list for BOTH direct reports and org subtree pull_flag = 3
        sqlText.append("INSERT INTO ld_bcn_doc_pullup03_mt \n");
        sqlText.append("  (SESID, FDOC_NBR, ORG_LEVEL_CD, ORG_FIN_COA_CD, ORG_CD) \n");
        sqlText.append("SELECT  ?, head.fdoc_nbr, pv.org_level_cd, pv.org_fin_coa_cd, pv.org_cd \n");
        sqlText.append("FROM ld_bcn_doc_pullup02_mt pv, ld_bcnstr_hdr_t head \n");
        sqlText.append("WHERE pv.SESID = ? \n");
        sqlText.append("  AND head.univ_fiscal_yr = pv.univ_fiscal_yr \n");
        sqlText.append("  AND head.fin_coa_cd = pv.fin_coa_cd \n");
        sqlText.append("  AND head.account_nbr = pv.account_nbr \n");
        sqlText.append("  AND head.org_level_cd < pv.org_level_cd \n");
        sqlText.append("  AND pv.pull_flag = ? \n");
        pullupSelectedOrganizationDocumentsTemplates[2] = sqlText.toString();
        sqlText.delete(0, sqlText.length());


        // add to list for direct reports only pull_flag = 1
        sqlText.append("INSERT INTO ld_bcn_doc_pullup03_mt \n");
        sqlText.append("  (SESID, FDOC_NBR, ORG_LEVEL_CD, ORG_FIN_COA_CD, ORG_CD) \n");
        sqlText.append("SELECT ?, head.fdoc_nbr, pv.org_level_cd, pv.org_fin_coa_cd, pv.org_cd \n");
        sqlText.append("FROM ld_bcn_doc_pullup02_mt pv, ld_bcn_acct_rpts_t bar, ld_bcnstr_hdr_t head \n");
        sqlText.append("WHERE pv.SESID = ? \n");
        sqlText.append("  AND head.univ_fiscal_yr = pv.univ_fiscal_yr \n");
        sqlText.append("  AND head.fin_coa_cd = pv.fin_coa_cd \n");
        sqlText.append("  AND head.account_nbr = pv.account_nbr \n");
        sqlText.append("  AND head.org_level_cd < pv.org_level_cd \n");
        sqlText.append("  AND pv.pull_flag = ? \n");
        sqlText.append("  AND bar.fin_coa_cd = pv.fin_coa_cd \n");
        sqlText.append("  AND bar.account_nbr = pv.account_nbr \n");
        sqlText.append("  AND bar.rpts_to_fin_coa_cd = pv.sorg_fin_coa_cd \n");
        sqlText.append("  AND bar.rpts_to_org_cd = pv.sorg_cd \n");
        pullupSelectedOrganizationDocumentsTemplates[3] = sqlText.toString();
        sqlText.delete(0, sqlText.length());


        // add to list for org subtree only - pull_flag = 2
        sqlText.append("INSERT INTO ld_bcn_doc_pullup03_mt \n");
        sqlText.append("  (SESID, FDOC_NBR, ORG_LEVEL_CD, ORG_FIN_COA_CD, ORG_CD) \n");
        sqlText.append("SELECT ?, head.fdoc_nbr, pv.org_level_cd, pv.org_fin_coa_cd, pv.org_cd \n");
        sqlText.append("FROM ld_bcn_doc_pullup02_mt pv, ld_bcn_acct_rpts_t bar, ld_bcnstr_hdr_t head \n");
        sqlText.append("WHERE pv.SESID = ? \n");
        sqlText.append("  AND head.univ_fiscal_yr = pv.univ_fiscal_yr \n");
        sqlText.append("  AND head.fin_coa_cd = pv.fin_coa_cd \n");
        sqlText.append("  AND head.account_nbr = pv.account_nbr \n");
        sqlText.append("  AND head.org_level_cd < pv.org_level_cd \n");
        sqlText.append("  AND pv.pull_flag = ? \n");
        sqlText.append("  AND bar.fin_coa_cd = pv.fin_coa_cd \n");
        sqlText.append("  AND bar.account_nbr = pv.account_nbr \n");
        sqlText.append("  AND (bar.rpts_to_org_cd != pv.sorg_cd \n");
        sqlText.append("       OR bar.rpts_to_fin_coa_cd != pv.sorg_fin_coa_cd) \n");
        pullupSelectedOrganizationDocumentsTemplates[4] = sqlText.toString();
        sqlText.delete(0, sqlText.length());


        // issue budget locks
        sqlText.append("UPDATE ld_bcnstr_hdr_t head \n");
        sqlText.append("SET bdgt_lock_usr_id = ? \n");
        sqlText.append("WHERE exists \n");
        sqlText.append("   (SELECT * \n");
        sqlText.append("   FROM ld_bcn_doc_pullup03_mt ul \n");
        sqlText.append("   WHERE ul.SESID = ? \n");
        sqlText.append("     AND head.fdoc_nbr = ul.fdoc_nbr \n");
        sqlText.append("     AND head.bdgt_lock_usr_id IS NULL) \n");
        pullupSelectedOrganizationDocumentsTemplates[5] = sqlText.toString();
        sqlText.delete(0, sqlText.length());


        // release budget locks where funding locks found - adhere to BC lock tree protocol
        sqlText.append("UPDATE ld_bcnstr_hdr_t head \n");
        sqlText.append("SET bdgt_lock_usr_id = NULL \n");
        sqlText.append("WHERE exists \n");
        sqlText.append("   (SELECT * \n");
        sqlText.append("    FROM ld_bcn_doc_pullup03_mt ul \n");
        sqlText.append("    WHERE ul.SESID = ? \n");
        sqlText.append("      AND head.fdoc_nbr = ul.fdoc_nbr \n");
        sqlText.append("      AND head.bdgt_lock_usr_id = ?  \n");
        sqlText.append("      AND EXISTS \n");
        sqlText.append("          (SELECT * \n");
        sqlText.append("           FROM ld_bcn_fnd_lock_t fl \n");
        sqlText.append("           WHERE fl.univ_fiscal_yr = head.univ_fiscal_yr \n");
        sqlText.append("             AND fl.fin_coa_cd = head.fin_coa_cd  \n");
        sqlText.append("             AND fl.account_nbr = head.account_nbr  \n");
        sqlText.append("             AND fl.sub_acct_nbr = head.sub_acct_nbr)) \n");
        pullupSelectedOrganizationDocumentsTemplates[6] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // pullup and release budget locks - SQL92 version
        sqlText.append("UPDATE ld_bcnstr_hdr_t head \n");
        sqlText.append("SET org_level_cd = \n");
        sqlText.append("        (SELECT ul.org_level_cd \n");
        sqlText.append("         FROM ld_bcn_doc_pullup03_mt ul \n");
        sqlText.append("         WHERE ul.SESID = ? \n");
        sqlText.append("           AND head.fdoc_nbr = ul.fdoc_nbr \n");
        sqlText.append("           AND head.bdgt_lock_usr_id = ?), \n");
        sqlText.append("    org_coa_of_lvl_cd = \n");
        sqlText.append("        (SELECT ul.org_fin_coa_cd \n");
        sqlText.append("         FROM ld_bcn_doc_pullup03_mt ul \n");
        sqlText.append("         WHERE ul.SESID = ? \n");
        sqlText.append("           AND head.fdoc_nbr = ul.fdoc_nbr \n");
        sqlText.append("           AND head.bdgt_lock_usr_id = ?), \n");
        sqlText.append("    org_of_lvl_cd = \n");
        sqlText.append("        (SELECT ul.org_cd \n");
        sqlText.append("         FROM ld_bcn_doc_pullup03_mt ul \n");
        sqlText.append("         WHERE ul.SESID = ? \n");
        sqlText.append("           AND head.fdoc_nbr = ul.fdoc_nbr \n");
        sqlText.append("           AND head.bdgt_lock_usr_id = ?), \n");
        sqlText.append("    bdgt_lock_usr_id = NULL \n");
        sqlText.append("WHERE exists \n");
        sqlText.append("    (SELECT * \n");
        sqlText.append("     FROM ld_bcn_doc_pullup03_mt ul \n");
        sqlText.append("     WHERE ul.SESID = ? \n");
        sqlText.append("       AND head.fdoc_nbr = ul.fdoc_nbr \n");
        sqlText.append("       AND head.bdgt_lock_usr_id = ?) \n");
        pullupSelectedOrganizationDocumentsTemplates[7] = sqlText.toString();

    }

    /**
     * @see org.kuali.module.budget.dao.BudgetOrganizationPushPullDao#pullupSelectedOrganizationDocuments(java.lang.String,
     *      java.lang.Integer, java.lang.String, java.lang.String)
     */
    @RawSQL
    public void pullupSelectedOrganizationDocuments(String personUniversalIdentifier, Integer fiscalYear, String pointOfViewCharOfAccountsCode, String pointOfViewOrganizationCode) {

        String sessionId = new Guid().toString();

        // run the steps
        this.getSimpleJdbcTemplate().update(pullupSelectedOrganizationDocumentsTemplates[0], sessionId, personUniversalIdentifier, fiscalYear);
        this.getSimpleJdbcTemplate().update(pullupSelectedOrganizationDocumentsTemplates[1], sessionId, sessionId, pointOfViewCharOfAccountsCode, pointOfViewOrganizationCode);
        this.getSimpleJdbcTemplate().update(pullupSelectedOrganizationDocumentsTemplates[2], sessionId, sessionId, BCConstants.OrgSelControlOption.BOTH.getKey());
        this.getSimpleJdbcTemplate().update(pullupSelectedOrganizationDocumentsTemplates[3], sessionId, sessionId, BCConstants.OrgSelControlOption.ORG.getKey());
        this.getSimpleJdbcTemplate().update(pullupSelectedOrganizationDocumentsTemplates[4], sessionId, sessionId, BCConstants.OrgSelControlOption.SUBORG.getKey());
        this.getSimpleJdbcTemplate().update(pullupSelectedOrganizationDocumentsTemplates[5], personUniversalIdentifier, sessionId);
        this.getSimpleJdbcTemplate().update(pullupSelectedOrganizationDocumentsTemplates[6], sessionId, personUniversalIdentifier);
        this.getSimpleJdbcTemplate().update(pullupSelectedOrganizationDocumentsTemplates[7], sessionId, personUniversalIdentifier, sessionId, personUniversalIdentifier, sessionId, personUniversalIdentifier, sessionId, personUniversalIdentifier);

        // cleanup temp table space
        this.clearTempTableBySesId("ld_bcn_doc_pullup01_mt", "SESID", sessionId);
        this.clearTempTableBySesId("ld_bcn_doc_pullup02_mt", "SESID", sessionId);
        this.clearTempTableBySesId("ld_bcn_doc_pullup03_mt", "SESID", sessionId);

    }

    /**
     * @see org.kuali.module.budget.dao.BudgetOrganizationPushPullDao#pushdownSelectedOrganizationDocuments(java.lang.String,
     *      java.lang.Integer, java.lang.String, java.lang.String)
     */
    @RawSQL
    public void pushdownSelectedOrganizationDocuments(String personUniversalIdentifier, Integer FiscalYear, String pointOfViewCharOfAccountsCode, String pointOfViewOrganizationCode) {
        // TODO Auto-generated method stub

    }

}

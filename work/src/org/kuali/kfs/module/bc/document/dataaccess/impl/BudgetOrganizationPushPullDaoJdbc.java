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

import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCConstants.OrgSelControlOption;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetOrganizationPushPullDao;

/**
 * Implements BudgetOrganizationPushPullDao using raw SQL and populating temporary tables with the potential set of documents to
 * push down or pull up. The temporary tables are then used to drive the entire push down or pull up process. First, an attempt is
 * made to place budget locks on each document. Successfully locked documents are then pushed down or pulled up by setting the
 * associated BudgetConstructionHeader (LD_BCNSTR_HDR_T) row with the appropriate level attribute values and releasing the locks.
 */
public class BudgetOrganizationPushPullDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetOrganizationPushPullDao {

    protected static String[] pullupSelectedOrganizationDocumentsTemplates = new String[8];
    protected static String[] pushdownSelectedOrganizationDocumentsTemplates = new String[11];
    protected static String[] accountSelectBudgetedDocumentsPullUpTemplates = new String[2];
    protected static String[] accountSelectBudgetedDocumentsPushDownTemplates = new String[1];

    public BudgetOrganizationPushPullDaoJdbc() {

        // get accounts for selected orgs and attach the pull_flag setting
        StringBuilder sqlText = new StringBuilder(1000);
        sqlText.append("INSERT INTO LD_BCN_DOC_PULLUP01_MT \n");
        sqlText.append(" (SESID, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SORG_FIN_COA_CD, SORG_CD, PULL_FLAG) \n");
        sqlText.append("SELECT  ?, hier.univ_fiscal_yr, hier.fin_coa_cd, hier.account_nbr, \n");
        sqlText.append("    hier.org_fin_coa_cd, hier.org_cd, pull.pull_flag \n");
        sqlText.append("FROM LD_BCN_PULLUP_T pull, LD_BCN_ACCT_ORG_HIER_T hier  \n");
        sqlText.append("WHERE pull.pull_flag > 0 \n");
        sqlText.append("  AND pull.person_unvl_id = ? \n");
        sqlText.append("  AND hier.univ_fiscal_yr = ? \n");
        sqlText.append("  AND hier.org_fin_coa_cd = pull.fin_coa_cd \n");
        sqlText.append("  AND hier.org_cd = pull.org_cd \n");
        pullupSelectedOrganizationDocumentsTemplates[0] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // get the point of view record for each account and attach pull_flag again
        sqlText.append("INSERT INTO LD_BCN_DOC_PULLUP02_MT \n");
        sqlText.append("  (SESID, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, ORG_LEVEL_CD, \n");
        sqlText.append("   ORG_FIN_COA_CD, ORG_CD, SORG_FIN_COA_CD, SORG_CD, PULL_FLAG) \n");
        sqlText.append("SELECT  ?, hier.univ_fiscal_yr, hier.fin_coa_cd, hier.account_nbr, hier.org_level_cd, \n");
        sqlText.append("        hier.org_fin_coa_cd, hier.org_cd, sel.sorg_fin_coa_cd, sel.sorg_cd, sel.pull_flag \n");
        sqlText.append("FROM LD_BCN_ACCT_ORG_HIER_T hier, LD_BCN_DOC_PULLUP01_MT sel \n");
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
        sqlText.append("INSERT INTO LD_BCN_DOC_PULLUP03_MT \n");
        sqlText.append("  (SESID, FDOC_NBR, ORG_LEVEL_CD, ORG_FIN_COA_CD, ORG_CD) \n");
        sqlText.append("SELECT  ?, head.fdoc_nbr, pv.org_level_cd, pv.org_fin_coa_cd, pv.org_cd \n");
        sqlText.append("FROM LD_BCN_DOC_PULLUP02_MT pv, LD_BCNSTR_HDR_T head \n");
        sqlText.append("WHERE pv.SESID = ? \n");
        sqlText.append("  AND head.univ_fiscal_yr = pv.univ_fiscal_yr \n");
        sqlText.append("  AND head.fin_coa_cd = pv.fin_coa_cd \n");
        sqlText.append("  AND head.account_nbr = pv.account_nbr \n");
        sqlText.append("  AND head.org_level_cd < pv.org_level_cd \n");
        sqlText.append("  AND pv.pull_flag = ? \n");
        pullupSelectedOrganizationDocumentsTemplates[2] = sqlText.toString();
        sqlText.delete(0, sqlText.length());


        // add to list for direct reports only pull_flag = 1
        sqlText.append("INSERT INTO LD_BCN_DOC_PULLUP03_MT \n");
        sqlText.append("  (SESID, FDOC_NBR, ORG_LEVEL_CD, ORG_FIN_COA_CD, ORG_CD) \n");
        sqlText.append("SELECT ?, head.fdoc_nbr, pv.org_level_cd, pv.org_fin_coa_cd, pv.org_cd \n");
        sqlText.append("FROM LD_BCN_DOC_PULLUP02_MT pv, LD_BCN_ACCT_RPTS_T bar, LD_BCNSTR_HDR_T head \n");
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
        sqlText.append("INSERT INTO LD_BCN_DOC_PULLUP03_MT \n");
        sqlText.append("  (SESID, FDOC_NBR, ORG_LEVEL_CD, ORG_FIN_COA_CD, ORG_CD) \n");
        sqlText.append("SELECT ?, head.fdoc_nbr, pv.org_level_cd, pv.org_fin_coa_cd, pv.org_cd \n");
        sqlText.append("FROM LD_BCN_DOC_PULLUP02_MT pv, LD_BCN_ACCT_RPTS_T bar, LD_BCNSTR_HDR_T head \n");
        sqlText.append("WHERE pv.SESID = ? \n");
        sqlText.append("  AND head.univ_fiscal_yr = pv.univ_fiscal_yr \n");
        sqlText.append("  AND head.fin_coa_cd = pv.fin_coa_cd \n");
        sqlText.append("  AND head.account_nbr = pv.account_nbr \n");
        sqlText.append("  AND head.org_level_cd < pv.org_level_cd \n");
        sqlText.append("  AND pv.pull_flag = ? \n");
        sqlText.append("  AND bar.fin_coa_cd = pv.fin_coa_cd \n");
        sqlText.append("  AND bar.account_nbr = pv.account_nbr \n");
        sqlText.append("  AND (bar.rpts_to_org_cd <> pv.sorg_cd \n");
        sqlText.append("       OR bar.rpts_to_fin_coa_cd <> pv.sorg_fin_coa_cd) \n");
        pullupSelectedOrganizationDocumentsTemplates[4] = sqlText.toString();
        sqlText.delete(0, sqlText.length());


        // issue budget locks
        sqlText.append("UPDATE LD_BCNSTR_HDR_T head \n");
        sqlText.append("SET bdgt_lock_usr_id = ? \n");
        sqlText.append("WHERE exists \n");
        sqlText.append("   (SELECT * \n");
        sqlText.append("   FROM LD_BCN_DOC_PULLUP03_MT ul \n");
        sqlText.append("   WHERE ul.SESID = ? \n");
        sqlText.append("     AND head.fdoc_nbr = ul.fdoc_nbr \n");
        sqlText.append("     AND head.bdgt_lock_usr_id IS NULL) \n");
        pullupSelectedOrganizationDocumentsTemplates[5] = sqlText.toString();
        sqlText.delete(0, sqlText.length());


        // release budget locks where funding locks found - adhere to BC lock tree protocol
        sqlText.append("UPDATE LD_BCNSTR_HDR_T head \n");
        sqlText.append("SET bdgt_lock_usr_id = NULL \n");
        sqlText.append("WHERE exists \n");
        sqlText.append("   (SELECT * \n");
        sqlText.append("    FROM LD_BCN_DOC_PULLUP03_MT ul \n");
        sqlText.append("    WHERE ul.SESID = ? \n");
        sqlText.append("      AND head.fdoc_nbr = ul.fdoc_nbr \n");
        sqlText.append("      AND head.bdgt_lock_usr_id = ?  \n");
        sqlText.append("      AND EXISTS \n");
        sqlText.append("          (SELECT * \n");
        sqlText.append("           FROM LD_BCN_FND_LOCK_T fl \n");
        sqlText.append("           WHERE fl.univ_fiscal_yr = head.univ_fiscal_yr \n");
        sqlText.append("             AND fl.fin_coa_cd = head.fin_coa_cd  \n");
        sqlText.append("             AND fl.account_nbr = head.account_nbr  \n");
        sqlText.append("             AND fl.sub_acct_nbr = head.sub_acct_nbr)) \n");
        pullupSelectedOrganizationDocumentsTemplates[6] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // pullup and release budget locks - SQL92 version
        sqlText.append("UPDATE LD_BCNSTR_HDR_T head \n");
        sqlText.append("SET org_level_cd = \n");
        sqlText.append("        (SELECT ul.org_level_cd \n");
        sqlText.append("         FROM LD_BCN_DOC_PULLUP03_MT ul \n");
        sqlText.append("         WHERE ul.SESID = ? \n");
        sqlText.append("           AND head.fdoc_nbr = ul.fdoc_nbr \n");
        sqlText.append("           AND head.bdgt_lock_usr_id = ?), \n");
        sqlText.append("    org_coa_of_lvl_cd = \n");
        sqlText.append("        (SELECT ul.org_fin_coa_cd \n");
        sqlText.append("         FROM LD_BCN_DOC_PULLUP03_MT ul \n");
        sqlText.append("         WHERE ul.SESID = ? \n");
        sqlText.append("           AND head.fdoc_nbr = ul.fdoc_nbr \n");
        sqlText.append("           AND head.bdgt_lock_usr_id = ?), \n");
        sqlText.append("    org_of_lvl_cd = \n");
        sqlText.append("        (SELECT ul.org_cd \n");
        sqlText.append("         FROM LD_BCN_DOC_PULLUP03_MT ul \n");
        sqlText.append("         WHERE ul.SESID = ? \n");
        sqlText.append("           AND head.fdoc_nbr = ul.fdoc_nbr \n");
        sqlText.append("           AND head.bdgt_lock_usr_id = ?), \n");
        sqlText.append("    bdgt_lock_usr_id = NULL \n");
        sqlText.append("WHERE exists \n");
        sqlText.append("    (SELECT * \n");
        sqlText.append("     FROM LD_BCN_DOC_PULLUP03_MT ul \n");
        sqlText.append("     WHERE ul.SESID = ? \n");
        sqlText.append("       AND head.fdoc_nbr = ul.fdoc_nbr \n");
        sqlText.append("       AND head.bdgt_lock_usr_id = ?) \n");
        pullupSelectedOrganizationDocumentsTemplates[7] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // Pushdown steps start here

        // get accounts for selected orgs. and attach the pull_flag setting
        sqlText.append("INSERT INTO LD_BCN_DOC_PUSHDOWN01_MT \n");
        sqlText.append(" (SESID, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, ORG_LEVEL_CD, ORG_FIN_COA_CD, ORG_CD, PULL_FLAG) \n");
        sqlText.append("SELECT  ?, hier.univ_fiscal_yr,  hier.fin_coa_cd, hier.account_nbr, hier.org_level_cd, \n");
        sqlText.append("        hier.org_fin_coa_cd, hier.org_cd, push.pull_flag \n");
        sqlText.append("FROM LD_BCN_PULLUP_T push, LD_BCN_ACCT_ORG_HIER_T hier \n");
        sqlText.append("WHERE push.pull_flag > 0 \n");
        sqlText.append("  AND push.person_unvl_id = ? \n");
        sqlText.append("  AND hier.univ_fiscal_yr = ? \n");
        sqlText.append("  AND hier.org_fin_coa_cd = push.fin_coa_cd \n");
        sqlText.append("  AND hier.org_cd = push.org_cd \n");
        pushdownSelectedOrganizationDocumentsTemplates[0] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // get selection coa, orgs and levels at point of view */
        sqlText.append("INSERT INTO LD_BCN_DOC_PUSHDOWN02_MT \n");
        sqlText.append(" (SESID, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, ORG_LEVEL_CD, \n");
        sqlText.append("  SEL_ORG_LVL, SEL_ORGFIN_COA, SEL_ORG, SEL_PULLFLAG) \n");
        sqlText.append("SELECT DISTINCT ?, hier.univ_fiscal_yr, hier.fin_coa_cd, hier.account_nbr, hier.org_level_cd, \n");
        sqlText.append("                sel.org_level_cd, sel.org_fin_coa_cd, sel.org_cd, sel.pull_flag \n");
        sqlText.append("FROM LD_BCN_ACCT_ORG_HIER_T hier, LD_BCN_DOC_PUSHDOWN01_MT sel \n");
        sqlText.append("WHERE sel.SESID = ? \n");
        sqlText.append("  AND hier.org_fin_coa_cd = ? \n");
        sqlText.append("  AND hier.org_cd = ? \n");
        sqlText.append("  AND hier.univ_fiscal_yr = sel.univ_fiscal_yr \n");
        sqlText.append("  AND hier.fin_coa_cd = sel.fin_coa_cd  \n");
        sqlText.append("  AND hier.account_nbr = sel.account_nbr \n");
        pushdownSelectedOrganizationDocumentsTemplates[1] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // add the level 1 coa and org to the selection list
        sqlText.append("INSERT INTO LD_BCN_DOC_PUSHDOWN03_MT \n");
        sqlText.append(" (SESID,UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,ORG_LEVEL_CD, \n");
        sqlText.append("  SEL_ORG_LVL,SEL_ORGFIN_COA,SEL_ORG,SEL_PULLFLAG,LONE_ORGFIN_COA,LONE_ORG) \n");
        sqlText.append("SELECT DISTINCT  ?, sel.univ_fiscal_yr, sel.fin_coa_cd, sel.account_nbr, sel.org_level_cd, \n");
        sqlText.append("                 sel.sel_org_lvl, sel.sel_orgfin_coa, sel.sel_org, sel.sel_pullflag, hier.org_fin_coa_cd, hier.org_cd \n");
        sqlText.append("FROM LD_BCN_ACCT_ORG_HIER_T hier, LD_BCN_DOC_PUSHDOWN02_MT sel \n");
        sqlText.append("WHERE sel.SESID = ? \n");
        sqlText.append("  AND hier.univ_fiscal_yr = sel.univ_fiscal_yr \n");
        sqlText.append("  AND hier.fin_coa_cd = sel.fin_coa_cd  \n");
        sqlText.append("  AND hier.account_nbr = sel.account_nbr  \n");
        sqlText.append("  AND hier.org_level_cd = 1 \n");
        pushdownSelectedOrganizationDocumentsTemplates[2] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // Note: (may need to/should as we scale) replace fy,coa,acct,sacct with fdoc_nbr in LD_BCN_DOC_PUSHDOWN04_MT
        // and use it in later steps instead of the candidate key
        // not sure why it uses the candidate key in the first place, may have been due to
        // the use of origin code as the first field in the pkey index, a secondary index exists on coa,acct.
        // Since origin code is no longer used fdoc_nbr may be the better choice
        // since it now starts the pkey index in KFS

        /* get list of accounts to push for sel_pullflag in 1,3,4,5 */
        sqlText.append("INSERT INTO LD_BCN_DOC_PUSHDOWN04_MT \n");
        sqlText.append(" (SESID, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, ORG_LEVEL_CD, \n");
        sqlText.append("  SEL_ORG_LVL, SEL_ORGFIN_COA, SEL_ORG, SEL_PULLFLAG, LONE_ORGFIN_COA, LONE_ORG) \n");
        sqlText.append("SELECT  ?, head.univ_fiscal_yr, head.fin_coa_cd, head.account_nbr, head.sub_acct_nbr, pv.org_level_cd, \n");
        sqlText.append("        pv.sel_org_lvl, pv.sel_orgfin_coa, pv.sel_org, pv.sel_pullflag, pv.lone_orgfin_coa, pv.lone_org \n");
        sqlText.append("FROM LD_BCN_DOC_PUSHDOWN03_MT pv, LD_BCNSTR_HDR_T head \n");
        sqlText.append("WHERE pv.SESID = ? \n");
        sqlText.append("  AND pv.sel_pullflag IN (?, ?, ?, ?) \n");
        sqlText.append("  AND head.univ_fiscal_yr = pv.univ_fiscal_yr \n");
        sqlText.append("  AND head.fin_coa_cd = pv.fin_coa_cd \n");
        sqlText.append("  AND head.account_nbr = pv.account_nbr \n");
        sqlText.append("  AND head.org_level_cd = pv.org_level_cd \n");
        pushdownSelectedOrganizationDocumentsTemplates[3] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // get list of accounts to push for sel_pullflag = 2
        sqlText.append("INSERT INTO LD_BCN_DOC_PUSHDOWN04_MT \n");
        sqlText.append(" (SESID, UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, ORG_LEVEL_CD, \n");
        sqlText.append("  SEL_ORG_LVL, SEL_ORGFIN_COA, SEL_ORG, SEL_PULLFLAG, LONE_ORGFIN_COA, LONE_ORG) \n");
        sqlText.append("SELECT ?, head.univ_fiscal_yr, head.fin_coa_cd, head.account_nbr, head.sub_acct_nbr, pv.org_level_cd, \n");
        sqlText.append("       pv.sel_org_lvl, pv.sel_orgfin_coa, pv.sel_org, pv.sel_pullflag, pv.lone_orgfin_coa, pv.lone_org \n");
        sqlText.append("FROM LD_BCN_DOC_PUSHDOWN03_MT pv, LD_BCNSTR_HDR_T head, LD_BCN_ACCT_RPTS_T rpts \n");
        sqlText.append("WHERE pv.SESID = ? \n");
        sqlText.append("  AND pv.sel_pullflag = ? \n");
        sqlText.append("  AND head.univ_fiscal_yr = pv.univ_fiscal_yr \n");
        sqlText.append("  AND head.fin_coa_cd = pv.fin_coa_cd \n");
        sqlText.append("  AND head.account_nbr = pv.account_nbr \n");
        sqlText.append("  AND head.org_level_cd = pv.org_level_cd \n");
        sqlText.append("  AND pv.fin_coa_cd = rpts.fin_coa_cd \n");
        sqlText.append("  AND pv.account_nbr = rpts.account_nbr \n");
        sqlText.append("  AND pv.sel_orgfin_coa = rpts.rpts_to_fin_coa_cd \n");
        sqlText.append("  AND pv.sel_org = rpts.rpts_to_org_cd \n");
        pushdownSelectedOrganizationDocumentsTemplates[4] = sqlText.toString();
        sqlText.delete(0, sqlText.length());


        // issue budget locks on the set to update
        sqlText.append("UPDATE LD_BCNSTR_HDR_T head \n");
        sqlText.append("SET bdgt_lock_usr_id = ? \n");
        sqlText.append("WHERE exists \n");
        sqlText.append("   (SELECT * \n");
        sqlText.append("    FROM LD_BCN_DOC_PUSHDOWN04_MT pv \n");
        sqlText.append("    WHERE pv.SESID = ? \n");
        sqlText.append("      AND head.univ_fiscal_yr = pv.univ_fiscal_yr \n");
        sqlText.append("      AND head.fin_coa_cd = pv.fin_coa_cd \n");
        sqlText.append("      AND head.account_nbr = pv.account_nbr \n");
        sqlText.append("      AND head.sub_acct_nbr = pv.sub_acct_nbr \n");
        sqlText.append("      AND head.bdgt_lock_usr_id IS NULL) \n");
        pushdownSelectedOrganizationDocumentsTemplates[5] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        /* release budget locks where funding locks are found */
        sqlText.append("UPDATE LD_BCNSTR_HDR_T head \n");
        sqlText.append("SET bdgt_lock_usr_id = NULL \n");
        sqlText.append("WHERE exists \n");
        sqlText.append("   (SELECT * \n");
        sqlText.append("    FROM LD_BCN_DOC_PUSHDOWN04_MT pv \n");
        sqlText.append("    WHERE pv.SESID = ? \n");
        sqlText.append("        AND head.univ_fiscal_yr = pv.univ_fiscal_yr \n");
        sqlText.append("        AND head.fin_coa_cd = pv.fin_coa_cd \n");
        sqlText.append("        AND head.account_nbr = pv.account_nbr \n");
        sqlText.append("        AND head.sub_acct_nbr = pv.sub_acct_nbr \n");
        sqlText.append("        AND head.bdgt_lock_usr_id = ? \n");
        sqlText.append("        AND EXISTS \n");
        sqlText.append("           (SELECT  * \n");
        sqlText.append("            FROM LD_BCN_FND_LOCK_T fl \n");
        sqlText.append("            WHERE fl.univ_fiscal_yr = head.univ_fiscal_yr \n");
        sqlText.append("              AND fl.fin_coa_cd = head.fin_coa_cd \n");
        sqlText.append("              AND fl.account_nbr = head.account_nbr \n");
        sqlText.append("              AND fl.sub_acct_nbr = head.sub_acct_nbr)) \n");
        pushdownSelectedOrganizationDocumentsTemplates[6] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // push the selected org's direct report accts to 0 and unlock as we go - sel_pullflag in 2, 3
        sqlText.append("UPDATE LD_BCNSTR_HDR_T head \n");
        sqlText.append("SET org_level_cd = 0, \n");
        sqlText.append("    org_coa_of_lvl_cd = NULL, \n");
        sqlText.append("    org_of_lvl_cd = NULL, \n");
        sqlText.append("    bdgt_lock_usr_id = NULL \n");
        sqlText.append("WHERE EXISTS \n");
        sqlText.append("    (SELECT * \n");
        sqlText.append("     FROM LD_BCN_DOC_PUSHDOWN04_MT pv, \n");
        sqlText.append("          LD_BCN_ACCT_RPTS_T rpts \n");
        sqlText.append("     WHERE pv.SESID = ? \n");
        sqlText.append("       AND pv.sel_pullflag IN (?, ?) \n");
        sqlText.append("       AND head.univ_fiscal_yr = pv.univ_fiscal_yr \n");
        sqlText.append("       AND head.fin_coa_cd = pv.fin_coa_cd \n");
        sqlText.append("       AND head.account_nbr = pv.account_nbr \n");
        sqlText.append("       AND head.sub_acct_nbr = pv.sub_acct_nbr \n");
        sqlText.append("       AND head.bdgt_lock_usr_id = ? \n");
        sqlText.append("       AND pv.fin_coa_cd = rpts.fin_coa_cd \n");
        sqlText.append("       AND pv.account_nbr = rpts.account_nbr \n");
        sqlText.append("       AND pv.sel_orgfin_coa = rpts.rpts_to_fin_coa_cd \n");
        sqlText.append("       AND pv.sel_org = rpts.rpts_to_org_cd) \n");
        pushdownSelectedOrganizationDocumentsTemplates[7] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // push the selected org(s) subtree accounts to its (selected org's) level - sel_pullflag IN (1, 3)
        // unlock as we go */
        sqlText.append("UPDATE LD_BCNSTR_HDR_T head \n");
        sqlText.append("SET org_level_cd = \n");
        sqlText.append("    (SELECT pv.sel_org_lvl \n");
        sqlText.append("     FROM LD_BCN_DOC_PUSHDOWN04_MT pv \n");
        sqlText.append("     WHERE pv.SESID = ? \n");
        sqlText.append("       AND pv.sel_pullflag IN (?, ?) \n");
        sqlText.append("       AND head.univ_fiscal_yr = pv.univ_fiscal_yr \n");
        sqlText.append("       AND head.fin_coa_cd = pv.fin_coa_cd \n");
        sqlText.append("       AND head.account_nbr = pv.account_nbr \n");
        sqlText.append("       AND head.sub_acct_nbr = pv.sub_acct_nbr \n");
        sqlText.append("       AND head.bdgt_lock_usr_id = ?), \n");
        sqlText.append("    org_coa_of_lvl_cd = \n");
        sqlText.append("    (SELECT pv.sel_orgfin_coa \n");
        sqlText.append("     FROM LD_BCN_DOC_PUSHDOWN04_MT pv \n");
        sqlText.append("     WHERE pv.SESID = ? \n");
        sqlText.append("       AND pv.sel_pullflag IN (?, ?) \n");
        sqlText.append("       AND head.univ_fiscal_yr = pv.univ_fiscal_yr \n");
        sqlText.append("       AND head.fin_coa_cd = pv.fin_coa_cd \n");
        sqlText.append("       AND head.account_nbr = pv.account_nbr \n");
        sqlText.append("       AND head.sub_acct_nbr = pv.sub_acct_nbr \n");
        sqlText.append("       AND head.bdgt_lock_usr_id = ?), \n");
        sqlText.append("    org_of_lvl_cd = \n");
        sqlText.append("    (SELECT pv.sel_org \n");
        sqlText.append("     FROM LD_BCN_DOC_PUSHDOWN04_MT pv \n");
        sqlText.append("     WHERE pv.SESID = ? \n");
        sqlText.append("       AND pv.sel_pullflag IN (?, ?) \n");
        sqlText.append("       AND head.univ_fiscal_yr = pv.univ_fiscal_yr \n");
        sqlText.append("       AND head.fin_coa_cd = pv.fin_coa_cd \n");
        sqlText.append("       AND head.account_nbr = pv.account_nbr \n");
        sqlText.append("       AND head.sub_acct_nbr = pv.sub_acct_nbr \n");
        sqlText.append("       AND head.bdgt_lock_usr_id = ?), \n");
        sqlText.append("    bdgt_lock_usr_id = NULL \n");
        sqlText.append("WHERE EXISTS \n");
        sqlText.append("    (SELECT * \n");
        sqlText.append("     FROM LD_BCN_DOC_PUSHDOWN04_MT pv \n");
        sqlText.append("     WHERE pv.SESID = ? \n");
        sqlText.append("       AND pv.sel_pullflag IN (?, ?) \n");
        sqlText.append("       AND head.univ_fiscal_yr = pv.univ_fiscal_yr \n");
        sqlText.append("       AND head.fin_coa_cd = pv.fin_coa_cd \n");
        sqlText.append("       AND head.account_nbr = pv.account_nbr \n");
        sqlText.append("       AND head.sub_acct_nbr = pv.sub_acct_nbr \n");
        sqlText.append("       AND head.bdgt_lock_usr_id = ?) \n");
        pushdownSelectedOrganizationDocumentsTemplates[8] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // push the selected org(s) subtree accts to level 1 - sel_pullflag = 4
        // unlock as we go
        sqlText.append("UPDATE LD_BCNSTR_HDR_T head \n");
        sqlText.append("SET org_level_cd = 1, \n");
        sqlText.append("    org_coa_of_lvl_cd = \n");
        sqlText.append("    (SELECT pv.lone_orgfin_coa \n");
        sqlText.append("     FROM LD_BCN_DOC_PUSHDOWN04_MT pv \n");
        sqlText.append("     WHERE pv.SESID = ? \n");
        sqlText.append("       AND pv.sel_pullflag = ? \n");
        sqlText.append("       AND head.univ_fiscal_yr = pv.univ_fiscal_yr \n");
        sqlText.append("       AND head.fin_coa_cd = pv.fin_coa_cd \n");
        sqlText.append("       AND head.account_nbr = pv.account_nbr \n");
        sqlText.append("       AND head.sub_acct_nbr = pv.sub_acct_nbr \n");
        sqlText.append("       AND head.bdgt_lock_usr_id = ?), \n");
        sqlText.append("    org_of_lvl_cd = \n");
        sqlText.append("    (SELECT pv.lone_org \n");
        sqlText.append("     FROM LD_BCN_DOC_PUSHDOWN04_MT pv \n");
        sqlText.append("     WHERE pv.SESID = ? \n");
        sqlText.append("       AND pv.sel_pullflag = ? \n");
        sqlText.append("       AND head.univ_fiscal_yr = pv.univ_fiscal_yr \n");
        sqlText.append("       AND head.fin_coa_cd = pv.fin_coa_cd \n");
        sqlText.append("       AND head.account_nbr = pv.account_nbr \n");
        sqlText.append("       AND head.sub_acct_nbr = pv.sub_acct_nbr \n");
        sqlText.append("       AND head.bdgt_lock_usr_id = ?), \n");
        sqlText.append("    bdgt_lock_usr_id = NULL \n");
        sqlText.append("WHERE EXISTS \n");
        sqlText.append("    (SELECT * \n");
        sqlText.append("     FROM LD_BCN_DOC_PUSHDOWN04_MT pv \n");
        sqlText.append("     WHERE pv.SESID = ? \n");
        sqlText.append("       AND pv.sel_pullflag = ? \n");
        sqlText.append("       AND head.univ_fiscal_yr = pv.univ_fiscal_yr \n");
        sqlText.append("       AND head.fin_coa_cd = pv.fin_coa_cd \n");
        sqlText.append("       AND head.account_nbr = pv.account_nbr \n");
        sqlText.append("       AND head.sub_acct_nbr = pv.sub_acct_nbr \n");
        sqlText.append("       AND head.bdgt_lock_usr_id = ?) \n");
        pushdownSelectedOrganizationDocumentsTemplates[9] = sqlText.toString();
        sqlText.delete(0, sqlText.length());


        // push the selected org(s) subtree accts to level 0 - sel_pullflag = 5
        // unlock as we go */
        sqlText.append("UPDATE LD_BCNSTR_HDR_T head \n");
        sqlText.append("SET org_level_cd = 0, \n");
        sqlText.append("    org_coa_of_lvl_cd = NULL, \n");
        sqlText.append("    org_of_lvl_cd = NULL, \n");
        sqlText.append("    bdgt_lock_usr_id = NULL \n");
        sqlText.append("WHERE EXISTS \n");
        sqlText.append("    (SELECT * \n");
        sqlText.append("     FROM LD_BCN_DOC_PUSHDOWN04_MT pv \n");
        sqlText.append("     WHERE pv.SESID = ? \n");
        sqlText.append("       AND pv.sel_pullflag = ? \n");
        sqlText.append("       AND head.univ_fiscal_yr = pv.univ_fiscal_yr \n");
        sqlText.append("       AND head.fin_coa_cd = pv.fin_coa_cd \n");
        sqlText.append("       AND head.account_nbr = pv.account_nbr \n");
        sqlText.append("       AND head.sub_acct_nbr = pv.sub_acct_nbr \n");
        sqlText.append("       AND head.bdgt_lock_usr_id = ?) \n");
        pushdownSelectedOrganizationDocumentsTemplates[10] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // build list of budget documents for selected orgs and below user's point of view (documents that will be pulled up by org
        // pullup)
        sqlText.append("INSERT INTO LD_BCN_ACCTSEL_T \n");
        sqlText.append("SELECT ?,\n");
        sqlText.append("        head.univ_fiscal_yr,\n");
        sqlText.append("        head.fin_coa_cd, \n");
        sqlText.append("        head.account_nbr, \n");
        sqlText.append("        head.sub_acct_nbr, \n");
        sqlText.append("        head.fdoc_nbr, \n");
        sqlText.append("        1, \n");
        sqlText.append("        head.org_level_cd, \n");
        sqlText.append("        head.org_coa_of_lvl_cd, \n");
        sqlText.append("        head.org_of_lvl_cd, \n");
        sqlText.append("        fphd.fdoc_status_cd, \n");
        sqlText.append("        '', \n");
        sqlText.append("        NULL \n");
        sqlText.append("FROM LD_BCNSTR_HDR_T head, \n");
        sqlText.append("      FS_DOC_HEADER_T fphd, \n");
        sqlText.append("      (SELECT head2.fdoc_nbr \n");
        sqlText.append("      FROM LD_BCNSTR_HDR_T head2, \n");
        sqlText.append("            LD_BCN_PULLUP_T pull, \n");
        sqlText.append("            LD_BCN_ACCT_ORG_HIER_T hs, \n");
        sqlText.append("            LD_BCN_ACCT_ORG_HIER_T  hp \n");
        sqlText.append("     WHERE pull.pull_flag = ?  \n");
        sqlText.append("       AND pull.person_unvl_id = ? \n");
        sqlText.append("       AND hs.univ_fiscal_yr = ? \n");
        sqlText.append("       AND hs.org_fin_coa_cd = pull.fin_coa_cd \n");
        sqlText.append("       AND hs.org_cd = pull.org_cd \n");
        sqlText.append("       AND hp.org_fin_coa_cd = ? \n");
        sqlText.append("       AND hp.org_cd= ? \n");
        sqlText.append("       AND hp.univ_fiscal_yr = hs.univ_fiscal_yr \n");
        sqlText.append("       AND hp.fin_coa_cd = hs.fin_coa_cd \n");
        sqlText.append("       AND hp.account_nbr = hs.account_nbr \n");
        sqlText.append("       AND head2.univ_fiscal_yr = hp.univ_fiscal_yr \n");
        sqlText.append("       AND head2.fin_coa_cd = hp.fin_coa_cd \n");
        sqlText.append("       AND head2.account_nbr = hp.account_nbr \n");
        sqlText.append("       AND head2.org_level_cd < hp.org_level_cd \n");
        sqlText.append("     UNION \n");
        sqlText.append("     SELECT head2.fdoc_nbr \n");
        sqlText.append("     FROM LD_BCNSTR_HDR_T head2, \n");
        sqlText.append("           LD_BCN_PULLUP_T pull, \n");
        sqlText.append("           LD_BCN_ACCT_ORG_HIER_T  hs, \n");
        sqlText.append("           LD_BCN_ACCT_ORG_HIER_T  hp, \n");
        sqlText.append("           LD_BCN_ACCT_RPTS_T bar \n");
        sqlText.append("    WHERE pull.pull_flag = ? \n");
        sqlText.append("       AND pull.person_unvl_id = ? \n");
        sqlText.append("       AND hs.univ_fiscal_yr = ? \n");
        sqlText.append("       AND hs.org_fin_coa_cd = pull.fin_coa_cd \n");
        sqlText.append("       AND hs.org_cd = pull.org_cd \n");
        sqlText.append("       AND hp.org_fin_coa_cd = ? \n");
        sqlText.append("       AND hp.org_cd= ? \n");
        sqlText.append("       AND hp.univ_fiscal_yr = hs.univ_fiscal_yr \n");
        sqlText.append("       AND hp.fin_coa_cd = hs.fin_coa_cd \n");
        sqlText.append("       AND hp.account_nbr = hs.account_nbr \n");
        sqlText.append("       AND head2.univ_fiscal_yr = hp.univ_fiscal_yr \n");
        sqlText.append("       AND head2.fin_coa_cd = hp.fin_coa_cd \n");
        sqlText.append("       AND head2.account_nbr = hp.account_nbr \n");
        sqlText.append("       AND head2.org_level_cd < hp.org_level_cd \n");
        sqlText.append("       AND bar.fin_coa_cd = hs.fin_coa_cd \n");
        sqlText.append("       AND bar.account_nbr = hs.account_nbr \n");
        sqlText.append("       AND bar.rpts_to_fin_coa_cd = hs.org_fin_coa_cd \n");
        sqlText.append("       AND bar.rpts_to_org_cd = hs.org_cd \n");
        sqlText.append("     UNION \n");
        sqlText.append("     SELECT head2.fdoc_nbr \n");
        sqlText.append("     FROM LD_BCNSTR_HDR_T head2, \n");
        sqlText.append("           LD_BCN_PULLUP_T pull, \n");
        sqlText.append("           LD_BCN_ACCT_ORG_HIER_T  hs, \n");
        sqlText.append("           LD_BCN_ACCT_ORG_HIER_T  hp, \n");
        sqlText.append("           LD_BCN_ACCT_RPTS_T bar \n");
        sqlText.append("    WHERE pull.pull_flag = ? \n");
        sqlText.append("       AND pull.person_unvl_id = ? \n");
        sqlText.append("       AND hs.univ_fiscal_yr = ? \n");
        sqlText.append("       AND hs.org_fin_coa_cd = pull.fin_coa_cd \n");
        sqlText.append("       AND hs.org_cd = pull.org_cd \n");
        sqlText.append("       AND hp.org_fin_coa_cd = ? \n");
        sqlText.append("       AND hp.org_cd= ? \n");
        sqlText.append("       AND hp.univ_fiscal_yr = hs.univ_fiscal_yr \n");
        sqlText.append("       AND hp.fin_coa_cd = hs.fin_coa_cd \n");
        sqlText.append("       AND hp.account_nbr = hs.account_nbr \n");
        sqlText.append("       AND head2.univ_fiscal_yr = hp.univ_fiscal_yr \n");
        sqlText.append("       AND head2.fin_coa_cd = hp.fin_coa_cd \n");
        sqlText.append("       AND head2.account_nbr = hp.account_nbr \n");
        sqlText.append("       AND head2.org_level_cd < hp.org_level_cd \n");
        sqlText.append("       AND bar.fin_coa_cd = hs.fin_coa_cd \n");
        sqlText.append("       AND bar.account_nbr = hs.account_nbr \n");
        sqlText.append("       AND (bar.rpts_to_fin_coa_cd <> hs.org_fin_coa_cd \n");
        sqlText.append("            OR bar.rpts_to_org_cd <> hs.org_cd) \n");
        sqlText.append("    ) s \n");
        sqlText.append("   WHERE head.fdoc_nbr = s.fdoc_nbr \n");
        sqlText.append("      AND fphd.fdoc_nbr = head.fdoc_nbr \n");

        accountSelectBudgetedDocumentsPullUpTemplates[0] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // update org for accounts at level 0
        sqlText.append("  UPDATE LD_BCN_ACCTSEL_T \n");
        sqlText.append("  SET org_fin_coa_cd = \n");
        sqlText.append("     (SELECT rpts2.rpts_to_fin_coa_cd \n");
        sqlText.append("     FROM LD_BCN_ACCT_RPTS_T rpts2 \n");
        sqlText.append("     WHERE LD_BCN_ACCTSEL_T.fin_coa_cd = rpts2.fin_coa_cd \n");
        sqlText.append("       AND LD_BCN_ACCTSEL_T.account_nbr = rpts2.account_nbr), \n");
        sqlText.append("     org_cd = \n");
        sqlText.append("      (SELECT rpts2.rpts_to_org_cd \n");
        sqlText.append("       FROM LD_BCN_ACCT_RPTS_T rpts2 \n");
        sqlText.append("       WHERE LD_BCN_ACCTSEL_T.fin_coa_cd = rpts2.fin_coa_cd \n");
        sqlText.append("          AND LD_BCN_ACCTSEL_T.account_nbr = rpts2.account_nbr) \n");
        sqlText.append("   WHERE LD_BCN_ACCTSEL_T.person_unvl_id = ? \n");
        sqlText.append("      AND LD_BCN_ACCTSEL_T.univ_fiscal_yr = ? \n");
        sqlText.append("      AND LD_BCN_ACCTSEL_T.org_level_cd = 0 \n");
        sqlText.append("      AND EXISTS (select * \n");
        sqlText.append("                  FROM LD_BCN_ACCT_RPTS_T rpts \n");
        sqlText.append("                  WHERE LD_BCN_ACCTSEL_T.fin_coa_cd = rpts.fin_coa_cd \n");
        sqlText.append("                     AND LD_BCN_ACCTSEL_T.account_nbr = rpts.account_nbr) \n");

        accountSelectBudgetedDocumentsPullUpTemplates[1] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // build list of budget documents for selected orgs at user's point of view (documents that will be pushed down by org
        // pushdown)
        sqlText.append("INSERT INTO LD_BCN_ACCTSEL_T \n");
        sqlText.append("SELECT ?, \n");
        sqlText.append("    head.univ_fiscal_yr, \n");
        sqlText.append("    head.fin_coa_cd, \n");
        sqlText.append("    head.account_nbr, \n");
        sqlText.append("    head.sub_acct_nbr, \n");
        sqlText.append("    head.fdoc_nbr, \n");
        sqlText.append("    1, \n");
        sqlText.append("    head.org_level_cd, \n");
        sqlText.append("    head.org_coa_of_lvl_cd, \n");
        sqlText.append("    head.org_of_lvl_cd, \n");
        sqlText.append("    fphd.fdoc_status_cd, \n");
        sqlText.append("    '', \n");
        sqlText.append("    NULL   \n");
        sqlText.append("FROM LD_BCN_PULLUP_T pull, \n");
        sqlText.append("    LD_BCN_ACCT_ORG_HIER_T hier, \n");
        sqlText.append("    LD_BCN_ACCT_ORG_HIER_T hier2, \n");
        sqlText.append("    LD_BCNSTR_HDR_T head, \n");
        sqlText.append("    FS_DOC_HEADER_T fphd \n");
        sqlText.append("WHERE pull.pull_flag in (?,?,?,?) \n");
        sqlText.append("  AND pull.person_unvl_id = ? \n");
        sqlText.append("  AND hier.univ_fiscal_yr = ? \n");
        sqlText.append("  AND hier.org_fin_coa_cd = pull.fin_coa_cd \n");
        sqlText.append("  AND hier.org_cd = pull.org_cd  \n");
        sqlText.append("  AND hier.univ_fiscal_yr = hier2.univ_fiscal_yr \n");
        sqlText.append("  AND hier.fin_coa_cd = hier2.fin_coa_cd \n");
        sqlText.append("  AND hier.account_nbr = hier2.account_nbr   \n");
        sqlText.append("  AND hier2.org_fin_coa_cd = ? \n");
        sqlText.append("  AND hier2.org_cd = ? \n");
        sqlText.append("  AND head.univ_fiscal_yr = hier2.univ_fiscal_yr \n");
        sqlText.append("  AND head.fin_coa_cd = hier2.fin_coa_cd \n");
        sqlText.append("  AND head.account_nbr = hier2.account_nbr \n");
        sqlText.append("  AND head.org_level_cd = hier2.org_level_cd \n");
        sqlText.append("  AND fphd.fdoc_nbr = head.fdoc_nbr \n");
        sqlText.append("UNION \n");
        sqlText.append("SELECT ?, \n");
        sqlText.append("    head.univ_fiscal_yr, \n");
        sqlText.append("    head.fin_coa_cd, \n");
        sqlText.append("    head.account_nbr, \n");
        sqlText.append("    head.sub_acct_nbr, \n");
        sqlText.append("    head.fdoc_nbr, \n");
        sqlText.append("    1, \n");
        sqlText.append("    head.org_level_cd, \n");
        sqlText.append("    head.org_coa_of_lvl_cd, \n");
        sqlText.append("    head.org_of_lvl_cd, \n");
        sqlText.append("    fphd.fdoc_status_cd, \n");
        sqlText.append("    '', \n");
        sqlText.append("    NULL     \n");
        sqlText.append("FROM LD_BCN_PULLUP_T pull, \n");
        sqlText.append("    LD_BCN_ACCT_ORG_HIER_T hier, \n");
        sqlText.append("    LD_BCN_ACCT_ORG_HIER_T hier2, \n");
        sqlText.append("    LD_BCN_ACCT_RPTS_T rpts, \n");
        sqlText.append("    LD_BCNSTR_HDR_T head, \n");
        sqlText.append("    FS_DOC_HEADER_T fphd \n");
        sqlText.append("WHERE pull.pull_flag = ? \n");
        sqlText.append("  AND pull.person_unvl_id = ? \n");
        sqlText.append("  AND hier.univ_fiscal_yr = ? \n");
        sqlText.append("  AND hier.org_fin_coa_cd = pull.fin_coa_cd \n");
        sqlText.append("  AND hier.org_cd = pull.org_cd \n");
        sqlText.append("  AND hier.fin_coa_cd = rpts.fin_coa_cd \n");
        sqlText.append("  AND hier.account_nbr = rpts.account_nbr \n");
        sqlText.append("  AND hier.org_fin_coa_cd = rpts.rpts_to_fin_coa_cd  \n");
        sqlText.append("  AND hier.org_cd = rpts.rpts_to_org_cd   \n");
        sqlText.append("  AND hier.univ_fiscal_yr = hier2.univ_fiscal_yr \n");
        sqlText.append("  AND hier.fin_coa_cd = hier2.fin_coa_cd \n");
        sqlText.append("  AND hier.account_nbr = hier2.account_nbr  \n");
        sqlText.append("  AND hier2.org_fin_coa_cd = ? \n");
        sqlText.append("  AND hier2.org_cd = ? \n");
        sqlText.append("  AND head.univ_fiscal_yr = hier2.univ_fiscal_yr \n");
        sqlText.append("  AND head.fin_coa_cd = hier2.fin_coa_cd \n");
        sqlText.append("  AND head.account_nbr = hier2.account_nbr \n");
        sqlText.append("  AND head.org_level_cd = hier2.org_level_cd \n");
        sqlText.append("  AND fphd.fdoc_nbr = head.fdoc_nbr \n");

        accountSelectBudgetedDocumentsPushDownTemplates[0] = sqlText.toString();
        sqlText.delete(0, sqlText.length());
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetOrganizationPushPullDao#pullupSelectedOrganizationDocuments(java.lang.String,
     *      java.lang.Integer, java.lang.String, java.lang.String)
     */
    @Override
    public void pullupSelectedOrganizationDocuments(String principalId, Integer fiscalYear, String pointOfViewCharOfAccountsCode, String pointOfViewOrganizationCode) {

        String sessionId = java.util.UUID.randomUUID().toString();

        // run the steps
        this.getSimpleJdbcTemplate().update(pullupSelectedOrganizationDocumentsTemplates[0], sessionId, principalId, fiscalYear);
        this.getSimpleJdbcTemplate().update(pullupSelectedOrganizationDocumentsTemplates[1], sessionId, sessionId, pointOfViewCharOfAccountsCode, pointOfViewOrganizationCode);
        this.getSimpleJdbcTemplate().update(pullupSelectedOrganizationDocumentsTemplates[2], sessionId, sessionId, BCConstants.OrgSelControlOption.BOTH.getKey());
        this.getSimpleJdbcTemplate().update(pullupSelectedOrganizationDocumentsTemplates[3], sessionId, sessionId, BCConstants.OrgSelControlOption.ORG.getKey());
        this.getSimpleJdbcTemplate().update(pullupSelectedOrganizationDocumentsTemplates[4], sessionId, sessionId, BCConstants.OrgSelControlOption.SUBORG.getKey());
        this.getSimpleJdbcTemplate().update(pullupSelectedOrganizationDocumentsTemplates[5], principalId, sessionId);
        this.getSimpleJdbcTemplate().update(pullupSelectedOrganizationDocumentsTemplates[6], sessionId, principalId);
        this.getSimpleJdbcTemplate().update(pullupSelectedOrganizationDocumentsTemplates[7], sessionId, principalId, sessionId, principalId, sessionId, principalId, sessionId, principalId);

        // cleanup temp table space
        this.clearTempTableBySesId("LD_BCN_DOC_PULLUP01_MT", "SESID", sessionId);
        this.clearTempTableBySesId("LD_BCN_DOC_PULLUP02_MT", "SESID", sessionId);
        this.clearTempTableBySesId("LD_BCN_DOC_PULLUP03_MT", "SESID", sessionId);

    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetOrganizationPushPullDao#pushdownSelectedOrganizationDocuments(java.lang.String,
     *      java.lang.Integer, java.lang.String, java.lang.String)
     */
    @Override
    public void pushdownSelectedOrganizationDocuments(String principalId, Integer fiscalYear, String pointOfViewCharOfAccountsCode, String pointOfViewOrganizationCode) {

        String sessionId = java.util.UUID.randomUUID().toString();

        // use some local vars to improve readability
        Integer orgLev = OrgSelControlOption.ORGLEV.getKey();
        Integer mgrLev = OrgSelControlOption.MGRLEV.getKey();
        Integer orgMgrLev = OrgSelControlOption.ORGMGRLEV.getKey();
        Integer levOne = OrgSelControlOption.LEVONE.getKey();
        Integer levZero = OrgSelControlOption.LEVZERO.getKey();
        String puid = principalId;

        // run the steps
        this.getSimpleJdbcTemplate().update(pushdownSelectedOrganizationDocumentsTemplates[0], sessionId, puid, fiscalYear);
        this.getSimpleJdbcTemplate().update(pushdownSelectedOrganizationDocumentsTemplates[1], sessionId, sessionId, pointOfViewCharOfAccountsCode, pointOfViewOrganizationCode);
        this.getSimpleJdbcTemplate().update(pushdownSelectedOrganizationDocumentsTemplates[2], sessionId, sessionId);
        this.getSimpleJdbcTemplate().update(pushdownSelectedOrganizationDocumentsTemplates[3], sessionId, sessionId, orgLev, orgMgrLev, levOne, levZero);
        this.getSimpleJdbcTemplate().update(pushdownSelectedOrganizationDocumentsTemplates[4], sessionId, sessionId, mgrLev);
        this.getSimpleJdbcTemplate().update(pushdownSelectedOrganizationDocumentsTemplates[5], puid, sessionId);
        this.getSimpleJdbcTemplate().update(pushdownSelectedOrganizationDocumentsTemplates[6], sessionId, puid);
        this.getSimpleJdbcTemplate().update(pushdownSelectedOrganizationDocumentsTemplates[7], sessionId, mgrLev, orgMgrLev, puid);
        this.getSimpleJdbcTemplate().update(pushdownSelectedOrganizationDocumentsTemplates[8], sessionId, orgLev, orgMgrLev, puid, sessionId, orgLev, orgMgrLev, puid, sessionId, orgLev, orgMgrLev, puid, sessionId, orgLev, orgMgrLev, puid);
        this.getSimpleJdbcTemplate().update(pushdownSelectedOrganizationDocumentsTemplates[9], sessionId, levOne, puid, sessionId, levOne, puid, sessionId, levOne, puid);
        this.getSimpleJdbcTemplate().update(pushdownSelectedOrganizationDocumentsTemplates[10], sessionId, levZero, puid);

        // cleanup temp table space
        this.clearTempTableBySesId("LD_BCN_DOC_PUSHDOWN01_MT", "SESID", sessionId);
        this.clearTempTableBySesId("LD_BCN_DOC_PUSHDOWN02_MT", "SESID", sessionId);
        this.clearTempTableBySesId("LD_BCN_DOC_PUSHDOWN03_MT", "SESID", sessionId);
        this.clearTempTableBySesId("LD_BCN_DOC_PUSHDOWN04_MT", "SESID", sessionId);
    }

    /**
     * Uses sql jdbc call to populate the account select table for the set of pull up documents.
     *
     * @see org.kuali.kfs.module.bc.document.dataaccess..BudgetOrganizationPushPullDao#buildPullUpBudgetedDocuments(java.lang.String,
     *      java.lang.Integer, java.lang.String, java.lang.String)
     */
    @Override
    public int buildPullUpBudgetedDocuments(String principalId, Integer fiscalYear, String pointOfViewCharOfAccountsCode, String pointOfViewOrganizationCode) {
        // clear temp records for users
        this.clearTempTableByUnvlId("LD_BCN_ACCTSEL_T", "person_unvl_id", principalId);

        Integer org = OrgSelControlOption.ORG.getKey();
        Integer subOrg = OrgSelControlOption.SUBORG.getKey();
        Integer both = OrgSelControlOption.BOTH.getKey();

        // build account select
        int rowCount = this.getSimpleJdbcTemplate().update(accountSelectBudgetedDocumentsPullUpTemplates[0], principalId, both, principalId, fiscalYear, pointOfViewCharOfAccountsCode, pointOfViewOrganizationCode, org, principalId, fiscalYear, pointOfViewCharOfAccountsCode, pointOfViewOrganizationCode, subOrg, principalId, fiscalYear, pointOfViewCharOfAccountsCode, pointOfViewOrganizationCode);

        // update org for accounts at level zero
        this.getSimpleJdbcTemplate().update(accountSelectBudgetedDocumentsPullUpTemplates[1], principalId, fiscalYear);

        return rowCount;
    }

    /**
     * Uses sql jdbc call to populate the account select table for the set of push up documents.
     *
     * @see org.kuali.kfs.module.bc.document.dataaccess..BudgetOrganizationPushPullDao#buildPushDownBudgetedDocuments(java.lang.String,
     *      java.lang.Integer, java.lang.String, java.lang.String)
     */
    @Override
    public int buildPushDownBudgetedDocuments(String principalId, Integer fiscalYear, String pointOfViewCharOfAccountsCode, String pointOfViewOrganizationCode) {
        // clear temp records for users
        this.clearTempTableByUnvlId("LD_BCN_ACCTSEL_T", "person_unvl_id", principalId);

        Integer orgLev = OrgSelControlOption.ORGLEV.getKey();
        Integer mgrLev = OrgSelControlOption.MGRLEV.getKey();
        Integer orgMgrLev = OrgSelControlOption.ORGMGRLEV.getKey();
        Integer levOne = OrgSelControlOption.LEVONE.getKey();
        Integer levZero = OrgSelControlOption.LEVZERO.getKey();

        // build account select
        int rowCount = this.getSimpleJdbcTemplate().update(accountSelectBudgetedDocumentsPushDownTemplates[0], principalId, orgLev, orgMgrLev, levOne, levZero, principalId, fiscalYear, pointOfViewCharOfAccountsCode, pointOfViewOrganizationCode, principalId, mgrLev, principalId, fiscalYear, pointOfViewCharOfAccountsCode, pointOfViewOrganizationCode);

        return rowCount;
    }

}

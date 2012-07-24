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

import org.kuali.kfs.module.bc.document.dataaccess.BudgetPullupDao;

/**
 * This class implemements BudgetPullupDao using Raw Sql
 */
public class BudgetPullupDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetPullupDao {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetPullupDaoJdbc.class);
    protected static final int MAXLEVEL = 50;

    protected static String[] initPointOfViewTemplates = new String[1];
    protected static String[] insertChildOrgTemplates = new String[2];

    public BudgetPullupDaoJdbc() {

        StringBuilder sqlText = new StringBuilder(500);
        sqlText.append("INSERT INTO LD_BCN_PULLUP_T \n");
        sqlText.append(" (PERSON_UNVL_ID, FIN_COA_CD, ORG_CD, RPTS_TO_FIN_COA_CD, RPTS_TO_ORG_CD, PULL_FLAG) \n");
        sqlText.append("SELECT ?, r.fin_coa_cd, r.org_cd, r.rpts_to_fin_coa_cd, r.rpts_to_org_cd, ? \n");
        sqlText.append("FROM LD_BCN_ORG_RPTS_T r \n");
        sqlText.append("WHERE fin_coa_cd = ? \n");
        sqlText.append("  AND org_cd = ? \n");
        initPointOfViewTemplates[0] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        sqlText.append("INSERT INTO LD_BCN_PULLUP_T \n");
        sqlText.append(" (PERSON_UNVL_ID, FIN_COA_CD, ORG_CD, RPTS_TO_FIN_COA_CD, RPTS_TO_ORG_CD, PULL_FLAG) \n");
        sqlText.append("SELECT ?, r.fin_coa_cd, r.org_cd, r.rpts_to_fin_coa_cd, r.rpts_to_org_cd, ? \n");
        sqlText.append("FROM LD_BCN_ORG_RPTS_T r, LD_BCN_PULLUP_T p, CA_ORG_T o \n");
        sqlText.append("WHERE p.person_unvl_id = ? \n");
        sqlText.append("  AND p.pull_flag = ? \n");
        sqlText.append("  AND p.fin_coa_cd = r.rpts_to_fin_coa_cd \n");
        sqlText.append("  AND p.org_cd = r.rpts_to_org_cd \n");
        sqlText.append("  AND not (r.fin_coa_cd = r.rpts_to_fin_coa_cd and r.org_cd = r.rpts_to_org_cd)");
        sqlText.append("  AND o.fin_coa_cd = r.fin_coa_cd \n");
        sqlText.append("  AND o.org_cd = r.org_cd \n");
        sqlText.append("  AND o.org_active_cd = 'Y' \n");
        insertChildOrgTemplates[0] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        sqlText.append("UPDATE LD_BCN_PULLUP_T \n");
        sqlText.append("SET pull_flag = 0 \n");
        sqlText.append("WHERE person_unvl_id = ? \n");
        insertChildOrgTemplates[1] = sqlText.toString();

    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetPullupDao#buildSubTree(java.lang.String, java.lang.String,
     *      java.lang.String, int)
     */
    public void buildSubTree(String principalName, String chartOfAccountsCode, String organizationCode, int currentLevel) {

        initPointOfView(principalName, chartOfAccountsCode, organizationCode, currentLevel);
        insertChildOrgs(principalName, currentLevel);

    }

    /**
     * This method initializes and inserts the root organization using raw SQL.
     * 
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetPullupDao#initPointOfView(java.lang.String, java.lang.String,
     *      java.lang.String, int)
     */
    protected void initPointOfView(String principalName, String chartOfAccountsCode, String organizationCode, int currentLevel) {

        LOG.debug("initPointOfView() called");

        getSimpleJdbcTemplate().update(initPointOfViewTemplates[0], principalName, currentLevel, chartOfAccountsCode, organizationCode);
    }

    /**
     * This method is implemented using RawSql
     * 
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetPullupDao#insertChildOrgs(java.lang.String, int)
     */
    protected void insertChildOrgs(String principalName, int previousLevel) {

        LOG.debug("insertChildOrgs() called");

        if (previousLevel <= MAXLEVEL) {
            int currentLevel = previousLevel + 1;

            // insert the children of the organizations at the current level for the user
            // and then recursively call on the new level
            int rowsAffected = getSimpleJdbcTemplate().update(insertChildOrgTemplates[0], principalName, currentLevel, principalName, previousLevel);
            if (rowsAffected > 0) {
                insertChildOrgs(principalName, currentLevel);
            }
            else {
                // cleanup by resetting the pull_flag to zero for all
                getSimpleJdbcTemplate().update(insertChildOrgTemplates[1], principalName);
            }
        }
        else {
            // overrun problem
            LOG.warn(String.format("\nWarning: One or more selected organizations have reporting organizations more than maxlevel of %d deep.", MAXLEVEL));
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetPullupDao#cleanGeneralLedgerObjectSummaryTable(java.lang.String)
     */
    public void cleanGeneralLedgerObjectSummaryTable(String principalName) {
        this.clearTempTableByUnvlId("LD_BCN_PULLUP_T", "PERSON_UNVL_ID", principalName);
    }

}

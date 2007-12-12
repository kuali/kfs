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
import org.kuali.module.budget.dao.BudgetPullupDao;

/**
 * This class implemements BudgetPullupDao using RawSql
 */
public class BudgetPullupDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetPullupDao {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetPullupDaoJdbc.class);
    private static final int MAXLEVEL = 50;
    
    private static String[] initPointOfViewTemplates = new String[1];
    private static String[] insertChildOrgTemplates = new String[2];
    
    @RawSQL
    public BudgetPullupDaoJdbc() {
        
        StringBuilder sqlText = new StringBuilder(500);
        sqlText.append("INSERT INTO ld_bcn_pullup_t \n");
        sqlText.append(" (PERSON_UNVL_ID, FIN_COA_CD, ORG_CD, OBJ_ID, RPTS_TO_FIN_COA_CD, RPTS_TO_ORG_CD, PULL_FLAG) \n");
        sqlText.append("SELECT ?, r.fin_coa_cd, r.org_cd, sys_guid(), r.rpts_to_fin_coa_cd, r.rpts_to_org_cd, ? \n");
        sqlText.append("FROM ld_bcn_org_rpts_t r \n");
        sqlText.append("WHERE fin_coa_cd = ? \n");
        sqlText.append("  AND org_cd = ? \n");
        initPointOfViewTemplates[0] = sqlText.toString();
        sqlText.delete(0, sqlText.length());
        
        sqlText.append("INSERT INTO ld_bcn_pullup_t \n");
        sqlText.append(" (PERSON_UNVL_ID, FIN_COA_CD, ORG_CD, OBJ_ID, RPTS_TO_FIN_COA_CD, RPTS_TO_ORG_CD, PULL_FLAG) \n");
        sqlText.append("SELECT ?, r.fin_coa_cd, r.org_cd, sys_guid(), r.rpts_to_fin_coa_cd, r.rpts_to_org_cd, ? \n");
        sqlText.append("FROM ld_bcn_org_rpts_t r, ld_bcn_pullup_t p, ca_org_t o \n");
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
        
        sqlText.append("UPDATE ld_bcn_pullup_t \n");
        sqlText.append("SET pull_flag = 0 \n");
        sqlText.append("WHERE person_unvl_id = ? \n");
        insertChildOrgTemplates[1] = sqlText.toString();

    }
    
    /**
     * This method initializes and inserts the root organization using raw SQL.
     * 
     * @see org.kuali.module.budget.dao.BudgetPullupDao#initPointOfView(java.lang.String, java.lang.String, java.lang.String, int)
     */
    @RawSQL
    public void initPointOfView(String personUserIdentifier, String chartOfAccountsCode, String organizationCode, int currentLevel) {
        //TODO remove OBJ_ID from the table and here before implementing any calls to this method
   
        LOG.debug("initPointOfView() called");
        
        getSimpleJdbcTemplate().update(initPointOfViewTemplates[0], personUserIdentifier, currentLevel, chartOfAccountsCode, organizationCode);
    }

    /**
     * This method is implemented using RawSql
     * 
     * @see org.kuali.module.budget.dao.BudgetPullupDao#insertChildOrgs(java.lang.String, int)
     */
    @RawSQL
    public void insertChildOrgs(String personUserIdentifier, int previousLevel) {
        //TODO remove OBJ_ID from the table and here before implementing any calls to this method
        
        LOG.debug("insertChildOrgs() called");

        if (previousLevel <= MAXLEVEL) {
            int currentLevel = previousLevel + 1;

            // insert the children of the organizations at the current level for the user
            // and then recursively call on the new level
            int rowsAffected = getSimpleJdbcTemplate().update(insertChildOrgTemplates[0], personUserIdentifier, currentLevel, personUserIdentifier, previousLevel);
            if (rowsAffected > 0) {
                insertChildOrgs(personUserIdentifier, currentLevel);
            } else {
                // cleanup by resetting the pull_flag to zero for all
                getSimpleJdbcTemplate().update(insertChildOrgTemplates[1], personUserIdentifier);
            }
        } else {
            // overrun problem
            LOG.warn(String.format("\nWarning: One or more selected organizations have reporting organizations more than maxlevel of %d deep.", MAXLEVEL));
        }
    }
}

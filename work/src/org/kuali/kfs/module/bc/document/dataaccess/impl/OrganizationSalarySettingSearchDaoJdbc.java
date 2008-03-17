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
import org.kuali.core.util.Guid;
import org.kuali.module.budget.dao.OrganizationSalarySettingSearchDao;

/**
 * This class...
 * 
 */
public class OrganizationSalarySettingSearchDaoJdbc extends BudgetConstructionDaoJdbcBase implements OrganizationSalarySettingSearchDao {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationSalarySettingSearchDaoJdbc.class);

    private static final int MAXLEVEL = 50;
    private static String[] buildIntendedIncumbentSelectTemplates = new String[1];
    private static String[] initSelectedPositionOrgsTemplates = new String[1];
    private static String[] populateSelectedPositionOrgsSubTreeTemplates = new String[1];
    private static String[] populatePositionSelectForSubTreeTemplates = new String[7];
    
    @RawSQL
    public OrganizationSalarySettingSearchDaoJdbc() {
        
        StringBuilder sqlText = new StringBuilder(500);

        // This uses the GROUP BY clause to force a distinct set so as to not trip over the unique constraint on the target table.
        // For some reason the constraint is violated without the use of GROUP BY in Oracle
        sqlText.append("INSERT INTO ld_bcn_incumbent_sel_t \n");
        sqlText.append(" (PERSON_UNVL_ID, EMPLID, FIN_OBJECT_CD, PERSON_NM) \n");
        sqlText.append("SELECT DISTINCT pull.person_unvl_id, bcaf.emplid, bcaf.fin_object_cd, iinc.person_nm \n");
        sqlText.append("FROM ld_bcn_pullup_t pull, ld_bcn_acct_org_hier_t hier, ld_pndbc_apptfnd_t bcaf, ld_bcn_intincbnt_t iinc \n");
        sqlText.append("WHERE pull.pull_flag > 0 \n");
        sqlText.append("  AND pull.person_unvl_id = ? \n");
        sqlText.append("  AND hier.univ_fiscal_yr = ? \n");
        sqlText.append("  AND hier.org_fin_coa_cd = pull.fin_coa_cd \n");
        sqlText.append("  AND hier.org_cd = pull.org_cd \n");
        sqlText.append("  AND bcaf.univ_fiscal_yr = hier.univ_fiscal_yr \n");
        sqlText.append("  AND bcaf.fin_coa_cd = hier.fin_coa_cd \n");
        sqlText.append("  AND bcaf.account_nbr = hier.account_nbr \n");
        sqlText.append("  AND bcaf.emplid NOT IN ('VACANT') \n");
        sqlText.append("  AND iinc.emplid = bcaf.emplid \n");
        sqlText.append("GROUP BY pull.person_unvl_id, bcaf.emplid, bcaf.fin_object_cd, iinc.person_nm");
        buildIntendedIncumbentSelectTemplates[0] = sqlText.toString();
        sqlText.delete(0, sqlText.length());
        
        sqlText.append("INSERT INTO ld_bcn_build_pos_sel01_mt \n");
        sqlText.append(" (SESID, FIN_COA_CD, ORG_CD, ORG_LEVEL_CD) \n");
        sqlText.append("SELECT ?, p.fin_coa_cd, p.org_cd,  ? \n");
        sqlText.append("FROM ld_bcn_pullup_t p \n");
        sqlText.append("WHERE p.pull_flag > 0 \n");
        sqlText.append("  AND p.person_unvl_id = ?");
        initSelectedPositionOrgsTemplates[0] = sqlText.toString();
        sqlText.delete(0, sqlText.length());
        
        sqlText.append("INSERT INTO ld_bcn_build_pos_sel01_mt \n");
        sqlText.append(" (SESID, FIN_COA_CD, ORG_CD, ORG_LEVEL_CD) \n");
        sqlText.append("SELECT ?, r.fin_coa_cd, r.org_cd, ? \n");
        sqlText.append("FROM ld_bcn_org_rpts_t r, ld_bcn_build_pos_sel01_mt a \n");
        sqlText.append("WHERE a.sesid = ? \n");
        sqlText.append("  AND a.org_level_cd = ? \n");
        sqlText.append("  AND a.fin_coa_cd = r.rpts_to_fin_coa_cd \n");
        sqlText.append("  AND a.org_cd = r.rpts_to_org_cd \n");
        sqlText.append("  AND not (r.fin_coa_cd = r.rpts_to_fin_coa_cd and r.org_cd = r.rpts_to_org_cd)");
        populateSelectedPositionOrgsSubTreeTemplates[0] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // insert actives that are funded with person or vacant
        sqlText.append("INSERT INTO ld_bcn_pos_sel_t \n");
        sqlText.append(" (PERSON_UNVL_ID, POSITION_NBR, UNIV_FISCAL_YR, EMPLID,  \n");
        sqlText.append("  IU_POSITION_TYPE, POS_DEPTID, SETID_SALARY , SAL_ADMIN_PLAN, GRADE, POS_DESCR, PERSON_NM) \n");
        sqlText.append("SELECT DISTINCT ?, p.position_nbr,p.univ_fiscal_yr, af.emplid, p.iu_position_type, \n");
        sqlText.append("    p.pos_deptid, p.setid_salary, p.pos_sal_plan_dflt, p.pos_grade_dflt, p.pos_descr, i.person_nm \n");
        sqlText.append("FROM ld_bcn_build_pos_sel01_mt o, ld_bcn_pos_t p, \n");
        sqlText.append("     ld_pndbc_apptfnd_t af LEFT OUTER JOIN ld_bcn_intincbnt_t i ON (af.emplid=i.emplid) \n");
        sqlText.append("WHERE o.sesid = ? \n");
        sqlText.append("  AND p.pos_deptid = CONCAT(o.fin_coa_cd, CONCAT('-', o.org_cd)) \n");
        sqlText.append("  AND p.univ_fiscal_yr = ? \n");
        sqlText.append("  AND p.pos_eff_status <> 'I' \n");
        sqlText.append("  AND p.univ_fiscal_yr = af.univ_fiscal_yr \n");
        sqlText.append("  AND p.position_nbr = af.position_nbr \n");
        sqlText.append("GROUP BY p.position_nbr, p.univ_fiscal_yr, af.emplid,p.iu_position_type, p.pos_deptid, \n");
        sqlText.append("         p.setid_salary, p.pos_sal_plan_dflt, p.pos_grade_dflt, p.pos_descr, i.person_nm");  
        populatePositionSelectForSubTreeTemplates[0] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // add actives that are unfunded
        sqlText.append("INSERT INTO ld_bcn_pos_sel_t \n");
        sqlText.append(" (PERSON_UNVL_ID, POSITION_NBR, UNIV_FISCAL_YR, EMPLID,  \n");
        sqlText.append("  IU_POSITION_TYPE, POS_DEPTID, SETID_SALARY , SAL_ADMIN_PLAN, GRADE, POS_DESCR) \n");
        sqlText.append("SELECT DISTINCT ?, p.position_nbr, p.univ_fiscal_yr, 'NOTFUNDED', p.iu_position_type, \n");
        sqlText.append("    p.pos_deptid, p.setid_salary, p.pos_sal_plan_dflt, p.pos_grade_dflt, p.pos_descr \n");
        sqlText.append("FROM ld_bcn_build_pos_sel01_mt o, ld_bcn_pos_t p \n");
        sqlText.append("WHERE o.sesid = ? \n");
        sqlText.append("  AND p.pos_deptid = CONCAT(o.fin_coa_cd, CONCAT('-', o.org_cd)) \n");
        sqlText.append("  AND p.univ_fiscal_yr = ? \n");
        sqlText.append("  AND p.pos_eff_status <> 'I' \n");
        sqlText.append("  AND NOT EXISTS \n");
        sqlText.append("      (SELECT * \n");
        sqlText.append("       FROM ld_bcn_pos_sel_t ps \n");
        sqlText.append("       WHERE ps.person_unvl_id = ? \n");
        sqlText.append("         AND ps.position_nbr = p.position_nbr \n");
        sqlText.append("         AND ps.univ_fiscal_yr = p.univ_fiscal_yr) \n");
        sqlText.append("GROUP BY p.position_nbr, p.univ_fiscal_yr, p.iu_position_type, p.pos_deptid, \n");
        sqlText.append("         p.setid_salary, p.pos_sal_plan_dflt, p.pos_grade_dflt, p.pos_descr");
        populatePositionSelectForSubTreeTemplates[1] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // insert inactives that are funded due to timing problem
        sqlText.append("INSERT INTO ld_bcn_pos_sel_t \n");
        sqlText.append(" (PERSON_UNVL_ID, POSITION_NBR, UNIV_FISCAL_YR, EMPLID,  \n");
        sqlText.append("  IU_POSITION_TYPE, POS_DEPTID, SETID_SALARY , SAL_ADMIN_PLAN, GRADE, POS_DESCR, PERSON_NM) \n");
        sqlText.append("SELECT DISTINCT ?, p.position_nbr, p.univ_fiscal_yr, af.emplid, p.iu_position_type, \n");
        sqlText.append("    p.pos_deptid, p.setid_salary, p.pos_sal_plan_dflt, p.pos_grade_dflt, p.pos_descr, 'INACTIVE POS.' \n");
        sqlText.append("FROM ld_bcn_build_pos_sel01_mt o, ld_bcn_pos_t p, \n");
        sqlText.append("     ld_pndbc_apptfnd_t af LEFT OUTER JOIN ld_bcn_intincbnt_t i ON (af.emplid=i.emplid) \n");
        sqlText.append("WHERE o.sesid = ? \n");
        sqlText.append("  AND p.pos_deptid = CONCAT(o.fin_coa_cd, CONCAT('-', o.org_cd)) \n");
        sqlText.append("  AND p.univ_fiscal_yr = ? \n");
        sqlText.append("  AND p.pos_eff_status = 'I' \n");
        sqlText.append("  AND p.univ_fiscal_yr = af.univ_fiscal_yr \n");
        sqlText.append("  AND p.position_nbr = af.position_nbr \n");
        sqlText.append("GROUP BY p.position_nbr, p.univ_fiscal_yr, af.emplid,p.iu_position_type, p.pos_deptid, \n");
        sqlText.append("         p.setid_salary, p.pos_sal_plan_dflt, p.pos_grade_dflt, p.pos_descr");  
        populatePositionSelectForSubTreeTemplates[2] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // insert inactives that are unfunded
        sqlText.append("INSERT INTO ld_bcn_pos_sel_t \n");
        sqlText.append(" (PERSON_UNVL_ID, POSITION_NBR, UNIV_FISCAL_YR, EMPLID,  \n");
        sqlText.append(" IU_POSITION_TYPE, POS_DEPTID, SETID_SALARY , SAL_ADMIN_PLAN, GRADE, POS_DESCR, PERSON_NM) \n");
        sqlText.append("SELECT DISTINCT ?, p.position_nbr, p.univ_fiscal_yr, 'NOTFUNDED', p.iu_position_type, \n");
        sqlText.append("    p.pos_deptid, p.setid_salary, p.pos_sal_plan_dflt, p.pos_grade_dflt, p.pos_descr, 'INACTIVE POS.' \n");
        sqlText.append("FROM ld_bcn_build_pos_sel01_mt o, ld_bcn_pos_t p \n");
        sqlText.append("WHERE o.sesid = ? \n");
        sqlText.append("  AND p.pos_deptid = CONCAT(o.fin_coa_cd, CONCAT('-', o.org_cd)) \n");
        sqlText.append("  AND p.univ_fiscal_yr = ? \n");
        sqlText.append("  AND p.pos_eff_status = 'I' \n");
        sqlText.append("  AND NOT EXISTS \n");
        sqlText.append("      (SELECT * \n");
        sqlText.append("       FROM ld_bcn_pos_sel_t ps \n");
        sqlText.append("       WHERE ps.person_unvl_id = ? \n");
        sqlText.append("         AND ps.position_nbr = p.position_nbr \n");
        sqlText.append("         AND ps.univ_fiscal_yr = p.univ_fiscal_yr) \n");
        sqlText.append("GROUP BY p.position_nbr, p.univ_fiscal_yr, p.iu_position_type, p.pos_deptid, \n");
        sqlText.append("         p.setid_salary, p.pos_sal_plan_dflt, p.pos_grade_dflt, p.pos_descr");
        populatePositionSelectForSubTreeTemplates[3] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // set name field for vacants
        sqlText.append("UPDATE ld_bcn_pos_sel_t p \n");
        sqlText.append("SET person_nm = 'VACANT' \n");
        sqlText.append("WHERE p.person_unvl_id = ? \n");
        sqlText.append("  AND p.emplid = 'VACANT' \n");
        sqlText.append("  AND p.person_nm IS NULL");
        populatePositionSelectForSubTreeTemplates[4] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // reset name field for positions that only have deleted funding associated
        // note that this overwrites any actual names except for Inactives
        sqlText.append("UPDATE ld_bcn_pos_sel_t p \n");
        sqlText.append("SET person_nm = 'NOT FUNDED' \n");
        sqlText.append("WHERE p.person_unvl_id = ? \n");
        sqlText.append("  AND p.person_nm <> 'INACTIVE POS.' \n");
        sqlText.append("  AND NOT EXISTS \n");
        sqlText.append("    (SELECT * \n");
        sqlText.append("    FROM ld_pndbc_apptfnd_t af \n");
        sqlText.append("    WHERE af.univ_fiscal_yr = p.univ_fiscal_yr \n");
        sqlText.append("      AND af.position_nbr = p.position_nbr \n");
        sqlText.append("      AND af.appt_fnd_dlt_cd = 'N')");
        populatePositionSelectForSubTreeTemplates[5] = sqlText.toString();
        sqlText.delete(0, sqlText.length());

        // anything leftover is not funded
        sqlText.append("UPDATE ld_bcn_pos_sel_t p \n");
        sqlText.append("SET person_nm = 'NOT FUNDED' \n");
        sqlText.append("WHERE p.person_unvl_id = ? \n");
        sqlText.append("    AND p.person_nm IS NULL \n");
        populatePositionSelectForSubTreeTemplates[6] = sqlText.toString();
        
    }
    
    /**
     * @see org.kuali.module.budget.dao.OrganizationSalarySettingSearchDao#buildIntendedIncumbentSelect(java.lang.String,
     *      java.lang.Integer)
     */
    @RawSQL
    public void buildIntendedIncumbentSelect(String personUserIdentifier, Integer universityFiscalYear) {

        LOG.debug("buildIntendedIncumbentSelect() started");
        
        getSimpleJdbcTemplate().update(buildIntendedIncumbentSelectTemplates[0], personUserIdentifier, universityFiscalYear);
    }

    /**
     * @see org.kuali.module.budget.dao.OrganizationSalarySettingSearchDao#cleanIntendedIncumbentSelect(java.lang.String)
     */
    public void cleanIntendedIncumbentSelect(String personUserIdentifier) {

        clearTempTableByUnvlId("ld_bcn_incumbent_sel_t", "PERSON_UNVL_ID", personUserIdentifier);
    }

    /**
     * @see org.kuali.module.budget.dao.OrganizationSalarySettingSearchDao#buildPositionSelect(java.lang.String, java.lang.Integer)
     */
    public void buildPositionSelect(String personUserIdentifier, Integer universityFiscalYear) {

        LOG.debug("buildPositionSelect() started");

        String sessionId = new Guid().toString();
        initSelectedPositionOrgs(sessionId, personUserIdentifier);

        populatePositionSelectForSubTree(sessionId, personUserIdentifier, universityFiscalYear);

        clearTempTableBySesId("ld_bcn_build_pos_sel01_mt", "SESID", sessionId);
    }

    @RawSQL
    private void initSelectedPositionOrgs(String sessionId, String personUserIdentifier) {

        int currentLevel = 0;

        int rowsAffected = getSimpleJdbcTemplate().update(initSelectedPositionOrgsTemplates[0], sessionId, currentLevel, personUserIdentifier);

        if (rowsAffected > 0) {
            populateSelectedPositionOrgsSubTree(currentLevel, sessionId);
        }
    }

    @RawSQL
    private void populateSelectedPositionOrgsSubTree(int previousLevel, String sessionId) {

        if (previousLevel <= MAXLEVEL) {
            int currentLevel = previousLevel + 1;

            int rowsAffected = getSimpleJdbcTemplate().update(populateSelectedPositionOrgsSubTreeTemplates[0], sessionId, currentLevel, sessionId, previousLevel);

            if (rowsAffected > 0) {
                populateSelectedPositionOrgsSubTree(currentLevel, sessionId);
            }
        }
        else {
            // overrun problem
            LOG.warn(String.format("\nWarning: One or more selected organizations have reporting organizations more than maxlevel of %d deep.", MAXLEVEL));
        }
    }

    @RawSQL
    private void populatePositionSelectForSubTree(String sessionId, String personUserIdentifier, Integer universityFiscalYear) {

        // insert actives that are funded with person or vacant
        getSimpleJdbcTemplate().update(populatePositionSelectForSubTreeTemplates[0], personUserIdentifier, sessionId, universityFiscalYear);

        // add actives that are unfunded
        getSimpleJdbcTemplate().update(populatePositionSelectForSubTreeTemplates[1], personUserIdentifier, sessionId, universityFiscalYear, personUserIdentifier);

        // insert inactives that are funded due to timing problem
        getSimpleJdbcTemplate().update(populatePositionSelectForSubTreeTemplates[2], personUserIdentifier, sessionId, universityFiscalYear);

        // insert inactives that are unfunded
        getSimpleJdbcTemplate().update(populatePositionSelectForSubTreeTemplates[3], personUserIdentifier, sessionId, universityFiscalYear, personUserIdentifier);

        // set name field for vacants
        getSimpleJdbcTemplate().update(populatePositionSelectForSubTreeTemplates[4], personUserIdentifier);

        // reset name field for positions that only have deleted funding associated
        // note that this overwrites any actual names except for Inactives
        getSimpleJdbcTemplate().update(populatePositionSelectForSubTreeTemplates[5], personUserIdentifier);

        // anything leftover is not funded
        getSimpleJdbcTemplate().update(populatePositionSelectForSubTreeTemplates[6], personUserIdentifier);

    }

    /**
     * @see org.kuali.module.budget.dao.OrganizationSalarySettingSearchDao#cleanPositionSelect(java.lang.String)
     */
    public void cleanPositionSelect(String personUserIdentifier) {

        clearTempTableByUnvlId("ld_bcn_pos_sel_t", "PERSON_UNVL_ID", personUserIdentifier);
    }

}

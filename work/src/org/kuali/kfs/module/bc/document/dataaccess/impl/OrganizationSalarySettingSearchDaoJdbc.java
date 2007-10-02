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

import org.kuali.module.budget.dao.OrganizationSalarySettingSearchDao;

/**
 * This class...
 */
public class OrganizationSalarySettingSearchDaoJdbc extends BudgetConstructionDaoJdbcBase implements OrganizationSalarySettingSearchDao {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger( OrganizationSalarySettingSearchDaoJdbc.class );

    /**
     * @see org.kuali.module.budget.dao.OrganizationSalarySettingSearchDao#buildIntendedIncumbentSelect(java.lang.String, java.lang.Integer)
     */
    public void buildIntendedIncumbentSelect(String personUserIdentifier, Integer universityFiscalYear) {

        LOG.debug( "buildIntendedIncumbentSelect() started" );

        // This uses the GROUP BY clause to force a distinct set so as to not trip over the unique constraint on the target table.
        // For some reason the constraint is violated without the use of GROUP BY in Oracle
        getSimpleJdbcTemplate()
        .update(  "INSERT INTO ld_bcn_incumbent_sel_t "
                + " (PERSON_UNVL_ID, EMPLID, FIN_OBJECT_CD, PERSON_NM) "
                + "SELECT DISTINCT pull.person_unvl_id, bcaf.emplid, bcaf.fin_object_cd, iinc.person_nm "
                + "FROM ld_bcn_pullup_t pull, ld_bcn_acct_org_hier_t hier, ld_pndbc_apptfnd_t bcaf, ld_bcn_intincbnt_t iinc "
                + "WHERE pull.pull_flag > 0 "
                + "  AND pull.person_unvl_id = ? "
                + "  AND hier.univ_fiscal_yr = ? "
                + "  AND hier.org_fin_coa_cd = pull.fin_coa_cd "
                + "  AND hier.org_cd = pull.org_cd "
                + "  AND bcaf.univ_fiscal_yr = hier.univ_fiscal_yr "
                + "  AND bcaf.fin_coa_cd = hier.fin_coa_cd "
                + "  AND bcaf.account_nbr = hier.account_nbr "
                + "  AND bcaf.emplid NOT IN ('VACANT') "
                + "  AND iinc.emplid = bcaf.emplid "
                + "GROUP BY pull.person_unvl_id, bcaf.emplid, bcaf.fin_object_cd, iinc.person_nm", personUserIdentifier, universityFiscalYear);

    }

    /**
     * @see org.kuali.module.budget.dao.OrganizationSalarySettingSearchDao#cleanIntendedIncumbentSelect(java.lang.String)
     */
    public void cleanIntendedIncumbentSelect(String personUserIdentifier) {

        clearTempTableByUnvlId("ld_bcn_incumbent_sel_t", "PERSON_UNVL_ID", personUserIdentifier);
    }

}

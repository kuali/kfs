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

import java.util.ArrayList;

import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.batch.dataaccess.impl.SQLForStep;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionSynchronizationProblemsReportDao;
import org.kuali.kfs.sys.KFSConstants.BudgetConstructionPositionConstants;

/**
 * builds a report table of people whose salaries are budgeted in the wrong object class or have had a position change that merits
 * an object code validity check
 */

public class BudgetConstructionSynchronizationProblemsReportDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetConstructionSynchronizationProblemsReportDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionSynchronizationProblemsReportDaoJdbc.class);

    protected static ArrayList<SQLForStep> updateReportsSynchronizationProblemsTable = new ArrayList<SQLForStep>(2);

    public BudgetConstructionSynchronizationProblemsReportDaoJdbc() {

        ArrayList<Integer> insertionPoints = new ArrayList<Integer>(2);

        // builds and updates SynchronizationProblemsReports
        // builds the salary and default object check MT table
        StringBuilder sqlText = new StringBuilder(1500);
        sqlText.append("INSERT INTO LD_BCN_POS_FND_T \n");
        sqlText.append("(PERSON_UNVL_ID, SEL_ORG_FIN_COA, SEL_ORG_CD, PERSON_NM, EMPLID, POSITION_NBR, \n");
        sqlText.append(" UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD) \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, COALESCE(iinc.PERSON_NM,'Name not Found'), bcaf.emplid, bcaf.position_nbr, \n");
        sqlText.append(" bcaf.univ_fiscal_yr, bcaf.fin_coa_cd, bcaf.account_nbr, bcaf.sub_acct_nbr, bcaf.fin_object_cd, bcaf.fin_sub_obj_cd \n");
        sqlText.append("FROM (LD_PNDBC_APPTFND_T bcaf LEFT OUTER JOIN LD_BCN_INTINCBNT_T iinc ON (bcaf.emplid = iinc.emplid)), LD_BCN_CTRL_LIST_T ctrl \n");
        sqlText.append("WHERE ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.appt_fnd_dlt_cd = 'N' \n");
        sqlText.append(" AND (bcaf.pos_obj_chg_ind = 'Y' OR bcaf.pos_sal_chg_ind = 'Y') \n");
        sqlText.append(" UNION ALL\n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, COALESCE(iinc.PERSON_NM,'Name not Found'), bcaf.emplid, bcaf.position_nbr, \n");
        sqlText.append(" bcaf.univ_fiscal_yr, bcaf.fin_coa_cd, bcaf.account_nbr, bcaf.sub_acct_nbr, bcaf.fin_object_cd, bcaf.fin_sub_obj_cd \n");
        sqlText.append("FROM (LD_PNDBC_APPTFND_T bcaf LEFT OUTER JOIN LD_BCN_INTINCBNT_T iinc ON (bcaf.emplid = iinc.emplid)), LD_BCN_POS_T bp, LD_BCN_CTRL_LIST_T ctrl \n");
        sqlText.append("WHERE ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND bcaf.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND bcaf.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND bcaf.appt_fnd_dlt_cd = 'N' \n");
        sqlText.append(" AND bcaf.pos_obj_chg_ind <> 'Y' \n");
        sqlText.append(" AND bcaf.pos_sal_chg_ind <> 'Y' \n");
        sqlText.append(" AND bcaf.univ_fiscal_yr = bp.univ_fiscal_yr \n");
        sqlText.append(" AND bcaf.position_nbr = bp.position_nbr \n");
        sqlText.append(" AND (bp.pos_eff_status <> '");
        // active effective status
        insertionPoints.add(sqlText.length());
        sqlText.append("' OR bp.budgeted_posn <> 'Y') \n");

        updateReportsSynchronizationProblemsTable.add(new SQLForStep(sqlText, insertionPoints));
        sqlText.delete(0, sqlText.length());
        insertionPoints.clear();

        sqlText.append("UPDATE LD_BCN_POS_FND_T \n");
        sqlText.append("SET PERSON_NM = '");
        // the string indicating a vacant EMPLID
        insertionPoints.add(sqlText.length());
        sqlText.append("' \n");
        sqlText.append("WHERE (PERSON_UNVL_ID = ?) \n");
        sqlText.append("AND (EMPLID = '");
        // the string indicating a vacant EMPLID
        insertionPoints.add(sqlText.length());
        sqlText.append("')");

        updateReportsSynchronizationProblemsTable.add(new SQLForStep(sqlText, insertionPoints));
        sqlText.delete(0, sqlText.length());
        insertionPoints.clear();

    }

    /**
     * removes any rows from a previous report for this uear
     * 
     * @param principalName--the user requesting the report
     */
    protected void cleanReportsSynchronizationProblemsTable(String principalName) {
        clearTempTableByUnvlId("LD_BCN_POS_FND_T", "PERSON_UNVL_ID", principalName);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionSynchronizationProblemsReportDao#updateReportsSynchronizationProblemsTable(java.lang.String)
     */
    public void updateReportsSynchronizationProblemsTable(String principalName) {
        ArrayList<String> stringsToInsert = new ArrayList<String>(2);
        // get rid of any old reports sitting around for this user
        cleanReportsSynchronizationProblemsTable(principalName);
        // insert the code for an active position
        stringsToInsert.add(BudgetConstructionPositionConstants.POSITION_EFFECTIVE_STATUS_ACTIVE);
        // insert into the report table filled or vacant lines with an object code change, a position change, or an inactive
        // position
        getSimpleJdbcTemplate().update(updateReportsSynchronizationProblemsTable.get(0).getSQL(stringsToInsert), principalName, principalName, principalName, principalName);
        // change the name field for any line with a vacant position
        stringsToInsert.clear();
        stringsToInsert.add(BCConstants.VACANT_EMPLID);
        stringsToInsert.add(BCConstants.VACANT_EMPLID);
        getSimpleJdbcTemplate().update(updateReportsSynchronizationProblemsTable.get(1).getSQL(stringsToInsert), principalName);
    }

}

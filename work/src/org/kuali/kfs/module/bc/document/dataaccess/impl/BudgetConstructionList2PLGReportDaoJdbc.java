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

import org.kuali.kfs.module.bc.batch.dataaccess.impl.SQLForStep;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionList2PLGReportDao;
import org.kuali.kfs.sys.KFSConstants.BudgetConstructionConstants;

/**
 * builds the report for accounts which are out of balance because benefits have been calculated on salaries and the amounts don't
 * match what is in GL. the discrepancy is in an object code called '2PLG'.
 */

public class BudgetConstructionList2PLGReportDaoJdbc extends BudgetConstructionDaoJdbcBase implements BudgetConstructionList2PLGReportDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionList2PLGReportDaoJdbc.class);

    protected static ArrayList<SQLForStep> updateReportsList2PLGTable = new ArrayList<SQLForStep>(1);

    public BudgetConstructionList2PLGReportDaoJdbc() {

        ArrayList<Integer> insertionPoints = new ArrayList<Integer>(1);

        // builds and updates List2PLGReports
        /*
         * get accounts, sub-accounts that have an imbalance between detailed salary and general ledger, displayed in the object
         * class 2PLG
         */
        StringBuilder sqlText = new StringBuilder(500);
        sqlText.append("INSERT INTO LD_BCN_2PLG_LIST_MT \n");
        sqlText.append("(PERSON_UNVL_ID, ORG_FIN_COA_CD, ORG_CD, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, ACLN_ANNL_BAL_AMT) \n");
        sqlText.append("SELECT ?, ctrl.sel_org_fin_coa, ctrl.sel_org_cd, ctrl.fin_coa_cd, ctrl.account_nbr, ctrl.sub_acct_nbr, pbgl.acln_annl_bal_amt \n");
        sqlText.append("FROM LD_PND_BCNSTR_GL_T pbgl, LD_BCN_CTRL_LIST_T ctrl \n");
        sqlText.append("WHERE ctrl.person_unvl_id = ? \n");
        sqlText.append(" AND pbgl.fdoc_nbr = ctrl.fdoc_nbr \n");
        sqlText.append(" AND pbgl.univ_fiscal_yr = ctrl.univ_fiscal_yr \n");
        sqlText.append(" AND pbgl.fin_coa_cd = ctrl.fin_coa_cd \n");
        sqlText.append(" AND pbgl.account_nbr = ctrl.account_nbr \n");
        sqlText.append(" AND pbgl.sub_acct_nbr = ctrl.sub_acct_nbr \n");
        sqlText.append(" AND pbgl.fin_object_cd = '");
        // the 2PLG constant
        insertionPoints.add(sqlText.length());
        sqlText.append("'");

        updateReportsList2PLGTable.add(new SQLForStep(sqlText, insertionPoints));
        insertionPoints.clear();
        sqlText.delete(0, sqlText.length());

    }

    /**
     * remove from the table rows for any previous report requested by this user
     */
    protected void cleanReportsList2PLGTable(String principalName) {
        clearTempTableByUnvlId("LD_BCN_2PLG_LIST_MT", "PERSON_UNVL_ID", principalName);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionList2PLGReportDao#updateReportsList2PLGTable(java.lang.String)
     */
    public void updateList2PLGReportsTable(String principalName) {
        // get rid of any rows from a previous report requested by this user
        cleanReportsList2PLGTable(principalName);
        // fetch the constant naming the 2PLG object class
        ArrayList<String> stringToInsert = new ArrayList<String>(1);
        stringToInsert.add(BudgetConstructionConstants.OBJECT_CODE_2PLG);
        // fill the table
        getSimpleJdbcTemplate().update(updateReportsList2PLGTable.get(0).getSQL(stringToInsert), principalName, principalName);
    }

}

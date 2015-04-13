/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

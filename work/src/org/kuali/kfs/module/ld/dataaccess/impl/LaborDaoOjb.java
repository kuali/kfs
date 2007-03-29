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
package org.kuali.module.labor.dao.ojb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.module.budget.bo.CalculatedSalaryFoundationTracker;
import org.kuali.module.gl.util.OJBUtility;
import org.kuali.module.labor.bo.AccountStatus;
import org.kuali.module.labor.bo.BalanceByGeneralLedgerKey;
import org.kuali.module.labor.bo.BalanceGlobalCalculatedSalaryFoundation;
import org.kuali.module.labor.dao.LaborDao;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * This class is a facade for Labor Distribution DAO balance inquiries
 */
public class LaborDaoOjb extends PersistenceBrokerDaoSupport implements LaborDao {
    private LaborDaoOjb dao;

    /**
     * 
     * @see org.kuali.module.labor.dao.LaborDao#getCSFTrackerData(java.util.Map)
     */
    public Collection getCSFTrackerData(Map fieldValues) {
        Criteria criteria = new Criteria();
        criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(fieldValues, new CalculatedSalaryFoundationTracker()));
        LookupUtils.applySearchResultsLimit(criteria);
        QueryByCriteria query = QueryFactory.newQuery(CalculatedSalaryFoundationTracker.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * 
     * @see org.kuali.module.labor.dao.LaborDao#getCurrentYearFunds(java.util.Map)
     */
    public Collection getCurrentYearFunds(Map fieldValues) {
        Criteria criteria = new Criteria();
        criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(fieldValues, new BalanceByGeneralLedgerKey()));
        LookupUtils.applySearchResultsLimit(criteria);
        QueryByCriteria query = QueryFactory.newQuery(BalanceByGeneralLedgerKey.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * 
     * @see org.kuali.module.labor.dao.LaborDao#getBaseFunds(java.util.Map)
     */
    public Collection getBaseFunds(Map fieldValues) {
        Criteria criteria = new Criteria();
        criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(fieldValues, new BalanceGlobalCalculatedSalaryFoundation()));
        LookupUtils.applySearchResultsLimit(criteria);
        QueryByCriteria query = QueryFactory.newQuery(BalanceGlobalCalculatedSalaryFoundation.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }
    
    /**
     * This method retrieves information for the Base Funds balance inquiry
     * 
     * @param fieldValues
     * @return
     */
    public Collection getBaseFunds2(Map fieldValues) {
        Statement stmt = null;
        ArrayList baseFundList = new ArrayList();
        String selectStatement = "select distinct A0.PERSON_UNVL_ID, A0.SUB_ACCT_NBR, A0.FIN_OBJECT_CD, A0.FIN_SUB_OBJ_CD, A0.OBJ_ID, A0.VER_NBR, A0.BUDGET_AMT, A0.CSF_AMT ";
        String fromStatement = "FROM LD_BAL_GLBL_CSF_T A0, LD_LDGR_ENTR_T A1 WHERE "; 
        // We also need to:
        //
        // 1) Use the EMPLID from LD_LDGR_ENTR_T and match this to the PERSON_UNVL_ID in LD_BAL_GLBL_CSF_T.
        // 2) * Return the employee name in the original data object. Doing option #2 is the reason for not using OJB.
        
        try {

            // Parse the UI for parameters
            String accountNumber = fieldValues.get("accountNumber").toString();
            String universityFiscalYear = fieldValues.get("universityFiscalYear").toString();
            String charOfAccountsCode = fieldValues.get("charOfAccountsCode").toString();
            String financialObjectCode = fieldValues.get("financialObjectCode").toString();

            // Build the where statement
            String whereStatement = "ACCOUNT_NBR like'" + accountNumber + "%' and UNIV_FISCAL_YR ='" + universityFiscalYear + "'";
                        
            // Use the KFS Connection String to retrieve data from the database
            Connection connection = getPersistenceBroker(true).serviceConnectionManager().getConnection();
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(selectStatement + fromStatement + whereStatement);
            while (rs.next()) {
                AccountStatus as = new AccountStatus();
                as.setUniversityFiscalYear(Integer.valueOf(rs.getString("UNIV_FISCAL_YR")));
                as.setAccountNumber(rs.getString("ACCOUNT_NBR"));
                baseFundList.add(as);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to execute: " + e.getMessage());
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch (Exception e) {
                throw new RuntimeException("Unable to close database connection: " + e.getMessage());
            }
        }
        // Return the query
        return baseFundList;
    }
}
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
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.module.budget.bo.CalculatedSalaryFoundationTracker;
import org.kuali.module.gl.util.OJBUtility;
import org.kuali.module.labor.bo.AccountStatus;
import org.kuali.module.labor.bo.BalanceByGeneralLedgerKey;
import org.kuali.module.labor.dao.LaborDao;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * This class is a facade for Labor Distribution DAO balance inquiries
 */
public class LaborDaoOjb extends PersistenceBrokerDaoSupport implements LaborDao {
    private LaborDaoOjb dao;

    // CSF Tracker Inquiry
    public Collection getCSFTrackerData(Map fieldValues) {        
        Criteria criteria = new Criteria();        
        criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(fieldValues, new CalculatedSalaryFoundationTracker()));
        LookupUtils.applySearchResultsLimit(criteria);
        QueryByCriteria query = QueryFactory.newQuery(CalculatedSalaryFoundationTracker.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }
    
    // CSF Current Year Funds Inquiry
    public Collection getCurrentYearFunds(Map fieldValues) {        
        Criteria criteria = new Criteria();        
        criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(fieldValues, new BalanceByGeneralLedgerKey()));
        LookupUtils.applySearchResultsLimit(criteria);
        QueryByCriteria query = QueryFactory.newQuery(BalanceByGeneralLedgerKey.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }
    
    // CSF Base Funds Inquiry
    public Collection getBaseFunds1(Map fieldValues) {
        
        Criteria criteria = new Criteria();        
        criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(fieldValues, new AccountStatus()));
        System.out.println("Criteria:" + criteria);
    //    LookupUtils.applySearchResultsLimit(criteria);
      //  QueryByCriteria query = QueryFactory.newQuery(AccountStatus.class, criteria);
        
//        dao.sqlCommand("DELETE fp_bal_by_obj_t WHERE person_sys_id = USERENV('SESSIONID')");        
        
//        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new AccountStatus());
        ReportQueryByCriteria query = QueryFactory.newReportQuery(AccountStatus.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);

    }
    
    // CSF Base Funds Inquiry
    public Collection getBaseFunds(Map fieldValues) {
    Statement stmt = null;
    ArrayList baseFundList = new ArrayList();
    String selectStatement = "SELECT distinct A1.UNIV_FISCAL_YR, A0.PERSON_UNVL_ID, A0.SUB_ACCT_NBR, A0.FIN_OBJECT_CD, A0.FIN_SUB_OBJ_CD, A0.OBJ_ID, A0.VER_NBR, A0.BUDGET_AMT, A0.CSF_AMT ";
    String fromStatement = "FROM LD_BAL_GLBL_CSF_T A0, LD_LDGR_ENTR_T A1 WHERE ";
    try {
        Criteria criteria = new Criteria();
        
        String accountNumber = fieldValues.get("accountNumber").toString();
        String universityFiscalYear = fieldValues.get("universityFiscalYear").toString();
        
        String whereStatement = "ACCOUNT_NBR like'" + accountNumber + "%' and UNIV_FISCAL_YR ='" + universityFiscalYear + "'";
        Connection connection = getPersistenceBroker(true).serviceConnectionManager().getConnection();
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(selectStatement + fromStatement + whereStatement); 
        while (rs.next()) {
            System.out.println("#1");
            AccountStatus as = new AccountStatus();
            as.setUniversityFiscalYear(Integer.valueOf(rs.getString("UNIV_FISCAL_YR")));                    
            as.setAccountNumber(rs.getString("ACCOUNT_NBR"));
            baseFundList.add(as);        
            System.out.println("#2");
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
            throw new RuntimeException("Unable to close connection: " + e.getMessage());
        }
    }
    return baseFundList;    
    }
}
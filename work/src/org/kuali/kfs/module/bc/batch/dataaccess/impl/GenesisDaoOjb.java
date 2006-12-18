/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.budget.dao.ojb;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Arrays;

import org.kuali.module.budget.dao.*;
import org.kuali.module.budget.bo.*;
import org.kuali.module.financial.bo.FiscalYearFunctionControl;
import org.kuali.module.financial.bo.FunctionControlCode;
import org.kuali.core.dao.DocumentHeaderDao;
import org.kuali.core.document.DocumentHeader;
import org.kuali.Constants;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.chart.bo.*;


import org.apache.ojb.broker.query.*;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;
import org.apache.log4j.Logger;


public class GenesisDaoOjb extends PersistenceBrokerDaoSupport 
             implements GenesisDao {

    private FiscalYearFunctionControl fiscalYearFunctionControl;
    private FunctionControlCode functionControlCode;
    
    /*  turn on the logger for the persistence broker */
    private static Logger LOG = org.apache.log4j.Logger.getLogger(GenesisDaoOjb.class);

    /*  these constants should be in PropertyConstants */
    public final static String BUDGET_FLAG_PROPERTY_NAME = "financialSystemFunctionControlCode";
    public final static String BUDGET_FLAG_VALUE = "financialSystemFunctionActiveIndicator";
    public final static String BUDGET_CZAR_CHART = "UA";
    public final static String FINANCIAL_CHART_PROPERTY = "chartOfAccountsCode"; 
    public final static String BUDGET_CZAR_ORG = "BUDU";
    public final static String ORG_CODE_PROPERTY = "organizationCode";
    public final static String FISCAL_OFFICER_ID_PROPERTY = "accountFiscalOfficerSystemIdentifier";
    public final static String ACCOUNT_CLOSED_INDICATOR_PROPERTY = "accountClosedIndicator";


    public final Map<String,String> getBudgetConstructionControlFlags (Integer universityFiscalYear)
    {
        /*  return the flag names and the values for all the BC flags for the fiscal year */
        
        /*  the key to the map returned will be the name of the flag
         *  the entry will be the flag's value 
         */
        Map<String, String> controlFlags = new HashMap();
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(Constants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME,
                                         universityFiscalYear); 
        String[] queryAttr = {BUDGET_FLAG_PROPERTY_NAME,BUDGET_FLAG_VALUE};
        ReportQueryByCriteria queryID = 
            new ReportQueryByCriteria(FiscalYearFunctionControl.class, 
                                        queryAttr, criteriaID);
        Iterator Results = 
            getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        /* fill in the map */
        while (Results.hasNext())
        {
          String[] mapValues = (String []) ((Object []) Results.next());
          controlFlags.put(mapValues[0],mapValues[1]);
        };
        return controlFlags;        
    }
    
    public final boolean getBudgetConstructionControlFlag(Integer universityFiscalYear, String FlagID)
    {
        /*  return true if a flag is on, false if it is not */
        String Result;
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(Constants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME,
                                         universityFiscalYear);
        criteriaID.addEqualTo(BUDGET_FLAG_PROPERTY_NAME,FlagID);
        String[] queryAttr = {BUDGET_FLAG_VALUE};
        ReportQueryByCriteria queryID = 
            new ReportQueryByCriteria(FiscalYearFunctionControl.class, queryAttr, criteriaID, true);
        Iterator Results = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(
                             queryID);
        // TODO@ we need to create an exception, put a try around this block, and log errors
        Result = (String) ((Object[]) Results.next()) [0];
        return (Result == Constants.ParameterValues.YES);
            
    }
    
    public final String getBudgetConstructionInitiatorID()
    {
        //@TODO: The constants and field names below should come from constants files
        //  the chart and department should be budget construction constants
        //  the others should be kuali constants
        final String DEFAULT_ID = "666-666-66";
        Criteria criteriaID = new Criteria();
        criteriaID.addEqualTo(FINANCIAL_CHART_PROPERTY, BUDGET_CZAR_CHART);
        criteriaID.addEqualTo(ORG_CODE_PROPERTY,BUDGET_CZAR_ORG);
        criteriaID.addColumnEqualTo(ACCOUNT_CLOSED_INDICATOR_PROPERTY,
                Constants.ParameterValues.NO);
        String[] queryAttr = {FISCAL_OFFICER_ID_PROPERTY};
        ReportQueryByCriteria queryID = 
            new ReportQueryByCriteria(Account.class, queryAttr, criteriaID, true);
        Iterator Results = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        if (!Results.hasNext())
        {
            return DEFAULT_ID;
        }
        else
        {  
           String retID = (String) ((Object[]) Results.next())[0];  
           return retID;
        }
    }
    
    /*
     *  here are the routines we will use for updating budget construction GL
     *  
     */
    // maps (hash maps) to return the results of the GL call
    // --pBGL contains all the rows returned, stuffed into an object that can be 
    //   saved to the pending budget construction general ledger
    // --bCHdr contains one entry for each potentially new key for the budget
    //   construction header table.
    private Map<String,PendingBudgetConstructionGeneralLedger>  pBGL;
    private Map<String,BudgetConstructionHeader> bCHdr;
    private HashMap[] returnPointers;
    // these are the indexes for each of the fields returned in the select list
    // of the SQL statement
    private Integer sqlChartOfAccountsCode = 0;
    private Integer sqlAccountNumber = 1;
    private Integer sqlSubAccountNumber = 2;
    private Integer sqlObjectCode = 3;
    private Integer sqlSubObjectCode = 4;
    private Integer sqlBalanceTypeCode = 5;
    private Integer sqlObjectTypeCode = 6;
    private Integer sqlAccountLineAnnualBalanceAmount = 7;
    private Integer sqlBeginningBalanceLineAmount = 8;
    
    public HashMap[] readGLForPBGL(Integer BaseYear)
    {
        pBGL      = new HashMap();
        bCHdr     = new HashMap();
        //
        //  set up a report query to fetch all the GL rows we are going to need
        Criteria criteriaID = new Criteria();
        // we only pick up a single balance type
        // we also use an integer fiscal year
        // *** this is a point of change if either of these criteria change ***
        // @@TODO We should regularize the sources for these constants
        // they should probably all come from GL (although UNIV_FISCAL_YR is generic)
        // we should add the two hard-wired strings at the bottom
        criteriaID.addEqualTo(GLConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR,
                BaseYear);
        criteriaID.addEqualTo(GLConstants.ColumnNames.BALANCE_TYPE_CODE,
                              Constants.BALANCE_TYPE_BASE_BUDGET);
        String[] queryAttr = {GLConstants.ColumnNames.CHART_OF_ACCOUNTS_CODE,
                              GLConstants.ColumnNames.ACCOUNT_NUMBER,
                              GLConstants.ColumnNames.SUB_ACCOUNT_NUMBER,
                              GLConstants.ColumnNames.OBJECT_CODE,
                              GLConstants.ColumnNames.SUB_OBJECT_CODE,
                              GLConstants.ColumnNames.BALANCE_TYPE_CODE,
                              GLConstants.ColumnNames.FIN_OBJ_TYP_CODE,
                              "ACLN_ANNL_BAL_AMT",
                              "FIN_BEG_BAL_LN_AMT"};
        ReportQueryByCriteria queryID = 
            new ReportQueryByCriteria(Balance.class, queryAttr, criteriaID, true);
        // @@TODO this should be in a try/catch structure.  We should catch a 
        //        SQL error, write it to the log, and raise a more generic error
        //        ("error reading GL Balance Table in BC batch"), and throw that
        Iterator Results = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(queryID);
        // @@TODO now we have to iterate and create the two maps
        // if the sum of the two balances is zero, we DO NOT want the row
        //  return the array pointing to the two lists
        returnPointers = new HashMap[2];
        returnPointers[0] = (HashMap) pBGL;
        returnPointers[1] = (HashMap) bCHdr;
        return returnPointers;
    }
    //
    // these two methods build the GL field string that triggers creation of a new
    // budget construction header
    public String buildHeaderTestKeyFromPBGL (PendingBudgetConstructionGeneralLedger
            pendingBudgetConstructionGeneralLedger)
            {
               String headerBCTestKey = new String();
               headerBCTestKey = pendingBudgetConstructionGeneralLedger.getChartOfAccountsCode()+
                                 pendingBudgetConstructionGeneralLedger.getAccountNumber()+
                                 pendingBudgetConstructionGeneralLedger.getSubAccountNumber();
               return headerBCTestKey;
            }
    private String buildHeaderTestKeyFromSQLResults (Object[] sqlResult)
    {
        String headerBCTestKey = new String();
        headerBCTestKey = (String) sqlResult[sqlChartOfAccountsCode]+
                          (String) sqlResult[sqlAccountNumber]+
                          (String) sqlResult[sqlSubAccountNumber];
        return headerBCTestKey;
    }
    //
    // these two methods build the GL field string that triggers creation of a new
    // pending budget construction general ledger row
    public String buildGLTestKeyFromPBGL (PendingBudgetConstructionGeneralLedger
            pendingBudgetConstructionGeneralLedger)
    {
       String PBGLTestKey = new String();
       PBGLTestKey = pendingBudgetConstructionGeneralLedger.getChartOfAccountsCode()+
                         pendingBudgetConstructionGeneralLedger.getAccountNumber()+
                         pendingBudgetConstructionGeneralLedger.getSubAccountNumber()+
                         pendingBudgetConstructionGeneralLedger.getFinancialObjectCode()+
                         pendingBudgetConstructionGeneralLedger.getFinancialSubObjectCode()+
                         pendingBudgetConstructionGeneralLedger.getFinancialBalanceTypeCode()+
                         pendingBudgetConstructionGeneralLedger.getFinancialObjectTypeCode();
       return PBGLTestKey;
    }
    private String buildGLTestKeyFromSQLResults (Object[] sqlResult)
    {
        String GLTestKey = new String();
        GLTestKey = (String) sqlResult[sqlChartOfAccountsCode]+
                    (String) sqlResult[sqlAccountNumber]+
                    (String) sqlResult[sqlSubAccountNumber]+
                    (String) sqlResult[sqlObjectCode]+
                    (String) sqlResult[sqlSubObjectCode]+
                    (String) sqlResult[sqlBalanceTypeCode]+
                    (String) sqlResult[sqlObjectTypeCode];
        return GLTestKey;
    }
}

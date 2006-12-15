/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/bc/batch/dataaccess/impl/GenesisDaoOjb.java,v $
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License")
;
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

import org.kuali.module.budget.dao.*;
import org.kuali.module.financial.bo.FiscalYearFunctionControl;
import org.kuali.module.financial.bo.FunctionControlCode;
import org.kuali.core.dao.DocumentHeaderDao;
import org.kuali.core.document.DocumentHeader;
import org.kuali.Constants;
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
    
}

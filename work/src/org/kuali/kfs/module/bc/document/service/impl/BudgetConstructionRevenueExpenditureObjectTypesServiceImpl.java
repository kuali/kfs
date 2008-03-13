/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.budget.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.module.budget.BCParameterConstants;
import org.kuali.module.budget.document.BudgetConstructionDocument;
import org.kuali.module.budget.service.BudgetConstructionRevenueExpenditureObjectTypesService;

import org.kuali.kfs.service.ParameterService;

import org.apache.log4j.*;

/**
 * 
 * get the Budget Construction object type constants once, and set up the structures that the various Dao's used to implement on-line
 * reporting will need to incorporate into their SQL.
 * on-line reporting deals with many rows, and needs to be reasonably fast because a user is waiting for it to finish.  this service reads the parameters once and
 * sets up static values which can be used repeatedly by instantiations of the Dao's to build their SQL.
 */
public class BudgetConstructionRevenueExpenditureObjectTypesServiceImpl implements BudgetConstructionRevenueExpenditureObjectTypesService {

    private ParameterService parameterService;
    
    private Logger LOG = Logger.getLogger(BudgetConstructionRevenueExpenditureObjectTypesService.class);
    
    private static ArrayList<String> expenditureObjectTypes;
    private static ArrayList<String> revenueObjectTypes;
    private static ArrayList<String> expenditureReportObjectTypes;
    private static ArrayList<String> revenueReportObjectTypes;
    
    private static String expenditureObjectTypesINList;
    private static String revenueObjectTypesINList;
    private static String expenditureReportObjectTypesINList;
    private static String revenueReportObjectTypesINList;
    
    private static final String liabilityObjectType = new String("LI");
    private static final String assetsObjectType    = new String("AS");
    
    public BudgetConstructionRevenueExpenditureObjectTypesServiceImpl(ParameterService parameterService)
    {
        // use the parameter service to access the DB and build the ObjectType variables
        this.parameterService = parameterService;
        expenditureObjectTypes = new ArrayList<String>(parameterService.getParameterValues(BudgetConstructionDocument.class,BCParameterConstants.EXPENDITURE_OBJECT_TYPES));
        revenueObjectTypes     = new ArrayList<String>(parameterService.getParameterValues(BudgetConstructionDocument.class,BCParameterConstants.REVENUE_OBJECT_TYPES));

        // reporting object types add liabilities to expenditure, and assets to revenue
        expenditureReportObjectTypes = new ArrayList<String>(expenditureObjectTypes);
        expenditureReportObjectTypes.add(liabilityObjectType);
        revenueReportObjectTypes = new ArrayList<String>(revenueObjectTypes);
        revenueReportObjectTypes.add(assetsObjectType);
        
        // build the corresponding IN List strings
        expenditureObjectTypesINList       = inString(expenditureObjectTypes);
        revenueObjectTypesINList           = inString(revenueObjectTypes);
        expenditureReportObjectTypesINList = inString(expenditureReportObjectTypes);
        revenueReportObjectTypesINList     = inString(revenueReportObjectTypes);
    }
    
    /**
     * 
     * @see org.kuali.module.budget.service.BudgetConstructionRevenueExpenditureObjectTypesService#getBudgetConstructionExpenditureObjectTypes()
     */
    public ArrayList<String> getBudgetConstructionExpenditureObjectTypes() {
        return expenditureObjectTypes;
    }

    /**
     * 
     * @see org.kuali.module.budget.service.BudgetConstructionRevenueExpenditureObjectTypesService#getBudgetConstructionExpenditureObjectTypesINList()
     */
    public String getBudgetConstructionExpenditureObjectTypesINList() {
        return expenditureObjectTypesINList;
    }

    /**
     * 
     * @see org.kuali.module.budget.service.BudgetConstructionRevenueExpenditureObjectTypesService#getBudgetConstructionRevenueObjectTypes()
     */
    public ArrayList<String> getBudgetConstructionRevenueObjectTypes() {
        return revenueObjectTypes;
    }

    /**
     * 
     * @see org.kuali.module.budget.service.BudgetConstructionRevenueExpenditureObjectTypesService#getBudgetConstructionRevenueObjectTypesINList()
     */
    public String getBudgetConstructionRevenueObjectTypesINList() {
        return revenueObjectTypesINList;
    }

    /**
     * 
     * @see org.kuali.module.budget.service.BudgetConstructionRevenueExpenditureObjectTypesService#getBudgetConstructionExpenditureReportObjectTypes()
     */
    public ArrayList<String> getBudgetConstructionExpenditureReportObjectTypes() {
        return expenditureReportObjectTypes;
    }

    /**
     * 
     * @see org.kuali.module.budget.service.BudgetConstructionRevenueExpenditureObjectTypesService#getBudgetConstructionExpenditureReportObjectTypesINList()
     */
    public String getBudgetConstructionExpenditureReportObjectTypesINList() {
        return expenditureReportObjectTypesINList;
    }

    /**
     * 
     * @see org.kuali.module.budget.service.BudgetConstructionRevenueExpenditureObjectTypesService#getBudgetConstructionRevenueReportObjectTypes()
     */
    public ArrayList<String> getBudgetConstructionRevenueReportObjectTypes() {
        return revenueReportObjectTypes;
    }

    /**
     * 
     * @see org.kuali.module.budget.service.BudgetConstructionRevenueExpenditureObjectTypesService#getBudgetConstructionRevenueReportObjectTypesINList()
     */
    public String getBudgetConstructionRevenueReportObjectTypesINList() {
        return revenueReportObjectTypesINList;
    }

    private String inString (ArrayList<String> inListValues)
    {
        // the delimiter for strings in the DB is assumed to be a single quote.
        // this is the ANSI-92 standard.
        // if the ArrayList input is empty, IN ('') is returned.
        StringBuilder inBuilder = new StringBuilder(150);
        
        inBuilder.append(" IN ('");
        if (! inListValues.isEmpty())
        {
          inBuilder.append(inListValues.get(0));
        }
        for (int idx = 1; idx < inListValues.size(); idx++)
        {
            inBuilder.append("','");
            inBuilder.append(inListValues.get(idx));
        }
        inBuilder.append("')");
        
        return inBuilder.toString();
    }

    // temporary test method to verify everything works 
    public void testMethod()
    {
        ArrayList<String> testArray = new ArrayList<String>();
        LOG.warn(String.format("\n\nreturned for a null list     : %s",inString(testArray)));
        LOG.warn(String.format("\nexpenditureObjectTypes       : %s",this.getBudgetConstructionExpenditureObjectTypesINList()));
        LOG.warn(String.format("\nrevenueObjectTypes           : %s",this.getBudgetConstructionRevenueObjectTypesINList()));
        LOG.warn(String.format("\nexpenditureReportObjectTypes : %s",this.getBudgetConstructionExpenditureReportObjectTypesINList()));
        LOG.warn(String.format("\nrevenueReportObjectTypes     : %s",this.getBudgetConstructionRevenueReportObjectTypesINList()));
        LOG.warn(String.format("\n\nArrayList returns:\nexpenditureObjectTypes       : %s",this.getBudgetConstructionExpenditureObjectTypes()));
        LOG.warn(String.format("\nrevenueObjectTypes           : %s",this.getBudgetConstructionRevenueObjectTypes()));
        LOG.warn(String.format("\nexpenditureReportObjectTypes : %s",this.getBudgetConstructionExpenditureReportObjectTypes()));
        LOG.warn(String.format("\nrevenueReportObjectTypes     : %s\n\n",this.getBudgetConstructionRevenueReportObjectTypes()));
        
    }
}

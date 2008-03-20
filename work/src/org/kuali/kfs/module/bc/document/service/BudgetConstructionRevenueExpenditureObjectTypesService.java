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
package org.kuali.module.budget.service;

import java.util.ArrayList;

/**
 * 
 * read the parameter table once to get the revenue and expenditure object types used in budget construction.
 * provide methods to return these as array lists.
 * provide methods to return these as SQL-ready IN clauses as well.
 */

public interface BudgetConstructionRevenueExpenditureObjectTypesService {

    /**
     * 
     * fetch the object types for expenditure which can be used in monthly budgets
     * @return an ArrayList of these
     */
    public ArrayList<String> getBudgetConstructionExpenditureObjectTypes();
    
    /**
     * 
     * fetch the object types for expenditure used in monthly budgets as a SQL IN list
     * @return--IN list as a String
     */
    public String getBudgetConstructionExpenditureObjectTypesINList();
    
    /**
     * 
     * fetch the object types for revenue which can be used in monthly budgets
     * @return an ArrayList of these
     */
    public ArrayList<String> getBudgetConstructionRevenueObjectTypes();
    
    /**
     * 
     * fetch the object types for revenue used in monthly budgets as a SQL IN list
     * @return--IN list as a String
     */
    public String getBudgetConstructionRevenueObjectTypesINList();
    
    //TODO:  this method systematically prints the returned values of all the routines
    public void testMethod();
}

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
package org.kuali.module.budget.service;

import java.util.List;

import org.kuali.module.budget.bo.BudgetConstructionPullup;


/**
 * TODO Need to modify comments
 * This interface defines methods that manipulate objects used by the Organization Selection screens. Manipulated objects include
 * BudgetConstructionPullup with methods that populate and depopulate the associated table for a specific user.
 */
public interface BudgetReportsControlListService {
    
    public void cleanReportsControlList(String idForSession, String personUserIdentifier);
    
    public void updateRportsControlList(String idForSession, String personUserIdentifier, Integer universityFiscalYear, List<BudgetConstructionPullup> budgetConstructionPullup);
    
    public void changeFlagOrganizationAndChartOfAccountCodeSelection(String personUserIdentifier, List<BudgetConstructionPullup> budgetConstructionPullup);
    
    public void cleanReportsSubFundGroupSelectList(String personUserIdentifier);
    
    public void updateReportsSubFundGroupSelectList(String personUserIdentifier);
    
    public void cleanReportsAccountSummaryTable(String personUserIdentifier);
    
    public void updateRepotsAccountSummaryTable(String personUserIdentifier);
    
    public void updateRepotsAccountSummaryTableWithConsolidation(String personUserIdentifier);
    
    public void updateReportsSelectedSubFundGroupFlags(String personUserIdentifier, List<String> selectedSubfundGroupCodeList);
    
}

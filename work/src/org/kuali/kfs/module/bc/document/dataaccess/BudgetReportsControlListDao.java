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
package org.kuali.module.budget.dao;

public interface BudgetReportsControlListDao {

    
    public void buildSubFundSelectList();
    
    public void cleanReportsControlList(String personUserIdentifier);    
    
    public void cleanReportsControlListPart1(String personUserIdentifier);    
    
    public void cleanReportsControlListPart2(String personUserIdentifier);
    
    public void updateReportsControlListpart1(String idForSession, String personUserIdentifier, Integer universityFiscalYear);

    public void updateReportsControlListpart2(String idForSession, String personUserIdentifier, String chartOfAccountsCode, String organizationCode );

    public void updateReportsControlListDisp1(String idForSession);    
    
    public void changeFlagOrganizationAndChartOfAccountCodeSelection(String personUserIdentifier, String chartOfAccountsCode, String organizationCode);
    
    public void updateReportsSubFundGroupSelectList(String personUserIdentifier);
            
    public void cleanReportsSubFundGroupSelectList(String personUserIdentifier);
    
    public void cleanReportsAccountSummaryTable (String personUserIdentifier);
    
    public void updateRepotsAccountSummaryTable(String personUserIdentifier);
    
    public void updateRepotsAccountSummaryTableWithConsolidation(String personUserIdentifier);
    
    public void updateReportsSelectedSubFundGroupFlags(String personUserIdentifier, String subfundGroupCode);
    
}

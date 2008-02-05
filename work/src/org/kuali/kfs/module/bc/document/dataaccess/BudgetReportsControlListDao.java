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

    
    /**
     * This method cleans control list table.
     * 
     * @param idForSession
     * @param personUserIdentifier
     * @return
     */
    public void cleanReportsControlList(String personUserIdentifier);    
    
    /**
     * This method cleans control list table 01.
     * 
     * @param idForSession
     * @param personUserIdentifier
     * @return
     */
    public void cleanReportsControlListPart1(String personUserIdentifier);    
    
    /**
     * This method cleans control list table 01.
     * 
     * @param idForSession
     * @param personUserIdentifier
     * @return
     */
    public void cleanReportsControlListPart2(String personUserIdentifier);
    
    /**
     * This method updates control list table 01.
     * 
     * @param idForSession
     * @param personUserIdentifier
     * @param universityFiscalYear
     * @return
     */
    public void updateReportsControlListpart1(String idForSession, String personUserIdentifier, Integer universityFiscalYear);

    /**
     * This method updates control list table 02.
     * 
     * @param idForSession
     * @param personUserIdentifier
     * @param universityFiscalYear
     * @return
     */
    public void updateReportsControlListpart2(String idForSession, String personUserIdentifier, String chartOfAccountsCode, String organizationCode );

    /**
     * This method updates control list table disp1.
     * 
     * @param idForSession
     * @return
     */
    public void updateReportsControlListDisp1(String idForSession);    
    
    /**
     * This method changes flags in ld_bcn_pullup_t with Organization and Chart code.
     * 
     * @param personUserIdentifier
     * @param budgetConstructionPullup
     * @return
     */
    public void changeFlagOrganizationAndChartOfAccountCodeSelection(String personUserIdentifier, String chartOfAccountsCode, String organizationCode);
    
    /**
     * This method updates sub-fund group list
     * 
     * @param personUserIdentifier
     * @return
     */
    public void updateReportsSubFundGroupSelectList(String personUserIdentifier);
            
    /**
     * This method cleans sub-fund group list.
     * 
     * @param personUserIdentifier
     * @return
     */
    public void cleanReportsSubFundGroupSelectList(String personUserIdentifier);
    
    /**
     * This method cleans acount summary table.
     * 
     * @param personUserIdentifier
     * @return
     */
    public void cleanReportsAccountSummaryTable (String personUserIdentifier);
    
    /**
     * This method updates acount summary table.
     * 
     * @param personUserIdentifier
     * @return
     */
    public void updateRepotsAccountSummaryTable(String personUserIdentifier);
    
    /**
     * This method updates acount summary table when users choose consolidation.
     * 
     * @param personUserIdentifier
     * @return
     */
    public void updateRepotsAccountSummaryTableWithConsolidation(String personUserIdentifier);
    
    /**
     * This method updates flags in LD_BCN_SUBFUND_PICK_T with selected sub-fund group code.
     * 
     * @param personUserIdentifier
     * @param selectedSubfundGroupCodeList
     * @return
     */
    public void updateReportsSelectedSubFundGroupFlags(String personUserIdentifier, String subfundGroupCode);
    
}

/*
 * Copyright 2006-2007 The Kuali Foundation.
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

/*
 * this service intializes/updates the budget construction data used by the budget module to build a new budget for the coming
 * fiscal year
 */
public interface GenesisService {
    /*
     * these routines indicate which actions are allowed in genesis
     */
    public boolean BatchPositionSynchAllowed(Integer BaseYear);

    public boolean CSFUpdatesAllowed(Integer BaseYear);

    public boolean GLUpdatesAllowed(Integer BaseYear);

    /*
     * build the budget construction GL table from the BALANCE_TYPE_BASE_BUDGET rows in GL table
     */
    public void stepBudgetConstructionGLLoad(Integer universityFiscalYear);

    // this step clears out the database for genesis
    public void clearDBForGenesis(Integer BaseYear);

    // use today's date to return the base fiscal year
    public Integer genesisFiscalYearFromToday();

    // this step runs genesis
    public void genesisStep(Integer BaseYear);

    // February, 2007
    // these are the two transactional steps
    // when workflow is "embedded", this will all change.
    // these are no longer needed now that workflow is in the same transaction
    public void genesisDocumentStep(Integer BaseYear);

    public void genesisFinalStep(Integer BaseYear);

    public void testStep(Integer universityFiscalYear);

    public void testSLFStep(Integer universityFiscalYear);

    public void testSLFAfterStep(Integer universityFiscalYear);

    public void testLockClearance(Integer currentFiscalYear);

    public void testPositionBuild(Integer currentFiscalYear);

    public Object testDocumentHeader();

    public void testChartCreation();

    public void testHierarchyCreation(Integer currentFiscalYear);
    
    public String testFindBCDocumentNumber (Integer fiscalYear, String chartOfAccounts, String accountNumber, String subAccountNumber);
}
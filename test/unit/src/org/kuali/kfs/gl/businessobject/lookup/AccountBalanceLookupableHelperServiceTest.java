/*
 * Copyright 2006 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.gl.businessobject.lookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.Constant;
import org.kuali.kfs.gl.businessobject.AccountBalance;
import org.kuali.kfs.gl.businessobject.AccountBalanceByConsolidation;
import org.kuali.kfs.gl.service.AccountBalanceService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.lookup.LookupableSpringContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;

/**
 * This class contains the test cases that can be applied to the method in AccountBalanceLookupableImpl class.
 */
@ConfigureContext
public class AccountBalanceLookupableHelperServiceTest extends AbstractGeneralLedgerLookupableHelperServiceTestBase {

    private AccountBalanceService accountBalanceService;

    /**
     * Initializes the services needed for this test
     * @see org.kuali.kfs.gl.businessobject.lookup.AbstractGeneralLedgerLookupableHelperServiceTestBase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        setAccountBalanceService(SpringContext.getBean(AccountBalanceService.class));
        lookupableHelperServiceImpl = LookupableSpringContext.getLookupableHelperService("glAccountBalanceLookupableHelperService");
        lookupableHelperServiceImpl.setBusinessObjectClass(AccountBalanceByConsolidation.class);
    }

    /**
     * Covers the search results returned by AccountBalanceLookupableService
     * @throws Exception thrown if any exception is encountered for any reason
     * @see org.kuali.module.gl.web.lookupable.AbstractGLLookupableTestBase#testGetSearchResults()
     */
    public void testGetSearchResults() throws Exception {
        testDataGenerator.generateTransactionData(pendingEntry);
        AccountBalance accountBalance = new AccountBalance(pendingEntry);

        // test the search results before the specified entry is inserted into the database
        Map fieldValues = getLookupFieldValues(accountBalance, true);
        List searchResults = lookupableHelperServiceImpl.getSearchResults(fieldValues);
        assertTrue(!contains(searchResults, accountBalance));

        // add a new entry into database. If complete this step, such entry should not in the database
        insertNewRecord(accountBalance);

        // test the search results with only the required fields
        fieldValues = getLookupFieldValues(accountBalance, true);
        searchResults = lookupableHelperServiceImpl.getSearchResults(fieldValues);
        assertTrue(searchResults.size() == 1);
        assertTrue(contains(searchResults, accountBalance));

        // add a new entry into database. If complete this step, such entry should not in the database
        // Here, we can reuse encumbrance declared above
        fieldValues = getLookupFieldValues(accountBalance, true);
        AccountBalance accountBalance1 = new AccountBalance(pendingEntry);
        accountBalance1.setSubAccountNumber(testDataGenerator.getPropertyValue("genericSubAccountNumber"));

        insertNewRecord(accountBalance1);

        // test the search results with all specified fields. The new record cannot meet the search criteria.
        fieldValues = getLookupFieldValues(accountBalance, true);
        searchResults = lookupableHelperServiceImpl.getSearchResults(fieldValues);
        assertTrue(searchResults.size() == 1);
        assertTrue(!contains(searchResults, accountBalance1));

        // test the search results with all specified fields. The new record cannot meet the search criteria.
        fieldValues = getLookupFieldValues(accountBalance, false);
        searchResults = lookupableHelperServiceImpl.getSearchResults(fieldValues);
        assertTrue(searchResults.size() >= 2);
        assertTrue(contains(searchResults, accountBalance));
        assertTrue(contains(searchResults, accountBalance1));
    }

    /**
     * This method includes the test cases applied to the consolidation option: Consolidate and Detail
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testConsolidationOption() throws Exception {
        // ensure the transaction data does not exist in enty table. Otherwise, execption may be raised
        testDataGenerator.generateTransactionData(pendingEntry);
        AccountBalance accountBalanceOne = new AccountBalance(pendingEntry);

        insertNewRecord(accountBalanceOne);

        // get the number of the search results before adding the second record into database
        Map fieldValues = getLookupFieldValues(accountBalanceOne, true);
        fieldValues.put(Constant.CONSOLIDATION_OPTION, Constant.CONSOLIDATION);

        List searchResults = lookupableHelperServiceImpl.getSearchResults(fieldValues);
        int numOfFirstResults = searchResults.size();

        String subAccountNumber = testDataGenerator.getPropertyValue("genericSubAccountNumber");
        pendingEntry.setSubAccountNumber(subAccountNumber);
        AccountBalance accountBalanceTwo = new AccountBalance(pendingEntry);

        insertNewRecord(accountBalanceTwo);

        // test if the second record is consolidated with others
        fieldValues = getLookupFieldValues(accountBalanceOne, true);
        fieldValues.put(Constant.CONSOLIDATION_OPTION, Constant.CONSOLIDATION);

        searchResults = lookupableHelperServiceImpl.getSearchResults(fieldValues);
        int numOfSecondResults = searchResults.size();
        assertTrue(numOfSecondResults == numOfFirstResults);

        // test if the search results appear in details
        fieldValues = getLookupFieldValues(accountBalanceOne, false);
        fieldValues.put(Constant.CONSOLIDATION_OPTION, Constant.DETAIL);

        searchResults = lookupableHelperServiceImpl.getSearchResults(fieldValues);
        int numOfThirdResults = searchResults.size();
        assertTrue(numOfSecondResults < numOfThirdResults);
    }

    /**
     * This method includes the test cases applied to the consolidation option: Consolidate and Detail
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testPerformance() throws Exception {
        long threshlod = 60000;

        // get the number of the search results before adding the second record into database
        AccountBalance accountBalance = new AccountBalance();
        accountBalance.setAccountNumber("1031400");
        accountBalance.setUniversityFiscalYear(TestUtils.getFiscalYearForTesting());
        accountBalance.setChartOfAccountsCode("BL");

        Map fieldValues = getLookupFieldValues(accountBalance, true);
        fieldValues.put(Constant.CONSOLIDATION_OPTION, Constant.CONSOLIDATION);

        long startTime = System.currentTimeMillis();
        List searchResults = lookupableHelperServiceImpl.getSearchResults(fieldValues);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println(duration + "ms");
        assertTrue("Too slow", duration < threshlod);

        // test if the search results appear in details
        fieldValues = getLookupFieldValues(accountBalance, false);
        fieldValues.put(Constant.CONSOLIDATION_OPTION, Constant.DETAIL);

        startTime = System.currentTimeMillis();
        searchResults = lookupableHelperServiceImpl.getSearchResults(fieldValues);
        endTime = System.currentTimeMillis();
        duration = endTime - startTime;

        System.out.println(duration + "ms");
        assertTrue("Too slow", duration < threshlod);
    }

    /**
     * Returns a List of field names to check in the search results
     * @param isExtended true if extended attributes should be included for checking, false otherwise
     * @return a List of field names to check
     * @see org.kuali.module.gl.web.lookupable.AbstractGLLookupableTestBase#getLookupFields(boolean)
     */
    public List getLookupFields(boolean isExtended) {
        List lookupFields = new ArrayList();

        lookupFields.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        lookupFields.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        lookupFields.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        lookupFields.add(Constant.CONSOLIDATION_OPTION);

        // include the extended fields
        if (isExtended) {
            lookupFields.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
            lookupFields.add(KFSPropertyConstants.OBJECT_CODE);
            lookupFields.add(KFSPropertyConstants.SUB_OBJECT_CODE);
        }
        return lookupFields;
    }


    /**
     * This method inserts a new account balance record into database
     * 
     * @param accounBalance the given account balance
     */
    protected void insertNewRecord(AccountBalance accounBalance) {
        try {
            getAccountBalanceService().save(accounBalance);
        }
        catch (Exception e) {
        }
    }

    /**
     * Gets the accountBalanceService attribute.
     * 
     * @return Returns the accountBalanceService.
     */
    public AccountBalanceService getAccountBalanceService() {
        return accountBalanceService;
    }

    /**
     * Sets the accountBalanceService attribute value.
     * 
     * @param accountBalanceService The accountBalanceService to set.
     */
    public void setAccountBalanceService(AccountBalanceService accountBalanceService) {
        this.accountBalanceService = accountBalanceService;
    }
}

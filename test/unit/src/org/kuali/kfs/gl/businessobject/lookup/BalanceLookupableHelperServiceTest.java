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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.Constant;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.service.BalanceService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.lookup.LookupableSpringContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class contains the test cases that can be applied to the method in BalanceLookupableImpl class.
 */
@ConfigureContext
public class BalanceLookupableHelperServiceTest extends AbstractGeneralLedgerLookupableHelperServiceTestBase {

    private BalanceService balanceService;

    /**
     * Sets up the services needed to test balance lookups
     * @see org.kuali.kfs.gl.businessobject.lookup.AbstractGeneralLedgerLookupableHelperServiceTestBase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        setBalanceService(SpringContext.getBean(BalanceService.class));
        lookupableHelperServiceImpl = LookupableSpringContext.getLookupableHelperService("glBalanceLookupableHelperService");
        lookupableHelperServiceImpl.setBusinessObjectClass(Balance.class);
    }

    /**
     * Covers the search results returned by BalanceLookupableHelperService
     * @throws Exception thrown if an exception is encountered for any reason
     * @see org.kuali.module.gl.web.lookupable.AbstractGLLookupableTestBase#testGetSearchResults()
     */
    public void testGetSearchResults() throws Exception {
        testDataGenerator.generateTransactionData(pendingEntry);
        Balance balance = new Balance(pendingEntry);

        // test the search results before the specified entry is inserted into the database
        Map fieldValues = getLookupFieldValues(balance, true);
        List searchResults = lookupableHelperServiceImpl.getSearchResults(fieldValues);
        assertTrue(testDataGenerator.getMessageValue("noSuchRecord"), !contains(searchResults, balance));

        // add a new entry into database
        this.insertNewRecord(balance);

        // the new record can meet the search criteria.
        fieldValues = getLookupFieldValues(balance, true);
        searchResults = lookupableHelperServiceImpl.getSearchResults(fieldValues);
        assertTrue(testDataGenerator.getMessageValue("wrongRecordSize"), searchResults.size() == 1);
        assertTrue(testDataGenerator.getMessageValue("failToFindRecord"), contains(searchResults, balance));

        // add another entry into database
        Balance anotherBalance = new Balance(pendingEntry);
        anotherBalance.setAccountNumber(testDataGenerator.getPropertyValue("genericAccountNumber"));
        this.insertNewRecord(anotherBalance);

        // the new record cannot meet the search criteria.
        fieldValues = getLookupFieldValues(balance, true);
        searchResults = lookupableHelperServiceImpl.getSearchResults(fieldValues);
        assertTrue(testDataGenerator.getMessageValue("wrongRecordSize"), searchResults.size() == 1);
        assertTrue(testDataGenerator.getMessageValue("failToFindRecord"), !contains(searchResults, anotherBalance));

        // the new record cannot meet the search criteria.
        fieldValues = getLookupFieldValues(balance, false);
        searchResults = lookupableHelperServiceImpl.getSearchResults(fieldValues);
        assertTrue(testDataGenerator.getMessageValue("wrongRecordSize"), searchResults.size() >= 1);
        assertTrue(testDataGenerator.getMessageValue("failToFindRecord"), !contains(searchResults, anotherBalance));
    }

    /**
     * This method includes the test cases applied to the pending entry option: Approved and All
     * 
     * @throws Exception thrown if an exception is encountered for any reason
     */
    public void testPendingEntryOption() throws Exception {
        testDataGenerator.generateTransactionData(pendingEntry);
        pendingEntry.setUniversityFiscalPeriodCode("CB");
        KualiDecimal pendingAmount = pendingEntry.getTransactionLedgerEntryAmount().abs();
        Balance balance = new Balance(pendingEntry);

        // prepare the test data for the pending option: approved
        insertNewPendingEntry(pendingEntry);
        this.insertNewRecord(balance);

        // test if the approved pending entry has been combined with cash balance
        Map fieldValues = getLookupFieldValues(balance, true);
        fieldValues.put(Constant.PENDING_ENTRY_OPTION, Constant.APPROVED_PENDING_ENTRY);
        fieldValues.put(Constant.CONSOLIDATION_OPTION, Constant.CONSOLIDATION);
        List searchResults = lookupableHelperServiceImpl.getSearchResults(fieldValues);
        assertTrue(testDataGenerator.getMessageValue("wrongRecordSize"), searchResults.size() >= 1);

        Balance result = (Balance) searchResults.get(0);
        KualiDecimal approvedCGBeginningBalance = result.getContractsGrantsBeginningBalanceAmount().abs();
        // assertTrue(testDataGenerator.getMessageValue("incorrectAmount"), approvedCGBeginningBalance.equals(pendingAmount));

        // prepare the test data for the pending option: All
        GeneralLedgerPendingEntry nonapprovedPendingEntry = new GeneralLedgerPendingEntry();
        testDataGenerator.generateTransactionData(nonapprovedPendingEntry);
        nonapprovedPendingEntry.setUniversityFiscalPeriodCode("CB");
        nonapprovedPendingEntry.setFinancialDocumentApprovedCode(" ");

        String sequenceNumber = testDataGenerator.getProperties().getProperty("genericSquenceNumber");
        nonapprovedPendingEntry.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceNumber));

        insertNewPendingEntry(nonapprovedPendingEntry);

        // test if the all pending entries have been combined with cash balance
        fieldValues = getLookupFieldValues(balance, true);
        fieldValues.put(Constant.PENDING_ENTRY_OPTION, Constant.ALL_PENDING_ENTRY);
        fieldValues.put(Constant.CONSOLIDATION_OPTION, Constant.CONSOLIDATION);

        searchResults = lookupableHelperServiceImpl.getSearchResults(fieldValues);
        assertTrue(testDataGenerator.getMessageValue("wrongRecordSize"), searchResults.size() == 1);

        Balance newResult = (Balance) searchResults.get(0);
        KualiDecimal allCGBeginningBalance = newResult.getContractsGrantsBeginningBalanceAmount().abs();
        // assertTrue(testDataGenerator.getMessageValue("incorrectAmount"),
        // !approvedCGBeginningBalance.equals(allCGBeginningBalance));
    }

    /**
     * This method tests if the orphan pending entries can be included in the search results
     * 
     * @throws Exception thrown if an exception is encountered for any reason
     */
    public void testOrphanPendingEntry() throws Exception {
        testDataGenerator.generateTransactionData(pendingEntry);
        Balance balance = new Balance(pendingEntry);

        // test if the all pending entries have been combined into a balance record
        // TODO the pending entry cannot be retrived. It took one day to find the reason, but I didn't sovle that.
        getPendingEntryService().save(pendingEntry);
        insertNewRecord(balance);

        Map fieldValues = getLookupFieldValues(balance, true);
        fieldValues.put(Constant.PENDING_ENTRY_OPTION, Constant.ALL_PENDING_ENTRY);

        Map pendingEntryFieldValues = BusinessObjectFieldConverter.convertToTransactionFieldValues(fieldValues);
        pendingEntryFieldValues.remove(Constant.CONSOLIDATION_OPTION);
        pendingEntryFieldValues.remove(Constant.PENDING_ENTRY_OPTION);
        pendingEntryFieldValues.remove(Constant.AMOUNT_VIEW_OPTION);
        Iterator iterator = getPendingEntryService().findPendingLedgerEntriesForBalance(pendingEntryFieldValues, true);

        List searchResults = lookupableHelperServiceImpl.getSearchResults(fieldValues);
        assertTrue(testDataGenerator.getMessageValue("failToFindRecord"), contains(searchResults, balance));
        assertTrue(testDataGenerator.getMessageValue("wrongRecordSize"), searchResults.size() >= 1);
    }

    /**
     * This method includes the test cases applied to the consolidation option: Consolidate and Detail
     * 
     * @throws Exception thrown if an exception is encountered for any reason
     */
    public void testConsolidationOption() throws Exception {
        // ensure the transaction data does not exist in enty table. Otherwise, execption may be raised
        testDataGenerator.generateTransactionData(pendingEntry);
        Balance balanceOne = new Balance(pendingEntry);
        this.insertNewRecord(balanceOne);

        // get the number of the search results before adding the second record into database
        Map fieldValues = getLookupFieldValues(balanceOne, true);
        fieldValues.put(Constant.CONSOLIDATION_OPTION, Constant.CONSOLIDATION);

        List searchResults = lookupableHelperServiceImpl.getSearchResults(fieldValues);
        int numOfFirstResults = searchResults.size();

        String subAccountNumber = testDataGenerator.getPropertyValue("genericSubAccountNumber");
        pendingEntry.setSubAccountNumber(subAccountNumber);
        Balance balanceTwo = new Balance(pendingEntry);
        this.insertNewRecord(balanceTwo);

        // test if the second record is consolidated with others
        fieldValues = getLookupFieldValues(balanceOne, true);
        fieldValues.put(Constant.CONSOLIDATION_OPTION, Constant.CONSOLIDATION);

        searchResults = lookupableHelperServiceImpl.getSearchResults(fieldValues);
        int numOfSecondResults = searchResults.size();

        // test if the search results appear in details
        fieldValues = getLookupFieldValues(balanceOne, false);
        fieldValues.put(Constant.CONSOLIDATION_OPTION, Constant.DETAIL);

        searchResults = lookupableHelperServiceImpl.getSearchResults(fieldValues);
        int numOfThirdResults = searchResults.size();
        assertTrue(testDataGenerator.getMessageValue("wrongRecordSize"), numOfSecondResults < numOfThirdResults);
    }

    /**
     * This method includes the test cases applied to the amount view option: Monthly and Accumulate
     * 
     * @throws Exception thrown if an exception is encountered for any reason
     */
    public void testAmountViewOption() throws Exception {
        try {
            testDataGenerator.generateTransactionData(pendingEntry);
            Balance balanceOne = new Balance(pendingEntry);

            this.insertNewRecord(balanceOne);

            // get the amount of the second month
            Map fieldValues = getLookupFieldValues(balanceOne, true);
            fieldValues.put(Constant.AMOUNT_VIEW_OPTION, Constant.MONTHLY);

            List searchResults = lookupableHelperServiceImpl.getSearchResults(fieldValues);
            assertTrue(testDataGenerator.getMessageValue("wrongRecordSize"), searchResults.size() == 1);
            assertTrue(testDataGenerator.getMessageValue("failToFindRecord"), contains(searchResults, balanceOne));

            KualiDecimal month2Amount = ((Balance) searchResults.get(0)).getMonth2Amount();
            assertTrue(testDataGenerator.getMessageValue("incorrectAmount"), month2Amount.equals(KualiDecimal.ZERO));

            // get the acculated amount of the second month
            fieldValues = getLookupFieldValues(balanceOne, true);
            fieldValues.put(Constant.AMOUNT_VIEW_OPTION, Constant.ACCUMULATE);

            searchResults = lookupableHelperServiceImpl.getSearchResults(fieldValues);
            assertTrue(testDataGenerator.getMessageValue("wrongRecordSize"), searchResults.size() == 1);
            assertTrue(testDataGenerator.getMessageValue("failToFindRecord"), contains(searchResults, balanceOne));

            KualiDecimal accumulatedMonth2Amount = ((Balance) searchResults.get(0)).getMonth2Amount();

            // test if the acculated amount is greater than or equal to the monthly amount
            assertTrue(testDataGenerator.getMessageValue("incorrectAmount"), accumulatedMonth2Amount.isGreaterEqual(month2Amount));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method includes the test cases applied to the consolidation option: Consolidate and Detail
     * 
     * @throws Exception thrown if an exception is encountered for any reason
     */
    public void testPerformance() throws Exception {
        long threshlod = 60000;

        // get the number of the search results before adding the second record into database
        Balance balance = new Balance();
        balance.setAccountNumber("1031400");
        balance.setUniversityFiscalYear(TestUtils.getFiscalYearForTesting());
        balance.setChartOfAccountsCode("BL");

        Map fieldValues = getLookupFieldValues(balance, true);
        fieldValues.put(Constant.CONSOLIDATION_OPTION, Constant.CONSOLIDATION);

        long startTime = System.currentTimeMillis();
        List searchResults = lookupableHelperServiceImpl.getSearchResults(fieldValues);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println(duration + "ms");
        assertTrue("Too slow", duration < threshlod);

        // test if the search results appear in details
        fieldValues = getLookupFieldValues(balance, false);
        fieldValues.put(Constant.CONSOLIDATION_OPTION, Constant.DETAIL);

        startTime = System.currentTimeMillis();
        searchResults = lookupableHelperServiceImpl.getSearchResults(fieldValues);
        endTime = System.currentTimeMillis();
        duration = endTime - startTime;

        System.out.println(duration + "ms");
        assertTrue("Too slow", duration < threshlod);
    }

    /**
     * Returns the lookup fields to test in the search results
     * @param isExtended true if extended fields should be included, false if they should not be included
     * @return a List of field names to check
     * @see org.kuali.module.gl.web.lookupable.AbstractGLLookupableTestBase#getLookupFields(boolean)
     */
    public List getLookupFields(boolean isExtended) {
        List lookupFields = new ArrayList();

        lookupFields.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        lookupFields.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        lookupFields.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        lookupFields.add(KFSPropertyConstants.BALANCE_TYPE_CODE);

        lookupFields.add(Constant.CONSOLIDATION_OPTION);
        lookupFields.add(Constant.PENDING_ENTRY_OPTION);
        lookupFields.add(Constant.AMOUNT_VIEW_OPTION);

        // include the extended fields
        if (isExtended) {
            lookupFields.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
            lookupFields.add(KFSPropertyConstants.OBJECT_CODE);
            lookupFields.add(KFSPropertyConstants.SUB_OBJECT_CODE);
            lookupFields.add(KFSPropertyConstants.OBJECT_TYPE_CODE);
        }
        return lookupFields;
    }

    /**
     * This method inserts a new balance record into database
     * 
     * @param balance the given balance
     */
    protected void insertNewRecord(Balance balance) {
        try {
            SpringContext.getBean(BusinessObjectService.class).save(balance);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the balanceService attribute.
     * 
     * @return Returns the balanceService.
     */
    public BalanceService getBalanceService() {
        return balanceService;
    }

    /**
     * Sets the balanceService attribute value.
     * 
     * @param balanceService The balanceService to set.
     */
    public void setBalanceService(BalanceService balanceService) {
        this.balanceService = balanceService;
    }
}

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
package org.kuali.module.gl.web.lookupable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.lookup.LookupableSpringContext;
import org.kuali.module.gl.bo.Entry;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.EntryDao;
import org.kuali.test.ConfigureContext;

/**
 * This class contains the test cases that can be applied to the method in EntryLookupableImpl class.
 */
@ConfigureContext
public class EntryLookupableHelperServiceTest extends AbstractGLLookupableHelperServiceTestBase {

    private EntryDao entryDao;

    /**
     * Initializes the services that this test needs to run
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        entryDao = SpringContext.getBean(EntryDao.class);
        lookupableHelperServiceImpl = LookupableSpringContext.getLookupableHelperService("glEntryLookupableHelperService");
        lookupableHelperServiceImpl.setBusinessObjectClass(Entry.class);
    }

    /**
     * Tests the search results returned by the EntryLookupableHelperService
     * @see org.kuali.module.gl.web.lookupable.AbstractGLLookupableTestBase#testGetSearchResults()
     */
    public void testGetSearchResults() throws Exception {
        testDataGenerator.generateTransactionData(pendingEntry);
        Entry entry = new Entry(pendingEntry, date);

        // test the search results before the specified entry is inserted into the database
        Map fieldValues = getLookupFieldValues(entry, true);
        List searchResults = lookupableHelperServiceImpl.getSearchResults(fieldValues);
        assertTrue(testDataGenerator.getMessageValue("noSuchRecord"), !contains(searchResults, entry));

        // add a new entry into database.
        insertNewRecord(pendingEntry, date);

        // test the search results with only the required fields
        fieldValues = getLookupFieldValues(entry, true);
        searchResults = lookupableHelperServiceImpl.getSearchResults(fieldValues);
        int numOfFirstResult = searchResults.size();
        assertTrue(testDataGenerator.getMessageValue("wrongRecordSize"), searchResults.size() >= 1);
        assertTrue(testDataGenerator.getMessageValue("failToFindRecord"), contains(searchResults, entry));

        // test the search results with all specified fields
        fieldValues = getLookupFieldValues(entry, false);
        searchResults = lookupableHelperServiceImpl.getSearchResults(fieldValues);
        assertTrue(testDataGenerator.getMessageValue("wrongRecordSize"), searchResults.size() >= numOfFirstResult);
        assertTrue(testDataGenerator.getMessageValue("failToFindRecord"), contains(searchResults, entry));
    }

    /**
     * Returns a List of lookup fields to include in the test
     * @param isExtended true if extended fields should also be included, false otherwise
     * @return a List of lookup fields
     * @see org.kuali.module.gl.web.lookupable.AbstractGLLookupableTestBase#getLookupFields(boolean)
     */
    public List getLookupFields(boolean isExtended) {
        List lookupFields = new ArrayList();

        lookupFields.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        lookupFields.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        lookupFields.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        lookupFields.add(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);
        lookupFields.add(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);

        // include the extended fields
        if (isExtended) {
            lookupFields.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
            lookupFields.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
            lookupFields.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);

            lookupFields.add(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);
            lookupFields.add(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE);
            lookupFields.add(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE);
            lookupFields.add(KFSPropertyConstants.DOCUMENT_NUMBER);
            lookupFields.add(KFSPropertyConstants.ORGANIZATION_DOCUMENT_NUMBER);
        }
        return lookupFields;
    }

    /**
     * This method inserts a new account balance record into database
     * 
     * @param transaction the given transaction
     * @param date the current date
     */
    private void insertNewRecord(Transaction transaction, Date date) {
        try {
            entryDao.addEntry(transaction, date);
        }
        catch (Exception e) {
        }
    }
}

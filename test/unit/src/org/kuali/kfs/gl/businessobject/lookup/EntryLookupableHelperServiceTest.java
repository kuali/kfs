/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.gl.businessobject.lookup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.gl.dataaccess.EntryDao;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.lookup.LookupableSpringContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class contains the test cases that can be applied to the method in EntryLookupableImpl class.
 */
@ConfigureContext
public class EntryLookupableHelperServiceTest extends AbstractGeneralLedgerLookupableHelperServiceTestBase {

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
            Entry e = new Entry(transaction, date);
            SpringContext.getBean(BusinessObjectService.class).save(e);
        }
        catch (Exception e) {
        }
    }
}

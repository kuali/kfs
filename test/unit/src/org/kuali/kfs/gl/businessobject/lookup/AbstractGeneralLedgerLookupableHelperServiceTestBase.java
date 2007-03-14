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

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.GLSpringBeansRegistry;
import org.kuali.module.gl.web.TestDataGenerator;
import org.kuali.test.KualiTestBase;
import org.springframework.beans.factory.BeanFactory;

/**
 * This class is a template being used by the test case classes of GL lookupable implementation.
 * 
 * 
 */
public abstract class AbstractGLLookupableHelperServiceTestBase extends KualiTestBase {

    protected Date date;
    protected BeanFactory beanFactory;
    protected GeneralLedgerPendingEntry pendingEntry;
    protected TestDataGenerator testDataGenerator;
    protected AbstractGLLookupableHelperServiceImpl lookupableHelperServiceImpl;
    protected GeneralLedgerPendingEntryService pendingEntryService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        beanFactory = SpringServiceLocator.getBeanFactory();
        date = SpringServiceLocator.getDateTimeService().getCurrentDate();
        pendingEntry = new GeneralLedgerPendingEntry();
        testDataGenerator = new TestDataGenerator();

        setPendingEntryService((GeneralLedgerPendingEntryService) beanFactory.getBean(GLSpringBeansRegistry.generalLedgerPendingEntryService));
    }

    /**
     * test cases for getSearchResults method of LookupableImpl class
     */
    public abstract void testGetSearchResults() throws Exception;

    /**
     * This method defines the lookup fields
     * 
     * @param isExtended flag if the non required fields are included
     * @return a list of lookup fields
     */
    public abstract List getLookupFields(boolean isExtended);

    /**
     * This method defines the primary key fields
     * 
     * @return a list of primary key fields
     */
    public List getPrimaryKeyFields() {
        return lookupableHelperServiceImpl.getReturnKeys();
    }

    /**
     * test cases for getInquiryUrl method of LookupableImpl class
     */
    public void testGetInquiryUrl() throws Exception {
    }

    /**
     * This method tests if the search results have the given entry
     * 
     * @param searchResults the search results
     * @param businessObject the given business object
     * @return true if the given business object is in the search results
     */
    protected boolean contains(List searchResults, PersistableBusinessObjectBase businessObject) {
        boolean isContains = false;
        List priamryKeyFields = getPrimaryKeyFields();
        int numberOfPrimaryKeyFields = priamryKeyFields.size();

        String propertyName, resultPropertyValue, propertyValue;

        Iterator searchResultsIterator = searchResults.iterator();
        while (searchResultsIterator.hasNext() && !isContains) {
            Object resultRecord = searchResultsIterator.next();

            isContains = true;
            for (int i = 0; i < numberOfPrimaryKeyFields; i++) {
                try {
                    propertyName = (String) (priamryKeyFields.get(i));
                    resultPropertyValue = PropertyUtils.getProperty(resultRecord, propertyName).toString();
                    propertyValue = PropertyUtils.getProperty(businessObject, propertyName).toString();

                    if (!resultPropertyValue.equals(propertyValue)) {
                        isContains = false;
                        break;
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return isContains;
    }

    /**
     * This method creates the lookup form fields with the given business object and lookup fields
     * 
     * @param businessObject the given business object
     * @param isExtended determine if the extended lookup fields are used
     * @return a lookup form fields
     * @throws Exception
     */
    public Map getLookupFieldValues(PersistableBusinessObjectBase businessObject, boolean isExtended) throws Exception {
        List lookupFields = this.getLookupFields(isExtended);
        return testDataGenerator.generateLookupFieldValues(businessObject, lookupFields);
    }

    /**
     * This method inserts a new pending ledger Entry record into database
     * 
     * @param pendingEntry the given pending ledger Entry
     */
    protected void insertNewPendingEntry(GeneralLedgerPendingEntry pendingEntry) {
        try {
            getPendingEntryService().save(pendingEntry);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the pendingEntryService attribute.
     * 
     * @return Returns the pendingEntryService.
     */
    public GeneralLedgerPendingEntryService getPendingEntryService() {
        return pendingEntryService;
    }

    /**
     * Sets the pendingEntryService attribute value.
     * 
     * @param pendingEntryService The pendingEntryService to set.
     */
    public void setPendingEntryService(GeneralLedgerPendingEntryService pendingEntryService) {
        this.pendingEntryService = pendingEntryService;
    }
}

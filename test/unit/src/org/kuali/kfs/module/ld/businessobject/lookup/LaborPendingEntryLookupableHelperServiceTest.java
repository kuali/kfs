/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.businessobject.lookup;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.kfs.gl.web.TestDataGenerator;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.service.LaborLedgerPendingEntryService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.businessobject.lookup.LookupableSpringContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.bo.PersistableBusinessObject;

/**
 * Unit tests for the Lookup Helper Service of the <code>{@link LaborLedgerPendingEntry}</code> business object
 */
@ConfigureContext
public class LaborPendingEntryLookupableHelperServiceTest extends KualiTestBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LaborPendingEntryLookupableHelperServiceTest.class);

    private String messageFileName;
    private String propertiesFileName;
    private Date date;
    private LaborLedgerPendingEntry pendingEntry;
    private TestDataGenerator testDataGenerator;
    private LaborLedgerPendingEntryService pendingEntryService;
    private LaborPendingEntryLookupableHelperServiceImpl lookupableHelperServiceImpl;


    /**
     * @see org.kuali.module.gl.web.lookupable.AbstractGLLookupableTestBase#testGetSearchResults()
     */
    public void testGetSearchResults() throws Exception {
    }

    /**
     * @see org.kuali.module.gl.web.lookupable.AbstractGLLookupableTestBase#getLookupFields(boolean)
     */
    public List getLookupFields(boolean isExtended) {
        return null;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        messageFileName = "test/unit/src/org/kuali/kfs/module/ld/testdata/message.properties";
        propertiesFileName = "test/unit/src/org/kuali/kfs/module/ld/testdata/laborLedgerPendingEntry.properties";
        date = SpringContext.getBean(DateTimeService.class).getCurrentDate();
        pendingEntry = new LaborLedgerPendingEntry();
        testDataGenerator = new TestDataGenerator(propertiesFileName, messageFileName);
        lookupableHelperServiceImpl = (LaborPendingEntryLookupableHelperServiceImpl) LookupableSpringContext.getLookupableHelperService("laborPendingEntryLookupableHelperService");
        setPendingEntryService(SpringContext.getBean(LaborLedgerPendingEntryService.class));
    }


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
    protected boolean contains(List searchResults, PersistableBusinessObject businessObject) {
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
    public Map getLookupFieldValues(PersistableBusinessObject businessObject, boolean isExtended) throws Exception {
        List lookupFields = this.getLookupFields(isExtended);
        // return testDataGenerator.generateLookupFieldValues(businessObject, lookupFields);
        return null;
    }


    /**
     * Gets the pendingEntryService attribute.
     * 
     * @return Returns the pendingEntryService.
     */
    public LaborLedgerPendingEntryService getPendingEntryService() {
        return pendingEntryService;
    }

    /**
     * Sets the pendingEntryService attribute value.
     * 
     * @param pendingEntryService The pendingEntryService to set.
     */
    public void setPendingEntryService(LaborLedgerPendingEntryService pendingEntryService) {
        this.pendingEntryService = pendingEntryService;
    }
}

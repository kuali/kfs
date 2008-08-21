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
package org.kuali.kfs.module.ar.businessobject.lookup;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceWriteoffLookupResult;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.CollectionIncomplete;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.comparator.BeanPropertyComparator;

public class CustomerInvoiceWriteoffLookupResultLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    
    private CustomerInvoiceDocumentService customerInvoiceDocumentService;
    
    /**
     * @see org.kuali.core.lookup.Lookupable#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {

        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        Collection searchResultsCollection = new ArrayList();
        
        //TODO Use service to populate searchCollectionResults
        CustomerInvoiceWriteoffLookupResult customerInvoiceWriteoffLookupResult = new CustomerInvoiceWriteoffLookupResult();
        customerInvoiceWriteoffLookupResult.setCustomerName("TESTING");
        customerInvoiceWriteoffLookupResult.setCustomerNumber("ABB2");
        customerInvoiceWriteoffLookupResult.setCustomerType("BLAH");
        customerInvoiceWriteoffLookupResult.setCustomerTotal(new KualiDecimal(100));
        searchResultsCollection.add(customerInvoiceWriteoffLookupResult);
        
        FinancialSystemDocumentHeader documentHeader = new FinancialSystemDocumentHeader();
        documentHeader.setDocumentFinalDate(new Date(new java.util.Date().getTime()));
        
        CustomerInvoiceDocument customerInvoiceDocument = new CustomerInvoiceDocument();
        customerInvoiceDocument.setAge(new Integer(100));
        customerInvoiceDocument.setDocumentNumber("123456");
        customerInvoiceDocument.setDocumentHeader(documentHeader);
        List<CustomerInvoiceDocument> customerInvoiceDocuments = new ArrayList<CustomerInvoiceDocument>();
        customerInvoiceDocuments.add(customerInvoiceDocument);
        customerInvoiceWriteoffLookupResult.setCustomerInvoiceDocuments(customerInvoiceDocuments);
        
        //searchResultsCollection = customerInvoiceDocumentService.getCustomerInvoiceDocumentsByCustomerInvoiceWriteoffLookup(fieldValues);

        return this.buildSearchResultList(searchResultsCollection, new Long(searchResultsCollection.size()));
    }   
    
    /**
     * build the search result list from the given collection and the number of all qualified search results
     * 
     * @param searchResultsCollection the given search results, which may be a subset of the qualified search results
     * @param actualSize the number of all qualified search results
     * @return the serach result list with the given results and actual size
     */
    protected List buildSearchResultList(Collection searchResultsCollection, Long actualSize) {
        CollectionIncomplete results = new CollectionIncomplete(searchResultsCollection, actualSize);

        // sort list if default sort column given
        List searchResults = (List) results;
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(results, new BeanPropertyComparator(defaultSortColumns, true));
        }
        return searchResults;
    }
    
    @Override
    /**
     * TODO Figure out what to really send here...
     */
    public List getReturnKeys() {
        return new ArrayList();
    }

    public CustomerInvoiceDocumentService getCustomerInvoiceDocumentService() {
        return customerInvoiceDocumentService;
    }

    public void setCustomerInvoiceDocumentService(CustomerInvoiceDocumentService customerInvoiceDocumentService) {
        this.customerInvoiceDocumentService = customerInvoiceDocumentService;
    }

}

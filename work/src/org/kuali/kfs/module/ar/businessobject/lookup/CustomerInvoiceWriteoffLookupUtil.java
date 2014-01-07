/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceWriteoffLookupResult;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.lookup.LookupResultsService;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.ObjectUtils;

public class CustomerInvoiceWriteoffLookupUtil {

    /**
     * This helper method returns a list of customer invoice writeoff lookup result BO's based off a collection of customer invoice documents
     * @param customerInvoiceDocuments
     * @return
     */
    public static Collection<CustomerInvoiceWriteoffLookupResult> getPopulatedCustomerInvoiceWriteoffLookupResults(Collection<CustomerInvoiceDocument> customerInvoiceDocuments) {
        Collection<CustomerInvoiceWriteoffLookupResult> populatedCustomerInvoiceWriteoffLookupResults = new ArrayList<CustomerInvoiceWriteoffLookupResult>();

        if (customerInvoiceDocuments.size() == 0)
            return populatedCustomerInvoiceWriteoffLookupResults;

        Iterator iter = getCustomerInvoiceDocumentsByCustomerNumberMap(customerInvoiceDocuments).entrySet().iterator();
        CustomerInvoiceWriteoffLookupResult customerInvoiceWriteoffLookupResult = null;
        while (iter.hasNext()) {

            Map.Entry entry = (Map.Entry) iter.next();
            String customerNumber = (String) entry.getKey();
            List<CustomerInvoiceDocument> list = (List<CustomerInvoiceDocument>) entry.getValue();

            // just get data from first invoice for customer data
            if (CollectionUtils.isNotEmpty(list)) {
                Customer customer = list.get(0).getCustomer();
                customerInvoiceWriteoffLookupResult = new CustomerInvoiceWriteoffLookupResult();
                if (ObjectUtils.isNotNull(customer)) {
                    customerInvoiceWriteoffLookupResult.setCustomerName(customer.getCustomerName());
                    customerInvoiceWriteoffLookupResult.setCustomerNumber(customer.getCustomerNumber());
                    customerInvoiceWriteoffLookupResult.setCollectionStatus(ArConstants.NO_COLLECTION_STATUS_STRING);
                    customerInvoiceWriteoffLookupResult.setCustomerTotal(getCustomerTotal(list));
                }
                customerInvoiceWriteoffLookupResult.setCustomerInvoiceDocuments(list);

                populatedCustomerInvoiceWriteoffLookupResults.add(customerInvoiceWriteoffLookupResult);
            }
        }

        return populatedCustomerInvoiceWriteoffLookupResults;
    }

    public static KualiDecimal getCustomerTotal(List<CustomerInvoiceDocument> customerInvoiceDocuments) {
        KualiDecimal customerTotal = KualiDecimal.ZERO;
        for (CustomerInvoiceDocument customerInvoiceDocument : customerInvoiceDocuments) {
            customerTotal = customerTotal.add(customerInvoiceDocument.getOpenAmount());
        }
        return customerTotal;
    }

    /**
     * This helper method returns a map of a list of invoices by customer number
     * @param customerInvoiceDocuments
     * @return
     */
    public static Map<String, List<CustomerInvoiceDocument>> getCustomerInvoiceDocumentsByCustomerNumberMap(Collection<CustomerInvoiceDocument> customerInvoiceDocuments) {
        // use a map to sort invoices by customer number
        Map<String, List<CustomerInvoiceDocument>> customerInvoiceDocumentsByCustomerNumberMap = new HashMap<String, List<CustomerInvoiceDocument>>();
        for (CustomerInvoiceDocument customerInvoiceDocument : customerInvoiceDocuments) {
            String customerNumber = customerInvoiceDocument.getAccountsReceivableDocumentHeader().getCustomerNumber();
            if (customerInvoiceDocumentsByCustomerNumberMap.containsKey(customerNumber)) {
                ((List<CustomerInvoiceDocument>)customerInvoiceDocumentsByCustomerNumberMap.get(customerNumber)).add(customerInvoiceDocument);
            } else {
                List<CustomerInvoiceDocument> customerInvoiceDocumentsForCustomerNumber = new ArrayList<CustomerInvoiceDocument>();
                customerInvoiceDocumentsForCustomerNumber.add(customerInvoiceDocument);
                customerInvoiceDocumentsByCustomerNumberMap.put(customerNumber, customerInvoiceDocumentsForCustomerNumber);
            }
        }

        return customerInvoiceDocumentsByCustomerNumberMap;
    }

    public static Collection<CustomerInvoiceDocument> getCustomerInvoiceDocumentsFromLookupResultsSequenceNumber(String lookupResultsSequenceNumber, String personId) {
        Collection<CustomerInvoiceDocument> customerInvoiceDocuments = new ArrayList<CustomerInvoiceDocument>();
        try {
            for (PersistableBusinessObject obj : SpringContext.getBean(LookupResultsService.class).retrieveSelectedResultBOs(lookupResultsSequenceNumber, CustomerInvoiceDocument.class, personId)) {
                customerInvoiceDocuments.add((CustomerInvoiceDocument) obj);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return customerInvoiceDocuments;
    }

    public static Collection<CustomerInvoiceWriteoffLookupResult> getCustomerInvoiceWriteoffResutlsFromLookupResultsSequenceNumber(String lookupResultsSequenceNumber, String personId) {
        return CustomerInvoiceWriteoffLookupUtil.getPopulatedCustomerInvoiceWriteoffLookupResults(getCustomerInvoiceDocumentsFromLookupResultsSequenceNumber(lookupResultsSequenceNumber, personId));
    }

}

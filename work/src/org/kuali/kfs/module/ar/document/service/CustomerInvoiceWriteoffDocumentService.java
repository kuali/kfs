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
package org.kuali.kfs.module.ar.document.service;


import java.util.Collection;

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceWriteoffLookupResult;
import org.kuali.kfs.module.ar.document.CustomerInvoiceWriteoffDocument;
import org.kuali.rice.kew.exception.WorkflowException;

public interface CustomerInvoiceWriteoffDocumentService {
    
    /**
     * This method setups any default values for a new customer invoice document 
     * @param customerInvoiceWriteoffDocument
     */
    public void setupDefaultValuesForNewCustomerInvoiceWriteoffDocument(CustomerInvoiceWriteoffDocument customerInvoiceWriteoffDocument);
    
    /**
     * This method returns true if a customer invoice writeoff document is approved
     * @param customerInvoiceWriteoffDocumentNumber
     * @return
     */
    public boolean isCustomerInvoiceWriteoffDocumentApproved(String customerInvoiceWriteoffDocumentNumber);
    
    /**
     * This method returns a collection of customer invoice documents that are eligible for writeoff
     * @return
     */
    public Collection<CustomerInvoiceWriteoffLookupResult> getCustomerInvoiceDocumentsForInvoiceWriteoffLookup();
    
    /**
     * This method initiates customer invoice writeoff documents based on a collection of customer invoice writeoff lookup results
     * @param customerInvoiceWriteoffLookupResults
     */
    public void createCustomerInvoiceWriteoffDocuments( Collection<CustomerInvoiceWriteoffLookupResult> customerInvoiceWriteoffLookupResults ) throws WorkflowException;

}

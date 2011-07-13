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
package org.kuali.kfs.module.ar.document.service;

import java.util.Collection;

import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;

public interface CustomerCreditMemoDocumentService {
    
    /**
     * 
     * Completes the work that needs to be done once a CreditMemo is fully approved, 
     * such as generating invoicepaidapplieds, and closing the invoice if appropriate.
     * 
     * @param creditMemo
     */
    public void completeCustomerCreditMemo(CustomerCreditMemoDocument creditMemo);
    
    /**
     * This method recalculates customer credit memo document based on the user input
     * @return
     */
    public void recalculateCustomerCreditMemoDocument(CustomerCreditMemoDocument customerCreditMemoDocument, boolean blanketApproveDocumentEventFlag);

    /**
     * Gets the collection of CustomerCreditMemoDocument by customerInvoiceDocumentNumber
     * 
     * @param customerNumber
     * @return
     */
    public Collection<CustomerCreditMemoDocument> getCustomerCreditMemoDocumentByInvoiceDocument(String customerInvoiceDocumentNumber);
    
    /**
     * This method checks if there is no data to submit
     * @return
     */
    public boolean isThereNoDataToSubmit(CustomerCreditMemoDocument customerCreditMemoDocument);    

}

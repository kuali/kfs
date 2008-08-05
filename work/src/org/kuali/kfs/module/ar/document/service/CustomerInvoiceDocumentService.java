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

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.NonInvoicedDistribution;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;

public interface CustomerInvoiceDocumentService {
    
    /**
     * This method sets up default values for customer invoice document on initiation.
     * @param document
     */
    public void setupDefaultValuesForNewCustomerInvoiceDocument( CustomerInvoiceDocument document );
    
    /**
     * This method sets up default values for customer invoice document when copied.
     * @param customerInvoiceDocument
     */
    public void setupDefaultValuesForCopiedCustomerInvoiceDocument( CustomerInvoiceDocument customerInvoiceDocument );
    
    /**
     * If the customer number and address identifiers are present, display customer information
     */
    public void loadCustomerAddressesForCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument);

    /**
     * @param customerNumber
     * @return
     */
    public Collection<CustomerInvoiceDocument> getCustomerInvoiceDocumentsByCustomerNumber(String customerNumber);
    
    /**
     * @param customerInvoiceDocumentNumber
     * @return
     */
    public Collection<CustomerInvoiceDetail> getCustomerInvoiceDetailsForCustomerInvoiceDocument(String customerInvoiceDocumentNumber);
    
    /**
     * @param customerInvoiceDocument
     * @return
     */
    public Collection<CustomerInvoiceDetail> getCustomerInvoiceDetailsForCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument);
    
    /**
     * @param invoiceNumber
     * @return
     */
    public Customer getCustomerByOrganizationInvoiceNumber(String invoiceNumber);
    
    /**
     * @param organizationInvoiceNumber
     * @return
     */
    public CustomerInvoiceDocument getInvoiceByOrganizationInvoiceNumber(String organizationInvoiceNumber);

    /**
     * @param documentNumber
     * @return
     */
    public Customer getCustomerByInvoiceDocumentNumber(String documentNumber);
    
    /**
     * @param invoiceDocumentNumber
     * @return
     */
    public CustomerInvoiceDocument getInvoiceByInvoiceDocumentNumber(String invoiceDocumentNumber);
    
    /**
     * @param documentNumber
     * @return
     */
//    public Collection<InvoicePaidApplied> getInvoicePaidAppliedsForInvoice(String documentNumber);
    
    /**
     * @param documentNumber
     * @return
     */
    public Collection<NonInvoicedDistribution> getNonInvoicedDistributionsForInvoice(String documentNumber);

    /**
     * @param documentNumber
     * @return
     */
    public KualiDecimal getNonInvoicedTotalForInvoice(String documentNumber);

    /**
     * @param invoice
     * @return
     */
    public KualiDecimal getNonInvoicedTotalForInvoice(CustomerInvoiceDocument invoice);
    
    /**
     * @param documentNumber
     * @return
     */
    public KualiDecimal getPaidAppliedTotalForInvoice(String documentNumber);
    
    /**
     * @param invoice
     * @return
     */
    public KualiDecimal getPaidAppliedTotalForInvoice(CustomerInvoiceDocument invoice);
    
   /**
     * This method updates the open invoice indicator if amounts have been completely paid off
     * @param invoice
     */
    public void closeCustomerInvoiceDocument(CustomerInvoiceDocument invoice);
    
     public KualiDecimal getOpenAmountForCustomerInvoiceDocument(String customerInvoiceDocumentNumber);
}

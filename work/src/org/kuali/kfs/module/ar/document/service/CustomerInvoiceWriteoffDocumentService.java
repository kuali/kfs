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
package org.kuali.kfs.module.ar.document.service;


import java.util.Collection;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceWriteoffLookupResult;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceWriteoffDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;

public interface CustomerInvoiceWriteoffDocumentService {

    /**
     *
     * Finalizes the actions of a Writeoff document, once its been completely approved.
     *
     * Generates paid applieds for the source invoice, and closes the source invoice.
     *
     * @param writeoff The approved Writeoff document to complete.
     */
    public void completeWriteoffProcess(CustomerInvoiceWriteoffDocument writeoff);

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
     * @param fieldValues
     * @return
     */
    public Collection<CustomerInvoiceWriteoffLookupResult> getCustomerInvoiceDocumentsForInvoiceWriteoffLookup(Map<String, String> fieldValues);

    /**
     * This method filters invoices which have related CRMs and/or writeoffs in route
     * @param customerInvoiceDocuments
     * @return filteredInvoices
     */
    public Collection<CustomerInvoiceDocument> filterInvoices(Collection<CustomerInvoiceDocument> customerInvoiceDocuments);

    /**
     * This method checks if there is no another CRM in route for the invoice
     * Not in route if CRM status is one of the following: processed, cancelled, or disapproved
     * @param invoice
     * @return
     */
    public boolean checkIfThereIsNoAnotherCRMInRouteForTheInvoice(String invoiceDocumentNumber);

    /**
     * This method checks if there is no another writeoff in route for the invoice
     * Not in route if writeoff status is one of the following: processed, cancelled, or disapproved
     *
     * @param invoice
     * @return
     */
    public boolean checkIfThereIsNoAnotherWriteoffInRouteForTheInvoice(String invoiceDocumentNumber);

    /**
     *
     * Accepts a lookup result and creates a batch file dropped into the batch system for later asynchronous
     * processing.
     *
     * @param personId
     * @param customerInvoiceWriteoffLookupResults
     * @return filename and path of created batch file
     */
    public String sendCustomerInvoiceWriteoffDocumentsToBatch(Person person, Collection<CustomerInvoiceWriteoffLookupResult> customerInvoiceWriteoffLookupResults);

    /**
     *
     * Creates a new Invoice Writeoff Document based on the indicated Invoice doc number and the initiator.
     * @param initiator Person who initiated the writeoffs.
     * @param invoiceNumber Invoice document number to base the writeoff on.
     * @param note User note to be added to the document.
     * @return Returns the Document Number of the Invoice Writeoff document created.
     * @throws WorkflowException
     */
    public String createCustomerInvoiceWriteoffDocument(String invoiceNumber, String note) throws WorkflowException;

    /**
     * Gets a collection of CustomerInvoiceWriteoffDocument by invoice number
     *
     * @param invoiceNumber
     * @return
     */
    public Collection<CustomerInvoiceWriteoffDocument> getCustomerCreditMemoDocumentByInvoiceDocument(String invoiceNumber);

    public String getFinancialObjectCode(CustomerInvoiceDetail postable, CustomerInvoiceWriteoffDocument poster, boolean isUsingOrgAcctDefaultWriteoffFAU, boolean isUsingChartForWriteoff, String chartOfAccountsCode);


    public ObjectCode getObjectCode(CustomerInvoiceDetail postable, CustomerInvoiceWriteoffDocument poster, boolean isUsingOrgAcctDefaultWriteoffFAU, boolean isUsingChartForWriteoff, String chartOfAccountsCode);

}

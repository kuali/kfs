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

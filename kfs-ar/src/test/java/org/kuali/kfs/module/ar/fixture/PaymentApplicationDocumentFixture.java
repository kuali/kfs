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
package org.kuali.kfs.module.ar.fixture;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.businessobject.NonAppliedDistribution;
import org.kuali.kfs.module.ar.businessobject.NonAppliedHolding;
import org.kuali.kfs.module.ar.businessobject.NonInvoiced;
import org.kuali.kfs.module.ar.businessobject.NonInvoicedDistribution;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;

/**
 * This class...
 */
public enum PaymentApplicationDocumentFixture {

    PAYMENT_APPLICATION(
            null
    
    );
        
    private String customerNumber;
    
    private List<InvoicePaidApplied> appliedPayments;
    private List<NonInvoiced> nonInvoicedPayments;
    private Collection<NonInvoicedDistribution> nonInvoicedDistributions;
    private Collection<NonAppliedDistribution> nonAppliedDistributions;
    private NonAppliedHolding nonAppliedHolding;
    private AccountsReceivableDocumentHeader accountsReceivableDocumentHeader;
    private transient PaymentApplicationDocumentService paymentApplicationDocumentService;
    
    /**
     * 
     * Constructs a PaymentApplicationDocumentFixture.java.
     */
    private PaymentApplicationDocumentFixture(String customerNumber) {
        this.customerNumber = customerNumber;
    }
    
    /**
     * This method creates a payment application document based on the passed in fixture array
     * 
     * @param paymentFixture
     * @param customerInvoiceDetailFixtures
     * @return
     */
    public PaymentApplicationDocument createPaymentApplicationDocument(){
    
        PaymentApplicationDocument paymentApplicationDocument = null;
        try {
            paymentApplicationDocument = (PaymentApplicationDocument) DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), PaymentApplicationDocument.class);
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Document creation failed.");
        }
        
        //set AR doc Header
        AccountsReceivableDocumentHeader arDocHeader = new AccountsReceivableDocumentHeader();
        AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService = SpringContext.getBean(AccountsReceivableDocumentHeaderService.class);
        arDocHeader.setDocumentNumber(paymentApplicationDocument.getDocumentNumber());
        arDocHeader.setCustomerNumber(customerNumber);
        arDocHeader.setDocumentNumber(paymentApplicationDocument.getDocumentNumber());
        paymentApplicationDocument.setAccountsReceivableDocumentHeader(arDocHeader);
        
        return paymentApplicationDocument;
    }
}

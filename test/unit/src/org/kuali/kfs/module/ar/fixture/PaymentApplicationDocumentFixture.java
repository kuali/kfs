/*
 * Copyright 2008-2009 The Kuali Foundation
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

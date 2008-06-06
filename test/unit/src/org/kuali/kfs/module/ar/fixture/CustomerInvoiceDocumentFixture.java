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
package org.kuali.module.ar.fixture;

import org.kuali.core.service.DocumentService;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.ar.bo.AccountsReceivableDocumentHeader;
import org.kuali.module.ar.document.CustomerInvoiceDocument;
import org.kuali.module.ar.service.CustomerInvoiceDocumentService;
import org.kuali.test.DocumentTestUtils;

import edu.iu.uis.eden.exception.WorkflowException;

public enum CustomerInvoiceDocumentFixture {
    
    BASE_CIDOC_NO_CUSTOMER( null, //customerNumber 
            "BA", //processingChartOfAccountsCode
            "ACAC", //processingOrganizationCode
            null, //paymentChartOfAccountsCode
            null, //paymentAccountNumber
            null, //paymentSubAccountNumber
            null, //paymentFinancialObjectCode
            null, //paymentSubObjectCode
            null, //paymentProjectCode
            null
    ),
    
    BASE_CIDOC_WITH_CUSTOMER( "ABB2", //customerNumber 
            "BA", //processingChartOfAccountsCode
            "ACAC", //processingOrganizationCode
            null, //paymentChartOfAccountsCode
            null, //paymentAccountNumber
            null, //paymentSubAccountNumber
            null, //paymentFinancialObjectCode
            null, //paymentSubObjectCode
            null, //paymentProjectCode
            null
    ),    
    
    CIDOC_WITH_FAU_RECEIVABLE( "ABB2", //customerNumber 
            "BA", //processingChartOfAccountsCode
            "ACAC", //processingOrganizationCode
            "BA", //paymentChartOfAccountsCode
            "6044900", //paymentAccountNumber
            "ARREC", //paymentSubAccountNumber
            "1466", //paymentFinancialObjectCode
            "001", //paymentSubObjectCode
            null, //paymentProjectCode
            "FAU" //FAU
    );
    
    public String customerNumber;
    public String processingChartOfAccountsCode;
    public String processingOrganizationCode;
    public String paymentChartOfAccountsCode;
    public String paymentAccountNumber;
    public String paymentSubAccountNumber;
    public String paymentFinancialObjectCode;
    public String paymentFinancialSubObjectCode;
    public String paymentProjectCode;
    public String paymentOrganizationReferenceIdentifier;
    
    private CustomerInvoiceDocumentFixture( String customerNumber, String processingChartOfAccountsCode, String processingOrganizationCode, String paymentChartOfAccountsCode, String paymentAccountNumber, String paymentSubAccountNumber, String paymentFinancialObjectCode, String paymentFinancialSubObjectCode, String paymentProjectCode, String paymentOrganizationReferenceIdentifier ){
        this.customerNumber = customerNumber;
        this.processingOrganizationCode = processingOrganizationCode;
        this.processingChartOfAccountsCode = processingChartOfAccountsCode;
        this.paymentChartOfAccountsCode = paymentChartOfAccountsCode;
        this.paymentAccountNumber = paymentAccountNumber;
        this.paymentSubAccountNumber = paymentSubAccountNumber;
        this.paymentFinancialObjectCode = paymentFinancialObjectCode;
        this.paymentFinancialSubObjectCode = paymentFinancialSubObjectCode;
        this.paymentProjectCode = paymentProjectCode;
        this.paymentOrganizationReferenceIdentifier = paymentOrganizationReferenceIdentifier;
    }
    
    /**
     * This method creates a customer invoice document based on the passed in customer fixture and customer invoice detail fixture array
     * 
     * @param customerFixture
     * @param customerInvoiceDetailFixtures
     * @return
     */
    public CustomerInvoiceDocument createCustomerInvoiceDocument(CustomerFixture customerFixture, CustomerInvoiceDetailFixture[] customerInvoiceDetailFixtures){
    
        CustomerInvoiceDocument customerInvoiceDocument = createCustomerInvoiceDocument( customerInvoiceDetailFixtures );
        customerInvoiceDocument.getAccountsReceivableDocumentHeader().setCustomerNumber(customerFixture.customerNumber);
        return customerInvoiceDocument;
    }
    
    
    /**
     * This method creates a customer invoice document based on the passed in customer fixture and customer invoice detail fixture array
     * 
     * @param customerInvoiceDetailFixtures
     * @return
     */
    public CustomerInvoiceDocument createCustomerInvoiceDocument(CustomerInvoiceDetailFixture[] customerInvoiceDetailFixtures){
        
        CustomerInvoiceDocument customerInvoiceDocument = null;
        try {
            customerInvoiceDocument = (CustomerInvoiceDocument) DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), CustomerInvoiceDocument.class);
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Document creation failed.");
        }
        
        //probably should change this to use values set in fixture
        SpringContext.getBean(CustomerInvoiceDocumentService.class).setupDefaultValuesForNewCustomerInvoiceDocument(customerInvoiceDocument);
        
        customerInvoiceDocument.setPaymentChartOfAccountsCode(paymentChartOfAccountsCode);
        customerInvoiceDocument.setPaymentAccountNumber(paymentAccountNumber);
        customerInvoiceDocument.setPaymentSubAccountNumber(paymentSubAccountNumber);
        customerInvoiceDocument.setPaymentFinancialObjectCode(paymentFinancialObjectCode);
        customerInvoiceDocument.setPaymentFinancialSubObjectCode(paymentFinancialSubObjectCode);
        customerInvoiceDocument.setPaymentProjectCode(paymentProjectCode);
        customerInvoiceDocument.setPaymentOrganizationReferenceIdentifier(paymentOrganizationReferenceIdentifier);
        
        //set AR doc Header
        AccountsReceivableDocumentHeader arDocHeader = new AccountsReceivableDocumentHeader();
        arDocHeader.setCustomerNumber(customerNumber);
        arDocHeader.setProcessingChartOfAccountCode( processingChartOfAccountsCode );
        arDocHeader.setProcessingOrganizationCode( processingOrganizationCode );
        arDocHeader.setDocumentNumber(customerInvoiceDocument.getDocumentNumber());
        customerInvoiceDocument.setAccountsReceivableDocumentHeader(arDocHeader);
        
        //associated customer invoice detail fixtures with invoice document
        for (CustomerInvoiceDetailFixture customerInvoiceDetailFixture : customerInvoiceDetailFixtures) { 
            customerInvoiceDetailFixture.addTo(customerInvoiceDocument);
        }        
        
        return customerInvoiceDocument;
    }
}

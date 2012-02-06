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
package org.kuali.kfs.module.ar.fixture;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerAddressService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;

public enum CustomerInvoiceDocumentFixture {
    
    BASE_CIDOC_NO_CUSTOMER(null, // customerNumber
            "UA", // processingChartOfAccountsCode
            "VPIT", // processingOrganizationCode
            null, // paymentChartOfAccountsCode
            null, // paymentAccountNumber
            null, // paymentSubAccountNumber
            null, // paymentFinancialObjectCode
            null, // paymentSubObjectCode
            null, // paymentProjectCode
            null, // financialDocumentHeader 
            null, 
            null, //billByChartOfAccountsCode
            null //billedByOrganizationCode
    ),

    BASE_CIDOC_WITH_CUSTOMER("ABB2", // customerNumber
            "UA", // processingChartOfAccountsCode
            "VPIT", // processingOrganizationCode
            null, // paymentChartOfAccountsCode
            null, // paymentAccountNumber
            null, // paymentSubAccountNumber
            null, // paymentFinancialObjectCode
            null, // paymentSubObjectCode
            null, // paymentProjectCode
            null, null, null, null),
            
    BASE_CIDOC_WITH_CUSTOMER_WITH_BILLING_INFO("ABB2", // customerNumber
            "UA", // processingChartOfAccountsCode
            "VPIT", // processingOrganizationCode
            null, // paymentChartOfAccountsCode
            null, // paymentAccountNumber
            null, // paymentSubAccountNumber
            null, // paymentFinancialObjectCode
            null, // paymentSubObjectCode
            null, // paymentProjectCode
            null, // financialDocumentHeader 
            null, 
            "UA", //billByChartOfAccountsCode
            "VPIT" //billedByOrganizationCode
    ),            

    REVERSAL_CIDOC("ABB2", // customerNumber
            "UA", // processingChartOfAccountsCode
            "VPIT", // processingOrganizationCode
            null, // paymentChartOfAccountsCode
            null, // paymentAccountNumber
            null, // paymentSubAccountNumber
            null, // paymentFinancialObjectCode
            null, // paymentSubObjectCode
            null, // paymentProjectCode
            null, "123456", null, null),

    CIDOC_WITH_FAU_RECEIVABLE("ABB2", // customerNumber
            "UA", // processingChartOfAccountsCode
            "VPIT", // processingOrganizationCode
            "BA", // paymentChartOfAccountsCode
            "6044900", // paymentAccountNumber
            "ARREC", // paymentSubAccountNumber
            "1466", // paymentFinancialObjectCode
            "001", // paymentSubObjectCode
            null, // paymentProjectCode
            "FAU" // FAU
            , null, null, null);
    
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
    public String financialDocumentInErrorNumber;
    public String billByChartOfAccountsCode;
    public String billedByOrganizationCode;
    
    private CustomerInvoiceDocumentFixture( String customerNumber, String processingChartOfAccountsCode, String processingOrganizationCode, String paymentChartOfAccountsCode, String paymentAccountNumber, String paymentSubAccountNumber, String paymentFinancialObjectCode, String paymentFinancialSubObjectCode, String paymentProjectCode, String paymentOrganizationReferenceIdentifier, String financialDocumentInErrorNumber, String billByChartOfAccountsCode, String billedByOrganizationCode ){
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
        this.financialDocumentInErrorNumber = financialDocumentInErrorNumber;
        this.billByChartOfAccountsCode = billByChartOfAccountsCode;
        this.billedByOrganizationCode = billedByOrganizationCode;
    }
    
    /**
     * This method creates a customer invoice document based on the passed in customer fixture and customer invoice detail fixture array
     * 
     * @param customerFixture
     * @param customerInvoiceDetailFixtures
     * @return
     */
    public CustomerInvoiceDocument createCustomerInvoiceDocument(CustomerFixture customerFixture, CustomerInvoiceDetailFixture[] customerInvoiceDetailFixtures) throws WorkflowException {
    
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
    public CustomerInvoiceDocument createCustomerInvoiceDocument(CustomerInvoiceDetailFixture[] customerInvoiceDetailFixtures) throws WorkflowException {
        
        CustomerInvoiceDocument customerInvoiceDocument = null;
        try {
            customerInvoiceDocument = (CustomerInvoiceDocument) DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), CustomerInvoiceDocument.class);
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Document creation failed.");
        }
        
        // Just verify the workflow pieces
        DocumentHeader documentHeader = customerInvoiceDocument.getDocumentHeader();
        WorkflowDocument workflowDocument = documentHeader.getWorkflowDocument();
        
        //probably should change this to use values set in fixture
        SpringContext.getBean(CustomerInvoiceDocumentService.class).setupDefaultValuesForNewCustomerInvoiceDocument(customerInvoiceDocument);
        
        customerInvoiceDocument.setPaymentChartOfAccountsCode(paymentChartOfAccountsCode);
        customerInvoiceDocument.setPaymentAccountNumber(paymentAccountNumber);
        customerInvoiceDocument.setPaymentSubAccountNumber(paymentSubAccountNumber);
        customerInvoiceDocument.setPaymentFinancialObjectCode(paymentFinancialObjectCode);
        customerInvoiceDocument.setPaymentFinancialSubObjectCode(paymentFinancialSubObjectCode);
        customerInvoiceDocument.setPaymentProjectCode(paymentProjectCode);
        customerInvoiceDocument.setPaymentOrganizationReferenceIdentifier(paymentOrganizationReferenceIdentifier);
        customerInvoiceDocument.getFinancialSystemDocumentHeader().setFinancialDocumentInErrorNumber(financialDocumentInErrorNumber);
        if( StringUtils.isNotEmpty(billByChartOfAccountsCode)){
            customerInvoiceDocument.setBillByChartOfAccountCode(billByChartOfAccountsCode);
        }
        if( StringUtils.isNotEmpty(billedByOrganizationCode)){
            customerInvoiceDocument.setBilledByOrganizationCode(billedByOrganizationCode);
        }
        
        //  this is a little hacky, as some of these dont even have a customer attached, 
        // but these are required fields now
        //customerInvoiceDocument.setCustomerBillToAddressIdentifier(1);
        //customerInvoiceDocument.setCustomerShipToAddressIdentifier(1);

        //set AR doc Header
        AccountsReceivableDocumentHeader arDocHeader = null;
        
        if(ObjectUtils.isNull(customerInvoiceDocument.getAccountsReceivableDocumentHeader())) {
            arDocHeader = new AccountsReceivableDocumentHeader();
            customerInvoiceDocument.setAccountsReceivableDocumentHeader(arDocHeader);
        } else {
            arDocHeader = customerInvoiceDocument.getAccountsReceivableDocumentHeader();
        }
        arDocHeader.setCustomerNumber(customerNumber);
        arDocHeader.setProcessingChartOfAccountCode( processingChartOfAccountsCode );
        arDocHeader.setProcessingOrganizationCode( processingOrganizationCode );
        arDocHeader.setDocumentNumber(customerInvoiceDocument.getDocumentNumber());
        //customerInvoiceDocument.setAccountsReceivableDocumentHeader(arDocHeader);
        
        // refresh to set the Customer on the arDocHeader so setaddressoninvoice doesn't throw an NPE
        arDocHeader.refresh();
        
        CustomerAddressService customerAddressService = SpringContext.getBean(CustomerAddressService.class);
        CustomerAddress customerShipToAddress = customerAddressService.getPrimaryAddress(customerNumber);
        CustomerAddress customerBillToAddress = customerShipToAddress;
        if (ObjectUtils.isNotNull(customerShipToAddress)) {
            customerInvoiceDocument.setCustomerShipToAddress(customerShipToAddress);
            customerInvoiceDocument.setCustomerShipToAddressOnInvoice(customerShipToAddress);
            customerInvoiceDocument.setCustomerShipToAddressIdentifier(customerShipToAddress.getCustomerAddressIdentifier());
            
            customerInvoiceDocument.setCustomerBillToAddress(customerBillToAddress);
            customerInvoiceDocument.setCustomerBillToAddressOnInvoice(customerBillToAddress);
            customerInvoiceDocument.setCustomerBillToAddressIdentifier(customerBillToAddress.getCustomerAddressIdentifier());
        }
        
        CustomerInvoiceDetailService customerInvoiceDetailService = SpringContext.getBean(CustomerInvoiceDetailService.class);
        
        //associated customer invoice detail fixtures with invoice document
        if ( customerInvoiceDetailFixtures != null ){
            for (CustomerInvoiceDetailFixture customerInvoiceDetailFixture : customerInvoiceDetailFixtures) {
                CustomerInvoiceDetail detail = customerInvoiceDetailFixture.addTo(customerInvoiceDocument);
                // FIXME Set the accountsReceivableObjectCode
                String accountsReceivableObjectCode = customerInvoiceDetailService.getAccountsReceivableObjectCodeBasedOnReceivableParameter(detail);
                detail.setAccountsReceivableObjectCode(accountsReceivableObjectCode);
            }
        }
        
        //customerInvoiceDocument.refreshReferenceObject("paymentFinancialObject");
        //customerInvoiceDocument.getPaymentFinancialObject().refresh();
        
        SpringContext.getBean(DocumentService.class).saveDocument(customerInvoiceDocument);
        customerInvoiceDocument = (CustomerInvoiceDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(customerInvoiceDocument.getDocumentNumber());
        return customerInvoiceDocument;
    }
}

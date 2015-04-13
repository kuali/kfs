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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.AccountsReceivablePendingEntryService;
import org.kuali.kfs.module.ar.document.service.CustomerAddressService;
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
            null,
            null, //billByChartOfAccountsCode
            null //billedByOrganizationCode
    ),

    BASE_CIDOC_WITH_CUSTOMER("ABB2", // customerNumber
            "UA", // processingChartOfAccountsCode
            "VPIT", // processingOrganizationCode
            null, null, null),

    BASE_CIDOC_WITH_CUSTOMER_WITH_BILLING_INFO("ABB2", // customerNumber
            "UA", // processingChartOfAccountsCode
            "VPIT", // processingOrganizationCode
            null,
            "UA", //billByChartOfAccountsCode
            "VPIT" //billedByOrganizationCode
    ),

    REVERSAL_CIDOC("ABB2", // customerNumber
            "UA", // processingChartOfAccountsCode
            "VPIT", // processingOrganizationCode
            "123456", null, null);

    public String customerNumber;
    public String processingChartOfAccountsCode;
    public String processingOrganizationCode;
    public String financialDocumentInErrorNumber;
    public String billByChartOfAccountsCode;
    public String billedByOrganizationCode;

    private CustomerInvoiceDocumentFixture( String customerNumber, String processingChartOfAccountsCode, String processingOrganizationCode, String financialDocumentInErrorNumber, String billByChartOfAccountsCode, String billedByOrganizationCode ){
        this.customerNumber = customerNumber;
        this.processingOrganizationCode = processingOrganizationCode;
        this.processingChartOfAccountsCode = processingChartOfAccountsCode;
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
            customerInvoiceDocument = DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), CustomerInvoiceDocument.class);
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Document creation failed.");
        }

        // Just verify the workflow pieces
        DocumentHeader documentHeader = customerInvoiceDocument.getDocumentHeader();
        WorkflowDocument workflowDocument = documentHeader.getWorkflowDocument();

        //probably should change this to use values set in fixture
        SpringContext.getBean(CustomerInvoiceDocumentService.class).setupDefaultValuesForNewCustomerInvoiceDocument(customerInvoiceDocument);

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

        AccountsReceivablePendingEntryService accountsReceivablePendingEntryService = SpringContext.getBean(AccountsReceivablePendingEntryService.class);

        //associated customer invoice detail fixtures with invoice document
        if ( customerInvoiceDetailFixtures != null ){
            for (CustomerInvoiceDetailFixture customerInvoiceDetailFixture : customerInvoiceDetailFixtures) {
                CustomerInvoiceDetail detail = customerInvoiceDetailFixture.addTo(customerInvoiceDocument);
                // FIXME Set the accountsReceivableObjectCode
                String accountsReceivableObjectCode = accountsReceivablePendingEntryService.getAccountsReceivableObjectCode(detail);
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

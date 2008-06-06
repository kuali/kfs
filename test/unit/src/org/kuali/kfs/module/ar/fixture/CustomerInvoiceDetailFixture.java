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

import java.math.BigDecimal;
import java.sql.Date;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.ar.bo.CustomerInvoiceDetail;
import org.kuali.module.ar.document.CustomerInvoiceDocument;
import org.kuali.module.purap.bo.CreditMemoItem;

/**
 * Fixture class for customer invoice details
 */
public enum CustomerInvoiceDetailFixture {   
    
    BASE_CUSTOMER_INVOICE_DETAIL(
            null, //documentNumber
            "BL", //chartOfAccountsCode
            "1031400", //accountNumber
            "ADV",  //subAccountNumber
            "5000",  //financialObjectCode
            "SAC", //financialSubObjectCode
            "CID", //organizationRefId
            "BOB", //projectCode
            null,  //accountsReceivableObjectCode
            Date.valueOf( "2008-01-01" ), //invoiceItemServiceDate
            new BigDecimal( 1 ), // invoiceItemQuantity
            new KualiDecimal( 1 ), //invoiceItemUnitPrice
            new KualiDecimal( 1 ), //amount
            new KualiDecimal(0) //invoiceItemTaxAmount
    ),
    
    CUSTOMER_INVOICE_DETAIL_SUBFUND_RECEIVABLE(
            null, //documentNumber
            "BL", //chartOfAccountsCode
            "1031400", //accountNumber
            "ADV",  //subAccountNumber
            "5000",  //financialObjectCode
            "SAC", //financialSubObjectCode
            "CID", //organizationRefId
            "BOB", //projectCode
            "8110",  //accountsReceivableObjectCode
            Date.valueOf( "2008-01-01" ), //invoiceItemServiceDate
            new BigDecimal( 1 ), // invoiceItemQuantity
            new KualiDecimal( 1 ), //invoiceItemUnitPrice
            new KualiDecimal( 1 ), //amount
            new KualiDecimal(0) //invoiceItemTaxAmount
    ),    
    
    CUSTOMER_INVOICE_DETAIL_CHART_RECEIVABLE(
            null, //documentNumber
            "BL", //chartOfAccountsCode
            "1031400", //accountNumber
            "ADV",  //subAccountNumber
            "5000",  //financialObjectCode
            "SAC", //financialSubObjectCode
            "CID", //organizationRefId
            "BOB", //projectCode
            "8118",  //accountsReceivableObjectCode
            Date.valueOf( "2008-01-01" ), //invoiceItemServiceDate
            new BigDecimal( 1 ), // invoiceItemQuantity
            new KualiDecimal( 1 ), //invoiceItemUnitPrice
            new KualiDecimal( 1 ), //amount
            new KualiDecimal(0) //invoiceItemTaxAmount
    );    
    
    public String documentNumber;
    public String chartOfAccountsCode;
    public String accountNumber;
    public String subAccountNumber;
    public String financialObjectCode;
    public String financialSubObjectCode;
    public String projectCode;
    public String organizationReferenceId;
    public String accountsReceivableObjectCode;
    public Date invoiceItemServiceDate;
    public BigDecimal invoiceItemQuantity;
    public KualiDecimal invoiceItemUnitPrice;
    public KualiDecimal amount;
    public KualiDecimal invoiceItemTaxAmount;    
    
    /**
     * Private Constructor.
     */
    CustomerInvoiceDetailFixture(String documentNumber, String chartOfAccountsCode, String accountNumber, String subAccountNumber, String financialObjectCode, String financialSubObjectCode, String organizationReferenceId, String projectCode, String accountsReceivableObjectCode, Date invoiceItemServiceDate, BigDecimal invoiceItemQuantity, KualiDecimal invoiceItemUnitPrice, KualiDecimal amount, KualiDecimal invoiceItemTaxAmount) {
        this.documentNumber = documentNumber;
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.accountNumber = accountNumber;
        this.subAccountNumber = subAccountNumber;
        this.financialObjectCode = financialObjectCode;
        this.financialSubObjectCode = financialSubObjectCode;
        this.projectCode = projectCode;
        this.organizationReferenceId = organizationReferenceId;
        this.accountsReceivableObjectCode = accountsReceivableObjectCode;
        this.invoiceItemServiceDate = invoiceItemServiceDate;
        this.invoiceItemQuantity = invoiceItemQuantity;
        this.invoiceItemUnitPrice = invoiceItemUnitPrice;
        this.amount = amount;
        this.invoiceItemTaxAmount = invoiceItemTaxAmount;
    }
    
    /**
     * This method creates a customer invoice detail based on the information for this enum
     * @return
     */
    public CustomerInvoiceDetail createCustomerInvoiceDetail(){
        CustomerInvoiceDetail customerInvoiceDetail = new CustomerInvoiceDetail();
        customerInvoiceDetail.setDocumentNumber(documentNumber);
        customerInvoiceDetail.setChartOfAccountsCode(chartOfAccountsCode);
        customerInvoiceDetail.setAccountNumber(accountNumber);
        customerInvoiceDetail.setSubAccountNumber(subAccountNumber);
        customerInvoiceDetail.setFinancialObjectCode(financialObjectCode);
        customerInvoiceDetail.setFinancialSubObjectCode(financialSubObjectCode);
        customerInvoiceDetail.setProjectCode(projectCode);
        customerInvoiceDetail.setOrganizationReferenceId(organizationReferenceId);
        customerInvoiceDetail.setAccountsReceivableObjectCode(accountsReceivableObjectCode);
        customerInvoiceDetail.setInvoiceItemServiceDate(invoiceItemServiceDate);
        customerInvoiceDetail.setInvoiceItemUnitPrice(invoiceItemUnitPrice);
        customerInvoiceDetail.setInvoiceItemQuantity(invoiceItemQuantity);
        customerInvoiceDetail.setAmount(amount);
        customerInvoiceDetail.setInvoiceItemTaxAmount(invoiceItemTaxAmount);
        
        return customerInvoiceDetail;
    }

    /**
     * Add this customer invoice detail to the passed in document
     * 
     * @param customerInvoiceDocument
     */
    public void addTo(CustomerInvoiceDocument customerInvoiceDocument) {
        CustomerInvoiceDetail customerInvoiceDetail = this.createCustomerInvoiceDetail();
        customerInvoiceDetail.setDocumentNumber(customerInvoiceDocument.getDocumentNumber());
        customerInvoiceDocument.addSourceAccountingLine(customerInvoiceDetail);
    }
}

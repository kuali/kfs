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

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceItemCode;

public enum CustomerInvoiceItemCodeFixture {
    
    
    BASE_CIIC( "999999", //invoiceItemCode
            "BL", //chartOfAccountsCode
            "AAAM", //organizationCode
            "BL", //defaultInvoiceChartOfAccountsCode
            "1031400", //defaultInvoiceAccountNumber
            "5821" ); //defaultInvoiceFinancialObjectCode
    
    
    public String invoiceItemCode;
    public String chartOfAccountsCode;
    public String organizationCode;
    public String defaultInvoiceChartOfAccountsCode;
    public String defaultInvoiceAccountNumber;
    public String defaultInvoiceFinancialObjectCode;
    
    
    private CustomerInvoiceItemCodeFixture( String invoiceItemCode, 
            String chartOfAccountsCode, 
            String organizationCode, 
            String defaultInvoiceChartOfAccountsCode,
            String defaultInvoiceAccountNumber,
            String defaultInvoiceFinancialObjectCode) {
        
        this.invoiceItemCode = invoiceItemCode;
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.organizationCode = organizationCode;
        this.defaultInvoiceChartOfAccountsCode = defaultInvoiceChartOfAccountsCode;
        this.defaultInvoiceAccountNumber = defaultInvoiceAccountNumber;
        this.defaultInvoiceFinancialObjectCode = defaultInvoiceFinancialObjectCode;
    }
    
    public CustomerInvoiceItemCode createCustomerInvoiceItemCode( ){
        CustomerInvoiceItemCode customerInvoiceItemCode = new CustomerInvoiceItemCode();
        customerInvoiceItemCode.setActive(true);
        customerInvoiceItemCode.setInvoiceItemCode(invoiceItemCode);
        customerInvoiceItemCode.setChartOfAccountsCode(chartOfAccountsCode);
        customerInvoiceItemCode.setOrganizationCode(organizationCode);
        customerInvoiceItemCode.setDefaultInvoiceChartOfAccountsCode(defaultInvoiceChartOfAccountsCode);
        customerInvoiceItemCode.setDefaultInvoiceAccountNumber(defaultInvoiceAccountNumber);
        customerInvoiceItemCode.setDefaultInvoiceFinancialObjectCode(defaultInvoiceFinancialObjectCode);
        return customerInvoiceItemCode;
    }
}

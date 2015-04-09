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

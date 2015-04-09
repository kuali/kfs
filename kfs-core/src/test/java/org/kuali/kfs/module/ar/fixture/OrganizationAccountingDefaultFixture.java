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

import org.kuali.kfs.module.ar.businessobject.OrganizationAccountingDefault;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;

public enum OrganizationAccountingDefaultFixture {
       
    BASE_OAD(
            "BL",
            "AAAM",
            "BA",
            "1044900",
            "5387",
            SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear()
    );
    
    public String chartOfAccountsCode;
    public String organizationCode;
    public String defaultInvoiceChartOfAccountsCode;
    public String defaultInvoiceAccountNumber;
    public String defaultInvoiceFinancialObjectCode;
    public Integer universityFiscalYear;
    
    private OrganizationAccountingDefaultFixture(
            String chartOfAccountsCode,
            String organizationCode,
            String defaultInvoiceChartOfAccountsCode,
            String defaultInvoiceAccountNumber,
            String defaultInvoiceFinancialObjectCode,
            Integer universityFiscalYear){
        
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.organizationCode = organizationCode;
        this.defaultInvoiceChartOfAccountsCode = defaultInvoiceChartOfAccountsCode;
        this.defaultInvoiceAccountNumber = defaultInvoiceAccountNumber;
        this.defaultInvoiceFinancialObjectCode = defaultInvoiceFinancialObjectCode;
        this.universityFiscalYear = universityFiscalYear;
    }
    
    public OrganizationAccountingDefault createOrganizationAccountingDefault( ){
        OrganizationAccountingDefault organizationAccountingDefault = new OrganizationAccountingDefault();
        organizationAccountingDefault.setChartOfAccountsCode(chartOfAccountsCode);
        organizationAccountingDefault.setOrganizationCode(organizationCode);
        organizationAccountingDefault.setDefaultInvoiceChartOfAccountsCode(defaultInvoiceChartOfAccountsCode);
        organizationAccountingDefault.setDefaultInvoiceAccountNumber(defaultInvoiceAccountNumber);
        organizationAccountingDefault.setDefaultInvoiceFinancialObjectCode(defaultInvoiceFinancialObjectCode);
        organizationAccountingDefault.setUniversityFiscalYear(universityFiscalYear);
        
        return organizationAccountingDefault;
    }
}

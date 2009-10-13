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

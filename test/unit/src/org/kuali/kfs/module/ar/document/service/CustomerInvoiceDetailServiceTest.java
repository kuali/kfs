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
package org.kuali.kfs.module.ar.document.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceItemCode;
import org.kuali.kfs.module.ar.businessobject.OrganizationAccountingDefault;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceItemCodeFixture;
import org.kuali.kfs.module.ar.fixture.OrganizationAccountingDefaultFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.krad.service.BusinessObjectService;

@ConfigureContext(session = khuntley)
public class CustomerInvoiceDetailServiceTest extends KualiTestBase {

    /**
     * This method tests if the CustomerInvoiceDetailService uses invoice item code to default values if the invoice item code exists.
     */
    public void testGetCustomerInvoiceDetailFromCustomerInvoiceItemCode() {

        //get customer invoice item code from fixture and save it
        CustomerInvoiceItemCode customerInvoiceItemCode = CustomerInvoiceItemCodeFixture.BASE_CIIC.createCustomerInvoiceItemCode();
        SpringContext.getBean(BusinessObjectService.class).save(customerInvoiceItemCode);
           
        //check if CustomerInvoiceDetailService returns the correct customerInvoiceDetail based on the invoice item code.
        CustomerInvoiceDetailService service = SpringContext.getBean(CustomerInvoiceDetailService.class);
        CustomerInvoiceDetail customerInvoiceDetail = service.getCustomerInvoiceDetailFromCustomerInvoiceItemCode(CustomerInvoiceItemCodeFixture.BASE_CIIC.invoiceItemCode, CustomerInvoiceItemCodeFixture.BASE_CIIC.chartOfAccountsCode, CustomerInvoiceItemCodeFixture.BASE_CIIC.organizationCode);
        assertEquals( CustomerInvoiceItemCodeFixture.BASE_CIIC.defaultInvoiceChartOfAccountsCode, customerInvoiceDetail.getChartOfAccountsCode());
        assertEquals( CustomerInvoiceItemCodeFixture.BASE_CIIC.defaultInvoiceAccountNumber, customerInvoiceDetail.getAccountNumber());
        assertEquals( CustomerInvoiceItemCodeFixture.BASE_CIIC.defaultInvoiceFinancialObjectCode, customerInvoiceDetail.getFinancialObjectCode());
    }

    /**
     * This method tests if the CustomerInvoiceDetailService uses the Org Acct Default
     */
    public void testGetCustomerInvoiceDetailFromOrganizationAccountingDefault() {

        OrganizationAccountingDefault orgAcctDefault = OrganizationAccountingDefaultFixture.BASE_OAD.createOrganizationAccountingDefault();
        SpringContext.getBean(BusinessObjectService.class).save(orgAcctDefault);

        CustomerInvoiceDetailService service = SpringContext.getBean(CustomerInvoiceDetailService.class);
        CustomerInvoiceDetail customerInvoiceDetail = service.getCustomerInvoiceDetailFromOrganizationAccountingDefault(SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear(), OrganizationAccountingDefaultFixture.BASE_OAD.chartOfAccountsCode, OrganizationAccountingDefaultFixture.BASE_OAD.organizationCode);
        assertEquals(OrganizationAccountingDefaultFixture.BASE_OAD.defaultInvoiceChartOfAccountsCode, customerInvoiceDetail.getChartOfAccountsCode());
        assertEquals(OrganizationAccountingDefaultFixture.BASE_OAD.defaultInvoiceAccountNumber, customerInvoiceDetail.getAccountNumber());
        assertEquals(OrganizationAccountingDefaultFixture.BASE_OAD.defaultInvoiceFinancialObjectCode, customerInvoiceDetail.getFinancialObjectCode());
    }

}


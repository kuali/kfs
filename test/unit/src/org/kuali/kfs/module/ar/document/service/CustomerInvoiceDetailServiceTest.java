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


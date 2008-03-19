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
package org.kuali.module.ar.service;

import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.ar.bo.CustomerInvoiceDetail;
import org.kuali.module.ar.bo.CustomerInvoiceItemCode;
import org.kuali.module.ar.bo.OrganizationAccountingDefault;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.test.ConfigureContext;

@ConfigureContext(session = KHUNTLEY)
public class CustomerInvoiceDetailServiceTest extends KualiTestBase {

    private static final String CHART_CODE = "BL";
    private static final String ORGNIZATION_CODE = "AAAM";
    private static final String INVOICE_ITEM_CODE = "999999";
    
    private static final String IIC_CHART_CODE = "BL";
    private static final String IIC_ACCOUNT_NUMBER = "1031400";
    private static final String IIC_FINANCIAL_OBJECT_CODE = "5821";

    private static final String OAD_CHART_CODE = "BA";
    private static final String OAD_ACCOUNT_NUMBER = "1044900";
    private static final String OAD_FINANCIAL_OBJECT_CODE = "5387";


    /**
     * This method tests if the CustomerInvoiceDetailService uses invoice item code to default values if the invoice item code exists.
     */
    public void testGetCustomerInvoiceDetailFromCustomerInvoiceItemCode() {

        CustomerInvoiceDetailService service = SpringContext.getBean(CustomerInvoiceDetailService.class);

        CustomerInvoiceItemCode code = new CustomerInvoiceItemCode();
        code.setInvoiceItemCode(INVOICE_ITEM_CODE);
        code.setChartOfAccountsCode(CHART_CODE);
        code.setOrganizationCode(ORGNIZATION_CODE);
        code.setDefaultInvoiceChartOfAccountsCode(IIC_CHART_CODE);
        code.setDefaultInvoiceAccountNumber(IIC_ACCOUNT_NUMBER);
        code.setDefaultInvoiceFinancialObjectCode(IIC_FINANCIAL_OBJECT_CODE);

        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        boService.save(code);

        CustomerInvoiceDetail customerInvoiceDetail = service.getCustomerInvoiceDetailFromCustomerInvoiceItemCode(INVOICE_ITEM_CODE, CHART_CODE, ORGNIZATION_CODE);
        assertEquals("Customer Invoice Detail's chart code should be " + IIC_CHART_CODE + "but is actually " + customerInvoiceDetail.getChartOfAccountsCode(), IIC_CHART_CODE, customerInvoiceDetail.getChartOfAccountsCode());
        assertEquals("Customer Invoice Detail's account number should be " + IIC_ACCOUNT_NUMBER + "but is actually " + customerInvoiceDetail.getAccountNumber(), IIC_ACCOUNT_NUMBER, customerInvoiceDetail.getAccountNumber());
        assertEquals("Customer Invoice Detail's object code should be " + IIC_FINANCIAL_OBJECT_CODE + "but is actually " + customerInvoiceDetail.getFinancialObjectCode(), IIC_FINANCIAL_OBJECT_CODE, customerInvoiceDetail.getFinancialObjectCode());

    }

    /**
     * This method tests if the CustomerInvoiceDetailService uses the Org Acct Default
     */
    public void testGetCustomerInvoiceDetailFromOrganizationAccountingDefault() {

        CustomerInvoiceDetailService service = SpringContext.getBean(CustomerInvoiceDetailService.class);
        
        OrganizationAccountingDefault orgAcctDefault = new OrganizationAccountingDefault();
        orgAcctDefault.setChartOfAccountsCode(CHART_CODE);
        orgAcctDefault.setOrganizationCode(ORGNIZATION_CODE);
        orgAcctDefault.setDefaultInvoiceChartOfAccountsCode(OAD_CHART_CODE);
        orgAcctDefault.setDefaultInvoiceAccountNumber(OAD_ACCOUNT_NUMBER);
        orgAcctDefault.setDefaultInvoiceFinancialObjectCode(OAD_FINANCIAL_OBJECT_CODE);
        orgAcctDefault.setUniversityFiscalYear(SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear());

        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        boService.save(orgAcctDefault);

        CustomerInvoiceDetail customerInvoiceDetail = service.getCustomerInvoiceDetailFromOrganizationAccountingDefault(SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear(), CHART_CODE, ORGNIZATION_CODE);
        assertEquals("Customer Invoice Detail's chart code should be " + OAD_CHART_CODE + "but is actually " + customerInvoiceDetail.getChartOfAccountsCode(), OAD_CHART_CODE, customerInvoiceDetail.getChartOfAccountsCode());
        assertEquals("Customer Invoice Detail's account number should be " + OAD_ACCOUNT_NUMBER + "but is actually " + customerInvoiceDetail.getAccountNumber(), OAD_ACCOUNT_NUMBER, customerInvoiceDetail.getAccountNumber());
        assertEquals("Customer Invoice Detail's object code should be " + OAD_FINANCIAL_OBJECT_CODE + "but is actually " + customerInvoiceDetail.getFinancialObjectCode(), OAD_FINANCIAL_OBJECT_CODE, customerInvoiceDetail.getFinancialObjectCode());
    }

}

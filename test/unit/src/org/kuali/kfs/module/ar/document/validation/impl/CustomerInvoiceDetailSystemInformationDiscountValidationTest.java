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
package org.kuali.kfs.module.ar.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.krad.service.BusinessObjectService;

@ConfigureContext(session = khuntley)
public class CustomerInvoiceDetailSystemInformationDiscountValidationTest extends KualiTestBase {
    
    private CustomerInvoiceDetailSystemInformationDiscountValidation validation;
    public static final String PROCESSING_CHART_OF_ACCOUNTS = "UA";
    public static final String PROCESSING_ORGANIZATION_CODE = "ARCG";
    private static final String DISCOUNT_OBJECT_CODE = "1999";
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validation = new CustomerInvoiceDetailSystemInformationDiscountValidation();
        validation.setUniversityDateService(SpringContext.getBean(UniversityDateService.class));
        validation.setBusinessObjectService(SpringContext.getBean(BusinessObjectService.class));
        
        SystemInformation systemInformation = new SystemInformation();
        systemInformation.setProcessingChartOfAccountCode(PROCESSING_CHART_OF_ACCOUNTS);
        systemInformation.setProcessingOrganizationCode(PROCESSING_ORGANIZATION_CODE);
        systemInformation.setDiscountObjectCode(DISCOUNT_OBJECT_CODE);
        systemInformation.setUniversityFiscalYear(validation.getUniversityDateService().getCurrentFiscalYear());
        
        validation.getBusinessObjectService().save(systemInformation);
        
        CustomerInvoiceDocument customerInvoiceDocument = new CustomerInvoiceDocument();
        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = new AccountsReceivableDocumentHeader();
        accountsReceivableDocumentHeader.setProcessingChartOfAccountCode(PROCESSING_CHART_OF_ACCOUNTS);
        customerInvoiceDocument.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);
        
        
        validation.setCustomerInvoiceDocument(customerInvoiceDocument);
    }

    @Override
    protected void tearDown() throws Exception {
        validation = null;
        super.tearDown();
    }
    
    public void testSystemInformationWithDiscount_True(){
        validation.getCustomerInvoiceDocument().getAccountsReceivableDocumentHeader().setProcessingOrganizationCode(PROCESSING_ORGANIZATION_CODE);
        assertTrue(validation.validate(null));
    }
    
    public void testSystemInformationWithDiscount_False(){
        assertFalse(validation.validate(null));
    }    

}


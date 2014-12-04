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


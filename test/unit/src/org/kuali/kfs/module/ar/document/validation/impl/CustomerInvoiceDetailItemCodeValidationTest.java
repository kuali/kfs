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

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

@ConfigureContext(session = khuntley)
public class CustomerInvoiceDetailItemCodeValidationTest extends KualiTestBase {

    private final static String VALID_ITEM_CODE = "CAR"; 
    private final static String VALID_BILLING_CHART_OF_ACCOUNTS_CODE = "BA";
    private final static String VALID_BILLING_ORGANIZATION_CODE = "MOTR";    
    private final static String INVALID_ITEM_CODE = "XXXX";

    
    private CustomerInvoiceDetailItemCodeValidation validation;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validation = new CustomerInvoiceDetailItemCodeValidation();
        validation.setCustomerInvoiceDocument(new CustomerInvoiceDocument());
        validation.getCustomerInvoiceDocument().setBillByChartOfAccountCode(VALID_BILLING_CHART_OF_ACCOUNTS_CODE);
        validation.getCustomerInvoiceDocument().setBilledByOrganizationCode(VALID_BILLING_ORGANIZATION_CODE);
        validation.setBusinessObjectService(SpringContext.getBean(BusinessObjectService.class));
    }

    @Override
    protected void tearDown() throws Exception {
        validation = null;
        super.tearDown();
    }
    
    public void testValidDetailItemCode_True(){
        validation.setCustomerInvoiceDetail(new CustomerInvoiceDetail());
        validation.getCustomerInvoiceDetail().setInvoiceItemCode(VALID_ITEM_CODE);
        assertTrue(validation.validate(null));
        
    }
    
    public void testValidDetailItemCode_False(){
        validation.setCustomerInvoiceDetail(new CustomerInvoiceDetail());
        validation.getCustomerInvoiceDetail().setInvoiceItemCode(INVALID_ITEM_CODE);
        assertFalse(validation.validate(null));        
    }

}


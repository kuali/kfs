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

import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;

@ConfigureContext(session = khuntley)
public class CustomerInvoiceBilledByOrganizationCodeValidationTest extends KualiTestBase {
    
    private CustomerInvoiceBilledByOrganizationCodeValidation validation;
    
    private static final String VALID_CHART_OF_ACCOUNTS_CODE = "UA";
    private static final String INVALID_ORGANIZATION_CODE = "XXXX";
    private static final String VALID_ORGANIZATION_CODE = "VPIT";
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validation = new CustomerInvoiceBilledByOrganizationCodeValidation();
        validation.setCustomerInvoiceDocument(new CustomerInvoiceDocument());
    }

    @Override
    protected void tearDown() throws Exception {
        validation = null;
        super.tearDown();
    }
    
    /**
     * This method tests if isValidBilledByOrganizationCode rule returns true when passed a valid organization code 
     */     
    public void testIsValidBilledByOrganizationCode_True() {
        validation.getCustomerInvoiceDocument().setBillByChartOfAccountCode(VALID_CHART_OF_ACCOUNTS_CODE);
        validation.getCustomerInvoiceDocument().setBilledByOrganizationCode(VALID_ORGANIZATION_CODE);
        assertTrue(validation.validate(null));     
    }
    
    /**
     * This method tests if isValidBilledByOrganizationCode rule returns false when passed an invalid organization code 
     */    
    public void testIsValidBilledByOrganizationCode_False() {
        validation.getCustomerInvoiceDocument().setBillByChartOfAccountCode(VALID_CHART_OF_ACCOUNTS_CODE);
        validation.getCustomerInvoiceDocument().setBilledByOrganizationCode(INVALID_ORGANIZATION_CODE);
        assertFalse(validation.validate(null));          
    }

}


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
public class CustomerInvoiceBilledByChartOfAccountsCodeValidationTest extends KualiTestBase {
    
    private static final String INVALID_CHART_OF_ACCOUNTS_CODE = "XX";
    private static final String VALID_CHART_OF_ACCOUNTS_CODE = "UA";
    
    CustomerInvoiceBilledByChartOfAccountsCodeValidation validation;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validation = new CustomerInvoiceBilledByChartOfAccountsCodeValidation();
        validation.setCustomerInvoiceDocument(new CustomerInvoiceDocument());
    }

    @Override
    protected void tearDown() throws Exception {
        validation = null;
        super.tearDown();
    }
    
    /**
     * This method tests if isValidBilledByChartOfAccountsCode rule returns true when passed a valid chart of accounts code 
     */
    public void testIsValidBilledByChartOfAccountsCode_True() {
        validation.getCustomerInvoiceDocument().setBillByChartOfAccountCode(VALID_CHART_OF_ACCOUNTS_CODE);
        assertTrue(validation.validate(null));
    }
    
    /**
     * This method tests if isValidBilledByChartOfAccountsCode rule returns false when passed an invalid chart of accounts code 
     */    
    public void testIsValidBilledByChartOfAccountsCode_False() {
        validation.getCustomerInvoiceDocument().setBillByChartOfAccountCode(INVALID_CHART_OF_ACCOUNTS_CODE);
        assertFalse(validation.validate(null));     
    }  

}


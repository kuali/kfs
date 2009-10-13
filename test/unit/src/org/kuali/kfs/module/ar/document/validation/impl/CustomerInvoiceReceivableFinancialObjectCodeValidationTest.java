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
import org.kuali.kfs.sys.context.TestUtils;

@ConfigureContext(session = khuntley)
public class CustomerInvoiceReceivableFinancialObjectCodeValidationTest extends KualiTestBase {
    
    private CustomerInvoiceReceivableFinancialObjectCodeValidation validation;
    private final static String VALID_CHART_OF_ACCOUNTS = "BL";
    private final static String VALID_FINANCIAL_OBJECT_CODE = "1500";
    private final static String INVALID_FINANCIAL_OBJECT_CODE = "XXXX";
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validation = new CustomerInvoiceReceivableFinancialObjectCodeValidation();
        validation.setCustomerInvoiceDocument(new CustomerInvoiceDocument());
        validation.getCustomerInvoiceDocument().setPostingYear(TestUtils.getFiscalYearForTesting());
        validation.getCustomerInvoiceDocument().setPaymentChartOfAccountsCode(VALID_CHART_OF_ACCOUNTS);
    }

    @Override
    protected void tearDown() throws Exception {
        validation = null;
        super.tearDown();
    }
    
    public void testValidReceivableFinancialObjectCode_True(){
        validation.getCustomerInvoiceDocument().setPaymentFinancialObjectCode(VALID_FINANCIAL_OBJECT_CODE);
        assertTrue(validation.validate(null));
    }
    
    public void testValidReceivableFinancialObjectCode_False(){
        validation.getCustomerInvoiceDocument().setPaymentFinancialObjectCode(INVALID_FINANCIAL_OBJECT_CODE);
        assertFalse(validation.validate(null));
    }   

}


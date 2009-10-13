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
public class CustomerInvoiceReceivableSubAccountNumberValidationTest extends KualiTestBase {
    
    private CustomerInvoiceReceivableSubAccountNumberValidation validation;
    private final static String VALID_CHART_OF_ACCOUNTS = "BL";
    private final static String VALID_ACCOUNT_NUMBER = "1031400";
    private final static String VALID_FINANCIAL_SUB_ACCOUNT_NUMBER = "ACWT";
    private final static String INVALID_FINANCIAL_SUB_ACCOUNT_NUMBER = "XXXX";
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validation = new CustomerInvoiceReceivableSubAccountNumberValidation();
        validation.setCustomerInvoiceDocument(new CustomerInvoiceDocument());
        validation.getCustomerInvoiceDocument().setPaymentChartOfAccountsCode(VALID_CHART_OF_ACCOUNTS);
        validation.getCustomerInvoiceDocument().setPaymentAccountNumber(VALID_ACCOUNT_NUMBER);
    }

    @Override
    protected void tearDown() throws Exception {
        validation = null;
        super.tearDown();
    }
    
    public void testValidReceivableFinancialObjectCode_True(){
        validation.getCustomerInvoiceDocument().setPaymentSubAccountNumber(VALID_FINANCIAL_SUB_ACCOUNT_NUMBER);
        assertTrue(validation.validate(null));
    }
    
    public void testValidReceivableFinancialObjectCode_False(){
        validation.getCustomerInvoiceDocument().setPaymentSubAccountNumber(INVALID_FINANCIAL_SUB_ACCOUNT_NUMBER);
        assertFalse(validation.validate(null));
    }         

}


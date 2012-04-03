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
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

@ConfigureContext(session = khuntley)
public class CustomerInvoiceDetailSubFundGroupReceivableValidationTest extends KualiTestBase {

    protected CustomerInvoiceDetailSubFundGroupReceivableValidation validation;
    protected final static String VALID_CHART_OF_ACCOUNTS_CODE = "UA";
    protected final static String VALID_ACCOUNT_NUMBER = "1912810";
    protected final static String INVALID_ACCOUNT_NUMBER_WITHOUT_MATCHING_OBJECT_CODE = "1912201";
    protected final static String INVALID_ACCOUNT_NUMBER_WITH_MATCHING_OBJECT_CODE = "2312810";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validation = new CustomerInvoiceDetailSubFundGroupReceivableValidation();
        validation.setParameterService(SpringContext.getBean(ParameterService.class));
        validation.setCustomerInvoiceDetail(new CustomerInvoiceDetail());
        validation.customerInvoiceDetail.setChartOfAccountsCode(VALID_CHART_OF_ACCOUNTS_CODE);
        validation.customerInvoiceDetail.setPostingYear(TestUtils.getFiscalYearForTesting());
    }

    @Override
    protected void tearDown() throws Exception {
        validation = null;
        super.tearDown();
    }

    /**
     * This method tests if a valid account number has a corresponding sub fund group that has a corresponding receivable object code in
     * the system parameter asserts true.
     */
    public void testValidAccountNumberWithSubFundGroupParameterReceviable(){
        validation.customerInvoiceDetail.setAccountNumber(VALID_ACCOUNT_NUMBER);
        assertTrue(validation.validate(null));
    }

    /**
     * This method tests if a valid account number without a corresponding sub fund group defined in the system parameter asserts false.
     */
    public void testValidAccountNumberWithNoSubFundGroupParameter(){
        validation.customerInvoiceDetail.setAccountNumber(INVALID_ACCOUNT_NUMBER_WITHOUT_MATCHING_OBJECT_CODE);
        assertFalse(validation.validate(null));
    }


    /**
     * This method tests if a valid account number number with a corresponding sub fund group defined in the system parameter, but doesn't
     * have a valid receivable object code.
     */
    public void testValidAccountNumberWithSubFundGroupNoReceivable(){
        validation.customerInvoiceDetail.setAccountNumber(INVALID_ACCOUNT_NUMBER_WITH_MATCHING_OBJECT_CODE);
        assertFalse(validation.validate(null));
    }

}


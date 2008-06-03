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
package org.kuali.module.ar.rules;

import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

import org.kuali.core.maintenance.MaintenanceRuleTestBase;
import org.kuali.module.ar.bo.CustomerType;
import org.kuali.test.ConfigureContext;

@ConfigureContext(session = KHUNTLEY)
public class CustomerTypeRuleTest extends MaintenanceRuleTestBase {

    private CustomerType customerType;

    private static String CUSTOMER_TYPE_CODE = "XY";    
    private static String CUSTOMER_TYPE_DESC_FEDERAL = "Federal";
    private static String CUSTOMER_TYPE_DESC_MISC = "Whatever";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        customerType = new CustomerType();
    }

    @Override
    protected void tearDown() throws Exception {
        customerType = null;
        super.tearDown();
    }


    /**
     * This method tests if the validateCustomerTypeDescription rule returns true when customer type desc is set to "Whatever"
     */
    public void testValidateCustomerTypeDescription_True() {

        customerType.setCustomerTypeCode(CUSTOMER_TYPE_CODE);
        customerType.setCustomerTypeDescription(CUSTOMER_TYPE_DESC_MISC);
        CustomerTypeRule rule = (CustomerTypeRule) setupMaintDocRule(newMaintDoc(customerType), CustomerTypeRule.class);

        boolean result = rule.validateCustomerTypeDescription(customerType);
        assertEquals("When customer type desc is " + CUSTOMER_TYPE_DESC_MISC + ", validateCustomerTypeDescription should return true. ", true, result);

    }

    /**
     *  This method tests if the validateCustomerTypeDescription rule returns false when customer type desc is set to a valuse that already exists in the database
     */
    public void testValidateCustomerTypeDescription_False() {

        customerType.setCustomerTypeCode(CUSTOMER_TYPE_CODE);
        // we assume that customer type "Federal" already exists in the database as part of the bootsrtap data
        customerType.setCustomerTypeDescription(CUSTOMER_TYPE_DESC_FEDERAL);
        CustomerTypeRule rule = (CustomerTypeRule) setupMaintDocRule(newMaintDoc(customerType), CustomerTypeRule.class);

        boolean result = rule.validateCustomerTypeDescription(customerType);
        assertEquals("When customer type desc is " + CUSTOMER_TYPE_DESC_FEDERAL + ", validateCustomerTypeDescription should return false. ", false, result);

    }
}
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

import org.kuali.kfs.module.ar.businessobject.CustomerType;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.document.validation.MaintenanceRuleTestBase;

@ConfigureContext(session = khuntley)
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


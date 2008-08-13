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
package org.kuali.kfs.module.bc.document.validation.impl;

import java.math.BigDecimal;

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
/**
 * 
 * Tests salary setting rules.
 */
@ConfigureContext
public class SalarySettingRuleUtilTest extends KualiTestBase {
    
    /**
     * tests AppointmentRequestedAmountIsNonNegative with negative value
     * expected result is false
     */
    public void testAppointmentRequestedAmountIsNonNegative_negativeValue() {
        PendingBudgetConstructionAppointmentFunding appointmentFunding = new PendingBudgetConstructionAppointmentFunding();
        appointmentFunding.setAppointmentRequestedAmount(new KualiInteger(-10));
        
        assertFalse(SalarySettingRuleUtil.isValidRequestedAmount(appointmentFunding));
    }
    
    /**
     * tests AppointmentRequestedAmountIsNonNegative with non-negative value
     * expected result is true
     */
    public void testAppointmentRequestedAmountIsNonNegative_nonNegativeValue() {
        PendingBudgetConstructionAppointmentFunding appointmentFunding = new PendingBudgetConstructionAppointmentFunding();
        appointmentFunding.setAppointmentRequestedAmount(new KualiInteger(10));
        
        assertTrue(SalarySettingRuleUtil.isValidRequestedAmount(appointmentFunding));
    }
}

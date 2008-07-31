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
import org.kuali.kfs.sys.context.KualiTestBase;

public class SalarySettingRuleUtilTest extends KualiTestBase {
    
    /**
     * tests AppointmentRequestedAmountIsNotEmpty with null value
     * expected result is false
     */
    public void testAppointmentRequestedAmountIsNotEmpty_nullValue() {
        PendingBudgetConstructionAppointmentFunding appointmentFunding = new PendingBudgetConstructionAppointmentFunding();
        
        assertFalse(SalarySettingRuleUtil.appointmentRequestedAmountIsNotEmpty(appointmentFunding));
    }
    
    /**
     * tests AppointmentRequestedAmountIsNotEmpty with non-null value
     * expected result is true
     * 
     */
    public void testAppointmentRequestedAmountIsNotEmpty_nonNullValue() {
        PendingBudgetConstructionAppointmentFunding appointmentFunding = new PendingBudgetConstructionAppointmentFunding();
        appointmentFunding.setAppointmentRequestedAmount(new KualiInteger(10));
        
        assertTrue(SalarySettingRuleUtil.appointmentRequestedAmountIsNotEmpty(appointmentFunding));
    }
    
    /**
     * tests AppointmentRequestedAmountIsNonNegative with negative value
     * expected result is false
     */
    public void testAppointmentRequestedAmountIsNonNegative_negativeValue() {
        PendingBudgetConstructionAppointmentFunding appointmentFunding = new PendingBudgetConstructionAppointmentFunding();
        appointmentFunding.setAppointmentRequestedAmount(new KualiInteger(-10));
        
        assertFalse(SalarySettingRuleUtil.appointmentRequestedAmountIsNonNegative(appointmentFunding));
    }
    
    /**
     * tests AppointmentRequestedAmountIsNonNegative with non-negative value
     * expected result is true
     */
    public void testAppointmentRequestedAmountIsNonNegative_nonNegativeValue() {
        PendingBudgetConstructionAppointmentFunding appointmentFunding = new PendingBudgetConstructionAppointmentFunding();
        appointmentFunding.setAppointmentRequestedAmount(new KualiInteger(10));
        
        assertTrue(SalarySettingRuleUtil.appointmentRequestedAmountIsNonNegative(appointmentFunding));
    }

    /**
     * tests AppointmentRequestedFteQuantityIsGreaterThanZero with zero amount value
     * expected result is true
     */
    public void testAppointmentRequestedFteQuantityIsGreaterThanZero_ZeroAmountValue() {
        PendingBudgetConstructionAppointmentFunding appointmentFunding = new PendingBudgetConstructionAppointmentFunding();
        appointmentFunding.setAppointmentRequestedAmount(new KualiInteger(0));
        appointmentFunding.setAppointmentRequestedFteQuantity(new BigDecimal(10.0));
        
        assertTrue(SalarySettingRuleUtil.appointmentRequestedFteQuantityIsGreaterThanZero(appointmentFunding));
    }

    /**
     * tests AppointmentRequestedFteQuantityIsGreaterThanZero with greater than zero amount and fte
     * expected result is true
     */
    public void testAppointmentRequestedFteQuantityIsGreaterThanZero_GreaterThanZeroAmountAndFteValue() {
        PendingBudgetConstructionAppointmentFunding appointmentFunding = new PendingBudgetConstructionAppointmentFunding();
        appointmentFunding.setAppointmentRequestedAmount(new KualiInteger(1));
        appointmentFunding.setAppointmentRequestedFteQuantity(new BigDecimal(10.0));
        
        assertTrue(SalarySettingRuleUtil.appointmentRequestedFteQuantityIsGreaterThanZero(appointmentFunding));
    }

    /**
     * tests AppointmentRequestedFteQuantityIsGreaterThanZero with greater than zero amount and zero fte value
     * expected result is false
     */
    public void testAppointmentRequestedFteQuantityIsGreaterThanZero_GreaterThanZeroAmountZeroFteValue() {
        PendingBudgetConstructionAppointmentFunding appointmentFunding = new PendingBudgetConstructionAppointmentFunding();
        appointmentFunding.setAppointmentRequestedAmount(new KualiInteger(1));
        appointmentFunding.setAppointmentRequestedFteQuantity(new BigDecimal(0));
        
        assertFalse(SalarySettingRuleUtil.appointmentRequestedFteQuantityIsGreaterThanZero(appointmentFunding));
    }

    /**
     * tests AppointmentRequestedFteQuantityIsGreaterThanZero with greater than zero amount and less than zero fte value
     * expected result is false
     */
    public void testAppointmentRequestedFteQuantityIsGreaterThanZero_GreaterThanZeroAmountLessThanZeroFteValue() {
        PendingBudgetConstructionAppointmentFunding appointmentFunding = new PendingBudgetConstructionAppointmentFunding();
        appointmentFunding.setAppointmentRequestedAmount(new KualiInteger(1));
        appointmentFunding.setAppointmentRequestedFteQuantity(new BigDecimal(-10));
        
        assertFalse(SalarySettingRuleUtil.appointmentRequestedFteQuantityIsGreaterThanZero(appointmentFunding));
    }

    /**
     * tests AdjustmentAmountIsNotEmpty with null value
     * expected result is false
     */
    public void testAdjustmentAmountIsNotEmpty_nullValue() {
        PendingBudgetConstructionAppointmentFunding appointmentFunding = new PendingBudgetConstructionAppointmentFunding();
        
        assertFalse(SalarySettingRuleUtil.adjustmentAmountIsNotEmpty(appointmentFunding));
    }

    /**
     * tests AdjustmentAmountIsNotEmpty with non-null value
     * expected result is true
     */
    public void testAdjustmentAmountIsNotEmpty_nonNullValue() {
        PendingBudgetConstructionAppointmentFunding appointmentFunding = new PendingBudgetConstructionAppointmentFunding();
        appointmentFunding.setAdjustmentAmount(new KualiDecimal(10));
        
        assertTrue(SalarySettingRuleUtil.adjustmentAmountIsNotEmpty(appointmentFunding));
    }

    /**
     * tests AppointmentRequestedPayRateIsNotEmpty with null value
     * expected result is false
     */
    public void testAppointmentRequestedPayRateIsNotEmpty_emptyValue() {
        PendingBudgetConstructionAppointmentFunding appointmentFunding = new PendingBudgetConstructionAppointmentFunding();
       
        
        assertFalse(SalarySettingRuleUtil.appointmentRequestedPayRateIsNotEmpty(appointmentFunding));
    }

    /**
     * tests AppointmentRequestedPayRateIsNotEmpty with non-null value
     * expected result is true
     */
    public void testAppointmentRequestedPayRateIsNotEmpty_nonEmptyValue() {
        PendingBudgetConstructionAppointmentFunding appointmentFunding = new PendingBudgetConstructionAppointmentFunding();
        appointmentFunding.setAppointmentRequestedPayRate(new BigDecimal(10));
        
        assertTrue(SalarySettingRuleUtil.appointmentRequestedPayRateIsNotEmpty(appointmentFunding));
    }
}

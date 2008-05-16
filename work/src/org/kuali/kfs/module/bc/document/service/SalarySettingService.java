/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.budget.service;

import org.kuali.module.budget.bo.PendingBudgetConstructionAppointmentFunding;

/**
 * This class defines methods a Salary Setting Service must provide The Salary Setting Service supports functionality associated
 * with detailed salary setting for an account as well as organization based salary setting by incumbent and by position.
 */
public interface SalarySettingService {

    /**
     * This method returns the disabled setting of the System Parameter controlling Budget module Salary Setting. Disabling Salary
     * Setting will cause any UI controls related to the salary setting functionality to not be displayed. Disabling will also cause
     * associated business rules checks to behave differently or not be run.
     * 
     * @return
     */
    public boolean isSalarySettingDisabled();

    /**
     * determine whehter the given appointment funding can be vacated
     * 
     * @param appointmentFunding the given appointment funding
     * @return true if the given appointment funding can be vacated; otherwise, false
     */
    public boolean canBeVacant(PendingBudgetConstructionAppointmentFunding appointmentFunding);

    /**
     * vacate the given appointment funding and create a vacant appointment funding based on the given funding
     * 
     * @param appointmentFunding the given apporintment funding
     * @return a vacant appointment funding
     */
    public PendingBudgetConstructionAppointmentFunding vacateAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding);

    /**
     * adjust the requested salary amount of the given appointment funding by amount
     * 
     * @param appointmentFunding the given appointment funding
     */
    public void adjustRequestedSalaryByAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding);

    /**
     * adjust the requested salary amount of the given appointment funding by percent
     * 
     * @param appointmentFunding the given appointment funding
     */
    public void adjustRequestedSalaryByPercent(PendingBudgetConstructionAppointmentFunding appointmentFunding);
}

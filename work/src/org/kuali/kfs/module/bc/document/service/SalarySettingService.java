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
package org.kuali.kfs.module.bc.document.service;

import java.math.BigDecimal;
import java.util.List;

import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger;
import org.kuali.kfs.module.bc.businessobject.SalarySettingExpansion;

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
     * determine whehter the salary is paid at hourly rate
     * 
     * @param pendingBudgetConstructionGeneralLedger the given pending Budget construction general ledger entry
     * @return true if the salary is paid at hourly rate; otherwise, false
     */
    public boolean isHourlyPaid(PendingBudgetConstructionGeneralLedger pendingBudgetConstructionGeneralLedger);

    /**
     * calculate the hourly pay rate from the request amount in the given appointment funding
     * 
     * @param appointmentFunding the given apporintment funding
     * @return the hourly pay rate
     */
    public BigDecimal calculateHourlyPayRate(PendingBudgetConstructionAppointmentFunding appointmentFunding);

    /**
     * calculate the annual pay amount from the request pay rate in the given appointment funding
     * 
     * @param appointmentFunding the given apporintment funding
     * @return the annual pay amount
     */
    public KualiInteger calculateAnnualPayAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding);

    /**
     * calculate the fte quantity based on the information of the given appointment funding
     * 
     * @param appointmentFunding the given appointment funding
     * @return the fte quantity calculated from the information of the given appointment funding
     */
    public BigDecimal calculateFteQuantity(PendingBudgetConstructionAppointmentFunding appointmentFunding);

    /**
     * determine whehter the given appointment funding can be vacated
     * 
     * @param appointmentFunding the given appointment funding
     * @return true if the given appointment funding can be vacated; otherwise, false
     */
    public boolean canBeVacant(PendingBudgetConstructionAppointmentFunding appointmentFunding);

    /**
     * determine whehter the given appointment funding can be vacated
     * 
     * @param appointmentFundings the given appointment funding collection that the given appointment funding belongs to
     * @param appointmentFunding the given appointment funding
     * @return true if the given appointment funding can be vacated; otherwise, false
     */
    public boolean canBeVacant(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings, PendingBudgetConstructionAppointmentFunding appointmentFunding);

    /**
     * vacate the given appointment funding and create a vacant appointment funding based on the given funding
     * 
     * @param appointmentFunding the given apporintment funding
     * @return a vacant appointment funding
     */
    public PendingBudgetConstructionAppointmentFunding vacateAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding);

    /**
     * vacate the given appointment funding, create a vacant appointment funding based on the given funding, and add the vacant line
     * into the given appointment funding collection
     * 
     * @param appointmentFundings the given appointment funding collection that the given appointment funding belongs to
     * @param appointmentFunding the given apporintment funding
     * @return a vacant appointment funding
     */
    public PendingBudgetConstructionAppointmentFunding vacateAppointmentFunding(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings, PendingBudgetConstructionAppointmentFunding appointmentFunding);

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

    /**
     * update the given salary setting expansion with the requested amount posted from the associated funding lines
     * 
     * @param salarySettingExpansion the given salary setting expansion, a pending budget construction GL object
     */
    public void updateSalarySettingExpansion(SalarySettingExpansion salarySettingExpansion);

    /**
     * save the salary setting and its associated budget construction monthly and appointment funding
     * 
     * @param salarySettingExpansion the given salary setting expansion, a pending budget construction GL object
     */
    public void saveSalarySetting(SalarySettingExpansion salarySettingExpansion);

    /**
     * reset the given appointment funcding as deleted
     * 
     * @param appointmentFunding the given appointment funcding
     */
    public void resetAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding);

    /**
     * mark the given appointment funding as deleted
     * 
     * @param appointmentFunding the given appointment funding
     */
    public void markAsDelete(PendingBudgetConstructionAppointmentFunding appointmentFunding);

    /**
     * revert the given appointment funding if it is just vacated
     * 
     * @param appointmentFundings the given appointment funding collection
     * @param appointmentFunding the given appointment funding
     */
    public void revert(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings, PendingBudgetConstructionAppointmentFunding appointmentFunding);
}

/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.bc.document.service;

import java.math.BigDecimal;
import java.util.List;

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAppointmentFundingReasonCode;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger;
import org.kuali.kfs.module.bc.businessobject.SalarySettingExpansion;
import org.kuali.kfs.module.bc.util.SalarySettingFieldsHolder;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.kim.api.identity.Person;

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
     * determine whehter the given pending budget construction general ledger is paid at a biweekly or hourly rate
     * 
     * @param pendingBudgetConstructionGeneralLedger the given pending budget construction general ledger
     * @return true if the given given pending budget construction general ledger is paid at a biweekly or hourly rate; otherwise,
     *         false
     */
    public boolean isHourlyPaid(PendingBudgetConstructionGeneralLedger pendingBudgetConstructionGeneralLedger);

    /**
     * determine whehter the given appointment funding is paid at a biweekly or hourly rate
     * 
     * @param appointmentFunding the given appointment funding
     * @return true if the given appointment funding is paid at a biweekly or hourly rate; otherwise, false
     */
    public boolean isHourlyPaid(PendingBudgetConstructionAppointmentFunding appointmentFunding);

    /**
     * determine whehter the given object code is of a biweekly or hourly pay type
     * 
     * @param fiscalYear the given fiscal year
     * @param chartOfAccountsCode the given chart of accounts code
     * @param objectCode the given object code
     * @return true if the given object code is of a biweekly or hourly pay type; otherwise, false
     */
    public boolean isHourlyPaidObject(Integer fiscalYear, String chartOfAccountsCode, String objectCode);

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
     * normalize the hourly pay rate and annual pay amount of the given appointment funding
     * 
     * @param appointmentFunding the given appointment funding
     */
    public void normalizePayRateAndAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding);

    /**
     * calculate the fte quantity based on the information of the given appointment funding
     * 
     * @param appointmentFunding the given appointment funding
     * @return the fte quantity calculated from the information of the given appointment funding
     */
    public BigDecimal calculateFteQuantityFromAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding);

    /**
     * calculate the FTE quantity through the given information
     * 
     * @param payMonth the given number of pay months
     * @param fundingMonth the given number of funding months
     * @param requestedTimePercent the requested FTE time percent
     * @return the FTE quantity calculated from the given information
     */
    public BigDecimal calculateFteQuantity(Integer payMonth, Integer fundingMonth, BigDecimal requestedTimePercent);

    /**
     * calculate the CSF fte quantity based on the information of the given appointment funding
     * 
     * @param appointmentFunding
     * @return the CSF fte quantity calculated from the information of the given appointment funding
     */
    public BigDecimal calculateCSFFteQuantityFromAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding);

    /**
     * calculate the CSF FTE quantity through the given information
     * 
     * @param payMonth the given number of pay months
     * @param normalWorkMonth the given number of normal work months
     * @param requestedCSFTimePercent the requested CSF time percent
     * @return the CSF FTE quantity from the given information
     */
    public BigDecimal calculateCSFFteQuantity(Integer payMonth, Integer normalWorkMonth, BigDecimal requestedCSFTimePercent);

    /**
     * determine whether the given appointment funding can be vacated
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
     * permanently delete the given appointment funding lines being purged
     * 
     * @param purgedAppointmentFundings the given appointment funding collection being purged
     */
    public void purgeAppointmentFundings(List<PendingBudgetConstructionAppointmentFunding> purgedAppointmentFundings);

    /**
     * find the appointment funding from the given appointment funding collection, which has the same key information as the
     * specified vacant appointment funding
     * 
     * @param appointmentFundings the given appointment funding collection
     * @param vacantAppointmentFunding the given vacant apporintment funding
     * @return the appointment funding from the given appointment funding collection, which has the same key information as the
     *         specified vacant appointment funding
     */
    public PendingBudgetConstructionAppointmentFunding findVacantAppointmentFunding(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings, PendingBudgetConstructionAppointmentFunding vacantAppointmentFunding);

    /**
     * find the appointment funding from the given appointment funding collection, which has the same key information as the
     * specified appointment funding
     * 
     * @param appointmentFundings the given appointment funding collection
     * @param vacantAppointmentFunding the given apporintment funding
     * @return the appointment funding from the given appointment funding collection, which has the same key information as the
     *         specified appointment funding
     */
    public PendingBudgetConstructionAppointmentFunding findAppointmentFunding(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings, PendingBudgetConstructionAppointmentFunding appointmentFunding);

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
     * save the salary setting and its associated appointment funding
     * 
     * @param salarySettingExpansion the given salary setting expansion, a pending budget construction GL object
     */
    public void saveSalarySetting(SalarySettingExpansion salarySettingExpansion);

    /**
     * save the pending budget general ledger rows associated with a given salary setting expansion
     * this also handles updating the special 2PLG row
     * 
     * @param salarySettingExpansion
     */
    public void savePBGLSalarySetting(SalarySettingExpansion salarySettingExpansion);

    /**
     * save the given appointment fundings and associated salary setting expansion,
     * also known as, pending budget general ledger row
     * 
     * @param appointmentFundings
     * @param isSalarySettingByIncumbent
     */
    public void saveSalarySetting(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings, Boolean isSalarySettingByIncumbent);

    /**
     * save the given appointment fundings
     * 
     * @param appointmentFundings the given appointment funding collection
     */
    public void saveAppointmentFundings(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings);

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

    /**
     * retrive the salary setting expension from the information provided by the given appointment funding
     * 
     * @param appointmentFunding the given appointment funding
     * @return the salary setting expension with the information provided by the given appointment funding
     */
    public SalarySettingExpansion retriveSalarySalarySettingExpansion(PendingBudgetConstructionAppointmentFunding appointmentFunding);

    /**
     * retrieve a list of PendingBudgetConstructionAppointmentFunding from the information provided by
     * the given SalarySettingExpansion
     * 
     * @param salarySettingExpansion
     * @return the list of PendingBudgetConstructionAppointmentFunding
     */
    public List<PendingBudgetConstructionAppointmentFunding> retrievePendingBudgetConstructionAppointmentFundings(SalarySettingExpansion salarySettingExpansion);
    
    /**
     * update the access flags of the given appointment funding according to the given information
     * 
     * @param appointmentFunding the given appointment funding
     * @param salarySettingFieldsHolder the field holder that contains the values passed from the user
     * @param budgetByObjectMode the budget by object mode flag
     * @param hasDocumentEditAccess indicates whether the user has edit permission for the budget document (for budget by object)
     * @param person the specified user
     * @return true if the access flags are updated successfully; otherwise, false
     */
    public boolean updateAccessOfAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding, SalarySettingFieldsHolder salarySettingFieldsHolder, boolean budgetByObjectMode, boolean hasDocumentEditAccess, Person person);

    /**
     * update the access flags of the given appointment funding according to the user level and document organization level
     * 
     * @param appointmentFunding the given appointment funding
     * @param person the specified user
     * @return true if the access flags are updated successfully; otherwsie, false
     */
    public boolean updateAccessOfAppointmentFundingByUserLevel(PendingBudgetConstructionAppointmentFunding appointmentFunding, Person person);

    /**
     * update the fields before saving the given appointment fundings
     * 
     * @param appointmentFundings the given collection of appointment fundings
     */
    public void updateAppointmentFundingsBeforeSaving(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings);

    /**
     * update the fields with the values that can be derived from the existing information, for example, hourly rate and FTE
     * 
     * @param appointmentFundings the given appointment funding
     */
    public void recalculateDerivedInformation(PendingBudgetConstructionAppointmentFunding appointmentFunding);

    /**
     * checks if a reason code has existing appointment funding reasons
     * 
     * @param budgetConstructionAppointmentFundingReasonCode
     * @return
     */
    public boolean hasExistingFundingReason(BudgetConstructionAppointmentFundingReasonCode budgetConstructionAppointmentFundingReasonCode);
}

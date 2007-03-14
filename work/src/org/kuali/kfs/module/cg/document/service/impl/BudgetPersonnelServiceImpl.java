/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.kra.budget.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.kra.KraConstants;
import org.kuali.module.kra.budget.bo.AppointmentType;
import org.kuali.module.kra.budget.bo.AppointmentTypeEffectiveDate;
import org.kuali.module.kra.budget.bo.Budget;
import org.kuali.module.kra.budget.bo.BudgetFringeRate;
import org.kuali.module.kra.budget.bo.BudgetPeriod;
import org.kuali.module.kra.budget.bo.BudgetTask;
import org.kuali.module.kra.budget.bo.BudgetUser;
import org.kuali.module.kra.budget.bo.UserAppointmentTask;
import org.kuali.module.kra.budget.bo.UserAppointmentTaskPeriod;
import org.kuali.module.kra.budget.document.BudgetDocument;
import org.kuali.module.kra.budget.service.BudgetFringeRateService;
import org.kuali.module.kra.budget.service.BudgetPersonnelService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class BudgetPersonnelServiceImpl implements BudgetPersonnelService {

    private static HashMap appointmentTypeMappings;

    private BusinessObjectService businessObjectService;
    private BudgetFringeRateService budgetFringeRateService;
    private KualiConfigurationService kualiConfigurationService;

    /**
     * This method will create all the necessary task/period combinations for a given BudgetUser
     * 
     * @parm budgetUser - The user that we are creating entries for
     * @parm budgetDocument - Stores information used to create the appropriate number of tasks/period
     */
    public void createPersonnelDetail(BudgetUser budgetUser, BudgetDocument budgetDocument) {
        BudgetFringeRate budgetFringeRate = budgetFringeRateService.getBudgetFringeRateForPerson(budgetUser);
        budgetUser.setAppointmentTypeCode(budgetFringeRate.getInstitutionAppointmentTypeCode());

        BudgetFringeRate secondaryBudgetFringeRate = null;
        if (budgetFringeRate.getAppointmentType().getRelatedAppointmentTypeCode() != null) {
            secondaryBudgetFringeRate = budgetFringeRateService.getBudgetFringeRate(budgetDocument.getDocumentNumber(), budgetFringeRate.getAppointmentType().getRelatedAppointmentTypeCode());
            budgetUser.setSecondaryAppointmentTypeCode(secondaryBudgetFringeRate.getInstitutionAppointmentTypeCode());
        }

        
        for (BudgetTask task :  budgetDocument.getBudget().getTasks()) {
            budgetUser.getUserAppointmentTasks().add(createUserAppointmentTask(budgetUser, budgetDocument, budgetFringeRate, task, budgetDocument.getBudget().getPeriods(), false));
            if (secondaryBudgetFringeRate != null) {
                budgetUser.getUserAppointmentTasks().add(createUserAppointmentTask(budgetUser, budgetDocument, secondaryBudgetFringeRate, task, budgetDocument.getBudget().getPeriods(), true));
            }
        }
        
        Collections.sort(budgetUser.getUserAppointmentTasks());
    }

    private UserAppointmentTask createUserAppointmentTask(BudgetUser budgetUser, BudgetDocument budgetDocument, BudgetFringeRate budgetFringeRate, BudgetTask task, List<BudgetPeriod> periods, boolean isSecondaryAppointment) {
        Integer budgetTaskSequenceNumber = task.getBudgetTaskSequenceNumber();

        UserAppointmentTask userAppointmentTask = new UserAppointmentTask();
        userAppointmentTask.setBudgetTaskSequenceNumber(budgetTaskSequenceNumber);
        userAppointmentTask.setBudgetUserSequenceNumber(budgetUser.getBudgetUserSequenceNumber());
        userAppointmentTask.setDocumentNumber(budgetUser.getDocumentNumber());
        userAppointmentTask.setInstitutionAppointmentTypeCode(budgetFringeRate.getInstitutionAppointmentTypeCode());
        userAppointmentTask.setBudgetFringeRate(budgetFringeRate);
        userAppointmentTask.setSecondaryAppointment(isSecondaryAppointment);

        userAppointmentTask.setTask(task);

        for (BudgetPeriod period : periods) {

            userAppointmentTask.getUserAppointmentTaskPeriods().add(createUserAppointmentTaskPeriod(budgetUser, budgetDocument, budgetFringeRate, task, period));
        }

        return userAppointmentTask;
    }

    private UserAppointmentTaskPeriod createUserAppointmentTaskPeriod(BudgetUser budgetUser, BudgetDocument budgetDocument, BudgetFringeRate budgetFringeRate, BudgetTask task, BudgetPeriod period) {
        Integer budgetPeriodSequenceNumber = period.getBudgetPeriodSequenceNumber();

        UserAppointmentTaskPeriod userAppointmentTaskPeriod;

        userAppointmentTaskPeriod = new UserAppointmentTaskPeriod();
        userAppointmentTaskPeriod.setDocumentNumber(budgetUser.getDocumentNumber());
        userAppointmentTaskPeriod.setBudgetUserSequenceNumber(budgetUser.getBudgetUserSequenceNumber());
        userAppointmentTaskPeriod.setBudgetTaskSequenceNumber(task.getBudgetTaskSequenceNumber());
        userAppointmentTaskPeriod.setBudgetPeriodSequenceNumber(budgetPeriodSequenceNumber);
        userAppointmentTaskPeriod.setInstitutionAppointmentTypeCode(budgetFringeRate.getInstitutionAppointmentTypeCode());
        userAppointmentTaskPeriod.setBudgetFringeRate(budgetFringeRate);

        if (StringUtils.contains(getAppointmentTypeMappings().get(KraConstants.ACADEMIC_YEAR_SUMMER).toString(), budgetFringeRate.getInstitutionAppointmentTypeCode()) || StringUtils.contains(getAppointmentTypeMappings().get(KraConstants.FULL_YEAR).toString(), budgetFringeRate.getInstitutionAppointmentTypeCode())) {

            PeriodSalary periodSalary = calculatePeriodSalaryAndDays(budgetUser, budgetFringeRate, period, budgetDocument.getBudget().getBudgetPersonnelInflationRate());

            userAppointmentTaskPeriod.setPeriodSalary(periodSalary);

            userAppointmentTaskPeriod.setUserBudgetPeriodSalaryAmount(periodSalary.getPeriodSalary());
        }

        userAppointmentTaskPeriod.setTask(task);
        userAppointmentTaskPeriod.setPeriod(period);

        return userAppointmentTaskPeriod;
    }

    /**
     * This method will calculate the salary amount for a person for an individual period. The algorithm that this uses takes into
     * account what the current Fiscal Year is, the FY for the period that we are trying to calculate, the number of FYs that the
     * period spans, the number of days in each FY and the inflated salary for the FY
     * 
     * @parm budgetUser - The user for whom we are calculating the period salary
     * @parm period - The period that we are calculation salary for
     * @inflationRate - The inflation rate to use when calculating the person's salary
     */

    public KualiInteger calculatePeriodSalary(BudgetUser budgetUser, BudgetFringeRate budgetFringeRate, BudgetPeriod period, KualiDecimal inflationRate) {
        return calculatePeriodSalaryAndDays(budgetUser, budgetFringeRate, period, inflationRate).periodSalary;
    }

    private PeriodSalary calculatePeriodSalaryAndDays(BudgetUser budgetUser, BudgetFringeRate budgetFringeRate, BudgetPeriod period, KualiDecimal inflationRate) {

        boolean calculateCalendarYear = StringUtils.equals(budgetFringeRate.getInstitutionAppointmentTypeCode(), getAppointmentTypeMappings().get(KraConstants.ACADEMIC_SUMMER).toString());

        Integer periodStartDateEvaluationYear = SpringServiceLocator.getUniversityDateService().getFiscalYear(period.getBudgetPeriodBeginDate());
        Integer periodEndDateEvaluationYear = SpringServiceLocator.getUniversityDateService().getFiscalYear(period.getBudgetPeriodEndDate());

        if (calculateCalendarYear) { // currently only used for Academic Summer appointments
            Calendar startDateCalendar = Calendar.getInstance();
            startDateCalendar.setTime(period.getBudgetPeriodBeginDate());
            periodStartDateEvaluationYear = new Integer(startDateCalendar.get(Calendar.YEAR));

            Calendar endDateCalendar = Calendar.getInstance();
            endDateCalendar.setTime(period.getBudgetPeriodEndDate());
            periodEndDateEvaluationYear = new Integer(endDateCalendar.get(Calendar.YEAR));
        }

        if (periodStartDateEvaluationYear.compareTo(periodEndDateEvaluationYear) != 0) {
            // Start date and end date are in different FYs
            // we know that the start date is before the end date b/c of validations, so if they are not equal the end date must be
            // in the next fiscal year

            budgetFringeRate.refreshReferenceObject("appointmentType");

            Date subPeriodOneEndDate = getAppointmentTypeEffectiveEndDate(budgetFringeRate.getAppointmentType(), periodStartDateEvaluationYear);
            Date subPeriodTwoStartDate = getAppointmentTypeEffectiveStartDate(budgetFringeRate.getAppointmentType(), periodEndDateEvaluationYear);

            BudgetPeriod subPeriodOne = new BudgetPeriod();
            subPeriodOne.setBudgetPeriodBeginDate(period.getBudgetPeriodBeginDate());
            subPeriodOne.setBudgetPeriodEndDate(new java.sql.Date(subPeriodOneEndDate.getTime()));
            PeriodSalary subPeriodOneSalary = calculatePeriodSalaryAndDays(budgetUser, budgetFringeRate, subPeriodOne, inflationRate);

            BudgetPeriod subPeriodTwo = new BudgetPeriod();
            subPeriodTwo.setBudgetPeriodBeginDate(new java.sql.Date(subPeriodTwoStartDate.getTime()));
            subPeriodTwo.setBudgetPeriodEndDate(period.getBudgetPeriodEndDate());
            PeriodSalary subPeriodTwoSalary = calculatePeriodSalaryAndDays(budgetUser, budgetFringeRate, subPeriodTwo, inflationRate);

            return subPeriodOneSalary.add(subPeriodTwoSalary);
        }
        else {
            // Start date and end date are in the same FY
            Integer currentFiscalYear = periodStartDateEvaluationYear;

            double inflationFactor = inflationRate.doubleValue() / 100 + 1;

            int compareToYear = SpringServiceLocator.getUniversityDateService().getCurrentFiscalYear().intValue();

            if (budgetUser.getBudgetSalaryFiscalYear() !=  null) {
                compareToYear = budgetUser.getBudgetSalaryFiscalYear().intValue();
            } else {
                budgetUser.setBudgetSalaryFiscalYear(new Integer(compareToYear));
            }


            BigDecimal inflatedSalary = new BigDecimal((budgetUser.getBaseSalary() != null ? budgetUser.getBaseSalary().doubleValue() : 0) * Math.pow(inflationFactor, periodEndDateEvaluationYear.intValue() - compareToYear));
            // Step 1 - divide the annual salary by the number of days in the fiscal year (taking into account leap year) to
            // determine the daily salary
            KualiInteger fullSalaryForAppointmentTypeDuration = new KualiInteger(inflatedSalary);

            Date evalStartDate = getAppointmentTypeEffectiveStartDate(budgetFringeRate.getAppointmentType(), currentFiscalYear);
            Date evalEndDate = getAppointmentTypeEffectiveEndDate(budgetFringeRate.getAppointmentType(), currentFiscalYear);

            Date dailySalaryStartDate = evalStartDate;
            Date dailySalaryEndDate = evalEndDate;

            if (calculateCalendarYear) {
                AppointmentType surrogateAppointmentType = new AppointmentType();
                surrogateAppointmentType.setAppointmentTypeCode(getAppointmentTypeMappings().get(KraConstants.ACADEMIC_YEAR).toString());
                dailySalaryStartDate = getAppointmentTypeEffectiveStartDate(surrogateAppointmentType, currentFiscalYear);
                dailySalaryEndDate = getAppointmentTypeEffectiveEndDate(surrogateAppointmentType, currentFiscalYear);
            }


            BigDecimal dailySalaryForAppointmentTypeDuration = fullSalaryForAppointmentTypeDuration.divide(new BigDecimal(SpringServiceLocator.getDateTimeService().dateDiff(dailySalaryStartDate, dailySalaryEndDate, true)));

            // Step 2 - find out the number of days in the period
            Date workStartDate = (period.getBudgetPeriodBeginDate().before(evalStartDate) ? evalStartDate : period.getBudgetPeriodBeginDate());
            Date workEndDate = (period.getBudgetPeriodEndDate().after(evalEndDate) ? evalEndDate : period.getBudgetPeriodEndDate());

            //method can return a negative - this may occur in cases when we are modifying the dates to calcualte based on.  For example, summer appointments in periods that 
            int dateDiff = SpringServiceLocator.getDateTimeService().dateDiff(workStartDate, workEndDate, true);
            int daysInPeriod = dateDiff > 0 ? dateDiff : 0;

            // Step 3 - multiply the number of days in the period (Step 2) by the daily salary (Step 1)
            KualiInteger periodSalary = new KualiInteger(daysInPeriod).multiply(dailySalaryForAppointmentTypeDuration);

            return new PeriodSalary(periodSalary, daysInPeriod);
        }
    }

    /**
     * This method will calculate the period salary for an individual for each task and period that exists on the budget.
     * 
     * @parm budgetUser - The budgetUser for whom we care calculating salary amounts
     * @parm budgetDocument - The budgetDocument stores values used to make calculations, like periods, period length, and inflation
     *       rates.
     */
    public void calculatePersonSalary(BudgetUser budgetUser, BudgetDocument budgetDocument) {

        for (UserAppointmentTask userAppointmentTask : budgetUser.getUserAppointmentTasks()) {

            for (UserAppointmentTaskPeriod userAppointmentTaskPeriod : userAppointmentTask.getUserAppointmentTaskPeriods()) {
                BudgetPeriod budgetPeriod = new BudgetPeriod();
                budgetPeriod.setDocumentNumber(userAppointmentTaskPeriod.getDocumentNumber());
                budgetPeriod.setBudgetPeriodSequenceNumber(userAppointmentTaskPeriod.getBudgetPeriodSequenceNumber());

                BudgetPeriod period = (BudgetPeriod) (ObjectUtils.retrieveObjectWithIdentitcalKey(budgetDocument.getBudget().getPeriods(), budgetPeriod));
                userAppointmentTaskPeriod.setPeriod(period);

                if ((StringUtils.contains(getAppointmentTypeMappings().get(KraConstants.ACADEMIC_YEAR_SUMMER).toString(), userAppointmentTask.getInstitutionAppointmentTypeCode()) || StringUtils.contains(getAppointmentTypeMappings().get(KraConstants.FULL_YEAR).toString(), userAppointmentTask.getInstitutionAppointmentTypeCode()))) {

                    // the following should ensure that we have up to date fringe rates in the case of modification from Parameters
                    BudgetFringeRate budgetFringeRate = new BudgetFringeRate();
                    budgetFringeRate.setDocumentNumber(userAppointmentTaskPeriod.getDocumentNumber());
                    budgetFringeRate.setInstitutionAppointmentTypeCode(userAppointmentTaskPeriod.getInstitutionAppointmentTypeCode());

                    budgetFringeRate = (BudgetFringeRate) (ObjectUtils.retrieveObjectWithIdentitcalKey(budgetDocument.getBudget().getFringeRates(), budgetFringeRate));
                    if (budgetFringeRate != null) {
                        userAppointmentTaskPeriod.setBudgetFringeRate(budgetFringeRate);

                        PeriodSalary periodSalary = calculatePeriodSalaryAndDays(budgetUser, userAppointmentTaskPeriod.getBudgetFringeRate(), userAppointmentTaskPeriod.getPeriod(), budgetDocument.getBudget().getBudgetPersonnelInflationRate());
    
                        userAppointmentTaskPeriod.setPeriodSalary(periodSalary);
    
                        userAppointmentTaskPeriod.setUserBudgetPeriodSalaryAmount(periodSalary.getPeriodSalary());
                    }
                }
            }
        }
    }

    /**
     * This method will recalculate the agency and institution/institution cost share amounts based on the period salary/hourly
     * rate/number of hours/ fringe rates/etc... for every personnel entry
     * 
     * @parm BudgetUser The user for whom request amounts are being calculated. Note that changing the appointment type class (for
     *       example, changing from a full year appointment to an hourly appointment) does not clear out the existing values. This
     *       is so we can switch back and forth and keep the appropriate values.
     */
    public void calculatePersonRequestAmounts(BudgetUser budgetUser, List budgetFringeRates) {
        BudgetFringeRate budgetFringeRate;

        for (UserAppointmentTask userAppointmentTask : budgetUser.getUserAppointmentTasks()) {
           userAppointmentTask.refreshReferenceObject("budgetFringeRate");
            
            BudgetFringeRate budgetFringeRateFromList = (BudgetFringeRate) (ObjectUtils.retrieveObjectWithIdentitcalKey(budgetFringeRates, userAppointmentTask.getBudgetFringeRate()));
            if (budgetFringeRateFromList != null) {
                budgetFringeRate = budgetFringeRateFromList;
            }
            else {
                budgetFringeRate = budgetFringeRateService.getBudgetFringeRate(userAppointmentTask.getDocumentNumber(), userAppointmentTask.getInstitutionAppointmentTypeCode());
            }

            // Zero out the totals
            userAppointmentTask.setAgencyRequestTotalAmountTask(new KualiInteger(0));
            userAppointmentTask.setAgencyFringeBenefitTotalAmountTask(new KualiInteger(0));
            userAppointmentTask.setInstitutionCostShareRequestTotalAmountTask(new KualiInteger(0));
            userAppointmentTask.setInstitutionCostShareFringeBenefitTotalAmountTask(new KualiInteger(0));
            userAppointmentTask.setGradAsstAgencyHealthInsuranceTotal(new KualiInteger(0));
            userAppointmentTask.setGradAsstAgencySalaryTotal(new KualiInteger(0));
            userAppointmentTask.setGradAsstInstHealthInsuranceTotal(new KualiInteger(0));
            userAppointmentTask.setGradAsstInstSalaryTotal(new KualiInteger(0));

            for (UserAppointmentTaskPeriod userAppointmentTaskPeriod : userAppointmentTask.getUserAppointmentTaskPeriods()) {
                userAppointmentTaskPeriod.setBudgetFringeRate(userAppointmentTask.getBudgetFringeRate());
                
                userAppointmentTaskPeriod.setTotalFeeRemissionsAmount(new KualiInteger(0));
                userAppointmentTaskPeriod.setTotalFteAmount(new KualiInteger(0));
                userAppointmentTaskPeriod.setTotalHealthInsuranceAmount(new KualiInteger(0));
                userAppointmentTaskPeriod.setTotalGradAsstSalaryAmount(new KualiInteger(0));
                userAppointmentTaskPeriod.setTotalPercentEffort(new KualiInteger(0));
                userAppointmentTaskPeriod.setTotalSalaryAmount(new KualiInteger(0));
                userAppointmentTaskPeriod.setTotalFringeAmount(new KualiInteger(0));
                

                BigDecimal costShareFringeRateDecimalMultiplier = budgetFringeRate.getInstitutionCostShareFringeRateAmount().bigDecimalValue().divide(BigDecimal.valueOf(100), 8, BigDecimal.ROUND_HALF_EVEN);
                BigDecimal agnecyFringeRateDecimalMultiplier = budgetFringeRate.getContractsAndGrantsFringeRateAmount().bigDecimalValue().divide(BigDecimal.valueOf(100), 8, BigDecimal.ROUND_HALF_EVEN);

                if (StringUtils.equals(getAppointmentTypeMappings().get(KraConstants.ACADEMIC_SUMMER).toString(), userAppointmentTask.getInstitutionAppointmentTypeCode())) {
                    PeriodSalary periodSalary = userAppointmentTaskPeriod.getPeriodSalary() != null ? userAppointmentTaskPeriod.getPeriodSalary() : new PeriodSalary(new KualiInteger(0), 0);
                    float weeks = periodSalary.getWorkDaysInPeriod() / 7;

                    float workWeeks = userAppointmentTaskPeriod.getPersonWeeksAmount() != null ? userAppointmentTaskPeriod.getPersonWeeksAmount().intValue() : 0;

                    if (workWeeks > weeks)
                        workWeeks = weeks;

                    BigDecimal workWeeksPercent = weeks > 0 && workWeeks > 0 ? new BigDecimal(workWeeks / weeks) : new BigDecimal(0);

                    BigDecimal userBudgetPeriodSalaryAmount = userAppointmentTaskPeriod.getUserBudgetPeriodSalaryAmount().bigDecimalValue().multiply(workWeeksPercent).setScale(0, BigDecimal.ROUND_HALF_EVEN);

                    userAppointmentTaskPeriod.setUserBudgetPeriodSalaryAmount(new KualiInteger(userBudgetPeriodSalaryAmount));
                    
                    userAppointmentTaskPeriod.setTotalPercentEffort(userAppointmentTaskPeriod.getAgencyPercentEffortAmount().add(userAppointmentTaskPeriod.getInstitutionCostSharePercentEffortAmount()));
                    userAppointmentTaskPeriod.setTotalSalaryAmount(userAppointmentTaskPeriod.getAgencyRequestTotalAmount().add(userAppointmentTaskPeriod.getInstitutionCostShareRequestTotalAmount()));
                    userAppointmentTaskPeriod.setTotalFringeAmount(userAppointmentTaskPeriod.getAgencyFringeBenefitTotalAmount().add(userAppointmentTaskPeriod.getInstitutionCostShareFringeBenefitTotalAmount()));
                }

                if (StringUtils.contains(getAppointmentTypeMappings().get(KraConstants.FULL_YEAR).toString(), userAppointmentTask.getInstitutionAppointmentTypeCode()) || StringUtils.contains(getAppointmentTypeMappings().get(KraConstants.ACADEMIC_YEAR_SUMMER).toString(), userAppointmentTask.getInstitutionAppointmentTypeCode())) {

                    KualiInteger agencyRequestSalary = userAppointmentTaskPeriod.getUserBudgetPeriodSalaryAmount().multiply(userAppointmentTaskPeriod.getAgencyPercentEffortAmount().bigDecimalValue().divide(BigDecimal.valueOf(100), 8, BigDecimal.ROUND_HALF_EVEN));
                    KualiInteger agencyRequestFringe = agencyRequestSalary.multiply(agnecyFringeRateDecimalMultiplier);

                    KualiInteger institutionCostShareRequestSalary = userAppointmentTaskPeriod.getUserBudgetPeriodSalaryAmount().multiply(userAppointmentTaskPeriod.getInstitutionCostSharePercentEffortAmount().bigDecimalValue().divide(BigDecimal.valueOf(100), 8, BigDecimal.ROUND_HALF_EVEN));
                    KualiInteger institutionCostShareRequestFringe = institutionCostShareRequestSalary.multiply(costShareFringeRateDecimalMultiplier);

                    userAppointmentTaskPeriod.setAgencyRequestTotalAmount(agencyRequestSalary);
                    userAppointmentTaskPeriod.setAgencyFringeBenefitTotalAmount(agencyRequestFringe);

                    userAppointmentTask.setAgencyRequestTotalAmountTask(userAppointmentTask.getAgencyRequestTotalAmountTask().add(agencyRequestSalary));
                    userAppointmentTask.setAgencyFringeBenefitTotalAmountTask(userAppointmentTask.getAgencyFringeBenefitTotalAmountTask().add(agencyRequestFringe));

                    userAppointmentTaskPeriod.setInstitutionCostShareRequestTotalAmount(institutionCostShareRequestSalary);
                    userAppointmentTaskPeriod.setInstitutionCostShareFringeBenefitTotalAmount(institutionCostShareRequestFringe);

                    userAppointmentTask.setInstitutionCostShareRequestTotalAmountTask(userAppointmentTask.getInstitutionCostShareRequestTotalAmountTask().add(institutionCostShareRequestSalary));
                    userAppointmentTask.setInstitutionCostShareFringeBenefitTotalAmountTask(userAppointmentTask.getInstitutionCostShareFringeBenefitTotalAmountTask().add(institutionCostShareRequestFringe));

                    userAppointmentTaskPeriod.setTotalPercentEffort(userAppointmentTaskPeriod.getAgencyPercentEffortAmount().add(userAppointmentTaskPeriod.getInstitutionCostSharePercentEffortAmount()));
                    userAppointmentTaskPeriod.setTotalSalaryAmount(userAppointmentTaskPeriod.getAgencyRequestTotalAmount().add(userAppointmentTaskPeriod.getInstitutionCostShareRequestTotalAmount()));
                    userAppointmentTaskPeriod.setTotalFringeAmount(userAppointmentTaskPeriod.getAgencyFringeBenefitTotalAmount().add(userAppointmentTaskPeriod.getInstitutionCostShareFringeBenefitTotalAmount()));
                }
                else if (StringUtils.contains(getAppointmentTypeMappings().get(KraConstants.HOURLY).toString(), userAppointmentTask.getInstitutionAppointmentTypeCode())) {

                    KualiInteger hourlyAgencyRequest = userAppointmentTaskPeriod.getUserAgencyHours().multiply(userAppointmentTaskPeriod.getUserHourlyRate().bigDecimalValue());
                    KualiInteger hourlyInstitutionRequest = userAppointmentTaskPeriod.getUserInstitutionHours().multiply(userAppointmentTaskPeriod.getUserHourlyRate());

                    userAppointmentTaskPeriod.setAgencyRequestTotalAmount(hourlyAgencyRequest);
                    userAppointmentTaskPeriod.setAgencyFringeBenefitTotalAmount(hourlyAgencyRequest.multiply(agnecyFringeRateDecimalMultiplier));

                    userAppointmentTask.setAgencyRequestTotalAmountTask(userAppointmentTask.getAgencyRequestTotalAmountTask().add(hourlyAgencyRequest));
                    userAppointmentTask.setAgencyFringeBenefitTotalAmountTask(userAppointmentTask.getAgencyFringeBenefitTotalAmountTask().add(hourlyAgencyRequest.multiply(agnecyFringeRateDecimalMultiplier)));

                    userAppointmentTaskPeriod.setInstitutionCostShareRequestTotalAmount(hourlyInstitutionRequest);
                    userAppointmentTaskPeriod.setInstitutionCostShareFringeBenefitTotalAmount(hourlyInstitutionRequest.multiply(costShareFringeRateDecimalMultiplier));

                    userAppointmentTask.setInstitutionCostShareRequestTotalAmountTask(userAppointmentTask.getInstitutionCostShareRequestTotalAmountTask().add(hourlyInstitutionRequest));
                    userAppointmentTask.setInstitutionCostShareFringeBenefitTotalAmountTask(userAppointmentTask.getInstitutionCostShareFringeBenefitTotalAmountTask().add(hourlyInstitutionRequest.multiply(costShareFringeRateDecimalMultiplier)));

                    userAppointmentTaskPeriod.setTotalPercentEffort(userAppointmentTaskPeriod.getAgencyPercentEffortAmount().add(userAppointmentTaskPeriod.getInstitutionCostSharePercentEffortAmount()));
                    userAppointmentTaskPeriod.setTotalSalaryAmount(userAppointmentTaskPeriod.getAgencyRequestTotalAmount().add(userAppointmentTaskPeriod.getInstitutionCostShareRequestTotalAmount()));
                    userAppointmentTaskPeriod.setTotalFringeAmount(userAppointmentTaskPeriod.getAgencyFringeBenefitTotalAmount().add(userAppointmentTaskPeriod.getInstitutionCostShareFringeBenefitTotalAmount()));
                    userAppointmentTaskPeriod.setTotalPercentEffort(userAppointmentTaskPeriod.getUserAgencyHours().add(userAppointmentTaskPeriod.getUserInstitutionHours()));
                }
                else if (StringUtils.contains(getAppointmentTypeMappings().get(KraConstants.GRADUATE_ASSISTANT).toString(), userAppointmentTask.getInstitutionAppointmentTypeCode())) {

                    userAppointmentTask.setGradAsstAgencyHealthInsuranceTotal(userAppointmentTask.getGradAsstAgencyHealthInsuranceTotal().add(userAppointmentTaskPeriod.getAgencyHealthInsuranceAmount()));
                    userAppointmentTask.setGradAsstAgencySalaryTotal(userAppointmentTask.getGradAsstAgencySalaryTotal().add(userAppointmentTaskPeriod.getAgencySalaryAmount()));
                    userAppointmentTask.setGradAsstInstHealthInsuranceTotal(userAppointmentTask.getGradAsstInstHealthInsuranceTotal().add(userAppointmentTaskPeriod.getInstitutionHealthInsuranceAmount()));
                    userAppointmentTask.setGradAsstInstSalaryTotal(userAppointmentTask.getGradAsstInstSalaryTotal().add(userAppointmentTaskPeriod.getInstitutionSalaryAmount()));
                    
                    userAppointmentTaskPeriod.setTotalFteAmount(userAppointmentTaskPeriod.getAgencyFullTimeEquivalentPercent().add(userAppointmentTaskPeriod.getInstitutionFullTimeEquivalentPercent()));
                    userAppointmentTaskPeriod.setTotalGradAsstSalaryAmount(userAppointmentTaskPeriod.getAgencySalaryAmount().add(userAppointmentTaskPeriod.getInstitutionSalaryAmount()));
                    userAppointmentTaskPeriod.setTotalHealthInsuranceAmount(userAppointmentTaskPeriod.getAgencyHealthInsuranceAmount().add(userAppointmentTaskPeriod.getInstitutionHealthInsuranceAmount()));
                    userAppointmentTaskPeriod.setTotalFeeRemissionsAmount(userAppointmentTaskPeriod.getUserCreditHoursNumber().multiply(userAppointmentTaskPeriod.getUserCreditHourAmount()).add(userAppointmentTaskPeriod.getUserMiscellaneousFeeAmount()));
                }
            }
        }
    }

    /**
     * This method calculates the salary/period salary one person listed on a budget.
     * 
     * @parm budgetUser - The user whose compensation is being calculated
     * @parm budgetDocument - The budgetDocument with information about how to calculate the period salary: period lengths and
     *       inflation rates
     */
    public void calculatePersonCompensation(BudgetUser budgetUser, BudgetDocument budgetDocument) {
        calculatePersonSalary(budgetUser, budgetDocument);
        calculatePersonRequestAmounts(budgetUser, budgetDocument.getBudget().getFringeRates());
    }

    /**
     * This method calculates the salary/period salary for all people listed on a budget.
     * 
     * @parm budgetUser - The user whose compensation is being calculated
     * @parm budgetDocument - The budgetDocument with information about how to calculate the period salary: period lengths and
     *       inflation rates
     */
    public void calculateAllPersonnelCompensation(BudgetDocument budgetDocument) {
        budgetDocument.getBudget().refreshReferenceObject("fringeRates");
        BudgetTask firstBudgetTask = (BudgetTask) budgetDocument.getBudget().getTasks().get(0);
        for (BudgetUser budgetUser : budgetDocument.getBudget().getPersonnel()) {
            if (budgetUser.getCurrentTaskNumber() == null)
                budgetUser.setCurrentTaskNumber(firstBudgetTask.getBudgetTaskSequenceNumber());
            verifyAndPropogateAppointmentType(budgetUser, budgetDocument);
            calculatePersonCompensation(budgetUser, budgetDocument);
        }
    }

    private void verifyAndPropogateAppointmentType(BudgetUser budgetUser, BudgetDocument budgetDocument) {
        List newUserAppointmentTasks = new ArrayList();

        //check to see if the budgetUser.getAppointmentTypeCode has a value - if it doesn't, we're coming into the page from another page or are reloading data.  appointment type code is not stored
        //in the database.  If it does exist, check to make sure that the type code hasn't changed since the last su
        if (budgetUser.getAppointmentTypeCode() != null && !budgetUser.getAppointmentTypeCode().equals(budgetUser.getPreviousAppointmentTypeCode())) {
            // appointment type has changed

            AppointmentType previousAppointmentType = (AppointmentType) businessObjectService.retrieve(new AppointmentType(budgetUser.getPreviousAppointmentTypeCode()));
            
            // Update the Fringes for this person to ensure that we're getting the most recent amounts
            BudgetFringeRate budgetFringeRate = budgetFringeRateService.getBudgetFringeRateForPerson(budgetUser);
            budgetUser.setAppointmentTypeCode(budgetFringeRate.getInstitutionAppointmentTypeCode());

            BudgetFringeRate secondaryBudgetFringeRate = null;
            if (budgetFringeRate.getAppointmentType().getRelatedAppointmentTypeCode() != null) {
                secondaryBudgetFringeRate = budgetFringeRateService.getBudgetFringeRate(budgetDocument.getDocumentNumber(), budgetFringeRate.getAppointmentType().getRelatedAppointmentTypeCode());
                budgetUser.setSecondaryAppointmentTypeCode(secondaryBudgetFringeRate.getInstitutionAppointmentTypeCode());
            } else {
                if (budgetUser.getSecondaryAppointmentTypeCode() != null && budgetUser.getSecondaryAppointmentTypeCode().equals(budgetUser.getAppointmentTypeCode())) {
                    //previous secondary appointment type is the same as the current appointment type - convert previous secondary to new primary
                    for (Iterator i = budgetUser.getUserAppointmentTasks().iterator(); i.hasNext(); ) {
                        //using iterator instead of enhanced for-loop so I can use iter.remove()
                        UserAppointmentTask userAppointmentTask = (UserAppointmentTask)i.next();
                        if (userAppointmentTask.getInstitutionAppointmentTypeCode().equals(budgetUser.getAppointmentTypeCode())) {
                            i.remove();
                        }
                    }
                }
                budgetUser.setSecondaryAppointmentTypeCode(null);
            }
            
            for (UserAppointmentTask userAppointmentTask : budgetUser.getUserAppointmentTasks()) {
    
                //check to see if this userAppointmentTask is associated with the primary or secondary (related) appointment type.
                if (!userAppointmentTask.isSecondaryAppointment()) {
                    userAppointmentTask.setBudgetFringeRate(budgetFringeRate);
                    userAppointmentTask.setInstitutionAppointmentTypeCode(budgetFringeRate.getInstitutionAppointmentTypeCode());
    
                    //trickle-down the new appointmentType into the userAppointmentTaskPeriod objects 
                    for (UserAppointmentTaskPeriod userAppointmentTaskPeriod : userAppointmentTask.getUserAppointmentTaskPeriods()) {
                        userAppointmentTaskPeriod.setBudgetFringeRate(budgetFringeRate);
                        userAppointmentTaskPeriod.setInstitutionAppointmentTypeCode(budgetFringeRate.getInstitutionAppointmentTypeCode());
                    }
                } else if (secondaryBudgetFringeRate != null) {
                    //this is a secondary appointment type record, update the record and the associated userAppointmentTaskPeriod records
                    userAppointmentTask.setBudgetFringeRate(secondaryBudgetFringeRate);
                    userAppointmentTask.setInstitutionAppointmentTypeCode(secondaryBudgetFringeRate.getInstitutionAppointmentTypeCode());
    
                    for (UserAppointmentTaskPeriod userAppointmentTaskPeriod : userAppointmentTask.getUserAppointmentTaskPeriods()) {
                        userAppointmentTaskPeriod.setBudgetFringeRate(secondaryBudgetFringeRate);
                        userAppointmentTaskPeriod.setInstitutionAppointmentTypeCode(secondaryBudgetFringeRate.getInstitutionAppointmentTypeCode());
                    }
                }
            }
            if (secondaryBudgetFringeRate != null && budgetUser.getUserAppointmentTasks().size() / budgetDocument.getBudget().getTasks().size() < (secondaryBudgetFringeRate != null ? 2 : 1)) {
                //There is a secondary appointment type, but there are no records created for it yet.  Create them.
                BudgetUser missingBudgetUser = new BudgetUser(budgetUser);
                missingBudgetUser.setAppointmentTypeCode(secondaryBudgetFringeRate.getInstitutionAppointmentTypeCode());
                for (BudgetTask task : budgetDocument.getBudget().getTasks()) {
                    budgetUser.getUserAppointmentTasks().add(createUserAppointmentTask(missingBudgetUser, budgetDocument, secondaryBudgetFringeRate, task, budgetDocument.getBudget().getPeriods(), true));
                }
            }
        } else if (budgetUser.getAppointmentTypeCode() == null) {
            //Coming into the page with no appointment type code set in BudgetUser - need to set it.
            List<AppointmentType> appointmentTypes = new ArrayList();
            for (UserAppointmentTask userAppointmentTask : budgetUser.getUserAppointmentTasks()) {
                if (!appointmentTypes.contains(userAppointmentTask.getInstitutionAppointmentTypeCode())) {
                    appointmentTypes.add((AppointmentType) businessObjectService.retrieve(new AppointmentType(userAppointmentTask.getInstitutionAppointmentTypeCode())));
                }
            }
            if (appointmentTypes.size() == 1) {
                BudgetFringeRate budgetFringeRate = budgetFringeRateService.getBudgetFringeRateForPerson(budgetUser);
                budgetUser.setAppointmentTypeCode(budgetFringeRate.getInstitutionAppointmentTypeCode());
            } else {
                for (AppointmentType appointmentType : appointmentTypes) {
                    if (appointmentType.getRelatedAppointmentTypeCode() != null) {
                        budgetUser.setAppointmentTypeCode(appointmentType.getAppointmentTypeCode());
                    } else {
                        budgetUser.setSecondaryAppointmentTypeCode(appointmentType.getAppointmentTypeCode());
                    }
                }
                for (UserAppointmentTask userAppointmentTask : budgetUser.getUserAppointmentTasks()) {
                    userAppointmentTask.setSecondaryAppointment(userAppointmentTask.getInstitutionAppointmentTypeCode().equals(budgetUser.getSecondaryAppointmentTypeCode()));
                }
            }
        }
    }

    public void reconcileAndCalculatePersonnel(BudgetDocument budgetDocument) {
        BudgetTask firstBudgetTask = (BudgetTask) budgetDocument.getBudget().getTasks().get(0);
        for (BudgetUser budgetUser : budgetDocument.getBudget().getPersonnel()) {
            if (budgetUser.getCurrentTaskNumber() == null)
                budgetUser.setCurrentTaskNumber(firstBudgetTask.getBudgetTaskSequenceNumber());
            reconcilePersonTaskPeriod(budgetUser, budgetDocument);
            calculatePersonCompensation(budgetUser, budgetDocument);
        }
    }

    /**
     * This method calculates whether tasks/period have been added to the budget that need to be included with all personnel
     * 
     * @parm budgetDocument - The budgetDocument with information about how to calculate the period salary: period lengths and
     *       inflation rates
     */
    public void reconcileAllPersonnelTaskPeriod(BudgetDocument budgetDocument) {
        for (BudgetUser budgetUser : budgetDocument.getBudget().getPersonnel()) {
            reconcilePersonTaskPeriod(budgetUser, budgetDocument);
        }
    }

    /**
     * This method calculates whether tasks/period have been added to the budget that need to be included for one person
     * 
     * @parm budgetDocument - The budgetDocument with information about how to calculate the period salary: period lengths and
     *       inflation rates
     * @parm budgetUser - The individual for whom we are reconciling tasks/period
     */
    public void reconcilePersonTaskPeriod(BudgetUser budgetUser, BudgetDocument budgetDocument) {
        BudgetFringeRate budgetFringeRate = budgetFringeRateService.getBudgetFringeRateForPerson(budgetUser);
        budgetUser.setAppointmentTypeCode(budgetFringeRate.getInstitutionAppointmentTypeCode());
        if (budgetFringeRate.getAppointmentType().getRelatedAppointmentTypeCode() != null) {
            budgetUser.setSecondaryAppointmentTypeCode(budgetFringeRate.getAppointmentType().getRelatedAppointmentTypeCode());
        }

        List<BudgetTask> tasks = new ArrayList(budgetDocument.getBudget().getTasks());

        List<BudgetFringeRate> budgetFringeRates = new ArrayList();
        budgetFringeRates.add(budgetFringeRate);

        // Iterate over the list of UserAppointmentTask objects, remove the task from each object from the Tasks List.
        for (UserAppointmentTask userAppointmentTask : budgetUser.getUserAppointmentTasks()) {
            userAppointmentTask.refreshReferenceObject(KraConstants.TASK);
            ObjectUtils.removeObjectWithIdentitcalKey(tasks, userAppointmentTask.getTask());

            if (!ObjectUtils.collectionContainsObjectWithIdentitcalKey(budgetFringeRates, userAppointmentTask.getBudgetFringeRate())) {
                budgetFringeRate = userAppointmentTask.getBudgetFringeRate();
                budgetFringeRates.add(budgetFringeRate);
            }

            List<BudgetPeriod> periods = new ArrayList(budgetDocument.getBudget().getPeriods());

            // Iterate over the list of UserAppointmentTaskPeriod objectes, remove the period associated with each object from the
            // uatpPeriods list
            for (UserAppointmentTaskPeriod userAppointmentTaskPeriod : userAppointmentTask.getUserAppointmentTaskPeriods()) {
                userAppointmentTaskPeriod.refreshReferenceObject(KraConstants.PERIOD);
                ObjectUtils.removeObjectWithIdentitcalKey(periods, userAppointmentTaskPeriod.getPeriod());
            }

            // Any periods still in the list do not have UserAppointmentTaskPeriod objects associated with them. Need to create one.
            for (BudgetPeriod period : periods) {
                userAppointmentTask.getUserAppointmentTaskPeriods().add(createUserAppointmentTaskPeriod(budgetUser, budgetDocument, budgetFringeRate, userAppointmentTask.getTask(), period));
            }

            userAppointmentTask.setSecondaryAppointment(userAppointmentTask.getInstitutionAppointmentTypeCode().equals(budgetUser.getSecondaryAppointmentTypeCode()));

            Collections.sort(userAppointmentTask.getUserAppointmentTaskPeriods());
        }

        // Any tasks left over did not have UserAppointmentTask objects associated with them. Create them.
        for (BudgetTask task : tasks) {
            for (Iterator budgetFringeRateIterator = budgetFringeRates.iterator(); budgetFringeRateIterator.hasNext();) {
                budgetFringeRate = (BudgetFringeRate) budgetFringeRateIterator.next();
                budgetUser.getUserAppointmentTasks().add(createUserAppointmentTask(budgetUser, budgetDocument, budgetFringeRate, task, budgetDocument.getBudget().getPeriods(), budgetFringeRate.getInstitutionAppointmentTypeCode().equals(budgetUser.getSecondaryAppointmentTypeCode())));
            }
        }
        
        Collections.sort(budgetUser.getUserAppointmentTasks());
    }

    /**
     * This method will remove Personnel entries that relate to tasks/periods that no longer exist. See cleanseNonpersonnel
     * 
     * @param budgetDocument
     */
    public void cleansePersonnel(BudgetDocument budgetDocument) {
        List budgetPersonnel = budgetDocument.getBudget().getPersonnel();
        List budgetTasks = budgetDocument.getBudget().getTasks();
        List budgetPeriods = budgetDocument.getBudget().getPeriods();

        //Not using JDK 1.5 style for-loop so Iterator.remove() can be called
        for (Iterator budgetUserIter = budgetPersonnel.iterator(); budgetUserIter.hasNext();) {
            BudgetUser budgetUser = (BudgetUser) budgetUserIter.next();
            List userAppointments = new ArrayList();
            for (Iterator userAppointmentTaskIter = budgetUser.getUserAppointmentTasks().iterator(); userAppointmentTaskIter.hasNext();) {
                // loop through usreAppointmentTasks and check that the associated exists in the Task list
                UserAppointmentTask userAppointmentTask = (UserAppointmentTask) userAppointmentTaskIter.next();
                userAppointments.add(userAppointmentTask.getInstitutionAppointmentTypeCode());
                BudgetTask budgetTask = (BudgetTask) businessObjectService.retrieve(new BudgetTask(userAppointmentTask.getDocumentNumber(), userAppointmentTask.getBudgetTaskSequenceNumber()));
                if (!ObjectUtils.collectionContainsObjectWithIdentitcalKey(budgetTasks, budgetTask)) {
                    userAppointmentTaskIter.remove();
                }
                else {
                    // loop through userAppointmentTaskPeriods and check each period.
                    for (Iterator userAppointmentTaskPeriodIter = userAppointmentTask.getUserAppointmentTaskPeriods().iterator(); userAppointmentTaskPeriodIter.hasNext();) {
                        UserAppointmentTaskPeriod userAppointmentTaskPeriod = (UserAppointmentTaskPeriod) userAppointmentTaskPeriodIter.next();
                        BudgetPeriod budgetPeriod = (BudgetPeriod) businessObjectService.retrieve(new BudgetPeriod(userAppointmentTaskPeriod.getDocumentNumber(), userAppointmentTaskPeriod.getBudgetPeriodSequenceNumber()));
                        if (!ObjectUtils.collectionContainsObjectWithIdentitcalKey(budgetPeriods, budgetPeriod)) {
                            userAppointmentTaskPeriodIter.remove();
                        }
                        else {
                            if (!(StringUtils.contains(getAppointmentTypeMappings().get(KraConstants.FULL_YEAR).toString(), userAppointmentTask.getInstitutionAppointmentTypeCode()) || StringUtils.contains(getAppointmentTypeMappings().get(KraConstants.ACADEMIC_YEAR_SUMMER).toString(), userAppointmentTask.getInstitutionAppointmentTypeCode()))) {
                                // Not a salaried appointment type
                                userAppointmentTaskPeriod.setAgencyPercentEffortAmount(new KualiInteger(0));
                                userAppointmentTaskPeriod.setInstitutionCostSharePercentEffortAmount(new KualiInteger(0));

                            }

                            if (!StringUtils.contains(getAppointmentTypeMappings().get(KraConstants.HOURLY).toString(), userAppointmentTask.getInstitutionAppointmentTypeCode())) {
                                // Not an hourly appointment type; remove hourly data
                                userAppointmentTaskPeriod.setUserAgencyHours(new KualiInteger(0));
                                userAppointmentTaskPeriod.setUserInstitutionHours(new KualiInteger(0));
                            }

                            if (!StringUtils.contains(getAppointmentTypeMappings().get(KraConstants.GRADUATE_ASSISTANT).toString(), userAppointmentTask.getInstitutionAppointmentTypeCode())) {
                                userAppointmentTaskPeriod.setAgencyFullTimeEquivalentPercent(new KualiInteger(0));
                                userAppointmentTaskPeriod.setAgencyHealthInsuranceAmount(new KualiInteger(0));
                                userAppointmentTaskPeriod.setAgencyRequestedFeesAmount(new KualiInteger(0));
                                userAppointmentTaskPeriod.setAgencySalaryAmount(new KualiInteger(0));
                                userAppointmentTaskPeriod.setInstitutionFullTimeEquivalentPercent(new KualiInteger(0));
                                userAppointmentTaskPeriod.setInstitutionHealthInsuranceAmount(new KualiInteger(0));
                                userAppointmentTaskPeriod.setInstitutionRequestedFeesAmount(new KualiInteger(0));
                                userAppointmentTaskPeriod.setUserMiscellaneousFeeAmount(new KualiInteger(0));
                                userAppointmentTaskPeriod.setUserCreditHourAmount(new KualiDecimal(0));
                                userAppointmentTaskPeriod.setUserCreditHoursNumber(new KualiInteger(0));
                                userAppointmentTaskPeriod.setInstitutionSalaryAmount(new KualiInteger(0));
                            }
                        }
                    }

                    if (userAppointmentTask.getUserAppointmentTaskPeriods().isEmpty() || userAppointments.size() > 1 && !userAppointments.contains(getAppointmentTypeMappings().get(KraConstants.ACADEMIC_YEAR)) && userAppointments.contains(getAppointmentTypeMappings().get(KraConstants.ACADEMIC_SUMMER))) {
                        // All of the associated userAppointmentTaskPeriods have been removed from this userAppointmentTask, and it
                        // should therefore be removed as well
                        userAppointmentTaskIter.remove();
                    }
                }
            }

            // done evaluating userAppointmentTasks for this budgetUser, check to see if there are any remaining
            if (budgetUser.getUserAppointmentTasks().isEmpty() && !budgetUser.isPersonProjectDirectorIndicator()) {
                // none remaining remove this budgetUser from the list
                budgetUserIter.remove();
            }
        }
    }

    public void reconcileProjectDirector(BudgetDocument budgetDocument) {
        Budget budget = budgetDocument.getBudget();

        BudgetUser projectDirector = null;

        for (Iterator i = budget.getPersonnel().iterator(); i.hasNext();) {
            BudgetUser budgetUser = (BudgetUser) i.next();
            if (budgetUser.isPersonProjectDirectorIndicator()) {
                projectDirector = budgetUser;
                break;
            }
        }

        if (projectDirector == null) {
            // PD does not exist in Personnel List already
            projectDirector = new BudgetUser();
            if (!budget.isProjectDirectorToBeNamedIndicator()) {
                // PD is a real person (not TBN)
                projectDirector.setPersonUniversalIdentifier(budget.getBudgetProjectDirectorUniversalIdentifier());
                projectDirector.refresh();
            }
            budgetDocument.addPersonnel(projectDirector);
        }
        else {
            // PD does exist in Personnel List already
            String paramsPdUid = budget.getBudgetProjectDirectorUniversalIdentifier() != null ? budget.getBudgetProjectDirectorUniversalIdentifier() : "";
            String personnelSysId = projectDirector.getPersonUniversalIdentifier() != null ? projectDirector.getPersonUniversalIdentifier() : "";
            if (!(budget.isProjectDirectorToBeNamedIndicator() && StringUtils.isEmpty(personnelSysId)) && !paramsPdUid.equals(personnelSysId)) {
                // PD from params is not the same as the PD from the Personnel List

                boolean projectDirectorFound = false;

                projectDirector.setPersonProjectDirectorIndicator(false);
                projectDirector = null;

                if (!budget.isProjectDirectorToBeNamedIndicator()) {
                    // PD from Params is not TBN
                    for (Iterator i = budget.getPersonnel().iterator(); i.hasNext() && !projectDirectorFound;) {
                        BudgetUser budgetUser = (BudgetUser) i.next();
                        if (budgetUser.getPersonUniversalIdentifier() != null && budgetUser.getPersonUniversalIdentifier().equals(paramsPdUid)) {
                            projectDirector = budgetUser;
                            projectDirectorFound = true;
                        }
                    }

                    if (!projectDirectorFound) {
                        // PD from params was not found in the existing Personnel list; need to create a new personnel entry
                        projectDirector = new BudgetUser();
                        projectDirector.setPersonUniversalIdentifier(paramsPdUid);
                        projectDirector.refresh();
                        budgetDocument.addPersonnel(projectDirector);
                    }
                }
                else {
                    // PD from Params is TBN
                    projectDirector = new BudgetUser();
                    budgetDocument.addPersonnel(projectDirector);
                }
            }
        }
        projectDirector.setPersonProjectDirectorIndicator(true);
        projectDirector.setPersonSeniorKeyIndicator(true);
        projectDirector.setRole(KraConstants.PROJECT_DIRECTOR);
    }

    public HashMap getAppointmentTypeMappings() {
//        if (appointmentTypeMappings == null) {
            appointmentTypeMappings = new HashMap();

            // using Arrays.asList(String[]).toString() so I can easily check from elsewhere whether or not a particular appointment
            // type is in the list
            appointmentTypeMappings.put(KraConstants.FULL_YEAR, Arrays.asList(kualiConfigurationService.getApplicationParameterValues(KraConstants.KRA_DEVELOPMENT_GROUP, KraConstants.KRA_BUDGET_PERSONNEL_FULL_YEAR_APPOINTMENT_TYPES)).toString());
            String[] academicYearSummerArray = kualiConfigurationService.getApplicationParameterValues(KraConstants.KRA_DEVELOPMENT_GROUP, KraConstants.KRA_BUDGET_PERSONNEL_SUMMER_GRID_APPOINTMENT_TYPES);
            appointmentTypeMappings.put(KraConstants.ACADEMIC_YEAR_SUMMER, Arrays.asList(academicYearSummerArray).toString());
            appointmentTypeMappings.put(KraConstants.ACADEMIC_YEAR_SUMMER_ARRAY, academicYearSummerArray);
            appointmentTypeMappings.put(KraConstants.HOURLY, Arrays.asList(kualiConfigurationService.getApplicationParameterValues(KraConstants.KRA_DEVELOPMENT_GROUP, KraConstants.KRA_BUDGET_PERSONNEL_HOURLY_APPOINTMENT_TYPES)).toString());
            appointmentTypeMappings.put(KraConstants.GRADUATE_ASSISTANT, Arrays.asList(kualiConfigurationService.getApplicationParameterValues(KraConstants.KRA_DEVELOPMENT_GROUP, KraConstants.KRA_BUDGET_PERSONNEL_GRADUATE_RESEARCH_ASSISTANT_APPOINTMENT_TYPES)).toString());
            appointmentTypeMappings.put(KraConstants.ACADEMIC_SUMMER, kualiConfigurationService.getApplicationParameterValue(KraConstants.KRA_DEVELOPMENT_GROUP, KraConstants.KRA_BUDGET_PERSONNEL_SUMMER_GRID_APPOINTMENT_TYPE));
            appointmentTypeMappings.put(KraConstants.ACADEMIC_YEAR, kualiConfigurationService.getApplicationParameterValue(KraConstants.KRA_DEVELOPMENT_GROUP, KraConstants.KRA_BUDGET_PERSONNEL_ACADEMIC_YEAR_APPOINTMENT_TYPE));
//        }
        return appointmentTypeMappings;
    }

    public Date getAppointmentTypeEffectiveStartDate(AppointmentType appointmentType, Integer fiscalYear) {
        
        AppointmentTypeEffectiveDate appointmentTypeEffectiveDate = (AppointmentTypeEffectiveDate) businessObjectService.retrieve(
                new AppointmentTypeEffectiveDate(appointmentType.getAppointmentTypeCode(), fiscalYear));
        
        if (appointmentTypeEffectiveDate != null) {
            return appointmentTypeEffectiveDate.getAppointmentTypeBeginDate();
        }
        else {
            return SpringServiceLocator.getUniversityDateService().getFirstDateOfFiscalYear(fiscalYear);
        }
    }

    public Date getAppointmentTypeEffectiveEndDate(AppointmentType appointmentType, Integer fiscalYear) {
        
        AppointmentTypeEffectiveDate appointmentTypeEffectiveDate = (AppointmentTypeEffectiveDate) businessObjectService.retrieve(
                new AppointmentTypeEffectiveDate(appointmentType.getAppointmentTypeCode(), fiscalYear));
        
        if (appointmentTypeEffectiveDate != null) {
            return appointmentTypeEffectiveDate.getAppointmentTypeEndDate();
        }
        else {
            return SpringServiceLocator.getUniversityDateService().getLastDateOfFiscalYear(fiscalYear);
        }
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setBudgetFringeRateService(BudgetFringeRateService budgetFringeRateService) {
        this.budgetFringeRateService = budgetFringeRateService;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * 
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public class PeriodSalary {
        private int workDaysInPeriod;
        private KualiInteger periodSalary;

        public PeriodSalary(KualiInteger periodSalary, int workDaysInPeriod) {
            this.periodSalary = periodSalary;
            this.workDaysInPeriod = workDaysInPeriod;
        }

        public PeriodSalary add(PeriodSalary periodSalary) {
            this.periodSalary = this.periodSalary.add(periodSalary.periodSalary);
            this.workDaysInPeriod += periodSalary.workDaysInPeriod;
            return this;
        }

        public KualiInteger getPeriodSalary() {
            return periodSalary;
        }

        public void setPeriodSalary(KualiInteger periodSalary) {
            this.periodSalary = periodSalary;
        }

        public int getWorkDaysInPeriod() {
            return workDaysInPeriod;
        }

        public void setWorkDaysInPeriod(int workDaysInPeriod) {
            this.workDaysInPeriod = workDaysInPeriod;
        }
    }
}

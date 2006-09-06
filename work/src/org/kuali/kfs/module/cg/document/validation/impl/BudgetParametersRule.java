/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.kra.budget.rules.budget;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.KeyConstants;
import org.kuali.core.document.Document;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.kra.budget.KraConstants;
import org.kuali.module.kra.budget.bo.BudgetFringeRate;
import org.kuali.module.kra.budget.bo.BudgetGraduateAssistantRate;
import org.kuali.module.kra.budget.bo.BudgetPeriod;
import org.kuali.module.kra.budget.document.BudgetDocument;

public class BudgetParametersRule {
    private String MAXIMUM_PERIOD_LENGTH;
    private String PERIOD_IDENTIFIER;
    private String NEW_PERIOD_IDENTIFIER;
    
    /**
     * 
     */
    protected BudgetParametersRule() {
        KualiConfigurationService kcs = SpringServiceLocator.getKualiConfigurationService();

        MAXIMUM_PERIOD_LENGTH = kcs.getApplicationParameterValue("KraDevelopmentGroup", "maximumPeriodLength");
        PERIOD_IDENTIFIER = kcs.getApplicationParameterValue("KraDevelopmentGroup", "periodIdentifier");
        NEW_PERIOD_IDENTIFIER = kcs.getApplicationParameterValue("KraDevelopmentGroup", "newPeriodIdentifier");
    }

    protected boolean isParametersValid(BudgetDocument budgetDocument) {
        boolean valid = true;
        valid &= isProjectDirectorValid(budgetDocument);
        valid &= isAgencyValid(budgetDocument);
        valid &= isInflationRatesValid(budgetDocument);
        valid &= isPeriodListValid(budgetDocument.getBudget().getPeriods(), budgetDocument.getBudget().isAgencyModularIndicator());
        valid &= isTaskListValid(budgetDocument.getBudget().getTasks());
        valid &= isFringeRateListValid(budgetDocument);
        valid &= isGraduateAssistantRateListValid(budgetDocument);
        return valid;
    }
    
    /**
     * Checks business rules related to adding a Period.
     * 
     * @param Document document
     * @return boolean True if the document is valid, false otherwise.
     */
    protected boolean processAddPeriodBusinessRules(Document document, BudgetPeriod budgetPeriod) {
        if (!(document instanceof BudgetDocument)) {
            return false;
        }

        boolean valid = true;

        BudgetDocument budgetDocument = (BudgetDocument) document;

        GlobalVariables.getErrorMap().addToErrorPath("document");

        valid &= isPeriodValid(budgetPeriod, NEW_PERIOD_IDENTIFIER, new Integer(0));

        List currentPeriods = budgetDocument.getBudget().getPeriods();
        if (currentPeriods != null && currentPeriods.size() > 0) {
            BudgetPeriod lastPeriod = (BudgetPeriod) currentPeriods.get(currentPeriods.size() - 1);
            valid &= isConsecutivePeriods(lastPeriod.getBudgetPeriodEndDate(), budgetPeriod.getBudgetPeriodBeginDate(), PERIOD_IDENTIFIER + " " + lastPeriod.getBudgetPeriodSequenceNumber().toString(), NEW_PERIOD_IDENTIFIER, "budget.period.notConsecutive_0");
        }

        GlobalVariables.getErrorMap().removeFromErrorPath("document");

        return valid;
    }

    protected boolean isInflationRatesValid(BudgetDocument budgetDocument) {
        boolean valid = true;
        /** TODO Use constants framework */
        KualiDecimal MAX_INFLATION_RATE = new KualiDecimal(11.00);

        if (budgetDocument.getBudget().getBudgetPersonnelInflationRate() != null && budgetDocument.getBudget().getBudgetPersonnelInflationRate().isGreaterThan(MAX_INFLATION_RATE)) {
            GlobalVariables.getErrorMap().putError("budget.budgetPersonnelInflationRate", KeyConstants.ERROR_INVALID_VALUE, new String[] { "Personnel Inflation Rate" });
            valid = false;
        }

        if (budgetDocument.getBudget().getBudgetNonpersonnelInflationRate() != null && budgetDocument.getBudget().getBudgetNonpersonnelInflationRate().isGreaterThan(MAX_INFLATION_RATE)) {
            GlobalVariables.getErrorMap().putError("budget.budgetNonpersonnelInflationRate", KeyConstants.ERROR_INVALID_VALUE, new String[] { "Nonpersonnel Inflation Rate" });
            valid = false;
        }

        return valid;
    }

    /**
     * Checks whether a list of periods is valid - consecutive, not too long or too short, and each individual period is valid.
     * 
     * @param List periods
     * @return boolean True if the list is valid, false otherwise.
     */
    protected boolean isPeriodListValid(List periods, boolean modularBudget) {
        boolean valid = true;

        valid &= isNumPeriodsValid(periods, modularBudget);

        for (int i = 0; i < periods.size(); i++) {
            BudgetPeriod currentPeriod = (BudgetPeriod) periods.get(i);

            valid &= isPeriodValid(currentPeriod, PERIOD_IDENTIFIER + " " + currentPeriod.getBudgetPeriodSequenceNumber().toString(), currentPeriod.getBudgetPeriodSequenceNumber());

            if (i > 0) {
                BudgetPeriod previousPeriod = (BudgetPeriod) periods.get(i - 1);
                valid &= isConsecutivePeriods(previousPeriod.getBudgetPeriodEndDate(), currentPeriod.getBudgetPeriodBeginDate(), PERIOD_IDENTIFIER + " " + previousPeriod.getBudgetPeriodSequenceNumber().toString(), PERIOD_IDENTIFIER + " " + currentPeriod.getBudgetPeriodSequenceNumber().toString(), "budget.period.notConsecutive_" + currentPeriod.getBudgetPeriodSequenceNumber().toString());
            }
        }
        return valid;
    }

    /**
     * Checks whether budgetDocument has a valid Project Director.
     * 
     * @param BudgetDocument budgetDocument
     * @return boolean True if the Project Director is valid, false otherwise.
     */
    protected boolean isProjectDirectorValid(BudgetDocument budgetDocument) {

        if (!StringUtils.isBlank(budgetDocument.getBudget().getBudgetProjectDirectorSystemId()) || budgetDocument.getBudget().isProjectDirectorToBeNamedIndicator()) {
            return true;
        }

        GlobalVariables.getErrorMap().putError("budget.budgetProjectDirectorSystemId", KeyConstants.ERROR_MISSING, new String[] { "Project Director" });

        return false;
    }

    /**
     * Checks whether budgetDocument has a valid Agency.
     * 
     * @param BudgetDocument budgetDocument
     * @return boolean True if the Agency is valid, false otherwise.
     */
    protected boolean isAgencyValid(BudgetDocument budgetDocument) {

        if (!StringUtils.isBlank(budgetDocument.getBudget().getBudgetAgencyNumber()) || budgetDocument.getBudget().isAgencyToBeNamedIndicator()) {
            return true;
        }

        GlobalVariables.getErrorMap().putError("budget.budgetAgencyNumber", KeyConstants.ERROR_MISSING, new String[] { "Agency" });

        return false;
    }

    /**
     * Checks whether budgetPeriod is valid - start & end date are in proper order, period is not too long.
     * 
     * @param BudgetPeriod budgetPeriod
     * @param String periodLabel The label identifying the period you want printed in the error message.
     * @param Integer periodNumber The budgetPeriod's sequence number.
     * @return boolean True if the budgetPeriod is valid, false otherwise.
     */
    protected boolean isPeriodValid(BudgetPeriod budgetPeriod, String periodLabel, Integer periodNumber) {
        boolean valid = true;

        if (budgetPeriod.getBudgetPeriodBeginDate() != null && budgetPeriod.getBudgetPeriodEndDate() != null) {
            String MAXIMUM_PERIOD_LENGTH = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue("KraDevelopmentGroup", "maximumPeriodLength");

            valid &= isPeriodDatesOrderValid(budgetPeriod.getBudgetPeriodBeginDate(), budgetPeriod.getBudgetPeriodEndDate(), periodLabel, "budget.period.invalidOrder_" + periodNumber);

            if (valid) {
                valid &= isPeriodLengthValid(budgetPeriod.getBudgetPeriodBeginDate(), budgetPeriod.getBudgetPeriodEndDate(), KraConstants.maximumPeriodLengthUnits, Integer.parseInt(MAXIMUM_PERIOD_LENGTH), periodLabel, "budget.period.invalidLength_" + periodNumber);
            }
        }

        return valid;
    }

    /**
     * Checks whether number of periods is valid - not too long or too short.
     * 
     * @param List periods
     * @return boolean True if the list is valid, false otherwise.
     */
    protected boolean isNumPeriodsValid(List periods, boolean modularBudget) {
        String MINIMUM_NUMBER_OF_PERIODS = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue("KraDevelopmentGroup", "minimumNumberOfPeriods");

        /** TODO Application Constants */
        if (periods.size() > KraConstants.maximumNumberOfPeriods) {
            GlobalVariables.getErrorMap().putError("budget.period.tooMany", KeyConstants.ERROR_TOO_MANY, new String[] { Integer.toString(KraConstants.maximumNumberOfPeriods), "period" });
            return false;
        }
        else if (modularBudget) {
            /** TODO Application Constants */
            int MAXIMUM_NUMBER_OF_MODULAR_PERIODS = 5;
            if (periods.size() > MAXIMUM_NUMBER_OF_MODULAR_PERIODS) {
                GlobalVariables.getErrorMap().putError("budget.period.modularTooMany", KeyConstants.ERROR_MODULAR_TOO_MANY, new String[] { Integer.toString(MAXIMUM_NUMBER_OF_MODULAR_PERIODS), "period" });
            }
        }

        if (periods.size() < Integer.parseInt(MINIMUM_NUMBER_OF_PERIODS)) {
            GlobalVariables.getErrorMap().putError("budget.period.notEnough", KeyConstants.ERROR_NOT_ENOUGH, new String[] { MINIMUM_NUMBER_OF_PERIODS, "period" });
            return false;
        }

        return true;
    }

    /**
     * Checks whether end date of one period & start date of next are consecutive.
     * 
     * @param Date formerPeriodEndDate
     * @param Date latterPeriodStartDate
     * @param String formerPeriodIdentifier
     * @param String latterPeriodIdentifier
     * @param String errorKey
     * @return boolean True if the dates are consecutive, false otherwise.
     */
    protected boolean isConsecutivePeriods(Date formerPeriodEndDate, Date latterPeriodStartDate, String formerPeriodIdentifier, String latterPeriodIdentifier, String errorKey) {

        if (formerPeriodEndDate == null || latterPeriodStartDate == null) {
            return true;
        }

        GregorianCalendar formerPeriodEndPlusOneDay = new GregorianCalendar();
        formerPeriodEndPlusOneDay.setTime(formerPeriodEndDate);
        formerPeriodEndPlusOneDay.add(Calendar.DATE, 1);

        GregorianCalendar latterPeriodStart = new GregorianCalendar();
        latterPeriodStart.setTime(latterPeriodStartDate);

        if (!formerPeriodEndPlusOneDay.equals(latterPeriodStart)) {
            GlobalVariables.getErrorMap().putError(errorKey, KeyConstants.ERROR_NONCONSECUTIVE, new String[] { formerPeriodIdentifier, latterPeriodIdentifier });
            return false;
        }

        return true;
    }

    /**
     * Checks whether startDate is before or equal to endDate.
     * 
     * @param Date startDate
     * @param Date endDate
     * @param String periodIdentifier
     * @param String errorKey
     * @return boolean True if the order is valid, false otherwise.
     */
    protected boolean isPeriodDatesOrderValid(Date startDate, Date endDate, String periodIdentifier, String errorKey) {
        if (startDate != null && endDate != null && startDate.compareTo(endDate) > 0) {
            GlobalVariables.getErrorMap().putError(errorKey, KeyConstants.ERROR_INVALID_ORDERING, new String[] { periodIdentifier });
            return false;
        }
        return true;
    }

    /**
     * Checks whether time period between startDate and endDate is within acceptable limit.
     * 
     * @param Date startDate
     * @param Date endDate
     * @param int validPeriodLengthUnits
     * @param int validPeriodLength
     * @param String periodIdentifier
     * @param String errorKey
     * @return boolean True if the length is valid, false otherwise.
     */
    protected boolean isPeriodLengthValid(Date startDate, Date endDate, int validPeriodLengthUnits, int validPeriodLength, String periodIdentifier, String errorKey) {
        try {
            GregorianCalendar startDatePlusMaximumPeriodLength = new GregorianCalendar();
            startDatePlusMaximumPeriodLength.setTime(startDate);
            startDatePlusMaximumPeriodLength.add(validPeriodLengthUnits, validPeriodLength);

            GregorianCalendar endCal = new GregorianCalendar();
            endCal.setTime(endDate);

            if (!endCal.before(startDatePlusMaximumPeriodLength)) {
                GlobalVariables.getErrorMap().putError(errorKey, KeyConstants.ERROR_INVALID_PERIOD_LENGTH, new String[] { periodIdentifier });
                return false;
            }

            return true;
        }
        catch (NullPointerException e) {
            return true;
        }
    }

    /**
     * Checks whether budgetTaskList is valid - not too big or small.
     * 
     * @param List budgetTaskList
     * @return boolean True if the list is valid, false otherwise.
     */
    protected boolean isTaskListValid(List budgetTaskList) {
        String MINIMUM_NUMBER_OF_TASKS = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue("KraDevelopmentGroup", "minimumNumberOfTasks");

        if (budgetTaskList.size() > KraConstants.maximumNumberOfTasks) {
            String[] tooManyTasksError = new String[] { Integer.toString(KraConstants.maximumNumberOfTasks), "task" };
            GlobalVariables.getErrorMap().putError("budget.tasks.tooMany", KeyConstants.ERROR_TOO_MANY, tooManyTasksError);
            return false;
        }

        if (budgetTaskList.size() < new Integer(MINIMUM_NUMBER_OF_TASKS).intValue()) {
            String[] notEnoughTasksError = new String[] { MINIMUM_NUMBER_OF_TASKS, "task" };
            GlobalVariables.getErrorMap().putError("budget.tasks.notEnough", KeyConstants.ERROR_NOT_ENOUGH, notEnoughTasksError);
            return false;
        }
        return true;
    }

    /**
     * Checks whether fringeRateList is valid.
     * 
     * @param Document budgetDocument
     * @return boolean True if the list is valid, false otherwise.
     */
    protected boolean isFringeRateListValid(BudgetDocument budgetDocument) {
        List fringeRateList = budgetDocument.getBudget().getFringeRates();
        boolean valid = true;
        boolean isRateChanged = false;
        for (int i = 0; i < fringeRateList.size(); i++) {
            // get the current budgetFringeRate object from the list collection
            BudgetFringeRate budgetFringeRate = (BudgetFringeRate) fringeRateList.get(i);

            // extract the fringe rate from the budgetFringeRate object
            KualiDecimal contractsAndGrantsFringeRate = budgetFringeRate.getContractsAndGrantsFringeRateAmount();

            // extract the university cost share object from the budgetFringeRate object
            KualiDecimal universityCostShare = budgetFringeRate.getUniversityCostShareFringeRateAmount();

            // check to see if the system value is different than the user input value
            if ((contractsAndGrantsFringeRate != null && budgetFringeRate.getAppointmentTypeFringeRateAmount().compareTo(contractsAndGrantsFringeRate) != 0) || (universityCostShare != null && budgetFringeRate.getAppointmentTypeCostShareFringeRateAmount().compareTo(universityCostShare) != 0)) {
                isRateChanged = (isRateChanged | true);
            }
        }

        // get the Rate Change Justification
        String fringeRateChange = budgetDocument.getBudget().getBudgetFringeRateDescription();
        // if there is a rate change and the justification note is not filled in then display an error
        if (isRateChanged && fringeRateChange == null) {
            GlobalVariables.getErrorMap().putError("budget.fringeRate", KeyConstants.ERROR_FRINGE_RATE_CHANGE_JUSTIFICATION_REQUIRED, new String[] {});
            valid = false;
        }
        return valid;
    }


    /**
     * Checks whether graduateAssistantRateList is valid - not too big or small.
     * 
     * @param BudgetDocument budgetDocument
     * @return boolean True if the list is valid, false otherwise.
     */
    protected boolean isGraduateAssistantRateListValid(BudgetDocument budgetDocument) {
        List graduateAssistantRateList = budgetDocument.getBudget().getGraduateAssistantRates();
        int numberOfAcademicYearSubdivisions = Integer.parseInt(SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue("KraDevelopmentGroup", "KraBudgetNumberOfAcademicYearSubdivisions"));
        String[] academicYearSubdivisionNames = null;
        boolean rateChanged = false;
        boolean valid = true;

        for (int i = 0; i < graduateAssistantRateList.size(); i++) {
            // get the current graduate rate object from the list collection
            BudgetGraduateAssistantRate budgetGraduateAssistantRate = (BudgetGraduateAssistantRate) graduateAssistantRateList.get(i);

            for (int anAcademicYearSubdivisionIndex = 1; anAcademicYearSubdivisionIndex <= numberOfAcademicYearSubdivisions; anAcademicYearSubdivisionIndex++) {
                KualiDecimal rateForTesting = budgetGraduateAssistantRate.getCampusMaximumPeriodRate(anAcademicYearSubdivisionIndex);
                KualiDecimal systemRateForComparison = budgetGraduateAssistantRate.getGraduateAssistantRate().getCampusMaximumPeriodRate(anAcademicYearSubdivisionIndex);

                if (rateForTesting != null) {
                    if (!SpringServiceLocator.getBudgetGraduateAssistantRateService().isValidGraduateAssistantRate(rateForTesting)) {
                        if (academicYearSubdivisionNames == null)
                            academicYearSubdivisionNames = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValues("KraDevelopmentGroup", "KraBudgetAcademicYearSubdivisionNames");
                        String[] graduateAssistantRateErrorMessage = { academicYearSubdivisionNames[anAcademicYearSubdivisionIndex - 1], budgetGraduateAssistantRate.getCampusCode() };
                        GlobalVariables.getErrorMap().putError("budget.graduateAssistantRate[" + i + "].campusMaximumPeriod" + anAcademicYearSubdivisionIndex + "Rate", KeyConstants.ERROR_GRAD_RATE_TOO_HIGH, graduateAssistantRateErrorMessage);
                        valid = false;
                    }


                    if (systemRateForComparison != null) {
                        rateChanged = rateChanged || rateForTesting.compareTo(systemRateForComparison) != 0;
                    }
                }
                else {
                    if (academicYearSubdivisionNames == null)
                        academicYearSubdivisionNames = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValues("KraDevelopmentGroup", "KraBudgetAcademicYearSubdivisionNames");
                    String[] graduateAssistantRateErrorMessage = { budgetGraduateAssistantRate.getCampusCode() + " " + academicYearSubdivisionNames[anAcademicYearSubdivisionIndex - 1] + " Current Rate" };
                    GlobalVariables.getErrorMap().putError("budget.graduateAssistantRate[" + i + "].campusMaximumPeriod" + anAcademicYearSubdivisionIndex + "Rate", KeyConstants.ERROR_REQUIRED, graduateAssistantRateErrorMessage);
                    valid = false;
                }
            }
        }

        // get the Rate Change Justification
        String gradFringeRateChange = budgetDocument.getBudget().getBudgetFringeRateDescription();
        // if there is a rate change and the justification note is not filled in then display an error
        if (rateChanged && gradFringeRateChange == null) {
            GlobalVariables.getErrorMap().putError("budget.grad", KeyConstants.ERROR_GRAD_RATE_CHANGE_JUSTIFICATION_REQUIRED, new String[] {});
            valid = false;
        }
        return valid;
    }
}

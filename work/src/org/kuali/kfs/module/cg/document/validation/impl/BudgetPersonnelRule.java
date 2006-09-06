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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.KeyConstants;
import org.kuali.core.document.Document;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.kra.budget.KraConstants;
import org.kuali.module.kra.budget.KraKeyConstants;
import org.kuali.module.kra.budget.bo.Budget;
import org.kuali.module.kra.budget.bo.BudgetUser;
import org.kuali.module.kra.budget.bo.UserAppointmentTask;
import org.kuali.module.kra.budget.bo.UserAppointmentTaskPeriod;
import org.kuali.module.kra.budget.document.BudgetDocument;
import org.kuali.module.kra.budget.util.AuditCluster;
import org.kuali.module.kra.budget.util.AuditError;

public class BudgetPersonnelRule {

    protected boolean runPersonnelAuditErrors(List personnel) {
        List<AuditError> personnelAuditErrors = new ArrayList<AuditError>();
        String INVALID_STATUSES = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue("KraDevelopmentGroup", "invalidPersonnelStatuses");
        for (Iterator iter = personnel.iterator(); iter.hasNext();) {
            BudgetUser person = (BudgetUser) iter.next();
            if (ObjectUtils.isNotNull(person.getUser()) && person.getUser().getEmployeeStatusCode() != null && StringUtils.contains(INVALID_STATUSES, person.getUser().getEmployeeStatusCode())) {
                personnelAuditErrors.add(new AuditError("document.budget.audit.personnel." + person.getPersonUniversalIdentifier() + ".status", KraKeyConstants.AUDIT_PERSONNEL_STATUS, "personnel", new String[] { person.getUser().getPersonName(), person.getUser().getEmployeeStatusCode() }));
                if (person.isPersonProjectDirectorIndicator()) {
                    List<AuditError> parametersAuditErrors = new ArrayList<AuditError>();
                    parametersAuditErrors.add(new AuditError("document.budget.audit.parameters.pd.status", KraKeyConstants.AUDIT_PERSONNEL_STATUS, "parameters", new String[] { person.getUser().getPersonName(), person.getUser().getEmployeeStatusCode() }));
                    GlobalVariables.getAuditErrorMap().put("parametersAuditErrors", new AuditCluster("Parameters", parametersAuditErrors));
                }
            }
        }
        if (!personnelAuditErrors.isEmpty()) {
            GlobalVariables.getAuditErrorMap().put("personnelAuditErrors", new AuditCluster("Personnel", personnelAuditErrors));
            return false;
        }
        return true;
    }
    
    protected boolean isPersonnelListValid(Budget budget) {
        boolean valid = true;

        HashMap appointmentTypeMappings = SpringServiceLocator.getBudgetPersonnelService().getAppointmentTypeMappings();

        valid &= verifyPersonnelEffortAmounts(budget, false, appointmentTypeMappings);
        valid &= verifySummerAppointmentLength(budget, appointmentTypeMappings);
        valid &= verifyPersonnelJustificationText(budget);

        return valid;
    }

    protected boolean processUpdatePersonnelBusinessRules(Document document) {
        boolean valid = true;

        GlobalVariables.getErrorMap().addToErrorPath("document");

        BudgetDocument budgetDocument = (BudgetDocument) document;
        Budget budget = budgetDocument.getBudget();

        HashMap appointmentTypeMappings = SpringServiceLocator.getBudgetPersonnelService().getAppointmentTypeMappings();

        valid &= verifySummerAppointmentLength(budget, appointmentTypeMappings);
        valid &= verifyPersonnelEffortAmounts(budget, true, appointmentTypeMappings);
        valid &= verifyRequiredFields(document);

        GlobalVariables.getErrorMap().removeFromErrorPath("document");

        return valid;
    }

    protected boolean processSavePersonnelBusinessRules(List personnel) {
        boolean valid = true;

        GlobalVariables.getErrorMap().addToErrorPath("document");

        valid &= verifyPersonnelChartOrg(personnel);
        valid &= verifyGradAssistantFeeRemission(personnel);

        GlobalVariables.getErrorMap().removeFromErrorPath("document");

        return valid;
    }

    private boolean verifyPersonnelChartOrg(List personnelList) {
        boolean valid = true;

        int personnelListIndex = 0;

        for (Iterator budgetUserIter = personnelList.iterator(); budgetUserIter.hasNext(); personnelListIndex++) {
            BudgetUser budgetUser = (BudgetUser) budgetUserIter.next();

            GlobalVariables.getErrorMap().addToErrorPath("budget.personFromList[" + personnelListIndex + "]");

            if (!StringUtils.isNotBlank(budgetUser.getFiscalCampusCode()) && !StringUtils.isNotBlank(budgetUser.getPrimaryDepartmentCode())) {
                GlobalVariables.getErrorMap().putError("chartOrg", KeyConstants.ERROR_MISSING, new String[] { "Chart/Org" });
                valid = false;
            }

            GlobalVariables.getErrorMap().removeFromErrorPath("budget.personFromList[" + personnelListIndex + "]");
        }

        return valid;
    }

    private boolean verifyGradAssistantFeeRemission(List personnelList) {
        boolean valid = true;

        int personnelListIndex = 0;

        for (Iterator budgetUserIter = personnelList.iterator(); budgetUserIter.hasNext(); personnelListIndex++) {
            BudgetUser budgetUser = (BudgetUser) budgetUserIter.next();

            GlobalVariables.getErrorMap().addToErrorPath("budget.personFromList[" + personnelListIndex + "]");

            int userAppointmentTaskListIndex = 0;

            for (Iterator userAppointmentTaskIter = budgetUser.getUserAppointmentTasks().iterator(); userAppointmentTaskIter.hasNext(); userAppointmentTaskListIndex++) {
                int userAppointmentTaskPeriodIndex = 0;

                for (Iterator userAppointmentTaskPeriodIter = ((UserAppointmentTask) userAppointmentTaskIter.next()).getUserAppointmentTaskPeriods().iterator(); userAppointmentTaskPeriodIter.hasNext(); userAppointmentTaskPeriodIndex++) {

                    GlobalVariables.getErrorMap().addToErrorPath("userAppointmentTask[" + userAppointmentTaskListIndex + "].userAppointmentTaskPeriod[" + userAppointmentTaskPeriodIndex + "]");

                    UserAppointmentTaskPeriod userAppointmentTaskPeriod = (UserAppointmentTaskPeriod) userAppointmentTaskPeriodIter.next();

                    if (userAppointmentTaskPeriod.getTotalFeeRemissionsAmount().compareTo(userAppointmentTaskPeriod.getAgencyRequestedFeesAmount().add(userAppointmentTaskPeriod.getUniversityRequestedFeesAmount())) != 0) {
                        GlobalVariables.getErrorMap().putError("feeRemissions", KeyConstants.ERROR_FEE_REMISSION_DISTRIBUTION);
                        valid = false;
                    }

                    GlobalVariables.getErrorMap().removeFromErrorPath("userAppointmentTask[" + userAppointmentTaskListIndex + "].userAppointmentTaskPeriod[" + userAppointmentTaskPeriodIndex + "]");
                }
            }

            GlobalVariables.getErrorMap().removeFromErrorPath("budget.personFromList[" + personnelListIndex + "]");
        }

        return valid;
    }

    protected boolean processInsertPersonnelBusinessRules(List personnelList, BudgetUser newBudgetUser) {
        boolean valid = true;

        if (StringUtils.isNotEmpty(newBudgetUser.getPersonUniversalIdentifier())) {
            for (Iterator budgetUserIter = personnelList.iterator(); budgetUserIter.hasNext();) {
                if (StringUtils.equals(newBudgetUser.getPersonUniversalIdentifier(), ((BudgetUser) budgetUserIter.next()).getPersonUniversalIdentifier())) {
                    GlobalVariables.getErrorMap().putError("newPersonnel", KeyConstants.ERROR_PERSON_ALREADY_EXISTS_ON_BUDGET, new String[] {});
                    valid = false;
                    break;
                }
            }
        }
        return valid;
    }

    private boolean verifySummerAppointmentLength(Budget budget, HashMap appointmentTypeMappings) {
        boolean valid = true;

        int personnelListIndex = 0;

        for (Iterator personnelIter = budget.getPersonnel().iterator(); personnelIter.hasNext(); personnelListIndex++) {
            BudgetUser budgetUser = (BudgetUser) personnelIter.next();

            GlobalVariables.getErrorMap().addToErrorPath("budget.personFromList[" + personnelListIndex + "]");

            int userAppointmentTaskListIndex = 0;

            for (Iterator userAppointmentTaskIter = budgetUser.getUserAppointmentTasks().iterator(); userAppointmentTaskIter.hasNext(); userAppointmentTaskListIndex++) {
                int userAppointmentTaskPeriodIndex = 0;

                for (Iterator userAppointmentTaskPeriodIter = ((UserAppointmentTask) userAppointmentTaskIter.next()).getUserAppointmentTaskPeriods().iterator(); userAppointmentTaskPeriodIter.hasNext(); userAppointmentTaskPeriodIndex++) {

                    GlobalVariables.getErrorMap().addToErrorPath("userAppointmentTask[" + userAppointmentTaskListIndex + "].userAppointmentTaskPeriod[" + userAppointmentTaskPeriodIndex + "]");

                    UserAppointmentTaskPeriod userAppointmentTaskPeriod = (UserAppointmentTaskPeriod) userAppointmentTaskPeriodIter.next();
                    if (StringUtils.equals(userAppointmentTaskPeriod.getUniversityAppointmentTypeCode(), appointmentTypeMappings.get("academicSummer").toString())) {
                        if (userAppointmentTaskPeriod.getPersonWeeksAmount().compareTo(new Integer(13)) == 1) {
                            GlobalVariables.getErrorMap().putError("personWeeksAmount", KeyConstants.ERROR_PERSONNEL_SUMMER_WEEKS_TOO_MUCH, new String[] { "Period " + (userAppointmentTaskPeriodIndex + 1), "13" });
                            valid = false;
                        }
                    }

                    GlobalVariables.getErrorMap().removeFromErrorPath("userAppointmentTask[" + userAppointmentTaskListIndex + "].userAppointmentTaskPeriod[" + userAppointmentTaskPeriodIndex + "]");
                }
            }

            GlobalVariables.getErrorMap().removeFromErrorPath("budget.personFromList[" + personnelListIndex + "]");
        }
        return valid;
    }

    private boolean verifyPersonnelJustificationText(Budget budget) {
        boolean valid = true;

        int personnelListIndex = 0;

        for (Iterator personnelIter = budget.getPersonnel().iterator(); personnelIter.hasNext(); personnelListIndex++) {
            BudgetUser budgetUser = (BudgetUser) personnelIter.next();

            GlobalVariables.getErrorMap().addToErrorPath("budget.personFromList[" + personnelListIndex + "]");

            budgetUser.refreshReferenceObject("user");
            // salary justification required for changes to personnel salary
            if (budgetUser.getUser() != null && budgetUser.getUser().getPersonBaseSalaryAmount() != null && budgetUser.getBaseSalary() != null && !budgetUser.getBaseSalary().equals(budgetUser.getUser().getPersonBaseSalaryAmount()) && StringUtils.isEmpty(budgetUser.getPersonSalaryJustificationText())) {
                GlobalVariables.getErrorMap().putError("personSalaryJustificationText", KeyConstants.ERROR_PERSONNEL_SALARY_CHANGE_JUSTIFICATION_REQUIRED, new String[] {});
                valid = false;
            }

            GlobalVariables.getErrorMap().removeFromErrorPath("budget.personFromList[" + personnelListIndex + "]");
        }
        return valid;
    }

    private boolean verifyPersonnelEffortAmounts(Budget budget, boolean verifyOnlyIfAppointmentTypeChanged, HashMap appointmentTypeMappings) {
        boolean valid = true;

        int personnelListIndex = 0;

        for (Iterator personnelIter = budget.getPersonnel().iterator(); personnelIter.hasNext(); personnelListIndex++) {
            BudgetUser budgetUser = (BudgetUser) personnelIter.next();
            if (!verifyOnlyIfAppointmentTypeChanged || (verifyOnlyIfAppointmentTypeChanged && !StringUtils.equals(budgetUser.getAppointmentTypeCode(), budgetUser.getPreviousAppointmentTypeCode()))) {
                GlobalVariables.getErrorMap().addToErrorPath("budget.personFromList[" + personnelListIndex + "]");

                // person cannot have more than 100% effort per period across tasks (except for summers); summer cannot have more
                // than X number of weeks of effort
                SortedMap periodEffortMap = new TreeMap();
                SortedMap summerPeriodEffortMap = new TreeMap();

                int userAppointmentTaskListIndex = 0;

                for (Iterator userAppointmentTaskIter = budgetUser.getUserAppointmentTasks().iterator(); userAppointmentTaskIter.hasNext(); userAppointmentTaskListIndex++) {
                    int userAppointmentTaskPeriodIndex = 0;

                    for (Iterator userAppointmentTaskPeriodIter = ((UserAppointmentTask) userAppointmentTaskIter.next()).getUserAppointmentTaskPeriods().iterator(); userAppointmentTaskPeriodIter.hasNext(); userAppointmentTaskPeriodIndex++) {
                        Integer periodNumber = new Integer(userAppointmentTaskPeriodIndex + 1);

                        UserAppointmentTaskPeriod userAppointmentTaskPeriod = (UserAppointmentTaskPeriod) userAppointmentTaskPeriodIter.next();
                        if (!StringUtils.equals(userAppointmentTaskPeriod.getUniversityAppointmentTypeCode(), appointmentTypeMappings.get("academicSummer").toString()) &&
                                !StringUtils.contains(appointmentTypeMappings.get("gradResAssistant").toString(), userAppointmentTaskPeriod.getUniversityAppointmentTypeCode()) &&
                                !StringUtils.contains(appointmentTypeMappings.get("hourly").toString(), userAppointmentTaskPeriod.getUniversityAppointmentTypeCode())) {
                            if (periodEffortMap.get(periodNumber) == null) {
                                periodEffortMap.put(periodNumber, userAppointmentTaskPeriod.getAgencyPercentEffortAmount().add(userAppointmentTaskPeriod.getUniversityCostSharePercentEffortAmount()));
                            }
                            else {
                                periodEffortMap.put(periodNumber, ((KualiInteger) periodEffortMap.get(periodNumber)).add(userAppointmentTaskPeriod.getAgencyPercentEffortAmount().add(userAppointmentTaskPeriod.getUniversityCostSharePercentEffortAmount())));
                            }
                        }
                        else if (StringUtils.equals(userAppointmentTaskPeriod.getUniversityAppointmentTypeCode(), appointmentTypeMappings.get("academicSummer").toString())) {
                            if (summerPeriodEffortMap.get(periodNumber) == null) {
                                summerPeriodEffortMap.put(periodNumber, userAppointmentTaskPeriod.getAgencyPercentEffortAmount().add(userAppointmentTaskPeriod.getUniversityCostSharePercentEffortAmount()));
                            }
                            else {
                                summerPeriodEffortMap.put(periodNumber, ((KualiInteger) summerPeriodEffortMap.get(periodNumber)).add(userAppointmentTaskPeriod.getAgencyPercentEffortAmount().add(userAppointmentTaskPeriod.getUniversityCostSharePercentEffortAmount())));
                            }
                        }
                    }
                }

                for (Iterator periodSalaryIter = periodEffortMap.keySet().iterator(); periodSalaryIter.hasNext();) {
                    Integer periodNumber = (Integer) periodSalaryIter.next();
                    KualiInteger periodEffort = (KualiInteger) periodEffortMap.get(periodNumber);
                    if (periodEffort.compareTo(new KualiInteger(100)) == 1) {
                        GlobalVariables.getErrorMap().putError("periodEffort.tooMuch", KeyConstants.ERROR_PERSONNEL_PERIOD_EFFORT_TOO_MUCH, new String[] { "Period " + periodNumber, periodEffort.toString() });
                        valid = false;
                    }
                }

                for (Iterator summerPeriodEffortIter = summerPeriodEffortMap.keySet().iterator(); summerPeriodEffortIter.hasNext();) {
                    Integer periodNumber = (Integer) summerPeriodEffortIter.next();
                    KualiInteger periodEffort = (KualiInteger) summerPeriodEffortMap.get(periodNumber);
                    if (periodEffort.compareTo(new KualiInteger(100)) == 1) {
                        GlobalVariables.getErrorMap().putError("periodEffort.tooMuch", KeyConstants.ERROR_PERSONNEL_PERIOD_EFFORT_TOO_MUCH_SUMMER, new String[] { "Period " + periodNumber, periodEffort.toString() });
                        valid = false;
                    }
                }
            }
            GlobalVariables.getErrorMap().removeFromErrorPath("budget.personFromList[" + personnelListIndex + "]");
        }
        return valid;
    }

    
    protected boolean verifyRequiredFields(Document document) {
        Budget budget = ((BudgetDocument)document).getBudget();
            
        HashMap appointmentTypeMappings = SpringServiceLocator.getBudgetPersonnelService().getAppointmentTypeMappings();
        
        boolean valid = true;

        GlobalVariables.getErrorMap().addToErrorPath("document.budget");
        
        int personIndex = 0;
        for (BudgetUser budgetUser : budget.getPersonnel()) {
            GlobalVariables.getErrorMap().addToErrorPath("personFromList[" + personIndex + "]");
            int task = 0;
            for (UserAppointmentTask userAppointmentTask : budgetUser.getUserAppointmentTasks()) {
                GlobalVariables.getErrorMap().addToErrorPath("userAppointmentTask[" + task + "]");
                int period = 0;
                for (UserAppointmentTaskPeriod userAppointmentTaskPeriod : userAppointmentTask.getUserAppointmentTaskPeriods()) {
                    GlobalVariables.getErrorMap().addToErrorPath("userAppointmentTaskPeriod[" + period + "]");
                    
                    if (StringUtils.contains(appointmentTypeMappings.get(KraConstants.FULL_YEAR).toString(), userAppointmentTask.getUniversityAppointmentTypeCode())) { //Salary
                        if (userAppointmentTaskPeriod.getAgencyPercentEffortAmount() == null) {
                            GlobalVariables.getErrorMap().putError("agencyPercentEffortAmount", KeyConstants.ERROR_REQUIRED, "Agency Percent Effort Amount");
                            valid = false;
                        }
                        
                        if (userAppointmentTaskPeriod.getUniversityCostSharePercentEffortAmount() == null) {
                            GlobalVariables.getErrorMap().putError("universityCostSharePercentEffortAmount", KeyConstants.ERROR_REQUIRED, "Institution Cost Share Percent Effort Amount");
                            valid = false;
                        }
                        
                    } else if (StringUtils.contains(appointmentTypeMappings.get(KraConstants.ACADEMIC_YEAR_SUMMER).toString(), userAppointmentTask.getUniversityAppointmentTypeCode())) { //Academic 9/Summer
                        if (userAppointmentTaskPeriod.getPersonWeeksAmount() == null) {
                            GlobalVariables.getErrorMap().putError("personWeeksAmount", KeyConstants.ERROR_REQUIRED, "Number of Weeks");
                            valid = false;
                        }
                        
                        if (userAppointmentTaskPeriod.getAgencyPercentEffortAmount() == null) {
                            GlobalVariables.getErrorMap().putError("agencyPercentEffortAmount", KeyConstants.ERROR_REQUIRED, "Agency Percent Effort Amount");
                            valid = false;
                        }
                        
                        if (userAppointmentTaskPeriod.getUniversityCostSharePercentEffortAmount() == null) {
                            GlobalVariables.getErrorMap().putError("universityCostSharePercentEffortAmount", KeyConstants.ERROR_REQUIRED, "Institution Cost Share Percent Effort Amount");
                            valid = false;
                        }
                    } else if (StringUtils.contains(appointmentTypeMappings.get(KraConstants.HOURLY).toString(), userAppointmentTask.getUniversityAppointmentTypeCode())) { //Hourly
                        if (userAppointmentTaskPeriod.getUserHourlyRate() == null) {
                            GlobalVariables.getErrorMap().putError("userHourlyRate", KeyConstants.ERROR_REQUIRED, "Hourly Rate");
                            valid = false;
                        }
                        
                        if (userAppointmentTaskPeriod.getUserAgencyHours() == null) {
                            GlobalVariables.getErrorMap().putError("userAgencyHours", KeyConstants.ERROR_REQUIRED, "Agency Request Number of Hours");
                            valid = false;
                        }
                        
                        if (userAppointmentTaskPeriod.getUserUniversityHours() == null) {
                            GlobalVariables.getErrorMap().putError("userUniversityHours", KeyConstants.ERROR_REQUIRED, "Institution Request Number of Hours");
                            valid = false;
                        }
                    } else if(StringUtils.contains(appointmentTypeMappings.get(KraConstants.GRADUATE_ASSISTANT).toString(), userAppointmentTask.getUniversityAppointmentTypeCode())) { //Grad Asst
                        if (userAppointmentTaskPeriod.getAgencyFullTimeEquivalentPercent() == null) {
                            GlobalVariables.getErrorMap().putError("agencyFullTimeEquivalentPercent", KeyConstants.ERROR_REQUIRED, "Agency Percent Effort");
                            valid = false;
                        }
                        
                        if (userAppointmentTaskPeriod.getAgencySalaryAmount() == null) {
                            GlobalVariables.getErrorMap().putError("agencySalaryAmount", KeyConstants.ERROR_REQUIRED, "Agency Salary Amount");
                            valid = false;
                        }
                        
                        if (userAppointmentTaskPeriod.getAgencyHealthInsuranceAmount() == null) {
                            GlobalVariables.getErrorMap().putError("agencyHealthInsuranceAmount", KeyConstants.ERROR_REQUIRED, "Agency Health Insurance Amount");
                            valid = false;
                        }
                        
                        if (userAppointmentTaskPeriod.getUserCreditHoursNumber() == null) {
                            GlobalVariables.getErrorMap().putError("userCreditHoursNumber", KeyConstants.ERROR_REQUIRED, "Number of Credit Hours");
                            valid = false;
                        }
                        
                        if (userAppointmentTaskPeriod.getUserCreditHourAmount() == null) {
                            GlobalVariables.getErrorMap().putError("userCreditHourAmount", KeyConstants.ERROR_REQUIRED, "Amount Per Credit Hour");
                            valid = false;
                        }
                        
                        if (userAppointmentTaskPeriod.getUserMiscellaneousFeeAmount() == null) {
                            GlobalVariables.getErrorMap().putError("userMiscellaneousFeesAmount", KeyConstants.ERROR_REQUIRED, "Miscellaneous Fees Amount");
                            valid = false;
                        }
                        
                        if (userAppointmentTaskPeriod.getAgencyRequestedFeesAmount() == null) {
                            GlobalVariables.getErrorMap().putError("agencyRequestedFeesAmount", KeyConstants.ERROR_REQUIRED, "Agency Requested Fees");
                            valid = false;
                        }
                        
                        if (userAppointmentTaskPeriod.getUniversityRequestedFeesAmount() == null) {
                            GlobalVariables.getErrorMap().putError("universityRequestedFeesAmount", KeyConstants.ERROR_REQUIRED, "Institution Requested Fees");
                            valid = false;
                        }
                        
                        if (userAppointmentTaskPeriod.getUniversityFullTimeEquivalentPercent() == null) {
                            GlobalVariables.getErrorMap().putError("universityFullTimeEquivalentPercent", KeyConstants.ERROR_REQUIRED, "Institution Percent Effort");
                            valid = false;
                        }

                        if (userAppointmentTaskPeriod.getUniversitySalaryAmount() == null) {
                            GlobalVariables.getErrorMap().putError("universitySalaryAmount", KeyConstants.ERROR_REQUIRED, "Institution Salary Amount");
                            valid = false;
                        }
                        
                        if (userAppointmentTaskPeriod.getUniversityHealthInsuranceAmount() == null) {
                            GlobalVariables.getErrorMap().putError("universityHealthInsuranceAmount", KeyConstants.ERROR_REQUIRED, "Institution Health Insurance Amount");
                            valid = false;
                        }
                        
                    } else {
                        //Some unrecognized type
                    }
                    
                    
                    GlobalVariables.getErrorMap().removeFromErrorPath("userAppointmentTaskPeriod[" + period + "]");
                    period++;
                }
                GlobalVariables.getErrorMap().removeFromErrorPath("userAppointmentTask[" + task + "]");
                task++;
            }
            GlobalVariables.getErrorMap().removeFromErrorPath("personFromList[" + personIndex + "]");
            personIndex++;
        }
        
        GlobalVariables.getErrorMap().removeFromErrorPath("document.budget");
        
        return valid;
    }
}

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
package org.kuali.module.kra.budget.rules.budget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.kra.KraConstants;
import org.kuali.module.kra.KraKeyConstants;
import org.kuali.module.kra.budget.bo.Budget;
import org.kuali.module.kra.budget.bo.BudgetUser;
import org.kuali.module.kra.budget.bo.UserAppointmentTask;
import org.kuali.module.kra.budget.bo.UserAppointmentTaskPeriod;
import org.kuali.module.kra.budget.document.BudgetDocument;
import org.kuali.module.kra.util.AuditCluster;
import org.kuali.module.kra.util.AuditError;

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
                GlobalVariables.getErrorMap().putError("chartOrg", KraKeyConstants.ERROR_MISSING, new String[] { "Chart/Org" });
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

                    if (userAppointmentTaskPeriod.getTotalFeeRemissionsAmount().compareTo(userAppointmentTaskPeriod.getAgencyRequestedFeesAmount().add(userAppointmentTaskPeriod.getInstitutionRequestedFeesAmount())) != 0) {
                        GlobalVariables.getErrorMap().putError("feeRemissions", KraKeyConstants.ERROR_FEE_REMISSION_DISTRIBUTION);
                        valid = false;
                    }

                    GlobalVariables.getErrorMap().removeFromErrorPath("userAppointmentTask[" + userAppointmentTaskListIndex + "].userAppointmentTaskPeriod[" + userAppointmentTaskPeriodIndex + "]");
                }
            }

            GlobalVariables.getErrorMap().removeFromErrorPath("budget.personFromList[" + personnelListIndex + "]");
        }

        return valid;
    }

    protected boolean processInsertPersonnelBusinessRules(List personnelList, BudgetUser newBudgetUser, boolean isToBeNamed) {
        boolean valid = true;

        if (StringUtils.isNotEmpty(newBudgetUser.getPersonUniversalIdentifier())) {
            for (Iterator budgetUserIter = personnelList.iterator(); budgetUserIter.hasNext();) {
                if (StringUtils.equals(newBudgetUser.getPersonUniversalIdentifier(), ((BudgetUser) budgetUserIter.next()).getPersonUniversalIdentifier())) {
                    GlobalVariables.getErrorMap().putError("newPersonnel", KraKeyConstants.ERROR_PERSON_ALREADY_EXISTS_ON_BUDGET, new String[] {});
                    valid = false;
                    break;
                }
            }
        } else if (!isToBeNamed) {
            GlobalVariables.getErrorMap().putError("newPersonnel", KraKeyConstants.ERROR_PERSON_NOT_SELECTED, new String[] {});
            valid = false;
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
                    if (StringUtils.equals(userAppointmentTaskPeriod.getInstitutionAppointmentTypeCode(), appointmentTypeMappings.get("academicSummer").toString())) {
                        if (userAppointmentTaskPeriod.getPersonWeeksAmount().compareTo(new Integer(13)) == 1) {
                            GlobalVariables.getErrorMap().putError("personWeeksAmount", KraKeyConstants.ERROR_PERSONNEL_SUMMER_WEEKS_TOO_MUCH, new String[] { "Period " + (userAppointmentTaskPeriodIndex + 1), "13" });
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
                GlobalVariables.getErrorMap().putError("personSalaryJustificationText", KraKeyConstants.ERROR_PERSONNEL_SALARY_CHANGE_JUSTIFICATION_REQUIRED, new String[] {});
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
                    UserAppointmentTask userAppointmentTask = (UserAppointmentTask) userAppointmentTaskIter.next();

                    GlobalVariables.getErrorMap().addToErrorPath("userAppointmentTask[" + userAppointmentTaskListIndex + "]");

                    int userAppointmentTaskPeriodIndex = 0;

                    for (Iterator userAppointmentTaskPeriodIter = userAppointmentTask.getUserAppointmentTaskPeriods().iterator(); userAppointmentTaskPeriodIter.hasNext(); userAppointmentTaskPeriodIndex++) {

                        Integer periodNumber = new Integer(userAppointmentTaskPeriodIndex + 1);

                        UserAppointmentTaskPeriod userAppointmentTaskPeriod = (UserAppointmentTaskPeriod) userAppointmentTaskPeriodIter.next();

                        valid &= SpringServiceLocator.getDictionaryValidationService().isBusinessObjectValid(userAppointmentTaskPeriod, "userAppointmentTaskPeriod[" + userAppointmentTaskPeriodIndex + "]");
                        
                        if (!StringUtils.equals(userAppointmentTaskPeriod.getInstitutionAppointmentTypeCode(), appointmentTypeMappings.get("academicSummer").toString()) &&
                                !StringUtils.contains(appointmentTypeMappings.get("gradResAssistant").toString(), userAppointmentTaskPeriod.getInstitutionAppointmentTypeCode()) &&
                                !StringUtils.contains(appointmentTypeMappings.get("hourly").toString(), userAppointmentTaskPeriod.getInstitutionAppointmentTypeCode())) {
                            if (periodEffortMap.get(periodNumber) == null) {
                                periodEffortMap.put(periodNumber, userAppointmentTaskPeriod.getAgencyPercentEffortAmount().add(userAppointmentTaskPeriod.getInstitutionCostSharePercentEffortAmount()));
                            }
                            else {
                                periodEffortMap.put(periodNumber, ((KualiInteger) periodEffortMap.get(periodNumber)).add(userAppointmentTaskPeriod.getAgencyPercentEffortAmount().add(userAppointmentTaskPeriod.getInstitutionCostSharePercentEffortAmount())));
                            }
                        }
                        else if (StringUtils.equals(userAppointmentTaskPeriod.getInstitutionAppointmentTypeCode(), appointmentTypeMappings.get("academicSummer").toString())) {
                            if (summerPeriodEffortMap.get(periodNumber) == null) {
                                summerPeriodEffortMap.put(periodNumber, userAppointmentTaskPeriod.getAgencyPercentEffortAmount().add(userAppointmentTaskPeriod.getInstitutionCostSharePercentEffortAmount()));
                            }
                            else {
                                summerPeriodEffortMap.put(periodNumber, ((KualiInteger) summerPeriodEffortMap.get(periodNumber)).add(userAppointmentTaskPeriod.getAgencyPercentEffortAmount().add(userAppointmentTaskPeriod.getInstitutionCostSharePercentEffortAmount())));
                            }
                        }
                    }
                    GlobalVariables.getErrorMap().removeFromErrorPath("userAppointmentTask[" + userAppointmentTaskListIndex + "]");
                }

                for (Iterator periodSalaryIter = periodEffortMap.keySet().iterator(); periodSalaryIter.hasNext();) {
                    Integer periodNumber = (Integer) periodSalaryIter.next();
                    KualiInteger periodEffort = (KualiInteger) periodEffortMap.get(periodNumber);
                    if (periodEffort.compareTo(new KualiInteger(100)) == 1) {
                        GlobalVariables.getErrorMap().putError("periodEffort.tooMuch", KraKeyConstants.ERROR_PERSONNEL_PERIOD_EFFORT_TOO_MUCH, new String[] { "Period " + periodNumber, periodEffort.toString() });
                        valid = false;
                    }
                }

                for (Iterator summerPeriodEffortIter = summerPeriodEffortMap.keySet().iterator(); summerPeriodEffortIter.hasNext();) {
                    Integer periodNumber = (Integer) summerPeriodEffortIter.next();
                    KualiInteger periodEffort = (KualiInteger) summerPeriodEffortMap.get(periodNumber);
                    if (periodEffort.compareTo(new KualiInteger(100)) == 1) {
                        GlobalVariables.getErrorMap().putError("periodEffort.tooMuch", KraKeyConstants.ERROR_PERSONNEL_PERIOD_EFFORT_TOO_MUCH_SUMMER, new String[] { "Period " + periodNumber, periodEffort.toString() });
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
        
        DataDictionaryService dataDictionaryService = SpringServiceLocator.getDataDictionaryService();
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
                    
                    if (StringUtils.contains(appointmentTypeMappings.get(KraConstants.FULL_YEAR).toString(), userAppointmentTask.getInstitutionAppointmentTypeCode())) { //Salary
                        if (userAppointmentTaskPeriod.getAgencyPercentEffortAmount() == null) {
                            GlobalVariables.getErrorMap().putError("agencyPercentEffortAmount", KFSKeyConstants.ERROR_REQUIRED, dataDictionaryService.getAttributeLabel(UserAppointmentTaskPeriod.class, "agencyPercentEffortAmount"));
                            valid = false;
                        }
                        
                        if (userAppointmentTaskPeriod.getInstitutionCostSharePercentEffortAmount() == null) {
                            GlobalVariables.getErrorMap().putError("institutionCostSharePercentEffortAmount", KFSKeyConstants.ERROR_REQUIRED, dataDictionaryService.getAttributeLabel(UserAppointmentTaskPeriod.class, "institutionCostSharePercentEffortAmount"));
                            valid = false;
                        }
                        
                    } else if (StringUtils.contains(appointmentTypeMappings.get(KraConstants.ACADEMIC_YEAR_SUMMER).toString(), userAppointmentTask.getInstitutionAppointmentTypeCode())) { //Academic 9/Summer
                        if (userAppointmentTaskPeriod.getPersonWeeksAmount() == null) {
                            GlobalVariables.getErrorMap().putError("personWeeksAmount", KFSKeyConstants.ERROR_REQUIRED, dataDictionaryService.getAttributeLabel(UserAppointmentTaskPeriod.class, "personWeeksAmount"));
                            valid = false;
                        }
                        
                        if (userAppointmentTaskPeriod.getAgencyPercentEffortAmount() == null) {
                            GlobalVariables.getErrorMap().putError("agencyPercentEffortAmount", KFSKeyConstants.ERROR_REQUIRED, dataDictionaryService.getAttributeLabel(UserAppointmentTaskPeriod.class, "agencyPercentEffortAmount"));
                            valid = false;
                        }
                        
                        if (userAppointmentTaskPeriod.getInstitutionCostSharePercentEffortAmount() == null) {
                            GlobalVariables.getErrorMap().putError("institutionCostSharePercentEffortAmount", KFSKeyConstants.ERROR_REQUIRED, dataDictionaryService.getAttributeLabel(UserAppointmentTaskPeriod.class, "institutionCostSharePercentEffortAmount"));
                            valid = false;
                        }
                    } else if (StringUtils.contains(appointmentTypeMappings.get(KraConstants.HOURLY).toString(), userAppointmentTask.getInstitutionAppointmentTypeCode())) { //Hourly
                        if (userAppointmentTaskPeriod.getUserHourlyRate() == null) {
                            GlobalVariables.getErrorMap().putError("userHourlyRate", KFSKeyConstants.ERROR_REQUIRED, dataDictionaryService.getAttributeLabel(UserAppointmentTaskPeriod.class, "userHourlyRate"));
                            valid = false;
                        }
                        
                        if (userAppointmentTaskPeriod.getUserAgencyHours() == null) {
                            GlobalVariables.getErrorMap().putError("userAgencyHours", KFSKeyConstants.ERROR_REQUIRED, dataDictionaryService.getAttributeLabel(UserAppointmentTaskPeriod.class, "userAgencyHours"));
                            valid = false;
                        }
                        
                        if (userAppointmentTaskPeriod.getUserInstitutionHours() == null) {
                            GlobalVariables.getErrorMap().putError("userInstitutionHours", KFSKeyConstants.ERROR_REQUIRED, dataDictionaryService.getAttributeLabel(UserAppointmentTaskPeriod.class, "userInstitutionHours"));
                            valid = false;
                        }
                    } else if(StringUtils.contains(appointmentTypeMappings.get(KraConstants.GRADUATE_ASSISTANT).toString(), userAppointmentTask.getInstitutionAppointmentTypeCode())) { //Grad Asst
                        if (userAppointmentTaskPeriod.getAgencyFullTimeEquivalentPercent() == null) {
                            GlobalVariables.getErrorMap().putError("agencyFullTimeEquivalentPercent", KFSKeyConstants.ERROR_REQUIRED, dataDictionaryService.getAttributeLabel(UserAppointmentTaskPeriod.class, "agencyPercentEffortAmount"));
                            valid = false;
                        }
                        
                        if (userAppointmentTaskPeriod.getAgencySalaryAmount() == null) {
                            GlobalVariables.getErrorMap().putError("agencySalaryAmount", KFSKeyConstants.ERROR_REQUIRED, dataDictionaryService.getAttributeLabel(UserAppointmentTaskPeriod.class, "agencySalaryAmount"));
                            valid = false;
                        }
                        
                        if (userAppointmentTaskPeriod.getAgencyHealthInsuranceAmount() == null) {
                            GlobalVariables.getErrorMap().putError("agencyHealthInsuranceAmount", KFSKeyConstants.ERROR_REQUIRED, dataDictionaryService.getAttributeLabel(UserAppointmentTaskPeriod.class, "agencyHealthInsuranceAmount"));
                            valid = false;
                        }
                        
                        if (userAppointmentTaskPeriod.getUserCreditHoursNumber() == null) {
                            GlobalVariables.getErrorMap().putError("userCreditHoursNumber", KFSKeyConstants.ERROR_REQUIRED, dataDictionaryService.getAttributeLabel(UserAppointmentTaskPeriod.class, "userCreditHoursNumber"));
                            valid = false;
                        }
                        
                        if (userAppointmentTaskPeriod.getUserCreditHourAmount() == null) {
                            GlobalVariables.getErrorMap().putError("userCreditHourAmount", KFSKeyConstants.ERROR_REQUIRED, dataDictionaryService.getAttributeLabel(UserAppointmentTaskPeriod.class, "userCreditHourAmount"));
                            valid = false;
                        }
                        
                        if (userAppointmentTaskPeriod.getUserMiscellaneousFeeAmount() == null) {
                            GlobalVariables.getErrorMap().putError("userMiscellaneousFeesAmount", KFSKeyConstants.ERROR_REQUIRED, dataDictionaryService.getAttributeLabel(UserAppointmentTaskPeriod.class, "userMiscellaneousFeeAmount"));
                            valid = false;
                        }
                        
                        if (userAppointmentTaskPeriod.getAgencyRequestedFeesAmount() == null) {
                            GlobalVariables.getErrorMap().putError("agencyRequestedFeesAmount", KFSKeyConstants.ERROR_REQUIRED, dataDictionaryService.getAttributeLabel(UserAppointmentTaskPeriod.class, "agencyRequestedFeesAmount"));
                            valid = false;
                        }
                        
                        if (userAppointmentTaskPeriod.getInstitutionRequestedFeesAmount() == null) {
                            GlobalVariables.getErrorMap().putError("institutionRequestedFeesAmount", KFSKeyConstants.ERROR_REQUIRED, dataDictionaryService.getAttributeLabel(UserAppointmentTaskPeriod.class, "institutionRequestedFeesAmount"));
                            valid = false;
                        }
                        
                        if (userAppointmentTaskPeriod.getInstitutionFullTimeEquivalentPercent() == null) {
                            GlobalVariables.getErrorMap().putError("institutionFullTimeEquivalentPercent", KFSKeyConstants.ERROR_REQUIRED, dataDictionaryService.getAttributeLabel(UserAppointmentTaskPeriod.class, "institutionCostSharePercentEffortAmount"));
                            valid = false;
                        }

                        if (userAppointmentTaskPeriod.getInstitutionSalaryAmount() == null) {
                            GlobalVariables.getErrorMap().putError("institutionSalaryAmount", KFSKeyConstants.ERROR_REQUIRED, dataDictionaryService.getAttributeLabel(UserAppointmentTaskPeriod.class, "institutionSalaryAmount"));
                            valid = false;
                        }
                        
                        if (userAppointmentTaskPeriod.getInstitutionHealthInsuranceAmount() == null) {
                            GlobalVariables.getErrorMap().putError("institutionHealthInsuranceAmount", KFSKeyConstants.ERROR_REQUIRED, dataDictionaryService.getAttributeLabel(UserAppointmentTaskPeriod.class, "institutionHealthInsuranceAmount"));
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

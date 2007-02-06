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
package org.kuali.module.kra.routingform.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.kuali.core.document.Document;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.kra.KraConstants;
import org.kuali.module.kra.KraKeyConstants;
import org.kuali.module.kra.routingform.bo.RoutingFormBudget;
import org.kuali.module.kra.routingform.bo.RoutingFormOrganizationCreditPercent;
import org.kuali.module.kra.routingform.bo.RoutingFormPersonnel;
import org.kuali.module.kra.routingform.bo.RoutingFormProjectType;
import org.kuali.module.kra.routingform.document.RoutingFormDocument;
import org.kuali.module.kra.util.AuditCluster;
import org.kuali.module.kra.util.AuditError;

public class RoutingFormAuditRule {

    /**
     * Runs audit mode business rule checks on a ResearchDocument.
     * 
     * @param Document document
     * @return boolean True if the researchDocument is valid, false otherwise.
     */
    public static boolean processRunAuditBusinessRules(Document document) {
        if (!(document instanceof RoutingFormDocument)) {
            return false;
        }
        
        RoutingFormDocument routingFormDocument = (RoutingFormDocument) document;
        
        boolean valid = true;
        
        valid &= processRoutingFormMainPageAuditChecks(routingFormDocument);
        
        return valid;
    }
    
    /**
     * Runs audit mode business rule checks on a Main Page.
     * 
     * @param routingFormDocument
     * @return boolean True if the researchDocument is valid, false otherwise.
     */
    private static boolean processRoutingFormMainPageAuditChecks(RoutingFormDocument routingFormDocument) {
        boolean valid = true;
        List<AuditError> auditErrors = new ArrayList<AuditError>();
        
        // Agency/Delivery Info
        valid &= processRoutingFormMainPageAgencyDeliveryAuditChecks(auditErrors, routingFormDocument);
        
        // Personnel and Units/Orgs
        valid &= processRoutingFormMainPagePersonnelUnitsAuditChecks(auditErrors, routingFormDocument);
        
        // Submission Details
        valid &= processRoutingFormMainPageSubmissionDetailsAuditChecks(auditErrors, routingFormDocument);
        
        // Done, finish up
        if (!auditErrors.isEmpty()) {
            GlobalVariables.getAuditErrorMap().put("mainPageAuditErrors", new AuditCluster("Main Page", auditErrors));
        }
        
        return valid;
    }

    /**
     * Runs audit mode business rule checks on a Main Page, section Agency/Delivery Info.
     * @param auditErrors
     * @param routingFormBudget
     * @return
     */
    private static boolean processRoutingFormMainPageAgencyDeliveryAuditChecks(List<AuditError> auditErrors, RoutingFormDocument routingFormDocument) {
        boolean valid = true;
        
        if (routingFormDocument.isRoutingFormAgencyToBeNamedIndicator() || ObjectUtils.isNull(routingFormDocument.getRoutingFormAgency().getAgencyNumber())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormAgency.agencyNumber", KraKeyConstants.AUDIT_MAIN_PAGE_AGENCY_REQUIRED, "mainpage.anchor1"));
        }
        
        if (ObjectUtils.isNull(routingFormDocument.getRoutingFormAgency().getRoutingFormDueDateTypeCode())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormAgency.routingFormDueDateTypeCode", KraKeyConstants.AUDIT_MAIN_PAGE_DUE_DATE_TYPE_REQUIRED, "mainpage.anchor1"));
        }
        
        if (ObjectUtils.isNull(routingFormDocument.getRoutingFormAgency().getRoutingFormRequiredCopyText())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormAgency.routingFormRequiredCopyText", KraKeyConstants.AUDIT_MAIN_PAGE_COPIES_REQUIRED, "mainpage.anchor1"));
        }
        
        if (ObjectUtils.isNull(routingFormDocument.getRoutingFormAgency().getAgencyAddressDescription())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormAgency.agencyAddressDescription", KraKeyConstants.AUDIT_MAIN_PAGE_ADDRESS_REQUIRED, "mainpage.anchor1"));
        }

        return valid;
    }
    
    /**
     * Runs audit mode business rule checks on a Main Page, section Personnel and Units/Orgs.
     * @param auditErrors
     * @param routingFormBudget
     * @return
     */
    private static boolean processRoutingFormMainPagePersonnelUnitsAuditChecks(List<AuditError> auditErrors, RoutingFormDocument routingFormDocument) {
        boolean valid = true;

        int projectDirectorCount = 0;
        
        int i = 0;
        for (RoutingFormPersonnel person : routingFormDocument.getRoutingFormPersonnel()) {
            if (person.isPersonToBeNamedIndicator()) {
                valid = false;
                auditErrors.add(new AuditError("document.routingFormPerson[" + i + "].personSystemIdentifier", KraKeyConstants.AUDIT_MAIN_PAGE_PERSON_REQUIRED, "mainpage.anchor2"));
            }
            
            if (KraConstants.PERSON_ROLE_CODE_PD.equals(person.getPersonRoleCode())) {
                projectDirectorCount++;
                
                /* TODO KULERA-816: Following line has to be replaced with an appropriate workgroup call. */
                if (false) {
                    valid = false;
                    auditErrors.add(new AuditError("document.routingFormPerson[" + i + "].personSystemIdentifier", KraKeyConstants.AUDIT_MAIN_PAGE_PERSON_NOT_PD, "mainpage.anchor2"));
                }
            }

            if (ObjectUtils.isNull(person.getPersonRoleCode())) {
                valid = false;
                auditErrors.add(new AuditError("document.routingFormPerson[" + i + "].personRoleCode", KraKeyConstants.AUDIT_MAIN_PAGE_PERSON_ROLE_CODE_REQUIRED, "mainpage.anchor2"));
            } else if (KraConstants.PERSON_ROLE_CODE_OTHER.equals(person.getPersonRoleCode()) && ObjectUtils.isNull(person.getPersonRoleText())) {
                valid = false;
                auditErrors.add(new AuditError("document.routingFormPerson[" + i + "].personRoleText", KraKeyConstants.AUDIT_MAIN_PAGE_PERSON_ROLE_TEXT_REQUIRED, "mainpage.anchor2"));
            }
            
            if (ObjectUtils.isNull(person.getPersonFinancialAidPercent())) {
                valid = false;
                auditErrors.add(new AuditError("document.routingFormPerson[" + i + "].personFinancialAidPercent", KraKeyConstants.AUDIT_MAIN_PAGE_PERSON_FA_REQUIRED, "mainpage.anchor2"));
            }
            
            if (ObjectUtils.isNull(person.getPersonCreditPercent())) {
                valid = false;
                auditErrors.add(new AuditError("document.routingFormPerson[" + i + "].personCreditPercent", KraKeyConstants.AUDIT_MAIN_PAGE_PERSON_CREDIT_REQUIRED, "mainpage.anchor2"));
            }
            
            i++;
        }
        
        if(projectDirectorCount == 0) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormPersonnel.personSystemIdentifier", KraKeyConstants.AUDIT_MAIN_PAGE_PD_REQUIRED, "mainpage.anchor2"));
        } else if (projectDirectorCount > 1) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormPersonnel.personSystemIdentifier", KraKeyConstants.AUDIT_MAIN_PAGE_ONLY_ONE_PD, "mainpage.anchor2"));
        }
        
        i = 0;
        for (RoutingFormOrganizationCreditPercent org : routingFormDocument.getRoutingFormOrganizationCreditPercents()) {
            if (ObjectUtils.isNull(org.getOrganizationFinancialAidPercent())) {
                valid = false;
                auditErrors.add(new AuditError("document.routingFormOrganizationCreditPercent[" + i + "].personFinancialAidPercent", KraKeyConstants.AUDIT_MAIN_PAGE_ORG_FA_REQUIRED, "mainpage.anchor2"));
            }
            
            if (ObjectUtils.isNull(org.getOrganizationCreditPercent())) {
                valid = false;
                auditErrors.add(new AuditError("document.routingFormOrganizationCreditPercent[" + i + "].personCreditPercent", KraKeyConstants.AUDIT_MAIN_PAGE_ORG_CREDIT_REQUIRED, "mainpage.anchor2"));
            }
            
            i++;
        }
        
        if (!new KualiInteger(100).equals(routingFormDocument.getTotalFinancialAidPercent())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormPersonnel.personFinancialAidPercent", KraKeyConstants.AUDIT_MAIN_PAGE_TOTAL_FA_PERCENT_NOT_100, "mainpage.anchor2"));
        }
        
        if (!new KualiInteger(100).equals(routingFormDocument.getTotalCreditPercent())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormPersonnel.personCreditPercent", KraKeyConstants.AUDIT_MAIN_PAGE_TOTAL_CREDIT_PERCENT_NOT_100, "mainpage.anchor2"));
        }
        
        return valid;
    }
    
    /**
     * Runs audit mode business rule checks on a Main Page, section Submission Details.
     * @param auditErrors
     * @param routingFormBudget
     * @return
     */
    private static boolean processRoutingFormMainPageSubmissionDetailsAuditChecks(List<AuditError> auditErrors, RoutingFormDocument routingFormDocument) {
        boolean valid = true;
        
        if (ObjectUtils.isNull(routingFormDocument.getSubmissionTypeCode())) {
            valid = false;
            auditErrors.add(new AuditError("document.submissionTypeCode", KraKeyConstants.AUDIT_MAIN_PAGE_SUBMISSION_TYPE_REQUIRED, "mainpage.anchor3"));
        }
        if (KraConstants.SUBMISSION_TYPE_CHANGE.equals(routingFormDocument.getSubmissionTypeCode()) && ObjectUtils.isNull(routingFormDocument.getPreviousFederalIdentifier())) {
            valid = false;
            auditErrors.add(new AuditError("document.previousFederalIdentifier", KraKeyConstants.AUDIT_MAIN_PAGE_SUBMISSION_TYPE_FEDID_REQUIRED, "mainpage.anchor3"));
        }

        String projectTypes[] = new String[routingFormDocument.getRoutingFormProjectTypes().size()];
        
        // Could do asList(projectTypes).contains but that's a bit less efficient since we need to check for quiet a few of them.
        boolean projectTypeNew = false;
        boolean projectTypeTimeExtention = false;
        boolean projectTypeBudgetRevisionActive = false;
        boolean projectTypeBudgetRevisionPending = false;
        boolean projectTypeOther = false;
        
        if (routingFormDocument.getRoutingFormProjectTypes() == null || routingFormDocument.getRoutingFormProjectTypes().size() == 0) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormProjectTypes", KraKeyConstants.AUDIT_MAIN_PAGE_PROJECT_TYPE_REQUIRED, "mainpage.anchor3"));
        } else if (routingFormDocument.getRoutingFormProjectTypes() != null) {
            int i = 0;
            for(RoutingFormProjectType routingFormProjectType : routingFormDocument.getRoutingFormProjectTypes()) {
                projectTypes[i] = routingFormProjectType.getProjectTypeCode();
                
                if (routingFormProjectType.getProjectTypeCode().equals(KraConstants.PROJECT_TYPE_NEW)) {
                    projectTypeNew = true;
                } else if (routingFormProjectType.getProjectTypeCode().equals(KraConstants.PROJECT_TYPE_TIME_EXTENTION)) {
                    projectTypeTimeExtention = true;
                } else if (routingFormProjectType.getProjectTypeCode().equals(KraConstants.PROJECT_TYPE_BUDGET_REVISION_ACTIVE)) {
                    projectTypeBudgetRevisionActive = true;
                } else if (routingFormProjectType.getProjectTypeCode().equals(KraConstants.PROJECT_TYPE_BUDGET_REVISION_PENDING)) {
                    projectTypeBudgetRevisionPending = true;
                } else if (routingFormProjectType.getProjectTypeCode().equals(KraConstants.PROJECT_TYPE_OTHER)) {
                    projectTypeOther = true;
                    
                    if (ObjectUtils.isNull(routingFormDocument.getProjectTypeOtherDescription())) {
                        valid = false;
                        auditErrors.add(new AuditError("document.projectTypeOtherDescription", KraKeyConstants.AUDIT_MAIN_PAGE_PROJECT_TYPE_OTHER_REQUIRED, "mainpage.anchor3"));
                    }
                }
                
                i++;
            }
            
            // It's important to sort because KraDevelopmentGroup.KraRoutingFormProjectTypesValid has elements in alphabetic order.
            Arrays.sort(projectTypes);
            
            // We could use .toString but rather not rely on the representation implementation of that.
            String projectTypesString = "";
            for(i = 0; i < projectTypes.length; i++) {
                projectTypesString += projectTypes[i];
                if (projectTypes.length != i+1) {
                    projectTypesString += ",";
                }
            }
            
            String[] projectTypesValid = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValues("KraDevelopmentGroup", "KraRoutingFormProjectTypesValid");
            List<String> projectTypesValidList = Arrays.asList(projectTypesValid);
            if (!projectTypesValidList.contains(projectTypesString)) {
                valid = false;
                auditErrors.add(new AuditError("document.routingFormProjectTypes", KraKeyConstants.AUDIT_MAIN_PAGE_PROJECT_TYPE_INVALID, "mainpage.anchor3"));
            }
        }
        
        if (ObjectUtils.isNotNull(routingFormDocument.getRoutingFormPriorGrantNumber()) && projectTypeNew) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormPriorGrantNumber", KraKeyConstants.AUDIT_MAIN_PAGE_PROJECT_TYPE_NEW_AND_PRIOR_GRANT, "mainpage.anchor3"));
        }

        if (ObjectUtils.isNotNull(routingFormDocument.getGrantNumber()) && 
                !(projectTypeTimeExtention || projectTypeBudgetRevisionActive || projectTypeBudgetRevisionPending || projectTypeOther)) {
            valid = false;
            auditErrors.add(new AuditError("document.grantNumber", KraKeyConstants.AUDIT_MAIN_PAGE_PROJECT_TYPE_SELECTION_AND_GRANT, "mainpage.anchor3"));
        }
        
        if (ObjectUtils.isNull(routingFormDocument.getRoutingFormPurposeCode())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormPurposeCode", KraKeyConstants.AUDIT_MAIN_PAGE_PURPOSE_REQUIRED, "mainpage.anchor3"));
        }

        if (KraConstants.PURPOSE_RESEARCH.equals(routingFormDocument.getRoutingFormPurposeCode()) && ObjectUtils.isNull(routingFormDocument.getResearchTypeCode())) {
            valid = false;
            auditErrors.add(new AuditError("document.researchTypeCode", KraKeyConstants.AUDIT_MAIN_PAGE_PURPOSE_RESEARCH_TYPE_REQUIRED, "mainpage.anchor3"));
        }
        
        if (KraConstants.PURPOSE_OTHER.equals(routingFormDocument.getRoutingFormPurposeCode()) && ObjectUtils.isNull(routingFormDocument.getRoutingFormOtherPurposeDescription())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormOtherPurposeDescription", KraKeyConstants.AUDIT_MAIN_PAGE_PURPOSE_OTHER_REQUIRED, "mainpage.anchor3"));
        }
        
        if (ObjectUtils.isNull(routingFormDocument.getRoutingFormProjectTitle())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormProjectTitle", KraKeyConstants.AUDIT_MAIN_PAGE_TITLE_REQUIRED, "mainpage.anchor3"));
        }
        
        if (ObjectUtils.isNull(routingFormDocument.getProjectAbstract())) {
            valid = false;
            auditErrors.add(new AuditError("document.projectAbstract", KraKeyConstants.AUDIT_MAIN_PAGE_ABSTRACT_REQUIRED, "mainpage.anchor3"));
        }
        
        // Amounts & Dates
        if (routingFormDocument.getRoutingFormBudget() != null) {
            valid &= processRoutingFormMainPageAmountsDateAuditChecks(auditErrors, routingFormDocument);
        }
        
        return valid;
    }
    
    /**
     * Runs audit mode business rule checks on a Main Page, section Amounts & Dates.
     * @param auditErrors
     * @param routingFormBudget
     * @return
     */
    private static boolean processRoutingFormMainPageAmountsDateAuditChecks(List<AuditError> auditErrors, RoutingFormDocument routingFormDocument) {
        boolean valid = true;
        
        RoutingFormBudget routingFormBudget = routingFormDocument.getRoutingFormBudget();

        // Required fields
        if (ObjectUtils.isNull(routingFormBudget.getRoutingFormBudgetDirectAmount())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetDirectAmount", KraKeyConstants.AUDIT_MAIN_PAGE_DIRECT_REQUIRED, "mainpage.anchor3"));
        }
        if (ObjectUtils.isNull(routingFormBudget.getRoutingFormBudgetIndirectCostAmount())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetIndirectCostAmount", KraKeyConstants.AUDIT_MAIN_PAGE_INDIRECT_REQUIRED, "mainpage.anchor3"));
        }
        if (ObjectUtils.isNull(routingFormBudget.getRoutingFormBudgetStartDate())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetStartDate", KraKeyConstants.AUDIT_MAIN_PAGE_START_DATE_REQUIRED, "mainpage.anchor3"));
        }
        if (ObjectUtils.isNull(routingFormBudget.getRoutingFormBudgetEndDate())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetEndDate", KraKeyConstants.AUDIT_MAIN_PAGE_END_DATE_REQUIRED, "mainpage.anchor3"));
        }
        
        if (ObjectUtils.isNull(routingFormBudget.getRoutingFormBudgetTotalDirectAmount())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetTotalDirectAmount", KraKeyConstants.AUDIT_MAIN_PAGE_TOTAL_DIRECT_REQUIRED, "mainpage.anchor3"));
        }
        if (ObjectUtils.isNull(routingFormBudget.getRoutingFormBudgetTotalIndirectCostAmount())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetTotalIndirectCostAmount", KraKeyConstants.AUDIT_MAIN_PAGE_TOTAL_INDIRECT_REQUIRED, "mainpage.anchor3"));
        }
        if (ObjectUtils.isNull(routingFormBudget.getRoutingFormBudgetTotalStartDate())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetTotalStartDate", KraKeyConstants.AUDIT_MAIN_PAGE_TOTAL_START_DATE_REQUIRED, "mainpage.anchor3"));
        }
        if (ObjectUtils.isNull(routingFormBudget.getRoutingFormBudgetTotalEndDate())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetTotalEndDate", KraKeyConstants.AUDIT_MAIN_PAGE_TOTAL_END_DATE_REQUIRED, "mainpage.anchor3"));
        }
        
        // logic data relation on each row (not relation between the two)
        if(routingFormBudget.getRoutingFormBudgetDirectAmount() != null && routingFormBudget.getRoutingFormBudgetIndirectCostAmount() != null &&
                routingFormBudget.getRoutingFormBudgetDirectAmount().isLessThan(routingFormBudget.getRoutingFormBudgetIndirectCostAmount())){
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetDirectAmount", KraKeyConstants.AUDIT_MAIN_PAGE_DIRECT_LESS_INDIRECT, "mainpage.anchor3"));
        }
        if(routingFormBudget.getRoutingFormBudgetTotalDirectAmount() != null && routingFormBudget.getRoutingFormBudgetTotalIndirectCostAmount() != null &&
                routingFormBudget.getRoutingFormBudgetTotalDirectAmount().isLessThan(routingFormBudget.getRoutingFormBudgetTotalIndirectCostAmount())){
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetTotalDirectAmount", KraKeyConstants.AUDIT_MAIN_PAGE_TOTAL_DIRECT_LESS_TOTAL_INDIRECT, "mainpage.anchor3"));
        }
        if(routingFormBudget.getRoutingFormBudgetStartDate() != null && routingFormBudget.getRoutingFormBudgetEndDate() != null &&
                routingFormBudget.getRoutingFormBudgetStartDate().compareTo(routingFormBudget.getRoutingFormBudgetEndDate()) >= 0){
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetStartDate", KraKeyConstants.AUDIT_MAIN_PAGE_START_DATE_BEFORE_END_DATE, "mainpage.anchor3"));
        }
        if(routingFormBudget.getRoutingFormBudgetTotalStartDate() != null && routingFormBudget.getRoutingFormBudgetTotalEndDate() != null &&
                routingFormBudget.getRoutingFormBudgetTotalStartDate().compareTo(routingFormBudget.getRoutingFormBudgetTotalEndDate()) >= 0){
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetTotalStartDate", KraKeyConstants.AUDIT_MAIN_PAGE_TOTAL_START_DATE_BEFORE_TOTAL_END_DATE, "mainpage.anchor3"));
        }
        
        // Current / Total row relation establishment (subset logic)
        if(routingFormBudget.getRoutingFormBudgetDirectAmount() != null && routingFormBudget.getRoutingFormBudgetTotalDirectAmount() != null &&
                routingFormBudget.getRoutingFormBudgetDirectAmount().isGreaterThan(routingFormBudget.getRoutingFormBudgetTotalDirectAmount())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetDirectAmount", KraKeyConstants.AUDIT_MAIN_PAGE_DIRECT_GREATER_TOTAL_DIRECT, "mainpage.anchor3"));
        }
        if(routingFormBudget.getRoutingFormBudgetIndirectCostAmount() != null && routingFormBudget.getRoutingFormBudgetTotalIndirectCostAmount() != null &&
                routingFormBudget.getRoutingFormBudgetIndirectCostAmount().isGreaterThan(routingFormBudget.getRoutingFormBudgetTotalIndirectCostAmount())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetIndirectCostAmount", KraKeyConstants.AUDIT_MAIN_PAGE_INDIRECT_GREATER_TOTAL_INDIRECT, "mainpage.anchor3"));
        }
        if(routingFormBudget.getRoutingFormBudgetStartDate() != null && routingFormBudget.getRoutingFormBudgetTotalStartDate() != null &&
                routingFormBudget.getRoutingFormBudgetStartDate().compareTo(routingFormBudget.getRoutingFormBudgetTotalStartDate()) < 0) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetStartDate", KraKeyConstants.AUDIT_MAIN_PAGE_START_DATE_GREATER_TOTAL_START_DATE, "mainpage.anchor3"));
        }
        if(routingFormBudget.getRoutingFormBudgetEndDate() != null && routingFormBudget.getRoutingFormBudgetTotalEndDate() != null &&
                routingFormBudget.getRoutingFormBudgetEndDate().compareTo(routingFormBudget.getRoutingFormBudgetTotalEndDate()) > 0) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetStartDate", KraKeyConstants.AUDIT_MAIN_PAGE_END_DATE_LESS_TOTAL_END_DATE, "mainpage.anchor3"));
        }
        
        KualiInteger totalSubcontractorAmountInteger = routingFormDocument.getTotalSubcontractorAmount();
        if (routingFormBudget.getRoutingFormBudgetDirectAmount() != null &&
                routingFormBudget.getRoutingFormBudgetDirectAmount().isLessThan(totalSubcontractorAmountInteger)) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget", KraKeyConstants.AUDIT_MAIN_PAGE_SUBCONTRACTOR_TOTAL_GREATER_DIRECT, "mainpage.anchor3"));
        }
        
        return valid;
    }
}

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
import java.util.List;

import org.kuali.core.document.Document;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.kra.KraConstants;
import org.kuali.module.kra.KraKeyConstants;
import org.kuali.module.kra.routingform.bo.RoutingFormBudget;
import org.kuali.module.kra.routingform.bo.RoutingFormOrganizationCreditPercent;
import org.kuali.module.kra.routingform.bo.RoutingFormPersonnel;
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
            auditErrors.add(new AuditError("document.routingFormAgency.agencyNumber", KraKeyConstants.AUDIT_MAIN_PAGE_AGENCY_REQUIRED, "mainpage"));
        }
        
        if (ObjectUtils.isNull(routingFormDocument.getRoutingFormAgency().getRoutingFormDueDateTypeCode())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormAgency.routingFormDueDateTypeCode", KraKeyConstants.AUDIT_MAIN_PAGE_DUE_DATE_TYPE_REQUIRED, "mainpage"));
        }
        
        if (ObjectUtils.isNull(routingFormDocument.getRoutingFormAgency().getRoutingFormRequiredCopyNumber())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormAgency.routingFormRequiredCopyNumber", KraKeyConstants.AUDIT_MAIN_PAGE_COPIES_REQUIRED, "mainpage"));
        }
        
        if (ObjectUtils.isNull(routingFormDocument.getRoutingFormAgency().getAgencyAddressDescription())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormAgency.agencyAddressDescription", KraKeyConstants.AUDIT_MAIN_PAGE_ADDRESS_REQUIRED, "mainpage"));
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
                auditErrors.add(new AuditError("document.routingFormPerson[" + i + "].personSystemIdentifier", KraKeyConstants.AUDIT_MAIN_PAGE_PERSON_REQUIRED, "mainpage"));
            }
            
            if (KraConstants.PERSON_ROLE_CODE_PD.equals(person.getPersonRoleCode())) {
                projectDirectorCount++;
            }

            if (ObjectUtils.isNull(person.getPersonFinancialAidPercent())) {
                valid = false;
                auditErrors.add(new AuditError("document.routingFormPerson[" + i + "].personFinancialAidPercent", KraKeyConstants.AUDIT_MAIN_PAGE_PERSON_FA_REQUIRED, "mainpage"));
            }
            
            if (ObjectUtils.isNull(person.getPersonCreditPercent())) {
                valid = false;
                auditErrors.add(new AuditError("document.routingFormPerson[" + i + "].personCreditPercent", KraKeyConstants.AUDIT_MAIN_PAGE_PERSON_CREDIT_REQUIRED, "mainpage"));
            }
            
            i++;
        }
        
        if(projectDirectorCount == 0) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormPersonnel.personSystemIdentifier", KraKeyConstants.AUDIT_MAIN_PAGE_PD_REQUIRED, "mainpage"));
        } else if (projectDirectorCount > 1) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormPersonnel.personSystemIdentifier", KraKeyConstants.AUDIT_MAIN_PAGE_ONLY_ONE_PD, "mainpage"));
        }
        
        i = 0;
        for (RoutingFormOrganizationCreditPercent org : routingFormDocument.getRoutingFormOrganizationCreditPercents()) {
            if (ObjectUtils.isNull(org.getOrganizationFinancialAidPercent())) {
                valid = false;
                auditErrors.add(new AuditError("document.routingFormOrganizationCreditPercent[" + i + "].personFinancialAidPercent", KraKeyConstants.AUDIT_MAIN_PAGE_ORG_FA_REQUIRED, "mainpage"));
            }
            
            if (ObjectUtils.isNull(org.getOrganizationCreditPercent())) {
                valid = false;
                auditErrors.add(new AuditError("document.routingFormOrganizationCreditPercent[" + i + "].personCreditPercent", KraKeyConstants.AUDIT_MAIN_PAGE_ORG_CREDIT_REQUIRED, "mainpage"));
            }
            
            i++;
        }
        
        if (!new KualiInteger(100).equals(routingFormDocument.getTotalFinancialAidPercent())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormPersonnel.personFinancialAidPercent", KraKeyConstants.AUDIT_MAIN_PAGE_TOTAL_FA_PERCENT_NOT_100, "mainpage"));
        }
        
        if (!new KualiInteger(100).equals(routingFormDocument.getTotalCreditPercent())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormPersonnel.personCreditPercent", KraKeyConstants.AUDIT_MAIN_PAGE_TOTAL_CREDIT_PERCENT_NOT_100, "mainpage"));
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
            auditErrors.add(new AuditError("document.submissionTypeCode", KraKeyConstants.AUDIT_MAIN_PAGE_SUBMISSION_TYPE_REQUIRED, "mainpage"));
        }
        if (KraConstants.SUBMISSION_TYPE_CHANGE.equals(routingFormDocument.getSubmissionTypeCode()) && ObjectUtils.isNull(routingFormDocument.getPreviousFederalIdentifier())) {
            valid = false;
            auditErrors.add(new AuditError("document.previousFederalIdentifier", KraKeyConstants.AUDIT_MAIN_PAGE_SUBMISSION_TYPE_FEDID_REQUIRED, "mainpage"));
        }

        /* TODO implement project type audit logic. Sample code shown is from ERA. */
        // Hard Audit Error: Select a project type.
        // Hard Audit Error: Project type-Describe the Other type action.
        
        // Hard Audit Error: PRIOR Grant # not valid if TYPE is "New" - for a revised submission, use Current Grant # field to identify.
        // prpslTypesForm.getPrpsl_new_ind().equals("Y") && (prpslForm.getPrpsl_prior_grant_nbr() != null && !prpslForm.getPrpsl_prior_grant_nbr().equals(""))
        
        // Hard Audit Error: Current IU Proposal # field only permitted with Time Extension, Budget Revision, or Other.
        // prpslTypesForm.getPrpsl_new_ind().equals("Y") || prpslTypesForm.getPrpsl_rnwl_ind().equals("Y") || prpslTypesForm.getPrpsl_rnwl_prev_cmt_ind().equals("Y") || prpslTypesForm.getPrpsl_supl_fnd_ind().equals("Y")) && (cgprpsl_nbr != null && !cgprpsl_nbr.equals("")) && !prpslForm.getPrpsl_stat_cd().equals("A")
        
        // Hard Audit Error: Invalid project type combination (See Help Screen).
//        if(prpslTypesForm.getPrpsl_new_ind().equals("Y")) {
//            typeSelectionAsString += "10,";
//          }
//          if(prpslTypesForm.getPrpsl_rnwl_ind().equals("Y")) {
//            typeSelectionAsString += "20,";
//          }
//          if(prpslTypesForm.getPrpsl_rnwl_prev_cmt_ind().equals("Y")) {
//            typeSelectionAsString += "30,";
//          }
//          if(prpslTypesForm.getPrpsl_supl_fnd_ind().equals("Y")) {
//            typeSelectionAsString += "40,";
//          }
//          if(prpslTypesForm.getPrpsl_tm_extns_ind().equals("Y")) {
//            typeSelectionAsString += "50,";
//          }
//          if(prpslTypesForm.getPrpsl_bdgt_rvsn_actv_ind().equals("Y")) {
//            typeSelectionAsString += "60,";
//          }
//          if(prpslTypesForm.getPrpsl_bdgt_rvsn_pend_ind().equals("Y")) {
//            typeSelectionAsString += "70,";
//          }
//          if(prpslTypesForm.getPrpsl_othr_ind().equals("Y")) {
//            typeSelectionAsString += "80,";
//          }
//          // It can be left empty (this is not an invalid combination, see runAudit
//          // -- it's a seperate error).
//          validTypeSelection.add("");
//
//          // 8 atomic selections
//          validTypeSelection.add("10,");
//          validTypeSelection.add("20,");
//          validTypeSelection.add("30,");
//          validTypeSelection.add("40,");
//          validTypeSelection.add("50,");
//          validTypeSelection.add("60,");
//          validTypeSelection.add("70,");
//          validTypeSelection.add("80,");
//
//          // 13 other selections
//          validTypeSelection.add("10,80,");
//          validTypeSelection.add("20,80,");
//          validTypeSelection.add("30,80,");
//          validTypeSelection.add("40,50,");
//          validTypeSelection.add("40,50,80,");
//          validTypeSelection.add("40,60,");
//          validTypeSelection.add("40,60,80,");
//          validTypeSelection.add("40,80,");
//          validTypeSelection.add("50,60,");
//          validTypeSelection.add("50,60,80,");
//          validTypeSelection.add("50,80,");
//          validTypeSelection.add("60,80,");
//          validTypeSelection.add("70,80,");

        if (ObjectUtils.isNull(routingFormDocument.getRoutingFormPurposeCode())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormPurposeCode", KraKeyConstants.AUDIT_MAIN_PAGE_PURPOSE_REQUIRED, "mainpage"));
        }
        
        if (KraConstants.PURPOSE_OTHER.equals(routingFormDocument.getRoutingFormPurposeCode()) && ObjectUtils.isNull(routingFormDocument.getRoutingFormOtherPurposeDescription())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormOtherPurposeDescription", KraKeyConstants.AUDIT_MAIN_PAGE_PURPOSE_OTHER_REQUIRED, "mainpage"));
        }
        
        if (ObjectUtils.isNull(routingFormDocument.getRoutingFormProjectTitle())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormProjectTitle", KraKeyConstants.AUDIT_MAIN_PAGE_TITLE_REQUIRED, "mainpage"));
        }
        
        if (ObjectUtils.isNull(routingFormDocument.getProjectAbstract())) {
            valid = false;
            auditErrors.add(new AuditError("document.projectAbstract", KraKeyConstants.AUDIT_MAIN_PAGE_ABSTRACT_REQUIRED, "mainpage"));
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
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetDirectAmount", KraKeyConstants.AUDIT_MAIN_PAGE_DIRECT_REQUIRED, "mainpage"));
        }
        if (ObjectUtils.isNull(routingFormBudget.getRoutingFormBudgetIndirectCostAmount())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetIndirectCostAmount", KraKeyConstants.AUDIT_MAIN_PAGE_INDIRECT_REQUIRED, "mainpage"));
        }
        if (ObjectUtils.isNull(routingFormBudget.getRoutingFormBudgetStartDate())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetStartDate", KraKeyConstants.AUDIT_MAIN_PAGE_START_DATE_REQUIRED, "mainpage"));
        }
        if (ObjectUtils.isNull(routingFormBudget.getRoutingFormBudgetStartDate())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetStartDate", KraKeyConstants.AUDIT_MAIN_PAGE_END_DATE_REQUIRED, "mainpage"));
        }
        
        if (ObjectUtils.isNull(routingFormBudget.getRoutingFormBudgetTotalDirectAmount())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetTotalDirectAmount", KraKeyConstants.AUDIT_MAIN_PAGE_TOTAL_DIRECT_REQUIRED, "mainpage"));
        }
        if (ObjectUtils.isNull(routingFormBudget.getRoutingFormBudgetTotalIndirectCostAmount())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetTotalIndirectCostAmount", KraKeyConstants.AUDIT_MAIN_PAGE_TOTAL_INDIRECT_REQUIRED, "mainpage"));
        }
        if (ObjectUtils.isNull(routingFormBudget.getRoutingFormBudgetTotalStartDate())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetTotalStartDate", KraKeyConstants.AUDIT_MAIN_PAGE_TOTAL_START_DATE_REQUIRED, "mainpage"));
        }
        if (ObjectUtils.isNull(routingFormBudget.getRoutingFormBudgetTotalStartDate())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetTotalStartDate", KraKeyConstants.AUDIT_MAIN_PAGE_TOTAL_END_DATE_REQUIRED, "mainpage"));
        }
        
        // logic data relation on each row (not relation between the two)
        if(routingFormBudget.getRoutingFormBudgetDirectAmount() != null && routingFormBudget.getRoutingFormBudgetIndirectCostAmount() != null &&
                routingFormBudget.getRoutingFormBudgetDirectAmount().isLessThan(routingFormBudget.getRoutingFormBudgetIndirectCostAmount())){
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetDirectAmount", KraKeyConstants.AUDIT_MAIN_PAGE_DIRECT_LESS_INDIRECT, "mainpage"));
        }
        if(routingFormBudget.getRoutingFormBudgetTotalDirectAmount() != null && routingFormBudget.getRoutingFormBudgetTotalIndirectCostAmount() != null &&
                routingFormBudget.getRoutingFormBudgetTotalDirectAmount().isLessThan(routingFormBudget.getRoutingFormBudgetTotalIndirectCostAmount())){
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetTotalDirectAmount", KraKeyConstants.AUDIT_MAIN_PAGE_TOTAL_DIRECT_LESS_TOTAL_INDIRECT, "mainpage"));
        }
        if(routingFormBudget.getRoutingFormBudgetStartDate() != null && routingFormBudget.getRoutingFormBudgetEndDate() != null &&
                routingFormBudget.getRoutingFormBudgetStartDate().compareTo(routingFormBudget.getRoutingFormBudgetEndDate()) >= 0){
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetStartDate", KraKeyConstants.AUDIT_MAIN_PAGE_START_DATE_BEFORE_END_DATE, "mainpage"));
        }
        if(routingFormBudget.getRoutingFormBudgetTotalStartDate() != null && routingFormBudget.getRoutingFormBudgetTotalEndDate() != null &&
                routingFormBudget.getRoutingFormBudgetTotalStartDate().compareTo(routingFormBudget.getRoutingFormBudgetTotalEndDate()) >= 0){
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetTotalStartDate", KraKeyConstants.AUDIT_MAIN_PAGE_TOTAL_START_DATE_BEFORE_TOTAL_END_DATE, "mainpage"));
        }
        
        // Current / Total row relation establishment (subset logic)
        if(routingFormBudget.getRoutingFormBudgetDirectAmount() != null && routingFormBudget.getRoutingFormBudgetTotalDirectAmount() != null &&
                routingFormBudget.getRoutingFormBudgetDirectAmount().isGreaterThan(routingFormBudget.getRoutingFormBudgetTotalDirectAmount())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetDirectAmount", KraKeyConstants.AUDIT_MAIN_PAGE_DIRECT_GREATER_TOTAL_DIRECT, "mainpage"));
        }
        if(routingFormBudget.getRoutingFormBudgetIndirectCostAmount() != null && routingFormBudget.getRoutingFormBudgetTotalIndirectCostAmount() != null &&
                routingFormBudget.getRoutingFormBudgetIndirectCostAmount().isGreaterThan(routingFormBudget.getRoutingFormBudgetTotalIndirectCostAmount())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetIndirectCostAmount", KraKeyConstants.AUDIT_MAIN_PAGE_INDIRECT_GREATER_TOTAL_INDIRECT, "mainpage"));
        }
        if(routingFormBudget.getRoutingFormBudgetStartDate() != null && routingFormBudget.getRoutingFormBudgetTotalStartDate() != null &&
                routingFormBudget.getRoutingFormBudgetStartDate().compareTo(routingFormBudget.getRoutingFormBudgetTotalStartDate()) < 0) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetStartDate", KraKeyConstants.AUDIT_MAIN_PAGE_START_DATE_GREATER_TOTAL_START_DATE, "mainpage"));
        }
        if(routingFormBudget.getRoutingFormBudgetEndDate() != null && routingFormBudget.getRoutingFormBudgetTotalEndDate() != null &&
                routingFormBudget.getRoutingFormBudgetEndDate().compareTo(routingFormBudget.getRoutingFormBudgetTotalEndDate()) > 0) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetStartDate", KraKeyConstants.AUDIT_MAIN_PAGE_END_DATE_LESS_TOTAL_END_DATE, "mainpage"));
        }
        
        /* TODO see KULERA-736 subtask. Following isn't a safe conversion */
        KualiInteger totalSubcontractorAmountInteger = new KualiInteger(routingFormDocument.getTotalSubcontractorAmount().longValue());
        if (routingFormBudget.getRoutingFormBudgetDirectAmount() != null &&
                routingFormBudget.getRoutingFormBudgetDirectAmount().isLessThan(totalSubcontractorAmountInteger)) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget", KraKeyConstants.AUDIT_MAIN_PAGE_SUBCONTRACTOR_TOTAL_GREATER_DIRECT, "mainpage"));
        }
        
        return valid;
    }
}

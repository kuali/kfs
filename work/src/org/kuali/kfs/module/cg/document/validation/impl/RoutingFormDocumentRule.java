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
package org.kuali.kfs.module.cg.document.validation.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.CGKeyConstants;
import org.kuali.kfs.module.cg.businessobject.RoutingFormAgency;
import org.kuali.kfs.module.cg.businessobject.RoutingFormInstitutionCostShare;
import org.kuali.kfs.module.cg.businessobject.RoutingFormOrganization;
import org.kuali.kfs.module.cg.businessobject.RoutingFormPersonnel;
import org.kuali.kfs.module.cg.businessobject.RoutingFormResearchRisk;
import org.kuali.kfs.module.cg.businessobject.RoutingFormResearchRiskStudy;
import org.kuali.kfs.module.cg.businessobject.RoutingFormSubcontractor;
import org.kuali.kfs.module.cg.document.BudgetDocument;
import org.kuali.kfs.module.cg.document.ResearchDocument;
import org.kuali.kfs.module.cg.document.RoutingFormDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationController;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This class...
 */
public class RoutingFormDocumentRule extends ResearchDocumentRuleBase {
    
    private DataDictionaryService dataDictionaryService;

    
    public RoutingFormDocumentRule() {
        super();
        dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
    }

    
    /**
     * Checks business rules related to saving a ResearchDocument.
     * 
     * @param ResearchDocument researchDocument
     * @return boolean True if the researchDocument is valid, false otherwise.
     */
    @Override
    public boolean processCustomSaveDocumentBusinessRules(ResearchDocument researchDocument) {
        if (!(researchDocument instanceof RoutingFormDocument)) {
            return false;
        }

        boolean valid = true;

        RoutingFormDocument routingFormDocument = (RoutingFormDocument) researchDocument;

        // changing this to '0' so it doesn't validate reference objects within a list (Subcontractors was causing a problem).
        SpringContext.getBean(DictionaryValidationService.class).validateDocumentAndUpdatableReferencesRecursively(routingFormDocument, 0, true);

        valid &= processRoutingFormAgency(routingFormDocument);
        
        valid &= processInstitutionCostShare(routingFormDocument);

        valid &= processRoutingFormOrganizations(routingFormDocument);

        valid &= processRoutingFormSubcontractors(routingFormDocument);

        valid &= processRoutingFormResearchRisks(routingFormDocument);

        valid &= processRoutingFormPersonnel(routingFormDocument);

        valid &= GlobalVariables.getErrorMap().isEmpty();

        return valid;
    }

    /**
     * Runs audit mode business rule checks on a ResearchDocument.
     * 
     * @param Document document
     * @return boolean True if the researchDocument is valid, false otherwise.
     */
    public boolean processRunAuditBusinessRules(Document document) {
        return RoutingFormAuditRule.processRunAuditBusinessRules(document);
    }

    
    /**
     * This method validates that the Agency and Federal Pass Through Agency are, if entered, valid.  No Agency will be handled via Audit Error.
     * @param routingFormDocument
     * @return 
     */
    private boolean processRoutingFormAgency(RoutingFormDocument routingFormDocument) {
        boolean valid = true;
        
        routingFormDocument.getRoutingFormAgency().refreshReferenceObject("agency");
        if (!StringUtils.isBlank(routingFormDocument.getRoutingFormAgency().getAgencyNumber()) && routingFormDocument.getRoutingFormAgency().getAgency() ==  null) {
            valid = false;
            GlobalVariables.getErrorMap().putError("routingFormAgency.agencyNumber", CGKeyConstants.ERROR_INVALID_VALUE, new String[] { dataDictionaryService.getAttributeLabel(RoutingFormAgency.class, "agencyNumber") });
        }

        routingFormDocument.refreshReferenceObject("federalPassThroughAgency");
        if (!StringUtils.isBlank(routingFormDocument.getAgencyFederalPassThroughNumber()) && routingFormDocument.getFederalPassThroughAgency() ==  null) {
            valid = false;
            GlobalVariables.getErrorMap().putError("agencyFederalPassThroughNumber", CGKeyConstants.ERROR_INVALID_VALUE, new String[] { dataDictionaryService.getAttributeLabel(RoutingFormDocument.class, "agencyFederalPassThroughNumber") });
        }

        
        return valid;
    }
    
    /**
     * This method validates Institution Cost Share Orgs. It checks the following:
     * <ul>
     * <li>The Org must exist</li>
     * <li>If an Account Number is specified, the Account must exist</li>
     * <li>Orgs without Accounts can appear in the list only once</li>
     * <li>Accounts, when specified, can appear in the list only once </li>
     * <li>Positive, non-zero amounts are required for all lines </li>
     * </ul>
     * 
     * @param routingFormDocument The routingFormDocument that is being validated
     * @return valid Does the validation pass
     */
    private boolean processInstitutionCostShare(RoutingFormDocument routingFormDocument) {
        boolean valid = true;

        List accounts = new ArrayList();
        List orgNoAccounts = new ArrayList();

        ErrorMap errorMap = GlobalVariables.getErrorMap();

        int i = 0;

        for (RoutingFormInstitutionCostShare costShare : routingFormDocument.getRoutingFormInstitutionCostShares()) {
            errorMap.addToErrorPath("routingFormInstitutionCostShare[" + i + "]");
            //KFSMI-798 - refreshNonUpdatableReferences() used instead of refresh(), 
            //RoutingFormInstitutionCostShare does not have any updatable references
            costShare.refreshNonUpdateableReferences();

            if (costShare.getRoutingFormCostShareAmount() == null || !costShare.getRoutingFormCostShareAmount().isPositive()) {
                // Amount is zero or less
                valid = false;
                errorMap.putError("routingFormCostShareAmount", CGKeyConstants.ERROR_INVALID_AMOUNT_POSITIVE_ONLY);
            }

            if (costShare.getOrganization() != null) {
                if (costShare.getAccountNumber() == null) {
                    if (!orgNoAccounts.contains(costShare.getOrganization())) {
                        orgNoAccounts.add(costShare.getOrganization());
                    }
                    else {
                        // org already in list
                        valid = false;
                        errorMap.putError("organizationCode", CGKeyConstants.ERROR_ORG_ALREADY_EXISTS_ON_RF, costShare.getChartOfAccountsCode(), costShare.getOrganizationCode());

                    }
                }
                else {
                    // account number is not null
                    if (costShare.getAccount() == null) {
                        // account number is specified account doesn't exist
                        valid = false;
                        errorMap.putError("accountNumber", KFSKeyConstants.ERROR_ACCOUNT_NOT_FOUND, costShare.getChartOfAccountsCode(), costShare.getOrganizationCode(), costShare.getAccountNumber());
                    }
                    else if (!accounts.contains(costShare.getAccount())) {
                        accounts.add(costShare.getAccount());
                    }
                    else {
                        // account already in list
                        valid = false;
                        errorMap.putError("accountNumber", CGKeyConstants.ERROR_ACCOUNT_ALREADY_EXISTS_ON_RF, costShare.getChartOfAccountsCode(), costShare.getOrganizationCode(), costShare.getAccountNumber());
                    }
                }
            }
            else {
                // organization doesn't exist
                valid = false;
                errorMap.putError("organizationCode", CGKeyConstants.ERROR_ORG_NOT_FOUND, costShare.getChartOfAccountsCode(), costShare.getOrganizationCode());
            }
            errorMap.removeFromErrorPath("routingFormInstitutionCostShare[" + i++ + "]");
        }
        return valid;
    }

    /**
     * This method validates 'Other Organizations'. It checks the following:
     * <ul>
     * <li>The Org must exist</li>
     * <li>The Org must appear in the list only once</li>
     * </ul>
     * 
     * @param routingFormDocument The routingFormDocument that is being validated
     * @return valid Does the validation pass
     */
    private boolean processRoutingFormOrganizations(RoutingFormDocument routingFormDocument) {
        boolean valid = true;

        List organizations = new ArrayList();

        ErrorMap errorMap = GlobalVariables.getErrorMap();

        int i = 0;

        for (RoutingFormOrganization organization : routingFormDocument.getRoutingFormOrganizations()) {
            //KFSMI-798 - refreshNonUpdatableReferences() used instead of refresh(), 
            //RoutingFormOrganization does not have any updatable references
            organization.refreshNonUpdateableReferences();

            errorMap.addToErrorPath("routingFormOrganization[" + i + "]");

            if (organization.getOrganization() == null) {
                // organization does not exist
                valid = false;
                errorMap.putError("organizationCode", CGKeyConstants.ERROR_ORG_NOT_FOUND, organization.getChartOfAccountsCode(), organization.getOrganizationCode());
            }
            else {
                if (!organizations.contains(organization.getOrganization())) {
                    organizations.add(organization.getOrganization());
                }
                else {
                    // organization already exists on RF
                    valid = false;
                    errorMap.putError("organizationCode", CGKeyConstants.ERROR_ORG_ALREADY_EXISTS_ON_RF, organization.getChartOfAccountsCode(), organization.getOrganizationCode());
                }
            }
            errorMap.removeFromErrorPath("routingFormOrganization[" + i++ + "]");
        }

        return valid;
    }

    /**
     * This method validates 'Subcontractors'. It checks the following:
     * <ul>
     * <li>The Subcontractor must exist</li>
     * <li>The Subcontractor must appear in the list only once</li>
     * </ul>
     * 
     * @param routingFormDocument The routingFormDocument that is being validated
     * @return valid Does the validation pass
     */
    private boolean processRoutingFormSubcontractors(RoutingFormDocument routingFormDocument) {
        boolean valid = true;

        List subcontractors = new ArrayList();

        ErrorMap errorMap = GlobalVariables.getErrorMap();

        int i = 0;

        for (RoutingFormSubcontractor subcontractor : routingFormDocument.getRoutingFormSubcontractors()) {
            //KFSMI-798 - refreshNonUpdatableReferences() used instead of refresh(), 
            //RoutingFormSubcontractor does not have any updatable references
            subcontractor.refreshNonUpdateableReferences();

            errorMap.addToErrorPath("routingFormSubcontractor[" + i + "]");

            if (subcontractor.getRoutingFormSubcontractorAmount() == null || subcontractor.getRoutingFormSubcontractorAmount().isNegative()) {
                // Amount is negative
                valid = false;
                errorMap.putError("routingFormSubcontractorAmount", CGKeyConstants.ERROR_INVALID_AMOUNT_NOT_NEGATIVE);
            }

            if (subcontractor.getRoutingFormSubcontractorNumber() != null) {

                if (subcontractor.getSubcontractor() == null) {
                    // subcontractor doesn't exist
                    valid = false;
                    errorMap.putError("routingFormSubcontractorAmount", CGKeyConstants.ERROR_SUBCONTRACTOR_NOT_FOUND);
                }
                else {
                    if (!subcontractors.contains(subcontractor.getSubcontractor())) {
                        subcontractors.add(subcontractor.getSubcontractor());
                    }
                    else {
                        // subcontractor already exists on RF
                        valid = false;
                        errorMap.putError("routingFormSubcontractorAmount", CGKeyConstants.ERROR_SUBCONTRACTOR_ALREADY_EXISTS_ON_RF);
                    }
                }
            }
            else {
                valid = false;
                errorMap.putError("routingFormSubcontractorAmount", CGKeyConstants.ERROR_SUBCONTRACTOR_NOT_SELECTED);
            }

            errorMap.removeFromErrorPath("routingFormSubcontractor[" + i++ + "]");
        }
        return valid;
    }

    /**
     * This method validates Personnel. It checks the following:
     * <ul>
     * <li>Required person name or TBNed selected.</li>
     * </ul>
     * 
     * @param routingFormDocument The routingFormDocument that is being validated
     * @return valid Does the validation pass
     */
    private boolean processRoutingFormPersonnel(RoutingFormDocument routingFormDocument) {
        boolean valid = true;
        ErrorMap errorMap = GlobalVariables.getErrorMap();

        // Functionally personnel doesn't need an index i, but it's left here for consistency of validating lists.
        int i = 0;

        for (RoutingFormPersonnel person : routingFormDocument.getRoutingFormPersonnel()) {
            errorMap.addToErrorPath("routingFormPersonnel[" + i + "]");

            if (person.getPrincipalId() == null && !person.isPersonToBeNamedIndicator()) {
                valid = false;
                errorMap.putError("principalId", CGKeyConstants.ERROR_PERSON_NOT_NAMED);
            }

            if (person.getChartOfAccountsCode() == null || person.getOrganizationCode() == null) {
                valid = false;
                errorMap.putError("principalId", CGKeyConstants.ERROR_MISSING, "Routing Form Personnel Chart and/or Org");
            }

            errorMap.removeFromErrorPath("routingFormPersonnel[" + i + "]");
            i++;
        }

        return valid;
    }

    /**
     * This method validates 'Research Risks'. It checks the following:
     * <ul>
     * <li>If Study is approved, approval date is required.</li>
     * <li>If study is not approved, approval date and expiration date must be empty.</li>
     * <li>If review status is 'exempt', exception number is required.</li>
     * <li>If review status in not 'exempt', exception number should be blank.</li>
     * <li>Expiration date must not be earlier than approval date.</li>
     * <li>If the Human Subjects approval date is more than one year prior to the routing form creation date, the user must enter a
     * more current date, or set the status to Pending.</li>
     * <li>If the Animal approval date is more than three years prior to the routing form creation date, the user must enter a more
     * current date, or set the status to Pending.</li>
     * </ul>
     * 
     * @param routingFormDocument The routingFormDocument that is being validated
     * @return valid Does the validation pass
     */
    private boolean processRoutingFormResearchRisks(RoutingFormDocument routingFormDocument) {

        boolean valid = true;
        ErrorMap errorMap = GlobalVariables.getErrorMap();

        String humanSubjectsActiveCode = SpringContext.getBean(ParameterService.class).getParameterValue(KfsParameterConstants.CONTRACTS_AND_GRANTS_DOCUMENT.class, CGConstants.RESEARCH_RISKS_HUMAN_SUBJECTS_ACTIVE_CODE);

        String animalsActiveCode = SpringContext.getBean(ParameterService.class).getParameterValue(KfsParameterConstants.CONTRACTS_AND_GRANTS_DOCUMENT.class, CGConstants.RESEARCH_RISKS_ANIMALS_ACTIVE_CODE);

        // Setup dates.
        Date createDate = routingFormDocument.getRoutingFormCreateDate();
        Calendar createCalendar = SpringContext.getBean(DateTimeService.class).getCalendar(createDate);
        createCalendar.add(Calendar.YEAR, -1);
        Date humanSubjectsEarliestApprovalDate = createCalendar.getTime();
        createCalendar.add(Calendar.YEAR, -2);
        Date animalsEarliestApprovalDate = createCalendar.getTime();

        int i = 0;
        for (RoutingFormResearchRisk researchRisk : routingFormDocument.getRoutingFormResearchRisks()) {
            errorMap.addToErrorPath("routingFormResearchRisk[" + i + "]");
            int j = 0;
            for (RoutingFormResearchRiskStudy study : researchRisk.getResearchRiskStudies()) {
                errorMap.addToErrorPath("researchRiskStudy[" + j + "]");

                // If study is approved, approval date is required.
                if (CGConstants.RESEARCH_RISK_STUDY_STATUS_APPROVED.equals(study.getResearchRiskStudyApprovalStatusCode()) && ObjectUtils.isNull(study.getResearchRiskStudyApprovalDate())) {
                    valid = false;
                    errorMap.putError("researchRiskStudyApprovalDate", CGKeyConstants.ERROR_APPROVAL_DATE_REQUIRED);
                }

                // If study is not approved, approval date and expiration date must be empty.
                if (!CGConstants.RESEARCH_RISK_STUDY_STATUS_APPROVED.equals(study.getResearchRiskStudyApprovalStatusCode())) {
                    if (ObjectUtils.isNotNull(study.getResearchRiskStudyApprovalDate())) {
                        valid = false;
                        errorMap.putError("researchRiskStudyApprovalDate", CGKeyConstants.ERROR_APPROVAL_DATE_REMOVE);
                    }
                    if (ObjectUtils.isNotNull(study.getResearchRiskStudyExpirationDate())) {
                        valid = false;
                        errorMap.putError("researchRiskStudyExpirationDate", CGKeyConstants.ERROR_EXPIRATION_DATE_REMOVE);
                    }
                }

                // If review status is 'exempt', exception number is required.
                if (CGConstants.RESEARCH_RISK_STUDY_REVIEW_EXEMPT.equals(study.getResearchRiskStudyReviewCode()) && StringUtils.isBlank(study.getResearchRiskExemptionNumber())) {
                    valid = false;
                    errorMap.putError("researchRiskExemptionNumber", CGKeyConstants.ERROR_EXEMPTION_NUMBER_REQUIRED);
                }

                // If review status in not 'exempt', exception number should be blank.
                if (!CGConstants.RESEARCH_RISK_STUDY_REVIEW_EXEMPT.equals(study.getResearchRiskStudyReviewCode()) && !StringUtils.isBlank(study.getResearchRiskExemptionNumber())) {
                    valid = false;
                    errorMap.putError("researchRiskExemptionNumber", CGKeyConstants.ERROR_EXEMPTION_NUMBER_REMOVE);
                }

                // Expiration date must not be earlier than approval date.
                if (ObjectUtils.isNotNull(study.getResearchRiskStudyApprovalDate()) && ObjectUtils.isNotNull(study.getResearchRiskStudyExpirationDate()) && study.getResearchRiskStudyExpirationDate().before(study.getResearchRiskStudyApprovalDate())) {
                    valid = false;
                    errorMap.putError("researchRiskStudyExpirationDate", CGKeyConstants.ERROR_EXPIRATION_DATE_TOO_EARLY);
                }

                // If Human Subjects approval date is more than one year prior to the routing form creation date, the user must
                // enter a more current date, or set the status to Pending.
                if (researchRisk.getResearchRiskTypeCode().equals(humanSubjectsActiveCode) && ObjectUtils.isNotNull(study.getResearchRiskStudyApprovalDate())) {
                    int dateDiff = SpringContext.getBean(DateTimeService.class).dateDiff(study.getResearchRiskStudyApprovalDate(), humanSubjectsEarliestApprovalDate, false);
                    if (dateDiff > 0) {
                        // Seems counterintuitive that 'before' is the proper operator here - but it is.
                        valid = false;
                        errorMap.putError("researchRiskStudyApprovalDate", CGKeyConstants.ERROR_HUMAN_SUBJECTS_APPROVAL_DATE_TOO_OLD);
                    }
                }

                // If Animals approval date is more than 3 years prior to the routing form creation date, the user must enter a more
                // current date, or set the status to Pending.
                if (researchRisk.getResearchRiskTypeCode().equals(animalsActiveCode) && ObjectUtils.isNotNull(study.getResearchRiskStudyApprovalDate())) {
                    int dateDiff = SpringContext.getBean(DateTimeService.class).dateDiff(study.getResearchRiskStudyApprovalDate(), animalsEarliestApprovalDate, false);
                    if (dateDiff > 0) {
                        valid = false;
                        errorMap.putError("researchRiskStudyApprovalDate", CGKeyConstants.ERROR_ANIMALS_APPROVAL_DATE_TOO_OLD);
                    }
                }

                errorMap.removeFromErrorPath("researchRiskStudy[" + j++ + "]");
            }

            errorMap.removeFromErrorPath("routingFormResearchRisk[" + i++ + "]");
        }

        return valid;
    }


    /**
     * This method validates that a Budget can be lined to a RF. It checks the following:
     * <ul>
     * <li>/...</li>
     * </ul>
     * 
     * @param routingFormDocument The routingFormDocument that is being validated
     * @param budgetDocumentHeaderId The doc header ID of the budget to be linked to this RF
     * @param selectedBudgetPeriods An array containing the
     * @param allPeriods
     * @return valid Does the validation pass
     */
    public boolean processBudgetRoutingFormLink(RoutingFormDocument routingFormDocument, String[] selectedBudgetPeriods, boolean allPeriods, boolean checkPeriods) {
        boolean valid = true;
        ErrorMap errorMap = GlobalVariables.getErrorMap();

        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);

        errorMap.addToErrorPath("document");

        try {
            DictionaryValidationService dictionaryValidationService = SpringContext.getBean(DictionaryValidationService.class);
            dictionaryValidationService.validateDocument(routingFormDocument);

            // see if the Budget Document Header ID is valid

            // This stinks, but it has to be done since Workflow uses Longs for document number.
            try {
                new Long(routingFormDocument.getRoutingFormBudgetNumber());
            }
            catch (NumberFormatException e) {
                errorMap.putError("routingFormBudgetNumber", CGKeyConstants.ERROR_DOCUMENT_NUMBER_NOT_BUDGET_DOCUMENT, new String[] { routingFormDocument.getRoutingFormBudgetNumber() });
                return false;
            }

            if (documentService.documentExists(routingFormDocument.getRoutingFormBudgetNumber())) {
                Document document = documentService.getByDocumentHeaderId(routingFormDocument.getRoutingFormBudgetNumber());
                if (BudgetDocument.class.isAssignableFrom(document.getClass())) {
                    BudgetDocument budgetDocument = (BudgetDocument) document;
                    
                    String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(BudgetDocument.class);
                    FinancialSystemTransactionalDocumentPresentationController presentationController = (FinancialSystemTransactionalDocumentPresentationController)SpringContext.getBean(DocumentHelperService.class).getDocumentPresentationController(documentTypeName);
                    FinancialSystemTransactionalDocumentAuthorizerBase budgetAdjustmentDocumentAuthorizer = (FinancialSystemTransactionalDocumentAuthorizerBase) SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(documentTypeName);
                    Person person = GlobalVariables.getUserSession().getPerson();

                    Set<String> editModes = presentationController.getEditModes(budgetDocument);
                    editModes = budgetAdjustmentDocumentAuthorizer.getEditModes(budgetDocument, person, editModes);
                    if (!editModes.contains(AuthorizationConstants.EditMode.FULL_ENTRY) && !editModes.contains(AuthorizationConstants.EditMode.VIEW_ONLY)) {
                        errorMap.putError("routingFormBudgetNumber1", CGKeyConstants.ERROR_SELECTED_PERIODS_CONSECUTIVE);
                        return false;
                    }

                    // see if this budget is already linked to another RF
                    Map<String, Object> fieldValues = new HashMap<String, Object>();
                    fieldValues.put("routingFormBudgetNumber", routingFormDocument.getRoutingFormBudgetNumber());

                    List<RoutingFormDocument> matching = (List<RoutingFormDocument>) businessObjectService.findMatching(RoutingFormDocument.class, fieldValues);

                    for (RoutingFormDocument rfd : matching) {
                        if (!rfd.getDocumentNumber().equals(routingFormDocument.getDocumentNumber())) {
                            valid = false;
                            errorMap.putError("routingFormBudgetNumber", CGKeyConstants.ERROR_BUDGET_ALREADY_LINKED, new String[] { routingFormDocument.getRoutingFormBudgetNumber(), rfd.getDocumentNumber() });
                            break;
                        }
                    }

                    // see if Modular has been distributed

                }
                else {
                    valid = false;
                    errorMap.putError("routingFormBudgetNumber", CGKeyConstants.ERROR_DOCUMENT_NUMBER_NOT_BUDGET_DOCUMENT, new String[] { routingFormDocument.getRoutingFormBudgetNumber() });
                }
            }
            else {
                valid = false;
                errorMap.putError("routingFormBudgetNumber", CGKeyConstants.ERROR_DOCUMENT_NUMBER_NOT_EXIST, new String[] { routingFormDocument.getRoutingFormBudgetNumber() });
            }

            // check selected periods
            if (!allPeriods && checkPeriods) {
                int nextPeriodNumberShouldBe = -1;
                if (selectedBudgetPeriods != null && selectedBudgetPeriods.length > 0) {
                    for (int i = 0; i < selectedBudgetPeriods.length; i++) {
                        if (i != 0 && Integer.valueOf(selectedBudgetPeriods[i]) != nextPeriodNumberShouldBe) { // first time
                            valid = false;
                            errorMap.putError("routingFormBudgetNumber1", CGKeyConstants.ERROR_SELECTED_PERIODS_CONSECUTIVE);
                            break;
                        }
                        nextPeriodNumberShouldBe = Integer.valueOf(selectedBudgetPeriods[i]) + 1;
                    }
                }
                else {
                    valid = false;
                    errorMap.putError("routingFormBudgetNumber1", CGKeyConstants.ERROR_AT_LEAST_ONE_PERIOD);
                }
            }
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Exception validating budget to link", e);
        }

        errorMap.removeFromErrorPath("document");
        return valid;
    }
}


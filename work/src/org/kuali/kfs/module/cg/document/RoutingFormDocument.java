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

package org.kuali.module.kra.routingform.document;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Campus;
import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.core.workflow.DocumentInitiator;
import org.kuali.core.workflow.KualiDocumentXmlMaterializer;
import org.kuali.core.workflow.KualiTransactionalDocumentInformation;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.cg.bo.Agency;
import org.kuali.module.cg.bo.Cfda;
import org.kuali.module.chart.service.ChartUserService;
import org.kuali.module.kra.KraConstants;
import org.kuali.module.kra.bo.AdhocOrg;
import org.kuali.module.kra.bo.AdhocPerson;
import org.kuali.module.kra.budget.bo.Budget;
import org.kuali.module.kra.document.ResearchDocumentBase;
import org.kuali.module.kra.routingform.bo.ContractGrantProposal;
import org.kuali.module.kra.routingform.bo.Purpose;
import org.kuali.module.kra.routingform.bo.ResearchTypeCode;
import org.kuali.module.kra.routingform.bo.RoutingFormAgency;
import org.kuali.module.kra.routingform.bo.RoutingFormBudget;
import org.kuali.module.kra.routingform.bo.RoutingFormInstitutionCostShare;
import org.kuali.module.kra.routingform.bo.RoutingFormKeyword;
import org.kuali.module.kra.routingform.bo.RoutingFormOrganization;
import org.kuali.module.kra.routingform.bo.RoutingFormOrganizationCreditPercent;
import org.kuali.module.kra.routingform.bo.RoutingFormOtherCostShare;
import org.kuali.module.kra.routingform.bo.RoutingFormPersonRole;
import org.kuali.module.kra.routingform.bo.RoutingFormPersonnel;
import org.kuali.module.kra.routingform.bo.RoutingFormProjectType;
import org.kuali.module.kra.routingform.bo.RoutingFormPurpose;
import org.kuali.module.kra.routingform.bo.RoutingFormQuestion;
import org.kuali.module.kra.routingform.bo.RoutingFormResearchRisk;
import org.kuali.module.kra.routingform.bo.RoutingFormResearchTypeCode;
import org.kuali.module.kra.routingform.bo.RoutingFormStatus;
import org.kuali.module.kra.routingform.bo.RoutingFormSubcontractor;
import org.kuali.module.kra.routingform.service.RoutingFormMainPageService;
import org.kuali.module.kra.routingform.service.RoutingFormProjectDetailsService;
import org.kuali.module.kra.routingform.service.RoutingFormResearchRiskService;
import org.kuali.module.kra.routingform.service.RoutingFormService;

/**
 * 
 */
public class RoutingFormDocument extends ResearchDocumentBase {

    private boolean agencyAdditionalShippingInstructionsIndicator;
    private boolean agencyFederalPassThroughNotAvailableIndicator;
    private String agencyFederalPassThroughNumber;
    private String grantNumber;
    private String routingFormAnnouncementNumber;
    private String routingFormBudgetNumber;
    private boolean routingFormConflictOfInterestCurrentIndicator;
    private boolean routingFormConflictOfInterestExistsIndicator;
    private boolean routingFormCoProjectDirectorIndicator;
    private boolean routingFormCreditPercentIndicator;
    private Date routingFormCreateDate;
    private boolean routingFormCostShareIndicator;
    private boolean routingFormFederalPassThroughIndicator;
    private String routingFormFellowFirstName;
    private String routingFormFellowEmailAddress;
    private String routingFormFellowFullName;
    private String routingFormFellowLastName;
    private String routingFormLayDescription;
    private Date routingFormLastUpdateDate;
    private Long routingFormLastUpdateUniversalIdentifier;
    private boolean routingFormOtherOrganizationIndicator;
    private String routingFormOtherPurposeDescription;
    private String routingFormOtherTypeDescription;
    private String routingFormParentNumber;
    private String routingFormPhysicalCampusCode;
    private String routingFormPriorGrantNumber;
    private String routingFormProjectTitle;
    private String routingFormPurposeCode;
    private String routingFormSpaceRequiredDescription;
    private boolean routingFormSpaceRequiredIndicator;
    private String routingFormStatusCode;
    private boolean routingFormSubcontractorIndicator;
    private String institutionAccountNumber;
    private String researchTypeCode;
    private String previousFederalIdentifier;
    private String federalIdentifier;
    private String grantsGovernmentConfirmationNumber;
    private boolean grantsGovernmentSubmissionIndicator;
    private String projectAbstract;
    private Integer creditPercentNextSequenceNumber;
    private Integer institutionCostShareNextSequenceNumber;
    private Integer otherCostShareNextSequenceNumber;
    private Integer projectDirectorNextSequenceNumber;
    private Integer subcontractorNextSequenceNumber;
    private Integer personnelNextSequenceNumber;
    private boolean routingFormAgencyToBeNamedIndicator;
    private String routingFormCatalogOfFederalDomesticAssistanceNumber;
    private String projectTypeOtherDescription;

    private Campus routingFormPhysicalCampus;
    private RoutingFormStatus routingFormStatus;
    private Purpose purpose;
    private RoutingFormAgency routingFormAgency;
    private Cfda cfda;
    private List<RoutingFormResearchRisk> routingFormResearchRisks;
    private List<RoutingFormKeyword> routingFormKeywords;
    private RoutingFormBudget routingFormBudget;
    private ResearchTypeCode researchType;
    private ContractGrantProposal contractGrantProposal;
    private List<RoutingFormInstitutionCostShare> routingFormInstitutionCostShares;
    private List<RoutingFormOtherCostShare> routingFormOtherCostShares;
    private List<RoutingFormSubcontractor> routingFormSubcontractors;
    private Agency federalPassThroughAgency;
    private List<RoutingFormPersonnel> routingFormPersonnel;
    private List<RoutingFormOrganizationCreditPercent> routingFormOrganizationCreditPercents;
    private List<RoutingFormQuestion> routingFormQuestions;
    private List<RoutingFormOrganization> routingFormOrganizations;
    private List<RoutingFormResearchTypeCode> routingFormResearchTypeCodes;
    private List<RoutingFormPurpose> routingFormPurposes;
    private List<RoutingFormProjectType> routingFormProjectTypes;
    private List<RoutingFormPersonRole> routingFormPersonRoles;
    // for budget document number ajax
    private Budget budget;

    /**
     * Default constructor.
     */
    public RoutingFormDocument() {
        super();

        creditPercentNextSequenceNumber = new Integer(1);
        institutionCostShareNextSequenceNumber = new Integer(1);
        otherCostShareNextSequenceNumber = new Integer(1);
        projectDirectorNextSequenceNumber = new Integer(1);
        subcontractorNextSequenceNumber = new Integer(1);
        personnelNextSequenceNumber = new Integer(1);

        routingFormResearchRisks = new ArrayList<RoutingFormResearchRisk>();
        routingFormKeywords = new TypedArrayList(RoutingFormKeyword.class);
        routingFormInstitutionCostShares = new ArrayList<RoutingFormInstitutionCostShare>();
        routingFormOtherCostShares = new ArrayList<RoutingFormOtherCostShare>();
        routingFormSubcontractors = new ArrayList<RoutingFormSubcontractor>();
        routingFormPersonnel = new TypedArrayList(RoutingFormPersonnel.class);
        routingFormOrganizationCreditPercents = new TypedArrayList(RoutingFormOrganizationCreditPercent.class);
        routingFormQuestions = new ArrayList<RoutingFormQuestion>();
        routingFormOrganizations = new ArrayList<RoutingFormOrganization>();
        routingFormResearchTypeCodes = new TypedArrayList(RoutingFormResearchTypeCode.class);
        routingFormPurposes = new TypedArrayList(RoutingFormPurpose.class);
        routingFormProjectTypes = new TypedArrayList(RoutingFormProjectType.class);
        routingFormPersonRoles = new TypedArrayList(RoutingFormPersonRole.class);
    }

    public void initialize() {
        this.setRoutingFormCreateDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());

        SpringContext.getBean(RoutingFormMainPageService.class).setupMainPageMaintainables(this);
    }

    @Override
    public void handleRouteStatusChange() {
        super.handleRouteStatusChange();

        if (super.getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
            this.refreshReferenceObject("contractGrantProposal");
            if (this.getContractGrantProposal().getProposalNumber() == null) {
                boolean createProposal = false;
                for (RoutingFormProjectType routingFormProjectType : this.getRoutingFormProjectTypes()) {
                    if (routingFormProjectType.isProjectTypeSelectedIndicator() && SpringContext.getBean(ParameterService.class).getParameterEvaluator(getClass(), KraConstants.CREATE_PROPOSAL_PROJECT_TYPES, routingFormProjectType.getProjectTypeCode()).evaluationSucceeds()) {
                        createProposal = true;
                        break;
                    }
                }

                if (createProposal) {
                    Long newProposalNumber = SpringContext.getBean(RoutingFormService.class).createAndRouteProposalMaintenanceDocument(this);

                    this.getContractGrantProposal().setProposalNumber(newProposalNumber);
                    SpringContext.getBean(BusinessObjectService.class).save(this.getContractGrantProposal());
                }

            }
        }
    }

    /**
     * Ensures required fields for supporting objects are properly set since we don't use transient objects.
     */
    @Override
    public void prepareForSave() {
        super.prepareForSave();

        String documentNumber = this.getDocumentHeader().getDocumentNumber();

        this.getContractGrantProposal().setDocumentNumber(documentNumber);
        this.getRoutingFormAgency().setDocumentNumber(documentNumber);
        this.getRoutingFormBudget().setDocumentNumber(documentNumber);

        // Setup research risks if this is the first save
        if (this.routingFormResearchRisks.isEmpty()) {
            SpringContext.getBean(RoutingFormResearchRiskService.class).setupResearchRisks(this);
        }

        // Setup project details questions if this is the first save
        if (this.routingFormQuestions.isEmpty()) {
            SpringContext.getBean(RoutingFormProjectDetailsService.class).setupOtherProjectDetailsQuestions(this);
        }
    }

    /**
     * Gets the agencyAdditionalShippingInstructionsIndicator attribute.
     * 
     * @return Returns the agencyAdditionalShippingInstructionsIndicator
     */
    public boolean getAgencyAdditionalShippingInstructionsIndicator() {
        return agencyAdditionalShippingInstructionsIndicator;
    }

    /**
     * Sets the agencyAdditionalShippingInstructionsIndicator attribute.
     * 
     * @param agencyAdditionalShippingInstructionsIndicator The agencyAdditionalShippingInstructionsIndicator to set.
     */
    public void setAgencyAdditionalShippingInstructionsIndicator(boolean agencyAdditionalShippingInstructionsIndicator) {
        this.agencyAdditionalShippingInstructionsIndicator = agencyAdditionalShippingInstructionsIndicator;
    }


    /**
     * Gets the agencyFederalPassThroughNotAvailableIndicator attribute.
     * 
     * @return Returns the agencyFederalPassThroughNotAvailableIndicator
     */
    public boolean getAgencyFederalPassThroughNotAvailableIndicator() {
        return agencyFederalPassThroughNotAvailableIndicator;
    }

    /**
     * Sets the agencyFederalPassThroughNotAvailableIndicator attribute.
     * 
     * @param agencyFederalPassThroughNotAvailableIndicator The agencyFederalPassThroughNotAvailableIndicator to set.
     */
    public void setAgencyFederalPassThroughNotAvailableIndicator(boolean agencyFederalPassThroughNotAvailableIndicator) {
        this.agencyFederalPassThroughNotAvailableIndicator = agencyFederalPassThroughNotAvailableIndicator;
    }


    /**
     * Gets the agencyFederalPassThroughNumber attribute.
     * 
     * @return Returns the agencyFederalPassThroughNumber
     */
    public String getAgencyFederalPassThroughNumber() {
        return agencyFederalPassThroughNumber;
    }

    /**
     * Sets the agencyFederalPassThroughNumber attribute.
     * 
     * @param agencyFederalPassThroughNumber The agencyFederalPassThroughNumber to set.
     */
    public void setAgencyFederalPassThroughNumber(String agencyFederalPassThroughNumber) {
        this.agencyFederalPassThroughNumber = agencyFederalPassThroughNumber;
    }


    /**
     * Gets the grantNumber attribute.
     * 
     * @return Returns the grantNumber
     */
    public String getGrantNumber() {
        return grantNumber;
    }

    /**
     * Sets the grantNumber attribute.
     * 
     * @param grantNumber The grantNumber to set.
     */
    public void setGrantNumber(String grantNumber) {
        this.grantNumber = grantNumber;
    }


    /**
     * Gets the routingFormAnnouncementNumber attribute.
     * 
     * @return Returns the routingFormAnnouncementNumber
     */
    public String getRoutingFormAnnouncementNumber() {
        return routingFormAnnouncementNumber;
    }

    /**
     * Sets the routingFormAnnouncementNumber attribute.
     * 
     * @param routingFormAnnouncementNumber The routingFormAnnouncementNumber to set.
     */
    public void setRoutingFormAnnouncementNumber(String routingFormAnnouncementNumber) {
        this.routingFormAnnouncementNumber = routingFormAnnouncementNumber;
    }


    /**
     * Gets the routingFormBudgetNumber attribute.
     * 
     * @return Returns the routingFormBudgetNumber
     */
    public String getRoutingFormBudgetNumber() {
        return routingFormBudgetNumber;
    }

    /**
     * Sets the routingFormBudgetNumber attribute.
     * 
     * @param routingFormBudgetNumber The routingFormBudgetNumber to set.
     */
    public void setRoutingFormBudgetNumber(String routingFormBudgetNumber) {
        this.routingFormBudgetNumber = routingFormBudgetNumber;
    }


    /**
     * Gets the routingFormConflictOfInterestCurrentIndicator attribute.
     * 
     * @return Returns the routingFormConflictOfInterestCurrentIndicator
     */
    public boolean getRoutingFormConflictOfInterestCurrentIndicator() {
        return routingFormConflictOfInterestCurrentIndicator;
    }

    /**
     * Sets the routingFormConflictOfInterestCurrentIndicator attribute.
     * 
     * @param routingFormConflictOfInterestCurrentIndicator The routingFormConflictOfInterestCurrentIndicator to set.
     */
    public void setRoutingFormConflictOfInterestCurrentIndicator(boolean routingFormConflictOfInterestCurrentIndicator) {
        this.routingFormConflictOfInterestCurrentIndicator = routingFormConflictOfInterestCurrentIndicator;
    }


    /**
     * Gets the routingFormConflictOfInterestExistsIndicator attribute.
     * 
     * @return Returns the routingFormConflictOfInterestExistsIndicator
     */
    public boolean getRoutingFormConflictOfInterestExistsIndicator() {
        return routingFormConflictOfInterestExistsIndicator;
    }

    /**
     * Sets the routingFormConflictOfInterestExistsIndicator attribute.
     * 
     * @param routingFormConflictOfInterestExistsIndicator The routingFormConflictOfInterestExistsIndicator to set.
     */
    public void setRoutingFormConflictOfInterestExistsIndicator(boolean routingFormConflictOfInterestExistsIndicator) {
        this.routingFormConflictOfInterestExistsIndicator = routingFormConflictOfInterestExistsIndicator;
    }


    /**
     * Gets the routingFormCoProjectDirectorIndicator attribute.
     * 
     * @return Returns the routingFormCoProjectDirectorIndicator
     */
    public boolean getRoutingFormCoProjectDirectorIndicator() {
        return routingFormCoProjectDirectorIndicator;
    }

    /**
     * Sets the routingFormCoProjectDirectorIndicator attribute.
     * 
     * @param routingFormCoProjectDirectorIndicator The routingFormCoProjectDirectorIndicator to set.
     */
    public void setRoutingFormCoProjectDirectorIndicator(boolean routingFormCoProjectDirectorIndicator) {
        this.routingFormCoProjectDirectorIndicator = routingFormCoProjectDirectorIndicator;
    }


    /**
     * Gets the routingFormCreditPercentIndicator attribute.
     * 
     * @return Returns the routingFormCreditPercentIndicator
     */
    public boolean getRoutingFormCreditPercentIndicator() {
        return routingFormCreditPercentIndicator;
    }

    /**
     * Sets the routingFormCreditPercentIndicator attribute.
     * 
     * @param routingFormCreditPercentIndicator The routingFormCreditPercentIndicator to set.
     */
    public void setRoutingFormCreditPercentIndicator(boolean routingFormCreditPercentIndicator) {
        this.routingFormCreditPercentIndicator = routingFormCreditPercentIndicator;
    }


    /**
     * Gets the routingFormCreateDate attribute.
     * 
     * @return Returns the routingFormCreateDate
     */
    public Date getRoutingFormCreateDate() {
        return routingFormCreateDate;
    }

    /**
     * Sets the routingFormCreateDate attribute.
     * 
     * @param routingFormCreateDate The routingFormCreateDate to set.
     */
    public void setRoutingFormCreateDate(Date routingFormCreateDate) {
        this.routingFormCreateDate = routingFormCreateDate;
    }


    /**
     * Gets the routingFormCostShareIndicator attribute.
     * 
     * @return Returns the routingFormCostShareIndicator
     */
    public boolean getRoutingFormCostShareIndicator() {
        return routingFormCostShareIndicator;
    }

    /**
     * Sets the routingFormCostShareIndicator attribute.
     * 
     * @param routingFormCostShareIndicator The routingFormCostShareIndicator to set.
     */
    public void setRoutingFormCostShareIndicator(boolean routingFormCostShareIndicator) {
        this.routingFormCostShareIndicator = routingFormCostShareIndicator;
    }


    /**
     * Gets the routingFormFederalPassThroughIndicator attribute.
     * 
     * @return Returns the routingFormFederalPassThroughIndicator
     */
    public boolean getRoutingFormFederalPassThroughIndicator() {
        return routingFormFederalPassThroughIndicator;
    }

    /**
     * Sets the routingFormFederalPassThroughIndicator attribute.
     * 
     * @param routingFormFederalPassThroughIndicator The routingFormFederalPassThroughIndicator to set.
     */
    public void setRoutingFormFederalPassThroughIndicator(boolean routingFormFederalPassThroughIndicator) {
        this.routingFormFederalPassThroughIndicator = routingFormFederalPassThroughIndicator;
    }


    /**
     * Gets the routingFormFellowFirstName attribute.
     * 
     * @return Returns the routingFormFellowFirstName
     */
    public String getRoutingFormFellowFirstName() {
        return routingFormFellowFirstName;
    }

    /**
     * Sets the routingFormFellowFirstName attribute.
     * 
     * @param routingFormFellowFirstName The routingFormFellowFirstName to set.
     */
    public void setRoutingFormFellowFirstName(String routingFormFellowFirstName) {
        this.routingFormFellowFirstName = routingFormFellowFirstName;
    }


    /**
     * Gets the routingFormFellowEmailAddress attribute.
     * 
     * @return Returns the routingFormFellowEmailAddress
     */
    public String getRoutingFormFellowEmailAddress() {
        return routingFormFellowEmailAddress;
    }

    /**
     * Sets the routingFormFellowEmailAddress attribute.
     * 
     * @param routingFormFellowEmailAddress The routingFormFellowEmailAddress to set.
     */
    public void setRoutingFormFellowEmailAddress(String routingFormFellowEmailAddress) {
        this.routingFormFellowEmailAddress = routingFormFellowEmailAddress;
    }


    /**
     * Gets the routingFormFellowFullName attribute.
     * 
     * @return Returns the routingFormFellowFullName
     */
    public String getRoutingFormFellowFullName() {
        return routingFormFellowFullName;
    }

    /**
     * Sets the routingFormFellowFullName attribute.
     * 
     * @param routingFormFellowFullName The routingFormFellowFullName to set.
     */
    public void setRoutingFormFellowFullName(String routingFormFellowFullName) {
        this.routingFormFellowFullName = routingFormFellowFullName;
    }


    /**
     * Gets the routingFormFellowLastName attribute.
     * 
     * @return Returns the routingFormFellowLastName
     */
    public String getRoutingFormFellowLastName() {
        return routingFormFellowLastName;
    }

    /**
     * Sets the routingFormFellowLastName attribute.
     * 
     * @param routingFormFellowLastName The routingFormFellowLastName to set.
     */
    public void setRoutingFormFellowLastName(String routingFormFellowLastName) {
        this.routingFormFellowLastName = routingFormFellowLastName;
    }


    /**
     * Gets the routingFormLayDescription attribute.
     * 
     * @return Returns the routingFormLayDescription
     */
    public String getRoutingFormLayDescription() {
        return routingFormLayDescription;
    }

    /**
     * Sets the routingFormLayDescription attribute.
     * 
     * @param routingFormLayDescription The routingFormLayDescription to set.
     */
    public void setRoutingFormLayDescription(String routingFormLayDescription) {
        this.routingFormLayDescription = routingFormLayDescription;
    }


    /**
     * Gets the routingFormLastUpdateDate attribute.
     * 
     * @return Returns the routingFormLastUpdateDate
     */
    public Date getRoutingFormLastUpdateDate() {
        return routingFormLastUpdateDate;
    }

    /**
     * Sets the routingFormLastUpdateDate attribute.
     * 
     * @param routingFormLastUpdateDate The routingFormLastUpdateDate to set.
     */
    public void setRoutingFormLastUpdateDate(Date routingFormLastUpdateDate) {
        this.routingFormLastUpdateDate = routingFormLastUpdateDate;
    }


    /**
     * Gets the routingFormLastUpdateUniversalIdentifier attribute.
     * 
     * @return Returns the routingFormLastUpdateUniversalIdentifier
     */
    public Long getRoutingFormLastUpdateUniversalIdentifier() {
        return routingFormLastUpdateUniversalIdentifier;
    }

    /**
     * Sets the routingFormLastUpdateUniversalIdentifier attribute.
     * 
     * @param routingFormLastUpdateUniversalIdentifier The routingFormLastUpdateUniversalIdentifier to set.
     */
    public void setRoutingFormLastUpdateUniversalIdentifier(Long routingFormLastUpdateUniversalIdentifier) {
        this.routingFormLastUpdateUniversalIdentifier = routingFormLastUpdateUniversalIdentifier;
    }


    /**
     * Gets the routingFormOtherOrganizationIndicator attribute.
     * 
     * @return Returns the routingFormOtherOrganizationIndicator
     */
    public boolean getRoutingFormOtherOrganizationIndicator() {
        return routingFormOtherOrganizationIndicator;
    }

    /**
     * Sets the routingFormOtherOrganizationIndicator attribute.
     * 
     * @param routingFormOtherOrganizationIndicator The routingFormOtherOrganizationIndicator to set.
     */
    public void setRoutingFormOtherOrganizationIndicator(boolean routingFormOtherOrganizationIndicator) {
        this.routingFormOtherOrganizationIndicator = routingFormOtherOrganizationIndicator;
    }


    /**
     * Gets the routingFormOtherPurposeDescription attribute.
     * 
     * @return Returns the routingFormOtherPurposeDescription
     */
    public String getRoutingFormOtherPurposeDescription() {
        return routingFormOtherPurposeDescription;
    }

    /**
     * Sets the routingFormOtherPurposeDescription attribute.
     * 
     * @param routingFormOtherPurposeDescription The routingFormOtherPurposeDescription to set.
     */
    public void setRoutingFormOtherPurposeDescription(String routingFormOtherPurposeDescription) {
        this.routingFormOtherPurposeDescription = routingFormOtherPurposeDescription;
    }


    /**
     * Gets the routingFormOtherTypeDescription attribute.
     * 
     * @return Returns the routingFormOtherTypeDescription
     */
    public String getRoutingFormOtherTypeDescription() {
        return routingFormOtherTypeDescription;
    }

    /**
     * Sets the routingFormOtherTypeDescription attribute.
     * 
     * @param routingFormOtherTypeDescription The routingFormOtherTypeDescription to set.
     */
    public void setRoutingFormOtherTypeDescription(String routingFormOtherTypeDescription) {
        this.routingFormOtherTypeDescription = routingFormOtherTypeDescription;
    }


    /**
     * Gets the routingFormParentNumber attribute.
     * 
     * @return Returns the routingFormParentNumber
     */
    public String getRoutingFormParentNumber() {
        return routingFormParentNumber;
    }

    /**
     * Sets the routingFormParentNumber attribute.
     * 
     * @param routingFormParentNumber The routingFormParentNumber to set.
     */
    public void setRoutingFormParentNumber(String routingFormParentNumber) {
        this.routingFormParentNumber = routingFormParentNumber;
    }


    /**
     * Gets the routingFormPhysicalCampusCode attribute.
     * 
     * @return Returns the routingFormPhysicalCampusCode
     */
    public String getRoutingFormPhysicalCampusCode() {
        return routingFormPhysicalCampusCode;
    }

    /**
     * Sets the routingFormPhysicalCampusCode attribute.
     * 
     * @param routingFormPhysicalCampusCode The routingFormPhysicalCampusCode to set.
     */
    public void setRoutingFormPhysicalCampusCode(String routingFormPhysicalCampusCode) {
        this.routingFormPhysicalCampusCode = routingFormPhysicalCampusCode;
    }


    /**
     * Gets the routingFormPriorGrantNumber attribute.
     * 
     * @return Returns the routingFormPriorGrantNumber
     */
    public String getRoutingFormPriorGrantNumber() {
        return routingFormPriorGrantNumber;
    }

    /**
     * Sets the routingFormPriorGrantNumber attribute.
     * 
     * @param routingFormPriorGrantNumber The routingFormPriorGrantNumber to set.
     */
    public void setRoutingFormPriorGrantNumber(String routingFormPriorGrantNumber) {
        this.routingFormPriorGrantNumber = routingFormPriorGrantNumber;
    }


    /**
     * Gets the routingFormProjectTitle attribute.
     * 
     * @return Returns the routingFormProjectTitle
     */
    public String getRoutingFormProjectTitle() {
        return routingFormProjectTitle;
    }

    /**
     * Sets the routingFormProjectTitle attribute.
     * 
     * @param routingFormProjectTitle The routingFormProjectTitle to set.
     */
    public void setRoutingFormProjectTitle(String routingFormProjectTitle) {
        this.routingFormProjectTitle = routingFormProjectTitle;
    }


    /**
     * Gets the routingFormPurposeCode attribute.
     * 
     * @return Returns the routingFormPurposeCode
     */
    public String getRoutingFormPurposeCode() {
        return routingFormPurposeCode;
    }

    /**
     * Sets the routingFormPurposeCode attribute.
     * 
     * @param routingFormPurposeCode The routingFormPurposeCode to set.
     */
    public void setRoutingFormPurposeCode(String routingFormPurposeCode) {
        this.routingFormPurposeCode = routingFormPurposeCode;
    }

    /**
     * Gets the routingFormSpaceRequiredDescription attribute.
     * 
     * @return Returns the routingFormSpaceRequiredDescription
     */
    public String getRoutingFormSpaceRequiredDescription() {
        return routingFormSpaceRequiredDescription;
    }

    /**
     * Sets the routingFormSpaceRequiredDescription attribute.
     * 
     * @param routingFormSpaceRequiredDescription The routingFormSpaceRequiredDescription to set.
     */
    public void setRoutingFormSpaceRequiredDescription(String routingFormSpaceRequiredDescription) {
        this.routingFormSpaceRequiredDescription = routingFormSpaceRequiredDescription;
    }


    /**
     * Gets the routingFormSpaceRequiredIndicator attribute.
     * 
     * @return Returns the routingFormSpaceRequiredIndicator
     */
    public boolean getRoutingFormSpaceRequiredIndicator() {
        return routingFormSpaceRequiredIndicator;
    }

    /**
     * Sets the routingFormSpaceRequiredIndicator attribute.
     * 
     * @param routingFormSpaceRequiredIndicator The routingFormSpaceRequiredIndicator to set.
     */
    public void setRoutingFormSpaceRequiredIndicator(boolean routingFormSpaceRequiredIndicator) {
        this.routingFormSpaceRequiredIndicator = routingFormSpaceRequiredIndicator;
    }


    /**
     * Gets the routingFormStatusCode attribute.
     * 
     * @return Returns the routingFormStatusCode
     */
    public String getRoutingFormStatusCode() {
        return routingFormStatusCode;
    }

    /**
     * Sets the routingFormStatusCode attribute.
     * 
     * @param routingFormStatusCode The routingFormStatusCode to set.
     */
    public void setRoutingFormStatusCode(String routingFormStatusCode) {
        this.routingFormStatusCode = routingFormStatusCode;
    }


    /**
     * Gets the routingFormSubcontractorIndicator attribute.
     * 
     * @return Returns the routingFormSubcontractorIndicator
     */
    public boolean getRoutingFormSubcontractorIndicator() {
        return routingFormSubcontractorIndicator;
    }

    /**
     * Sets the routingFormSubcontractorIndicator attribute.
     * 
     * @param routingFormSubcontractorIndicator The routingFormSubcontractorIndicator to set.
     */
    public void setRoutingFormSubcontractorIndicator(boolean routingFormSubcontractorIndicator) {
        this.routingFormSubcontractorIndicator = routingFormSubcontractorIndicator;
    }

    /**
     * Gets the institutionAccountNumber attribute.
     * 
     * @return Returns the institutionAccountNumber
     */
    public String getInstitutionAccountNumber() {
        return institutionAccountNumber;
    }

    /**
     * Sets the institutionAccountNumber attribute.
     * 
     * @param institutionAccountNumber The institutionAccountNumber to set.
     */
    public void setInstitutionAccountNumber(String institutionAccountNumber) {
        this.institutionAccountNumber = institutionAccountNumber;
    }


    /**
     * Gets the researchTypeCode attribute.
     * 
     * @return Returns the researchTypeCode
     */
    public String getResearchTypeCode() {
        return researchTypeCode;
    }

    /**
     * Sets the researchTypeCode attribute.
     * 
     * @param researchTypeCode The researchTypeCode to set.
     */
    public void setResearchTypeCode(String researchTypeCode) {
        this.researchTypeCode = researchTypeCode;
    }

    /**
     * Gets the previousFederalIdentifier attribute.
     * 
     * @return Returns the previousFederalIdentifier
     */
    public String getPreviousFederalIdentifier() {
        return previousFederalIdentifier;
    }

    /**
     * Sets the previousFederalIdentifier attribute.
     * 
     * @param previousFederalIdentifier The previousFederalIdentifier to set.
     */
    public void setPreviousFederalIdentifier(String previousFederalIdentifier) {
        this.previousFederalIdentifier = previousFederalIdentifier;
    }


    /**
     * Gets the routingFormPhysicalCampus attribute.
     * 
     * @return Returns the routingFormPhysicalCampus
     */
    public Campus getRoutingFormPhysicalCampus() {
        return routingFormPhysicalCampus;
    }

    /**
     * Sets the routingFormPhysicalCampus attribute.
     * 
     * @param routingFormPhysicalCampus The routingFormPhysicalCampus to set.
     * @deprecated
     */
    public void setRoutingFormPhysicalCampus(Campus routingFormPhysicalCampus) {
        this.routingFormPhysicalCampus = routingFormPhysicalCampus;
    }

    /**
     * Gets the routingFormStatus attribute.
     * 
     * @return Returns the routingFormStatus.
     */
    public RoutingFormStatus getRoutingFormStatus() {
        return routingFormStatus;
    }

    /**
     * Sets the routingFormStatus attribute value.
     * 
     * @param routingFormStatus The routingFormStatus to set.
     * @deprecated
     */
    public void setRoutingFormStatus(RoutingFormStatus routingFormStatus) {
        this.routingFormStatus = routingFormStatus;
    }

    /**
     * Gets the purpose attribute.
     * 
     * @return Returns the purpose.
     */
    public Purpose getPurpose() {
        return purpose;
    }

    /**
     * Sets the purpose attribute value.
     * 
     * @param purpose The purpose to set.
     * @deprecated
     */
    public void setPurpose(Purpose purpose) {
        this.purpose = purpose;
    }

    /**
     * Gets the creditPercentNextSequenceNumber attribute.
     * 
     * @return Returns the creditPercentNextSequenceNumber.
     */
    public Integer getCreditPercentNextSequenceNumber() {
        return creditPercentNextSequenceNumber;
    }

    /**
     * Sets the creditPercentNextSequenceNumber attribute value.
     * 
     * @param creditPercentNextSequenceNumber The creditPercentNextSequenceNumber to set.
     */
    public void setCreditPercentNextSequenceNumber(Integer creditPercentNextSequenceNumber) {
        this.creditPercentNextSequenceNumber = creditPercentNextSequenceNumber;
    }

    /**
     * Gets the institutionCostShareNextSequenceNumber attribute.
     * 
     * @return Returns the institutionCostShareNextSequenceNumber.
     */
    public Integer getInstitutionCostShareNextSequenceNumber() {
        return institutionCostShareNextSequenceNumber;
    }

    /**
     * Sets the institutionCostShareNextSequenceNumber attribute value.
     * 
     * @param institutionCostShareNextSequenceNumber The institutionCostShareNextSequenceNumber to set.
     */
    public void setInstitutionCostShareNextSequenceNumber(Integer institutionCostShareNextSequenceNumber) {
        this.institutionCostShareNextSequenceNumber = institutionCostShareNextSequenceNumber;
    }

    /**
     * Gets the otherCostShareNextSequenceNumber attribute.
     * 
     * @return Returns the otherCostShareNextSequenceNumber.
     */
    public Integer getOtherCostShareNextSequenceNumber() {
        return otherCostShareNextSequenceNumber;
    }

    /**
     * Sets the otherCostShareNextSequenceNumber attribute value.
     * 
     * @param otherCostShareNextSequenceNumber The otherCostShareNextSequenceNumber to set.
     */
    public void setOtherCostShareNextSequenceNumber(Integer otherCostShareNextSequenceNumber) {
        this.otherCostShareNextSequenceNumber = otherCostShareNextSequenceNumber;
    }

    /**
     * Gets the projectDirectorNextSequenceNumber attribute.
     * 
     * @return Returns the projectDirectorNextSequenceNumber.
     */
    public Integer getProjectDirectorNextSequenceNumber() {
        return projectDirectorNextSequenceNumber;
    }

    /**
     * Sets the projectDirectorNextSequenceNumber attribute value.
     * 
     * @param projectDirectorNextSequenceNumber The projectDirectorNextSequenceNumber to set.
     */
    public void setProjectDirectorNextSequenceNumber(Integer projectDirectorNextSequenceNumber) {
        this.projectDirectorNextSequenceNumber = projectDirectorNextSequenceNumber;
    }

    /**
     * Gets the subcontractorNextSequenceNumber attribute.
     * 
     * @return Returns the subcontractorNextSequenceNumber.
     */
    public Integer getSubcontractorNextSequenceNumber() {
        return subcontractorNextSequenceNumber;
    }

    /**
     * Sets the subcontractorNextSequenceNumber attribute value.
     * 
     * @param subcontractorNextSequenceNumber The subcontractorNextSequenceNumber to set.
     */
    public void setSubcontractorNextSequenceNumber(Integer subcontractorNextSequenceNumber) {
        this.subcontractorNextSequenceNumber = subcontractorNextSequenceNumber;
    }

    /**
     * Gets the federalIdentifier attribute.
     * 
     * @return Returns the federalIdentifier
     */
    public String getFederalIdentifier() {
        return federalIdentifier;
    }

    /**
     * Sets the federalIdentifier attribute.
     * 
     * @param federalIdentifier The federalIdentifier to set.
     */
    public void setFederalIdentifier(String federalIdentifier) {
        this.federalIdentifier = federalIdentifier;
    }


    /**
     * Gets the grantsGovernmentConfirmationNumber attribute.
     * 
     * @return Returns the grantsGovernmentConfirmationNumber
     */
    public String getGrantsGovernmentConfirmationNumber() {
        return grantsGovernmentConfirmationNumber;
    }

    /**
     * Sets the grantsGovernmentConfirmationNumber attribute.
     * 
     * @param grantsGovernmentConfirmationNumber The grantsGovernmentConfirmationNumber to set.
     */
    public void setGrantsGovernmentConfirmationNumber(String grantsGovernmentConfirmationNumber) {
        this.grantsGovernmentConfirmationNumber = grantsGovernmentConfirmationNumber;
    }


    /**
     * Gets the grantsGovernmentSubmissionIndicator attribute.
     * 
     * @return Returns the grantsGovernmentSubmissionIndicator
     */
    public boolean isGrantsGovernmentSubmissionIndicator() {
        return grantsGovernmentSubmissionIndicator;
    }

    /**
     * Sets the grantsGovernmentSubmissionIndicator attribute.
     * 
     * @param grantsGovernmentSubmissionIndicator The grantsGovernmentSubmissionIndicator to set.
     */
    public void setGrantsGovernmentSubmissionIndicator(boolean grantsGovernmentSubmissionIndicator) {
        this.grantsGovernmentSubmissionIndicator = grantsGovernmentSubmissionIndicator;
    }


    /**
     * Gets the projectAbstract attribute.
     * 
     * @return Returns the projectAbstract
     */
    public String getProjectAbstract() {
        return projectAbstract;
    }

    /**
     * Sets the projectAbstract attribute.
     * 
     * @param projectAbstract The projectAbstract to set.
     */
    public void setProjectAbstract(String projectAbstract) {
        this.projectAbstract = projectAbstract;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }

    public RoutingFormAgency getRoutingFormAgency() {
        return routingFormAgency;
    }

    public void setRoutingFormAgency(RoutingFormAgency routingFormAgency) {
        this.routingFormAgency = routingFormAgency;
    }

    public Cfda getCfda() {
        return cfda;
    }

    public void setCfda(Cfda cfda) {
        this.cfda = cfda;
    }

    public List<RoutingFormResearchRisk> getRoutingFormResearchRisks() {
        return routingFormResearchRisks;
    }

    public void setRoutingFormResearchRisks(List<RoutingFormResearchRisk> routingFormResearchRisks) {
        this.routingFormResearchRisks = routingFormResearchRisks;
    }

    public RoutingFormResearchRisk getRoutingFormResearchRisk(int index) {
        while (getRoutingFormResearchRisks().size() <= index) {
            getRoutingFormResearchRisks().add(new RoutingFormResearchRisk());
        }
        return (RoutingFormResearchRisk) getRoutingFormResearchRisks().get(index);
    }

    private List getAllRoutingFormResearchRiskStudies() {
        List allStudies = new ArrayList();

        for (RoutingFormResearchRisk researchRisk : this.routingFormResearchRisks) {
            allStudies.addAll(researchRisk.getResearchRiskStudies());
        }

        return allStudies;
    }

    public void addRoutingFormResearchRisk(RoutingFormResearchRisk routingFormResearchRisk) {
        getRoutingFormResearchRisks().add(routingFormResearchRisk);
    }

    public RoutingFormBudget getRoutingFormBudget() {
        return routingFormBudget;
    }

    public void setRoutingFormBudget(RoutingFormBudget routingFormBudget) {
        this.routingFormBudget = routingFormBudget;
    }

    public List<RoutingFormKeyword> getRoutingFormKeywords() {
        return routingFormKeywords;
    }

    public void setRoutingFormKeywords(List<RoutingFormKeyword> routingFormKeywords) {
        this.routingFormKeywords = routingFormKeywords;
    }

    public void addRoutingFormKeyword(RoutingFormKeyword routingFormKeyword) {
        getRoutingFormKeywords().add(routingFormKeyword);
    }


    /**
     * Gets the researchType attribute.
     * 
     * @return Returns the researchType.
     */
    public ResearchTypeCode getResearchType() {
        return researchType;
    }

    /**
     * Sets the researchType attribute value.
     * 
     * @param researchType The researchType to set.
     * @deprecated
     */
    public void setResearchType(ResearchTypeCode researchType) {
        this.researchType = researchType;
    }

    public ContractGrantProposal getContractGrantProposal() {
        return contractGrantProposal;
    }

    public void setContractGrantProposal(ContractGrantProposal contractGrantProposal) {
        this.contractGrantProposal = contractGrantProposal;
    }

    public List<RoutingFormInstitutionCostShare> getRoutingFormInstitutionCostShares() {
        return routingFormInstitutionCostShares;
    }

    public RoutingFormInstitutionCostShare getRoutingFormInstitutionCostShare(int index) {
        while (getRoutingFormInstitutionCostShares().size() <= index) {
            getRoutingFormInstitutionCostShares().add(new RoutingFormInstitutionCostShare());
        }
        return (RoutingFormInstitutionCostShare) getRoutingFormInstitutionCostShares().get(index);
    }

    public void setRoutingFormInstitutionCostShares(List<RoutingFormInstitutionCostShare> routingFormInstitutionCostShares) {
        this.routingFormInstitutionCostShares = routingFormInstitutionCostShares;
    }

    /**
     * This method...
     * 
     * @param routingFormInstitutionCostShare
     */
    public void addRoutingFormInstitutionCostShare(RoutingFormInstitutionCostShare routingFormInstitutionCostShare, boolean aggregateExisting) {
        if (aggregateExisting) {
            for (RoutingFormInstitutionCostShare institutionCostShare : this.getRoutingFormInstitutionCostShares()) {
                if (institutionCostShare.getChartOfAccountsCode().equals(routingFormInstitutionCostShare.getChartOfAccountsCode()) && institutionCostShare.getOrganizationCode().equals(routingFormInstitutionCostShare.getOrganizationCode())) {

                    institutionCostShare.setRoutingFormCostShareAmount(institutionCostShare.getRoutingFormCostShareAmount().add(routingFormInstitutionCostShare.getRoutingFormCostShareAmount()));

                    return;
                }
            }
        }

        routingFormInstitutionCostShare.setDocumentNumber(this.getDocumentNumber());
        Integer nextSequenceNumber = this.getInstitutionCostShareNextSequenceNumber();
        routingFormInstitutionCostShare.setRoutingFormCostShareSequenceNumber(nextSequenceNumber);
        this.setInstitutionCostShareNextSequenceNumber(++nextSequenceNumber);
        getRoutingFormInstitutionCostShares().add(routingFormInstitutionCostShare);
    }


    /**
     * This method...
     * 
     * @return
     */
    public List<RoutingFormOtherCostShare> getRoutingFormOtherCostShares() {
        return routingFormOtherCostShares;
    }

    /**
     * This method...
     * 
     * @param index
     * @return
     */
    public RoutingFormOtherCostShare getRoutingFormOtherCostShare(int index) {
        while (getRoutingFormOtherCostShares().size() <= index) {
            getRoutingFormOtherCostShares().add(new RoutingFormOtherCostShare());
        }
        return (RoutingFormOtherCostShare) getRoutingFormOtherCostShares().get(index);
    }

    /**
     * This method...
     * 
     * @param routingFormOtherCostShares
     */
    public void setRoutingFormOtherCostShares(List<RoutingFormOtherCostShare> routingFormOtherCostShares) {
        this.routingFormOtherCostShares = routingFormOtherCostShares;
    }

    /**
     * This method...
     * 
     * @param routingFormOtherCostShare
     */
    public void addRoutingFormOtherCostShare(RoutingFormOtherCostShare routingFormOtherCostShare) {
        routingFormOtherCostShare.setDocumentNumber(this.getDocumentNumber());
        Integer nextSequenceNumber = this.getOtherCostShareNextSequenceNumber();
        routingFormOtherCostShare.setRoutingFormCostShareSequenceNumber(nextSequenceNumber);
        this.setOtherCostShareNextSequenceNumber(++nextSequenceNumber);
        getRoutingFormOtherCostShares().add(routingFormOtherCostShare);
    }

    /**
     * This method...
     * 
     * @return
     */
    public List<RoutingFormSubcontractor> getRoutingFormSubcontractors() {
        return routingFormSubcontractors;
    }

    /**
     * This method...
     * 
     * @param index
     * @return
     */
    public RoutingFormSubcontractor getRoutingFormSubcontractor(int index) {
        while (getRoutingFormSubcontractors().size() <= index) {
            getRoutingFormSubcontractors().add(new RoutingFormSubcontractor());
        }
        return (RoutingFormSubcontractor) getRoutingFormSubcontractors().get(index);
    }

    /**
     * This method...
     * 
     * @param routingFormSubcontractors
     */
    public void setRoutingFormSubcontractors(List<RoutingFormSubcontractor> routingFormSubcontractors) {
        this.routingFormSubcontractors = routingFormSubcontractors;
    }

    /**
     * This method...
     * 
     * @param routingFormSubcontractor
     */
    public void addRoutingFormSubcontractor(RoutingFormSubcontractor routingFormSubcontractor) {
        routingFormSubcontractor.setDocumentNumber(this.getDocumentNumber());
        Integer nextSequenceNumber = this.getSubcontractorNextSequenceNumber();
        routingFormSubcontractor.setRoutingFormSubcontractorSequenceNumber(nextSequenceNumber);
        this.setSubcontractorNextSequenceNumber(++nextSequenceNumber);
        getRoutingFormSubcontractors().add(routingFormSubcontractor);

    }

    /**
     * Adds a RoutingFormPersonnel item to the routingFormPersonnel list.
     * 
     * @param routingFormPersonnel
     */
    public void addPerson(RoutingFormPersonnel routingFormPersonnel) {
        routingFormPersonnel.setDocumentNumber(this.getDocumentNumber());

        Integer nextSequenceNumber = this.getPersonnelNextSequenceNumber();
        routingFormPersonnel.setRoutingFormPersonSequenceNumber(nextSequenceNumber);
        this.setPersonnelNextSequenceNumber(++nextSequenceNumber);

        getRoutingFormPersonnel().add(routingFormPersonnel);
    }

    /**
     * Adds a RoutingFormOrganizationCreditPercent item to the routingFormOrganizationCreditPercent list.
     * 
     * @param routingFormOrganizationCreditPercent
     */
    public void addOrganizationCreditPercent(RoutingFormOrganizationCreditPercent routingFormOrganizationCreditPercent) {
        routingFormOrganizationCreditPercent.setDocumentNumber(this.getDocumentNumber());

        getRoutingFormOrganizationCreditPercents().add(routingFormOrganizationCreditPercent);
    }

    /**
     * This method...
     * 
     * @return
     */
    public KualiInteger getTotalInstitutionCostShareAmount() {
        KualiInteger total = KualiInteger.ZERO;
        for (RoutingFormInstitutionCostShare institutionCostShare : this.getRoutingFormInstitutionCostShares()) {
            if (institutionCostShare.getRoutingFormCostShareAmount() != null)
                total = total.add(institutionCostShare.getRoutingFormCostShareAmount());
        }
        return total;
    }

    /**
     * This method...
     * 
     * @return
     */
    public KualiInteger getTotalOtherCostShareAmount() {
        KualiInteger total = KualiInteger.ZERO;
        for (RoutingFormOtherCostShare otherCostShare : getRoutingFormOtherCostShares()) {
            if (otherCostShare.getRoutingFormCostShareAmount() != null)
                total = total.add(otherCostShare.getRoutingFormCostShareAmount());
        }
        return total;
    }

    /**
     * This method...
     * 
     * @return
     */
    public KualiInteger getTotalSubcontractorAmount() {
        KualiInteger total = KualiInteger.ZERO;
        for (RoutingFormSubcontractor subcontractor : getRoutingFormSubcontractors()) {
            if (subcontractor.getRoutingFormSubcontractorAmount() != null)
                total = total.add(subcontractor.getRoutingFormSubcontractorAmount());
        }
        return total;
    }

    /**
     * Gets the federalPassThroughAgency attribute.
     * 
     * @return Returns the federalPassThroughAgency.
     */
    public Agency getFederalPassThroughAgency() {
        return federalPassThroughAgency;
    }

    /**
     * Sets the federalPassThroughAgency attribute value.
     * 
     * @param federalPassThroughAgency The federalPassThroughAgency to set.
     * @deprecated
     */
    public void setFederalPassThroughAgency(Agency federalPassThroughAgency) {
        this.federalPassThroughAgency = federalPassThroughAgency;
    }

    @Override
    public List buildListOfDeletionAwareLists() {
        List list = new ArrayList();

        list.add(this.getRoutingFormSubcontractors());
        list.add(this.getRoutingFormInstitutionCostShares());
        list.add(this.getRoutingFormOtherCostShares());
        list.add(this.getRoutingFormPersonnel());
        list.add(this.getRoutingFormKeywords());
        list.add(this.getRoutingFormResearchRisks());
        list.add(this.getAllRoutingFormResearchRiskStudies());
        list.add(this.getRoutingFormOrganizations());
        list.add(this.getRoutingFormOrganizationCreditPercents());
        list.add(this.getRoutingFormProjectTypes());
        list.add(this.getAdhocOrgs());
        list.add(this.getAdhocPersons());
        list.add(this.getAdhocWorkgroups());

        return list;
    }

    /**
     * Gets the routingFormAgencyToBeNamedIndicator attribute.
     * 
     * @return Returns the routingFormAgencyToBeNamedIndicator.
     */
    public boolean isRoutingFormAgencyToBeNamedIndicator() {
        return routingFormAgencyToBeNamedIndicator;
    }

    /**
     * Sets the routingFormAgencyToBeNamedIndicator attribute value.
     * 
     * @param routingFormAgencyToBeNamedIndicator The routingFormAgencyToBeNamedIndicator to set.
     */
    public void setRoutingFormAgencyToBeNamedIndicator(boolean routingFormAgencyToBeNamedIndicator) {
        this.routingFormAgencyToBeNamedIndicator = routingFormAgencyToBeNamedIndicator;
    }

    /**
     * Gets the routingFormPersonnel attribute.
     * 
     * @return Returns the routingFormPersonnel.
     */
    public List<RoutingFormPersonnel> getRoutingFormPersonnel() {
        return routingFormPersonnel;
    }

    /**
     * Sets the routingFormPersonnel attribute value.
     * 
     * @param routingFormPersonnel The routingFormPersonnel to set.
     */
    public void setRoutingFormPersonnel(List<RoutingFormPersonnel> routingFormPersonnel) {
        this.routingFormPersonnel = routingFormPersonnel;
    }

    /**
     * Gets the routingFormOrganizationCreditPercents attribute.
     * 
     * @return Returns the routingFormOrganizationCreditPercents.
     */
    public List<RoutingFormOrganizationCreditPercent> getRoutingFormOrganizationCreditPercents() {
        return routingFormOrganizationCreditPercents;
    }

    /**
     * Sets the routingFormOrganizationCreditPercents attribute value.
     * 
     * @param routingFormOrganizationCreditPercents The routingFormOrganizationCreditPercents to set.
     */
    public void setRoutingFormOrganizationCreditPercents(List<RoutingFormOrganizationCreditPercent> routingFormOrganizationCreditPercents) {
        this.routingFormOrganizationCreditPercents = routingFormOrganizationCreditPercents;
    }

    /**
     * Gets the routingFormQuestions attribute.
     * 
     * @return Returns the routingFormQuestions.
     */
    public List<RoutingFormQuestion> getRoutingFormQuestions() {
        return routingFormQuestions;
    }

    /**
     * Sets the routingFormQuestions attribute value.
     * 
     * @param routingFormQuestions The routingFormQuestions to set.
     */
    public void setRoutingFormQuestions(List<RoutingFormQuestion> routingFormQuestions) {
        this.routingFormQuestions = routingFormQuestions;
    }

    /**
     * Gets index i from the routingFormQuestions list.
     * 
     * @param index
     * @return Question at index i
     */
    public RoutingFormQuestion getRoutingFormQuestion(int index) {
        while (getRoutingFormQuestions().size() <= index) {
            getRoutingFormQuestions().add(new RoutingFormQuestion());
        }
        return (RoutingFormQuestion) getRoutingFormQuestions().get(index);
    }

    /**
     * Gets the routingFormCatalogOfFederalDomesticAssistanceNumber attribute.
     * 
     * @return Returns the routingFormCatalogOfFederalDomesticAssistanceNumber.
     */
    public String getRoutingFormCatalogOfFederalDomesticAssistanceNumber() {
        return routingFormCatalogOfFederalDomesticAssistanceNumber;
    }

    /**
     * Sets the routingFormCatalogOfFederalDomesticAssistanceNumber attribute value.
     * 
     * @param routingFormCatalogOfFederalDomesticAssistanceNumber The routingFormCatalogOfFederalDomesticAssistanceNumber to set.
     */
    public void setRoutingFormCatalogOfFederalDomesticAssistanceNumber(String routingFormCatalogOfFederalDomesticAssistanceNumber) {
        this.routingFormCatalogOfFederalDomesticAssistanceNumber = routingFormCatalogOfFederalDomesticAssistanceNumber;
    }

    /**
     * Gets the projectTypeOtherDescription attribute.
     * 
     * @return Returns the projectTypeOtherDescription
     */
    public String getProjectTypeOtherDescription() {
        return projectTypeOtherDescription;
    }

    /**
     * Sets the projectTypeOtherDescription attribute.
     * 
     * @param projectTypeOtherDescription The projectTypeOtherDescription to set.
     */
    public void setProjectTypeOtherDescription(String projectTypeOtherDescription) {
        this.projectTypeOtherDescription = projectTypeOtherDescription;
    }

    /**
     * Gets the personnelNextSequenceNumber attribute.
     * 
     * @return Returns the personnelNextSequenceNumber.
     */
    public Integer getPersonnelNextSequenceNumber() {
        return personnelNextSequenceNumber;
    }

    /**
     * Sets the personnelNextSequenceNumber attribute value.
     * 
     * @param personnelNextSequenceNumber The personnelNextSequenceNumber to set.
     */
    public void setPersonnelNextSequenceNumber(Integer personnelNextSequenceNumber) {
        this.personnelNextSequenceNumber = personnelNextSequenceNumber;
    }

    public List<RoutingFormOrganization> getRoutingFormOrganizations() {
        return routingFormOrganizations;
    }

    public void setRoutingFormOrganizations(List<RoutingFormOrganization> routingFormOrganizations) {
        this.routingFormOrganizations = routingFormOrganizations;
    }

    /**
     * Gets the routingFormPurposes attribute.
     * 
     * @return Returns the routingFormPurposes.
     */
    public List<RoutingFormPurpose> getRoutingFormPurposes() {
        return routingFormPurposes;
    }

    /**
     * Sets the routingFormPurposes attribute value.
     * 
     * @param routingFormPurposes The routingFormPurposes to set.
     */
    public void setRoutingFormPurposes(List<RoutingFormPurpose> routingFormPurposes) {
        this.routingFormPurposes = routingFormPurposes;
    }

    /**
     * Gets the routingFormResearchTypeCodes attribute.
     * 
     * @return Returns the routingFormResearchTypeCodes.
     */
    public List<RoutingFormResearchTypeCode> getRoutingFormResearchTypeCodes() {
        return routingFormResearchTypeCodes;
    }

    /**
     * Sets the routingFormResearchTypeCodes attribute value.
     * 
     * @param routingFormResearchTypeCodes The routingFormResearchTypeCodes to set.
     */
    public void setRoutingFormResearchTypeCodes(List<RoutingFormResearchTypeCode> routingFormResearchTypeCodes) {
        this.routingFormResearchTypeCodes = routingFormResearchTypeCodes;
    }

    /**
     * Gets the routingFormProjectTypes attribute.
     * 
     * @return Returns the routingFormProjectTypes.
     */
    public List<RoutingFormProjectType> getRoutingFormProjectTypes() {
        return routingFormProjectTypes;
    }

    /**
     * Sets the routingFormProjectTypes attribute value.
     * 
     * @param routingFormProjectTypes The routingFormProjectTypes to set.
     */
    public void setRoutingFormProjectTypes(List<RoutingFormProjectType> routingFormProjectTypes) {
        this.routingFormProjectTypes = routingFormProjectTypes;
    }

    /**
     * Gets the routingFormPersonRoles attribute.
     * 
     * @return Returns the routingFormPersonRoles.
     */
    public List<RoutingFormPersonRole> getRoutingFormPersonRoles() {
        return routingFormPersonRoles;
    }

    /**
     * Sets the routingFormPersonRoles attribute value.
     * 
     * @param routingFormPersonRoles The routingFormPersonRoles to set.
     */
    public void setRoutingFormPersonRoles(List<RoutingFormPersonRole> routingFormPersonRoles) {
        this.routingFormPersonRoles = routingFormPersonRoles;
    }

    public List<RoutingFormPersonRole> getRoutingFormProjectDirectorRoles() {
        List<RoutingFormPersonRole> projectDirectorRoles = new ArrayList();

        for (RoutingFormPersonRole routingFormPersonRole : getRoutingFormPersonRoles()) {
            if (routingFormPersonRole.getPersonRoleCode().equals(SpringContext.getBean(ParameterService.class).getParameterValue(getClass(), KraConstants.CO_PROJECT_DIRECTOR_PARAM)) || routingFormPersonRole.getPersonRoleCode().equals(SpringContext.getBean(ParameterService.class).getParameterValue(getClass(), KraConstants.PROJECT_DIRECTOR_PARAM))) {
                projectDirectorRoles.add(routingFormPersonRole);
            }
        }

        return projectDirectorRoles;
    }

    public List<RoutingFormPersonRole> getRoutingFormOtherPersonRoles() {
        List<RoutingFormPersonRole> otherPersonRoles = new ArrayList();

        for (RoutingFormPersonRole routingFormPersonRole : getRoutingFormPersonRoles()) {
            if (!routingFormPersonRole.getPersonRoleCode().equals(SpringContext.getBean(ParameterService.class).getParameterValue(getClass(), KraConstants.CO_PROJECT_DIRECTOR_PARAM)) && !routingFormPersonRole.getPersonRoleCode().equals(SpringContext.getBean(ParameterService.class).getParameterValue(getClass(), KraConstants.PROJECT_DIRECTOR_PARAM))) {
                otherPersonRoles.add(routingFormPersonRole);
            }
        }

        return otherPersonRoles;
    }

    public boolean isUserProjectDirector(String personUniversalIdentifier) {
        for (RoutingFormPersonnel person : this.routingFormPersonnel) {
            if (person.isProjectDirector()) {
                return personUniversalIdentifier.equals(person.getPersonUniversalIdentifier());
            }
        }
        return false;
    }

    /**
     * Sums percent financial aid from all personnel and organizations.
     * 
     * @return total financial aid for summary display
     */
    public KualiInteger getTotalFinancialAidPercent() {
        KualiInteger total = KualiInteger.ZERO;
        for (RoutingFormPersonnel routingFormPerson : this.getRoutingFormPersonnel()) {
            if (routingFormPerson.getPersonFinancialAidPercent() != null)
                total = total.add(routingFormPerson.getPersonFinancialAidPercent());
        }
        for (RoutingFormOrganizationCreditPercent routingFormOrganizationCreditPercent : this.getRoutingFormOrganizationCreditPercents()) {
            if (routingFormOrganizationCreditPercent.getOrganizationFinancialAidPercent() != null)
                total = total.add(routingFormOrganizationCreditPercent.getOrganizationFinancialAidPercent());
        }
        return total;
    }

    /**
     * Sums percent credit from all personnel and organizations.
     * 
     * @return total percent credit for summary display
     */
    public KualiInteger getTotalCreditPercent() {
        KualiInteger total = KualiInteger.ZERO;
        for (RoutingFormPersonnel routingFormPerson : this.getRoutingFormPersonnel()) {
            if (routingFormPerson.getPersonCreditPercent() != null)
                total = total.add(routingFormPerson.getPersonCreditPercent());
        }
        for (RoutingFormOrganizationCreditPercent routingFormOrganizationCreditPercent : this.getRoutingFormOrganizationCreditPercents()) {
            if (routingFormOrganizationCreditPercent.getOrganizationCreditPercent() != null)
                total = total.add(routingFormOrganizationCreditPercent.getOrganizationCreditPercent());
        }
        return total;
    }

    /**
     * Gets index i from the routingFormPersonnel list.
     * 
     * @param index
     * @return Person at index i
     */
    public RoutingFormOrganization getRoutingFormOrganization(int index) {
        while (getRoutingFormOrganizations().size() <= index) {
            getRoutingFormOrganizations().add(new RoutingFormOrganization());
        }
        return (RoutingFormOrganization) getRoutingFormOrganizations().get(index);
    }

    public void addRoutingFormOrganization(RoutingFormOrganization routingFormOrganization) {
        routingFormOrganization.setDocumentNumber(this.getDocumentNumber());

        getRoutingFormOrganizations().add(routingFormOrganization);
    }

    @Override
    public void populateDocumentForRouting() {
        KualiTransactionalDocumentInformation transInfo = new KualiTransactionalDocumentInformation();
        DocumentInitiator initiator = new DocumentInitiator();
        String initiatorNetworkId = documentHeader.getWorkflowDocument().getInitiatorNetworkId();
        try {
            UniversalUser initiatorUser = SpringContext.getBean(UniversalUserService.class).getUniversalUser(new AuthenticationUserId(initiatorNetworkId));
            initiator.setUniversalUser(initiatorUser);
        }
        catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
        transInfo.setDocumentInitiator(initiator);
        KualiDocumentXmlMaterializer xmlWrapper = new KualiDocumentXmlMaterializer();
        xmlWrapper.setDocument(this);
        xmlWrapper.setKualiTransactionalDocumentInformation(transInfo);
        documentHeader.getWorkflowDocument().setApplicationContent(generateDocumentContent());
    }

    public String generateDocumentContent() {
        List referenceObjects = new ArrayList();
        referenceObjects.add("adhocPersons");
        referenceObjects.add("adhocOrgs");
        referenceObjects.add("adhocWorkgroups");
        referenceObjects.add("routingFormInstitutionCostShares");
        SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(this, referenceObjects);

        StringBuffer xml = new StringBuffer("<documentContent>");
        xml.append(buildProjectDirectorReportXml(false));
        xml.append(buildAdhocApproverReportXml());
        xml.append(buildCostShareOrgReportXml(false));
        xml.append(buildOtherOrgReportXml(false));
        xml.append(buildAdhocOrgReportXml(false));
        xml.append("</documentContent>");

        return xml.toString();
    }

    /**
     * Build the xml to use when generating the workflow routing report.
     * 
     * @param boolean encloseContent - whether the generated xml should be enclosed within a <documentContent> tag
     * @return String
     */
    public String buildProjectDirectorReportXml(boolean encloseContent) {
        StringBuffer xml = new StringBuffer();
        if (encloseContent) {
            xml.append("<documentContent>");
        }
        RoutingFormPersonnel projectDirector = null;

        SpringContext.getBean(PersistenceService.class).retrieveReferenceObject(this, "routingFormPersonnel");
        for (RoutingFormPersonnel user : this.getRoutingFormPersonnel()) {
            if (ObjectUtils.isNotNull(user.getPersonRoleCode()) && user.isProjectDirector()) {
                projectDirector = user;
            }
            else if (ObjectUtils.isNotNull(user.getPersonRoleCode()) && user.getPersonRoleCode().equals(KraConstants.CO_PROJECT_DIRECTOR_CODE)) {
                if (!StringUtils.isBlank(user.getChartOfAccountsCode())) {
                    xml.append("<chartOrg><chartOfAccountsCode>");
                    xml.append(user.getChartOfAccountsCode());
                    xml.append("</chartOfAccountsCode><organizationCode>");
                    xml.append(user.getOrganizationCode());
                    xml.append("</organizationCode></chartOrg>");
                }
            }
        }

        if (ObjectUtils.isNotNull(projectDirector) && ObjectUtils.isNotNull(projectDirector.getUser())) {
            xml.append("<projectDirector>");
            xml.append(projectDirector.getPersonUniversalIdentifier());
            xml.append("</projectDirector>");
            if (!StringUtils.isBlank(projectDirector.getChartOfAccountsCode())) {
                xml.append("<chartOrg><chartOfAccountsCode>");
                xml.append(projectDirector.getChartOfAccountsCode());
                xml.append("</chartOfAccountsCode><organizationCode>");
                if (StringUtils.isBlank(projectDirector.getOrganizationCode())) {
                    xml.append(SpringContext.getBean(ChartUserService.class).getDefaultOrganizationCode(projectDirector.getUser()));
                }
                else {
                    xml.append(projectDirector.getOrganizationCode());
                }
                xml.append("</organizationCode></chartOrg>");
            }
        }
        if (encloseContent) {
            xml.append("</documentContent>");
        }
        return xml.toString();
    }

    public String buildAdhocApproverReportXml() {
        StringBuffer xml = new StringBuffer();
        List<AdhocPerson> people = this.getAdhocPersons();
        for (AdhocPerson person : people) {
            xml.append("<adhocApprover>");
            xml.append(person.getPersonUniversalIdentifier());
            xml.append("</adhocApprover>");
        }
        return xml.toString();
    }

    /**
     * Build the xml to use when generating the workflow org routing report.
     * 
     * @param boolean encloseContent - whether the generated xml should be enclosed within a <documentContent> tag
     * @return String
     */
    public String buildCostShareOrgReportXml(boolean encloseContent) {

        String costSharePermissionCode = SpringContext.getBean(ParameterService.class).getParameterValue(getClass(), KraConstants.ROUTING_FORM_COST_SHARE_PERMISSION_CODE);

        StringBuffer xml = new StringBuffer();
        if (encloseContent) {
            xml.append("<documentContent>");
        }

        if ("Y".equals(costSharePermissionCode)) {
            for (RoutingFormInstitutionCostShare costShare : this.getRoutingFormInstitutionCostShares()) {
                xml.append("<chartOrg><chartOfAccountsCode>");
                if (costShare.getChartOfAccountsCode() != null) {
                    xml.append(costShare.getChartOfAccountsCode());
                }
                xml.append("</chartOfAccountsCode><organizationCode>");
                if (costShare.getOrganizationCode() != null) {
                    xml.append(costShare.getOrganizationCode());
                }
                xml.append("</organizationCode></chartOrg>");
            }
        }

        if (encloseContent) {
            xml.append("</documentContent>");
        }

        return xml.toString();
    }

    /**
     * Build the xml to use when generating the workflow org routing report.
     * 
     * @param boolean encloseContent - whether the generated xml should be enclosed within a <documentContent> tag
     * @return String
     */
    public String buildOtherOrgReportXml(boolean encloseContent) {

        StringBuffer xml = new StringBuffer();
        if (encloseContent) {
            xml.append("<documentContent>");
        }

        for (RoutingFormOrganization otherOrg : this.getRoutingFormOrganizations()) {
            xml.append("<chartOrg><chartOfAccountsCode>");
            if (otherOrg.getChartOfAccountsCode() != null) {
                xml.append(otherOrg.getChartOfAccountsCode());
            }
            xml.append("</chartOfAccountsCode><organizationCode>");
            if (otherOrg.getOrganizationCode() != null) {
                xml.append(otherOrg.getOrganizationCode());
            }
            xml.append("</organizationCode></chartOrg>");
        }

        if (encloseContent) {
            xml.append("</documentContent>");
        }

        return xml.toString();
    }

    /**
     * Build the xml to use when generating the workflow org routing report.
     * 
     * @param List<BudgetAdHocOrg> orgs
     * @param boolean encloseContent - whether the generated xml should be enclosed within a <documentContent> tag
     * @return String
     */
    public String buildAdhocOrgReportXml(boolean encloseContent) {
        StringBuffer xml = new StringBuffer();
        if (encloseContent) {
            xml.append("<documentContent>");
        }
        List<AdhocOrg> orgs = this.getAdhocOrgs();
        for (AdhocOrg org : orgs) {
            xml.append("<chartOrg><chartOfAccountsCode>");
            xml.append(org.getFiscalCampusCode());
            xml.append("</chartOfAccountsCode><organizationCode>");
            xml.append(org.getPrimaryDepartmentCode());
            xml.append("</organizationCode></chartOrg>");
        }
        if (encloseContent) {
            xml.append("</documentContent>");
        }
        return xml.toString();
    }

    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
    }
}
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

import org.kuali.PropertyConstants;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.cg.bo.Agency;
import org.kuali.module.cg.bo.CatalogOfFederalDomesticAssistanceReference;
import org.kuali.module.chart.bo.Campus;
import org.kuali.module.kra.document.ResearchDocumentBase;
import org.kuali.module.kra.routingform.bo.ContractGrantProposal;
import org.kuali.module.kra.routingform.bo.Purpose;
import org.kuali.module.kra.routingform.bo.ResearchTypeCode;
import org.kuali.module.kra.routingform.bo.RoutingFormAdHocOrg;
import org.kuali.module.kra.routingform.bo.RoutingFormAdHocPerson;
import org.kuali.module.kra.routingform.bo.RoutingFormAdHocWorkgroup;
import org.kuali.module.kra.routingform.bo.RoutingFormAgency;
import org.kuali.module.kra.routingform.bo.RoutingFormBudget;
import org.kuali.module.kra.routingform.bo.RoutingFormInstitutionCostShare;
import org.kuali.module.kra.routingform.bo.RoutingFormKeyword;
import org.kuali.module.kra.routingform.bo.RoutingFormOrganization;
import org.kuali.module.kra.routingform.bo.RoutingFormOrganizationCreditPercent;
import org.kuali.module.kra.routingform.bo.RoutingFormOtherCostShare;
import org.kuali.module.kra.routingform.bo.RoutingFormPersonnel;
import org.kuali.module.kra.routingform.bo.RoutingFormProjectType;
import org.kuali.module.kra.routingform.bo.RoutingFormQuestion;
import org.kuali.module.kra.routingform.bo.RoutingFormResearchRisk;
import org.kuali.module.kra.routingform.bo.RoutingFormStatus;
import org.kuali.module.kra.routingform.bo.RoutingFormSubcontractor;
import org.kuali.module.kra.routingform.bo.SubmissionType;

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
	private Long routingFormLastUpdateSystemIdentifier;
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
    private String submissionTypeCode;
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
    private CatalogOfFederalDomesticAssistanceReference catalogOfFederalDomesticAssistanceReference;
    private List<RoutingFormResearchRisk> routingFormResearchRisks;
    private List<RoutingFormKeyword> routingFormKeywords;
    private RoutingFormBudget routingFormBudget;
    private ResearchTypeCode researchType;
    private SubmissionType submissionType;
    private ContractGrantProposal contractGrantProposal;
    private List<RoutingFormInstitutionCostShare> routingFormInstitutionCostShares;
    private List<RoutingFormOtherCostShare> routingFormOtherCostShares;
    private List<RoutingFormSubcontractor> routingFormSubcontractors;
    private Agency federalPassThroughAgency;
    private List<RoutingFormPersonnel> routingFormPersonnel;
    private List<RoutingFormOrganizationCreditPercent> routingFormOrganizationCreditPercents;
    private List<RoutingFormQuestion> routingFormQuestions;
    private List<RoutingFormOrganization> routingFormOrganizations;
    private List<RoutingFormProjectType> routingFormProjectTypes;
    
    private List<RoutingFormAdHocPerson> routingFormAdHocPeople;
    private List<RoutingFormAdHocOrg> routingFormAdHocOrgs;
    private List<RoutingFormAdHocWorkgroup> routingFormAdHocWorkgroups;
    
    //Sequence numbers for keeping track of the 'next' number
    private Integer routingFormNextInstitutionCostShareSequenceNumber;
    private Integer routingFormNextOtherCostShareSequenceNumber;
    private Integer routingFormNextSubcontractorSequenceNumber;
    
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
        routingFormKeywords = new ArrayList<RoutingFormKeyword>();
        routingFormInstitutionCostShares = new ArrayList<RoutingFormInstitutionCostShare>();
        routingFormOtherCostShares = new ArrayList<RoutingFormOtherCostShare>();
        routingFormSubcontractors = new ArrayList<RoutingFormSubcontractor>();
        routingFormPersonnel = new ArrayList<RoutingFormPersonnel>();
        routingFormOrganizationCreditPercents = new ArrayList<RoutingFormOrganizationCreditPercent>();
        routingFormQuestions = new ArrayList<RoutingFormQuestion>();
        routingFormOrganizations = new ArrayList<RoutingFormOrganization>();
        routingFormProjectTypes = new ArrayList<RoutingFormProjectType>();
        
        routingFormAdHocPeople = new ArrayList<RoutingFormAdHocPerson>();
        routingFormAdHocOrgs = new ArrayList<RoutingFormAdHocOrg>();
        routingFormAdHocWorkgroups = new ArrayList<RoutingFormAdHocWorkgroup>();
	}

    public void initialize() {
        this.setRoutingFormCreateDate(SpringServiceLocator.getDateTimeService().getCurrentSqlDate());
    }

    @Override
    public void handleRouteStatusChange() {
        super.handleRouteStatusChange();

        //TODO Need to handle logic for the RF Type: Copy into Proposal if the type is New, Renewal, Renewal Previous Commit, or Supplemental Funds
        if (super.getDocumentHeader().getWorkflowDocument().stateIsApproved()) {
            //Logic to determine whether or not this RF should become a C&G Proposal
            if (this.getContractGrantProposal().getProposalNumber() == null) {
                SpringServiceLocator.getProposalService().createAndRouteProposalMaintenanceDocument(this);
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
            SpringServiceLocator.getRoutingFormResearchRiskService().setupResearchRisks(this);
        }
        
        // Setup project details questions if this is the first save
        if (this.routingFormQuestions.isEmpty()) {
            SpringServiceLocator.getRoutingFormProjectDetailsService().setupOtherProjectDetailsQuestions(this);
        }
    }

    /**
	 * Gets the agencyAdditionalShippingInstructionsIndicator attribute.
	 * 
	 * @return Returns the agencyAdditionalShippingInstructionsIndicator
	 * 
	 */
	public boolean getAgencyAdditionalShippingInstructionsIndicator() { 
		return agencyAdditionalShippingInstructionsIndicator;
	}

	/**
	 * Sets the agencyAdditionalShippingInstructionsIndicator attribute.
	 * 
	 * @param agencyAdditionalShippingInstructionsIndicator The agencyAdditionalShippingInstructionsIndicator to set.
	 * 
	 */
	public void setAgencyAdditionalShippingInstructionsIndicator(boolean agencyAdditionalShippingInstructionsIndicator) {
		this.agencyAdditionalShippingInstructionsIndicator = agencyAdditionalShippingInstructionsIndicator;
	}


	/**
	 * Gets the agencyFederalPassThroughNotAvailableIndicator attribute.
	 * 
	 * @return Returns the agencyFederalPassThroughNotAvailableIndicator
	 * 
	 */
	public boolean getAgencyFederalPassThroughNotAvailableIndicator() { 
		return agencyFederalPassThroughNotAvailableIndicator;
	}

	/**
	 * Sets the agencyFederalPassThroughNotAvailableIndicator attribute.
	 * 
	 * @param agencyFederalPassThroughNotAvailableIndicator The agencyFederalPassThroughNotAvailableIndicator to set.
	 * 
	 */
	public void setAgencyFederalPassThroughNotAvailableIndicator(boolean agencyFederalPassThroughNotAvailableIndicator) {
		this.agencyFederalPassThroughNotAvailableIndicator = agencyFederalPassThroughNotAvailableIndicator;
	}


	/**
	 * Gets the agencyFederalPassThroughNumber attribute.
	 * 
	 * @return Returns the agencyFederalPassThroughNumber
	 * 
	 */
	public String getAgencyFederalPassThroughNumber() { 
		return agencyFederalPassThroughNumber;
	}

	/**
	 * Sets the agencyFederalPassThroughNumber attribute.
	 * 
	 * @param agencyFederalPassThroughNumber The agencyFederalPassThroughNumber to set.
	 * 
	 */
	public void setAgencyFederalPassThroughNumber(String agencyFederalPassThroughNumber) {
		this.agencyFederalPassThroughNumber = agencyFederalPassThroughNumber;
	}
    


	/**
	 * Gets the grantNumber attribute.
	 * 
	 * @return Returns the grantNumber
	 * 
	 */
	public String getGrantNumber() { 
		return grantNumber;
	}

	/**
	 * Sets the grantNumber attribute.
	 * 
	 * @param grantNumber The grantNumber to set.
	 * 
	 */
	public void setGrantNumber(String grantNumber) {
		this.grantNumber = grantNumber;
	}


	/**
	 * Gets the routingFormAnnouncementNumber attribute.
	 * 
	 * @return Returns the routingFormAnnouncementNumber
	 * 
	 */
	public String getRoutingFormAnnouncementNumber() { 
		return routingFormAnnouncementNumber;
	}

	/**
	 * Sets the routingFormAnnouncementNumber attribute.
	 * 
	 * @param routingFormAnnouncementNumber The routingFormAnnouncementNumber to set.
	 * 
	 */
	public void setRoutingFormAnnouncementNumber(String routingFormAnnouncementNumber) {
		this.routingFormAnnouncementNumber = routingFormAnnouncementNumber;
	}


	/**
	 * Gets the routingFormBudgetNumber attribute.
	 * 
	 * @return Returns the routingFormBudgetNumber
	 * 
	 */
	public String getRoutingFormBudgetNumber() { 
		return routingFormBudgetNumber;
	}

	/**
	 * Sets the routingFormBudgetNumber attribute.
	 * 
	 * @param routingFormBudgetNumber The routingFormBudgetNumber to set.
	 * 
	 */
	public void setRoutingFormBudgetNumber(String routingFormBudgetNumber) {
		this.routingFormBudgetNumber = routingFormBudgetNumber;
	}


	/**
	 * Gets the routingFormConflictOfInterestCurrentIndicator attribute.
	 * 
	 * @return Returns the routingFormConflictOfInterestCurrentIndicator
	 * 
	 */
	public boolean getRoutingFormConflictOfInterestCurrentIndicator() { 
		return routingFormConflictOfInterestCurrentIndicator;
	}

	/**
	 * Sets the routingFormConflictOfInterestCurrentIndicator attribute.
	 * 
	 * @param routingFormConflictOfInterestCurrentIndicator The routingFormConflictOfInterestCurrentIndicator to set.
	 * 
	 */
	public void setRoutingFormConflictOfInterestCurrentIndicator(boolean routingFormConflictOfInterestCurrentIndicator) {
		this.routingFormConflictOfInterestCurrentIndicator = routingFormConflictOfInterestCurrentIndicator;
	}


	/**
	 * Gets the routingFormConflictOfInterestExistsIndicator attribute.
	 * 
	 * @return Returns the routingFormConflictOfInterestExistsIndicator
	 * 
	 */
	public boolean getRoutingFormConflictOfInterestExistsIndicator() { 
		return routingFormConflictOfInterestExistsIndicator;
	}

	/**
	 * Sets the routingFormConflictOfInterestExistsIndicator attribute.
	 * 
	 * @param routingFormConflictOfInterestExistsIndicator The routingFormConflictOfInterestExistsIndicator to set.
	 * 
	 */
	public void setRoutingFormConflictOfInterestExistsIndicator(boolean routingFormConflictOfInterestExistsIndicator) {
		this.routingFormConflictOfInterestExistsIndicator = routingFormConflictOfInterestExistsIndicator;
	}


	/**
	 * Gets the routingFormCoProjectDirectorIndicator attribute.
	 * 
	 * @return Returns the routingFormCoProjectDirectorIndicator
	 * 
	 */
	public boolean getRoutingFormCoProjectDirectorIndicator() { 
		return routingFormCoProjectDirectorIndicator;
	}

	/**
	 * Sets the routingFormCoProjectDirectorIndicator attribute.
	 * 
	 * @param routingFormCoProjectDirectorIndicator The routingFormCoProjectDirectorIndicator to set.
	 * 
	 */
	public void setRoutingFormCoProjectDirectorIndicator(boolean routingFormCoProjectDirectorIndicator) {
		this.routingFormCoProjectDirectorIndicator = routingFormCoProjectDirectorIndicator;
	}


	/**
	 * Gets the routingFormCreditPercentIndicator attribute.
	 * 
	 * @return Returns the routingFormCreditPercentIndicator
	 * 
	 */
	public boolean getRoutingFormCreditPercentIndicator() { 
		return routingFormCreditPercentIndicator;
	}

	/**
	 * Sets the routingFormCreditPercentIndicator attribute.
	 * 
	 * @param routingFormCreditPercentIndicator The routingFormCreditPercentIndicator to set.
	 * 
	 */
	public void setRoutingFormCreditPercentIndicator(boolean routingFormCreditPercentIndicator) {
		this.routingFormCreditPercentIndicator = routingFormCreditPercentIndicator;
	}


	/**
	 * Gets the routingFormCreateDate attribute.
	 * 
	 * @return Returns the routingFormCreateDate
	 * 
	 */
	public Date getRoutingFormCreateDate() { 
		return routingFormCreateDate;
	}

	/**
	 * Sets the routingFormCreateDate attribute.
	 * 
	 * @param routingFormCreateDate The routingFormCreateDate to set.
	 * 
	 */
	public void setRoutingFormCreateDate(Date routingFormCreateDate) {
		this.routingFormCreateDate = routingFormCreateDate;
	}


	/**
	 * Gets the routingFormCostShareIndicator attribute.
	 * 
	 * @return Returns the routingFormCostShareIndicator
	 * 
	 */
	public boolean getRoutingFormCostShareIndicator() { 
		return routingFormCostShareIndicator;
	}

	/**
	 * Sets the routingFormCostShareIndicator attribute.
	 * 
	 * @param routingFormCostShareIndicator The routingFormCostShareIndicator to set.
	 * 
	 */
	public void setRoutingFormCostShareIndicator(boolean routingFormCostShareIndicator) {
		this.routingFormCostShareIndicator = routingFormCostShareIndicator;
	}


	/**
	 * Gets the routingFormFederalPassThroughIndicator attribute.
	 * 
	 * @return Returns the routingFormFederalPassThroughIndicator
	 * 
	 */
	public boolean getRoutingFormFederalPassThroughIndicator() { 
		return routingFormFederalPassThroughIndicator;
	}

	/**
	 * Sets the routingFormFederalPassThroughIndicator attribute.
	 * 
	 * @param routingFormFederalPassThroughIndicator The routingFormFederalPassThroughIndicator to set.
	 * 
	 */
	public void setRoutingFormFederalPassThroughIndicator(boolean routingFormFederalPassThroughIndicator) {
		this.routingFormFederalPassThroughIndicator = routingFormFederalPassThroughIndicator;
	}


	/**
	 * Gets the routingFormFellowFirstName attribute.
	 * 
	 * @return Returns the routingFormFellowFirstName
	 * 
	 */
	public String getRoutingFormFellowFirstName() { 
		return routingFormFellowFirstName;
	}

	/**
	 * Sets the routingFormFellowFirstName attribute.
	 * 
	 * @param routingFormFellowFirstName The routingFormFellowFirstName to set.
	 * 
	 */
	public void setRoutingFormFellowFirstName(String routingFormFellowFirstName) {
		this.routingFormFellowFirstName = routingFormFellowFirstName;
	}


	/**
	 * Gets the routingFormFellowEmailAddress attribute.
	 * 
	 * @return Returns the routingFormFellowEmailAddress
	 * 
	 */
	public String getRoutingFormFellowEmailAddress() { 
		return routingFormFellowEmailAddress;
	}

	/**
	 * Sets the routingFormFellowEmailAddress attribute.
	 * 
	 * @param routingFormFellowEmailAddress The routingFormFellowEmailAddress to set.
	 * 
	 */
	public void setRoutingFormFellowEmailAddress(String routingFormFellowEmailAddress) {
		this.routingFormFellowEmailAddress = routingFormFellowEmailAddress;
	}


	/**
	 * Gets the routingFormFellowFullName attribute.
	 * 
	 * @return Returns the routingFormFellowFullName
	 * 
	 */
	public String getRoutingFormFellowFullName() { 
		return routingFormFellowFullName;
	}

	/**
	 * Sets the routingFormFellowFullName attribute.
	 * 
	 * @param routingFormFellowFullName The routingFormFellowFullName to set.
	 * 
	 */
	public void setRoutingFormFellowFullName(String routingFormFellowFullName) {
		this.routingFormFellowFullName = routingFormFellowFullName;
	}


	/**
	 * Gets the routingFormFellowLastName attribute.
	 * 
	 * @return Returns the routingFormFellowLastName
	 * 
	 */
	public String getRoutingFormFellowLastName() { 
		return routingFormFellowLastName;
	}

	/**
	 * Sets the routingFormFellowLastName attribute.
	 * 
	 * @param routingFormFellowLastName The routingFormFellowLastName to set.
	 * 
	 */
	public void setRoutingFormFellowLastName(String routingFormFellowLastName) {
		this.routingFormFellowLastName = routingFormFellowLastName;
	}


	/**
	 * Gets the routingFormLayDescription attribute.
	 * 
	 * @return Returns the routingFormLayDescription
	 * 
	 */
	public String getRoutingFormLayDescription() { 
		return routingFormLayDescription;
	}

	/**
	 * Sets the routingFormLayDescription attribute.
	 * 
	 * @param routingFormLayDescription The routingFormLayDescription to set.
	 * 
	 */
	public void setRoutingFormLayDescription(String routingFormLayDescription) {
		this.routingFormLayDescription = routingFormLayDescription;
	}


	/**
	 * Gets the routingFormLastUpdateDate attribute.
	 * 
	 * @return Returns the routingFormLastUpdateDate
	 * 
	 */
	public Date getRoutingFormLastUpdateDate() { 
		return routingFormLastUpdateDate;
	}

	/**
	 * Sets the routingFormLastUpdateDate attribute.
	 * 
	 * @param routingFormLastUpdateDate The routingFormLastUpdateDate to set.
	 * 
	 */
	public void setRoutingFormLastUpdateDate(Date routingFormLastUpdateDate) {
		this.routingFormLastUpdateDate = routingFormLastUpdateDate;
	}


	/**
	 * Gets the routingFormLastUpdateSystemIdentifier attribute.
	 * 
	 * @return Returns the routingFormLastUpdateSystemIdentifier
	 * 
	 */
	public Long getRoutingFormLastUpdateSystemIdentifier() { 
		return routingFormLastUpdateSystemIdentifier;
	}

	/**
	 * Sets the routingFormLastUpdateSystemIdentifier attribute.
	 * 
	 * @param routingFormLastUpdateSystemIdentifier The routingFormLastUpdateSystemIdentifier to set.
	 * 
	 */
	public void setRoutingFormLastUpdateSystemIdentifier(Long routingFormLastUpdateSystemIdentifier) {
		this.routingFormLastUpdateSystemIdentifier = routingFormLastUpdateSystemIdentifier;
	}


	/**
	 * Gets the routingFormOtherOrganizationIndicator attribute.
	 * 
	 * @return Returns the routingFormOtherOrganizationIndicator
	 * 
	 */
	public boolean getRoutingFormOtherOrganizationIndicator() { 
		return routingFormOtherOrganizationIndicator;
	}

	/**
	 * Sets the routingFormOtherOrganizationIndicator attribute.
	 * 
	 * @param routingFormOtherOrganizationIndicator The routingFormOtherOrganizationIndicator to set.
	 * 
	 */
	public void setRoutingFormOtherOrganizationIndicator(boolean routingFormOtherOrganizationIndicator) {
		this.routingFormOtherOrganizationIndicator = routingFormOtherOrganizationIndicator;
	}


	/**
	 * Gets the routingFormOtherPurposeDescription attribute.
	 * 
	 * @return Returns the routingFormOtherPurposeDescription
	 * 
	 */
	public String getRoutingFormOtherPurposeDescription() { 
		return routingFormOtherPurposeDescription;
	}

	/**
	 * Sets the routingFormOtherPurposeDescription attribute.
	 * 
	 * @param routingFormOtherPurposeDescription The routingFormOtherPurposeDescription to set.
	 * 
	 */
	public void setRoutingFormOtherPurposeDescription(String routingFormOtherPurposeDescription) {
		this.routingFormOtherPurposeDescription = routingFormOtherPurposeDescription;
	}


	/**
	 * Gets the routingFormOtherTypeDescription attribute.
	 * 
	 * @return Returns the routingFormOtherTypeDescription
	 * 
	 */
	public String getRoutingFormOtherTypeDescription() { 
		return routingFormOtherTypeDescription;
	}

	/**
	 * Sets the routingFormOtherTypeDescription attribute.
	 * 
	 * @param routingFormOtherTypeDescription The routingFormOtherTypeDescription to set.
	 * 
	 */
	public void setRoutingFormOtherTypeDescription(String routingFormOtherTypeDescription) {
		this.routingFormOtherTypeDescription = routingFormOtherTypeDescription;
	}


	/**
	 * Gets the routingFormParentNumber attribute.
	 * 
	 * @return Returns the routingFormParentNumber
	 * 
	 */
	public String getRoutingFormParentNumber() { 
		return routingFormParentNumber;
	}

	/**
	 * Sets the routingFormParentNumber attribute.
	 * 
	 * @param routingFormParentNumber The routingFormParentNumber to set.
	 * 
	 */
	public void setRoutingFormParentNumber(String routingFormParentNumber) {
		this.routingFormParentNumber = routingFormParentNumber;
	}


	/**
	 * Gets the routingFormPhysicalCampusCode attribute.
	 * 
	 * @return Returns the routingFormPhysicalCampusCode
	 * 
	 */
	public String getRoutingFormPhysicalCampusCode() { 
		return routingFormPhysicalCampusCode;
	}

	/**
	 * Sets the routingFormPhysicalCampusCode attribute.
	 * 
	 * @param routingFormPhysicalCampusCode The routingFormPhysicalCampusCode to set.
	 * 
	 */
	public void setRoutingFormPhysicalCampusCode(String routingFormPhysicalCampusCode) {
		this.routingFormPhysicalCampusCode = routingFormPhysicalCampusCode;
	}


	/**
	 * Gets the routingFormPriorGrantNumber attribute.
	 * 
	 * @return Returns the routingFormPriorGrantNumber
	 * 
	 */
	public String getRoutingFormPriorGrantNumber() { 
		return routingFormPriorGrantNumber;
	}

	/**
	 * Sets the routingFormPriorGrantNumber attribute.
	 * 
	 * @param routingFormPriorGrantNumber The routingFormPriorGrantNumber to set.
	 * 
	 */
	public void setRoutingFormPriorGrantNumber(String routingFormPriorGrantNumber) {
		this.routingFormPriorGrantNumber = routingFormPriorGrantNumber;
	}


	/**
	 * Gets the routingFormProjectTitle attribute.
	 * 
	 * @return Returns the routingFormProjectTitle
	 * 
	 */
	public String getRoutingFormProjectTitle() { 
		return routingFormProjectTitle;
	}

	/**
	 * Sets the routingFormProjectTitle attribute.
	 * 
	 * @param routingFormProjectTitle The routingFormProjectTitle to set.
	 * 
	 */
	public void setRoutingFormProjectTitle(String routingFormProjectTitle) {
		this.routingFormProjectTitle = routingFormProjectTitle;
	}


	/**
	 * Gets the routingFormPurposeCode attribute.
	 * 
	 * @return Returns the routingFormPurposeCode
	 * 
	 */
	public String getRoutingFormPurposeCode() { 
		return routingFormPurposeCode;
	}

	/**
	 * Sets the routingFormPurposeCode attribute.
	 * 
	 * @param routingFormPurposeCode The routingFormPurposeCode to set.
	 * 
	 */
	public void setRoutingFormPurposeCode(String routingFormPurposeCode) {
		this.routingFormPurposeCode = routingFormPurposeCode;
	}

    /**
	 * Gets the routingFormSpaceRequiredDescription attribute.
	 * 
	 * @return Returns the routingFormSpaceRequiredDescription
	 * 
	 */
	public String getRoutingFormSpaceRequiredDescription() { 
		return routingFormSpaceRequiredDescription;
	}

	/**
	 * Sets the routingFormSpaceRequiredDescription attribute.
	 * 
	 * @param routingFormSpaceRequiredDescription The routingFormSpaceRequiredDescription to set.
	 * 
	 */
	public void setRoutingFormSpaceRequiredDescription(String routingFormSpaceRequiredDescription) {
		this.routingFormSpaceRequiredDescription = routingFormSpaceRequiredDescription;
	}


	/**
	 * Gets the routingFormSpaceRequiredIndicator attribute.
	 * 
	 * @return Returns the routingFormSpaceRequiredIndicator
	 * 
	 */
	public boolean getRoutingFormSpaceRequiredIndicator() { 
		return routingFormSpaceRequiredIndicator;
	}

	/**
	 * Sets the routingFormSpaceRequiredIndicator attribute.
	 * 
	 * @param routingFormSpaceRequiredIndicator The routingFormSpaceRequiredIndicator to set.
	 * 
	 */
	public void setRoutingFormSpaceRequiredIndicator(boolean routingFormSpaceRequiredIndicator) {
		this.routingFormSpaceRequiredIndicator = routingFormSpaceRequiredIndicator;
	}


	/**
	 * Gets the routingFormStatusCode attribute.
	 * 
	 * @return Returns the routingFormStatusCode
	 * 
	 */
	public String getRoutingFormStatusCode() { 
		return routingFormStatusCode;
	}

	/**
	 * Sets the routingFormStatusCode attribute.
	 * 
	 * @param routingFormStatusCode The routingFormStatusCode to set.
	 * 
	 */
	public void setRoutingFormStatusCode(String routingFormStatusCode) {
		this.routingFormStatusCode = routingFormStatusCode;
	}


	/**
	 * Gets the routingFormSubcontractorIndicator attribute.
	 * 
	 * @return Returns the routingFormSubcontractorIndicator
	 * 
	 */
	public boolean getRoutingFormSubcontractorIndicator() { 
		return routingFormSubcontractorIndicator;
	}

	/**
	 * Sets the routingFormSubcontractorIndicator attribute.
	 * 
	 * @param routingFormSubcontractorIndicator The routingFormSubcontractorIndicator to set.
	 * 
	 */
	public void setRoutingFormSubcontractorIndicator(boolean routingFormSubcontractorIndicator) {
		this.routingFormSubcontractorIndicator = routingFormSubcontractorIndicator;
	}

	/**
	 * Gets the institutionAccountNumber attribute.
	 * 
	 * @return Returns the institutionAccountNumber
	 * 
	 */
	public String getInstitutionAccountNumber() { 
		return institutionAccountNumber;
	}

	/**
	 * Sets the institutionAccountNumber attribute.
	 * 
	 * @param institutionAccountNumber The institutionAccountNumber to set.
	 * 
	 */
	public void setInstitutionAccountNumber(String institutionAccountNumber) {
		this.institutionAccountNumber = institutionAccountNumber;
	}

    
    /**
     * Gets the researchTypeCode attribute.
     * 
     * @return Returns the researchTypeCode
     * 
     */
    public String getResearchTypeCode() { 
        return researchTypeCode;
    }

    /**
     * Sets the researchTypeCode attribute.
     * 
     * @param researchTypeCode The researchTypeCode to set.
     * 
     */
    public void setResearchTypeCode(String researchTypeCode) {
        this.researchTypeCode = researchTypeCode;
    }


    /**
     * Gets the submissionTypeCode attribute.
     * 
     * @return Returns the submissionTypeCode
     * 
     */
    public String getSubmissionTypeCode() { 
        return submissionTypeCode;
    }

    /**
     * Sets the submissionTypeCode attribute.
     * 
     * @param submissionTypeCode The submissionTypeCode to set.
     * 
     */
    public void setSubmissionTypeCode(String submissionTypeCode) {
        this.submissionTypeCode = submissionTypeCode;
    }


    /**
     * Gets the previousFederalIdentifier attribute.
     * 
     * @return Returns the previousFederalIdentifier
     * 
     */
    public String getPreviousFederalIdentifier() { 
        return previousFederalIdentifier;
    }

    /**
     * Sets the previousFederalIdentifier attribute.
     * 
     * @param previousFederalIdentifier The previousFederalIdentifier to set.
     * 
     */
    public void setPreviousFederalIdentifier(String previousFederalIdentifier) {
        this.previousFederalIdentifier = previousFederalIdentifier;
    }    

    
	/**
	 * Gets the routingFormPhysicalCampus attribute.
	 * 
	 * @return Returns the routingFormPhysicalCampus
	 * 
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
     * @return Returns the routingFormStatus.
     */
    public RoutingFormStatus getRoutingFormStatus() {
        return routingFormStatus;
    }

    /**
     * Sets the routingFormStatus attribute value.
     * @param routingFormStatus The routingFormStatus to set.
     * @deprecated
     */
    public void setRoutingFormStatus(RoutingFormStatus routingFormStatus) {
        this.routingFormStatus = routingFormStatus;
    }

    /**
     * Gets the purpose attribute. 
     * @return Returns the purpose.
     */
    public Purpose getPurpose() {
        return purpose;
    }

    /**
     * Sets the purpose attribute value.
     * @param purpose The purpose to set.
     * @deprecated
     */
    public void setPurpose(Purpose purpose) {
        this.purpose = purpose;
    }    

    /**
     * Gets the creditPercentNextSequenceNumber attribute. 
     * @return Returns the creditPercentNextSequenceNumber.
     */
    public Integer getCreditPercentNextSequenceNumber() {
        return creditPercentNextSequenceNumber;
    }

    /**
     * Sets the creditPercentNextSequenceNumber attribute value.
     * @param creditPercentNextSequenceNumber The creditPercentNextSequenceNumber to set.
     */
    public void setCreditPercentNextSequenceNumber(Integer creditPercentNextSequenceNumber) {
        this.creditPercentNextSequenceNumber = creditPercentNextSequenceNumber;
    }

    /**
     * Gets the institutionCostShareNextSequenceNumber attribute. 
     * @return Returns the institutionCostShareNextSequenceNumber.
     */
    public Integer getInstitutionCostShareNextSequenceNumber() {
        return institutionCostShareNextSequenceNumber;
    }

    /**
     * Sets the institutionCostShareNextSequenceNumber attribute value.
     * @param institutionCostShareNextSequenceNumber The institutionCostShareNextSequenceNumber to set.
     */
    public void setInstitutionCostShareNextSequenceNumber(Integer institutionCostShareNextSequenceNumber) {
        this.institutionCostShareNextSequenceNumber = institutionCostShareNextSequenceNumber;
    }

    /**
     * Gets the otherCostShareNextSequenceNumber attribute. 
     * @return Returns the otherCostShareNextSequenceNumber.
     */
    public Integer getOtherCostShareNextSequenceNumber() {
        return otherCostShareNextSequenceNumber;
    }

    /**
     * Sets the otherCostShareNextSequenceNumber attribute value.
     * @param otherCostShareNextSequenceNumber The otherCostShareNextSequenceNumber to set.
     */
    public void setOtherCostShareNextSequenceNumber(Integer otherCostShareNextSequenceNumber) {
        this.otherCostShareNextSequenceNumber = otherCostShareNextSequenceNumber;
    }

    /**
     * Gets the projectDirectorNextSequenceNumber attribute. 
     * @return Returns the projectDirectorNextSequenceNumber.
     */
    public Integer getProjectDirectorNextSequenceNumber() {
        return projectDirectorNextSequenceNumber;
    }

    /**
     * Sets the projectDirectorNextSequenceNumber attribute value.
     * @param projectDirectorNextSequenceNumber The projectDirectorNextSequenceNumber to set.
     */
    public void setProjectDirectorNextSequenceNumber(Integer projectDirectorNextSequenceNumber) {
        this.projectDirectorNextSequenceNumber = projectDirectorNextSequenceNumber;
    }

    /**
     * Gets the subcontractorNextSequenceNumber attribute. 
     * @return Returns the subcontractorNextSequenceNumber.
     */
    public Integer getSubcontractorNextSequenceNumber() {
        return subcontractorNextSequenceNumber;
    }

    /**
     * Sets the subcontractorNextSequenceNumber attribute value.
     * @param subcontractorNextSequenceNumber The subcontractorNextSequenceNumber to set.
     */
    public void setSubcontractorNextSequenceNumber(Integer subcontractorNextSequenceNumber) {
        this.subcontractorNextSequenceNumber = subcontractorNextSequenceNumber;
    }

    /**
     * Gets the federalIdentifier attribute.
     * 
     * @return Returns the federalIdentifier
     * 
     */
    public String getFederalIdentifier() { 
        return federalIdentifier;
    }

    /**
     * Sets the federalIdentifier attribute.
     * 
     * @param federalIdentifier The federalIdentifier to set.
     * 
     */
    public void setFederalIdentifier(String federalIdentifier) {
        this.federalIdentifier = federalIdentifier;
    }


    /**
     * Gets the grantsGovernmentConfirmationNumber attribute.
     * 
     * @return Returns the grantsGovernmentConfirmationNumber
     * 
     */
    public String getGrantsGovernmentConfirmationNumber() { 
        return grantsGovernmentConfirmationNumber;
    }

    /**
     * Sets the grantsGovernmentConfirmationNumber attribute.
     * 
     * @param grantsGovernmentConfirmationNumber The grantsGovernmentConfirmationNumber to set.
     * 
     */
    public void setGrantsGovernmentConfirmationNumber(String grantsGovernmentConfirmationNumber) {
        this.grantsGovernmentConfirmationNumber = grantsGovernmentConfirmationNumber;
    }


    /**
     * Gets the grantsGovernmentSubmissionIndicator attribute.
     * 
     * @return Returns the grantsGovernmentSubmissionIndicator
     * 
     */
    public boolean isGrantsGovernmentSubmissionIndicator() { 
        return grantsGovernmentSubmissionIndicator;
    }

    /**
     * Sets the grantsGovernmentSubmissionIndicator attribute.
     * 
     * @param grantsGovernmentSubmissionIndicator The grantsGovernmentSubmissionIndicator to set.
     * 
     */
    public void setGrantsGovernmentSubmissionIndicator(boolean grantsGovernmentSubmissionIndicator) {
        this.grantsGovernmentSubmissionIndicator = grantsGovernmentSubmissionIndicator;
    }


    /**
     * Gets the projectAbstract attribute.
     * 
     * @return Returns the projectAbstract
     * 
     */
    public String getProjectAbstract() { 
        return projectAbstract;
    }

    /**
     * Sets the projectAbstract attribute.
     * 
     * @param projectAbstract The projectAbstract to set.
     * 
     */
    public void setProjectAbstract(String projectAbstract) {
        this.projectAbstract = projectAbstract;
    }
    
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put(PropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }

    public RoutingFormAgency getRoutingFormAgency() {
        return routingFormAgency;
    }

    public void setRoutingFormAgency(RoutingFormAgency routingFormAgency) {
        this.routingFormAgency = routingFormAgency;
    }

    public CatalogOfFederalDomesticAssistanceReference getCatalogOfFederalDomesticAssistanceReference() {
        return catalogOfFederalDomesticAssistanceReference;
    }

    public void setCatalogOfFederalDomesticAssistanceReference(CatalogOfFederalDomesticAssistanceReference catalogOfFederalDomesticAssistanceReference) {
        this.catalogOfFederalDomesticAssistanceReference = catalogOfFederalDomesticAssistanceReference;
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
        
        for (RoutingFormResearchRisk researchRisk: this.routingFormResearchRisks) {
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

    public RoutingFormKeyword getRoutingFormKeyword(int index) {
        while (getRoutingFormKeywords().size() <= index) {
            getRoutingFormKeywords().add(new RoutingFormKeyword());
        }
        return (RoutingFormKeyword) getRoutingFormKeywords().get(index);
    }
    
    public void setRoutingFormKeywords(List<RoutingFormKeyword> routingFormKeywords) {
        this.routingFormKeywords = routingFormKeywords;
    }

    public void addRoutingFormKeyword(RoutingFormKeyword routingFormKeyword) {
        getRoutingFormKeywords().add(routingFormKeyword);
    }


    /**
     * Gets the researchType attribute. 
     * @return Returns the researchType.
     */
    public ResearchTypeCode getResearchType() {
        return researchType;
    }

    /**
     * Sets the researchType attribute value.
     * @param researchType The researchType to set.
     * @deprecated
     */
    public void setResearchType(ResearchTypeCode researchType) {
        this.researchType = researchType;
    }

    /**
     * Gets the submissionType attribute. 
     * @return Returns the submissionType.
     */
    public SubmissionType getSubmissionType() {
        return submissionType;
    }

    /**
     * Sets the submissionType attribute value.
     * @param submissionType The submissionType to set.
     * @deprecated
     */
    public void setSubmissionType(SubmissionType submissionType) {
        this.submissionType = submissionType;
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
     * 
     * This method...
     * @param routingFormInstitutionCostShare
     */
    public void addRoutingFormInstitutionCostShare(RoutingFormInstitutionCostShare routingFormInstitutionCostShare) {
        routingFormInstitutionCostShare.setDocumentNumber(this.getDocumentNumber());
        Integer nextSequenceNumber = this.getRoutingFormNextInstitutionCostShareSequenceNumber();
        routingFormInstitutionCostShare.setRoutingFormCostShareSequenceNumber(nextSequenceNumber);
        this.setRoutingFormNextInstitutionCostShareSequenceNumber(nextSequenceNumber++);
        getRoutingFormInstitutionCostShares().add(routingFormInstitutionCostShare);
    }

    private Integer getRoutingFormNextInstitutionCostShareSequenceNumber() {
        //TODO This should come from the database.
        if (routingFormInstitutionCostShares.size() > 0) {
            return routingFormInstitutionCostShares.get(routingFormInstitutionCostShares.size() - 1).getRoutingFormCostShareSequenceNumber() + 1;    
        } else {
            return 1;
        }
//        return routingFormNextInstitutionCostShareSequenceNumber;
    }

    private void setRoutingFormNextInstitutionCostShareSequenceNumber(Integer routingFormNextInstitutionCostShareSequenceNumber) {
        this.routingFormNextInstitutionCostShareSequenceNumber = routingFormNextInstitutionCostShareSequenceNumber;
    }

    
    /**
     * 
     * This method...
     * @return
     */
    public List<RoutingFormOtherCostShare> getRoutingFormOtherCostShares() {
        return routingFormOtherCostShares;
    }

    /**
     * 
     * This method...
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
     * 
     * This method...
     * @param routingFormOtherCostShares
     */
    public void setRoutingFormOtherCostShares(List<RoutingFormOtherCostShare> routingFormOtherCostShares) {
        this.routingFormOtherCostShares = routingFormOtherCostShares;
    }

    /**
     * 
     * This method...
     * @param routingFormOtherCostShare
     */
    public void addRoutingFormOtherCostShare(RoutingFormOtherCostShare routingFormOtherCostShare) {
        routingFormOtherCostShare.setDocumentNumber(this.getDocumentNumber());
        Integer nextSequenceNumber = this.getRoutingFormNextOtherCostShareSequenceNumber();
        routingFormOtherCostShare.setRoutingFormCostShareSequenceNumber(nextSequenceNumber);
        this.setRoutingFormNextOtherCostShareSequenceNumber(nextSequenceNumber++);
        getRoutingFormOtherCostShares().add(routingFormOtherCostShare);
    }

    private Integer getRoutingFormNextOtherCostShareSequenceNumber() {
        //TODO This should come from the database.
        if (routingFormOtherCostShares.size() > 0) {
            return routingFormOtherCostShares.get(routingFormOtherCostShares.size() - 1).getRoutingFormCostShareSequenceNumber() + 1;    
        } else {
            return 1;
        }
//        return routingFormNextInstitutionCostShareSequenceNumber;
    }

    private void setRoutingFormNextOtherCostShareSequenceNumber(Integer routingFormNextOtherCostShareSequenceNumber) {
        this.routingFormNextOtherCostShareSequenceNumber = routingFormNextOtherCostShareSequenceNumber;
    }

    
    /**
     * 
     * This method...
     * @return
     */
    public List<RoutingFormSubcontractor> getRoutingFormSubcontractors() {
        return routingFormSubcontractors;
    }

    /**
     * 
     * This method...
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
     * 
     * This method...
     * @param routingFormSubcontractors
     */
    public void setRoutingFormSubcontractors(List<RoutingFormSubcontractor> routingFormSubcontractors) {
        this.routingFormSubcontractors = routingFormSubcontractors;
    }

    /**
     * 
     * This method...
     * @param routingFormSubcontractor
     */
    public void addRoutingFormSubcontractor(RoutingFormSubcontractor routingFormSubcontractor) {
        routingFormSubcontractor.setDocumentNumber(this.getDocumentNumber());
        Integer nextSequenceNumber = this.getRoutingFormNextOtherCostShareSequenceNumber();
        routingFormSubcontractor.setRoutingFormSubcontractorSequenceNumber(nextSequenceNumber);
        this.setRoutingFormNextOtherCostShareSequenceNumber(nextSequenceNumber++);
        getRoutingFormSubcontractors().add(routingFormSubcontractor);
        
    }
    
    /**
     * Adds a RoutingFormPersonnel item to the routingFormPersonnel list.
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
     * @param routingFormOrganizationCreditPercent
     */
    public void addOrganizationCreditPercent(RoutingFormOrganizationCreditPercent routingFormOrganizationCreditPercent) {
        routingFormOrganizationCreditPercent.setDocumentNumber(this.getDocumentNumber());

        getRoutingFormOrganizationCreditPercents().add(routingFormOrganizationCreditPercent);
    }
    
    private Integer getRoutingFormNextSubcontractorSequenceNumber() {
        //TODO This should come from the database.
        if (routingFormSubcontractors.size() > 0) {
            return routingFormSubcontractors.get(routingFormSubcontractors.size() - 1).getRoutingFormSubcontractorSequenceNumber() + 1;    
        } else {
            return 1;
        }
//        return routingFormNextSubcontractorSequenceNumber;
    }

    private void setRoutingFormNextSubcontractorSequenceNumber(Integer routingFormNextSubcontractorSequenceNumber) {
        this.routingFormNextSubcontractorSequenceNumber = routingFormNextSubcontractorSequenceNumber;
    }
    
    /**
     * 
     * This method...
     * @return
     */
    public KualiDecimal getTotalInstitutionCostShareAmount() {
        KualiDecimal total = KualiDecimal.ZERO;
        for (RoutingFormInstitutionCostShare institutionCostShare : this.getRoutingFormInstitutionCostShares()) {
            if (institutionCostShare.getRoutingFormCostShareAmount() != null)
                total = total.add(institutionCostShare.getRoutingFormCostShareAmount());
        }
        return total;
    }

    /**
     * 
     * This method...
     * @return
     */
    public KualiDecimal getTotalOtherCostShareAmount() {
        KualiDecimal total = KualiDecimal.ZERO;
        for (RoutingFormOtherCostShare otherCostShare : getRoutingFormOtherCostShares()) {
            if (otherCostShare.getRoutingFormCostShareAmount() != null)
                total = total.add(otherCostShare.getRoutingFormCostShareAmount());
        }
        return total;
    }

    /**
     * 
     * This method...
     * @return
     */
    public KualiInteger getTotalSubcontractorAmount() {
        KualiInteger total = KualiInteger.ZERO;
        for (RoutingFormSubcontractor subcontractor: getRoutingFormSubcontractors()) {
            if (subcontractor.getRoutingFormSubcontractorAmount() != null)
                total = total.add(subcontractor.getRoutingFormSubcontractorAmount());
        }
        return total;
    }

    /**
     * Gets the federalPassThroughAgency attribute. 
     * @return Returns the federalPassThroughAgency.
     */
    public Agency getFederalPassThroughAgency() {
        return federalPassThroughAgency;
    }

    /**
     * Sets the federalPassThroughAgency attribute value.
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
        
        return list;
    }

    /**
     * Gets the routingFormAgencyToBeNamedIndicator attribute. 
     * @return Returns the routingFormAgencyToBeNamedIndicator.
     */
    public boolean isRoutingFormAgencyToBeNamedIndicator() {
        return routingFormAgencyToBeNamedIndicator;
    }

    /**
     * Sets the routingFormAgencyToBeNamedIndicator attribute value.
     * @param routingFormAgencyToBeNamedIndicator The routingFormAgencyToBeNamedIndicator to set.
     */
    public void setRoutingFormAgencyToBeNamedIndicator(boolean routingFormAgencyToBeNamedIndicator) {
        this.routingFormAgencyToBeNamedIndicator = routingFormAgencyToBeNamedIndicator;
    }

    /**
     * Gets the routingFormPersonnel attribute. 
     * @return Returns the routingFormPersonnel.
     */
    public List<RoutingFormPersonnel> getRoutingFormPersonnel() {
        return routingFormPersonnel;
    }

    /**
     * Sets the routingFormPersonnel attribute value.
     * @param routingFormPersonnel The routingFormPersonnel to set.
     */
    public void setRoutingFormPersonnel(List<RoutingFormPersonnel> routingFormPersonnel) {
        this.routingFormPersonnel = routingFormPersonnel;
    }

    /**
     * Gets index i from the routingFormPersonnel list. 
     * @param index
     * @return Person at index i
     */
    public RoutingFormPersonnel getRoutingFormPerson(int index) {
        while (getRoutingFormPersonnel().size() <= index) {
            getRoutingFormPersonnel().add(new RoutingFormPersonnel());
        }
        return (RoutingFormPersonnel) getRoutingFormPersonnel().get(index);
    }
    
    /**
     * Gets the routingFormOrganizationCreditPercents attribute. 
     * @return Returns the routingFormOrganizationCreditPercents.
     */
    public List<RoutingFormOrganizationCreditPercent> getRoutingFormOrganizationCreditPercents() {
        return routingFormOrganizationCreditPercents;
    }

    /**
     * Sets the routingFormOrganizationCreditPercents attribute value.
     * @param routingFormOrganizationCreditPercents The routingFormOrganizationCreditPercents to set.
     */
    public void setRoutingFormOrganizationCreditPercents(List<RoutingFormOrganizationCreditPercent> routingFormOrganizationCreditPercents) {
        this.routingFormOrganizationCreditPercents = routingFormOrganizationCreditPercents;
    }
    
    /**
     * Gets index i from the routingFormOrganizationCreditPercents list. 
     * @param index
     * @return Person at index i
     */
    public RoutingFormOrganizationCreditPercent getRoutingFormOrganizationCreditPercent(int index) {
        while (getRoutingFormOrganizationCreditPercents().size() <= index) {
            getRoutingFormOrganizationCreditPercents().add(new RoutingFormOrganizationCreditPercent());
        }
        return (RoutingFormOrganizationCreditPercent) getRoutingFormOrganizationCreditPercents().get(index);
    }
    
    /**
     * Gets the routingFormQuestions attribute. 
     * @return Returns the routingFormQuestions.
     */
    public List<RoutingFormQuestion> getRoutingFormQuestions() {
        return routingFormQuestions;
    }

    /**
     * Sets the routingFormQuestions attribute value.
     * @param routingFormQuestions The routingFormQuestions to set.
     */
    public void setRoutingFormQuestions(List<RoutingFormQuestion> routingFormQuestions) {
        this.routingFormQuestions = routingFormQuestions;
    }
    
    /**
     * Gets index i from the routingFormQuestions list. 
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
     * @return Returns the routingFormCatalogOfFederalDomesticAssistanceNumber.
     */
    public String getRoutingFormCatalogOfFederalDomesticAssistanceNumber() {
        return routingFormCatalogOfFederalDomesticAssistanceNumber;
    }

    /**
     * Sets the routingFormCatalogOfFederalDomesticAssistanceNumber attribute value.
     * @param routingFormCatalogOfFederalDomesticAssistanceNumber The routingFormCatalogOfFederalDomesticAssistanceNumber to set.
     */
    public void setRoutingFormCatalogOfFederalDomesticAssistanceNumber(String routingFormCatalogOfFederalDomesticAssistanceNumber) {
        this.routingFormCatalogOfFederalDomesticAssistanceNumber = routingFormCatalogOfFederalDomesticAssistanceNumber;
    }

    /**
     * Gets the projectTypeOtherDescription attribute.
     * 
     * @return Returns the projectTypeOtherDescription
     * 
     */
    public String getProjectTypeOtherDescription() { 
        return projectTypeOtherDescription;
    }

    /**
     * Sets the projectTypeOtherDescription attribute.
     * 
     * @param projectTypeOtherDescription The projectTypeOtherDescription to set.
     * 
     */
    public void setProjectTypeOtherDescription(String projectTypeOtherDescription) {
        this.projectTypeOtherDescription = projectTypeOtherDescription;
    }
    
    /**
     * Gets the personnelNextSequenceNumber attribute. 
     * @return Returns the personnelNextSequenceNumber.
     */
    public Integer getPersonnelNextSequenceNumber() {
        return personnelNextSequenceNumber;
    }

    /**
     * Sets the personnelNextSequenceNumber attribute value.
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
    
    public List<RoutingFormProjectType> getRoutingFormProjectTypes() {
        return routingFormProjectTypes;
    }

    public void setRoutingFormProjectTypes(List<RoutingFormProjectType> routingFormProjectTypes) {
        this.routingFormProjectTypes = routingFormProjectTypes;
    }
    
    public RoutingFormProjectType getRoutingFormProjectType(int index) {
        while (getRoutingFormProjectTypes().size() <= index) {
            getRoutingFormProjectTypes().add(new RoutingFormProjectType());
        }
        return (RoutingFormProjectType) getRoutingFormProjectTypes().get(index);
    }
    
    /**
     * Gets the routingFormAdHocOrgs attribute. 
     * @return Returns the routingFormAdHocOrgs.
     */
    public List<RoutingFormAdHocOrg> getRoutingFormAdHocOrgs() {
        return routingFormAdHocOrgs;
    }

    /**
     * Sets the routingFormAdHocOrgs attribute value.
     * @param routingFormAdHocOrgs The routingFormAdHocOrgs to set.
     */
    public void setRoutingFormAdHocOrgs(List<RoutingFormAdHocOrg> routingFormAdHocOrgs) {
        this.routingFormAdHocOrgs = routingFormAdHocOrgs;
    }
    
    /**
     * Gets the RoutingFormAdHocOrg item at given index.
     * 
     * @param index
     * @return RoutingFormAdHocOrg
     */
    public RoutingFormAdHocOrg getRoutingFormAdHocOrg(int index) {
        while (this.getRoutingFormAdHocOrgs().size() <= index) {
            this.getRoutingFormAdHocOrgs().add(new RoutingFormAdHocOrg());
        }
        return this.getRoutingFormAdHocOrgs().get(index);
    }
    
    /**
     * Gets the routingFormAdHocPeople attribute. 
     * @return Returns the routingFormAdHocPeople.
     */
    public List<RoutingFormAdHocPerson> getRoutingFormAdHocPeople() {
        return routingFormAdHocPeople;
    }

    /**
     * Sets the routingFormAdHocPeople attribute value.
     * @param routingFormAdHocPeople The routingFormAdHocPeople to set.
     */
    public void setRoutingFormAdHocPeople(List<RoutingFormAdHocPerson> routingFormAdHocPeople) {
        this.routingFormAdHocPeople = routingFormAdHocPeople;
    }
    
    /**
     * Gets the RoutingFormAdHocPerson item at given index.
     * 
     * @param index
     * @return RoutingFormAdHocPerson
     */
    public RoutingFormAdHocPerson getRoutingFormAdHocPerson(int index) {
        while (this.getRoutingFormAdHocPeople().size() <= index) {
            this.getRoutingFormAdHocPeople().add(new RoutingFormAdHocPerson());
        }
        return this.getRoutingFormAdHocPeople().get(index);
    }

    /**
     * Gets the routingFormAdHocWorkgroups attribute. 
     * @return Returns the routingFormAdHocWorkgroups.
     */
    public List<RoutingFormAdHocWorkgroup> getRoutingFormAdHocWorkgroups() {
        return routingFormAdHocWorkgroups;
    }

    /**
     * Sets the routingFormAdHocWorkgroups attribute value.
     * @param routingFormAdHocWorkgroups The routingFormAdHocWorkgroups to set.
     */
    public void setRoutingFormAdHocWorkgroups(List<RoutingFormAdHocWorkgroup> routingFormAdHocWorkgroups) {
        this.routingFormAdHocWorkgroups = routingFormAdHocWorkgroups;
    }
    
    /**
     * Gets the RoutingFormAdHocWorkgroup item at given index.
     * 
     * @param index
     * @return RoutingFormAdHocWorkgroup
     */
    public RoutingFormAdHocWorkgroup getRoutingFormAdHocWorkgroup(int index) {
        while (this.getRoutingFormAdHocWorkgroups().size() <= index) {
            this.getRoutingFormAdHocWorkgroups().add(new RoutingFormAdHocWorkgroup());
        }
        return this.getRoutingFormAdHocWorkgroups().get(index);
    }

    /**
     * Sums percent financial aid from all personnel and organizations.
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
}
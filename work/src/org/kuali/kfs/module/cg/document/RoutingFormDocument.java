/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/document/RoutingFormDocument.java,v $
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

import org.kuali.core.exceptions.IllegalObjectStateException;
import org.kuali.module.cg.bo.CatalogOfFederalDomesticAssistanceReference;
import org.kuali.module.chart.bo.Campus;
import org.kuali.module.kra.budget.document.ResearchDocumentBase;
import org.kuali.module.kra.routingform.bo.ContractGrantProposal;
import org.kuali.module.kra.routingform.bo.RoutingFormAgency;
import org.kuali.module.kra.routingform.bo.RoutingFormBudget;
import org.kuali.module.kra.routingform.bo.RoutingFormInstitutionCostShare;
import org.kuali.module.kra.routingform.bo.RoutingFormKeyword;
import org.kuali.module.kra.routingform.bo.RoutingFormResearchRisk;
import org.kuali.module.kra.routingform.bo.RoutingFormPurpose;
import org.kuali.module.kra.routingform.bo.RoutingFormResearchType;
import org.kuali.module.kra.routingform.bo.RoutingFormStatus;
import org.kuali.module.kra.routingform.bo.SubmissionType;

/**
 * 
 */
public class RoutingFormDocument extends ResearchDocumentBase {

	private String researchDocumentNumber;
	private boolean agencyAdditionalShippingInstructionsIndicator;
	private boolean agencyFederalPassThroughNotAvailableIndicator;
	private String agencyFederalPassThroughNumber;
	private String grantNumber;
	private String proposalAnnouncementNumber;
    private String proposalBudgetNumber;
	private boolean proposalConflictOfInterestCurrentIndicator;
	private boolean proposalConflictOfInterestExistsIndicator;
	private String proposalContactFaxNumber;
	private String proposalContactPhoneNumber;
	private Long proposalContactSystemsIdentifier;
	private boolean proposalCoProjectDirectorIndicator;
	private boolean proposalCreditPercentIndicator;
	private Date proposalCreateDate;
	private boolean proposalCostShareIndicator;
	private boolean proposalFederalPassThroughIndicator;
	private String proposalFellowFirstName;
	private String proposalFellowEmailAddress;
	private String proposalFellowFullName;
	private String proposalFellowLastName;
	private boolean proposalForeignPartnerIndicator;
	private boolean proposalForeignTravelIndicator;
	private boolean proposalIncomeIndicator;
	private boolean proposalInventionIndicator;
	private String proposalLayDescription;
	private Date proposalLastUpdateDate;
	private Long proposalLastUpdateSystemIdentifier;
	private boolean proposalNewSpaceIndicator;
	private String proposalOffCampusDescription;
	private boolean proposalOffCampusIndicator;
	private boolean proposalOtherOrganizationIndicator;
	private String proposalOtherPurposeDescription;
	private String proposalOtherTypeDescription;
    private String proposalParentNumber;
	private String proposalPhysicalCampusCode;
	private String proposalPriorGrantNumber;
	private String proposalProjectTitle;
	private String proposalPurposeCode;
	private String proposalSpaceRequiredDescription;
	private boolean proposalSpaceRequiredIndicator;
	private String proposalStatusCode;
	private boolean proposalSubcontractorIndicator;
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
    
    private Campus proposalPhysicalCampus;
    private RoutingFormStatus proposalStatus;
    private RoutingFormPurpose proposalPurpose;
    private RoutingFormAgency routingFormAgency;
    private CatalogOfFederalDomesticAssistanceReference catalogOfFederalDomesticAssistanceReference;
    private List<RoutingFormResearchRisk> routingFormResearchRisks;
    private List<RoutingFormKeyword> routingFormKeywords;
    private RoutingFormBudget routingFormBudget;
    private RoutingFormResearchType researchType;
    private SubmissionType submissionType;
    private ContractGrantProposal contractGrantProposal;
    private List<RoutingFormInstitutionCostShare> routingFormInstitutionCostShares;
   
	/**
	 * Default constructor.
	 */
	public RoutingFormDocument() {
        routingFormResearchRisks = new ArrayList<RoutingFormResearchRisk>();
	}

	/**
	 * Gets the researchDocumentNumber attribute.
	 * 
	 * @return - Returns the researchDocumentNumber
	 * 
	 */
	public String getResearchDocumentNumber() { 
		return researchDocumentNumber;
	}

	/**
	 * Sets the researchDocumentNumber attribute.
	 * 
	 * @param - researchDocumentNumber The researchDocumentNumber to set.
	 * 
	 */
	public void setResearchDocumentNumber(String researchDocumentNumber) {
		this.researchDocumentNumber = researchDocumentNumber;
	}


	/**
	 * Gets the agencyAdditionalShippingInstructionsIndicator attribute.
	 * 
	 * @return - Returns the agencyAdditionalShippingInstructionsIndicator
	 * 
	 */
	public boolean getAgencyAdditionalShippingInstructionsIndicator() { 
		return agencyAdditionalShippingInstructionsIndicator;
	}

	/**
	 * Sets the agencyAdditionalShippingInstructionsIndicator attribute.
	 * 
	 * @param - agencyAdditionalShippingInstructionsIndicator The agencyAdditionalShippingInstructionsIndicator to set.
	 * 
	 */
	public void setAgencyAdditionalShippingInstructionsIndicator(boolean agencyAdditionalShippingInstructionsIndicator) {
		this.agencyAdditionalShippingInstructionsIndicator = agencyAdditionalShippingInstructionsIndicator;
	}


	/**
	 * Gets the agencyFederalPassThroughNotAvailableIndicator attribute.
	 * 
	 * @return - Returns the agencyFederalPassThroughNotAvailableIndicator
	 * 
	 */
	public boolean getAgencyFederalPassThroughNotAvailableIndicator() { 
		return agencyFederalPassThroughNotAvailableIndicator;
	}

	/**
	 * Sets the agencyFederalPassThroughNotAvailableIndicator attribute.
	 * 
	 * @param - agencyFederalPassThroughNotAvailableIndicator The agencyFederalPassThroughNotAvailableIndicator to set.
	 * 
	 */
	public void setAgencyFederalPassThroughNotAvailableIndicator(boolean agencyFederalPassThroughNotAvailableIndicator) {
		this.agencyFederalPassThroughNotAvailableIndicator = agencyFederalPassThroughNotAvailableIndicator;
	}


	/**
	 * Gets the agencyFederalPassThroughNumber attribute.
	 * 
	 * @return - Returns the agencyFederalPassThroughNumber
	 * 
	 */
	public String getAgencyFederalPassThroughNumber() { 
		return agencyFederalPassThroughNumber;
	}

	/**
	 * Sets the agencyFederalPassThroughNumber attribute.
	 * 
	 * @param - agencyFederalPassThroughNumber The agencyFederalPassThroughNumber to set.
	 * 
	 */
	public void setAgencyFederalPassThroughNumber(String agencyFederalPassThroughNumber) {
		this.agencyFederalPassThroughNumber = agencyFederalPassThroughNumber;
	}


	/**
	 * Gets the grantNumber attribute.
	 * 
	 * @return - Returns the grantNumber
	 * 
	 */
	public String getGrantNumber() { 
		return grantNumber;
	}

	/**
	 * Sets the grantNumber attribute.
	 * 
	 * @param - grantNumber The grantNumber to set.
	 * 
	 */
	public void setGrantNumber(String grantNumber) {
		this.grantNumber = grantNumber;
	}


	/**
	 * Gets the proposalAnnouncementNumber attribute.
	 * 
	 * @return - Returns the proposalAnnouncementNumber
	 * 
	 */
	public String getProposalAnnouncementNumber() { 
		return proposalAnnouncementNumber;
	}

	/**
	 * Sets the proposalAnnouncementNumber attribute.
	 * 
	 * @param - proposalAnnouncementNumber The proposalAnnouncementNumber to set.
	 * 
	 */
	public void setProposalAnnouncementNumber(String proposalAnnouncementNumber) {
		this.proposalAnnouncementNumber = proposalAnnouncementNumber;
	}


	/**
	 * Gets the proposalBudgetNumber attribute.
	 * 
	 * @return - Returns the proposalBudgetNumber
	 * 
	 */
	public String getProposalBudgetNumber() { 
		return proposalBudgetNumber;
	}

	/**
	 * Sets the proposalBudgetNumber attribute.
	 * 
	 * @param - proposalBudgetNumber The proposalBudgetNumber to set.
	 * 
	 */
	public void setProposalBudgetNumber(String proposalBudgetNumber) {
		this.proposalBudgetNumber = proposalBudgetNumber;
	}


	/**
	 * Gets the proposalConflictOfInterestCurrentIndicator attribute.
	 * 
	 * @return - Returns the proposalConflictOfInterestCurrentIndicator
	 * 
	 */
	public boolean getProposalConflictOfInterestCurrentIndicator() { 
		return proposalConflictOfInterestCurrentIndicator;
	}

	/**
	 * Sets the proposalConflictOfInterestCurrentIndicator attribute.
	 * 
	 * @param - proposalConflictOfInterestCurrentIndicator The proposalConflictOfInterestCurrentIndicator to set.
	 * 
	 */
	public void setProposalConflictOfInterestCurrentIndicator(boolean proposalConflictOfInterestCurrentIndicator) {
		this.proposalConflictOfInterestCurrentIndicator = proposalConflictOfInterestCurrentIndicator;
	}


	/**
	 * Gets the proposalConflictOfInterestExistsIndicator attribute.
	 * 
	 * @return - Returns the proposalConflictOfInterestExistsIndicator
	 * 
	 */
	public boolean getProposalConflictOfInterestExistsIndicator() { 
		return proposalConflictOfInterestExistsIndicator;
	}

	/**
	 * Sets the proposalConflictOfInterestExistsIndicator attribute.
	 * 
	 * @param - proposalConflictOfInterestExistsIndicator The proposalConflictOfInterestExistsIndicator to set.
	 * 
	 */
	public void setProposalConflictOfInterestExistsIndicator(boolean proposalConflictOfInterestExistsIndicator) {
		this.proposalConflictOfInterestExistsIndicator = proposalConflictOfInterestExistsIndicator;
	}


	/**
	 * Gets the proposalContactFaxNumber attribute.
	 * 
	 * @return - Returns the proposalContactFaxNumber
	 * 
	 */
	public String getProposalContactFaxNumber() { 
		return proposalContactFaxNumber;
	}

	/**
	 * Sets the proposalContactFaxNumber attribute.
	 * 
	 * @param - proposalContactFaxNumber The proposalContactFaxNumber to set.
	 * 
	 */
	public void setProposalContactFaxNumber(String proposalContactFaxNumber) {
		this.proposalContactFaxNumber = proposalContactFaxNumber;
	}


	/**
	 * Gets the proposalContactPhoneNumber attribute.
	 * 
	 * @return - Returns the proposalContactPhoneNumber
	 * 
	 */
	public String getProposalContactPhoneNumber() { 
		return proposalContactPhoneNumber;
	}

	/**
	 * Sets the proposalContactPhoneNumber attribute.
	 * 
	 * @param - proposalContactPhoneNumber The proposalContactPhoneNumber to set.
	 * 
	 */
	public void setProposalContactPhoneNumber(String proposalContactPhoneNumber) {
		this.proposalContactPhoneNumber = proposalContactPhoneNumber;
	}


	/**
	 * Gets the proposalContactSystemsIdentifier attribute.
	 * 
	 * @return - Returns the proposalContactSystemsIdentifier
	 * 
	 */
	public Long getProposalContactSystemsIdentifier() { 
		return proposalContactSystemsIdentifier;
	}

	/**
	 * Sets the proposalContactSystemsIdentifier attribute.
	 * 
	 * @param - proposalContactSystemsIdentifier The proposalContactSystemsIdentifier to set.
	 * 
	 */
	public void setProposalContactSystemsIdentifier(Long proposalContactSystemsIdentifier) {
		this.proposalContactSystemsIdentifier = proposalContactSystemsIdentifier;
	}


	/**
	 * Gets the proposalCoProjectDirectorIndicator attribute.
	 * 
	 * @return - Returns the proposalCoProjectDirectorIndicator
	 * 
	 */
	public boolean getProposalCoProjectDirectorIndicator() { 
		return proposalCoProjectDirectorIndicator;
	}

	/**
	 * Sets the proposalCoProjectDirectorIndicator attribute.
	 * 
	 * @param - proposalCoProjectDirectorIndicator The proposalCoProjectDirectorIndicator to set.
	 * 
	 */
	public void setProposalCoProjectDirectorIndicator(boolean proposalCoProjectDirectorIndicator) {
		this.proposalCoProjectDirectorIndicator = proposalCoProjectDirectorIndicator;
	}


	/**
	 * Gets the proposalCreditPercentIndicator attribute.
	 * 
	 * @return - Returns the proposalCreditPercentIndicator
	 * 
	 */
	public boolean getProposalCreditPercentIndicator() { 
		return proposalCreditPercentIndicator;
	}

	/**
	 * Sets the proposalCreditPercentIndicator attribute.
	 * 
	 * @param - proposalCreditPercentIndicator The proposalCreditPercentIndicator to set.
	 * 
	 */
	public void setProposalCreditPercentIndicator(boolean proposalCreditPercentIndicator) {
		this.proposalCreditPercentIndicator = proposalCreditPercentIndicator;
	}


	/**
	 * Gets the proposalCreateDate attribute.
	 * 
	 * @return - Returns the proposalCreateDate
	 * 
	 */
	public Date getProposalCreateDate() { 
		return proposalCreateDate;
	}

	/**
	 * Sets the proposalCreateDate attribute.
	 * 
	 * @param - proposalCreateDate The proposalCreateDate to set.
	 * 
	 */
	public void setProposalCreateDate(Date proposalCreateDate) {
		this.proposalCreateDate = proposalCreateDate;
	}


	/**
	 * Gets the proposalCostShareIndicator attribute.
	 * 
	 * @return - Returns the proposalCostShareIndicator
	 * 
	 */
	public boolean getProposalCostShareIndicator() { 
		return proposalCostShareIndicator;
	}

	/**
	 * Sets the proposalCostShareIndicator attribute.
	 * 
	 * @param - proposalCostShareIndicator The proposalCostShareIndicator to set.
	 * 
	 */
	public void setProposalCostShareIndicator(boolean proposalCostShareIndicator) {
		this.proposalCostShareIndicator = proposalCostShareIndicator;
	}


	/**
	 * Gets the proposalFederalPassThroughIndicator attribute.
	 * 
	 * @return - Returns the proposalFederalPassThroughIndicator
	 * 
	 */
	public boolean getProposalFederalPassThroughIndicator() { 
		return proposalFederalPassThroughIndicator;
	}

	/**
	 * Sets the proposalFederalPassThroughIndicator attribute.
	 * 
	 * @param - proposalFederalPassThroughIndicator The proposalFederalPassThroughIndicator to set.
	 * 
	 */
	public void setProposalFederalPassThroughIndicator(boolean proposalFederalPassThroughIndicator) {
		this.proposalFederalPassThroughIndicator = proposalFederalPassThroughIndicator;
	}


	/**
	 * Gets the proposalFellowFirstName attribute.
	 * 
	 * @return - Returns the proposalFellowFirstName
	 * 
	 */
	public String getProposalFellowFirstName() { 
		return proposalFellowFirstName;
	}

	/**
	 * Sets the proposalFellowFirstName attribute.
	 * 
	 * @param - proposalFellowFirstName The proposalFellowFirstName to set.
	 * 
	 */
	public void setProposalFellowFirstName(String proposalFellowFirstName) {
		this.proposalFellowFirstName = proposalFellowFirstName;
	}


	/**
	 * Gets the proposalFellowEmailAddress attribute.
	 * 
	 * @return - Returns the proposalFellowEmailAddress
	 * 
	 */
	public String getProposalFellowEmailAddress() { 
		return proposalFellowEmailAddress;
	}

	/**
	 * Sets the proposalFellowEmailAddress attribute.
	 * 
	 * @param - proposalFellowEmailAddress The proposalFellowEmailAddress to set.
	 * 
	 */
	public void setProposalFellowEmailAddress(String proposalFellowEmailAddress) {
		this.proposalFellowEmailAddress = proposalFellowEmailAddress;
	}


	/**
	 * Gets the proposalFellowFullName attribute.
	 * 
	 * @return - Returns the proposalFellowFullName
	 * 
	 */
	public String getProposalFellowFullName() { 
		return proposalFellowFullName;
	}

	/**
	 * Sets the proposalFellowFullName attribute.
	 * 
	 * @param - proposalFellowFullName The proposalFellowFullName to set.
	 * 
	 */
	public void setProposalFellowFullName(String proposalFellowFullName) {
		this.proposalFellowFullName = proposalFellowFullName;
	}


	/**
	 * Gets the proposalFellowLastName attribute.
	 * 
	 * @return - Returns the proposalFellowLastName
	 * 
	 */
	public String getProposalFellowLastName() { 
		return proposalFellowLastName;
	}

	/**
	 * Sets the proposalFellowLastName attribute.
	 * 
	 * @param - proposalFellowLastName The proposalFellowLastName to set.
	 * 
	 */
	public void setProposalFellowLastName(String proposalFellowLastName) {
		this.proposalFellowLastName = proposalFellowLastName;
	}


	/**
	 * Gets the proposalForeignPartnerIndicator attribute.
	 * 
	 * @return - Returns the proposalForeignPartnerIndicator
	 * 
	 */
	public boolean getProposalForeignPartnerIndicator() { 
		return proposalForeignPartnerIndicator;
	}

	/**
	 * Sets the proposalForeignPartnerIndicator attribute.
	 * 
	 * @param - proposalForeignPartnerIndicator The proposalForeignPartnerIndicator to set.
	 * 
	 */
	public void setProposalForeignPartnerIndicator(boolean proposalForeignPartnerIndicator) {
		this.proposalForeignPartnerIndicator = proposalForeignPartnerIndicator;
	}


	/**
	 * Gets the proposalForeignTravelIndicator attribute.
	 * 
	 * @return - Returns the proposalForeignTravelIndicator
	 * 
	 */
	public boolean getProposalForeignTravelIndicator() { 
		return proposalForeignTravelIndicator;
	}

	/**
	 * Sets the proposalForeignTravelIndicator attribute.
	 * 
	 * @param - proposalForeignTravelIndicator The proposalForeignTravelIndicator to set.
	 * 
	 */
	public void setProposalForeignTravelIndicator(boolean proposalForeignTravelIndicator) {
		this.proposalForeignTravelIndicator = proposalForeignTravelIndicator;
	}


	/**
	 * Gets the proposalIncomeIndicator attribute.
	 * 
	 * @return - Returns the proposalIncomeIndicator
	 * 
	 */
	public boolean getProposalIncomeIndicator() { 
		return proposalIncomeIndicator;
	}

	/**
	 * Sets the proposalIncomeIndicator attribute.
	 * 
	 * @param - proposalIncomeIndicator The proposalIncomeIndicator to set.
	 * 
	 */
	public void setProposalIncomeIndicator(boolean proposalIncomeIndicator) {
		this.proposalIncomeIndicator = proposalIncomeIndicator;
	}


	/**
	 * Gets the proposalInventionIndicator attribute.
	 * 
	 * @return - Returns the proposalInventionIndicator
	 * 
	 */
	public boolean getProposalInventionIndicator() { 
		return proposalInventionIndicator;
	}

	/**
	 * Sets the proposalInventionIndicator attribute.
	 * 
	 * @param - proposalInventionIndicator The proposalInventionIndicator to set.
	 * 
	 */
	public void setProposalInventionIndicator(boolean proposalInventionIndicator) {
		this.proposalInventionIndicator = proposalInventionIndicator;
	}


	/**
	 * Gets the proposalLayDescription attribute.
	 * 
	 * @return - Returns the proposalLayDescription
	 * 
	 */
	public String getProposalLayDescription() { 
		return proposalLayDescription;
	}

	/**
	 * Sets the proposalLayDescription attribute.
	 * 
	 * @param - proposalLayDescription The proposalLayDescription to set.
	 * 
	 */
	public void setProposalLayDescription(String proposalLayDescription) {
		this.proposalLayDescription = proposalLayDescription;
	}


	/**
	 * Gets the proposalLastUpdateDate attribute.
	 * 
	 * @return - Returns the proposalLastUpdateDate
	 * 
	 */
	public Date getProposalLastUpdateDate() { 
		return proposalLastUpdateDate;
	}

	/**
	 * Sets the proposalLastUpdateDate attribute.
	 * 
	 * @param - proposalLastUpdateDate The proposalLastUpdateDate to set.
	 * 
	 */
	public void setProposalLastUpdateDate(Date proposalLastUpdateDate) {
		this.proposalLastUpdateDate = proposalLastUpdateDate;
	}


	/**
	 * Gets the proposalLastUpdateSystemIdentifier attribute.
	 * 
	 * @return - Returns the proposalLastUpdateSystemIdentifier
	 * 
	 */
	public Long getProposalLastUpdateSystemIdentifier() { 
		return proposalLastUpdateSystemIdentifier;
	}

	/**
	 * Sets the proposalLastUpdateSystemIdentifier attribute.
	 * 
	 * @param - proposalLastUpdateSystemIdentifier The proposalLastUpdateSystemIdentifier to set.
	 * 
	 */
	public void setProposalLastUpdateSystemIdentifier(Long proposalLastUpdateSystemIdentifier) {
		this.proposalLastUpdateSystemIdentifier = proposalLastUpdateSystemIdentifier;
	}


	/**
	 * Gets the proposalNewSpaceIndicator attribute.
	 * 
	 * @return - Returns the proposalNewSpaceIndicator
	 * 
	 */
	public boolean getProposalNewSpaceIndicator() { 
		return proposalNewSpaceIndicator;
	}

	/**
	 * Sets the proposalNewSpaceIndicator attribute.
	 * 
	 * @param - proposalNewSpaceIndicator The proposalNewSpaceIndicator to set.
	 * 
	 */
	public void setProposalNewSpaceIndicator(boolean proposalNewSpaceIndicator) {
		this.proposalNewSpaceIndicator = proposalNewSpaceIndicator;
	}


	/**
	 * Gets the proposalOffCampusDescription attribute.
	 * 
	 * @return - Returns the proposalOffCampusDescription
	 * 
	 */
	public String getProposalOffCampusDescription() { 
		return proposalOffCampusDescription;
	}

	/**
	 * Sets the proposalOffCampusDescription attribute.
	 * 
	 * @param - proposalOffCampusDescription The proposalOffCampusDescription to set.
	 * 
	 */
	public void setProposalOffCampusDescription(String proposalOffCampusDescription) {
		this.proposalOffCampusDescription = proposalOffCampusDescription;
	}


	/**
	 * Gets the proposalOffCampusIndicator attribute.
	 * 
	 * @return - Returns the proposalOffCampusIndicator
	 * 
	 */
	public boolean getProposalOffCampusIndicator() { 
		return proposalOffCampusIndicator;
	}

	/**
	 * Sets the proposalOffCampusIndicator attribute.
	 * 
	 * @param - proposalOffCampusIndicator The proposalOffCampusIndicator to set.
	 * 
	 */
	public void setProposalOffCampusIndicator(boolean proposalOffCampusIndicator) {
		this.proposalOffCampusIndicator = proposalOffCampusIndicator;
	}


	/**
	 * Gets the proposalOtherOrganizationIndicator attribute.
	 * 
	 * @return - Returns the proposalOtherOrganizationIndicator
	 * 
	 */
	public boolean getProposalOtherOrganizationIndicator() { 
		return proposalOtherOrganizationIndicator;
	}

	/**
	 * Sets the proposalOtherOrganizationIndicator attribute.
	 * 
	 * @param - proposalOtherOrganizationIndicator The proposalOtherOrganizationIndicator to set.
	 * 
	 */
	public void setProposalOtherOrganizationIndicator(boolean proposalOtherOrganizationIndicator) {
		this.proposalOtherOrganizationIndicator = proposalOtherOrganizationIndicator;
	}


	/**
	 * Gets the proposalOtherPurposeDescription attribute.
	 * 
	 * @return - Returns the proposalOtherPurposeDescription
	 * 
	 */
	public String getProposalOtherPurposeDescription() { 
		return proposalOtherPurposeDescription;
	}

	/**
	 * Sets the proposalOtherPurposeDescription attribute.
	 * 
	 * @param - proposalOtherPurposeDescription The proposalOtherPurposeDescription to set.
	 * 
	 */
	public void setProposalOtherPurposeDescription(String proposalOtherPurposeDescription) {
		this.proposalOtherPurposeDescription = proposalOtherPurposeDescription;
	}


	/**
	 * Gets the proposalOtherTypeDescription attribute.
	 * 
	 * @return - Returns the proposalOtherTypeDescription
	 * 
	 */
	public String getProposalOtherTypeDescription() { 
		return proposalOtherTypeDescription;
	}

	/**
	 * Sets the proposalOtherTypeDescription attribute.
	 * 
	 * @param - proposalOtherTypeDescription The proposalOtherTypeDescription to set.
	 * 
	 */
	public void setProposalOtherTypeDescription(String proposalOtherTypeDescription) {
		this.proposalOtherTypeDescription = proposalOtherTypeDescription;
	}


	/**
	 * Gets the proposalParentNumber attribute.
	 * 
	 * @return - Returns the proposalParentNumber
	 * 
	 */
	public String getProposalParentNumber() { 
		return proposalParentNumber;
	}

	/**
	 * Sets the proposalParentNumber attribute.
	 * 
	 * @param - proposalParentNumber The proposalParentNumber to set.
	 * 
	 */
	public void setProposalParentNumber(String proposalParentNumber) {
		this.proposalParentNumber = proposalParentNumber;
	}


	/**
	 * Gets the proposalPhysicalCampusCode attribute.
	 * 
	 * @return - Returns the proposalPhysicalCampusCode
	 * 
	 */
	public String getProposalPhysicalCampusCode() { 
		return proposalPhysicalCampusCode;
	}

	/**
	 * Sets the proposalPhysicalCampusCode attribute.
	 * 
	 * @param - proposalPhysicalCampusCode The proposalPhysicalCampusCode to set.
	 * 
	 */
	public void setProposalPhysicalCampusCode(String proposalPhysicalCampusCode) {
		this.proposalPhysicalCampusCode = proposalPhysicalCampusCode;
	}


	/**
	 * Gets the proposalPriorGrantNumber attribute.
	 * 
	 * @return - Returns the proposalPriorGrantNumber
	 * 
	 */
	public String getProposalPriorGrantNumber() { 
		return proposalPriorGrantNumber;
	}

	/**
	 * Sets the proposalPriorGrantNumber attribute.
	 * 
	 * @param - proposalPriorGrantNumber The proposalPriorGrantNumber to set.
	 * 
	 */
	public void setProposalPriorGrantNumber(String proposalPriorGrantNumber) {
		this.proposalPriorGrantNumber = proposalPriorGrantNumber;
	}


	/**
	 * Gets the proposalProjectTitle attribute.
	 * 
	 * @return - Returns the proposalProjectTitle
	 * 
	 */
	public String getProposalProjectTitle() { 
		return proposalProjectTitle;
	}

	/**
	 * Sets the proposalProjectTitle attribute.
	 * 
	 * @param - proposalProjectTitle The proposalProjectTitle to set.
	 * 
	 */
	public void setProposalProjectTitle(String proposalProjectTitle) {
		this.proposalProjectTitle = proposalProjectTitle;
	}


	/**
	 * Gets the proposalPurposeCode attribute.
	 * 
	 * @return - Returns the proposalPurposeCode
	 * 
	 */
	public String getProposalPurposeCode() { 
		return proposalPurposeCode;
	}

	/**
	 * Sets the proposalPurposeCode attribute.
	 * 
	 * @param - proposalPurposeCode The proposalPurposeCode to set.
	 * 
	 */
	public void setProposalPurposeCode(String proposalPurposeCode) {
		this.proposalPurposeCode = proposalPurposeCode;
	}

    /**
	 * Gets the proposalSpaceRequiredDescription attribute.
	 * 
	 * @return - Returns the proposalSpaceRequiredDescription
	 * 
	 */
	public String getProposalSpaceRequiredDescription() { 
		return proposalSpaceRequiredDescription;
	}

	/**
	 * Sets the proposalSpaceRequiredDescription attribute.
	 * 
	 * @param - proposalSpaceRequiredDescription The proposalSpaceRequiredDescription to set.
	 * 
	 */
	public void setProposalSpaceRequiredDescription(String proposalSpaceRequiredDescription) {
		this.proposalSpaceRequiredDescription = proposalSpaceRequiredDescription;
	}


	/**
	 * Gets the proposalSpaceRequiredIndicator attribute.
	 * 
	 * @return - Returns the proposalSpaceRequiredIndicator
	 * 
	 */
	public boolean getProposalSpaceRequiredIndicator() { 
		return proposalSpaceRequiredIndicator;
	}

	/**
	 * Sets the proposalSpaceRequiredIndicator attribute.
	 * 
	 * @param - proposalSpaceRequiredIndicator The proposalSpaceRequiredIndicator to set.
	 * 
	 */
	public void setProposalSpaceRequiredIndicator(boolean proposalSpaceRequiredIndicator) {
		this.proposalSpaceRequiredIndicator = proposalSpaceRequiredIndicator;
	}


	/**
	 * Gets the proposalStatusCode attribute.
	 * 
	 * @return - Returns the proposalStatusCode
	 * 
	 */
	public String getProposalStatusCode() { 
		return proposalStatusCode;
	}

	/**
	 * Sets the proposalStatusCode attribute.
	 * 
	 * @param - proposalStatusCode The proposalStatusCode to set.
	 * 
	 */
	public void setProposalStatusCode(String proposalStatusCode) {
		this.proposalStatusCode = proposalStatusCode;
	}


	/**
	 * Gets the proposalSubcontractorIndicator attribute.
	 * 
	 * @return - Returns the proposalSubcontractorIndicator
	 * 
	 */
	public boolean getProposalSubcontractorIndicator() { 
		return proposalSubcontractorIndicator;
	}

	/**
	 * Sets the proposalSubcontractorIndicator attribute.
	 * 
	 * @param - proposalSubcontractorIndicator The proposalSubcontractorIndicator to set.
	 * 
	 */
	public void setProposalSubcontractorIndicator(boolean proposalSubcontractorIndicator) {
		this.proposalSubcontractorIndicator = proposalSubcontractorIndicator;
	}

	/**
	 * Gets the institutionAccountNumber attribute.
	 * 
	 * @return - Returns the institutionAccountNumber
	 * 
	 */
	public String getInstitutionAccountNumber() { 
		return institutionAccountNumber;
	}

	/**
	 * Sets the institutionAccountNumber attribute.
	 * 
	 * @param - institutionAccountNumber The institutionAccountNumber to set.
	 * 
	 */
	public void setInstitutionAccountNumber(String institutionAccountNumber) {
		this.institutionAccountNumber = institutionAccountNumber;
	}

    
    /**
     * Gets the researchTypeCode attribute.
     * 
     * @return - Returns the researchTypeCode
     * 
     */
    public String getResearchTypeCode() { 
        return researchTypeCode;
    }

    /**
     * Sets the researchTypeCode attribute.
     * 
     * @param - researchTypeCode The researchTypeCode to set.
     * 
     */
    public void setResearchTypeCode(String researchTypeCode) {
        this.researchTypeCode = researchTypeCode;
    }


    /**
     * Gets the submissionTypeCode attribute.
     * 
     * @return - Returns the submissionTypeCode
     * 
     */
    public String getSubmissionTypeCode() { 
        return submissionTypeCode;
    }

    /**
     * Sets the submissionTypeCode attribute.
     * 
     * @param - submissionTypeCode The submissionTypeCode to set.
     * 
     */
    public void setSubmissionTypeCode(String submissionTypeCode) {
        this.submissionTypeCode = submissionTypeCode;
    }


    /**
     * Gets the previousFederalIdentifier attribute.
     * 
     * @return - Returns the previousFederalIdentifier
     * 
     */
    public String getPreviousFederalIdentifier() { 
        return previousFederalIdentifier;
    }

    /**
     * Sets the previousFederalIdentifier attribute.
     * 
     * @param - previousFederalIdentifier The previousFederalIdentifier to set.
     * 
     */
    public void setPreviousFederalIdentifier(String previousFederalIdentifier) {
        this.previousFederalIdentifier = previousFederalIdentifier;
    }    

    
	/**
	 * Gets the proposalPhysicalCampus attribute.
	 * 
	 * @return - Returns the proposalPhysicalCampus
	 * 
	 */
	public Campus getProposalPhysicalCampus() { 
		return proposalPhysicalCampus;
	}

	/**
	 * Sets the proposalPhysicalCampus attribute.
	 * 
	 * @param - proposalPhysicalCampus The proposalPhysicalCampus to set.
	 * @deprecated
	 */
	public void setProposalPhysicalCampus(Campus proposalPhysicalCampus) {
		this.proposalPhysicalCampus = proposalPhysicalCampus;
	}

    /**
     * Gets the proposalStatus attribute. 
     * @return Returns the proposalStatus.
     */
    public RoutingFormStatus getProposalStatus() {
        return proposalStatus;
    }

    /**
     * Sets the proposalStatus attribute value.
     * @param proposalStatus The proposalStatus to set.
     * @deprecated
     */
    public void setProposalStatus(RoutingFormStatus proposalStatus) {
        this.proposalStatus = proposalStatus;
    }

    /**
     * Gets the proposalPurpose attribute. 
     * @return Returns the proposalPurpose.
     */
    public RoutingFormPurpose getProposalPurpose() {
        return proposalPurpose;
    }

    /**
     * Sets the proposalPurpose attribute value.
     * @param proposalPurpose The proposalPurpose to set.
     * @deprecated
     */
    public void setProposalPurpose(RoutingFormPurpose proposalPurpose) {
        this.proposalPurpose = proposalPurpose;
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
     * @return - Returns the federalIdentifier
     * 
     */
    public String getFederalIdentifier() { 
        return federalIdentifier;
    }

    /**
     * Sets the federalIdentifier attribute.
     * 
     * @param - federalIdentifier The federalIdentifier to set.
     * 
     */
    public void setFederalIdentifier(String federalIdentifier) {
        this.federalIdentifier = federalIdentifier;
    }


    /**
     * Gets the grantsGovernmentConfirmationNumber attribute.
     * 
     * @return - Returns the grantsGovernmentConfirmationNumber
     * 
     */
    public String getGrantsGovernmentConfirmationNumber() { 
        return grantsGovernmentConfirmationNumber;
    }

    /**
     * Sets the grantsGovernmentConfirmationNumber attribute.
     * 
     * @param - grantsGovernmentConfirmationNumber The grantsGovernmentConfirmationNumber to set.
     * 
     */
    public void setGrantsGovernmentConfirmationNumber(String grantsGovernmentConfirmationNumber) {
        this.grantsGovernmentConfirmationNumber = grantsGovernmentConfirmationNumber;
    }


    /**
     * Gets the grantsGovernmentSubmissionIndicator attribute.
     * 
     * @return - Returns the grantsGovernmentSubmissionIndicator
     * 
     */
    public boolean isGrantsGovernmentSubmissionIndicator() { 
        return grantsGovernmentSubmissionIndicator;
    }

    /**
     * Sets the grantsGovernmentSubmissionIndicator attribute.
     * 
     * @param - grantsGovernmentSubmissionIndicator The grantsGovernmentSubmissionIndicator to set.
     * 
     */
    public void setGrantsGovernmentSubmissionIndicator(boolean grantsGovernmentSubmissionIndicator) {
        this.grantsGovernmentSubmissionIndicator = grantsGovernmentSubmissionIndicator;
    }


    /**
     * Gets the projectAbstract attribute.
     * 
     * @return - Returns the projectAbstract
     * 
     */
    public String getProjectAbstract() { 
        return projectAbstract;
    }

    /**
     * Sets the projectAbstract attribute.
     * 
     * @param - projectAbstract The projectAbstract to set.
     * 
     */
    public void setProjectAbstract(String projectAbstract) {
        this.projectAbstract = projectAbstract;
    }
    
    /**
     * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("researchDocumentNumber", this.researchDocumentNumber);
        return m;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.bo.Document#validate()
     */
    public void validate() throws IllegalObjectStateException {
        // TODO Auto-generated method stub
        super.validate();
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

    public RoutingFormResearchRisk getRoutingFormResearchRisk(int index) {
        while (getRoutingFormResearchRisks().size() <= index) {
            getRoutingFormResearchRisks().add(new RoutingFormResearchRisk());
        }
        return (RoutingFormResearchRisk) getRoutingFormResearchRisks().get(index);
    }
    
    public void setRoutingFormResearchRisks(List<RoutingFormResearchRisk> routingFormResearchRisks) {
        this.routingFormResearchRisks = routingFormResearchRisks;
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
    public RoutingFormResearchType getResearchType() {
        return researchType;
    }

    /**
     * Sets the researchType attribute value.
     * @param researchType The researchType to set.
     * @deprecated
     */
    public void setResearchType(RoutingFormResearchType researchType) {
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

    public void addRoutingFormInstitutionCostShare(RoutingFormInstitutionCostShare routingFormInstitutionCostShare) {
        getRoutingFormInstitutionCostShares().add(routingFormInstitutionCostShare);
    }

}

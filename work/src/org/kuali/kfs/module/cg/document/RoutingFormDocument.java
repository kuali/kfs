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
import org.kuali.module.kra.routingform.bo.RoutingFormAgency;
import org.kuali.module.kra.routingform.bo.RoutingFormBudget;
import org.kuali.module.kra.routingform.bo.RoutingFormKeyword;
import org.kuali.module.kra.routingform.bo.RoutingFormProtocol;
import org.kuali.module.kra.routingform.bo.RoutingFormPurpose;
import org.kuali.module.kra.routingform.bo.RoutingFormStatus;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class RoutingFormDocument extends ResearchDocumentBase {

	private String researchDocumentNumber;
	private boolean agencyAdditionalShippingInstructionsIndicator;
	private boolean agencyFederalPassThroughNotAvailableIndicator;
	private String agencyFederalPassThroughNumber;
	private String grantNumber;
	private String proposalAnnouncementNumber;
	private Long proposalBudgetNumber;
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
	private Long proposalParentNumber;
	private String proposalPhysicalCampusCode;
	private String proposalPriorGrantNumber;
	private String proposalProjectTitle;
	private String proposalPurposeCode;
	private String proposalSpaceRequiredDescription;
	private boolean proposalSpaceRequiredIndicator;
	private String proposalStatusCode;
	private boolean proposalSubcontractorIndicator;
	private boolean protocolAnimalIndicator;
	private boolean protocolHumanIndicator;
	private String protocolPathogenicAgentDescription;
	private boolean protocolPathogenicAgentIndicator;
	private boolean protocolRecombinantDnaIndicator;
	private String protocolHumanTissueFluidDescription;
	private boolean protocolHumanTissueFluidIndicator;
	private String institutionAccountNumber;
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
    private List<RoutingFormProtocol> routingFormProtocols;
    private List<RoutingFormKeyword> routingFormKeywords;
    private RoutingFormBudget routingFormBudget;
    
	/**
	 * Default constructor.
	 */
	public RoutingFormDocument() {
        routingFormProtocols = new ArrayList<RoutingFormProtocol>();
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
	public Long getProposalBudgetNumber() { 
		return proposalBudgetNumber;
	}

	/**
	 * Sets the proposalBudgetNumber attribute.
	 * 
	 * @param - proposalBudgetNumber The proposalBudgetNumber to set.
	 * 
	 */
	public void setProposalBudgetNumber(Long proposalBudgetNumber) {
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
	public Long getProposalParentNumber() { 
		return proposalParentNumber;
	}

	/**
	 * Sets the proposalParentNumber attribute.
	 * 
	 * @param - proposalParentNumber The proposalParentNumber to set.
	 * 
	 */
	public void setProposalParentNumber(Long proposalParentNumber) {
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
	 * Gets the protocolAnimalIndicator attribute.
	 * 
	 * @return - Returns the protocolAnimalIndicator
	 * 
	 */
	public boolean getProtocolAnimalIndicator() { 
		return protocolAnimalIndicator;
	}

	/**
	 * Sets the protocolAnimalIndicator attribute.
	 * 
	 * @param - protocolAnimalIndicator The protocolAnimalIndicator to set.
	 * 
	 */
	public void setProtocolAnimalIndicator(boolean protocolAnimalIndicator) {
		this.protocolAnimalIndicator = protocolAnimalIndicator;
	}


	/**
	 * Gets the protocolHumanIndicator attribute.
	 * 
	 * @return - Returns the protocolHumanIndicator
	 * 
	 */
	public boolean getProtocolHumanIndicator() { 
		return protocolHumanIndicator;
	}

	/**
	 * Sets the protocolHumanIndicator attribute.
	 * 
	 * @param - protocolHumanIndicator The protocolHumanIndicator to set.
	 * 
	 */
	public void setProtocolHumanIndicator(boolean protocolHumanIndicator) {
		this.protocolHumanIndicator = protocolHumanIndicator;
	}


	/**
	 * Gets the protocolPathogenicAgentDescription attribute.
	 * 
	 * @return - Returns the protocolPathogenicAgentDescription
	 * 
	 */
	public String getProtocolPathogenicAgentDescription() { 
		return protocolPathogenicAgentDescription;
	}

	/**
	 * Sets the protocolPathogenicAgentDescription attribute.
	 * 
	 * @param - protocolPathogenicAgentDescription The protocolPathogenicAgentDescription to set.
	 * 
	 */
	public void setProtocolPathogenicAgentDescription(String protocolPathogenicAgentDescription) {
		this.protocolPathogenicAgentDescription = protocolPathogenicAgentDescription;
	}


	/**
	 * Gets the protocolPathogenicAgentIndicator attribute.
	 * 
	 * @return - Returns the protocolPathogenicAgentIndicator
	 * 
	 */
	public boolean getProtocolPathogenicAgentIndicator() { 
		return protocolPathogenicAgentIndicator;
	}

	/**
	 * Sets the protocolPathogenicAgentIndicator attribute.
	 * 
	 * @param - protocolPathogenicAgentIndicator The protocolPathogenicAgentIndicator to set.
	 * 
	 */
	public void setProtocolPathogenicAgentIndicator(boolean protocolPathogenicAgentIndicator) {
		this.protocolPathogenicAgentIndicator = protocolPathogenicAgentIndicator;
	}


	/**
	 * Gets the protocolRecombinantDnaIndicator attribute.
	 * 
	 * @return - Returns the protocolRecombinantDnaIndicator
	 * 
	 */
	public boolean getProtocolRecombinantDnaIndicator() { 
		return protocolRecombinantDnaIndicator;
	}

	/**
	 * Sets the protocolRecombinantDnaIndicator attribute.
	 * 
	 * @param - protocolRecombinantDnaIndicator The protocolRecombinantDnaIndicator to set.
	 * 
	 */
	public void setProtocolRecombinantDnaIndicator(boolean protocolRecombinantDnaIndicator) {
		this.protocolRecombinantDnaIndicator = protocolRecombinantDnaIndicator;
	}


	/**
	 * Gets the protocolHumanTissueFluidDescription attribute.
	 * 
	 * @return - Returns the protocolHumanTissueFluidDescription
	 * 
	 */
	public String getProtocolHumanTissueFluidDescription() { 
		return protocolHumanTissueFluidDescription;
	}

	/**
	 * Sets the protocolHumanTissueFluidDescription attribute.
	 * 
	 * @param - protocolHumanTissueFluidDescription The protocolHumanTissueFluidDescription to set.
	 * 
	 */
	public void setProtocolHumanTissueFluidDescription(String protocolHumanTissueFluidDescription) {
		this.protocolHumanTissueFluidDescription = protocolHumanTissueFluidDescription;
	}


	/**
	 * Gets the protocolHumanTissueFluidIndicator attribute.
	 * 
	 * @return - Returns the protocolHumanTissueFluidIndicator
	 * 
	 */
	public boolean getProtocolHumanTissueFluidIndicator() { 
		return protocolHumanTissueFluidIndicator;
	}

	/**
	 * Sets the protocolHumanTissueFluidIndicator attribute.
	 * 
	 * @param - protocolHumanTissueFluidIndicator The protocolHumanTissueFluidIndicator to set.
	 * 
	 */
	public void setProtocolHumanTissueFluidIndicator(boolean protocolHumanTissueFluidIndicator) {
		this.protocolHumanTissueFluidIndicator = protocolHumanTissueFluidIndicator;
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
    
    public List<RoutingFormProtocol> getRoutingFormProtocols() {
        return routingFormProtocols;
    }

    public RoutingFormProtocol getRoutingFormProtocol(int index) {
        while (getRoutingFormProtocols().size() <= index) {
            getRoutingFormProtocols().add(new RoutingFormProtocol());
        }
        return (RoutingFormProtocol) getRoutingFormProtocols().get(index);
    }
    
    public void setRoutingFormProtocols(List<RoutingFormProtocol> routingFormProtocols) {
        this.routingFormProtocols = routingFormProtocols;
    }

    public void addRoutingFormProtocol(RoutingFormProtocol routingFormProtocol) {
        getRoutingFormProtocols().add(routingFormProtocol);
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


}

/*
 * Copyright 2006 The Kuali Foundation.
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

import org.kuali.PropertyConstants;
import org.kuali.core.exceptions.IllegalObjectStateException;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.module.cg.bo.Agency;
import org.kuali.module.cg.bo.CatalogOfFederalDomesticAssistanceReference;
import org.kuali.module.chart.bo.Campus;
import org.kuali.module.kra.budget.document.ResearchDocumentBase;
import org.kuali.module.kra.routingform.bo.ContractGrantProposal;
import org.kuali.module.kra.routingform.bo.RoutingFormAgency;
import org.kuali.module.kra.routingform.bo.RoutingFormBudget;
import org.kuali.module.kra.routingform.bo.RoutingFormInstitutionCostShare;
import org.kuali.module.kra.routingform.bo.RoutingFormKeyword;
import org.kuali.module.kra.routingform.bo.RoutingFormOtherCostShare;
import org.kuali.module.kra.routingform.bo.RoutingFormPurpose;
import org.kuali.module.kra.routingform.bo.RoutingFormResearchRisk;
import org.kuali.module.kra.routingform.bo.RoutingFormResearchType;
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
	private String routingFormContactFaxNumber;
	private String routingFormContactPhoneNumber;
	private Long routingFormContactSystemsIdentifier;
	private boolean routingFormCoProjectDirectorIndicator;
	private boolean routingFormCreditPercentIndicator;
	private Date routingFormCreateDate;
	private boolean routingFormCostShareIndicator;
	private boolean routingFormFederalPassThroughIndicator;
	private String routingFormFellowFirstName;
	private String routingFormFellowEmailAddress;
	private String routingFormFellowFullName;
	private String routingFormFellowLastName;
	private boolean routingFormForeignPartnerIndicator;
	private boolean routingFormForeignTravelIndicator;
	private boolean routingFormIncomeIndicator;
	private boolean routingFormInventionIndicator;
	private String routingFormLayDescription;
	private Date routingFormLastUpdateDate;
	private Long routingFormLastUpdateSystemIdentifier;
	private boolean routingFormNewSpaceIndicator;
	private boolean routingFormOffCampusIndicator;
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
    private boolean routingFormAgencyToBeNamedIndicator;
    private String routingFormCatalogOfFederalDomesticAssistanceNumber;    
    
    // monetary attributes
    private KualiDecimal totalInstitutionCostShareAmount = KualiDecimal.ZERO;
    private KualiDecimal totalOtherCostShareAmount = KualiDecimal.ZERO;
    private KualiDecimal totalSubcontractorAmount = KualiDecimal.ZERO;
    
    private Campus routingFormPhysicalCampus;
    private RoutingFormStatus routingFormStatus;
    private RoutingFormPurpose routingFormPurpose;
    private RoutingFormAgency routingFormAgency;
    private CatalogOfFederalDomesticAssistanceReference catalogOfFederalDomesticAssistanceReference;
    private List<RoutingFormResearchRisk> routingFormResearchRisks;
    private List<RoutingFormKeyword> routingFormKeywords;
    private RoutingFormBudget routingFormBudget;
    private RoutingFormResearchType researchType;
    private SubmissionType submissionType;
    private ContractGrantProposal contractGrantProposal;
    private List<RoutingFormInstitutionCostShare> routingFormInstitutionCostShares;
    private List<RoutingFormOtherCostShare> routingFormOtherCostShares;
    private List<RoutingFormSubcontractor> routingFormSubcontractors;
    private Agency federalPassThroughAgency;
   
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
        
        routingFormResearchRisks = new ArrayList<RoutingFormResearchRisk>();
        routingFormKeywords = new ArrayList<RoutingFormKeyword>();
        routingFormInstitutionCostShares = new ArrayList<RoutingFormInstitutionCostShare>();
        routingFormOtherCostShares = new ArrayList<RoutingFormOtherCostShare>();
        routingFormSubcontractors = new ArrayList<RoutingFormSubcontractor>();
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
	 * Gets the routingFormContactFaxNumber attribute.
	 * 
	 * @return Returns the routingFormContactFaxNumber
	 * 
	 */
	public String getRoutingFormContactFaxNumber() { 
		return routingFormContactFaxNumber;
	}

	/**
	 * Sets the routingFormContactFaxNumber attribute.
	 * 
	 * @param routingFormContactFaxNumber The routingFormContactFaxNumber to set.
	 * 
	 */
	public void setRoutingFormContactFaxNumber(String routingFormContactFaxNumber) {
		this.routingFormContactFaxNumber = routingFormContactFaxNumber;
	}


	/**
	 * Gets the routingFormContactPhoneNumber attribute.
	 * 
	 * @return Returns the routingFormContactPhoneNumber
	 * 
	 */
	public String getRoutingFormContactPhoneNumber() { 
		return routingFormContactPhoneNumber;
	}

	/**
	 * Sets the routingFormContactPhoneNumber attribute.
	 * 
	 * @param routingFormContactPhoneNumber The routingFormContactPhoneNumber to set.
	 * 
	 */
	public void setRoutingFormContactPhoneNumber(String routingFormContactPhoneNumber) {
		this.routingFormContactPhoneNumber = routingFormContactPhoneNumber;
	}


	/**
	 * Gets the routingFormContactSystemsIdentifier attribute.
	 * 
	 * @return Returns the routingFormContactSystemsIdentifier
	 * 
	 */
	public Long getRoutingFormContactSystemsIdentifier() { 
		return routingFormContactSystemsIdentifier;
	}

	/**
	 * Sets the routingFormContactSystemsIdentifier attribute.
	 * 
	 * @param routingFormContactSystemsIdentifier The routingFormContactSystemsIdentifier to set.
	 * 
	 */
	public void setRoutingFormContactSystemsIdentifier(Long routingFormContactSystemsIdentifier) {
		this.routingFormContactSystemsIdentifier = routingFormContactSystemsIdentifier;
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
	 * Gets the routingFormForeignPartnerIndicator attribute.
	 * 
	 * @return Returns the routingFormForeignPartnerIndicator
	 * 
	 */
	public boolean getRoutingFormForeignPartnerIndicator() { 
		return routingFormForeignPartnerIndicator;
	}

	/**
	 * Sets the routingFormForeignPartnerIndicator attribute.
	 * 
	 * @param routingFormForeignPartnerIndicator The routingFormForeignPartnerIndicator to set.
	 * 
	 */
	public void setRoutingFormForeignPartnerIndicator(boolean routingFormForeignPartnerIndicator) {
		this.routingFormForeignPartnerIndicator = routingFormForeignPartnerIndicator;
	}


	/**
	 * Gets the routingFormForeignTravelIndicator attribute.
	 * 
	 * @return Returns the routingFormForeignTravelIndicator
	 * 
	 */
	public boolean getRoutingFormForeignTravelIndicator() { 
		return routingFormForeignTravelIndicator;
	}

	/**
	 * Sets the routingFormForeignTravelIndicator attribute.
	 * 
	 * @param routingFormForeignTravelIndicator The routingFormForeignTravelIndicator to set.
	 * 
	 */
	public void setRoutingFormForeignTravelIndicator(boolean routingFormForeignTravelIndicator) {
		this.routingFormForeignTravelIndicator = routingFormForeignTravelIndicator;
	}


	/**
	 * Gets the routingFormIncomeIndicator attribute.
	 * 
	 * @return Returns the routingFormIncomeIndicator
	 * 
	 */
	public boolean getRoutingFormIncomeIndicator() { 
		return routingFormIncomeIndicator;
	}

	/**
	 * Sets the routingFormIncomeIndicator attribute.
	 * 
	 * @param routingFormIncomeIndicator The routingFormIncomeIndicator to set.
	 * 
	 */
	public void setRoutingFormIncomeIndicator(boolean routingFormIncomeIndicator) {
		this.routingFormIncomeIndicator = routingFormIncomeIndicator;
	}


	/**
	 * Gets the routingFormInventionIndicator attribute.
	 * 
	 * @return Returns the routingFormInventionIndicator
	 * 
	 */
	public boolean getRoutingFormInventionIndicator() { 
		return routingFormInventionIndicator;
	}

	/**
	 * Sets the routingFormInventionIndicator attribute.
	 * 
	 * @param routingFormInventionIndicator The routingFormInventionIndicator to set.
	 * 
	 */
	public void setRoutingFormInventionIndicator(boolean routingFormInventionIndicator) {
		this.routingFormInventionIndicator = routingFormInventionIndicator;
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
	 * Gets the routingFormNewSpaceIndicator attribute.
	 * 
	 * @return Returns the routingFormNewSpaceIndicator
	 * 
	 */
	public boolean getRoutingFormNewSpaceIndicator() { 
		return routingFormNewSpaceIndicator;
	}

	/**
	 * Sets the routingFormNewSpaceIndicator attribute.
	 * 
	 * @param routingFormNewSpaceIndicator The routingFormNewSpaceIndicator to set.
	 * 
	 */
	public void setRoutingFormNewSpaceIndicator(boolean routingFormNewSpaceIndicator) {
		this.routingFormNewSpaceIndicator = routingFormNewSpaceIndicator;
	}


	/**
	 * Gets the routingFormOffCampusIndicator attribute.
	 * 
	 * @return Returns the routingFormOffCampusIndicator
	 * 
	 */
	public boolean getRoutingFormOffCampusIndicator() { 
		return routingFormOffCampusIndicator;
	}

	/**
	 * Sets the routingFormOffCampusIndicator attribute.
	 * 
	 * @param routingFormOffCampusIndicator The routingFormOffCampusIndicator to set.
	 * 
	 */
	public void setRoutingFormOffCampusIndicator(boolean routingFormOffCampusIndicator) {
		this.routingFormOffCampusIndicator = routingFormOffCampusIndicator;
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
     * Gets the routingFormPurpose attribute. 
     * @return Returns the routingFormPurpose.
     */
    public RoutingFormPurpose getRoutingFormPurpose() {
        return routingFormPurpose;
    }

    /**
     * Sets the routingFormPurpose attribute value.
     * @param routingFormPurpose The routingFormPurpose to set.
     * @deprecated
     */
    public void setRoutingFormPurpose(RoutingFormPurpose routingFormPurpose) {
        this.routingFormPurpose = routingFormPurpose;
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

    /**
     * This is a helper method that automatically populates document specfic information into the institution cost share
     * (RoutingFormInstitutionCostShare) instance.
     * 
     * @param routingFormInstitutionCostShare
     */
    public final void prepareNewRoutingFormInstitutionCostShare(RoutingFormInstitutionCostShare routingFormInstitutionCostShare) {
        routingFormInstitutionCostShare.setDocumentNumber(this.getDocumentNumber());
    }

    /**
     * 
     * This method...
     * @param routingFormInstitutionCostShare
     */
    public void addRoutingFormInstitutionCostShare(RoutingFormInstitutionCostShare routingFormInstitutionCostShare) {
        getRoutingFormInstitutionCostShares().add(routingFormInstitutionCostShare);

        // update the overall amount
        this.totalInstitutionCostShareAmount = this.totalInstitutionCostShareAmount.add(new KualiDecimal(routingFormInstitutionCostShare.getRoutingFormCostShareAmount()));
    }

    /**
     * This method removes an institution cost share from the list and updates the total appropriately.
     * 
     * @param index
     */
    public void removeRoutingFormInstitutionCostShare(int index) {
        RoutingFormInstitutionCostShare routingFormInstitutionCostShare = routingFormInstitutionCostShares.remove(index);
        this.totalInstitutionCostShareAmount = this.totalInstitutionCostShareAmount.subtract(new KualiDecimal(routingFormInstitutionCostShare.getRoutingFormCostShareAmount()));
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
     * This is a helper method that automatically populates document specfic information into the other cost share
     * (RoutingFormOtherCostShare) instance.
     * 
     * @param routingFormOtherCostShare
     */
    public final void prepareNewRoutingFormOtherCostShare(RoutingFormOtherCostShare routingFormOtherCostShare) {
        routingFormOtherCostShare.setDocumentNumber(this.getDocumentNumber());
    }

    /**
     * 
     * This method...
     * @param routingFormOtherCostShare
     */
    public void addRoutingFormOtherCostShare(RoutingFormOtherCostShare routingFormOtherCostShare) {
        getRoutingFormOtherCostShares().add(routingFormOtherCostShare);

        // update the overall amount
        this.totalOtherCostShareAmount = this.totalOtherCostShareAmount.add(new KualiDecimal(routingFormOtherCostShare.getRoutingFormCostShareAmount()));
    }

    /**
     * This method removes an other cost share from the list and updates the total appropriately.
     * 
     * @param index
     */
    public void removeRoutingFormOtherCostShare(int index) {
        RoutingFormOtherCostShare routingFormOtherCostShare = routingFormOtherCostShares.remove(index);
        this.totalOtherCostShareAmount = this.totalOtherCostShareAmount.subtract(new KualiDecimal(routingFormOtherCostShare.getRoutingFormCostShareAmount()));
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
     * This is a helper method that automatically populates document specfic information into the subcontractor
     * (RoutingFormSubcontractor) instance.
     * 
     * @param routingFormSubcontractor
     */
    public final void prepareNewRoutingFormSubcontractor(RoutingFormSubcontractor routingFormSubcontractor) {
        routingFormSubcontractor.setDocumentNumber(this.getDocumentNumber());
    }

    /**
     * 
     * This method...
     * @param routingFormSubcontractor
     */
    public void addRoutingFormSubcontractor(RoutingFormSubcontractor routingFormSubcontractor) {
        getRoutingFormSubcontractors().add(routingFormSubcontractor);

        // update the overall amount
        this.totalSubcontractorAmount = this.totalSubcontractorAmount.add(new KualiDecimal(routingFormSubcontractor.getRoutingFormSubcontractorAmount()));
    }

    /**
     * This method removes a subcontractor from the list and updates the total appropriately.
     * 
     * @param index
     */
    public void removeRoutingFormSubcontractor(int index) {
        RoutingFormSubcontractor routingFormSubcontractor = routingFormSubcontractors.remove(index);
        this.totalSubcontractorAmount = this.totalSubcontractorAmount.subtract(new KualiDecimal(routingFormSubcontractor.getRoutingFormSubcontractorAmount()));
    }

    /**
     * 
     * This method...
     * @return
     */
    public KualiDecimal getTotalInstitutionCostShareAmount() {
        return totalInstitutionCostShareAmount;
    }

    /**
     * This method returns the institution total amount as a currency formatted string.
     * 
     * @return String
     */
    public String getCurrencyFormattedTotalInstitutionCostShareAmount() {
        return (String) new CurrencyFormatter().format(totalInstitutionCostShareAmount);
    }

    /**
     * 
     * This method...
     * @param totalInstitutionCostShareAmount
     */
    public void setTotalInstitutionCostShareAmount(KualiDecimal totalInstitutionCostShareAmount) {
        this.totalInstitutionCostShareAmount = totalInstitutionCostShareAmount;
    }

    /**
     * 
     * This method...
     * @return
     */
    public KualiDecimal getTotalOtherCostShareAmount() {
        return totalOtherCostShareAmount;
    }

    /**
     * This method returns the other cost share total amount as a currency formatted string.
     * 
     * @return String
     */
    public String getCurrencyFormattedTotalOtherCostShareAmount() {
        return (String) new CurrencyFormatter().format(totalOtherCostShareAmount);
    }

    /**
     * 
     * This method...
     * @param totalOtherCostShareAmount
     */
    public void setTotalOtherCostShareAmount(KualiDecimal totalOtherCostShareAmount) {
        this.totalOtherCostShareAmount = totalOtherCostShareAmount;
    }

    /**
     * 
     * This method...
     * @return
     */
    public KualiDecimal getTotalSubcontractorAmount() {
        return totalSubcontractorAmount;
    }

    /**
     * This method returns the subcontractor total amount as a currency formatted string.
     * 
     * @return String
     */
    public String getCurrencyFormattedTotalSubcontractorAmount() {
        return (String) new CurrencyFormatter().format(totalSubcontractorAmount);
    }

    /**
     * 
     * This method...
     * @param totalSubcontractorAmount
     */
    public void setTotalSubcontractorAmount(KualiDecimal totalSubcontractorAmount) {
        this.totalSubcontractorAmount = totalSubcontractorAmount;
    }
    
    public Agency getFederalPassThroughAgency() {
        return federalPassThroughAgency;
    }

    public void setFederalPassThroughAgency(Agency federalPassThroughAgency) {
        this.federalPassThroughAgency = federalPassThroughAgency;
    }

    @Override
    public List buildListOfDeletionAwareLists() {
        List list = new ArrayList();
        //TODO Figure out a way to add the appropriate number of lists on the 2nd pass
        if (routingFormResearchRisks.isEmpty()) {
            for (int i = 0; i < 6; i++) {
                list.add(new ArrayList());
            }
        }
        else {
            for (RoutingFormResearchRisk researchRisk: this.routingFormResearchRisks) {
                list.add(researchRisk.getResearchRiskStudies());
            }
        }
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
}
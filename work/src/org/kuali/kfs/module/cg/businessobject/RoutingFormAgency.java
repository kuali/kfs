/*
 * Copyright 2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/businessobject/RoutingFormAgency.java,v $
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

package org.kuali.module.kra.routingform.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.module.cg.bo.Agency;
import org.kuali.PropertyConstants;

/**
 * 
 */
public class RoutingFormAgency extends BusinessObjectBase {

	private String documentNumber;
	private String agencyAddressDescription;
	private String agencyContactName;
	private String agencyCountryCode;
	private String agencyCityName;
	private boolean agencyDiskAccompanyIndicator;
	private boolean agencyElectronicSubmissionIndicator;
	private String agencyLine1Address;
	private String agencyLine2Address;
	private String agencyLine3Address;
	private String agencyLine4Address;
	private String agencyLine5Address;
	private String agencyLine6Address;
	private String agencyLine7Address;
	private String agencyLine8Address;
	private String agencyNumber;
	private String agencyShippingInstructionsDescription;
	private String agencyStateCode;
	private String agencyZipCode;
	private String routingFormDueDateTypeCode;
	private Date routingFormDueDate;
	private String routingFormDueTime;
	private Integer routingFormRequiredCopyNumber;
	private String routingFormRequiredCopyText;
	private Date routingFormSubmitDate;

    private RoutingFormDueDateType routingFormDueDateType;
    private Agency routingFormAgency;
    
	/**
	 * Default constructor.
	 */
	public RoutingFormAgency() {

	}

	/**
	 * Gets the documentNumber attribute.
	 * 
	 * @return Returns the documentNumber
	 * 
	 */
	public String getDocumentNumber() { 
		return documentNumber;
	}

	/**
	 * Sets the documentNumber attribute.
	 * 
	 * @param documentNumber The documentNumber to set.
	 * 
	 */
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}


	/**
	 * Gets the agencyAddressDescription attribute.
	 * 
	 * @return Returns the agencyAddressDescription
	 * 
	 */
	public String getAgencyAddressDescription() { 
		return agencyAddressDescription;
	}

	/**
	 * Sets the agencyAddressDescription attribute.
	 * 
	 * @param agencyAddressDescription The agencyAddressDescription to set.
	 * 
	 */
	public void setAgencyAddressDescription(String agencyAddressDescription) {
		this.agencyAddressDescription = agencyAddressDescription;
	}


	/**
	 * Gets the agencyContactName attribute.
	 * 
	 * @return Returns the agencyContactName
	 * 
	 */
	public String getAgencyContactName() { 
		return agencyContactName;
	}

	/**
	 * Sets the agencyContactName attribute.
	 * 
	 * @param agencyContactName The agencyContactName to set.
	 * 
	 */
	public void setAgencyContactName(String agencyContactName) {
		this.agencyContactName = agencyContactName;
	}


	/**
	 * Gets the agencyCountryCode attribute.
	 * 
	 * @return Returns the agencyCountryCode
	 * 
	 */
	public String getAgencyCountryCode() { 
		return agencyCountryCode;
	}

	/**
	 * Sets the agencyCountryCode attribute.
	 * 
	 * @param agencyCountryCode The agencyCountryCode to set.
	 * 
	 */
	public void setAgencyCountryCode(String agencyCountryCode) {
		this.agencyCountryCode = agencyCountryCode;
	}


	/**
	 * Gets the agencyCityName attribute.
	 * 
	 * @return Returns the agencyCityName
	 * 
	 */
	public String getAgencyCityName() { 
		return agencyCityName;
	}

	/**
	 * Sets the agencyCityName attribute.
	 * 
	 * @param agencyCityName The agencyCityName to set.
	 * 
	 */
	public void setAgencyCityName(String agencyCityName) {
		this.agencyCityName = agencyCityName;
	}


	/**
	 * Gets the agencyDiskAccompanyIndicator attribute.
	 * 
	 * @return Returns the agencyDiskAccompanyIndicator
	 * 
	 */
	public boolean getAgencyDiskAccompanyIndicator() { 
		return agencyDiskAccompanyIndicator;
	}

	/**
	 * Sets the agencyDiskAccompanyIndicator attribute.
	 * 
	 * @param agencyDiskAccompanyIndicator The agencyDiskAccompanyIndicator to set.
	 * 
	 */
	public void setAgencyDiskAccompanyIndicator(boolean agencyDiskAccompanyIndicator) {
		this.agencyDiskAccompanyIndicator = agencyDiskAccompanyIndicator;
	}


	/**
	 * Gets the agencyElectronicSubmissionIndicator attribute.
	 * 
	 * @return Returns the agencyElectronicSubmissionIndicator
	 * 
	 */
	public boolean getAgencyElectronicSubmissionIndicator() { 
		return agencyElectronicSubmissionIndicator;
	}

	/**
	 * Sets the agencyElectronicSubmissionIndicator attribute.
	 * 
	 * @param agencyElectronicSubmissionIndicator The agencyElectronicSubmissionIndicator to set.
	 * 
	 */
	public void setAgencyElectronicSubmissionIndicator(boolean agencyElectronicSubmissionIndicator) {
		this.agencyElectronicSubmissionIndicator = agencyElectronicSubmissionIndicator;
	}


	/**
	 * Gets the agencyLine1Address attribute.
	 * 
	 * @return Returns the agencyLine1Address
	 * 
	 */
	public String getAgencyLine1Address() { 
		return agencyLine1Address;
	}

	/**
	 * Sets the agencyLine1Address attribute.
	 * 
	 * @param agencyLine1Address The agencyLine1Address to set.
	 * 
	 */
	public void setAgencyLine1Address(String agencyLine1Address) {
		this.agencyLine1Address = agencyLine1Address;
	}


	/**
	 * Gets the agencyLine2Address attribute.
	 * 
	 * @return Returns the agencyLine2Address
	 * 
	 */
	public String getAgencyLine2Address() { 
		return agencyLine2Address;
	}

	/**
	 * Sets the agencyLine2Address attribute.
	 * 
	 * @param agencyLine2Address The agencyLine2Address to set.
	 * 
	 */
	public void setAgencyLine2Address(String agencyLine2Address) {
		this.agencyLine2Address = agencyLine2Address;
	}


	/**
	 * Gets the agencyLine3Address attribute.
	 * 
	 * @return Returns the agencyLine3Address
	 * 
	 */
	public String getAgencyLine3Address() { 
		return agencyLine3Address;
	}

	/**
	 * Sets the agencyLine3Address attribute.
	 * 
	 * @param agencyLine3Address The agencyLine3Address to set.
	 * 
	 */
	public void setAgencyLine3Address(String agencyLine3Address) {
		this.agencyLine3Address = agencyLine3Address;
	}


	/**
	 * Gets the agencyLine4Address attribute.
	 * 
	 * @return Returns the agencyLine4Address
	 * 
	 */
	public String getAgencyLine4Address() { 
		return agencyLine4Address;
	}

	/**
	 * Sets the agencyLine4Address attribute.
	 * 
	 * @param agencyLine4Address The agencyLine4Address to set.
	 * 
	 */
	public void setAgencyLine4Address(String agencyLine4Address) {
		this.agencyLine4Address = agencyLine4Address;
	}


	/**
	 * Gets the agencyLine5Address attribute.
	 * 
	 * @return Returns the agencyLine5Address
	 * 
	 */
	public String getAgencyLine5Address() { 
		return agencyLine5Address;
	}

	/**
	 * Sets the agencyLine5Address attribute.
	 * 
	 * @param agencyLine5Address The agencyLine5Address to set.
	 * 
	 */
	public void setAgencyLine5Address(String agencyLine5Address) {
		this.agencyLine5Address = agencyLine5Address;
	}


	/**
	 * Gets the agencyLine6Address attribute.
	 * 
	 * @return Returns the agencyLine6Address
	 * 
	 */
	public String getAgencyLine6Address() { 
		return agencyLine6Address;
	}

	/**
	 * Sets the agencyLine6Address attribute.
	 * 
	 * @param agencyLine6Address The agencyLine6Address to set.
	 * 
	 */
	public void setAgencyLine6Address(String agencyLine6Address) {
		this.agencyLine6Address = agencyLine6Address;
	}


	/**
	 * Gets the agencyLine7Address attribute.
	 * 
	 * @return Returns the agencyLine7Address
	 * 
	 */
	public String getAgencyLine7Address() { 
		return agencyLine7Address;
	}

	/**
	 * Sets the agencyLine7Address attribute.
	 * 
	 * @param agencyLine7Address The agencyLine7Address to set.
	 * 
	 */
	public void setAgencyLine7Address(String agencyLine7Address) {
		this.agencyLine7Address = agencyLine7Address;
	}


	/**
	 * Gets the agencyLine8Address attribute.
	 * 
	 * @return Returns the agencyLine8Address
	 * 
	 */
	public String getAgencyLine8Address() { 
		return agencyLine8Address;
	}

	/**
	 * Sets the agencyLine8Address attribute.
	 * 
	 * @param agencyLine8Address The agencyLine8Address to set.
	 * 
	 */
	public void setAgencyLine8Address(String agencyLine8Address) {
		this.agencyLine8Address = agencyLine8Address;
	}


	/**
	 * Gets the agencyNumber attribute.
	 * 
	 * @return Returns the agencyNumber
	 * 
	 */
	public String getAgencyNumber() { 
		return agencyNumber;
	}

	/**
	 * Sets the agencyNumber attribute.
	 * 
	 * @param agencyNumber The agencyNumber to set.
	 * 
	 */
	public void setAgencyNumber(String agencyNumber) {
		this.agencyNumber = agencyNumber;
	}


	/**
	 * Gets the agencyShippingInstructionsDescription attribute.
	 * 
	 * @return Returns the agencyShippingInstructionsDescription
	 * 
	 */
	public String getAgencyShippingInstructionsDescription() { 
		return agencyShippingInstructionsDescription;
	}

	/**
	 * Sets the agencyShippingInstructionsDescription attribute.
	 * 
	 * @param agencyShippingInstructionsDescription The agencyShippingInstructionsDescription to set.
	 * 
	 */
	public void setAgencyShippingInstructionsDescription(String agencyShippingInstructionsDescription) {
		this.agencyShippingInstructionsDescription = agencyShippingInstructionsDescription;
	}


	/**
	 * Gets the agencyStateCode attribute.
	 * 
	 * @return Returns the agencyStateCode
	 * 
	 */
	public String getAgencyStateCode() { 
		return agencyStateCode;
	}

	/**
	 * Sets the agencyStateCode attribute.
	 * 
	 * @param agencyStateCode The agencyStateCode to set.
	 * 
	 */
	public void setAgencyStateCode(String agencyStateCode) {
		this.agencyStateCode = agencyStateCode;
	}


	/**
	 * Gets the agencyZipCode attribute.
	 * 
	 * @return Returns the agencyZipCode
	 * 
	 */
	public String getAgencyZipCode() { 
		return agencyZipCode;
	}

	/**
	 * Sets the agencyZipCode attribute.
	 * 
	 * @param agencyZipCode The agencyZipCode to set.
	 * 
	 */
	public void setAgencyZipCode(String agencyZipCode) {
		this.agencyZipCode = agencyZipCode;
	}


	/**
	 * Gets the routingFormDueDateTypeCode attribute.
	 * 
	 * @return Returns the routingFormDueDateTypeCode
	 * 
	 */
	public String getRoutingFormDueDateTypeCode() { 
		return routingFormDueDateTypeCode;
	}

	/**
	 * Sets the routingFormDueDateTypeCode attribute.
	 * 
	 * @param routingFormDueDateTypeCode The routingFormDueDateTypeCode to set.
	 * 
	 */
	public void setRoutingFormDueDateTypeCode(String routingFormDueDateTypeCode) {
		this.routingFormDueDateTypeCode = routingFormDueDateTypeCode;
	}


	/**
	 * Gets the routingFormDueDate attribute.
	 * 
	 * @return Returns the routingFormDueDate
	 * 
	 */
	public Date getRoutingFormDueDate() { 
		return routingFormDueDate;
	}

	/**
	 * Sets the routingFormDueDate attribute.
	 * 
	 * @param routingFormDueDate The routingFormDueDate to set.
	 * 
	 */
	public void setRoutingFormDueDate(Date routingFormDueDate) {
		this.routingFormDueDate = routingFormDueDate;
	}


	/**
	 * Gets the routingFormDueTime attribute.
	 * 
	 * @return Returns the routingFormDueTime
	 * 
	 */
	public String getRoutingFormDueTime() { 
		return routingFormDueTime;
	}

	/**
	 * Sets the routingFormDueTime attribute.
	 * 
	 * @param routingFormDueTime The routingFormDueTime to set.
	 * 
	 */
	public void setRoutingFormDueTime(String routingFormDueTime) {
		this.routingFormDueTime = routingFormDueTime;
	}


	/**
	 * Gets the routingFormRequiredCopyNumber attribute.
	 * 
	 * @return Returns the routingFormRequiredCopyNumber
	 * 
	 */
	public Integer getRoutingFormRequiredCopyNumber() { 
		return routingFormRequiredCopyNumber;
	}

	/**
	 * Sets the routingFormRequiredCopyNumber attribute.
	 * 
	 * @param routingFormRequiredCopyNumber The routingFormRequiredCopyNumber to set.
	 * 
	 */
	public void setRoutingFormRequiredCopyNumber(Integer routingFormRequiredCopyNumber) {
		this.routingFormRequiredCopyNumber = routingFormRequiredCopyNumber;
	}


	/**
	 * Gets the routingFormRequiredCopyText attribute.
	 * 
	 * @return Returns the routingFormRequiredCopyText
	 * 
	 */
	public String getRoutingFormRequiredCopyText() { 
		return routingFormRequiredCopyText;
	}

	/**
	 * Sets the routingFormRequiredCopyText attribute.
	 * 
	 * @param routingFormRequiredCopyText The routingFormRequiredCopyText to set.
	 * 
	 */
	public void setRoutingFormRequiredCopyText(String routingFormRequiredCopyText) {
		this.routingFormRequiredCopyText = routingFormRequiredCopyText;
	}


	/**
	 * Gets the routingFormSubmitDate attribute.
	 * 
	 * @return Returns the routingFormSubmitDate
	 * 
	 */
	public Date getRoutingFormSubmitDate() { 
		return routingFormSubmitDate;
	}

	/**
	 * Sets the routingFormSubmitDate attribute.
	 * 
	 * @param routingFormSubmitDate The routingFormSubmitDate to set.
	 * 
	 */
	public void setRoutingFormSubmitDate(Date routingFormSubmitDate) {
		this.routingFormSubmitDate = routingFormSubmitDate;
	}

    /**
     * Gets the routingFormDueDateType attribute. 
     * @return Returns the routingFormDueDateType.
     */
    public RoutingFormDueDateType getRoutingFormDueDateType() {
        return routingFormDueDateType;
    }

    /**
     * Sets the routingFormDueDateType attribute value.
     * @param routingFormDueDateType The routingFormDueDateType to set.
     * @deprecated
     */
    public void setRoutingFormDueDateType(RoutingFormDueDateType routingFormDueDateType) {
        this.routingFormDueDateType = routingFormDueDateType;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put(PropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }

    public Agency getRoutingFormAgency() {
        return routingFormAgency;
    }

    public void setRoutingFormAgency(Agency routingFormAgency) {
        this.routingFormAgency = routingFormAgency;
    }

}

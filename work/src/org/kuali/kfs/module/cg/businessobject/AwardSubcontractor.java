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

package org.kuali.module.cg.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;

/**
 * 
 */
public class AwardSubcontractor extends PersistableBusinessObjectBase {

    private String awardSubcontractorAmendmentNumber;
    private String awardSubcontractorNumber;
    private String subcontractorNumber;
    private Long proposalNumber;
    private KualiDecimal subcontractorAmount;
    private String subcontractorContactFirstName;
    private String subcontractorContactLastName;
    private String subcontractorAuditHistoryText;
    private String awardSubcontractorDescription;

    private Subcontractor subcontractor;

    /**
     * Default constructor.
     */
    public AwardSubcontractor() {

    }

    /**
     * Gets the awardSubcontractorAmendmentNumber attribute.
     * 
     * @return Returns the awardSubcontractorAmendmentNumber
     */
    public String getAwardSubcontractorAmendmentNumber() {
        return awardSubcontractorAmendmentNumber;
    }

    /**
     * Sets the awardSubcontractorAmendmentNumber attribute.
     * 
     * @param awardSubcontractorAmendmentNumber The awardSubcontractorAmendmentNumber to set.
     */
    public void setAwardSubcontractorAmendmentNumber(String awardSubcontractorAmendmentNumber) {
        this.awardSubcontractorAmendmentNumber = awardSubcontractorAmendmentNumber;
    }


    /**
     * Gets the awardSubcontractorNumber attribute.
     * 
     * @return Returns the awardSubcontractorNumber
     */
    public String getAwardSubcontractorNumber() {
        return awardSubcontractorNumber;
    }

    /**
     * Sets the awardSubcontractorNumber attribute.
     * 
     * @param awardSubcontractorNumber The awardSubcontractorNumber to set.
     */
    public void setAwardSubcontractorNumber(String awardSubcontractorNumber) {
        this.awardSubcontractorNumber = awardSubcontractorNumber;
    }


    /**
     * Gets the subcontractorNumber attribute.
     * 
     * @return Returns the subcontractorNumber
     */
    public String getSubcontractorNumber() {
        return subcontractorNumber;
    }

    /**
     * Sets the subcontractorNumber attribute.
     * 
     * @param subcontractorNumber The subcontractorNumber to set.
     */
    public void setSubcontractorNumber(String subcontractorNumber) {
        this.subcontractorNumber = subcontractorNumber;
    }


    /**
     * Gets the proposalNumber attribute.
     * 
     * @return Returns the proposalNumber
     */
    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the proposalNumber attribute.
     * 
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }


    /**
     * Gets the subcontractorAmount attribute.
     * 
     * @return Returns the subcontractorAmount
     */
    public KualiDecimal getSubcontractorAmount() {
        return subcontractorAmount;
    }

    /**
     * Sets the subcontractorAmount attribute.
     * 
     * @param subcontractorAmount The subcontractorAmount to set.
     */
    public void setSubcontractorAmount(KualiDecimal subcontractorAmount) {
        this.subcontractorAmount = subcontractorAmount;
    }


    /**
     * Gets the subcontractorContactFirstName attribute.
     * 
     * @return Returns the subcontractorContactFirstName
     */
    public String getSubcontractorContactFirstName() {
        return subcontractorContactFirstName;
    }

    /**
     * Sets the subcontractorContactFirstName attribute.
     * 
     * @param subcontractorContactFirstName The subcontractorContactFirstName to set.
     */
    public void setSubcontractorContactFirstName(String subcontractorContactFirstName) {
        this.subcontractorContactFirstName = subcontractorContactFirstName;
    }


    /**
     * Gets the subcontractorContactLastName attribute.
     * 
     * @return Returns the subcontractorContactLastName
     */
    public String getSubcontractorContactLastName() {
        return subcontractorContactLastName;
    }

    /**
     * Sets the subcontractorContactLastName attribute.
     * 
     * @param subcontractorContactLastName The subcontractorContactLastName to set.
     */
    public void setSubcontractorContactLastName(String subcontractorContactLastName) {
        this.subcontractorContactLastName = subcontractorContactLastName;
    }


    /**
     * Gets the subcontractorAuditHistoryText attribute.
     * 
     * @return Returns the subcontractorAuditHistoryText
     */
    public String getSubcontractorAuditHistoryText() {
        return subcontractorAuditHistoryText;
    }

    /**
     * Sets the subcontractorAuditHistoryText attribute.
     * 
     * @param subcontractorAuditHistoryText The subcontractorAuditHistoryText to set.
     */
    public void setSubcontractorAuditHistoryText(String subcontractorAuditHistoryText) {
        this.subcontractorAuditHistoryText = subcontractorAuditHistoryText;
    }

    /**
     * Gets the awardSubcontractorDescription attribute.
     * 
     * @return Returns the awardSubcontractorDescription.
     */
    public String getAwardSubcontractorDescription() {
        return awardSubcontractorDescription;
    }

    /**
     * Sets the awardSubcontractorDescription attribute value.
     * 
     * @param awardSubcontractorDescription The awardSubcontractorDescription to set.
     */
    public void setAwardSubcontractorDescription(String awardSubcontractorDescription) {
        this.awardSubcontractorDescription = awardSubcontractorDescription;
    }

    /**
     * @return Returns the subcontractor.
     */
    public Subcontractor getSubcontractor() {
        return subcontractor;
    }

    /**
     * @param subcontractor The subcontractor to set.
     * @deprecated
     */
    @Deprecated
    public void setSubcontractor(Subcontractor subcontractor) {
        this.subcontractor = subcontractor;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("awardSubcontractorAmendmentNumber", this.awardSubcontractorAmendmentNumber);
        m.put("awardSubcontractorNumber", this.awardSubcontractorNumber);
        m.put("subcontractorNumber", this.subcontractorNumber);
        if (this.proposalNumber != null) {
            m.put("proposalNumber", this.proposalNumber.toString());
        }
        return m;
    }

}

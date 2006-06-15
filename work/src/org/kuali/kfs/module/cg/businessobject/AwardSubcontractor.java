/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.cg.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AwardSubcontractor extends BusinessObjectBase {

    private String awardSubcontractorAmendmentNumber;
    private String awardSubcontractorNumber;
    private String subcontractorNumber;
    private Long proposalNumber;
    private KualiDecimal subcontractorAmount;
    private String subcontractorContactFirstName;
    private String subcontractorContactLastName;
    private String subcontractorAuditHistoryText;

    private Subcontractor subcontractor;

    /**
     * Default constructor.
     */
    public AwardSubcontractor() {

    }

    /**
     * Gets the awardSubcontractorAmendmentNumber attribute.
     * 
     * @return - Returns the awardSubcontractorAmendmentNumber
     * 
     */
    public String getAwardSubcontractorAmendmentNumber() {
        return awardSubcontractorAmendmentNumber;
    }

    /**
     * Sets the awardSubcontractorAmendmentNumber attribute.
     * 
     * @param awardSubcontractorAmendmentNumber The awardSubcontractorAmendmentNumber to set.
     * 
     */
    public void setAwardSubcontractorAmendmentNumber(String awardSubcontractorAmendmentNumber) {
        this.awardSubcontractorAmendmentNumber = awardSubcontractorAmendmentNumber;
    }


    /**
     * Gets the awardSubcontractorNumber attribute.
     * 
     * @return - Returns the awardSubcontractorNumber
     * 
     */
    public String getAwardSubcontractorNumber() {
        return awardSubcontractorNumber;
    }

    /**
     * Sets the awardSubcontractorNumber attribute.
     * 
     * @param awardSubcontractorNumber The awardSubcontractorNumber to set.
     * 
     */
    public void setAwardSubcontractorNumber(String awardSubcontractorNumber) {
        this.awardSubcontractorNumber = awardSubcontractorNumber;
    }


    /**
     * Gets the subcontractorNumber attribute.
     * 
     * @return - Returns the subcontractorNumber
     * 
     */
    public String getSubcontractorNumber() {
        return subcontractorNumber;
    }

    /**
     * Sets the subcontractorNumber attribute.
     * 
     * @param subcontractorNumber The subcontractorNumber to set.
     * 
     */
    public void setSubcontractorNumber(String subcontractorNumber) {
        this.subcontractorNumber = subcontractorNumber;
    }


    /**
     * Gets the proposalNumber attribute.
     * 
     * @return - Returns the proposalNumber
     * 
     */
    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the proposalNumber attribute.
     * 
     * @param proposalNumber The proposalNumber to set.
     * 
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }


    /**
     * Gets the subcontractorAmount attribute.
     * 
     * @return - Returns the subcontractorAmount
     * 
     */
    public KualiDecimal getSubcontractorAmount() {
        return subcontractorAmount;
    }

    /**
     * Sets the subcontractorAmount attribute.
     * 
     * @param subcontractorAmount The subcontractorAmount to set.
     * 
     */
    public void setSubcontractorAmount(KualiDecimal subcontractorAmount) {
        this.subcontractorAmount = subcontractorAmount;
    }


    /**
     * Gets the subcontractorContactFirstName attribute.
     * 
     * @return - Returns the subcontractorContactFirstName
     * 
     */
    public String getSubcontractorContactFirstName() {
        return subcontractorContactFirstName;
    }

    /**
     * Sets the subcontractorContactFirstName attribute.
     * 
     * @param subcontractorContactFirstName The subcontractorContactFirstName to set.
     * 
     */
    public void setSubcontractorContactFirstName(String subcontractorContactFirstName) {
        this.subcontractorContactFirstName = subcontractorContactFirstName;
    }


    /**
     * Gets the subcontractorContactLastName attribute.
     * 
     * @return - Returns the subcontractorContactLastName
     * 
     */
    public String getSubcontractorContactLastName() {
        return subcontractorContactLastName;
    }

    /**
     * Sets the subcontractorContactLastName attribute.
     * 
     * @param subcontractorContactLastName The subcontractorContactLastName to set.
     * 
     */
    public void setSubcontractorContactLastName(String subcontractorContactLastName) {
        this.subcontractorContactLastName = subcontractorContactLastName;
    }


    /**
     * Gets the subcontractorAuditHistoryText attribute.
     * 
     * @return - Returns the subcontractorAuditHistoryText
     * 
     */
    public String getSubcontractorAuditHistoryText() {
        return subcontractorAuditHistoryText;
    }

    /**
     * Sets the subcontractorAuditHistoryText attribute.
     * 
     * @param subcontractorAuditHistoryText The subcontractorAuditHistoryText to set.
     * 
     */
    public void setSubcontractorAuditHistoryText(String subcontractorAuditHistoryText) {
        this.subcontractorAuditHistoryText = subcontractorAuditHistoryText;
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
    public void setSubcontractor(Subcontractor subcontractor) {
        this.subcontractor = subcontractor;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
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

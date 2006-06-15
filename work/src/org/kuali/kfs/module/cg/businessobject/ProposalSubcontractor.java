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
public class ProposalSubcontractor extends BusinessObjectBase {

    private String proposalSubcontractorNumber;
    private Long proposalNumber;
    private String subcontractorNumber;
    private KualiDecimal proposalSubcontractorAmount;

    private Subcontractor subcontractor;

    /**
     * Default constructor.
     */
    public ProposalSubcontractor() {

    }

    /**
     * Gets the proposalSubcontractorNumber attribute.
     * 
     * @return - Returns the proposalSubcontractorNumber
     * 
     */
    public String getProposalSubcontractorNumber() {
        return proposalSubcontractorNumber;
    }

    /**
     * Sets the proposalSubcontractorNumber attribute.
     * 
     * @param proposalSubcontractorNumber The proposalSubcontractorNumber to set.
     * 
     */
    public void setProposalSubcontractorNumber(String proposalSubcontractorNumber) {
        this.proposalSubcontractorNumber = proposalSubcontractorNumber;
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
     * Gets the proposalSubcontractorAmount attribute.
     * 
     * @return - Returns the proposalSubcontractorAmount
     * 
     */
    public KualiDecimal getProposalSubcontractorAmount() {
        return proposalSubcontractorAmount;
    }

    /**
     * Sets the proposalSubcontractorAmount attribute.
     * 
     * @param proposalSubcontractorAmount The proposalSubcontractorAmount to set.
     * 
     */
    public void setProposalSubcontractorAmount(KualiDecimal proposalSubcontractorAmount) {
        this.proposalSubcontractorAmount = proposalSubcontractorAmount;
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
        m.put("proposalSubcontractorNumber", this.proposalSubcontractorNumber);
        if (this.proposalNumber != null) {
            m.put("proposalNumber", this.proposalNumber.toString());
        }
        m.put("subcontractorNumber", this.subcontractorNumber);
        return m;
    }


}

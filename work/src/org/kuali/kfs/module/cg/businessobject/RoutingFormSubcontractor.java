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

package org.kuali.module.kra.routingform.bo;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class RoutingFormSubcontractor extends BusinessObjectBase {

	private String researchDocumentNumber;
	private Integer proposalSubcontractorSequenceNumber;
	private BigDecimal proposalSubcontractorAmount;
	private String proposalSubcontractorNumber;
	private String proposalSubcontractorName;

	/**
	 * Default constructor.
	 */
	public RoutingFormSubcontractor() {

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
	 * Gets the proposalSubcontractorSequenceNumber attribute.
	 * 
	 * @return - Returns the proposalSubcontractorSequenceNumber
	 * 
	 */
	public Integer getProposalSubcontractorSequenceNumber() { 
		return proposalSubcontractorSequenceNumber;
	}

	/**
	 * Sets the proposalSubcontractorSequenceNumber attribute.
	 * 
	 * @param - proposalSubcontractorSequenceNumber The proposalSubcontractorSequenceNumber to set.
	 * 
	 */
	public void setProposalSubcontractorSequenceNumber(Integer proposalSubcontractorSequenceNumber) {
		this.proposalSubcontractorSequenceNumber = proposalSubcontractorSequenceNumber;
	}


	/**
	 * Gets the proposalSubcontractorAmount attribute.
	 * 
	 * @return - Returns the proposalSubcontractorAmount
	 * 
	 */
	public BigDecimal getProposalSubcontractorAmount() { 
		return proposalSubcontractorAmount;
	}

	/**
	 * Sets the proposalSubcontractorAmount attribute.
	 * 
	 * @param - proposalSubcontractorAmount The proposalSubcontractorAmount to set.
	 * 
	 */
	public void setProposalSubcontractorAmount(BigDecimal proposalSubcontractorAmount) {
		this.proposalSubcontractorAmount = proposalSubcontractorAmount;
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
	 * @param - proposalSubcontractorNumber The proposalSubcontractorNumber to set.
	 * 
	 */
	public void setProposalSubcontractorNumber(String proposalSubcontractorNumber) {
		this.proposalSubcontractorNumber = proposalSubcontractorNumber;
	}


	/**
	 * Gets the proposalSubcontractorName attribute.
	 * 
	 * @return - Returns the proposalSubcontractorName
	 * 
	 */
	public String getProposalSubcontractorName() { 
		return proposalSubcontractorName;
	}

	/**
	 * Sets the proposalSubcontractorName attribute.
	 * 
	 * @param - proposalSubcontractorName The proposalSubcontractorName to set.
	 * 
	 */
	public void setProposalSubcontractorName(String proposalSubcontractorName) {
		this.proposalSubcontractorName = proposalSubcontractorName;
	}


	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("researchDocumentNumber", this.researchDocumentNumber);
        if (this.proposalSubcontractorSequenceNumber != null) {
            m.put("proposalSubcontractorSequenceNumber", this.proposalSubcontractorSequenceNumber.toString());
        }
	    return m;
    }
}

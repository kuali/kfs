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
public class RoutingFormOtherCostShare extends BusinessObjectBase {

	private Integer proposalCostShareSequenceNumber;
	private String researchDocumentNumber;
	private String proposalCostShareSourceName;
	private BigDecimal proposalCostShareAmount;

	/**
	 * Default constructor.
	 */
	public RoutingFormOtherCostShare() {

	}

	/**
	 * Gets the proposalCostShareSequenceNumber attribute.
	 * 
	 * @return - Returns the proposalCostShareSequenceNumber
	 * 
	 */
	public Integer getProposalCostShareSequenceNumber() { 
		return proposalCostShareSequenceNumber;
	}

	/**
	 * Sets the proposalCostShareSequenceNumber attribute.
	 * 
	 * @param - proposalCostShareSequenceNumber The proposalCostShareSequenceNumber to set.
	 * 
	 */
	public void setProposalCostShareSequenceNumber(Integer proposalCostShareSequenceNumber) {
		this.proposalCostShareSequenceNumber = proposalCostShareSequenceNumber;
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
	 * Gets the proposalCostShareSourceName attribute.
	 * 
	 * @return - Returns the proposalCostShareSourceName
	 * 
	 */
	public String getProposalCostShareSourceName() { 
		return proposalCostShareSourceName;
	}

	/**
	 * Sets the proposalCostShareSourceName attribute.
	 * 
	 * @param - proposalCostShareSourceName The proposalCostShareSourceName to set.
	 * 
	 */
	public void setProposalCostShareSourceName(String proposalCostShareSourceName) {
		this.proposalCostShareSourceName = proposalCostShareSourceName;
	}

	/**
	 * Gets the proposalCostShareAmount attribute.
	 * 
	 * @return - Returns the proposalCostShareAmount
	 * 
	 */
	public BigDecimal getProposalCostShareAmount() { 
		return proposalCostShareAmount;
	}

	/**
	 * Sets the proposalCostShareAmount attribute.
	 * 
	 * @param - proposalCostShareAmount The proposalCostShareAmount to set.
	 * 
	 */
	public void setProposalCostShareAmount(BigDecimal proposalCostShareAmount) {
		this.proposalCostShareAmount = proposalCostShareAmount;
	}


	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.proposalCostShareSequenceNumber != null) {
            m.put("proposalCostShareSequenceNumber", this.proposalCostShareSequenceNumber.toString());
        }
        m.put("researchDocumentNumber", this.researchDocumentNumber);
	    return m;
    }
}

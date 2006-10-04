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

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class RoutingFormDueDateType extends BusinessObjectBase {

	private String proposalDueDateTypeCode;
	private boolean dataObjectMaintenanceCodeActiveIndicator;
	private Integer proposalApprovalLeadTime;
	private String proposalDueDateDescription;

	/**
	 * Default constructor.
	 */
	public RoutingFormDueDateType() {

	}

	/**
	 * Gets the proposalDueDateTypeCode attribute.
	 * 
	 * @return - Returns the proposalDueDateTypeCode
	 * 
	 */
	public String getProposalDueDateTypeCode() { 
		return proposalDueDateTypeCode;
	}

	/**
	 * Sets the proposalDueDateTypeCode attribute.
	 * 
	 * @param - proposalDueDateTypeCode The proposalDueDateTypeCode to set.
	 * 
	 */
	public void setProposalDueDateTypeCode(String proposalDueDateTypeCode) {
		this.proposalDueDateTypeCode = proposalDueDateTypeCode;
	}


	/**
	 * Gets the dataObjectMaintenanceCodeActiveIndicator attribute.
	 * 
	 * @return - Returns the dataObjectMaintenanceCodeActiveIndicator
	 * 
	 */
	public boolean getDataObjectMaintenanceCodeActiveIndicator() { 
		return dataObjectMaintenanceCodeActiveIndicator;
	}

	/**
	 * Sets the dataObjectMaintenanceCodeActiveIndicator attribute.
	 * 
	 * @param - dataObjectMaintenanceCodeActiveIndicator The dataObjectMaintenanceCodeActiveIndicator to set.
	 * 
	 */
	public void setDataObjectMaintenanceCodeActiveIndicator(boolean dataObjectMaintenanceCodeActiveIndicator) {
		this.dataObjectMaintenanceCodeActiveIndicator = dataObjectMaintenanceCodeActiveIndicator;
	}


	/**
	 * Gets the proposalApprovalLeadTime attribute.
	 * 
	 * @return - Returns the proposalApprovalLeadTime
	 * 
	 */
	public Integer getProposalApprovalLeadTime() { 
		return proposalApprovalLeadTime;
	}

	/**
	 * Sets the proposalApprovalLeadTime attribute.
	 * 
	 * @param - proposalApprovalLeadTime The proposalApprovalLeadTime to set.
	 * 
	 */
	public void setProposalApprovalLeadTime(Integer proposalApprovalLeadTime) {
		this.proposalApprovalLeadTime = proposalApprovalLeadTime;
	}


	/**
	 * Gets the proposalDueDateDescription attribute.
	 * 
	 * @return - Returns the proposalDueDateDescription
	 * 
	 */
	public String getProposalDueDateDescription() { 
		return proposalDueDateDescription;
	}

	/**
	 * Sets the proposalDueDateDescription attribute.
	 * 
	 * @param - proposalDueDateDescription The proposalDueDateDescription to set.
	 * 
	 */
	public void setProposalDueDateDescription(String proposalDueDateDescription) {
		this.proposalDueDateDescription = proposalDueDateDescription;
	}


	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("proposalDueDateTypeCode", this.proposalDueDateTypeCode);
	    return m;
    }
}

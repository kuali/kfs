/*
 * Copyright 2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/businessobject/RoutingFormResearchRiskStudy.java,v $
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
import org.kuali.PropertyConstants;

/**
 * 
 */
public class RoutingFormResearchRiskStudy extends BusinessObjectBase {

	private String documentNumber;
	private Integer routingFormResearchRiskStudySequenceNumber;
	private String researchRiskApprovalPendingIndicator;
	private String researchRiskExemptionNumber;
	private Date researchRiskStudyApprovalDate;
	private String researchRiskStudyNumber;
	private String researchRiskStudyName;
	private String researchRiskStudyReviewCode;
	private String researchRiskTypeCode;

    private RoutingFormResearchRisk researchRiskType;
    
	/**
	 * Default constructor.
	 */
	public RoutingFormResearchRiskStudy() {

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
	 * Gets the routingFormResearchRiskStudySequenceNumber attribute.
	 * 
	 * @return Returns the routingFormResearchRiskStudySequenceNumber
	 * 
	 */
	public Integer getRoutingFormResearchRiskStudySequenceNumber() { 
		return routingFormResearchRiskStudySequenceNumber;
	}

	/**
	 * Sets the routingFormResearchRiskStudySequenceNumber attribute.
	 * 
	 * @param routingFormResearchRiskStudySequenceNumber The routingFormResearchRiskStudySequenceNumber to set.
	 * 
	 */
	public void setRoutingFormResearchRiskStudySequenceNumber(Integer routingFormResearchRiskStudySequenceNumber) {
		this.routingFormResearchRiskStudySequenceNumber = routingFormResearchRiskStudySequenceNumber;
	}


	/**
	 * Gets the researchRiskApprovalPendingIndicator attribute.
	 * 
	 * @return Returns the researchRiskApprovalPendingIndicator
	 * 
	 */
	public String getResearchRiskApprovalPendingIndicator() { 
		return researchRiskApprovalPendingIndicator;
	}

	/**
	 * Sets the researchRiskApprovalPendingIndicator attribute.
	 * 
	 * @param researchRiskApprovalPendingIndicator The researchRiskApprovalPendingIndicator to set.
	 * 
	 */
	public void setResearchRiskApprovalPendingIndicator(String researchRiskApprovalPendingIndicator) {
		this.researchRiskApprovalPendingIndicator = researchRiskApprovalPendingIndicator;
	}


	/**
	 * Gets the researchRiskExemptionNumber attribute.
	 * 
	 * @return Returns the researchRiskExemptionNumber
	 * 
	 */
	public String getResearchRiskExemptionNumber() { 
		return researchRiskExemptionNumber;
	}

	/**
	 * Sets the researchRiskExemptionNumber attribute.
	 * 
	 * @param researchRiskExemptionNumber The researchRiskExemptionNumber to set.
	 * 
	 */
	public void setResearchRiskExemptionNumber(String researchRiskExemptionNumber) {
		this.researchRiskExemptionNumber = researchRiskExemptionNumber;
	}


	/**
	 * Gets the researchRiskStudyApprovalDate attribute.
	 * 
	 * @return Returns the researchRiskStudyApprovalDate
	 * 
	 */
	public Date getResearchRiskStudyApprovalDate() { 
		return researchRiskStudyApprovalDate;
	}

	/**
	 * Sets the researchRiskStudyApprovalDate attribute.
	 * 
	 * @param researchRiskStudyApprovalDate The researchRiskStudyApprovalDate to set.
	 * 
	 */
	public void setResearchRiskStudyApprovalDate(Date researchRiskStudyApprovalDate) {
		this.researchRiskStudyApprovalDate = researchRiskStudyApprovalDate;
	}


	/**
	 * Gets the researchRiskStudyNumber attribute.
	 * 
	 * @return Returns the researchRiskStudyNumber
	 * 
	 */
	public String getResearchRiskStudyNumber() { 
		return researchRiskStudyNumber;
	}

	/**
	 * Sets the researchRiskStudyNumber attribute.
	 * 
	 * @param researchRiskStudyNumber The researchRiskStudyNumber to set.
	 * 
	 */
	public void setResearchRiskStudyNumber(String researchRiskStudyNumber) {
		this.researchRiskStudyNumber = researchRiskStudyNumber;
	}


	/**
	 * Gets the researchRiskStudyName attribute.
	 * 
	 * @return Returns the researchRiskStudyName
	 * 
	 */
	public String getResearchRiskStudyName() { 
		return researchRiskStudyName;
	}

	/**
	 * Sets the researchRiskStudyName attribute.
	 * 
	 * @param researchRiskStudyName The researchRiskStudyName to set.
	 * 
	 */
	public void setResearchRiskStudyName(String researchRiskStudyName) {
		this.researchRiskStudyName = researchRiskStudyName;
	}


	/**
	 * Gets the researchRiskStudyReviewCode attribute.
	 * 
	 * @return Returns the researchRiskStudyReviewCode
	 * 
	 */
	public String getResearchRiskStudyReviewCode() { 
		return researchRiskStudyReviewCode;
	}

	/**
	 * Sets the researchRiskStudyReviewCode attribute.
	 * 
	 * @param researchRiskStudyReviewCode The researchRiskStudyReviewCode to set.
	 * 
	 */
	public void setResearchRiskStudyReviewCode(String researchRiskStudyReviewCode) {
		this.researchRiskStudyReviewCode = researchRiskStudyReviewCode;
	}


	/**
	 * Gets the researchRiskTypeCode attribute.
	 * 
	 * @return Returns the researchRiskTypeCode
	 * 
	 */
	public String getResearchRiskTypeCode() { 
		return researchRiskTypeCode;
	}

	/**
	 * Sets the researchRiskTypeCode attribute.
	 * 
	 * @param researchRiskTypeCode The researchRiskTypeCode to set.
	 * 
	 */
	public void setResearchRiskTypeCode(String researchRiskTypeCode) {
		this.researchRiskTypeCode = researchRiskTypeCode;
	}

    /**
     * Gets the researchRiskType attribute. 
     * @return Returns the researchRiskType.
     */
    public RoutingFormResearchRisk getResearchRiskType() {
        return researchRiskType;
    }

    /**
     * Sets the researchRiskType attribute value.
     * @param researchRiskType The researchRiskType to set.
     * @deprecated
     */
    public void setResearchRiskType(RoutingFormResearchRisk researchRiskType) {
        this.researchRiskType = researchRiskType;
    }
    
	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put(PropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        if (this.routingFormResearchRiskStudySequenceNumber != null) {
            m.put("routingFormResearchRiskStudySequenceNumber", this.routingFormResearchRiskStudySequenceNumber.toString());
        }
        m.put("researchRiskTypeCode", this.researchRiskTypeCode);
        return m;
    }


}

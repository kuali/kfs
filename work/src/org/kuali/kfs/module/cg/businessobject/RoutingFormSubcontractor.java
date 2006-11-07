/*
 * Copyright 2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/businessobject/RoutingFormSubcontractor.java,v $
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

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * 
 */
public class RoutingFormSubcontractor extends BusinessObjectBase {

	private String researchDocumentNumber;
	private Integer routingFormSubcontractorSequenceNumber;
	private BigDecimal routingFormSubcontractorAmount;
	private String routingFormSubcontractorNumber;
	private String routingFormSubcontractorName;

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
	 * Gets the routingFormSubcontractorSequenceNumber attribute.
	 * 
	 * @return - Returns the routingFormSubcontractorSequenceNumber
	 * 
	 */
	public Integer getRoutingFormSubcontractorSequenceNumber() { 
		return routingFormSubcontractorSequenceNumber;
	}

	/**
	 * Sets the routingFormSubcontractorSequenceNumber attribute.
	 * 
	 * @param - routingFormSubcontractorSequenceNumber The routingFormSubcontractorSequenceNumber to set.
	 * 
	 */
	public void setRoutingFormSubcontractorSequenceNumber(Integer routingFormSubcontractorSequenceNumber) {
		this.routingFormSubcontractorSequenceNumber = routingFormSubcontractorSequenceNumber;
	}


	/**
	 * Gets the routingFormSubcontractorAmount attribute.
	 * 
	 * @return - Returns the routingFormSubcontractorAmount
	 * 
	 */
	public BigDecimal getRoutingFormSubcontractorAmount() { 
		return routingFormSubcontractorAmount;
	}

	/**
	 * Sets the routingFormSubcontractorAmount attribute.
	 * 
	 * @param - routingFormSubcontractorAmount The routingFormSubcontractorAmount to set.
	 * 
	 */
	public void setRoutingFormSubcontractorAmount(BigDecimal routingFormSubcontractorAmount) {
		this.routingFormSubcontractorAmount = routingFormSubcontractorAmount;
	}


	/**
	 * Gets the routingFormSubcontractorNumber attribute.
	 * 
	 * @return - Returns the routingFormSubcontractorNumber
	 * 
	 */
	public String getRoutingFormSubcontractorNumber() { 
		return routingFormSubcontractorNumber;
	}

	/**
	 * Sets the routingFormSubcontractorNumber attribute.
	 * 
	 * @param - routingFormSubcontractorNumber The routingFormSubcontractorNumber to set.
	 * 
	 */
	public void setRoutingFormSubcontractorNumber(String routingFormSubcontractorNumber) {
		this.routingFormSubcontractorNumber = routingFormSubcontractorNumber;
	}


	/**
	 * Gets the routingFormSubcontractorName attribute.
	 * 
	 * @return - Returns the routingFormSubcontractorName
	 * 
	 */
	public String getRoutingFormSubcontractorName() { 
		return routingFormSubcontractorName;
	}

	/**
	 * Sets the routingFormSubcontractorName attribute.
	 * 
	 * @param - routingFormSubcontractorName The routingFormSubcontractorName to set.
	 * 
	 */
	public void setRoutingFormSubcontractorName(String routingFormSubcontractorName) {
		this.routingFormSubcontractorName = routingFormSubcontractorName;
	}


	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("researchDocumentNumber", this.researchDocumentNumber);
        if (this.routingFormSubcontractorSequenceNumber != null) {
            m.put("routingFormSubcontractorSequenceNumber", this.routingFormSubcontractorSequenceNumber.toString());
        }
	    return m;
    }
}

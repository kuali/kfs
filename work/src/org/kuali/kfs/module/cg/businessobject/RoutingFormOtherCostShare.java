/*
 * Copyright 2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/businessobject/RoutingFormOtherCostShare.java,v $
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
public class RoutingFormOtherCostShare extends BusinessObjectBase {

	private Integer routingFormCostShareSequenceNumber;
	private String researchDocumentNumber;
	private String routingFormCostShareSourceName;
	private BigDecimal routingFormCostShareAmount;

	/**
	 * Default constructor.
	 */
	public RoutingFormOtherCostShare() {

	}

	/**
	 * Gets the routingFormCostShareSequenceNumber attribute.
	 * 
	 * @return - Returns the routingFormCostShareSequenceNumber
	 * 
	 */
	public Integer getRoutingFormCostShareSequenceNumber() { 
		return routingFormCostShareSequenceNumber;
	}

	/**
	 * Sets the routingFormCostShareSequenceNumber attribute.
	 * 
	 * @param - routingFormCostShareSequenceNumber The routingFormCostShareSequenceNumber to set.
	 * 
	 */
	public void setRoutingFormCostShareSequenceNumber(Integer routingFormCostShareSequenceNumber) {
		this.routingFormCostShareSequenceNumber = routingFormCostShareSequenceNumber;
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
	 * Gets the routingFormCostShareSourceName attribute.
	 * 
	 * @return - Returns the routingFormCostShareSourceName
	 * 
	 */
	public String getRoutingFormCostShareSourceName() { 
		return routingFormCostShareSourceName;
	}

	/**
	 * Sets the routingFormCostShareSourceName attribute.
	 * 
	 * @param - routingFormCostShareSourceName The routingFormCostShareSourceName to set.
	 * 
	 */
	public void setRoutingFormCostShareSourceName(String routingFormCostShareSourceName) {
		this.routingFormCostShareSourceName = routingFormCostShareSourceName;
	}

	/**
	 * Gets the routingFormCostShareAmount attribute.
	 * 
	 * @return - Returns the routingFormCostShareAmount
	 * 
	 */
	public BigDecimal getRoutingFormCostShareAmount() { 
		return routingFormCostShareAmount;
	}

	/**
	 * Sets the routingFormCostShareAmount attribute.
	 * 
	 * @param - routingFormCostShareAmount The routingFormCostShareAmount to set.
	 * 
	 */
	public void setRoutingFormCostShareAmount(BigDecimal routingFormCostShareAmount) {
		this.routingFormCostShareAmount = routingFormCostShareAmount;
	}


	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.routingFormCostShareSequenceNumber != null) {
            m.put("routingFormCostShareSequenceNumber", this.routingFormCostShareSequenceNumber.toString());
        }
        m.put("researchDocumentNumber", this.researchDocumentNumber);
	    return m;
    }
}

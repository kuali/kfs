/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/businessobject/CreditMemoStatus.java,v $
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

package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * 
 */
public class CreditMemoStatus extends BusinessObjectBase {

	private String creditMemoStatusCode;
	private String creditMemoStatusDescription;
	private boolean dataObjectMaintenanceCodeActiveIndicator;

	/**
	 * Default constructor.
	 */
	public CreditMemoStatus() {

	}

	/**
	 * Gets the creditMemoStatusCode attribute.
	 * 
	 * @return Returns the creditMemoStatusCode
	 * 
	 */
	public String getCreditMemoStatusCode() { 
		return creditMemoStatusCode;
	}

	/**
	 * Sets the creditMemoStatusCode attribute.
	 * 
	 * @param creditMemoStatusCode The creditMemoStatusCode to set.
	 * 
	 */
	public void setCreditMemoStatusCode(String creditMemoStatusCode) {
		this.creditMemoStatusCode = creditMemoStatusCode;
	}


	/**
	 * Gets the creditMemoStatusDescription attribute.
	 * 
	 * @return Returns the creditMemoStatusDescription
	 * 
	 */
	public String getCreditMemoStatusDescription() { 
		return creditMemoStatusDescription;
	}

	/**
	 * Sets the creditMemoStatusDescription attribute.
	 * 
	 * @param creditMemoStatusDescription The creditMemoStatusDescription to set.
	 * 
	 */
	public void setCreditMemoStatusDescription(String creditMemoStatusDescription) {
		this.creditMemoStatusDescription = creditMemoStatusDescription;
	}


	/**
	 * Gets the dataObjectMaintenanceCodeActiveIndicator attribute.
	 * 
	 * @return Returns the dataObjectMaintenanceCodeActiveIndicator
	 * 
	 */
	public boolean getDataObjectMaintenanceCodeActiveIndicator() { 
		return dataObjectMaintenanceCodeActiveIndicator;
	}

	/**
	 * Sets the dataObjectMaintenanceCodeActiveIndicator attribute.
	 * 
	 * @param dataObjectMaintenanceCodeActiveIndicator The dataObjectMaintenanceCodeActiveIndicator to set.
	 * 
	 */
	public void setDataObjectMaintenanceCodeActiveIndicator(boolean dataObjectMaintenanceCodeActiveIndicator) {
		this.dataObjectMaintenanceCodeActiveIndicator = dataObjectMaintenanceCodeActiveIndicator;
	}

	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("creditMemoStatusCode", this.creditMemoStatusCode);
	    return m;
    }
}

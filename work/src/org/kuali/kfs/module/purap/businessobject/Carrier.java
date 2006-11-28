/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/businessobject/Carrier.java,v $
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
public class Carrier extends BusinessObjectBase {

	private String carrierCode;
	private String carrierDescription;
	private boolean dataObjectMaintenanceCodeActiveIndicator;

	/**
	 * Default constructor.
	 */
	public Carrier() {

	}

	/**
	 * Gets the carrierCode attribute.
	 * 
	 * @return Returns the carrierCode
	 * 
	 */
	public String getCarrierCode() { 
		return carrierCode;
	}

	/**
	 * Sets the carrierCode attribute.
	 * 
	 * @param carrierCode The carrierCode to set.
	 * 
	 */
	public void setCarrierCode(String carrierCode) {
		this.carrierCode = carrierCode;
	}


	/**
	 * Gets the carrierDescription attribute.
	 * 
	 * @return Returns the carrierDescription
	 * 
	 */
	public String getCarrierDescription() { 
		return carrierDescription;
	}

	/**
	 * Sets the carrierDescription attribute.
	 * 
	 * @param carrierDescription The carrierDescription to set.
	 * 
	 */
	public void setCarrierDescription(String carrierDescription) {
		this.carrierDescription = carrierDescription;
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
        m.put("carrierCode", this.carrierCode);
	    return m;
    }
}

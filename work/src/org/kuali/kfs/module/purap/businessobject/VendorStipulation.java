/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/businessobject/VendorStipulation.java,v $
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
public class VendorStipulation extends BusinessObjectBase {

	private Integer vendorStipulationIdentifier;
	private String vendorStipulationName;
	private String vendorStipulationDescription;
	private boolean dataObjectMaintenanceCodeActiveIndicator;

	/**
	 * Default constructor.
	 */
	public VendorStipulation() {

	}

	/**
	 * Gets the vendorStipulationIdentifier attribute.
	 * 
	 * @return Returns the vendorStipulationIdentifier
	 * 
	 */
	public Integer getVendorStipulationIdentifier() { 
		return vendorStipulationIdentifier;
	}

	/**
	 * Sets the vendorStipulationIdentifier attribute.
	 * 
	 * @param vendorStipulationIdentifier The vendorStipulationIdentifier to set.
	 * 
	 */
	public void setVendorStipulationIdentifier(Integer vendorStipulationIdentifier) {
		this.vendorStipulationIdentifier = vendorStipulationIdentifier;
	}


	/**
	 * Gets the vendorStipulationName attribute.
	 * 
	 * @return Returns the vendorStipulationName
	 * 
	 */
	public String getVendorStipulationName() { 
		return vendorStipulationName;
	}

	/**
	 * Sets the vendorStipulationName attribute.
	 * 
	 * @param vendorStipulationName The vendorStipulationName to set.
	 * 
	 */
	public void setVendorStipulationName(String vendorStipulationName) {
		this.vendorStipulationName = vendorStipulationName;
	}


	/**
	 * Gets the vendorStipulationDescription attribute.
	 * 
	 * @return Returns the vendorStipulationDescription
	 * 
	 */
	public String getVendorStipulationDescription() { 
		return vendorStipulationDescription;
	}

	/**
	 * Sets the vendorStipulationDescription attribute.
	 * 
	 * @param vendorStipulationDescription The vendorStipulationDescription to set.
	 * 
	 */
	public void setVendorStipulationDescription(String vendorStipulationDescription) {
		this.vendorStipulationDescription = vendorStipulationDescription;
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
        if (this.vendorStipulationIdentifier != null) {
            m.put("vendorStipulationIdentifier", this.vendorStipulationIdentifier.toString());
        }
        return m;
    }
}

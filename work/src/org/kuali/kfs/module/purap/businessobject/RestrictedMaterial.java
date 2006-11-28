/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/businessobject/RestrictedMaterial.java,v $
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
public class RestrictedMaterial extends BusinessObjectBase {

	private String restrictedMaterialCode;
	private String restrictedMaterialDescription;
	private String restrictedMaterialDefaultDescription;
	private String restrictedMaterialWorkgroupName;
	private boolean dataObjectMaintenanceCodeActiveIndicator;

	/**
	 * Default constructor.
	 */
	public RestrictedMaterial() {

	}

	/**
	 * Gets the restrictedMaterialCode attribute.
	 * 
	 * @return Returns the restrictedMaterialCode
	 * 
	 */
	public String getRestrictedMaterialCode() { 
		return restrictedMaterialCode;
	}

	/**
	 * Sets the restrictedMaterialCode attribute.
	 * 
	 * @param restrictedMaterialCode The restrictedMaterialCode to set.
	 * 
	 */
	public void setRestrictedMaterialCode(String restrictedMaterialCode) {
		this.restrictedMaterialCode = restrictedMaterialCode;
	}


	/**
	 * Gets the restrictedMaterialDescription attribute.
	 * 
	 * @return Returns the restrictedMaterialDescription
	 * 
	 */
	public String getRestrictedMaterialDescription() { 
		return restrictedMaterialDescription;
	}

	/**
	 * Sets the restrictedMaterialDescription attribute.
	 * 
	 * @param restrictedMaterialDescription The restrictedMaterialDescription to set.
	 * 
	 */
	public void setRestrictedMaterialDescription(String restrictedMaterialDescription) {
		this.restrictedMaterialDescription = restrictedMaterialDescription;
	}


	/**
	 * Gets the restrictedMaterialDefaultDescription attribute.
	 * 
	 * @return Returns the restrictedMaterialDefaultDescription
	 * 
	 */
	public String getRestrictedMaterialDefaultDescription() { 
		return restrictedMaterialDefaultDescription;
	}

	/**
	 * Sets the restrictedMaterialDefaultDescription attribute.
	 * 
	 * @param restrictedMaterialDefaultDescription The restrictedMaterialDefaultDescription to set.
	 * 
	 */
	public void setRestrictedMaterialDefaultDescription(String restrictedMaterialDefaultDescription) {
		this.restrictedMaterialDefaultDescription = restrictedMaterialDefaultDescription;
	}


	/**
	 * Gets the restrictedMaterialWorkgroupName attribute.
	 * 
	 * @return Returns the restrictedMaterialWorkgroupName
	 * 
	 */
	public String getRestrictedMaterialWorkgroupName() { 
		return restrictedMaterialWorkgroupName;
	}

	/**
	 * Sets the restrictedMaterialWorkgroupName attribute.
	 * 
	 * @param restrictedMaterialWorkgroupName The restrictedMaterialWorkgroupName to set.
	 * 
	 */
	public void setRestrictedMaterialWorkgroupName(String restrictedMaterialWorkgroupName) {
		this.restrictedMaterialWorkgroupName = restrictedMaterialWorkgroupName;
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
        m.put("restrictedMaterialCode", this.restrictedMaterialCode);
	    return m;
    }
}

/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class ControlAttributeType extends PersistableBusinessObjectBase {

    /**
     * Constants defining possible type codes and their associated descriptions
     */
    public static final String TYPE_CODE_A = "A";
    public static final String TYPE_CODE_S = "S";
    public static final String TYPE_CODE_D = "D";    
    public static final String TYPE_CODE_A_DESC = "All Columns";
    public static final String TYPE_CODE_S_DESC = "Some Columns";
    public static final String TYPE_CODE_D_DESC = "Description Textarea";
    
    
	private String controlAttributeTypeCode;
    private String controlAttributeTypeDescription;
	private boolean dataObjectMaintenanceCodeActiveIndicator;
	private boolean requiredIndicator;

	/**
	 * Default constructor.
	 */
	public ControlAttributeType() {

	}

	/**
	 * Gets the controlAttributeTypeCode attribute.
	 * 
	 * @return Returns the controlAttributeTypeCode
	 * 
	 */
	public String getControlAttributeTypeCode() { 
		return controlAttributeTypeCode;
	}

	/**
	 * Sets the controlAttributeTypeCode attribute.
	 * 
	 * @param controlAttributeTypeCode The controlAttributeTypeCode to set.
	 * 
	 */
	public void setControlAttributeTypeCode(String controlAttributeTypeCode) {
		this.controlAttributeTypeCode = controlAttributeTypeCode;
	}
    
	/**
     * Gets the controlAttributeTypeDescription attribute. 
     * @return Returns the controlAttributeTypeDescription.
     */
    public String getControlAttributeTypeDescription() {
        return controlAttributeTypeDescription;
    }

    /**
     * Sets the controlAttributeTypeDescription attribute value.
     * @param controlAttributeTypeDescription The controlAttributeTypeDescription to set.
     */
    public void setControlAttributeTypeDescription(String controlAttributeTypeDescription) {
        this.controlAttributeTypeDescription = controlAttributeTypeDescription;
    }

    /**
	 * Gets the dataObjectMaintenanceCodeActiveIndicator attribute.
	 * 
	 * @return Returns the dataObjectMaintenanceCodeActiveIndicator
	 * 
	 */
	public boolean isDataObjectMaintenanceCodeActiveIndicator() { 
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
	 * Gets the requiredIndicator attribute.
	 * 
	 * @return Returns the requiredIndicator
	 * 
	 */
	public boolean isRequiredIndicator() { 
		return requiredIndicator;
	}

	/**
	 * Sets the requiredIndicator attribute.
	 * 
	 * @param requiredIndicator The requiredIndicator to set.
	 * 
	 */
	public void setRequiredIndicator(boolean requiredIndicator) {
		this.requiredIndicator = requiredIndicator;
	}

	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("controlAttributeTypeCode", this.controlAttributeTypeCode);
	    return m;
    }
}

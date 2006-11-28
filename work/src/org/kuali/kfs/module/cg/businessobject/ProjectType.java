/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/businessobject/ProjectType.java,v $
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

import org.kuali.core.bo.BusinessObjectBase;

/**
 * 
 */
public class ProjectType extends BusinessObjectBase {

	private String projectTypeCode;
	private boolean dataObjectMaintenanceCodeActiveIndicator;
	private String projectTypeDescription;
	private Integer sortNumber;
    private String controlAttributeTypeCode;

    private ControlAttributeType controlAttributeType;
    
	/**
	 * Default constructor.
	 */
	public ProjectType() {

	}

	/**
	 * Gets the projectTypeCode attribute.
	 * 
	 * @return Returns the projectTypeCode
	 * 
	 */
	public String getProjectTypeCode() { 
		return projectTypeCode;
	}

	/**
	 * Sets the projectTypeCode attribute.
	 * 
	 * @param projectTypeCode The projectTypeCode to set.
	 * 
	 */
	public void setProjectTypeCode(String projectTypeCode) {
		this.projectTypeCode = projectTypeCode;
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
	 * Gets the projectTypeDescription attribute.
	 * 
	 * @return Returns the projectTypeDescription
	 * 
	 */
	public String getProjectTypeDescription() { 
		return projectTypeDescription;
	}

	/**
	 * Sets the projectTypeDescription attribute.
	 * 
	 * @param projectTypeDescription The projectTypeDescription to set.
	 * 
	 */
	public void setProjectTypeDescription(String projectTypeDescription) {
		this.projectTypeDescription = projectTypeDescription;
	}


	/**
	 * Gets the sortNumber attribute.
	 * 
	 * @return Returns the sortNumber
	 * 
	 */
	public Integer getSortNumber() { 
		return sortNumber;
	}

	/**
	 * Sets the sortNumber attribute.
	 * 
	 * @param sortNumber The sortNumber to set.
	 * 
	 */
	public void setSortNumber(Integer sortNumber) {
		this.sortNumber = sortNumber;
	}

    /**
     * Gets the controlAttributeTypeCode attribute. 
     * @return Returns the controlAttributeTypeCode.
     */
    public String getControlAttributeTypeCode() {
        return controlAttributeTypeCode;
    }

    /**
     * Sets the controlAttributeTypeCode attribute value.
     * @param controlAttributeTypeCode The controlAttributeTypeCode to set.
     */
    public void setControlAttributeTypeCode(String controlAttributeTypeCode) {
        this.controlAttributeTypeCode = controlAttributeTypeCode;
    }

    /**
     * Gets the controlAttributeType attribute. 
     * @return Returns the controlAttributeType.
     */
    public ControlAttributeType getControlAttributeType() {
        return controlAttributeType;
    }

    /**
     * Sets the controlAttributeType attribute value.
     * @param controlAttributeType The controlAttributeType to set.
     * @deprecated
     */
    public void setControlAttributeType(ControlAttributeType controlAttributeType) {
        this.controlAttributeType = controlAttributeType;
    }    
    
	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("projectTypeCode", this.projectTypeCode);
	    return m;
    }
}

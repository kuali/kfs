/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/businessobject/ResearchRiskType.java,v $
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
public class ResearchRiskType extends BusinessObjectBase {

	private String researchRiskTypeCode;
	private boolean dataObjectMaintenanceCodeActiveIndicator;
	private String researchRiskTypeDescription;
	private boolean researchRiskBiosafetyIndicator;
    private String controlAttributeTypeCode;

    private ControlAttributeType controlAttributeType;
    
	/**
	 * Default constructor.
	 */
	public ResearchRiskType() {

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
	 * Gets the researchRiskTypeDescription attribute.
	 * 
	 * @return Returns the researchRiskTypeDescription
	 * 
	 */
	public String getResearchRiskTypeDescription() { 
		return researchRiskTypeDescription;
	}

	/**
	 * Sets the researchRiskTypeDescription attribute.
	 * 
	 * @param researchRiskTypeDescription The researchRiskTypeDescription to set.
	 * 
	 */
	public void setResearchRiskTypeDescription(String researchRiskTypeDescription) {
		this.researchRiskTypeDescription = researchRiskTypeDescription;
	}


	/**
	 * Gets the researchRiskBiosafetyIndicator attribute.
	 * 
	 * @return Returns the researchRiskBiosafetyIndicator
	 * 
	 */
	public boolean isResearchRiskBiosafetyIndicator() { 
		return researchRiskBiosafetyIndicator;
	}

	/**
	 * Sets the researchRiskBiosafetyIndicator attribute.
	 * 
	 * @param researchRiskBiosafetyIndicator The researchRiskBiosafetyIndicator to set.
	 * 
	 */
	public void setResearchRiskBiosafetyIndicator(boolean researchRiskBiosafetyIndicator) {
		this.researchRiskBiosafetyIndicator = researchRiskBiosafetyIndicator;
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
        m.put("researchRiskTypeCode", this.researchRiskTypeCode);
        return m;
    }    

}

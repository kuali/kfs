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
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSPropertyConstants;

/**
 * 
 */
public class RoutingFormProjectType extends PersistableBusinessObjectBase {

	private String projectTypeCode;
	private String documentNumber;
    private boolean projectTypeSelectedIndicator;
    
    private ProjectType projectType;
    
	/**
	 * Default constructor.
	 */
	public RoutingFormProjectType() {

	}

    /**
     * Constructs a RoutingFormProjectType.
     * @param documentNumber
     * @param projectType
     */
    public RoutingFormProjectType(String documentNumber, ProjectType projectType) {
        this();
        this.documentNumber = documentNumber;
        this.projectTypeCode = projectType.getProjectTypeCode();
        this.projectType = projectType;
    }
    
    /**
     * Constructs with projectTypeCode argument.
     * @param projectTypeCode
     */
    public RoutingFormProjectType(String projectTypeCode) {
        this.projectTypeCode = projectTypeCode;
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
     * Gets the projectTypeSelectedIndicator attribute. 
     * @return Returns the projectTypeSelectedIndicator.
     */
    public boolean isProjectTypeSelectedIndicator() {
        return projectTypeSelectedIndicator;
    }

    /**
     * Sets the projectTypeSelectedIndicator attribute value.
     * @param projectTypeSelectedIndicator The projectTypeSelectedIndicator to set.
     */
    public void setProjectTypeSelectedIndicator(boolean projectTypeSelectedIndicator) {
        this.projectTypeSelectedIndicator = projectTypeSelectedIndicator;
    }
    
    /**
     * Gets the projectType attribute. 
     * @return Returns the projectType.
     */
    public ProjectType getProjectType() {
        return projectType;
    }

    /**
     * Sets the projectType attribute value.
     * @param projectType The projectType to set.
     * @deprecated
     */
    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        boolean equals = false;

        if (ObjectUtils.isNotNull(obj) && obj instanceof RoutingFormProjectType) {
            RoutingFormProjectType routingFormProjectType = (RoutingFormProjectType) obj;
            
            equals = this.projectTypeCode.equals(routingFormProjectType.getProjectTypeCode());
        }
        
        return equals;
    }
    
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("projectTypeCode", this.projectTypeCode);
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        String hashString = this.getDocumentNumber() + "|" + this.getProjectTypeCode() + "|" + this.getDocumentNumber();
        return hashString.hashCode();
    }
}

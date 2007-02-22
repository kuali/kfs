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

package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class RequisitionSource extends PersistableBusinessObjectBase {

	private String requisitionSourceCode;
	private String requisitionSourceDescription;
	private boolean active;

	/**
	 * Default constructor.
	 */
	public RequisitionSource() {

	}

	/**
	 * Gets the requisitionSourceCode attribute.
	 * 
	 * @return Returns the requisitionSourceCode
	 * 
	 */
	public String getRequisitionSourceCode() { 
		return requisitionSourceCode;
	}

	/**
	 * Sets the requisitionSourceCode attribute.
	 * 
	 * @param requisitionSourceCode The requisitionSourceCode to set.
	 * 
	 */
	public void setRequisitionSourceCode(String requisitionSourceCode) {
		this.requisitionSourceCode = requisitionSourceCode;
	}


	/**
	 * Gets the requisitionSourceDescription attribute.
	 * 
	 * @return Returns the requisitionSourceDescription
	 * 
	 */
	public String getRequisitionSourceDescription() { 
		return requisitionSourceDescription;
	}

	/**
	 * Sets the requisitionSourceDescription attribute.
	 * 
	 * @param requisitionSourceDescription The requisitionSourceDescription to set.
	 * 
	 */
	public void setRequisitionSourceDescription(String requisitionSourceDescription) {
		this.requisitionSourceDescription = requisitionSourceDescription;
	}

	/**
     * Gets the active attribute. 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("requisitionSourceCode", this.requisitionSourceCode);
	    return m;
    }
}

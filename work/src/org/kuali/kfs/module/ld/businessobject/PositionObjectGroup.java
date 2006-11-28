/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/ld/businessobject/PositionObjectGroup.java,v $
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

package org.kuali.module.labor.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * 
 */
public class PositionObjectGroup extends BusinessObjectBase {

	private String positionObjectGroupCode;
	private String positionObjectGroupName;
	private boolean rowActiveIndicator;

	/**
	 * Default constructor.
	 */
	public PositionObjectGroup() {

	}

	/**
	 * Gets the positionObjectGroupCode attribute.
	 * 
	 * @return Returns the positionObjectGroupCode
	 * 
	 */
	public String getPositionObjectGroupCode() { 
		return positionObjectGroupCode;
	}

	/**
	 * Sets the positionObjectGroupCode attribute.
	 * 
	 * @param positionObjectGroupCode The positionObjectGroupCode to set.
	 * 
	 */
	public void setPositionObjectGroupCode(String positionObjectGroupCode) {
		this.positionObjectGroupCode = positionObjectGroupCode;
	}


	/**
	 * Gets the positionObjectGroupName attribute.
	 * 
	 * @return Returns the positionObjectGroupName
	 * 
	 */
	public String getPositionObjectGroupName() { 
		return positionObjectGroupName;
	}

	/**
	 * Sets the positionObjectGroupName attribute.
	 * 
	 * @param positionObjectGroupName The positionObjectGroupName to set.
	 * 
	 */
	public void setPositionObjectGroupName(String positionObjectGroupName) {
		this.positionObjectGroupName = positionObjectGroupName;
	}


	/**
	 * Gets the rowActiveIndicator attribute.
	 * 
	 * @return Returns the rowActiveIndicator
	 * 
	 */
	public boolean isRowActiveIndicator() { 
		return rowActiveIndicator;
	}
	

	/**
	 * Sets the rowActiveIndicator attribute.
	 * 
	 * @param rowActiveIndicator The rowActiveIndicator to set.
	 * 
	 */
	public void setRowActiveIndicator(boolean rowActiveIndicator) {
		this.rowActiveIndicator = rowActiveIndicator;
	}


	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("positionObjectGroupCode", this.positionObjectGroupCode);
	    return m;
    }
}

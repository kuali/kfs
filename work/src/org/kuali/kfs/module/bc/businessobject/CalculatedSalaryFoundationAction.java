/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/bc/businessobject/CalculatedSalaryFoundationAction.java,v $
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

package org.kuali.module.budget.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * 
 */
public class CalculatedSalaryFoundationAction extends BusinessObjectBase {

	private String action;
	private String actionReason;
	private String csfFundingStatusCode;

	/**
	 * Default constructor.
	 */
	public CalculatedSalaryFoundationAction() {

	}

	/**
	 * Gets the action attribute.
	 * 
	 * @return Returns the action
	 * 
	 */
	public String getAction() { 
		return action;
	}

	/**
	 * Sets the action attribute.
	 * 
	 * @param action The action to set.
	 * 
	 */
	public void setAction(String action) {
		this.action = action;
	}


	/**
	 * Gets the actionReason attribute.
	 * 
	 * @return Returns the actionReason
	 * 
	 */
	public String getActionReason() { 
		return actionReason;
	}

	/**
	 * Sets the actionReason attribute.
	 * 
	 * @param actionReason The actionReason to set.
	 * 
	 */
	public void setActionReason(String actionReason) {
		this.actionReason = actionReason;
	}


	/**
	 * Gets the csfFundingStatusCode attribute.
	 * 
	 * @return Returns the csfFundingStatusCode
	 * 
	 */
	public String getCsfFundingStatusCode() { 
		return csfFundingStatusCode;
	}

	/**
	 * Sets the csfFundingStatusCode attribute.
	 * 
	 * @param csfFundingStatusCode The csfFundingStatusCode to set.
	 * 
	 */
	public void setCsfFundingStatusCode(String csfFundingStatusCode) {
		this.csfFundingStatusCode = csfFundingStatusCode;
	}


	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("action", this.action);
        m.put("actionReason", this.actionReason);
	    return m;
    }
}

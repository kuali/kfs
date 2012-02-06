/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class CustomerType extends PersistableBusinessObjectBase implements MutableInactivatable {

	private String customerTypeCode;
	private String customerTypeDescription;
	private boolean active;

	/**
	 * Default constructor.
	 */
	public CustomerType() {

	}

	/**
	 * Gets the customerTypeCode attribute.
	 * 
	 * @return Returns the customerTypeCode
	 * 
	 */
	public String getCustomerTypeCode() { 
		return customerTypeCode;
	}

	/**
	 * Sets the customerTypeCode attribute.
	 * 
	 * @param customerTypeCode The customerTypeCode to set.
	 * 
	 */
	public void setCustomerTypeCode(String customerTypeCode) {
		this.customerTypeCode = customerTypeCode;
	}


	/**
	 * Gets the customerTypeDescription attribute.
	 * 
	 * @return Returns the customerTypeDescription
	 * 
	 */
	public String getCustomerTypeDescription() { 
		return customerTypeDescription;
	}

	/**
	 * Sets the customerTypeDescription attribute.
	 * 
	 * @param customerTypeDescription The customerTypeDescription to set.
	 * 
	 */
	public void setCustomerTypeDescription(String customerTypeDescription) {
		this.customerTypeDescription = customerTypeDescription;
	}


	/**
	 * Gets the active attribute.
	 * 
	 * @return Returns the active
	 * 
	 */
	public boolean isActive() { 
		return active;
	}

	/**
	 * Sets the active attribute.
	 * 
	 * @param active The active to set.
	 * 
	 */
	public void setActive(boolean active) {
		this.active = active;
	}


	/**
	 * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
	 */
    @SuppressWarnings("unchecked")
	protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("customerTypeCode", this.customerTypeCode);
	    return m;
    }
}

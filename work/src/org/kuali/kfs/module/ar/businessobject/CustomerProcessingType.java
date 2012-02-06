/*
 * Copyright 2007-2009 The Kuali Foundation
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
public class CustomerProcessingType extends PersistableBusinessObjectBase implements MutableInactivatable {

	private String customerSpecialProcessingCode;
	private String customerSpecialProcessingDescription;
	private boolean active;

	/**
	 * Default constructor.
	 */
	public CustomerProcessingType() {

	}

	/**
	 * Gets the customerSpecialProcessingCode attribute.
	 * 
	 * @return Returns the customerSpecialProcessingCode
	 * 
	 */
	public String getCustomerSpecialProcessingCode() { 
		return customerSpecialProcessingCode;
	}

	/**
	 * Sets the customerSpecialProcessingCode attribute.
	 * 
	 * @param customerSpecialProcessingCode The customerSpecialProcessingCode to set.
	 * 
	 */
	public void setCustomerSpecialProcessingCode(String customerSpecialProcessingCode) {
		this.customerSpecialProcessingCode = customerSpecialProcessingCode;
	}


	/**
	 * Gets the customerSpecialProcessingDescription attribute.
	 * 
	 * @return Returns the customerSpecialProcessingDescription
	 * 
	 */
	public String getCustomerSpecialProcessingDescription() { 
		return customerSpecialProcessingDescription;
	}

	/**
	 * Sets the customerSpecialProcessingDescription attribute.
	 * 
	 * @param customerSpecialProcessingDescription The customerSpecialProcessingDescription to set.
	 * 
	 */
	public void setCustomerSpecialProcessingDescription(String customerSpecialProcessingDescription) {
		this.customerSpecialProcessingDescription = customerSpecialProcessingDescription;
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
        m.put("customerSpecialProcessingCode", this.customerSpecialProcessingCode);
	    return m;
    }
}

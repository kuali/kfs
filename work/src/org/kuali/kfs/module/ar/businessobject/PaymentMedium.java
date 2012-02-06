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
public class PaymentMedium extends PersistableBusinessObjectBase implements MutableInactivatable {

	private String customerPaymentMediumCode;
	private String customerPaymentMediumDescription;
	private boolean active;

	/**
	 * Default constructor.
	 */
	public PaymentMedium() {

	}

	/**
	 * Gets the customerPaymentMediumCode attribute.
	 * 
	 * @return Returns the customerPaymentMediumCode
	 * 
	 */
	public String getCustomerPaymentMediumCode() { 
		return customerPaymentMediumCode;
	}

	/**
	 * Sets the customerPaymentMediumCode attribute.
	 * 
	 * @param customerPaymentMediumCode The customerPaymentMediumCode to set.
	 * 
	 */
	public void setCustomerPaymentMediumCode(String customerPaymentMediumCode) {
		this.customerPaymentMediumCode = customerPaymentMediumCode;
	}


	/**
	 * Gets the customerPaymentMediumDescription attribute.
	 * 
	 * @return Returns the customerPaymentMediumDescription
	 * 
	 */
	public String getCustomerPaymentMediumDescription() { 
		return customerPaymentMediumDescription;
	}

	/**
	 * Sets the customerPaymentMediumDescription attribute.
	 * 
	 * @param customerPaymentMediumDescription The customerPaymentMediumDescription to set.
	 * 
	 */
	public void setCustomerPaymentMediumDescription(String customerPaymentMediumDescription) {
		this.customerPaymentMediumDescription = customerPaymentMediumDescription;
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
	protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("customerPaymentMediumCode", this.customerPaymentMediumCode);
	    return m;
    }
}

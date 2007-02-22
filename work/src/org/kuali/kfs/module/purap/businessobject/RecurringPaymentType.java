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
public class RecurringPaymentType extends PersistableBusinessObjectBase {

	private String recurringPaymentTypeCode;
	private String recurringPaymentTypeDescription;
    private boolean active;
    
	/**
	 * Default constructor.
	 */
	public RecurringPaymentType() {

	}

	/**
	 * Gets the recurringPaymentTypeCode attribute.
	 * 
	 * @return Returns the recurringPaymentTypeCode
	 * 
	 */
	public String getRecurringPaymentTypeCode() { 
		return recurringPaymentTypeCode;
	}

	/**
	 * Sets the recurringPaymentTypeCode attribute.
	 * 
	 * @param recurringPaymentTypeCode The recurringPaymentTypeCode to set.
	 * 
	 */
	public void setRecurringPaymentTypeCode(String recurringPaymentTypeCode) {
		this.recurringPaymentTypeCode = recurringPaymentTypeCode;
	}


	/**
	 * Gets the recurringPaymentTypeDescription attribute.
	 * 
	 * @return Returns the recurringPaymentTypeDescription
	 * 
	 */
	public String getRecurringPaymentTypeDescription() { 
		return recurringPaymentTypeDescription;
	}

	/**
	 * Sets the recurringPaymentTypeDescription attribute.
	 * 
	 * @param recurringPaymentTypeDescription The recurringPaymentTypeDescription to set.
	 * 
	 */
	public void setRecurringPaymentTypeDescription(String recurringPaymentTypeDescription) {
		this.recurringPaymentTypeDescription = recurringPaymentTypeDescription;
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
        m.put("recurringPaymentTypeCode", this.recurringPaymentTypeCode);
        return m;
    }

}

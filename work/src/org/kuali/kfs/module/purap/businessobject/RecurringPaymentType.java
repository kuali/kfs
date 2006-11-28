/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/businessobject/RecurringPaymentType.java,v $
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

import org.kuali.core.bo.BusinessObjectBase;

/**
 * 
 */
public class RecurringPaymentType extends BusinessObjectBase {

	private String recurringPaymentTypeCode;
	private String recurringPaymentTypeDescription;
    private boolean dataObjectMaintenanceCodeActiveIndicator;
    
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
     * Gets the dataObjectMaintenanceCodeActiveIndicator attribute. 
     * @return Returns the dataObjectMaintenanceCodeActiveIndicator.
     */
    public boolean isDataObjectMaintenanceCodeActiveIndicator() {
        return dataObjectMaintenanceCodeActiveIndicator;
    }

    /**
     * Sets the dataObjectMaintenanceCodeActiveIndicator attribute value.
     * @param dataObjectMaintenanceCodeActiveIndicator The dataObjectMaintenanceCodeActiveIndicator to set.
     */
    public void setDataObjectMaintenanceCodeActiveIndicator(boolean dataObjectMaintenanceCodeActiveIndicator) {
        this.dataObjectMaintenanceCodeActiveIndicator = dataObjectMaintenanceCodeActiveIndicator;
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

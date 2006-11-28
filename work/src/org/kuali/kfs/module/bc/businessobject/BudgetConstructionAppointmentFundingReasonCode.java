/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/bc/businessobject/BudgetConstructionAppointmentFundingReasonCode.java,v $
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
public class BudgetConstructionAppointmentFundingReasonCode extends BusinessObjectBase {

	private String appointmentFundingReasonCode;
	private String appointmentFundingReasonDescription;
    private boolean rowActiveIndicator;
    
    /**
	 * Default constructor.
	 */
	public BudgetConstructionAppointmentFundingReasonCode() {

	}

	/**
	 * Gets the appointmentFundingReasonCode attribute.
	 * 
	 * @return Returns the appointmentFundingReasonCode
	 * 
	 */
	public String getAppointmentFundingReasonCode() { 
		return appointmentFundingReasonCode;
	}

	/**
	 * Sets the appointmentFundingReasonCode attribute.
	 * 
	 * @param appointmentFundingReasonCode The appointmentFundingReasonCode to set.
	 * 
	 */
	public void setAppointmentFundingReasonCode(String appointmentFundingReasonCode) {
		this.appointmentFundingReasonCode = appointmentFundingReasonCode;
	}


	/**
	 * Gets the appointmentFundingReasonDescription attribute.
	 * 
	 * @return Returns the appointmentFundingReasonDescription
	 * 
	 */
	public String getAppointmentFundingReasonDescription() { 
		return appointmentFundingReasonDescription;
	}

	/**
	 * Sets the appointmentFundingReasonDescription attribute.
	 * 
	 * @param appointmentFundingReasonDescription The appointmentFundingReasonDescription to set.
	 * 
	 */
	public void setAppointmentFundingReasonDescription(String appointmentFundingReasonDescription) {
		this.appointmentFundingReasonDescription = appointmentFundingReasonDescription;
	}

    /**
     * Gets the rowActiveIndicator attribute. 
     * @return Returns the rowActiveIndicator.
     */
    public boolean isRowActiveIndicator() {
        return rowActiveIndicator;
    }

    /**
     * Sets the rowActiveIndicator attribute value.
     * @param rowActiveIndicator The rowActiveIndicator to set.
     */
    public void setRowActiveIndicator(boolean rowActiveIndicator) {
        this.rowActiveIndicator = rowActiveIndicator;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("appointmentFundingReasonCode", this.appointmentFundingReasonCode);
        return m;
    }
    

}

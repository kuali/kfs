/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/businessobject/PaymentRequestStatus.java,v $
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
public class PaymentRequestStatus extends BusinessObjectBase {

	private String paymentRequestStatusCode;
	private String paymentRequestStatusDescription;
	private boolean dataObjectMaintenanceCodeActiveIndicator;

	/**
	 * Default constructor.
	 */
	public PaymentRequestStatus() {

	}

	/**
	 * Gets the paymentRequestStatusCode attribute.
	 * 
	 * @return Returns the paymentRequestStatusCode
	 * 
	 */
	public String getPaymentRequestStatusCode() { 
		return paymentRequestStatusCode;
	}

	/**
	 * Sets the paymentRequestStatusCode attribute.
	 * 
	 * @param paymentRequestStatusCode The paymentRequestStatusCode to set.
	 * 
	 */
	public void setPaymentRequestStatusCode(String paymentRequestStatusCode) {
		this.paymentRequestStatusCode = paymentRequestStatusCode;
	}


	/**
	 * Gets the paymentRequestStatusDescription attribute.
	 * 
	 * @return Returns the paymentRequestStatusDescription
	 * 
	 */
	public String getPaymentRequestStatusDescription() { 
		return paymentRequestStatusDescription;
	}

	/**
	 * Sets the paymentRequestStatusDescription attribute.
	 * 
	 * @param paymentRequestStatusDescription The paymentRequestStatusDescription to set.
	 * 
	 */
	public void setPaymentRequestStatusDescription(String paymentRequestStatusDescription) {
		this.paymentRequestStatusDescription = paymentRequestStatusDescription;
	}


	/**
	 * Gets the dataObjectMaintenanceCodeActiveIndicator attribute.
	 * 
	 * @return Returns the dataObjectMaintenanceCodeActiveIndicator
	 * 
	 */
	public boolean getDataObjectMaintenanceCodeActiveIndicator() { 
		return dataObjectMaintenanceCodeActiveIndicator;
	}

	/**
	 * Sets the dataObjectMaintenanceCodeActiveIndicator attribute.
	 * 
	 * @param dataObjectMaintenanceCodeActiveIndicator The dataObjectMaintenanceCodeActiveIndicator to set.
	 * 
	 */
	public void setDataObjectMaintenanceCodeActiveIndicator(boolean dataObjectMaintenanceCodeActiveIndicator) {
		this.dataObjectMaintenanceCodeActiveIndicator = dataObjectMaintenanceCodeActiveIndicator;
	}

	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("paymentRequestStatusCode", this.paymentRequestStatusCode);
	    return m;
    }
}

/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/businessobject/ElectronicInvoiceRejectTypeCode.java,v $
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
public class ElectronicInvoiceRejectTypeCode extends BusinessObjectBase {

	private String invoiceRejectReasonTypeCode;
	private String invoiceRejectReasonTypeDescription;
	private boolean dataObjectMaintenanceCodeActiveIndicator;

	/**
	 * Default constructor.
	 */
	public ElectronicInvoiceRejectTypeCode() {

	}

	/**
	 * Gets the invoiceRejectReasonTypeCode attribute.
	 * 
	 * @return Returns the invoiceRejectReasonTypeCode
	 * 
	 */
	public String getInvoiceRejectReasonTypeCode() { 
		return invoiceRejectReasonTypeCode;
	}

	/**
	 * Sets the invoiceRejectReasonTypeCode attribute.
	 * 
	 * @param invoiceRejectReasonTypeCode The invoiceRejectReasonTypeCode to set.
	 * 
	 */
	public void setInvoiceRejectReasonTypeCode(String invoiceRejectReasonTypeCode) {
		this.invoiceRejectReasonTypeCode = invoiceRejectReasonTypeCode;
	}


	/**
	 * Gets the invoiceRejectReasonTypeDescription attribute.
	 * 
	 * @return Returns the invoiceRejectReasonTypeDescription
	 * 
	 */
	public String getInvoiceRejectReasonTypeDescription() { 
		return invoiceRejectReasonTypeDescription;
	}

	/**
	 * Sets the invoiceRejectReasonTypeDescription attribute.
	 * 
	 * @param invoiceRejectReasonTypeDescription The invoiceRejectReasonTypeDescription to set.
	 * 
	 */
	public void setInvoiceRejectReasonTypeDescription(String invoiceRejectReasonTypeDescription) {
		this.invoiceRejectReasonTypeDescription = invoiceRejectReasonTypeDescription;
	}


	/**
	 * Gets the dataObjectMaintenanceCodeActiveIndicator attribute.
	 * 
	 * @return Returns the dataObjectMaintenanceCodeActiveIndicator
	 * 
	 */
	public boolean isDataObjectMaintenanceCodeActiveIndicator() { 
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
        m.put("invoiceRejectReasonTypeCode", this.invoiceRejectReasonTypeCode);
	    return m;
    }
}

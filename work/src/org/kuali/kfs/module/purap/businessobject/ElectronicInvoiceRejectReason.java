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
 * Electronic Invoice Reject Reason Business Object.
 */
public class ElectronicInvoiceRejectReason extends PersistableBusinessObjectBase {

	private Integer invoiceRejectReasonIdentifier;
	private Integer invoiceHeaderInformationIdentifier;
	private String invoiceRejectReasonTypeCode;
	private String invoiceRejectReasonDescription;

    private ElectronicInvoiceHeaderInformation invoiceHeaderInformation;
	private ElectronicInvoiceRejectTypeCode invoiceRejectReasonType;

	/**
	 * Default constructor.
	 */
	public ElectronicInvoiceRejectReason() {

	}

	public ElectronicInvoiceHeaderInformation getInvoiceHeaderInformation() {
        return invoiceHeaderInformation;
    }

    /**
     * @deprecated
     */
    public void setInvoiceHeaderInformation(ElectronicInvoiceHeaderInformation invoiceHeaderInformation) {
        this.invoiceHeaderInformation = invoiceHeaderInformation;
    }

    public Integer getInvoiceHeaderInformationIdentifier() {
        return invoiceHeaderInformationIdentifier;
    }

    public void setInvoiceHeaderInformationIdentifier(Integer invoiceHeaderInformationIdentifier) {
        this.invoiceHeaderInformationIdentifier = invoiceHeaderInformationIdentifier;
    }

    public String getInvoiceRejectReasonDescription() {
        return invoiceRejectReasonDescription;
    }

    public void setInvoiceRejectReasonDescription(String invoiceRejectReasonDescription) {
        this.invoiceRejectReasonDescription = invoiceRejectReasonDescription;
    }

    public Integer getInvoiceRejectReasonIdentifier() {
        return invoiceRejectReasonIdentifier;
    }

    public void setInvoiceRejectReasonIdentifier(Integer invoiceRejectReasonIdentifier) {
        this.invoiceRejectReasonIdentifier = invoiceRejectReasonIdentifier;
    }

    public ElectronicInvoiceRejectTypeCode getInvoiceRejectReasonType() {
        return invoiceRejectReasonType;
    }

    /**
     * @deprecated
     */
    public void setInvoiceRejectReasonType(ElectronicInvoiceRejectTypeCode invoiceRejectReasonType) {
        this.invoiceRejectReasonType = invoiceRejectReasonType;
    }

    public String getInvoiceRejectReasonTypeCode() {
        return invoiceRejectReasonTypeCode;
    }

    public void setInvoiceRejectReasonTypeCode(String invoiceRejectReasonTypeCode) {
        this.invoiceRejectReasonTypeCode = invoiceRejectReasonTypeCode;
    }

    /**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.invoiceRejectReasonIdentifier != null) {
            m.put("invoiceRejectReasonIdentifier", this.invoiceRejectReasonIdentifier.toString());
        }
	    return m;
    }
}

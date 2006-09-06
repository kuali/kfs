/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ElectronicInvoiceRejectReason extends BusinessObjectBase {

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

	/**
	 * Gets the invoiceRejectReasonIdentifier attribute.
	 * 
	 * @return - Returns the invoiceRejectReasonIdentifier
	 * 
	 */
	public Integer getInvoiceRejectReasonIdentifier() { 
		return invoiceRejectReasonIdentifier;
	}

	/**
	 * Sets the invoiceRejectReasonIdentifier attribute.
	 * 
	 * @param - invoiceRejectReasonIdentifier The invoiceRejectReasonIdentifier to set.
	 * 
	 */
	public void setInvoiceRejectReasonIdentifier(Integer invoiceRejectReasonIdentifier) {
		this.invoiceRejectReasonIdentifier = invoiceRejectReasonIdentifier;
	}


	/**
	 * Gets the invoiceHeaderInformationIdentifier attribute.
	 * 
	 * @return - Returns the invoiceHeaderInformationIdentifier
	 * 
	 */
	public Integer getInvoiceHeaderInformationIdentifier() { 
		return invoiceHeaderInformationIdentifier;
	}

	/**
	 * Sets the invoiceHeaderInformationIdentifier attribute.
	 * 
	 * @param - invoiceHeaderInformationIdentifier The invoiceHeaderInformationIdentifier to set.
	 * 
	 */
	public void setInvoiceHeaderInformationIdentifier(Integer invoiceHeaderInformationIdentifier) {
		this.invoiceHeaderInformationIdentifier = invoiceHeaderInformationIdentifier;
	}


	/**
	 * Gets the invoiceRejectReasonTypeCode attribute.
	 * 
	 * @return - Returns the invoiceRejectReasonTypeCode
	 * 
	 */
	public String getInvoiceRejectReasonTypeCode() { 
		return invoiceRejectReasonTypeCode;
	}

	/**
	 * Sets the invoiceRejectReasonTypeCode attribute.
	 * 
	 * @param - invoiceRejectReasonTypeCode The invoiceRejectReasonTypeCode to set.
	 * 
	 */
	public void setInvoiceRejectReasonTypeCode(String invoiceRejectReasonTypeCode) {
		this.invoiceRejectReasonTypeCode = invoiceRejectReasonTypeCode;
	}


	/**
	 * Gets the invoiceRejectReasonDescription attribute.
	 * 
	 * @return - Returns the invoiceRejectReasonDescription
	 * 
	 */
	public String getInvoiceRejectReasonDescription() { 
		return invoiceRejectReasonDescription;
	}

	/**
	 * Sets the invoiceRejectReasonDescription attribute.
	 * 
	 * @param - invoiceRejectReasonDescription The invoiceRejectReasonDescription to set.
	 * 
	 */
	public void setInvoiceRejectReasonDescription(String invoiceRejectReasonDescription) {
		this.invoiceRejectReasonDescription = invoiceRejectReasonDescription;
	}


	/**
	 * Gets the invoiceHeaderInformation attribute.
	 * 
	 * @return - Returns the invoiceHeaderInformation
	 * 
	 */
	public ElectronicInvoiceHeaderInformation getInvoiceHeaderInformation() { 
		return invoiceHeaderInformation;
	}

	/**
	 * Sets the invoiceHeaderInformation attribute.
	 * 
	 * @param - invoiceHeaderInformation The invoiceHeaderInformation to set.
	 * @deprecated
	 */
	public void setInvoiceHeaderInformation(ElectronicInvoiceHeaderInformation invoiceHeaderInformation) {
		this.invoiceHeaderInformation = invoiceHeaderInformation;
	}

	/**
	 * Gets the invoiceRejectReasonType attribute.
	 * 
	 * @return - Returns the invoiceRejectReasonType
	 * 
	 */
	public ElectronicInvoiceRejectTypeCode getInvoiceRejectReasonType() { 
		return invoiceRejectReasonType;
	}

	/**
	 * Sets the invoiceRejectReasonType attribute.
	 * 
	 * @param - invoiceRejectReasonType The invoiceRejectReasonType to set.
	 * @deprecated
	 */
	public void setInvoiceRejectReasonType(ElectronicInvoiceRejectTypeCode invoiceRejectReasonType) {
		this.invoiceRejectReasonType = invoiceRejectReasonType;
	}

	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.invoiceRejectReasonIdentifier != null) {
            m.put("invoiceRejectReasonIdentifier", this.invoiceRejectReasonIdentifier.toString());
        }
	    return m;
    }
}

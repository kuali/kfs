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
	 * @return - Returns the paymentRequestStatusCode
	 * 
	 */
	public String getPaymentRequestStatusCode() { 
		return paymentRequestStatusCode;
	}

	/**
	 * Sets the paymentRequestStatusCode attribute.
	 * 
	 * @param - paymentRequestStatusCode The paymentRequestStatusCode to set.
	 * 
	 */
	public void setPaymentRequestStatusCode(String paymentRequestStatusCode) {
		this.paymentRequestStatusCode = paymentRequestStatusCode;
	}


	/**
	 * Gets the paymentRequestStatusDescription attribute.
	 * 
	 * @return - Returns the paymentRequestStatusDescription
	 * 
	 */
	public String getPaymentRequestStatusDescription() { 
		return paymentRequestStatusDescription;
	}

	/**
	 * Sets the paymentRequestStatusDescription attribute.
	 * 
	 * @param - paymentRequestStatusDescription The paymentRequestStatusDescription to set.
	 * 
	 */
	public void setPaymentRequestStatusDescription(String paymentRequestStatusDescription) {
		this.paymentRequestStatusDescription = paymentRequestStatusDescription;
	}


	/**
	 * Gets the dataObjectMaintenanceCodeActiveIndicator attribute.
	 * 
	 * @return - Returns the dataObjectMaintenanceCodeActiveIndicator
	 * 
	 */
	public boolean getDataObjectMaintenanceCodeActiveIndicator() { 
		return dataObjectMaintenanceCodeActiveIndicator;
	}

	/**
	 * Sets the dataObjectMaintenanceCodeActiveIndicator attribute.
	 * 
	 * @param - dataObjectMaintenanceCodeActiveIndicator The dataObjectMaintenanceCodeActiveIndicator to set.
	 * 
	 */
	public void setDataObjectMaintenanceCodeActiveIndicator(boolean dataObjectMaintenanceCodeActiveIndicator) {
		this.dataObjectMaintenanceCodeActiveIndicator = dataObjectMaintenanceCodeActiveIndicator;
	}

	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("paymentRequestStatusCode", this.paymentRequestStatusCode);
	    return m;
    }
}

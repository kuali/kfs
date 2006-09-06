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

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class PurchaseOrderVendorStipulation extends BusinessObjectBase {

	private Integer purchaseOrderVendorStipulationIdentifier;
	private Integer purchaseOrderIdentifier;
	private String vendorStipulationDescription;
	private String vendorStipulationAuthorEmployeeIdentifier;
	private Date vendorStipulationCreateDate;

    private PurchaseOrder purchaseOrder;

	/**
	 * Default constructor.
	 */
	public PurchaseOrderVendorStipulation() {

	}

	/**
	 * Gets the purchaseOrderVendorStipulationIdentifier attribute.
	 * 
	 * @return - Returns the purchaseOrderVendorStipulationIdentifier
	 * 
	 */
	public Integer getPurchaseOrderVendorStipulationIdentifier() { 
		return purchaseOrderVendorStipulationIdentifier;
	}

	/**
	 * Sets the purchaseOrderVendorStipulationIdentifier attribute.
	 * 
	 * @param - purchaseOrderVendorStipulationIdentifier The purchaseOrderVendorStipulationIdentifier to set.
	 * 
	 */
	public void setPurchaseOrderVendorStipulationIdentifier(Integer purchaseOrderVendorStipulationIdentifier) {
		this.purchaseOrderVendorStipulationIdentifier = purchaseOrderVendorStipulationIdentifier;
	}


	/**
	 * Gets the purchaseOrderIdentifier attribute.
	 * 
	 * @return - Returns the purchaseOrderIdentifier
	 * 
	 */
	public Integer getPurchaseOrderIdentifier() { 
		return purchaseOrderIdentifier;
	}

	/**
	 * Sets the purchaseOrderIdentifier attribute.
	 * 
	 * @param - purchaseOrderIdentifier The purchaseOrderIdentifier to set.
	 * 
	 */
	public void setPurchaseOrderIdentifier(Integer purchaseOrderIdentifier) {
		this.purchaseOrderIdentifier = purchaseOrderIdentifier;
	}


	/**
	 * Gets the vendorStipulationDescription attribute.
	 * 
	 * @return - Returns the vendorStipulationDescription
	 * 
	 */
	public String getVendorStipulationDescription() { 
		return vendorStipulationDescription;
	}

	/**
	 * Sets the vendorStipulationDescription attribute.
	 * 
	 * @param - vendorStipulationDescription The vendorStipulationDescription to set.
	 * 
	 */
	public void setVendorStipulationDescription(String vendorStipulationDescription) {
		this.vendorStipulationDescription = vendorStipulationDescription;
	}


	/**
	 * Gets the vendorStipulationAuthorEmployeeIdentifier attribute.
	 * 
	 * @return - Returns the vendorStipulationAuthorEmployeeIdentifier
	 * 
	 */
	public String getVendorStipulationAuthorEmployeeIdentifier() { 
		return vendorStipulationAuthorEmployeeIdentifier;
	}

	/**
	 * Sets the vendorStipulationAuthorEmployeeIdentifier attribute.
	 * 
	 * @param - vendorStipulationAuthorEmployeeIdentifier The vendorStipulationAuthorEmployeeIdentifier to set.
	 * 
	 */
	public void setVendorStipulationAuthorEmployeeIdentifier(String vendorStipulationAuthorEmployeeIdentifier) {
		this.vendorStipulationAuthorEmployeeIdentifier = vendorStipulationAuthorEmployeeIdentifier;
	}


	/**
	 * Gets the vendorStipulationCreateDate attribute.
	 * 
	 * @return - Returns the vendorStipulationCreateDate
	 * 
	 */
	public Date getVendorStipulationCreateDate() { 
		return vendorStipulationCreateDate;
	}

	/**
	 * Sets the vendorStipulationCreateDate attribute.
	 * 
	 * @param - vendorStipulationCreateDate The vendorStipulationCreateDate to set.
	 * 
	 */
	public void setVendorStipulationCreateDate(Date vendorStipulationCreateDate) {
		this.vendorStipulationCreateDate = vendorStipulationCreateDate;
	}


	/**
	 * Gets the purchaseOrder attribute.
	 * 
	 * @return - Returns the purchaseOrder
	 * 
	 */
	public PurchaseOrder getPurchaseOrder() { 
		return purchaseOrder;
	}

	/**
	 * Sets the purchaseOrder attribute.
	 * 
	 * @param - purchaseOrder The purchaseOrder to set.
	 * @deprecated
	 */
	public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
	}

	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.purchaseOrderVendorStipulationIdentifier != null) {
            m.put("purchaseOrderVendorStipulationIdentifier", this.purchaseOrderVendorStipulationIdentifier.toString());
        }
	    return m;
    }
}

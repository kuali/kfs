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
import org.kuali.core.util.SpringServiceLocator;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class PurchaseOrderContractLanguage extends BusinessObjectBase {

	private Integer purchaseOrderContractLanguageIdentifier;
	private String campusCode;
	private String purchaseOrderContractLanguageDescription;
	private Date contractLanguageCreateDate;
	private boolean dataObjectMaintenanceCodeActiveIndicator;

	/**
	 * Default constructor.
	 */
	public PurchaseOrderContractLanguage() {
        this.setContractLanguageCreateDate(SpringServiceLocator.getDateTimeService().getCurrentSqlDate());
	}

	/**
	 * Gets the purchaseOrderContractLanguageIdentifier attribute.
	 * 
	 * @return - Returns the purchaseOrderContractLanguageIdentifier
	 * 
	 */
	public Integer getPurchaseOrderContractLanguageIdentifier() { 
		return purchaseOrderContractLanguageIdentifier;
	}

	/**
	 * Sets the purchaseOrderContractLanguageIdentifier attribute.
	 * 
	 * @param - purchaseOrderContractLanguageIdentifier The purchaseOrderContractLanguageIdentifier to set.
	 * 
	 */
	public void setPurchaseOrderContractLanguageIdentifier(Integer purchaseOrderContractLanguageIdentifier) {
		this.purchaseOrderContractLanguageIdentifier = purchaseOrderContractLanguageIdentifier;
	}


	/**
	 * Gets the campusCode attribute.
	 * 
	 * @return - Returns the campusCode
	 * 
	 */
	public String getCampusCode() { 
		return campusCode;
	}

	/**
	 * Sets the campusCode attribute.
	 * 
	 * @param - campusCode The campusCode to set.
	 * 
	 */
	public void setCampusCode(String campusCode) {
		this.campusCode = campusCode;
	}


	/**
	 * Gets the purchaseOrderContractLanguageDescription attribute.
	 * 
	 * @return - Returns the purchaseOrderContractLanguageDescription
	 * 
	 */
	public String getPurchaseOrderContractLanguageDescription() { 
		return purchaseOrderContractLanguageDescription;
	}

	/**
	 * Sets the purchaseOrderContractLanguageDescription attribute.
	 * 
	 * @param - purchaseOrderContractLanguageDescription The purchaseOrderContractLanguageDescription to set.
	 * 
	 */
	public void setPurchaseOrderContractLanguageDescription(String purchaseOrderContractLanguageDescription) {
		this.purchaseOrderContractLanguageDescription = purchaseOrderContractLanguageDescription;
	}


	/**
	 * Gets the contractLanguageCreateDate attribute.
	 * 
	 * @return - Returns the contractLanguageCreateDate
	 * 
	 */
	public Date getContractLanguageCreateDate() { 
		return contractLanguageCreateDate;
	}

	/**
	 * Sets the contractLanguageCreateDate attribute.
	 * 
	 * @param - contractLanguageCreateDate The contractLanguageCreateDate to set.
	 * 
	 */
	public void setContractLanguageCreateDate(Date contractLanguageCreateDate) {
		this.contractLanguageCreateDate = contractLanguageCreateDate;
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
        if (this.purchaseOrderContractLanguageIdentifier != null) {
            m.put("purchaseOrderContractLanguageIdentifier", this.purchaseOrderContractLanguageIdentifier.toString());
        }
	    return m;
    }
}

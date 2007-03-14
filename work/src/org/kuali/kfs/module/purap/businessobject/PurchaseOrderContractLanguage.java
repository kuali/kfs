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

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.util.SpringServiceLocator;

/**
 * 
 */
public class PurchaseOrderContractLanguage extends PersistableBusinessObjectBase {

	private Integer purchaseOrderContractLanguageIdentifier;
	private String campusCode;
	private String purchaseOrderContractLanguageDescription;
	private Date contractLanguageCreateDate;
	private boolean active;

	/**
	 * Default constructor.
	 */
	public PurchaseOrderContractLanguage() {
        this.setContractLanguageCreateDate(SpringServiceLocator.getDateTimeService().getCurrentSqlDate());
	}

	/**
	 * Gets the purchaseOrderContractLanguageIdentifier attribute.
	 * 
	 * @return Returns the purchaseOrderContractLanguageIdentifier
	 * 
	 */
	public Integer getPurchaseOrderContractLanguageIdentifier() { 
		return purchaseOrderContractLanguageIdentifier;
	}

	/**
	 * Sets the purchaseOrderContractLanguageIdentifier attribute.
	 * 
	 * @param purchaseOrderContractLanguageIdentifier The purchaseOrderContractLanguageIdentifier to set.
	 * 
	 */
	public void setPurchaseOrderContractLanguageIdentifier(Integer purchaseOrderContractLanguageIdentifier) {
		this.purchaseOrderContractLanguageIdentifier = purchaseOrderContractLanguageIdentifier;
	}


	/**
	 * Gets the campusCode attribute.
	 * 
	 * @return Returns the campusCode
	 * 
	 */
	public String getCampusCode() { 
		return campusCode;
	}

	/**
	 * Sets the campusCode attribute.
	 * 
	 * @param campusCode The campusCode to set.
	 * 
	 */
	public void setCampusCode(String campusCode) {
		this.campusCode = campusCode;
	}


	/**
	 * Gets the purchaseOrderContractLanguageDescription attribute.
	 * 
	 * @return Returns the purchaseOrderContractLanguageDescription
	 * 
	 */
	public String getPurchaseOrderContractLanguageDescription() { 
		return purchaseOrderContractLanguageDescription;
	}

	/**
	 * Sets the purchaseOrderContractLanguageDescription attribute.
	 * 
	 * @param purchaseOrderContractLanguageDescription The purchaseOrderContractLanguageDescription to set.
	 * 
	 */
	public void setPurchaseOrderContractLanguageDescription(String purchaseOrderContractLanguageDescription) {
		this.purchaseOrderContractLanguageDescription = purchaseOrderContractLanguageDescription;
	}


	/**
	 * Gets the contractLanguageCreateDate attribute.
	 * 
	 * @return Returns the contractLanguageCreateDate
	 * 
	 */
	public Date getContractLanguageCreateDate() { 
		return contractLanguageCreateDate;
	}

	/**
	 * Sets the contractLanguageCreateDate attribute.
	 * 
	 * @param contractLanguageCreateDate The contractLanguageCreateDate to set.
	 * 
	 */
	public void setContractLanguageCreateDate(Date contractLanguageCreateDate) {
		this.contractLanguageCreateDate = contractLanguageCreateDate;
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
        if (this.purchaseOrderContractLanguageIdentifier != null) {
            m.put("purchaseOrderContractLanguageIdentifier", this.purchaseOrderContractLanguageIdentifier.toString());
        }
	    return m;
    }
}

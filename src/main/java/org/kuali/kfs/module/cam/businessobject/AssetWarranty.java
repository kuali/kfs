/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.cam.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AssetWarranty extends PersistableBusinessObjectBase implements MutableInactivatable {

	private Long capitalAssetNumber;
	private String warrantyContactName;
	private String warrantyPhoneNumber;
	private Date warrantyBeginningDate;
	private Date warrantyEndingDate;
	private String warrantyNumber;
	private String warrantyPurchaseOrderNumber;
	private String warrantyText;
	private boolean active;

    private Asset asset;

	/**
	 * Default constructor.
	 */
	public AssetWarranty() {

	}

	/**
	 * Gets the capitalAssetNumber attribute.
	 *
	 * @return Returns the capitalAssetNumber
	 *
	 */
	public Long getCapitalAssetNumber() {
		return capitalAssetNumber;
	}

	/**
	 * Sets the capitalAssetNumber attribute.
	 *
	 * @param capitalAssetNumber The capitalAssetNumber to set.
	 *
	 */
	public void setCapitalAssetNumber(Long capitalAssetNumber) {
		this.capitalAssetNumber = capitalAssetNumber;
	}


	/**
	 * Gets the warrantyContactName attribute.
	 *
	 * @return Returns the warrantyContactName
	 *
	 */
	public String getWarrantyContactName() {
		return warrantyContactName;
	}

	/**
	 * Sets the warrantyContactName attribute.
	 *
	 * @param warrantyContactName The warrantyContactName to set.
	 *
	 */
	public void setWarrantyContactName(String warrantyContactName) {
		this.warrantyContactName = warrantyContactName;
	}


	/**
	 * Gets the warrantyPhoneNumber attribute.
	 *
	 * @return Returns the warrantyPhoneNumber
	 *
	 */
	public String getWarrantyPhoneNumber() {
		return warrantyPhoneNumber;
	}

	/**
	 * Sets the warrantyPhoneNumber attribute.
	 *
	 * @param warrantyPhoneNumber The warrantyPhoneNumber to set.
	 *
	 */
	public void setWarrantyPhoneNumber(String warrantyPhoneNumber) {
		this.warrantyPhoneNumber = warrantyPhoneNumber;
	}


	/**
	 * Gets the warrantyBeginningDate attribute.
	 *
	 * @return Returns the warrantyBeginningDate
	 *
	 */
	public Date getWarrantyBeginningDate() {
		return warrantyBeginningDate;
	}

	/**
	 * Sets the warrantyBeginningDate attribute.
	 *
	 * @param warrantyBeginningDate The warrantyBeginningDate to set.
	 *
	 */
	public void setWarrantyBeginningDate(Date warrantyBeginningDate) {
		this.warrantyBeginningDate = warrantyBeginningDate;
	}


	/**
	 * Gets the warrantyEndingDate attribute.
	 *
	 * @return Returns the warrantyEndingDate
	 *
	 */
	public Date getWarrantyEndingDate() {
		return warrantyEndingDate;
	}

	/**
	 * Sets the warrantyEndingDate attribute.
	 *
	 * @param warrantyEndingDate The warrantyEndingDate to set.
	 *
	 */
	public void setWarrantyEndingDate(Date warrantyEndingDate) {
		this.warrantyEndingDate = warrantyEndingDate;
	}


	/**
	 * Gets the warrantyNumber attribute.
	 *
	 * @return Returns the warrantyNumber
	 *
	 */
	public String getWarrantyNumber() {
		return warrantyNumber;
	}

	/**
	 * Sets the warrantyNumber attribute.
	 *
	 * @param warrantyNumber The warrantyNumber to set.
	 *
	 */
	public void setWarrantyNumber(String warrantyNumber) {
		this.warrantyNumber = warrantyNumber;
	}


	/**
	 * Gets the warrantyPurchaseOrderNumber attribute.
	 *
	 * @return Returns the warrantyPurchaseOrderNumber
	 *
	 */
	public String getWarrantyPurchaseOrderNumber() {
		return warrantyPurchaseOrderNumber;
	}

	/**
	 * Sets the warrantyPurchaseOrderNumber attribute.
	 *
	 * @param warrantyPurchaseOrderNumber The warrantyPurchaseOrderNumber to set.
	 *
	 */
	public void setWarrantyPurchaseOrderNumber(String warrantyPurchaseOrderNumber) {
		this.warrantyPurchaseOrderNumber = warrantyPurchaseOrderNumber;
	}


	/**
	 * Gets the warrantyText attribute.
	 *
	 * @return Returns the warrantyText
	 *
	 */
	public String getWarrantyText() {
		return warrantyText;
	}

	/**
	 * Sets the warrantyText attribute.
	 *
	 * @param warrantyText The warrantyText to set.
	 *
	 */
	public void setWarrantyText(String warrantyText) {
		this.warrantyText = warrantyText;
	}


	/**
	 * Gets the asset attribute.
	 *
	 * @return Returns the asset
	 *
	 */
	public Asset getAsset() {
		return asset;
	}

	/**
	 * Sets the asset attribute.
	 *
	 * @param asset The asset to set.
	 * @deprecated
	 */
	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	 /**
     * Gets the active attribute.
     *
     * @return Returns the active
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute.
     *
     * @param active The active to set.
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

	/**
	 * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
	    LinkedHashMap m = new LinkedHashMap();
        if (this.capitalAssetNumber != null) {
            m.put("capitalAssetNumber", this.capitalAssetNumber.toString());
        }
	    return m;
    }
}

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
 * 
 */
public class ItemType extends PersistableBusinessObjectBase {

	private String itemTypeCode;
	private String itemTypeDescription;
    private boolean quantityBasedGeneralLedgerIndicator;
    private boolean itemTypeAboveTheLineIndicator;
	private boolean active;
    
	/**
	 * Default constructor.
	 */
	public ItemType() {

	}

	/**
	 * Gets the itemTypeCode attribute.
	 * 
	 * @return Returns the itemTypeCode
	 * 
	 */
	public String getItemTypeCode() { 
		return itemTypeCode;
	}

	/**
	 * Sets the itemTypeCode attribute.
	 * 
	 * @param itemTypeCode The itemTypeCode to set.
	 * 
	 */
	public void setItemTypeCode(String itemTypeCode) {
		this.itemTypeCode = itemTypeCode;
	}

	/**
	 * Gets the itemTypeDescription attribute.
	 * 
	 * @return Returns the itemTypeDescription
	 * 
	 */
	public String getItemTypeDescription() { 
		return itemTypeDescription;
	}

	/**
	 * Sets the itemTypeDescription attribute.
	 * 
	 * @param itemTypeDescription The itemTypeDescription to set.
	 * 
	 */
	public void setItemTypeDescription(String itemTypeDescription) {
		this.itemTypeDescription = itemTypeDescription;
	}

    /**
     * Gets the negative value of the quantityBasedGeneralLedgerIndicator attribute which
     * indicates the item is an amount based item.
     *  
     * @return Returns the negative of the quantityBasedGeneralLedgerIndicator.
     */
    public boolean isAmountBasedGeneralLedgerIndicator() {
        return !quantityBasedGeneralLedgerIndicator;
    }

    /**
     * Gets the quantityBasedGeneralLedgerIndicator attribute. 
     * @return Returns the quantityBasedGeneralLedgerIndicator.
     */
    public boolean isQuantityBasedGeneralLedgerIndicator() {
        return quantityBasedGeneralLedgerIndicator;
    }

    /**
     * Sets the quantityBasedGeneralLedgerIndicator attribute value.
     * @param quantityBasedGeneralLedgerIndicator The quantityBasedGeneralLedgerIndicator to set.
     */
    public void setQuantityBasedGeneralLedgerIndicator(boolean quantityBasedGeneralLedgerIndicator) {
        this.quantityBasedGeneralLedgerIndicator = quantityBasedGeneralLedgerIndicator;
    }

    /**
     * Gets the itemTypeAboveTheLineIndicator attribute. 
     * @return Returns the itemTypeAboveTheLineIndicator.
     */
    public boolean isItemTypeAboveTheLineIndicator() {
        return itemTypeAboveTheLineIndicator;
    }

    /**
     * Sets the itemTypeAboveTheLineIndicator attribute value.
     * @param itemTypeAboveTheLineIndicator The itemTypeAboveTheLineIndicator to set.
     */
    public void setItemTypeAboveTheLineIndicator(boolean itemTypeAboveTheLineIndicator) {
        this.itemTypeAboveTheLineIndicator = itemTypeAboveTheLineIndicator;
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
        m.put("itemTypeCode", this.itemTypeCode);
        return m;
    }

}

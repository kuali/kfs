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
 * Item Type Business Object. Defines various types of items.
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

    public String getItemTypeCode() {
        return itemTypeCode;
    }

    public void setItemTypeCode(String itemTypeCode) {
        this.itemTypeCode = itemTypeCode;
    }

    public String getItemTypeDescription() {
        return itemTypeDescription;
    }

    public void setItemTypeDescription(String itemTypeDescription) {
        this.itemTypeDescription = itemTypeDescription;
    }

    public boolean isQuantityBasedGeneralLedgerIndicator() {
        return quantityBasedGeneralLedgerIndicator;
    }

    public void setQuantityBasedGeneralLedgerIndicator(boolean quantityBasedGeneralLedgerIndicator) {
        this.quantityBasedGeneralLedgerIndicator = quantityBasedGeneralLedgerIndicator;
    }

    public boolean isItemTypeBelowTheLineIndicator() {
        return !itemTypeAboveTheLineIndicator;
    }

    public boolean isItemTypeAboveTheLineIndicator() {
        return itemTypeAboveTheLineIndicator;
    }

    public void setItemTypeAboveTheLineIndicator(boolean itemTypeAboveTheLineIndicator) {
        this.itemTypeAboveTheLineIndicator = itemTypeAboveTheLineIndicator;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return Returns the opposite of quantityBasedGeneralLedgerIndicator.
     */
    public boolean isAmountBasedGeneralLedgerIndicator() {
        return !quantityBasedGeneralLedgerIndicator;
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

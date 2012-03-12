/*
 * Copyright 2006 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.module.purap.PurapConstants.ItemTypeCodes;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Item Type Business Object. Defines various types of items.
 */
public class ItemType extends PersistableBusinessObjectBase implements MutableInactivatable{

    private String itemTypeCode;
    private String itemTypeDescription;
    private boolean quantityBasedGeneralLedgerIndicator;
    private boolean additionalChargeIndicator;
    private boolean active;
    private boolean taxableIndicator;

    /**
     * Default constructor.
     */
    public ItemType() {

    }
    
    public boolean isTaxableIndicator() {
        return taxableIndicator;
    }

    public void setTaxableIndicator(boolean taxableIndicator) {
        this.taxableIndicator = taxableIndicator;
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

    /**
     * @return Returns the opposite of quantityBasedGeneralLedgerIndicator.
     */
    public boolean isAmountBasedGeneralLedgerIndicator() {
        return !quantityBasedGeneralLedgerIndicator;
    }

    public boolean isQuantityBasedGeneralLedgerIndicator() {
        return quantityBasedGeneralLedgerIndicator;
    }

    public void setQuantityBasedGeneralLedgerIndicator(boolean quantityBasedGeneralLedgerIndicator) {
        this.quantityBasedGeneralLedgerIndicator = quantityBasedGeneralLedgerIndicator;
    }

    public boolean isLineItemIndicator() {
        return !additionalChargeIndicator;
    }

    public boolean isAdditionalChargeIndicator() {
        return additionalChargeIndicator;
    }

    public void setAdditionalChargeIndicator(boolean additionalChargeIndicator) {
        this.additionalChargeIndicator = additionalChargeIndicator;
    }
    
    public boolean getIsTaxCharge() {
        boolean isTax = itemTypeCode.equals(ItemTypeCodes.ITEM_TYPE_FEDERAL_TAX_CODE);
        isTax |= itemTypeCode.equals(ItemTypeCodes.ITEM_TYPE_FEDERAL_GROSS_CODE); 
        isTax |= itemTypeCode.equals(ItemTypeCodes.ITEM_TYPE_STATE_TAX_CODE); 
        isTax |= itemTypeCode.equals(ItemTypeCodes.ITEM_TYPE_STATE_GROSS_CODE); 
        return isTax;
    }

    public static boolean getIsTaxCharge(String itemTypeCode) {
        boolean isTax = itemTypeCode.equals(ItemTypeCodes.ITEM_TYPE_FEDERAL_TAX_CODE);
        isTax |= itemTypeCode.equals(ItemTypeCodes.ITEM_TYPE_FEDERAL_GROSS_CODE); 
        isTax |= itemTypeCode.equals(ItemTypeCodes.ITEM_TYPE_STATE_TAX_CODE); 
        isTax |= itemTypeCode.equals(ItemTypeCodes.ITEM_TYPE_STATE_GROSS_CODE); 
        return isTax;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("itemTypeCode", this.itemTypeCode);
        return m;
    }
}

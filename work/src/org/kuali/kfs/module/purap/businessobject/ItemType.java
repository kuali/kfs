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

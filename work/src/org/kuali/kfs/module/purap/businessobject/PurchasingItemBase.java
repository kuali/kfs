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

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.sys.businessobject.UnitOfMeasure;
import org.kuali.kfs.vnd.businessobject.CommodityCode;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Purchasing Item Base Business Object.
 */
public abstract class PurchasingItemBase extends PurApItemBase implements PurchasingItem {
    
    private String purchasingCommodityCode;
    
    private CommodityCode commodityCode;
    
    private UnitOfMeasure itemUnitOfMeasure;
    
    /**
     * @see org.kuali.kfs.module.purap.businessobject.PurApItem#isConsideredEntered()
     */
    public boolean isConsideredEntered() {
        if (this instanceof PurchaseOrderItem) {
            // if item is PO item... only validate active items
            PurchaseOrderItem poi = (PurchaseOrderItem) this;
            if (!poi.isItemActiveIndicator()) {
                return false;
            }
        }
        if (getItemType().isAdditionalChargeIndicator()) {
            if ((ObjectUtils.isNull(getItemUnitPrice())) && (StringUtils.isBlank(getItemDescription())) && (getSourceAccountingLines().isEmpty())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if the Purchasing Item is empty.
     * 
     * @return boolean - true if item is empty, false if conditions show its not empty.
     */
    public boolean isEmpty() {
        return !(StringUtils.isNotEmpty(getItemUnitOfMeasureCode()) || StringUtils.isNotEmpty(getItemCatalogNumber()) || StringUtils.isNotEmpty(getItemDescription()) || StringUtils.isNotEmpty(getItemAuxiliaryPartIdentifier()) || ObjectUtils.isNotNull(getItemQuantity()) || (ObjectUtils.isNotNull(getItemUnitPrice()) && (getItemUnitPrice().compareTo(BigDecimal.ZERO) != 0)) || (!this.isAccountListEmpty()));
    }

    /**
     * Determines if the Purchasing Item Detail is empty.
     * 
     * @return boolean - true if item is empty, false if conditions show its not empty.
     */
    public boolean isItemDetailEmpty() {
        boolean empty = true;
        empty &= ObjectUtils.isNull(getItemQuantity()) || StringUtils.isEmpty(getItemQuantity().toString());
        empty &= StringUtils.isEmpty(getItemUnitOfMeasureCode());
        empty &= StringUtils.isEmpty(getItemCatalogNumber());
        empty &= StringUtils.isEmpty(getItemDescription());
        empty &= ObjectUtils.isNull(getItemUnitPrice()) || (getItemUnitPrice().compareTo(BigDecimal.ZERO) == 0);
        return empty;
    }

    public CommodityCode getCommodityCode() {
        if (ObjectUtils.isNull(commodityCode) || !StringUtils.equalsIgnoreCase( commodityCode.getPurchasingCommodityCode(), getPurchasingCommodityCode()) )  {
            refreshReferenceObject(PurapPropertyConstants.COMMODITY_CODE);
        }
        return commodityCode;
    }

    public void setCommodityCode(CommodityCode commodityCode) {
        this.commodityCode = commodityCode;
    }

    public String getPurchasingCommodityCode() {
        return purchasingCommodityCode;
    }

    public void setPurchasingCommodityCode(String purchasingCommodityCode) {
        this.purchasingCommodityCode = (StringUtils.isNotBlank(purchasingCommodityCode) ? purchasingCommodityCode.toUpperCase() : purchasingCommodityCode);
    }
    
    public PurchasingCapitalAssetItem getPurchasingCapitalAssetItem(){
        PurchasingDocument pd = (PurchasingDocument)this.getPurapDocument();
        if (this.getItemIdentifier() != null) {
            return pd.getPurchasingCapitalAssetItem(this.getItemIdentifier());
        }
        else {
            return null;
        }
    }

    public UnitOfMeasure getItemUnitOfMeasure() {
    	if (ObjectUtils.isNull(itemUnitOfMeasure) || !StringUtils.equalsIgnoreCase( itemUnitOfMeasure.getItemUnitOfMeasureCode(), getItemUnitOfMeasureCode()) ) {
            refreshReferenceObject(PurapPropertyConstants.ITEM_UNIT_OF_MEASURE);
        }
        return itemUnitOfMeasure;
    }

    public void setItemUnitOfMeasure(UnitOfMeasure itemUnitOfMeasure) {
        this.itemUnitOfMeasure = itemUnitOfMeasure;
    }

    public boolean isNewItemForAmendment() {
        return false;
    }
}

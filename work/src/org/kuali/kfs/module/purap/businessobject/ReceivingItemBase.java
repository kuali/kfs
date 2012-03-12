/*
 * Copyright 2008 The Kuali Foundation
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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.sys.businessobject.UnitOfMeasure;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class...
 */
public abstract class ReceivingItemBase extends PersistableBusinessObjectBase implements PurapEnterableItem, ReceivingItem {

    private Integer receivingItemIdentifier;
    private String documentNumber;
    private Integer purchaseOrderIdentifier;
    private Integer itemLineNumber;
    private String itemTypeCode;
    private String itemUnitOfMeasureCode;
    private String itemCatalogNumber;
    private String itemDescription;
    private KualiDecimal itemReceivedTotalQuantity;
    private KualiDecimal itemReturnedTotalQuantity;
    private KualiDecimal itemDamagedTotalQuantity;
    private String itemReasonAddedCode;
    protected KualiDecimal itemOriginalReceivedTotalQuantity;
    protected KualiDecimal itemOriginalReturnedTotalQuantity;
    protected KualiDecimal itemOriginalDamagedTotalQuantity;
    
    private ItemReasonAdded itemReasonAdded;
    private ItemType itemType;
    private UnitOfMeasure itemUnitOfMeasure;
    
    
    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();      
        if (this.receivingItemIdentifier != null) {
            m.put("receivingItemIdentifier", this.receivingItemIdentifier.toString());
        }
        return m;
    }

    public boolean isConsideredEntered() {
        //if all are not null then return true
        return !((ObjectUtils.isNull(this.getItemReceivedTotalQuantity()) || this.getItemReceivedTotalQuantity().isZero()) &&
                (ObjectUtils.isNull(this.getItemDamagedTotalQuantity()) || this.getItemDamagedTotalQuantity().isZero()) &&
                (ObjectUtils.isNull(this.getItemReturnedTotalQuantity()) || this.getItemReturnedTotalQuantity().isZero()));
    }

    public Integer getReceivingItemIdentifier() { 
    	return receivingItemIdentifier;
    }

    public void setReceivingItemIdentifier(Integer receivingItemIdentifier) {
    	this.receivingItemIdentifier = receivingItemIdentifier;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Integer getPurchaseOrderIdentifier() {
        return purchaseOrderIdentifier;
    }

    public void setPurchaseOrderIdentifier(Integer purchaseOrderIdentifier) {
        this.purchaseOrderIdentifier = purchaseOrderIdentifier;
    }

    public Integer getItemLineNumber() {
        return itemLineNumber;
    }

    public void setItemLineNumber(Integer itemLineNumber) {
        this.itemLineNumber = itemLineNumber;
    }

    public String getItemTypeCode() {
        return itemTypeCode;
    }

    public void setItemTypeCode(String itemTypeCode) {
        this.itemTypeCode = itemTypeCode;
    }

    public String getItemUnitOfMeasureCode() {
        return itemUnitOfMeasureCode;
    }

    public void setItemUnitOfMeasureCode(String itemUnitOfMeasureCode) {
        this.itemUnitOfMeasureCode = (StringUtils.isNotBlank(itemUnitOfMeasureCode) ? itemUnitOfMeasureCode.toUpperCase() : itemUnitOfMeasureCode);
    }    
    
    public String getItemCatalogNumber() {
        return itemCatalogNumber;
    }

    public void setItemCatalogNumber(String itemCatalogNumber) {
        this.itemCatalogNumber = itemCatalogNumber;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public KualiDecimal getItemReceivedTotalQuantity() {
        return itemReceivedTotalQuantity;
    }

    public void setItemReceivedTotalQuantity(KualiDecimal itemReceivedTotalQuantity) {
        this.itemReceivedTotalQuantity = itemReceivedTotalQuantity;
    }

    public KualiDecimal getItemReturnedTotalQuantity() {
        return itemReturnedTotalQuantity;
    }

    public void setItemReturnedTotalQuantity(KualiDecimal itemReturnedTotalQuantity) {
        this.itemReturnedTotalQuantity = itemReturnedTotalQuantity;
    }

    public KualiDecimal getItemDamagedTotalQuantity() {
        return itemDamagedTotalQuantity;
    }

    public void setItemDamagedTotalQuantity(KualiDecimal itemDamagedTotalQuantity) {
        this.itemDamagedTotalQuantity = itemDamagedTotalQuantity;
    }

    public String getItemReasonAddedCode() {
        return itemReasonAddedCode;
    }

    public void setItemReasonAddedCode(String itemReasonAddedCode) {
        this.itemReasonAddedCode = itemReasonAddedCode;
    }
    /**
     * Gets the itemType attribute. 
     * @return Returns the itemType.
     */
    public ItemType getItemType() {
        if (ObjectUtils.isNull(itemType)) {
            refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);
        }
        return itemType;
    }

    /**
     * Sets the itemType attribute value.
     * @param itemType The itemType to set.
     * @deprecated
     */
    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public KualiDecimal getItemOriginalReceivedTotalQuantity() { 
    	return itemOriginalReceivedTotalQuantity;
    }

    public void setItemOriginalReceivedTotalQuantity(KualiDecimal itemOriginalReceivedTotalQuantity) {
    	this.itemOriginalReceivedTotalQuantity = itemOriginalReceivedTotalQuantity;
    }

    public KualiDecimal getItemOriginalReturnedTotalQuantity() { 
    	return itemOriginalReturnedTotalQuantity;
    }

    public void setItemOriginalReturnedTotalQuantity(KualiDecimal itemOriginalReturnedTotalQuantity) {
    	this.itemOriginalReturnedTotalQuantity = itemOriginalReturnedTotalQuantity;
    }

    public KualiDecimal getItemOriginalDamagedTotalQuantity() { 
    	return itemOriginalDamagedTotalQuantity;
    }

    public void setItemOriginalDamagedTotalQuantity(KualiDecimal itemOriginalDamagedTotalQuantity) {
    	this.itemOriginalDamagedTotalQuantity = itemOriginalDamagedTotalQuantity;
    }

    public ItemReasonAdded getItemReasonAdded() {
        if (itemReasonAdded != null) {
            return itemReasonAdded;
        }
        else if (itemReasonAddedCode != null){
            refreshReferenceObject(PurapPropertyConstants.ITEM_REASON_ADDED);
        }
        return itemReasonAdded;
    } 

    public void setItemReasonAdded(ItemReasonAdded itemReasonAdded) {
        this.itemReasonAdded = itemReasonAdded;
    }    

    public UnitOfMeasure getItemUnitOfMeasure() {
        if (ObjectUtils.isNull(itemUnitOfMeasure) || (!itemUnitOfMeasure.getItemUnitOfMeasureCode().equalsIgnoreCase(getItemUnitOfMeasureCode()))) {
            refreshReferenceObject(PurapPropertyConstants.ITEM_UNIT_OF_MEASURE);
        }
        return itemUnitOfMeasure;
    }

    public void setItemUnitOfMeasure(UnitOfMeasure itemUnitOfMeasure) {
        this.itemUnitOfMeasure = itemUnitOfMeasure;
    }

}

/*
 * Copyright 2008 The Kuali Foundation.
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

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.UnitOfMeasure;


public interface ReceivingItem {

    public Integer getReceivingItemIdentifier();

    public void setReceivingItemIdentifier(Integer receivingItemIdentifier);

    public String getDocumentNumber();

    public void setDocumentNumber(String documentNumber);

    public Integer getPurchaseOrderIdentifier();

    public void setPurchaseOrderIdentifier(Integer purchaseOrderIdentifier);

    public Integer getItemLineNumber();

    public void setItemLineNumber(Integer itemLineNumber);

    public String getItemTypeCode();

    public void setItemTypeCode(String itemTypeCode);

    public String getItemUnitOfMeasureCode();

    public void setItemUnitOfMeasureCode(String itemUnitOfMeasureCode);

    public String getItemCatalogNumber();

    public void setItemCatalogNumber(String itemCatalogNumber);

    public String getItemDescription();

    public void setItemDescription(String itemDescription);

    public KualiDecimal getItemReceivedTotalQuantity();

    public void setItemReceivedTotalQuantity(KualiDecimal itemReceivedTotalQuantity);

    public KualiDecimal getItemReturnedTotalQuantity();

    public void setItemReturnedTotalQuantity(KualiDecimal itemReturnedTotalQuantity);

    public KualiDecimal getItemDamagedTotalQuantity();

    public void setItemDamagedTotalQuantity(KualiDecimal itemDamagedTotalQuantity);

    public String getItemReasonAddedCode();

    public void setItemReasonAddedCode(String itemReasonAddedCode);

    /**
     * Gets the itemType attribute. 
     * @return Returns the itemType.
     */
    public ItemType getItemType();

    /**
     * Sets the itemType attribute value.
     * @param itemType The itemType to set.
     * @deprecated
     */
    public void setItemType(ItemType itemType);

    /**
     * Gets the itemUnitOfMeasure attribute. 
     * @return Returns the itemUnitOfMeasure.
     */
    public UnitOfMeasure getItemUnitOfMeasure();

    /**
     * Sets the itemUnitOfMeasure attribute value.
     * @param itemUnitOfMeasure The itemUnitOfMeasure to set.
     * @deprecated
     */
    public void setItemUnitOfMeasure(UnitOfMeasure itemUnitOfMeasure);
    
    public KualiDecimal getItemOriginalReceivedTotalQuantity();
    
    public void setItemOriginalReceivedTotalQuantity(KualiDecimal itemOriginalReceivedTotalQuantity);

    public KualiDecimal getItemOriginalReturnedTotalQuantity();

    public void setItemOriginalReturnedTotalQuantity(KualiDecimal itemOriginalReturnedTotalQuantity);

    public KualiDecimal getItemOriginalDamagedTotalQuantity() ;

    public void setItemOriginalDamagedTotalQuantity(KualiDecimal itemOriginalDamagedTotalQuantity);

}
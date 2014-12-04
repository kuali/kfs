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

import org.kuali.kfs.sys.businessobject.UnitOfMeasure;
import org.kuali.rice.core.api.util.type.KualiDecimal;


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

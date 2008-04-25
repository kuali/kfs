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
package org.kuali.module.purap.fixtures;

import java.math.BigDecimal;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.PurapConstants.ItemTypeCodes;
import org.kuali.module.purap.bo.ItemType;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.RequisitionItem;
import org.kuali.module.purap.fixtures.PurapTestConstants.ItemsAccounts;

public enum ItemFieldsFixture {
    
    ALL_FIELDS_ABOVE_QUANTITY_BASED(ItemTypeCodes.ITEM_TYPE_ITEM_CODE,ItemsAccounts.QUANTITY,ItemsAccounts.ITEM_UOM,ItemsAccounts.ITEM_DESC,ItemsAccounts.UNIT_PRICE),
    NO_QUANTITY_ABOVE_QUANTITY_BASED(ItemTypeCodes.ITEM_TYPE_ITEM_CODE,null,ItemsAccounts.ITEM_UOM,ItemsAccounts.ITEM_DESC,ItemsAccounts.UNIT_PRICE),
    NO_UOM_ABOVE_QUANTITY_BASED(ItemTypeCodes.ITEM_TYPE_ITEM_CODE,ItemsAccounts.QUANTITY,null,ItemsAccounts.ITEM_DESC,ItemsAccounts.UNIT_PRICE),
    NO_DESC_ABOVE_QUANTITY_BASED(ItemTypeCodes.ITEM_TYPE_ITEM_CODE,ItemsAccounts.QUANTITY,ItemsAccounts.ITEM_UOM,null,ItemsAccounts.UNIT_PRICE),
    NO_UNIT_PRICE_ABOVE_QUANTITY_BASED(ItemTypeCodes.ITEM_TYPE_ITEM_CODE,ItemsAccounts.QUANTITY,ItemsAccounts.ITEM_UOM,ItemsAccounts.ITEM_DESC,null),
    ALL_FIELDS_ABOVE_SERVICE(ItemTypeCodes.ITEM_TYPE_SERVICE_CODE,ItemsAccounts.QUANTITY,ItemsAccounts.ITEM_UOM,ItemsAccounts.ITEM_DESC,ItemsAccounts.UNIT_PRICE),
    NO_QUANTITY_ABOVE_SERVICE(ItemTypeCodes.ITEM_TYPE_SERVICE_CODE,null,ItemsAccounts.ITEM_UOM,ItemsAccounts.ITEM_DESC,ItemsAccounts.UNIT_PRICE),
    NO_UOM_ABOVE_SERVICE(ItemTypeCodes.ITEM_TYPE_SERVICE_CODE,ItemsAccounts.QUANTITY,null,ItemsAccounts.ITEM_DESC,ItemsAccounts.UNIT_PRICE),
    NO_DESC_ABOVE_SERVICE(ItemTypeCodes.ITEM_TYPE_SERVICE_CODE,ItemsAccounts.QUANTITY,ItemsAccounts.ITEM_UOM,null,ItemsAccounts.UNIT_PRICE),
    NO_UNIT_PRICE_ABOVE_SERVICE(ItemTypeCodes.ITEM_TYPE_SERVICE_CODE,ItemsAccounts.QUANTITY,ItemsAccounts.ITEM_UOM,ItemsAccounts.ITEM_DESC,null),
    ALL_FIELDS_BELOW(ItemTypeCodes.ITEM_TYPE_SHIP_AND_HAND_CODE,ItemsAccounts.QUANTITY,ItemsAccounts.ITEM_UOM,ItemsAccounts.ITEM_DESC,ItemsAccounts.UNIT_PRICE),
    NO_QUANTITY_BELOW(ItemTypeCodes.ITEM_TYPE_SHIP_AND_HAND_CODE,null,ItemsAccounts.ITEM_UOM,ItemsAccounts.ITEM_DESC,ItemsAccounts.UNIT_PRICE),
    NO_UOM_BELOW(ItemTypeCodes.ITEM_TYPE_SHIP_AND_HAND_CODE,ItemsAccounts.QUANTITY,null,ItemsAccounts.ITEM_DESC,ItemsAccounts.UNIT_PRICE),
    NO_DESC_BELOW(ItemTypeCodes.ITEM_TYPE_SHIP_AND_HAND_CODE,ItemsAccounts.QUANTITY,ItemsAccounts.ITEM_UOM,null,ItemsAccounts.UNIT_PRICE),
    NO_UNIT_PRICE_BELOW(ItemTypeCodes.ITEM_TYPE_SHIP_AND_HAND_CODE,ItemsAccounts.QUANTITY,ItemsAccounts.ITEM_UOM,ItemsAccounts.ITEM_DESC,null),
    NEGATIVE_UNIT_PRICE_QUANTITY_BASED(ItemTypeCodes.ITEM_TYPE_ITEM_CODE,ItemsAccounts.QUANTITY,ItemsAccounts.ITEM_UOM,ItemsAccounts.ITEM_DESC,ItemsAccounts.NEGATIVE_UNIT_PRICE),
    POSITIVE_UNIT_PRICE_DISCOUNT(ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE,ItemsAccounts.QUANTITY,ItemsAccounts.ITEM_UOM,ItemsAccounts.ITEM_DESC,ItemsAccounts.UNIT_PRICE),
    NEGATIVE_UNIT_PRICE_DISCOUNT(ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE,ItemsAccounts.QUANTITY,ItemsAccounts.ITEM_UOM,ItemsAccounts.ITEM_DESC,ItemsAccounts.NEGATIVE_UNIT_PRICE),
    POSITIVE_UNIT_PRICE_TRADEIN(ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE,ItemsAccounts.QUANTITY,ItemsAccounts.ITEM_UOM,ItemsAccounts.ITEM_DESC,ItemsAccounts.UNIT_PRICE),
    NEGATIVE_UNIT_PRICE_TRADEIN(ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE,ItemsAccounts.QUANTITY,ItemsAccounts.ITEM_UOM,ItemsAccounts.ITEM_DESC,ItemsAccounts.NEGATIVE_UNIT_PRICE),
    ;
    
    ItemType itemType = new ItemType();
    String itemTypeCode;
    KualiDecimal itemQuantity;
    String uomCode;
    String itemDescription;
    BigDecimal itemUnitPrice;
    
    private ItemFieldsFixture(String itemTypeCode, KualiDecimal itemQuantity, String uomCode, String itemDescription, BigDecimal itemUnitPrice) {
        this.itemTypeCode = itemTypeCode;
        this.itemQuantity = itemQuantity;
        this.uomCode = uomCode;
        this.itemDescription = itemDescription;
        this.itemUnitPrice = itemUnitPrice;
        
        this.itemType.setItemTypeCode(itemTypeCode);
    }
    
    public RequisitionItem populateRequisitionItem() {
        RequisitionItem item = new RequisitionItem();
        item.setItemTypeCode(this.itemTypeCode);
        item.setItemQuantity(this.itemQuantity);
        item.setItemUnitOfMeasureCode(this.uomCode);
        item.setItemDescription(this.itemDescription);
        item.setItemUnitPrice(this.itemUnitPrice);
        return item;
    }
    
    public PurchaseOrderItem populatePurchaseOrderItem() {
        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setItemTypeCode(this.itemTypeCode);
        item.setItemQuantity(this.itemQuantity);
        item.setItemUnitOfMeasureCode(this.uomCode);
        item.setItemDescription(this.itemDescription);
        item.setItemUnitPrice(this.itemUnitPrice);
        return item;
    }

}

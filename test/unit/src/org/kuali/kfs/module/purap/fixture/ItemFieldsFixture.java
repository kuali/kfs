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
package org.kuali.kfs.module.purap.fixture;

import java.math.BigDecimal;

import org.kuali.kfs.module.purap.PurapConstants.ItemTypeCodes;
import org.kuali.kfs.module.purap.businessobject.ItemType;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.fixture.PurapTestConstants.ItemsAccounts;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public enum ItemFieldsFixture {

    ALL_FIELDS_ABOVE_QUANTITY_BASED(ItemTypeCodes.ITEM_TYPE_ITEM_CODE,ItemsAccounts.QUANTITY,ItemsAccounts.ITEM_UOM,ItemsAccounts.ITEM_DESC,ItemsAccounts.UNIT_PRICE,1),
    NO_QUANTITY_ABOVE_QUANTITY_BASED(ItemTypeCodes.ITEM_TYPE_ITEM_CODE,null,ItemsAccounts.ITEM_UOM,ItemsAccounts.ITEM_DESC,ItemsAccounts.UNIT_PRICE,1),
    NO_UOM_ABOVE_QUANTITY_BASED(ItemTypeCodes.ITEM_TYPE_ITEM_CODE,ItemsAccounts.QUANTITY,null,ItemsAccounts.ITEM_DESC,ItemsAccounts.UNIT_PRICE,1),
    NO_DESC_ABOVE_QUANTITY_BASED(ItemTypeCodes.ITEM_TYPE_ITEM_CODE,ItemsAccounts.QUANTITY,ItemsAccounts.ITEM_UOM,null,ItemsAccounts.UNIT_PRICE,1),
    NO_UNIT_PRICE_ABOVE_QUANTITY_BASED(ItemTypeCodes.ITEM_TYPE_ITEM_CODE,ItemsAccounts.QUANTITY,ItemsAccounts.ITEM_UOM,ItemsAccounts.ITEM_DESC,null,1),
    ALL_FIELDS_ABOVE_SERVICE(ItemTypeCodes.ITEM_TYPE_SERVICE_CODE,ItemsAccounts.QUANTITY,ItemsAccounts.ITEM_UOM,ItemsAccounts.ITEM_DESC,ItemsAccounts.UNIT_PRICE,1),
    NO_QUANTITY_ABOVE_SERVICE(ItemTypeCodes.ITEM_TYPE_SERVICE_CODE,null,ItemsAccounts.ITEM_UOM,ItemsAccounts.ITEM_DESC,ItemsAccounts.UNIT_PRICE,1),
    NO_UOM_ABOVE_SERVICE(ItemTypeCodes.ITEM_TYPE_SERVICE_CODE,ItemsAccounts.QUANTITY,null,ItemsAccounts.ITEM_DESC,ItemsAccounts.UNIT_PRICE,1),
    NO_DESC_ABOVE_SERVICE(ItemTypeCodes.ITEM_TYPE_SERVICE_CODE,ItemsAccounts.QUANTITY,ItemsAccounts.ITEM_UOM,null,ItemsAccounts.UNIT_PRICE,1),
    NO_UNIT_PRICE_ABOVE_SERVICE(ItemTypeCodes.ITEM_TYPE_SERVICE_CODE,ItemsAccounts.QUANTITY,ItemsAccounts.ITEM_UOM,ItemsAccounts.ITEM_DESC,null,1),
    ALL_FIELDS_BELOW(ItemTypeCodes.ITEM_TYPE_SHIP_AND_HAND_CODE,ItemsAccounts.QUANTITY,ItemsAccounts.ITEM_UOM,ItemsAccounts.ITEM_DESC,ItemsAccounts.UNIT_PRICE,null),
    NO_QUANTITY_BELOW(ItemTypeCodes.ITEM_TYPE_SHIP_AND_HAND_CODE,null,ItemsAccounts.ITEM_UOM,ItemsAccounts.ITEM_DESC,ItemsAccounts.UNIT_PRICE,null),
    NO_UOM_BELOW(ItemTypeCodes.ITEM_TYPE_SHIP_AND_HAND_CODE,ItemsAccounts.QUANTITY,null,ItemsAccounts.ITEM_DESC,ItemsAccounts.UNIT_PRICE,null),
    NO_DESC_BELOW(ItemTypeCodes.ITEM_TYPE_SHIP_AND_HAND_CODE,ItemsAccounts.QUANTITY,ItemsAccounts.ITEM_UOM,null,ItemsAccounts.UNIT_PRICE,null),
    NO_UNIT_PRICE_BELOW(ItemTypeCodes.ITEM_TYPE_SHIP_AND_HAND_CODE,ItemsAccounts.QUANTITY,ItemsAccounts.ITEM_UOM,ItemsAccounts.ITEM_DESC,null,null),
    NEGATIVE_UNIT_PRICE_QUANTITY_BASED(ItemTypeCodes.ITEM_TYPE_ITEM_CODE,ItemsAccounts.QUANTITY,ItemsAccounts.ITEM_UOM,ItemsAccounts.ITEM_DESC,ItemsAccounts.NEGATIVE_UNIT_PRICE,null),
    POSITIVE_UNIT_PRICE_DISCOUNT(ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE,ItemsAccounts.QUANTITY,ItemsAccounts.ITEM_UOM,ItemsAccounts.ITEM_DESC,ItemsAccounts.UNIT_PRICE,null),
    NEGATIVE_UNIT_PRICE_DISCOUNT(ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE,ItemsAccounts.QUANTITY,ItemsAccounts.ITEM_UOM,ItemsAccounts.ITEM_DESC,ItemsAccounts.NEGATIVE_UNIT_PRICE,null),
    POSITIVE_UNIT_PRICE_TRADEIN(ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE,ItemsAccounts.QUANTITY,ItemsAccounts.ITEM_UOM,ItemsAccounts.ITEM_DESC,ItemsAccounts.UNIT_PRICE,null),
    NEGATIVE_UNIT_PRICE_TRADEIN(ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE,ItemsAccounts.QUANTITY,ItemsAccounts.ITEM_UOM,ItemsAccounts.ITEM_DESC,ItemsAccounts.NEGATIVE_UNIT_PRICE,null),
    ;

    ItemType itemType = new ItemType();
    String itemTypeCode;
    KualiDecimal itemQuantity;
    String uomCode;
    String itemDescription;
    BigDecimal itemUnitPrice;
    Integer itemLineNumber;

    private ItemFieldsFixture(String itemTypeCode, KualiDecimal itemQuantity, String uomCode, String itemDescription, BigDecimal itemUnitPrice, Integer itemLineNumber) {
        this.itemTypeCode = itemTypeCode;
        this.itemQuantity = itemQuantity;
        this.uomCode = uomCode;
        this.itemDescription = itemDescription;
        this.itemUnitPrice = itemUnitPrice;
        this.itemLineNumber = itemLineNumber;

        this.itemType.setItemTypeCode(itemTypeCode);
    }

    public RequisitionItem populateRequisitionItem() {
        RequisitionItem item = new RequisitionItem();
        item.setItemTypeCode(this.itemTypeCode);
        item.setItemQuantity(this.itemQuantity);
        item.setItemUnitOfMeasureCode(this.uomCode);
        item.setItemDescription(this.itemDescription);
        item.setItemUnitPrice(this.itemUnitPrice);
        item.setItemLineNumber(this.itemLineNumber);
        return item;
    }

    public PurchaseOrderItem populatePurchaseOrderItem() {
        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setItemTypeCode(this.itemTypeCode);
        item.setItemQuantity(this.itemQuantity);
        item.setItemUnitOfMeasureCode(this.uomCode);
        item.setItemDescription(this.itemDescription);
        item.setItemUnitPrice(this.itemUnitPrice);
        item.setItemLineNumber(this.itemLineNumber);
        return item;
    }

}

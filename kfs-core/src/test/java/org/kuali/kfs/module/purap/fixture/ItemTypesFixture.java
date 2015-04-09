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

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants.ItemTypeCodes;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;

public enum ItemTypesFixture {

    WITH_TRADEIN_WITH_DISCOUNT(ItemTypeCodes.ITEM_TYPE_ITEM_CODE, ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE, ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE), WITH_TRADEIN_WITH_MISC(ItemTypeCodes.ITEM_TYPE_ITEM_CODE, ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE, ItemTypeCodes.ITEM_TYPE_MISC_CODE), WITH_MISC_WITH_DISCOUNT(ItemTypeCodes.ITEM_TYPE_ITEM_CODE, ItemTypeCodes.ITEM_TYPE_MISC_CODE, ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE), ;

    private String item1TypeCode;
    private String item2TypeCode;
    private String item3TypeCode;

    private ItemTypesFixture(String itemOneTypeCode, String itemTwoTypeCode, String itemThreeTypeCode) {
        this.item1TypeCode = itemOneTypeCode;
        this.item2TypeCode = itemTwoTypeCode;
        this.item3TypeCode = itemThreeTypeCode;
    }

    public PurchaseOrderDocument populate() {
        PurchaseOrderDocument po = new PurchaseOrderDocument();
        PurchaseOrderItem item1 = new PurchaseOrderItem();
        PurchaseOrderItem item2 = new PurchaseOrderItem();
        PurchaseOrderItem item3 = new PurchaseOrderItem();
        item1.setItemTypeCode(this.item1TypeCode);
        item2.setItemTypeCode(this.item2TypeCode);
        item3.setItemTypeCode(this.item3TypeCode);
        List<PurchaseOrderItem> items = new ArrayList();
        items.add(item1);
        items.add(item2);
        items.add(item3);
        po.setItems(items);
        return po;
    }
}

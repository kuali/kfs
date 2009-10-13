/*
 * Copyright 2007 The Kuali Foundation
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

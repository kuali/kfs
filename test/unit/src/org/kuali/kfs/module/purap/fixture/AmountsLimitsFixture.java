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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants.ItemTypeCodes;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.fixture.PurapTestConstants.AmountsLimits;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public enum AmountsLimitsFixture {
    ZERO_AMOUNT_SMALL_LIMIT(AmountsLimits.ZERO, AmountsLimits.SMALL_POSITIVE_AMOUNT), SMALL_AMOUNT_SMALL_LIMIT(AmountsLimits.SMALL_POSITIVE_AMOUNT, AmountsLimits.SMALL_POSITIVE_AMOUNT), LARGE_AMOUNT_SMALL_LIMIT(AmountsLimits.LARGE_POSITIVE_AMOUNT, AmountsLimits.SMALL_POSITIVE_AMOUNT), ;

    private KualiDecimal totalAmount;
    private KualiDecimal poLimit;

    private AmountsLimitsFixture(KualiDecimal amt, KualiDecimal lim) {
        this.totalAmount = amt;
        this.poLimit = lim;
    }

    public RequisitionDocument populateRequisition() {
        RequisitionDocument req = new RequisitionDocument();
        RequisitionItem item = new RequisitionItem();
        item.setItemUnitPrice(new BigDecimal(this.totalAmount.doubleValue()));
        item.setItemQuantity(new KualiDecimal(1));
        item.setItemTypeCode(ItemTypeCodes.ITEM_TYPE_ITEM_CODE);
        item.refreshNonUpdateableReferences();
        item.getItemType().setQuantityBasedGeneralLedgerIndicator(true);
        List<RequisitionItem> items = new ArrayList();
        items.add(item);
        req.setItems(items);
        req.setPurchaseOrderTotalLimit(this.poLimit);
        req.refreshNonUpdateableReferences();
        return req;
    }

    public PurchaseOrderDocument populatePurchaseOrder() {
        PurchaseOrderDocument po = new PurchaseOrderDocument();
        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setItemUnitPrice(new BigDecimal(this.totalAmount.doubleValue()));
        item.setItemQuantity(new KualiDecimal(1));
        item.setItemTypeCode(ItemTypeCodes.ITEM_TYPE_ITEM_CODE);
        item.refreshNonUpdateableReferences();
        item.getItemType().setQuantityBasedGeneralLedgerIndicator(true);
        List<PurchaseOrderItem> items = new ArrayList();
        items.add(item);
        po.setItems(items);
        po.setPurchaseOrderTotalLimit(this.poLimit);
        po.fixItemReferences();
        return po;
    }
}

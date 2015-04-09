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

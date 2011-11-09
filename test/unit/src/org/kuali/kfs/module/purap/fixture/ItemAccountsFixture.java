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
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderAccount;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.fixture.PurapTestConstants.ItemsAccounts;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

public enum ItemAccountsFixture {

    WITH_QUANTITY_WITH_PRICE_WITH_ACCOUNT( ItemsAccounts.QUANTITY, ItemsAccounts.ITEM_UOM, ItemsAccounts.ITEM_CATALOG_NUMBER, ItemsAccounts.ITEM_DESC, ItemsAccounts.UNIT_PRICE, ItemsAccounts.PO_ACCOUNT),
    NULL_QUANTITY_WITH_PRICE_WITH_ACCOUNT( null, ItemsAccounts.ITEM_DESC, ItemsAccounts.ITEM_UOM, ItemsAccounts.ITEM_CATALOG_NUMBER, ItemsAccounts.UNIT_PRICE, ItemsAccounts.PO_ACCOUNT),
    WITH_QUANTITY_NULL_PRICE_WITH_ACCOUNT( ItemsAccounts.QUANTITY, ItemsAccounts.ITEM_UOM, ItemsAccounts.ITEM_CATALOG_NUMBER, ItemsAccounts.ITEM_DESC, null, ItemsAccounts.PO_ACCOUNT),
    WITH_QUANTITY_WITH_PRICE_NULL_ACCOUNT( ItemsAccounts.QUANTITY, ItemsAccounts.ITEM_UOM, ItemsAccounts.ITEM_CATALOG_NUMBER, ItemsAccounts.ITEM_DESC, ItemsAccounts.UNIT_PRICE, null), 
    NULL_ITEM_WITH_ACCOUNT( null, null, null, null, null, ItemsAccounts.PO_ACCOUNT),;

    private PurchaseOrderItem poItem;
    private String itemDescription;
    private KualiDecimal quantity;
    private String unitOfMeasure;
    private String catNbr;
    private String itemDesc;
    private BigDecimal unitPrice;
    private PurchaseOrderAccount poAccount;
    private String acctNumber;

    private ItemAccountsFixture(KualiDecimal quantity, String unitOfMeasure, String catNbr, String itemDesc, BigDecimal unitPrice, PurchaseOrderAccount acct) {
        this.poItem = (PurchaseOrderItem)PurchaseOrderItemFixture.PO_QTY_UNRESTRICTED_ITEM_1.createPurchaseOrderItem(
                PurApItemFixture.BASIC_QTY_ITEM_1);        
        this.quantity = quantity;
        this.unitOfMeasure = unitOfMeasure;
        this.catNbr = catNbr;
        this.itemDescription = itemDesc;
        this.unitPrice = unitPrice;
        this.poAccount = acct;
    }

    public PurchaseOrderItem populateItem() {      
        this.poItem.setItemQuantity(this.quantity);
        this.poItem.setItemUnitOfMeasureCode(this.unitOfMeasure);
        this.poItem.setItemCatalogNumber(this.catNbr);
        this.poItem.setItemDescription(this.itemDescription);
        this.poItem.setItemUnitPrice(this.unitPrice);
        if (ObjectUtils.isNotNull(this.poAccount)) {
            this.poAccount.setAccountNumber(ItemsAccounts.ACCOUNT_NUMBER);
            List<PurApAccountingLine> lines = new ArrayList();
            lines.add((PurApAccountingLine) this.poAccount);
            this.poItem.setSourceAccountingLines(lines);
        }
        this.poItem.setItemTypeCode(ItemTypeCodes.ITEM_TYPE_ITEM_CODE);
        this.poItem.setItemLineNumber(new Integer(1));
        this.poItem.refreshNonUpdateableReferences();
        return this.poItem;
    }

}

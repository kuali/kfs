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

/**
 * Fixture class for Purchase Order Item and its Accounts.
 */
public enum PurchaseOrderItemAccountsFixture {

    WITH_DESC_WITH_UOM_WITH_PRICE_WITH_ACCOUNT(ItemsAccounts.ITEM_DESC, // itemDescription
            ItemsAccounts.ITEM_UOM, // itemUnitOfMeasure
            ItemsAccounts.UNIT_PRICE, // unitPrice
            ItemsAccounts.PO_ACCOUNT, // poAccount
            ItemsAccounts.PERCENTAGE, // percent
            ItemTypeCodes.ITEM_TYPE_ITEM_CODE), // itemTypeCode
    WITH_DESC_WITH_UOM_WITH_PRICE_WITH_ACCOUNT_NO_PERCENT(ItemsAccounts.ITEM_DESC, ItemsAccounts.ITEM_UOM, ItemsAccounts.UNIT_PRICE, ItemsAccounts.PO_ACCOUNT, null, ItemTypeCodes.ITEM_TYPE_ITEM_CODE), WITH_DESC_NULL_UOM_WITH_PRICE_WITH_ACCOUNT(ItemsAccounts.ITEM_DESC, null, ItemsAccounts.UNIT_PRICE, ItemsAccounts.PO_ACCOUNT, ItemsAccounts.PERCENTAGE, ItemTypeCodes.ITEM_TYPE_ITEM_CODE), WITH_DESC_EMPTY_UOM_WITH_PRICE_WITH_ACCOUNT(ItemsAccounts.ITEM_DESC, "", ItemsAccounts.UNIT_PRICE, ItemsAccounts.PO_ACCOUNT, ItemsAccounts.PERCENTAGE, ItemTypeCodes.ITEM_TYPE_ITEM_CODE), WITH_DESC_WITH_UOM_WITH_PRICE_NULL_ACCOUNT(ItemsAccounts.ITEM_DESC, ItemsAccounts.ITEM_UOM, ItemsAccounts.UNIT_PRICE, null, ItemsAccounts.PERCENTAGE, ItemTypeCodes.ITEM_TYPE_ITEM_CODE), NULL_DESC_NULL_UOM_NULL_PRICE_WTIH_ACCOUNT(null, null, null, ItemsAccounts.PO_ACCOUNT, ItemsAccounts.PERCENTAGE, ItemTypeCodes.ITEM_TYPE_ITEM_CODE), EMPTY_DESC_EMPTY_UOM_NULL_PRICE_WITH_ACCOUNT("", "", null, ItemsAccounts.PO_ACCOUNT,
            ItemsAccounts.PERCENTAGE, ItemTypeCodes.ITEM_TYPE_ITEM_CODE), APO_ACCOUNT_1(ItemsAccounts.ITEM_DESC, ItemsAccounts.ITEM_UOM, ItemsAccounts.UNIT_PRICE_APO_1, ItemsAccounts.PO_ACCOUNT, ItemsAccounts.PERCENTAGE, ItemTypeCodes.ITEM_TYPE_ITEM_CODE), APO_ACCOUNT_2(ItemsAccounts.ITEM_DESC, ItemsAccounts.ITEM_UOM, ItemsAccounts.UNIT_PRICE_APO_2, ItemsAccounts.PO_ACCOUNT, ItemsAccounts.PERCENTAGE, ItemTypeCodes.ITEM_TYPE_SERVICE_CODE), ;

    private PurchaseOrderItem poItem;
    private String itemDescription;
    private String itemUnitOfMeasure;
    private BigDecimal unitPrice;
    private BigDecimal percent;
    private String itemTypeCode;
    private PurchaseOrderAccount poAccount;

    /**
     * Private Constructor.
     */
    private PurchaseOrderItemAccountsFixture(String itemDesc, String itemUOM, BigDecimal unitPrice, PurchaseOrderAccount acct, BigDecimal percent, String itemTypeCode) {
        this.poItem = ItemsAccounts.PO_ITEM;
        this.itemDescription = itemDesc;
        this.itemUnitOfMeasure = itemUOM;
        this.unitPrice = unitPrice;
        this.percent = percent;
        this.itemTypeCode = itemTypeCode;
        this.poAccount = acct;
    }

    /**
     * Populates the Purchase Order Item using the info contained in this fixture.
     * 
     * @return the populated Purchase Order Item.
     */
    public PurchaseOrderItem populateItem() {
        this.poItem.setItemDescription(this.itemDescription);
        this.poItem.setItemUnitOfMeasureCode(this.itemUnitOfMeasure);
        this.poItem.setItemUnitPrice(this.unitPrice);
        this.poItem.setItemTypeCode(itemTypeCode);
        this.poItem.setItemQuantity(new KualiDecimal(1));
        this.poItem.setItemLineNumber(new Integer(1));
        this.poItem.refreshNonUpdateableReferences();

        if (ObjectUtils.isNotNull(this.poAccount)) {
            this.poAccount.setAccountNumber(ItemsAccounts.ACCOUNT_NUMBER);
            this.poAccount.setAccountLinePercent(this.percent);
            this.poAccount.setChartOfAccountsCode(ItemsAccounts.CHART_CODE);
            this.poAccount.setFinancialObjectCode(ItemsAccounts.OBJECT_CODE);
            List<PurApAccountingLine> lines = new ArrayList<PurApAccountingLine>();
            lines.add((PurApAccountingLine) this.poAccount);
            this.poItem.setSourceAccountingLines(lines);
        }

        return this.poItem;
    }
}

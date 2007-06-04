/*
 * Copyright 2007 The Kuali Foundation.
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
import java.util.ArrayList;
import java.util.List;

import org.kuali.core.util.ObjectUtils;
import org.kuali.module.purap.PurapConstants.ItemTypeCodes;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurchaseOrderAccount;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.fixtures.PurapTestConstants.ItemsAccounts;

public enum ItemAccountsFixture {
    
    WITH_DESC_WITH_UOM_WITH_PRICE_WITH_ACCOUNT(ItemsAccounts.ITEM_DESC,ItemsAccounts.ITEM_UOM,ItemsAccounts.UNIT_PRICE,ItemsAccounts.PO_ACCOUNT),
    WITH_DESC_NULL_UOM_WITH_PRICE_WITH_ACCOUNT(ItemsAccounts.ITEM_DESC,null,ItemsAccounts.UNIT_PRICE,ItemsAccounts.PO_ACCOUNT),
    WITH_DESC_EMPTY_UOM_WITH_PRICE_WITH_ACCOUNT(ItemsAccounts.ITEM_DESC,"",ItemsAccounts.UNIT_PRICE,ItemsAccounts.PO_ACCOUNT),
    WITH_DESC_WITH_UOM_WITH_PRICE_NULL_ACCOUNT(ItemsAccounts.ITEM_DESC,ItemsAccounts.ITEM_UOM,ItemsAccounts.UNIT_PRICE,null),
    NULL_DESC_NULL_UOM_NULL_PRICE_WTIH_ACCOUNT(null,null,null,ItemsAccounts.PO_ACCOUNT),
    EMPTY_DESC_EMPTY_UOM_NULL_PRICE_WITH_ACCOUNT("","",null,ItemsAccounts.PO_ACCOUNT),
    ;
    
    private PurchaseOrderItem poItem;
    private String itemDescription;
    private String itemUnitOfMeasure;
    private BigDecimal unitPrice;
    private PurchaseOrderAccount poAccount;
    private String acctNumber;
    
    private ItemAccountsFixture(String itemDesc, String itemUOM, BigDecimal unitPrice, PurchaseOrderAccount acct ) {
        this.poItem = ItemsAccounts.PO_ITEM;
        this.itemDescription = itemDesc;
        this.itemUnitOfMeasure = itemUOM;
        this.unitPrice = unitPrice;
        this.poAccount = acct;
    }
    
    public PurchaseOrderItem populateItem() {
        this.poItem.setItemDescription(this.itemDescription);
        this.poItem.setItemUnitOfMeasureCode(this.itemUnitOfMeasure);
        this.poItem.setItemUnitPrice(this.unitPrice);        
        if ( ObjectUtils.isNotNull(this.poAccount) ) {
            this.poAccount.setAccountNumber(ItemsAccounts.ACCOUNT_NUMBER);
            List<PurApAccountingLine> lines = new ArrayList();
            lines.add((PurApAccountingLine)this.poAccount);
            this.poItem.setSourceAccountingLines(lines);
        }
        this.poItem.setItemTypeCode(ItemTypeCodes.ITEM_TYPE_ITEM_CODE);
        this.poItem.setItemLineNumber(new Integer(1));
        this.poItem.refreshNonUpdateableReferences();
        return this.poItem;
    }
    
}

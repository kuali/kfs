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

import java.util.ArrayList;
import java.util.List;

import org.kuali.module.purap.PurapConstants.ItemTypeCodes;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurchaseOrderAccount;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.fixtures.PurapTestConstants.ItemsAccounts;

public enum ItemAccountsFixture {
    
    WITH_DESC_WITH_UOM_WITH_ACCOUNT(ItemsAccounts.ITEM_DESC,ItemsAccounts.ITEM_UOM,ItemsAccounts.PO_ACCOUNT),
    WITH_DESC_WITH_UOM_NULL_ACCOUNT(ItemsAccounts.ITEM_DESC,ItemsAccounts.ITEM_UOM,null),
    NULL_DESC_NULL_UOM_WTIH_ACCOUNT(null,null,ItemsAccounts.PO_ACCOUNT),
    ;
    
    PurchaseOrderItem poItem;
    String itemDescription;
    String itemUnitOfMeasure;
    PurchaseOrderAccount poAccount;
    String acctNumber;
    
    private ItemAccountsFixture(String itemDesc, String itemUOM, PurchaseOrderAccount acct ) {
        this.poItem = ItemsAccounts.PO_ITEM;
        this.itemDescription = itemDesc;
        this.itemUnitOfMeasure = itemUOM;
        this.poAccount = acct;
        this.acctNumber = ItemsAccounts.ACCOUNT_NUMBER;
    }
    
    public PurchaseOrderItem populateItem() {
        this.poItem.setItemDescription(this.itemDescription);
        this.poItem.setItemUnitOfMeasureCode(this.itemUnitOfMeasure);
        this.poAccount.setAccountNumber(this.acctNumber);
        List<PurApAccountingLine> lines = new ArrayList();
        lines.add((PurApAccountingLine)this.poAccount);
        this.poItem.setSourceAccountingLines(lines);
        this.poItem.setItemTypeCode(ItemTypeCodes.ITEM_TYPE_ITEM_CODE);
        this.poItem.setItemLineNumber(new Integer(1));
        this.poItem.refreshNonUpdateableReferences();
        return this.poItem;
    }
    
}

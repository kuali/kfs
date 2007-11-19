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

import org.kuali.module.purap.bo.AccountsPayableItem;
import org.kuali.module.purap.bo.CreditMemoItem;
import org.kuali.module.purap.bo.PurchasingItem;
import org.kuali.module.purap.bo.RequisitionItem;
import org.kuali.module.purap.document.CreditMemoDocument;

public enum CreditMemoItemFixture {

    CM_QTY_UNRESTRICTED_ITEM_1(
            PurApItemFixture.BASIC_QTY_ITEM_1, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 } // creditMemoAccountMultiFixtures
    ),

    CM_ITEM_NO_APO(
            PurApItemFixture.BASIC_QTY_ITEM_NO_APO, // purApItemFixture
            new CreditMemoAccountingLineFixture[] { CreditMemoAccountingLineFixture.BASIC_CM_ACCOUNT_1 } // creditMemoAccountMultiFixtures
    ),

    /*
     * CM_QTY_APO_ITEM_1 ( false, // itemRestrictedIndicator PurApItemFixture.APO_QTY_ITEM_1, // purApItemFixture new
     * CreditMemoAccountingLineFixture[] {CreditMemoAccountingLineFixture.APO_CM_ACCOUNT_1} // creditMemoAccountMultiFixtures ),
     * CM_SERVICE_APO_ITEM_1 ( false, // itemRestrictedIndicator PurApItemFixture.APO_SERVICE_ITEM_1, // purApItemFixture new
     * RequisitionAccountingLineFixture[] {CreditMemoAccountingLineFixture.APO_CM_ACCOUNT_2,
     * CreditMemoAccountingLineFixture.APO_CM_ACCOUNT_3} // creditMemoAccountMultiFixtures ), CM_FREIGHT_ITEM_1 ( false, //
     * itemRestrictedIndicator PurApItemFixture.APO_FREIGHT_ITEM_1, // purApItemFixture new CreditMemoAccountingLineFixture[]
     * {RequisitionAccountingLineFixture.APO_REQ_ACCOUNT_4} // creditMemoAccountMultiFixtures ),
     */

    ;


    //private boolean itemRestrictedIndicator;
    private PurApItemFixture purApItemFixture;
    private CreditMemoAccountingLineFixture[] creditMemoAccountingLineFixtures;


    private CreditMemoItemFixture(PurApItemFixture purApItemFixture, CreditMemoAccountingLineFixture[] creditMemoAccountingLineFixtures) {
        this.purApItemFixture = purApItemFixture;
        this.creditMemoAccountingLineFixtures = creditMemoAccountingLineFixtures;
    }

    public void addTo(CreditMemoDocument creditMemoDocument) {
        CreditMemoItem item = null;
        item = (CreditMemoItem) this.createCreditMemoItem();
        creditMemoDocument.addItem(item);
        // iterate over the accounts
        for (CreditMemoAccountingLineFixture creditMemoAccountMultiFixture : creditMemoAccountingLineFixtures) {
            creditMemoAccountMultiFixture.addTo(item);
        }
    }

   public AccountsPayableItem createCreditMemoItem() {
        CreditMemoItem item = (CreditMemoItem) purApItemFixture.createPurApItem(CreditMemoItem.class);
        return item;
    }
}

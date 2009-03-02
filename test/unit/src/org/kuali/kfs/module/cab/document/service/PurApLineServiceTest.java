/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.cab.document.service;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableActionHistory;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableLineAssetAccount;
import org.kuali.kfs.module.cab.fixture.PurchasingAccountsPayableDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.kns.util.KualiDecimal;


public class PurApLineServiceTest extends KualiTestBase {

    private PurApLineService purApLineService;
    private PurchasingAccountsPayableDocument purApDocument;
    private PurchasingAccountsPayableItemAsset itemAsset;

    @ConfigureContext(session = UserNameFixture.khuntley, shouldCommitTransactions = false)
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        purApLineService = SpringContext.getBean(PurApLineService.class);

        prepareTestDataRecords();
    }

    private void prepareTestDataRecords() throws Exception {
        purApDocument = PurchasingAccountsPayableDocumentFixture.createPurApDocument();
        itemAsset = purApDocument.getPurchasingAccountsPayableItemAssets().get(0);
    }

    public void testProcessPercentPayment() throws Exception {
        List<PurchasingAccountsPayableActionHistory> actionsTaken = new ArrayList<PurchasingAccountsPayableActionHistory>();
        purApLineService.processPercentPayment(itemAsset, actionsTaken);
        assertEquals(itemAsset.getAccountsPayableItemQuantity(), new KualiDecimal(1));
        assertEquals(itemAsset.getTotalCost(), getTotalCost(itemAsset));
        assertEquals(itemAsset.getUnitCost(), getTotalCost(itemAsset));
        // check action taken history
        assertEquals(actionsTaken.size(), 1);
        assertEquals(actionsTaken.get(0).getActionTypeCode(), CabConstants.Actions.PERCENT_PAYMENT);
        
    }

    public void testProcessPercentPayment_noAction() throws Exception {
        itemAsset.setAccountsPayableItemQuantity(new KualiDecimal(2));
        List<PurchasingAccountsPayableActionHistory> actionsTaken = new ArrayList<PurchasingAccountsPayableActionHistory>();
        purApLineService.processPercentPayment(itemAsset, actionsTaken);
        assertEquals(itemAsset.getAccountsPayableItemQuantity(), new KualiDecimal(2));
        // check action taken history
        assertEquals(actionsTaken.size(), 0);
        
    }

    private KualiDecimal getTotalCost(PurchasingAccountsPayableItemAsset item) {
        KualiDecimal totalCost = KualiDecimal.ZERO;
        for (PurchasingAccountsPayableLineAssetAccount account:item.getPurchasingAccountsPayableLineAssetAccounts()) {
            totalCost = totalCost.add(account.getItemAccountTotalAmount());
        }
        return totalCost;
    }

}

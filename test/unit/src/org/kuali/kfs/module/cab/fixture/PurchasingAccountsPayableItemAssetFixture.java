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
package org.kuali.kfs.module.cab.fixture;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.rice.kns.util.KualiDecimal;

public enum PurchasingAccountsPayableItemAssetFixture {
    REC1 {
        public PurchasingAccountsPayableItemAsset newRecord() {
            PurchasingAccountsPayableItemAsset itemAsset = new PurchasingAccountsPayableItemAsset();
            itemAsset.setAccountsPayableItemQuantity(new KualiDecimal(0.7));
            itemAsset.setActive(true);
            return itemAsset;
        }
    };

    public abstract PurchasingAccountsPayableItemAsset newRecord();

    public static List<PurchasingAccountsPayableItemAsset> createPurApItems() {
        List<PurchasingAccountsPayableItemAsset> itemAssets = new ArrayList<PurchasingAccountsPayableItemAsset>();
        PurchasingAccountsPayableItemAsset newItem1 = REC1.newRecord();
        newItem1.getPurchasingAccountsPayableLineAssetAccounts().addAll(PurchasingAccountsPayableLineAssetAccountFixture.createPurApAccounts());
        itemAssets.add(newItem1);
        return itemAssets;
    }
}

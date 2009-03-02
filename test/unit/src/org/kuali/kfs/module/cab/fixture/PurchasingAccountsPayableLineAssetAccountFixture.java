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

import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableLineAssetAccount;


public enum PurchasingAccountsPayableLineAssetAccountFixture {
    REC1 {
        public PurchasingAccountsPayableLineAssetAccount newRecord() {
            PurchasingAccountsPayableLineAssetAccount account = new PurchasingAccountsPayableLineAssetAccount();
            account.setActive(true);
            return account;
        }

    };
    public abstract PurchasingAccountsPayableLineAssetAccount newRecord();

    public static List<PurchasingAccountsPayableLineAssetAccount> createPurApAccounts() {
        List<PurchasingAccountsPayableLineAssetAccount> newAccounts = new ArrayList<PurchasingAccountsPayableLineAssetAccount>();
        PurchasingAccountsPayableLineAssetAccount account1 = REC1.newRecord();
        GeneralLedgerEntry newGlEntry = GeneralLedgerEntryFixture.createGeneralLedgerEntry();
        account1.setGeneralLedgerAccountIdentifier(newGlEntry.getGeneralLedgerAccountIdentifier());
        account1.setItemAccountTotalAmount(newGlEntry.getAmount());
        account1.setGeneralLedgerEntry(newGlEntry);
        newAccounts.add(account1);
        return newAccounts;
    }

}

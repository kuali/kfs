/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.cab.fixture;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableLineAssetAccount;


public enum PurchasingAccountsPayableLineAssetAccountFixture {
    REC1 {
        public PurchasingAccountsPayableLineAssetAccount newRecord() {
            PurchasingAccountsPayableLineAssetAccount account = new PurchasingAccountsPayableLineAssetAccount();
            account.setActivityStatusCode(CabConstants.ActivityStatusCode.NEW);
            return account;
        }
    },

    REC2 {
        public PurchasingAccountsPayableLineAssetAccount newRecord() {
            PurchasingAccountsPayableLineAssetAccount account = new PurchasingAccountsPayableLineAssetAccount();
            account.setActivityStatusCode(CabConstants.ActivityStatusCode.NEW);
            return account;
        }
    },
    REC3 {
        public PurchasingAccountsPayableLineAssetAccount newRecord() {
            PurchasingAccountsPayableLineAssetAccount account = new PurchasingAccountsPayableLineAssetAccount();
            account.setActivityStatusCode(CabConstants.ActivityStatusCode.NEW);
            return account;
        }
        
    },
    REC4 {
        public PurchasingAccountsPayableLineAssetAccount newRecord() {
            PurchasingAccountsPayableLineAssetAccount account = new PurchasingAccountsPayableLineAssetAccount();
            account.setActivityStatusCode(CabConstants.ActivityStatusCode.NEW);
            return account;
        }
    },
    REC5 {
        public PurchasingAccountsPayableLineAssetAccount newRecord() {
            PurchasingAccountsPayableLineAssetAccount account = new PurchasingAccountsPayableLineAssetAccount();
            account.setActivityStatusCode(CabConstants.ActivityStatusCode.NEW);
            return account;
        }
    };

    public abstract PurchasingAccountsPayableLineAssetAccount newRecord();

    public static List<PurchasingAccountsPayableLineAssetAccount> createPurApAccounts() {
        List<PurchasingAccountsPayableLineAssetAccount> newAccounts = new ArrayList<PurchasingAccountsPayableLineAssetAccount>();
        List<GeneralLedgerEntry> newGlEntries = GeneralLedgerEntryFixture.createGeneralLedgerEntry();
        Iterator glIterator = newGlEntries.iterator();

        if (glIterator.hasNext()) {
            GeneralLedgerEntry newGlEntry1 = (GeneralLedgerEntry) glIterator.next();
            PurchasingAccountsPayableLineAssetAccount account1 = REC1.newRecord();
            setAccountByGlEntry(newGlEntry1, account1);
            newAccounts.add(account1);
        }
        
        if (glIterator.hasNext()) {
            GeneralLedgerEntry newGlEntry2 = (GeneralLedgerEntry) glIterator.next();
            PurchasingAccountsPayableLineAssetAccount account2 = REC2.newRecord();
            setAccountByGlEntry(newGlEntry2, account2);
            newAccounts.add(account2);
        }
        
        if (glIterator.hasNext()) {
            GeneralLedgerEntry newGlEntry3 = (GeneralLedgerEntry) glIterator.next();
            PurchasingAccountsPayableLineAssetAccount account3 = REC3.newRecord();
            setAccountByGlEntry(newGlEntry3, account3);
            newAccounts.add(account3);
        }
        
        if (glIterator.hasNext()) {
            GeneralLedgerEntry newGlEntry4 = (GeneralLedgerEntry) glIterator.next();
            PurchasingAccountsPayableLineAssetAccount account4 = REC4.newRecord();
            setAccountByGlEntry(newGlEntry4, account4);
            newAccounts.add(account4);
        }
        
        if (glIterator.hasNext()) {
            GeneralLedgerEntry newGlEntry5 = (GeneralLedgerEntry) glIterator.next();
            PurchasingAccountsPayableLineAssetAccount account5 = REC5.newRecord();
            setAccountByGlEntry(newGlEntry5, account5);
            newAccounts.add(account5);
        }
        
        return newAccounts;
    }

    private static void setAccountByGlEntry(GeneralLedgerEntry newGlEntry, PurchasingAccountsPayableLineAssetAccount account) {
        account.setGeneralLedgerAccountIdentifier(newGlEntry.getGeneralLedgerAccountIdentifier());
        account.setItemAccountTotalAmount(newGlEntry.getAmount());
        account.setGeneralLedgerEntry(newGlEntry);
    }

}

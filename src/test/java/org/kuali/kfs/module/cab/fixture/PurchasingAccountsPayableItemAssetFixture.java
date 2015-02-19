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
package org.kuali.kfs.module.cab.fixture;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableLineAssetAccount;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public enum PurchasingAccountsPayableItemAssetFixture {
    REC1 {
        public PurchasingAccountsPayableItemAsset newRecord() {
            PurchasingAccountsPayableItemAsset itemAsset = new PurchasingAccountsPayableItemAsset();
            itemAsset.setAccountsPayableItemQuantity(new KualiDecimal(0.7));
            itemAsset.setAccountsPayableLineItemIdentifier(new Integer(100));
            itemAsset.setCapitalAssetBuilderLineNumber(new Integer(1));
            itemAsset.setActivityStatusCode(CabConstants.ActivityStatusCode.NEW);
            return itemAsset;
        }
    },
    
    REC2 {
        public PurchasingAccountsPayableItemAsset newRecord() {
            PurchasingAccountsPayableItemAsset itemAsset = new PurchasingAccountsPayableItemAsset();
            itemAsset.setAccountsPayableItemQuantity(new KualiDecimal(2));
            itemAsset.setAccountsPayableLineItemIdentifier(new Integer(101));
            itemAsset.setCapitalAssetBuilderLineNumber(new Integer(1));
            itemAsset.setActivityStatusCode(CabConstants.ActivityStatusCode.NEW);
            return itemAsset;
        }
    },
    
    REC3 {
        public PurchasingAccountsPayableItemAsset newRecord() {
            PurchasingAccountsPayableItemAsset itemAsset = new PurchasingAccountsPayableItemAsset();
            itemAsset.setAccountsPayableItemQuantity(new KualiDecimal(3));
            itemAsset.setAccountsPayableLineItemIdentifier(new Integer(102));
            itemAsset.setCapitalAssetBuilderLineNumber(new Integer(1));
            itemAsset.setActivityStatusCode(CabConstants.ActivityStatusCode.NEW);
            return itemAsset;
        }
    },
    
    REC4 {
        public PurchasingAccountsPayableItemAsset newRecord() {
            PurchasingAccountsPayableItemAsset itemAsset = new PurchasingAccountsPayableItemAsset();
            itemAsset.setAccountsPayableLineItemIdentifier(new Integer(200));
            itemAsset.setAccountsPayableItemQuantity(new KualiDecimal(4));
            itemAsset.setCapitalAssetBuilderLineNumber(new Integer(1));
            itemAsset.setActivityStatusCode(CabConstants.ActivityStatusCode.NEW);
            return itemAsset;
        }
    };

    public abstract PurchasingAccountsPayableItemAsset newRecord();

    public static List<PurchasingAccountsPayableItemAsset> createPurApItems() {
        List<PurchasingAccountsPayableItemAsset> itemAssets = new ArrayList<PurchasingAccountsPayableItemAsset>();
        List<PurchasingAccountsPayableLineAssetAccount> newAccounts = PurchasingAccountsPayableLineAssetAccountFixture.createPurApAccounts();
        Iterator iterator =  newAccounts.iterator();
        
        // build four item lines
        if (iterator.hasNext()) {
            PurchasingAccountsPayableLineAssetAccount newAccount1 = (PurchasingAccountsPayableLineAssetAccount)iterator.next();
            PurchasingAccountsPayableItemAsset newItem1 = REC1.newRecord();
            setAccountByItem(newAccount1, newItem1);
            newItem1.getPurchasingAccountsPayableLineAssetAccounts().add(newAccount1);
            newAccount1.setPurchasingAccountsPayableItemAsset(newItem1);
            itemAssets.add(newItem1);
        }
        if (iterator.hasNext()) {
            PurchasingAccountsPayableLineAssetAccount newAccount2 = (PurchasingAccountsPayableLineAssetAccount)iterator.next();
            PurchasingAccountsPayableItemAsset newItem2 = REC1.newRecord();
            setAccountByItem(newAccount2, newItem2);
            newItem2.getPurchasingAccountsPayableLineAssetAccounts().add(newAccount2);
            newAccount2.setPurchasingAccountsPayableItemAsset(newItem2);
            itemAssets.add(newItem2);
        }
        
        if (iterator.hasNext()) {
            PurchasingAccountsPayableLineAssetAccount newAccount3 = (PurchasingAccountsPayableLineAssetAccount)iterator.next();
            PurchasingAccountsPayableItemAsset newItem3 = REC3.newRecord();
            setAccountByItem(newAccount3, newItem3);
            newItem3.getPurchasingAccountsPayableLineAssetAccounts().add(newAccount3);
            newAccount3.setPurchasingAccountsPayableItemAsset(newItem3);
            itemAssets.add(newItem3);
        }
        
        if (iterator.hasNext()) {
            // add the 1st account to this item
            PurchasingAccountsPayableLineAssetAccount newAccount4 = (PurchasingAccountsPayableLineAssetAccount)iterator.next();
            PurchasingAccountsPayableItemAsset newItem4 = REC4.newRecord();
            setAccountByItem(newAccount4, newItem4);
            newItem4.getPurchasingAccountsPayableLineAssetAccounts().add(newAccount4);
            newAccount4.setPurchasingAccountsPayableItemAsset(newItem4);
            // add the 2nd account to this item
            if (iterator.hasNext()) {
                PurchasingAccountsPayableLineAssetAccount newAccount5 = (PurchasingAccountsPayableLineAssetAccount)iterator.next();
                setAccountByItem(newAccount5, newItem4);
                newItem4.getPurchasingAccountsPayableLineAssetAccounts().add(newAccount5);
                newAccount5.setPurchasingAccountsPayableItemAsset(newItem4);
            }
            
            itemAssets.add(newItem4);
        }
        
        return itemAssets;
    }

    private static void setAccountByItem(PurchasingAccountsPayableLineAssetAccount newAccount, PurchasingAccountsPayableItemAsset newItem) {
        newAccount.setAccountsPayableLineItemIdentifier(newItem.getAccountsPayableLineItemIdentifier());
        newAccount.setDocumentNumber(newItem.getDocumentNumber());
        newAccount.setCapitalAssetBuilderLineNumber(newItem.getCapitalAssetBuilderLineNumber());
    }
}

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

import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;

public enum PurchasingAccountsPayableDocumentFixture {
    REC1 {
        public PurchasingAccountsPayableDocument newRecord() {
            PurchasingAccountsPayableDocument document = new PurchasingAccountsPayableDocument();
            document.setDocumentNumber("33");
            document.setDocumentTypeCode("PREQ");
            return document;
        }
    },
    
    REC2 {
        public PurchasingAccountsPayableDocument newRecord() {
            PurchasingAccountsPayableDocument document = new PurchasingAccountsPayableDocument();
            document.setDocumentNumber("34");
            document.setDocumentTypeCode("PREQ");
            return document;
        }
    },
    
    REC3 {
        public PurchasingAccountsPayableDocument newRecord() {
            PurchasingAccountsPayableDocument document = new PurchasingAccountsPayableDocument();
            document.setDocumentNumber("44");
            document.setDocumentTypeCode("CM");
            return document;
        }
    };

    public abstract PurchasingAccountsPayableDocument newRecord();

    public static List<PurchasingAccountsPayableDocument> createPurApDocuments() {
        List<PurchasingAccountsPayableDocument> newDocuments = new ArrayList<PurchasingAccountsPayableDocument>();
        List<PurchasingAccountsPayableItemAsset> items = PurchasingAccountsPayableItemAssetFixture.createPurApItems();
        Iterator<PurchasingAccountsPayableItemAsset> itemIterator = items.iterator();
        
        if (itemIterator.hasNext()) {
            PurchasingAccountsPayableDocument document1 = REC1.newRecord();
            PurchasingAccountsPayableItemAsset item1 = (PurchasingAccountsPayableItemAsset)itemIterator.next();
            item1.setPurchasingAccountsPayableDocument(document1);
            item1.setDocumentNumber(document1.getDocumentNumber());
            document1.getPurchasingAccountsPayableItemAssets().add(item1);
            
            if (itemIterator.hasNext()) {
                PurchasingAccountsPayableItemAsset item2 = (PurchasingAccountsPayableItemAsset)itemIterator.next();
                item2.setPurchasingAccountsPayableDocument(document1);
                item2.setDocumentNumber(document1.getDocumentNumber());
                document1.getPurchasingAccountsPayableItemAssets().add(item2);
            }
            
            newDocuments.add(document1);
        }
        
        if (itemIterator.hasNext()) {
            PurchasingAccountsPayableDocument document2 = REC2.newRecord();
            PurchasingAccountsPayableItemAsset item3 = (PurchasingAccountsPayableItemAsset)itemIterator.next();
            item3.setPurchasingAccountsPayableDocument(document2);
            item3.setDocumentNumber(document2.getDocumentNumber());
            document2.getPurchasingAccountsPayableItemAssets().add(item3);
            newDocuments.add(document2);
        }
        
        if (itemIterator.hasNext()) {
            PurchasingAccountsPayableDocument document3 = REC3.newRecord();
            PurchasingAccountsPayableItemAsset item4 = (PurchasingAccountsPayableItemAsset)itemIterator.next();
            item4.setPurchasingAccountsPayableDocument(document3);
            item4.setDocumentNumber(document3.getDocumentNumber());
            document3.getPurchasingAccountsPayableItemAssets().add(item4);
            newDocuments.add(document3);
        }
        
        return newDocuments;
    }

}

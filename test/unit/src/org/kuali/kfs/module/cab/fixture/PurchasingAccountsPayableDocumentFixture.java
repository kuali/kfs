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
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.BusinessObjectService;

public enum PurchasingAccountsPayableDocumentFixture {
    REC1 {
        public PurchasingAccountsPayableDocument newRecord() {
            PurchasingAccountsPayableDocument document = new PurchasingAccountsPayableDocument();
            document.setDocumentNumber("33");
            document.setDocumentTypeCode("PREQ");
            return document;
        }
    };

    public abstract PurchasingAccountsPayableDocument newRecord();

    public static PurchasingAccountsPayableDocument createPurApDocument() {
        PurchasingAccountsPayableDocument newDocument = REC1.newRecord();
        newDocument.getPurchasingAccountsPayableItemAssets().addAll(PurchasingAccountsPayableItemAssetFixture.createPurApItems());
        return newDocument;
    }

}

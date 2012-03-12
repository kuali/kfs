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
import java.util.List;

import org.kuali.kfs.module.purap.businessobject.PurchaseOrderCapitalAssetItem;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum PurchaseOrderCapitalAssetItemFixture {

    REC1 {
        @Override
        public PurchaseOrderCapitalAssetItem newRecord() {
            PurchaseOrderCapitalAssetItem obj = new PurchaseOrderCapitalAssetItem();
            obj.setCapitalAssetItemIdentifier(1000);
            obj.setItemIdentifier(211);
            obj.setDocumentNumber("21");
            obj.setCapitalAssetTransactionTypeCode("MDEX");
            obj.setCapitalAssetSystemIdentifier(1100);
            return obj;
        };
    },
    REC2 {
        @Override
        public PurchaseOrderCapitalAssetItem newRecord() {
            PurchaseOrderCapitalAssetItem obj = new PurchaseOrderCapitalAssetItem();
            obj.setCapitalAssetItemIdentifier(1001);
            obj.setItemIdentifier(212);
            obj.setDocumentNumber("21");
            obj.setCapitalAssetTransactionTypeCode("MDEX");
            obj.setCapitalAssetSystemIdentifier(1101);
            return obj;
        };
    },
    REC3 {
        @Override
        public PurchaseOrderCapitalAssetItem newRecord() {
            PurchaseOrderCapitalAssetItem obj = new PurchaseOrderCapitalAssetItem();
            obj.setCapitalAssetItemIdentifier(1002);
            obj.setItemIdentifier(221);
            obj.setDocumentNumber("22");
            obj.setCapitalAssetTransactionTypeCode("NEW");
            obj.setCapitalAssetSystemIdentifier(1102);
            return obj;
        };
    },
    REC4 {
        @Override
        public PurchaseOrderCapitalAssetItem newRecord() {
            PurchaseOrderCapitalAssetItem obj = new PurchaseOrderCapitalAssetItem();
            obj.setCapitalAssetItemIdentifier(1003);
            obj.setItemIdentifier(222);
            obj.setDocumentNumber("22");
            obj.setCapitalAssetTransactionTypeCode("NEW");
            obj.setCapitalAssetSystemIdentifier(1103);
            return obj;
        };
    };
    public abstract PurchaseOrderCapitalAssetItem newRecord();

    public static void setUpData() {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(getAll());

    }

    private static List<PersistableBusinessObjectBase> getAll() {
        List<PersistableBusinessObjectBase> recs = new ArrayList<PersistableBusinessObjectBase>();
        recs.add(REC1.newRecord());
        recs.add(REC2.newRecord());
        recs.add(REC3.newRecord());
        recs.add(REC4.newRecord());
        return recs;
    }
}

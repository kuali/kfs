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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum PurchaseOrderItemFixture {

    REC1 {
        @Override
        public PurchaseOrderItem newRecord() {
            PurchaseOrderItem obj = new PurchaseOrderItem();
            obj.setDocumentNumber("21");
            obj.setItemIdentifier(211);
            obj.setItemLineNumber(1);
            obj.setItemTypeCode("ITEM");
            obj.setItemUnitOfMeasureCode("UN");
            obj.setItemQuantity(new KualiDecimal(3));
            obj.setItemInvoicedTotalQuantity(KualiDecimal.ZERO);
            obj.setItemInvoicedTotalAmount(KualiDecimal.ZERO);
            obj.setItemReceivedTotalQuantity(KualiDecimal.ZERO);
            obj.setItemDescription("Laptop");
            obj.setItemUnitPrice(new BigDecimal(6000));
            obj.setItemOutstandingEncumberedQuantity(new KualiDecimal(3));
            obj.setItemOutstandingEncumberedAmount(new KualiDecimal(18000));
            obj.setItemActiveIndicator(true);
            obj.setItemAssignedToTradeInIndicator(false);
            obj.setItemDamagedTotalQuantity(KualiDecimal.ZERO);
            return obj;
        };
    },
    REC2 {
        @Override
        public PurchaseOrderItem newRecord() {
            PurchaseOrderItem obj = new PurchaseOrderItem();
            obj.setDocumentNumber("21");
            obj.setItemIdentifier(212);
            obj.setItemLineNumber(2);
            obj.setItemTypeCode("ITEM");
            obj.setItemUnitOfMeasureCode("PR");
            obj.setItemQuantity(new KualiDecimal(2));
            obj.setItemInvoicedTotalQuantity(KualiDecimal.ZERO);
            obj.setItemInvoicedTotalAmount(KualiDecimal.ZERO);
            obj.setItemReceivedTotalQuantity(KualiDecimal.ZERO);
            obj.setItemDescription("Desk & Chair");
            obj.setItemUnitPrice(new BigDecimal(7000));
            obj.setItemOutstandingEncumberedQuantity(new KualiDecimal(2));
            obj.setItemOutstandingEncumberedAmount(new KualiDecimal(14000));
            obj.setItemActiveIndicator(true);
            obj.setItemAssignedToTradeInIndicator(false);
            obj.setItemDamagedTotalQuantity(KualiDecimal.ZERO);
            return obj;
        };
    },
    REC3 {
        @Override
        public PurchaseOrderItem newRecord() {
            PurchaseOrderItem obj = new PurchaseOrderItem();
            obj.setDocumentNumber("21");
            obj.setItemIdentifier(213);
            obj.setItemTypeCode("FRHT");
            obj.setItemInvoicedTotalQuantity(KualiDecimal.ZERO);
            obj.setItemInvoicedTotalAmount(KualiDecimal.ZERO);
            obj.setItemReceivedTotalQuantity(KualiDecimal.ZERO);
            obj.setItemDescription("USPS");
            obj.setItemUnitPrice(new BigDecimal(750));
            obj.setItemOutstandingEncumberedAmount(new KualiDecimal(750));
            obj.setItemActiveIndicator(true);
            obj.setItemAssignedToTradeInIndicator(false);
            return obj;
        };
    },
    REC4 {
        @Override
        public PurchaseOrderItem newRecord() {
            PurchaseOrderItem obj = new PurchaseOrderItem();
            obj.setDocumentNumber("21");
            obj.setItemIdentifier(214);
            obj.setItemTypeCode("SPHD");
            obj.setItemInvoicedTotalQuantity(KualiDecimal.ZERO);
            obj.setItemInvoicedTotalAmount(KualiDecimal.ZERO);
            obj.setItemReceivedTotalQuantity(KualiDecimal.ZERO);
            obj.setItemDescription("USPS");
            obj.setItemUnitPrice(new BigDecimal(250));
            obj.setItemOutstandingEncumberedAmount(new KualiDecimal(250));
            obj.setItemActiveIndicator(true);
            obj.setItemAssignedToTradeInIndicator(false);
            return obj;
        };
    },
    REC5 {
        @Override
        public PurchaseOrderItem newRecord() {
            PurchaseOrderItem obj = new PurchaseOrderItem();
            obj.setDocumentNumber("22");
            obj.setItemIdentifier(221);
            obj.setItemLineNumber(1);
            obj.setItemTypeCode("ITEM");
            obj.setItemUnitOfMeasureCode("UN");
            obj.setItemQuantity(new KualiDecimal(3));
            obj.setItemInvoicedTotalQuantity(KualiDecimal.ZERO);
            obj.setItemInvoicedTotalAmount(KualiDecimal.ZERO);
            obj.setItemReceivedTotalQuantity(KualiDecimal.ZERO);
            obj.setItemDescription("Laptop");
            obj.setItemUnitPrice(new BigDecimal(6000));
            obj.setItemOutstandingEncumberedQuantity(new KualiDecimal(3));
            obj.setItemOutstandingEncumberedAmount(new KualiDecimal(18000));
            obj.setItemActiveIndicator(true);
            obj.setItemAssignedToTradeInIndicator(false);
            obj.setItemDamagedTotalQuantity(KualiDecimal.ZERO);
            return obj;
        };
    },
    REC6 {
        @Override
        public PurchaseOrderItem newRecord() {
            PurchaseOrderItem obj = new PurchaseOrderItem();
            obj.setDocumentNumber("22");
            obj.setItemIdentifier(222);
            obj.setItemLineNumber(2);
            obj.setItemTypeCode("ITEM");
            obj.setItemUnitOfMeasureCode("PR");
            obj.setItemQuantity(new KualiDecimal(2));
            obj.setItemInvoicedTotalQuantity(KualiDecimal.ZERO);
            obj.setItemInvoicedTotalAmount(KualiDecimal.ZERO);
            obj.setItemReceivedTotalQuantity(KualiDecimal.ZERO);
            obj.setItemDescription("Desk & Chair");
            obj.setItemUnitPrice(new BigDecimal(7000));
            obj.setItemOutstandingEncumberedQuantity(new KualiDecimal(2));
            obj.setItemOutstandingEncumberedAmount(new KualiDecimal(14000));
            obj.setItemActiveIndicator(true);
            obj.setItemAssignedToTradeInIndicator(false);
            obj.setItemDamagedTotalQuantity(KualiDecimal.ZERO);
            return obj;
        };
    },
    REC7 {
        @Override
        public PurchaseOrderItem newRecord() {
            PurchaseOrderItem obj = new PurchaseOrderItem();
            obj.setDocumentNumber("22");
            obj.setItemIdentifier(223);
            obj.setItemTypeCode("FRHT");
            obj.setItemInvoicedTotalQuantity(KualiDecimal.ZERO);
            obj.setItemInvoicedTotalAmount(KualiDecimal.ZERO);
            obj.setItemReceivedTotalQuantity(KualiDecimal.ZERO);
            obj.setItemDescription("USPS");
            obj.setItemUnitPrice(new BigDecimal(750));
            obj.setItemOutstandingEncumberedAmount(new KualiDecimal(750));
            obj.setItemActiveIndicator(true);
            obj.setItemAssignedToTradeInIndicator(false);
            return obj;
        };
    },
    REC8 {
        @Override
        public PurchaseOrderItem newRecord() {
            PurchaseOrderItem obj = new PurchaseOrderItem();
            obj.setDocumentNumber("22");
            obj.setItemIdentifier(224);
            obj.setItemTypeCode("SPHD");
            obj.setItemInvoicedTotalQuantity(KualiDecimal.ZERO);
            obj.setItemInvoicedTotalAmount(KualiDecimal.ZERO);
            obj.setItemReceivedTotalQuantity(KualiDecimal.ZERO);
            obj.setItemDescription("USPS");
            obj.setItemUnitPrice(new BigDecimal(250));
            obj.setItemOutstandingEncumberedAmount(new KualiDecimal(250));
            obj.setItemActiveIndicator(true);
            obj.setItemAssignedToTradeInIndicator(false);
            return obj;
        };
    },
    REC9 {
        @Override
        public PurchaseOrderItem newRecord() {
            PurchaseOrderItem obj = new PurchaseOrderItem();
            obj.setDocumentNumber("23");
            obj.setItemIdentifier(231);
            obj.setItemLineNumber(1);
            obj.setItemTypeCode("ITEM");
            obj.setItemUnitOfMeasureCode("UN");
            obj.setItemQuantity(new KualiDecimal(3));
            obj.setItemInvoicedTotalQuantity(KualiDecimal.ZERO);
            obj.setItemInvoicedTotalAmount(KualiDecimal.ZERO);
            obj.setItemReceivedTotalQuantity(KualiDecimal.ZERO);
            obj.setItemDescription("Laptop");
            obj.setItemUnitPrice(new BigDecimal(6000));
            obj.setItemOutstandingEncumberedQuantity(new KualiDecimal(3));
            obj.setItemOutstandingEncumberedAmount(new KualiDecimal(18000));
            obj.setItemActiveIndicator(true);
            obj.setItemAssignedToTradeInIndicator(false);
            obj.setItemDamagedTotalQuantity(KualiDecimal.ZERO);
            return obj;
        };
    },
    REC10 {
        @Override
        public PurchaseOrderItem newRecord() {
            PurchaseOrderItem obj = new PurchaseOrderItem();
            obj.setDocumentNumber("23");
            obj.setItemIdentifier(232);
            obj.setItemLineNumber(2);
            obj.setItemTypeCode("ITEM");
            obj.setItemUnitOfMeasureCode("PR");
            obj.setItemQuantity(new KualiDecimal(2));
            obj.setItemInvoicedTotalQuantity(KualiDecimal.ZERO);
            obj.setItemInvoicedTotalAmount(KualiDecimal.ZERO);
            obj.setItemReceivedTotalQuantity(KualiDecimal.ZERO);
            obj.setItemDescription("Desk & Chair");
            obj.setItemUnitPrice(new BigDecimal(7000));
            obj.setItemOutstandingEncumberedQuantity(new KualiDecimal(2));
            obj.setItemOutstandingEncumberedAmount(new KualiDecimal(14000));
            obj.setItemActiveIndicator(true);
            obj.setItemAssignedToTradeInIndicator(false);
            obj.setItemDamagedTotalQuantity(KualiDecimal.ZERO);
            return obj;
        };
    },
    REC11 {
        @Override
        public PurchaseOrderItem newRecord() {
            PurchaseOrderItem obj = new PurchaseOrderItem();
            obj.setDocumentNumber("23");
            obj.setItemIdentifier(233);
            obj.setItemTypeCode("FRHT");
            obj.setItemInvoicedTotalQuantity(KualiDecimal.ZERO);
            obj.setItemInvoicedTotalAmount(KualiDecimal.ZERO);
            obj.setItemReceivedTotalQuantity(KualiDecimal.ZERO);
            obj.setItemDescription("USPS");
            obj.setItemUnitPrice(new BigDecimal(750));
            obj.setItemOutstandingEncumberedAmount(new KualiDecimal(750));
            obj.setItemActiveIndicator(true);
            obj.setItemAssignedToTradeInIndicator(false);
            return obj;
        };
    },
    REC12 {
        @Override
        public PurchaseOrderItem newRecord() {
            PurchaseOrderItem obj = new PurchaseOrderItem();
            obj.setDocumentNumber("23");
            obj.setItemIdentifier(234);
            obj.setItemTypeCode("SPHD");
            obj.setItemInvoicedTotalQuantity(KualiDecimal.ZERO);
            obj.setItemInvoicedTotalAmount(KualiDecimal.ZERO);
            obj.setItemReceivedTotalQuantity(KualiDecimal.ZERO);
            obj.setItemDescription("USPS");
            obj.setItemUnitPrice(new BigDecimal(250));
            obj.setItemOutstandingEncumberedAmount(new KualiDecimal(250));
            obj.setItemActiveIndicator(true);
            obj.setItemAssignedToTradeInIndicator(false);
            return obj;
        };
    };
    public abstract PurchaseOrderItem newRecord();

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
        recs.add(REC5.newRecord());
        recs.add(REC6.newRecord());
        recs.add(REC7.newRecord());
        recs.add(REC8.newRecord());
        recs.add(REC9.newRecord());
        recs.add(REC10.newRecord());
        recs.add(REC11.newRecord());
        recs.add(REC12.newRecord());
        return recs;
    }
}

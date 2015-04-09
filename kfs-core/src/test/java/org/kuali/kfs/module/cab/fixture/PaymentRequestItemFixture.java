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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum PaymentRequestItemFixture {

    REC1 {
        @Override
        public PaymentRequestItem newRecord() {
            PaymentRequestItem obj = new PaymentRequestItem();
            obj.setItemIdentifier(311);
            obj.setPurapDocumentIdentifier(311);
            obj.setItemTypeCode("FRHT");
            obj.setItemUnitPrice(new BigDecimal(750));
            obj.setPurchaseOrderItemUnitPrice(new BigDecimal(750));
            obj.setExtendedPrice(new KualiDecimal(750));
            obj.setItemAssignedToTradeInIndicator(false);
            obj.setCapitalAssetTransactionTypeCode("MDEX");
            return obj;
        };
    },
    REC2 {
        @Override
        public PaymentRequestItem newRecord() {
            PaymentRequestItem obj = new PaymentRequestItem();
            obj.setItemIdentifier(312);
            obj.setPurapDocumentIdentifier(311);
            obj.setItemTypeCode("SPHD");
            obj.setItemUnitPrice(new BigDecimal(250));
            obj.setPurchaseOrderItemUnitPrice(new BigDecimal(250));
            obj.setExtendedPrice(new KualiDecimal(250));
            obj.setItemAssignedToTradeInIndicator(false);
            obj.setCapitalAssetTransactionTypeCode("NEW");
            return obj;
        };
    },
    REC3 {
        @Override
        public PaymentRequestItem newRecord() {
            PaymentRequestItem obj = new PaymentRequestItem();
            obj.setItemIdentifier(313);
            obj.setPurapDocumentIdentifier(311);
            obj.setItemLineNumber(1);
            obj.setItemTypeCode("ITEM");
            obj.setItemDescription("Laptop");
            obj.setItemUnitOfMeasureCode("UN");
            obj.setItemQuantity(new KualiDecimal(3));
            obj.setItemUnitPrice(new BigDecimal(6000));
            obj.setPurchaseOrderItemUnitPrice(new BigDecimal(6000));
            obj.setExtendedPrice(new KualiDecimal(18000));
            obj.setItemAssignedToTradeInIndicator(false);
            return obj;
        };
    },
    REC4 {
        @Override
        public PaymentRequestItem newRecord() {
            PaymentRequestItem obj = new PaymentRequestItem();
            obj.setItemIdentifier(314);
            obj.setPurapDocumentIdentifier(321);
            obj.setItemLineNumber(2);
            obj.setItemTypeCode("ITEM");
            obj.setItemDescription("Desk & Chair");
            obj.setItemUnitOfMeasureCode("PR");
            obj.setItemQuantity(new KualiDecimal(2));
            obj.setItemUnitPrice(new BigDecimal(7000));
            obj.setPurchaseOrderItemUnitPrice(new BigDecimal(7000));
            obj.setExtendedPrice(new KualiDecimal(14000));
            obj.setItemAssignedToTradeInIndicator(true);
            return obj;
        };
    },
    REC5 {
        @Override
        public PaymentRequestItem newRecord() {
            PaymentRequestItem obj = new PaymentRequestItem();
            obj.setItemIdentifier(323);
            obj.setPurapDocumentIdentifier(311);
            obj.setItemTypeCode("TRDI");
            obj.setItemDescription("Trade In Item Description");
            obj.setItemUnitPrice(new BigDecimal(-3000));
            obj.setPurchaseOrderItemUnitPrice(new BigDecimal(-3000));
            obj.setExtendedPrice(new KualiDecimal(-3000));
            obj.setItemAssignedToTradeInIndicator(false);
            return obj;
        };
    },
    REC6 {
        @Override
        public PaymentRequestItem newRecord() {
            PaymentRequestItem obj = new PaymentRequestItem();
            obj.setItemIdentifier(315);
            obj.setPurapDocumentIdentifier(331);
            obj.setItemTypeCode("FRHT");
            obj.setItemUnitPrice(new BigDecimal(750));
            obj.setPurchaseOrderItemUnitPrice(new BigDecimal(750));
            obj.setExtendedPrice(new KualiDecimal(750));
            obj.setItemAssignedToTradeInIndicator(false);
            return obj;
        };
    },
    REC7 {
        @Override
        public PaymentRequestItem newRecord() {
            PaymentRequestItem obj = new PaymentRequestItem();
            obj.setItemIdentifier(316);
            obj.setPurapDocumentIdentifier(331);
            obj.setItemTypeCode("SPHD");
            obj.setItemUnitPrice(new BigDecimal(250));
            obj.setPurchaseOrderItemUnitPrice(new BigDecimal(250));
            obj.setExtendedPrice(new KualiDecimal(250));
            obj.setItemAssignedToTradeInIndicator(false);
            return obj;
        };
    },
    REC8 {
        @Override
        public PaymentRequestItem newRecord() {
            PaymentRequestItem obj = new PaymentRequestItem();
            obj.setItemIdentifier(317);
            obj.setPurapDocumentIdentifier(331);
            obj.setItemLineNumber(1);
            obj.setItemTypeCode("ITEM");
            obj.setItemDescription("Laptop");
            obj.setItemUnitOfMeasureCode("UN");
            obj.setItemQuantity(new KualiDecimal(3));
            obj.setItemUnitPrice(new BigDecimal(6000));
            obj.setPurchaseOrderItemUnitPrice(new BigDecimal(6000));
            obj.setExtendedPrice(new KualiDecimal(18000));
            obj.setItemAssignedToTradeInIndicator(false);
            return obj;
        };
    },
    REC9 {
        @Override
        public PaymentRequestItem newRecord() {
            PaymentRequestItem obj = new PaymentRequestItem();
            obj.setItemIdentifier(318);
            obj.setPurapDocumentIdentifier(341);
            obj.setItemLineNumber(2);
            obj.setItemTypeCode("ITEM");
            obj.setItemDescription("Desk & Chair");
            obj.setItemUnitOfMeasureCode("PR");
            obj.setItemQuantity(new KualiDecimal(2));
            obj.setItemUnitPrice(new BigDecimal(7000));
            obj.setPurchaseOrderItemUnitPrice(new BigDecimal(7000));
            obj.setExtendedPrice(new KualiDecimal(14000));
            obj.setItemAssignedToTradeInIndicator(false);
            return obj;
        };
    },
    REC10 {
        @Override
        public PaymentRequestItem newRecord() {
            PaymentRequestItem obj = new PaymentRequestItem();
            obj.setItemIdentifier(319);
            obj.setPurapDocumentIdentifier(351);
            obj.setItemTypeCode("FRHT");
            obj.setItemUnitPrice(new BigDecimal(750));
            obj.setPurchaseOrderItemUnitPrice(new BigDecimal(750));
            obj.setExtendedPrice(new KualiDecimal(750));
            obj.setItemAssignedToTradeInIndicator(false);
            return obj;
        };
    },
    REC11 {
        @Override
        public PaymentRequestItem newRecord() {
            PaymentRequestItem obj = new PaymentRequestItem();
            obj.setItemIdentifier(320);
            obj.setPurapDocumentIdentifier(351);
            obj.setItemTypeCode("SPHD");
            obj.setItemUnitPrice(new BigDecimal(250));
            obj.setPurchaseOrderItemUnitPrice(new BigDecimal(250));
            obj.setExtendedPrice(new KualiDecimal(250));
            obj.setItemAssignedToTradeInIndicator(false);
            return obj;
        };
    },
    REC12 {
        @Override
        public PaymentRequestItem newRecord() {
            PaymentRequestItem obj = new PaymentRequestItem();
            obj.setItemIdentifier(321);
            obj.setPurapDocumentIdentifier(351);
            obj.setItemLineNumber(1);
            obj.setItemTypeCode("ITEM");
            obj.setItemDescription("Laptop");
            obj.setItemUnitOfMeasureCode("UN");
            obj.setItemQuantity(new KualiDecimal(3));
            obj.setItemUnitPrice(new BigDecimal(6000));
            obj.setPurchaseOrderItemUnitPrice(new BigDecimal(6000));
            obj.setExtendedPrice(new KualiDecimal(18000));
            obj.setItemAssignedToTradeInIndicator(false);
            return obj;
        };
    },
    REC13 {
        @Override
        public PaymentRequestItem newRecord() {
            PaymentRequestItem obj = new PaymentRequestItem();
            obj.setItemIdentifier(322);
            obj.setPurapDocumentIdentifier(361);
            obj.setItemLineNumber(2);
            obj.setItemTypeCode("ITEM");
            obj.setItemDescription("Desk & Chair");
            obj.setItemUnitOfMeasureCode("PR");
            obj.setItemQuantity(new KualiDecimal(2));
            obj.setItemUnitPrice(new BigDecimal(7000));
            obj.setPurchaseOrderItemUnitPrice(new BigDecimal(7000));
            obj.setExtendedPrice(new KualiDecimal(14000));
            obj.setItemAssignedToTradeInIndicator(false);
            return obj;
        };
    };
    public abstract PaymentRequestItem newRecord();

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
        recs.add(REC13.newRecord());
        return recs;
    }
}

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

import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum RequisitionItemFixture {
    REC1 {
        @Override
        public RequisitionItem newRecord() {
            RequisitionItem obj = new RequisitionItem();
            obj.setItemIdentifier(111);
            obj.setPurapDocumentIdentifier(11);
            obj.setItemLineNumber(1);
            obj.setItemUnitOfMeasureCode("UN");
            obj.setItemQuantity(new KualiDecimal(3));
            obj.setItemDescription("Laptop");
            obj.setItemUnitPrice(new BigDecimal(6000));
            obj.setItemRestrictedIndicator(false);
            obj.setItemTypeCode("ITEM");
            obj.setItemAssignedToTradeInIndicator(false);
            return obj;
        };
    },

    REC2 {
        @Override
        public RequisitionItem newRecord() {
            RequisitionItem obj = new RequisitionItem();
            obj.setItemIdentifier(112);
            obj.setPurapDocumentIdentifier(11);
            obj.setItemLineNumber(2);
            obj.setItemUnitOfMeasureCode("PR");
            obj.setItemQuantity(new KualiDecimal(2));
            obj.setItemDescription("Desk & Chair");
            obj.setItemUnitPrice(new BigDecimal(7000));
            obj.setItemRestrictedIndicator(false);
            obj.setItemTypeCode("ITEM");
            obj.setItemAssignedToTradeInIndicator(false);
            return obj;
        };
    },

    REC3 {
        @Override
        public RequisitionItem newRecord() {
            RequisitionItem obj = new RequisitionItem();
            obj.setItemIdentifier(113);
            obj.setPurapDocumentIdentifier(11);
            obj.setItemDescription("USPS");
            obj.setItemUnitPrice(new BigDecimal(750));
            obj.setItemRestrictedIndicator(false);
            obj.setItemTypeCode("FRHT");
            obj.setItemAssignedToTradeInIndicator(false);
            return obj;
        };
    },

    REC4 {
        @Override
        public RequisitionItem newRecord() {
            RequisitionItem obj = new RequisitionItem();
            obj.setItemIdentifier(114);
            obj.setPurapDocumentIdentifier(11);
            obj.setItemDescription("USPS");
            obj.setItemUnitPrice(new BigDecimal(250));
            obj.setItemRestrictedIndicator(false);
            obj.setItemTypeCode("SPHD");
            obj.setItemAssignedToTradeInIndicator(false);
            return obj;
        };
    },

    REC5 {
        @Override
        public RequisitionItem newRecord() {
            RequisitionItem obj = new RequisitionItem();
            obj.setItemIdentifier(121);
            obj.setPurapDocumentIdentifier(12);
            obj.setItemLineNumber(1);
            obj.setItemUnitOfMeasureCode("UN");
            obj.setItemQuantity(new KualiDecimal(3));
            obj.setItemDescription("Laptop");
            obj.setItemUnitPrice(new BigDecimal(6000));
            obj.setItemRestrictedIndicator(false);
            obj.setItemTypeCode("ITEM");
            obj.setItemAssignedToTradeInIndicator(false);
            return obj;
        };
    },

    REC6 {
        @Override
        public RequisitionItem newRecord() {
            RequisitionItem obj = new RequisitionItem();
            obj.setItemIdentifier(122);
            obj.setPurapDocumentIdentifier(12);
            obj.setItemLineNumber(2);
            obj.setItemUnitOfMeasureCode("PR");
            obj.setItemQuantity(new KualiDecimal(2));
            obj.setItemDescription("Desk & Chair");
            obj.setItemUnitPrice(new BigDecimal(7000));
            obj.setItemRestrictedIndicator(false);
            obj.setItemTypeCode("ITEM");
            obj.setItemAssignedToTradeInIndicator(false);
            return obj;
        };
    },

    REC7 {
        @Override
        public RequisitionItem newRecord() {
            RequisitionItem obj = new RequisitionItem();
            obj.setItemIdentifier(123);
            obj.setPurapDocumentIdentifier(12);
            obj.setItemDescription("USPS");
            obj.setItemUnitPrice(new BigDecimal(750));
            obj.setItemRestrictedIndicator(false);
            obj.setItemTypeCode("FRHT");
            obj.setItemAssignedToTradeInIndicator(false);
            return obj;
        };
    },

    REC8 {
        @Override
        public RequisitionItem newRecord() {
            RequisitionItem obj = new RequisitionItem();
            obj.setItemIdentifier(124);
            obj.setPurapDocumentIdentifier(12);
            obj.setItemDescription("USPS");
            obj.setItemUnitPrice(new BigDecimal(250));
            obj.setItemRestrictedIndicator(false);
            obj.setItemTypeCode("SPHD");
            obj.setItemAssignedToTradeInIndicator(false);
            return obj;
        };
    },

    REC9 {
        @Override
        public RequisitionItem newRecord() {
            RequisitionItem obj = new RequisitionItem();
            obj.setItemIdentifier(131);
            obj.setPurapDocumentIdentifier(13);
            obj.setItemLineNumber(1);
            obj.setItemUnitOfMeasureCode("UN");
            obj.setItemQuantity(new KualiDecimal(3));
            obj.setItemDescription("Laptop");
            obj.setItemUnitPrice(new BigDecimal(6000));
            obj.setItemRestrictedIndicator(false);
            obj.setItemTypeCode("ITEM");
            obj.setItemAssignedToTradeInIndicator(false);
            return obj;
        };
    },

    REC10 {
        @Override
        public RequisitionItem newRecord() {
            RequisitionItem obj = new RequisitionItem();
            obj.setItemIdentifier(132);
            obj.setPurapDocumentIdentifier(13);
            obj.setItemLineNumber(2);
            obj.setItemUnitOfMeasureCode("PR");
            obj.setItemQuantity(new KualiDecimal(2));
            obj.setItemDescription("Desk & Chair");
            obj.setItemUnitPrice(new BigDecimal(7000));
            obj.setItemRestrictedIndicator(false);
            obj.setItemTypeCode("ITEM");
            obj.setItemAssignedToTradeInIndicator(false);
            return obj;
        };
    },

    REC11 {
        @Override
        public RequisitionItem newRecord() {
            RequisitionItem obj = new RequisitionItem();
            obj.setItemIdentifier(133);
            obj.setPurapDocumentIdentifier(13);
            obj.setItemDescription("USPS");
            obj.setItemUnitPrice(new BigDecimal(750));
            obj.setItemRestrictedIndicator(false);
            obj.setItemTypeCode("FRHT");
            obj.setItemAssignedToTradeInIndicator(false);
            return obj;
        };
    },

    REC12 {
        @Override
        public RequisitionItem newRecord() {
            RequisitionItem obj = new RequisitionItem();
            obj.setItemIdentifier(134);
            obj.setPurapDocumentIdentifier(13);
            obj.setItemDescription("USPS");
            obj.setItemUnitPrice(new BigDecimal(250));
            obj.setItemRestrictedIndicator(false);
            obj.setItemTypeCode("SPHD");
            obj.setItemAssignedToTradeInIndicator(false);
            return obj;
        };
    };

    public abstract RequisitionItem newRecord();

    public static void setUpData() {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(getAll());
    }

    private static List<RequisitionItem> getAll() {
        List<RequisitionItem> recs = new ArrayList<RequisitionItem>();
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

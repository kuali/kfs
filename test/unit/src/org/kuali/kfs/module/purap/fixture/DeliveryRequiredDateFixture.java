/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.purap.fixture;

import java.sql.Date;

import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;

public enum DeliveryRequiredDateFixture {
    DELIVERY_REQUIRED_EQUALS_CURRENT_DATE(SpringContext.getBean(DateTimeService.class).getCurrentSqlDateMidnight()),
    
    DELIVERY_REQUIRED_BEFORE_CURRENT_DATE(new Date(System.currentTimeMillis()/2)),
    
    DELIVERY_REQUIRED_AFTER_CURRENT_DATE(new Date(System.currentTimeMillis() + 100000))
    
    ;
    
    Date deliveryRequiredDate;
    
    private DeliveryRequiredDateFixture(Date theDate) {
        this.deliveryRequiredDate = theDate;
    }
    
    public PurchasingDocument createRequisitionDocument() {
        PurchasingDocument document = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
        document.setDeliveryRequiredDate(deliveryRequiredDate);
        return document;
    }
    
    public PurchasingDocument createPurchaseOrderDocument() {
        PurchasingDocument document = PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS.createPurchaseOrderDocument();
        document.setDeliveryRequiredDate(deliveryRequiredDate);
        return document;
    }
}

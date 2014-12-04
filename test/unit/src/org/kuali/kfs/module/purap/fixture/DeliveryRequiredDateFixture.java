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

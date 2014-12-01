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

import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;


public enum PurchaseOrderChangeDocumentFixture {

    STATUS_IN_PROCESS(PurchaseOrderStatuses.APPDOC_IN_PROCESS), 
    STATUS_PENDING_PRINT(PurchaseOrderStatuses.APPDOC_PENDING_PRINT), 
    STATUS_OPEN(PurchaseOrderStatuses.APPDOC_OPEN), 
    STATUS_AMENDMENT(PurchaseOrderStatuses.APPDOC_AMENDMENT),
    STATUS_PENDING_PAYMENT_HOLD(PurchaseOrderStatuses.APPDOC_PENDING_PAYMENT_HOLD),
    STATUS_PENDING_REMOVE_HOLD(PurchaseOrderStatuses.APPDOC_PENDING_REMOVE_HOLD),
    STATUS_CLOSED(PurchaseOrderStatuses.APPDOC_CLOSED),
    STATUS_PENDING_CLOSE(PurchaseOrderStatuses.APPDOC_PENDING_CLOSE), 
    STATUS_VOID(PurchaseOrderStatuses.APPDOC_VOID);

    private String status;

    private PurchaseOrderChangeDocumentFixture(String status) {
        this.status = status;
    }

    public PurchaseOrderDocument generatePO() {
        PurchaseOrderDocument po = new PurchaseOrderDocument();
        try {
            po = PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS.createPurchaseOrderDocument();
            po.setApplicationDocumentStatus(status);
            po.refreshNonUpdateableReferences();
        }
        catch (Exception e) {
            throw new RuntimeException("Problems creating new PO: " + e);
        }
        return po;
    }
}

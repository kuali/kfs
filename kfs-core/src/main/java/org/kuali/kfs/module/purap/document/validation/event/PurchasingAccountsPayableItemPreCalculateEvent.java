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
package org.kuali.kfs.module.purap.document.validation.event;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.rice.krad.document.Document;

/**
 * Event Base class for Purchasing Accounts Payable Item
 * 
 * contains the base methods for item events
 */
public class PurchasingAccountsPayableItemPreCalculateEvent extends AttributedDocumentEventBase {
    private static final Logger LOG = Logger.getLogger(PurchasingAccountsPayableItemPreCalculateEvent.class);

    private final PurApItem item;

    /**
     * Copies the item and calls the super constructor
     * 
     * @param description the description of the event
     * @param errorPathPrefix the error path
     * @param document the document the event is being called on
     * @param item the item that is having the event called on
     */
    public PurchasingAccountsPayableItemPreCalculateEvent(Document document, PurApItem item) {
        super("PurAP Document Item PreCalculate Check for " + getDocumentId(document), "", document);
        this.item = item;
    }

    /**
     * 
     * @see org.kuali.kfs.module.purap.document.validation.event.PurchasingAccountsPayableItemEvent#getItem()
     */
    public PurApItem getItem() {
        return item;
    }
}

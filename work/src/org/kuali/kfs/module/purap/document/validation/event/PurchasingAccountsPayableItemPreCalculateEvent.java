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

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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.rice.krad.document.Document;

/**
 * Event Base class for Purchasing Accounts Payable Item
 * 
 * contains the base methods for item events
 */
public abstract class AttributedPurchasingAccountsPayableItemEventBase extends AttributedDocumentEventBase implements AttributedPurchasingAccountsPayableItemEvent {
    private static final Logger LOG = Logger.getLogger(AttributedPurchasingAccountsPayableItemEventBase.class);


    private final PurApItem item;

    /**
     * Copies the item and calls the super constructor
     * 
     * @param description the description of the event
     * @param errorPathPrefix the error path
     * @param document the document the event is being called on
     * @param item the item that is having the event called on
     */
    public AttributedPurchasingAccountsPayableItemEventBase(String description, String errorPathPrefix, Document document, PurApItem item) {
        super(description, errorPathPrefix, document);

        this.item = item;

        logEvent();
    }

    /**
     * 
     * @see org.kuali.kfs.module.purap.document.validation.event.PurchasingAccountsPayableItemEvent#getItem()
     */
    public PurApItem getItem() {
        return item;
    }


    /**
     * @see org.kuali.rice.krad.rule.event.KualiDocumentEvent#validate()
     */
    public void validate() {
        super.validate();
        if (getItem() == null) {
            throw new IllegalArgumentException("invalid (null) item");
        }
    }

    /**
     * Logs the event type and some information about the associated item
     */
    private void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
        logMessage.append(" with ");

        // vary logging detail as needed
        if (item == null) {
            logMessage.append("null item");
        }
        else {
            logMessage.append(" item# ");
            logMessage.append(item.getItemIdentifier());
        }

        LOG.debug(logMessage);
    }
}

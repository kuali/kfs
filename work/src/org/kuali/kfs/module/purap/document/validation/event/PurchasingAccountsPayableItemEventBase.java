/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.purap.rule.event;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.core.document.Document;
import org.kuali.core.rule.event.KualiDocumentEventBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.financial.bo.Check;
import org.kuali.module.purap.bo.PurchasingApItem;

/**
 * ItemEvents.
 * 
 * 
 */
public abstract class PurchasingAccountsPayableItemEventBase extends KualiDocumentEventBase implements PurchasingAccountsPayableItemEvent {
    private static final Logger LOG = Logger.getLogger(PurchasingAccountsPayableItemEventBase.class);


    private final PurchasingApItem item;

    /**
     * Initializes fields common to all subclasses
     * 
     * @param description
     * @param errorPathPrefix
     * @param document
     * @param check
     */
    public PurchasingAccountsPayableItemEventBase(String description, String errorPathPrefix, Document document, PurchasingApItem item) {
        super(description, errorPathPrefix, document);

        // by doing a deep copy, we are ensuring that the business rule class can't update
        // the original object by reference
        this.item = (PurchasingApItem) ObjectUtils.deepCopy(item);

        logEvent();
    }


    /**
     * @see org.kuali.core.rule.event.CheckEvent#getCheck()
     */
    public PurchasingApItem getItem() {
        return item;
    }


    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#validate()
     */
    public void validate() {
        super.validate();
        if (getItem() == null) {
            throw new IllegalArgumentException("invalid (null) item");
        }
    }

    /**
     * Logs the event type and some information about the associated accountingLine
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
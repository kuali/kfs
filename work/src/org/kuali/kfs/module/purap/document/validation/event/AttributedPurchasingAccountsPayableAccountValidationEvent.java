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
package org.kuali.kfs.module.purap.document.validation.event;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.rice.kns.document.Document;

/**
 * Event class for Purchasing Accounts Payable Account validation
 * 
 */
public final class AttributedPurchasingAccountsPayableAccountValidationEvent extends AttributedDocumentEventBase {

    private final PurApItem item;

    /**
     * Copies the item and calls the super constructor
     * 
     * @param description the description of the event
     * @param errorPathPrefix the error path
     * @param document the document the event is being called on
     * @param item the item that is having the event called on
     */
    public AttributedPurchasingAccountsPayableAccountValidationEvent(String description, String errorPathPrefix, Document document, PurApItem item) {
        super(description, errorPathPrefix, document);

        this.item = item;     
    }

    public PurApItem getItem() {
        return item;
    }

    /**
     * @see org.kuali.rice.kns.rule.event.KualiDocumentEventBase#validate()
     */
    public void validate() {
        super.validate();
        if (getItem() == null) {
            throw new IllegalArgumentException("invalid (null) item");
        }
    }
}

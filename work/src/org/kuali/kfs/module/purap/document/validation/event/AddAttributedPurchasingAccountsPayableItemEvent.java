/*
 * Copyright 2008 The Kuali Foundation.
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

import org.kuali.core.document.Document;
import org.kuali.kfs.rule.event.AttributedDocumentEventBase;
import org.kuali.module.purap.bo.PurApItem;

/**
 *
 * This class...
 */
public class AddAttributedPurchasingAccountsPayableItemEvent extends AttributedDocumentEventBase {
    private PurApItem item;
    
    /**
     * Constructs an AddItemEvent with the given errorPathPrefix, document, and item.
     *
     * @param errorPathPrefix the error path
     * @param document document the event was invoked on
     * @param item the item being added
     */
    public AddAttributedPurchasingAccountsPayableItemEvent(String errorPathPrefix, Document document, PurApItem item) {
        super("adding item to document " + getDocumentId(document), errorPathPrefix, document);
        this.item = item;
    }

    /**
     * Gets the item attribute.
     * @return Returns the item.
     */
    public PurApItem getItem() {
        return item;
    }
}

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

import org.kuali.kfs.integration.purap.PurApItem;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.rice.kns.document.Document;

/**
 * Add Purchasing Accounts Payble Item Event. 
 * This is triggered when a user presses the add button for a given document's item line.
 */
public final class AttributedAddPurchasingAccountsPayableItemEvent extends AttributedDocumentEventBase {
   
    private PurApItem item;
    
    public AttributedAddPurchasingAccountsPayableItemEvent(Document document, PurApItem item) {
        super("adding item to document " + getDocumentId(document), null, document);
        this.document = document;
        this.item = item;
    }

    public PurApItem getItem(){
        return this.item;
    }
}

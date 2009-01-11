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
package org.kuali.kfs.module.purap.document.validation.event;

import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.validation.ImportPurchasingAccountsPayableItemRule;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rule.BusinessRule;

/**
 * Import Purchasing Accounts Payble Item Event. 
 * This is triggered when a user presses the import button on a purchasing document, 
 * and then clicks the add button after selecting a file to import items from.
 */
public class AttributedImportPurchasingAccountsPayableItemEvent extends AttributedDocumentEventBase {

    private PurApItem item;
    
    public AttributedImportPurchasingAccountsPayableItemEvent(Document document, PurApItem item) {
        super("importing item to document " + getDocumentId(document), null, document);
        this.document = document;
        this.item = item;
    }

    public PurApItem getItem(){
        return this.item;
    }
}

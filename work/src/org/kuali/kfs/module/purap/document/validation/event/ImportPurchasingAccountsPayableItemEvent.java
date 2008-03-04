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
import org.kuali.core.rule.BusinessRule;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.rule.ImportPurchasingAccountsPayableItemRule;

/**
 * Import Purchasing Accounts Payble Item Event. 
 * This is triggered when a user presses the import button on a purchasing document, 
 * and then clicks the add button after selecting a file to import items from.
 */
public class ImportPurchasingAccountsPayableItemEvent extends PurchasingAccountsPayableItemEventBase {
    /**
     * Constructs an ImportItemEvent with the given errorPathPrefix, document, and item.
     * 
     * @param errorPathPrefix the error path
     * @param document document the event was invoked on
     * @param item the item being added 
     */
    public ImportPurchasingAccountsPayableItemEvent(String errorPathPrefix, Document document, PurApItem item) {
        super("importing item to document " + getDocumentId(document), errorPathPrefix, document, item);
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class getRuleInterfaceClass() {
        return ImportPurchasingAccountsPayableItemRule.class;
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.core.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((ImportPurchasingAccountsPayableItemRule) rule).processImportItemBusinessRules((AccountingDocument) getDocument(), getItem());
    }

}

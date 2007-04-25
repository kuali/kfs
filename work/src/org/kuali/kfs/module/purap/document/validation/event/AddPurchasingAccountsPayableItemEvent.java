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

import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.BusinessRule;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.financial.bo.Check;
import org.kuali.module.financial.rule.AddCheckRule;
import org.kuali.module.purap.bo.PurchasingApItem;
import org.kuali.module.purap.rule.AddPurchasingAccountsPayableItemRule;

/**
 * This class represents the add item event. This could be triggered when a user presses the add button for a given document's
 * item line.
 */
public final class AddPurchasingAccountsPayableItemEvent extends PurchasingAccountsPayableItemEventBase {
    /**
     * Constructs an AddItemEvent with the given errorPathPrefix, document, and item.
     * 
     * @param errorPathPrefix
     * @param document
     * @param accountingLine
     */
    public AddPurchasingAccountsPayableItemEvent(String errorPathPrefix, Document document, PurchasingApItem item) {
        super("adding item to document " + getDocumentId(document), errorPathPrefix, document, item);
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class getRuleInterfaceClass() {
        return AddPurchasingAccountsPayableItemRule.class;
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.core.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AddPurchasingAccountsPayableItemRule) rule).processAddItemBusinessRules((AccountingDocument) getDocument(), getItem());
    }
}
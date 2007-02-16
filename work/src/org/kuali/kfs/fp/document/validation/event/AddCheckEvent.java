/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.financial.rule.event;

import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.BusinessRule;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.financial.bo.Check;
import org.kuali.module.financial.rule.AddCheckRule;

/**
 * This class represents the add check event. This could be triggered when a user presses the add button for a given document's
 * check line.
 */
public final class AddCheckEvent extends CheckEventBase {
    /**
     * Constructs an AddCheckEvent with the given errorPathPrefix, document, and accountingLine.
     * 
     * @param errorPathPrefix
     * @param document
     * @param accountingLine
     */
    public AddCheckEvent(String errorPathPrefix, Document document, Check check) {
        super("adding check to document " + getDocumentId(document), errorPathPrefix, document, check);
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class getRuleInterfaceClass() {
        return AddCheckRule.class;
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.core.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AddCheckRule) rule).processAddCheckBusinessRules((AccountingDocument) getDocument(), getCheck());
    }
}
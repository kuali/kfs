/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.validation.event;

import org.kuali.kfs.module.endow.businessobject.EndowmentAccountingLine;
import org.kuali.kfs.module.endow.document.EndowmentAccountingLinesDocument;
import org.kuali.kfs.module.endow.document.validation.DeleteEndowmentAccountingLineRule;
import org.kuali.rice.kns.rule.BusinessRule;
import org.kuali.rice.kns.rule.event.KualiDocumentEventBase;

public class DeleteAccountingLineEvent extends KualiDocumentEventBase {
    private EndowmentAccountingLine line;

    /**
     * @param description
     * @param errorPathPrefix
     * @param document
     */
    public DeleteAccountingLineEvent(String errorPathPrefix, EndowmentAccountingLinesDocument document, EndowmentAccountingLine line) {
        super("Deleting Accounting Line from document " + getDocumentId(document), errorPathPrefix, document);
        this.document = document;
        this.line = line;
    }

    /**
     * @see org.kuali.rice.kns.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return DeleteEndowmentAccountingLineRule.class;
    }

    /**
     * @see org.kuali.rice.kns.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.rice.kns.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((DeleteEndowmentAccountingLineRule<EndowmentAccountingLinesDocument, EndowmentAccountingLine>) rule).processDeleteAccountingLineRules((EndowmentAccountingLinesDocument) document, line);
    }
}

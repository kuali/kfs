/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.validation.event;

import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.validation.RefreshTransactionLineRule;
import org.kuali.rice.krad.rules.rule.BusinessRule;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEventBase;

public class RefreshTransactionLineEvent extends KualiDocumentEventBase {
    private EndowmentTransactionLine line;
    private int index;

    /**
     * Constructs a RefreshTransactionLineEvent.java.
     * 
     * @param errorPathPrefix
     * @param document
     * @param line
     */
    public RefreshTransactionLineEvent(String errorPathPrefix, EndowmentTransactionLinesDocument document, EndowmentTransactionLine line, int index) {
        super("Refresh Transaction Line for document " + getDocumentId(document), errorPathPrefix, document);
        this.document = document;
        this.line = line;
        this.index = index;
    }

    /**
     * @see org.kuali.rice.krad.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {

        return RefreshTransactionLineRule.class;
    }

    /**
     * @see org.kuali.rice.krad.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.rice.krad.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((RefreshTransactionLineRule<EndowmentTransactionLinesDocument, EndowmentTransactionLine, Number>) rule).processRefreshTransactionLineRules((EndowmentTransactionLinesDocument) document, line, index);
    }

}

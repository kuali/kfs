/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

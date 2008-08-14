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
package org.kuali.kfs.sys.document.validation.event;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.DeleteAccountingLineRule;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rule.BusinessRule;

/**
 * This class represents the delete accounting line event. This could be triggered when a user presses the delete button for a given
 * document's accounting line.
 */
public final class DeleteAccountingLineEvent extends AccountingLineEventBase {

    private final boolean lineWasAlreadyDeletedFromDocument;

    /**
     * Constructs a DeleteAccountingLineEvent with the given errorPathPrefix, document, and accountingLine.
     * 
     * @param errorPathPrefix
     * @param document
     * @param accountingLine
     */
    public DeleteAccountingLineEvent(String errorPathPrefix, Document document, AccountingLine accountingLine, boolean lineWasAlreadyDeletedFromDocument) {
        super("deleting accountingLine from document " + getDocumentId(document), errorPathPrefix, document, accountingLine);
        this.lineWasAlreadyDeletedFromDocument = lineWasAlreadyDeletedFromDocument;
    }

    /**
     * @see org.kuali.rice.kns.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class getRuleInterfaceClass() {
        return DeleteAccountingLineRule.class;
    }

    /**
     * @see org.kuali.rice.kns.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.rice.kns.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((DeleteAccountingLineRule) rule).processDeleteAccountingLineBusinessRules((AccountingDocument) getDocument(), getAccountingLine(), lineWasAlreadyDeletedFromDocument);
    }
}

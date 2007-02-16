/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.kfs.rule.event;

import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.BusinessRule;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rule.UpdateAccountingLineRule;

/**
 * This class represents the update accounting line event. This could be triggered when a user changes one or more values in an
 * existing accounting line.
 */
public final class UpdateAccountingLineEvent extends AccountingLineEventBase {
    private final AccountingLine updatedAccountingLine;

    /**
     * Constructs an UpdateAccountingLineEvent with the given errorPathPrefix, document, and accountingLine.
     * 
     * @param errorPathPrefix
     * @param document
     * @param accountingLine
     * @param newAccountingLine
     */
    public UpdateAccountingLineEvent(String errorPathPrefix, Document document, AccountingLine originalAccountingLine, AccountingLine updatedAccountingLine) {
        super("updating accountingLine in document " + getDocumentId(document), errorPathPrefix, document, originalAccountingLine);

        this.updatedAccountingLine = (AccountingLine) ObjectUtils.deepCopy(updatedAccountingLine);
    }


    /**
     * @return updated accountingLine associated with this event
     */
    public AccountingLine getUpdatedAccountingLine() {
        return updatedAccountingLine;
    }


    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class getRuleInterfaceClass() {
        return UpdateAccountingLineRule.class;
    }


    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.core.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((UpdateAccountingLineRule) rule).processUpdateAccountingLineBusinessRules((AccountingDocument) getDocument(), getAccountingLine(), getUpdatedAccountingLine());
    }


    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#validate()
     */
    public void validate() {
        super.validate();
        if (getUpdatedAccountingLine() == null) {
            throw new IllegalArgumentException("invalid (null) updated accounting line");
        }
    }
}
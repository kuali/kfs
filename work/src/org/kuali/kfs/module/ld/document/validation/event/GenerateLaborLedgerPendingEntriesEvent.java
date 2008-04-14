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
package org.kuali.module.labor.rule.event;

import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.BusinessRule;
import org.kuali.core.rule.event.KualiDocumentEventBase;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.module.labor.document.LaborLedgerPostingDocument;
import org.kuali.module.labor.rule.GenerateLaborLedgerPendingEntriesRule;

/**
 * Event used to re/generate general ledger pending entries for a transactional document.
 */
public final class GenerateLaborLedgerPendingEntriesEvent extends KualiDocumentEventBase {
    private final AccountingLine accountingLine;
    private GeneralLedgerPendingEntrySequenceHelper sequenceHelper;

    /**
     * Constructs a GenerateGeneralLedgerPendingEntriesEvent with the given errorPathPrefix, document, accountingLine, and counter
     * 
     * @param errorPathPrefix
     * @param generalLedgerPostingDocument
     * @param accountingLine
     * @param sequenceHelper
     */
    public GenerateLaborLedgerPendingEntriesEvent(String errorPathPrefix, LaborLedgerPostingDocument accountingDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        super("creating generatePendingEntries event for document " + getDocumentId(accountingDocument), errorPathPrefix, accountingDocument);

        this.accountingLine = accountingLine;
        this.sequenceHelper = sequenceHelper;
    }

    /**
     * Constructs a GenerateGeneralLedgerPendingEntriesEvent with the given document and accountingLine
     * 
     * @param generalLedgerPostingDocument
     * @param accountingLine
     * @param sequenceHelper
     */
    public GenerateLaborLedgerPendingEntriesEvent(LaborLedgerPostingDocument accountingDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        this("", accountingDocument, accountingLine, sequenceHelper);
    }

    /**
     * Gets the transactionalDocument attribute.
     * 
     * @return TransactionalDocument associated with this event
     */
    public TransactionalDocument getTransactionalDocument() {
        return (TransactionalDocument) getDocument();
    }

    /**
     * Gets the accountingLine attribute.
     * 
     * @return accountingLine associated with this event
     */
    public AccountingLine getAccountingLine() {
        return accountingLine;
    }

    /**
     * Gets the sequenceHelper.
     * 
     * @return sequenceHelper associated with this event
     */
    public GeneralLedgerPendingEntrySequenceHelper getSequenceHelper() {
        return sequenceHelper;
    }

    /**
     * Sets the value of the sequenceHelper.
     * 
     * @param sequenceHelper
     */
    public void setSequenceHelper(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        this.sequenceHelper = sequenceHelper;
    }

    public void validate() {
        super.validate();
        if (getAccountingLine() == null) {
            throw new IllegalArgumentException("invalid (null) accounting line");
        }
    }

    /**
     * Gets the GenerateLaborLedgerPendingEntriesRule.
     * 
     * @return GenerateLaborLedgerPendingEntriesRule class
     */
    public Class getRuleInterfaceClass() {
        return GenerateLaborLedgerPendingEntriesRule.class;
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.core.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((GenerateLaborLedgerPendingEntriesRule) rule).processGenerateLaborLedgerPendingEntries((LaborLedgerPostingDocument) getDocument(), getAccountingLine(), getSequenceHelper());
    }
}

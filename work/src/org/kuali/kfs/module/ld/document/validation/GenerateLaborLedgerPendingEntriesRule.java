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
package org.kuali.module.labor.rule;

import org.kuali.core.rule.BusinessRule;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.document.AccountingDocument;

/**
 * Defines a rule which gets invoked whenever pending entries for a document need to be re/generated (which is usually when the
 * document gets persisted).
 */
public interface GenerateLaborLedgerPendingEntriesRule<G extends AccountingDocument> extends BusinessRule {
    /**
     * @param generalLedgerPostingDocument
     * @return boolean If the GLPE generation processed successfully.
     */
    public boolean processGenerateLaborLedgerPendingEntries(G AccountingDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper);
}

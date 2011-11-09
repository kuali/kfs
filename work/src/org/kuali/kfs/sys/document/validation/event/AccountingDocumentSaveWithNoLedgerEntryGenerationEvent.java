/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.sys.document.validation.event;

import org.kuali.rice.krad.document.Document;

/**
 * An event to save an accounting document, which AccountingDocumentBase will *not* generate
 * general ledger pending entries for
 * 
 * @see org.kuali.kfs.sys.document.AccountingDocumentBase#prepareForSave
 */
public class AccountingDocumentSaveWithNoLedgerEntryGenerationEvent extends DocumentSystemSaveEvent {
    /**
     * Constructs a AccountingDocumentSaveWithNoLedgerEntryGenerationEvent with the specified errorPathPrefix and document. Event will
     * 
     * @param document the document that the rules for this event should be applied to
     * @param errorPathPrefix the error path for any resultant errors to be applied to
     */
    public AccountingDocumentSaveWithNoLedgerEntryGenerationEvent(String errorPathPrefix, Document document) {
        super("creating unvalidated save event for document " + errorPathPrefix, document);
    }
}

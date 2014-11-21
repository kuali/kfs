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

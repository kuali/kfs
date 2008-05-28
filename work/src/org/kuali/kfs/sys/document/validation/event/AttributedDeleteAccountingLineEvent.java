/*
 * Copyright 2008 The Kuali Foundation.
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
import org.kuali.kfs.bo.AccountingLine;

public class AttributedDeleteAccountingLineEvent extends AttributedDocumentEventBase {
    private final AccountingLine accountingLine;
    private final boolean lineWasAlreadyDeletedFromDocument;
    
    /**
     * Constructs a DeleteAccountingLineEvent with the given errorPathPrefix, document, and accountingLine.
     * 
     * @param errorPathPrefix
     * @param document
     * @param accountingLine
     */
    public AttributedDeleteAccountingLineEvent(String errorPathPrefix, Document document, AccountingLine accountingLine, boolean lineWasAlreadyDeletedFromDocument) {
        super("deleting accountingLine from document " + getDocumentId(document), errorPathPrefix, document);
        this.accountingLine = accountingLine;
        this.lineWasAlreadyDeletedFromDocument = lineWasAlreadyDeletedFromDocument;
    }


    /**
     * @see org.kuali.core.rule.event.AccountingLineEvent#getAccountingLine()
     */
    public AccountingLine getAccountingLine() {
        return accountingLine;
    }

    /**
     * Gets the lineWasAlreadyDeletedFromDocument attribute. 
     * @return Returns the lineWasAlreadyDeletedFromDocument.
     */
    public boolean isLineWasAlreadyDeletedFromDocument() {
        return lineWasAlreadyDeletedFromDocument;
    }
}

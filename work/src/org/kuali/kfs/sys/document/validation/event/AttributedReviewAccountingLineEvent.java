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

public class AttributedReviewAccountingLineEvent extends AttributedDocumentEventBase {
    private AccountingLine accountingLine;
    
    /**
     * Constructs an ReviewAccountingLineEvent with the given errorPathPrefix, document, and accountingLine.
     * 
     * @param errorPathPrefix
     * @param document
     * @param accountingLine
     */
    public AttributedReviewAccountingLineEvent(String errorPathPrefix, Document document, AccountingLine accountingLine) {
        super("reviewing accountingLine in document " + getDocumentId(document), errorPathPrefix, document);
        this.accountingLine = accountingLine;
    }
    
    /**
     * @see org.kuali.core.rule.event.AccountingLineEvent#getAccountingLine()
     */
    public AccountingLine getAccountingLine() {
        return accountingLine;
    }
}

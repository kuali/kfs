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
package org.kuali.kfs.rule;

import org.kuali.core.document.TransactionalDocument;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.document.AccountingDocument;

/**
 * Defines a rule which gets invoked immediately before an accounting line is reviewed for a document.
 */
public interface ReviewAccountingLineRule<F extends AccountingDocument> extends AccountingLineRule {
    /**
     * This method should be leveraged by a transactional document, when the business rules that need to be implemented do not fit
     * into any of the other finer grained business rule checking methods that are defined in this interface.
     * 
     * @param accountingLine
     * @param financialDocument
     * @return True if the business rules pass, false otherwise.
     */
    public boolean processReviewAccountingLineBusinessRules(F financialDocument, AccountingLine accountingLine);
}
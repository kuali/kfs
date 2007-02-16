/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.financial.rule;

import org.kuali.core.document.TransactionalDocument;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.financial.bo.Check;

/**
 * Defines a rule which gets invoked immediately before a check line is added to a document.
 */
public interface AddCheckRule<F extends AccountingDocument> extends CheckRule {
    /**
     * @param check
     * @param financialDocument
     * @return true if the business rules pass
     */
    public boolean processAddCheckBusinessRules(F financialDocument, Check check);
}
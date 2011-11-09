/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.document.validation.impl;

import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validation which checks a one-sided accounting document (ie, an accounting document which only uses source accounting lines, not target)
 * has a required number of accounting lines.
 */
public class OneSidedRequiredAccountingLinesCountValidation extends GenericValidation {
    private AccountingDocument accountingDocumentForValidation;
    private int requiredMinimumCount = 1; // default - you need at least one accounting line

    /**
     * Validates that the accountingDocumentForValidation has at least the requiredMinimumCount accounting lines
     * in its sourceAccountingLines (yep, it's assumed that one-sided accounting docs *always* use source...isn't that dumb?)
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        if (getAccountingDocumentForValidation().getSourceAccountingLines().size() < getRequiredMinimumCount()) {
            GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_LINES_NO_SINGLE_SECTION_ACCOUNTING_LINES);
            return false;
        }
        return true;
    }

    /**
     * Gets the accountingDocumentForValdation attribute. 
     * @return Returns the accountingDocumentForValdation.
     */
    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Sets the accountingDocumentForValdation attribute value.
     * @param accountingDocumentForValdation The accountingDocumentForValdation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

    /**
     * Gets the requiredMinimumCount attribute. 
     * @return Returns the requiredMinimumCount.
     */
    public int getRequiredMinimumCount() {
        return requiredMinimumCount;
    }

    /**
     * Sets the requiredMinimumCount attribute value.
     * @param requiredMinimumCount The requiredMinimumCount to set.
     */
    public void setRequiredMinimumCount(int requiredCount) {
        this.requiredMinimumCount = requiredCount;
    }
}

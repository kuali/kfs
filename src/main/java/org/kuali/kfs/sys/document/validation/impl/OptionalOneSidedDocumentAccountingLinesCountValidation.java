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
package org.kuali.kfs.sys.document.validation.impl;

import static org.kuali.kfs.sys.KFSConstants.ACCOUNTING_LINE_ERRORS;
import static org.kuali.kfs.sys.KFSKeyConstants.ERROR_DOCUMENT_OPTIONAL_ONE_SIDED_DOCUMENT_REQUIRED_NUMBER_OF_ACCOUNTING_LINES_NOT_MET;

import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validation which checks a one-sided accounting document (ie, an accounting document which only uses source accounting lines, not target)
 * has a required number of accounting lines.
 */
public class OptionalOneSidedDocumentAccountingLinesCountValidation extends GenericValidation {
    private AccountingDocument accountingDocumentForValidation;

    /**
     * Some double-sided documents also allow for one sided entries for correcting - so if one side is empty, the other side must
     * have at least two lines in it. The balancing rules take care of validation of amounts.
     * 
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        int sourceSectionSize = getAccountingDocumentForValidation().getSourceAccountingLines().size();
        int targetSectionSize = getAccountingDocumentForValidation().getTargetAccountingLines().size();

        if ((sourceSectionSize == 0 && targetSectionSize < 2) || (targetSectionSize == 0 && sourceSectionSize < 2)) {
            GlobalVariables.getMessageMap().putError(ACCOUNTING_LINE_ERRORS, ERROR_DOCUMENT_OPTIONAL_ONE_SIDED_DOCUMENT_REQUIRED_NUMBER_OF_ACCOUNTING_LINES_NOT_MET);

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

    
}

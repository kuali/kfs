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
package org.kuali.kfs.module.tem.document.validation.impl;

import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.document.TEMReimbursementDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Accounting line count validation for TEM Reimbursement docs (TR, ENT, and RELO).  We can't use the normal one-sided validation here
 * because there are cases where a TEM Reimbursement document is not required to have an accounting line.
 */
public class TEMReimbursementRequiredAccountingLinesCountValidation extends GenericValidation {

    /**
     * Validates the document to make sure that under conditions when it should have an accounting line (basically, any reimbursable expense),
     * it has one...
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean validated = true;
        final TEMReimbursementDocument reimbursableDocument = (TEMReimbursementDocument) event.getDocument();

        if (reimbursableDocument.hasReimbursableExpenses() && !reimbursableDocument.hasOnlyPrepaidExpenses()) {
            if (reimbursableDocument.getSourceAccountingLines() == null || reimbursableDocument.getSourceAccountingLines().isEmpty()) {
                validated = false; // there's no accounting lines even though we have reimbursable expenses
                GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_LINES_NO_SINGLE_SECTION_ACCOUNTING_LINES);
            }
        } else {
            // we need to see if the traveler has a default chart and account...
            if (StringUtils.isBlank(reimbursableDocument.getTemProfile().getDefaultChartCode()) || StringUtils.isBlank(reimbursableDocument.getTemProfile().getDefaultAccount())) {
                validated = false;
                GlobalVariables.getMessageMap().putError(KFSConstants.NEW_SOURCE_LINE_ERRORS, TemKeyConstants.ERROR_REIMBURSABLE_NOT_COMPLETE_FOR_NO_REIMBURSEMENT);
            }
        }

        return validated;
    }

}

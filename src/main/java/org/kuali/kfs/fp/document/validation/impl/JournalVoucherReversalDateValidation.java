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
package org.kuali.kfs.fp.document.validation.impl;

import static org.kuali.kfs.sys.KFSPropertyConstants.REVERSAL_DATE;
import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import org.kuali.kfs.fp.document.JournalVoucherDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * A validation of the reversal date on a Journal Voucher document
 */
public class JournalVoucherReversalDateValidation extends GenericValidation {
    private JournalVoucherDocument journalVoucherForValidation;

    /**
     * Checks that if the reveral date for the document is not null, that the reversal date has not already occurred
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        java.sql.Date today = SpringContext.getBean(DateTimeService.class).getCurrentSqlDateMidnight();
        if (null != getJournalVoucherForValidation().getReversalDate() && getJournalVoucherForValidation().getReversalDate().before(today)) {
            GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + REVERSAL_DATE, KFSKeyConstants.ERROR_DOCUMENT_INCORRECT_REVERSAL_DATE);
            return false;
        }
        return true;
    }

    /**
     * Gets the journalVoucherForValidation attribute. 
     * @return Returns the journalVoucherForValidation.
     */
    public JournalVoucherDocument getJournalVoucherForValidation() {
        return journalVoucherForValidation;
    }

    /**
     * Sets the journalVoucherForValidation attribute value.
     * @param journalVoucherForValidation The journalVoucherForValidation to set.
     */
    public void setJournalVoucherForValidation(JournalVoucherDocument journalVoucherForDocument) {
        this.journalVoucherForValidation = journalVoucherForDocument;
    }
}

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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

public class DisbursementVoucherDocumentFieldValidation extends GenericValidation {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherDocumentFieldValidation.class);

    private AccountingDocument accountingDocumentForValidation;

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        LOG.debug("validate start");
        boolean isValid = true;

        DisbursementVoucherDocument document = (DisbursementVoucherDocument) accountingDocumentForValidation;

        MessageMap errors = GlobalVariables.getMessageMap();

        // validate document required fields
        SpringContext.getBean(DictionaryValidationService.class).validateDocument(document);

        // validate payee fields
        errors.addToErrorPath(KFSPropertyConstants.DOCUMENT);
        errors.addToErrorPath(KFSPropertyConstants.DV_PAYEE_DETAIL);
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(document.getDvPayeeDetail());
        errors.removeFromErrorPath(KFSPropertyConstants.DV_PAYEE_DETAIL);
        errors.removeFromErrorPath(KFSPropertyConstants.DOCUMENT);

        //hasErrors() returns true if it not empty else false.  
        if (errors.hasErrors()) {
            return false;
        }

        /* special handling name & address required if special handling is indicated */
        if (document.isDisbVchrSpecialHandlingCode()) {
            if (StringUtils.isBlank(document.getDvPayeeDetail().getDisbVchrSpecialHandlingPersonName()) || StringUtils.isBlank(document.getDvPayeeDetail().getDisbVchrSpecialHandlingLine1Addr())) {
                errors.putErrorWithoutFullErrorPath(KFSConstants.GENERAL_SPECHAND_TAB_ERRORS, KFSKeyConstants.ERROR_SPECIAL_HANDLING);
                isValid = false;
            }
        }

        boolean hasNoNotes = this.hasNoNotes(document);

        /* if no documentation is selected, must be a note explaining why */
        if (DisbursementVoucherConstants.NO_DOCUMENTATION_LOCATION.equals(document.getDisbursementVoucherDocumentationLocationCode()) && hasNoNotes) {
            errors.putError(KFSPropertyConstants.DISBURSEMENT_VOUCHER_DOCUMENTATION_LOCATION_CODE, KFSKeyConstants.ERROR_DV_NO_DOCUMENTATION_NOTE_MISSING);
            isValid = false;
        }

        /* if special handling indicated, must be a note explaining why */
        if (document.isDisbVchrSpecialHandlingCode() && hasNoNotes) {
            errors.putErrorWithoutFullErrorPath(KFSConstants.GENERAL_PAYMENT_TAB_ERRORS, KFSKeyConstants.ERROR_SPECIAL_HANDLING_NOTE_MISSING);
            isValid = false;
        }

        /* if exception attached indicated, must be a note explaining why */
        if (document.isExceptionIndicator() && hasNoNotes) {
            errors.putErrorWithoutFullErrorPath(KFSConstants.GENERAL_PAYMENT_TAB_ERRORS, KFSKeyConstants.ERROR_EXCEPTION_ATTACHED_NOTE_MISSING);
            isValid = false;
        }

        return isValid;
    }

    /**
     * Return true if disbursement voucher does not have any notes
     * 
     * @param document submitted disbursement voucher document
     * @return whether the given document has no notes
     */
    protected boolean hasNoNotes(DisbursementVoucherDocument document) {
        List<Note> notes = document.getNotes();

        return (notes == null || notes.isEmpty());
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * 
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

    /**
     * Gets the accountingDocumentForValidation attribute.
     * 
     * @return Returns the accountingDocumentForValidation.
     */
    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }
}

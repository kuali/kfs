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

import org.kuali.kfs.fp.document.PreEncumbranceDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validates that an accounting line does not have a capital object object code 
 */
public class PreEncumbranceRequiredAccountingLinesCountValidation extends GenericValidation {
    private PreEncumbranceDocument accountingDocumentForValidation;

    /**
     * Validates that an accounting line does not have a capital object object code
     * <strong>Expects an accounting line as the first a parameter</strong>
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        List sourceAccountingLineList = this.accountingDocumentForValidation.getSourceAccountingLines();
        List targetAccountingLineList = this.accountingDocumentForValidation.getTargetAccountingLines();
        if (sourceAccountingLineList.size() == 0 && targetAccountingLineList.size() == 0 ) {
            GlobalVariables.getMessageMap().putError("sourceAccountingLines", KFSKeyConstants.ERROR_DOCUMENT_PRE_ENCUMBRANCE_SINGLE_SECTION_NO_ACCOUNTING_LINES, new String[] { null });
            return false;
        }
        
        return true;
    }
    
    /**
     * Gets the accountingDocumentForValidation attribute. 
     * @return Returns the accountingDocumentForValidation.
     */
    public PreEncumbranceDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(PreEncumbranceDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

    
   
}

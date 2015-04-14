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
package org.kuali.kfs.module.ld.document.validation.impl;

import java.util.Map;

import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.document.LaborExpenseTransferDocumentBase;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * target accounting lines must have the same amounts as source accounting lines for each object code in the document
 * 
 * @param document the given document
 * @return true if target accounting lines have the same amounts as source accounting lines for each object code; otherwise, false
 */
public class LaborExpenseTransferValidAmountTransferredByObjectCodeValidation extends GenericValidation {    
    private Document documentForValidation;  
    
    /**
     * Validates before the document routes 
     * @see org.kuali.kfs.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;

        Document documentForValidation = getDocumentForValidation();
        
        LaborExpenseTransferDocumentBase expenseTransferDocument = (LaborExpenseTransferDocumentBase) documentForValidation;

        // check to ensure totals of accounting lines in source and target sections match
        if (!isValidAmountTransferredByObjectCode(expenseTransferDocument)) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.TARGET_ACCOUNTING_LINES, LaborKeyConstants.ERROR_TRANSFER_AMOUNT_NOT_BALANCED_BY_OBJECT);
            return false;
        }

        return result;       
    }

    /**
     * Determine whether target accounting lines have the same amounts as source accounting lines for each object code
     * 
     * @param accountingDocument the given accounting document
     * @return true if target accounting lines have the same amounts as source accounting lines for each object code; otherwise,
     *         false
     */
    protected boolean isValidAmountTransferredByObjectCode(AccountingDocument accountingDocument) {
        LaborExpenseTransferDocumentBase expenseTransferDocument = (LaborExpenseTransferDocumentBase) accountingDocument;

        boolean isValid = true;

        Map<String, KualiDecimal> unbalancedObjectCodes = expenseTransferDocument.getUnbalancedObjectCodes();
        if (!unbalancedObjectCodes.isEmpty()) {
            isValid = false;
        }

        return isValid;
    }

    /**
     * Gets the documentForValidation attribute. 
     * @return Returns the documentForValidation.
     */
    public Document getDocumentForValidation() {
        return documentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param documentForValidation The documentForValidation to set.
     */
    public void setDocumentForValidation(Document documentForValidation) {
        this.documentForValidation = documentForValidation;
    }    
}

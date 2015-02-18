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

import java.util.HashSet;
import java.util.Set;

import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.document.LaborExpenseTransferDocumentBase;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * benefit transfers cannot be made between two different fringe benefit labor object codes 
 */
public class BenefitExpenseTransferSameFringeBenefitObjectCodeValidation extends GenericValidation {
    private Document documentForValidation;
    
    /**
     * Validates that the accounting lines in the accounting document have the same employee id 
     * <strong>Expects an accounting document as the first a parameter</strong>
     * @see org.kuali.kfs.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
        
        Document documentForValidation = getDocumentForValidation();
        
        AccountingDocument accountingDocument = (AccountingDocument) documentForValidation;
        
        // benefit transfers cannot be made between two different fringe benefit labor object codes.
        boolean sameFringeBenefitObjectCodes = hasSameFringeBenefitObjectCodes(accountingDocument);
        if (!sameFringeBenefitObjectCodes) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.TARGET_ACCOUNTING_LINES, LaborKeyConstants.DISTINCT_OBJECT_CODE_ERROR);
            result = false;
        }

        return result;
    }

    /**
     * Determines whether target accounting lines have the same fringe benefit object codes as source accounting lines
     * 
     * @param accountingDocument the given accounting document
     * @return true if target accounting lines have the same fringe benefit object codes as source accounting lines; otherwise, false
     */
    protected boolean hasSameFringeBenefitObjectCodes(AccountingDocument accountingDocument) {
        LaborExpenseTransferDocumentBase expenseTransferDocument = (LaborExpenseTransferDocumentBase) accountingDocument;

        Set<String> objectCodesFromSourceLine = new HashSet<String>();
        for (Object sourceAccountingLine : expenseTransferDocument.getSourceAccountingLines()) {
            AccountingLine line = (AccountingLine) sourceAccountingLine;
            objectCodesFromSourceLine.add(line.getFinancialObjectCode());
        }

        Set<String> objectCodesFromTargetLine = new HashSet<String>();
        for (Object targetAccountingLine : expenseTransferDocument.getTargetAccountingLines()) {
            AccountingLine line = (AccountingLine) targetAccountingLine;
            objectCodesFromTargetLine.add(line.getFinancialObjectCode());
        }

        if (objectCodesFromSourceLine.size() != objectCodesFromTargetLine.size()) {
            return false;
        }

        return objectCodesFromSourceLine.containsAll(objectCodesFromTargetLine);
    }

    /**
     * Gets the accountingDocumentForValidation attribute. 
     * @return Returns the accountingDocumentForValidation.
     */
    public Document getDocumentForValidation() {
        return documentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setDocumentForValidation(Document documentForValidation) {
        this.documentForValidation = documentForValidation;
    } 
}

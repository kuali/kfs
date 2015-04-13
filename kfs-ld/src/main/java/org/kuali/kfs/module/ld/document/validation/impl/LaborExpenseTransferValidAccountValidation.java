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

import org.kuali.kfs.module.ld.document.LaborExpenseTransferDocumentBase;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * determine whether the given accounting line has already been in the given document
 * 
 * @param accountingDocument the given document
 * @param accountingLine the given accounting line
 * @return true if the given accounting line has already been in the given document; otherwise, false
 */
public class LaborExpenseTransferValidAccountValidation extends GenericValidation {
    private Document documentForValidation;
    
    /**
     * Validates before the document routes 
     * @see org.kuali.kfs.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
               
        Document documentForValidation = getDocumentForValidation();
        
        LaborExpenseTransferDocumentBase expenseTransferDocument = (LaborExpenseTransferDocumentBase) documentForValidation;
        
        // check to ensure the accounts in source/target accounting lines are valid
        if (!isValidAccount(expenseTransferDocument)) {
            return false;
        }

        return result;       
    }

    /**
     * Determine whether the accounts in source/target accounting lines are valid
     * 
     * @param accountingDocument the given accounting document
     * @return true if the accounts in source/target accounting lines are valid; otherwise, false
     */
    public boolean isValidAccount(LaborExpenseTransferDocumentBase expenseTransferDocument) {

        for (Object sourceAccountingLine : expenseTransferDocument.getSourceAccountingLines()) {
            AccountingLine line = (AccountingLine) sourceAccountingLine;
            if (ObjectUtils.isNull(line.getAccount())) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_ACCOUNT_INVALID_ACCOUNT, new String[] { line.getChartOfAccountsCode(), line.getAccountNumber() });
                return false;
            }
        }

        for (Object targetAccountingLine : expenseTransferDocument.getTargetAccountingLines()) {
            AccountingLine line = (AccountingLine) targetAccountingLine;
            if (ObjectUtils.isNull(line.getAccount())) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.TARGET_ACCOUNTING_LINES, KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_ACCOUNT_INVALID_ACCOUNT, new String[] { line.getChartOfAccountsCode(), line.getAccountNumber() });
                return false;
            }
        }
        return true;
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

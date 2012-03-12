/*
 * Copyright 2008-2009 The Kuali Foundation
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

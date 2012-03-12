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

import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.document.LaborExpenseTransferDocumentBase;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * determine whether the given accounting line has already been in the given document
 * 
 * @param accountingDocument the given document
 * @param accountingLine the given accounting line
 * @return true if the given accounting line has already been in the given document; otherwise, false
 */
public class LaborExpenseTransferAccountLineTotalsMatchValidation extends GenericValidation {
    private Document documentForValidation;
    
    /**
     * Validates before the document routes 
     * @see org.kuali.kfs.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
           
        Document documentForValidation = getDocumentForValidation();
        
        LaborExpenseTransferDocumentBase expenseTransferDocument = (LaborExpenseTransferDocumentBase) documentForValidation;
        
        List sourceLines = expenseTransferDocument.getSourceAccountingLines();
        List targetLines = expenseTransferDocument.getTargetAccountingLines();

        // check to ensure totals of accounting lines in source and target sections match
        if (!isAccountingLineTotalsMatch(sourceLines, targetLines)) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, LaborKeyConstants.ACCOUNTING_LINE_TOTALS_MISMATCH_ERROR);
            return false;
        }

        return result;       
    }

    /**
     * This method checks if the total sum amount of the source accounting line matches the total sum amount of the target
     * accounting line, return true if the totals match, false otherwise.
     * 
     * @param sourceLines
     * @param targetLines
     * @return
     */
    public boolean isAccountingLineTotalsMatch(List sourceLines, List targetLines) {
        boolean isValid = true;

        AccountingLine line = null;

        // totals for the from and to lines.
        KualiDecimal sourceLinesAmount = KualiDecimal.ZERO;
        KualiDecimal targetLinesAmount = KualiDecimal.ZERO;

        // sum source lines
        for (Iterator i = sourceLines.iterator(); i.hasNext();) {
            line = (ExpenseTransferAccountingLine) i.next();
            sourceLinesAmount = sourceLinesAmount.add(line.getAmount());
        }

        // sum target lines
        for (Iterator i = targetLines.iterator(); i.hasNext();) {
            line = (ExpenseTransferAccountingLine) i.next();
            targetLinesAmount = targetLinesAmount.add(line.getAmount());
        }

        // if totals don't match, then add error message
        if (sourceLinesAmount.compareTo(targetLinesAmount) != 0) {
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

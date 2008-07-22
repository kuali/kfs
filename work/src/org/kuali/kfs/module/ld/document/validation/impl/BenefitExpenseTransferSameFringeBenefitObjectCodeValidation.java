/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ld.document.validation.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.List;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.module.ld.LaborConstants ;
import org.kuali.kfs.module.ld.LaborKeyConstants; 
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.module.ld.document.BenefitExpenseTransferDocument;
import org.kuali.kfs.module.ld.document.LaborExpenseTransferDocumentBase ;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferSourceAccountingLine;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferTargetAccountingLine;
import org.kuali.kfs.module.ld.service.LaborLedgerPendingEntryService;
import org.kuali.kfs.module.ld.util.LaborPendingEntryGenerator;

/**
 * Validates that the target and source lines have the same object code
 */
public class BenefitExpenseTransferSameFringeBenefitObjectCodeValidation extends GenericValidation {
    private BenefitExpenseTransferDocument accountingDocumentForValidation;
    
    /**
     * Validates that the accounting lines in the accounting document have 
     * any pending labor ledger entries with the same emplID, periodCode, accountNumber, objectCode 
     * <strong>Expects an accounting document as the first a parameter</strong>
     * @see org.kuali.kfs.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
        BenefitExpenseTransferDocument benefitExpenseTransferDocument = getAccountingDocumentForValidation() ;
        
        if (!hasSameFringeBenefitObjectCodes(benefitExpenseTransferDocument)) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.TARGET_ACCOUNTING_LINES, LaborKeyConstants.DISTINCT_OBJECT_CODE_ERROR);
            result = false;
        }
        
        return result ;    
    }

    /**
     * Checks whether amounts by object codes are unchanged
     * 
     * @param accountingDocumentForValidation The accounting document from which the amounts by objects codes are checked
     * @return True if the given accounting documents amounts by object code are unchanged, false otherwise.
     */ 
 
    /**
     * Determines whether target accouting lines have the same fringe benefit object codes as source accounting lines
     * 
     * @param accountingDocument the given accounting document
     * @return true if target accouting lines have the same fringe benefit object codes as source accounting lines; otherwise, false
     */
   
    public boolean hasSameFringeBenefitObjectCodes(BenefitExpenseTransferDocument accountingDocument) {
        boolean entriesSame = true ;
        
   
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

        return entriesSame;
    
     }

    /**
     * Gets the accountingDocumentForValidation attribute. 
     * @return Returns the accountingDocumentForValidation.
     */
    public BenefitExpenseTransferDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingLineForValidation(BenefitExpenseTransferDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }
}

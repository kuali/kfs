/*
 * Copyright 2008 The Kuali Foundation
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

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validates that an accounting document's balances by object codes are unchanged
 */
public class SalaryExpenseTransferObjectCodeBalancesUnchangedValidation extends GenericValidation {
    private AccountingDocument accountingDocumentForValidation;

    /**
     * Validates that an accounting document have balances unchanged for the object codes <strong>Expects an accounting document as
     * the first a parameter</strong>
     * 
     * @see org.kuali.kfs.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;

        AccountingDocument accountingDocumentForValidation = getAccountingDocumentForValidation();        
        SalaryExpenseTransferDocument salaryExpenseTransferDocument = (SalaryExpenseTransferDocument) accountingDocumentForValidation;
        
        Map<String, KualiDecimal> approvalObjectCodeBalances = salaryExpenseTransferDocument.getApprovalObjectCodeBalances();
        boolean unBalanced = approvalObjectCodeBalances != null && approvalObjectCodeBalances.isEmpty();
        
        Map<String, KualiDecimal> unbalancedObjectCodes = salaryExpenseTransferDocument.getUnbalancedObjectCodes();
        unBalanced &= (unbalancedObjectCodes ==null || !unbalancedObjectCodes.isEmpty());
        
        if (unBalanced || !isObjectCodeBalancesUnchanged(salaryExpenseTransferDocument)) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.TARGET_ACCOUNTING_LINES, LaborKeyConstants.ERROR_TRANSFER_AMOUNT_BY_OBJECT_APPROVAL_CHANGE);
            result = false;
        }

        return result;
    }

    /**
     * Checks whether amounts by object codes are unchanged
     * 
     * @param accountingDocumentForValidation The accounting document from which the amounts by objects codes are checked
     * @return True if the given accounting documents amounts by object code are unchanged, false otherwise.
     */
    protected boolean isObjectCodeBalancesUnchanged(SalaryExpenseTransferDocument salaryExpenseTransferDocument) {
        boolean isUnchanged = true;

        Map<String, KualiDecimal> initiatedObjectCodeBalances = salaryExpenseTransferDocument.getApprovalObjectCodeBalances();
        Map<String, KualiDecimal> currentObjectCodeBalances = salaryExpenseTransferDocument.getUnbalancedObjectCodes();

        Set<Entry<String, KualiDecimal>> initiatedObjectCodes = initiatedObjectCodeBalances.entrySet();
        Set<Entry<String, KualiDecimal>> currentObjectCodes = currentObjectCodeBalances.entrySet();

        if (!initiatedObjectCodes.equals(currentObjectCodes))
            isUnchanged = false;

        return isUnchanged;

    }

    /**
     * Gets the accountingDocumentForValidation attribute. 
     * @return Returns the accountingDocumentForValidation.
     */
    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * 
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }
}

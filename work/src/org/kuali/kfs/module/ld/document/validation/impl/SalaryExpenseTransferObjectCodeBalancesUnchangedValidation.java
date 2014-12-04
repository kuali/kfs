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

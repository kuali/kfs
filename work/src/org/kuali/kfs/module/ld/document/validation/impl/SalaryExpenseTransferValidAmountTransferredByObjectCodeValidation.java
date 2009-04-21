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

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentAuthorizer;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * Validates that an accounting document's balances by object codes are unchanged
 */
public class SalaryExpenseTransferValidAmountTransferredByObjectCodeValidation extends GenericValidation {
    private AccountingDocument documentForValidation;

    /**
     * Validates that an accounting document's unbalanced object code balances exist <strong>Expects an accounting document as the
     * first a parameter</strong>
     * 
     * @see org.kuali.kfs.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        SalaryExpenseTransferDocument expenseTransferDocument = (SalaryExpenseTransferDocument) documentForValidation;

        TransactionalDocumentAuthorizer documentAuthorizer = (TransactionalDocumentAuthorizer) SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(expenseTransferDocument);
        String principalId = GlobalVariables.getUserSession().getPerson().getPrincipalId();
        boolean isAdmin = documentAuthorizer.isAuthorized(expenseTransferDocument, LaborConstants.LABOR_MODULE_CODE, LaborConstants.PermissionNames.OVERRIDE_TRANSFER_IMPACTING_EFFORT_CERTIFICATION, principalId);
        if (isAdmin) {
            return true;
        }

        // if approving document, check the object code balances match when document was inititated, else check the balance
        boolean isValid = true;
        if (expenseTransferDocument.getDocumentHeader().getWorkflowDocument().isApprovalRequested()) {
            if (!isObjectCodeBalancesUnchanged(expenseTransferDocument)) {
                GlobalVariables.getErrorMap().putError(KFSPropertyConstants.TARGET_ACCOUNTING_LINES, LaborKeyConstants.ERROR_TRANSFER_AMOUNT_BY_OBJECT_APPROVAL_CHANGE);
                isValid = false;
            }
        }
        else {
            if (!expenseTransferDocument.getUnbalancedObjectCodes().isEmpty()) {
                GlobalVariables.getErrorMap().putError(KFSPropertyConstants.TARGET_ACCOUNTING_LINES, LaborKeyConstants.ERROR_TRANSFER_AMOUNT_NOT_BALANCED_BY_OBJECT);
                isValid = false;
            }
        }

        return isValid;
    }

    /**
     * Checks the current object code balance map of the document against the balances captured before the document was returned for
     * approval.
     * 
     * @param accountingDocument SalaryExpenseTransferDocument to check
     * @return true if the balances have not changed, false if they have
     */
    private boolean isObjectCodeBalancesUnchanged(AccountingDocument accountingDocument) {
        boolean isUnchanged = true;

        Map<String, KualiDecimal> initiatedObjectCodeBalances = ((SalaryExpenseTransferDocument) accountingDocument).getApprovalObjectCodeBalances();
        Map<String, KualiDecimal> currentObjectCodeBalances = ((SalaryExpenseTransferDocument) accountingDocument).getUnbalancedObjectCodes();

        Set<Entry<String, KualiDecimal>> initiatedObjectCodes = initiatedObjectCodeBalances.entrySet();
        Set<Entry<String, KualiDecimal>> currentObjectCodes = currentObjectCodeBalances.entrySet();

        if (initiatedObjectCodes == null) {
            if (currentObjectCodes != null) {
                isUnchanged = false;
            }
        }
        else {
            if (!initiatedObjectCodes.equals(currentObjectCodes)) {
                isUnchanged = false;
            }
        }

        return isUnchanged;
    }

    /**
     * Sets the documentForValidation attribute value.
     * 
     * @param documentForValidation The documentForValidation to set.
     */
    public void setDocumentForValidation(AccountingDocument documentForValidation) {
        this.documentForValidation = documentForValidation;
    }
}

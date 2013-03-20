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
package org.kuali.kfs.fp.document.validation.impl;

import static org.kuali.kfs.sys.KFSConstants.ACCOUNTING_LINE_ERRORS;
import static org.kuali.kfs.sys.KFSKeyConstants.ERROR_DOCUMENT_PC_TRANSACTION_TOTAL_ACCTING_LINE_TOTAL_NOT_EQUAL;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.fp.businessobject.ProcurementCardTargetAccountingLine;
import org.kuali.kfs.fp.businessobject.ProcurementCardTransactionDetail;
import org.kuali.kfs.fp.document.ProcurementCardDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validates that an accounting line does not have a capital object object code
 */
public class ProcurementCardAccountAccessibilityValidation extends GenericValidation {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardAccountAccessibilityValidation.class);

    protected ProcurementCardDocument accountingDocumentForValidation;

    /**
     * Validates that an accounting line does not have a capital object object code
     * <strong>Expects an accounting line as the first a parameter</strong>
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(java.lang.Object[])
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean isValid = false;
        ProcurementCardDocument pcDocument = getAccountingDocumentForValidation();

        WorkflowDocument workflowDocument = pcDocument.getDocumentHeader().getWorkflowDocument();

        Set<String> activeNodes = workflowDocument.getCurrentNodeNames();
        if (workflowDocument.isEnroute() && CollectionUtils.isNotEmpty(activeNodes) && activeNodes.contains(KFSConstants.RouteLevelNames.ACCOUNT_REVIEW_FULL_EDIT)) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * This method validates the balance of the transaction given.  A procurement card transaction is in balance if
     * the total amount of the transaction equals the total of the target accounting lines corresponding to the transaction.
     *
     * @param pcTransaction The transaction detail used to retrieve the procurement card transaction and target accounting
     *                      lines used to check for in balance.
     * @return True if the amounts are equal and the transaction is in balance, false otherwise.
     */
    protected boolean isTransactionBalanceValid(ProcurementCardTransactionDetail pcTransactionDetail) {
        boolean inBalance = true;
        KualiDecimal transAmount = pcTransactionDetail.getTransactionTotalAmount();
        List<ProcurementCardTargetAccountingLine> targetAcctingLines = pcTransactionDetail.getTargetAccountingLines();

        KualiDecimal targetLineTotal = KualiDecimal.ZERO;

        for (TargetAccountingLine targetLine : targetAcctingLines) {
            targetLineTotal = targetLineTotal.add(targetLine.getAmount());
        }

        // perform absolute value check because current system has situations where amounts may be opposite in sign
        // This will no longer be necessary following completion of KULFDBCK-1290
        inBalance = transAmount.abs().equals(targetLineTotal.abs());

        if (!inBalance) {
            GlobalVariables.getMessageMap().putError(ACCOUNTING_LINE_ERRORS, ERROR_DOCUMENT_PC_TRANSACTION_TOTAL_ACCTING_LINE_TOTAL_NOT_EQUAL, new String[] { transAmount.toString(), targetLineTotal.toString() });
        }

        return inBalance;
    }

    public ProcurementCardDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    public void setAccountingDocumentForValidation(ProcurementCardDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }
}

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
package org.kuali.kfs.fp.document.validation.impl;

import static org.kuali.kfs.sys.KFSConstants.ACCOUNTING_LINE_ERRORS;
import static org.kuali.kfs.sys.KFSKeyConstants.ERROR_DOCUMENT_BALANCE_CONSIDERING_SOURCE_AND_TARGET_AMOUNTS;
import static org.kuali.kfs.sys.KFSKeyConstants.ERROR_DOCUMENT_PC_TRANSACTION_TOTAL_ACCTING_LINE_TOTAL_NOT_EQUAL;

import java.util.List;

import org.kuali.kfs.fp.businessobject.ProcurementCardTargetAccountingLine;
import org.kuali.kfs.fp.businessobject.ProcurementCardTransactionDetail;
import org.kuali.kfs.fp.document.ProcurementCardDocument;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validates that an accounting line does not have a capital object object code 
 */
public class ProcurementCardBalanceValidation extends GenericValidation {
    private ProcurementCardDocument accountingDocumentForValidation;

    /**
     * Validates that an accounting line does not have a capital object object code
     * <strong>Expects an accounting line as the first a parameter</strong>
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        ProcurementCardDocument pcDocument = (ProcurementCardDocument) getAccountingDocumentForValidation();

        KualiDecimal targetTotal = pcDocument.getTargetTotal();
        KualiDecimal sourceTotal = pcDocument.getSourceTotal();

        boolean isValid = targetTotal.compareTo(sourceTotal) == 0;

        if (!isValid) {
            GlobalVariables.getMessageMap().putError(ACCOUNTING_LINE_ERRORS, ERROR_DOCUMENT_BALANCE_CONSIDERING_SOURCE_AND_TARGET_AMOUNTS, new String[] { sourceTotal.toString(), targetTotal.toString() });
        }

        List<ProcurementCardTransactionDetail> pcTransactionEntries = pcDocument.getTransactionEntries();

        for (ProcurementCardTransactionDetail pcTransactionDetail : pcTransactionEntries) {
            isValid &= isTransactionBalanceValid(pcTransactionDetail);
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

    /**
     * Gets the accountingDocumentForValidation attribute. 
     * @return Returns the accountingDocumentForValidation.
     */
    public ProcurementCardDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(ProcurementCardDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

    
}

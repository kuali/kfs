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
import static org.kuali.kfs.sys.KFSConstants.GL_CREDIT_CODE;
import static org.kuali.kfs.sys.KFSKeyConstants.ERROR_DOCUMENT_BALANCE;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validation that checks that the general ledger pending entries associated with an auxiliary voucher
 * document balance.
 */
public class AuxiliaryVoucherGeneralLedgerPendingEntriesBalanceValdiation extends GenericValidation {
    private AccountingDocument accountingDocumentForValidation;
    private GeneralLedgerPendingEntryService generalLedgerPendingEntryService;

    /**
     * Returns true if the explicit, non-DI credit and debit GLPEs derived from the document's accountingLines are in balance
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
//      generate GLPEs specifically here so that we can compare debits to credits
        if (!getGeneralLedgerPendingEntryService().generateGeneralLedgerPendingEntries(getAccountingDocumentForValidation())) {
            throw new ValidationException("general ledger GLPE generation failed");
        }

        // now loop through all of the GLPEs and calculate buckets for debits and credits
        KualiDecimal creditAmount = KualiDecimal.ZERO;
        KualiDecimal debitAmount = KualiDecimal.ZERO;

        for (GeneralLedgerPendingEntry glpe : getAccountingDocumentForValidation().getGeneralLedgerPendingEntries()) {
            // make sure we are looking at only the explicit entries that aren't DI types
            if (!glpe.isTransactionEntryOffsetIndicator() && !glpe.getFinancialDocumentTypeCode().equals(KFSConstants.FinancialDocumentTypeCodes.DISTRIBUTION_OF_INCOME_AND_EXPENSE)) {
                if (GL_CREDIT_CODE.equals(glpe.getTransactionDebitCreditCode())) {
                    creditAmount = creditAmount.add(glpe.getTransactionLedgerEntryAmount());
                }
                else { // DEBIT
                    debitAmount = debitAmount.add(glpe.getTransactionLedgerEntryAmount());
                }
            }
        }

        boolean balanced = debitAmount.equals(creditAmount);
        if (!balanced) {
            String errorParams[] = { creditAmount.toString(), debitAmount.toString() };
            GlobalVariables.getMessageMap().putError(ACCOUNTING_LINE_ERRORS, ERROR_DOCUMENT_BALANCE, errorParams);
        }
        return balanced;
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
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

    /**
     * Gets the generalLedgerPendingEntryService attribute. 
     * @return Returns the generalLedgerPendingEntryService.
     */
    public GeneralLedgerPendingEntryService getGeneralLedgerPendingEntryService() {
        return generalLedgerPendingEntryService;
    }

    /**
     * Sets the generalLedgerPendingEntryService attribute value.
     * @param generalLedgerPendingEntryService The generalLedgerPendingEntryService to set.
     */
    public void setGeneralLedgerPendingEntryService(GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
        this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
    }
}

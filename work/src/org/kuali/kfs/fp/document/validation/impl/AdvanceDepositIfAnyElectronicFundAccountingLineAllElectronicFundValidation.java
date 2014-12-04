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

import org.kuali.kfs.fp.document.AdvanceDepositDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validates that if the given advance deposit document has any electronic fund accounting lines,
 * that all accounting lines on the document go to the electronic funds account
 */
public class AdvanceDepositIfAnyElectronicFundAccountingLineAllElectronicFundValidation extends GenericValidation {
    private AdvanceDepositDocument advanceDepositDocumentForValidation;
    private ElectronicPaymentClaimingService electronicPaymentClaimingService;

    /**
     * Validates the advance deposit document, that if it has one eft accounting line, all accounting lines represent electronic funds
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true; // assume that the document works just fine
        if (anyAccountingLinesRepresentElectronicPayments() && !allAccountingLinesRepresentElectronicPayments()) {
            GlobalVariables.getMessageMap().putError(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.AdvanceDeposit.ERROR_DOCUMENT_ADVANCE_DEPOSIT_INCORRECT_ELECTRONIC_PAYMENT_STATE, new String[] {});
            result = false; // oh you document! you've disappointed me!
        }
        return result;
    }
    
    /**
     * Determines if any of the accounting lines on the document represent electronic payments
     * @return true if the document contains an electronic transfer accounting line, false if none do
     */
    protected boolean anyAccountingLinesRepresentElectronicPayments() {
        for (Object accountingLineAsObject : getAdvanceDepositDocumentForValidation().getSourceAccountingLines()) {
            final AccountingLine accountingLine = (AccountingLine)accountingLineAsObject;
            if (getElectronicPaymentClaimingService().representsElectronicFundAccount(accountingLine)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Determines if all of the accounting lines on the document represent electronic payments
     * @return true if the document contains all electronic transfer accounting line, false if any accounting line does not represent an electronic payment
     */
    protected boolean allAccountingLinesRepresentElectronicPayments() {
        for (Object accountingLineAsObject : getAdvanceDepositDocumentForValidation().getSourceAccountingLines()) {
            final AccountingLine accountingLine = (AccountingLine)accountingLineAsObject;
            if (!getElectronicPaymentClaimingService().representsElectronicFundAccount(accountingLine)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the advanceDepositDocumentForValidation attribute. 
     * @return Returns the advanceDepositDocumentForValidation.
     */
    public AdvanceDepositDocument getAdvanceDepositDocumentForValidation() {
        return advanceDepositDocumentForValidation;
    }

    /**
     * Sets the advanceDepositDocumentForValidation attribute value.
     * @param advanceDepositDocumentForValidation The advanceDepositDocumentForValidation to set.
     */
    public void setAdvanceDepositDocumentForValidation(AdvanceDepositDocument documentForValidation) {
        this.advanceDepositDocumentForValidation = documentForValidation;
    }

    /**
     * Gets the electronicPaymentClaimingService attribute. 
     * @return Returns the electronicPaymentClaimingService.
     */
    public ElectronicPaymentClaimingService getElectronicPaymentClaimingService() {
        return electronicPaymentClaimingService;
    }

    /**
     * Sets the electronicPaymentClaimingService attribute value.
     * @param electronicPaymentClaimingService The electronicPaymentClaimingService to set.
     */
    public void setElectronicPaymentClaimingService(ElectronicPaymentClaimingService electronicPaymentClaimingService) {
        this.electronicPaymentClaimingService = electronicPaymentClaimingService;
    }
}

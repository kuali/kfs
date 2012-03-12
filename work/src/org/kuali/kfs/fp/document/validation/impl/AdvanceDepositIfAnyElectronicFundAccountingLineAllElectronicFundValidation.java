/*
 * Copyright 2009 The Kuali Foundation
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

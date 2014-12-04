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
package org.kuali.kfs.module.endow.document.validation.impl;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.document.CashTransferDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;

public class CashTransferDocumentRules extends CashDocumentBaseRules {

    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);
        isValid &= !GlobalVariables.getMessageMap().hasErrors();

        if (isValid) {
            CashTransferDocument cashTransferDocument = (CashTransferDocument) document;

            // check if the total of the transaction amount (Income plus Principal) in the From transaction lines
            // equals the total of the transaction amount in the To transaction lines.
            if (!cashTransferDocument.getTargetTotalAmount().equals(cashTransferDocument.getSourceTotalAmount())) {
                GlobalVariables.getMessageMap().putError(EndowConstants.TRANSACTION_LINE_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_BALANCE);
            }

            // check must have one source tranaction line
            if (!transactionLineSizeGreaterThanZero(cashTransferDocument, true))
                return false;

            // check must have one source tranaction line
            if (!transactionLineSizeGreaterThanZero(cashTransferDocument, false))
                return false;

            //KFSMI-7505
            //validations of security and registration attributes
            isValid &= validateSecurityAndRegistrationRules(document);

            if (isValid){
                for (int i = 0; i < cashTransferDocument.getSourceTransactionLines().size(); i++) {
                    EndowmentTransactionLine sourceTransactionLine = cashTransferDocument.getSourceTransactionLines().get(i);
                    isValid &= validateCashTransactionLine(cashTransferDocument, sourceTransactionLine, i);
                }
    
                for (int i = 0; i < cashTransferDocument.getTargetTransactionLines().size(); i++) {
                    EndowmentTransactionLine targetTransactionLine = cashTransferDocument.getTargetTransactionLines().get(i);
                    isValid &= validateCashTransactionLine(cashTransferDocument, targetTransactionLine, i);
                }
            }

        }

        return isValid;
    }


    /**
     * @see org.kuali.kfs.module.endow.document.validation.impl.CashDocumentBaseRules#validateCashTransactionLine(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocumentBase,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    @Override
    protected boolean validateCashTransactionLine(EndowmentTransactionLinesDocument document, EndowmentTransactionLine line, int index) {

        boolean isValid = super.validateCashTransactionLine(document, line, index);
        isValid &= !GlobalVariables.getMessageMap().hasErrors();

        if (isValid) {
            // Obtain Prefix for Error fields in UI.
            String ERROR_PREFIX = getErrorPrefix(line, index);

            if (EndowConstants.TRANSACTION_LINE_TYPE_SOURCE.equalsIgnoreCase(line.getTransactionLineTypeCode())) {
                checkWhetherReducePermanentlyRestrictedFund(line, ERROR_PREFIX);
                checkWhetherHaveSufficientFundsForCashBasedTransaction(line, ERROR_PREFIX);
            }
        }
        return GlobalVariables.getMessageMap().getErrorCount() == 0;

    }


    /**
     * CashTranserDocument will use the source
     * 
     * @see org.kuali.kfs.module.endow.document.validation.impl.CashDocumentBaseRules#isSourceDocument()
     */
    @Override
    boolean isSourceDocument() {
        return true;
    }

}

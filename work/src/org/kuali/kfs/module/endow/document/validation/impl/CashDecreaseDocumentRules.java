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

import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.document.CashDecreaseDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;

public class CashDecreaseDocumentRules extends CashDocumentBaseRules {

    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean processCustomRouteDocumentBusinessRules(Document document) {
        CashDecreaseDocument cashDecreaseDocument = (CashDecreaseDocument) document;

        // Validate at least one Tx was entered.
        if (!transactionLineSizeGreaterThanZero(cashDecreaseDocument, isSourceDocument()))
            return false;

        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);
        isValid &= !GlobalVariables.getMessageMap().hasErrors();

        if (isValid) {
            //KFSMI-7505
            //validations of security and registration attributes
            isValid &= validateSecurityAndRegistrationRules(document);
            if (isValid) {
                for (int i = 0; i < cashDecreaseDocument.getSourceTransactionLines().size(); i++) {
                    EndowmentTransactionLine txLine = cashDecreaseDocument.getSourceTransactionLines().get(i);
                    isValid &= validateCashTransactionLine(cashDecreaseDocument, txLine, i);
                }
            }
        }

        return isValid;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.validation.impl.CashDocumentBaseRules# (
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    @Override
    protected boolean validateCashTransactionLine(EndowmentTransactionLinesDocument document, EndowmentTransactionLine line, int index) {
        boolean isValid = super.validateCashTransactionLine(document, line, index);
        isValid &= !GlobalVariables.getMessageMap().hasErrors();

        if (isValid) {
            // Obtain Prefix for Error fields in UI.
            String ERROR_PREFIX = getErrorPrefix(line, index);

            checkWhetherReducePermanentlyRestrictedFund(line, ERROR_PREFIX);
            checkWhetherHaveSufficientFundsForCashBasedTransaction(line, ERROR_PREFIX);
        }
        return GlobalVariables.getMessageMap().getErrorCount() == 0;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.validation.impl.EndowmentTransactionLinesDocumentBaseRules#validateEndowmentTransactionTypeCode(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument, org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine, java.lang.String)
     */
    @Override
    protected boolean validateEndowmentTransactionTypeCode(EndowmentTransactionLinesDocument endowmentTransactionLinesDocument, EndowmentTransactionLine line, String prefix) {

        return super.validateEtranTypeBasedOnDocSource(endowmentTransactionLinesDocument, line, prefix);
    }

    /**
     * CashDecreaseDocument will use the source
     * 
     * @see org.kuali.kfs.module.endow.document.validation.impl.CashDocumentBaseRules#isSourceDocument()
     */
    @Override
    boolean isSourceDocument() {
        return true;
    }

}

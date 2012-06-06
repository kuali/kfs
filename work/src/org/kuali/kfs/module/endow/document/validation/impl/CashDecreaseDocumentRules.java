/*
 * Copyright 2010 The Kuali Foundation.
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

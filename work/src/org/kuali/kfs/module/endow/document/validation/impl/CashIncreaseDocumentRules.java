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
import org.kuali.kfs.module.endow.document.CashIncreaseDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionalDocument;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;

public class CashIncreaseDocumentRules extends CashDocumentBaseRules {

    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean processCustomRouteDocumentBusinessRules(Document document) {
        CashIncreaseDocument cashIncreaseDocument = (CashIncreaseDocument) document;
        
        // Validate at least one Tx was entered.
        if (!transactionLineSizeGreaterThanZero(cashIncreaseDocument, isSourceDocument()))
            return false;

        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);
        isValid &= !GlobalVariables.getMessageMap().hasErrors();

        if (isValid) {
            //KFSMI-7505
            //validations of security and registration attributes
            isValid &= validateSecurityAndRegistrationRules(document);
            if (isValid) {
                for (int i = 0; i < cashIncreaseDocument.getTargetTransactionLines().size(); i++) {
                    EndowmentTransactionLine txLine = cashIncreaseDocument.getTargetTransactionLines().get(i);
                    isValid &= validateCashTransactionLine(cashIncreaseDocument, txLine, i);
                }
            }
        }

        return isValid;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.validation.impl.EndowmentTransactionLinesDocumentBaseRules#validateEndowmentTransactionTypeCode(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine, java.lang.String)
     */
    @Override
    protected boolean validateEndowmentTransactionTypeCode(EndowmentTransactionLinesDocument endowmentTransactionLinesDocument, EndowmentTransactionLine line, String prefix) {

        return validateEtranTypeBasedOnDocSource(endowmentTransactionLinesDocument, line, prefix);
    }

    /**
     * CashIncreseDocument will use the target
     * 
     * @see org.kuali.kfs.module.endow.document.validation.impl.CashDocumentBaseRules#isSourceDocument()
     */
    @Override
    boolean isSourceDocument() {
        return false;
    }
}

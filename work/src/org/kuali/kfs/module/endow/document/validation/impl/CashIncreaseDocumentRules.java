/*
 * Copyright 2010 The Kuali Foundation.
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

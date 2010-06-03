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
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocumentBase;
import org.kuali.kfs.module.endow.document.service.EndowmentTransactionLinesDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.util.GlobalVariables;

public class CashDecreaseDocumentRules extends CashDocumentBaseRules {

    /**
     * @see org.kuali.kfs.module.endow.document.validation.impl.EndowmentTransactionLinesDocumentBaseRules#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        CashDecreaseDocument cashDecreaseDocument = (CashDecreaseDocument) document;
        
        // Validate at least one Tx was entered.
        if (!transactionLineSizeGreaterThanZero(cashDecreaseDocument, true))
            return false;
        
        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);
        isValid &= !GlobalVariables.getMessageMap().hasErrors();

        if (isValid) {
           
            // Checks if Security field is not empty, security code must be valid.
            if (!isSecurityCodeEmpty(cashDecreaseDocument, true)){
                if (!validateSecurityCode(cashDecreaseDocument, true))
                    return false;               
            }
               
            for (int i = 0; i < cashDecreaseDocument.getSourceTransactionLines().size(); i++) {
                EndowmentTransactionLine txLine = cashDecreaseDocument.getSourceTransactionLines().get(i);
                isValid &= validateCashTransactionLine(cashDecreaseDocument,txLine, i);
            }
        }

        return isValid;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.document.validation.impl.CashDocumentBaseRules#
(
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */    
    @Override
    protected boolean validateCashTransactionLine(EndowmentTransactionLinesDocument document, EndowmentTransactionLine line, int index) {
        boolean isValid = super.validateCashTransactionLine(document,line, index);
        isValid &= !GlobalVariables.getMessageMap().hasErrors();

        if (isValid) {
            // Obtain Prefix for Error fields in UI.
            String ERROR_PREFIX = getErrorPrefix(line, index);
            
            checkWhetherReducePermanentlyRestrictedFund(line, ERROR_PREFIX);
            checkWhetherHaveSufficientFundsForCashBasedTransaction(line, ERROR_PREFIX);
        }
        return GlobalVariables.getMessageMap().getErrorCount() == 0;
    }

}

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

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.module.endow.document.CashTransferDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocumentBase;
import org.kuali.kfs.module.endow.document.service.EndowmentTransactionLinesDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.util.GlobalVariables;

public class CashTransferDocumentRules extends CashDocumentBaseRules{

    /**
     * @see org.kuali.kfs.module.endow.document.validation.impl.EndowmentTransactionLinesDocumentBaseRules#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);
        isValid &= !GlobalVariables.getMessageMap().hasErrors();

        if (isValid) {
            CashTransferDocument cashTransferDocument = (CashTransferDocument) document;

            // Checks if Security field is not empty, security code must be valid.
            if (!isSecurityCodeEmpty(cashTransferDocument, true)){
                if (!validateSecurityCode(cashTransferDocument, true))
                    return false;               
            }
            
            for (int i = 0; i < cashTransferDocument.getSourceTransactionLines().size(); i++) {
                EndowmentTransactionLine sourceTransactionLine = cashTransferDocument.getSourceTransactionLines().get(i);
                isValid &= validateCashTransactionLine(cashTransferDocument,sourceTransactionLine, i);
            }
            
            for (int i = 0; i < cashTransferDocument.getTargetTransactionLines().size(); i++) {
                EndowmentTransactionLine targetTransactionLine = cashTransferDocument.getTargetTransactionLines().get(i);
                isValid &= validateCashTransactionLine(cashTransferDocument,targetTransactionLine, i);
            }            
            
        }

        return isValid;
    }

    /**
     * @see org.kuali.rice.kns.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);
        CashTransferDocument cashTransferDocument = (CashTransferDocument) document;
        if (isValid){
            //check if the total of the transaction amount (Income plus Principal) in the From transaction lines 
            //equals the total of the transaction amount in the To transaction lines.  
            if(!cashTransferDocument.getTargetTotalAmount().equals(cashTransferDocument.getSourceTotalAmount())){
                GlobalVariables.getMessageMap().putError(EndowConstants.ENDOWMENT_TRANSACTION_LINE_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_BALANCE);
            }
        
            //check must have one source tranaction line
            if (!transactionLineSizeGreaterThanZero(cashTransferDocument, true))
                return false;
        
            //check must have one source tranaction line
            if (!transactionLineSizeGreaterThanZero(cashTransferDocument, false))
                return false;
        }
        return isValid;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.validation.impl.CashDocumentBaseRules#validateCashTransactionLine(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocumentBase,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */    
    @Override
    protected boolean validateCashTransactionLine(EndowmentTransactionLinesDocument document,EndowmentTransactionLine line, int index) {
        
        boolean isValid = super.validateCashTransactionLine(document,line, index);
        isValid &= !GlobalVariables.getMessageMap().hasErrors();

        if (isValid) {
            // Obtain Prefix for Error fields in UI.
            String ERROR_PREFIX = getErrorPrefix(line, index);
            
            if (EndowConstants.TRANSACTION_LINE_TYPE_SOURCE.equalsIgnoreCase(line.getTransactionLineTypeCode())){
                checkWhetherReducePermanentlyRestrictedFund(line, ERROR_PREFIX);
                checkWhetherHaveSufficientFundsForCashBasedTransaction(line, ERROR_PREFIX);
            }
         }
        return GlobalVariables.getMessageMap().getErrorCount() == 0;
    
    }


}

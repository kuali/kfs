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
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.CorpusAdjustmentDocument;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.util.GlobalVariables;

public class CorpusAdjustmentDocumentRules extends EndowmentTransactionLinesDocumentBaseRules {

    /**
     * @see org.kuali.kfs.module.endow.document.validation.AddTransactionLineRule#processAddTransactionLineRules(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    @Override
    public boolean processAddTransactionLineRules(EndowmentTransactionLinesDocument transLineDocument, EndowmentTransactionLine line) 
    {
        boolean isValid = true; 
        
        String ERROR_PREFIX = getErrorPrefix(line, -1);
        
        isValid &= super.processAddTransactionLineRules(transLineDocument, line);
        
        return isValid;
    }
    
    /**
     * This method validates the Tx line but from Liability Increase perspective. 
     * 
     * @param endowmentTransactionLinesDocumentBase
     * @param line
     * @param index
     * @return
     */
    protected boolean validateTransactionLine(EndowmentTransactionLinesDocument endowmentTransactionLinesDocument, EndowmentTransactionLine line, int index)
    {
        boolean isValid = true;

        isValid &= super.validateTransactionLine(endowmentTransactionLinesDocument, line, index);

        if(isValid)
        {
            // Obtain Prefix for Error fields in UI.
            String ERROR_PREFIX = getErrorPrefix(line, index);
    
            //Ensure for cash Tx do not have a Etran. 
            isValid &= checkCashTransactionEndowmentCode(endowmentTransactionLinesDocument, line, ERROR_PREFIX);
    
            if(endowmentTransactionLinesDocument.isErrorCorrectedDocument())
            {
                // Validate Amount is Less than Zero.
                isValid &= validateTransactionAmountLessThanZero(line, ERROR_PREFIX);
            }
            else
            {
                // Validate Amount is Greater than Zero.
                isValid &= validateTransactionAmountGreaterThanZero(line, ERROR_PREFIX);
            }
        }

        return GlobalVariables.getMessageMap().getErrorCount() == 0;
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) 
    {
        boolean isValid = !GlobalVariables.getMessageMap().hasErrors();
        
        CorpusAdjustmentDocument corpusAdjustmentDocument = (CorpusAdjustmentDocument) document;
        
        if (isValid) 
        {
            if(!isValid)
                return isValid;

            // Empty out the Source Tx Line in weird case they got entered.
            corpusAdjustmentDocument.getSourceTransactionLines().clear();

            // Validate at least one Tx was entered.
            if (!transactionLineSizeGreaterThanZero(corpusAdjustmentDocument, false))
                return false;

            isValid &= super.processCustomSaveDocumentBusinessRules(document);
        }

        return GlobalVariables.getMessageMap().getErrorCount() == 0;
    }



    /**
     * @see org.kuali.rice.kns.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        return super.processCustomRouteDocumentBusinessRules(document);
    }
}

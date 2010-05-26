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

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.document.EndowmentSecurityDetailsDocumentBase;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocumentBase;
import org.kuali.kfs.module.endow.document.SecurityTransferDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.util.GlobalVariables;

public class SecurityTransferDocumentRules extends EndowmentTransactionLinesDocumentBaseRules {


    /**
     * @see org.kuali.kfs.module.endow.document.validation.AddTransactionLineRule#processAddTransactionLineRules(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    @Override
    public boolean processAddTransactionLineRules(EndowmentTransactionLinesDocument transLineDocument, EndowmentTransactionLine line) 
    {
        boolean isValid = true; 
        
        String ERROR_PREFIX = getErrorPrefix(line, -1);
        
        isValid = validateSecurity(isValid, (SecurityTransferDocument) transLineDocument, true);
        
        isValid = validateRegistration(isValid, (SecurityTransferDocument) transLineDocument, true);
        
        isValid = super.processAddTransactionLineRules(transLineDocument, line);
        isValid &= !GlobalVariables.getMessageMap().hasErrors();

        if (isValid) {
            isValid &= validateLiabilityTransactionLine((EndowmentTransactionLinesDocumentBase) transLineDocument, line, -1);
        }

        return GlobalVariables.getMessageMap().getErrorCount() == 0;
    }
    
    /**
     * This method validates the Tx line but from Liability Increase perspective. 
     * 
     * @param endowmentTransactionLinesDocumentBase
     * @param line
     * @param index
     * @return
     */
    protected boolean validateLiabilityTransactionLine(EndowmentTransactionLinesDocumentBase endowmentTransactionLinesDocumentBase, EndowmentTransactionLine line, int index) {
        boolean isValid = true;

        // Obtain Prefix for Error fields in UI.
        String ERROR_PREFIX = getErrorPrefix(line, index);

        //Ensure for cash Tx do not have a Etran. 
        isValid &= checkCashTransactionEndowmentCode(endowmentTransactionLinesDocumentBase, line, ERROR_PREFIX);

        // Validate Greater then Zero(thus positive) value
        isValid &= validateTransactionAmountGreaterThanZero(line, ERROR_PREFIX);

        // Validate Units is Greater then Zero(thus positive) value
        isValid &= validateTransactionUnitsGreaterThanZero(line, ERROR_PREFIX);

        // Validate if Sufficient Units are Avaiable
        isValid &= checkSufficientUnitsAvaiable(endowmentTransactionLinesDocumentBase,line, ERROR_PREFIX);
        
        //Check if value of Endowment is being reduced.
        isValid &= checkEndowmentValueReduction(endowmentTransactionLinesDocumentBase,line, ERROR_PREFIX);

        return GlobalVariables.getMessageMap().getErrorCount() == 0;
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);
        isValid &= !GlobalVariables.getMessageMap().hasErrors();

        if (isValid) {
            SecurityTransferDocument liabilityIncreaseDocument = (SecurityTransferDocument) document;

            //Validate Security
            isValid = validateSecurity(isValid, liabilityIncreaseDocument, true);

            //Validate Registration code.
            isValid = validateRegistration(isValid, liabilityIncreaseDocument, true);

            // Validate atleast one Tx was entered.
            if (!transactionLineSizeGreaterThanZero(liabilityIncreaseDocument, true))
                return false;

            // Obtaining all the transaction lines for validations
            List<EndowmentTransactionLine> txLines = new ArrayList<EndowmentTransactionLine>();
            txLines.addAll(liabilityIncreaseDocument.getTargetTransactionLines());

            for (int i = 0; i < liabilityIncreaseDocument.getTargetTransactionLines().size(); i++) {
                EndowmentTransactionLine txLine = liabilityIncreaseDocument.getTargetTransactionLines().get(i);
                isValid &= validateLiabilityTransactionLine(liabilityIncreaseDocument, txLine, i);
            }
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

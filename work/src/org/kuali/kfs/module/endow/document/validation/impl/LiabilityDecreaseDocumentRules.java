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

import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.document.EndowmentTaxLotLinesDocumentBase;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.LiabilityDecreaseDocument;
import org.kuali.kfs.module.endow.document.service.LiabilityDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;

public class LiabilityDecreaseDocumentRules extends EndowmentTransactionLinesDocumentBaseRules {


    /**
     * @see org.kuali.kfs.module.endow.document.validation.AddTransactionLineRule#processAddTransactionLineRules(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    @Override
    public boolean processAddTransactionLineRules(EndowmentTransactionLinesDocument transLineDocument, EndowmentTransactionLine line) {
        boolean isValid = true;

        String ERROR_PREFIX = getErrorPrefix(line, -1);

        isValid &= validateSecurity(isValid, (LiabilityDecreaseDocument) transLineDocument, true);

        isValid &= validateRegistration(isValid, (LiabilityDecreaseDocument) transLineDocument, true);

        if (isValid) {
            isValid &= super.processAddTransactionLineRules(transLineDocument, line);
        }

        if (!isValid)
            return isValid;

        LiabilityDocumentService taxLotsService = SpringContext.getBean(LiabilityDocumentService.class);
        LiabilityDecreaseDocument liabilityDecreaseDocument = (LiabilityDecreaseDocument) transLineDocument;
        boolean isSource = line instanceof EndowmentSourceTransactionLine ? true : false;
        taxLotsService.updateLiabilityDecreaseTransactionLineTaxLots(isSource, (EndowmentTaxLotLinesDocumentBase) liabilityDecreaseDocument, line);

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
    @Override
    protected boolean validateTransactionLine(EndowmentTransactionLinesDocument endowmentTransactionLinesDocument, EndowmentTransactionLine line, int index) {
        boolean isValid = true;

        isValid &= super.validateTransactionLine(endowmentTransactionLinesDocument, line, index);

        if (isValid) {
            // Obtain Prefix for Error fields in UI.
            String ERROR_PREFIX = getErrorPrefix(line, index);

            // Ensure for cash Tx do not have a Etran.
            isValid &= checkCashTransactionEndowmentCode(endowmentTransactionLinesDocument, line, ERROR_PREFIX);

            if (endowmentTransactionLinesDocument.isErrorCorrectedDocument()) {
                // Validate Amount is Less than Zero.
                isValid &= validateTransactionAmountLessThanZero(line, ERROR_PREFIX);

                // Validate Units is Less than Zero.
                isValid &= validateTransactionUnitsLessThanZero(line, ERROR_PREFIX);
            }
            else {
                // Validate Amount is Greater than Zero.
                isValid &= validateTransactionAmountGreaterThanZero(line, ERROR_PREFIX);

                // Validate Units is Greater than Zero.
                isValid &= validateTransactionUnitsGreaterThanZero(line, ERROR_PREFIX);
            }

            // Validates Units & Amount are equal.
            isValid &= validateTransactionUnitsAmountEqual(line, ERROR_PREFIX);
        }

        return GlobalVariables.getMessageMap().getErrorCount() == 0;
    }

    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);
        isValid &= !GlobalVariables.getMessageMap().hasErrors();

        LiabilityDecreaseDocument liabilityDecreaseDocument = (LiabilityDecreaseDocument) document;

        if (isValid) {
            // Validate Security
            isValid &= validateSecurity(isValid, liabilityDecreaseDocument, true);

            // Validate Registration code.
            isValid &= validateRegistration(isValid, liabilityDecreaseDocument, true);

            if (!isValid)
                return isValid;

            // Empty out the Target Tx Line in weird case they got entered.
            liabilityDecreaseDocument.getTargetTransactionLines().clear();

            // Validate atleast one Tx was entered.
            if (!transactionLineSizeGreaterThanZero(liabilityDecreaseDocument, true))
                return false;

            isValid &= super.processCustomSaveDocumentBusinessRules(document);
        }

        return GlobalVariables.getMessageMap().getErrorCount() == 0;
    }

}

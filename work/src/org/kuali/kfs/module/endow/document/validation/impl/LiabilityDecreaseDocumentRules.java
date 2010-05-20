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

import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.document.EndowmentTaxLotLinesDocumentBase;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.LiabilityDecreaseDocument;
import org.kuali.kfs.module.endow.document.service.LiabilityDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.util.GlobalVariables;

public class LiabilityDecreaseDocumentRules extends EndowmentTransactionLinesDocumentBaseRules {
    private static final String LIABILITY_CLASS_CODE = "L";

    /**
     * @see org.kuali.kfs.module.endow.document.validation.AddTransactionLineRule#processAddTransactionLineRules(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    @Override
    public boolean processAddTransactionLineRules(EndowmentTransactionLinesDocument transLine, EndowmentTransactionLine line) {
        boolean isValid = super.processAddTransactionLineRules(transLine, line);
        isValid &= !GlobalVariables.getMessageMap().hasErrors();

        if (isValid) {
            isValid &= validateLiabilityTransactionLine(line, -1);
        }

        LiabilityDocumentService taxLotsService = SpringContext.getBean(LiabilityDocumentService.class);
        LiabilityDecreaseDocument liabilityDecreaseDocument = (LiabilityDecreaseDocument) transLine;
        boolean isSource = line instanceof EndowmentSourceTransactionLine ? true : false;
        taxLotsService.updateLiabilityDecreaseTransactionLineTaxLots(isSource, (EndowmentTaxLotLinesDocumentBase) liabilityDecreaseDocument, line);
        
        return GlobalVariables.getMessageMap().getErrorCount() == 0;
    }

    protected boolean validateLiabilityTransactionLine(EndowmentTransactionLine line, int index) {
        boolean isValid = true;

        // Obtain Prefix for Error fields in UI.
        String ERROR_PREFIX = null;
        if (line instanceof EndowmentSourceTransactionLine) {
            if (index == -1) {
                ERROR_PREFIX = EndowPropertyConstants.SOURCE_TRANSACTION_LINE_PREFIX;
            }
            else {
                ERROR_PREFIX = EndowPropertyConstants.EXISTING_SOURCE_TRANSACTION_LINE_PREFIX + "[" + index + "].";
            }
        }
        else {
            if (index == -1) {
                ERROR_PREFIX = EndowPropertyConstants.TARGET_TRANSACTION_LINE_PREFIX;
            }
            else {
                ERROR_PREFIX = EndowPropertyConstants.EXISTING_TARGET_TRANSACTION_LINE_PREFIX + "[" + index + "].";
            }
        }

        // Validate Greater then Zero(thus positive) value
        isValid &= validateTransactionAmountGreaterThanZero(line, ERROR_PREFIX);

        // Validate Units is Greater then Zero(thus positive) value
        isValid &= validateTransactionUnitsGreaterThanZero(line, ERROR_PREFIX);

        // Validates Units & Amount are equal.
        isValid &= validateTransactionUnitsAmountEqual(line, ERROR_PREFIX);

        return GlobalVariables.getMessageMap().getErrorCount() == 0;
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);
        isValid &= !GlobalVariables.getMessageMap().hasErrors();

        if (isValid) {
            LiabilityDecreaseDocument liabilitydecreaseDocument = (LiabilityDecreaseDocument) document;

            // Checks if Security Code is empty.
            if (isSecurityCodeEmpty(liabilitydecreaseDocument, true))
                return false;

            // Validates Security Code.
            if (!validateSecurityCode(liabilitydecreaseDocument, true))
                return false;

            // Checks if Security is Active
            isValid &= isSecurityActive(liabilitydecreaseDocument, true);

            // Validates Security class code
            isValid &= validateSecurityClassTypeCode(liabilitydecreaseDocument, true, LIABILITY_CLASS_CODE);

            // Checks if registration code is empty
            if (isRegistrationCodeEmpty(liabilitydecreaseDocument, true))
                return false;

            // Validate Registration code.
            if (!validateRegistrationCode(liabilitydecreaseDocument, true))
                return false;

            // Checks if registration code is active
            isValid &= isRegistrationCodeActive(liabilitydecreaseDocument, true);

            // Empty out the Source Tx Line in weird case they got entered.
            liabilitydecreaseDocument.getTargetTransactionLines().clear();

            // Obtaining all the transaction lines for validations
            List<EndowmentTransactionLine> txLines = new ArrayList<EndowmentTransactionLine>();
            txLines.addAll(liabilitydecreaseDocument.getSourceTransactionLines());

            for (int i = 0; i < liabilitydecreaseDocument.getSourceTransactionLines().size(); i++) 
            {
                EndowmentTransactionLine txLine = liabilitydecreaseDocument.getSourceTransactionLines().get(i);
                isValid &= validateLiabilityTransactionLine(txLine, i);
            }
        }

        return GlobalVariables.getMessageMap().getErrorCount() == 0;
    }

    /**
     * @see org.kuali.rice.kns.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        super.processCustomRouteDocumentBusinessRules(document);

        LiabilityDecreaseDocument liabilitydecreaseDocument = (LiabilityDecreaseDocument) document;

        // TODO:Remove below statement
        if (GlobalVariables.getMessageMap().getErrorCount() == 0) {
            List txLines = liabilitydecreaseDocument.getSourceTransactionLines();

            // Ensure atleast one Tx line is entered.
            if (txLines.size() != 0) {

            }
            else {
                putFieldError(EndowPropertyConstants.KEMID_CLOSE_CODE, EndowKeyConstants.KEMIDConstants.ERROR_INVALID_CLOSED_CODE);
            }

            // TODO:Remove below statement
            return GlobalVariables.getMessageMap().getErrorCount() == 0;
        }
        else {
            return false;
        }
    }
}

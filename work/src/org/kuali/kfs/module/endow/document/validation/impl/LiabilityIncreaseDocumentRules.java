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
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.document.AssetTransferDocument;
import org.kuali.kfs.module.cam.document.service.AssetTransferService;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.document.EndowmentSecurityDetailsDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocumentBase;
import org.kuali.kfs.module.endow.document.LiabilityIncreaseDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.rules.TransactionalDocumentRuleBase;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.MessageMap;

public class LiabilityIncreaseDocumentRules extends EndowmentTransactionLinesDocumentBaseRules {
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
            isValid &= validateLiabilityTransactionLine((EndowmentTransactionLinesDocumentBase) transLine, line, -1);
        }

        return GlobalVariables.getMessageMap().getErrorCount() == 0;
    }

    protected boolean validateLiabilityTransactionLine(EndowmentTransactionLinesDocumentBase endowmentTransactionLinesDocumentBase,EndowmentTransactionLine line, int index) {
        boolean isValid = true;

        // Obtain Prefix for Error fields in UI.
        String ERROR_PREFIX = getErrorPrefix(line, index);           

        isValid &= cashEndowTranCheck(endowmentTransactionLinesDocumentBase,line,ERROR_PREFIX);
        
        //Validate Units is Greater then Zero(thus positive) value
        isValid &= validateTransactionUnitsGreaterThanZero(line,ERROR_PREFIX);
        
        //Validates Units & Amount are equal.
        isValid &= validateTransactionUnitsAmountEqual(line,ERROR_PREFIX);
        
        return GlobalVariables.getMessageMap().getErrorCount() == 0;
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);
        isValid &= !GlobalVariables.getMessageMap().hasErrors();

        if (isValid) {
            LiabilityIncreaseDocument liabilityIncreaseDocument = (LiabilityIncreaseDocument) document;

            // Checks if Security Code is empty.
            if (isSecurityCodeEmpty(liabilityIncreaseDocument, false))
                return false;

            // Validates Security Code.
            if (!validateSecurityCode(liabilityIncreaseDocument, false))
                return false;

            // Checks if Security is Active
            isValid &= isSecurityActive(liabilityIncreaseDocument, false);

            // Validates Security class code
            isValid &= validateSecurityClassTypeCode(liabilityIncreaseDocument, false, LIABILITY_CLASS_CODE);

            // Checks if registration code is empty
            if (isRegistrationCodeEmpty(liabilityIncreaseDocument, false))
                return false;

            // Validate Registration code.
            if (!validateRegistrationCode(liabilityIncreaseDocument, false))
                return false;

            // Checks if registration code is active
            isValid &= isRegistrationCodeActive(liabilityIncreaseDocument, false);

            // Empty out the Source Tx Line in weird case they got entered.
            liabilityIncreaseDocument.getSourceTransactionLines().clear();

            // Obtaining all the transaction lines for validations
            List<EndowmentTransactionLine> txLines = new ArrayList<EndowmentTransactionLine>();
            txLines.addAll(liabilityIncreaseDocument.getTargetTransactionLines());

            for (int i = 0; i < liabilityIncreaseDocument.getTargetTransactionLines().size(); i++) 
            {
                EndowmentTransactionLine txLine = liabilityIncreaseDocument.getTargetTransactionLines().get(i);
                isValid &= validateLiabilityTransactionLine(liabilityIncreaseDocument,txLine,i);
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

        LiabilityIncreaseDocument liabilityIncreaseDocument = (LiabilityIncreaseDocument) document;

        //TODO:Remove below statement
        if (GlobalVariables.getMessageMap().getErrorCount() == 0) {
            List txLines = liabilityIncreaseDocument.getSourceTransactionLines();

            // Ensure atleast one Tx line is entered.
            if (txLines.size() != 0) {

            }
            else {
                //putFieldError(EndowPropertyConstants.KEMID_CLOSE_CODE, EndowKeyConstants.KEMIDConstants.ERROR_INVALID_CLOSED_CODE);
            }

            //TODO:Remove below statement
            return GlobalVariables.getMessageMap().getErrorCount() == 0;
        }
        else {
            return false;
        }
    }
}

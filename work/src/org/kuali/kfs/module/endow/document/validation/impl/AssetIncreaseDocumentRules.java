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

import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.module.endow.document.AssetIncreaseDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.rice.kns.document.Document;

public class AssetIncreaseDocumentRules extends EndowmentTransactionLinesDocumentBaseRules {


    /**
     * @see org.kuali.rice.kns.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.Document)
     */
    public boolean processCustomRouteDocumentBusinessRules(Document document) {
        AssetIncreaseDocument assetIncreaseDoc = (AssetIncreaseDocument) document;
        EndowmentTransactionSecurity endowmentTransactionSecurity = assetIncreaseDoc.getTargetTransactionSecurity();

        // Validate at least one Tx was entered.
        if (!transactionLineSizeGreaterThanZero(assetIncreaseDoc, false))
            return false;

        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);

        if (isValid) {


            if (isSecurityCodeEmpty(assetIncreaseDoc, false)) {
                return false;
            }
            if (!validateSecurityCode(assetIncreaseDoc, false)) {
                return false;
            }

            // Checks if Security is Active
            isValid &= isSecurityActive(assetIncreaseDoc, false);

            if (isValid) {
                isValid &= validateSecurityClassCodeTypeNotLiability(assetIncreaseDoc, false);
            }

            if (isRegistrationCodeEmpty(assetIncreaseDoc, false)) {
                return false;
            }
            if (!validateRegistrationCode(assetIncreaseDoc, false)) {

                return false;

            }
            // Checks if registration code is active
            isValid &= isRegistrationCodeActive(assetIncreaseDoc, false);
        }

        return isValid;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.validation.impl.EndowmentTransactionLinesDocumentBaseRules#processAddTransactionLineRules(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    @Override
    public boolean processAddTransactionLineRules(EndowmentTransactionLinesDocument document, EndowmentTransactionLine line) {

        AssetIncreaseDocument assetIncreaseDoc = (AssetIncreaseDocument) document;
        EndowmentTransactionSecurity endowmentTransactionSecurity = assetIncreaseDoc.getTargetTransactionSecurity();
        boolean isValid = super.processAddTransactionLineRules(document, line);

        if (isValid) {

            if (isSecurityCodeEmpty(assetIncreaseDoc, false)) {
                return false;
            }
            if (!validateSecurityCode(assetIncreaseDoc, false)) {
                return false;
            }
            if (isValid) {
                isValid &= validateSecurityClassCodeTypeNotLiability(assetIncreaseDoc, false);
            }

            // Checks if Security is Active
            isValid &= isSecurityActive(assetIncreaseDoc, false);
        }

        return isValid;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.validation.impl.EndowmentTransactionLinesDocumentBaseRules#validateTransactionLine(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine, int)
     */
    @Override
    protected boolean validateTransactionLine(EndowmentTransactionLinesDocument endowmentTransactionLinesDocument, EndowmentTransactionLine line, int index) {

        boolean isValid = super.validateTransactionLine(endowmentTransactionLinesDocument, line, index);
        EndowmentTargetTransactionLine targetTransactionLine = (EndowmentTargetTransactionLine) line;

        AssetIncreaseDocument assetIncreaseDocument = (AssetIncreaseDocument) endowmentTransactionLinesDocument;

        if (isSecurityCodeEmpty(assetIncreaseDocument, false))
            return false;
        if (!validateSecurityCode(assetIncreaseDocument, false))
            return false;

        isValid &= isSecurityActive(assetIncreaseDocument, false);
        isValid &= validateSecurityClassCodeTypeNotLiability(assetIncreaseDocument, false);

        if (isRegistrationCodeEmpty(assetIncreaseDocument, false))
            return false;
        if (!validateRegistrationCode(assetIncreaseDocument, false))
            return false;

        isValid &= isRegistrationCodeActive(assetIncreaseDocument, false);

        isValid &= super.validateTransactionLine(endowmentTransactionLinesDocument, line, index);

        if (isValid) {

            isValid &= checkCashTransactionEndowmentCode(endowmentTransactionLinesDocument, targetTransactionLine, getErrorPrefix(targetTransactionLine, index));

            if (endowmentTransactionLinesDocument.isErrorCorrectedDocument()) {
                // Validate Amount is Less than Zero.
                isValid &= validateTransactionAmountLessThanZero(line, getErrorPrefix(targetTransactionLine, index));

                // Validate Units is Less than Zero.
                isValid &= validateTransactionUnitsLessThanZero(line, getErrorPrefix(targetTransactionLine, index));
            }
            else {
                // Validate Greater then Zero(thus positive) value
                isValid &= validateTransactionAmountGreaterThanZero(line, getErrorPrefix(targetTransactionLine, index));

                // Validate Units is Greater than Zero.
                isValid &= validateTransactionUnitsGreaterThanZero(line, getErrorPrefix(targetTransactionLine, index));
            }

            isValid &= validateSecurityEtranChartMatch(endowmentTransactionLinesDocument, line, getErrorPrefix(targetTransactionLine, index), false);
        }

        return isValid;
    }

}

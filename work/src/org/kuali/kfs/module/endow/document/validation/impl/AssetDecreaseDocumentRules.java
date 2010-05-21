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

import java.math.BigDecimal;
import java.util.List;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.document.AssetDecreaseDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocumentBase;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.Document;

public class AssetDecreaseDocumentRules extends EndowmentTransactionLinesDocumentBaseRules {

    /**
     * @see org.kuali.kfs.module.endow.document.validation.impl.EndowmentTransactionLinesDocumentBaseRules#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        AssetDecreaseDocument assetDecreaseDocument = (AssetDecreaseDocument) document;

        // Validate at least one Tx was entered.
        if (!transactionLineSizeGreaterThanZero(assetDecreaseDocument, true))
            return false;

        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);

        if (isValid) {
            if (isSecurityCodeEmpty(assetDecreaseDocument, true))
                return false;
            if (!validateSecurityCode(assetDecreaseDocument, true))
                return false;
            isValid &= isSecurityActive(assetDecreaseDocument, true);
            isValid &= validateSecurityClassCodeTypeNotLiability(assetDecreaseDocument, true);

            if (isRegistrationCodeEmpty(assetDecreaseDocument, true))
                return false;
            if (!validateRegistrationCode(assetDecreaseDocument, true))
                return false;
            isValid &= isRegistrationCodeActive(assetDecreaseDocument, true);

        }

        return isValid;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.validation.impl.EndowmentTransactionLinesDocumentBaseRules#validateTransactionLine(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocumentBase,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine, int)
     */
    @Override
    protected boolean validateTransactionLine(EndowmentTransactionLinesDocumentBase endowmentTransactionLinesDocumentBase, EndowmentTransactionLine line, int index) {
        boolean isValid = true;
        AssetDecreaseDocument assetDecreaseDocument = (AssetDecreaseDocument) endowmentTransactionLinesDocumentBase;
        EndowmentSourceTransactionLine targetTransactionLine = (EndowmentSourceTransactionLine) line;

        if (isSecurityCodeEmpty(assetDecreaseDocument, true))
            return false;
        if (!validateSecurityCode(assetDecreaseDocument, true))
            return false;

        isValid &= isSecurityActive(assetDecreaseDocument, true);
        isValid &= validateSecurityClassCodeTypeNotLiability(assetDecreaseDocument, true);

        if (isRegistrationCodeEmpty(assetDecreaseDocument, true))
            return false;
        if (!validateRegistrationCode(assetDecreaseDocument, true))
            return false;

        isValid &= isRegistrationCodeActive(assetDecreaseDocument, true);
        isValid &= super.validateTransactionLine(endowmentTransactionLinesDocumentBase, line, index);


        if (isValid) {
            isValid &= checkCashTransactionEndowmentCode(endowmentTransactionLinesDocumentBase, targetTransactionLine, getErrorPrefix(targetTransactionLine, index));

            if (EndowConstants.TransactionSubTypeCode.CASH.equalsIgnoreCase(assetDecreaseDocument.getTransactionSubTypeCode())) {
                // Validate Greater then Zero(thus positive) value
                isValid &= validateTransactionAmountGreaterThanZero(line, getErrorPrefix(targetTransactionLine, index));
            }

            isValid &= validateTransactionUnitsGreaterThanZero(line, getErrorPrefix(targetTransactionLine, index));

            isValid &= validateSufficientUnits(assetDecreaseDocument, line, index);
        }

        return isValid;
    }

    /**
     * Validates that the KEMID has sufficient units in the tax lots to perform the transaction.
     * 
     * @param endowmentTransactionLinesDocumentBase
     * @param line
     * @param index
     * @return true if valid, false otherwise
     */
    private boolean validateSufficientUnits(EndowmentTransactionLinesDocumentBase endowmentTransactionLinesDocumentBase, EndowmentTransactionLine line, int index) {
        EndowmentTransactionSecurity endowmentTransactionSecurity = getEndowmentTransactionSecurity(endowmentTransactionLinesDocumentBase, true);
        boolean isValid = true;

        List<HoldingTaxLot> holdingTaxLots = SpringContext.getBean(HoldingTaxLotService.class).getAllTaxLots(line.getKemid(), endowmentTransactionSecurity.getSecurityID(), endowmentTransactionSecurity.getRegistrationCode(), line.getTransactionIPIndicatorCode());

        BigDecimal totalTaxLotsUnits = BigDecimal.ZERO;

        if (holdingTaxLots != null && holdingTaxLots.size() > 0) {
            for (HoldingTaxLot holdingTaxLot : holdingTaxLots) {
                totalTaxLotsUnits = totalTaxLotsUnits.add(holdingTaxLot.getUnits());
            }
        }

        if (line.getTransactionUnits().bigDecimalValue().compareTo(totalTaxLotsUnits) == 1) {
            isValid = false;
            putFieldError(getErrorPrefix(line, index) + EndowPropertyConstants.TRANSACTION_LINE_TRANSACTION_UNITS, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_ASSET_DECREASE_INSUFFICIENT_UNITS);
        }
        return isValid;
    }

}

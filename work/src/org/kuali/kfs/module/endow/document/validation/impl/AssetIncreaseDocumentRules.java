/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.endow.document.validation.impl;

import java.math.BigDecimal;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.module.endow.document.AssetIncreaseDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.rice.krad.document.Document;

public class AssetIncreaseDocumentRules extends EndowmentTransactionLinesDocumentBaseRules {


    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.krad.document.Document)
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

        if (isValid) {
            isValid = validateTaxLotsCostAndTransactionAmountLessOrEqualToSecurityCommitment(assetIncreaseDoc);
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

    /**
     * If the SECURITY_ID has a class code type A (Alternative Investments), the system must validate that the total
     * END_SEC_T: SEC_CVAL for the SECURITY_ID plus the END_TRAN_LN_T:TRAN_AMT does not exceed the value in END_SEC_T: CMTMNT_AMT
     * for the Security. If it does, the transaction should not be allowed.
     * 
     * @return true if valid, false otherwise
     */
    protected boolean validateTaxLotsCostAndTransactionAmountLessOrEqualToSecurityCommitment(AssetIncreaseDocument assetIncreaseDocument) {
        boolean isValid = true;

        if (assetIncreaseDocument.getTargetTransactionSecurity() != null && assetIncreaseDocument.getTargetTransactionSecurity().getSecurity() != null && assetIncreaseDocument.getTargetTransactionSecurity().getSecurity().getClassCode() != null && EndowConstants.ClassCodeTypes.ALTERNATIVE_INVESTMENT.equalsIgnoreCase(assetIncreaseDocument.getTargetTransactionSecurity().getSecurity().getClassCode().getClassCodeType())) {

            BigDecimal totalTransactionLinesAmt = BigDecimal.ZERO;
            BigDecimal securityCommitmentAmt = BigDecimal.ZERO;
            BigDecimal securityCarryValue = BigDecimal.ZERO;

            if (assetIncreaseDocument.getTargetTransactionLines() != null) {
                for (EndowmentTransactionLine transactionLine : assetIncreaseDocument.getTargetTransactionLines()) {

                    // add to total amount
                    totalTransactionLinesAmt = totalTransactionLinesAmt.add(transactionLine.getTransactionAmount().bigDecimalValue());
                }
            }

            if (assetIncreaseDocument.getTargetTransactionSecurity() != null && assetIncreaseDocument.getTargetTransactionSecurity().getSecurity() != null) {
                if (assetIncreaseDocument.getTargetTransactionSecurity().getSecurity().getCommitmentAmount() != null) {
                    securityCommitmentAmt = assetIncreaseDocument.getTargetTransactionSecurity().getSecurity().getCommitmentAmount();
                }
                if (assetIncreaseDocument.getTargetTransactionSecurity().getSecurity().getCarryValue() != null) {
                    securityCarryValue = assetIncreaseDocument.getTargetTransactionSecurity().getSecurity().getCarryValue();
                }
            }

            isValid = (securityCarryValue.add(totalTransactionLinesAmt)).compareTo(securityCommitmentAmt) <= 0;

            if (!isValid) {
                putFieldError(getErrorPrefix(assetIncreaseDocument.getTargetTransactionLine(0), 0) + EndowPropertyConstants.TRANSACTION_LINE_TRANSACTION_AMOUNT, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_SECURITY_COMMITMENT_AMT);
            }
        }

        return isValid;
    }
}

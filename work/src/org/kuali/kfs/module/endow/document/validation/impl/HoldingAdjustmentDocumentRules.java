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

import java.math.BigDecimal;
import java.util.List;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionTaxLotLine;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.document.EndowmentSecurityDetailsDocument;
import org.kuali.kfs.module.endow.document.EndowmentTaxLotLinesDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.HoldingAdjustmentDocument;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.document.validation.DeleteTaxLotLineRule;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.ObjectUtils;

public class HoldingAdjustmentDocumentRules extends EndowmentTransactionLinesDocumentBaseRules implements DeleteTaxLotLineRule<EndowmentTaxLotLinesDocument, EndowmentTransactionTaxLotLine, EndowmentTransactionLine, Number, Number> {

    /**
     * @see org.kuali.kfs.module.endow.document.validation.impl.EndowmentTransactionLinesDocumentBaseRules#processAddTransactionLineRules(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    @Override
    public boolean processAddTransactionLineRules(EndowmentTransactionLinesDocument document, EndowmentTransactionLine line) {
        boolean isValid = true;

        HoldingAdjustmentDocument holdingAdjustmentDocument = (HoldingAdjustmentDocument) document;

        isValid &= validateSecurity(isValid, holdingAdjustmentDocument, true);
        isValid &= validateSecurityClassCodeTypeNotLiability(holdingAdjustmentDocument, true);

        isValid &= validateRegistration(isValid, holdingAdjustmentDocument, true);

        isValid &= canOnlyAddSourceOrTargetTransactionLines(holdingAdjustmentDocument, line, -1);

        isValid &= !checkIfBothTransactionAmountAndUnitAdjustmentAmountEmpty(line, -1);
        isValid &= !checkIfBothTransactionAmountAndUnitAdjustmentAmountEntered(line, -1);

        if (isValid) {
            isValid &= super.processAddTransactionLineRules(holdingAdjustmentDocument, line);

            if (isValid) {
                isValid &= validateKemidHasTaxLots(holdingAdjustmentDocument, line, -1);
            }
        }

        return isValid;

    }

    /**
     * Check if the transaction amount and unit adjustment amount are empty
     * 
     * @return true if valid, false otherwise
     */
    protected boolean checkIfBothTransactionAmountAndUnitAdjustmentAmountEmpty(EndowmentTransactionLine line, int index) {
        if ((ObjectUtils.isNull(line.getTransactionAmount()) || line.getTransactionAmount().isZero()) && (ObjectUtils.isNull(line.getUnitAdjustmentAmount()) || (line.getUnitAdjustmentAmount().compareTo(BigDecimal.ZERO) == 0))) {
            putFieldError(getErrorPrefix(line, index) + EndowConstants.TRANSACTION_LINE_ERRORS, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_BOTH_AMOUNTS_BLANK);
            return true;
        }
        return false;
    }

    /**
     * Check if the transaction amount and unit adjustment amount are filled in then do not allow the validation to succeed
     * 
     * @return true if valid, false otherwise
     */
    protected boolean checkIfBothTransactionAmountAndUnitAdjustmentAmountEntered(EndowmentTransactionLine line, int index) {
        if ((ObjectUtils.isNotNull(line.getTransactionAmount()) && !line.getTransactionAmount().isZero()) && (ObjectUtils.isNotNull(line.getUnitAdjustmentAmount()) && (line.getUnitAdjustmentAmount().compareTo(BigDecimal.ZERO) == 0))) {
            putFieldError(getErrorPrefix(line, index) + EndowConstants.TRANSACTION_LINE_ERRORS, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_BOTH_AMOUNTS_ENTERED);
            return true;
        }
        return false;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.validation.impl.EndowmentTransactionLinesDocumentBaseRules#validateTransactionLine(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine, int)
     */
    @Override
    protected boolean validateTransactionLine(EndowmentTransactionLinesDocument endowmentTransactionLinesDocument, EndowmentTransactionLine line, int index) {

        boolean isValid = !checkIfBothTransactionAmountAndUnitAdjustmentAmountEmpty(line, index);
        // isValid &= !checkIfBothTransactionAmountAndUnitAdjustmentAmountEntered(line, index);
        if (isValid) {
            isValid &= super.validateTransactionLine(endowmentTransactionLinesDocument, line, index);
        }

        return isValid;
    }

    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = true;
        HoldingAdjustmentDocument holdingAdjustmentDocument = (HoldingAdjustmentDocument) document;

        isValid &= validateSecurity(isValid, holdingAdjustmentDocument, true);
        isValid &= validateSecurityClassCodeTypeNotLiability(holdingAdjustmentDocument, true);

        isValid &= validateRegistration(isValid, holdingAdjustmentDocument, true);

        if (isValid) {
            isValid &= hasOnlySourceOrTargetTransactionLines(holdingAdjustmentDocument);
            isValid &= super.processCustomRouteDocumentBusinessRules(document);

            List<EndowmentTransactionLine> transLines = (holdingAdjustmentDocument.getSourceTransactionLines() != null && holdingAdjustmentDocument.getSourceTransactionLines().size() > 0) ? holdingAdjustmentDocument.getSourceTransactionLines() : holdingAdjustmentDocument.getTargetTransactionLines();

            if (transLines.isEmpty()) {
                putFieldError(EndowConstants.TRANSACTION_LINE_ERRORS, EndowKeyConstants.HoldingAdjustmentConstants.ERROR_HOLDING_ADJUSTMENT_BOTH_SOURCE_AND_TARGET_TRAN_LINES_BLANK);
                isValid =  false;
            } else {
                for (int i = 0; i < transLines.size(); i++) {
                    EndowmentTransactionLine transLine = transLines.get(i);
                    isValid &= validateTaxLots(holdingAdjustmentDocument, transLine, i);
                }
            }
        }

        return isValid;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.validation.impl.EndowmentTransactionalDocumentBaseRule#validateSecurityClassTypeCode(org.kuali.kfs.module.endow.document.EndowmentSecurityDetailsDocument,
     *      boolean, java.lang.String)
     */
    @Override
    protected boolean validateSecurityClassTypeCode(EndowmentSecurityDetailsDocument document, boolean isSource, String classCodeType) {
        return true;
    }

    /**
     * Validates that the document has only source or target transaction lines, not both.
     * 
     * @param endowmentTransactionLinesDocument
     * @return true if valid, false otherwise
     */
    protected boolean hasOnlySourceOrTargetTransactionLines(EndowmentTransactionLinesDocument endowmentTransactionLinesDocument) {
        boolean isValid = true;

        boolean hasSourceTransLines = endowmentTransactionLinesDocument.getSourceTransactionLines() != null && endowmentTransactionLinesDocument.getSourceTransactionLines().size() > 0;
        boolean hasTargetTransLines = endowmentTransactionLinesDocument.getTargetTransactionLines() != null && endowmentTransactionLinesDocument.getTargetTransactionLines().size() > 0;

        if (hasSourceTransLines && hasTargetTransLines) {
            isValid = false;
            putFieldError(EndowConstants.TRANSACTION_LINE_ERRORS, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_UNIT_SHARE_ADJUSTMENT_ADD_ONLY_SOURCE_OR_TARGET_TRAN_LINES);
        }
        return isValid;
    }

    /**
     * Validates that the user can only enter to or from transaction lines not both.
     * 
     * @param endowmentTransactionLinesDocument
     * @param transLine
     * @param index
     * @return true if valid, false otherwise
     */
    protected boolean canOnlyAddSourceOrTargetTransactionLines(EndowmentTransactionLinesDocument endowmentTransactionLinesDocument, EndowmentTransactionLine transLine, int index) {
        boolean isValid = true;

        boolean isSource = transLine instanceof EndowmentSourceTransactionLine;

        // the user can only enter to or from transaction lines not both
        if (isSource) {
            if (endowmentTransactionLinesDocument.getTargetTransactionLines() != null && endowmentTransactionLinesDocument.getTargetTransactionLines().size() > 0) {
                isValid = false;
                putFieldError(getErrorPrefix(transLine, index), EndowKeyConstants.HoldingAdjustmentConstants.ERROR_HOLDING_ADJUSTMENT_ADD_ONLY_SOURCE_OR_TARGET_TRAN_LINES);
            }
        }
        else {
            if (endowmentTransactionLinesDocument.getSourceTransactionLines() != null && endowmentTransactionLinesDocument.getSourceTransactionLines().size() > 0) {
                isValid = false;
                putFieldError(getErrorPrefix(transLine, index), EndowKeyConstants.HoldingAdjustmentConstants.ERROR_HOLDING_ADJUSTMENT_ADD_ONLY_SOURCE_OR_TARGET_TRAN_LINES);
            }
        }

        return isValid;
    }

    /**
     * Checks that there are holding tax lots with positive units for the given kemid, security, registration code and IP indicator.
     * 
     * @param endowmentTransactionLinesDocument
     * @param transLine
     * @param index
     * @return true if there are tax lots that meet the criteria, false otherwise
     */
    protected boolean validateKemidHasTaxLots(EndowmentTransactionLinesDocument endowmentTransactionLinesDocument, EndowmentTransactionLine transLine, int index) {
        boolean isValid = true;

        HoldingAdjustmentDocument holdingAdjustmentDocument = (HoldingAdjustmentDocument) endowmentTransactionLinesDocument;
        EndowmentTransactionSecurity endowmentTransactionSecurity = holdingAdjustmentDocument.getSourceTransactionSecurity();

        HoldingTaxLotService holdingTaxLotService = SpringContext.getBean(HoldingTaxLotService.class);
        List<HoldingTaxLot> holdingTaxLots = holdingTaxLotService.getAllTaxLotsWithPositiveUnits(transLine.getKemid(), endowmentTransactionSecurity.getSecurityID(), endowmentTransactionSecurity.getRegistrationCode(), transLine.getTransactionIPIndicatorCode());

        if (holdingTaxLots == null || holdingTaxLots.size() == 0) {
            isValid = false;
            putFieldError(getErrorPrefix(transLine, index) + EndowPropertyConstants.KEMID, EndowKeyConstants.HoldingAdjustmentConstants.ERROR_HOLDING_ADJUSTMENT_NO_TAX_LOTS_FOUND);
        }

        return isValid;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.validation.DeleteTaxLotLineRule#processDeleteTaxLotLineRules(org.kuali.kfs.module.endow.document.EndowmentTaxLotLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionTaxLotLine,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine, java.lang.Number, java.lang.Number)
     */
    public boolean processDeleteTaxLotLineRules(EndowmentTaxLotLinesDocument endowmentTaxLotLinesDocument, EndowmentTransactionTaxLotLine endowmentTransactionTaxLotLine, EndowmentTransactionLine endowmentTransactionLine, Number transLineIndex, Number taxLotLineIndex) {
        boolean isValid = true;

        if (endowmentTransactionLine.getTaxLotLines() != null && endowmentTransactionLine.getTaxLotLines().size() <= 1) {
            isValid = false;
            putFieldError(getErrorPrefix(endowmentTransactionLine, (Integer) transLineIndex) + EndowPropertyConstants.KEMID, EndowKeyConstants.HoldingAdjustmentConstants.ERROR_HOLDING__ADJUSTMENT_TRANS_LINE_MUST_HAVE_AT_LEAST_ONE_TAX_LOT);
        }

        return isValid;
    }
}

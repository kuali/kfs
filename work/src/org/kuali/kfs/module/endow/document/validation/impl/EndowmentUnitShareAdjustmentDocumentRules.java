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
import org.kuali.kfs.module.endow.document.EndowmentUnitShareAdjustmentDocument;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.document.validation.DeleteTaxLotLineRule;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.Document;

public class EndowmentUnitShareAdjustmentDocumentRules extends EndowmentTransactionLinesDocumentBaseRules implements DeleteTaxLotLineRule<EndowmentTaxLotLinesDocument, EndowmentTransactionTaxLotLine, EndowmentTransactionLine, Number, Number> {

    /**
     * @see org.kuali.kfs.module.endow.document.validation.impl.EndowmentTransactionLinesDocumentBaseRules#processAddTransactionLineRules(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    @Override
    public boolean processAddTransactionLineRules(EndowmentTransactionLinesDocument document, EndowmentTransactionLine line) {
        boolean isValid = true;

        EndowmentUnitShareAdjustmentDocument endowmentUnitShareAdjustmentDocument = (EndowmentUnitShareAdjustmentDocument) document;

        isValid &= validateSecurity(isValid, endowmentUnitShareAdjustmentDocument, true);
        isValid &= validateSecurityClassCodeTypeNotLiability(endowmentUnitShareAdjustmentDocument, true);

        isValid &= validateRegistration(isValid, endowmentUnitShareAdjustmentDocument, true);

        isValid &= canOnlyAddSourceOrTargetTransactionLines(endowmentUnitShareAdjustmentDocument, line, -1);

        if (isValid) {
            isValid &= super.processAddTransactionLineRules(endowmentUnitShareAdjustmentDocument, line);

            if (isValid) {
                isValid &= validateKemidHasTaxLots(endowmentUnitShareAdjustmentDocument, line, -1);
            }
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

        if (isValid) {
            isValid &= validateTransactionUnitsGreaterThanZero(line, getErrorPrefix(line, index));
        }
        return isValid;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.validation.impl.EndowmentTransactionLinesDocumentBaseRules#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = true;
        EndowmentUnitShareAdjustmentDocument endowmentUnitShareAdjustmentDocument = (EndowmentUnitShareAdjustmentDocument) document;

        isValid &= validateSecurity(isValid, endowmentUnitShareAdjustmentDocument, true);
        isValid &= validateSecurityClassCodeTypeNotLiability(endowmentUnitShareAdjustmentDocument, true);

        isValid &= validateRegistration(isValid, endowmentUnitShareAdjustmentDocument, true);

        if (isValid) {
            isValid &= hasOnlySourceOrTargetTransactionLines(endowmentUnitShareAdjustmentDocument);
            isValid &= super.processCustomSaveDocumentBusinessRules(document);

            List<EndowmentTransactionLine> transLines = (endowmentUnitShareAdjustmentDocument.getSourceTransactionLines() != null && endowmentUnitShareAdjustmentDocument.getSourceTransactionLines().size() > 0) ? endowmentUnitShareAdjustmentDocument.getSourceTransactionLines() : endowmentUnitShareAdjustmentDocument.getTargetTransactionLines();

            for (int i = 0; i < transLines.size(); i++) {
                EndowmentTransactionLine transLine = transLines.get(i);
                validateTaxLotsRelateToKemid(endowmentUnitShareAdjustmentDocument, transLine, i);
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
     * @see org.kuali.kfs.module.endow.document.validation.impl.EndowmentTransactionLinesDocumentBaseRules#hasEtranCode(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument)
     */
    @Override
    protected boolean hasEtranCode(EndowmentTransactionLinesDocument endowmentTransactionLinesDocument) {
        return false;
    }

    /**
     * Validates that the document has only source or target transaction lines, not both.
     * 
     * @param endowmentTransactionLinesDocument
     * @return true if valid, false otherwise
     */
    private boolean hasOnlySourceOrTargetTransactionLines(EndowmentTransactionLinesDocument endowmentTransactionLinesDocument) {
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
    private boolean canOnlyAddSourceOrTargetTransactionLines(EndowmentTransactionLinesDocument endowmentTransactionLinesDocument, EndowmentTransactionLine transLine, int index) {
        boolean isValid = true;

        boolean isSource = transLine instanceof EndowmentSourceTransactionLine;

        // the user can only enter to or from transaction lines not both
        if (isSource) {
            if (endowmentTransactionLinesDocument.getTargetTransactionLines() != null && endowmentTransactionLinesDocument.getTargetTransactionLines().size() > 0) {

                isValid = false;
                putFieldError(getErrorPrefix(transLine, index), EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_UNIT_SHARE_ADJUSTMENT_ADD_ONLY_SOURCE_OR_TARGET_TRAN_LINES);
            }
        }
        else {
            if (endowmentTransactionLinesDocument.getSourceTransactionLines() != null && endowmentTransactionLinesDocument.getSourceTransactionLines().size() > 0) {

                isValid = false;
                putFieldError(getErrorPrefix(transLine, index), EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_UNIT_SHARE_ADJUSTMENT_ADD_ONLY_SOURCE_OR_TARGET_TRAN_LINES);
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
    private boolean validateKemidHasTaxLots(EndowmentTransactionLinesDocument endowmentTransactionLinesDocument, EndowmentTransactionLine transLine, int index) {
        boolean isValid = true;
        HoldingTaxLotService holdingTaxLotService = SpringContext.getBean(HoldingTaxLotService.class);
        EndowmentUnitShareAdjustmentDocument unitShareAdjustmentDocument = (EndowmentUnitShareAdjustmentDocument) endowmentTransactionLinesDocument;
        EndowmentTransactionSecurity endowmentTransactionSecurity = unitShareAdjustmentDocument.getSourceTransactionSecurity();

        List<HoldingTaxLot> holdingTaxLots = holdingTaxLotService.getAllTaxLotsWithPositiveUnits(transLine.getKemid(), endowmentTransactionSecurity.getSecurityID(), endowmentTransactionSecurity.getRegistrationCode(), transLine.getTransactionIPIndicatorCode());

        if (holdingTaxLots == null || holdingTaxLots.size() == 0) {
            isValid = false;
            putFieldError(getErrorPrefix(transLine, index) + EndowPropertyConstants.KEMID, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_UNIT_SHARE_ADJUSTMENT_NO_TAX_LOTS_FOUND);
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
            putFieldError(getErrorPrefix(endowmentTransactionLine, (Integer) transLineIndex) + EndowPropertyConstants.KEMID, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_UNIT_SHARE_ADJUSTMENT_TRANS_LINE_MUST_HAVE_AT_LEAST_ONE_TAX_LOT);
        }

        return isValid;
    }
}

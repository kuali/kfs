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

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionTaxLotLine;
import org.kuali.kfs.module.endow.document.CorporateReorganizationDocument;
import org.kuali.kfs.module.endow.document.EndowmentSecurityDetailsDocument;
import org.kuali.kfs.module.endow.document.EndowmentTaxLotLinesDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.validation.DeleteTaxLotLineRule;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;

public class CorporateReorganizationDocumentRules extends EndowmentTransactionLinesDocumentBaseRules implements DeleteTaxLotLineRule<EndowmentTaxLotLinesDocument, EndowmentTransactionTaxLotLine, EndowmentTransactionLine, Number, Number> {


    /**
     * @see org.kuali.kfs.module.endow.document.validation.AddTransactionLineRule#processAddTransactionLineRules(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    @Override
    public boolean processAddTransactionLineRules(EndowmentTransactionLinesDocument transLineDocument, EndowmentTransactionLine line) {
        boolean isValid = true;

        CorporateReorganizationDocument corporateReorganizationDocument = (CorporateReorganizationDocument) transLineDocument;

        // Validate source security id and registration code.
        isValid &= validateSecurity(isValid, corporateReorganizationDocument, true);
        isValid &= validateRegistration(isValid, corporateReorganizationDocument, true);
        
        // Validate target security id and registration code.
        isValid &= validateSecurity(isValid, corporateReorganizationDocument, false);
        isValid &= validateRegistration(isValid, corporateReorganizationDocument, false);

        // there can be only one source transaction line
        isValid &= validateOnlyOneSourceTransactionLine(true, corporateReorganizationDocument, line, -1);
        
        if (isValid) {
            isValid &= super.processAddTransactionLineRules(corporateReorganizationDocument, line);
        }

        if (isValid) {
            isValid &= validateCorpReorganizationTransferTransactionLine(true, corporateReorganizationDocument, line, -1, -1);
        }

        if (isValid) {
            // Source and target security lines cannot be the same.
            isValid &= validateNonDuplicateSecurityCodes(corporateReorganizationDocument);            
        }
        
        return GlobalVariables.getMessageMap().getErrorCount() == 0;
    }

    /**
     * Validates that one and only one source transaction line can be added.
     * 
     * @param validateForAdd tells whether the validation is for add or not
     * @param endowmentTransactionLinesDocument
     * @return true if valid, false otherwise
     */
    protected boolean validateOnlyOneSourceTransactionLine(boolean validateForAdd, EndowmentTransactionLinesDocument endowmentTransactionLinesDocument, EndowmentTransactionLine line, int index) {
        boolean isValid = true;

        if (line instanceof EndowmentSourceTransactionLine) {

            // if we do validation upon adding a new transaction line make sure we don't allow more than one line to be added; if
            // there
            // is already one source transaction line than validation will fail as no more source transaction line can be added
            if (validateForAdd) {
                if (endowmentTransactionLinesDocument.getSourceTransactionLines() != null && endowmentTransactionLinesDocument.getSourceTransactionLines().size() >= 1) {
                    isValid = false;
                    putFieldError(getErrorPrefix(line, index), EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_SECURITY_TRANSFER_ONE_AND_ONLY_ONE_SOURCE_TRANS_LINE);
                }
            }
            // if we do validation on save or submit we have to make sure that there is one and only one source transaction line
            else {
                if (endowmentTransactionLinesDocument.getSourceTransactionLines() != null && endowmentTransactionLinesDocument.getSourceTransactionLines().size() != 1) {
                    isValid = false;
                    putFieldError(getErrorPrefix(line, index), EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_SECURITY_TRANSFER_ONE_AND_ONLY_ONE_SOURCE_TRANS_LINE);
                }
            }
        }

        return isValid;

    }

    /**
     * Validate Security Transfer Transaction Line.
     * 
     * @param endowmentTransactionLinesDocumentBase
     * @param line
     * @param index
     * @return
     */
    protected boolean validateCorpReorganizationTransferTransactionLine(boolean isAdd, EndowmentTransactionLinesDocument endowmentTransactionLinesDocument, EndowmentTransactionLine line, int transLineIndex, int taxLotIndex) {
        boolean isValid = super.validateTransactionLine(endowmentTransactionLinesDocument, line, transLineIndex);

        if (isValid) {
            // Obtain Prefix for Error fields in UI.
            String ERROR_PREFIX = getErrorPrefix(line, transLineIndex);

            // Validate Units is Greater then Zero(thus positive) value
            isValid &= validateTransactionUnitsGreaterThanZero(line, ERROR_PREFIX);

            if (line instanceof EndowmentSourceTransactionLine) {
                // Validate if Sufficient Units are Available
                isValid &= validateSufficientUnits(isAdd, endowmentTransactionLinesDocument, line, transLineIndex, taxLotIndex);
            }

            // Check if value of Endowment is being reduced.
            checkWhetherReducePermanentlyRestrictedFund(line, ERROR_PREFIX);
        }

        return GlobalVariables.getMessageMap().getErrorCount() == 0;
    }

    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);

        if (isValid) {
            CorporateReorganizationDocument corporateReorganizationDocument = (CorporateReorganizationDocument) document;

            // Validate Security
            isValid &= validateSecurity(isValid, corporateReorganizationDocument, true);

            // Validate Registration code.
            isValid &= validateRegistration(isValid, corporateReorganizationDocument, true);

            // Validate at least one Source Tx was entered.
            if (!transactionLineSizeGreaterThanZero(corporateReorganizationDocument, true))
                return false;

            // Validate at least one Target Tx was entered.
            if (!transactionLineSizeGreaterThanZero(corporateReorganizationDocument, false))
                return false;

            // Obtaining all the transaction lines for validations
            List<EndowmentTransactionLine> txLines = new ArrayList<EndowmentTransactionLine>();
            txLines.addAll(corporateReorganizationDocument.getSourceTransactionLines());
            txLines.addAll(corporateReorganizationDocument.getTargetTransactionLines());

            // Validate All the Transaction Lines.
            for (int i = 0; i < txLines.size(); i++) {
                EndowmentTransactionLine txLine = txLines.get(i);
                isValid &= validateCorpReorganizationTransferTransactionLine(false, corporateReorganizationDocument, txLine, i, -1);
                isValid &= validateTaxLots(corporateReorganizationDocument, txLine, i);
                isValid &= validateTotalUnits(corporateReorganizationDocument, txLine, i);
            }

//            isValid = &= validateSourceTransSecurityEtranEqual(corporateReorganizationDocument);
//            isValid = &= validateTargetTransSecurityEtranEqual(corporateReorganizationDocument);

        }

        return GlobalVariables.getMessageMap().getErrorCount() == 0;
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
     * @see org.kuali.kfs.module.endow.document.validation.impl.EndowmentTransactionLinesDocumentBaseRules#processRefreshTransactionLineRules(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine, java.lang.Number)
     */
    @Override
    public boolean processRefreshTransactionLineRules(EndowmentTransactionLinesDocument endowmentTransactionLinesDocument, EndowmentTransactionLine endowmentTransactionLine, Number index) {
        boolean isValid = super.processRefreshTransactionLineRules(endowmentTransactionLinesDocument, endowmentTransactionLine, index);
        if (isValid) {
            isValid &= validateCorpReorganizationTransferTransactionLine(false, endowmentTransactionLinesDocument, endowmentTransactionLine, (Integer) index, -1);
        }
        return isValid;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.validation.DeleteTaxLotLineRule#processDeleteTaxLotLineRules(org.kuali.kfs.module.endow.document.EndowmentTaxLotLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionTaxLotLine,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine, java.lang.Number, java.lang.Number)
     */
    public boolean processDeleteTaxLotLineRules(EndowmentTaxLotLinesDocument endowmentTaxLotLinesDocument, EndowmentTransactionTaxLotLine endowmentTransactionTaxLotLine, EndowmentTransactionLine endowmentTransactionLine, Number index, Number numberX) {

        boolean isValid = true;
        isValid &= validateTransactionLine(endowmentTaxLotLinesDocument, endowmentTransactionLine, (Integer) index);
        if (isValid) {
            isValid &= validateCorpReorganizationTransferTransactionLine(false, endowmentTaxLotLinesDocument, endowmentTransactionLine, (Integer) index, (Integer) numberX);
        }
        return isValid;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.validation.impl.EndowmentTransactionLinesDocumentBaseRules#hasEtranCode(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument)
     */
    @Override
    protected boolean hasEtranCode(EndowmentTransactionLinesDocument endowmentTransactionLinesDocument) {

        return false;
    }
    
}

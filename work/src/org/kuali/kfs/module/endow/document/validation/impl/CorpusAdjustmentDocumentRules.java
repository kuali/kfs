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

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.document.CorpusAdjustmentDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocumentBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;

public class CorpusAdjustmentDocumentRules extends EndowmentTransactionLinesDocumentBaseRules {

    /**
     * @see org.kuali.kfs.module.endow.document.validation.AddTransactionLineRule#processAddTransactionLineRules(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    @Override
    public boolean processAddTransactionLineRules(EndowmentTransactionLinesDocument transLineDocument, EndowmentTransactionLine line) {
        return validateTransactionLine((EndowmentTransactionLinesDocumentBase) transLineDocument, line, -1);
    }

    /**
     * This method validates the Tx line but from Corpus Adjustment document.
     * 
     * @param endowmentTransactionLinesDocumentBase
     * @param line
     * @param index
     * @return true if validation successful else false.
     */
    protected boolean validateTransactionLine(EndowmentTransactionLinesDocument endowmentTransactionLinesDocument, EndowmentTransactionLine line, int index) {
        boolean isValid = true;
        isValid &= !GlobalVariables.getMessageMap().hasErrors();

        String ERROR_PREFIX = getErrorPrefix(line, index);

        if (isValid) {
            GlobalVariables.getMessageMap().clearErrorPath();

            // General not null validation for KemID
            SpringContext.getBean(DictionaryValidationService.class).validateAttributeRequired(line.getClass().getName(), "kemid", line.getKemid(), false, ERROR_PREFIX + EndowPropertyConstants.KEMID);

            // Validate KemID
            if (!validateKemId(line, ERROR_PREFIX))
                return false;

            // Active Kemid
            isValid &= isActiveKemId(line, ERROR_PREFIX);

            // Validate no restriction transaction restriction
            isValid &= validateNoTransactionRestriction(line, ERROR_PREFIX);

            // Validate Income/Principal DropDown
            SpringContext.getBean(DictionaryValidationService.class).validateAttributeRequired(line.getClass().getName(), "transactionIPIndicatorCode", line.getTransactionIPIndicatorCode(), false, ERROR_PREFIX + EndowPropertyConstants.TRANSACTION_IPINDICATOR);
            isValid &= GlobalVariables.getMessageMap().getErrorCount() == 0 ? true : false;
            if (!isValid)
                return isValid;

            // This error is checked in addition save rule method since the sub type is used for determining chart code.
            if (!isSubTypeEmpty(endowmentTransactionLinesDocument))
                return false;

            // If non-cash transactions
            if (nonCashTransaction(endowmentTransactionLinesDocument)) {
                // Validate if a KEMID can have a principal transaction when IP indicator is P
                if (!canKEMIDHaveAPrincipalTransaction(line, ERROR_PREFIX))
                    return false;
                // Set Corpus Indicator
                line.setCorpusIndicator(true);
            }
        }

        return GlobalVariables.getMessageMap().getErrorCount() == 0;
    }

    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.krad.document.Document)
     */
    public boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);

        if (isValid) {
            CorpusAdjustmentDocument corpusAdjustmentDocument = (CorpusAdjustmentDocument) document;
            if (corpusAdjustmentDocument.getSourceTransactionLines().isEmpty() && corpusAdjustmentDocument.getTargetTransactionLines().isEmpty()) {
                putFieldError(EndowConstants.TRANSACTION_LINE_ERRORS, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_DOCUMENT_CORPUS_ADJUSTMENT_TRANSACTION_LINES_COUNT_GREATER_THAN_ONE);
                isValid = false;
            }
        }
        return isValid;
    }
}

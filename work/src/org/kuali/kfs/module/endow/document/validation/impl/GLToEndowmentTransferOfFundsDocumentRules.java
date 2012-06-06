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

import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.GLToEndowmentTransferOfFundsDocument;
import org.kuali.kfs.module.endow.document.service.EndowmentTransactionLinesDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.document.Document;

public class GLToEndowmentTransferOfFundsDocumentRules extends EndowmentAccountingLinesDocumentBaseRules {

    /**
     * @see org.kuali.kfs.module.endow.document.validation.impl.EndowmentTransactionLinesDocumentBaseRules#validateTransactionLine(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine, int)
     */
    @Override
    protected boolean validateTransactionLine(EndowmentTransactionLinesDocument endowmentTransactionLinesDocument, EndowmentTransactionLine line, int index) {
        boolean isValid = super.validateTransactionLine(endowmentTransactionLinesDocument, line, index);
        // Obtain Prefix for Error fields in UI.
        String ERROR_PREFIX = getErrorPrefix(line, index);

        if (isValid) {

            // Is Etran code empty
            if (isEndowmentTransactionCodeEmpty(line, ERROR_PREFIX))
                return false;

            // Validate ETran code
            if (!validateEndowmentTransactionCode(line, ERROR_PREFIX))
                return false;

            // Validate ETran code as E or I
            isValid &= validateEndowmentTransactionTypeCode(endowmentTransactionLinesDocument, line, ERROR_PREFIX);

            // Validate if a KEMID can have a principal transaction when IP indicator is P
            if (!canKEMIDHaveAPrincipalTransaction(line, ERROR_PREFIX))
                return false;

            // Validate if the chart is matched between the KEMID and EtranCode
            isValid &= validateChartMatch(line, ERROR_PREFIX);

            // Validate Amount is Greater than Zero.
            isValid &= validateTransactionAmountGreaterThanZero(line, ERROR_PREFIX);

            // Set Corpus Indicator
            line.setCorpusIndicator(SpringContext.getBean(EndowmentTransactionLinesDocumentService.class).getCorpusIndicatorValueforAnEndowmentTransactionLine(line.getKemid(), line.getEtranCode(), line.getTransactionIPIndicatorCode()));

        }

        return isValid;
    }

    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);
        GLToEndowmentTransferOfFundsDocument transferOfFundsDocument = (GLToEndowmentTransferOfFundsDocument) document;

        // validate the document has at least one target transaction line
        if (!transactionLineSizeGreaterThanZero(transferOfFundsDocument, false))
            return false;

        // validate that the document has at least one source accounting line
        if (!validateAccountingLinesSizeGreaterThanZero(transferOfFundsDocument, true)) {
            return false;
        }

        //validations of security and registration attributes
        isValid &= validateSecurityAndRegistrationRules(document);
        
        return isValid;
    }

    /**
     * GLToEndowmentTransferOfFundsDocument use target
     * 
     * @see org.kuali.kfs.module.endow.document.validation.impl.OptionalSecurityBaseRules#isSourceDocument()
     */
    @Override
    boolean isSourceDocument() {
        return false;
    }

}

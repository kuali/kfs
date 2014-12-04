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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.document.EndowmentToGLTransferOfFundsDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.service.EndowmentTransactionLinesDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.document.Document;

public class EndowmentToGLTransferOfFundsDocumentRules extends EndowmentAccountingLinesDocumentBaseRules {

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

            if (isValid) {
                if (EndowConstants.TRANSACTION_LINE_TYPE_SOURCE.equalsIgnoreCase(line.getTransactionLineTypeCode())) {
                    checkWhetherReducePermanentlyRestrictedFund(line, ERROR_PREFIX);
                    checkWhetherHaveSufficientFundsForCashBasedTransaction(line, ERROR_PREFIX);
                }
            }
        }

        return isValid;
    }

    /**
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);
        EndowmentToGLTransferOfFundsDocument transferOfFundsDocument = (EndowmentToGLTransferOfFundsDocument) document;

        // if security is not empty validate that security is valid
        if (StringUtils.isNotBlank(transferOfFundsDocument.getSourceTransactionSecurity().getSecurityID())) {
            // Validates Security Code.
            if (!validateSecurityCode(transferOfFundsDocument, true))
                return false;

            // Checks if Security is Active
            isValid &= isSecurityActive(transferOfFundsDocument, true);
        }

        // if registration code is not empty validate that registration code are valid
        if (StringUtils.isNotBlank(transferOfFundsDocument.getSourceTransactionSecurity().getRegistrationCode())) {
            // Validate Registration code.
            if (!validateRegistrationCode(transferOfFundsDocument, true))
                return false;

            // Checks if registration code is active
            isValid &= isRegistrationCodeActive(transferOfFundsDocument, true);
        }

        // validate the document has at least one source transaction line
        if (!transactionLineSizeGreaterThanZero(transferOfFundsDocument, true))
            return false;

        // validate that the document has at least one target accounting line
        if (!validateAccountingLinesSizeGreaterThanZero(transferOfFundsDocument, false)) {
            return false;
        }

        //validations of security and registration attributes
        isValid &= validateSecurityAndRegistrationRules(document);
        
        return isValid;
    }
    
    /**
     * EndowmentToGLTransferOfFunds use source
     * 
     * @see org.kuali.kfs.module.endow.document.validation.impl.OptionalSecurityBaseRules#isSourceDocument()
     */
    @Override
    boolean isSourceDocument() {
        return true;
    }


}

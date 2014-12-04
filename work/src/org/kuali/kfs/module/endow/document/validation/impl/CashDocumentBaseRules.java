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
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.module.endow.document.EndowmentSecurityDetailsDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionalDocument;
import org.kuali.kfs.module.endow.document.service.EndowmentTransactionLinesDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;

abstract class CashDocumentBaseRules extends OptionalSecurityBaseRules {
    
    /**
     * @see org.kuali.kfs.module.endow.document.validation.impl.EndowmentTransactionLinesDocumentBaseRules#processAddTransactionLineRules(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    @Override
    public boolean processAddTransactionLineRules(EndowmentTransactionLinesDocument document, EndowmentTransactionLine line) {

        boolean isValid = super.processAddTransactionLineRules(document, line);
        isValid &= !GlobalVariables.getMessageMap().hasErrors();

        if (isValid) {
            isValid &= validateCashTransactionLine(document, line, -1);
        }

        return isValid;

    }

    /**
     * This method should be overridden by children rule classes as a hook to implement document specific business rule checks for
     * the general validating a transaction line.
     * 
     * @param line
     * @param index
     * @return boolean True if the rules checks passed, false otherwise.
     */
    protected boolean validateCashTransactionLine(EndowmentTransactionLinesDocument document, EndowmentTransactionLine line, int index) {
        boolean isValid = true;

        if (isValid) {
            // Obtain Prefix for Error fields in UI.
            String ERROR_PREFIX = getErrorPrefix(line, index);

            // Is Etran code empty
            if (isEndowmentTransactionCodeEmpty(line, ERROR_PREFIX))
                return false;

            // Validate ETran code
            if (!validateEndowmentTransactionCode(line, ERROR_PREFIX))
                return false;

            // Validate ETran code as E or I
            isValid &= validateEndowmentTransactionTypeCode(document, line, ERROR_PREFIX);

            // Validate if a KEMID can have a principal transaction when IP indicator is P
            if (!canKEMIDHaveAPrincipalTransaction(line, ERROR_PREFIX))
                return false;

            // Validate if the chart is matched between the KEMID and EtranCode
            isValid &= validateChartMatch(line, ERROR_PREFIX);

            if (document.isErrorCorrectedDocument()) {
                // Validate Amount is Less than Zero.
                isValid &= validateTransactionAmountLessThanZero(line, ERROR_PREFIX);
            }
            else {
                // Validate Amount is Greater than Zero.
                isValid &= validateTransactionAmountGreaterThanZero(line, ERROR_PREFIX);
            }


            // Set Corpus Indicator
            line.setCorpusIndicator(SpringContext.getBean(EndowmentTransactionLinesDocumentService.class).getCorpusIndicatorValueforAnEndowmentTransactionLine(line.getKemid(), line.getEtranCode(), line.getTransactionIPIndicatorCode()));
        }

        return GlobalVariables.getMessageMap().getErrorCount() == 0;
    }

    /**
     * This method...
     * @param endowmentTransactionLinesDocument
     * @param line
     * @param prefix
     * @return
     */
    protected boolean validateEtranTypeBasedOnDocSource(EndowmentTransactionLinesDocument endowmentTransactionLinesDocument, EndowmentTransactionLine line, String prefix) {
        // when created from batch allow ASSET etran type too
        if (EndowConstants.TransactionSourceTypeCode.AUTOMATED.equalsIgnoreCase(endowmentTransactionLinesDocument.getTransactionSourceTypeCode())) {
            if (line.getEtranCodeObj().getEndowmentTransactionTypeCode().equalsIgnoreCase(EndowConstants.EndowmentTransactionTypeCodes.INCOME_TYPE_CODE) || line.getEtranCodeObj().getEndowmentTransactionTypeCode().equalsIgnoreCase(EndowConstants.EndowmentTransactionTypeCodes.EXPENSE_TYPE_CODE) || line.getEtranCodeObj().getEndowmentTransactionTypeCode().equalsIgnoreCase(EndowConstants.EndowmentTransactionTypeCodes.ASSET_TYPE_CODE))
                return true;
            else {
                putFieldError(prefix + EndowPropertyConstants.TRANSACTION_LINE_ENDOWMENT_TRANSACTION_CODE, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_ENDOWMENT_TRANSACTION_TYPE_CODE_VALIDITY_INCOME_EXPENSE_ASSET);
                return false;
            }
        }
        else
            return super.validateEndowmentTransactionTypeCode(endowmentTransactionLinesDocument, line, prefix);
    }
     
}

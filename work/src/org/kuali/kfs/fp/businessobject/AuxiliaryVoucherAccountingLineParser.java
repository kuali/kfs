/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.module.financial.bo;

import static org.kuali.kfs.KFSKeyConstants.AccountingLineParser.ERROR_INVALID_PROPERTY_VALUE;
import static org.kuali.kfs.KFSPropertyConstants.ACCOUNT_NUMBER;
import static org.kuali.kfs.KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE;
import static org.kuali.kfs.KFSPropertyConstants.CREDIT;
import static org.kuali.kfs.KFSPropertyConstants.DEBIT;
import static org.kuali.kfs.KFSPropertyConstants.FINANCIAL_OBJECT_CODE;
import static org.kuali.kfs.KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE;
import static org.kuali.kfs.KFSPropertyConstants.ORGANIZATION_REFERENCE_ID;
import static org.kuali.kfs.KFSPropertyConstants.PROJECT_CODE;
import static org.kuali.kfs.KFSPropertyConstants.SUB_ACCOUNT_NUMBER;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.AccountingLineParserBase;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.exceptions.AccountingLineParserException;

/**
 * This class is used to parse an <code>AuxiliaryVocherDocument</code> accounting line.
 */
public class AuxiliaryVoucherAccountingLineParser extends AccountingLineParserBase {
    private static final String[] AV_FORMAT = { CHART_OF_ACCOUNTS_CODE, ACCOUNT_NUMBER, SUB_ACCOUNT_NUMBER, FINANCIAL_OBJECT_CODE, FINANCIAL_SUB_OBJECT_CODE, PROJECT_CODE, ORGANIZATION_REFERENCE_ID, DEBIT, CREDIT };

    /**
     * Constructs a AuxiliaryVoucherAccountingLineParser.java.
     */
    public AuxiliaryVoucherAccountingLineParser() {
        super();
    }

    /**
     * Populates source accounting lines and sets debit and credit amounts and codes if they exist
     * 
     * @see org.kuali.kfs.bo.AccountingLineParserBase#performCustomSourceAccountingLinePopulation(java.util.Map, org.kuali.kfs.bo.SourceAccountingLine, java.lang.String)
     */
    @Override
    protected void performCustomSourceAccountingLinePopulation(Map<String, String> attributeValueMap, SourceAccountingLine sourceAccountingLine, String accountingLineAsString) {
        super.performCustomSourceAccountingLinePopulation(attributeValueMap, sourceAccountingLine, accountingLineAsString);

        // chose debit/credit
        String debitValue = attributeValueMap.remove(DEBIT);
        String creditValue = attributeValueMap.remove(CREDIT);
        KualiDecimal debitAmount = null;
        try {
            if (StringUtils.isNotBlank(debitValue)) {
                debitAmount = new KualiDecimal(debitValue);
            }
        }
        catch (NumberFormatException e) {
            String[] errorParameters = { debitValue, retrieveAttributeLabel(sourceAccountingLine.getClass(), DEBIT), accountingLineAsString };
            throw new AccountingLineParserException("invalid (NaN) '" + DEBIT + "=" + debitValue + " for " + accountingLineAsString, ERROR_INVALID_PROPERTY_VALUE, errorParameters);
        }
        KualiDecimal creditAmount = null;
        try {
            if (StringUtils.isNotBlank(creditValue)) {
                creditAmount = new KualiDecimal(creditValue);
            }
        }
        catch (NumberFormatException e) {
            String[] errorParameters = { creditValue, retrieveAttributeLabel(sourceAccountingLine.getClass(), CREDIT), accountingLineAsString };
            throw new AccountingLineParserException("invalid (NaN) '" + CREDIT + "=" + creditValue + " for " + accountingLineAsString, ERROR_INVALID_PROPERTY_VALUE, errorParameters);
        }

        KualiDecimal amount = null;
        String debitCreditCode = null;
        if (debitAmount != null && debitAmount.isNonZero()) {
            amount = debitAmount;
            debitCreditCode = KFSConstants.GL_DEBIT_CODE;
        }

        if (creditAmount != null && creditAmount.isNonZero()) {
            amount = creditAmount;
            debitCreditCode = KFSConstants.GL_CREDIT_CODE;
        }

        sourceAccountingLine.setAmount(amount);
        sourceAccountingLine.setDebitCreditCode(debitCreditCode);
    }

    /**
     * @see org.kuali.core.bo.AccountingLineParserBase#getSourceAccountingLineFormat()
     */
    @Override
    public String[] getSourceAccountingLineFormat() {
        return AV_FORMAT;
    }
}

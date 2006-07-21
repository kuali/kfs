/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package org.kuali.module.financial.bo;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import static org.kuali.KeyConstants.AccountingLineParser.ERROR_INVALID_PROPERTY_VALUE;
import static org.kuali.PropertyConstants.ACCOUNT_NUMBER;
import static org.kuali.PropertyConstants.CHART_OF_ACCOUNTS_CODE;
import static org.kuali.PropertyConstants.CREDIT;
import static org.kuali.PropertyConstants.DEBIT;
import static org.kuali.PropertyConstants.FINANCIAL_OBJECT_CODE;
import static org.kuali.PropertyConstants.FINANCIAL_SUB_OBJECT_CODE;
import static org.kuali.PropertyConstants.ORGANIZATION_REFERENCE_ID;
import static org.kuali.PropertyConstants.OVERRIDE_CODE;
import static org.kuali.PropertyConstants.PROJECT_CODE;
import static org.kuali.PropertyConstants.SUB_ACCOUNT_NUMBER;
import org.kuali.core.bo.AccountingLineParserBase;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.exceptions.AccountingLineParserException;
import org.kuali.core.util.KualiDecimal;

/**
 * <code>AuxiliaryVocherDocument</code> accounting line parser
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class AuxiliaryVoucherAccountingLineParser extends AccountingLineParserBase {
    private static final String[] AV_FORMAT = { CHART_OF_ACCOUNTS_CODE, ACCOUNT_NUMBER, SUB_ACCOUNT_NUMBER, FINANCIAL_OBJECT_CODE, FINANCIAL_SUB_OBJECT_CODE, PROJECT_CODE, ORGANIZATION_REFERENCE_ID, OVERRIDE_CODE, DEBIT, CREDIT };

    /**
     * Constructs a AuxiliaryVoucherAccountingLineParser.java.
     */
    public AuxiliaryVoucherAccountingLineParser() {
        super();
    }

    /**
     * 
     * @see org.kuali.core.bo.AccountingLineParserBase#performCustomSourceAccountingLinePopulation(java.util.Map,
     *      org.kuali.core.bo.SourceAccountingLine, java.lang.String)
     */
    @Override
    protected void performCustomSourceAccountingLinePopulation(Map<String, String> attributeValueMap, SourceAccountingLine sourceAccountingLine, String accountingLineAsString) {
        super.performCustomSourceAccountingLinePopulation(attributeValueMap, sourceAccountingLine, accountingLineAsString);
        // chose debit/credit
        String debitValue = attributeValueMap.remove(DEBIT);
        String creditValue = attributeValueMap.remove(CREDIT);
        KualiDecimal amount = null;
        String debitCreditCode = null;
        if (StringUtils.isNotBlank(debitValue)) {
            try {
                amount = new KualiDecimal(debitValue);
                debitCreditCode = Constants.GL_DEBIT_CODE;
            }
            catch (NumberFormatException e) {
                String[] errorParameters = { debitValue, retrieveAttributeLabel(sourceAccountingLine.getClass(), DEBIT), accountingLineAsString };
                throw new AccountingLineParserException("invalid (NaN) '" + DEBIT + "=" + debitValue + "for " + accountingLineAsString, ERROR_INVALID_PROPERTY_VALUE, errorParameters);
            }
        }
        else {
            try {
                amount = new KualiDecimal(creditValue);
                debitCreditCode = Constants.GL_CREDIT_CODE;
            }
            catch (NumberFormatException e) {
                String[] errorParameters = { creditValue, retrieveAttributeLabel(sourceAccountingLine.getClass(), CREDIT), accountingLineAsString };
                throw new AccountingLineParserException("invalid (NaN) '" + CREDIT + "=" + creditValue + "for " + accountingLineAsString, ERROR_INVALID_PROPERTY_VALUE, errorParameters);
            }
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

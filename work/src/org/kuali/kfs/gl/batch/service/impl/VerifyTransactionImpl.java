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
package org.kuali.module.gl.batch.poster.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.module.gl.batch.poster.VerifyTransaction;
import org.kuali.module.gl.bo.Transaction;

/**
 * @author jsissom
 * 
 */
public class VerifyTransactionImpl implements VerifyTransaction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(VerifyTransactionImpl.class);
    private KualiConfigurationService kualiConfigurationService;

    public VerifyTransactionImpl() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.gl.batch.poster.VerifyTransaction#verifyTransaction(org.kuali.module.gl.bo.Transaction)
     */
    public List verifyTransaction(Transaction t) {
        LOG.debug("verifyTransaction() started");

        // List of error messages for the current transaction
        List errors = new ArrayList();

        // Check the chart of accounts code
        if (t.getChart() == null) {
            errors.add(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_CHART_NOT_FOUND));
        }

        // Check the account
        if (t.getAccount() == null) {
            errors.add(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_ACCOUNT_NOT_FOUND));
        }

        // Check the object type
        if (t.getObjectType() == null) {
            errors.add(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OBJECT_TYPE_NOT_FOUND));
        }

        // Check the balance type
        if (t.getBalanceType() == null) {
            errors.add(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_BALANCE_TYPE_NOT_FOUND));
        }

        // Check the fiscal year
        if (t.getOption() == null) {
            errors.add(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_UNIV_FISCAL_YR_NOT_FOUND));
        }

        // Check the debit/credit code (only if we have a valid balance type code)
        if (t.getTransactionDebitCreditCode() == null) {
            errors.add(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_DEDIT_CREDIT_CODE_NOT_BE_NULL));
        }
        else {
            if (t.getBalanceType() != null) {
                if (t.getBalanceType().isFinancialOffsetGenerationIndicator()) {
                    if ((!Constants.GL_DEBIT_CODE.equals(t.getTransactionDebitCreditCode())) && (!Constants.GL_CREDIT_CODE.equals(t.getTransactionDebitCreditCode()))) {
                        errors.add(kualiConfigurationService.getPropertyString(KeyConstants.MSG_DEDIT_CREDIT_CODE_MUST_BE) + Constants.GL_DEBIT_CODE + " or " + Constants.GL_CREDIT_CODE + kualiConfigurationService.getPropertyString(KeyConstants.MSG_FOR_BALANCE_TYPE));
                    }
                }
                else {
                    if (!Constants.GL_BUDGET_CODE.equals(t.getTransactionDebitCreditCode())) {
                        errors.add(kualiConfigurationService.getPropertyString(KeyConstants.MSG_DEDIT_CREDIT_CODE_MUST_BE) + Constants.GL_BUDGET_CODE + kualiConfigurationService.getPropertyString(KeyConstants.MSG_FOR_BALANCE_TYPE));
                    }
                }
            }
        }

        // KULGL-58 Make sure all GL entry primary key fields are not null
        if ((t.getSubAccountNumber() == null) || (t.getSubAccountNumber().trim().length() == 0)) {
            errors.add(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_SUB_ACCOUNT_NOT_BE_NULL));
        }
        if ((t.getFinancialObjectCode() == null) || (t.getFinancialObjectCode().trim().length() == 0)) {
            errors.add(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OBJECT_CODE_NOT_BE_NULL));
        }
        if ((t.getFinancialSubObjectCode() == null) || (t.getFinancialSubObjectCode().trim().length() == 0)) {
            errors.add(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_SUB_OBJECT_CODE_NOT_BE_NULL));
        }
        if ((t.getUniversityFiscalPeriodCode() == null) || (t.getUniversityFiscalPeriodCode().trim().length() == 0)) {
            errors.add(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_FISCAL_PERIOD_CODE_NOT_BE_NULL));
        }
        if ((t.getFinancialDocumentTypeCode() == null) || (t.getFinancialDocumentTypeCode().trim().length() == 0)) {
            errors.add(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_DOCUMENT_TYPE_NOT_BE_NULL));
        }
        if ((t.getFinancialSystemOriginationCode() == null) || (t.getFinancialSystemOriginationCode().trim().length() == 0)) {
            errors.add(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_ORIGIN_CODE_NOT_BE_NULL));
        }
        if ((t.getFinancialDocumentNumber() == null) || (t.getFinancialDocumentNumber().trim().length() == 0)) {
            errors.add(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_DOCUMENT_NUMBER_NOT_BE_NULL));
        }
        if (t.getTransactionLedgerEntrySequenceNumber() == null) {
            errors.add(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_SEQUENCE_NUMBER_NOT_BE_NULL));
        }

        return errors;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * 
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
}

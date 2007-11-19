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
package org.kuali.module.gl.batch.poster.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.module.gl.batch.poster.VerifyTransaction;
import org.kuali.module.gl.bo.Transaction;

/**
 * A general use implementation of VerifyTransaction
 */
public class VerifyTransactionImpl implements VerifyTransaction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(VerifyTransactionImpl.class);
    private KualiConfigurationService kualiConfigurationService;

    /**
     * Constructs a VerifyTransactionImpl instance
     */
    public VerifyTransactionImpl() {
        super();
    }

    /**
     * Determines if the given transaction qualifies for posting
     * 
     * @param t the transaction to verify
     * @return a List of String error messages
     * @see org.kuali.module.gl.batch.poster.VerifyTransaction#verifyTransaction(org.kuali.module.gl.bo.Transaction)
     */
    public List verifyTransaction(Transaction t) {
        LOG.debug("verifyTransaction() started");

        // List of error messages for the current transaction
        List errors = new ArrayList();

        // Check the chart of accounts code
        if (t.getChart() == null) {
            errors.add(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_CHART_NOT_FOUND));
        }

        // Check the account
        if (t.getAccount() == null) {
            errors.add(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_ACCOUNT_NOT_FOUND));
        }

        // Check the object type
        if (t.getObjectType() == null) {
            errors.add(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_OBJECT_TYPE_NOT_FOUND));
        }

        // Check the balance type
        if (t.getBalanceType() == null) {
            errors.add(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_BALANCE_TYPE_NOT_FOUND));
        }

        // Check the fiscal year
        if (t.getOption() == null) {
            errors.add(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_UNIV_FISCAL_YR_NOT_FOUND));
        }

        // Check the debit/credit code (only if we have a valid balance type code)
        if (t.getTransactionDebitCreditCode() == null) {
            errors.add(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_DEDIT_CREDIT_CODE_NOT_BE_NULL));
        }
        else {
            if (t.getBalanceType() != null) {
                if (t.getBalanceType().isFinancialOffsetGenerationIndicator()) {
                    if ((!KFSConstants.GL_DEBIT_CODE.equals(t.getTransactionDebitCreditCode())) && (!KFSConstants.GL_CREDIT_CODE.equals(t.getTransactionDebitCreditCode()))) {
                        errors.add(kualiConfigurationService.getPropertyString(KFSKeyConstants.MSG_DEDIT_CREDIT_CODE_MUST_BE) + KFSConstants.GL_DEBIT_CODE + " or " + KFSConstants.GL_CREDIT_CODE + kualiConfigurationService.getPropertyString(KFSKeyConstants.MSG_FOR_BALANCE_TYPE));
                    }
                }
                else {
                    if (!KFSConstants.GL_BUDGET_CODE.equals(t.getTransactionDebitCreditCode())) {
                        errors.add(kualiConfigurationService.getPropertyString(KFSKeyConstants.MSG_DEDIT_CREDIT_CODE_MUST_BE) + KFSConstants.GL_BUDGET_CODE + kualiConfigurationService.getPropertyString(KFSKeyConstants.MSG_FOR_BALANCE_TYPE));
                    }
                }
            }
        }

        // KULGL-58 Make sure all GL entry primary key fields are not null
        if ((t.getSubAccountNumber() == null) || (t.getSubAccountNumber().trim().length() == 0)) {
            errors.add(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_SUB_ACCOUNT_NOT_BE_NULL));
        }
        if ((t.getFinancialObjectCode() == null) || (t.getFinancialObjectCode().trim().length() == 0)) {
            errors.add(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_OBJECT_CODE_NOT_BE_NULL));
        }
        if ((t.getFinancialSubObjectCode() == null) || (t.getFinancialSubObjectCode().trim().length() == 0)) {
            errors.add(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_SUB_OBJECT_CODE_NOT_BE_NULL));
        }
        if ((t.getUniversityFiscalPeriodCode() == null) || (t.getUniversityFiscalPeriodCode().trim().length() == 0)) {
            errors.add(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_FISCAL_PERIOD_CODE_NOT_BE_NULL));
        }
        if ((t.getFinancialDocumentTypeCode() == null) || (t.getFinancialDocumentTypeCode().trim().length() == 0)) {
            errors.add(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_DOCUMENT_TYPE_NOT_BE_NULL));
        }
        if ((t.getFinancialSystemOriginationCode() == null) || (t.getFinancialSystemOriginationCode().trim().length() == 0)) {
            errors.add(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_ORIGIN_CODE_NOT_BE_NULL));
        }
        if ((t.getDocumentNumber() == null) || (t.getDocumentNumber().trim().length() == 0)) {
            errors.add(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_DOCUMENT_NUMBER_NOT_BE_NULL));
        }
        if (t.getTransactionLedgerEntrySequenceNumber() == null) {
            errors.add(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_SEQUENCE_NUMBER_NOT_BE_NULL));
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

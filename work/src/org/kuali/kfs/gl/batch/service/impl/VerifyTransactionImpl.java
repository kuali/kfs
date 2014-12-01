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
package org.kuali.kfs.gl.batch.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.VerifyTransaction;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.rice.core.api.config.property.ConfigurationService;

/**
 * A general use implementation of VerifyTransaction
 */
public class VerifyTransactionImpl implements VerifyTransaction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(VerifyTransactionImpl.class);
    private ConfigurationService kualiConfigurationService;

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
     * @see org.kuali.kfs.gl.batch.service.VerifyTransaction#verifyTransaction(org.kuali.kfs.gl.businessobject.Transaction)
     */
    public List<Message> verifyTransaction(Transaction t) {
        LOG.debug("verifyTransaction() started");

        // List of error messages for the current transaction
        List<Message> errors = new ArrayList();

        // Check the chart of accounts code
        if (t.getChart() == null) {
            errors.add(new Message(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_CHART_NOT_FOUND), Message.TYPE_FATAL));
        }

        // Check the account
        if (t.getAccount() == null) {
            errors.add(new Message(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_ACCOUNT_NOT_FOUND), Message.TYPE_FATAL));
        }

        // Check the object type
        if (t.getObjectType() == null) {
            errors.add(new Message(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_OBJECT_TYPE_NOT_FOUND), Message.TYPE_FATAL));
        }

        // Check the balance type
        if (t.getBalanceType() == null) {
            errors.add(new Message(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_BALANCE_TYPE_NOT_FOUND), Message.TYPE_FATAL));
        }

        // Check the fiscal year
        if (t.getOption() == null) {
            errors.add(new Message(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_UNIV_FISCAL_YR_NOT_FOUND), Message.TYPE_FATAL));
        }

        // Check the debit/credit code (only if we have a valid balance type code)
        if (t.getTransactionDebitCreditCode() == null) {
            errors.add(new Message(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_DEDIT_CREDIT_CODE_NOT_BE_NULL), Message.TYPE_FATAL));
        }
        else {
            if (t.getBalanceType() != null) {
                if (t.getBalanceType().isFinancialOffsetGenerationIndicator()) {
                    if ((!KFSConstants.GL_DEBIT_CODE.equals(t.getTransactionDebitCreditCode())) && (!KFSConstants.GL_CREDIT_CODE.equals(t.getTransactionDebitCreditCode()))) {
                        errors.add(new Message(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.MSG_DEDIT_CREDIT_CODE_MUST_BE) + " '" + KFSConstants.GL_DEBIT_CODE + " or " + KFSConstants.GL_CREDIT_CODE + kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.MSG_FOR_BALANCE_TYPE), Message.TYPE_FATAL));
                    }
                }
                else {
                    if (!KFSConstants.GL_BUDGET_CODE.equals(t.getTransactionDebitCreditCode())) {
                        errors.add(new Message(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.MSG_DEDIT_CREDIT_CODE_MUST_BE) + KFSConstants.GL_BUDGET_CODE + kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.MSG_FOR_BALANCE_TYPE), Message.TYPE_FATAL));
                    }
                }
            }
        }

        // KULGL-58 Make sure all GL entry primary key fields are not null
        if ((t.getSubAccountNumber() == null) || (t.getSubAccountNumber().trim().length() == 0)) {
            errors.add(new Message(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_SUB_ACCOUNT_NOT_BE_NULL), Message.TYPE_FATAL));
        }
        if ((t.getFinancialObjectCode() == null) || (t.getFinancialObjectCode().trim().length() == 0)) {
            errors.add(new Message(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_OBJECT_CODE_NOT_BE_NULL), Message.TYPE_FATAL));
        }
        if ((t.getFinancialSubObjectCode() == null) || (t.getFinancialSubObjectCode().trim().length() == 0)) {
            errors.add(new Message(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_SUB_OBJECT_CODE_NOT_BE_NULL), Message.TYPE_FATAL));
        }
        if ((t.getUniversityFiscalPeriodCode() == null) || (t.getUniversityFiscalPeriodCode().trim().length() == 0)) {
            errors.add(new Message(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_FISCAL_PERIOD_CODE_NOT_BE_NULL), Message.TYPE_FATAL));
        }
        if ((t.getFinancialDocumentTypeCode() == null) || (t.getFinancialDocumentTypeCode().trim().length() == 0)) {
            errors.add(new Message(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_DOCUMENT_TYPE_NOT_BE_NULL), Message.TYPE_FATAL));
        }
        if ((t.getFinancialSystemOriginationCode() == null) || (t.getFinancialSystemOriginationCode().trim().length() == 0)) {
            errors.add(new Message(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_ORIGIN_CODE_NOT_BE_NULL), Message.TYPE_FATAL));
        }
        if ((t.getDocumentNumber() == null) || (t.getDocumentNumber().trim().length() == 0)) {
            errors.add(new Message(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_DOCUMENT_NUMBER_NOT_BE_NULL), Message.TYPE_FATAL));
        }
        
        // Don't need to check SequenceNumber because it sets in PosterServiceImpl, so commented out
//        if (t.getTransactionLedgerEntrySequenceNumber() == null) {
//            errors.add(new Message(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_SEQUENCE_NUMBER_NOT_BE_NULL), Message.TYPE_FATAL));
//        }
        
        if (t.getBalanceType() != null && t.getBalanceType().isFinBalanceTypeEncumIndicator()  && !t.getObjectType().isFundBalanceIndicator()){
            if (t.getTransactionEncumbranceUpdateCode().trim().equals(GeneralLedgerConstants.EMPTY_CODE)){
                errors.add(new Message(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_ENCUMBRANCE_UPDATE_CODE_CANNOT_BE_BLANK_FOR_BALANCE_TYPE) + " " + t.getFinancialBalanceTypeCode(), Message.TYPE_FATAL));
            }
        }

        // The encumbrance update code can only be space, N, R or D. Nothing else
        if ((StringUtils.isNotBlank(t.getTransactionEncumbranceUpdateCode())) && (!" ".equals(t.getTransactionEncumbranceUpdateCode())) && (!KFSConstants.ENCUMB_UPDT_NO_ENCUMBRANCE_CD.equals(t.getTransactionEncumbranceUpdateCode())) && (!KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD.equals(t.getTransactionEncumbranceUpdateCode())) && (!KFSConstants.ENCUMB_UPDT_DOCUMENT_CD.equals(t.getTransactionEncumbranceUpdateCode()))) {
            errors.add(new Message("Invalid Encumbrance Update Code (" + t.getTransactionEncumbranceUpdateCode() + ")", Message.TYPE_FATAL));
        }

        

        return errors;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * 
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
}

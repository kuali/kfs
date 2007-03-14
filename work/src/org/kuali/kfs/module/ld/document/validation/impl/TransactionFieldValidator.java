/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.rules;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.util.Message;
import org.springframework.beans.factory.BeanFactory;

public class TransactionFieldValidator {
    private static KualiConfigurationService kualiConfigurationService = SpringServiceLocator.getKualiConfigurationService();

    public static Message checkUniversityFiscalYear(Transaction transaction) {
        Integer fiscalYear = transaction.getUniversityFiscalYear();
        if (fiscalYear == null) {
            return buildErrorMessage(KeyConstants.ERROR_UNIV_FISCAL_YR_NOT_FOUND, Message.TYPE_FATAL);
        }
        else if (transaction.getOption() == null) {
            return buildErrorMessage(KeyConstants.ERROR_UNIV_FISCAL_YR_NOT_FOUND, fiscalYear.toString(), Message.TYPE_FATAL);
        }
        return null;
    }

    public static Message checkChartOfAccountsCode(Transaction transaction) {
        String chartOfAccountsCode = transaction.getChartOfAccountsCode();
        if (StringUtils.isBlank(chartOfAccountsCode) || transaction.getChart() == null) {
            return buildErrorMessage(KeyConstants.ERROR_CHART_NOT_FOUND, chartOfAccountsCode, Message.TYPE_FATAL);
        }

        if (!transaction.getChart().isFinChartOfAccountActiveIndicator()) {
            return buildErrorMessage(KeyConstants.ERROR_CHART_NOT_ACTIVE, chartOfAccountsCode, Message.TYPE_FATAL);
        }
        return null;
    }

    public static Message checkAccountNumber(Transaction transaction) {
        String accountNumber = transaction.getAccountNumber();
        if (StringUtils.isBlank(accountNumber) || transaction.getAccount() == null) {
            String chartOfAccountsCode = transaction.getChartOfAccountsCode();
            String accountKey = chartOfAccountsCode + "-" + accountNumber;
            return buildErrorMessage(KeyConstants.ERROR_ACCOUNT_NOT_FOUND, accountNumber, Message.TYPE_FATAL);
        }
        return null;
    }

    public static Message checkSubAccountNumber(Transaction transaction) {
        String subAccountNumber = transaction.getSubAccountNumber();
        String chartOfAccountsCode = transaction.getChartOfAccountsCode();
        String accountNumber = transaction.getAccountNumber();

        String subAccountKey = chartOfAccountsCode + "-" + accountNumber + "-" + subAccountNumber;
        if (StringUtils.isBlank(subAccountNumber)) {
            return buildErrorMessage(KeyConstants.ERROR_SUB_ACCOUNT_NOT_FOUND, subAccountKey, Message.TYPE_FATAL);
        }

        if (!Constants.DASHES_SUB_ACCOUNT_NUMBER.equals(subAccountNumber)) {
            if (transaction.getSubAccount() == null) {
                return buildErrorMessage(KeyConstants.ERROR_SUB_ACCOUNT_NOT_FOUND, subAccountKey, Message.TYPE_FATAL);
            }

            if (!transaction.getSubAccount().isSubAccountActiveIndicator()) {
                return buildErrorMessage(KeyConstants.ERROR_SUB_ACCOUNT_NOT_ACTIVE, subAccountKey, Message.TYPE_FATAL);
            }
        }
        return null;
    }

    public static Message checkFinancialObjectCode(Transaction transaction) {
        String objectCode = transaction.getFinancialObjectCode();
        if (StringUtils.isBlank(objectCode)) {
            return buildErrorMessage(KeyConstants.ERROR_OBJECT_CODE_EMPTY, Message.TYPE_FATAL);
        }

        Integer fiscalYear = transaction.getUniversityFiscalYear();
        String chartOfAccountsCode = transaction.getChartOfAccountsCode();
        String objectCodeKey = fiscalYear + "-" + chartOfAccountsCode + "-" + objectCode;
        if (transaction.getFinancialObject() == null) {
            return buildErrorMessage(KeyConstants.ERROR_OBJECT_CODE_NOT_FOUND, objectCodeKey, Message.TYPE_FATAL);
        }

        if (!transaction.getFinancialObject().isActive()) {
            return buildErrorMessage(KeyConstants.ERROR_OBJECT_CODE_NOT_ACTIVE, objectCodeKey, Message.TYPE_FATAL);
        }
        return null;
    }

    public static Message checkFinancialSubObjectCode(Transaction transaction) {
        Integer fiscalYear = transaction.getUniversityFiscalYear();
        String chartOfAccountsCode = transaction.getChartOfAccountsCode();
        String objectCode = transaction.getFinancialObjectCode();
        String subObjectCode = transaction.getFinancialSubObjectCode();
        
        String subObjectCodeKey = fiscalYear + "-" + chartOfAccountsCode + "-" + objectCode + "-" + subObjectCode;
        if (StringUtils.isBlank(subObjectCode)) {
            return buildErrorMessage(KeyConstants.ERROR_SUB_OBJECT_CODE_NOT_BE_NULL, subObjectCodeKey, Message.TYPE_FATAL);
        }

        if (!Constants.DASHES_SUB_OBJECT_CODE.equals(subObjectCode)) {
            if (transaction.getFinancialSubObject() == null) {
                return buildErrorMessage(KeyConstants.ERROR_SUB_OBJECT_CODE_NOT_BE_NULL, subObjectCodeKey, Message.TYPE_FATAL);
            }
        }
        return null;
    }

    public static Message checkFinancialBalanceTypeCode(Transaction transaction) {
        String balanceTypeCode = transaction.getFinancialBalanceTypeCode();
        if (StringUtils.isBlank(balanceTypeCode) || transaction.getBalanceType() == null) {
            return buildErrorMessage(KeyConstants.ERROR_BALANCE_TYPE_NOT_FOUND, balanceTypeCode, Message.TYPE_FATAL);
        }
        return null;
    }

    public static Message checkFinancialObjectTypeCode(Transaction transaction) {
        String objectTypeCode = transaction.getFinancialObjectTypeCode();
        if (StringUtils.isBlank(objectTypeCode) || transaction.getObjectType() == null) {
            return buildErrorMessage(KeyConstants.ERROR_OBJECT_TYPE_NOT_FOUND, objectTypeCode, Message.TYPE_FATAL);
        }
        return null;
    }

    public static Message checkUniversityFiscalPeriodCode(Transaction transaction) {
        String fiscalPeriodCode = transaction.getUniversityFiscalPeriodCode();
        if (StringUtils.isBlank(fiscalPeriodCode)) {
            return buildErrorMessage(KeyConstants.ERROR_ACCOUNTING_PERIOD_NOT_FOUND, fiscalPeriodCode, Message.TYPE_FATAL);
        }
        return null;
    }

    public static Message checkFinancialDocumentTypeCode(Transaction transaction) {
        String documentTypeCode = transaction.getFinancialDocumentTypeCode();
        if (StringUtils.isBlank(documentTypeCode) || transaction.getDocumentType() == null) {
            return buildErrorMessage(KeyConstants.ERROR_DOCUMENT_TYPE_NOT_FOUND, documentTypeCode, Message.TYPE_FATAL);
        }
        return null;
    }

    public static Message checkFinancialDocumentNumber(Transaction transaction) {
        String documentNumber = transaction.getDocumentNumber();
        if (StringUtils.isBlank(documentNumber)) {
            return buildErrorMessage(KeyConstants.ERROR_DOCUMENT_NUMBER_REQUIRED, Message.TYPE_FATAL);
        }
        return null;
    }

    public static Message checkTransactionLedgerEntrySequenceNumber(Transaction transaction) {
        Integer sequenceNumber = transaction.getTransactionLedgerEntrySequenceNumber();
        if (sequenceNumber == null) {
            return buildErrorMessage(KeyConstants.ERROR_SEQUENCE_NUMBER_NOT_BE_NULL, Message.TYPE_FATAL);
        }
        return null;
    }

    public static Message checkTransactionDebitCreditCode(Transaction transaction) {
        String[] validDebitCreditCode = {Constants.GL_BUDGET_CODE, Constants.GL_CREDIT_CODE, Constants.GL_DEBIT_CODE};
        String debitCreditCode = transaction.getTransactionDebitCreditCode();
        if (!ArrayUtils.contains(validDebitCreditCode, debitCreditCode)) {
            return buildErrorMessage(KeyConstants.ERROR_DEDIT_CREDIT_CODE_NOT_BE_NULL, Message.TYPE_FATAL);
        } 
        return null;
    }

    public static Message checkFinancialSystemOriginationCode(Transaction transaction) {
        String originationCode = transaction.getFinancialSystemOriginationCode();
        if (StringUtils.isBlank(originationCode)) {
            return buildErrorMessage(KeyConstants.ERROR_ORIGIN_CODE_NOT_FOUND, Message.TYPE_FATAL);
        } 
        return null;
    }
    
    /**
     * Build the error message with the message body and error type
     */
    public static Message buildErrorMessage(String errorMessageKey, int errorType) {
        return buildErrorMessage(errorMessageKey, null, errorType);
    }

    /**
     * Build the error message with the message body, invalid value and error type
     */
    public static Message buildErrorMessage(String errorMessageKey, String invalidValue, int errorType) {
        String errorMessageBody = kualiConfigurationService.getPropertyString(errorMessageKey);
        String errorMessage = formatErrorMessageBody(errorMessageBody, invalidValue);
        return new Message(errorMessage, errorType);
    }

    /**
     * Format the error message body based on the given error message and invalid value
     */
    public static String formatErrorMessageBody(String errorMessageBody, String invalidValue) {
        String value = StringUtils.isBlank(invalidValue) ? "" : "[" + invalidValue + "]";
        return errorMessageBody + value;
    }
}
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
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.util.Message;
import org.kuali.module.labor.util.MessageBuilder;

/**
 * 
 * This class provides a set of utilities that can be used to validate a transaction in the filed level.
 */
public class TransactionFieldValidator {

    /**
     * Check if the given transaction contains valid university fiscal year
     * @param transaction the given transaction
     * @return null if the university fiscal year is valid; otherwise, return error message
     */
    public static Message checkUniversityFiscalYear(Transaction transaction) {
        Integer fiscalYear = transaction.getUniversityFiscalYear();
        if (fiscalYear == null) {
            return MessageBuilder.buildErrorMessage(KFSKeyConstants.ERROR_UNIV_FISCAL_YR_NOT_FOUND, Message.TYPE_FATAL);
        }
        else if (transaction.getOption() == null) {
            return MessageBuilder.buildErrorMessage(KFSKeyConstants.ERROR_UNIV_FISCAL_YR_NOT_FOUND, fiscalYear.toString(), Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * Check if the given transaction contains valid char of accounts code
     * @param transaction the given transaction
     * @return null if the char of accounts code is valid; otherwise, return error message
     */
    public static Message checkChartOfAccountsCode(Transaction transaction) {
        String chartOfAccountsCode = transaction.getChartOfAccountsCode();
        if (StringUtils.isBlank(chartOfAccountsCode) || transaction.getChart() == null) {
            return MessageBuilder.buildErrorMessage(KFSKeyConstants.ERROR_CHART_NOT_FOUND, chartOfAccountsCode, Message.TYPE_FATAL);
        }

        if (!transaction.getChart().isFinChartOfAccountActiveIndicator()) {
            return MessageBuilder.buildErrorMessage(KFSKeyConstants.ERROR_CHART_NOT_ACTIVE, chartOfAccountsCode, Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * Check if the given transaction contains valid account number
     * @param transaction the given transaction
     * @return null if the account number is valid; otherwise, return error message
     */
    public static Message checkAccountNumber(Transaction transaction) {
        String accountNumber = transaction.getAccountNumber();
        if (StringUtils.isBlank(accountNumber) || transaction.getAccount() == null) {
            String chartOfAccountsCode = transaction.getChartOfAccountsCode();
            String accountKey = chartOfAccountsCode + "-" + accountNumber;
            return MessageBuilder.buildErrorMessage(KFSKeyConstants.ERROR_ACCOUNT_NOT_FOUND, accountKey, Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * Check if the given transaction contains valid sub account number
     * @param transaction the given transaction
     * @return null if the sub account number is valid; otherwise, return error message
     */
    public static Message checkSubAccountNumber(Transaction transaction) {
        String subAccountNumber = transaction.getSubAccountNumber();
        String chartOfAccountsCode = transaction.getChartOfAccountsCode();
        String accountNumber = transaction.getAccountNumber();

        String subAccountKey = chartOfAccountsCode + "-" + accountNumber + "-" + subAccountNumber;
        if (StringUtils.isBlank(subAccountNumber)) {
            return MessageBuilder.buildErrorMessage(KFSKeyConstants.ERROR_SUB_ACCOUNT_NOT_FOUND, subAccountKey, Message.TYPE_FATAL);
        }

        if (!KFSConstants.DASHES_SUB_ACCOUNT_NUMBER.equals(subAccountNumber)) {
            if (transaction.getSubAccount() == null) {
                return MessageBuilder.buildErrorMessage(KFSKeyConstants.ERROR_SUB_ACCOUNT_NOT_FOUND, subAccountKey, Message.TYPE_FATAL);
            }

            if (!transaction.getSubAccount().isSubAccountActiveIndicator()) {
                return MessageBuilder.buildErrorMessage(KFSKeyConstants.ERROR_SUB_ACCOUNT_NOT_ACTIVE, subAccountKey, Message.TYPE_FATAL);
            }
        }
        return null;
    }

    /**
     * Check if the given transaction contains valid account number
     * @param transaction the given transaction
     * @return null if the account number is valid; otherwise, return error message
     */
    public static Message checkFinancialObjectCode(Transaction transaction) {
        String objectCode = transaction.getFinancialObjectCode();
        if (StringUtils.isBlank(objectCode)) {
            return MessageBuilder.buildErrorMessage(KFSKeyConstants.ERROR_OBJECT_CODE_EMPTY, Message.TYPE_FATAL);
        }

        Integer fiscalYear = transaction.getUniversityFiscalYear();
        String chartOfAccountsCode = transaction.getChartOfAccountsCode();
        String objectCodeKey = fiscalYear + "-" + chartOfAccountsCode + "-" + objectCode;
        if (transaction.getFinancialObject() == null) {
            return MessageBuilder.buildErrorMessage(KFSKeyConstants.ERROR_OBJECT_CODE_NOT_FOUND, objectCodeKey, Message.TYPE_FATAL);
        }

        if (!transaction.getFinancialObject().isActive()) {
            return MessageBuilder.buildErrorMessage(KFSKeyConstants.ERROR_OBJECT_CODE_NOT_ACTIVE, objectCodeKey, Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * Check if the given transaction contains valid sub object code
     * @param transaction the given transaction
     * @return null if the sub object code is valid; otherwise, return error message
     */
    public static Message checkFinancialSubObjectCode(Transaction transaction) {
        Integer fiscalYear = transaction.getUniversityFiscalYear();
        String chartOfAccountsCode = transaction.getChartOfAccountsCode();
        String objectCode = transaction.getFinancialObjectCode();
        String subObjectCode = transaction.getFinancialSubObjectCode();

        String subObjectCodeKey = fiscalYear + "-" + chartOfAccountsCode + "-" + objectCode + "-" + subObjectCode;
        if (StringUtils.isBlank(subObjectCode)) {
            return MessageBuilder.buildErrorMessage(KFSKeyConstants.ERROR_SUB_OBJECT_CODE_NOT_BE_NULL, subObjectCodeKey, Message.TYPE_FATAL);
        }

        if (!KFSConstants.DASHES_SUB_OBJECT_CODE.equals(subObjectCode)) {
            if (transaction.getFinancialSubObject() == null) {
                return MessageBuilder.buildErrorMessage(KFSKeyConstants.ERROR_SUB_OBJECT_CODE_NOT_BE_NULL, subObjectCodeKey, Message.TYPE_FATAL);
            }
        }
        return null;
    }

    /**
     * Check if the given transaction contains valid balance type code
     * @param transaction the given transaction
     * @return null if the balance type code is valid; otherwise, return error message
     */
    public static Message checkFinancialBalanceTypeCode(Transaction transaction) {
        String balanceTypeCode = transaction.getFinancialBalanceTypeCode();
        if (StringUtils.isBlank(balanceTypeCode) || transaction.getBalanceType() == null) {
            return MessageBuilder.buildErrorMessage(KFSKeyConstants.ERROR_BALANCE_TYPE_NOT_FOUND, balanceTypeCode, Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * Check if the given transaction contains valid object type code
     * @param transaction the given transaction
     * @return null if the object type code is valid; otherwise, return error message
     */
    public static Message checkFinancialObjectTypeCode(Transaction transaction) {
        String objectTypeCode = transaction.getFinancialObjectTypeCode();
        if (StringUtils.isBlank(objectTypeCode) || transaction.getObjectType() == null) {
            return MessageBuilder.buildErrorMessage(KFSKeyConstants.ERROR_OBJECT_TYPE_NOT_FOUND, objectTypeCode, Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * Check if the given transaction contains university fiscal period code
     * @param transaction the given transaction
     * @return null if the university fiscal period code is valid; otherwise, return error message
     */
    public static Message checkUniversityFiscalPeriodCode(Transaction transaction) {
        String fiscalPeriodCode = transaction.getUniversityFiscalPeriodCode();
        if (StringUtils.isBlank(fiscalPeriodCode)) {
            return MessageBuilder.buildErrorMessage(KFSKeyConstants.ERROR_ACCOUNTING_PERIOD_NOT_FOUND, fiscalPeriodCode, Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * Check if the given transaction contains document type code
     * @param transaction the given transaction
     * @return null if the document type code is valid; otherwise, return error message
     */
    public static Message checkFinancialDocumentTypeCode(Transaction transaction) {
        String documentTypeCode = transaction.getFinancialDocumentTypeCode();
        if (StringUtils.isBlank(documentTypeCode) || transaction.getDocumentType() == null) {
            return MessageBuilder.buildErrorMessage(KFSKeyConstants.ERROR_DOCUMENT_TYPE_NOT_FOUND, documentTypeCode, Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * Check if the given transaction contains document number
     * @param transaction the given transaction
     * @return null if the document number is valid; otherwise, return error message
     */
    public static Message checkFinancialDocumentNumber(Transaction transaction) {
        String documentNumber = transaction.getDocumentNumber();
        if (StringUtils.isBlank(documentNumber)) {
            return MessageBuilder.buildErrorMessage(KFSKeyConstants.ERROR_DOCUMENT_NUMBER_REQUIRED, Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * Check if the given transaction contains transaction sequence number
     * @param transaction the given transaction
     * @return null if the transaction sequence number is valid; otherwise, return error message
     */
    public static Message checkTransactionLedgerEntrySequenceNumber(Transaction transaction) {
        Integer sequenceNumber = transaction.getTransactionLedgerEntrySequenceNumber();
        if (sequenceNumber == null) {
            return MessageBuilder.buildErrorMessage(KFSKeyConstants.ERROR_SEQUENCE_NUMBER_NOT_BE_NULL, Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * Check if the given transaction contains debit credit code
     * @param transaction the given transaction
     * @return null if the debit credit code is valid; otherwise, return error message
     */
    public static Message checkTransactionDebitCreditCode(Transaction transaction) {
        String[] validDebitCreditCode = { KFSConstants.GL_BUDGET_CODE, KFSConstants.GL_CREDIT_CODE, KFSConstants.GL_DEBIT_CODE };
        String debitCreditCode = transaction.getTransactionDebitCreditCode();
        if (!ArrayUtils.contains(validDebitCreditCode, debitCreditCode)) {
            return MessageBuilder.buildErrorMessage(KFSKeyConstants.ERROR_DEDIT_CREDIT_CODE_NOT_BE_NULL, Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * Check if the given transaction contains system origination code
     * @param transaction the given transaction
     * @return null if the system origination code is valid; otherwise, return error message
     */
    public static Message checkFinancialSystemOriginationCode(Transaction transaction) {
        String originationCode = transaction.getFinancialSystemOriginationCode();
        if (StringUtils.isBlank(originationCode)) {
            return MessageBuilder.buildErrorMessage(KFSKeyConstants.ERROR_ORIGIN_CODE_NOT_FOUND, Message.TYPE_FATAL);
        }
        return null;
    }
    
    /**
     * Check if the given transaction contains the posteable period code
     * @param transaction the given transaction
     * @param unpostableperidCodes the list of unpostable period code
     * @return null if the perid code of the transaction is not in unpostableperidCodes; otherwise, return error message
     */
    public static Message checkPostablePeridCode(Transaction transaction, String[] unpostableperidCodes) {
        String periodCode = transaction.getUniversityFiscalPeriodCode();
        if (ArrayUtils.contains(unpostableperidCodes, periodCode)) {
            return MessageBuilder.buildErrorMessage(KFSKeyConstants.Labor.ERROR_UNPOSTABLE_PERIOD_CODE, periodCode, Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * Check if the given transaction contains the posteable balance type code
     * @param transaction the given transaction
     * @param unpostableBalanceTypeCodes the list of unpostable balance type codes
     * @return null if the balance type code of the transaction is not in unpostableBalanceTypeCodes; otherwise, return error message
     */
    public static Message checkPostableBalanceTypeCode(Transaction transaction, String[] unpostableBalanceTypeCodes) {
        String balanceTypeCode = transaction.getFinancialBalanceTypeCode();
        if (ArrayUtils.contains(unpostableBalanceTypeCodes, balanceTypeCode)) {
            return MessageBuilder.buildErrorMessage(KFSKeyConstants.Labor.ERROR_UNPOSTABLE_BALANCE_TYPE, balanceTypeCode, Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * Check if the transaction amount of the given transaction is ZERO
     * @param transaction the given transaction
     * @return null if the transaction amount is not ZERO or null; otherwise, return error message
     */
    public static Message checkZeroTotalAmount(Transaction transaction) {
        KualiDecimal amount = transaction.getTransactionLedgerEntryAmount();
        if (amount == null || amount.isZero()) {
            return MessageBuilder.buildErrorMessage(KFSKeyConstants.Labor.ERROR_ZERO_TOTAL_AMOUNT, Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * Check if the given transaction contains the posteable object code
     * @param transaction the given transaction
     * @param unpostableObjectCodes the list of unpostable object codes
     * @return null if the object code of the transaction is not in unpostableObjectCodes; otherwise, return error message
     */
    public static Message checkPostableObjectCode(Transaction transaction, String[] unpostableObjectCodes) {
        String objectCode = transaction.getFinancialObjectCode();
        if (ArrayUtils.contains(unpostableObjectCodes, objectCode)) {
            return MessageBuilder.buildErrorMessage(KFSKeyConstants.Labor.ERROR_UNPOSTABLE_OBJECT_CODE, objectCode, Message.TYPE_FATAL);
        }
        return null;
    }
}
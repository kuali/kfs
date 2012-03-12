/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ld.document.validation.impl;


import java.util.Collection;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.batch.service.LaborAccountingCycleCachingService;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.module.ld.businessobject.LaborTransaction;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.MessageBuilder;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class provides a set of utilities that can be used to validate a transaction in the field level.
 */
public class TransactionFieldValidator {
    private static LaborAccountingCycleCachingService accountingCycleCachingService;
    private static ConfigurationService kualiConfigurationService;
    
    /**
     * Checks if the given transaction contains valid university fiscal year
     * 
     * @param transaction the given transaction
     * @return null if the university fiscal year is valid; otherwise, return error message
     */
    public static Message checkUniversityFiscalYear(LaborTransaction transaction) {

        Integer fiscalYear = transaction.getUniversityFiscalYear();
        if (fiscalYear == null) {
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_UNIV_FISCAL_YR_NOT_FOUND, Message.TYPE_FATAL);
        }

        else {
            SystemOptions option = getAccountingCycleCachingService().getSystemOptions(((LaborOriginEntry) transaction).getUniversityFiscalYear());
            if (ObjectUtils.isNull(option)) {
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_UNIV_FISCAL_YR_NOT_FOUND, fiscalYear.toString(), Message.TYPE_FATAL);
            }
        }
        return null;
    }

    /**
     * Checks if the given transaction contains valid char of accounts code
     * 
     * @param transaction the given transaction
     * @return null if the char of accounts code is valid; otherwise, return error message
     */
    public static Message checkChartOfAccountsCode(LaborTransaction transaction) {
        String chartOfAccountsCode = transaction.getChartOfAccountsCode();
        Chart chart = getAccountingCycleCachingService().getChart(((LaborOriginEntry) transaction).getChartOfAccountsCode());
        if (StringUtils.isBlank(chartOfAccountsCode) || ObjectUtils.isNull(chart)) {
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_CHART_NOT_FOUND, chartOfAccountsCode, Message.TYPE_FATAL);
        }

        if (!chart.isActive()) {
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_CHART_NOT_ACTIVE, chartOfAccountsCode, Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * Checks if the given transaction contains valid account number
     * 
     * @param transaction the given transaction
     * @return null if the account number is valid; otherwise, return error message
     */
    public static Message checkAccountNumber(LaborTransaction transaction) {
        String accountNumber = transaction.getAccountNumber();
        Account account = getAccountingCycleCachingService().getAccount(((LaborOriginEntry) transaction).getChartOfAccountsCode(), ((LaborOriginEntry) transaction).getAccountNumber());
        if (StringUtils.isBlank(accountNumber) || ObjectUtils.isNull(account)) {
            String chartOfAccountsCode = transaction.getChartOfAccountsCode();
            String accountKey = chartOfAccountsCode + "-" + accountNumber;
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_ACCOUNT_NOT_FOUND, accountKey, Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * Checks if the given transaction contains valid sub account number
     * 
     * @param transaction the given transaction
     * @return null if the sub account number is valid; otherwise, return error message
     */
    public static Message checkSubAccountNumber(LaborTransaction transaction) {
        return checkSubAccountNumber(transaction, null);
    }

    /**
     * Checks if the given transaction contains valid sub account number
     * 
     * @param transaction the given transaction
     * @param exclusiveDocumentTypeCode inactive sub account can be OK if the document type of the given transaction is
     *        exclusiveDocumentTypeCode
     * @return null if the sub account number is valid; otherwise, return error message
     */
    public static Message checkSubAccountNumber(LaborTransaction transaction, String exclusiveDocumentTypeCode) {
        String subAccountNumber = transaction.getSubAccountNumber();
        String chartOfAccountsCode = transaction.getChartOfAccountsCode();
        String accountNumber = transaction.getAccountNumber();
        String documentTypeCode = transaction.getFinancialDocumentTypeCode();
        String subAccountKey = chartOfAccountsCode + "-" + accountNumber + "-" + subAccountNumber;
        SubAccount subAccount = getAccountingCycleCachingService().getSubAccount(((LaborOriginEntry) transaction).getChartOfAccountsCode(), ((LaborOriginEntry) transaction).getAccountNumber(), ((LaborOriginEntry) transaction).getSubAccountNumber());

        if (StringUtils.isBlank(subAccountNumber)) {
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_SUB_ACCOUNT_NOT_FOUND, subAccountKey, Message.TYPE_FATAL);
        }

        if (!KFSConstants.getDashSubAccountNumber().equals(subAccountNumber)) {
            if (ObjectUtils.isNull(subAccount)) {
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_SUB_ACCOUNT_NOT_FOUND, subAccountKey, Message.TYPE_FATAL);
            }

            if (!StringUtils.equals(documentTypeCode, exclusiveDocumentTypeCode)) {
                if (!subAccount.isActive()) {
                    return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_SUB_ACCOUNT_NOT_ACTIVE, subAccountKey, Message.TYPE_FATAL);
                }
            }
        }
        return null;
    }

    /**
     * Checks if the given transaction contains valid account number
     * 
     * @param transaction the given transaction
     * @return null if the account number is valid; otherwise, return error message
     */
    public static Message checkFinancialObjectCode(LaborTransaction transaction) {
        String objectCode = transaction.getFinancialObjectCode();
        if (StringUtils.isBlank(objectCode)) {
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_OBJECT_CODE_EMPTY, Message.TYPE_FATAL);
        }

        Integer fiscalYear = transaction.getUniversityFiscalYear();
        String chartOfAccountsCode = transaction.getChartOfAccountsCode();
        String objectCodeKey = fiscalYear + "-" + chartOfAccountsCode + "-" + objectCode;
        ObjectCode financialObject = getAccountingCycleCachingService().getObjectCode(((LaborOriginEntry) transaction).getUniversityFiscalYear(), ((LaborOriginEntry) transaction).getChartOfAccountsCode(), ((LaborOriginEntry) transaction).getFinancialObjectCode());
        
        //do we need it?
        transaction.refreshNonUpdateableReferences();
        
        if (ObjectUtils.isNull(financialObject)) {
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_OBJECT_CODE_NOT_FOUND, objectCodeKey, Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * Checks if the given transaction contains valid sub object code
     * 
     * @param transaction the given transaction
     * @return null if the sub object code is valid; otherwise, return error message
     */
    public static Message checkFinancialSubObjectCode(LaborTransaction transaction) {
        Integer fiscalYear = transaction.getUniversityFiscalYear();
        String chartOfAccountsCode = transaction.getChartOfAccountsCode();
        String objectCode = transaction.getFinancialObjectCode();
        String subObjectCode = transaction.getFinancialSubObjectCode();

        String subObjectCodeKey = fiscalYear + "-" + chartOfAccountsCode + "-" + objectCode + "-" + subObjectCode;
        if (StringUtils.isBlank(subObjectCode)) {
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_SUB_OBJECT_CODE_NOT_BE_NULL, subObjectCodeKey, Message.TYPE_FATAL);
        }
        SubObjectCode financialSubObject = getAccountingCycleCachingService().getSubObjectCode(((LaborOriginEntry) transaction).getUniversityFiscalYear(), ((LaborOriginEntry) transaction).getChartOfAccountsCode(), ((LaborOriginEntry) transaction).getAccountNumber(), ((LaborOriginEntry) transaction).getFinancialObjectCode(), ((LaborOriginEntry) transaction).getFinancialSubObjectCode());
        if (!KFSConstants.getDashFinancialSubObjectCode().equals(subObjectCode)) {
            if (ObjectUtils.isNull(financialSubObject)) {
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_SUB_OBJECT_CODE_NOT_BE_NULL, subObjectCodeKey, Message.TYPE_FATAL);
            }
        }
        return null;
    }

    /**
     * Checks if the given transaction contains valid balance type code
     * 
     * @param transaction the given transaction
     * @return null if the balance type code is valid; otherwise, return error message
     */
    public static Message checkFinancialBalanceTypeCode(LaborTransaction transaction) {
        String balanceTypeCode = transaction.getFinancialBalanceTypeCode();
        BalanceType balanceType = getAccountingCycleCachingService().getBalanceType(((LaborOriginEntry) transaction).getFinancialBalanceTypeCode());
        if (StringUtils.isBlank(balanceTypeCode) || ObjectUtils.isNull(balanceType)) {
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_BALANCE_TYPE_NOT_FOUND, balanceTypeCode, Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * Checks if the given transaction contains valid object type code
     * 
     * @param transaction the given transaction
     * @return null if the object type code is valid; otherwise, return error message
     */
    public static Message checkFinancialObjectTypeCode(LaborTransaction transaction) {
        String objectTypeCode = transaction.getFinancialObjectTypeCode();
        ObjectType objectType = getAccountingCycleCachingService().getObjectType(((LaborOriginEntry) transaction).getFinancialObjectTypeCode());
        if (StringUtils.isBlank(objectTypeCode) || ObjectUtils.isNull(objectType)) {
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_OBJECT_TYPE_NOT_FOUND, objectTypeCode, Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * Checks if the given transaction contains university fiscal period code
     * 
     * @param transaction the given transaction
     * @return null if the university fiscal period code is valid; otherwise, return error message
     */
    public static Message checkUniversityFiscalPeriodCode(LaborTransaction transaction) {
        String fiscalPeriodCode = transaction.getUniversityFiscalPeriodCode();
        if (StringUtils.isBlank(fiscalPeriodCode)) {
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_ACCOUNTING_PERIOD_NOT_FOUND, fiscalPeriodCode, Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * Checks if the given transaction contains document type code
     * 
     * @param transaction the given transaction
     * @return null if the document type code is valid; otherwise, return error message
     */
    public static Message checkFinancialDocumentTypeCode(LaborTransaction transaction) {
        if (StringUtils.isBlank(transaction.getFinancialDocumentTypeCode()) || !getAccountingCycleCachingService().isCurrentActiveAccountingDocumentType(transaction.getFinancialDocumentTypeCode())) {
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_DOCUMENT_TYPE_NOT_FOUND, transaction.getFinancialDocumentTypeCode(), Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * Checks if the given transaction contains document number
     * 
     * @param transaction the given transaction
     * @return null if the document number is valid; otherwise, return error message
     */
    public static Message checkFinancialDocumentNumber(LaborTransaction transaction) {
        String documentNumber = transaction.getDocumentNumber();
        if (StringUtils.isBlank(documentNumber)) {
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_DOCUMENT_NUMBER_REQUIRED, Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * Checks if the given transaction contains transaction sequence number
     * 
     * @param transaction the given transaction
     * @return null if the transaction sequence number is valid; otherwise, return error message
     */
    // Don't need to check SequenceNumber because it sets in each poster (LaborLedgerEntryPoster and LaborGLLedgerEntryPoster), so commented out
//    public static Message checkTransactionLedgerEntrySequenceNumber(LaborTransaction transaction) {
//        Integer sequenceNumber = transaction.getTransactionLedgerEntrySequenceNumber();
//        if (sequenceNumber == null) {
//            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_SEQUENCE_NUMBER_NOT_BE_NULL, Message.TYPE_FATAL);
//        }
//        return null;
//    }

    /**
     * Checks if the given transaction contains debit credit code
     * 
     * @param transaction the given transaction
     * @return null if the debit credit code is valid; otherwise, return error message
     */
    public static Message checkTransactionDebitCreditCode(LaborTransaction transaction) {
        String[] validDebitCreditCode = { KFSConstants.GL_BUDGET_CODE, KFSConstants.GL_CREDIT_CODE, KFSConstants.GL_DEBIT_CODE };
        String debitCreditCode = transaction.getTransactionDebitCreditCode();
        if (debitCreditCode == null || !ArrayUtils.contains(validDebitCreditCode, debitCreditCode)) {
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_DEDIT_CREDIT_CODE_NOT_BE_NULL, Message.TYPE_FATAL);
        } else if (transaction.getBalanceType().isFinancialOffsetGenerationIndicator() && !KFSConstants.GL_DEBIT_CODE.equals(transaction.getTransactionDebitCreditCode()) && !KFSConstants.GL_CREDIT_CODE.equals(transaction.getTransactionDebitCreditCode())) {
            return new Message(getConfigurationService().getPropertyValueAsString(KFSKeyConstants.MSG_DEDIT_CREDIT_CODE_MUST_BE) + " '" + KFSConstants.GL_DEBIT_CODE + " or " + KFSConstants.GL_CREDIT_CODE + getConfigurationService().getPropertyValueAsString(KFSKeyConstants.MSG_FOR_BALANCE_TYPE), Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * Checks if the given transaction contains system origination code
     * 
     * @param transaction the given transaction
     * @return null if the system origination code is valid; otherwise, return error message
     */
    public static Message checkFinancialSystemOriginationCode(LaborTransaction transaction) {
        String originationCode = transaction.getFinancialSystemOriginationCode();
        if (StringUtils.isBlank(originationCode)) {
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_ORIGIN_CODE_NOT_FOUND, Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * Checks if the given transaction contains the posteable period code
     * 
     * @param transaction the given transaction
     * @param unpostableperidCodes the list of unpostable period code
     * @return null if the perid code of the transaction is not in unpostableperidCodes; otherwise, return error message
     */
    public static Message checkPostablePeridCode(LaborTransaction transaction, Collection<String> unpostableperidCodes) {
        String periodCode = transaction.getUniversityFiscalPeriodCode();
        if (unpostableperidCodes.contains(periodCode)) {
            return MessageBuilder.buildMessage(LaborKeyConstants.ERROR_UNPOSTABLE_PERIOD_CODE, periodCode, Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * Checks if the given transaction contains the posteable balance type code
     * 
     * @param transaction the given transaction
     * @param unpostableBalanceTypeCodes the list of unpostable balance type codes
     * @return null if the balance type code of the transaction is not in unpostableBalanceTypeCodes; otherwise, return error
     *         message
     */
    public static Message checkPostableBalanceTypeCode(LaborTransaction transaction, Collection<String> unpostableBalanceTypeCodes) {
        String balanceTypeCode = transaction.getFinancialBalanceTypeCode();
        if (unpostableBalanceTypeCodes.contains(balanceTypeCode)) {
            return MessageBuilder.buildMessage(LaborKeyConstants.ERROR_UNPOSTABLE_BALANCE_TYPE, balanceTypeCode, Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * Checks if the transaction amount of the given transaction is ZERO
     * 
     * @param transaction the given transaction
     * @return null if the transaction amount is not ZERO or null; otherwise, return error message
     */
    public static Message checkZeroTotalAmount(LaborTransaction transaction) {
        KualiDecimal amount = transaction.getTransactionLedgerEntryAmount();
        if (amount == null || amount.isZero()) {
            return MessageBuilder.buildMessage(LaborKeyConstants.ERROR_ZERO_TOTAL_AMOUNT, Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * Checks if the given transaction contains the valid employee id
     * 
     * @param transaction the given transaction
     * @param unpostableObjectCodes the list of unpostable object codes
     * @return null if the object code of the transaction is not in unpostableObjectCodes; otherwise, return error message
     */
    public static Message checkEmplid(LaborTransaction transaction) {
        String emplid = transaction.getEmplid();
        if (StringUtils.isBlank(emplid)) {
            return MessageBuilder.buildMessage(LaborKeyConstants.MISSING_EMPLOYEE_ID, Message.TYPE_FATAL);
        }
        return null;
    }
    
    /**
     * When in Rome... This method checks if the encumbrance update code is valid
     * @param transaction the transaction to check
     * @return a Message if the encumbrance update code is not valid, or null if all is well
     */
    public static Message checkEncumbranceUpdateCode(LaborTransaction transaction) {
        // The encumbrance update code can only be space, N, R or D. Nothing else
        if ((StringUtils.isNotBlank(transaction.getTransactionEncumbranceUpdateCode())) && (!" ".equals(transaction.getTransactionEncumbranceUpdateCode())) && (!KFSConstants.ENCUMB_UPDT_NO_ENCUMBRANCE_CD.equals(transaction.getTransactionEncumbranceUpdateCode())) && (!KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD.equals(transaction.getTransactionEncumbranceUpdateCode())) && (!KFSConstants.ENCUMB_UPDT_DOCUMENT_CD.equals(transaction.getTransactionEncumbranceUpdateCode()))) {
            return new Message("Invalid Encumbrance Update Code (" + transaction.getTransactionEncumbranceUpdateCode() + ")", Message.TYPE_FATAL);
        }
        return null;
    }
    
    static LaborAccountingCycleCachingService getAccountingCycleCachingService() {
        if (accountingCycleCachingService == null) {
            accountingCycleCachingService = SpringContext.getBean(LaborAccountingCycleCachingService.class);
        }
        return accountingCycleCachingService;        
    }
    
    static ConfigurationService getConfigurationService() {
        if (kualiConfigurationService == null) {
            kualiConfigurationService = SpringContext.getBean(ConfigurationService.class);
        }
        return kualiConfigurationService;        
    }
}

/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.validation.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.FeeClassCode;
import org.kuali.kfs.module.endow.businessobject.FeeEndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.FeeMethod;
import org.kuali.kfs.module.endow.businessobject.FeePaymentType;
import org.kuali.kfs.module.endow.businessobject.FeeSecurity;
import org.kuali.kfs.module.endow.businessobject.FeeTransaction;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.TransactionTypeCode;
import org.kuali.kfs.module.endow.document.service.ClassCodeService;
import org.kuali.kfs.module.endow.document.service.EndowmentTransactionCodeService;
import org.kuali.kfs.module.endow.document.service.FeeMethodService;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.module.endow.document.service.TransactionTypeService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.rule.event.ApproveDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.MessageMap;
import org.kuali.rice.kns.util.ObjectUtils;

public class FeeMethodRule extends MaintenanceDocumentRuleBase {

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRule#processRouteDocument(org.kuali.rice.kns.document.Document)
     */
    @Override
    public boolean processRouteDocument(Document document) {
        boolean isValid = true;
        isValid &= super.processRouteDocument(document);
        MessageMap errorMap = GlobalVariables.getMessageMap();
        isValid &= errorMap.hasNoErrors();

        if (!isValid) {
            return isValid;
        }

        isValid &= validationRulesPassedForFeeMethod(document);

        return isValid;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRule#processApproveDocument(ApproveDocumentEvent)
     */
    @Override
    public boolean processApproveDocument(ApproveDocumentEvent approveEvent) {
        boolean isValid = true;
        isValid &= super.processApproveDocument(approveEvent);
        MessageMap errorMap = GlobalVariables.getMessageMap();
        isValid &= errorMap.hasNoErrors();

        if (!isValid) {
            return isValid;
        }

        Document document = (Document) approveEvent.getDocument();
        isValid &= validationRulesPassedForFeeMethod(document);

        return isValid;
    }

    /**
     * This method will validate the custom business rules for fee method
     * 
     * @param feeMethod
     * @return true if rules are passed else return false
     */
    private boolean validationRulesPassedForFeeMethod(Document document) {
        boolean rulesPassed = true;

        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;
        FeeMethod feeMethod = (FeeMethod) maintenanceDocument.getNewMaintainableObject().getBusinessObject();

        rulesPassed &= checkFeeTransactionTypeCodeValue(feeMethod); // rule #2
        rulesPassed &= recordExistsInFeeTransactionType(feeMethod); // rule #3
        rulesPassed &= recordExistsInFeeEndowmentTransactionType(feeMethod); // rule #4
        rulesPassed &= checkFeeBalanceTypeCodeValue(feeMethod); // rule #5
        rulesPassed &= recordExistsInFeeClassCode(feeMethod); // rule #6
        rulesPassed &= recordExistsInFeeSecurity(feeMethod); // rule #7
        rulesPassed &= checkFeePaymentTypeCodeValue(feeMethod); // rule #8
        rulesPassed &= recordExistsInFeePaymentType(feeMethod); // rule #9
        rulesPassed &= checkRateDefinitionAndFeeBalanceTypes(feeMethod); // rule #12 and rule#14
        rulesPassed &= recordExistsInEndowmentTransactionCode(feeMethod); // rule #12 and rule#14
        rulesPassed &= checkCorpusToMarketTolerance(feeMethod); // rule #16
        rulesPassed &= checkFeeRateAndBreakpointAmounts(feeMethod); // rule #17, rule #18, rule #19
        rulesPassed &= checkFrequencyCodeNotChangedIfFeeMethodUsedOnAnyKemid(document);

        return rulesPassed;
    }

    /**
     * This method will check that if Fee By Transaction Type or Fee By ETran Code or both set to Y and Fee Type is set to T.
     * 
     * @param feeMethod
     * @return true if Fee By Transaction Type field or Fee By ETran Code field or both set to Y AND fee type code is T else return
     *         false
     */
    private boolean checkFeeTransactionTypeCodeValue(FeeMethod feeMethod) {
        boolean isValidTransactionCode = true;

        if (feeMethod.getFeeByTransactionType() || feeMethod.getFeeByETranCode()) {
            if (ObjectUtils.isNotNull(feeMethod.getFeeTypeCode()) && !EndowConstants.FeeMethod.FEE_TYPE_CODE_VALUE_FOR_TRANSACTIONS.equalsIgnoreCase(feeMethod.getFeeTypeCode())) {
                GlobalVariables.getMessageMap().putError(EndowPropertyConstants.FEE_TYPE_CODE, EndowKeyConstants.FeeMethodConstants.ERROR_INVALID_FEE_TYPE_CODE_FOR_TRANSACTIONS_ENTERED);
                return false;
            }
        }

        return isValidTransactionCode;
    }

    /**
     * This method will check that if Fee By Class Code or Fee By Security or both set to Yes and Fee Type is set to B.
     * 
     * @param feeMethod
     * @return true if Fee By Transaction Type field or Fee By ETran Code field or both set to Y AND fee type code is B else return
     *         false
     */
    private boolean checkFeeBalanceTypeCodeValue(FeeMethod feeMethod) {
        boolean isValidTransactionCode = true;

        if (feeMethod.getFeeByClassCode() || feeMethod.getFeeBySecurityCode()) {
            if (ObjectUtils.isNotNull(feeMethod.getFeeTypeCode()) && !EndowConstants.FeeMethod.FEE_TYPE_CODE_VALUE_FOR_BALANCES.equalsIgnoreCase(feeMethod.getFeeTypeCode())) {
                GlobalVariables.getMessageMap().putError(EndowPropertyConstants.FEE_TYPE_CODE, EndowKeyConstants.FeeMethodConstants.ERROR_INVALID_FEE_TYPE_CODE_FOR_BALANCE_ENTERED);
                return false;
            }
        }

        return isValidTransactionCode;
    }

    /**
     * This method will check that if Fee Type is set to value P then fee base code should be I
     * 
     * @param feeMethod
     * @return true if Fee Type is P and Fee Base code is I else return false
     */
    private boolean checkFeePaymentTypeCodeValue(FeeMethod feeMethod) {
        boolean isValidBaseCode = true;

        if (ObjectUtils.isNotNull(feeMethod.getFeeTypeCode()) && EndowConstants.FeeMethod.FEE_TYPE_CODE_VALUE_FOR_PAYMENTS.equalsIgnoreCase(feeMethod.getFeeTypeCode())) {
            if (!EndowConstants.FeeMethod.FEE_BASE_CD_VALUE.equalsIgnoreCase(feeMethod.getFeeBaseCode())) {
                GlobalVariables.getMessageMap().putError(EndowPropertyConstants.FEE_BASE_CODE, EndowKeyConstants.FeeMethodConstants.ERROR_INVALID_FEE_BASE_CODE_FOR_PAYMENTS_ENTERED);
                return false;
            }
        }

        return isValidBaseCode;
    }

    /**
     * This method will check if at least one record exists in Fee Transaction Type collection with INCL checked on when Fee
     * Transaction Type Code is checked on
     * 
     * @param feeMethod
     * @return true if Fee Transaction Type code checked on and at least one record with INCL flag is checked on else return false
     */
    private boolean recordExistsInFeeTransactionType(FeeMethod feeMethod) {
        boolean recordExists = true;

        if (feeMethod.getFeeByTransactionType()) {
            recordExists = false;
            List<FeeTransaction> feeTransactions = (List<FeeTransaction>) feeMethod.getFeeTransactions();

            for (FeeTransaction feeTransactionsRecord : feeTransactions) {
                if (feeTransactionsRecord.getInclude()) {
                    recordExists = true;
                    break;
                }
            }
            if (!recordExists) {
                GlobalVariables.getMessageMap().putError(EndowPropertyConstants.FEE_BY_TRANSACTION_TYPE_CODE, EndowKeyConstants.FeeMethodConstants.ERROR_NO_RECORDS_WITH_YES_IN_FEE_TRANSACTION_TYPE);
            }
        }

        return recordExists;
    }

    /**
     * This method will check if at least one record exists in Fee Class Codes collection with INCL checked on when Fee Class code
     * is checked on.
     * 
     * @param feeMethod
     * @return true if Fee Class code is checked on and at least one record with INCL flag is checked on else return false
     */
    private boolean recordExistsInFeeClassCode(FeeMethod feeMethod) {
        boolean recordExists = true;

        if (feeMethod.getFeeByClassCode()) {
            recordExists = false;
            List<FeeClassCode> feeClassCodes = (List<FeeClassCode>) feeMethod.getFeeClassCodes();

            for (FeeClassCode feeClassCodeRecord : feeClassCodes) {
                if (feeClassCodeRecord.getInclude()) {
                    recordExists = true;
                    break;
                }
            }
            if (!recordExists) {
                GlobalVariables.getMessageMap().putError(EndowPropertyConstants.FEE_BY_CLASS_CODE, EndowKeyConstants.FeeMethodConstants.ERROR_NO_RECORDS_WITH_YES_IN_FEE_CLASS_CODE);
            }
        }

        return recordExists;
    }

    /**
     * This method will check if at least one record exists in Fee Endowment Transaction Codes collection with INCL checked on when
     * Fee Endowment Transaction Code is checked on.
     * 
     * @param feeMethod
     * @return true if Fee EndowmentTransaction Type is checked on and at least one record with INCL flag is checked on else return
     *         false
     */
    private boolean recordExistsInFeeEndowmentTransactionType(FeeMethod feeMethod) {
        boolean recordExists = true;

        if (feeMethod.getFeeByETranCode()) {
            recordExists = false;
            List<FeeEndowmentTransactionCode> feeEndowmentTransactionCodes = (List<FeeEndowmentTransactionCode>) feeMethod.getFeeEndowmentTransactionCodes();

            for (FeeEndowmentTransactionCode feeEndowmentTransactionCodesRecord : feeEndowmentTransactionCodes) {
                if (feeEndowmentTransactionCodesRecord.getInclude()) {
                    recordExists = true;
                    break;
                }
            }
            if (!recordExists) {
                GlobalVariables.getMessageMap().putError(EndowPropertyConstants.FEE_BY_ENDOWMENT_TRANSACTION_TYPE_CODE, EndowKeyConstants.FeeMethodConstants.ERROR_NO_RECORDS_WITH_YES_IN_FEE_ENDOWMENT_TRANSACTION_CODE);
            }
        }

        return recordExists;
    }

    /**
     * This method will check if at least one record exists in Fee Security collection with INCL checked on when Fee Class Code is
     * checked on.
     * 
     * @param feeMethod
     * @return true if Fee Security Code is checked on and at least one record with INCL flag is checked on else return false
     */
    private boolean recordExistsInFeeSecurity(FeeMethod feeMethod) {
        boolean recordExists = true;

        if (feeMethod.getFeeBySecurityCode()) {
            recordExists = false;
            List<FeeSecurity> feeSecurity = (List<FeeSecurity>) feeMethod.getFeeSecurity();

            for (FeeSecurity feeSecurityRecord : feeSecurity) {
                if (feeSecurityRecord.getInclude()) {
                    recordExists = true;
                    break;
                }
            }
            if (!recordExists) {
                GlobalVariables.getMessageMap().putError(EndowPropertyConstants.FEE_BY_SECURITY_CODE, EndowKeyConstants.FeeMethodConstants.ERROR_NO_RECORDS_WITH_YES_IN_FEE_SECURITY_CODE);
            }
        }

        return recordExists;
    }

    /**
     * This method will check if at least one record exists in Fee Payment Type collection with INCL checked on when Fee Type Code
     * is checked on.
     * 
     * @param feeMethod
     * @return true if at least one record with INCL flag is checked on else return false
     */
    private boolean recordExistsInFeePaymentType(FeeMethod feeMethod) {
        boolean recordExists = true;

        if (ObjectUtils.isNotNull(feeMethod.getFeeTypeCode()) && EndowConstants.FeeMethod.FEE_TYPE_CODE_VALUE_FOR_PAYMENTS.equalsIgnoreCase(feeMethod.getFeeTypeCode())) {
            recordExists = false;
            List<FeePaymentType> feePaymentTypes = (List<FeePaymentType>) feeMethod.getFeePaymentTypes();

            for (FeePaymentType feePaymentTypeRecord : feePaymentTypes) {
                if (feePaymentTypeRecord.getInclude()) {
                    recordExists = true;
                    break;
                }
            }

            if (!recordExists) {
                GlobalVariables.getMessageMap().putError(EndowPropertyConstants.FEE_BASE_CODE, EndowKeyConstants.FeeMethodConstants.ERROR_NO_RECORDS_WITH_YES_IN_FEE_PAYMENT_TYPE);
            }
        }

        return recordExists;
    }

    /**
     * This method will check rate definition and fee balance types based on fee type code
     * 
     * @param feeMethod
     * @return true if ((rate definition is C AND fee type code is B AND fee balances types are AU or CU and/or MU) or (rate
     *         definition is V AND fee type code is B AND fee balances types are AMV or CMV and/or MMV)) else return false
     */
    private boolean checkRateDefinitionAndFeeBalanceTypes(FeeMethod feeMethod) {
        boolean isValid = true;

        String feeRateDefinitionCode = feeMethod.getFeeRateDefinitionCode();

        if (ObjectUtils.isNotNull(feeRateDefinitionCode)) {
            // if rate definition is C and type fee code is B
            if (EndowConstants.FeeMethod.FEE_RATE_DEFINITION_CODE_FOR_COUNT.equalsIgnoreCase(feeRateDefinitionCode) && EndowConstants.FeeMethod.FEE_TYPE_CODE_VALUE_FOR_BALANCES.equalsIgnoreCase(feeMethod.getFeeTypeCode())) {
                String feeBalanceTypeCode = feeMethod.getFeeBalanceTypeCode();

                if (!EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_AVERAGE_UNITS.equalsIgnoreCase(feeBalanceTypeCode) && !EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_CURRENT_UNITS.equalsIgnoreCase(feeBalanceTypeCode) && !EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_MONTH_END_UNITS.equalsIgnoreCase(feeBalanceTypeCode)) {
                    putFieldError(EndowPropertyConstants.FEE_BALANCE_TYPES_CODE, EndowKeyConstants.FeeMethodConstants.ERROR_INVALID_FEE_BALANCE_TYPE_CODE_WHEN_COUNT_ENTERED);
                    return false;
                }
            }

            // if rate definition is V and type fee code is B
            if (EndowConstants.FeeMethod.FEE_RATE_DEFINITION_CODE_FOR_VALUE.equalsIgnoreCase(feeRateDefinitionCode) && EndowConstants.FeeMethod.FEE_TYPE_CODE_VALUE_FOR_BALANCES.equalsIgnoreCase(feeMethod.getFeeTypeCode())) {
                String feeBalanceTypeCode = feeMethod.getFeeBalanceTypeCode();

                if (!EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_AVERAGE_MARKET_VALUE.equalsIgnoreCase(feeBalanceTypeCode) && !EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_CURRENT_MARKET_VALUE.equalsIgnoreCase(feeBalanceTypeCode) && !EndowConstants.FeeBalanceTypes.FEE_BALANCE_TYPE_VALUE_FOR_MONTH_END_MARKET_VALUE.equalsIgnoreCase(feeBalanceTypeCode)) {
                    putFieldError(EndowPropertyConstants.FEE_BALANCE_TYPES_CODE, EndowKeyConstants.FeeMethodConstants.ERROR_INVALID_FEE_BALANCE_TYPE_CODE_WHEN_VALUE_ENTERED);
                    return false;
                }
            }
        }

        return isValid;
    }

    /**
     * This method will check if record exists in Endowment Transaction Code table
     * 
     * @param feeMethod feeMethod object
     * @return true if Fee Endowment Transaction code exists else return false
     */
    private boolean recordExistsInEndowmentTransactionCode(FeeMethod feeMethod) {
        boolean recordExists = true;

        String endowmentTransactionCode = feeMethod.getFeeExpenseETranCode();

        EndowmentTransactionCodeService endowmentTransactionCodeService = SpringContext.getBean(EndowmentTransactionCodeService.class);
        EndowmentTransactionCode endowmentTransaction = endowmentTransactionCodeService.getByPrimaryKey(endowmentTransactionCode.toUpperCase());

        if (ObjectUtils.isNull(endowmentTransaction)) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.FEE_ENDOWMENT_TRANSACTION_TYPE_CODE_ATTRIBUTE, EndowKeyConstants.FeeMethodConstants.ERROR_NO_RECORD_IN_ENDOWMENT_TRANSACTION_CODE);
            return false;
        }

        return recordExists;
    }

    /**
     * This method will check that if corpus to market tolerance is 0 or > 1
     * 
     * @param feeMethod
     * @return true if corpus to market tolerance is 0 (default) or > 1.00 else return false
     */
    private boolean checkCorpusToMarketTolerance(FeeMethod feeMethod) {
        boolean isValid = true;

        if (ObjectUtils.isNotNull(feeMethod.getCorpusPctTolerance())) {
            KualiDecimal corputPctToTolerance = feeMethod.getCorpusPctTolerance();
            if (corputPctToTolerance.isLessThan(KualiDecimal.ZERO)) {
                GlobalVariables.getMessageMap().putError(EndowPropertyConstants.CORPUS_TO_PCT_TOLERANCE, EndowKeyConstants.FeeMethodConstants.ERROR_CORPUS_PCT_TO_TOLERANCE_NEGATIVE);
                return false;
            }
            if (!corputPctToTolerance.isZero() && !corputPctToTolerance.isGreaterThan(new KualiDecimal("1.0"))) {
                GlobalVariables.getMessageMap().putError(EndowPropertyConstants.CORPUS_TO_PCT_TOLERANCE, EndowKeyConstants.FeeMethodConstants.ERROR_CORPUS_PCT_TO_TOLERANCE_MUST_BE_GREATER_THAN_ONE);
                return false;
            }
        }

        return isValid;
    }

    /**
     * This method will check the fee rate and fee rate breakpoints
     * 
     * @param feeMethod
     * @return true fee rate and fee rate breakpoints pass the validations else return false
     */
    private boolean checkFeeRateAndBreakpointAmounts(FeeMethod feeMethod) {
        boolean isValid = true;

        // no negative values for first, second, and third rate fields
        if (feeMethod.getFirstFeeRate().compareTo(BigDecimal.ZERO) == -1) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.FIRST_FEE_RATE, EndowKeyConstants.FeeMethodConstants.ERROR_FIRST_FEE_RATE_CAN_NOT_BE_NEGATIVE);
            return false;
        }
        if (feeMethod.getSecondFeeRate().compareTo(BigDecimal.ZERO) == -1) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.SECOND_FEE_RATE, EndowKeyConstants.FeeMethodConstants.ERROR_SECOND_FEE_RATE_CAN_NOT_BE_NEGATIVE);
            return false;
        }

        if (feeMethod.getThirdFeeRate().compareTo(BigDecimal.ZERO) == -1) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.THIRD_FEE_RATE, EndowKeyConstants.FeeMethodConstants.ERROR_THIRD_FEE_RATE_CAN_NOT_BE_NEGATIVE);
            return false;
        }

        // no negative values for first, second fee breakpoint values.
        if (feeMethod.getFirstFeeBreakpoint().isLessThan(KualiDecimal.ZERO)) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.FIRST_FEE_BREAK_POINT, EndowKeyConstants.FeeMethodConstants.ERROR_FIRST_FEE_BREAK_POINT_MUST_BE_GREATER_THAN_OR_ZERO);
            return false;
        }

        if (feeMethod.getSecondFeeBreakpoint().isLessThan(KualiDecimal.ZERO)) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.SECOND_FEE_BREAK_POINT, EndowKeyConstants.FeeMethodConstants.ERROR_SECOND_FEE_BREAK_POINT_MUST_BE_GREATER_THAN_OR_ZERO);
            return false;
        }

        if (feeMethod.getFirstFeeBreakpoint().isLessThan(EndowConstants.FeeMethod.FEE_RATE_DEFAULT_VALUE) && feeMethod.getSecondFeeRate().compareTo(BigDecimal.ZERO) == 0) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.SECOND_FEE_RATE, EndowKeyConstants.FeeMethodConstants.ERROR_SECOND_FEE_RATE_MUST_BE_GREATER_THAN_OR_ZERO);
            return false;
        }

        if (feeMethod.getSecondFeeRate().compareTo(BigDecimal.ZERO) == 1 && feeMethod.getFirstFeeBreakpoint().isGreaterEqual(feeMethod.getSecondFeeBreakpoint())) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.FIRST_FEE_BREAK_POINT, EndowKeyConstants.FeeMethodConstants.ERROR_FIRST_FEE_BREAK_POINT_MUST_BE_LESS_THAN_SECOND_FEE_BREAK_POINT);
            return false;
        }

        if (feeMethod.getThirdFeeRate().compareTo(BigDecimal.ZERO) == 1 && feeMethod.getSecondFeeBreakpoint().isGreaterEqual(EndowConstants.FeeMethod.FEE_RATE_DEFAULT_VALUE)) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.SECOND_FEE_BREAK_POINT, EndowKeyConstants.FeeMethodConstants.ERROR_SECOND_FEE_BREAK_POINT_MUST_BE_LESS_THAN_MAX_FEE_BREAK_POINT);
            return false;
        }

        if (feeMethod.getSecondFeeBreakpoint().isLessThan(EndowConstants.FeeMethod.FEE_RATE_DEFAULT_VALUE) && (feeMethod.getThirdFeeRate().compareTo(BigDecimal.ZERO) == 0)) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.THIRD_FEE_RATE, EndowKeyConstants.FeeMethodConstants.ERROR_THIRD_FEE_RATE_MUST_BE_GREATER_THAN_ZERO);
            return false;
        }

        return isValid;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.lang.String, org.kuali.rice.kns.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject bo) {
        boolean isValid = true;
        isValid &= super.processCustomAddCollectionLineBusinessRules(document, collectionName, bo);
        MessageMap errorMap = GlobalVariables.getMessageMap();
        isValid &= errorMap.hasNoErrors();

        if (!isValid) {
            return isValid;
        }

        FeeMethod feeMethod = (FeeMethod) document.getNewMaintainableObject().getBusinessObject();

        // process add line for feeClassCodes collection
        if (collectionName.equalsIgnoreCase(EndowPropertyConstants.FEE_CLASS_CODES_COLLECTION_NAME)) {
            FeeClassCode feeClassCode = (FeeClassCode) bo;
            bo.refreshReferenceObject(EndowPropertyConstants.FEE_CLASS_CODE_REF);

            if (isEmptyFeeClassCode(bo)) {
                return false;
            }
            if (duplicateFeeClassCodeEntered(feeMethod, bo)) {
                return false;
            }
            if (!validateFeeClassCode(bo)) {
                isValid = false;
            }
        }

        // process add line for feeSecurity collection
        if (collectionName.equalsIgnoreCase(EndowPropertyConstants.FEE_SECURITY_COLLECTION_NAME)) {
            FeeSecurity feeSecurity = (FeeSecurity) bo;
            bo.refreshReferenceObject(EndowPropertyConstants.FEE_SECURITY_REF);

            if (isEmptyFeeSecurityCode(bo)) {
                return false;
            }
            if (duplicateFeeSecurityCodeEntered(feeMethod, bo)) {
                return false;
            }
            if (!validateFeeSecurityCode(bo)) {
                isValid = false;
            }
        }

        // process add line for feeTransaction collection
        if (collectionName.equalsIgnoreCase(EndowPropertyConstants.FEE_TRANSACTION_TYPE_COLLECTION_NAME)) {
            FeeTransaction feeTransaction = (FeeTransaction) bo;
            bo.refreshReferenceObject(EndowPropertyConstants.FEE_TRANSACTION_TYPE_REF);

            if (isEmptyFeeTransactionTypeCode(bo)) {
                return false;
            }
            if (duplicateFeeTransactionTypeCode(feeMethod, bo)) {
                return false;
            }
            if (!validateFeeTransactionTypeCode(bo)) {
                isValid = false;
            }
        }

        // process add line for feeEndowmentTransaction collection
        if (collectionName.equalsIgnoreCase(EndowPropertyConstants.FEE_ENDOWMENT_TRANSACTION_CODE_COLLECTION_NAME)) {
            FeeEndowmentTransactionCode feeEndowmentTransactionCode = (FeeEndowmentTransactionCode) bo;
            bo.refreshReferenceObject(EndowPropertyConstants.FEE_ENDOWMENT_TRANSACTION_CODE_REF);

            if (isEmptyFeeEndowmentTransactionCode(bo)) {
                return false;
            }
            if (duplicateFeeEndowmentTransactionCode(feeMethod, bo)) {
                return false;
            }
            if (!validateFeeEndowmentTransactionCode(bo)) {
                isValid = false;
            }
        }

        // process add line for feePaymentType collection
        if (collectionName.equalsIgnoreCase(EndowPropertyConstants.FEE_PAYMENT_TYPE_COLLECTION_NAME)) {
            if (isEmptyFeePaymentTypeCode(bo)) {
                return false;
            }
            if (duplicateFeePaymentTypeCode(feeMethod, bo)) {
                return false;
            }
        }

        return isValid;
    }

    /**
     * This method checks to make sure that fee class code is not empty
     * 
     * @param feeClassCode The object feeClassCode
     * @return isValid is true if fee class code is empty else return false
     */
    private boolean isEmptyFeeClassCode(PersistableBusinessObject bo) {
        boolean isValid = false;

        FeeClassCode feeClass = (FeeClassCode) bo;

        String feeClassCode = feeClass.getFeeClassCode();
        if (ObjectUtils.isNull(feeClassCode)) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.FEE_CLASS_CODE_ATTRIBUTE, EndowKeyConstants.FeeMethodConstants.ERROR_BLANK_FEE_CLASS_CODE_ENTERED);
            return true;
        }

        return isValid;
    }

    /**
     * This method checks to make sure that fee class code exists in ClassCode
     * 
     * @param feeClassCode The object feeClassCode
     * @return isValid is true if fee class code exists in the database else return false
     */
    private boolean validateFeeClassCode(PersistableBusinessObject bo) {
        boolean isValid = true;

        FeeClassCode feeClass = (FeeClassCode) bo;
        String feeClassCode = feeClass.getFeeClassCode();

        ClassCodeService classCodeService = SpringContext.getBean(ClassCodeService.class);
        ClassCode classCode = classCodeService.getByPrimaryKey(feeClassCode);

        if (ObjectUtils.isNull(classCode)) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.FEE_CLASS_CODE_ATTRIBUTE, EndowKeyConstants.FeeMethodConstants.ERROR_INVALID_FEE_CLASS_CODE_ENTERED);
            return false;
        }

        return isValid;
    }

    /**
     * This method checks to make sure that fee class code is not a duplicate in the collection list Compare the entered fee class
     * code on the line to the fee class codes in the list to make sure it is not a duplicate.
     * 
     * @param feeMethod, bo
     * @return isDuplicate is true if fee class code is in the list already else return false
     */
    private boolean duplicateFeeClassCodeEntered(FeeMethod feeMethod, PersistableBusinessObject bo) {
        boolean isDuplicate = false;

        FeeClassCode feeClass = (FeeClassCode) bo;
        String feeClassCode = feeClass.getFeeClassCode();

        List<FeeClassCode> feeClassCodes = (List<FeeClassCode>) feeMethod.getFeeClassCodes();

        for (FeeClassCode feeClassCodeRecord : feeClassCodes) {
            if (feeClassCodeRecord.getFeeClassCode().equalsIgnoreCase(feeClassCode)) {
                isDuplicate = true;
                GlobalVariables.getMessageMap().putError(EndowPropertyConstants.FEE_CLASS_CODE_ATTRIBUTE, EndowKeyConstants.FeeMethodConstants.ERROR_DUPLICATE_FEE_CLASS_CODE_ENTERED);
            }
        }

        return isDuplicate;
    }

    /**
     * This method checks to make sure that fee security code is not empty
     * 
     * @param bo
     * @return isValid is true if fee security code is empty else return false
     */
    private boolean isEmptyFeeSecurityCode(PersistableBusinessObject bo) {
        boolean isValid = false;

        FeeSecurity feeSecurity = (FeeSecurity) bo;

        String feeSecurityCode = feeSecurity.getSecurityCode();
        if (ObjectUtils.isNull(feeSecurityCode)) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.FEE_SECURITY_CODE_ATTRIBUTE, EndowKeyConstants.FeeMethodConstants.ERROR_BLANK_FEE_SECURITY_CODE_ENTERED);
            return true;
        }

        return isValid;
    }

    /**
     * This method checks to make sure that fee security code exists in Security
     * 
     * @param bo The bo to be mapped into feeSecurity
     * @return isValid is true if fee security code is in the database else return false
     */
    private boolean validateFeeSecurityCode(PersistableBusinessObject bo) {
        boolean isValid = true;

        FeeSecurity feeSecurity = (FeeSecurity) bo;
        String securityCode = feeSecurity.getSecurityCode();

        SecurityService securityService = SpringContext.getBean(SecurityService.class);
        Security security = securityService.getByPrimaryKey(securityCode);

        if (ObjectUtils.isNull(security)) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.FEE_SECURITY_CODE_ATTRIBUTE, EndowKeyConstants.FeeMethodConstants.ERROR_INVALID_FEE_SECURITY_CODE_ENTERED);
            return false;
        }

        return isValid;
    }

    /**
     * This method checks to make sure that fee security code is not a duplicate in the collection list Compare the entered fee
     * security code on the line to the fee security codes in the list to make sure it is not a duplicate.
     * 
     * @param feeMethod, bo
     * @return isDuplicate is true if fee security code in the list else return false
     */
    private boolean duplicateFeeSecurityCodeEntered(FeeMethod feeMethod, PersistableBusinessObject bo) {
        boolean isDuplicate = false;

        FeeSecurity feeSecurityCode = (FeeSecurity) bo;
        String securityCode = feeSecurityCode.getSecurityCode();

        List<FeeSecurity> feeSecurity = (List<FeeSecurity>) feeMethod.getFeeSecurity();

        for (FeeSecurity feeSecurityRecord : feeSecurity) {
            if (feeSecurityRecord.getSecurityCode().equalsIgnoreCase(securityCode)) {
                isDuplicate = true;
                GlobalVariables.getMessageMap().putError(EndowPropertyConstants.FEE_SECURITY_CODE_ATTRIBUTE, EndowKeyConstants.FeeMethodConstants.ERROR_DUPLICATE_FEE_SECURITY_CODE_ENTERED);
            }
        }

        return isDuplicate;
    }

    /**
     * This method checks to make sure that fee transaction type code is not empty
     * 
     * @param bo bo to be mapped into feeTransaction
     * @return isValid is true if fee transaction code is empty else return false
     */
    private boolean isEmptyFeeTransactionTypeCode(PersistableBusinessObject bo) {
        boolean isValid = false;

        FeeTransaction feeTransaction = (FeeTransaction) bo;
        String feeTransactionTypeCode = feeTransaction.getTransactionTypeCode();

        if (ObjectUtils.isNull(feeTransactionTypeCode)) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.FEE_TRANSACTION_TYPE_CODE_ATTRIBUTE, EndowKeyConstants.FeeMethodConstants.ERROR_BLANK_TRANSACTION_TYPE_CODE_ENTERED);
            return true;
        }

        return isValid;
    }

    /**
     * This method checks to make sure that fee transaction type code exists in TransactionType table
     * 
     * @param feeTransaction The object feeTranaction
     * @return isValid is true if fee transaction type code is in the database else return false
     */
    private boolean validateFeeTransactionTypeCode(PersistableBusinessObject bo) {
        boolean isValid = true;

        FeeTransaction feeTransaction = (FeeTransaction) bo;
        String feeTransactionTypeCode = feeTransaction.getTransactionTypeCode();

        TransactionTypeService transactionTypeService = SpringContext.getBean(TransactionTypeService.class);
        TransactionTypeCode transactionTypeCode = transactionTypeService.getByPrimaryKey(feeTransactionTypeCode);

        if (ObjectUtils.isNull(transactionTypeCode)) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.FEE_TRANSACTION_TYPE_CODE_ATTRIBUTE, EndowKeyConstants.FeeMethodConstants.ERROR_INVALID_TRANSACTION_TYPE_CODE_ENTERED);
            return false;
        }

        return isValid;
    }

    /**
     * This method checks to make sure that fee class code is not a duplicate in the collection list Compare the entered fee
     * transaction type code on the line to the fee transaction type codes in the list to make sure it is not a duplicate
     * 
     * @param feeMethod, bo
     * @return isDuplicate is true if fee transaction type code is already in the list else return false
     */
    private boolean duplicateFeeTransactionTypeCode(FeeMethod feeMethod, PersistableBusinessObject bo) {
        boolean isDuplicate = false;

        FeeTransaction feeTransaction = (FeeTransaction) bo;
        String feeTransactionTypeCode = feeTransaction.getTransactionTypeCode();

        List<FeeTransaction> feeTransactions = (List<FeeTransaction>) feeMethod.getFeeTransactions();

        for (FeeTransaction feeTransactionsRecord : feeTransactions) {
            if (feeTransactionsRecord.getTransactionTypeCode().equalsIgnoreCase(feeTransactionTypeCode)) {
                isDuplicate = true;
                GlobalVariables.getMessageMap().putError(EndowPropertyConstants.FEE_TRANSACTION_TYPE_CODE_ATTRIBUTE, EndowKeyConstants.FeeMethodConstants.ERROR_DUPLICATE_TRANSACTION_TYPE_CODE_ENTERED);
            }
        }

        return isDuplicate;
    }

    /**
     * This method checks to make sure that fee endowment transaction code is not empty
     * 
     * @param bo bo will be mapped to feeEndowmentTransactionCode
     * @return isValid is true if fee endowment transaction code is empty else return false
     */
    private boolean isEmptyFeeEndowmentTransactionCode(PersistableBusinessObject bo) {
        boolean isValid = false;

        FeeEndowmentTransactionCode feeEndowmentTransactionCode = (FeeEndowmentTransactionCode) bo;
        String feeEndowmentCode = feeEndowmentTransactionCode.getEndowmentTransactionCode();

        if (ObjectUtils.isNull(feeEndowmentCode)) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.FEE_ENDOWMENT_TRANSACTION_TYPE_CODE_ATTRIBUTE, EndowKeyConstants.FeeMethodConstants.ERROR_BLANK_ENDOWMENT_TRANSACTION_CODE_ENTERED);
            return true;
        }

        return isValid;
    }

    /**
     * This method checks to make sure that fee endowment transaction code exists in Endowment Transaction Code
     * 
     * @param bo bo to be mapped into feeEndowmentTransactionCode
     * @return isValid is true if fee endowment transaction code is in the database else return false
     */
    private boolean validateFeeEndowmentTransactionCode(PersistableBusinessObject bo) {
        boolean isValid = true;

        FeeEndowmentTransactionCode feeEndowmentTransactionCode = (FeeEndowmentTransactionCode) bo;
        String feeEndowmentCode = feeEndowmentTransactionCode.getEndowmentTransactionCode();

        EndowmentTransactionCodeService endowmentTransactionCodeService = SpringContext.getBean(EndowmentTransactionCodeService.class);
        EndowmentTransactionCode endowmentTransactionCode = endowmentTransactionCodeService.getByPrimaryKey(feeEndowmentCode);

        if (ObjectUtils.isNull(endowmentTransactionCode)) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.FEE_ENDOWMENT_TRANSACTION_TYPE_CODE_ATTRIBUTE, EndowKeyConstants.FeeMethodConstants.ERROR_INVALID_ENDOWMENT_TRANSACTION_CODE_ENTERED);
            return false;
        }

        return isValid;
    }

    /**
     * This method checks to make sure that fee endowment transaction code is not a duplicate in the collection list Compare the
     * entered fee endowment transaction code on the line to the fee endowment transaction codes in the list to make sure it is not
     * a duplicate.
     * 
     * @param feeMethod, bo
     * @return isDuplicate is true if fee endowment transaction code in the list else return false
     */
    private boolean duplicateFeeEndowmentTransactionCode(FeeMethod feeMethod, PersistableBusinessObject bo) {
        boolean isDuplicate = false;

        FeeEndowmentTransactionCode feeEndowmentTransactionCode = (FeeEndowmentTransactionCode) bo;
        String feeEndowmentCode = feeEndowmentTransactionCode.getEndowmentTransactionCode();

        List<FeeEndowmentTransactionCode> feeEndowmentTransactionCodes = (List<FeeEndowmentTransactionCode>) feeMethod.getFeeEndowmentTransactionCodes();

        for (FeeEndowmentTransactionCode feeEndowmentTransactionCodesRecord : feeEndowmentTransactionCodes) {
            if (feeEndowmentTransactionCodesRecord.getEndowmentTransactionCode().equalsIgnoreCase(feeEndowmentCode)) {
                isDuplicate = true;
                GlobalVariables.getMessageMap().putError(EndowPropertyConstants.FEE_ENDOWMENT_TRANSACTION_TYPE_CODE_ATTRIBUTE, EndowKeyConstants.FeeMethodConstants.ERROR_DUPLICATE_ENDOWMENT_TRANSACTION_CODE_ENTERED);
            }
        }

        return isDuplicate;
    }

    /**
     * This method checks to make sure that fee payment type code is not empty
     * 
     * @param bo bo to be mapped into feePaymentTypeCode
     * @return isValid is true if fee payment type code is empty else return false
     */
    private boolean isEmptyFeePaymentTypeCode(PersistableBusinessObject bo) {
        boolean isValid = false;

        FeePaymentType feePaymentType = (FeePaymentType) bo;
        String feePaymentTypeCode = feePaymentType.getPaymentTypeCode();

        if (ObjectUtils.isNull(feePaymentTypeCode)) {
            GlobalVariables.getMessageMap().putError(EndowPropertyConstants.FEE_PAYMENT_TYPE_CODE, EndowKeyConstants.FeeMethodConstants.ERROR_BLANK_PAYMENT_TYPE_CODE_ENTERED);
            return true;
        }

        return isValid;
    }

    /**
     * This method checks to make sure that fee payment type code is not a duplicate in the collection list Compare the entered fee
     * payment type code on the line to the fee payment type codes in the list to make sure it is not a duplicate.
     * 
     * @param feeMethod, bo
     * @return isDuplicate is true if fee payment type code in the list else return false
     */
    private boolean duplicateFeePaymentTypeCode(FeeMethod feeMethod, PersistableBusinessObject bo) {
        boolean isDuplicate = false;

        FeePaymentType feePaymentType = (FeePaymentType) bo;
        String feePaymentTypeCode = feePaymentType.getPaymentTypeCode();

        List<FeePaymentType> feePaymentTypes = (List<FeePaymentType>) feeMethod.getFeePaymentTypes();

        for (FeePaymentType feePaymentTypesRecord : feePaymentTypes) {
            if (feePaymentTypesRecord.getPaymentTypeCode().equalsIgnoreCase(feePaymentTypeCode)) {
                isDuplicate = true;
                GlobalVariables.getMessageMap().putError(EndowPropertyConstants.FEE_PAYMENT_TYPE_CODE, EndowKeyConstants.FeeMethodConstants.ERROR_DUPLICATE_PAYMENT_TYPE_CODE_ENTERED);
            }
        }

        return isDuplicate;
    }

    /**
     * Check that the frequency code on the Fee Method did not change if the Fee Method is used on at least one KEMID.
     * 
     * @param document
     * @return true if valid, false otherwise
     */
    private boolean checkFrequencyCodeNotChangedIfFeeMethodUsedOnAnyKemid(Document document) {
        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;
        boolean isValid = true;

        if (KNSConstants.MAINTENANCE_EDIT_ACTION.equals(maintenanceDocument.getNewMaintainableObject().getMaintenanceAction())) {
            FeeMethod newFeeMethod = (FeeMethod) maintenanceDocument.getNewMaintainableObject().getBusinessObject();
            FeeMethod oldFeeMethod = (FeeMethod) maintenanceDocument.getOldMaintainableObject().getBusinessObject();
            String feeMethodCode = newFeeMethod.getCode();

            if (!StringUtils.equalsIgnoreCase(newFeeMethod.getFeeFrequencyCode(), oldFeeMethod.getFeeFrequencyCode())) {
                FeeMethodService feeMethodService = SpringContext.getBean(FeeMethodService.class);
                if (feeMethodService.isFeeMethodUsedOnAnyKemid(feeMethodCode)) {
                    GlobalVariables.getMessageMap().putError(EndowPropertyConstants.FEE_METHOD_FREQUENCY_CODE, EndowKeyConstants.FeeMethodConstants.ERROR_FEE_MTHD_FREQ_CD_CANNOT_BE_CHANGED_IF_FEE_USED_ON_ANY_KEMID);
                    isValid = false;
                }
            }
        }

        return isValid;
    }
}

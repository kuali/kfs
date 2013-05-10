/*
 * Copyright 2009 The Kuali Foundation.
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
import org.kuali.kfs.module.endow.document.service.ClassCodeService;
import org.kuali.kfs.module.endow.document.service.EndowmentTransactionCodeService;
import org.kuali.kfs.module.endow.document.service.FeeMethodService;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.event.ApproveDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

public class FeeMethodRule extends MaintenanceDocumentRuleBase {

    /**
     * @see org.kuali.rice.kns.rules.MaintenanceDocumentRule#processRouteDocument(org.kuali.rice.krad.document.Document)
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
     * @see org.kuali.rice.kns.rules.MaintenanceDocumentRule#processApproveDocument(ApproveDocumentEvent)
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
        rulesPassed &= validFeeTransactionTypeEntered(feeMethod); // rule #20
        rulesPassed &= checkFrequencyCodeNotChangedIfFeeMethodUsedOnAnyKemid(document);
        rulesPassed &= validFeeExpenseETranCodeEntered(feeMethod); //new rule added by Norm. Per KULENDOW-564
        
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
                putFieldError(EndowPropertyConstants.FEE_TYPE_CODE, EndowKeyConstants.FeeMethodConstants.ERROR_INVALID_FEE_TYPE_CODE_FOR_TRANSACTIONS_ENTERED);
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
                putFieldError(EndowPropertyConstants.FEE_TYPE_CODE, EndowKeyConstants.FeeMethodConstants.ERROR_INVALID_FEE_TYPE_CODE_FOR_BALANCE_ENTERED);
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
                putFieldError(EndowPropertyConstants.FEE_BASE_CODE, EndowKeyConstants.FeeMethodConstants.ERROR_INVALID_FEE_BASE_CODE_FOR_PAYMENTS_ENTERED);
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
        boolean recordExists = false;

        List<FeeTransaction> feeTransactions = (List<FeeTransaction>) feeMethod.getFeeTransactions();

        for (FeeTransaction feeTransactionsRecord : feeTransactions) {
            if (feeTransactionsRecord.getInclude()) {
                recordExists = true;
                break;
            }
        }
        
        if (feeMethod.getFeeByTransactionType()) {
            if (!recordExists) {
                putFieldError(EndowPropertyConstants.FEE_BY_TRANSACTION_TYPE_CODE, EndowKeyConstants.FeeMethodConstants.ERROR_NO_RECORDS_WITH_YES_IN_FEE_TRANSACTION_TYPE);
                return false;
            }
        }
        //However, If Fee Transaction Type code is not checked, there can be no records in END_FEE_TRAN_DOC_TYP_T with the field INCL set to Yes 
        else {
            if (recordExists) {
                putFieldError(EndowPropertyConstants.FEE_BY_TRANSACTION_TYPE_CODE, EndowKeyConstants.FeeMethodConstants.ERROR_RECORDS_WITH_YES_IN_FEE_TRANSACTION_TYPE);
                return false;
            }
        }
        
        return true;
    }

    /**
     * This method will check if at least one record exists in Fee Class Codes collection with INCL checked on when Fee Class code
     * is checked on.
     * 
     * @param feeMethod
     * @return true if Fee Class code is checked on and at least one record with INCL flag is checked on else return false
     */
    private boolean recordExistsInFeeClassCode(FeeMethod feeMethod) {
        boolean recordExists = false;
        
        List<FeeClassCode> feeClassCodes = (List<FeeClassCode>) feeMethod.getFeeClassCodes();

        for (FeeClassCode feeClassCodeRecord : feeClassCodes) {
            if (feeClassCodeRecord.getInclude()) {
                recordExists = true;
                break;
            }
        }

        if (feeMethod.getFeeByClassCode()) {
            if (!recordExists) {
                putFieldError(EndowPropertyConstants.FEE_BY_CLASS_CODE, EndowKeyConstants.FeeMethodConstants.ERROR_NO_RECORDS_WITH_YES_IN_FEE_CLASS_CODE);
                return false;
            }
        }
        else {
            if (recordExists) {
                putFieldError(EndowPropertyConstants.FEE_BY_CLASS_CODE, EndowKeyConstants.FeeMethodConstants.ERROR_RECORDS_WITH_YES_IN_FEE_CLASS_CODE);
                return false;
            }
        }
        
        return true;
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
        boolean recordExists = false;

        List<FeeEndowmentTransactionCode> feeEndowmentTransactionCodes = (List<FeeEndowmentTransactionCode>) feeMethod.getFeeEndowmentTransactionCodes();

        for (FeeEndowmentTransactionCode feeEndowmentTransactionCodesRecord : feeEndowmentTransactionCodes) {
            if (feeEndowmentTransactionCodesRecord.getInclude()) {
                recordExists = true;
                break;
            }
        }
        
        if (feeMethod.getFeeByETranCode()) {
            if (!recordExists) {
                putFieldError(EndowPropertyConstants.FEE_BY_ENDOWMENT_TRANSACTION_TYPE_CODE, EndowKeyConstants.FeeMethodConstants.ERROR_NO_RECORDS_WITH_YES_IN_FEE_ENDOWMENT_TRANSACTION_CODE);
                return false;                
            }
        }
        //However, If etran code is not checked, there can be no records in etrancode table with the field INCL set to Yes 
        else {
            if (recordExists) {
                putFieldError(EndowPropertyConstants.FEE_BY_ENDOWMENT_TRANSACTION_TYPE_CODE, EndowKeyConstants.FeeMethodConstants.ERROR_RECORDS_WITH_YES_IN_FEE_ENDOWMENT_TRANSACTION_CODE);
                return false;
            }
        }

        return true;
    }

    /**
     * This method will check if at least one record exists in Fee Security collection with INCL checked on when Fee Class Code is
     * checked on.
     * 
     * @param feeMethod
     * @return true if Fee Security Code is checked on and at least one record with INCL flag is checked on else return false
     */
    private boolean recordExistsInFeeSecurity(FeeMethod feeMethod) {
        boolean recordExists = false;
        List<FeeSecurity> feeSecurity = (List<FeeSecurity>) feeMethod.getFeeSecurity();

        for (FeeSecurity feeSecurityRecord : feeSecurity) {
            if (feeSecurityRecord.getInclude()) {
                recordExists = true;
                break;
            }
        }

        if (feeMethod.getFeeBySecurityCode()) {
            if (!recordExists) {
                putFieldError(EndowPropertyConstants.FEE_BY_SECURITY_CODE, EndowKeyConstants.FeeMethodConstants.ERROR_NO_RECORDS_WITH_YES_IN_FEE_SECURITY_CODE);
                return false;
            }
        }
        else {
            if (recordExists) {
                putFieldError(EndowPropertyConstants.FEE_BY_ENDOWMENT_TRANSACTION_TYPE_CODE, EndowKeyConstants.FeeMethodConstants.ERROR_RECORDS_WITH_YES_IN_FEE_SECURITY_CODE);
                return false;
            }
        }

        return true;
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
                putFieldError(EndowPropertyConstants.FEE_BASE_CODE, EndowKeyConstants.FeeMethodConstants.ERROR_NO_RECORDS_WITH_YES_IN_FEE_PAYMENT_TYPE);
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
            putFieldError(EndowPropertyConstants.FEE_ENDOWMENT_TRANSACTION_TYPE_CODE_ATTRIBUTE, EndowKeyConstants.FeeMethodConstants.ERROR_NO_RECORD_IN_ENDOWMENT_TRANSACTION_CODE);
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
                putFieldError(EndowPropertyConstants.CORPUS_TO_PCT_TOLERANCE, EndowKeyConstants.FeeMethodConstants.ERROR_CORPUS_PCT_TO_TOLERANCE_NEGATIVE);
                return false;
            }
            if (!corputPctToTolerance.isZero() && !corputPctToTolerance.isGreaterThan(new KualiDecimal("1.0"))) {
                putFieldError(EndowPropertyConstants.CORPUS_TO_PCT_TOLERANCE, EndowKeyConstants.FeeMethodConstants.ERROR_CORPUS_PCT_TO_TOLERANCE_MUST_BE_GREATER_THAN_ONE);
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
            putFieldError(EndowPropertyConstants.FIRST_FEE_RATE, EndowKeyConstants.FeeMethodConstants.ERROR_FIRST_FEE_RATE_CAN_NOT_BE_NEGATIVE);
            return false;
        }
        if (feeMethod.getSecondFeeRate().compareTo(BigDecimal.ZERO) == -1) {
            putFieldError(EndowPropertyConstants.SECOND_FEE_RATE, EndowKeyConstants.FeeMethodConstants.ERROR_SECOND_FEE_RATE_CAN_NOT_BE_NEGATIVE);
            return false;
        }

        if (feeMethod.getThirdFeeRate().compareTo(BigDecimal.ZERO) == -1) {
            putFieldError(EndowPropertyConstants.THIRD_FEE_RATE, EndowKeyConstants.FeeMethodConstants.ERROR_THIRD_FEE_RATE_CAN_NOT_BE_NEGATIVE);
            return false;
        }

        // no negative values for first, second fee breakpoint values.
        if (feeMethod.getFirstFeeBreakpoint().isLessThan(KualiDecimal.ZERO)) {
            putFieldError(EndowPropertyConstants.FIRST_FEE_BREAK_POINT, EndowKeyConstants.FeeMethodConstants.ERROR_FIRST_FEE_BREAK_POINT_MUST_BE_GREATER_THAN_OR_ZERO);
            return false;
        }

        if (feeMethod.getSecondFeeBreakpoint().isLessThan(KualiDecimal.ZERO)) {
            putFieldError(EndowPropertyConstants.SECOND_FEE_BREAK_POINT, EndowKeyConstants.FeeMethodConstants.ERROR_SECOND_FEE_BREAK_POINT_MUST_BE_GREATER_THAN_OR_ZERO);
            return false;
        }

        if (feeMethod.getFirstFeeBreakpoint().isLessThan(EndowConstants.FeeMethod.FEE_RATE_DEFAULT_VALUE) && feeMethod.getSecondFeeRate().compareTo(BigDecimal.ZERO) == 0) {
            putFieldError(EndowPropertyConstants.SECOND_FEE_RATE, EndowKeyConstants.FeeMethodConstants.ERROR_SECOND_FEE_RATE_MUST_BE_GREATER_THAN_OR_ZERO);
            return false;
        }

        if (feeMethod.getSecondFeeRate().compareTo(BigDecimal.ZERO) == 1 && feeMethod.getFirstFeeBreakpoint().isGreaterEqual(feeMethod.getSecondFeeBreakpoint())) {
            putFieldError(EndowPropertyConstants.FIRST_FEE_BREAK_POINT, EndowKeyConstants.FeeMethodConstants.ERROR_FIRST_FEE_BREAK_POINT_MUST_BE_LESS_THAN_SECOND_FEE_BREAK_POINT);
            return false;
        }

        if (feeMethod.getThirdFeeRate().compareTo(BigDecimal.ZERO) == 1 && feeMethod.getSecondFeeBreakpoint().isGreaterEqual(EndowConstants.FeeMethod.FEE_RATE_DEFAULT_VALUE)) {
            putFieldError(EndowPropertyConstants.SECOND_FEE_BREAK_POINT, EndowKeyConstants.FeeMethodConstants.ERROR_SECOND_FEE_BREAK_POINT_MUST_BE_LESS_THAN_MAX_FEE_BREAK_POINT);
            return false;
        }

        if (feeMethod.getSecondFeeBreakpoint().isLessThan(EndowConstants.FeeMethod.FEE_RATE_DEFAULT_VALUE) && (feeMethod.getThirdFeeRate().compareTo(BigDecimal.ZERO) == 0)) {
            putFieldError(EndowPropertyConstants.THIRD_FEE_RATE, EndowKeyConstants.FeeMethodConstants.ERROR_THIRD_FEE_RATE_MUST_BE_GREATER_THAN_ZERO);
            return false;
        }

        return isValid;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.lang.String, org.kuali.rice.krad.bo.PersistableBusinessObject)
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
            // bo.refreshReferenceObject(EndowPropertyConstants.FEE_TRANSACTION_ARCHIVE_REF);

            if (isEmptyFeeTransactionDocumentTypeName(bo)) {
                return false;
            }
            if (duplicateFeeTransactionDocumentTypeName(feeMethod, bo)) {
                return false;
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
            putFieldError(EndowPropertyConstants.FEE_CLASS_CODE_ATTRIBUTE, EndowKeyConstants.FeeMethodConstants.ERROR_BLANK_FEE_CLASS_CODE_ENTERED);
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
            putFieldError(EndowPropertyConstants.FEE_CLASS_CODE_ATTRIBUTE, EndowKeyConstants.FeeMethodConstants.ERROR_INVALID_FEE_CLASS_CODE_ENTERED);
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
                putFieldError(EndowPropertyConstants.FEE_CLASS_CODE_ATTRIBUTE, EndowKeyConstants.FeeMethodConstants.ERROR_DUPLICATE_FEE_CLASS_CODE_ENTERED);
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
            putFieldError(EndowPropertyConstants.FEE_SECURITY_CODE_ATTRIBUTE, EndowKeyConstants.FeeMethodConstants.ERROR_BLANK_FEE_SECURITY_CODE_ENTERED);
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
            putFieldError(EndowPropertyConstants.FEE_SECURITY_CODE_ATTRIBUTE, EndowKeyConstants.FeeMethodConstants.ERROR_INVALID_FEE_SECURITY_CODE_ENTERED);
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
                putFieldError(EndowPropertyConstants.FEE_SECURITY_CODE_ATTRIBUTE, EndowKeyConstants.FeeMethodConstants.ERROR_DUPLICATE_FEE_SECURITY_CODE_ENTERED);
            }
        }

        return isDuplicate;
    }

    /**
     * This method checks to make sure that fee transaction type code is not empty
     * 
     * @param bo bo to be mapped into feeTransaction
     * @return isValid is true if fee transaction document name is empty else return false
     */
    private boolean isEmptyFeeTransactionDocumentTypeName(PersistableBusinessObject bo) {
        boolean isValid = false;

        FeeTransaction feeTransaction = (FeeTransaction) bo;
        String feeTransactionTypeName = feeTransaction.getDocumentTypeName();

        if (ObjectUtils.isNull(feeTransactionTypeName)) {
            putFieldError(EndowPropertyConstants.FEE_TRANSACTION_DOCUMENT_TYPE_NAME_ATTRIBUTE, EndowKeyConstants.FeeMethodConstants.ERROR_BLANK_DOCUMENT_TYPE_NAME_ENTERED);
            return true;
        }

        return isValid;
    }

    /**
     * This method checks to make sure that fee class code is not a duplicate in the collection list Compare the entered fee
     * transaction type code on the line to the fee transaction type codes in the list to make sure it is not a duplicate
     * 
     * @param feeMethod, bo
     * @return isDuplicate is true if fee transaction document type name is already in the list else return false
     */
    private boolean duplicateFeeTransactionDocumentTypeName(FeeMethod feeMethod, PersistableBusinessObject bo) {
        boolean isDuplicate = false;

        FeeTransaction feeTransaction = (FeeTransaction) bo;
        String feeTransactionDocumentTypeName = feeTransaction.getDocumentTypeName();

        List<FeeTransaction> feeTransactions = (List<FeeTransaction>) feeMethod.getFeeTransactions();

        for (FeeTransaction feeTransactionsRecord : feeTransactions) {
            if (feeTransactionsRecord.getDocumentTypeName().equalsIgnoreCase(feeTransactionDocumentTypeName)) {
                isDuplicate = true;
                putFieldError(EndowPropertyConstants.FEE_TRANSACTION_DOCUMENT_TYPE_NAME_ATTRIBUTE, EndowKeyConstants.FeeMethodConstants.ERROR_DUPLICATE_TRANSACTION_TYPE_NAME_ENTERED);
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
            putFieldError(EndowPropertyConstants.FEE_ENDOWMENT_TRANSACTION_TYPE_CODE_ATTRIBUTE, EndowKeyConstants.FeeMethodConstants.ERROR_BLANK_ENDOWMENT_TRANSACTION_CODE_ENTERED);
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
            putFieldError(EndowPropertyConstants.FEE_ENDOWMENT_TRANSACTION_TYPE_CODE_ATTRIBUTE, EndowKeyConstants.FeeMethodConstants.ERROR_INVALID_ENDOWMENT_TRANSACTION_CODE_ENTERED);
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
                putFieldError(EndowPropertyConstants.FEE_ENDOWMENT_TRANSACTION_TYPE_CODE_ATTRIBUTE, EndowKeyConstants.FeeMethodConstants.ERROR_DUPLICATE_ENDOWMENT_TRANSACTION_CODE_ENTERED);
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
            putFieldError(EndowPropertyConstants.FEE_PAYMENT_TYPE_CODE, EndowKeyConstants.FeeMethodConstants.ERROR_BLANK_PAYMENT_TYPE_CODE_ENTERED);
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
                putFieldError(EndowPropertyConstants.FEE_PAYMENT_TYPE_CODE, EndowKeyConstants.FeeMethodConstants.ERROR_DUPLICATE_PAYMENT_TYPE_CODE_ENTERED);
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

        if (KRADConstants.MAINTENANCE_EDIT_ACTION.equals(maintenanceDocument.getNewMaintainableObject().getMaintenanceAction())) {
            FeeMethod newFeeMethod = (FeeMethod) maintenanceDocument.getNewMaintainableObject().getBusinessObject();
            FeeMethod oldFeeMethod = (FeeMethod) maintenanceDocument.getOldMaintainableObject().getBusinessObject();
            String feeMethodCode = newFeeMethod.getCode();

            if (!StringUtils.equalsIgnoreCase(newFeeMethod.getFeeFrequencyCode(), oldFeeMethod.getFeeFrequencyCode())) {
                FeeMethodService feeMethodService = SpringContext.getBean(FeeMethodService.class);
                if (feeMethodService.isFeeMethodUsedOnAnyKemid(feeMethodCode)) {
                    putFieldError(EndowPropertyConstants.FEE_METHOD_FREQUENCY_CODE, EndowKeyConstants.FeeMethodConstants.ERROR_FEE_MTHD_FREQ_CD_CANNOT_BE_CHANGED_IF_FEE_USED_ON_ANY_KEMID);
                    isValid = false;
                }
            }
        }

        return isValid;
    }

    /**
     * This method will check if Fee Transaction Type collection does not have type as EHVA
     * 
     * @param feeMethod
     * @return true if Fee Transaction Type code is not EHVA else return false
     */
    private boolean validFeeTransactionTypeEntered(FeeMethod feeMethod) {
        boolean valid = true;

        if (feeMethod.getFeeByTransactionType()) {
            valid = true;
            List<FeeTransaction> feeTransactions = (List<FeeTransaction>) feeMethod.getFeeTransactions();

            for (FeeTransaction feeTransactionsRecord : feeTransactions) {
                if (feeTransactionsRecord.getDocumentTypeName().equals(EndowConstants.FeeMethod.ENDOWMENT_HISTORY_VALUE_ADJUSTMENT)) {
                    valid = false;
                    break;
                }
            }
            if (!valid) {
                putFieldError(EndowPropertyConstants.FEE_TRANSACTION_DOCUMENT_TYPE_NAME_ATTRIBUTE, EndowKeyConstants.FeeMethodConstants.ERROR_INVALID_TRANSACTION_DOCUMENT_TYPE_CODE_ENTERED);
            }
        }

        return valid;
    }

    /**
     * Check to see if fee method etran code has a valiD expense etran code type = E
     */
    private boolean validFeeExpenseETranCodeEntered(FeeMethod feeMethod) {
        boolean valid = true;
        
        if (!EndowConstants.EndowmentTransactionTypeCodes.EXPENSE_TYPE_CODE.equalsIgnoreCase(feeMethod.getEndowmentTransactionCode().getEndowmentTransactionTypeCode())) {
            valid = false;
            putFieldError(EndowPropertyConstants.FEE_EXPENSE_ENDOWMENT_TRANSACTION_CODE, EndowKeyConstants.FeeMethodConstants.ERROR_INVALID_TRANSACTION_TYPE_CODE_FOR_EXPENSE_ETRANCODE);
        }
        
        return valid;
    }
}

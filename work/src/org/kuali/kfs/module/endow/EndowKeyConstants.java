/*
k * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow;


public class EndowKeyConstants {

    public static class SecurityReportingGroupConstants {
        public static final String ERROR_SECURITY_REPORTING_GROUP_ORDER_DUPLICATE_VALUE = "error.document.securityReportingGroupOrder.duplicateValue";
    }

    public static class ClassCodeConstants {
        public static final String ERROR_ENDOWMENT_TRANSACTION_TYPE_ASSET_OR_LIABILITY = "error.document.classCode.endowmentTransactionType.assesOrLiability";
        public static final String ERROR_INCOME_ENDOWMENT_TRANSACTION_POST_TYPE_INCOME = "error.document.classCode.incomeEndowmentTransactionType.income";
        public static final String ERROR_CLASS_CODE_TYPE_CASH_EQ_REP_GRPCASH_EQ = "error.document.classCode.classCodeTypeCashEqMustHaveRepGrpCashEq";
        public static final String ERROR_CLASS_CODE_TYPE_POOLED_INVESTMENT_MUST_HAVE_VLTN_MTHD_UNITS = "error.document.classCode.classCodeTypePooledInvestmentsMustHaveVltnMthdUnits";
        public static final String ERROR_CLASS_CODE_TYPE_LIABILITY_MUST_HAVE_SEC_ETRAN_TYPE_LIABILITY = "error.document.classCode.classCodeTypeLiabilityMustHaveSecurityEtranTypeLiability";
        public static final String ERROR_CLASS_CODE_TYPE_CASHEQ_OR_LIABILITY_MUST_HAVE_TAX_LOT_IND_NO = "error.document.classCode.classCodeTypeCashEqOrLiabilityMustHaveTaxLotIndicatorNo";
    }

    public static class SecurityConstants {
        public static final String ERROR_SECURITY_UNIT_VALUE_LESS_THAN_OR_EQ_ZERO_FOR_LIABILITIES = "error.document.security.unitValueForLiabilities";
        public static final String ERROR_SECURITY_UNIT_VALUE_LESS_THAN_OR_EQ_ZERO_FOR_NON_LIABILITIES = "error.document.security.unitValueForNonLiabilities";
        public static final String MESSAGE_SECURITY_IS_NINTH_DIGIT_CORRECT = "error.document.security.isNinthDigitCorrect";
        public static final String GENERATE_SECURITY_ID_QUESTION_ID = "GenerateSecurityQuestionID";
        public static final String ERROR_ENTERED_SECURITY_ID_SHOULD_BE_8_CHARS = "error.document.security.id";
        public static final String EROR_NEW_SECURITY_CLASS_CODE_TYPE_MUST_EQUAL_OLD_SEC_CLASS_CODE_TYPE = "error.document.security.classCode.newClassCodeTypeMustEqOldType";
        public static final String ERROR_SECURITY_VAL_BY_MKT_MUST_BE_EMPTY_WHEN_VAL_MTHD_UNITS = "error.document.security.valueByMarket.mustBeEmptyWhenValMthdUnits";
        public static final String ERROR_SECURITY_UNIT_VAL_MUST_BE_EMPTY_WHEN_VAL_MTHD_MARKET = "error.document.security.unitValue.mustBeEmptyWhenValMthdMarket";
        public static final String ERROR_SECURITY_INCOME_PAY_FREQUENCY_CODE_NOT_ENTERED = "error.document.security.mustEnterFrequnecyCode.whenPooledFundClasscodeUsed";
    }

    public static class EndowmentTransactionConstants {
        public static final String ERROR_GL_LINK_WITH_SAME_CHART_ALREADY_EXISTS = "error.document.endowmentTransaction.glLink.alreadyExists";
        public static final String ERROR_ENDOWMENT_TRANSACTION_MUST_HAVE_AT_LEAST_ONE_GLLINK = "error.document.endowmentTransaction.atLeastOneGLLink";
        public static final String ERROR_GL_LINK_CHART_CD_MUST_EQUAL_OBJECT_CHART_CD = "error.document.endowmentTransaction.glLink.chart.mustEqualObjectCodeChart";
        public static final String ERROR_GL_LINK_OBJ_CD_ACC_CATEGORY_MUST_EQUAL_ETRAN_TYPE = "error.document.endowmentTransaction.glLink.objectCode.accCategory.mustEqualEtranType";
    }

    public static class PooledFundValueConstants {
        public static final String ERROR_VALUATION_DATE_IS_NOT_THE_END_OF_MONTH = "error.document.pooledFundValue.valuationDate.notTheEndOfMonth";
        public static final String ERROR_VALUATION_DATE_IS_NOT_LATEST_ONE = "error.document.pooledFundValue.valuationDate.notTheLatestOne";
        public static final String ERROR_UNIT_VALUE_IS_NOT_POSITIVE = "error.document.pooledFundValue.unitValue.notPositive";
        public static final String ERROR_INCOME_DISTRIBUTION_PER_UNIT_IS_NOT_POSITIVE = "error.document.pooledFundValue.incomeDistributionPerUnit.notPositive";
        public static final String ERROR_DISTRIBUTE_INCOME_ON_DATE_IS_REQUIRED_FIELD = "error.document.pooledFundValue.distributeIncomeOnDate.isRequiredField";
        public static final String ERROR_DISTRIBUTE_LONG_TERM_GAIN_LOSS_ON_DATE_IS_REQUIRED_FIELD = "error.document.pooledFundValue.distributeLongTermGainLossOnDate.isRequiredField";
        public static final String ERROR_DISTRIBUTE_SHORT_TERM_GAIN_LOSS_ON_DATE_IS_REQUIRED_FIELD = "error.document.pooledFundValue.distributeShortTermGainLossOnDate.isRequiredField";
    }

    public static class ACIModelConstants {
        public static final String ERROR_TOTAL_OF_ALL_PERCENTAGES_NOT_EQUAL_ONE = "error.document.aciModel.totalPercentages.notEqualOne";
    }

    public static class PurposeConstants {
        public static final String ERROR_INCOME_CAE_CODE_TYPE_CANNOT_BE_PRINCIPAL = "error.document.purposeCode.incomeCAECode.typeCannotBePrincipal";
        public static final String ERROR_PRINCIPAL_CAE_CODE_TYPE_CANNOT_BE_INCOME = "error.document.purposeCode.incomeCAECode.typeCannotBeIncome";
    }

    public static class TypeRestrictionCodeConstants {
        public static final String ERROR_PERMANENT_INDICATOR_CANNOT_BE_NO_WHEN_TYPE_RESTRICTION_CODE_IS_P = "error.document.code.indicatorCannotBeNoWhenTypeRestrictedCodeIsP";
        public static final String ERROR_PERMANENT_INDICATOR_CANNOT_BE_YES_WHEN_TYPE_RESTRICTION_CODE_IS_U = "error.document.code.indicatorCannotBeYesWhenTypeRestrictedCodeIsU";
        public static final String ERROR_PERMANENT_INDICATOR_CANNOT_BE_YES_WHEN_TYPE_RESTRICTION_CODE_IS_NA = "error.document.code.indicatorCannotBeYesWhenTypeRestrictedCodeIsNA";
        public static final String ERROR_PERMANENT_INDICATOR_CANNOT_BE_YES_WHEN_TYPE_RESTRICTION_CODE_IS_T = "error.document.code.indicatorCannotBeYesWhenTypeRestrictedCodeIsT";
        public static final String ERROR_PERMANENT_INDICATOR_CANNOT_BE_USED_FOR_TYPE_RESTRICTION_CODE = "error.document.code.indicatorCannotBePForTypeIncRestrcode";
    }

    public static class TypeFeeMethodCodeConstants {
        public static final String ERROR_FEE_METHOD_CODE_CANNOT_BE_BLANK = "error.document.typeFeeMethod.feeMethodCode.cannotBeBlank";
        public static final String ERROR_DUPLICATE_FEE_METHOD_CODE_ENTERED = "error.document.typeFeeMethod.feeMethodCode.duplicate";
        public static final String ERROR_INVALID_FEE_METHOD_CODE_ENTERED = "error.document.typeFeeMethod.feeMethodCode.inValid";
    }

    public static class TypeCodeConstants {
        public static final String ERROR_INVALID_PRINCIPAL_ACI_MODEL_ID = "error.document.typeCode.principalACIModelId.indicatorShouldBePrincipal";
    }

    public static class FeeMethodConstants {
        public static final String ERROR_INVALID_FEE_CLASS_CODE_ENTERED = "error.document.feeClassCode.feeClassCode.inValid";
        public static final String ERROR_BLANK_FEE_CLASS_CODE_ENTERED = "error.document.feeClassCode.feeClassCode.cannotBeBlank";
        public static final String ERROR_DUPLICATE_FEE_CLASS_CODE_ENTERED = "error.document.feeClassCode.feeClassCode.duplicateEntered";
        public static final String ERROR_BLANK_FEE_SECURITY_CODE_ENTERED = "error.document.feeSecurity.securityCode.cannotBeBlank";
        public static final String ERROR_INVALID_FEE_SECURITY_CODE_ENTERED = "error.document.feeSecurity.securityCode.inValid";
        public static final String ERROR_DUPLICATE_FEE_SECURITY_CODE_ENTERED = "error.document.feeSecurity.securityCode.duplicateEntered";
        public static final String ERROR_BLANK_TRANSACTION_TYPE_CODE_ENTERED = "error.document.feeTransactions.transactionTypeCode.cannotBeBlank";
        public static final String ERROR_BLANK_DOCUMENT_TYPE_NAME_ENTERED = "error.document.feeTransactions.documentTypeName.cannotBeBlank";
        public static final String ERROR_INVALID_TRANSACTION_TYPE_CODE_ENTERED = "error.document.feeTransactions.transactionTypeCode.inValid";
        public static final String ERROR_DUPLICATE_TRANSACTION_TYPE_CODE_ENTERED = "error.document.feeTransactions.transactionTypeCode.duplicateEntered";
        public static final String ERROR_DUPLICATE_TRANSACTION_TYPE_NAME_ENTERED = "error.document.feeTransactions.documentTypeName.duplicateEntered";
        public static final String ERROR_BLANK_ENDOWMENT_TRANSACTION_CODE_ENTERED = "error.document.feeEndowmentTransactionCodes.transactionTypeCode.cannotBeBlank";
        public static final String ERROR_BLANK_PAYMENT_TYPE_CODE_ENTERED = "error.document.feePaymentType.paymentTypeCode.cannotBeBlank";
        public static final String ERROR_DUPLICATE_PAYMENT_TYPE_CODE_ENTERED = "error.document.feePaymentType.paymentTypeCode.duplicateEntered";
        public static final String ERROR_INVALID_ENDOWMENT_TRANSACTION_CODE_ENTERED = "error.document.feeEndowmentTransactionCodes.transactionTypeCode.inValid";
        public static final String ERROR_DUPLICATE_ENDOWMENT_TRANSACTION_CODE_ENTERED = "error.document.feeEndowmentTransactionCodes.transactionTypeCode.duplicateEntered";
        public static final String ERROR_INVALID_FEE_TYPE_CODE_FOR_TRANSACTIONS_ENTERED = "error.document.feeMethod.feeTypeCode.invalidValueForTransactions";
        public static final String ERROR_NO_RECORDS_WITH_YES_IN_FEE_TRANSACTION_TYPE = "error.document.feeMethod.feeByTransactionType.noRecord";
        public static final String ERROR_RECORDS_WITH_YES_IN_FEE_TRANSACTION_TYPE = "error.document.feeMethod.feeByTransactionType.yesRecord";
        public static final String ERROR_INVALID_TRANSACTION_DOCUMENT_TYPE_CODE_ENTERED = "error.document.feeMethod.feeByTransactionType.invalid";
        public static final String ERROR_INVALID_TRANSACTION_TYPE_CODE_FOR_EXPENSE_ETRANCODE = "error.document.feeMethod.feeByTransactionType.expenseTypeCode.shouldBeE";
        public static final String ERROR_NO_RECORD_IN_ENDOWMENT_TRANSACTION_CODE = "error.document.feeMethod.feeByETranCode.invalidETranCode";
        public static final String ERROR_NO_RECORDS_WITH_YES_IN_FEE_ENDOWMENT_TRANSACTION_CODE = "error.document.feeMethod.feeByETranCode.noRecord";
        public static final String ERROR_RECORDS_WITH_YES_IN_FEE_ENDOWMENT_TRANSACTION_CODE = "error.document.feeMethod.feeByETranCode.yesRecord";
        public static final String ERROR_INVALID_FEE_TYPE_CODE_FOR_BALANCE_ENTERED = "error.document.feeMethod.feeTypeCode.invalidValueForBalance";
        public static final String ERROR_NO_RECORDS_WITH_YES_IN_FEE_CLASS_CODE = "error.document.feeMethod.feeByClassCode.noRecord";
        public static final String ERROR_RECORDS_WITH_YES_IN_FEE_CLASS_CODE = "error.document.feeMethod.feeByClassCode.yesRecord";
        public static final String ERROR_NO_RECORDS_WITH_YES_IN_FEE_SECURITY_CODE = "error.document.feeMethod.feeBySecurityCode.noRecord";
        public static final String ERROR_RECORDS_WITH_YES_IN_FEE_SECURITY_CODE = "error.document.feeMethod.feeBySecurityCode.yesRecord";
        public static final String ERROR_INVALID_FEE_BASE_CODE_FOR_PAYMENTS_ENTERED = "error.document.feeMethod.feeBaseCode.invalidValueForPayments";
        public static final String ERROR_NO_RECORDS_WITH_YES_IN_FEE_PAYMENT_TYPE = "error.document.feeMethod.feePaymentTypes.noRecord";
        public static final String ERROR_INVALID_FEE_BALANCE_TYPE_CODE_WHEN_COUNT_ENTERED = "error.document.feeMethod.feeBalanceTypeCode.inValidFeeBalanceTypeWhenCount";
        public static final String ERROR_INVALID_FEE_BALANCE_TYPE_CODE_WHEN_VALUE_ENTERED = "error.document.feeMethod.feeBalanceTypeCode.inValidFeeBalanceTypeWhenValue";
        public static final String ERROR_CORPUS_PCT_TO_TOLERANCE_NEGATIVE = "error.document.feeMethod.corpusPctTolerance.isNegative";
        public static final String ERROR_CORPUS_PCT_TO_TOLERANCE_MUST_BE_GREATER_THAN_ONE = "error.document.feeMethod.corpusPctTolerance.shouldBeGreaterThanOne";
        public static final String ERROR_FIRST_FEE_BREAK_POINT_MUST_BE_LESS_THAN_SECOND_FEE_BREAK_POINT = "error.document.feeMethod.firstFeeBreakpoint.shouldBeLessThan";
        public static final String ERROR_SECOND_FEE_BREAK_POINT_MUST_BE_LESS_THAN_MAX_FEE_BREAK_POINT = "error.document.feeMethod.firstFeeBreakpoint.shouldBeLessThanMaxlimit";
        public static final String ERROR_THIRD_FEE_RATE_MUST_BE_GREATER_THAN_ZERO = "error.document.feeMethod.thirdFeeRate.shouldBeGreaterThanZero";
        public static final String ERROR_FIRST_FEE_RATE_CAN_NOT_BE_NEGATIVE = "error.document.feeMethod.firstFeeRate.canNotBeNegative";
        public static final String ERROR_FIRST_FEE_BREAK_POINT_MUST_BE_GREATER_THAN_OR_ZERO = "error.document.feeMethod.firstFeeBreakpoint.shouldBeZeroOrGreater";
        public static final String ERROR_SECOND_FEE_RATE_MUST_BE_GREATER_THAN_OR_ZERO = "error.document.feeMethod.secondFeeRate.shouldBeGreaterThanZero";
        public static final String ERROR_SECOND_FEE_RATE_CAN_NOT_BE_NEGATIVE = "error.document.feeMethod.secondFeeRate.canNotBeNegative";
        public static final String ERROR_THIRD_FEE_RATE_CAN_NOT_BE_NEGATIVE = "error.document.feeMethod.thirdFeeRate.canNotBeNegative";
        public static final String ERROR_SECOND_FEE_BREAK_POINT_MUST_BE_GREATER_THAN_OR_ZERO = "error.document.feeMethod.secondFeeBreakpoint.shouldBeZeroOrGreater";
        public static final String ERROR_FEE_MTHD_FREQ_CD_CANNOT_BE_CHANGED_IF_FEE_USED_ON_ANY_KEMID = "error.document.feeMethod.frequencyCode.canNotChangeIfFeeUsedOnAnyKemid";
    }

    public static class KEMIDConstants {
        public static final String ERROR_INVALID_CLOSED_CODE = "error.document.kemid.reasonClosed.invalid";
        public static final String ERROR_HAS_OPEN_RECORDS_IN_CURRENT_CASH = "error.document.kemid.closedIndicator.cannotCloseKemid.hasOpenRecordsInCurrentCash";
        public static final String ERROR_HAS_OPEN_RECORDS_IN_HOLDING_TAX_LOT = "error.document.kemid.closedIndicator.cannotCloseKemid.hasOpenRecordsInHoldingTaxLot";
        public static final String ERROR_KEMID_MUST_HAVE_AT_LEAST_ONE_ACTIVE_AGREEMENT = "error.document.kemid.agreements.mustHaveAtLeastOneActiveAgreement";
        public static final String ERROR_KEMID_ONLY_ONE_AGREEMENT_CAN_BR_MARKED_FOR_TRANSACTION_RESTR_USE = "error.document.kemid.agreements.onlyOneAgreementMarkedForTransactionRestrictionUse";
        public static final String ERROR_KEMID_MUST_HAVE_AT_LEAST_ONE_ACTIVE_SOURCE_OF_FUNDS = "error.document.kemid.sourceOfFunds.mustHaveAtLeastOneActiveSourceOfFunds";
        public static final String ERROR_KEMID_MUST_HAVE_AT_LEAST_ONE_ACTIVE_BENEFITTING_ORG = "error.document.kemid.benefittingOrganizations.mustHaveAtLeastOneActiveBenefittingOrg";
        public static final String ERROR_KEMID_ACTIVE_BENE_ORGS_PCT_SUM_MUST_BE_ONE = "error.document.kemid.benefittingOrganizations.activeBeneOrgsPctSumMustBeOne";
        public static final String ERROR_KEMID_MUST_HAVE_AT_LEAST_ONE_INCOME_GL_ACC = "error.document.kemid.glAccounts.mustHaveAtLeastOneIncomeGLAcc";
        public static final String ERROR_KEMID_CAN_ONLY_HAVE_ONE_INCOME_GL_ACC = "error.document.kemid.glAccounts.canOnlyHaveOneIncomeGLAcc";
        public static final String ERROR_KEMID_MUST_HAVE_AT_LEAST_ONE_ACTIVE_PRINCIPAL_GL_ACC_IF_PRINCIPAL_CD_NOT_NA = "error.document.kemid.glAccounts.mustHaveAtLeastOneActivePrincipalGLAccIfPrincipalRestrCdNotNA";
        public static final String ERROR_KEMID_CAN_ONLY_HAVE_ONE_PRINCIPAL_GL_ACC = "error.document.kemid.glAccounts.canOnlyHaveOnePrincipalGLAccIfPrincipalRestrCdNotNA";
        public static final String ERROR_KEMID_CAN_NOT_HAVE_A_PRINCIPAL_GL_ACC_IF_PRINCIPAL_RESTR_CD_IS_NA = "error.document.kemid.glAccounts.canNotHaveAPrincipalGLAccIfPrincipalRestrCdIsNA";
        public static final String ERROR_KEMID_MUST_HAVE_AT_LEAST_ONE_PAYOUT_INSTRUCTION = "error.document.kemid.payoutInstructions.mustHaveAtLeastOnePayoutInstruction";
        public static final String ERROR_KEMID_TOTAL_OFF_ALL_PAYOUT_RECORDS_MUST_BE_ONE = "error.document.kemid.payoutInstructions.theTotalOfAllNonterminatedInstructionsMustBeOne";
        public static final String ERROR_KEMID_PAYOUT_INSTRUCTION_START_DATE_SHOULD_BE_PRIOR_TO_END_DATE = "error.document.kemid.payoutInstructions.startDateShouldBePriorToEndDate";
        public static final String ERROR_KEMID_DONOR_STMNT_COMBINE_WITH_DONR_MUST_BE_DIFF_FROM_DONOR = "error.document.kemid.donorStatements.donorStatementCombineWithDonorMustBeDiffFromDonr";
        public static final String ERROR_KEMID_DONOR_STMNT_TERM_RSN_CANT_BE_EMPTY_IS_TERM_DATE_ENTERED = "error.document.kemid.donorStatements.terminationReasonCantBeEmptyIfTermDateEntered";
        public static final String ERROR_KEMID_FEE_PCT_CHRG_FEE_SUM_MUST_NOT_BE_GREATER_THAN_ONE = "error.document.kemid.fees.percentageChargedToFeeSumMustNotExceedOne";
        public static final String ERROR_KEMID_FEE_PCT_CHRG_TO_PRIN_CANNOT_EXCEED_ZERO_IF_TYPE_RESTR_CD_NA = "error.document.kemid.fees.percentageOfFeeChargedToPrincCannotExceedZeroIfTypeRestrCdNA";
        public static final String ERROR_KEMID_FEE_START_DATE_NOT_VALID = "error.document.kemid.fees.feeStartDateNotValid";
        public static final String ERROR_ENTERED_KEMID_INVALID = "error.document.kemid.invalid";
        public static final String ERROR_KEMID_MUST_HAVE_AT_LEAST_ONE_ACTIVE_AUTHORIZATION = "error.document.kemid.authorizations.mustHaveAtLeastOneActiveAuthorization";
        public static final String ERROR_KEMID_AUTHORIZATION_ROLE_NAMESPACE_ENDOW = "error.document.kemid.authorizations.authorizationRoleMustHaveNamespaceEndow";
    }

    public static class TicklerConstants {
        public static final String ERROR_TICKLER_FREQUENCYORNEXTDUEDATEREQUIREMENT = "error.document.tickler.frequencyOrNextDueDateRequirement";
        public static final String ERROR_TICKLER_FREQUENCY_NEXTDUEDATE_MISMATCH = "error.document.tickler.frequencyNextDueDateMismatch";
        public static final String ERROR_TICKLER_PRINCIPAL_GROUP_REQUIRED = "error.document.tickler.principalGroupRequired";
        public static final String ERROR_TICKLER_TERMINATION_DATE_GREATER_SYSTEMDATE = "error.document.tickler.terminationDateGreaterSystemDate";


    }

    public static class EndowmentTransactionDocumentConstants {
        public static final String ERROR_DOCUMENT_CORPUS_ADJUSTMENT_TRANSACTION_LINES_COUNT_GREATER_THAN_ONE = "error.document.corpusAdjustment.transactionLines.atleatOneShouldExist";
        public static final String ERROR_TRANSACTION_LINE_KEMID_REQUIRED = "error.document.transactionLine.kemid.required";
        public static final String ERROR_TRANSACTION_LINE_KEMID_INVALID = "error.document.transactionLine.kemid.invalid";
        public static final String ERROR_TRANSACTION_LINE_KEMID_INACTIVE = "error.document.transactionLine.kemid.inactive";
        public static final String ERROR_TRANSACTION_LINE_KEMID_NO_TRAN_CODE = "error.document.transactionLine.kemid.notran.code";
        public static final String ERROR_TRANSACTION_LINE_AMOUNT_GREATER_THAN_ZERO = "error.document.transactionLine.amount.greaterthan.zero";
        public static final String ERROR_TRANSACTION_LINE_AMOUNT_GREATER_THAN_EQUAL_ZERO = "error.document.transactionLine.amount.greaterthanEqual.zero";
        public static final String ERROR_TRANSACTION_LINE_AMOUNT_LESS_THAN_ZERO = "error.document.transactionLine.amount.lessthan.zero";
        public static final String ERROR_TRANSACTION_UNITS_GREATER_THAN_ZERO = "error.document.transactionLine.units.greaterthan.zero";
        public static final String ERROR_TRANSACTION_UNITS_LESS_THAN_ZERO = "error.document.transactionLine.units.lessthan.zero";
        public static final String ERROR_TRANSACTION_AMOUNT_UNITS_EQUAL = "error.document.transactionLine.units.amount.equal";
        public static final String ERROR_TRANSACTION_LINE_ETRAN_REQUIRED = "error.document.transactionLine.etran.required";
        public static final String ERROR_TRANSACTION_LINE_ETRAN_BLANK = "error.document.transactionLine.etran.blank";
        public static final String ERROR_TRANSACTION_LINE_ETRAN_INVALID = "error.document.transactionLine.etran.invalid";
        public static final String ERROR_ENDOWMENT_TRANSACTION_TYPE_CODE_VALIDITY = "error.document.transactionLine.etran.typecode.validity";
        public static final String ERROR_ENDOWMENT_TRANSACTION_TYPE_CODE_VALIDITY_INCOME_EXPENSE_ASSET = "error.document.transactionLine.etran.typecode.validityIncomeExpenseOrAsset";
        public static final String ERROR_TRANSACTION_SECURITY_REQUIRED = "error.document.transaction.security.required";
        public static final String ERROR_TRANSACTION_REGISTRATION_CODE_REQUIRED = "error.document.transaction.registrationCode.required";
        public static final String ERROR_TRANSACTION_REGISTRATION_CODE_ENTERED_WITHOUT_SECURITY = "error.document.transaction.registrationCode.entered.without.security";
        public static final String ERROR_TRANSACTION_SECURITY_INVALID = "error.document.transaction.security.invalid";
        public static final String ERROR_TRANSACTION_SECURITY_INACTIVE = "error.document.transaction.security.inactive";
        public static final String ERROR_TRANSACTION_SECURITY_CLASS_CODE_MISMATCH = "error.document.transaction.security.classCode.mismatch";
        public static final String ERROR_TRANSACTION_SECURITY_CODE_EQUAL = "error.document.transaction.security.code.equal";
        public static final String ERROR_TRANSACTION_DETAILS_SUB_TYPE_REQUIRED = "error.document.transaction.detail.subtype.required";
        public static final String ERROR_TRANSACTION_REGISTRATION_CODE_INVALID = "error.document.transaction.registrationCode.invalid";
        public static final String ERROR_TRANSACTION_REGISTRATION_CODE_INACTIVE = "error.document.transaction.registrationCode.inactive";
        public static final String ERROR_TRANSACTION_LINE_KEMID_CAN_NOT_HAVE_A_PRINCIPAL_TRANSACTION = "error.document.transactionLine.kemid.canNotHaveAPrincipalTransaction";
        public static final String ERROR_TRANSACTION_LINE_CHART_CODE_DOES_NOT_MATCH_FOR_PRINCIPAL = "error.document.transactionLine.etran.chartCodeDoesNotMatchForPrincipal";
        public static final String ERROR_TRANSACTION_LINE_CHART_CODE_DOES_NOT_MATCH_FOR_INCOME = "error.document.transactionLine.etran.chartCodeDoesNotMatchForIncome";
        public static final String ERROR_TRANSACTION_LINE_SECURITY_KEMID_CHART_CODE_DOES_NOT_MATCH = "error.document.transactionLine.security.kemid.chartCodeDoesNotMatch";
        public static final String ERROR_TRANSACTION_LINE_BOTH_AMOUNTS_BLANK = "error.document.transactionLine.both.amountsFields.blank";
        public static final String ERROR_TRANSACTION_LINE_BOTH_AMOUNTS_ENTERED = "error.document.transactionLine.both.amountsFields.entered";

        public static final String ERROR_FROM_TRANSACTION_LINE_COUNT_INSUFFICIENT = "error.document.from.transactionLine.count.insufficient";
        public static final String ERROR_TO_TRANSACTION_LINE_COUNT_INSUFFICIENT = "error.document.to.transactionLine.count.insufficient";

        public static final String ERROR_TRANSACTION_LINE_TAXLOT_INVALID = "error.document.transactionLine.taxlot.invalid";

        public static final String ERROR_DELETING_TRANSACTION_LINE = "error.transactionLine.deleteRule.invalid";

        public static final String WARNING_TRANSACTION_LINE_ENDOWMENT_VALUE_REDUCTION = "warning.transactionLine.endowment.value.reduction";

        public static final String ERROR_TRANSACTION_LINE_SOURCE_TARGET_UNITS_EQUAL = "error.transactionLine.source.target.unit.equal";
        public static final String ERROR_TRANSACTION_LINE_SOURCE_TARGET_AMOUNT_EQUAL = "error.transactionLine.source.target.amount.equal";

        public static final String WARNING_REDUCE_PERMANENTLY_RESTRICTED_FUNDS = "warning.document.transactionLine.reducePermanentlyRestrictedFunds";
        public static final String WARNING_NO_SUFFICIENT_FUNDS = "warning.document.transactionLine.noSufficientFunds";

        public static final String ERROR_TRANSACTION_LINE_TAX_LOT_DONT_CORRESPOND = "error.document.transactionLine.taxLotsDontCorrespond";

        public static final String ERROR_TRANSACTION_LINE_TAX_LOT_UNITS_DONT_CORRESPOND = "error.document.transactionLine.taxLotsUnitsDontCorrespond";

        // Asset increase constants
        public static final String ERROR_TRANSACTION_SECURITY_NOT_LIABILITY = "error.assetIncreaseDocument.security.notLiability";
        public static final String ERROR_TRANSACTION_SECURITY_COMMITMENT_AMT = "error.assetIncreaseDocument.security.commitmentAmountValidation";

        // Asset decrease constants
        public static final String ERROR_ASSET_DECREASE_INSUFFICIENT_UNITS = "error.assetDecreaseDocument.insufficientUnits";
        public static final String ERROR_ASSET_DECREASE_TOTAL_AMOUNT_DOES_NOT_MATCH = "error.assetDecreaseDocument.totalAmountDoesNotMatch";
        public static final String ERROR_ASSET_DECREASE_TOTAL_UNITS_DO_NOT_MATCH = "error.assetDecreaseDocument.totalUnitsDoNotMatch";

        // Unit/Share adjustment constants
        public static final String ERROR_UNIT_SHARE_ADJUSTMENT_ADD_ONLY_SOURCE_OR_TARGET_TRAN_LINES = "error.unitShareAdjustment.addOnlySourceOrTargetTransactionLines";
        public static final String ERROR_UNIT_SHARE_ADJUSTMENT_NO_TAX_LOTS_FOUND = "error.unitShareAdjustment.noTaxLotsFoundForKemid";
        public static final String ERROR_UNIT_SHARE_ADJUSTMENT_TRANS_LINE_MUST_HAVE_AT_LEAST_ONE_TAX_LOT = "error.unitShareAdjustment.transactionLineMustHaveAtLeastOneTaxLotLine";
        public static final String ERROR_UNIT_SHARE_ADJUSTMENT_MUST_HAVE_AT_LEAST_ONE_TRANS_LINE = "error.unitShareAdjustment.mustHaveAtLeastOneTransactionLine";
        // Parser Errors
        public static final String ERROR_LINEPARSER_INVALID_PROPERTY_VALUE = "error.lineParser.invalidPropertyValue";
        public static final String ERROR_TRANSACTION_LINE_PARSE_INVALID = "error.transactionLineParser.line.invalid";
        public static final String ERROR_LINEPARSER_WRONG_PROPERTY_NUMBER = "error.lineParser.wrongPropertyNumber";

        // Security Transfer constants
        public static final String ERROR_SECURITY_TRANSFER_ONE_AND_ONLY_ONE_SOURCE_TRANS_LINE = "error.document.securityTransfer.oneAndOnlyOneTransactionLine";
        public static final String ERROR_SECURITY_TRANSFER_ENTER_SOURCE_TRANS_LINE = "error.document.securityTransfer.enterSourceTransactionLine";


        // Accounting Lines
        public static final String ERROR_ACCT_LINE_COUNT_INSUFFICIENT = "error.document.accountingLine.count.insufficient";
        public static final String ERROR_DELETING_ACCOUNTING_LINE = "error.accountingLine.deleteRule.invalid";
        public static final String ERROR_ACCT_LINE_OBJECT_CODE_REQUIRED = "error.accountingLine.objectCode.required";
        public static final String ERROR_ACCT_LINE_OBJECT_CODE_INVALID = "error.accountingLine.objectCode.invalid";
        public static final String ERROR_ACCT_LINE_OBJECT_CODE_INACTIVE = "error.accountingLine.objectCode.inactive";
        public static final String ERROR_ACCT_LINE_OBJECT_CODE_NOT_ASSET_LIABILITY_OR_FUND_BAL = "error.accountingLine.objectCode.notAssetLiabilityOrFundBalance";
        public static final String ERROR_ACCT_LINE_OBJECT_TYPE_NOT_VALID = "error.accountingLine.objectCode.objectTypeNotValid";
        public static final String ERROR_ACCT_LINE_CHART_CODE_REQUIRED = "error.accountingLine.chartCode.required";
        public static final String ERROR_ACCT_LINE_CHART_CODE_INVALID = "error.accountingLine.chartCode.invalid";
        public static final String ERROR_ACCT_LINE_CHART_CODE_INACTIVE = "error.accountingLine.chartCode.inactive";
        public static final String ERROR_ACCT_LINE_ACCT_NBR_REQUIRED = "error.accountingLine.accountNumber.required";
        public static final String ERROR_ACCT_LINE_ACCT_NBR_INVALID = "error.accountingLine.accountNumber.invalid";
        public static final String ERROR_ACCT_LINE_ACCT_NBR_INACTIVE = "error.accountingLine.account.inactive";
        public static final String ERROR_ACCT_LINE_ACCT_NBR_EXPIRED = "error.accountingLine.account.expired";
        public static final String ERROR_ACCT_LINE_AMT_INVALID = "error.accountingLine.amount.invalid";
    }

    public static class HoldingHistoryValueAdjustmentConstants {
        public static final String ERROR_HISTORY_VALUE_ADJUSTMENT_SECURITY_ID_REQUIRED = "error.holdingHistoryValueAdjustment.securityId.required";
        public static final String ERROR_HISTORY_VALUE_ADJUSTMENT_SECURITY_ID_INVALID = "error.holdingHistoryValueAdjustment.securityId.invalid";
        public static final String ERROR_HISTORY_VALUE_ADJUSTMENT_SECURITY_ID_INACTIVE = "error.holdingHistoryValueAdjustment.securityId.inactive";
        public static final String ERROR_HISTORY_VALUE_ADJUSTMENT_SECURITY_ID_NOT_LIABILITY = "error.holdingHistoryValueAdjustment.securityId.notLiability";
        public static final String ERROR_HISTORY_VALUE_ADJUSTMENT_UNIT_VALUE_NOT_POSITIVE = "error.holdingHistoryValueAdjustment.unitValue.notPositive";
        public static final String ERROR_HISTORY_VALUE_ADJUSTMENT_MARKET_VALUE_NOT_POSITIVE = "error.holdingHistoryValueAdjustment.marketValue.notPositive";
        public static final String ERROR_HISTORY_VALUE_ADJUSTMENT_UNIT_VALUE_REQUIRED = "error.holdingHistoryValueAdjustment.unitValue.required";
        public static final String ERROR_HISTORY_VALUE_ADJUSTMENT_MARKET_VALUE_REQUIRED = "error.holdingHistoryValueAdjustment.marketValue.required";
        public static final String ERROR_HISTORY_VALUE_ADJUSTMENT_MARKET_VALUE_ENTERED_WHEN_VALUATION_METHOD_IS_UNIT_VALUE = "error.holdingHistoryValueAdjustment.marketValue.entered";
        public static final String ERROR_HISTORY_VALUE_ADJUSTMENT_UNIT_VALUE_ENTERED_WHEN_VALUATION_METHOD_IS_MARKET_VALUE = "error.holdingHistoryValueAdjustment.unitValue.entered";
    }

    public static class HoldingAdjustmentConstants {
        public static final String ERROR_HOLDING_ADJUSTMENT_ADD_ONLY_SOURCE_OR_TARGET_TRAN_LINES = "error.holdingHistoryAdjustment.addOnlySourceOrTargetTransactionLines";
        public static final String ERROR_HOLDING_ADJUSTMENT_NO_TAX_LOTS_FOUND = "error.holdingHistoryAdjustment.noTaxLotsFoundForKemid";
        public static final String ERROR_HOLDING__ADJUSTMENT_TRANS_LINE_MUST_HAVE_AT_LEAST_ONE_TAX_LOT = "error.holdingHistoryAdjustment.transactionLineMustHaveAtLeastOneTaxLotLine";
        public static final String ERROR_HOLDING_ADJUSTMENT_BOTH_SOURCE_AND_TARGET_TRAN_LINES_BLANK = "error.holdingHistoryAdjustment.bothTransactionLineSections.canNotBeBlank";
    }

    public static class HoldingTaxLotRebalanceConstants {
        public static final String ERROR_HLDG_TAX_LOT_REBALANCE_TOTAL_UNITS_NOT_BALANCED = "error.document.holdingTaxLotRebalance.totalUnits.notBalanced";
        public static final String ERROR_HLDG_TAX_LOT_REBALANCE_TOTAL_COST_NOT_BALANCED = "error.document.holdingTaxLotRebalance.totalCost.notBalanced";
        public static final String ERROR_HLDG_TAX_LOT_REBALANCE_UNITS_INVALID = "error.document.holdingTaxLotRebalance.units.invalid";
        public static final String ERROR_HLDG_TAX_LOT_REBALANCE_COST_INVALID = "error.document.holdingTaxLotRebalance.cost.invalid";
        public static final String ERROR_HLDG_TAX_LOT_REBALANCE_UNITS_COST_ZERO = "error.document.holdingTaxLotRebalance.unitsAndCost.zero";
    }

    public static final class EndowmentAccountingLineParser {
        public static final String ERROR_INVALID_FILE_FORMAT = "error.endowmentAccountingLineParser.invalidFileFormat";
        public static final String ERROR_INVALID_PROPERTY_VALUE = "error.endowmentAccountingLineParser.invalidPropertyValue";
    }

    public static final String ERROR_ENDOW_ACCOUNTING_LINES_DOCUMENT_ACCOUNTING_LINE_IMPORT_GENERAL = "error.endowmentAccountingLinesDocument.endowmentaccountinglineimport.general";

    public static final class EndowmentRecurringCashTransfer {
        public static final String ERROR_DOCUMENT_TARGETOBJECT_EXIST = "error.document.targetObject.exist";
        public static final String ERROR_DOCUMENT_AMOUNT_SPECIFIED_PERCENT_OR_ETRAN = "error.document.amount.specified.percent.or.etran";
        public static final String ERROR_DOCUMENT_TOTAL_PERCENT_CANNOT_EXCEED = "error.document.total.percent.cannot.exceed";
        public static final String ERROR_DOCUMENT_TOTAL_PERCENT_CANNOT_EXCEED_SPECIFIED_ETRAN = "error.document.total.percent.cannot.exceed.specified.etran";
        public static final String ERROR_DOCUMENT_ETRAN_CODE_INVALID_TYPE = "error.document.etran.code.invalid.type";

    }

}

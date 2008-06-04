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
package org.kuali.kfs;


/**
 * Holds error key constants.
 */
public class KFSKeyConstants {
    public static final String ERROR_EXISTENCE = "error.existence";
    public static final String ERROR_EXPIRED = "error.expired";
    public static final String ERROR_INACTIVE = "error.inactive";
    public static final String ERROR_CLOSED = "error.closed";
    public static final String ERROR_DUPLICATE_ELEMENT = "error.duplicate.element";
    public static final String ERROR_INVALIDNEGATIVEAMOUNT = "error.invalidNegativeAmount";
    public static final String ERROR_REQUIRED = "error.required";
    public static final String ERROR_REQUIRED_FOR_US = "error.requiredForUs";
    public static final String ERROR_DATE = "error.invalidDate";
    public static final String ERROR_DATE_TIME = "error.invalidDateTime";
    public static final String ERROR_NUMBER = "error.invalidNumber";
    public static final String ERROR_BOOLEAN = "error.invalidBoolean";
    public final static String ERROR_CURRENCY = "error.currency";
    public final static String ERROR_CURRENCY_DECIMAL = "error.currency.decimal";
    public final static String ERROR_BIG_DECIMAL = "error.bigDecimal";
    public final static String ERROR_INTEGER = "error.integer";
    public final static String ERROR_LONG = "error.long";
    public final static String ERROR_PHONE_NUMBER = "error.phonenumber";
    public final static String ERROR_PERCENTAGE = "error.percentage";
    public final static String ERROR_NUMERIC = "error.numeric";
    public static final String ERROR_MIN_LENGTH = "error.minLength";
    public static final String ERROR_MAX_LENGTH = "error.maxLength";
    public static final String ERROR_INVALID_FORMAT = "error.invalidFormat";
    public static final String ERROR_EXCLUSIVE_MIN = "error.exclusiveMin";
    public static final String ERROR_INCLUSIVE_MAX = "error.inclusiveMax";
    public static final String ERROR_INVALID_XML_FORMAT = "error.invalidXML.format";

    public static final String ERROR_INVALID_ADHOC_PERSON_ID = "error.adhoc.invalid.person";
    public static final String ERROR_UNAUTHORIZED_ADHOC_PERSON_ID = "error.adhoc.unauthorized.person";
    public static final String ERROR_INVALID_ADHOC_WORKGROUP_ID = "error.adhoc.invalid.workgroup";

    public static final String ERROR_SECURE_FIELD = "error.secureField";

    public static final String UNAUTHORIZED_INQUIRY = "unauthorized.inquiry";
    public static final String UNAUTHORIZED_LOOKUP = "unauthorized.lookup";
    public static final String UNAUTHORIZED_DOCUMENT = "unauthorized.document";
    public static final String UNAUTHORIZED_CUSTOM = "unauthorized.custom";

    public static final String MULTIPLE_VALUE_LOOKUP_ICON_LABEL = "multiple.value.lookup.icon.label";

    public static final String AUTHORIZATION_ERROR_GENERAL = "error.authorization.general";
    public static final String AUTHORIZATION_ERROR_INACTIVE_DOCTYPE = "error.authorization.inactiveDocumentType";
    public static final String AUTHORIZATION_ERROR_DOCTYPE = "error.authorization.documentType";
    public static final String AUTHORIZATION_ERROR_DOCUMENT = "error.authorization.document";
    public static final String AUTHORIZATION_ERROR_MAINTENANCE_NEWCOPY = "error.authorization.maintenance.newCopy";
    public static final String AUTHORIZATION_ERROR_MODULE = "error.authorization.module";
    public static final String AUTHORIZATION_ERROR_DOCUMENT_WORKGROUP = "error.authorization.workgroupInitiation";

    // Document-specific errors
    public static final String ERROR_DOCUMENT_ANNOTATION_MAX_LENGTH_EXCEEDED = "error.document.annotation.maxLength.exceeded";
    public static final String ERROR_DOCUMENT_ACCOUNT_CLOSED = "error.document.accountClosed";
    public static final String ERROR_DOCUMENT_ACCOUNT_EXPIRED = "error.document.accountExpired";
    public static final String ERROR_DOCUMENT_ACCOUNT_EXPIRED_NO_CONTINUATION = "error.document.accountExpiredNoContinuation";
    public static final String ERROR_DOCUMENT_ACCOUNT_PRESENCE_NON_BUDGETED_OBJECT_CODE = "error.document.accountPresenceNonBudgetedObjectCode";
    public static final String ERROR_DOCUMENT_BA_MONTH_TOTAL_NOT_EQUAL_CURRENT = "error.document.ba.monthTotalNotEqualCurrent";
    public static final String ERROR_DOCUMENT_BA_BASE_AMOUNT_CHANGE_NOT_ALLOWED = "error.document.ba.baseAmountChangeNotAllowed";
    public static final String ERROR_DOCUMENT_BA_MONTH_TOTAL_NOT_ALLOWED_EMPTY_CURRENT = "error.document.ba.monthTotalNotAllowedCurrent";
    public static final String ERROR_DOCUMENT_BA_CANNOT_INITIATE = "error.document.ba.cannotInitiate";
    public static final String ERROR_DOCUMENT_BA_NON_BUDGETED_ACCOUNT = "error.document.ba.nonBudgetedAccount";
    public static final String ERROR_DOCUMENT_BA_MIXED_FUND_GROUPS = "error.document.ba.mixedFundGroups";
    public static final String ERROR_DOCUMENT_BA_RESTRICTION_LEVELS = "error.document.ba.restrictionLevelS";
    public static final String ERROR_DOCUMENT_BA_BASE_AMOUNTS_BALANCED = "error.document.ba.baseAmountsNotBalanced";
    public static final String ERROR_DOCUMENT_BA_CURRENT_AMOUNTS_BALANCED = "error.document.ba.currentAmountsNotBalanced";
    public static final String ERROR_DOCUMENT_BA_NO_INCOME_STREAM_ACCOUNT = "error.document.ba.noIncomeAccount";
    public static final String WARNING_DOCUMENT_BA_COPY_LABOR_BENEFITS = "warning.document.ba.copyLaborBenefits";
    public static final String ERROR_DOCUMENT_BALANCE = "error.document.balance";
    public static final String ERROR_DOCUMENT_BALANCE_CONSIDERING_OBJECT_TYPES = "error.document.balanceConsideringObjectTypes";
    public static final String ERROR_DOCUMENT_BALANCE_CONSIDERING_CREDIT_AND_DEBIT_AMOUNTS = "error.document.balanceConsideringCreditAndDebitAmounts";
    public static final String ERROR_DOCUMENT_BALANCE_CONSIDERING_SOURCE_AND_TARGET_AMOUNTS = "error.document.balanceConsideringSourceAndTargetAmounts";
    public static final String ERROR_DOCUMENT_BALANCE_CONSIDERING_REVENUE_AND_EXPENSE_OBJECT_TYPES = "error.document.balanceConsideringRevenueAndExpenseObjectTypes";
    public static final String ERROR_DOCUMENT_DISAPPROVE_REASON_REQUIRED = "error.document.disapprove.reasonRequired";
    public static final String ERROR_DOCUMENT_DV_INCORRECT_OBJ_SUB_TYPE_WITH_PAYMENT_REASON = "error.document.dv.incorrectObjSubTypeWithPaymentReason";
    public static final String ERROR_DOCUMENT_EXPENSE_ON_INCOME_SIDE = "error.document.incorrectExpenseOnIncomeSide";
    public static final String ERROR_DOCUMENT_INCOME_ON_EXPENSE_SIDE = "error.document.incorrectIncomeOnExpenseSide";
    public static final String ERROR_DOCUMENT_INVALID_VALUE = "error.document.invalid.value";
    public static final String ERROR_DOCUMENT_INCORRECT_OBJ_CODE = "error.document.incorrectObjCode";
    public static final String ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_OBJ_TYPE = "error.document.incorrectObjCodeWithObjType";
    public static final String ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_SUB_TYPE = "error.document.incorrectObjCodeWithSubType";
    public static final String ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_OBJ_LEVEL = "error.document.incorrectObjCodeWithObjLevel";
    public static final String ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_CONSOLIDATED_OBJ_CODE = "error.document.incorrectObjCodeWithConsObjCode";
    public static final String ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_BUDGET_AGGREGATION = "error.document.incorrectObjCodeWithBudgetAggregation";
    public static final String ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_SUB_TYPE_ON_CANDG_ACCT = "error.document.incorrectObjCodeWithSubTypeOnCandGAcct";
    public static final String ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_SUB_TYPE_OBJ_LEVEL_AND_OBJ_TYPE = "error.document.incorrectObjCodeWithSubTypeObjLevelAndObjType";
    public static final String ERROR_DOCUMENT_INCORRECT_OBJ_SUB_TYPE = "error.document.incorrectObjSubType";
    public static final String ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_SUB_FUND_GROUP = "error.document.incorrectObjCodeWithSubFundGroup";
    public static final String ERROR_DOCUMENT_INCORRECT_OBJ_TYPE = "error.document.incorrectObjTypeCode";
    public static final String ERROR_DOCUMENT_INCORRECT_OBJ_TYPE_WITH_OBJ_SUB_TYPE = "error.document.incorrectObjSubTypeWithObjType";
    public static final String ERROR_DOCUMENT_NO_ACCOUNTING_LINES = "error.document.noAccountingLines";
    public static final String ERROR_DOCUMENT_SOURCE_SECTION_NO_ACCOUNTING_LINES = "error.document.sourceSectionNoAccountingLines";
    public static final String ERROR_DOCUMENT_OPTIONAL_ONE_SIDED_DOCUMENT_REQUIRED_NUMBER_OF_ACCOUNTING_LINES_NOT_MET = "error.document.optionalOneSidedDocument.requiredNumberOfAccountingLinesNotMet";
    public static final String ERROR_DOCUMENT_PC_TRANSACTION_TOTAL_ACCTING_LINE_TOTAL_NOT_EQUAL = "error.document.pc.transactionTotal.accountingLineTotal.notEqual";
    public static final String ERROR_DOCUMENT_TARGET_SECTION_NO_ACCOUNTING_LINES = "error.document.targetSectionNoAccountingLines";
    public static final String ERROR_DOCUMENT_SINGLE_SECTION_NO_ACCOUNTING_LINES = "error.document.singleSectionNoAccountingLines";
    public static final String ERROR_DOCUMENT_NO_DESCRIPTION = "error.document.noDescription";
    public static final String ERROR_DOCUMENT_NO_OBJECT_TYPE_CODE = "error.document.noObjectTypeCode";
    public static final String ERROR_DOCUMENT_SUB_ACCOUNT_CLOSED = "error.document.subAccountClosed";
    public static final String ERROR_DOCUMENT_SUB_ACCOUNT_INACTIVE = "error.document.subAccountInactive";
    public static final String ERROR_DOCUMENT_NULL_ACCOUNTING_LINE = "error.document.nullAccountingLine";
    public static final String ERROR_DOCUMENT_ACCOUNTING_PERIOD_CLOSED = "error.document.accountingPeriod.closed";
    public static final String ERROR_DOCUMENT_ACCOUNTING_PERIOD_THREE_OPEN = "error.document.accountingPeriod.threeOpen";
    public static final String ERROR_DOCUMENT_ACCOUNTING_TWO_PERIODS = "error.document.accountingPeriod.twoPeriods";
    public static final String ERROR_DOCUMENT_JV_AMOUNTS_IN_CREDIT_AND_DEBIT_FIELDS = "error.document.journalVoucher.amountsInCreditAndDebitFields";
    public static final String ERROR_DOCUMENT_JV_INVALID_ACCOUNTING_LINE_TEMPLATE = "error.document.journalVoucher.incorrectJvAccountingLineTemplate";
    public static final String ERROR_DOCUMENT_AV_INCORRECT_FISCAL_YEAR_AVRC = "error.document.auxiliaryVoucher.incorrectFiscalYearAVRC";
    public static final String ERROR_DOCUMENT_AV_INCORRECT_POST_PERIOD_AVRC = "error.document.auxiliaryVoucher.incorrectPostPeriodAVRC";
    public static final String ERROR_DOCUMENT_TOF_OBJECT_SUB_TYPE_NOT_MANDATORY_OR_NON_MANDATORY_TRANSFER = "error.document.transferOfFunds.objectSubTypeCodeNotMandatoryOrNonMandatoryTransfer";
    public static final String ERROR_DOCUMENT_TOF_OBJECT_SUB_TYPE_IS_NULL = "error.document.transferOfFunds.objectSubTypeCodeIsNull";
    public static final String ERROR_DOCUMENT_TOF_MANDATORY_TRANSFERS_DO_NOT_BALANCE = "error.document.transferOfFunds.mandatoryTransfersDoNotBalance";
    public static final String ERROR_DOCUMENT_TOF_NON_MANDATORY_TRANSFERS_DO_NOT_BALANCE = "error.document.transferOfFunds.nonMandatoryTransfersDoNotBalance";
    public static final String ERROR_DOCUMENT_TOF_INVALID_OBJECT_TYPE_CODES = "error.document.transferOfFunds.invalidObjectTypeCodes";
    public static final String ERROR_DOCUMENT_IB_CAPITAL_OBJECT_IN_INCOME_SECTION = "error.document.internalBilling.capitalObjectInIncomeSection";
    public static final String ERROR_DOCUMENT_INCORRECT_REVERSAL_DATE = "error.document.incorrectReversalDate";
    public static final String ERROR_DOCUMENT_NO_OFFSET_DEFINITION = "error.document.noOffsetDefinition";
    public static final String ERROR_DOCUMENT_BANK_OFFSET_NO_ACCOUNT = "error.document.bankOffset.noAccount";
    public static final String ERROR_DOCUMENT_BANK_OFFSET_ACCOUNT_CLOSED = "error.document.bankOffset.accountClosed";
    public static final String ERROR_DOCUMENT_BANK_OFFSET_ACCOUNT_EXPIRED = "error.document.bankOffset.accountExpired";
    public static final String ERROR_DOCUMENT_BANK_OFFSET_NO_OBJECT_CODE = "error.document.bankOffset.noObjectCode";
    public static final String ERROR_DOCUMENT_BANK_OFFSET_INACTIVE_OBJECT_CODE = "error.document.bankOffset.inactiveObjectCode";
    public static final String ERROR_DOCUMENT_BANK_OFFSET_NONEXISTENT_SUB_ACCOUNT = "error.document.bankOffset.nonexistentSubAccount";
    public static final String ERROR_DOCUMENT_BANK_OFFSET_INACTIVE_SUB_ACCOUNT = "error.document.bankOffset.inactiveSubAccount";
    public static final String ERROR_DOCUMENT_BANK_OFFSET_NONEXISTENT_SUB_OBJ = "error.document.bankOffset.nonexistentSubObj";
    public static final String ERROR_DOCUMENT_BANK_OFFSET_INACTIVE_SUB_OBJ = "error.document.bankOffset.inactiveSubObj";
    public static final String ERROR_DOCUMENT_FUND_GROUP_SET_DOES_NOT_BALANCE = "error.document.fundGroupSetDoesNotBalance";
    public static final String ERROR_UPLOADFILE_NULL = "error.uploadFile.null";
    public static final String ERROR_UPLOADFILE_EMPTY = "error.uploadFile.empty";
    public static final String ERROR_UNIMPLEMENTED = "error.unimplemented";
    public static final String ERROR_OPTIMISTIC_LOCK = "error.document.optimisticLockException";
    public static final String ERROR_ACCOUNTING_LINES_DIFFERENT_BUDGET_YEAR = "error.document.accountingLine.differentBudgetYear";

    public static final String QUESTION_CONTINUATION_ACCOUNT_SELECTION = "document.question.selectContinuationAccount.text";
    public static final String QUESTION_ACCT_SUB_FUND_RESTRICTED_STATUS = "document.question.selectSubFundGroup.text";
    public static final String QUESTION_SAVE_BEFORE_CLOSE = "document.question.saveBeforeClose.text";

    // AccountingLine errors
    public static final String ERROR_ACCOUNTINGLINE_INACCESSIBLE_ADD = "error.accountingLine.inaccessibleAdd";
    public static final String ERROR_ACCOUNTINGLINE_INACCESSIBLE_DELETE = "error.accountingLine.inaccessibleDelete";
    public static final String ERROR_ACCOUNTINGLINE_INACCESSIBLE_UPDATE = "error.accountingLine.inaccessibleUpdate";
    public static final String ERROR_ACCOUNTINGLINE_INACCESSIBLE_ADD_NO_SB_CTRL = "error.accountingLine.inaccessibleAdd.noServiceBillingControl";
    public static final String ERROR_ACCOUNTINGLINE_INACCESSIBLE_DELETE_NO_SB_CTRL = "error.accountingLine.inaccessibleDelete.noServiceBillingControl";
    public static final String ERROR_ACCOUNTINGLINE_INACCESSIBLE_UPDATE_NO_SB_CTRL = "error.accountingLine.inaccessibleUpdate.noServiceBillingControl";
    public static final String ERROR_ACCOUNTINGLINE_INACCESSIBLE_ADD_NOT_IN_SB_CTRL_GRP = "error.accountingLine.inaccessibleAdd.notInServiceBillingControlGroup";
    public static final String ERROR_ACCOUNTINGLINE_INACCESSIBLE_DELETE_NOT_IN_SB_CTRL_GRP = "error.accountingLine.inaccessibleDelete.notInServiceBillingControlGroup";
    public static final String ERROR_ACCOUNTINGLINE_INACCESSIBLE_UPDATE_NOT_IN_SB_CTRL_GRP = "error.accountingLine.inaccessibleUpdate.notInServiceBillingControlGroup";
    public static final String ERROR_ACCOUNTINGLINE_LASTACCESSIBLE_DELETE = "error.accountingLine.deleteLastAccessible";
    public static final String ERROR_DOCUMENT_ACCOUNTING_LINE_TOTAL_CHANGED = "error.document.accountingLineTotalChanged";
    public static final String ERROR_DOCUMENT_SINGLE_ACCOUNTING_LINE_SECTION_TOTAL_CHANGED = "error.document.singleAccountingLineSectionTotalChanged";
    public static final String ERROR_ACCOUNTINGLINE_DELETERULE_INVALIDACCOUNT = "error.accountingLine.deleteRule.invalidAccount";
    public static final String ERROR_DOCUMENT_ACCOUNTING_LINE_INVALID_FORMAT = "error.document.accountingLine.invalidFormat";
    public static final String ERROR_DOCUMENT_ACCOUNTING_LINE_MAX_LENGTH = "error.document.accountingLine.maxLength";
    public static final String ERROR_DOCUMENT_ACCOUNTING_LINE_SALES_TAX_REQUIRED = "error.document.accountingLine.salesTaxRequired";
    public static final String ERROR_DOCUMENT_ACCOUNTING_LINE_INVALID_ACCT_OBJ_CD = "error.document.accountingLine.invalidAccountAndObjectCode";
    public static final String ERROR_DOCUMENT_ACCOUNTING_LINE_SALES_TAX_INVALID_ACCOUNT = "error.document.accountingLine.salesTax.invalidAccountChart";

    // General Maintenance Document Error Messages
    public static final String ERROR_DOCUMENT_MAINTENANCE_PRIMARY_KEYS_CHANGED_ON_EDIT = "error.document.maintenance.general.primaryKeysChangedOnEdit";
    public static final String ERROR_DOCUMENT_MAINTENANCE_KEYS_ALREADY_EXIST_ON_CREATE_NEW = "error.document.maintenance.general.objectAlreadyExistsByPrimaryKeysOnCreateNew";
    public static final String ERROR_DOCUMENT_AUTHORIZATION_RESTRICTED_FIELD_CHANGED = "error.document.maintenance.authorization.restrictedFieldChanged";
    public static final String ERROR_DOCUMENT_MAINTENANCE_PARTIALLY_FILLED_OUT_REF_FKEYS = "error.document.maintenance.partiallyFilledOutReferenceForeignKeys";
    public static final String ERROR_DOCUMENT_MAINTENANCE_FORMATTING_ERROR = "error.document.maintenance.formattingError";

    // Account Maintenance error messages
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCT_NMBR_NOT_ALLOWED = "error.document.accountMaintenance.accountNumberNotAllowed";
    public static final String ERROR_DOCUMENT_ACCMAINT_ONLY_SUPERVISORS_CAN_EDIT = "error.document.accountMaintenance.onlySupervisorsCanEditClosedAccounts";
    public static final String ERROR_DOCUMENT_ACCMAINT_RESTRICTED_STATUS_DT_REQ = "error.document.accountMaintenance.restrictedStatusDateRequired";
    public static final String ERROR_DOCUMENT_ACCMAINT_RPTS_TO_ACCT_REQUIRED_IF_FRINGEBENEFIT_FALSE = "error.document.accountMaintenance.reportsToAccountRequiredIfFringeBenefitsFalse";
    public static final String ERROR_DOCUMENT_ACCMAINT_RPTS_TO_CHART_REQUIRED_IF_FRINGEBENEFIT_FALSE = "error.document.accountMaintenance.reportsToChartRequiredIfFringeBenefitsFalse";
    public static final String ERROR_DOCUMENT_ACCMAINT_RPTS_TO_ACCT_MUST_BE_FLAGGED_FRINGEBENEFIT = "error.document.accountMaintenance.reportsToAccountMustBeFringeBenefitFlaggedIfThisAccountFringeBenefitsIsFalse";
    public static final String ERROR_DOCUMENT_ACCMAINT_PRO_TYPE_REQD_FOR_EMPLOYEE = "error.document.accountMaintenance.professionalTypeRequiredForEmployee";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACTIVE_REQD_FOR_EMPLOYEE = "error.document.accountMaintenance.activeStatusRequiredForEmployee";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCT_SUPER_CANNOT_BE_FISCAL_OFFICER = "error.document.accountMaintenance.accountSupervisorCannotBeFiscalOfficer";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCT_SUPER_CANNOT_BE_ACCT_MGR = "error.document.accountMaintenance.accountSupervisorCannotBeAcctManager";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCT_SUPER_CANNOT_EQUAL_EXISTING_FISCAL_OFFICER = "error.document.accountMaintenance.accountSupervisorCannotEqualExistingFiscalOfficer";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCT_SUPER_CANNOT_EQUAL_EXISTING_ACCT_MGR = "error.document.accountMaintenance.accountSupervisorCannotEqualExistingAcctManager";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCT_MGR_CANNOT_EQUAL_EXISTING_ACCT_SUPERVISOR = "error.document.accountMaintenance.accountManagerCannotEqualExistingAccountSupervisor";
    public static final String ERROR_DOCUMENT_ACCMAINT_FISCAL_OFFICER_CANNOT_EQUAL_EXISTING_ACCT_SUPERVISOR = "error.document.accountMaintenance.fiscalOfficerCannotEqualExistingAccountSupervisor";
    public static final String ERROR_DOCUMENT_ACCMAINT_INVALID_BUDGET_RECORD_LVL_CD = "error.document.accountMaintenance.invalidBudgetRecordingLevel";
    public static final String ERROR_DOCUMENT_ACCMAINT_INACTIVE_BUDGET_RECORD_LVL_CD = "error.document.accountMaintenance.inactiveBudgetRecordingLevel";
    public static final String ERROR_DOCUMENT_ACCMAINT_INVALID_ACCT_TYPE = "error.document.accountMaintenance.invalidAccountType";
    public static final String ERROR_DOCUMENT_ACCMAINT_INVALID_HIGHER_ED_CD = "error.document.accountMaintenance.invalidFinHigherEdFunctionCd";
    public static final String ERROR_DOCUMENT_ACCMAINT_INVALID_ORG = "error.document.accountMaintenance.organizationInvalid";
    public static final String ERROR_DOCUMENT_ACCMAINT_INACTIVE_ORG = "error.document.accountMaintenance.organizationInactive";
    public static final String ERROR_DOCUMENT_ACCMAINT_INVALID_CAMPUS_CD = "error.document.accountMaintenance.campusCodeInvalid";
    public static final String ERROR_DOCUMENT_ACCMAINT_INVALID_STATE_CD = "error.document.accountMaintenance.stateCodeInvalid";
    public static final String ERROR_DOCUMENT_ACCMAINT_INVALID_ZIP_CD = "error.document.accountMaintenance.zipCodeInvalid";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCT_CANNOT_BE_CLOSED_EXP_DATE_INVALID = "error.document.accountMaintenance.accountCannotBeClosedExpDateInvalid";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCT_CLOSE_CONTINUATION_ACCT_REQD = "error.document.accountMaintenance.accountCloseContinuationAcctReqd";
    public static final String ERROR_DOCUMENT_ACCMAINT_INCOME_STREAM_ACCT_NBR_CANNOT_BE_EMPTY = "error.document.accountMaintenance.incomeStreamAcctNbrCannotBeEmpty";
    public static final String ERROR_DOCUMENT_ACCMAINT_INCOME_STREAM_ACCT_COA_CANNOT_BE_EMPTY = "error.document.accountMaintenance.incomeStreamAcctCOACannotBeEmpty";
    public static final String ERROR_DOCUMENT_ACCMAINT_CONTRACT_CONTROL_COA_CANNOT_BE_EMPTY = "error.document.accountMaintenance.contractControlCOACannotBeEmpty";
    public static final String ERROR_DOCUMENT_ACCMAINT_CONTRACT_CONTROL_ACCT_CANNOT_BE_EMPTY = "error.document.accountMaintenance.contractControlAcctCannotBeEmpty";
    public static final String ERROR_DOCUMENT_ACCMAINT_ICR_TYPE_CODE_CANNOT_BE_EMPTY = "error.document.accountMaintenance.icrTypeCodeCannotBeEmpty";
    public static final String ERROR_DOCUMENT_ACCMAINT_ICR_SERIES_IDENTIFIER_CANNOT_BE_EMPTY = "error.document.accountMaintenance.icrSeriesIdentifierCannotBeEmpty";
    public static final String ERROR_DOCUMENT_ACCMAINT_ICR_CHART_CODE_CANNOT_BE_EMPTY = "error.document.accountMaintenance.icrChartCodeCannotBeEmpty";
    public static final String ERROR_DOCUMENT_ACCMAINT_ICR_ACCOUNT_CANNOT_BE_EMPTY = "error.document.accountMaintenance.icrAccountCannotBeEmpty";
    public static final String ERROR_DOCUMENT_ACCMAINT_CG_FEDERAL_ASSISTANCE_NUMBER_CANNOT_BE_EMPTY = "error.document.accountMaintenance.cgFederalAssitanceNumberCannotBeEmpty";
    public static final String ERROR_DOCUMENT_ACCMAINT_EXP_DATE_TODAY_LATER = "error.document.accountMaintenance.expDateTodayLater";
    public static final String ERROR_DOCUMENT_ACCMAINT_CONTINUATION_ACCT_REQD_IF_EXP_DATE_COMPLETED = "error.document.accountMaintenance.continuationAcctReqdIfExpDateCompleted";
    public static final String ERROR_DOCUMENT_ACCMAINT_CONTINUATION_FINCODE_REQD_IF_EXP_DATE_COMPLETED = "error.document.accountMaintenance.continuationFinCodeIfExpDateCompleted";
    public static final String ERROR_DOCUMENT_ACCMAINT_EXP_DATE_CANNOT_BE_BEFORE_EFFECTIVE_DATE = "error.document.accountMaintenance.expDateCannotBeBeforeEffectiveDate";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCT_GF_BUDGET_RECORD_LVL_MIXED = "error.document.accountMaintenance.acctGeneralFundCannotHaveBudgetRecLvlMixed";
    public static final String ERROR_DOCUMENT_ACCMAINT_INVALID_SUBFUNDGROUP = "error.document.accountMaintenance.invalidSubFundGroup";
    public static final String ERROR_DOCUMENT_ACCMAINT_CAMS_SUBFUNDGROUP_WITH_MISSING_CAMPUS_CD_FOR_BLDG = "error.document.accountMaintenance.camsSubFundGroupWithMissingCampusCode";
    public static final String ERROR_DOCUMENT_ACCMAINT_NONCAMS_SUBFUNDGROUP_WITH_CAMPUS_CD_FOR_BLDG = "error.document.accountMaintenance.nonCamsSubFundGroupWithCampusCode";
    public static final String ERROR_DOCUMENT_ACCMAINT_BLANK_SUBFUNDGROUP_WITH_CAMPUS_CD_FOR_BLDG = "error.document.accountMaintenance.blankSubFundGroupWithCampusCode";
    public static final String ERROR_DOCUMENT_ACCMAINT_CAMS_SUBFUNDGROUP_WITH_MISSING_BUILDING_CD = "error.document.accountMaintenance.camsSubFundGroupWithMissingBuildingCode";
    public static final String ERROR_DOCUMENT_ACCMAINT_NONCAMS_SUBFUNDGROUP_WITH_BUILDING_CD = "error.document.accountMaintenance.nonCamsSubFundGroupWithBuildingCode";
    public static final String ERROR_DOCUMENT_ACCMAINT_BLANK_SUBFUNDGROUP_WITH_BUILDING_CD = "error.document.accountMaintenance.blankSubFundGroupWithBuildingCode";
    public static final String ERROR_DOCUMENT_ACCMAINT_INVALID_FRINGE_BENEFIT_ACCOUNT = "error.document.accountMaintenance.invalidFringeBenefitAccount";
    public static final String ERROR_DOCUMENT_ACCMAINT_INVALID_CONTINUATION_ACCOUNT = "error.document.accountMaintenance.invalidContinuationAccount";
    public static final String ERROR_DOCUMENT_ACCMAINT_INVALID_ENDOWMENT_ACCOUNT = "error.document.accountMaintenance.invalidEndowmentAccount";
    public static final String ERROR_DOCUMENT_ACCMAINT_INVALID_CONTRACT_CONTROL_ACCOUNT = "error.document.accountMaintenance.invalidContractControlAccount";
    public static final String ERROR_DOCUMENT_ACCMAINT_INVALID_INCOME_STREAM_ACCOUNT = "error.document.accountMaintenance.invalidIncomeStreamAccount";
    public static final String ERROR_DOCUMENT_ACCMAINT_INVALID_ICR_ACCOUNT = "error.document.accountMaintenance.invalidIndirectCostRecoveryAccount";
    public static final String ERROR_DOCUMENT_ACCMAINT_INVALID_SUFFICIENT_FUNDS_CODE = "error.document.accountMaintenance.invalidSufficientFundsCode";
    public static final String ERROR_DOCUMENT_ACCMAINT_INVALID_RESTRICTED_STATUS_CODE = "error.document.accountMaintenance.invalidRestrictedStatusCode";
    public static final String ERROR_DOCUMENT_ACCMAINT_INVALID_PERSON_FISCAL_OFFICER = "error.document.accountMaintenance.invalidPersonAccountFiscalOfficer";
    public static final String ERROR_DOCUMENT_ACCMAINT_INVALID_PERSON_SUPERVISOR = "error.document.accountMaintenance.invalidPersonAccountSupervisor";
    public static final String ERROR_DOCUMENT_ACCMAINT_INVALID_PERSON_MANAGER = "error.document.accountMaintenance.invalidPersonAccountManager";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCOUNT_CLOSED_CONTINUATION = "error.document.accountMaintenance.closedAccount.continuationAccount";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCOUNT_EXPIRED_CONTINUATION = "error.document.accountMaintenance.expiredAccount.continuationAccount";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCOUNT_CLOSED_FRINGE_BENEFIT = "error.document.accountMaintenance.closedAccount.fringeBenefitAccount";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCOUNT_CLOSED_ENDOWMENT = "error.document.accountMaintenance.closedAccount.endowmentAccount";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCOUNT_CLOSED_CONTRACT_CONTROL = "error.document.accountMaintenance.closedAccount.contractControlAccount";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCOUNT_CLOSED_INCOME_STREAM = "error.document.accountMaintenance.closedAccount.incomeStreamAccount";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCOUNT_CLOSED_ICR = "error.document.accountMaintenance.closedAccount.icrAccount";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCOUNT_CLOSED_PENDING_LEDGER_ENTRIES = "error.document.accountMaintenance.closedAccount.noPendingLedgerEntriesAllowed";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCOUNT_CLOSED_NO_LOADED_BEGINNING_BALANCE = "error.document.accountMaintenance.closedAccount.beginningBalanceNotLoaded";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCOUNT_CLOSED_NO_FUND_BALANCES = "error.document.accountMaintenance.closedAccount.noFundBalances";
    public static final String ERROR_DOCUMENT_GLOBAL_ACCOUNT_NO_ACCOUNTS = "error.document.accountGlobalDetails.noAccountsEntered";
    public static final String ERROR_DOCUMENT_GLOBAL_ACCOUNT_INVALID_ACCOUNT = "error.document.accountGlobalDetails.invalidAccount";
    public static final String ERROR_DOCUMENT_GLOBAL_ACCOUNT_INVALID_ORG = "error.document.accountGlobal.invalidOrganization";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCOUNT_CLOSED_PENDING_LABOR_LEDGER_ENTRIES = "error.document.accountMaintenance.closedAccount.noPendingLaborLedgerEntriesAllowed";
    public static final String ERROR_DOCUMENT_ACCMAINT_REPORTING_USER_MUST_BE_CHART_MANAGER_OR_ROOT_MANAGER = "error.document.accountMaintenance.userMustBeChartManagerOrRootManager";
    public static final String ERROR_DOCUMENT_ACCMAINT_CG_FIELDS_FILLED_FOR_NON_CG_ACCOUNT = "error.document.accountMaintenance.cgFieldsFilledInForNonCGAccount";

    // Fiscal Period Maintenance Errors
    public static final String ERROR_DOCUMENT_FISCAL_PERIOD_YEAR_DOESNT_EXIST = "error.document.fiscalPeriodMaintenance.yearDosntExist";

    // Chart Maintenance Errors
    public static final String ERROR_DOCUMENT_CHART_REPORTS_TO_CHART_MUST_EXIST = "error.document.chart.reportsToChartMustExist";

    // Account Delegate Maintenance Errors
    public static final String ERROR_DOCUMENT_ACCTDELEGATEMAINT_STARTDATE_IN_PAST = "error.document.accountDelegateMaintenance.startDateMustBeGreaterThanOrEqualToToday";
    public static final String ERROR_DOCUMENT_ACCTDELEGATEMAINT_FROM_AMOUNT_NONNEGATIVE = "error.document.accountDelegateMaintenance.fromAmountMustBeNonNegative";
    public static final String ERROR_DOCUMENT_ACCTDELEGATEMAINT_TO_AMOUNT_MORE_THAN_FROM_OR_ZERO = "error.document.accountDelegateMaintenance.toAmountMustBeEqualOrGreaterThanFromAmountOrZero";
    public static final String ERROR_DOCUMENT_ACCTDELEGATEMAINT_PRIMARY_ROUTE_ALL_TYPES_ALREADY_EXISTS = "error.document.accountDelegateMaintenance.primaryRouteAlreadyExistsAllDocTypes";
    public static final String ERROR_DOCUMENT_ACCTDELEGATEMAINT_PRIMARY_ROUTE_ALREADY_EXISTS_FOR_NEW_ALL = "error.document.accountDelegateMaintenance.primaryRouteAlreadyExistsForNewAll";
    public static final String ERROR_DOCUMENT_ACCTDELEGATEMAINT_PRIMARY_ROUTE_ALREADY_EXISTS_FOR_DOCTYPE = "error.document.accountDelegateMaintenance.primaryRouteAlreadyExistsForNewDocType";
    public static final String ERROR_DOCUMENT_GLOBAL_DELEGATEMAINT_PRIMARY_ROUTE_ALL_TYPES_ALREADY_EXISTS = "error.document.globalDelegateMaintenance.primaryRouteAlreadyExistsAllDocTypes";
    public static final String ERROR_DOCUMENT_GLOBAL_DELEGATEMAINT_PRIMARY_ROUTE_ALREADY_EXISTS_FOR_DOCTYPE = "error.document.globalDelegateMaintenance.primaryRouteAlreadyExistsForNewDocType";
    public static final String ERROR_DOCUMENT_ACCTDELEGATEMAINT_USER_DOESNT_EXIST = "error.document.accountDelegateMaintenance.userDoesntExist";
    public static final String ERROR_DOCUMENT_ACCTDELEGATEMAINT_USER_NOT_ACTIVE = "error.document.accountDelegateMaintenance.userNotActive";
    public static final String ERROR_DOCUMENT_ACCTDELEGATEMAINT_USER_NOT_ACTIVE_KUALI_USER = "error.document.accountDelegateMaintenance.notActiveKualiUser";
    public static final String ERROR_DOCUMENT_ACCTDELEGATEMAINT_USER_NOT_PROFESSIONAL = "error.document.accountDelegateMaintenance.userNotProfessional";
    public static final String ERROR_DOCUMENT_ACCTDELEGATEMAINT_ACCT_NOT_CLOSED = "error.document.accountDelegateMaintenance.acctNotClosed";
    public static final String ERROR_DOCUMENT_GLOBAL_ACCOUNT_ONE_CHART_ONLY = "error.document.accountGlobalDetails.onlyOneChartAllowed";
    public static final String ERROR_DOCUMENT_GLOBAL_ACCOUNT_ONE_CHART_ONLY_ADDNEW = "error.document.accountGlobalDetails.onlyOneChartAllowedOnAddNew";
    public static final String ERROR_DOCUMENT_DELEGATE_CHANGE_NO_DELEGATE = "error.document.delegateGlobal.noDelegate";
    public static final String ERROR_DOCUMENT_DELEGATE_CHANGE_NO_ACTIVE_DELEGATE = "error.document.delegateGlobal.noActiveDelegate";
    public static final String ERROR_DOCUMENT_DELEGATE_CHANGE_ACCT_DOESNT_EXIST = "error.document.delegateGlobal.acctDoesntExist";
    public static final String ERROR_DOCUMENT_DELEGATE_CHANGE_SAVE_AND_LOAD = "error.document.delegateGlobal.saveAndLoad";
    public static final String ERROR_DOCUMENT_DELEGATE_CHANGE_SAVE_SUCCESS = "error.document.delegateGlobal.saveSuccess";

    // SubAccount Maintenance Errors
    public static final String ERROR_DOCUMENT_SUBACCTMAINT_RPTCODE_ALL_FIELDS_IF_ANY_FIELDS = "error.document.subAccountMaintenance.someReportingCodeFieldsEnteredButNotAll";
    public static final String ERROR_DOCUMENT_SUBACCTMAINT_RPTCODE_DOES_NOT_EXIST_IN_SYSTEM = "error.document.subAccountMaintenance.reportingCodeNotFoundInTheSystem";
    public static final String ERROR_DOCUMENT_SUBACCTMAINT_NOT_AUTHORIZED_ENTER_CG_FIELDS = "error.document.subAccountMaintenance.cannotEnterCgValuesNotAuthorized";
    public static final String ERROR_DOCUMENT_SUBACCTMAINT_NOT_AUTHORIZED_CHANGE_CG_FIELDS = "error.document.subAccountMaintenance.cannotChangeCgValuesNotAuthorized";
    public static final String ERROR_DOCUMENT_SUBACCTMAINT_INVALI_SUBACCOUNT_TYPE_CODES = "error.document.subAccountMaintenance.invalidSubAccountTypeCodes";
    public static final String ERROR_DOCUMENT_SUBACCTMAINT_COST_SHARING_ACCT_FIELDS_INCOMPLETE = "error.document.subAccountMaintenance.someCostSharingAccountFieldsEnteredButNotAll";
    public static final String ERROR_DOCUMENT_SUBACCTMAINT_ICR_FIN_SERIES_ID_EXISTS_BUT_NOT_FOR_THIS_FY = "error.document.subAccountMaintenance.financialIcrSeriesIdExistsButNotForThisFiscalYear";
    public static final String ERROR_DOCUMENT_SUBACCTMAINT_COST_SHARE_ACCOUNT_MAY_NOT_BE_CG_FUNDGROUP = "error.document.subAccountMaintenance.costSharingAccountCannotBeCGFundGroup";
    public static final String ERROR_DOCUMENT_SUBACCTMAINT_COST_SHARE_SECTION_INVALID = "error.document.subAccountMaintenance.costSharingSectionInvalid";
    public static final String ERROR_DOCUMENT_SUBACCTMAINT_ICR_SECTION_INVALID = "error.document.subAccountMaintenance.icrSectionInvalid";
    public static final String ERROR_DOCUMENT_SUBACCTMAINT_NON_FUNDED_ACCT_CS_INVALID = "error.document.subAccountMaintenance.nonFundedAcctCsInvalid";
    public static final String ERROR_DOCUMENT_SUBACCTMAINT_NON_FUNDED_ACCT_ICR_INVALID = "error.document.subAccountMaintenance.nonFundedAcctIcrInvalid";
    public static final String ERROR_DOCUMENT_SUBACCTMAINT_NON_FUNDED_ACCT_SUB_ACCT_TYPE_CODE_INVALID = "error.document.subAccountMaintenance.nonFundedAcctSubAcctTypeCodeInvalid";

    // SubObjectCode Maintenance Errors
    public static final String ERROR_DOCUMENT_SUBOBJECTMAINT_ACCOUNT_MAY_NOT_BE_CLOSED = "error.document.subObjectCodeMaintenance.accountMayNotBeClosed";
    public static final String ERROR_DOCUMENT_GLOBAL_SUBOBJECTMAINT_CHART_MUST_EXIST = "error.document.globalSubObjectCodeMaintenance.chartMustExist";
    public static final String ERROR_DOCUMENT_GLOBAL_SUBOBJECTMAINT_CHART_MUST_BE_SAME = "error.document.globalSubObjectCodeMaintenance.chartMustBeSame";
    public static final String ERROR_DOCUMENT_GLOBAL_SUBOBJECTMAINT_FISCAL_YEAR_MUST_EXIST = "error.document.globalSubObjectCodeMaintenance.fiscalYearMustExist";
    public static final String ERROR_DOCUMENT_GLOBAL_SUBOBJECTMAINT_FISCAL_YEAR_MUST_BE_SAME = "error.document.globalSubObjectCodeMaintenance.fiscalYearMustBeSame";
    public static final String ERROR_DOCUMENT_GLOBAL_SUBOBJECTMAINT_NO_OBJECT_CODE = "error.document.globalSubObjectCodeMaintenance.noObjectCode";
    public static final String ERROR_DOCUMENT_GLOBAL_SUBOBJECTMAINT_NO_ACCOUNT = "error.document.globalSubObjectCodeMaintenance.noAccount";
    public static final String ERROR_DOCUMENT_GLOBAL_SUBOBJECTMAINT_INVALID_OBJECT_CODE = "error.document.subObjCdGlobalDetails.invalidObjectCode";

    // Org Maintenance Errors
    public static final String ERROR_DOCUMENT_ORGMAINT_ONLY_CHART_MGRS_MAY_MODIFY_ORG_PLANT_ACCT_NUMBER = "error.document.orgMaintenance.onlyChartManagersCanModifyOrgPlantAccountNumber";
    public static final String ERROR_DOCUMENT_ORGMAINT_ONLY_CHART_MGRS_MAY_MODIFY_ORG_PLANT_ACCT_CHARTCODE = "error.document.orgMaintenance.onlyChartManagersCanModifyOrgPlantChartCode";
    public static final String ERROR_DOCUMENT_ORGMAINT_ONLY_CHART_MGRS_MAY_MODIFY_CAMPUS_PLANT_ACCT_NUMBER = "error.document.orgMaintenance.onlyChartManagersCanModifyCampusPlantAccountNumber";
    public static final String ERROR_DOCUMENT_ORGMAINT_ONLY_CHART_MGRS_MAY_MODIFY_CAMPUS_PLANT_ACCT_CHARTCODE = "error.document.orgMaintenance.onlyChartManagersCanModifyCampusPlantChartCode";
    public static final String ERROR_DOCUMENT_ORGMAINT_END_DATE_REQUIRED_ON_ORG_CLOSURE = "error.document.orgMaintenance.closingOrgMustHaveEndDate";
    public static final String ERROR_DOCUMENT_ORGMAINT_OPEN_CHILD_ACCOUNTS_ON_ORG_CLOSURE = "error.document.orgMaintenance.closingOrgMustHaveNoChildAccounts";
    public static final String ERROR_DOCUMENT_ORGMAINT_OPEN_CHILD_ORGS_ON_ORG_CLOSURE = "error.document.orgMaintenance.closingOrgMustHaveNoChildOrgs";
    public static final String ERROR_DOCUMENT_ORGMAINT_END_DATE_GREATER_THAN_BEGIN_DATE = "error.document.orgMaintenance.endDateMustBeAfterBeginDate";
    public static final String ERROR_DOCUMENT_ORGMAINT_REPORTING_ORG_CANNOT_BE_SAME_ORG = "error.document.orgMaintenance.reportingOrgCannotBeSameOrg";
    public static final String ERROR_DOCUMENT_ORGMAINT_REPORTING_ORG_CANNOT_BE_CIRCULAR_REF_TO_SAME_ORG = "error.document.orgMaintenance.reportingOrgCannotBeCircularRefToSameOrg";
    public static final String ERROR_DOCUMENT_ORGMAINT_REPORTING_ORG_MUST_BE_SAME_ORG = "error.document.orgMaintenance.reportingOrgMustBeSameOrg";
    public static final String ERROR_DOCUMENT_ORGMAINT_REPORTING_ORG_MUST_EXIST = "error.document.orgMaintenance.reportingOrgMustExist";
    public static final String ERROR_DOCUMENT_ORGMAINT_REPORTING_USER_MUST_BE_CHART_MANAGER_OR_ROOT_MANAGER = "error.document.orgMaintenance.userMustBeChartManagerOrRootManager";
    public static final String ERROR_DOCUMENT_ORGMAINT_DEFAULT_ACCOUNT_NUMBER_REQUIRED = "error.document.orgMaintenance.defaultAccountNumberRequired";
    public static final String ERROR_DOCUMENT_ORGMAINT_STARTDATE_IN_PAST = "error.document.orgMaintenance.startDateMustBeGreaterThanOrEqualToToday";
    public static final String ERROR_DOCUMENT_ORGMAINT_ONLY_ONE_TOP_LEVEL_ORG = "error.document.orgMaintenance.onlyOneTopLevelOrg";

    // Org Reversion Errors
    public static final String ERROR_DOCUMENT_GLOBAL_ORG_REVERSION_BUDGET_REVERSION_INCOMPLETE = "error.document.globalOrgReversion.incompleteBudgetReversion";
    public static final String ERROR_DOCUMENT_GLOBAL_ORG_REVERSION_CASH_REVERSION_INCOMPLETE = "error.document.globalOrgReversion.incompleteCashReversion";
    public static final String ERROR_DOCUMENT_GLOBAL_ORG_REVERSION_INVALID_ORG_REVERSION_CATEGORY = "error.document.globalOrgReversion.invalidOrgReversionCategory";
    public static final String ERROR_DOCUMENT_GLOBAL_ORG_REVERSION_INVALID_ORG_REVERSION_CODE = "error.document.globalOrgReversion.invalidOrgReversionCode";
    public static final String ERROR_DOCUMENT_GLOBAL_ORG_REVERSION_OBJECT_CODE_INVALID = "error.document.globalOrgReversion.objectCodeInvalid";
    public static final String ERROR_DOCUMENT_GLOBAL_ORG_REVERSION_NO_ORGANIZATIONS = "error.document.globalOrgReversion.noOrganizations";
    public static final String ERROR_DOCUMENT_GLOBAL_ORG_REVERSION_INVALID_CHART = "error.document.globalOrgReversion.invalidChart";
    public static final String ERROR_DOCUMENT_GLOBAL_ORG_REVERSION_INVALID_ORGANIZATION = "error.document.globalOrgReversion.invalidOrganization";
    public static final String ERROR_DOCUMENT_GLOBAL_ORG_REVERSION_NO_ORG_REVERSION = "error.document.globalOrgReversion.noOrgReversion";
    public static final String ERROR_DOCUMENT_GLOBAL_ORG_REVERSION_DUPLICATE_ORGS = "error.document.globalOrgReversion.duplicateOrgs";
    // AchBank errors
    public static final String ERROR_DOCUMENT_ACHBANKMAINT_INVALID_TYPE_CODE = "error.document.achBankMaintenance.invalidTypeCode";
    public static final String ERROR_DOCUMENT_ACHBANKMAINT_INVALID_OFFICE_CODE = "error.document.achBankMaintenance.invalidOfficeCode";
    public static final String ERROR_DOCUMENT_ACHBANKMAINT_INVALID_INST_STATUS_CODE = "error.document.achBankMaintenance.invalidInstStatusCode";
    public static final String ERROR_DOCUMENT_ACHBANKMAINT_INVALID_DATA_VIEW_CODE = "error.document.achBankMaintenance.invalidDataViewCode";

    // PayeeAchAccount errors
    public static final String ERROR_DOCUMENT_PAYEEACHACCOUNTMAINT_DUPLICATE_RECORD = "error.document.payeeAchAccountMaintenance.duplicateAccount";

    // BankAccount errors
    public static final String ERROR_DOCUMENT_BANKACCMAINT_INVALID_BANK = "error.document.bankAcctMaintenance.invalidBank";
    public static final String ERROR_DOCUMENT_BANKACCMAINT_INVALID_ORG = "error.document.bankAcctMaintenance.invalidOrganization";
    public static final String ERROR_DOCUMENT_BANKACCMAINT_INACTIVE_ORG = "error.document.bankAcctMaintenance.inactiveOrganization";
    public static final String ERROR_DOCUMENT_BANKACCMAINT_INVALID_UNIV_ACCT = "error.document.bankAcctMaintenance.invalidUnivAcct";
    public static final String ERROR_DOCUMENT_BANKACCMAINT_INACTIVE_UNIV_ACCT = "error.document.bankAcctMaintenance.inactiveUnivAcct";

    // KualiUser errors
    public static final String ERROR_DOCUMENT_KUALIUSERMAINT_CANNOT_MARK_INACTIVE = "error.document.kualiUserMaintenance.cannotMarkInactive";
    public static final String ERROR_DOCUMENT_KUALIUSERMAINT_INVALID_CODE = "error.document.kualiUserMaintenance.invalidCode";
    public static final String ERROR_DOCUMENT_KUALIUSERMAINT_CANNOT_EDIT_USER_ID = "error.document.kualiUserMaintenance.cannotEditUserId";
    public static final String ERROR_DOCUMENT_KUALIUSERMAINT_INVALID_EMP_STATUS = "error.document.kualiUserMaintenance.invalidEmpStatus";

    // UniversalUser errors
    public static final String ERROR_DOCUMENT_KUALIUSERMAINT_UNIQUE_EMPLID = "error.document.UniversalUserMaintenance.UniqueEmplId";

    // Object Code errors

    public static final String ERROR_DOCUMENT_OBJCODE_ILLEGAL = "error.document.objectCode.illegal";
    public static final String ERROR_DOCUMENT_REPORTS_TO_OBJCODE_ILLEGAL = "error.document.objectCode.invalidReportsToObject";
    public static final String ERROR_DOCUMENT_OBJCODE_INVALID_CHART = "error.document.objectCode.inValidChart";
    public static final String ERROR_DOCUMENT_OBJCODE_MUST_ONEOF_VALID = "error.document.objectCode.mustOneOfValid";
    public static final String ERROR_DOCUMENT_OBJCODE_MUST_BEVALID = "error.document.objectCode.mustBeValid";
    public static final String ERROR_DOCUMENT_OBJCODE_CONSOLIDATION_ERROR = "error.document.objectCode.consolidation";
    public static final String ERROR_DOCUMENT_OBJCODE_LEVEL_ERROR = "error.document.objectCode.level";
    public static final String ERROR_DOCUMENT_GLOBAL_OBJECTMAINT_INVALID_RPTS_TO_OBJ_CODE = "error.document.objectCodeGlobalDetails.invalidReportsToObjectCode";
    public static final String ERROR_DOCUMENT_GLOBAL_OBJECTMAINT_INVALID_NEXT_YEAR_OBJ_CODE = "error.document.objectCodeGlobalDetails.invalidNextYearObjectCode";
    public static final String ERROR_DOCUMENT_GLOBAL_OBJECTMAINT_INVALID_OBJ_LEVEL = "error.document.objectCodeGlobalDetails.invalidObjectLevel";
    public static final String ERROR_DOCUMENT_GLOBAL_OBJECTMAINT_NO_CHART_FISCAL_YEAR = "error.document.objectCodeGlobalDetails.noChartFiscalYear";
    public static final String ERROR_DOCUMENT_GLOBAL_OBJECTMAINT_FISCAL_YEAR_MUST_EXIST = "error.document.objectCodeGlobalDetails.noFiscalYear";
    public static final String ERROR_DOCUMENT_GLOBAL_OBJECTMAINT_CHART_MUST_EXIST = "error.document.objectCodeGlobalDetails.noChart";

    // Object Consolidation errors
    public static final String ERROR_DOCUMENT_OBJCONSMAINT_ALREADY_EXISTS_AS_OBJLEVEL = "error.document.objConsMaintenance.alreadyExistsAsObjLevel";
    public static final String ERROR_DOCUMENT_OBJCONSMAINT_INVALID_ELIM_OBJCODE = "error.document.objConsMaintenance.invalidElimObjCode";

    // Object Level errors
    public static final String ERROR_DOCUMENT_OBJLEVELMAINT_ALREADY_EXISTS_AS_OBJCONS = "error.document.objLevelMaintenance.alreadyExistsAsObjCons";

    // Object Type errors
    public static final String ERROR_DOCUMENT_OBJTYPE_INVALID_ACCT_CTGRY = "error.document.objType.invalidAccountCategory";

    // Offset Definition errors
    public static final String ERROR_DOCUMENT_OFFSETDEFMAINT_INVALID_OBJ_CODE_FOR_DOCTYPE = "error.document.offsetDefinitionMaintenance.invalidObjectCodeForDocType";
    public static final String ERROR_DOCUMENT_OFFSETDEFMAINT_INACTIVE_OBJ_CODE_FOR_DOCTYPE = "error.document.offsetDefinitionMaintenance.inactiveObjectCodeForDocType";

    public static final String ERROR_CUSTOM = "error.custom";
    public static final String ERROR_INQUIRY = "error.inquiry";
    public static final String ERROR_MAINTENANCE_LOCKED = "error.maintenance.locked";
    public static final String ERROR_MAINTENANCE_LOCKED1 = "error.maintenance.locked.1";
    public static final String ERROR_MAINTENANCE_LOCKED2 = "error.maintenance.locked.2";
    public static final String ERROR_MAINTENANCE_LOCKED3 = "error.maintenance.locked.3";
    public static final String ERROR_ZERO_AMOUNT = "error.zeroAmount";
    public static final String ERROR_ZERO_OR_NEGATIVE_AMOUNT = "error.zeroOrNegativeAmount";
    public static final String ERROR_NEGATIVE_AMOUNT = "error.negativeAmount";
    public static final String ERROR_INVALID_1_CENT_AMOUNT = "error.invalid1CentAmount";
    public static final String ERROR_INVALID_5_CENT_AMOUNT = "error.invalid5CentAmount";
    public static final String ERROR_INVALID_10_CENT_AMOUNT = "error.invalid10CentAmount";
    public static final String ERROR_INVALID_25_CENT_AMOUNT = "error.invalid25CentAmount";
    public static final String ERROR_INVALID_50_CENT_AMOUNT = "error.invalid50CentAmount";
    public static final String ERROR_INVALID_100_CENT_AMOUNT = "error.invalid100CentAmount";
    public static final String ERROR_INVALID_NEGATIVE_AMOUNT = "error.invalidNegativeAmount";
    public static final String ERROR_INVALID_NEGATIVE_AMOUNT_NON_CORRECTION = "error.invalidNegativeAmountForNonCorrection";
    public static final String ERROR_NOT_AMONG = "error.invalidNotAmong";
    public static final String ERROR_PAYEE_INITIATOR = "error.document.payeeinitiator";
    public static final String ERROR_PAYMENT_REASON_PAYEE = "error.document.payreason.payee";
    public static final String ERROR_PAYMENT_REASON_EMPLOYEE = "error.document.payreason.employee";
    public static final String ERROR_NEGATIVE_OR_ZERO_CHECK_TOTAL = "error.document.checktotal.zeroOrNegative";
    public static final String ERROR_NEGATIVE_ACCOUNTING_TOTAL = "error.document.accountingtotal.negative";
    public static final String ERROR_NO_ACCOUNTING_LINES = "error.document.accountinglines.noLines";
    public static final String ERROR_CHECK_ACCOUNTING_TOTAL = "error.document.checkaccounting.match";
    public static final String ERROR_FOUND_TAX_ID_VENDOR = "error.document.payee.foundtaxidvendor";
    public static final String ERROR_FOUND_TAX_ID_PAYEE = "error.document.payee.foundtaxidpayee";
    public static final String ERROR_FOUND_TAX_ID_PEND_PAYEE = "error.document.payee.foundtaxidpendpayee";
    public static final String ERROR_FOUND_TAX_ID_EMPL = "error.document.payee.foundtaxidempl";
    public static final String ERROR_FOUND_TAX_ID_INSTITUTION = "error.document.payee.foundtaxidinstitution";
    public static final String ERROR_EMPL_FLAG = "error.document.payee.emplflag";
    public static final String ERROR_TAX_TYPE_REQUIRED = "error.document.taxtype.required";
    public static final String ERROR_EMPL_FLAG_NOT_SSN = "error.document.emplflag.notssn";
    public static final String WARNING_DV_W9_ALIEN = "message.dv.w9.alien";
    public static final String WARNING_DV_W9_NONALIEN = "message.dv.w9.nonalien";
    public static final String WARNING_MISSING_DV_W9_ALIEN = "message.dv.w9.w8ben";
    public static final String WARNING_MISSING_DV_W9_NONALIEN = "message.dv.w9.w9";

    public static final String QUESTION_EXISTING_PAYEE_NAME = "question.payee.nameExist";
    public static final String QUESTION_COPY_NEW_PAYEE_ADDRESS_LINES = "question.newPayee.copyAddressLines";
    public static final String QUESTION_COPY_CHANGED_PAYEE_ADDRESS_LINES = "question.changePayee.copyAddressLines";
    public static final String QUESTION_GENERATE_LABOR_BENEFIT_LINES = "question.budgetAdjustment.generateBenefitLines";

    public static final String QUESTION_CLEAR_UNNEEDED_TAB = "question.dv.clearUnneededTab";

    public static final String ERROR_DV_ALIEN_NOT_INDIVIDUAL = "message.dv.alien.notindividual";
    public static final String ERROR_DV_DUE_DATE = "error.document.dueDate";
    public static final String ERROR_DV_PAYMENT_REASON = "error.document.paymentReason";
    public static final String ERROR_DV_SPECIAL_HANDLING = "error.document.specialHandling";
    public static final String ERROR_DV_NO_DOCUMENTATION_NOTE = "error.document.noDocumentationNote";
    public static final String ERROR_DV_SPECIAL_HANDLING_NOTE = "error.document.specialHandlingNote";
    public static final String ERROR_DV_NO_DOCUMENTATION_NOTE_MISSING = "error.document.noDocumentationNoteMissing";
    public static final String ERROR_DV_SPECIAL_HANDLING_NOTE_MISSING = "error.document.specialHandlingNoteMissing";
    public static final String ERROR_DV_EXCEPTION_ATTACHED_NOTE_MISSING = "error.document.exceptionAttachedNoteMissing";
    public static final String ERROR_DV_PAYEE_CITY_NAME = "error.document.payee.cityName";
    public static final String ERROR_DV_PAYEE_STATE_CODE = "error.document.payee.stateCode";
    public static final String ERROR_DV_PAYEE_ZIP_CODE = "error.document.payee.zipCode";
    public static final String ERROR_DV_PAYMENT_OBJECT_LEVEL = "error.dv.paymentReasonObjectLevel";
    public static final String ERROR_DV_PAYMENT_OBJECT_CODE = "error.dv.paymentReasonObjectCode";
    public static final String ERROR_DV_OBJECT_TYPE_CODE = "error.dv.objectTypeCode";
    public static final String ERROR_DV_OBJECT_SUB_TYPE_CODE = "error.dv.objectSubTypeCode";
    public static final String ERROR_DV_CURRENCY_TYPE_CODE = "error.dv.currencyTypeCode";
    public static final String ERROR_DV_CURRENCY_TYPE_NAME = "error.dv.currencyTypeName";
    public static final String ERROR_DV_BANK_ROUTING_NUMBER = "error.dv.bankRoutingNumber";
    public static final String ERROR_DV_BANK_WIRE_TO = "error.dv.bankWireTo";
    public static final String ERROR_DV_WIRE_ATTACHMENT = "error.dv.attachmentWire";
    public static final String ERROR_DV_FEDERAL_TAX_NOT_ZERO = "error.dv.federalTaxNotZero";
    public static final String ERROR_DV_STATE_TAX_NOT_ZERO = "error.dv.stateTaxNotZero";
    public static final String ERROR_DV_GROSS_UP_INDICATOR = "error.dv.grossUpIndicator";
    public static final String ERROR_DV_POSTAL_COUNTRY_CODE = "error.dv.postalCountryCode";
    public static final String ERROR_DV_INVALID_FED_TAX_PERCENT = "error.dv.federalTaxPercent";
    public static final String ERROR_DV_INVALID_STATE_TAX_PERCENT = "error.dv.stateTaxPercent";
    public static final String ERROR_DV_NRA_PERMISSIONS_GENERATE = "error.dv.nraTaxLinesPermissions";
    public static final String ERROR_DV_GENERATE_TAX_BOTH_0 = "error.dv.generateLinesBoth0";
    public static final String ERROR_DV_NRA_GENERATE_TAX_LINES_NOT_ALLOWED = "error.dv.nraGenerateLinesNotAllowed";
    public static final String ERROR_DV_GENERATE_TAX_NO_SOURCE = "error.dv.generateLinesNoSource";
    public static final String ERROR_DV_GENERATE_TAX_NOT_NRA = "error.dv.generateNotNRA";
    public static final String ERROR_DV_GENERATE_TAX_DOC_REFERENCE = "error.dv.generateDocReference";
    public static final String ERROR_DV_ADD_LINE_MISSING_PAYMENT_REASON = "error.dv.addLineMissingPaymentReason";
    public static final String ERROR_DV_ADD_LINE_MISSING_PAYEE = "error.dv.addLineMissingPayee";
    public static final String ERROR_DV_TRAVEL_TO_STATE = "error.dv.travelToState";
    public static final String ERROR_DV_TRAVEL_FROM_STATE = "error.dv.travelFromState";
    public static final String ERROR_DV_PERDIEM_CHANGE_REQUIRED = "error.dv.prediemChangeRequired";
    public static final String ERROR_DV_TRAVEL_CHECK_TOTAL = "error.dv.travelCheckTotal";
    public static final String ERROR_DV_TRAVEL_ACCOUNTING_TOTAL = "error.dv.travelAccountingTotal";
    public static final String ERROR_DV_PER_DIEM_CALC_CHANGE = "error.dv.perDiemCalcChange";
    public static final String ERROR_DV_PER_DIEM_RATE = "error.dv.perDiemRateIsBlank";
    public static final String ERROR_DV_PER_DIEM_CALC_AMT = "error.dv.perDiemCalcAmountIsBlank";
    public static final String ERROR_DV_PER_DIEM_CATEGORY = "error.dv.perDiemCategoryIsBlank";
    public static final String ERROR_DV_PER_DIEM_ACTUAL_AMT = "error.dv.perDiemActualAmountIsBlank";
    public static final String ERROR_DV_MILEAGE_CALC_CHANGE = "error.dv.mileageCalcChange";
    public static final String ERROR_DV_ACTUAL_MILEAGE_TOO_HIGH = "error.dv.actualMileageTooHigh";
    public static final String ERROR_DV_AUTO_TO_CITY = "error.dv.autoToCityIsBlank";
    public static final String ERROR_DV_AUTO_TO_STATE = "error.dv.autoToStateIsBlank";
    public static final String ERROR_DV_AUTO_FROM_CITY = "error.dv.autoFromCityIsBlank";
    public static final String ERROR_DV_AUTO_FROM_STATE = "error.dv.autoFromStateIsBlank";
    public static final String ERROR_DV_MILEAGE_AMT = "error.dv.mileageAmountIsBlank";
    public static final String ERROR_DV_MILEAGE_CALC_AMT = "error.dv.mileageCalcAmountIsBlank";
    public static final String ERROR_DV_MILEAGE_ACTUAL_AMT = "error.dv.mileageActualAmountIsBlank";
    public static final String ERROR_DV_CONF_END_DATE = "error.dv.confEndDate";
    public static final String ERROR_DV_EXPENSE_CODE = "error.dv.expenseCodeIsBlank";
    public static final String ERROR_DV_EXPENSE_COMPANY_NAME = "error.dv.expenseCompanyNameIsBlank";
    public static final String ERROR_DV_EXPENSE_AMOUNT = "error.dv.expenseAmountIsBlank";
    public static final String ERROR_DV_PREPAID_EXPENSE_CODE = "error.dv.prepaidExpenseCodeIsBlank";
    public static final String ERROR_DV_PREPAID_EXPENSE_COMPANY_NAME = "error.dv.prepaidExpenseCompanyNameIsBlank";
    public static final String ERROR_DV_PREPAID_EXPENSE_AMOUNT = "error.dv.prepaidExpenseAmountIsBlank";
    public static final String ERROR_DV_PREPAID_CHECK_TOTAL = "error.dv.prepaidCheckTotal";
    public static final String WARNING_DV_PAYEE_NONEXISTANT_CLEARED = "message.dv.payee.nonexistant.cleared";
    public static final String MESSAGE_DV_WIRE_CHARGE = "message.dv.wireCharge";
    public static final String ERROR_EMPLOYEE_PAID_OUTSIDE_PAYROLL = "error.dv.emplPaidOutsidePayroll";
    public static final String ERROR_DV_MOVING_PAYMENT_PAYEE = "error.dv.movingPaymentPayee";
    public static final String ERROR_DV_RESEARCH_PAYMENT_PAYEE = "error.dv.researchPaymentPayee";
    public static final String ERROR_ACTIVE_EMPLOYEE_PREPAID_TRAVEL = "error.dv.activeEmployeePrepaidTravel";
    public static final String ERROR_DV_REVOLVING_PAYMENT_REASON = "error.dv.revolvingPaymentReason";
    public static final String ERROR_DV_DELETE_TAX_LINE = "error.dv.deleteTaxLine";
    public static final String ERROR_DV_CHECK_TOTAL_CHANGE = "error.dv.checkTotalChange";
    public static final String ERROR_BA_AMOUNT_ZERO = "error.document.ba.amount.zero";
    public static final String ERROR_BA_AMOUNT_NEGATIVE = "error.document.ba.amount.negative";
    
    public static final class ElectronicPaymentClaim {
        public static final String ERROR_EFT_NO_CHOSEN_CLAIMING_DOCTYPE = "error.bo.eft.no.chosen.claiming.doctype";
        public static final String ERROR_PRE_CLAIMING_DOCUMENT_DOES_NOT_EXIST = "error.bo.eft.pre.claiming.document.does.not.exist";
        public static final String ERROR_NO_DOCUMENTATION = "error.bo.eft.no.documentation";
        public static final String MESSAGE_EFT_CLAIMING_DOCUMENTATION = "message.bo.eft.claimant.documentation";
        public static final String MESSAGE_EFT_DOCUMENT_CHOICE = "message.bo.eft.document.choice";
        public static final String MESSAGE_EFT_PREVIOUSLY_CLAIMED_HEADER = "message.bo.eft.previously.claimed.header";
        public static final String MESSAGE_EFT_CLAIMING_DOCUMENT_NUMBER_HEADER = "message.bo.eft.claiming.document.number.header";
    }

    public static final String MESSAGE_RELOADED = "message.document.reloaded";
    public static final String MESSAGE_ROUTE_SUCCESSFUL = "message.route.successful";
    public static final String MESSAGE_SAVED = "message.saved";
    public static final String MESSAGE_ROUTE_APPROVED = "message.route.approved";
    public static final String MESSAGE_ROUTE_DISAPPROVED = "message.route.disapproved";
    public static final String MESSAGE_ROUTE_CANCELED = "message.route.canceled";
    public static final String MESSAGE_ROUTE_ACKNOWLEDGED = "message.route.acknowledged";
    public static final String MESSAGE_ROUTE_FYIED = "message.route.fyied";
    public static final String MESSAGE_NO_HELP_TEXT = "message.nohelp";
    public static final String MESSAGE_JV_CANCELLED_ROUTE = "message.journalVoucher.cancelledRoute";
    public static final String MESSAGE_REVERT_SUCCESSFUL = "message.revert.successful";
    public static final String MESSAGE_REVERT_UNNECESSARY = "message.revert.unnecessary";
    public static final String MESSAGE_DISAPPROVAL_NOTE_TEXT_INTRO = "message.disapprove.noteTextIntro";
    public static final String MESSAGE_NO_NRA_LINES_GENERATED = "message.dv.noNRALinesGenerated";

    public static final String QUESTION_CHANGE_JV_BAL_TYPE_FROM_SINGLE_AMT_TO_CREDIT_DEBIT_MODE = "document.journalVoucher.question.changeBalanceType.singleAmountToCreditDebitMode.text";
    public static final String QUESTION_CHANGE_JV_BAL_TYPE_FROM_SINGLE_AMT_TO_EXT_ENCUMB_CREDIT_DEBIT_MODE = "document.journalVoucher.question.changeBalanceType.singleAmountToCreditDebitMode.externalEncumbrance.text";
    public static final String QUESTION_CHANGE_JV_BAL_TYPE_FROM_CREDIT_DEBIT_TO_SINGLE_AMT_MODE = "document.journalVoucher.question.changeBalanceType.creditDebitToSingleAmountMode.text";
    public static final String QUESTION_CHANGE_JV_BAL_TYPE_FROM_EXT_ENCUMB_CREDIT_DEBIT_TO_SINGLE_AMT_MODE = "document.journalVoucher.question.changeBalanceType.creditDebitToSingleAmountMode.externalEncumbrance.text";
    public static final String QUESTION_CHANGE_JV_BAL_TYPE_FROM_EXT_ENCUMB_TO_NON_EXT_ENCUMB = "document.journalVoucher.question.changeBalanceType.externalEncumbranceToNonExternalEncumbrance.text";
    public static final String QUESTION_CHANGE_JV_BAL_TYPE_FROM_NON_EXT_ENCUMB_TO_EXT_ENCUMB = "document.journalVoucher.question.changeBalanceType.NonExternalEncumbranceToExternalEncumbrance.text";
    public static final String QUESTION_ROUTE_OUT_OF_BALANCE_JV_DOC = "document.journalVoucher.question.routeOutOfBalance.text";
    public static final String QUESTION_ROUTE_OUT_OF_BALANCE_JV_DOC_SINGLE_AMT_MODE = "document.journalVoucher.question.routeOutOfBalanceSingleAmountMode.text";

    public static final String QUESTION_DISAPPROVE_DOCUMENT = "document.question.disapprove.text";

    public static final String ERROR_MISSING = "error.missing";

    public static final String INSTITUTION_TAX_FEIN_NUMBER = "institution.tax.fein";


    // Calculated Salary Foundation Tracker Override errors
    public static final String ERROR_FISCAL_YEAR_NOT_CURRENT = "error.fiscalYear.notCurrentFiscalYear";
    public static final String ERROR_INVALID_APPOINTMENT = "error.invalid.appointment";
    public static final String ERROR_INVALID_POSITION = "error.invalid.position";

    // GL specific Error Codes
    public static final String ERROR_DEBIT_CREDIT_INDICATOR_NEITHER_D_NOR_C = "error.gl.DebitCreditIndicatorNeitherDNorC";
    public static final String ERROR_DEBIT_CREDIT_INDICATOR_MUST_BE_SPACE = "error.gl.DebitCreditIndicatorMustBeSpace";
    public static final String ERROR_CIRCULAR_DEPENDENCY_IN_CONTINUATION_ACCOUNT_LOGIC = "error.gl.CircularityInContinuationAccountLogic";
    public static final String ERROR_OFFSET_DEFINITION_NOT_FOUND = "error.gl.offsetDefinitionNotFound";
    public static final String ERROR_OFFSET_DEFINITION_OBJECT_CODE_NOT_FOUND = "error.gl.offsetDefinitionObjectCodeNotFound";
    public static final String ERROR_OFFSET_ACCOUNT_NOT_FOUND = "error.gl.offsetAccountNotFound";
    public static final String ERROR_DOCUMENT_TYPE_NOT_FOUND = "error.gl.DocumentTypeNotFound";
    public static final String ERROR_ORIGIN_CODE_NOT_FOUND = "error.gl.OriginCodeNotFound";
    public static final String ERROR_DOCUMENT_NUMBER_REQUIRED = "error.gl.DocumentNumberRequired";
    public static final String ERROR_CHART_NOT_FOUND = "error.gl.ChartNotFound";
    public static final String ERROR_CHART_NOT_ACTIVE = "error.gl.ChartNotActive";
    public static final String ERROR_ACCOUNT_NOT_FOUND = "error.gl.AccountNotFound";
    public static final String ERROR_ACCOUNT_CLOSED = "error.gl.AccountClosed";
    public static final String ERROR_ACCOUNT_EXPIRED = "error.gl.AccountExpired";
    public static final String ERROR_SUB_ACCOUNT_NOT_FOUND = "error.gl.SubAccountNotFound";
    public static final String ERROR_SUB_ACCOUNT_NOT_ACTIVE = "error.gl.SubAccountNotActive";
    public static final String ERROR_OBJECT_CODE_EMPTY = "error.gl.ObjectCodeEmpty";
    public static final String ERROR_OBJECT_CODE_NOT_FOUND = "error.gl.ObjectCodeNotFound";
    public static final String ERROR_OBJECT_CODE_NOT_ACTIVE = "error.gl.ObjectCodeNotActive";
    public static final String ERROR_OBJECT_TYPE_NOT_FOUND = "error.gl.ObjectTypeNotFound";
    public static final String ERROR_BALANCE_TYPE_NOT_FOUND = "error.gl.BalanceTypeNotFound";
    public static final String ERROR_FISCAL_PERIOD_CLOSED = "error.gl.FiscalPeriodClosed";
    public static final String ERROR_TRANS_CANNOT_BE_NEGATIVE_IF_OFFSET = "error.gl.TransCannotBeNegativeIfOffset";
    public static final String ERROR_DC_INDICATOR_MUST_BE_EMPTY = "error.gl.DCIndicatorMustBeEmpty";
    public static final String ERROR_DC_INDICATOR_MUST_BE_D_OR_C = "error.gl.DCIndicatorMustBeDorC";
    public static final String ERROR_DC_INDICATOR_MUST_BE_NEITHER_D_NOR_C = "error.gl.DCIndicatorMustBeNeitherDNorC";
    public static final String ERROR_FISCAL_YEAR_INVALID = "error.gl.FiscalYearInvalid";
    public static final String ERROR_TRANSACTION_DATE_INVALID = "error.gl.TransactionDateInvalid";

    public static final String ERROR_NUMBER_FORMAT_ORIGIN_ENTRY_FROM_TEXT_FILE = "error.gl.NumberFormatOriginEntryFromTextFile";

    public static final String ERROR_ENTERPRISE_FEEDER_RECONCILIATION_OR_LOADING_ERROR = "error.gl.EnterpriseFeederReconciliationOrLoadingError";

    public static final String ERROR_PROJECT_CODE_MUST_BE_ACTIVE = "error.gl.ProjectCodeMustBeActive";
    public static final String ERROR_REFERENCE_DOCUMENT_TYPE_NOT_FOUND = "error.gl.ReferenceDocumentTypeNotFound";
    public static final String ERROR_REFERENCE_ORIGINATION_CODE_NOT_FOUND = "error.gl.ReferenceOriginationCodeNotFound";
    public static final String ERROR_REFERENCE_DOC_NUMBER_CANNOT_BE_NULL_IF_UPDATE_CODE_IS_R = "error.gl.ReferenceDocNumberCannotBeNullIfUpdateCodeIsR";
    public static final String ERROR_REVERSAL_DATE_NOT_FOUND = "error.gl.ReversalDateNotFound";
    public static final String ERROR_ENC_UPDATE_CODE_NOT_DRN = "error.gl.EncUpdateCodeNotDRN";
    public static final String ERROR_SUB_FUND_GROUP_NOT_FOUND = "error.gl.SubFundGroupNotFound";
    public static final String ERROR_UNIV_DATE_NOT_FOUND = "error.gl.UniversityDateNotFound";
    public static final String ERROR_PROJECT_CODE_NOT_FOUND = "error.gl.ProjectCodeNotFound";
    public static final String ERROR_ACCOUNTING_PERIOD_NOT_FOUND = "error.gl.AccountingPeriodNotFound";
    public static final String ERROR_COST_SHARE_OBJECT_NOT_FOUND = "error.gl.CostShareNotFound";
    public static final String ERROR_ORIGIN_CODE_CANNOT_HAVE_CLOSED_ACCOUNT = "error.gl.OriginCodeCannotHaveClosedAccount";
    public static final String ERROR_INVALID_ORG_CODE_FOR_PLANT_FUND = "error.gl.InvalidOrgCodeForPlantFund";
    public static final String ERROR_CONTINUATION_ACCOUNT_LIMIT_REACHED = "error.gl.ContinuationAccountLimitReached";
    public static final String ERROR_NO_OBJECT_FOR_OBJECT_ON_OFSD = "error.gl.NoObjectForObjectOnOFSD";
    public static final String ERROR_CONTINUATION_ACCOUNT_NOT_FOUND = "error.gl.ContinuationAccountNotFound";
    public static final String ERROR_CONTINUATION_CHART_NOT_FOUND = "error.gl.ContinuationChartNotFound";
    public static final String ERROR_UNIV_FISCAL_YR_NOT_FOUND = "error.gl.UniversityFiscalYearNotFound";
    public static final String ERROR_ACCOUNTING_PERIOD_CLOSED = "error.gl.AccountingPeriodClosed";
    public static final String ERROR_INVALID_SF_OBJECT_TYPE_CODE = "error.gl.InvalidSufficentFundObjectTypeCode";
    public static final String ERROR_BALANCE_NOT_FOUND_FOR = "error.gl.BalanceNotFoundFor";
    public static final String ERROR_INVALID_ACCOUNT_SF_CODE_FOR = "error.gl.InvalidAccountSufficentFundsCodeFor";
    public static final String ERROR_REF_DOC_NOT_BE_SPACE = "error.gl.RefDocNumNotBeSpace";
    public static final String ERROR_UNIV_DATE_NOT_IN_ACCOUNTING_PERIOD_TABLE = "error.gl.UniversityDateNotInAccountingPeriodTable";
    public static final String ERROR_REVERSAL_DATE_NOT_IN_UNIV_DATE_TABLE = "error.gl.ReversalDateNotInUniversityDateTable";
    public static final String ERROR_OBJECT_CODE_NOT_FOUND_FOR = "error.gl.ObjectCodeNotFoundFor";
    public static final String ERROR_INVALID_OFFSET_OBJECT_CODE = "error.gl.InvalidOffsetObjectCode";
    public static final String ERROR_DEDIT_CREDIT_CODE_NOT_BE_NULL = "error.gl.NullDebitCreditIndicator";
    public static final String ERROR_SUB_ACCOUNT_NOT_BE_NULL = "error.gl.NullSubAccount";
    public static final String ERROR_OBJECT_CODE_NOT_BE_NULL = "error.gl.NullObjectCode";
    public static final String ERROR_SUB_OBJECT_CODE_NOT_BE_NULL = "error.gl.NullSubObjectCode";
    public static final String ERROR_FISCAL_PERIOD_CODE_NOT_BE_NULL = "error.gl.NullFiscalPeriodCode";
    public static final String ERROR_DOCUMENT_TYPE_NOT_BE_NULL = "error.gl.NullDocumentType";
    public static final String ERROR_ORIGIN_CODE_NOT_BE_NULL = "error.gl.NullOriginCode";
    public static final String ERROR_DOCUMENT_NUMBER_NOT_BE_NULL = "error.gl.NullDocumentNumber";
    public static final String ERROR_SEQUENCE_NUMBER_NOT_BE_NULL = "error.gl.NullSequenceNumber";
    public static final String ERROR_NO_ORIGIN_ENTRY_GROUPS = "error.gl.NoOriginEntryGroups";
    public static final String ERROR_EMPLID_NOT_BE_NULL = "error.labor.NullEmplId";


    public static final String MSG_DEDIT_CREDIT_CODE_MUST_BE = "message.gl.DebitCreditCodeMustBe";
    public static final String MSG_FOR_BALANCE_TYPE = "message.gl.ForBalanceType";
    public static final String MSG_GENERATED_CAPITALIZATION = "message.gl.GeneratedCapitalization";
    public static final String MSG_GENERATED_LIABILITY = "message.gl.GeneratedLiability";
    public static final String MSG_GENERATED_OFFSET = "message.gl.GeneratedOffset";
    public static final String MSG_GENERATED_COST_SHARE = "message.gl.GeneratedCostShareDescription";
    public static final String MSG_GENERATED_TRANSFER = "message.gl.GeneratedTransfer";
    public static final String MSG_CLOSE_ENTRY_TO_NOMINAL_BUDGET = "message.gl.ClosingEntryToNB";
    public static final String MSG_CLOSE_ENTRY_TO_NOMINAL_REVENUE = "message.gl.ClosingEntryToNR";
    public static final String MSG_CLOSE_ENTRY_TO_NOMINAL_EXPENSE = "message.gl.ClosingEntryToNE";
    public static final String MSG_CLOSE_ENTRY_TO_FUND_BALANCE = "message.gl.ClosingEntryToFB";
    public static final String MSG_AUTO_FORWARD = "message.gl.AutoForward";
    public static final String MSG_ACCOUNT_CLOSED_TO = "message.gl.AccountClosedTo";

    // Application Parameter Component errors
    public static final String ERROR_APPLICATION_PARAMETERS_ALLOWED_RESTRICTION = "error.applicationParametersAllowedRestriction";
    public static final String ERROR_APPLICATION_PARAMETERS_DENIED_RESTRICTION = "error.applicationParametersDeniedRestriction";
    public static final String ERROR_PAYMENT_REASON_ALLOWED_RESTRICTION = "error.paymentReasonAllowedRestriction";
    public static final String ERROR_PAYMENT_REASON_DENIED_RESTRICTION = "error.paymentReasonDeniedRestriction";
    public static final String ERROR_APC_INDIRECT_DENIED_MULTIPLE = "error.apc.indirectDeniedMultiple";
    public static final String ERROR_APC_INDIRECT_ALLOWED_MULTIPLE = "error.apc.indirectAllowedMultiple";

    // Credit Card Vendor errors
    public static final String ERROR_CCV_INCOME_SUBACCOUNT_REQUIRED = "error.document.ccv.incomeSubAccount.required";
    public static final String ERROR_CCV_EXPENSE_SUBACCOUNT_REQUIRED = "error.document.ccv.expenseSubAccount.required";
    public static final String ERROR_CCV_INVALIDSUBACCOUNT = "error.document.ccv.invalidSubAccount";
    public static final String ERROR_CCV_INCOME_SUBOBJ_REQUIRED = "error.document.ccv.incomeSubOBJ.required";
    public static final String ERROR_CCV_EXPENSE_SUBOBJ_REQUIRED = "error.document.ccv.expenseSubOBJ.required";
    public static final String ERROR_CCV_INVALIDSUBOBJECT = "error.document.ccv.invalidSubObject";

    public static final String ERROR_GL_ERROR_CORRECTION_SYSTEMFIELD_REQUIRED = "error.gl.correction.systemfield.required";
    public static final String ERROR_GL_ERROR_CORRECTION_EDITMETHODFIELD_REQUIRED = "error.gl.correction.editmethodfield.required";
    public static final String ERROR_GL_ERROR_CORRECTION_SEARCHFIELD_REQUIRED = "error.gl.correction.searchfield.required";
    public static final String ERROR_GL_ERROR_CORRECTION_MODIFYFIELD_REQUIRED = "error.gl.correction.modifyfield.required";
    public static final String ERROR_GL_ERROR_CORRECTION_ORIGINGROUP_REQUIRED = "error.gl.correction.Origingroup.required";
    public static final String ERROR_GL_ERROR_CORRECTION_ORIGINGROUP_REQUIRED_FOR_ROUTING = "error.gl.correction.Origingroup.required.for.route";
    public static final String ERROR_GL_ERROR_CORRECTION_NO_RECORDS = "error.gl.correction.norecords";
    public static final String ERROR_GL_ERROR_CORRECTION_INVALID_VALUE = "error.gl.correction.invalid.value";
    public static final String ERROR_GL_ERROR_CORRECTION_UNABLE_TO_MANUAL_EDIT_LARGE_GROUP = "error.gl.correction.unable.to.manual.edit.large.group";
    public static final String ERROR_GL_ERROR_CORRECTION_UNABLE_TO_MANUAL_EDIT_ANY_GROUP = "error.gl.correction.unable.to.manual.edit.any.group";
    public static final String ERROR_GL_ERROR_CORRECTION_INVALID_SYSTEM_OR_EDIT_METHOD_CHANGE = "error.gl.correction.invalid.system.or.edit.method.change";
    public static final String ERROR_GL_ERROR_CORRECTION_INVALID_INPUT_GROUP_CHANGE = "error.gl.correction.invalid.input.group.change";
    public static final String ERROR_GL_ERROR_CORRECTION_MUST_CHOOSE_FIELD_NAME_WHEN_ADDING_CRITERION = "error.gl.correction.must.choose.field.name.when.adding.criterion";
    public static final String ERROR_GL_ERROR_CORRECTION_CRITERIA_TO_ADD_MUST_BE_BLANK_FOR_SAVE = "error.gl.correction.criteria.to.add.must.be.blank.for.save";
    public static final String ERROR_GL_ERROR_CORRECTION_PERSISTED_ORIGIN_ENTRIES_MISSING = "error.gl.correction.persisted.origin.entries.missing";
    public static final String ERROR_GL_ERROR_CORRECTION_REMOVE_GROUP_REQUIRES_DATABASE = "error.gl.correction.remove.group.requires.database";
    public static final String GL_ERROR_CORRECTION_RESTRICTED_FUNCTIONALITY_SEARCH_RESULTS_LABEL = "gl.correction.restricted.functionality.search.results.label";

    public static final String ERROR_GL_ERROR_GROUP_ALREADY_MARKED_NO_PROCESS = "error.gl.correction.already.marked.no.process";

    public static final String ERROR_DOCUMENT_CHART_MANAGER_MUST_EXIST = "error.document.chart.chartManagerMustExist";
    public static final String ERROR_DOCUMENT_CHART_MANAGER_MUST_BE_KUALI_USER = "error.document.chart.chartManagerMustBeKualiUser";
    public static final String ERROR_DOCUMENT_ACCOUNT_FISCAL_OFFICER_MUST_EXIST = "error.document.account.accountFiscalOfficerMustExist";
    public static final String ERROR_DOCUMENT_ACCOUNT_FISCAL_OFFICER_MUST_BE_KUALI_USER = "error.document.account.accountFiscalOfficerMustBeKualiUser";

    // Contracts and Grants Maintenance error messages
    public static final String ERROR_AGENCY_NOT_FOUND = "error.cg.agencyNotFound";
    public static final String ERROR_AGENCY_TYPE_NOT_FOUND = "error.cg.agencyTypeNotFound";
    public static final String ERROR_AGENCY_INACTIVE = "error.cg.agenyInactive";
    public static final String ERROR_AGENCY_CIRCULAR_REPORTING = "error.cg.agencyCircularReporting";
    public static final String ERROR_AGENCY_REPORTS_TO_SELF = "error.cg.agencyReportsToSelf";
    public static final String ERROR_MULTIPLE_PRIMARY = "error.cg.multiplePrimary";
    public static final String ERROR_NO_PRIMARY = "error.cg.noPrimary";
    public static final String ERROR_COUNTRY_CODE_INVALID = "error.cg.invalidCountryCode";
    public static final String ERROR_STATE_CODE_INVALID = "error.cg.invalidStateCode";
    public static final String ERROR_ENDING_DATE_NOT_AFTER_BEGIN = "error.cg.endingDateNotAfterBegin";
    public static final String ERROR_ONE_REQUIRED = "error.cg.oneRequired";
    public static final String ERROR_AWARD_PROPOSAL_AWARDED = "error.cg.award.proposal.awarded";
    public static final String ERROR_AWARD_PROPOSAL_INACTIVE = "error.cg.award.proposal.inactive";
    public static final String ERROR_AGENCY_EQUALS_FEDERAL_PASS_THROUGH_AGENCY = "error.cg.agencyEqualsFederalPassThroughAgency";
    public static final String ERROR_FEDERAL_PASS_THROUGH_AGENCY_EQUALS_AGENCY = "error.cg.federalPassThroughAgencyEqualsAgency";
    public static final String ERROR_FPT_AGENCY_NUMBER_REQUIRED = "error.cg.federalPassThroughAgencyIndicatorIsChecked";
    public static final String ERROR_FPT_AGENCY_NUMBER_NOT_BLANK = "error.cg.federalPassThroughAgencyNumberIsNotBlank";
    public static final String ERROR_PRIMARY_AGENCY_IS_FEDERAL_AND_FPT_INDICATOR_IS_CHECKED = "error.cg.primaryAgencyIsFederalAndFPTIndicatorIsChecked";
    public static final String ERROR_PRIMARY_AGENCY_IS_FEDERAL_AND_FPT_AGENCY_IS_NOT_BLANK = "error.cg.primaryAgencyIsFederalAndFPTAgencyNumberIsNotBlank";
    public static final String ERROR_GRANT_DESCRIPTION_INVALID_WITH_FED_PASS_THROUGH_AGENCY_INDICATOR_SELECTED = "error.cg.invalidGrantDescription";

    public static final String ERROR_INVALID_PROJECT_DIRECTOR_STATUS = "error.cg.invalidProjectDirectorStatus";
    public static final String ERROR_PROPOSAL_SUBCONTRACTOR_NUMBER_REQUIRED_FOR_ADD = "error.cg.proposal.subcontractorNumberRequiredForAdd";


    public static final String WARNING_AWARD_ENTRY_BEFORE_START_DATE = "warning.cg.award.entryDateBeforeStartDate";
    public static final String WARNING_AWARD_SUBCONTRACTOR_TOTAL_GREATER_THAN_AWARD_TOTAL = "warning.cg.award.subcontractorAmountGreaterThanAwardAmount";

    public static final Object WARNING_LINE_IMPORT_LENGTH_MISMATCH = "warning.core.bo.AccountImportLengthMismatch";

    // batch upload
    public static final String MESSAGE_BATCH_UPLOAD_TITLE_PCDO = "message.batchUpload.title.procurementCard";
    public static final String MESSAGE_BATCH_UPLOAD_TITLE_COLLECTOR = "message.batchUpload.title.collector";
    public static final String MESSAGE_BATCH_UPLOAD_TITLE_ENTERPRISE_FEEDER = "message.batchUpload.title.enterprise.feeder";
    public static final String MESSAGE_BATCH_UPLOAD_SAVE_SUCCESSFUL = "message.batchUpload.saveSuccessful";
    public static final String MESSAGE_BATCH_UPLOAD_SAVE_SUCCESSFUL_WITH_FILE_INFO = "message.batchUpload.saveSuccessful.with.file.info";
    public static final String MESSAGE_BATCH_UPLOAD_DELETE_SUCCESSFUL = "message.batchUpload.deleteSuccessful";
    public static final String ERROR_BATCH_UPLOAD_DELETE_FAILED_FILE_ALREADY_PROCESSED = "error.batchUpload.deleteFailed.fileAlreadyProcessed";
    public static final String ERROR_BATCH_UPLOAD_DELETE_FAILED_FILE_SET_ALREADY_PROCESSED = "error.batchUpload.deleteFailed.fileSetAlreadyProcessed";
    public static final String ERROR_BATCH_UPLOAD_DELETE_FAILED_NOT_AUTHORIZED = "error.batchUpload.deleteFailed.fileNotAuthorized";
    public static final String ERROR_BATCH_UPLOAD_DELETE_FAILED_FILE_SET_NOT_AUTHORIZED = "error.batchUpload.deleteFailed.fileSetNotAuthorized";
    public static final String ERROR_BATCH_UPLOAD_PARSING_XML = "error.batchUpload.xmlParse";
    public static final String ERROR_BATCH_UPLOAD_SAVE = "error.batchUpload.save";
    public static final String ERROR_BATCH_UPLOAD_DELETE = "error.batchUpload.delete";
    public static final String ERROR_BATCH_UPLOAD_DOWNLOAD = "error.batchUpload.download";
    public static final String ERROR_BATCH_UPLOAD_NO_FILE_SELECTED_SAVE = "error.batchUpload.save.noFileSelected";
    public static final String ERROR_BATCH_UPLOAD_NO_FILE_SET_IDENTIFIER_SELECTED = "error.batchUpload.save.noFileSetIdentifier";
    public static final String ERROR_BATCH_UPLOAD_NO_FILE_SELECTED_SAVE_FOR_FILE_TYPE = "error.batchUpload.save.noFileSelected.for.file.type";
    public static final String ERROR_BATCH_UPLOAD_FILE_SAVE_ERROR = "error.batchUpload.save.fileSaveError";
    public static final String ERROR_BATCH_UPLOAD_FILE_VALIDATION_ERROR = "error.batchUpload.validation.fileSaveError";
    public static final String ERROR_BATCH_UPLOAD_NO_FILE_SELECTED_DELETE = "error.batchUpload.delete.noFileSelected";
    public static final String ERROR_BATCH_UPLOAD_NO_FILE_SELECTED_DOWNLOAD = "error.batchUpload.download.noFileSelected";
    public static final String ERROR_BATCH_UPLOAD_NO_FILE_TYPE_SELECTED_DOWNLOAD = "error.batchUpload.download.noFileTypeSelected";
    public static final String ERROR_BATCH_UPLOAD_FILE_USER_IDENTIFIER_BAD_FORMAT = "error.batchUpload.save.fileUserIdentifierBadFormat";
    public static final String ERROR_BATCH_UPLOAD_FILE_SET_IDENTIFIER_BAD_FORMAT = "error.batchUpload.save.fileSetIdentifierBadFormat";

    public static final class AdvanceDeposit {
        public static final String ERROR_DOCUMENT_ADVANCE_DEPOSIT_TOTAL_INVALID = "error.document.advanceDeposit.depositTotalInvalid";
        public static final String ERROR_DOCUMENT_ADVANCE_DEPOSIT_REQ_NUMBER_DEPOSITS_NOT_MET = "error.document.advanceDeposit.requiredNumberOfAdvanceDepositsNotMet";
        public static final String ERROR_DOCUMENT_ADVANCE_DEPOSIT_OUT_OF_BALANCE = "error.document.advanceDeposit.balance";
        public static final String ERROR_DOCUMENT_ADVANCE_DEPOSIT_ZERO_AMOUNT = "error.document.advanceDeposit.zeroAmount";

        public static final String DESCRIPTION_GLPE_BANK_OFFSET = "description.glpe.bankOffset.advanceDeposit";
    }

    public static final class budget{
        public static final String MSG_REPORT_ACCOUNT_LIST = "message.budget.report.accountList";
        public static final String MSG_ACCOUNT_LIST = "message.budget.accountList";
    }
    
    public static final class Check {
        public static final String ERROR_CHECK_DELETERULE = "error.check.deleteRule";
    }

    public static final class CashDrawer {
        public static final String CASH_DRAWER_STATUS_OPEN = "cashDrawer.status.open";
        public static final String CASH_DRAWER_STATUS_CLOSED = "cashDrawer.status.closed";
        public static final String CASH_DRAWER_STATUS_LOCKED = "cashDrawer.status.locked";

        public static final String MSG_CASH_DRAWER_ALREADY_OPEN = "message.cashDrawer.alreadyOpen";
    }

    public static final class CashReceipt {
        public static final String MSG_CHECK_ENTRY_TOTAL = "message.cashReceipt.changeCheckEntryMode.total";
        public static final String MSG_CHECK_ENTRY_INDIVIDUAL = "message.cashReceipt.changeCheckEntryMode.individual";
        public static final String ERROR_DOCUMENT_CASH_RECEIPT_INVALID_OBJECT_TYPE_CODE_FOR_OBJECT_CODE = "error.document.cashReceipt.invalidObjectTypeCodeForObjectCode";
        public static final String ERROR_DOCUMENT_CASH_RECEIPT_INVALID_CONSOLIDATED_OBJECT_CODE = "error.document.cashReceipt.invalidConsolidatedObjectCode";
        public static final String ERROR_DOCUMENT_CASH_RECEIPT_INVALID_OBJECT_SUB_TYPE_CODE = "error.document.cashReceipt.invalidObjectSubTypeCode";
        public static final String ERROR_DOCUMENT_CASH_RECEIPT_BALANCE = "error.document.cashReceipt.balance";
        public static final String ERROR_DOCUMENT_CASH_RECEIPT_NO_CASH_RECONCILIATION_TOTAL = "error.document.cashReceipt.noCashReconciliationTotal";
        public static final String MSG_VERIFIED_BUT_NOT_AWAITING_DEPOSIT = "message.document.cashReceipt.verifiedButNotAwaitingDeposit";
        public static final String MSG_VERIFIED_AND_AWAITING_DEPOSIT = "message.document.cashReceipt.verifiedAndAwaitingDeposit";
        public static final String MSG_VERIFIED_AND_DEPOSITED = "message.document.cashReceipt.verifiedAndDeposited";
        public static final String MSG_CASH_DRAWER_CLOSED_VERIFICATION_NOT_ALLOWED = "message.document.cashReceipt.cashDrawerClosedVerificationNotAllowed";
        public static final String ERROR_NEGATIVE_RECONCILIATION_AMOUNT = "error.document.cashReceipt.negativeReconciliationAmount";
        public static final String ERROR_NEGATIVE_CHECK_AMOUNT = "error.document.cashReceipt.negativeCheckAmount";
        public static final String ERROR_ZERO_CHECK_AMOUNT = "error.document.cashReceipt.zeroCheckAmount";

        public static final String ERROR_EXCESSIVE_TOTAL = "error.document.cashReceipt.invalidTotal";
        public static final String ERROR_NEGATIVE_TOTAL = "error.document.cashReceipt.negativeTotal";
        public static final String ERROR_ZERO_TOTAL = "error.document.cashReceipt.zeroTotal";
    }

    public static final class CreditCardReceipt {
        public static final String ERROR_DOCUMENT_CREDIT_CARD_RECEIPT_TOTAL_INVALID = "error.document.creditCardReceipt.creditCardReceiptTotalInvalid";
        public static final String ERROR_DOCUMENT_CREDIT_CARD_RECEIPT_REQ_NUMBER_RECEIPTS_NOT_MET = "error.document.creditCardReceipt.requiredNumberOfCreditCardReceiptsNotMet";
        public static final String ERROR_DOCUMENT_CREDIT_CARD_RECEIPT_OUT_OF_BALANCE = "error.document.creditCardReceipt.balance";
        public static final String ERROR_DOCUMENT_CREDIT_CARD_BANK_MUST_EXIST_WHEN_FLEXIBLE = "error.document.creditCardReceipt.bankMustExistWhenFlexible";

        public static final String DESCRIPTION_GLPE_BANK_OFFSET = "description.glpe.bankOffset.creditCardReceipt";
    }

    public static final class Deposit {
        public static final String DEPOSIT_TYPE_INTERIM = "deposit.type.interim";
        public static final String DEPOSIT_TYPE_FINAL = "deposit.type.final";

        public static final String ERROR_NO_CASH_RECEIPTS_SELECTED = "error.deposit.noCashReceiptsSelected";
        public static final String ERROR_CASH_RECEIPT_NOT_VERIFIED = "error.deposit.cashReceiptNotVerified";

        public static final String ERROR_MISSING_BANK = "error.deposit.missingBank";
        public static final String ERROR_UNKNOWN_BANK = "error.deposit.unknownBank";

        public static final String ERROR_MISSING_BANKACCOUNT = "error.deposit.missingBankAccount";
        public static final String ERROR_UNKNOWN_BANKACCOUNT = "error.deposit.unknownBankAccount";

        public static final String ERROR_NON_DEPOSITED_VERIFIED_CASH_RECEIPT = "error.deposit.nonDepositedVerifiedCashReceipt";
        public static final String ERROR_CASH_DEPOSIT_DID_NOT_BALANCE = "error.deposit.cashDepositDidNotBalance";
        public static final String ERROR_NOT_ENOUGH_CASH_TO_COMPLETE_DEPOSIT = "error.deposit.notEnoughCashToCompleteDeposit";
        public static final String ERROR_CASHIERING_CHECK_MUST_BE_DEPOSITED = "error.deposit.cashieringChecksMustBeDeposited";
    }

    public static final class CashManagement {
        public static final String URL_LOAD_DOCUMENT_CASH_MGMT = "url.load.document.cashManagement";

        public static final String DEFAULT_DOCUMENT_DESCRIPTION = "cashManagement.document.defaultDescription";
        public static final String STATUS_DEPOSIT_CANCELED = "cashManagement.document.status.depositCanceled";

        public static final String DESCRIPTION_GLPE_BANK_OFFSET_INTERIM = "description.glpe.bankOffset.cashManagement.interim";
        public static final String DESCRIPTION_GLPE_BANK_OFFSET_FINAL = "description.glpe.bankOffset.cashManagement.final";

        public static final String ERROR_NON_DEPOSITED_VERIFIED_CASH_RECEIPT = "error.cashManagement.nonDepositedVerifiedCashReceipt";
        public static final String ERROR_DOCUMENT_ALREADY_HAS_FINAL_DEPOSIT = "error.cashManagement.documentAlreadyHasFinalDeposit";
        public static final String ERROR_DOCUMENT_NO_DEPOSITS_TO_MAKE_FINAL = "error.cashManagement.documentHasNoDepositsToMakeFinal";
        public static final String ERROR_NON_DEPOSITED_VERIFIED_CASH_RECEIPTS = "error.cashManagement.nonDepositedVerifiedCashReceips";
        public static final String ERROR_DOCUMENT_CASHIERING_TRANSACTION_AMOUNT_PAID_BACK_EXCEEDS_AMOUNT_LEFT = "error.document.cashieringTransaction.openItemInProcess.amountPaidBackExceedsAmountLeft";
        public static final String ERROR_DOCUMENT_CASHIERING_TRANSACTION_AMOUNT_EXCEEDS_DRAWER = "error.document.cashieringTransaction.newItemInProcess.amountExceedsDrawer";
        public static final String ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE = "error.document.cashieringTransaction.cashCountCannotBeNegative";
        public static final String ERROR_DOCUMENT_CASHIERING_TRANSACTION_CHECK_AMOUNT_NOT_NEGATIVE = "error.document.cashieringTransaction.checkAmountCannotBeNegative";
        public static final String ERROR_DOCUMENT_CASHIERING_TRANSACTION_NEW_ITEM_IN_PROCESS_NOT_NEGATIVE = "error.document.cashieringTransaction.newItemInProcess.amountCannotBeNegative";
        public static final String ERROR_DOCUMENT_CASHIERING_TRANSACTION_REDUCED_ITEM_IN_PROCESS_NOT_NEGATIVE = "error.document.cashieringTransaction.openItemInProcess.reducedAmountCannotBeNegative";
        public static final String ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_COUNT_EXCEEDS_DRAWER = "error.document.cashieringTransaction.cashAmountExceedsDrawerAmount";
        public static final String ERROR_DOCUMENT_CASHIERING_TRANSACTION_IN_OUT_DO_NOT_BALANCE = "error.document.cashieringTransaction.moneyInAndMoneyOutDoNotBalance";
        public static final String ERROR_DOCUMENT_CASHIERING_TRANSACTION_CANNOT_PAY_OFF_ADVANCE_WITH_ADVANCE = "error.document.cashieringTransaction.cannotPayOffAdvanceWithAdvance";
        public static final String ERROR_NEW_ITEM_IN_PROCESS_IN_FUTURE = "error.document.cashieringTransaction.newItemInProcessInFuture";

        public static final String ERROR_CASH_DRAWER_CORRECTION_NEGATIVE_AMOUNT = "error.cashDrawerCorrection.negativeAmount";


    }

    public static final class GeneralErrorCorrection {
        public static final String ERROR_DOCUMENT_GENERAL_ERROR_CORRECTION_INVALID_OBJECT_TYPE_CODE_FOR_OBJECT_CODE = "error.document.generalErrorCorrection.invalidObjectTypeCodeForObjectCode";
        public static final String ERROR_DOCUMENT_GENERAL_ERROR_CORRECTION_INVALID_OBJECT_TYPE_CODE_WITH_SUB_TYPE_CODE = "error.document.generalErrorCorrection.invalidObjectTypeCodeWithSubTypeCode";
        public static final String ERROR_DOCUMENT_GENERAL_ERROR_CORRECTION_INVALID_OBJECT_SUB_TYPE_CODE = "error.document.generalErrorCorrection.invalidObjectSubTypeCode";
    }

    public static final class JournalVoucher {
        public static final String ERROR_NEGATIVE_NON_BUDGET_AMOUNTS = "error.document.journalVoucher.negativeNonBudgetAmounts";
    }

    public static final class NonCheckDisbursement {
        public static final String ERROR_DOCUMENT_NON_CHECK_DISBURSEMENT_INVALID_OBJECT_TYPE_CODE_FOR_OBJECT_CODE = "error.document.nonCheckDisbursement.invalidObjectTypeCodeForObjectCode";
        public static final String ERROR_DOCUMENT_NON_CHECK_DISBURSEMENT_INVALID_OBJECT_SUB_TYPE_CODE = "error.document.nonCheckDisbursement.invalidObjectSubTypeCode";
        public static final String ERROR_DOCUMENT_NON_CHECK_DISBURSEMENT_INVALID_SUB_FUND_GROUP = "error.document.nonCheckDisbursement.invalidSubFundGroup";
        public static final String ERROR_DOCUMENT_NON_CHECK_DISBURSEMENT_INVALID_CONSOLIDATION_CODE = "error.document.nonCheckDisbursement.invalidConsolidationCode";
    }

    public static final class AuxiliaryVoucher {
        public static final String ERROR_DOCUMENT_AUXILIARY_VOUCHER_INVALID_OBJECT_SUB_TYPE_CODE = "error.document.auxiliaryVoucher.invalidObjectSubTypeCode";
        public static final String ERROR_ACCOUNTING_PERIOD_OUT_OF_RANGE = "error.document.auxiliaryVoucher.accountingPeriodOutOfRange";
        public static final String ERROR_DIFFERENT_CHARTS = "error.document.auxiliaryVoucher.differentCharts";
        public static final String ERROR_DIFFERENT_SUB_FUND_GROUPS = "error.document.auxiliaryVoucher.differentSubFundGroups";
        public static final String ERROR_INVALID_ACCRUAL_REVERSAL_DATE = "error.document.auxiliaryVoucher.invalidAccrualReversalDate";
    }

    public static final class SufficientFunds {
        public static final String ERROR_INSUFFICIENT_FUNDS = "error.account.insufficientFunds";
    }

    public static final class DistributionOfIncomeAndExpense {
        public static final String ERROR_DOCUMENT_DI_INVALID_OBJECT_TYPE_CODE = "error.document.DistributionOfIncomeAndExpense.invalidObjectTypeCode";
        public static final String ERROR_DOCUMENT_DI_INVALID_OBJ_SUB_TYPE = "error.document.DistributionOfIncomeAndExpense.ObjSubType";
    }

    public static final class IndirectCostAdjustment {
        public static final String ERROR_DOCUMENT_ICA_INVALID_OBJECT_TYPE_CODE = "error.document.IndirectCostAdjustment.invalidObjectTypeCode";
        public static final String ERROR_DOCUMENT_ICA_INVALID_OBJ_SUB_TYPE = "error.document.IndirectCostAdjustment.ObjSubType";
        public static final String ERROR_DOCUMENT_ICA_GRANT_INVALID_CHART_OF_ACCOUNTS = "error.document.IndirectCostAdjustment.grant.invalidChartOfAccount";
        public static final String ERROR_DOCUMENT_ICA_RECEIPT_INVALID_CHART_OF_ACCOUNTS = "error.document.IndirectCostAdjustment.receipt.invalidChartOfAccount";
        public static final String ERROR_DOCUMENT_ICA_GRANT_INVALID_ACCOUNT = "error.document.IndirectCostAdjustment.receipt.invalidAccount";
    }

    public static final class IndirectCostRecovery {
        public static final String ERROR_DOCUMENT_ICR_INVALID_WILDCARD = "error.document.IndirectCostRecovery.invalidWildcard";
        public static final String ERROR_DOCUMENT_ICR_WILDCARDS_MUST_MATCH = "error.document.IndirectCostRecovery.wildcardsMustMatch";
        public static final String ERROR_DOCUMENT_ICR_INVALID_DASH_STRING = "error.document.IndirectCostRecovery.invalidDashString";
        public static final String ERROR_DOCUMENT_ICR_FIELD_MUST_NOT_BE_WILDCARD = "error.document.IndirectCostRecovery.fieldMustNotBeWildcard";
    }

    public static final class AccountingLineParser {
        public static final String ERROR_INVALID_FILE_FORMAT = "error.accountingLineParser.invalidFileFormat";
        public static final String ERROR_TOO_FEW_VALUES = "error.accountingLineParser.values.tooFew";
        public static final String ERROR_TOO_MANY_VALUES = "error.accountingLineParser.values.tooMany";
        public static final String ERROR_INVALID_PROPERTY_VALUE = "error.accountingLineParser.invalidPropertyValue";
        public static final String ERROR_TOO_MANY_AMOUNTS = "error.accountingLineParser.tooManyAmounts";
    }

    public static final class AccountBalanceService {
        public static final String INCOME = "account.balance.service.income";
        public static final String INCOME_FROM_TRANSFERS = "account.balance.service.income.from.transfers";
        public static final String INCOME_TOTAL = "account.balance.service.total.income";
        public static final String EXPENSE = "account.balance.service.expense";
        public static final String EXPENSE_FROM_TRANSFERS = "account.balance.service.expense.from.transfers";
        public static final String EXPENSE_TOTAL = "account.balance.service.total.expense";
        public static final String TOTAL = "account.balance.service.total";
    }

    public static final class Collector {
        public static final String TRAILER_ERROR_COUNTNOMATCH = "error.collector.countNoMatch";
        public static final String TRAILER_ERROR_AMOUNTNOMATCH1 = "error.collector.amountNoMatch1";
        public static final String TRAILER_ERROR_AMOUNTNOMATCH2 = "error.collector.amountNoMatch2";
        public static final String TRAILER_ERROR_AMOUNT_SHOULD_BE_ZERO = "error.collector.amountShouldBeZero";
        public static final String MIXED_DOCUMENT_TYPES = "error.collector.mixedDocumentTypes";
        public static final String MIXED_BALANCE_TYPES = "error.collector.mixedBalanceTypes";
        public static final String DUPLICATE_BATCH_HEADER = "error.collector.duplicateHeader";
        public static final String NONMATCHING_DETAIL_KEY = "error.collector.nonmatchingDetailKey";
        public static final String EMAIL_ADDRESS_NOT_FOUND = "error.collector.emailAddressNotFound";
        public static final String NOTIFICATION_EMAIL_SENT = "collector.notificationEmailSent";
        public static final String EMAIL_SEND_ERROR = "error.collector.emailSendError";
    }

    public static final class OrganizationReversionProcess {
        public static final String CASH_REVERTED_TO = "cash.reverted";
        public static final String FUND_BALANCE_REVERTED_TO = "fund.balance.reverted";
        public static final String CASH_REVERTED_FROM = "cash.reverted.from";
        public static final String FUND_BALANCE_REVERTED_FROM = "fund.balance.reverted.from";
        public static final String FUND_REVERTED_TO = "fund.reverted.to";
        public static final String FUND_CARRIED = "fund.carried";
        public static final String FUND_REVERTED_FROM = "fund.reverted.from";
    }

    public static final class PendingEntryLookupableImpl {
        public static final String FISCAL_YEAR_FOUR_DIGIT = "fiscal.year.four.digit";
    }

    public static final class ObjectCode {
        public static final String QUESTION_INACTIVE_OBJECT_LEVEL_CONFIRMATION = "document.question.allowInactiveObjectLevel.text";
    }

    public static final class ContractsAndGrants {
        public static final String USER_INITIATED_DATE_TOO_EARLY = "error.cg.userInitiatedDateTooEarly";
        public static final String CLOSE_ON_OR_BEFORE_DATE_TOO_EARLY = "error.cg.closeOnOrBeforeDateTooEarly";
        public static final String CLOSE_ON_OR_BEFORE_DATE_TOO_LATE = "error.cg.closeOnOrBeforeDateTooLate";
    }
    
}
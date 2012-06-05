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
package org.kuali.kfs.sys;


/**
 * Holds error key constants.
 */
public class KFSKeyConstants {
    public static final String CONTINUE_QUESTION = "document.question.continue.text";
    public static final String WARNING_NOT_SAME_OBJECT_SUB_TYPES = "warning.capital.object.subtype.not.the.same";
    public static final String ERROR_EXISTENCE = "error.existence";
    public static final String ERROR_EXPIRED = "error.expired";
    public static final String ERROR_INACTIVE = "error.inactive";
    public static final String ERROR_CLOSED = "error.closed";
    public static final String ERROR_REQUIRED = "error.required";
    public static final String ERROR_REQUIRED_FOR_US = "error.requiredForUs";
    public static final String ERROR_DATE_TIME = "error.invalidDateTime";
    public final static String ERROR_NUMERIC = "error.numeric";
    public static final String ERROR_MIN_LENGTH = "error.minLength";
    public static final String ERROR_MAX_LENGTH = "error.maxLength";
    public static final String ERROR_INVALID_FORMAT = "error.invalidFormat";
    public static final String ERROR_USER_MISSING_PERMISSION = "error.user.missing.permission";

    public static final String AUTHORIZATION_ERROR_GENERAL = "error.authorization.general";
    public static final String AUTHORIZATION_ERROR_INACTIVE_DOCTYPE = "error.authorization.inactiveDocumentType";
    public static final String AUTHORIZATION_ERROR_DOCTYPE = "error.authorization.documentType";
    public static final String AUTHORIZATION_ERROR_DOCUMENT = "error.authorization.document";
    public static final String AUTHORIZATION_ERROR_MAINTENANCE_NEWCOPY = "error.authorization.maintenance.newCopy";
    public static final String AUTHORIZATION_ERROR_MODULE = "error.authorization.module";

    // Document-specific errors
    public static final String ERROR_DOCUMENT_DESCRIPTION_REQUIRED = "error.document.documentDescription.required";
    
    public static final String ERROR_DOCUMENT_ACCOUNT_CLOSED = "error.document.accountClosed";
    public static final String ERROR_DOCUMENT_ACCOUNT_EXPIRED = "error.document.accountExpired";
    public static final String ERROR_DOCUMENT_ACCOUNT_EXPIRED_NO_CONTINUATION = "error.document.accountExpiredNoContinuation";
    public static final String ERROR_DOCUMENT_ACCOUNT_PRESENCE_NON_BUDGETED_OBJECT_CODE = "error.document.accountPresenceNonBudgetedObjectCode";
    public static final String ERROR_DOCUMENT_BA_MONTH_TOTAL_NOT_EQUAL_CURRENT = "error.document.ba.monthTotalNotEqualCurrent";
    public static final String ERROR_DOCUMENT_BA_BASE_AMOUNT_CHANGE_NOT_ALLOWED = "error.document.ba.baseAmountChangeNotAllowed";
    public static final String ERROR_DOCUMENT_BA_NON_BUDGETED_ACCOUNT = "error.document.ba.nonBudgetedAccount";
    public static final String ERROR_DOCUMENT_BA_MIXED_FUND_GROUPS = "error.document.ba.mixedFundGroups";
    public static final String ERROR_DOCUMENT_BA_RESTRICTION_LEVELS = "error.document.ba.restrictionLevelS";
    public static final String ERROR_DOCUMENT_BA_BASE_AMOUNTS_BALANCED = "error.document.ba.baseAmountsNotBalanced";
    public static final String ERROR_DOCUMENT_BA_CURRENT_AMOUNTS_BALANCED = "error.document.ba.currentAmountsNotBalanced";
    public static final String ERROR_DOCUMENT_BA_NO_INCOME_STREAM_ACCOUNT = "error.document.ba.noIncomeAccount";
    public static final String WARNING_DOCUMENT_BA_COPY_LABOR_BENEFITS = "warning.document.ba.copyLaborBenefits";
    public static final String ERROR_DOCUMENT_BALANCE = "error.document.balance";
    public static final String ERROR_BALANCE_CONSOLIDATION_EXCLUDE_SUBACCOUNT = "error.balance.consolidation.exclude.subaccount";
    public static final String ERROR_DOCUMENT_BALANCE_CONSIDERING_CREDIT_AND_DEBIT_AMOUNTS = "error.document.balanceConsideringCreditAndDebitAmounts";
    public static final String ERROR_DOCUMENT_BALANCE_CONSIDERING_SOURCE_AND_TARGET_AMOUNTS = "error.document.balanceConsideringSourceAndTargetAmounts";
    public static final String ERROR_DOCUMENT_EXPENSE_ON_INCOME_SIDE = "error.document.incorrectExpenseOnIncomeSide";
    public static final String ERROR_DOCUMENT_INCOME_ON_EXPENSE_SIDE = "error.document.incorrectIncomeOnExpenseSide";
    public static final String ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_BUDGET_AGGREGATION = "error.document.incorrectObjCodeWithBudgetAggregation";
    public static final String ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_SUB_TYPE_OBJ_LEVEL_AND_OBJ_TYPE = "error.document.incorrectObjCodeWithSubTypeObjLevelAndObjType";
    public static final String ERROR_DOCUMENT_NO_ACCOUNTING_LINES = "error.document.noAccountingLines";
    public static final String ERROR_DOCUMENT_SOURCE_SECTION_NO_ACCOUNTING_LINES = "error.document.sourceSectionNoAccountingLines";
    public static final String ERROR_DOCUMENT_ACCOUNTING_LINES_NO_SINGLE_SECTION_ACCOUNTING_LINES="error.document.singleSectionAccountingLines.NoAccountingLines";
    public static final String ERROR_DOCUMENT_OPTIONAL_ONE_SIDED_DOCUMENT_REQUIRED_NUMBER_OF_ACCOUNTING_LINES_NOT_MET = "error.document.optionalOneSidedDocument.requiredNumberOfAccountingLinesNotMet";
    public static final String ERROR_DOCUMENT_PC_TRANSACTION_TOTAL_ACCTING_LINE_TOTAL_NOT_EQUAL = "error.document.pc.transactionTotal.accountingLineTotal.notEqual";
    public static final String ERROR_DOCUMENT_TARGET_SECTION_NO_ACCOUNTING_LINES = "error.document.targetSectionNoAccountingLines";
    public static final String ERROR_DOCUMENT_SINGLE_SECTION_NO_ACCOUNTING_LINES = "error.document.singleSectionNoAccountingLines";
    public static final String ERROR_DOCUMENT_SOURCE_ACCOUNTING_LINE_NOT_DISTRIBUTED = "error.document.sourceAccountLines.notDistributed";
    public static final String ERROR_DOCUMENT_TARGET_ACCOUNTING_LINE_NOT_DISTRIBUTED = "error.document.targetAccountLines.notDistributed";
    public static final String ERROR_DOCUMENT_SOURCE_ACCOUNTING_LINE_AMOUNT_NOT_DISTRIBUTED = "error.document.sourceAccountLines.amount.notDistributed";
    public static final String ERROR_DOCUMENT_TARGET_ACCOUNTING_LINE_AMOUNT_NOT_DISTRIBUTED = "error.document.targetAccountLines.amount.notDistributed";
    public static final String ERROR_DOCUMENT_NO_DESCRIPTION = "error.document.noDescription";
    public static final String ERROR_DOCUMENT_SUB_ACCOUNT_INACTIVE = "error.document.subAccountInactive";
    public static final String ERROR_DOCUMENT_NULL_ACCOUNTING_LINE = "error.document.nullAccountingLine";
    public static final String ERROR_DOCUMENT_ACCOUNTING_PERIOD_CLOSED = "error.document.accountingPeriod.closed";
    public static final String ERROR_DOCUMENT_ACCOUNTING_TWO_PERIODS = "error.document.accountingPeriod.twoPeriods";
    public static final String ERROR_DOCUMENT_JV_AMOUNTS_IN_CREDIT_AND_DEBIT_FIELDS = "error.document.journalVoucher.amountsInCreditAndDebitFields";
    public static final String ERROR_DOCUMENT_LABOR_JOURNAL_VOUCHER_OFFSET_TYPE_CODE_NON_ACTIVE_CURRENT_ACCOUNTING_DOCUMENT_TYPE = "error.document.laborJournalVoucher.offsetTypeCodeNonActiveCurrentAccountDocumentType";
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
    public static final String ERROR_UNIMPLEMENTED = "error.unimplemented";

    public static final String QUESTION_CONTINUATION_ACCOUNT_SELECTION = "document.question.selectContinuationAccount.text";
    public static final String QUESTION_SAVE_BEFORE_CLOSE = "document.question.saveBeforeClose.text";

    // AccountingLine errors
    public static final String ERROR_ACCOUNTINGLINE_INACCESSIBLE_ADD = "error.accountingLine.inaccessibleAdd";
    public static final String ERROR_ACCOUNTINGLINE_INACCESSIBLE_DELETE = "error.accountingLine.inaccessibleDelete";
    public static final String ERROR_ACCOUNTINGLINE_INACCESSIBLE_UPDATE = "error.accountingLine.inaccessibleUpdate";
    public static final String ERROR_ACCOUNTINGLINE_LASTACCESSIBLE_DELETE = "error.accountingLine.deleteLastAccessible";
    public static final String ERROR_DOCUMENT_ACCOUNTING_LINE_TOTAL_CHANGED = "error.document.accountingLineTotalChanged";
    public static final String ERROR_DOCUMENT_SINGLE_ACCOUNTING_LINE_SECTION_TOTAL_CHANGED = "error.document.singleAccountingLineSectionTotalChanged";
    public static final String ERROR_ACCOUNTINGLINE_DELETERULE_INVALIDACCOUNT = "error.accountingLine.deleteRule.invalidAccount";
    public static final String ERROR_DOCUMENT_ACCOUNTING_LINE_INVALID_FORMAT = "error.document.accountingLine.invalidFormat";
    public static final String ERROR_DOCUMENT_ACCOUNTING_LINE_MAX_LENGTH = "error.document.accountingLine.maxLength";
    public static final String ERROR_DOCUMENT_ACCOUNTING_LINE_SALES_TAX_REQUIRED = "error.document.accountingLine.salesTaxRequired";
    public static final String ERROR_DOCUMENT_ACCOUNTING_LINE_INVALID_ACCT_OBJ_CD = "error.document.accountingLine.invalidAccountAndObjectCode";
    public static final String ERROR_DOCUMENT_ACCOUNTING_LINE_SALES_TAX_INVALID_ACCOUNT = "error.document.accountingLine.salesTax.invalidAccountChart";
    public static final String ERROR_DOCUMENT_ACCOUNTING_LINE_NON_ACTIVE_CURRENT_ACCOUNTING_DOCUMENT_TYPE = "error.document.accountingLine.nonActiveCurrentAccountingDocumentType";
    // Capital accounting lines errors
    public static final String ERROR_DOCUMENT_ACCOUNTING_LINE_FOR_CAPITALIZATAION_REQUIRED_MODIFY = "error.document.capitalAccountingLines.selectLinesRequired.modify";
    public static final String ERROR_DOCUMENT_ACCOUNTING_LINE_FOR_CAPITALIZATAION_REQUIRED_CREATE = "error.document.capitalAccountingLines.selectLinesRequired.create";
    public static final String ERROR_DOCUMENT_ACCOUNTING_LINE_FOR_CAPITALIZATION_NOT_PROCESSED = "error.document.capitalAccountingLines.notProcessed";
    public static final String ERROR_DOCUMENT_ACCOUNTING_LINE_FOR_CAPITALIZATION_HAS_NO_CAPITAL_ASSET = "error.document.capitalAccountingLines.capitalAssetsInformationMissing";
    public static final String ERROR_DOCUMENT_ACCOUNTING_LINES_NOT_ALL_TOTALS_DISTRIBUTED_TO_CAPITAL_ASSETS = "error.document.capitalAccountingLines.totalAmountNotDistributed";
    public static final String ERROR_DOCUMENT_ACCOUNTING_LINES_MORE_TOTALS_DISTRIBUTED_TO_CAPITAL_ASSETS = "error.document.capitalAccountingLines.moreTotalAmountDistributed";
    public static final String ERROR_DOCUMENT_CAPITAL_ASSETS_AMOUNTS_GREATER_THAN_CAPITAL_ACCOUNTING_LINE = "error.document.capitalAccountingLines.capitalAssetsAmountGreater";

    //capital asset information errors
    public static final String ERROR_DOCUMENT_CAPITAL_ASSET_NUMBER_REQUIRED = "error.document.capitalAsset.capitalAssetNumber.required";
    public static final String ERROR_DOCUMENT_CAPITAL_ASSET_QUANTITY_REQUIRED = "error.document.capitalAsset.quantity.notEntered";

    public static final String ERROR_DOCUMENT_PRE_ENCUMBRANCE_SINGLE_SECTION_NO_ACCOUNTING_LINES="error.document.singleSectionNoAccountingLines";

    // General Maintenance Document Error Messages
    public static final String ERROR_DOCUMENT_AUTHORIZATION_RESTRICTED_FIELD_CHANGED = "error.document.maintenance.authorization.restrictedFieldChanged";

    // Account Maintenance error messages
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCT_NMBR_NOT_ALLOWED = "error.document.accountMaintenance.accountNumberNotAllowed";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCT_NMBR_NOT_UNIQUE = "error.document.accountMaintenance.accountNumberNotUnique";
    public static final String ERROR_DOCUMENT_ACCMAINT_ONLY_SUPERVISORS_CAN_EDIT = "error.document.accountMaintenance.onlySupervisorsCanEditClosedAccounts";
    public static final String ERROR_DOCUMENT_ACCMAINT_RESTRICTED_STATUS_DT_REQ = "error.document.accountMaintenance.restrictedStatusDateRequired";
    public static final String ERROR_DOCUMENT_ACCMAINT_RPTS_TO_ACCT_REQUIRED_IF_FRINGEBENEFIT_FALSE = "error.document.accountMaintenance.reportsToAccountRequiredIfFringeBenefitsFalse";
    public static final String ERROR_DOCUMENT_ACCMAINT_RPTS_TO_ACCT_MUST_BE_FLAGGED_FRINGEBENEFIT = "error.document.accountMaintenance.reportsToAccountMustBeFringeBenefitFlaggedIfThisAccountFringeBenefitsIsFalse";
    public static final String ERROR_DOCUMENT_ACCMAINT_PRO_TYPE_REQD_FOR_EMPLOYEE = "error.document.accountMaintenance.professionalTypeRequiredForEmployee";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACTIVE_REQD_FOR_EMPLOYEE = "error.document.accountMaintenance.activeStatusRequiredForEmployee";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCT_SUPER_CANNOT_BE_FISCAL_OFFICER = "error.document.accountMaintenance.accountSupervisorCannotBeFiscalOfficer";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCT_CONT_ACCOUNT_CANNOT_BE_SAME = "error.document.accountMaintenance.accountContinuationAccountCannotBeSame";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCT_SUPER_CANNOT_BE_ACCT_MGR = "error.document.accountMaintenance.accountSupervisorCannotBeAcctManager";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCT_SUPER_CANNOT_EQUAL_EXISTING_FISCAL_OFFICER = "error.document.accountMaintenance.accountSupervisorCannotEqualExistingFiscalOfficer";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCT_SUPER_CANNOT_EQUAL_EXISTING_ACCT_MGR = "error.document.accountMaintenance.accountSupervisorCannotEqualExistingAcctManager";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCT_MGR_CANNOT_EQUAL_EXISTING_ACCT_SUPERVISOR = "error.document.accountMaintenance.accountManagerCannotEqualExistingAccountSupervisor";
    public static final String ERROR_DOCUMENT_ACCMAINT_FISCAL_OFFICER_CANNOT_EQUAL_EXISTING_ACCT_SUPERVISOR = "error.document.accountMaintenance.fiscalOfficerCannotEqualExistingAccountSupervisor";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCT_CANNOT_BE_CLOSED_EXP_DATE_INVALID = "error.document.accountMaintenance.accountCannotBeClosedExpDateInvalid";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCT_CLOSE_CONTINUATION_ACCT_REQD = "error.document.accountMaintenance.accountCloseContinuationAcctReqd";
    public static final String ERROR_DOCUMENT_ACCMAINT_INCOME_STREAM_ACCT_NBR_CANNOT_BE_EMPTY = "error.document.accountMaintenance.incomeStreamAcctNbrCannotBeEmpty";
    public static final String ERROR_DOCUMENT_ACCMAINT_INCOME_STREAM_ACCT_COA_CANNOT_BE_EMPTY = "error.document.accountMaintenance.incomeStreamAcctCOACannotBeEmpty";
    public static final String ERROR_DOCUMENT_ACCMAINT_ICR_TYPE_CODE_CANNOT_BE_EMPTY = "error.document.accountMaintenance.icrTypeCodeCannotBeEmpty";
    public static final String ERROR_DOCUMENT_ACCMAINT_ICR_SERIES_IDENTIFIER_CANNOT_BE_EMPTY = "error.document.accountMaintenance.icrSeriesIdentifierCannotBeEmpty";
    public static final String ERROR_DOCUMENT_ACCMAINT_ICR_CHART_CODE_CANNOT_BE_EMPTY = "error.document.accountMaintenance.icrChartCodeCannotBeEmpty";
    public static final String ERROR_DOCUMENT_ACCMAINT_ICR_ACCOUNT_CANNOT_BE_EMPTY = "error.document.accountMaintenance.icrAccountCannotBeEmpty";
    public static final String ERROR_DOCUMENT_ACCMAINT_EXP_DATE_TODAY_LATER = "error.document.accountMaintenance.expDateTodayLater";
    public static final String ERROR_DOCUMENT_ACCMAINT_CONTINUATION_ACCT_REQD_IF_EXP_DATE_COMPLETED = "error.document.accountMaintenance.continuationAcctReqdIfExpDateCompleted";
    public static final String ERROR_DOCUMENT_ACCMAINT_CONTINUATION_FINCODE_REQD_IF_EXP_DATE_COMPLETED = "error.document.accountMaintenance.continuationFinCodeIfExpDateCompleted";
    public static final String ERROR_DOCUMENT_ACCMAINT_EXP_DATE_CANNOT_BE_BEFORE_EFFECTIVE_DATE = "error.document.accountMaintenance.expDateCannotBeBeforeEffectiveDate";
    public static final String ERROR_DOCUMENT_ACCMAINT_CAMS_SUBFUNDGROUP_WITH_MISSING_CAMPUS_CD_FOR_BLDG = "error.document.accountMaintenance.camsSubFundGroupWithMissingCampusCode";
    public static final String ERROR_DOCUMENT_ACCMAINT_NONCAMS_SUBFUNDGROUP_WITH_CAMPUS_CD_FOR_BLDG = "error.document.accountMaintenance.nonCamsSubFundGroupWithCampusCode";
    public static final String ERROR_DOCUMENT_ACCMAINT_BLANK_SUBFUNDGROUP_WITH_CAMPUS_CD_FOR_BLDG = "error.document.accountMaintenance.blankSubFundGroupWithCampusCode";
    public static final String ERROR_DOCUMENT_ACCMAINT_CAMS_SUBFUNDGROUP_WITH_MISSING_BUILDING_CD = "error.document.accountMaintenance.camsSubFundGroupWithMissingBuildingCode";
    public static final String ERROR_DOCUMENT_ACCMAINT_NONCAMS_SUBFUNDGROUP_WITH_BUILDING_CD = "error.document.accountMaintenance.nonCamsSubFundGroupWithBuildingCode";
    public static final String ERROR_DOCUMENT_ACCMAINT_BLANK_SUBFUNDGROUP_WITH_BUILDING_CD = "error.document.accountMaintenance.blankSubFundGroupWithBuildingCode";
    public static final String ERROR_DOCUMENT_ACCTMAINT_INVALID_CG_RESPONSIBILITY = "error.document.accountMaintenance.invalidContractsAndGrantsResponsibility";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCOUNT_EXPIRED_CONTINUATION = "error.document.accountMaintenance.expiredAccount.continuationAccount";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCOUNT_CLOSED_PENDING_LEDGER_ENTRIES = "error.document.accountMaintenance.closedAccount.noPendingLedgerEntriesAllowed";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCOUNT_CLOSED_NO_LOADED_BEGINNING_BALANCE = "error.document.accountMaintenance.closedAccount.beginningBalanceNotLoaded";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCOUNT_CLOSED_NO_FUND_BALANCES = "error.document.accountMaintenance.closedAccount.noFundBalances";
    public static final String ERROR_DOCUMENT_GLOBAL_ACCOUNT_NO_ACCOUNTS = "error.document.accountGlobalDetails.noAccountsEntered";
    public static final String ERROR_DOCUMENT_GLOBAL_ACCOUNT_INVALID_ACCOUNT = "error.document.accountGlobalDetails.invalidAccount";
    public static final String ERROR_DOCUMENT_GLOBAL_ACCOUNT_INVALID_ORG = "error.document.accountGlobal.invalidOrganization";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCOUNT_CLOSED_PENDING_LABOR_LEDGER_ENTRIES = "error.document.accountMaintenance.closedAccount.noPendingLaborLedgerEntriesAllowed";
    public static final String ERROR_DOCUMENT_ACCMAINT_REPORTING_USER_MUST_BE_CHART_MANAGER_OR_ROOT_MANAGER = "error.document.accountMaintenance.userMustBeChartManagerOrRootManager";
    public static final String ERROR_DOCUMENT_ACCMAINT_CG_FIELDS_FILLED_FOR_NON_CG_ACCOUNT = "error.document.accountMaintenance.cgFieldsFilledInForNonCGAccount";
    public static final String ERROR_DOCUMENT_ACCMAINT_CG_ICR_FIELDS_FILLED_FOR_NON_CG_ACCOUNT = "error.document.accountMaintenance.cgICRFieldsFilledInForNonCGAccount";
    public static final String ERROR_DOCUMENT_ACCMAINT_ACCOUNT_CANNOT_CLOSE_OPEN_ENCUMBRANCE = "error.document.accountMaintenance.accountCannotCloseOpenEncumbrance";
    public static final String ERROR_DOCUMENT_ACCMAINT_ICR_ACCOUNT_INVALID_LINE_PERCENT = "error.document.accountMaintenance.indirectCostRecoveryAccounts.invalidLinePercent";
    public static final String ERROR_DOCUMENT_ACCMAINT_ICR_ACCOUNT_TOTAL_NOT_100_PERCENT = "error.document.accountMaintenance.indirectCostRecoveryAccounts.totalNot100Percent";

    // Fiscal Period Maintenance Errors
    public static final String ERROR_DOCUMENT_FISCAL_PERIOD_YEAR_DOESNT_EXIST = "error.document.fiscalPeriodMaintenance.yearDosntExist";

    // Chart Maintenance Errors
    public static final String ERROR_DOCUMENT_CHART_REPORTS_TO_CHART_MUST_EXIST = "error.document.chart.reportsToChartMustExist";

    // Account Delegate Maintenance Errors
    public static final String ERROR_DOCUMENT_ACCTDELEGATEMAINT_STARTDATE_IN_PAST = "error.document.accountDelegateMaintenance.startDateMustBeGreaterThanOrEqualToToday";
    public static final String ERROR_DOCUMENT_ACCTDELEGATEMAINT_FROM_AMOUNT_NONNEGATIVE = "error.document.accountDelegateMaintenance.fromAmountMustBeNonNegative";
    public static final String ERROR_DOCUMENT_ACCTDELEGATEMAINT_TO_AMOUNT_MORE_THAN_FROM_OR_ZERO = "error.document.accountDelegateMaintenance.toAmountMustBeEqualOrGreaterThanFromAmountOrZero";
    public static final String ERROR_DOCUMENT_ACCTDELEGATEMAINT_PRIMARY_ROUTE_ALREADY_EXISTS_FOR_DOCTYPE = "error.document.accountDelegateMaintenance.primaryRouteAlreadyExistsForNewDocType";
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
    public static final String ERROR_DOCUMENT_ACCTDELEGATEMAINT_INVALID_DOC_TYPE = "error.document.delegateMaintenance.invalidFinancialSystemDocumentTypeCode";

    // SubAccount Maintenance Errors
    public static final String ERROR_DOCUMENT_SUBACCTMAINT_RPTCODE_ALL_FIELDS_IF_ANY_FIELDS = "error.document.subAccountMaintenance.someReportingCodeFieldsEnteredButNotAll";
    public static final String ERROR_DOCUMENT_SUBACCTMAINT_NOT_AUTHORIZED_ENTER_CG_FIELDS = "error.document.subAccountMaintenance.cannotEnterCgValuesNotAuthorized";
    public static final String ERROR_DOCUMENT_SUBACCTMAINT_NOT_AUTHORIZED_CHANGE_CG_FIELDS = "error.document.subAccountMaintenance.cannotChangeCgValuesNotAuthorized";
    public static final String ERROR_DOCUMENT_SUBACCTMAINT_INVALI_SUBACCOUNT_TYPE_CODES = "error.document.subAccountMaintenance.invalidSubAccountTypeCodes";
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

    // Org Review Errors

    public static final String ERROR_DOCUMENT_ORGREVIEW_INVALID_DOCUMENT_TYPE = "error.document.orgReview.invalidDocumentType";

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
    public static final String ERROR_DOCUMENT_GLOBAL_ORG_REVERSION_NO_REVERSION_CODE = "error.document.globalOrgReversion.noReversionCode";

    // AchBank errors
    public static final String ERROR_DOCUMENT_ACHBANKMAINT_INVALID_TYPE_CODE = "error.document.achBankMaintenance.invalidTypeCode";
    public static final String ERROR_DOCUMENT_ACHBANKMAINT_INVALID_OFFICE_CODE = "error.document.achBankMaintenance.invalidOfficeCode";
    public static final String ERROR_DOCUMENT_ACHBANKMAINT_INVALID_INST_STATUS_CODE = "error.document.achBankMaintenance.invalidInstStatusCode";
    public static final String ERROR_DOCUMENT_ACHBANKMAINT_INVALID_DATA_VIEW_CODE = "error.document.achBankMaintenance.invalidDataViewCode";

    // PayeeAchAccount errors
    public static final String ERROR_DOCUMENT_PAYEEACHACCOUNTMAINT_DUPLICATE_RECORD = "error.document.payeeAchAccountMaintenance.duplicateAccount";

    // BankAccount errors
    public static final String ERROR_DOCUMENT_BANKACCMAINT_INVALID_BANK = "error.document.bankAcctMaintenance.invalidBank";

    // KualiUser errors
    public static final String ERROR_DOCUMENT_KUALIUSERMAINT_CANNOT_MARK_INACTIVE = "error.document.kualiUserMaintenance.cannotMarkInactive";
    public static final String ERROR_DOCUMENT_KUALIUSERMAINT_INVALID_EMP_STATUS = "error.document.kualiUserMaintenance.invalidEmpStatus";

    //Tax Errors
    public static final String ERROR_DOCUMENT_TAX_REGION_CANT_ADD_PAST_OR_CURRENT_DATE_FOR_TAX_DISTRICT = "error.document.taxRegionMaintenance.cannotAddPastOrCurrentDateForTaxDistrict";
    public static final String ERROR_DOCUMENT_TAX_REGION_TAX_RATE_BETWEEN0AND1 = "error.document.taxRegionMaintenance.taxRateShouldBeBetween0And1";
    public static final String ERROR_DOCUMENT_TAX_REGION_INVALID_STATE = "error.document.taxRegionMaintenance.invalidState";
    public static final String ERROR_DOCUMENT_TAX_REGION_INVALID_COUNTY = "error.document.taxRegionMaintenance.invalidCounty";
    public static final String ERROR_DOCUMENT_TAX_REGION_INVALID_POSTAL_CODE = "error.document.taxRegionMaintenance.invalidPostalCode";

    // Object Code errors
    public static final String ERROR_DOCUMENT_OBJCODE_ILLEGAL = "error.document.objectCode.illegal";
    public static final String ERROR_DOCUMENT_REPORTS_TO_OBJCODE_ILLEGAL = "error.document.objectCode.invalidReportsToObject";
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
    public static final String ERROR_DOCUMENT_GLOBAL_OBJECTMAINT_INACTIVATION_BLOCKING = "error.document.objectCodeGlobal.inactivationBlocking";
    public static final String ERROR_DOCUMENT_OBJECTMAINT_INACTIVATION_BLOCKING = "error.document.objectCode.inactivationBlocking";
    public static final String ERROR_DOCUMENT_BALANCETYPMAINT_INACTIVATION_BLOCKING = "error.document.balanceType.inactivationBlocking";
    public static final String ERROR_DOCUMENT_OBJECTMAINT_BUDGET_CATEGORY_CODE = "error.document.objectCode.mustBeValid";


    // Object Consolidation errors
    public static final String ERROR_DOCUMENT_OBJCONSMAINT_ALREADY_EXISTS_AS_OBJLEVEL = "error.document.objConsMaintenance.alreadyExistsAsObjLevel";

    // Object Level errors
    public static final String ERROR_DOCUMENT_OBJLEVELMAINT_ALREADY_EXISTS_AS_OBJCONS = "error.document.objLevelMaintenance.alreadyExistsAsObjCons";

    // Object Type errors
    public static final String ERROR_DOCUMENT_OBJTYPE_INVALID_ACCT_CTGRY = "error.document.objType.invalidAccountCategory";

    // Offset Definition errors
    public static final String ERROR_DOCUMENT_OFFSETDEFMAINT_INACTIVE_OBJ_CODE_FOR_DOCTYPE = "error.document.offsetDefinitionMaintenance.inactiveObjectCodeForDocType";

    public static final String ERROR_CUSTOM = "error.custom";
    public static final String ERROR_MAINTENANCE_LOCKED = "error.maintenance.locked";
    public static final String ERROR_ZERO_AMOUNT = "error.zeroAmount";
    public static final String ERROR_BLANK_AMOUNT = "error.blankAmount";
    public static final String ERROR_ZERO_OR_NEGATIVE_AMOUNT = "error.zeroOrNegativeAmount";
    public static final String ERROR_NEGATIVE_AMOUNT = "error.negativeAmount";
    public static final String ERROR_INVALID_NEGATIVE_AMOUNT_NON_CORRECTION = "error.invalidNegativeAmountForNonCorrection";
    public static final String ERROR_NEGATIVE_OR_ZERO_CHECK_TOTAL = "error.document.checktotal.zeroOrNegative";
    public static final String ERROR_NEGATIVE_ACCOUNTING_TOTAL = "error.document.accountingtotal.negative";
    public static final String ERROR_NO_ACCOUNTING_LINES = "error.document.accountinglines.noLines";
    public static final String ERROR_CHECK_ACCOUNTING_TOTAL = "error.document.checkaccounting.match";

    public static final String QUESTION_GENERATE_LABOR_BENEFIT_LINES = "question.budgetAdjustment.generateBenefitLines";

    public static final String QUESTION_CLEAR_UNNEEDED_TAB = "question.dv.clearUnneededTab";
    public static final String QUESTION_BANK_INACTIVE = "question.document.bank.inactive";

    public static final String ERROR_PAYEE_INITIATOR = "error.document.payeeinitiator";

    public static final String ERROR_DV_SPECIAL_HANDLING = "error.document.specialHandling";
    public static final String ERROR_DV_NO_DOCUMENTATION_NOTE_MISSING = "error.document.noDocumentationNoteMissing";
    public static final String ERROR_DV_SPECIAL_HANDLING_NOTE_MISSING = "error.document.specialHandlingNoteMissing";
    public static final String ERROR_DV_EXCEPTION_ATTACHED_NOTE_MISSING = "error.document.exceptionAttachedNoteMissing";
    public static final String ERROR_DV_PAYEE_SELECTION_EMPTY = "error.document.payeeSelectionEmpty";
    public static final String ERROR_DV_CURRENCY_TYPE_CODE = "error.dv.currencyTypeCode";
    public static final String ERROR_DV_CURRENCY_TYPE_NAME = "error.dv.currencyTypeName";
    public static final String ERROR_DV_BANK_ROUTING_NUMBER = "error.dv.bankRoutingNumber";
    public static final String ERROR_DV_WIRE_ATTACHMENT = "error.dv.attachmentWire";
    public static final String ERROR_DV_FEDERAL_TAX_NOT_ZERO = "error.dv.federalTaxNotZero";
    public static final String ERROR_DV_STATE_TAX_NOT_ZERO = "error.dv.stateTaxNotZero";
    public static final String ERROR_DV_GROSS_UP_INDICATOR = "error.dv.grossUpIndicator";
    public static final String ERROR_DV_POSTAL_COUNTRY_CODE = "error.dv.postalCountryCode";
    public static final String ERROR_DV_INVALID_FED_TAX_PERCENT = "error.dv.federalTaxPercent";
    public static final String ERROR_DV_INVALID_STATE_TAX_PERCENT = "error.dv.stateTaxPercent";
    public static final String ERROR_DV_NRA_PERMISSIONS_GENERATE = "error.dv.nraTaxLinesPermissions";
    public static final String ERROR_DV_NRA_NO_TAXLINES_GENERATED = "error.dv.nraTaxLinesNotGenerated";
    public static final String ERROR_DV_GENERATE_TAX_BOTH_0 = "error.dv.generateLinesBoth0";
    public static final String ERROR_DV_GENERATE_TAX_NO_SOURCE = "error.dv.generateLinesNoSource";
    public static final String ERROR_DV_GENERATE_TAX_NOT_NRA = "error.dv.generateNotNRA";
    public static final String ERROR_DV_STATE_TAX_SHOULD_BE_ZERO = "error.dv.nraTaxStateTaxShouldBeZero";
    public static final String ERROR_DV_STATE_INCOME_TAX_PERCENT_SHOULD_BE_GREATER_THAN_ZERO = "error.dv.nraTaxStateTaxShouldBeGreaterThanZero";
    public static final String ERROR_DV_ONLY_ONE_SELECTION_ALLOWED = "error.dv.nraTaxOnlyOneSelectionAllowed";
    public static final String ERROR_DV_FED_AND_STATE_TAXES_SHOULD_BE_ZERO = "error.dv.nraTaxFedAndStateTaxesShouldBeZero";
    public static final String ERROR_DV_NON_REPORTABLE_ONLY = "error.dv.nraTaxNonReportableOnly";
    public static final String ERROR_DV_WHEN_CHECKED_CANNOT_HAVE_VALUE = "error.dv.nraTaxWhenCheckedCannotHaveValue";
    public static final String ERROR_DV_TAXES_CANNOT_BE_ZERO = "error.dv.nraTaxTaxesCannotBeZero";
    public static final String ERROR_DV_SHOULD_BE_SELECTED_AND_EUOC_CHECKED = "error.dv.nraTaxShouldBeSelectedAndEuocChecked";
    public static final String ERROR_DV_NRA_TAX_WHEN_CHECKED_SHOULD_BE_SELECTED = "error.dv.nraTaxWhenCheckedShouldBeChecked";
    public static final String ERROR_DV_NRA_TAX_CANNOT_SELECT_FS_TE_GUP_USAID = "error.dv.nraTaxCannotSelectFsTeGupUsaid";

    public static final String ERROR_DV_GENERATE_TAX_DOC_REFERENCE = "error.dv.generateDocReference";
    public static final String ERROR_DV_ADD_LINE_MISSING_PAYMENT_REASON = "error.dv.addLineMissingPaymentReason";
    public static final String ERROR_DV_ADD_LINE_MISSING_PAYEE = "error.dv.addLineMissingPayee";
    public static final String ERROR_DV_TRAVEL_TO_STATE = "error.dv.travelToState";
    public static final String ERROR_DV_TRAVEL_FROM_STATE = "error.dv.travelFromState";
    public static final String ERROR_DV_PERDIEM_CHANGE_REQUIRED = "error.dv.prediemChangeRequired";
    public static final String ERROR_DV_TRAVEL_CHECK_TOTAL = "error.dv.travelCheckTotal";
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
    public static final String ERROR_DV_EMPLOYEE_PAID_OUTSIDE_PAYROLL = "error.dv.emplPaidOutsidePayroll";
    public static final String ERROR_DV_MOVING_PAYMENT_PAYEE = "error.dv.movingPaymentPayee";
    public static final String ERROR_DV_RESEARCH_PAYMENT_PAYEE = "error.dv.researchPaymentPayee";
    public static final String ERROR_DV_ACTIVE_EMPLOYEE_PREPAID_TRAVEL = "error.dv.activeEmployeePrepaidTravel";
    public static final String ERROR_DV_REVOLVING_PAYMENT_REASON = "error.dv.revolvingPaymentReason";
    public static final String ERROR_DV_CHECK_TOTAL_CHANGE = "error.dv.checkTotalChange";
    public static final String ERROR_DV_CAMPUS_TURNED_OFF_SPECIAL_HANDLING_WITHOUT_EXPLANATORY_NOTE = "error.dv.campusTurnedOffSpecialHandlingWithoutExplanatoryNote";
    public static final String MESSAGE_DV_IMMEDIATE_EXTRACT_EMAIL_SUBJECT = "message.dv.immediateExtractEmail.subject";
    public static final String MESSAGE_DV_IMMEDIATE_EXTRACT_EMAIL_BODY = "message.dv.immediateExtractEmail.body";

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

    public static final String MESSAGE_SAVED = "message.saved";
    public static final String MESSAGE_JV_CANCELLED_ROUTE = "message.journalVoucher.cancelledRoute";
    public static final String MESSAGE_REVERT_SUCCESSFUL = "message.revert.successful";

    public static final String QUESTION_CHANGE_JV_BAL_TYPE_FROM_SINGLE_AMT_TO_CREDIT_DEBIT_MODE = "document.journalVoucher.question.changeBalanceType.singleAmountToCreditDebitMode.text";
    public static final String QUESTION_CHANGE_JV_BAL_TYPE_FROM_SINGLE_AMT_TO_EXT_ENCUMB_CREDIT_DEBIT_MODE = "document.journalVoucher.question.changeBalanceType.singleAmountToCreditDebitMode.externalEncumbrance.text";
    public static final String QUESTION_CHANGE_JV_BAL_TYPE_FROM_CREDIT_DEBIT_TO_SINGLE_AMT_MODE = "document.journalVoucher.question.changeBalanceType.creditDebitToSingleAmountMode.text";
    public static final String QUESTION_CHANGE_JV_BAL_TYPE_FROM_EXT_ENCUMB_CREDIT_DEBIT_TO_SINGLE_AMT_MODE = "document.journalVoucher.question.changeBalanceType.creditDebitToSingleAmountMode.externalEncumbrance.text";
    public static final String QUESTION_CHANGE_JV_BAL_TYPE_FROM_EXT_ENCUMB_TO_NON_EXT_ENCUMB = "document.journalVoucher.question.changeBalanceType.externalEncumbranceToNonExternalEncumbrance.text";
    public static final String QUESTION_CHANGE_JV_BAL_TYPE_FROM_NON_EXT_ENCUMB_TO_EXT_ENCUMB = "document.journalVoucher.question.changeBalanceType.NonExternalEncumbranceToExternalEncumbrance.text";
    public static final String QUESTION_ROUTE_OUT_OF_BALANCE_JV_DOC = "document.journalVoucher.question.routeOutOfBalance.text";
    public static final String QUESTION_ROUTE_OUT_OF_BALANCE_JV_DOC_SINGLE_AMT_MODE = "document.journalVoucher.question.routeOutOfBalanceSingleAmountMode.text";

    public static final String ERROR_MISSING = "error.missing";


    // GL specific Error Codes
    public static final String ERROR_DEBIT_CREDIT_INDICATOR_NEITHER_D_NOR_C = "error.gl.DebitCreditIndicatorNeitherDNorC";
    public static final String ERROR_DEBIT_CREDIT_INDICATOR_MUST_BE_SPACE = "error.gl.DebitCreditIndicatorMustBeSpace";
    public static final String ERROR_CIRCULAR_DEPENDENCY_IN_CONTINUATION_ACCOUNT_LOGIC = "error.gl.CircularityInContinuationAccountLogic";
    public static final String ERROR_OFFSET_DEFINITION_NOT_FOUND = "error.gl.offsetDefinitionNotFound";
    public static final String ERROR_OFFSET_DEFINITION_OBJECT_CODE_NOT_FOUND = "error.gl.offsetDefinitionObjectCodeNotFound";
    public static final String ERROR_DOCUMENT_TYPE_NOT_FOUND = "error.gl.DocumentTypeNotFound";
    public static final String ERROR_ORIGIN_CODE_NOT_FOUND = "error.gl.OriginCodeNotFound";
    public static final String ERROR_ORIGIN_CODE_NOT_ACTIVE = "error.gl.OriginCodeNotActive";
    public static final String ERROR_DOCUMENT_NUMBER_REQUIRED = "error.gl.DocumentNumberRequired";
    public static final String ERROR_CHART_NOT_FOUND = "error.gl.ChartNotFound";
    public static final String ERROR_CHART_NOT_ACTIVE = "error.gl.ChartNotActive";
    public static final String ERROR_ACCOUNT_NOT_FOUND = "error.gl.AccountNotFound";
    public static final String ERROR_ACCOUNT_EXPIRED = "error.gl.AccountExpired";
    public static final String ERROR_SUB_ACCOUNT_NOT_FOUND = "error.gl.SubAccountNotFound";
    public static final String ERROR_SUB_ACCOUNT_NOT_ACTIVE = "error.gl.SubAccountNotActive";
    public static final String ERROR_OBJECT_CODE_EMPTY = "error.gl.ObjectCodeEmpty";
    public static final String ERROR_OBJECT_CODE_NOT_FOUND = "error.gl.ObjectCodeNotFound";
    public static final String ERROR_OBJECT_CODE_NOT_ACTIVE = "error.gl.ObjectCodeNotActive";
    public static final String ERROR_OBJECT_TYPE_NOT_ACTIVE = "error.gl.ObjectTypeNotActive";
    public static final String ERROR_OBJECT_TYPE_NOT_FOUND = "error.gl.ObjectTypeNotFound";
    public static final String ERROR_BALANCE_TYPE_NOT_ACTIVE = "error.gl.BalanceTypeNotActive";
    public static final String ERROR_BALANCE_TYPE_NOT_FOUND = "error.gl.BalanceTypeNotFound";
    public static final String ERROR_FISCAL_PERIOD_CLOSED = "error.gl.FiscalPeriodClosed";
    public static final String ERROR_TRANS_CANNOT_BE_NEGATIVE_IF_OFFSET = "error.gl.TransCannotBeNegativeIfOffset";
    public static final String ERROR_DC_INDICATOR_MUST_BE_EMPTY = "error.gl.DCIndicatorMustBeEmpty";
    public static final String ERROR_DC_INDICATOR_MUST_BE_D_OR_C = "error.gl.DCIndicatorMustBeDorC";
    public static final String ERROR_DC_INDICATOR_MUST_BE_NEITHER_D_NOR_C = "error.gl.DCIndicatorMustBeNeitherDNorC";
    public static final String ERROR_TRANSACTION_DATE_INVALID = "error.gl.TransactionDateInvalid";

    public static final String ERROR_NUMBER_FORMAT_ORIGIN_ENTRY_FROM_TEXT_FILE = "error.gl.NumberFormatOriginEntryFromTextFile";
    public static final String ERROR_INVALID_FORMAT_ORIGIN_ENTRY_FROM_TEXT_FILE = "error.gl.InvalidFormatOriginEntryFromTextFile";

    public static final String ERROR_ENTERPRISE_FEEDER_RECONCILIATION_OR_LOADING_ERROR = "error.gl.EnterpriseFeederReconciliationOrLoadingError";

    public static final String ERROR_PROJECT_CODE_MUST_BE_ACTIVE = "error.gl.ProjectCodeMustBeActive";
    public static final String ERROR_REFERENCE_DOCUMENT_TYPE_NOT_FOUND = "error.gl.ReferenceDocumentTypeNotFound";
    public static final String ERROR_REFERENCE_ORIGINATION_CODE_NOT_FOUND = "error.gl.ReferenceOriginationCodeNotFound";
    public static final String ERROR_REVERSAL_DATE_NOT_FOUND = "error.gl.ReversalDateNotFound";
    public static final String ERROR_ENC_UPDATE_CODE_NOT_DRN = "error.gl.EncUpdateCodeNotDRN";
    public static final String ERROR_UNIV_DATE_NOT_FOUND = "error.gl.UniversityDateNotFound";
    public static final String ERROR_PROJECT_CODE_NOT_FOUND = "error.gl.ProjectCodeNotFound";
    public static final String ERROR_ACCOUNTING_PERIOD_NOT_ACTIVE = "error.gl.AccountingPeriodNotActive";
    public static final String ERROR_ACCOUNTING_PERIOD_NOT_FOUND = "error.gl.AccountingPeriodNotFound";
    public static final String ERROR_COST_SHARE_OBJECT_NOT_FOUND = "error.gl.CostShareNotFound";
    public static final String ERROR_ORIGIN_CODE_CANNOT_HAVE_CLOSED_ACCOUNT = "error.gl.OriginCodeCannotHaveClosedAccount";
    public static final String ERROR_CONTINUATION_ACCOUNT_LIMIT_REACHED = "error.gl.ContinuationAccountLimitReached";
    public static final String ERROR_NO_OBJECT_FOR_OBJECT_ON_OFSD = "error.gl.NoObjectForObjectOnOFSD";
    public static final String ERROR_CONTINUATION_ACCOUNT_NOT_FOUND = "error.gl.ContinuationAccountNotFound";
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
    public static final String ERROR_ENCUMBRANCE_UPDATE_CODE_CANNOT_BE_BLANK_FOR_BALANCE_TYPE = "error.gl.EncumbranceUpdateCodeCannotBeBlankForBalanceType";
    public static final String ERROR_PAYROLL_END_DATE_FISCAL_PERIOD = "error.labor.PayrollEndDateFiscalPeriodCodeNotFound";
    public static final String ERROR_PAYROLL_END_DATE_FISCAL_YEAR = "error.labor.PayrollEndDateFiscalYearNotFound";
    public static final String ERROR_EMPTY_LABOR_BENEFIT_CATEGORY_CODE = "error.labor.laborBenefitRateCategoryEmpty";

    public static final String WARNING_ICR_GENERATION_PROBLEM_WITH_A21SUBACCOUNT_FIELD_BLANK_INVALID = "warning.gl.IndirectCostRecoveryGeneration.A21SubAccount.icrField.blankInvalid";

    public static final String MSG_DEDIT_CREDIT_CODE_MUST_BE = "message.gl.DebitCreditCodeMustBe";
    public static final String MSG_FOR_BALANCE_TYPE = "message.gl.ForBalanceType";
    public static final String MSG_GENERATED_CAPITALIZATION = "message.gl.GeneratedCapitalization";
    public static final String MSG_GENERATED_LIABILITY = "message.gl.GeneratedLiability";
    public static final String MSG_GENERATED_OFFSET = "message.gl.GeneratedOffset";
    public static final String MSG_GENERATED_COST_SHARE = "message.gl.GeneratedCostShareDescription";
    public static final String MSG_GENERATED_TRANSFER = "message.gl.GeneratedTransfer";
    public static final String MSG_CLOSE_ENTRY_TO_NOMINAL_REVENUE = "message.gl.ClosingEntryToNR";
    public static final String MSG_CLOSE_ENTRY_TO_NOMINAL_EXPENSE = "message.gl.ClosingEntryToNE";
    public static final String MSG_CLOSE_ENTRY_TO_FUND_BALANCE = "message.gl.ClosingEntryToFB";
    public static final String MSG_AUTO_FORWARD = "message.gl.AutoForward";
    public static final String MSG_ACCOUNT_CLOSED_TO = "message.gl.AccountClosedTo";
    public static final String MSG_ACCOUNT_EXPIRED_TO = "message.gl.AccountExpiredTo";

    public static final String ERROR_FISCAL_YEAR_CANNOT_BE_NULL_BALANCE_TYPE_A2 = "error.gl.FiscalYearCannotBeNullForBalanceTypeA2";
    public static final String ERROR_FISCAL_PERIOD_CANNOT_BE_NULL_BALANCE_TYPE_A2 = "error.gl.FiscalPeriodCannotBeNullForBalanceTypeA2";
    public static final String ERROR_REFERENCE_ORIGIN_CODE_NOT_FOUND = "error.gl.ReferenceOriginCodeNotFound";

    public static final String ERROR_REFERENCE_FIELDS = "error.gl.ReferenceFields";
    public static final String ERROR_DESCRIPTION_CANNOT_BE_BLANK = "error.gl.DescriptionCannotBeBlank";

    // Credit Card Vendor errors
    public static final String ERROR_CCV_INCOME_SUBACCOUNT_REQUIRED = "error.document.ccv.incomeSubAccount.required";
    public static final String ERROR_CCV_EXPENSE_SUBACCOUNT_REQUIRED = "error.document.ccv.expenseSubAccount.required";
    public static final String ERROR_CCV_INVALIDSUBACCOUNT = "error.document.ccv.invalidSubAccount";
    public static final String ERROR_CCV_INCOME_SUBOBJ_REQUIRED = "error.document.ccv.incomeSubOBJ.required";
    public static final String ERROR_CCV_EXPENSE_SUBOBJ_REQUIRED = "error.document.ccv.expenseSubOBJ.required";
    public static final String ERROR_CCV_INVALIDSUBOBJECT = "error.document.ccv.invalidSubObject";

    public static final String ERROR_GL_ERROR_CORRECTION_SYSTEMFIELD_REQUIRED = "error.gl.correction.systemfield.required";
    public static final String ERROR_GL_ERROR_CORRECTION_EDITMETHODFIELD_REQUIRED = "error.gl.correction.editmethodfield.required";
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

    public static final String ERROR_GL_ERROR_GROUP_ALREADY_MARKED_NO_PROCESS = "error.gl.correction.already.marked.no.process";

    public static final String ERROR_GL_LOOKUP_ENTRY_NON_MATCHING_REQUIRED_FIELDS = "error.gl.lookup.entry.nonMatchingRequiredFields";
    public static final String ERROR_GL_LOOKUP_PENDING_ENTRY_NON_MATCHING_REQUIRED_FIELDS = "error.gl.lookup.pendingEntry.nonMatchingRequiredFields";
    public static final String ERROR_GL_LOOKUP_ENCUMBRANCE_NON_MATCHING_REQUIRED_FIELDS = "error.gl.lookup.encumbrance.nonMatchingRequiredFields";

    public static final String ERROR_DOCUMENT_CHART_MANAGER_MUST_EXIST = "error.document.chart.chartManagerMustExist";
    public static final String ERROR_DOCUMENT_CHART_MANAGER_MUST_BE_KUALI_USER = "error.document.chart.chartManagerMustBeKualiUser";
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
    public static final String ERROR_AGENCY_EQUALS_FEDERAL_PASS_THROUGH_AGENCY = "error.cg.agencyEqualsFederalPassThroughAgency";
    public static final String ERROR_FEDERAL_PASS_THROUGH_AGENCY_EQUALS_AGENCY = "error.cg.federalPassThroughAgencyEqualsAgency";
    public static final String ERROR_FPT_AGENCY_NUMBER_REQUIRED = "error.cg.federalPassThroughAgencyIndicatorIsChecked";
    public static final String ERROR_FPT_AGENCY_NUMBER_NOT_BLANK = "error.cg.federalPassThroughAgencyNumberIsNotBlank";
    public static final String ERROR_PRIMARY_AGENCY_IS_FEDERAL_AND_FPT_INDICATOR_IS_CHECKED = "error.cg.primaryAgencyIsFederalAndFPTIndicatorIsChecked";
    public static final String ERROR_PRIMARY_AGENCY_IS_FEDERAL_AND_FPT_AGENCY_IS_NOT_BLANK = "error.cg.primaryAgencyIsFederalAndFPTAgencyNumberIsNotBlank";

    public static final String ERROR_INVALID_PROJECT_DIRECTOR_STATUS = "error.cg.invalidProjectDirectorStatus";
    public static final String ERROR_NOT_A_PROJECT_DIRECTOR = "error.cg.notAProjectDirector";
    public static final String ERROR_PROPOSAL_SUBCONTRACTOR_NUMBER_REQUIRED_FOR_ADD = "error.cg.proposal.subcontractorNumberRequiredForAdd";


    public static final String WARNING_AWARD_ENTRY_BEFORE_START_DATE = "warning.cg.award.entryDateBeforeStartDate";
    public static final String WARNING_AWARD_SUBCONTRACTOR_TOTAL_GREATER_THAN_AWARD_TOTAL = "warning.cg.award.subcontractorAmountGreaterThanAwardAmount";

    public static final String CFDA_UPDATE_EMAIL_SUBJECT_LINE = "cfda.updateEmail.subject";

    // batch upload
    public static final String MESSAGE_BATCH_UPLOAD_TITLE_PCDO = "message.batchUpload.title.procurementCard";
    public static final String MESSAGE_BATCH_UPLOAD_TITLE_COLLECTOR = "message.batchUpload.title.collector";
    public static final String MESSAGE_BATCH_UPLOAD_TITLE_COLLECTOR_FLAT_FILE = "message.batchUpload.title.collector,flat.file";
    public static final String MESSAGE_BATCH_UPLOAD_TITLE_ENTERPRISE_FEEDER = "message.batchUpload.title.enterprise.feeder";
    public static final String MESSAGE_BATCH_UPLOAD_TITLE_LABOR_ENTERPRISE_FEEDER = "message.batchUpload.title.labor.enterprise.feeder";
    public static final String MESSAGE_BATCH_UPLOAD_SAVE_SUCCESSFUL = "message.batchUpload.saveSuccessful";
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
    public static final String ERROR_BATCH_UPLOAD_FILE_EMPTY_CHART = "error.batchUpload.emptyChart";
    public static final String ERROR_BATCH_UPLOAD_FILE_INVALID_ACCOUNT = "error.batchUpload.invalidAccount";

    public static final class AdvanceDeposit {
        public static final String ERROR_DOCUMENT_ADVANCE_DEPOSIT_REQ_NUMBER_DEPOSITS_NOT_MET = "error.document.advanceDeposit.requiredNumberOfAdvanceDepositsNotMet";
        public static final String ERROR_DOCUMENT_ADVANCE_DEPOSIT_OUT_OF_BALANCE = "error.document.advanceDeposit.balance";
        public static final String ERROR_DOCUMENT_ADVANCE_DEPOSIT_ZERO_AMOUNT = "error.document.advanceDeposit.zeroAmount";
        public static final String ERROR_DOCUMENT_ADVANCE_DEPOSIT_INCORRECT_ELECTRONIC_PAYMENT_STATE = "error.document.advanceDeposit.ifAnyElectronicPaymentsThenAllElectronicPayments";

        public static final String DESCRIPTION_GLPE_BANK_OFFSET = "description.glpe.bankOffset.advanceDeposit";
    }

    public static final class budget{
        public static final String MSG_REPORT_ACCOUNT_LIST = "message.budget.report.accountList";
        public static final String MSG_REPORT_EMPTY_ACCOUNT_LIST = "message.budget.report.emptyAccountList";
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
        public static final String ERROR_DOCUMENT_CASH_RECEIPT_BALANCE = "error.document.cashReceipt.balance";
        public static final String ERROR_DOCUMENT_CASH_RECEIPT_NO_CASH_RECONCILIATION_TOTAL = "error.document.cashReceipt.noCashReconciliationTotal";
        public static final String MSG_VERIFIED_BUT_NOT_AWAITING_DEPOSIT = "message.document.cashReceipt.verifiedButNotAwaitingDeposit";
        public static final String MSG_VERIFIED_AND_AWAITING_DEPOSIT = "message.document.cashReceipt.verifiedAndAwaitingDeposit";
        public static final String MSG_VERIFIED_AND_DEPOSITED = "message.document.cashReceipt.verifiedAndDeposited";
        public static final String MSG_CASH_DRAWER_CLOSED_VERIFICATION_NOT_ALLOWED = "message.document.cashReceipt.cashDrawerClosedVerificationNotAllowed";
        public static final String ERROR_NEGATIVE_CHECK_AMOUNT = "error.document.cashReceipt.negativeCheckAmount";
        public static final String ERROR_ZERO_CHECK_AMOUNT = "error.document.cashReceipt.zeroCheckAmount";
        public static final String ERROR_EXCESSIVE_TOTAL = "error.document.cashReceipt.invalidTotal";
        public static final String ERROR_NEGATIVE_TOTAL = "error.document.cashReceipt.negativeTotal";
        public static final String ERROR_ZERO_TOTAL = "error.document.cashReceipt.zeroTotal";
		public static final String ERROR_CONFIRMED_TOTAL = "error.document.cashReceipt.incorrectConfirmedTotal";
        public static final String ERROR_CASH_DRAWER_DOES_NOT_EXIST = "error.document.cashReceipt.cashDrawerDoesNotExist";
        public static final String ERROR_CHANGE_REQUEST = "error.document.cashReceipt.invalidChangeRequest";
    }

    public static final class CreditCardReceipt {
        public static final String ERROR_DOCUMENT_CREDIT_CARD_RECEIPT_REQ_NUMBER_RECEIPTS_NOT_MET = "error.document.creditCardReceipt.requiredNumberOfCreditCardReceiptsNotMet";
        public static final String ERROR_DOCUMENT_CREDIT_CARD_RECEIPT_OUT_OF_BALANCE = "error.document.creditCardReceipt.balance";
        public static final String ERROR_DOCUMENT_CREDIT_CARD_BANK_MUST_EXIST_WHEN_BANK_ENHANCEMENT_ENABLED = "error.document.creditCardReceipt.bankMustExistWhenBankEnhancementEnabled";
    }

    public static final class Deposit {
        public static final String DEPOSIT_TYPE_INTERIM = "deposit.type.interim";
        public static final String DEPOSIT_TYPE_FINAL = "deposit.type.final";

        public static final String ERROR_NO_CASH_RECEIPTS_SELECTED = "error.deposit.noCashReceiptsSelected";

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
        public static final String ERROR_NO_VERIFIED_CASH = "depositWizard.status.noCashReceipts";

        public static final String ERROR_CASH_DRAWER_CORRECTION_NEGATIVE_AMOUNT = "error.cashDrawerCorrection.negativeAmount";
    }

    public static final class CashDrawerMaintenance {
        public static final String HUNDRED_DOLLAR_AMOUNT_NEGATIVE = "error.document.cashDrawer.hundredDollarAmountNegative";
        public static final String FIFTY_DOLLAR_AMOUNT_NEGATIVE = "error.document.cashDrawer.fifyDollarAmountNegative";
        public static final String TWENTY_DOLLAR_AMOUNT_NEGATIVE = "error.document.cashDrawer.twentyDollarAmountNegative";
        public static final String TEN_DOLLAR_AMOUNT_NEGATIVE = "error.document.cashDrawer.tenDollarAmountNegative";
        public static final String FIVE_DOLLAR_AMOUNT_NEGATIVE = "error.document.cashDrawer.fiveDollarAmountNegative";
        public static final String TWO_DOLLAR_AMOUNT_NEGATIVE = "error.document.cashDrawer.twoDollarAmountNegative";
        public static final String ONE_DOLLAR_AMOUNT_NEGATIVE = "error.document.cashDrawer.oneDollarAmountNegative";
        public static final String OTHER_DOLLAR_AMOUNT_NEGATIVE = "error.document.cashDrawer.otherDollarAmountNegative";
        public static final String HUNDRED_CENT_AMOUNT_NEGATIVE = "error.document.cashDrawer.hundredCentAmountNegative";
        public static final String FIFTY_CENT_AMOUNT_NEGATIVE = "error.document.cashDrawer.fiftyCentAmountNegative";
        public static final String TWENTY_FIVE_CENT_AMOUNT_NEGATIVE = "error.document.cashDrawer.twentyFiveCentAmountNegative";
        public static final String TEN_CENT_AMOUNT_NEGATIVE = "error.document.cashDrawer.tenCentAmountNegative";
        public static final String FIVE_CENT_AMOUNT_NEGATIVE = "error.document.cashDrawer.fiveCentAmountNegative";
        public static final String ONE_CENT_AMOUNT_NEGATIVE = "error.document.cashDrawer.oneCentAmountNegative";
        public static final String OTHER_CENT_AMOUNT_NEGATIVE = "error.document.cashDrawer.otherCentAmountNegative";
        public static final String CASH_DRAWER_NOT_CLOSED = "error.document.cashDrawer.cashDrawerNotClosed";
    }

    public static final class JournalVoucher {
        public static final String ERROR_NEGATIVE_NON_BUDGET_AMOUNTS = "error.document.journalVoucher.negativeNonBudgetAmounts";
    }

    public static final class AuxiliaryVoucher {
        public static final String ERROR_ACCOUNTING_PERIOD_OUT_OF_RANGE = "error.document.auxiliaryVoucher.accountingPeriodOutOfRange";
        public static final String ERROR_DIFFERENT_CHARTS = "error.document.auxiliaryVoucher.differentCharts";
        public static final String ERROR_DIFFERENT_SUB_FUND_GROUPS = "error.document.auxiliaryVoucher.differentSubFundGroups";
        public static final String ERROR_INVALID_ACCRUAL_REVERSAL_DATE = "error.document.auxiliaryVoucher.invalidAccrualReversalDate";
    }

    public static final class SufficientFunds {
        public static final String ERROR_INSUFFICIENT_FUNDS = "error.account.insufficientFunds";
    }

    public static final class IndirectCostAdjustment {
        public static final String ERROR_DOCUMENT_ICA_GRANT_INVALID_CHART_OF_ACCOUNTS = "error.document.IndirectCostAdjustment.grant.invalidChartOfAccount";
        public static final String ERROR_DOCUMENT_ICA_RECEIPT_INVALID_CHART_OF_ACCOUNTS = "error.document.IndirectCostAdjustment.receipt.invalidChartOfAccount";
        public static final String ERROR_DOCUMENT_ICA_GRANT_INVALID_ACCOUNT = "error.document.IndirectCostAdjustment.receipt.invalidAccount";
    }

    public static final class IndirectCostRecovery {
        public static final String ERROR_DOCUMENT_ICR_WILDCARDS_MUST_MATCH = "error.document.IndirectCostRecovery.wildcardsMustMatch";
        public static final String ERROR_DOCUMENT_ICR_WILDCARDS_MUST_MATCH_OBJCD_SUBACCT = "error.document.IndirectCostRecovery.wildcardsMustMatchObjCdSubAcct";
        public static final String ERROR_DOCUMENT_ICR_WILDCARD_NOT_VALID = "error.document.IndirectCostRecovery.wildcardNotValid";
        public static final String ERROR_DOCUMENT_ICR_WILDCARDS_NOT_VALID_OBJCD_ACCOUNT= "error.document.IndirectCostRecovery.wildcardsNotValidObjCdAccount";
        public static final String ERROR_DOCUMENT_ICR_MULTIPLE_WILDCARDS_ON_ITEM = "error.document.IndirectCostRecovery.multipleWildcardsOnItem";
        public static final String ERROR_DOCUMENT_ICR_SUB_OBJ_ACTUAL_VALUE_WITH_WILDCARDS = "error.document.IndirectCostRecovery.subObjActualValueOnlyIfNoWildcards";
        public static final String ERROR_DOCUMENT_ICR_CANNOT_BE_WILDCARD = "error.document.IndirectCostRecovery.cannotBeWildcard";
        public static final String ERROR_DOCUMENT_ICR_CHART_CODE_NOT_ONLY_WILDCARD = "error.document.IndirectCostRecovery.chartCodeNotOnlyWildcard";
        public static final String ERROR_DOCUMENT_ICR_RATE_PERCENTS_NOT_EQUAL = "error.document.IndirectCostRecovery.ratePercentsNotEqual";
        public static final String ERROR_DOCUMENT_ICR_RATE_NOT_FOUND = "error.document.IndirectCostRecovery.rateNotFound";
        public static final String ERROR_DOCUMENT_ICR_RATE_PERCENT_INVALID_FORMAT_SCALE = "error.document.IndirectCostRecovery.ratePercentsInvalidFormatScale";
        public static final String ERROR_DOCUMENT_ICR_RATE_PERCENT_INVALID_FORMAT_ZERO = "error.document.IndirectCostRecovery.ratePercentsInvalidFormatZero";
        public static final String ERROR_DOCUMENT_ICR_EXSISTENCE_CHART_CODE = "error.document.IndirectCostRecovery.existence.chartCode";
        public static final String ERROR_DOCUMENT_ICR_EXSISTENCE_OBJECT_CODE = "error.document.IndirectCostRecovery.existence.objectCode";
        public static final String ERROR_DOCUMENT_ICR_EXSISTENCE_OBJECT_CODE_DELETE = "error.document.IndirectCostRecovery.existence.objectCode.delete";

        public static final String ERROR_DOCUMENT_ICR_FIELD_MUST_BE_DASHES = "error.document.IndirectCostRecovery.fieldMustBeDashes";
        public static final String ERROR_DOCUMENT_ICR_FIELD_MUST_NOT_BE_DASHES = "error.document.IndirectCostRecovery.fieldMustNotBeDashes";
        public static final String ERROR_DOCUMENT_ICR_ACCOUNT_USE_EXPENDITURE_ENTRY_WILDCARD_RESTRICTION_ON_SUB_ACCOUNT = "error.document.IndirectCostRecovery.accountUseExpenditureEntryWildcardRestrictionOnSubAccount";
    }

    public static final class AccountingLineParser {
        public static final String ERROR_INVALID_FILE_FORMAT = "error.accountingLineParser.invalidFileFormat";
        public static final String ERROR_INVALID_PROPERTY_VALUE = "error.accountingLineParser.invalidPropertyValue";
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
        public static final String NOTIFICATION_EMAIL_SENT = "collector.notificationEmailSent";
        public static final String EMAIL_SEND_ERROR = "error.collector.emailSendError";
        public static final String MISSING_HEADER_RECORD = "error.collector.header.record.missing";
        public static final String MISSING_TRAILER_RECORD = "error.collector.trailer.record.missing";
        public static final String HEADER_CHART_CODE_REQUIRED = "error.collector.header.chart.code.required";
        public static final String HEADER_ORGANIZATION_CODE_REQUIRED = "error.collector.header.organization.code.required";
        public static final String HEADER_CAMPUS_CODE_REQUIRED = "error.collector.header.campus.code.required";
        public static final String HEADER_PHONE_NUMBER_REQUIRED = "error.collector.header.phone.number.required";
        public static final String HEADER_MAILING_ADDRESS_REQUIRED = "error.collector.header.mailing.address.required";
        public static final String HEADER_DEPARTMENT_NAME_REQUIRED = "error.collector.header.department.name.required";
        public static final String HEADER_BAD_TRANSMISSION_DATE_FORMAT = "error.collector.header.bad.transmission.date.format";
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

    public static final String SUB_ACCOUNT_TRICKLE_DOWN_INACTIVATION = "note.trickleDownInactivation.inactivatedSubAccounts";
    public static final String SUB_ACCOUNT_TRICKLE_DOWN_INACTIVATION_ERROR_DURING_PERSISTENCE = "note.trickleDownInactivation.inactivatedSubAccounts.errorDuringPersistence";
    public static final String SUB_ACCOUNT_TRICKLE_DOWN_INACTIVATION_RECORD_ALREADY_MAINTENANCE_LOCKED = "note.trickleDownInactivation.inactivatedSubAccounts.recordAlreadyMaintenanceLocked";

    public static final String SUB_OBJECT_TRICKLE_DOWN_INACTIVATION = "note.trickleDownInactivation.inactivatedSubObjects";
    public static final String SUB_OBJECT_TRICKLE_DOWN_INACTIVATION_ERROR_DURING_PERSISTENCE = "note.trickleDownInactivation.inactivatedSubObjects.errorDuringPersistence";
    public static final String SUB_OBJECT_TRICKLE_DOWN_INACTIVATION_RECORD_ALREADY_MAINTENANCE_LOCKED = "note.trickleDownInactivation.inactivatedSubObjects.recordAlreadyMaintenanceLocked";

    public static final String ORGANIZATION_REVERSION_DETAIL_TRICKLE_DOWN_INACTIVATION = "note.trickleDownInactivation.inactivatedOrganizationReversionDetail";
    public static final String ORGANIZATION_REVERSION_DETAIL_TRICKLE_DOWN_INACTIVATION_ERROR_DURING_PERSISTENCE = "note.trickleDownInactivation.inactivatedOrganizationReversionDetail.errorDuringPersistence";
    public static final String ORGANIZATION_REVERSION_DETAIL_TRICKLE_DOWN_ACTIVATION = "note.trickleDownActivation.activatedOrganizationReversionDetail";
    public static final String ORGANIZATION_REVERSION_DETAIL_TRICKLE_DOWN_ACTIVATION_ERROR_DURING_PERSISTENCE = "note.trickleDownActivation.activatedOrganizationReversionDetail.errorDuringPersistence";


    public static final class AccountingLineViewRendering {
        public static final String ACCOUNTING_LINE_ACTIONS_LABEL = "accounting.line.actions.header.label";
        public static final String ACCOUNTING_LINE_BALANCE_INQUIRY_ACTION_LABEL = "accounting.line.actions.balanceInquiry.label";
        public static final String ACCOUNTING_LINE_DELETE_ACTION_LABEL = "accounting.line.actions.delete.label";
        public static final String ACCOUNTING_LINE_ADD_ACTION_LABEL = "accounting.line.actions.add.label";
        public static final String ACCOUNTING_LINE_COPY_ACTION_LABEL = "accounting.line.actions.copy.label";
        public static final String ACCOUNTING_LINE_REFRESH_ACTION_LABEL = "accounting.line.actions.refresh.label";
    }

    public static final class Bank {
        public static final String ERROR_MISSING_CASH_ACCOUNT_NUMBER = "error.document.bank.missingCashAccountNumber";
        public static final String ERROR_MISSING_CASH_OBJECT_CODE = "error.document.bank.missingCashObjectCode";
        public static final String DESCRIPTION_GLPE_BANK_OFFSET = "description.glpe.bankOffset";
        public static final String ERROR_DEPOSIT_NOT_SUPPORTED = "error.document.bank.depositNotSupported";
        public static final String ERROR_DISBURSEMENT_NOT_SUPPORTED = "error.document.bank.disbursementNotSupported";
        public static final String ERROR_ACCOUNT_NUMBER_NOT_UNIQUE = "error.document.bank.accountNumberNotUnique";
    }

    public static final String LABEL_NEW_ACCOUNTING_LINE_FIELD = "accounting.line.field.newLine.label";
    public static final String LABEL_ACCOUNTING_LINE_FIELD = "accounting.line.field.line.label";
    public static final String LABEL_ACCOUNTING_LINE_QUICKFINDER_ACCESSIBLE_TITLE = "accounting.line.quickfinder.accessible.label";
    public static final String ERROR_INVALID_CAPITAL_ASSET_QUANTITY = "error.document.capitalAssetEdit.invalidAssetQuantity";
    public static final String ERROR_DV_PAYMENT_REASON_NOT_SELECTED = "error.document.paymentReasonNotSelected";

    public static final String WARNING_DV_PAYMENT_REASON_VALID_FOR_MULTIPLE_PAYEE_TYPES = "warning.dv.paymentReason.mutilpeValidPayeeTypes";
    public static final String WARNING_DV_PAYMENT_REASON_VALID_FOR_SINGEL_PAYEE_TYPE = "warning.dv.paymentReason.singleValidPayeeType";
    public static final String WARNING_DV_REASERCH_PAYMENT_REASON = "warning.dv.paymentReason.research";
    public static final String WARNING_DV_MOVING_PAYMENT_REASON = "warning.dv.paymentReason.moving";
    public static final String WARNING_DV_PREPAID_TRAVEL_PAYMENT_REASON = "warning.dv.paymentReason.prepaidTravel";

    public static final String WARNING_DV_PREPAID_TRAVEL_TAB = "warning.dv.paymentReason.prepaidTravelTab";
    public static final String WARNING_DV_NON_EMPLOYEE_TRAVEL_TAB = "warning.dv.paymentReason.nonEmployeeTravelTab";

    public static final String ERROR_DV_VENDOR_NAME_PERSON_NAME_CONFUSION = "error.dv.vendorNamePersonNameConfusion";
    public static final String ERROR_DV_VENDOR_EMPLOYEE_CONFUSION = "error.dv.vendorEmployeeConfusion";
    public static final String ERROR_DV_NAME_NOT_FILLED_ENOUGH = "error.dv.nameNotFilledEnough";
    public static final String ERROR_DV_LOOKUP_NEEDS_SOME_FIELD = "error.dv.lookupNeedsSomeField";
    public static final String ERROR_DV_LOOKUP_TAX_NUMBER_EMPLOYEE_DETAILS_CONFUSION = "error.dv.vendorTaxNumberEmployeeDetailsConfusion";

    public static final class Balancing {
        public static final String ERROR_BATCH_BALANCING_FILES = "error.batch.balancing.files";
        public static final String ERROR_BATCH_BALANCING_UNKNOWN_FAILURE = "error.batch.balancing.unknown.failure";
        public static final String MESSAGE_BATCH_BALANCING_DATA_INSERT = "message.batch.balancing.data.insert";
        public static final String MESSAGE_BATCH_BALANCING_OBSOLETE_FISCAL_YEAR_DATA_DELETED = "message.batch.balancing.obsolete.fiscal.year.data.deleted";
        public static final String MESSAGE_BATCH_BALANCING_FAILURE_COUNT = "message.batch.balancing.failure.count";
        public static final String MESSAGE_BATCH_BALANCING_RECORD_BEFORE_FISCAL_YEAR = "message.batch.balancing.record.before.fiscal.year";
        public static final String MESSAGE_BATCH_BALANCING_RECORD_FAILED_BALANCING = "message.batch.balancing.record.failed.balancing";
        public static final String MESSAGE_BATCH_BALANCING_HISTORY_PURGED = "message.batch.balancing.history.purged";
        public static final String MESSAGE_BATCH_BALANCING_FILE_LISTING = "message.batch.balancing.file.listing";

        public static final String REPORT_UNKNOWN_LABEL = "message.batch.balancing.report.unknown.label";

        public static final String REPORT_ENTRY_LABEL = "message.gl.balancing.report.entry.label";
        public static final String REPORT_BALANCE_LABEL = "message.gl.balancing.report.balance.label";
        public static final String REPORT_ACCOUNT_BALANCE_LABEL = "message.gl.balancing.report.account.balance.label";
        public static final String REPORT_ENCUMBRANCE_LABEL = "message.gl.balancing.report.encumbrance.label";

        public static final String REPORT_FISCAL_YEARS_INCLUDED = "message.gl.balancing.report.fiscal.years.included";
        public static final String REPORT_HISTORY_TABLES_INITIALIZED = "message.gl.balancing.report.history.tables.initialized";
        public static final String REPORT_OBSOLETE_DELETED = "message.gl.balancing.report.obsolete.deleted";
        public static final String REPORT_UPDATED_SKIPPED = "message.gl.balancing.report.updated.skipped";
        public static final String REPORT_COMPARISION_FAILURE = "message.gl.balancing.report.comparision.failure";
        public static final String REPORT_ENTRY_SUM_ROW_COUNT_HISTORY = "message.gl.balancing.report.entry.sum.row.count.history";
        public static final String REPORT_ENTRY_ROW_COUNT_PRODUCTION = "message.gl.balancing.report.entry.row.count.production";
        public static final String REPORT_BALANCE_ROW_COUNT_HISTORY = "message.gl.balancing.report.balance.row.count.history";
        public static final String REPORT_BALANCE_ROW_COUNT_PRODUCTION = "message.gl.balancing.report.balance.row.count.production";
        public static final String REPORT_ACCOUNT_BALANCE_ROW_COUNT_HISTORY = "message.gl.balancing.report.account.balance.row.count.history";
        public static final String REPORT_ACCOUNT_BALANCE_ROW_COUNT_PRODUCTION = "message.gl.balancing.report.account.balance.row.count.production";
        public static final String REPORT_ENCUMBRANCE_ROW_COUNT_HISTORY = "message.gl.balancing.report.encumbrance.row.count.history";
        public static final String REPORT_ENCUMBRANCE_ROW_COUNT_PRODUCTION = "message.gl.balancing.report.encumbrance.row.count.production";
    }

    public static final String NO_MEMBER_SELECTED = "error.member.none.selected";
    public static final String ALREADY_ASSIGNED_MEMBER = "error.member.already.assigned";
    public static final String TO_AMOUNT_OUT_OF_RANGE = "error.member.toamount.outofrange";
    public static final String FROM_AMOUNT_OUT_OF_RANGE = "error.member.fromamount.outofrange";
    public static final String FROM_AMOUNT_GREATER_THAN_TO_AMOUNT = "error.fromamount.greaterthan.toamount";
    public static final String INVALID_DOCTYPE_SELECTED = "error.invalid.doctype.selected";
    public static final String ERROR_CHART_OR_ORG_NOTEMPTY_ALL_REQUIRED = "error.chart.or.org.notempty.all.required";

    public static final String WORKFLOW_DIRECTORY = "data.xml.root.location";
    public static final String INGESTION_DIRECTORY = "ingestion";

    public static final String ERROR_US_REQUIRES_STATE = "error.USRequiresState.required";
    public static final String ERROR_US_REQUIRES_ZIP = "error.USRequiresZip.required";
    public static final String ERROR_POSTAL_CODE_INVALID = "error.postalCode.invalid";

    public static final String MESSAGE_REPORT_NIGHTLY_OUT_LEDGER_TOTAL = "message.report.nightlyOut.ledger.total";
    public static final String MESSAGE_REPORT_NIGHTLY_OUT_LEDGER_BALANCE_TYPE_TOTAL = "message.report.nightlyOut.ledger.balanceType.total";

    public static final String MESSAGE_REPORT_POSTER_OUTPUT_SUMMARY_TOTAL = "message.report.poster.output.summary.total";
    public static final String MESSAGE_REPORT_POSTER_OUTPUT_SUMMARY_BALANCE_TYPE_TOTAL = "message.report.poster.output.summary.balanceType.total";
    public static final String MESSAGE_REPORT_POSTER_OUTPUT_SUMMARY_BALANCE_TYPE_FISCAL_YEAR_TOTAL = "message.report.poster.output.summary.balanceType.fiscalYear.total";
    public static final String MESSAGE_REPORT_POSTER_OUTPUT_SUMMARY_BALANCE_TYPE_FISCAL_YEAR_AND_PERIOD_TOTAL = "message.report.poster.output.summary.balanceType.fiscalYear.fiscalPeriod.total";
    public static final String MESSAGE_REPORT_POSTER_OUTPUT_SUMMARY_TITLE_LINE = "message.report.poster.output.summary.titleLine";
    public static final String MESSAGE_REPORT_YEAR_END_NOMINAL_ACTIVITY_CLOSING_LEDGER_TITLE_LINE = "message.report.yearEnd.nominalActivityClosing.ledger.titleLine";
    public static final String MESSAGE_REPORT_YEAR_END_BALANCE_FORWARD_OPEN_ACCOUNT_LEDGER_TITLE_LINE = "message.report.yearEnd.balanceForwarding.openAccount.ledger.titleLine";
    public static final String MESSAGE_REPORT_YEAR_END_BALANCE_FORWARD_CLOSED_ACCOUNT_LEDGER_TITLE_LINE = "message.report.yearEnd.balanceForwarding.closedAccount.ledger.titleLine";
    public static final String MESSAGE_REPORT_YEAR_END_ENCUMBRANCE_FORWARDS_LEDGER_TITLE_LINE = "message.report.yearEnd.encumbranceForwards.ledger.titleLine";
    public static final String MESSAGE_REPORT_YEAR_END_ORGANIZATION_REVERSION_LEDGER_TITLE_LINE = "message.report.yearEnd.organizationReversion.ledger.titleLine";

    public static final String MESSAGE_ACCOUNTING_LINES_ERROR_SECTION_TITLE = "message.accountingLines.errorSectionTitle";
    public static final String MESSAGE_ACCOUNTING_LINES_WARNING_SECTION_TITLE = "message.accountingLines.warningSectionTitle";
    public static final String MESSAGE_ACCOUNTING_LINES_INFORMATION_SECTION_TITLE = "message.accountingLines.informationSectionTitle";

    public static final String MESSAGE_ACCOUNT_DERIVED_ROLE_PRINCIPAL_INACTIVATED_NOTIFICATION_SUBJECT = "message.accountDerivedRole.principalInactivated.notification.subject";
    public static final String MESSAGE_ACCOUNT_DERIVED_ROLE_PRINCIPAL_INACTIVATED_FISCAL_OFFICER_NOTIFICATION = "message.accountDerivedRole.principalInactivated.fiscalOfficer.notification";
    public static final String MESSAGE_ACCOUNT_DERIVED_ROLE_PRINCIPAL_INACTIVATED_ACCOUNT_SUPERVISOR_NOTIFICATION = "message.accountDerivedRole.principalInactivated.accountSupervisor.notification";
    public static final String MESSAGE_ACCOUNT_DERIVED_ROLE_PRINCIPAL_INACTIVATED_FISCAL_OFFICER_PRIMARY_DELEGATE_NOTIFICATION = "message.accountDerivedRole.principalInactivated.fiscalOfficerPrimaryDelegate.notification";
    public static final String MESSAGE_ACCOUNT_DERIVED_ROLE_PRINCIPAL_INACTIVATED_FISCAL_OFFICER_SECONARY_DELEGATE_NOTIFICATION = "message.accountDerivedRole.principalInactivated.fiscalOfficerSecondaryDelegate.notification";
    public static final String MESSAGE_ACCOUNT_DERIVED_ROLE_PRINCIPAL_INACTIVATED_ACCOUNT_DELEGATE_INACTIVATED_INFORMATION = "message.accountDerivedRole.principalInactivated.accountDelegateInactivatedInformation";
    public static final String MESSAGE_ACCOUNT_DERIVED_ROLE_PRINCIPAL_INACTIVATED_ACCOUNT_DELEGATE_BLOCKED_INACTIVATION_INFORMATION = "message.accountDerivedRole.principalInactivated.accountDelegateBlockedInactivationInformation";

    public static final String QUESTION_BATCH_FILE_ADMIN_DELETE_CONFIRM = "question.batch.file.admin.delete.confirm";
    public static final String MESSAGE_BATCH_FILE_ADMIN_DELETE_SUCCESSFUL = "message.batch.file.admin.delete.successful";
    public static final String MESSAGE_BATCH_FILE_ADMIN_DELETE_ERROR = "message.batch.file.admin.delete.error";
    public static final String MESSAGE_BATCH_FILE_ADMIN_DELETE_CANCELLED = "message.batch.file.admin.delete.cancelled";

    public static final String MESSAGE_BATCH_FILE_LOG_EMAIL_BODY = "message.batch.log.email.body";
    public static final String SALARY_TRANSFER_FRINGE_BENEFIT_INQUIRY_LABEL =  "salary.transfer.fringe.benefit.inquiry.label";

    public static final String ERROR_ACCOUNTING_DOCUMENT_ACCOUNTING_LINE_IMPORT_GENERAL = "error.accountingdocument.accountinglineimport.general";

    public static final String ERROR_LINEPARSER_INVALID_FILE_FORMAT = "error.lineparser.invalid.file.format";

    // inactivation of system params
    public static final String ERROR_CANNOT_INACTIVATE_USED_IN_SYSTEM_PARAMETERS = "error.document.cannot.inactivate.used.in.system.parameters";
    public static final String ERROR_CANNOT_INACTIVATE_USED_BY_ACTIVE_RECORDS = "error.document.cannot.inactivate.used.by.active.records";

    // Recurring Cash Transfer Maintenance Errors
    public static final String ERROR_TRANSACTION_TYPE_INVALID = "error.document.transactionType.invalid";

    public static final String ERROR_COLLECTOR_EMAILSEND_NOTIFICATION_SUBJECT = "error.collector.emailSend.notification.subject";
    public static final String ERROR_COLLECTOR_EMAILSEND_NOTIFICATION_BODY = "error.collector.emailSend.notification.body";

    /**
     * This class stores message keys for the SemaphoreInputFileType upload page
     */
    public static final class Semaphore {
        public static final String MESSAGE_BATCH_UPLOAD_TITLE_PREFIX = "message.sys.batch.semaphore.upload.title";
        public static final String ERROR_BATCH_UPLOAD_INVALID_STEP = "error.sys.batch.semaphore.upload.invalidStep";
        public static final String ERROR_BATCH_UPLOAD_DELETE_DONE_FILE = "error.sys.batch.semaphore.upload.deleteDoneFile";
    }

    //capital asset information specific errors...
    public static final String ERROR_ASSET_ACCOUNT_NUMBER_LINE_NOT_IN_SOURCE_OR_TARGET_ACCOUNTING_LINES = "error.asset.accounting.lines.not.in.source.or.target.accountLines";
    public static final String ERROR_ASSET_LINE_AMOUNT_NOT_EQUAL_TO_DISTRIBUTED_ACCOUNTING_LINES = "error.asset.amount.not.equalDistributedAccountingLines";
}

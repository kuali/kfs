/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.bc;

/**
 * Constants for message keys. Should have corresponding key=message in resources.
 */
public class BCKeyConstants {
    public static final String QUESTION_CONFIRM_CLEANUP = "document.budget.question.confirmCleanup.text";
    public static final String QUESTION_DELETE = "document.question.delete.text";
    public static final String QUESTION_CONFIRM_MONTHLY_OVERRIDE = "document.budget.question.confirmMonthlyOverride.text";

    public static final String MESSAGE_BUDGET_DOCUMENT_NOT_BUDGETABLE = "message.budget.documentNotBudgetable";
    public static final String MESSAGE_BUDGET_NOCREATE_DOCUMENT = "message.budget.nocreate.document";
    public static final String MESSAGE_BUDGET_PREVIOUS_SESSION_NOTCLEANED = "message.budget.system.session.notCleaned";
    public static final String MESSAGE_BUDGET_PREVIOUS_SESSION_TIMEOUT = "message.budget.system.session.timeOut";
    public static final String MESSAGE_BUDGET_SYSTEM_NOT_ACTIVE = "message.budget.system.not.active";
    public static final String MESSAGE_BUDGET_SYSTEM_MULTIPLE_ACTIVE = "message.budget.system.multiple.active";
    public static final String MESSAGE_BUDGET_SYSTEM_VIEW_ONLY = "message.budget.systemViewOnly";
    public static final String MESSAGE_BUDGET_VIEW_ONLY = "message.budget.viewOnly";
    public static final String MESSAGE_BUDGET_EDIT_ACCESS = "message.budget.editAccess";
    public static final String MESSAGE_BUDGET_SUCCESSFUL_CLOSE = "message.budget.successfulClose";
    public static final String MESSAGE_BENEFITS_CALCULATED = "message.budget.benefitsCalculated";
    public static final String MESSAGE_BENEFITS_MONTHLY_CALCULATED = "message.budget.benefitsMonthlyCalculated";
    public static final String MESSAGE_SALARY_SETTING_SAVED = "message.budget.salarySettingSaved";
    public static final String MESSAGE_SALARY_SETTING_SAVED_AND_CLOSED = "message.budget.salarySettingSavedAndClosed";
    public static final String MESSAGE_MONTHLY_ANNUAL_OVERRIDE_SAVED = "message.budget.monthlyAnnualOverrideSaved";

    public static final String ERROR_BUDGET_USER_NOT_ORG_APPROVER = "error.budget.userNotOrgApprover";
    public static final String ERROR_BUDGET_USER_BELOW_DOCLEVEL = "error.budget.userBelowDocLevel";
    public static final String ERROR_BUDGET_USER_NOT_IN_HIERARCHY = "error.budget.userNotInHierarchy";
    public static final String ERROR_BUDGET_DOCUMENT_LOCKED = "error.budget.documentLocked";
    public static final String ERROR_BUDGET_FUNDING_LOCKED = "error.budget.fundingLocked";
    public static final String ERROR_BUDGET_DOCUMENT_OTHER = "error.budget.documentOther";

    public static final String ERROR_BUDGET_AUTHORIZATION_DOCUMENT = "error.budget.authorization.document";
    public static final String ERROR_BUDGET_DOCUMENT_NOT_BUDGETABLE = "error.budget.documentNotBudgetable";
    public static final String ERROR_BUDGET_PULLUP_DOCUMENT = "error.budget.pullup.document";
    public static final String ERROR_BUDGET_PUSHDOWN_DOCUMENT = "error.budget.pushdown.document";
    public static final String ERROR_BUDGET_SUBFUND_NOT_SELECTED = "error.budget.subFundNotSelected";
    public static final String ERROR_BUDGET_OBJECT_CODE_NOT_SELECTED = "error.budget.objectCodeNotSelected";
    public static final String ERROR_BUDGET_REASON_CODE_NOT_SELECTED = "error.budget.reasonCodeNotSelected";
    public static final String ERROR_BUDGET_THRESHOLD_PERCENT_NEEDED = "error.budget.thresholdPercentNeeded";
    public static final String ERROR_BUDGET_ORG_NOT_SELECTED = "error.budget.orgNotSelected";
    public static final String ERROR_BUDGET_LINE_EXISTS = "error.budget.lineExists";
    public static final String ERROR_BUDGET_LINE_REINSTATED = "error.budget.lineReinstated";
    public static final String ERROR_LABOR_OBJECT_IN_NOWAGES_ACCOUNT = "error.budget.laborObjectInNoWagesAccount";
    public static final String ERROR_FRINGE_BENEFIT_OBJECT_NOT_ALLOWED = "error.budget.fringeBenefitObjectNotAllowed";
    public static final String ERROR_SALARY_SETTING_OBJECT_ONLY = "error.budget.salarySettingObjectOnly";
    public static final String ERROR_SALARY_SETTING_OBJECT_ONLY_NO_PARAMETER = "error.budget.salarySettingObjectOnlyNoParameter";
    public static final String ERROR_NO_BUDGET_ALLOWED = "error.budget.noBudgetAllowed";
    public static final String ERROR_BUDGET_RECORDING_LEVEL_NOT_ALLOWED = "error.budget.budgetRecordingLevelNotAllowed";
    public static final String ERROR_SUB_ACCOUNT_TYPE_NOT_ALLOWED = "error.budget.subAccountTypeNotAllowed";
    public static final String ERROR_NO_DELETE_ALLOWED_WITH_BASE = "error.budget.noDeleteAllowedWithBase";
    public static final String ERROR_NO_DELETE_ALLOWED_SALARY_DETAIL = "error.budget.noDeleteAllowedSalaryDetail";
    public static final String ERROR_MONTHLY_SUM_REQUEST_NOT_EQUAL = "error.budget.monthlySumRequestNotEqual";
    public static final String ERROR_SALARY_SUM_REQUEST_NOT_EQUAL = "error.budget.salarySumRequestNotEqual";
    public static final String ERROR_MONTHLY_DETAIL_SALARY_OVERIDE = "error.budget.monthlyDetailSalarySettingNoRequestOverride";
    public static final String ERROR_MONTHLY_TOTAL_ZERO = "error.budget.monthlyTotalZero";
    public static final String ERROR_BUDGET_ACCOUNT_ORGANIZATION_HIERARCHY = "error.budget.AccountOrganizationHierarchy";
    public static final String ERROR_BUDGET_REASONMAINT_INACTIVATE_REASONEXIST = "error.budget.reasonMaintenance.inactivate.reasonExist";
    public static final String ERROR_BUDGET_OBJECT_TYPE_INVALID_REVENUE = "error.budget.object.type.invalid.revenue";
    public static final String ERROR_BUDGET_OBJECT_TYPE_INVALID_EXPENSE = "error.budget.object.type.invalid.expense";

    public static final String ERROR_REPORT_GETTING_CHART_DESCRIPTION = "error.budget.report.gettingChartDescription";
    public static final String ERROR_REPORT_GETTING_OBJECT_CODE = "error.budget.report.gettingObjectCode";
    public static final String ERROR_REPORT_GETTING_OBJECT_NAME = "error.budget.report.gettingObjectName";
    public static final String ERROR_REPORT_GETTING_OBJECT_LEVEL_NAME = "error.budget.report.gettingObjectLevelName";
    public static final String ERROR_REPORT_GETTING_ACCOUNT_DESCRIPTION = "error.budget.report.gettingAccountDescription";
    public static final String ERROR_REPORT_GETTING_SUB_ACCOUNT_DESCRIPTION = "error.budget.report.gettingSubAccountDescription";
    public static final String ERROR_REPORT_GETTING_ORGANIZATION_NAME = "error.budget.report.gettingOrganizationName";
    public static final String ERROR_REPORT_GETTING_FUNDGROUP_NAME = "error.budget.report.gettingFundGroupName";
    public static final String ERROR_REPORT_GETTING_FUNDGROUP_CODE = "error.budget.report.gettingFundGroupCode";
    public static final String ERROR_REPORT_GETTING_SUBFUNDGROUP_DESCRIPTION = "error.budget.report.gettingSubFundGroupDescription";
    public static final String MSG_REPORT_HEADER_ACCOUNT_SUB = "message.budget.report.header.accountSub";
    public static final String MSG_REPORT_HEADER_ACCOUNT_SUB_NAME = "message.budget.report.header.accountSubName";
    public static final String MSG_REPORT_HEADER_BASE_AMOUNT = "message.budget.report.header.baseAmount";
    public static final String MSG_REPORT_HEADER_REQ_AMOUNT = "message.budget.report.header.reqAmount";
    public static final String MSG_REPORT_HEADER_CHANGE = "message.budget.report.header.change";
    public static final String MSG_REPORT_INCOME_EXP_DESC_REVENUE = "message.budget.report.incomeExpDescRevenue";
    public static final String MSG_REPORT_INCOME_EXP_DESC_UPPERCASE_REVENUE = "message.budget.report.incomeExpDescUppercaseRevenue";
    public static final String MSG_REPORT_INCOME_EXP_DESC_EXP_GROSS = "message.budget.report.incomeExpDescExpGross";
    public static final String MSG_REPORT_INCOME_EXP_DESC_TRNFR_IN = "message.budget.report.incomeExpDescTrnfrIn";
    public static final String MSG_REPORT_INCOME_EXP_DESC_EXP_NET_TRNFR = "message.budget.report.incomeExpDescExpNetTrnfr";
    public static final String MSG_REPORT_INCOME_EXP_DESC_EXPENDITURE_NET_TRNFR = "message.budget.report.incomeExpDescExpenditureNetTrnfr";
    public static final String MSG_REPORT_INCOME_EXP_DESC_EXPENDITURE = "message.budget.report.incomeExpDescExpenditure";

    public static final String MSG_REPORT_HEADER_SUBFUND = "message.budget.report.header.subFund";
    public static final String MSG_REPORT_HEADER_SUBFUND_DESCRIPTION = "message.budget.report.header.subFundDescription";

    public static final String ERROR_FILE_IS_REQUIRED = "error.budget.requestImport.missingFile";
    public static final String ERROR_FILE_EMPTY = "error.budget.requestImport.emptyFile";
    public static final String ERROR_FILE_TYPE_IS_REQUIRED = "error.budget.requestImport.missingFileType";
    public static final String ERROR_FILENAME_REQUIRED = "error.budget.requestImport.missingFileName";
    public static final String ERROR_FIELD_SEPARATOR_REQUIRED = "error.budget.requestImport.missingFieldSeparator";
    public static final String ERROR_TEXT_DELIMITER_REQUIRED = "error.budget.requestImport.missingTextFieldDelimiter";
    public static final String ERROR_DISTINCT_DELIMITERS_REQUIRED = "error.budget.requestImport.nonDistinctDelimiters";
    public static final String ERROR_BUDGET_YEAR_REQUIRED = "error.budget.requestImport.missingBudgetYear";
    public static final String MSG_UNLOCK_CONFIRMATION = "message.budget.lock.unlockConfirmation";
    public static final String MSG_LOCK_NOTEXIST = "message.budget.lock.lockNotExist";
    public static final String MSG_UNLOCK_SUCCESSFUL = "message.budget.lock.unlockSuccessful";
    public static final String MSG_UNLOCK_NOTSUCCESSFUL = "message.budget.lock.unlockNotSuccessful";
    public static final String MSG_LOCK_POSITIONKEY = "message.budget.lock.positionKey";
    public static final String MSG_LOCK_POSITIONFUNDINGKEY = "message.budget.lock.positionFundingKey";
    public static final String MSG_LOCK_ACCOUNTKEY = "message.budget.lock.accountKey";
    public static final String ERROR_LOCK_INVALID_USER = "error.budget.lock.invalidUser";
    public static final String ERROR_PAYRATE_IMPORT_UPDATE_NOT_ALLOWED = "error.budget.payrateImportExport.updatesNotAllowed";
    public static final String ERROR_NO_RECORDS_MY_ACCOUNTS = "error.budget.noRecordsMyAccounts";
    public static final String ERROR_NO_ACCOUNTS_PUSH_DOWN = "error.budget.noAccountsPushDown";
    public static final String ERROR_NO_ACCOUNTS_PULL_UP = "error.budget.noAccountsPullUp";

    public static final String MSG_ACCOUNT_PULLUP_LIST = "message.budget.accountPullupList";
    public static final String MSG_ACCOUNT_PUSHDOWN_LIST = "message.budget.accountPushdownList";
    public static final String MSG_ACCOUNT_MANAGER_DELEGATE_LIST = "message.budget.accountManagerDelegateList";
    public static final String MSG_ORG_PULL_UP_SUCCESSFUL = "message.budget.orgPullUpSuccessful";
    public static final String MSG_ORG_PUSH_DOWN_SUCCESSFUL = "message.budget.orgPushDownSuccessful";

    public static final String ERROR_PAYRATE_IMPORT_ABORTED = "error.budget.payrate.importAborted";
    public static final String ERROR_PAYRATE_UPDATE_ABORTED = "error.budget.payrate.updateAborted";
    public static final String ERROR_PAYRATE_IMPORT_NO_PAYROLL_MATCH = "error.budget.payrate.noPayrollMatch";
    public static final String ERROR_PAYRATE_NO_ACTIVE_FUNDING_RECORDS = "error.budget.payrate.noActiveFundingRecords";
    public static final String ERROR_PAYRATE_NO_BUDGET_DOCUMENT = "error.budget.payrate.noBudgetDocument";
    public static final String ERROR_PAYRATE_OBJECT_LEVEL_ERROR = "error.budget.payrate.objectLevelError";
    public static final String ERROR_PAYRATE_ACCOUNT_LOCK_EXISTS = "error.budget.payrate.accountLockExists";
    public static final String ERROR_PAYRATE_FUNDING_LOCK_EXISTS = "error.budget.payrate.fundingLockExists";
    public static final String ERROR_PAYRATE_BATCH_ACCOUNT_LOCK_FAILED = "error.budget.payrate.batchAccountLockFailed";
    public static final String ERROR_PAYRATE_NO_UPDATE_FTE_ZERO_OR_BLANK = "error.budget.payrate.noUpdateFteZero";

    public static final String MSG_PAYRATE_IMPORT_LOG_FILE_HEADER_LINE = "message.budget.payrate.headerLine";
    public static final String MSG_PAYRATE_IMPORT_NO_IMPORT_RECORDS = "message.budget.payrate.noImportRecords";
    public static final String MSG_PAYRATE_IMPORT_COUNT = "message.budget.payrate.importCount";
    public static final String MSG_PAYRATE_IMPORT_COMPLETE = "message.budget.payrate.importComplete";
    public static final String MSG_PAYRATE_IMPORT_UPDATE_COMPLETE = "message.budget.payrate.updateComplete";
    public static final String MSG_PAYRATE_IMPORT_LOG_FILE_FOOTER = "message.budget.payrate.footerLine";

    public static final String ERROR_REQUIRED_FOR_GET_NEW_POSITION = "error.budget.requiredGetNewPosition";
    public static final String ERROR_EXTERNAL_POSITION_NOT_FOUND = "error.budget.externalPositionNotFound";
    public static final String ERROR_BUDGET_POSITION_ALREADY_EXISTS = "error.budget.positionAlreadyExists";
    public static final String ERROR_REQUIRED_FOR_GET_NEW_INCUMBENT = "error.budget.requiredGetNewIncumbent";
    public static final String ERROR_BUDGET_INCUMBENT_ALREADY_EXISTS = "error.budget.incumbentAlreadyExists";
    public static final String ERROR_EXTERNAL_INCUMBENT_NOT_FOUND = "error.budget.externalIncumbentNotFound";
    public static final String ERROR_POSITION_LOCK_NOT_OBTAINED = "error.budget.positionLockNotObtained";

    public static final String ERROR_PAYRATE_EXPORT_INVALID_POSITION_UNION_CODE = "error.budget.payrate.export.invalidPositionUnionCode";
    public static final String ERROR_PAYRATE_EXPORT_POSITION_UNION_CODE_REQUIRED = "error.budget.payrate.export.positionUnionCodeRequired";
    public static final String ERROR_PAYRATE_EXPORT_CSF_FREEZE_DATE_REQUIRED = "error.budget.payrate.export.CsfFreezeDateRequired";
    public static final String ERROR_PAYRATE_EXPORT_CSF_FREEZE_DATE_INCORRECT_FORMAT = "error.budget.payrate.export.CsfFreezeDateIncorrectFormat";

    public static final String ERROR_FAIL_TO_LOCK_POSITION = "error.budget.failToLockPosition";
    public static final String ERROR_FAIL_TO_LOCK_FUNDING = "error.budget.failToLockFunding";
    public static final String ERROR_FAIL_TO_UPDATE_FUNDING_ACCESS = "error.budget.failToUpdateFundingAccess";

    public static final String ERROR_INCUMBENT_NOT_FOUND = "error.budget.incumbentNotFound";
    public static final String ERROR_POSITION_NOT_FOUND = "error.budget.positionNotFound";
    public static final String ERROR_FAIL_TO_ACQUIRE_TRANSACTION_LOCK = "error.budget.failToAcquireTransactionLock";
    public static final String ERROR_SALARY_SETTING_EXPANSION_NOT_FOUND = "error.budget.salarySettingExpansionNotFound";
    public static final String ERROR_ADJUSTMENT_AMOUNT_REQUIRED = "error.budget.adjustmentAmountRequired";
    public static final String ERROR_ADJUSTMENT_PERCENT_REQUIRED = "error.budget.adjustmentPercentRequired";
    public static final String ERROR_REQUESTED_AMOUNT_NONNEGATIVE_REQUIRED = "error.budget.requestedSalaryIsNonnegative";
    public static final String ERROR_REQUESTED_AMOUNT_NEEDS_FTE_FIRST = "error.budget.requestedSalaryNeedsFteFirst";
    public static final String ERROR_FTE_GREATER_THAN_ZERO_REQUIRED = "error.budget.fteAmountGreaterThanZero";
    public static final String ERROR_BUDGET_DOCUMENT_NOT_FOUND = "error.budget.budgetDocumentNotFound";
    public static final String ERROR_EMPTY_PAY_RATE_ANNUAL_AMOUNT = "error.budget.emptyPayRateAnnualAmount";

    public static final String WARNING_FTE_NOT_EQUAL = "warning.budget.fteNotEqual";
    public static final String WARNING_FTE_NOT_ONE = "warning.budget.fteNotOne";
    public static final String WARNING_WORKING_HOUR_NOT_EQUAL = "warning.budget.workingHourNotEqual";
    public static final String WARNING_RECALCULATE_NEEDED = "warning.budget.recalculateNeeded";

    public static final String ERROR_NO_ACTIVE_JOB_FOUND = "error.budget.noActiveJobFound";
    public static final String ERROR_TIME_PERCENT_GREATER_THAN_ZERO_REQUIRED = "error.budget.timePercentGreaterThanZero";
    public static final String ERROR_NOT_EQUAL_NORMAL_WORK_MONTHS = "error.budget.notEqualNormalWorkMonths";
    public static final String ERROR_FUNDIN_MONTH_NOT_IN_RANGE = "error.budget.fundingMonthNotInRange";
    public static final String ERROR_EMPTY_FUNDIN_MONTH = "error.budget.emptyFundingMonth";
    public static final String ERROR_DUPLICATE_FUNDING_LINE = "error.budget.duplicateFundingLine";
    public static final String ERROR_NOT_DEFAULT_OBJECT_CODE = "error.budget.notDefaultObjectCode";
    public static final String ERROR_REQUEST_AMOUNT_NOT_ZERO_WHEN_FULL_YEAR_LEAVE = "error.budget.requestedAmountNotZeroWhenFullYearleave";
    public static final String ERROR_REQUEST_FTE_NOT_ZERO_WHEN_FULL_YEAR_LEAVE = "error.budget.requestedFteNotZeroWhenFullYearleave";
    public static final String ERROR_EMPTY_REQUESTED_TIME_PERCENT = "error.budget.emptyRequestedTimePercent";
    public static final String ERROR_DETAIL_POSITION_NOT_REQUIRED = "error.budget.detailPositionNotRequired";
    public static final String ERROR_FTE_QUANTITY_NOT_IN_RANGE = "error.budget.fteQuantityNotInRange";
    public static final String ERROR_LEAVE_TIME_PERCENT_NOT_IN_RANGE = "error.budget.leaveTimePercentNotInRange";
    public static final String ERROR_TIME_PERCENT_NOT_IN_RANGE = "error.budget.timePercentNotInRange";
    public static final String ERROR_CANNOT_ADJUST_FUNDING_WITHOUT_EFFECTIVE_CSF_TRACKER = "error.budget.cannotAdjustFundingWithoutEffectiveCSFTracker";
    public static final String ERROR_CANNOT_ADJUST_FUNDING_MARKED_AS_DELETE = "error.budget.cannotAdjustFundingMarkedAsDelete";
    public static final String WARNING_AUTHORIZATION_DISABLED = "warning.budget.authorizationDisabled";

    public static final String ERROR_NO_SALARY_SETTING_PERMISSION = "error.budget.noSalarySettingPermission";

    // Calculated Salary Foundation Tracker Override errors
    public static final String ERROR_FISCAL_YEAR_NOT_CURRENT = "error.fiscalYear.notCurrentFiscalYear";
    public static final String ERROR_INVALID_APPOINTMENT = "error.invalid.appointment";
    public static final String ERROR_INVALID_POSITION = "error.invalid.position";

}


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
package org.kuali.kfs.module.ec;

/**
 * Constants that represent keys to messages or errors given in the effort reporting module.
 */
public class EffortKeyConstants {
    public static final String ERROR_A21_SUB_ACCOUNT_NOT_FOUND = "error.effort.a21SubAccountNotFound";
    public static final String ERROR_ACCOUNT_CLOSED = "error.effort.accountClosed";

    public static final String ERROR_ACCOUNT_NUMBER_NOT_FOUND = "error.effort.accountNumberNotFound";
    public static final String ERROR_BATCH_JOB_NOT_SCHEDULED = "error.effort.batchJobNotScheduled";
    public static final String ERROR_CREATE_PROCESS_HAS_NOT_BEEN_COMPLETED = "error.effort.createProcessHasNotBeenCompleted";
    public static final String ERROR_EMPLOYEE_NO_ELIGIBLE_LABOR_BALANCE = "error.effort.employeeNoEligibleLaborBalance";
    public static final String ERROR_EMPLOYEE_NOT_ELIGIBLE = "error.effort.employeeNotEligibleForEffortCertification";
    public static final String ERROR_END_FISCAL_YEAR = "error.efffort.effortCertificationReport.endFiscalYear";
    public static final String ERROR_FISCAL_YEAR_MISSING = "error.effort.fiscalYearMissing";
    public static final String ERROR_FISCAL_YEAR_OR_REPORT_NUMBER_INVALID = "error.effort.fiscalYearOrReportNumberInvalid";
    public static final String ERROR_FISCAL_YR = "error.requiredForUs";
    public static final String ERROR_FUND_GROUP_NOT_FOUND = "error.effort.fundGroupNotFound";
    public static final String ERROR_INVALID_EFFORT_PERCENT = "error.effort.invalidEffortPercent";
    public static final String ERROR_LINE_EXISTS = "error.effort.lineExists";
    public static final String ERROR_MULTIPLE_ORGANIZATIONS_FOUND = "error.effort.multipleOrganizationFound";
    public static final String ERROR_NEGATIVE_PAYROLL_AMOUNT = "error.effort.negativePayrollAmount";
    public static final String ERROR_NONPOSITIVE_PAYROLL_AMOUNT = "error.effort.nonpositivePayrollAmount";
    public static final String ERROR_NOT_COST_SHARE_SUB_ACCOUNT = "error.effort.notCostShareSubAccount";
    public static final String ERROR_NOT_LABOR_OBJECT_CODE = "error.effort.notLaborObjectCode";
    public static final String ERROR_NOT_PAID_BY_FEDERAL_FUNDS = "error.effort.notPaidByFederalFunds";
    public static final String ERROR_NOT_PAID_BY_GRANT_ACCOUNT = "error.effort.notPaidByGrantAccount";
    public static final String ERROR_NOTE_REQUIRED_WHEN_APPROVED_EFFORT_CERTIFICATION_EXIST = "error.effort.noteRequiredWhenApprovedEffortCertificationExist";
    public static final String ERROR_NOTE_REQUIRED_WHEN_EFFORT_CHANGED = "error.effort.noteRequiredWhenEffortChanged";
    public static final String ERROR_PAYROLL_AMOUNT_OVERCHANGED = "error.effort.payrollAmountOverchanged";
    public static final String ERROR_PENDING_EFFORT_CERTIFICATION_EXIST = "error.effort.pendingEffortCertificationExist";
    public static final String ERROR_PENDING_SALARAY_EXPENSE_TRANSFER_EXIST = "error.effort.pendingSalaryExpenseTranferExist";
    public static final String ERROR_REPORT_DEFINITION_INACTIVE = "error.effort.reportDefinitionInactive";
    public static final String ERROR_REPORT_DEFINITION_NOT_EXIST = "error.effort.reportDefinitionNotExist";
    public static final String ERROR_REPORT_DEFINITION_PERIOD_NOT_OPENED = "error.effort.reportDefinitionPeriodNotOpened";
    public static final String ERROR_REPORT_DOCUMENT_BUILD_NOT_EXIST = "error.effort.reportDocumentBuildNotExist";
    public static final String ERROR_REPORT_DOCUMENT_EXIST = "error.effort.reportDocumentExist";
    public static final String ERROR_REPORT_LINES_EXIST = "error.effort.reportLinesExist";
    public static final String ERROR_REPORT_NUMBER_MISSING = "error.effort.reportNumberMissing";
    public static final String ERROR_SALARY_EXPENSE_TRANSFER_DOCUMENT_NOT_GENERATED = "error.effort.salaryExpenseTransferDocumentNotGenerated";
    public static final String ERROR_TOTAL_EFFORT_PERCENTAGE_NOT_100 = "error.effort.totalEffortPercentageNot100";
    public static final String ERROR_TOTAL_PAYROLL_AMOUNT_OVERCHANGED = "error.effort.totalPayrollAmountOverchanged";
    public static final String ERROR_UNDEFINED_UNIVERSITY_DATE = "error.effort.undefinedUnversityDate";
    public static final String ERROR_ZERO_PAYROLL_AMOUNT = "error.effort.zeroPayrollAmount";
    public static final String ERROR_NOT_HAVE_DETAIL_LINE = "error.effort.notHaveDetailLine";
    public static final String ERROR_NOT_HAVE_POSITION_GROUP = "error.effort.notHavePositionGroup";

    public static final String MESSAGE_CREATE_DOCUMENT_EXPLANATION = "message.effort.create.explanation";
    public static final String MESSAGE_CREATE_SET_DOCUMENT_DESCRIPTION = "message.effort.create.SETDocument.explanation";
    public static final String MESSAGE_NUM_BALANCE_RECORDS_READ = "message.effort.report.numOfBalanceRecordsRead";
    public static final String MESSAGE_NUM_BALANCE_RECORDS_SELECTED = "message.effort.report.numOfBalanceRecordsSelected";
    public static final String MESSAGE_NUM_CERTIFICATION_RECORDS_WRITTEN = "message.effort.report.numOfCertificationRecordsWritten";
    public static final String MESSAGE_NUM_DETAIL_LINE_BUILD_RECORDS_WRITTEN = "message.effort.report.numOfDetailLineBuildRecordsWritten";
    public static final String MESSAGE_NUM_EMPLOYEES_SELECTED = "message.effort.report.numOfEmployeesSelected";
    public static final String MESSAGE_RECALCULATE_SALARY_AMOUNT = "message.effort.report.recalculateSalaryAmount";

    public static final String QUESTION_OVERLAPPING_REPORT_DEFINITION = "question.effort.effortCertificationReportDefinition.overlappingDefinitions";
    public static final String INVALID_REPORT_BEGIN_PERIOD = "error.rptdefn.begin.period.invalid";
    public static final String INVALID_REPORT_END_PERIOD = "error.rptdefn.end.period.invalid";
    public static final String INVALID_EXPENSE_TRANSFER_PERIOD = "error.rptdefn.transfer.period.invalid";
}

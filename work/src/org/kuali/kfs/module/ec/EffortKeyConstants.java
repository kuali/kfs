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
package org.kuali.module.effort;

/**
 * Constants that represent keys to messages or errors given in the effort reporting module.
 */
public class EffortKeyConstants {
    public static final String ERROR_ACCOUNT_NUMBER_NOT_FOUND = "error.effort.accountNumberNotFound";
    public static final String ERROR_BATCH_JOB_NOT_SCHEDULED = "error.effort.batchJobNotScheduled";
     
    public static final String ERROR_END_FISCAL_YEAR = "error.efffort.effortCertificationReport.endFiscalYear";
    public static final String ERROR_FISCAL_YEAR_MISSING = "error.effort.fiscalYearMissing";
    public static final String ERROR_FISCAL_YEAR_OR_REPORT_NUMBER_INVALID = "error.effort.fiscalYearOrReportNumberInvalid";
    public static final String ERROR_FISCAL_YR = "error.requiredForUs";
    public static final String ERROR_FUND_GROUP_NOT_FOUND = "error.effort.fundGroupNotFound";
    public static final String ERROR_MULTIPLE_ORGANIZATIONS_FOUND = "error.effort.multipleOrganizationFound";
    public static final String ERROR_NONPOSITIVE_PAYROLL_AMOUNT = "error.effort.nonpositivePayrollAmount";
    public static final String ERROR_NOT_LABOR_OBJECT_CODE = "error.effort.notLaborObjectCode";
    public static final String ERROR_NOT_PAID_BY_FEDERAL_FUNDS = "error.effort.notPaidByFederalFunds";
    public static final String ERROR_REPORT_DEFINITION_INACTIVE = "error.effort.reportDefinitionInactive";
    public static final String ERROR_REPORT_DEFINITION_PERIOD_NOT_OPENED = "error.effort.reportDefinitionPeriodNotOpened";
    public static final String ERROR_REPORT_DOCUMENT_BUILD_NOT_EXIST = "error.effort.reportDocumentBuildNotExist";
    public static final String ERROR_REPORT_DOCUMENT_EXIST = "error.effort.reportDocumentExist";
    public static final String ERROR_REPORT_LINES_EXIST = "error.effort.reportLinesExist";
    
    public static final String ERROR_REPORT_NUMBER_MISSING = "error.effort.reportNumberMissing";
    public static final String ERROR_UNDEFINED_UNIVERSITY_DATE = "error.effort.undefinedUnversityDate";        
    public static final String ERROR_ZERO_PAYROLL_AMOUNT = "error.effort.zeroPayrollAmount";
    
    public static final String MESSAGE_CREATE_DOCUMENT_EXPLANATION = "message.effort.create.explanation";
    public static final String MESSAGE_CREATE_SET_DOCUMENT_DESCRIPTION = "message.effort.create.SETDocument.explanation";
    public static final String MESSAGE_NUM_BALANCE_RECORDS_READ = "message.effort.report.numOfBalanceRecordsRead";
    public static final String MESSAGE_NUM_BALANCE_RECORDS_SELECTED = "message.effort.report.numOfBalanceRecordsSelected";
    public static final String MESSAGE_NUM_CERTIFICATION_RECORDS_WRITTEN = "message.effort.report.numOfCertificationRecordsWritten";
    public static final String MESSAGE_NUM_DETAIL_LINE_BUILD_RECORDS_WRITTEN = "message.effort.report.numOfDetailLineBuildRecordsWritten";    
    public static final String MESSAGE_NUM_EMPLOYEES_SELECTED = "message.effort.report.numOfEmployeesSelected";
    
    public static final String QUESTION_OVERLAPPING_REPORT_DEFINITION = "question.effort.effortCertificationReportDefinition.overlappingDefinitions";
}

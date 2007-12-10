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
    public static final String ERROR_FISCAL_YR = "error.requiredForUs";
    public static final String ERROR_END_FISCAL_YEAR = "error.efffort.effortCertificationReport.endFiscalYear";
    
    public static final String ERROR_FISCAL_YEAR_MISSING = "error.effort.extract.fiscalYearMissing";
    public static final String ERROR_REPORT_NUMBER_MISSING = "error.effort.extract.reportNumberMissing";
    public static final String ERROR_FISCAL_YEAR_OR_REPORT_NUMBER_INVALID = "error.effort.extract.fiscalYearOrReportNumberInvalid";
    public static final String ERROR_REPORT_DEFINITION_INACTIVE = "error.effort.extract.reportDefinitionInactive";
    public static final String ERROR_NOT_LABOR_OBJECT_CODE = "error.effort.extract.notLaborObjectCode";
    public static final String ERROR_HIGHER_EDUCATION_CODE_NOT_FOUND = "error.effort.extract.higherEducationCodeNotFound";
    public static final String ERROR_ACCOUNT_NUMBER_NOT_FOUND = "error.effort.extract.accountNumberNotFound";
    public static final String ERROR_FUND_GROUP_NOT_FOUND = "error.effort.extract.fundGroupNotFound";
    public static final String ERROR_NONPOSITIVE_PAYROLL_AMOUNT = "error.effort.extract.nonpositivePayrollAmount";
    public static final String ERROR_REPORT_DOCUMENT_EXIST = "error.effort.extract.reportDocumentExist";
    public static final String ERROR_REPORT_LINES_EXIST = "error.effort.extract.reportLinesExist";
    public static final String ERROR_UNDEFINED_UNIVERSITY_DATE = "error.effort.extract.undefinedUnversityDate";
    public static final String QUESTION_OVERLAPPING_REPORT_DEFINITION = "question.effort.effortCertificationReportDefinition.overlappingDefinitions";
}

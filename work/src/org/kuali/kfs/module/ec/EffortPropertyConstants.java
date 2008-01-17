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

import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.labor.LaborPropertyConstants;

/**
 * Constants that represent a property name in an effort reporting business object.
 */
public class EffortPropertyConstants {
    public final static String EXPENSE_TRANSFER_FISCAL_PERIOD = "expenseTransferFiscalPeriod";
    public final static String EXPENSE_TRANSFER_FISCAL_PERIOD_CODE = "expenseTransferFiscalPeriodCode";
    public final static String EXPENSE_TRANSFER_FISCAL_YEAR = "expenseTransferFiscalYear";
    public final static String EFFORT_CERTIFICATION_PERIOD_STATUS_CODE = "effortCertificationPeriodStatusCode";
    public final static String EFFORT_CERTIFICATION_REPORT_BEGIN_FISCAL_YEAR = "effortCertificationReportBeginFiscalYear";
    public final static String EFFORT_CERTIFICATION_REPORT_BEGIN_PERIOD_CODE = "effortCertificationReportBeginPeriodCode";
    public final static String EFFORT_CERTIFICATION_REPORT_END_FISCAL_YEAR = "effortCertificationReportEndFiscalYear";
    public final static String EFFORT_CERTIFICATION_REPORT_END_PERIOD_CODE = "effortCertificationReportEndPeriodCode";
    public final static String EFFORT_CERTIFICATION_REPORT_NUMBER = "effortCertificationReportNumber";
    public final static String EFFORT_CERTIFICATION_REPORT_PERIOD_STATUS_CODE = "effortCertificationReportPeriodStatusCode";
    public final static String EFFORT_CERTIFICATION_REPORT_PERIOD_TITLE = "effortCertificationReportPeriodTitle";
    public final static String EFFORT_CERTIFICATION_REPORT_POSITIONS = "effortCertificationReportPositions";
    public final static String EFFORT_CERTIFICATION_REPORT_RETURN_DATE = "effortCertificationReportReturnDate";
    public final static String EFFORT_CERTIFICATION_REPORT_TYPE = "effortCertificationReportType";
    public final static String EFFORT_CERTIFICATION_REPORT_TYPE_CODE = "effortCertificationReportTypeCode";
    public final static String EFFORT_CERTIFICATION_REPORT_DEFINITION_ACTIVE_IND = "active";
    public final static String EFFORT_CERTIFICATION_REPORT_DEFINITION_UNIVERSITY_FISCAL_YEAR = "universityFiscalYear";
    
    public static final String FINANCIAL_DOCUMENT_POSTING_YEAR = "financialDocumentPostingYear";

    public final static String LABOR_OBJECT_FRINGE_OR_SALARY_CODE = LaborPropertyConstants.LABOR_OBJECT + "." + LaborPropertyConstants.FINANCIAL_OBJECT_FRINGE_OR_SALARY_CODE;
    public final static String ACCOUNT_ACCOUNT_TYPE_CODE = KFSPropertyConstants.ACCOUNT + "." + KFSPropertyConstants.ACCOUNT_TYPE_CODE;
}

/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ld;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;

/**
 * Constants for labor business object property names.
 */
public class LaborPropertyConstants {

    public enum AccountingPeriodProperties {
        APRIL(KFSPropertyConstants.MONTH10_AMOUNT, KFSConstants.MONTH10), AUGUST(KFSPropertyConstants.MONTH2_AMOUNT, KFSConstants.MONTH2), DECEMBER(KFSPropertyConstants.MONTH6_AMOUNT, KFSConstants.MONTH6), FEBRUARY(KFSPropertyConstants.MONTH8_AMOUNT, KFSConstants.MONTH8), JANUARY(KFSPropertyConstants.MONTH7_AMOUNT, KFSConstants.MONTH7), JULY(KFSPropertyConstants.MONTH1_AMOUNT, KFSConstants.MONTH1), JUNE(KFSPropertyConstants.MONTH12_AMOUNT, KFSConstants.MONTH12), MARCH(KFSPropertyConstants.MONTH9_AMOUNT, KFSConstants.MONTH9), MAY(KFSPropertyConstants.MONTH11_AMOUNT, KFSConstants.MONTH11), NOVEMBER(KFSPropertyConstants.MONTH5_AMOUNT, KFSConstants.MONTH5), OCTOBER(KFSPropertyConstants.MONTH4_AMOUNT, KFSConstants.MONTH4), SEPTEMBER(KFSPropertyConstants.MONTH3_AMOUNT, KFSConstants.MONTH3), YEAR_END(KFSPropertyConstants.MONTH13_AMOUNT, KFSConstants.MONTH13);

        /**
         * Spew out accounting period codes as a String array
         * 
         * @return String[]
         */
        public static String[] codeToArray() {
            return new String[] { JULY.periodCode, AUGUST.periodCode, SEPTEMBER.periodCode, OCTOBER.periodCode, NOVEMBER.periodCode, DECEMBER.periodCode, JANUARY.periodCode, FEBRUARY.periodCode, MARCH.periodCode, APRIL.periodCode, MAY.periodCode, JUNE.periodCode, YEAR_END.periodCode };
        }

        /**
         * Spew out the property names in as a String array
         * 
         * @return String[]
         */
        public static String[] namesToArray() {
            return new String[] { JULY.propertyName, AUGUST.propertyName, SEPTEMBER.propertyName, OCTOBER.propertyName, NOVEMBER.propertyName, DECEMBER.propertyName, JANUARY.propertyName, FEBRUARY.propertyName, MARCH.propertyName, APRIL.propertyName, MAY.propertyName, JUNE.propertyName, YEAR_END.propertyName };
        }

        /**
         * Spew out everything in an array
         */
        public static AccountingPeriodProperties[] toArray() {
            return new AccountingPeriodProperties[] { JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER, JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, YEAR_END };
        }

        public String periodCode;

        public String propertyName;

        private AccountingPeriodProperties(String propertyName, String periodCode) {
            this.propertyName = propertyName;
            this.periodCode = periodCode;
        }
    }

    public static final String ANNUAL_ACTUAL_AMOUNT = "annualActualAmount";
    public static final String EARN_CODE = "earnCode";
    public static final String EFFECTIVE_DATE = "effectiveDate";
    public static final String GRADE = "grade";
    public static final String HRMS_COMPANY = "hrmsCompany";
    public static final String JULY_1_BUDGET_AMOUNT = "july1BudgetAmount";
    public static final String JULY_1_BUDGET_FTE_QUANTITY = "july1BudgetFteQuantity";
    public static final String JULY_1_BUDGET_TIME_PERCENT = "july1BudgetTimePercent";
    public static final String LABORLEDGER_ORIGINAL_ACCOUNT_NUMBER = "laborLedgerOriginalAccountNumber";
    public static final String LABORLEDGER_ORIGINAL_CHART_OF_ACCOUNTS_CODE = "laborLedgerOriginalChartOfAccountsCode";
    public static final String LABORLEDGER_ORIGINAL_FINANCIAL_OBJECT_CODE = "laborLedgerOriginalFinancialObjectCode";
    public static final String LABORLEDGER_ORIGINAL_FINANCIAL_SUB_OBJECT_CODE = "laborLedgerOriginalFinancialSubObjectCode";
    public static final String LABORLEDGER_ORIGINAL_SUB_ACCOUNT_NUMBER = "laborLedgerOriginalSubAccountNumber";
    public static final String PAY_GROUP = "payGroup";
    public static final String PAY_PERIOD_END_DATE = "payPeriodEndDate";
    public static final String PAYROLL_END_DATE_FISCAL_PERIOD_CODE = "payrollEndDateFiscalPeriodCode";
    public static final String PAYROLL_END_DATE_FISCAL_YEAR = "payrollEndDateFiscalYear";
    public static final String RUN_IDENTIFIER = "runIdentifier";
    public static final String SALARY_ADMINISTRATION_PLAN = "salaryAdministrationPlan";
    public static final String SET_ID = "setid";
    public static final String TRANSACTION_TOTAL_HOURS = "transactionTotalHours";
    public static final String PAYROLL_TOTAL_HOURS = "payrollTotalHours";
    public static final String POSITION_BENEFIT_TYPE_CODE = "positionBenefitTypeCode";
    public static final String FINANCIAL_OBJECT_FRINGE_OR_SALARY_CODE = "financialObjectFringeOrSalaryCode";
    public static final String POSITION_OBJECT_GROUP_CODE = "positionObjectGroupCode";
    public static final String POSITION_OBJECT_GROUP = "positionObjectGroup";
    public static final String OUTSTANDING_ENCUMBRANCE = "outstandingEncum";
    public static final String LABOR_BENEFIT_RATE_CATEGORY_CODE = "laborBenefitRateCategoryCode";
}

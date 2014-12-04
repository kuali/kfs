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
package org.kuali.kfs.module.ar.report.service;



/**
 * Services to support the Federal Financial Report
 */
public interface FederalFinancialReportService {
    public static final String FINANCIAL_FORM_REQUIRED = "Please select a Financial Form to generate.";
    public static final String FISCAL_YEAR_AND_PERIOD_REQUIRED = "Enter both period and fiscal year.";
    public static final String PROPOSAL_NUMBER_REQUIRED = "Please enter a proposal Number for SF425.";
    public static final String AGENCY_REQUIRED = "Please enter an Agency for SF425A.";
    public static final String REPORTING_PERIOD = "reportingPeriod";
    public static final String FEDERAL_FORM = "federalForm";
    public static final String FISCAL_YEAR = "fiscalYear";

    /**
     * Validates the user input to the report.
     * @param federalForm the type of federal form to validate
     * @param proposalNumber the proposal number to validate
     * @param fiscalYear the fiscal year to validate
     * @param reportingPeriod the reporting period to validate
     * @param agencyNumber the agency number to validate
     * @return error message if an error occurred; otherwise, a blank String
     */
    public String validate(String federalForm, String proposalNumber, String fiscalYear, String reportingPeriod, String agencyNumber);

    /**
     * Creates a URL to be used in printing the federal forms.
     * @param basePath String: The base path of the current URL
     * @param docId String: The document ID of the document to be printed
     * @param period the accounting period of the document to be printed
     * @param year the fiscal year of the document to be printed
     * @param agencyNumber the agencyNumber of the document to be printed
     * @param formType the formType of the document to be printed
     * @param methodToCall String: The name of the method that will be invoked to do this particular print
     * @return The URL
     */
    public String getUrlForPrintInvoice(String basePath, String docId, String period, String year, String agencyNumber, String formType, String methodToCall);

}

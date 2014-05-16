/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.service;

import java.io.File;
import java.io.IOException;

import org.kuali.kfs.module.ar.businessobject.ReportPDFHolder;

import com.lowagie.text.DocumentException;


/**
 * Services to support the Federal Financial Report
 */
public interface FederalFinancialReportService {
    public static final Object FEDERAL_FORM_425 = "425";
    public static final Object FEDERAL_FORM_425A = "425A";
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

    /**
     * Mangles the given report file into a PDF
     * @param report the original report to PDFerize
     * @param formType the type of form we're reporting on
     * @param period the accounting period
     * @param proposalNumber the proposal number of the report
     * @param agencyNumber the agency number of the report
     * @param useJavascript whether javascript is being used, which affects content disposition
     * @return the report holder with the bytes of the PDF and the content disposition
     * @throws DocumentException if the pdf cannot be written
     * @throws IOException for more general output errors
     */
    public ReportPDFHolder pdferizeReport(File report, String formType, String period, String proposalNumber, String agencyNumber, boolean useJavascript) throws DocumentException, IOException;
}
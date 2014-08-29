/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.report.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryRateDetail;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsLetterOfCreditReviewDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.businessobject.InvoiceAddressDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.ContractsGrantsLetterOfCreditReviewDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceReportService;
import org.kuali.kfs.module.ar.service.ContractsGrantsBillingUtilityService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.PdfFormFillerUtil;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.sys.service.ReportGenerationService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

import au.com.bytecode.opencsv.CSVWriter;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;

/**
 * This class implements the methods for report generation services for Contracts and Grants.
 */
public class ContractsGrantsInvoiceReportServiceImpl implements ContractsGrantsInvoiceReportService {
    private final static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsInvoiceReportServiceImpl.class);
    protected DateTimeService dateTimeService;
    protected DataDictionaryService dataDictionaryService;
    protected PersonService personService;
    protected BusinessObjectService businessObjectService;
    protected ParameterService parameterService;
    protected ConfigurationService configService;
    protected KualiModuleService kualiModuleService;
    protected DocumentService documentService;
    protected NoteService noteService;
    protected ReportInfo reportInfo;
    protected ReportGenerationService reportGenerationService;
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected ContractsGrantsBillingUtilityService contractsGrantsBillingUtilityService;

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceReportService#generateInvoice(org.kuali.kfs.module.ar.document.ContractsGrantsLOCReviewDocument)
     */
    @Override
    public byte[] generateLOCReviewAsPdf(ContractsGrantsLetterOfCreditReviewDocument document) {
        Date runDate = new Date(new java.util.Date().getTime());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.generateLOCReviewInPdf(baos, document);
        return baos.toByteArray();
    }

    /**
     * this method generated the actual pdf for the Contracts and Grants LOC Review Document.
     *
     * @param os
     * @param LOCDocument
     */
    protected void generateLOCReviewInPdf(OutputStream os, ContractsGrantsLetterOfCreditReviewDocument locDocument) {
        try {
            Document document = new Document(new Rectangle(ArConstants.LOCReviewPdf.LENGTH, ArConstants.LOCReviewPdf.WIDTH));
            PdfWriter.getInstance(document, os);
            document.open();

            Paragraph header = new Paragraph();
            Paragraph text = new Paragraph();
            Paragraph title = new Paragraph();

            // Lets write the header
            header.add(new Paragraph(configService.getPropertyValueAsString(ArKeyConstants.LOC_REVIEW_PDF_TITLE), ArConstants.PdfReportFonts.LOC_REVIEW_TITLE_FONT));
            if (StringUtils.isNotEmpty(locDocument.getLetterOfCreditFundGroupCode())) {
                header.add(new Paragraph(configService.getPropertyValueAsString(ArKeyConstants.LOC_REVIEW_PDF_HEADER_FUND_GROUP_CODE) + locDocument.getLetterOfCreditFundGroupCode(), ArConstants.PdfReportFonts.LOC_REVIEW_TITLE_FONT));
            }
            if (StringUtils.isNotEmpty(locDocument.getLetterOfCreditFundCode())) {
                header.add(new Paragraph(configService.getPropertyValueAsString(ArKeyConstants.LOC_REVIEW_PDF_HEADER_FUND_CODE) + locDocument.getLetterOfCreditFundCode(), ArConstants.PdfReportFonts.LOC_REVIEW_TITLE_FONT));
            }
            header.add(new Paragraph(KFSConstants.BLANK_SPACE));
            header.setAlignment(Element.ALIGN_CENTER);
            title.add(new Paragraph(configService.getPropertyValueAsString(ArKeyConstants.LOC_REVIEW_PDF_HEADER_DOCUMENT_NUMBER) + locDocument.getDocumentNumber(), ArConstants.PdfReportFonts.LOC_REVIEW_HEADER_FONT));
            Person person = getPersonService().getPerson(locDocument.getFinancialSystemDocumentHeader().getInitiatorPrincipalId());
            // writing the Document details
            title.add(new Paragraph(configService.getPropertyValueAsString(ArKeyConstants.LOC_REVIEW_PDF_HEADER_APP_DOC_STATUS) + locDocument.getFinancialSystemDocumentHeader().getApplicationDocumentStatus(), ArConstants.PdfReportFonts.LOC_REVIEW_HEADER_FONT));
            title.add(new Paragraph(configService.getPropertyValueAsString(ArKeyConstants.LOC_REVIEW_PDF_HEADER_DOCUMENT_INITIATOR) + person.getName(), ArConstants.PdfReportFonts.LOC_REVIEW_HEADER_FONT));
            title.add(new Paragraph(configService.getPropertyValueAsString(ArKeyConstants.LOC_REVIEW_PDF_HEADER_DOCUMENT_CREATE_DATE) + getDateTimeService().toDateString(locDocument.getFinancialSystemDocumentHeader().getWorkflowCreateDate()), ArConstants.PdfReportFonts.LOC_REVIEW_HEADER_FONT));

            title.add(new Paragraph(KFSConstants.BLANK_SPACE));
            title.setAlignment(Element.ALIGN_RIGHT);

            text.add(new Paragraph(configService.getPropertyValueAsString(ArKeyConstants.LOC_REVIEW_PDF_SUBHEADER_AWARDS), ArConstants.PdfReportFonts.LOC_REVIEW_SMALL_BOLD));
            text.add(new Paragraph(KFSConstants.BLANK_SPACE));

            document.add(header);
            document.add(title);
            document.add(text);
            PdfPTable table = new PdfPTable(11);
            table.setTotalWidth(ArConstants.LOCReviewPdf.RESULTS_TABLE_WIDTH);
            // fix the absolute width of the table
            table.setLockedWidth(true);

            // relative col widths in proportions - 1/11
            float[] widths = new float[] { 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f };
            table.setWidths(widths);
            table.setHorizontalAlignment(0);
            addAwardHeaders(table);
            if (CollectionUtils.isNotEmpty(locDocument.getHeaderReviewDetails()) && CollectionUtils.isNotEmpty(locDocument.getAccountReviewDetails())) {
                for (ContractsGrantsLetterOfCreditReviewDetail item : locDocument.getHeaderReviewDetails()) {
                    table.addCell(Long.toString(item.getProposalNumber()));
                    table.addCell(item.getAwardDocumentNumber());
                    table.addCell(item.getAgencyNumber());
                    table.addCell(item.getCustomerNumber());
                    table.addCell(getDateTimeService().toDateString(item.getAwardBeginningDate()));
                    table.addCell(getDateTimeService().toDateString(item.getAwardEndingDate()));
                    table.addCell(contractsGrantsBillingUtilityService.formatForCurrency(item.getAwardBudgetAmount()));
                    table.addCell(contractsGrantsBillingUtilityService.formatForCurrency(item.getLetterOfCreditAmount()));
                    table.addCell(contractsGrantsBillingUtilityService.formatForCurrency(item.getClaimOnCashBalance()));
                    table.addCell(contractsGrantsBillingUtilityService.formatForCurrency(item.getAmountToDraw()));
                    table.addCell(contractsGrantsBillingUtilityService.formatForCurrency(item.getAmountAvailableToDraw()));

                    PdfPCell cell = new PdfPCell();
                    cell.setPadding(ArConstants.LOCReviewPdf.RESULTS_TABLE_CELL_PADDING);
                    cell.setColspan(ArConstants.LOCReviewPdf.RESULTS_TABLE_COLSPAN);
                    PdfPTable newTable = new PdfPTable(ArConstants.LOCReviewPdf.INNER_TABLE_COLUMNS);
                    newTable.setTotalWidth(ArConstants.LOCReviewPdf.INNER_TABLE_WIDTH);
                    // fix the absolute width of the newTable
                    newTable.setLockedWidth(true);

                    // relative col widths in proportions - 1/8
                    float[] newWidths = new float[] { 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f };
                    newTable.setWidths(newWidths);
                    newTable.setHorizontalAlignment(0);
                    addAccountsHeaders(newTable);
                    for (ContractsGrantsLetterOfCreditReviewDetail newItem : locDocument.getAccountReviewDetails()) {
                        if (item.getProposalNumber().equals(newItem.getProposalNumber())) {
                            newTable.addCell(newItem.getAccountDescription());
                            newTable.addCell(newItem.getChartOfAccountsCode());
                            newTable.addCell(newItem.getAccountNumber());
                            newTable.addCell(getDateTimeService().toDateString(newItem.getAccountExpirationDate()));
                            newTable.addCell(contractsGrantsBillingUtilityService.formatForCurrency(newItem.getAwardBudgetAmount()));
                            newTable.addCell(contractsGrantsBillingUtilityService.formatForCurrency(newItem.getClaimOnCashBalance()));
                            newTable.addCell(contractsGrantsBillingUtilityService.formatForCurrency(newItem.getAmountToDraw()));
                            newTable.addCell(contractsGrantsBillingUtilityService.formatForCurrency(newItem.getFundsNotDrawn()));
                        }
                    }
                    cell.addElement(newTable);
                    table.addCell(cell);

                }
                document.add(table);
            }
            document.close();
        }
        catch (DocumentException e) {
            LOG.error("problem during ContractsGrantsInvoiceReportServiceImpl.generateInvoiceInPdf()", e);
        }
    }

    /**
     * This method is used to set the headers for the CG LOC review Document
     *
     * @param table
     */
    protected void addAccountsHeaders(PdfPTable table) {
        table.addCell(getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, KFSPropertyConstants.ACCOUNT_DESCRIPTION));
        table.addCell(getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE));
        table.addCell(getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, KFSPropertyConstants.ACCOUNT_NUMBER));
        table.addCell(getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, KFSPropertyConstants.ACCOUNT_EXPIRATION_DATE));
        table.addCell(getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, ArPropertyConstants.AWARD_BUDGET_AMOUNT));
        table.addCell(getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, ArPropertyConstants.CLAIM_ON_CASH_BALANCE));
        table.addCell(getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, ArPropertyConstants.AMOUNT_TO_DRAW));
        table.addCell(getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, ArPropertyConstants.FUNDS_NOT_DRAWN));
    }

    /**
     * This method is used to set the headers for the CG LOC review Document
     *
     * @param table
     */
    protected void addAwardHeaders(PdfPTable table) {
        table.addCell(getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, KFSPropertyConstants.PROPOSAL_NUMBER));
        table.addCell(getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, KFSPropertyConstants.AWARD_DOCUMENT_NUMBER));
        table.addCell(getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, KFSPropertyConstants.AGENCY_NUMBER));
        table.addCell(getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, KFSPropertyConstants.CUSTOMER_NUMBER));
        table.addCell(getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, KFSPropertyConstants.AWARD_BEGINNING_DATE));
        table.addCell(getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, KFSPropertyConstants.AWARD_ENDING_DATE));
        table.addCell(getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, ArPropertyConstants.AWARD_BUDGET_AMOUNT));
        table.addCell(getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, ArPropertyConstants.LETTER_OF_CREDIT_AMOUNT));
        table.addCell(getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, ArPropertyConstants.CLAIM_ON_CASH_BALANCE));
        table.addCell(getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, ArPropertyConstants.AMOUNT_TO_DRAW));
        table.addCell(getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, ArPropertyConstants.FUNDS_NOT_DRAWN));
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceReportService#generateFederalFinancialForm(org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward,
     *      java.lang.String, java.lang.String, java.lang.String, org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency)
     */
    @Override
    public File generateFederalFinancialForm(ContractsAndGrantsBillingAward award, String period, String year, String formType, ContractsAndGrantsBillingAgency agency) {
        Map<String, String> replacementList = new HashMap<String, String>();
        Date runDate = new Date(new java.util.Date().getTime());
        String reportFileName = getReportInfo().getReportFileName();
        String reportDirectory = getReportInfo().getReportsDirectory();
        try {
            if (formType.equals(ArConstants.FEDERAL_FORM_425) && ObjectUtils.isNotNull(award)) {
                String fullReportFileName = reportGenerationService.buildFullFileName(runDate, reportDirectory, reportFileName, ArConstants.FEDERAL_FUND_425_REPORT_ABBREVIATION) + KFSConstants.ReportGeneration.PDF_FILE_EXTENSION;
                File file = new File(fullReportFileName);
                FileOutputStream fos = new FileOutputStream(file);
                stampPdfFormValues425(award, period, year, fos, replacementList);
                return file;
            }
            else if (formType.equals(ArConstants.FEDERAL_FORM_425A) && ObjectUtils.isNotNull(agency)) {
                String fullReportFileName = reportGenerationService.buildFullFileName(runDate, reportDirectory, reportFileName, ArConstants.FEDERAL_FUND_425A_REPORT_ABBREVIATION) + KFSConstants.ReportGeneration.PDF_FILE_EXTENSION;
                File file = new File(fullReportFileName);
                FileOutputStream fos = new FileOutputStream(file);
                stampPdfFormValues425A(agency, period, year, fos, replacementList);
                return file;
            }
        }
        catch (FileNotFoundException ex) {
            throw new RuntimeException("Cannot find pdf to stamp for federal financial form", ex);
        }
        return null;
    }

    /**
     * @param award
     * @return
     */
    protected KualiDecimal getCashReceipts(ContractsAndGrantsBillingAward award) {
    	KualiDecimal cashReceipt = KualiDecimal.ZERO;
        Map<String,String> fieldValues = new HashMap<String,String>();
        if (ObjectUtils.isNotNull(award) && ObjectUtils.isNotNull(award.getProposalNumber())){
            fieldValues.put(KFSPropertyConstants.PROPOSAL_NUMBER, award.getProposalNumber().toString());
        }
        List<ContractsGrantsInvoiceDocument> list = (List<ContractsGrantsInvoiceDocument>) contractsGrantsInvoiceDocumentService.retrieveAllCGInvoicesByCriteria(fieldValues);
        if (ObjectUtils.isNotNull(list)) {
            for(ContractsGrantsInvoiceDocument invoice: list){
                Map primaryKeys = new HashMap<String, Object>();
                primaryKeys.put(ArPropertyConstants.CustomerInvoiceDocumentFields.FINANCIAL_DOCUMENT_REF_INVOICE_NUMBER, invoice.getDocumentNumber());
                List<InvoicePaidApplied> ipas = (List<InvoicePaidApplied>)businessObjectService.findMatching(InvoicePaidApplied.class, primaryKeys);
                if(ObjectUtils.isNotNull(ipas)) {
                    for(InvoicePaidApplied ipa : ipas) {
                        cashReceipt = cashReceipt.add(ipa.getInvoiceItemAppliedAmount());
                    }
                }
            }
        }
        return cashReceipt;
    }

    /**
     * This method is used to populate the replacement list to replace values from pdf template to actual values for Federal Form
     * 425
     *
     * @param award
     * @param reportingPeriod
     * @param year
     */
    protected void populateListByAward(ContractsAndGrantsBillingAward award, String reportingPeriod, String year, Map<String, String> replacementList) {
        KualiDecimal cashDisbursement = KualiDecimal.ZERO;
        for (ContractsAndGrantsBillingAwardAccount awardAccount : award.getActiveAwardAccounts()) {
            int index = 0;
            KualiDecimal baseSum = KualiDecimal.ZERO;
            KualiDecimal amountSum = KualiDecimal.ZERO;
            cashDisbursement = cashDisbursement.add(contractsGrantsInvoiceDocumentService.getBudgetAndActualsForAwardAccount(awardAccount, ArPropertyConstants.ACTUAL_BALANCE_TYPE, award.getAwardBeginningDate()));
            if (ObjectUtils.isNotNull(awardAccount.getAccount().getFinancialIcrSeriesIdentifier()) && ObjectUtils.isNotNull(awardAccount.getAccount().getAcctIndirectCostRcvyTypeCd())) {
                index++;
                contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.INDIRECT_EXPENSE_TYPE + " " + index, awardAccount.getAccount().getAcctIndirectCostRcvyTypeCd());
                contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.INDIRECT_EXPENSE_RATE + " " + index, awardAccount.getAccount().getFinancialIcrSeriesIdentifier());
                if (ObjectUtils.isNotNull(awardAccount.getAccount().getAccountEffectiveDate())) {
                    contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.INDIRECT_EXPENSE_PERIOD_FROM + " " + index, getDateTimeService().toDateString(awardAccount.getAccount().getAccountEffectiveDate()));
                }
                if (ObjectUtils.isNotNull(awardAccount.getAccount().getAccountExpirationDate())) {
                    contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.INDIRECT_EXPENSE_PERIOD_TO + " " + index, getDateTimeService().toDateString(awardAccount.getAccount().getAccountExpirationDate()));
                }
                contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.INDIRECT_EXPENSE_BASE + " " + index, contractsGrantsBillingUtilityService.formatForCurrency(award.getAwardTotalAmount()));
                Map<String, Object> key = new HashMap<String, Object>();
                key.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);
                key.put(KFSPropertyConstants.FINANCIAL_ICR_SERIES_IDENTIFIER, awardAccount.getAccount().getFinancialIcrSeriesIdentifier());
                key.put(KFSPropertyConstants.ACTIVE, true);
                key.put(KFSPropertyConstants.TRANSACTION_DEBIT_INDICATOR, KFSConstants.GL_DEBIT_CODE);
                List<IndirectCostRecoveryRateDetail> icrDetail = (List<IndirectCostRecoveryRateDetail>) businessObjectService.findMatchingOrderBy(IndirectCostRecoveryRateDetail.class, key, KFSPropertyConstants.AWARD_INDR_COST_RCVY_ENTRY_NBR, false);
                if (CollectionUtils.isNotEmpty(icrDetail)) {
                    KualiDecimal rate = new KualiDecimal(icrDetail.get(0).getAwardIndrCostRcvyRatePct());
                    if (ObjectUtils.isNotNull(rate)) {
                        contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.INDIRECT_EXPENSE_AMOUNT + " " + index, contractsGrantsBillingUtilityService.formatForCurrency(award.getAwardTotalAmount().multiply(rate)));
                        contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.INDIRECT_EXPENSE_FEDERAL + " " + index, contractsGrantsBillingUtilityService.formatForCurrency(award.getAwardTotalAmount().multiply(rate)));
                        amountSum = amountSum.add(award.getAwardTotalAmount().multiply(rate));
                    }
                }
                baseSum = baseSum.add(award.getAwardTotalAmount());
            }
            contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.INDIRECT_EXPENSE_BASE_SUM, contractsGrantsBillingUtilityService.formatForCurrency(baseSum));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.INDIRECT_EXPENSE_AMOUNT_SUM, contractsGrantsBillingUtilityService.formatForCurrency(amountSum));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.INDIRECT_EXPENSE_FEDERAL_SUM, contractsGrantsBillingUtilityService.formatForCurrency(amountSum));
        }

        final SystemInformation sysInfo = retrieveSystemInformationForAward(award, year);
        if (ObjectUtils.isNotNull(sysInfo)) {
            final String address = concatenateAddressFromSystemInformation(sysInfo);
            contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.RECIPIENT_ORGANIZATION, address);
            contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.ZWEI, sysInfo.getUniversityFederalEmployerIdentificationNumber());
        }

        contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.FEDERAL_AGENCY, award.getAgency().getFullName());
        contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.FEDERAL_GRANT_NUMBER, award.getAwardDocumentNumber());
        if(CollectionUtils.isNotEmpty(award.getActiveAwardAccounts())){
            contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.RECIPIENT_ACCOUNT_NUMBER, award.getActiveAwardAccounts().get(0).getAccountNumber());
        }
        if (ObjectUtils.isNotNull(award.getAwardBeginningDate())) {
            contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.GRANT_PERIOD_FROM, getDateTimeService().toDateString(award.getAwardBeginningDate()));
        }
        if (ObjectUtils.isNotNull(award.getAwardClosingDate())) {
            contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.GRANT_PERIOD_TO, getDateTimeService().toDateString(award.getAwardClosingDate()));
        }
        contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.CASH_RECEIPTS, contractsGrantsBillingUtilityService.formatForCurrency(this.getCashReceipts(award)));
        contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.TOTAL_FEDERAL_FUNDS_AUTHORIZED, contractsGrantsBillingUtilityService.formatForCurrency(award.getAwardTotalAmount()));

        contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.REPORTING_PERIOD_END_DATE, getReportingPeriodEndDate(reportingPeriod, year));
        if (ObjectUtils.isNotNull(cashDisbursement)) {
            contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.CASH_DISBURSEMENTS, contractsGrantsBillingUtilityService.formatForCurrency(cashDisbursement));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.CASH_ON_HAND, contractsGrantsBillingUtilityService.formatForCurrency(getCashReceipts(award).subtract(cashDisbursement)));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.FEDERAL_SHARE_OF_EXPENDITURES, contractsGrantsBillingUtilityService.formatForCurrency(cashDisbursement));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.TOTAL_FEDERAL_SHARE, contractsGrantsBillingUtilityService.formatForCurrency(cashDisbursement));
            contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.UNOBLIGATED_BALANCE_OF_FEDERAL_FUNDS, contractsGrantsBillingUtilityService.formatForCurrency(award.getAwardTotalAmount().subtract(cashDisbursement)));
        }
        contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.FEDERAL_SHARE_OF_UNLIQUIDATED_OBLIGATION, contractsGrantsBillingUtilityService.formatForCurrency(KualiDecimal.ZERO));

        contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.TOTAL_FEDERAL_INCOME_EARNED, KFSConstants.EMPTY_STRING);
        contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.INCOME_EXPENDED_DEDUCATION_ALTERNATIVE, KFSConstants.EMPTY_STRING);
        contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.INCOME_EXPENDED_ADDITION_ALTERNATIVE, KFSConstants.EMPTY_STRING);
        contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.UNEXPECTED_PROGRAM_INCOME, KFSConstants.EMPTY_STRING);
        contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.NAME, KFSConstants.EMPTY_STRING);
        contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.TELEPHONE, KFSConstants.EMPTY_STRING);
        contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.EMAIL_ADDRESS, KFSConstants.EMPTY_STRING);
        contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.DATE_REPORT_SUBMITTED, getDateTimeService().toDateString(getDateTimeService().getCurrentDate()));
        if (ArConstants.QUARTER1.equals(reportingPeriod) || ArConstants.QUARTER2.equals(reportingPeriod) || ArConstants.QUARTER3.equals(reportingPeriod) || ArConstants.QUARTER4.equals(reportingPeriod)) {
            contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.QUARTERLY, KFSConstants.OptionLabels.YES);
        }
        if (ArConstants.SEMI_ANNUAL.equals(reportingPeriod)) {
            contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.SEMI_ANNUAL, KFSConstants.OptionLabels.YES);
        }
        if (ArConstants.ANNUAL.equals(reportingPeriod)) {
            contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.ANNUAL, KFSConstants.OptionLabels.YES);
        }
        if (ArConstants.FINAL.equals(reportingPeriod)) {
            contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.FINAL, KFSConstants.OptionLabels.YES);
        }
        String accountingBasis = parameterService.getParameterValueAsString(ArConstants.AR_NAMESPACE_CODE, KRADConstants.DetailTypes.ALL_DETAIL_TYPE, ArConstants.BASIS_OF_ACCOUNTING);
        if (ArConstants.BASIS_OF_ACCOUNTING_CASH.equals(accountingBasis)) {
            contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.CASH, KFSConstants.OptionLabels.YES);
        }
        if (ArConstants.BASIS_OF_ACCOUNTING_ACCRUAL.equals(accountingBasis)) {
            contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.ACCRUAL, KFSConstants.OptionLabels.YES);
        }
    }

    /**
     * Concatenates the address from an AR System Information object into a single String
     * @param sysInfo the System Information business object to concatenate the address of
     * @return the concatenated address
     */
    protected String concatenateAddressFromSystemInformation(final SystemInformation sysInfo) {
        String address = sysInfo.getOrganizationRemitToAddressName();
        if(!StringUtils.isBlank(sysInfo.getOrganizationRemitToLine1StreetAddress())) {
            address += ", " + sysInfo.getOrganizationRemitToLine1StreetAddress();
        }
        if(!StringUtils.isBlank(sysInfo.getOrganizationRemitToLine2StreetAddress())) {
            address += ", " + sysInfo.getOrganizationRemitToLine2StreetAddress();
        }
        if(!StringUtils.isBlank(sysInfo.getOrganizationRemitToCityName())) {
            address += ", " + sysInfo.getOrganizationRemitToCityName();
        }
        if(!StringUtils.isBlank(sysInfo.getOrganizationRemitToStateCode())) {
            address += " " + sysInfo.getOrganizationRemitToStateCode();
        }
        if(!StringUtils.isBlank(sysInfo.getOrganizationRemitToZipCode())) {
            address += "-" + sysInfo.getOrganizationRemitToZipCode();
        }
        return address;
    }

    /**
     * Retrieves an AR System Information object for an award
     * @param award the award to retrieve an associated System Information for
     * @param year the year of the System Information object to retrieve
     * @return the System Information object, or null if nothing is found
     */
    protected SystemInformation retrieveSystemInformationForAward(ContractsAndGrantsBillingAward award, String year) {
        Map primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);
        primaryKeys.put(ArPropertyConstants.SystemInformationFields.PROCESSING_CHART_OF_ACCOUNTS_CODE, award.getPrimaryAwardOrganization().getChartOfAccountsCode());
        primaryKeys.put(ArPropertyConstants.SystemInformationFields.PROCESSING_ORGANIZATION_CODE, award.getPrimaryAwardOrganization().getOrganizationCode());
        SystemInformation sysInfo = businessObjectService.findByPrimaryKey(SystemInformation.class, primaryKeys);
        return sysInfo;
    }

    /**
     * This method is used to populate the replacement list to replace values from pdf template to actual values for Federal Form
     * 425A
     *
     * @param awards
     * @param reportingPeriod
     * @param year
     * @param agency
     * @return total amount
     */
    protected List<KualiDecimal> populateListByAgency(List<ContractsAndGrantsBillingAward> awards, String reportingPeriod, String year, ContractsAndGrantsBillingAgency agency) {
        Map<String, String> replacementList = new HashMap<String, String>();
        contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.REPORTING_PERIOD_END_DATE, getReportingPeriodEndDate(reportingPeriod, year));
        contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.FEDERAL_AGENCY, agency.getFullName());

        Map primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);
        if (CollectionUtils.isNotEmpty(awards)){
            primaryKeys.put(ArPropertyConstants.SystemInformationFields.PROCESSING_CHART_OF_ACCOUNTS_CODE, awards.get(0).getPrimaryAwardOrganization().getChartOfAccountsCode());
            primaryKeys.put(ArPropertyConstants.SystemInformationFields.PROCESSING_ORGANIZATION_CODE, awards.get(0).getPrimaryAwardOrganization().getOrganizationCode());
        }

        SystemInformation sysInfo = businessObjectService.findByPrimaryKey(SystemInformation.class, primaryKeys);

        if (ObjectUtils.isNotNull(sysInfo)) {
            String address = concatenateAddressFromSystemInformation(sysInfo);

            contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.RECIPIENT_ORGANIZATION, address);
            contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.ZWEI, sysInfo.getUniversityFederalEmployerIdentificationNumber());
        }

        if (ArConstants.QUARTER1.equals(reportingPeriod) || ArConstants.QUARTER2.equals(reportingPeriod) || ArConstants.QUARTER3.equals(reportingPeriod) || ArConstants.QUARTER4.equals(reportingPeriod)) {
            contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.QUARTERLY, KFSConstants.OptionLabels.YES);
        }
        if (ArConstants.SEMI_ANNUAL.equals(reportingPeriod)) {
            contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.SEMI_ANNUAL, KFSConstants.OptionLabels.YES);
        }
        if (ArConstants.ANNUAL.equals(reportingPeriod)) {
            contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.ANNUAL, KFSConstants.OptionLabels.YES);
        }
        if (ArConstants.FINAL.equals(reportingPeriod)) {
            contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.FINAL, KFSConstants.OptionLabels.YES);
        }
        String accountingBasis = parameterService.getParameterValueAsString(ArConstants.AR_NAMESPACE_CODE, KRADConstants.DetailTypes.ALL_DETAIL_TYPE, ArConstants.BASIS_OF_ACCOUNTING);
        if (ArConstants.BASIS_OF_ACCOUNTING_CASH.equals(accountingBasis)) {
            contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.CASH, KFSConstants.OptionLabels.YES);
        }
        if (ArConstants.BASIS_OF_ACCOUNTING_ACCRUAL.equals(accountingBasis)) {
            contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.ACCRUAL,KFSConstants.OptionLabels.YES);
        }
        contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.DATE_REPORT_SUBMITTED, getDateTimeService().toDateString(new Date(new java.util.Date().getTime())));
        KualiDecimal totalCashControl = KualiDecimal.ZERO;
        KualiDecimal totalCashDisbursement = KualiDecimal.ZERO;
        for (int i = 0; i < 30; i++) {
            if (i < awards.size()) {
                contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.FEDERAL_GRANT_NUMBER + " " + (i + 1), awards.get(i).getAwardDocumentNumber());
                contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.RECIPIENT_ACCOUNT_NUMBER + " " + (i + 1), awards.get(i).getActiveAwardAccounts().get(0).getAccountNumber());
                contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.FEDERAL_CASH_DISBURSEMENT + " " + (i + 1), contractsGrantsBillingUtilityService.formatForCurrency(getCashReceipts(awards.get(i))));
                totalCashControl = totalCashControl.add(this.getCashReceipts(awards.get(i)));

                for (ContractsAndGrantsBillingAwardAccount awardAccount : awards.get(i).getActiveAwardAccounts()) {
                    totalCashDisbursement = totalCashDisbursement.add(contractsGrantsInvoiceDocumentService.getBudgetAndActualsForAwardAccount(awardAccount, ArPropertyConstants.ACTUAL_BALANCE_TYPE, awards.get(i).getAwardBeginningDate()));
                }
            }
        }
        ArrayList<KualiDecimal> list = new ArrayList<KualiDecimal>();
        list.add(totalCashControl);
        list.add(totalCashDisbursement);
        return list;
    }

    /**
     * This method returns the last day of the given reporting period.
     *
     * @param reportingPeriod
     * @param year
     * @return
     */
    protected String getReportingPeriodEndDate(String reportingPeriod, String year) {
        Integer yearAsInt = Integer.parseInt(year);
        java.util.Date endDate = null;
        if (ArConstants.QUARTER1.equals(reportingPeriod)) {
            endDate = ArConstants.BillingQuarterLastDays.FIRST_QUARTER.getDateForYear(yearAsInt);
        }
        else if (ArConstants.QUARTER2.equals(reportingPeriod) || ArConstants.SEMI_ANNUAL.equals(reportingPeriod)) {
            endDate = ArConstants.BillingQuarterLastDays.SECOND_QUARTER.getDateForYear(yearAsInt);
        }
        else if (ArConstants.QUARTER3.equals(reportingPeriod)) {
            endDate = ArConstants.BillingQuarterLastDays.THIRD_QUARTER.getDateForYear(yearAsInt);
        }
        else {
            endDate = ArConstants.BillingQuarterLastDays.FOURTH_QUARTER.getDateForYear(yearAsInt);
        }
        return getDateTimeService().toDateString(endDate);
    }

    /**
     * Use iText <code>{@link PdfStamper}</code> to stamp information into field values on a PDF Form Template.
     *
     * @param award The award the values will be pulled from.
     * @param reportingPeriod
     * @param year
     * @param returnStream The output stream the federal form will be written to.
     */
    protected void stampPdfFormValues425(ContractsAndGrantsBillingAward award, String reportingPeriod, String year, OutputStream returnStream, Map<String, String> replacementList) {
        String reportTemplateName = ArConstants.FF_425_TEMPLATE_NM + KFSConstants.ReportGeneration.PDF_FILE_EXTENSION;
        try {
            String federalReportTemplatePath = configService.getPropertyValueAsString(KFSConstants.EXTERNALIZABLE_HELP_URL_KEY);
            // populate form with document values
            PdfReader reader = new PdfReader(federalReportTemplatePath + reportTemplateName);
            PdfStamper stamper = new PdfStamper(reader, returnStream);
            AcroFields fields = stamper.getAcroFields();
            populateListByAward(award, reportingPeriod, year, replacementList);
            for (String field : replacementList.keySet()) {
                fields.setField(field, replacementList.get(field));
            }
            stamper.close();
        }
        catch (IOException | DocumentException ex) {
            throw new RuntimeException("Troubles stamping the old 425!", ex);
        }
    }

    /**
     * Use iText <code>{@link PdfStamper}</code> to stamp information into field values on a PDF Form Template.
     *
     * @param agency The award the values will be pulled from.
     * @param reportingPeriod
     * @param year
     * @param returnStream The output stream the federal form will be written to.
     */
    protected void stampPdfFormValues425A(ContractsAndGrantsBillingAgency agency, String reportingPeriod, String year, OutputStream returnStream, Map<String, String> replacementList) {
        String federalReportTemplatePath = configService.getPropertyValueAsString(KFSConstants.EXTERNALIZABLE_HELP_URL_KEY);
        try {
            URL federal425ATemplateUrl = new URL(federalReportTemplatePath + ArConstants.FF_425A_TEMPLATE_NM + KFSConstants.ReportGeneration.PDF_FILE_EXTENSION);
            URL federal425TemplateUrl = new URL(federalReportTemplatePath + ArConstants.FF_425_TEMPLATE_NM + KFSConstants.ReportGeneration.PDF_FILE_EXTENSION);

            Map<String, Object> fieldValues = new HashMap<>();
            fieldValues.put(KFSPropertyConstants.AGENCY_NUMBER, agency.getAgencyNumber());
            fieldValues.put(KFSPropertyConstants.ACTIVE, Boolean.TRUE);
            List<ContractsAndGrantsBillingAward> awards = kualiModuleService.getResponsibleModuleService(ContractsAndGrantsBillingAward.class).getExternalizableBusinessObjectsList(ContractsAndGrantsBillingAward.class, fieldValues);
            Integer pageNumber = 1, totalPages;
            totalPages = (awards.size() / ArConstants.Federal425APdf.NUMBER_OF_SUMMARIES_PER_PAGE) + 1;
            PdfCopyFields copy = new PdfCopyFields(returnStream);

            // generate replacement list for FF425
            populateListByAgency(awards, reportingPeriod, year, agency);
            contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.TOTAL_PAGES, org.apache.commons.lang.ObjectUtils.toString(totalPages + 1));
            KualiDecimal sumCashControl = KualiDecimal.ZERO;
            KualiDecimal sumCumExp = KualiDecimal.ZERO;
            while (pageNumber <= totalPages) {
                List<ContractsAndGrantsBillingAward> awardsList = new ArrayList<ContractsAndGrantsBillingAward>();
                for (int i = ((pageNumber - 1) * ArConstants.Federal425APdf.NUMBER_OF_SUMMARIES_PER_PAGE); i < (pageNumber * ArConstants.Federal425APdf.NUMBER_OF_SUMMARIES_PER_PAGE); i++) {
                    if (i < awards.size()) {
                        awardsList.add(awards.get(i));
                    }
                }
                // generate replacement list for FF425
                List<KualiDecimal> list = populateListByAgency(awardsList, reportingPeriod, year, agency);
                if (CollectionUtils.isNotEmpty(list)){
                    sumCashControl = sumCashControl.add(list.get(0));
                    if (list.size() > 1){
                        sumCumExp = sumCumExp.add(list.get(1));
                    }
                }

                // populate form with document values
                contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.PAGE_NUMBER, org.apache.commons.lang.ObjectUtils.toString(pageNumber + 1));
                if (pageNumber == totalPages){
                    contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.TOTAL, contractsGrantsBillingUtilityService.formatForCurrency(sumCashControl));
                    contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.CASH_RECEIPTS, contractsGrantsBillingUtilityService.formatForCurrency(sumCashControl));
                    contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.CASH_DISBURSEMENTS, contractsGrantsBillingUtilityService.formatForCurrency(sumCumExp));
                    contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.CASH_ON_HAND, contractsGrantsBillingUtilityService.formatForCurrency(sumCashControl.subtract(sumCumExp)));
                }
                // add a document
                copy.addDocument(new PdfReader(PdfFormFillerUtil.populateTemplate(federal425ATemplateUrl.openStream(), replacementList)));
                pageNumber++;
            }
            contractsGrantsBillingUtilityService.putValueOrEmptyString(replacementList, ArPropertyConstants.FederalFormReportFields.PAGE_NUMBER, "1");

            // add the FF425 form.
            copy.addDocument(new PdfReader(PdfFormFillerUtil.populateTemplate(federal425TemplateUrl.openStream(), replacementList)));
            // Close the PdfCopyFields object
            copy.close();
        }
        catch (DocumentException | IOException ex) {
            throw new RuntimeException("Tried to stamp the 425A, but couldn't do it.  Just...just couldn't do it.", ex);
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceReportService#generateListOfInvoicesPdfToPrint(java.util.Collection)
     */
    @Override
    public byte[] combineInvoicePdfs(Collection<ContractsGrantsInvoiceDocument> list) throws DocumentException, IOException {
        Date runDate = new Date(new java.util.Date().getTime());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        generateCombinedPdfForInvoices(list, baos);
        return baos.toByteArray();
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceReportService#generateListOfInvoicesEnvelopesPdfToPrint(java.util.Collection)
     */
    @Override
    public byte[] combineInvoicePdfEnvelopes(Collection<ContractsGrantsInvoiceDocument> list) throws DocumentException, IOException {
        Date runDate = new Date(new java.util.Date().getTime());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        generateCombinedPdfForEnvelopes(list, baos);
        return baos.toByteArray();
    }

    /**
     * Generates the pdf file for printing the invoices.
     *
     * @param list
     * @param outputStream
     * @throws DocumentException
     * @throws IOException
     */
    protected void generateCombinedPdfForInvoices(Collection<ContractsGrantsInvoiceDocument> list, OutputStream outputStream) throws DocumentException, IOException {
        PdfCopyFields copy = new PdfCopyFields(outputStream);
        boolean pageAdded = false;
        for (ContractsGrantsInvoiceDocument invoice : list) {
            // add a document
            List<InvoiceAddressDetail> invoiceAddressDetails = invoice.getInvoiceAddressDetails();

            for (InvoiceAddressDetail invoiceAddressDetail : invoiceAddressDetails) {
                if (ArConstants.InvoiceTransmissionMethod.MAIL.equals(invoiceAddressDetail.getInvoiceTransmissionMethodCode())) {
                    CustomerAddress address = invoiceAddressDetail.getCustomerAddress();

                    Note note = noteService.getNoteByNoteId(invoiceAddressDetail.getNoteId());
                    Integer numberOfCopiesToPrint = address.getCustomerCopiesToPrint();
                    if (ObjectUtils.isNull(numberOfCopiesToPrint)) {
                        numberOfCopiesToPrint = 1;
                    }

                    if (!ObjectUtils.isNull(note)) {
                        for (int i = 0; i < numberOfCopiesToPrint; i++) {
                            if (!pageAdded) {
                                copy.open();
                            }
                            pageAdded = true;
                            copy.addDocument(new PdfReader(note.getAttachment().getAttachmentContents()));
                        }
                    }
                }
            }
            invoice.setDateReportProcessed(new Date(new java.util.Date().getTime()));
            documentService.updateDocument(invoice);
        }
        if (pageAdded) {
            copy.close();
        }
    }

    /**
     * Generates the pdf file for printing the envelopes.
     *
     * @param list
     * @param outputStream
     * @throws DocumentException
     * @throws IOException
     */
    protected void generateCombinedPdfForEnvelopes(Collection<ContractsGrantsInvoiceDocument> list, OutputStream outputStream) throws DocumentException, IOException {
        Document document = new Document(new Rectangle(ArConstants.InvoiceEnvelopePdf.LENGTH, ArConstants.InvoiceEnvelopePdf.WIDTH));
        PdfWriter.getInstance(document, outputStream);
        boolean pageAdded = false;

        for (ContractsGrantsInvoiceDocument invoice : list) {
            // add a document
            List<InvoiceAddressDetail> agencyAddresses = invoice.getInvoiceAddressDetails();

            for (InvoiceAddressDetail agencyAddress : agencyAddresses) {
                if (ArConstants.InvoiceTransmissionMethod.MAIL.equals(agencyAddress.getInvoiceTransmissionMethodCode())) {
                    CustomerAddress address = agencyAddress.getCustomerAddress();

                    Integer numberOfEnvelopesToPrint = address.getCustomerPrintEnvelopesNumber();
                    if (ObjectUtils.isNull(numberOfEnvelopesToPrint)) {
                        numberOfEnvelopesToPrint = 1;
                    }
                    for (int i = 0; i < numberOfEnvelopesToPrint; i++) {
                        // if a page has not already been added then open the document.
                        if (!pageAdded) {
                            document.open();
                        }
                        pageAdded = true;
                        document.newPage();
                        Paragraph sendTo = new Paragraph();
                        Paragraph sentBy = new Paragraph();
                        sentBy.setIndentationLeft(ArConstants.InvoiceEnvelopePdf.INDENTATION_LEFT);
                        // adding the send To address
                        sendTo.add(new Paragraph(address.getCustomerAddressName(), ArConstants.PdfReportFonts.ENVELOPE_TITLE_FONT));
                        if (StringUtils.isNotEmpty(address.getCustomerLine1StreetAddress())) {
                            sendTo.add(new Paragraph(address.getCustomerLine1StreetAddress(), ArConstants.PdfReportFonts.ENVELOPE_TITLE_FONT));
                        }
                        if (StringUtils.isNotEmpty(address.getCustomerLine2StreetAddress())) {
                            sendTo.add(new Paragraph(address.getCustomerLine2StreetAddress(), ArConstants.PdfReportFonts.ENVELOPE_TITLE_FONT));
                        }
                        String string = "";
                        if (StringUtils.isNotEmpty(address.getCustomerCityName())) {
                            string += address.getCustomerCityName();
                        }
                        if (StringUtils.isNotEmpty(address.getCustomerStateCode())) {
                            string += ", " + address.getCustomerStateCode();
                        }
                        if (StringUtils.isNotEmpty(address.getCustomerZipCode())) {
                            string += "-" + address.getCustomerZipCode();
                        }
                        if (StringUtils.isNotEmpty(string)) {
                            sendTo.add(new Paragraph(string, ArConstants.PdfReportFonts.ENVELOPE_TITLE_FONT));
                        }
                        sendTo.setAlignment(Element.ALIGN_CENTER);
                        sendTo.add(new Paragraph(KFSConstants.BLANK_SPACE));

                        // adding the sent From address
                        Organization org = invoice.getAward().getPrimaryAwardOrganization().getOrganization();
                        sentBy.add(new Paragraph(org.getOrganizationName(), ArConstants.PdfReportFonts.ENVELOPE_SMALL_FONT));
                        if (StringUtils.isNotEmpty(org.getOrganizationLine1Address())) {
                            sentBy.add(new Paragraph(org.getOrganizationLine1Address(), ArConstants.PdfReportFonts.ENVELOPE_SMALL_FONT));
                        }
                        if (StringUtils.isNotEmpty(org.getOrganizationLine2Address())) {
                            sentBy.add(new Paragraph(org.getOrganizationLine2Address(), ArConstants.PdfReportFonts.ENVELOPE_SMALL_FONT));
                        }
                        string = "";
                        if (StringUtils.isNotEmpty(address.getCustomerCityName())) {
                            string += org.getOrganizationCityName();
                        }
                        if (StringUtils.isNotEmpty(address.getCustomerStateCode())) {
                            string += ", " + org.getOrganizationStateCode();
                        }
                        if (StringUtils.isNotEmpty(address.getCustomerZipCode())) {
                            string += "-" + org.getOrganizationZipCode();
                        }
                        if (StringUtils.isNotEmpty(string)) {
                            sentBy.add(new Paragraph(string, ArConstants.PdfReportFonts.ENVELOPE_SMALL_FONT));
                        }
                        sentBy.setAlignment(Element.ALIGN_LEFT);

                        document.add(sentBy);
                        document.add(sendTo);
                    }
                }
            }
        }
        if (pageAdded) {
            document.close();
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceReportService#generateCSVToExport(org.kuali.kfs.module.ar.document.ContractsGrantsLOCReviewDocument)
     */
    @Override
    public byte[] convertLetterOfCreditReviewToCSV(ContractsGrantsLetterOfCreditReviewDocument LOCDocument) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(baos);
        CSVWriter csvWriter = new CSVWriter(writer);
        try {
            csvWriter.writeNext(new String[] {getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, KFSPropertyConstants.PROPOSAL_NUMBER),
                    getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, ArPropertyConstants.ReferralToCollectionsFields.AWARD_DOCUMENT_NUMBER),
                    getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, KFSPropertyConstants.ACCOUNT_DESCRIPTION),
                    getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE),
                    getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, KFSPropertyConstants.ACCOUNT_NUMBER),
                    getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, KFSPropertyConstants.ACCOUNT_EXPIRATION_DATE),
                    getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, ArPropertyConstants.AWARD_BUDGET_AMOUNT),
                    getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, ArPropertyConstants.CLAIM_ON_CASH_BALANCE),
                    getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, ArPropertyConstants.AMOUNT_TO_DRAW),
                    getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, ArPropertyConstants.FUNDS_NOT_DRAWN)});

            if (CollectionUtils.isNotEmpty(LOCDocument.getHeaderReviewDetails()) && CollectionUtils.isNotEmpty(LOCDocument.getAccountReviewDetails())) {
                for (ContractsGrantsLetterOfCreditReviewDetail item : LOCDocument.getHeaderReviewDetails()) {
                    String proposalNumber = org.apache.commons.lang.ObjectUtils.toString(item.getProposalNumber());
                    String awardDocumentNumber = org.apache.commons.lang.ObjectUtils.toString(item.getAwardDocumentNumber());

                    for (ContractsGrantsLetterOfCreditReviewDetail newItem : LOCDocument.getAccountReviewDetails()) {
                        if (org.apache.commons.lang.ObjectUtils.equals(item.getProposalNumber(), newItem.getProposalNumber())) {
                            csvWriter.writeNext(new String[] { proposalNumber,
                                awardDocumentNumber,
                                newItem.getAccountDescription(),
                                newItem.getChartOfAccountsCode(),
                                newItem.getAccountNumber(),
                                getDateTimeService().toDateString(newItem.getAccountExpirationDate()),
                                contractsGrantsBillingUtilityService.formatForCurrency(newItem.getAwardBudgetAmount()),
                                contractsGrantsBillingUtilityService.formatForCurrency(newItem.getClaimOnCashBalance()),
                                contractsGrantsBillingUtilityService.formatForCurrency(newItem.getAmountToDraw()),
                                contractsGrantsBillingUtilityService.formatForCurrency(newItem.getFundsNotDrawn()) });
                        }
                    }
                }
            }
        }
        finally {
            if (csvWriter != null) {
                try {
                    csvWriter.close();
                }
                catch (IOException ex) {
                    csvWriter = null;
                    throw new RuntimeException("problem during ContractsGrantsInvoiceReportServiceImpl.generateCSVToExport()", ex);
                }
            }
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                }
                catch (IOException ex) {
                    writer = null;
                    throw new RuntimeException("problem during ContractsGrantsInvoiceReportServiceImpl.generateCSVToExport()", ex);
                }
            }
        }
        return baos.toByteArray();
    }

    public PersonService getPersonService() {
        return personService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    /**
     * Gets the businessObjectService attribute.
     *
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setConfigService(ConfigurationService configService) {
        this.configService = configService;
    }

    /**
     * Sets the kualiModuleService attribute value.
     *
     * @param kualiModuleService The kualiModuleService to set.
     */
    @NonTransactional
    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    /**
     * @return the documentService
     */
    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * @param documentService the documentService to set
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public NoteService getNoteService() {
        return noteService;
    }

    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    public ReportInfo getReportInfo() {
        return reportInfo;
    }

    public void setReportInfo(ReportInfo reportInfo) {
        this.reportInfo = reportInfo;
    }

    public ReportGenerationService getReportGenerationService() {
        return reportGenerationService;
    }

    public void setReportGenerationService(ReportGenerationService reportGenerationService) {
        this.reportGenerationService = reportGenerationService;
    }

    public ContractsGrantsInvoiceDocumentService getContractsGrantsInvoiceDocumentService() {
        return contractsGrantsInvoiceDocumentService;
    }

    public void setContractsGrantsInvoiceDocumentService(ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService) {
        this.contractsGrantsInvoiceDocumentService = contractsGrantsInvoiceDocumentService;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public ContractsGrantsBillingUtilityService getContractsGrantsBillingUtilityService() {
        return contractsGrantsBillingUtilityService;
    }

    public void setContractsGrantsBillingUtilityService(ContractsGrantsBillingUtilityService contractsGrantsBillingUtilityService) {
        this.contractsGrantsBillingUtilityService = contractsGrantsBillingUtilityService;
    }
}
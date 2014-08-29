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
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.sys.service.ReportGenerationService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.web.format.CurrencyFormatter;
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

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
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
            Document document = new Document(new Rectangle(1350, 595));
            PdfWriter.getInstance(document, os);
            document.open();
            Font titleFont = new Font(Font.TIMES_ROMAN, 18, Font.BOLD);
            Font headerFont = new Font(Font.TIMES_ROMAN, 16, Font.BOLD);
            Font smallBold = new Font(Font.TIMES_ROMAN, 14, Font.BOLD);
            Paragraph header = new Paragraph();
            Paragraph text = new Paragraph();
            Paragraph title = new Paragraph();

            // Lets write the header
            header.add(new Paragraph(configService.getPropertyValueAsString(ArKeyConstants.LOC_REVIEW_PDF_TITLE), titleFont));
            if (StringUtils.isNotEmpty(locDocument.getLetterOfCreditFundGroupCode())) {
                header.add(new Paragraph(configService.getPropertyValueAsString(ArKeyConstants.LOC_REVIEW_PDF_HEADER_FUND_GROUP_CODE) + returnProperStringValue(locDocument.getLetterOfCreditFundGroupCode()), titleFont));
            }
            if (StringUtils.isNotEmpty(locDocument.getLetterOfCreditFundCode())) {
                header.add(new Paragraph(configService.getPropertyValueAsString(ArKeyConstants.LOC_REVIEW_PDF_HEADER_FUND_CODE) + returnProperStringValue(locDocument.getLetterOfCreditFundCode()), titleFont));
            }
            header.add(new Paragraph(KFSConstants.BLANK_SPACE));
            header.setAlignment(Element.ALIGN_CENTER);
            title.add(new Paragraph(configService.getPropertyValueAsString(ArKeyConstants.LOC_REVIEW_PDF_HEADER_DOCUMENT_NUMBER) + returnProperStringValue(locDocument.getDocumentNumber()), headerFont));
            Person person = getPersonService().getPerson(locDocument.getFinancialSystemDocumentHeader().getInitiatorPrincipalId());
            // writing the Document details
            title.add(new Paragraph(configService.getPropertyValueAsString(ArKeyConstants.LOC_REVIEW_PDF_HEADER_APP_DOC_STATUS) + returnProperStringValue(locDocument.getFinancialSystemDocumentHeader().getApplicationDocumentStatus()), headerFont));
            title.add(new Paragraph(configService.getPropertyValueAsString(ArKeyConstants.LOC_REVIEW_PDF_HEADER_DOCUMENT_INITIATOR) + returnProperStringValue(person.getName()), headerFont));
            title.add(new Paragraph(configService.getPropertyValueAsString(ArKeyConstants.LOC_REVIEW_PDF_HEADER_DOCUMENT_CREATE_DATE) + returnProperStringValue(getDateTimeService().toDateString(locDocument.getFinancialSystemDocumentHeader().getWorkflowCreateDate())), headerFont));

            title.add(new Paragraph(KFSConstants.BLANK_SPACE));
            title.setAlignment(Element.ALIGN_RIGHT);

            text.add(new Paragraph(configService.getPropertyValueAsString(ArKeyConstants.LOC_REVIEW_PDF_SUBHEADER_AWARDS), smallBold));
            text.add(new Paragraph(KFSConstants.BLANK_SPACE));

            document.add(header);
            document.add(title);
            document.add(text);
            PdfPTable table = new PdfPTable(11);
            table.setTotalWidth(1300f);
            // fix the absolute width of the table
            table.setLockedWidth(true);

            // relative col widths in proportions - 1/11
            float[] widths = new float[] { 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f };
            table.setWidths(widths);
            table.setHorizontalAlignment(0);
            addAwardHeaders(table);
            if (CollectionUtils.isNotEmpty(locDocument.getHeaderReviewDetails()) && CollectionUtils.isNotEmpty(locDocument.getAccountReviewDetails())) {
                for (ContractsGrantsLetterOfCreditReviewDetail item : locDocument.getHeaderReviewDetails()) {
                    table.addCell(returnProperStringValue(item.getProposalNumber()));
                    table.addCell(returnProperStringValue(item.getAwardDocumentNumber()));
                    table.addCell(returnProperStringValue(item.getAgencyNumber()));
                    table.addCell(returnProperStringValue(item.getCustomerNumber()));
                    table.addCell(returnProperStringValue(item.getAwardBeginningDate()));
                    table.addCell(returnProperStringValue(item.getAwardEndingDate()));
                    table.addCell(returnProperStringValue(item.getAwardBudgetAmount()));
                    table.addCell(returnProperStringValue(item.getLetterOfCreditAmount()));
                    table.addCell(returnProperStringValue(item.getClaimOnCashBalance()));
                    table.addCell(returnProperStringValue(item.getAmountToDraw()));
                    table.addCell(returnProperStringValue(item.getAmountAvailableToDraw()));

                    PdfPCell cell = new PdfPCell();
                    cell.setPadding(20f);
                    cell.setColspan(11);
                    PdfPTable newTable = new PdfPTable(8);
                    newTable.setTotalWidth(1000f);
                    // fix the absolute width of the newTable
                    newTable.setLockedWidth(true);

                    // relative col widths in proportions - 1/8
                    float[] newWidths = new float[] { 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f };
                    newTable.setWidths(newWidths);
                    newTable.setHorizontalAlignment(0);
                    addAccountsHeaders(newTable);
                    for (ContractsGrantsLetterOfCreditReviewDetail newItem : locDocument.getAccountReviewDetails()) {
                        if (item.getProposalNumber().equals(newItem.getProposalNumber())) {
                            newTable.addCell(returnProperStringValue(newItem.getAccountDescription()));
                            newTable.addCell(returnProperStringValue(newItem.getChartOfAccountsCode()));
                            newTable.addCell(returnProperStringValue(newItem.getAccountNumber()));
                            newTable.addCell(returnProperStringValue(newItem.getAccountExpirationDate()));
                            newTable.addCell(returnProperStringValue(newItem.getAwardBudgetAmount()));
                            newTable.addCell(returnProperStringValue(newItem.getClaimOnCashBalance()));
                            newTable.addCell(returnProperStringValue(newItem.getAmountToDraw()));
                            newTable.addCell(returnProperStringValue(newItem.getFundsNotDrawn()));
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
     * This method returns a proper String value for any given object.
     *
     * @param string
     * @return
     */
    protected String returnProperStringValue(Object string) {
        if (ObjectUtils.isNotNull(string)) {
            if (string instanceof KualiDecimal) {
                String amount = (new CurrencyFormatter()).format(string).toString();
                return "$" + (StringUtils.isEmpty(amount) ? "0.00" : amount);
            }
            return string.toString();
        }
        return "";
    }

    /**
     * This method is used to set the headers for the CG LOC review Document
     *
     * @param table
     */
    protected void addAccountsHeaders(PdfPTable table) {
        table.addCell("Account Description");
        table.addCell("Chart Code");
        table.addCell("Account/Chart of Account Number");
        table.addCell("Account Expiration Date");
        table.addCell("Award Budget Amount");
        table.addCell("Claim On Cash Balance");
        table.addCell("Amount To Draw");
        table.addCell("Funds Not Drawn");
    }

    /**
     * This method is used to set the headers for the CG LOC review Document
     *
     * @param table
     */
    protected void addAwardHeaders(PdfPTable table) {
        table.addCell("Award");
        table.addCell("Award Document Number");
        table.addCell("Agency Number");
        table.addCell("Customer Number");
        table.addCell("Award Start Date");
        table.addCell("Award Stop Date");
        table.addCell("Award Budget Amount");
        table.addCell("LOC Amount");
        table.addCell("Claim On Cash Balance");
        table.addCell("Amount To Draw");
        table.addCell("Award Amount Available To Draw");
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
                String fullReportFileName = reportGenerationService.buildFullFileName(runDate, reportDirectory, reportFileName, "FF425") + ".pdf";
                File file = new File(fullReportFileName);
                FileOutputStream fos = new FileOutputStream(file);
                stampPdfFormValues425(award, period, year, fos, replacementList);
                return file;
            }
            else if (formType.equals(ArConstants.FEDERAL_FORM_425A) && ObjectUtils.isNotNull(agency)) {
                String fullReportFileName = reportGenerationService.buildFullFileName(runDate, reportDirectory, reportFileName, "FF425A") + ".pdf";
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
                replacementList.put("Indirect Expense Type " + index, returnProperStringValue(awardAccount.getAccount().getAcctIndirectCostRcvyTypeCd()));
                replacementList.put("Indirect Expense Rate " + index, returnProperStringValue(awardAccount.getAccount().getFinancialIcrSeriesIdentifier()));
                if (ObjectUtils.isNotNull(awardAccount.getAccount().getAccountEffectiveDate())) {
                    replacementList.put("Indirect Expense Period From " + index, returnProperStringValue(getDateTimeService().toDateString(awardAccount.getAccount().getAccountEffectiveDate())));
                }
                if (ObjectUtils.isNotNull(awardAccount.getAccount().getAccountExpirationDate())) {
                    replacementList.put("Indirect Expense Period To " + index, returnProperStringValue(getDateTimeService().toDateString(awardAccount.getAccount().getAccountExpirationDate())));
                }
                replacementList.put("Indirect Expense Base " + index, returnProperStringValue(award.getAwardTotalAmount()));
                Map<String, Object> key = new HashMap<String, Object>();
                key.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);
                key.put("financialIcrSeriesIdentifier", awardAccount.getAccount().getFinancialIcrSeriesIdentifier());
                key.put(KFSPropertyConstants.ACTIVE, true);
                key.put("transactionDebitIndicator", KFSConstants.GL_DEBIT_CODE);
                List<IndirectCostRecoveryRateDetail> icrDetail = (List<IndirectCostRecoveryRateDetail>) businessObjectService.findMatchingOrderBy(IndirectCostRecoveryRateDetail.class, key, "awardIndrCostRcvyEntryNbr", false);
                if (CollectionUtils.isNotEmpty(icrDetail)) {
                    KualiDecimal rate = new KualiDecimal(icrDetail.get(0).getAwardIndrCostRcvyRatePct());
                    if (ObjectUtils.isNotNull(rate)) {
                        replacementList.put("Indirect Expense Amount " + index, returnProperStringValue(award.getAwardTotalAmount().multiply(rate)));
                        replacementList.put("Indirect Expense Federal " + index, returnProperStringValue(award.getAwardTotalAmount().multiply(rate)));
                        amountSum = amountSum.add(award.getAwardTotalAmount().multiply(rate));
                    }
                }
                baseSum = baseSum.add(award.getAwardTotalAmount());
            }
            replacementList.put("Indirect Expense Base Sum", returnProperStringValue(baseSum));
            replacementList.put("Indirect Expense Amount Sum", returnProperStringValue(amountSum));
            replacementList.put("Indirect Expense Federal Sum", returnProperStringValue(amountSum));
        }
        Map primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);
        primaryKeys.put(ArPropertyConstants.SystemInformationFields.PROCESSING_CHART_OF_ACCOUNTS_CODE, award.getPrimaryAwardOrganization().getChartOfAccountsCode());
        primaryKeys.put(ArPropertyConstants.SystemInformationFields.PROCESSING_ORGANIZATION_CODE, award.getPrimaryAwardOrganization().getOrganizationCode());
        SystemInformation sysInfo = businessObjectService.findByPrimaryKey(SystemInformation.class, primaryKeys);

        if (ObjectUtils.isNotNull(sysInfo)) {
            String address = returnProperStringValue(sysInfo.getOrganizationRemitToAddressName());
            if(StringUtils.isNotEmpty(sysInfo.getOrganizationRemitToLine1StreetAddress())) {
                address += ", " + returnProperStringValue(sysInfo.getOrganizationRemitToLine1StreetAddress());
            }
            if(StringUtils.isNotEmpty(sysInfo.getOrganizationRemitToLine2StreetAddress())) {
                address += ", " + returnProperStringValue(sysInfo.getOrganizationRemitToLine2StreetAddress());
            }
            if(StringUtils.isNotEmpty(sysInfo.getOrganizationRemitToCityName())) {
                address += ", " + returnProperStringValue(sysInfo.getOrganizationRemitToCityName());
            }
            if(StringUtils.isNotEmpty(sysInfo.getOrganizationRemitToStateCode())) {
                address += " " + returnProperStringValue(sysInfo.getOrganizationRemitToStateCode());
            }
            if(StringUtils.isNotEmpty(sysInfo.getOrganizationRemitToZipCode())) {
                address += "-" + returnProperStringValue(sysInfo.getOrganizationRemitToZipCode());
            }

            replacementList.put("Recipient Organization", returnProperStringValue(address));
            replacementList.put("EIN", returnProperStringValue(sysInfo.getUniversityFederalEmployerIdentificationNumber()));
        }
        replacementList.put("Federal Agency", returnProperStringValue(returnProperStringValue(award.getAgency().getFullName())));
        replacementList.put("Federal Grant Number", returnProperStringValue(award.getAwardDocumentNumber()));
        if(CollectionUtils.isNotEmpty(award.getActiveAwardAccounts())){
            replacementList.put("Recipient Account Number", returnProperStringValue(award.getActiveAwardAccounts().get(0).getAccountNumber()));
        }
        if (ObjectUtils.isNotNull(award.getAwardBeginningDate())) {
            replacementList.put("Grant Period From", returnProperStringValue(getDateTimeService().toDateString(award.getAwardBeginningDate())));
        }
        if (ObjectUtils.isNotNull(award.getAwardClosingDate())) {
            replacementList.put("Grant Period To", returnProperStringValue(getDateTimeService().toDateString(award.getAwardClosingDate())));
        }
        replacementList.put("Cash Receipts", returnProperStringValue(this.getCashReceipts(award)));
        replacementList.put("Total Federal Funds Authorized", returnProperStringValue(award.getAwardTotalAmount()));

        replacementList.put("Reporting Period End Date", returnProperStringValue(getReportingPeriodEndDate(reportingPeriod, year)));
        if (ObjectUtils.isNotNull(cashDisbursement)) {
            replacementList.put("Cash Disbursements", returnProperStringValue(cashDisbursement));
            replacementList.put("Cash on Hand", returnProperStringValue(this.getCashReceipts(award).subtract(cashDisbursement)));
            replacementList.put("Federal Share of Expenditures", returnProperStringValue(returnProperStringValue(cashDisbursement)));
            replacementList.put("Total Federal Share", returnProperStringValue(returnProperStringValue(cashDisbursement)));
            replacementList.put("Unobligated Balance of Federal Funds", returnProperStringValue(award.getAwardTotalAmount().subtract(cashDisbursement)));
        }
        replacementList.put("Federal Share of Unliquidated Obligations", returnProperStringValue(KualiDecimal.ZERO));

        replacementList.put("Total Federal Income Earned", returnProperStringValue(null));
        replacementList.put("Income Expended in Accordance to Deduction Alternative", returnProperStringValue(null));
        replacementList.put("Income Expended in Accordance to Addition Alternative", returnProperStringValue(null));
        replacementList.put("Unexpended Program Income", returnProperStringValue(null));
        replacementList.put("Name", returnProperStringValue(null));
        replacementList.put("Telephone", returnProperStringValue(null));
        replacementList.put("Email Address", returnProperStringValue(null));
        replacementList.put("Date Report Submitted", returnProperStringValue(getDateTimeService().toDateString(new Date(new java.util.Date().getTime()))));
        if (ArConstants.QUATER1.equals(reportingPeriod) || ArConstants.QUATER2.equals(reportingPeriod) || ArConstants.QUATER3.equals(reportingPeriod) || ArConstants.QUATER4.equals(reportingPeriod)) {
            replacementList.put("Quaterly", KFSConstants.OptionLabels.YES);
        }
        if (ArConstants.SEMI_ANNUAL.equals(reportingPeriod)) {
            replacementList.put("Semi Annual", KFSConstants.OptionLabels.YES);
        }
        if (ArConstants.ANNUAL.equals(reportingPeriod)) {
            replacementList.put("Annual", KFSConstants.OptionLabels.YES);
        }
        if (ArConstants.FINAL.equals(reportingPeriod)) {
            replacementList.put("Final", KFSConstants.OptionLabels.YES);
        }
        String accountingBasis = parameterService.getParameterValueAsString(ArConstants.AR_NAMESPACE_CODE, KRADConstants.DetailTypes.ALL_DETAIL_TYPE, ArConstants.BASIS_OF_ACCOUNTING);
        if (ArConstants.BASIS_OF_ACCOUNTING_CASH.equals(accountingBasis)) {
            replacementList.put("Cash", KFSConstants.OptionLabels.YES);
        }
        if (ArConstants.BASIS_OF_ACCOUNTING_ACCRUAL.equals(accountingBasis)) {
            replacementList.put("Accrual", KFSConstants.OptionLabels.YES);
        }
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
        replacementList.put("Reporting Period End Date", returnProperStringValue(getReportingPeriodEndDate(reportingPeriod, year)));
        replacementList.put("Federal Agency", returnProperStringValue(returnProperStringValue(agency.getFullName())));

        Map primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);
        if (CollectionUtils.isNotEmpty(awards)){
            primaryKeys.put(ArPropertyConstants.SystemInformationFields.PROCESSING_CHART_OF_ACCOUNTS_CODE, awards.get(0).getPrimaryAwardOrganization().getChartOfAccountsCode());
            primaryKeys.put(ArPropertyConstants.SystemInformationFields.PROCESSING_ORGANIZATION_CODE, awards.get(0).getPrimaryAwardOrganization().getOrganizationCode());
        }

        SystemInformation sysInfo = businessObjectService.findByPrimaryKey(SystemInformation.class, primaryKeys);

        if (ObjectUtils.isNotNull(sysInfo)) {
            String address = returnProperStringValue(sysInfo.getOrganizationRemitToAddressName());
            if(StringUtils.isNotEmpty(sysInfo.getOrganizationRemitToLine1StreetAddress())) {
                address += ", " + returnProperStringValue(sysInfo.getOrganizationRemitToLine1StreetAddress());
            }
            if(StringUtils.isNotEmpty(sysInfo.getOrganizationRemitToLine2StreetAddress())) {
                address += ", " + returnProperStringValue(sysInfo.getOrganizationRemitToLine2StreetAddress());
            }
            if(StringUtils.isNotEmpty(sysInfo.getOrganizationRemitToCityName())) {
                address += ", " + returnProperStringValue(sysInfo.getOrganizationRemitToCityName());
            }
            if(StringUtils.isNotEmpty(sysInfo.getOrganizationRemitToStateCode())) {
                address += " " + returnProperStringValue(sysInfo.getOrganizationRemitToStateCode());
            }
            if(StringUtils.isNotEmpty(sysInfo.getOrganizationRemitToZipCode())) {
                address += "-" + returnProperStringValue(sysInfo.getOrganizationRemitToZipCode());
            }

            replacementList.put("Recipient Organization", returnProperStringValue(address));
            replacementList.put("EIN", returnProperStringValue(sysInfo.getUniversityFederalEmployerIdentificationNumber()));
        }

        if (ArConstants.QUATER1.equals(reportingPeriod) || ArConstants.QUATER2.equals(reportingPeriod) || ArConstants.QUATER3.equals(reportingPeriod) || ArConstants.QUATER4.equals(reportingPeriod)) {
            replacementList.put("Quaterly", KFSConstants.OptionLabels.YES);
        }
        if (ArConstants.SEMI_ANNUAL.equals(reportingPeriod)) {
            replacementList.put("Semi Annual", KFSConstants.OptionLabels.YES);
        }
        if (ArConstants.ANNUAL.equals(reportingPeriod)) {
            replacementList.put("Annual", KFSConstants.OptionLabels.YES);
        }
        if (ArConstants.FINAL.equals(reportingPeriod)) {
            replacementList.put("Final", KFSConstants.OptionLabels.YES);
        }
        String accountingBasis = parameterService.getParameterValueAsString(ArConstants.AR_NAMESPACE_CODE, KRADConstants.DetailTypes.ALL_DETAIL_TYPE, ArConstants.BASIS_OF_ACCOUNTING);
        if (ArConstants.BASIS_OF_ACCOUNTING_CASH.equals(accountingBasis)) {
            replacementList.put("Cash", KFSConstants.OptionLabels.YES);
        }
        if (ArConstants.BASIS_OF_ACCOUNTING_ACCRUAL.equals(accountingBasis)) {
            replacementList.put("Accrual",KFSConstants.OptionLabels.YES);
        }
        replacementList.put("Date Report Submitted", returnProperStringValue(getDateTimeService().toDateString(new Date(new java.util.Date().getTime()))));
        KualiDecimal totalCashControl = KualiDecimal.ZERO;
        KualiDecimal totalCashDisbursement = KualiDecimal.ZERO;
        for (int i = 0; i < 30; i++) {
            if (i < awards.size()) {
                replacementList.put("Federal Grant Number " + (i + 1), returnProperStringValue(awards.get(i).getAwardDocumentNumber()));
                replacementList.put("Recipient Acount Number " + (i + 1), returnProperStringValue(awards.get(i).getActiveAwardAccounts().get(0).getAccountNumber()));
                replacementList.put("Federal Cash Disbursement " + (i + 1), returnProperStringValue(this.getCashReceipts(awards.get(i))));
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
        if (ArConstants.QUATER1.equals(reportingPeriod)) {
            return "03/31/" + year;
        }
        else if (ArConstants.QUATER2.equals(reportingPeriod) || ArConstants.SEMI_ANNUAL.equals(reportingPeriod)) {
            return "06/30/" + year;
        }
        else if (ArConstants.QUATER3.equals(reportingPeriod)) {
            return "09/30/" + year;
        }
        else {
            return "12/31/" + year;
        }
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
        String reportTemplateName = ArConstants.FF_425_TEMPLATE_NM + ".pdf";
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
        String reportTemplateName = ArConstants.FF_425A_TEMPLATE_NM + ".pdf";
        String federalReportTemplatePath = configService.getPropertyValueAsString(KFSConstants.EXTERNALIZABLE_HELP_URL_KEY);
        try {
            Map fieldValues = new HashMap<String, String>();
            fieldValues.put(KFSPropertyConstants.AGENCY_NUMBER, agency.getAgencyNumber());
            fieldValues.put(KFSPropertyConstants.ACTIVE, true);
            List<ContractsAndGrantsBillingAward> awards = kualiModuleService.getResponsibleModuleService(ContractsAndGrantsBillingAward.class).getExternalizableBusinessObjectsList(ContractsAndGrantsBillingAward.class, fieldValues);
            Integer pageNumber = 1, totalPages;
            totalPages = (awards.size() / 30) + 1;
            PdfCopyFields copy = new PdfCopyFields(returnStream);

            // generate replacement list for FF425
            populateListByAgency(awards, reportingPeriod, year, agency);
            replacementList.put("totalPages", returnProperStringValue(totalPages + 1));
            KualiDecimal sumCashControl = KualiDecimal.ZERO;
            KualiDecimal sumCumExp = KualiDecimal.ZERO;
            while (pageNumber <= totalPages) {
                List<ContractsAndGrantsBillingAward> awardsList = new ArrayList<ContractsAndGrantsBillingAward>();
                for (int i = ((pageNumber - 1) * 30); i < (pageNumber * 30); i++) {
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
                replacementList.put("pageNumber", returnProperStringValue(pageNumber + 1));
                if (pageNumber == totalPages){
                    replacementList.put("Total", returnProperStringValue(sumCashControl));
                    replacementList.put("Cash Receipts", returnProperStringValue(sumCashControl));
                    replacementList.put("Cash Disbursements", returnProperStringValue(sumCumExp));
                    replacementList.put("Cash on Hand", returnProperStringValue(sumCashControl.subtract(sumCumExp)));
                }
                // add a document
                copy.addDocument(new PdfReader(renameFieldsIn(federalReportTemplatePath + reportTemplateName, replacementList)));
                pageNumber++;
            }
            replacementList.put("pageNumber", "1");

            // add the FF425 form.
            copy.addDocument(new PdfReader(renameFieldsIn(federalReportTemplatePath + ArConstants.FF_425_TEMPLATE_NM + ".pdf", replacementList)));
            // Close the PdfCopyFields object
            copy.close();
        }
        catch (DocumentException | IOException ex) {
            throw new RuntimeException("Tried to stamp the 425A, but couldn't do it.  Just...just couldn't do it.", ex);
        }
    }

    /**
     *
     *
     * @param template the path to the original form
     * @param list the replacement list
     * @return
     * @throws IOException
     * @throws DocumentException
     */
    protected static byte[] renameFieldsIn(String template, Map<String, String> list) throws IOException, DocumentException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Create the stamper
        PdfStamper stamper = new PdfStamper(new PdfReader(template), baos);
        // Get the fields
        AcroFields fields = stamper.getAcroFields();
        // Loop over the fields
        for (String field : list.keySet()) {
            fields.setField(field, list.get(field));
        }
        // close the stamper
        stamper.close();
        return baos.toByteArray();
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
        // copy.open();
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
        Document document = new Document(new Rectangle(650, 320));
        PdfWriter.getInstance(document, outputStream);
        boolean pageAdded = false;
        Font titleFont = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
        Font smallFont = new Font(Font.TIMES_ROMAN, 9, Font.NORMAL);
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
                        sentBy.setIndentationLeft(20);
                        // adding the send To address
                        sendTo.add(new Paragraph(address.getCustomerAddressName(), titleFont));
                        if (StringUtils.isNotEmpty(address.getCustomerLine1StreetAddress())) {
                            sendTo.add(new Paragraph(address.getCustomerLine1StreetAddress(), titleFont));
                        }
                        if (StringUtils.isNotEmpty(address.getCustomerLine2StreetAddress())) {
                            sendTo.add(new Paragraph(address.getCustomerLine2StreetAddress(), titleFont));
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
                            sendTo.add(new Paragraph(string, titleFont));
                        }
                        sendTo.setAlignment(Element.ALIGN_CENTER);
                        sendTo.add(new Paragraph(KFSConstants.BLANK_SPACE));

                        // adding the sent From address
                        Organization org = invoice.getAward().getPrimaryAwardOrganization().getOrganization();
                        sentBy.add(new Paragraph(org.getOrganizationName(), smallFont));
                        if (StringUtils.isNotEmpty(org.getOrganizationLine1Address())) {
                            sentBy.add(new Paragraph(org.getOrganizationLine1Address(), smallFont));
                        }
                        if (StringUtils.isNotEmpty(org.getOrganizationLine2Address())) {
                            sentBy.add(new Paragraph(org.getOrganizationLine2Address(), smallFont));
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
                            sentBy.add(new Paragraph(string, smallFont));
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
        try {
            writer.append(getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, KFSPropertyConstants.PROPOSAL_NUMBER));
            writer.append(',');
            writer.append(getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, ArPropertyConstants.ReferralToCollectionsFields.AWARD_DOCUMENT_NUMBER));
            writer.append(',');
            writer.append(getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, KFSPropertyConstants.ACCOUNT_DESCRIPTION));
            writer.append(',');
            writer.append(getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE));
            writer.append(',');
            writer.append(getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, KFSPropertyConstants.ACCOUNT_NUMBER));
            writer.append(',');
            writer.append(getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, KFSPropertyConstants.ACCOUNT_EXPIRATION_DATE));
            writer.append(',');
            writer.append(getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, ArPropertyConstants.AWARD_BUDGET_AMOUNT));
            writer.append(',');
            writer.append(getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, ArPropertyConstants.CLAIM_ON_CASH_BALANCE));
            writer.append(',');
            writer.append(getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, ArPropertyConstants.AMOUNT_TO_DRAW));
            writer.append(',');
            writer.append(getDataDictionaryService().getAttributeLabel(ContractsGrantsLetterOfCreditReviewDetail.class, ArPropertyConstants.FUNDS_NOT_DRAWN));
            writer.append('\n');

            if (CollectionUtils.isNotEmpty(LOCDocument.getHeaderReviewDetails()) && CollectionUtils.isNotEmpty(LOCDocument.getAccountReviewDetails())) {
                for (ContractsGrantsLetterOfCreditReviewDetail item : LOCDocument.getHeaderReviewDetails()) {
                    String proposalNumber = returnProperStringValue(item.getProposalNumber());
                    String awardDocumentNumber = returnProperStringValue(item.getAwardDocumentNumber());

                    for (ContractsGrantsLetterOfCreditReviewDetail newItem : LOCDocument.getAccountReviewDetails()) {
                        if (item.getProposalNumber().equals(newItem.getProposalNumber())) {
                            writer.append("\"" + proposalNumber + "\"");
                            writer.append(',');
                            writer.append("\"" + awardDocumentNumber + "\"");
                            writer.append(',');
                            writer.append("\"" + returnProperStringValue(newItem.getAccountDescription()) + "\"");
                            writer.append(',');
                            writer.append("\"" + returnProperStringValue(newItem.getChartOfAccountsCode()) + "\"");
                            writer.append(',');
                            writer.append("\"" + returnProperStringValue(newItem.getAccountNumber()) + "\"");
                            writer.append(',');
                            writer.append("\"" + returnProperStringValue(newItem.getAccountExpirationDate()) + "\"");
                            writer.append(',');
                            writer.append("\"" + returnProperStringValue(newItem.getAwardBudgetAmount()) + "\"");
                            writer.append(',');
                            writer.append("\"" + returnProperStringValue(newItem.getClaimOnCashBalance()) + "\"");
                            writer.append(',');
                            writer.append("\"" + returnProperStringValue(newItem.getAmountToDraw()) + "\"");
                            writer.append(',');
                            writer.append("\"" + returnProperStringValue(newItem.getFundsNotDrawn()) + "\"");
                            writer.append('\n');
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            LOG.error("problem during ContractsGrantsInvoiceReportServiceImpl.generateCSVToExport()", e);
            throw new RuntimeException("problem during ContractsGrantsInvoiceReportServiceImpl.generateCSVToExport()", e);
        }
        finally {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                }
                catch (IOException ex) {
                    writer = null;
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
}
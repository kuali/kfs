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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryRateDetail;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAgencyAddress;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAwardAccount;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsLetterOfCreditReviewDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceAgencyAddressDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.ContractsGrantsLetterOfCreditReviewDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
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
public class ContractsGrantsInvoiceReportServiceImpl extends ContractsGrantsReportServiceImplBase implements ContractsGrantsInvoiceReportService {

    private static final String FF_425_TEMPLATE_NM = "FEDERAL_FINANCIAL_FORM_425";
    private static final String FF_425A_TEMPLATE_NM = "FEDERAL_FINANCIAL_FORM_425A";
    private static final Object FEDERAL_FORM_425 = "425";
    private static final Object FEDERAL_FORM_425A = "425A";
    private ReportInfo contractsGrantsInvoiceReportInfo;
    private Map<String, String> replacementList = new HashMap<String, String>();
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceReportService#generateReport(org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder,
     *      java.io.ByteArrayOutputStream)
     */
    @Override
    public String generateReport(ContractsGrantsReportDataHolder reportDataHolder, ByteArrayOutputStream baos) {
        return generateReport(reportDataHolder, contractsGrantsInvoiceReportInfo, baos);
    }

    /**
     * @return contractsGrantsInvoiceReportInfo
     */
    public ReportInfo getContractsGrantsInvoiceReportInfo() {
        return contractsGrantsInvoiceReportInfo;
    }

    /**
     * @param contractsGrantsInvoiceReportInfo
     */
    public void setContractsGrantsInvoiceReportInfo(ReportInfo contractsGrantsInvoiceReportInfo) {
        this.contractsGrantsInvoiceReportInfo = contractsGrantsInvoiceReportInfo;
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceReportService#generateInvoice(org.kuali.kfs.module.ar.document.ContractsGrantsLOCReviewDocument)
     */
    @Override
    public byte[] generateInvoice(ContractsGrantsLetterOfCreditReviewDocument document) {
        Date runDate = new Date();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.generateInvoiceInPdf(baos, document);
        return baos.toByteArray();
    }

    /**
     * this method generated the actual pdf for the Contracts and Grants LOC Review Document.
     *
     * @param os
     * @param LOCDocument
     */
    private void generateInvoiceInPdf(OutputStream os, ContractsGrantsLetterOfCreditReviewDocument LOCDocument) {
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
            header.add(new Paragraph("Contracts and Grants Letter of Credit Review Document", titleFont));
            if (StringUtils.isNotEmpty(LOCDocument.getLetterOfCreditFundGroupCode())) {
                header.add(new Paragraph("Letter of Credit Fund Group: " + returnProperStringValue(LOCDocument.getLetterOfCreditFundGroupCode()), titleFont));
            }
            if (StringUtils.isNotEmpty(LOCDocument.getLetterOfCreditFundCode())) {
                header.add(new Paragraph("Letter of Credit Fund: " + returnProperStringValue(LOCDocument.getLetterOfCreditFundCode()), titleFont));
            }
            header.add(new Paragraph(" "));
            header.setAlignment(Element.ALIGN_CENTER);
            title.add(new Paragraph("Document Number: " + returnProperStringValue(LOCDocument.getDocumentNumber()), headerFont));
            Person person = SpringContext.getBean(PersonService.class).getPerson(LOCDocument.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
            // writing the Document details
            title.add(new Paragraph("Document Status: " + returnProperStringValue(LOCDocument.getDocumentHeader().getWorkflowDocument().getApplicationDocumentStatus()), headerFont));
            title.add(new Paragraph("Document Initiator: " + returnProperStringValue(person.getName()), headerFont));
            title.add(new Paragraph("Document Creation Date: " + returnProperStringValue(LOCDocument.getDocumentHeader().getWorkflowDocument().getDateCreated().toLocalDate().toString()), headerFont));

            title.add(new Paragraph(" "));
            title.setAlignment(Element.ALIGN_RIGHT);

            text.add(new Paragraph("Awards", smallBold));
            text.add(new Paragraph(" "));

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
            if (CollectionUtils.isNotEmpty(LOCDocument.getHeaderReviewDetails()) && CollectionUtils.isNotEmpty(LOCDocument.getAccountReviewDetails())) {
                for (ContractsGrantsLetterOfCreditReviewDetail item : LOCDocument.getHeaderReviewDetails()) {
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
                    for (ContractsGrantsLetterOfCreditReviewDetail newItem : LOCDocument.getAccountReviewDetails()) {
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
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method returns a proper String value for any given object.
     *
     * @param string
     * @return
     */
    private String returnProperStringValue(Object string) {
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
    private void addAccountsHeaders(PdfPTable table) {
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
    private void addAwardHeaders(PdfPTable table) {
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
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceReportService#generateFederalFinancialForm(org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward,
     *      java.lang.String, java.lang.String, java.lang.String, org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAgency)
     */
    @Override
    public File generateFederalFinancialForm(ContractsAndGrantsCGBAward award, String period, String year, String formType, ContractsAndGrantsCGBAgency agency) throws Exception {
        Date runDate = new Date();
        String reportFileName = contractsGrantsInvoiceReportInfo.getReportFileName();
        String reportDirectory = contractsGrantsInvoiceReportInfo.getReportsDirectory();
        if (formType.equals(FEDERAL_FORM_425) && ObjectUtils.isNotNull(award)) {
            String fullReportFileName = reportGenerationService.buildFullFileName(runDate, reportDirectory, reportFileName, "FF425") + ".pdf";
            File file = new File(fullReportFileName);
            FileOutputStream fos = new FileOutputStream(file);
            stampPdfFormValues425(award, period, year, fos);
            return file;
        }
        else if (formType.equals(FEDERAL_FORM_425A) && ObjectUtils.isNotNull(agency)) {
            String fullReportFileName = reportGenerationService.buildFullFileName(runDate, reportDirectory, reportFileName, "FF425A") + ".pdf";
            File file = new File(fullReportFileName);
            FileOutputStream fos = new FileOutputStream(file);
            stampPdfFormValues425A(agency, period, year, fos);
            return file;
        }
        return null;
    }

    /**
     * Gets the replacementList attribute.
     *
     * @return Returns the replacementList.
     */
    public Map<String, String> getReplacementList() {
        return replacementList;
    }

    /**
     * Sets the replacementList attribute value.
     *
     * @param replacementList The replacementList to set.
     */
    public void setReplacementList(Map<String, String> replacementList) {
        this.replacementList = replacementList;
    }

    /**
     * @param award
     * @return
     */
    protected KualiDecimal getCashReceipts(ContractsAndGrantsCGBAward award) {
    	KualiDecimal cashReceipt = KualiDecimal.ZERO;
    	Criteria key = new Criteria();
        key.addEqualTo("proposalNumber", award.getProposalNumber());
        List<ContractsGrantsInvoiceDocument> list = (List<ContractsGrantsInvoiceDocument>) SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class).retrieveAllCGInvoicesByCriteria(key);
        for(ContractsGrantsInvoiceDocument invoice: list){
            Map primaryKeys = new HashMap<String, Object>();
            primaryKeys.put("financialDocumentReferenceInvoiceNumber", invoice.getDocumentNumber());
            List<InvoicePaidApplied> ipas = (List<InvoicePaidApplied>)SpringContext.getBean(BusinessObjectService.class).findMatching(InvoicePaidApplied.class, primaryKeys);
            if(ObjectUtils.isNotNull(ipas)) {
                for(InvoicePaidApplied ipa : ipas) {
                    cashReceipt = cashReceipt.add(ipa.getInvoiceItemAppliedAmount());
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
    private void populateListByAward(ContractsAndGrantsCGBAward award, String reportingPeriod, String year) {
        replacementList.clear();
        ContractsGrantsInvoiceDocumentService service = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
        KualiDecimal cashDisbursement = KualiDecimal.ZERO;
        for (ContractsAndGrantsCGBAwardAccount awardAccount : award.getActiveAwardAccounts()) {
            int index = 0;
            KualiDecimal baseSum = KualiDecimal.ZERO;
            KualiDecimal amountSum = KualiDecimal.ZERO;
            cashDisbursement = cashDisbursement.add(service.getBudgetAndActualsForAwardAccount(awardAccount, ArPropertyConstants.ACTUAL_BALANCE_TYPE, award.getAwardBeginningDate()));
            if (ObjectUtils.isNotNull(awardAccount.getAccount().getFinancialIcrSeriesIdentifier()) && ObjectUtils.isNotNull(awardAccount.getAccount().getAcctIndirectCostRcvyTypeCd())) {
                index++;
                replacementList.put("Indirect Expense Type " + index, returnProperStringValue(awardAccount.getAccount().getAcctIndirectCostRcvyTypeCd()));
                replacementList.put("Indirect Expense Rate " + index, returnProperStringValue(awardAccount.getAccount().getFinancialIcrSeriesIdentifier()));
                replacementList.put("Indirect Expense Period From " + index, returnProperStringValue(formatter.format(awardAccount.getAccount().getAccountEffectiveDate())));
                replacementList.put("Indirect Expense Period To " + index, returnProperStringValue(formatter.format(awardAccount.getAccount().getAccountExpirationDate())));
                replacementList.put("Indirect Expense Base " + index, returnProperStringValue(award.getAwardTotalAmount()));
                Map<String, Object> key = new HashMap<String, Object>();
                key.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);
                key.put("financialIcrSeriesIdentifier", awardAccount.getAccount().getFinancialIcrSeriesIdentifier());
                key.put(KFSPropertyConstants.ACTIVE, true);
                key.put("transactionDebitIndicator", KFSConstants.GL_DEBIT_CODE);
                List<IndirectCostRecoveryRateDetail> icrDetail = (List<IndirectCostRecoveryRateDetail>) SpringContext.getBean(BusinessObjectService.class).findMatchingOrderBy(IndirectCostRecoveryRateDetail.class, key, "awardIndrCostRcvyEntryNbr", false);
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
        primaryKeys.put("processingChartOfAccountCode", award.getPrimaryAwardOrganization().getChartOfAccountsCode());
        primaryKeys.put("processingOrganizationCode", award.getPrimaryAwardOrganization().getOrganizationCode());
        SystemInformation sysInfo = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(SystemInformation.class, primaryKeys);

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
        replacementList.put("Recipient Account Number", returnProperStringValue(award.getActiveAwardAccounts().get(0).getAccountNumber()));
        if (ObjectUtils.isNotNull(award.getAwardBeginningDate())) {
            replacementList.put("Grant Period From", returnProperStringValue(formatter.format(award.getAwardBeginningDate())));
        }
        if (ObjectUtils.isNotNull(award.getAwardClosingDate())) {
            replacementList.put("Grant Period To", returnProperStringValue(formatter.format(award.getAwardClosingDate())));
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
        replacementList.put("Date Report Submitted", returnProperStringValue(formatter.format(new Date())));
        if (ArConstants.QUATER1.equals(reportingPeriod) || ArConstants.QUATER2.equals(reportingPeriod) || ArConstants.QUATER3.equals(reportingPeriod) || ArConstants.QUATER4.equals(reportingPeriod)) {
            replacementList.put("Quaterly", "Yes");
        }
        if (ArConstants.SEMI_ANNUAL.equals(reportingPeriod)) {
            replacementList.put("Semi Annual", "Yes");
        }
        if (ArConstants.ANNUAL.equals(reportingPeriod)) {
            replacementList.put("Annual", "Yes");
        }
        if (ArConstants.FINAL.equals(reportingPeriod)) {
            replacementList.put("Final", "Yes");
        }
        String accountingBasis = SpringContext.getBean(ParameterService.class).getParameterValueAsString(ArConstants.AR_NAMESPACE_CODE, KRADConstants.DetailTypes.ALL_DETAIL_TYPE, ArConstants.BASIS_OF_ACCOUNTING);
        if (ArConstants.BASIS_OF_ACCOUNTING_CASH.equals(accountingBasis)) {
            replacementList.put("Cash", "Yes");
        }
        if (ArConstants.BASIS_OF_ACCOUNTING_ACCRUAL.equals(accountingBasis)) {
            replacementList.put("Accrual", "Yes");
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
    private List<KualiDecimal> populateListByAgency(List<ContractsAndGrantsCGBAward> awards, String reportingPeriod, String year, ContractsAndGrantsCGBAgency agency) {
        replacementList.clear();
        replacementList.put("Reporting Period End Date", returnProperStringValue(getReportingPeriodEndDate(reportingPeriod, year)));
        replacementList.put("Federal Agency", returnProperStringValue(returnProperStringValue(agency.getFullName())));

        Map primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);
        primaryKeys.put("processingChartOfAccountCode", awards.get(0).getPrimaryAwardOrganization().getChartOfAccountsCode());
        primaryKeys.put("processingOrganizationCode", awards.get(0).getPrimaryAwardOrganization().getOrganizationCode());
        SystemInformation sysInfo = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(SystemInformation.class, primaryKeys);

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
            replacementList.put("Quaterly", "Yes");
        }
        if (ArConstants.SEMI_ANNUAL.equals(reportingPeriod)) {
            replacementList.put("Semi Annual", "Yes");
        }
        if (ArConstants.ANNUAL.equals(reportingPeriod)) {
            replacementList.put("Annual", "Yes");
        }
        if (ArConstants.FINAL.equals(reportingPeriod)) {
            replacementList.put("Final", "Yes");
        }
        String accountingBasis = SpringContext.getBean(ParameterService.class).getParameterValueAsString(ArConstants.AR_NAMESPACE_CODE, KRADConstants.DetailTypes.ALL_DETAIL_TYPE, ArConstants.BASIS_OF_ACCOUNTING);
        if (ArConstants.BASIS_OF_ACCOUNTING_CASH.equals(accountingBasis)) {
            replacementList.put("Cash", "Yes");
        }
        if (ArConstants.BASIS_OF_ACCOUNTING_ACCRUAL.equals(accountingBasis)) {
            replacementList.put("Accrual", "Yes");
        }
        replacementList.put("Date Report Submitted", returnProperStringValue(formatter.format(new Date())));
        KualiDecimal totalCashControl = KualiDecimal.ZERO;
        KualiDecimal totalCashDisbursement = KualiDecimal.ZERO;
        ContractsGrantsInvoiceDocumentService service = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
        for (int i = 0; i < 30; i++) {
            if (i < awards.size()) {
                replacementList.put("Federal Grant Number " + (i + 1), returnProperStringValue(awards.get(i).getAwardDocumentNumber()));
                replacementList.put("Recipient Acount Number " + (i + 1), returnProperStringValue(awards.get(i).getActiveAwardAccounts().get(0).getAccountNumber()));
                replacementList.put("Federal Cash Disbursement " + (i + 1), returnProperStringValue(this.getCashReceipts(awards.get(i))));
                totalCashControl = totalCashControl.add(this.getCashReceipts(awards.get(i)));

                for (ContractsAndGrantsCGBAwardAccount awardAccount : awards.get(i).getActiveAwardAccounts()) {
                    totalCashDisbursement = totalCashDisbursement.add(service.getBudgetAndActualsForAwardAccount(awardAccount, ArPropertyConstants.ACTUAL_BALANCE_TYPE, awards.get(i).getAwardBeginningDate()));
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
    private String getReportingPeriodEndDate(String reportingPeriod, String year) {
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
     * @throws Exception
     */
    protected void stampPdfFormValues425(ContractsAndGrantsCGBAward award, String reportingPeriod, String year, OutputStream returnStream) throws Exception {
        String reportTemplateName = FF_425_TEMPLATE_NM + ".pdf";
        try {
            String federalReportTemplatePath = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.EXTERNALIZABLE_HELP_URL_KEY);
            // populate form with document values
            PdfReader reader = new PdfReader(federalReportTemplatePath + reportTemplateName);
            PdfStamper stamper = new PdfStamper(reader, returnStream);
            AcroFields fields = stamper.getAcroFields();
            populateListByAward(award, reportingPeriod, year);
            Map<String, String> list = getReplacementList();
            for (String field : list.keySet()) {
                fields.setField(field, list.get(field));
            }
            stamper.close();
        }
        catch (Exception e) {
            throw e;
        }
    }

    /**
     * Use iText <code>{@link PdfStamper}</code> to stamp information into field values on a PDF Form Template.
     *
     * @param agency The award the values will be pulled from.
     * @param reportingPeriod
     * @param year
     * @param returnStream The output stream the federal form will be written to.
     * @throws Exception
     */
    protected void stampPdfFormValues425A(ContractsAndGrantsCGBAgency agency, String reportingPeriod, String year, OutputStream returnStream) throws Exception {
        String reportTemplateName = FF_425A_TEMPLATE_NM + ".pdf";
        String federalReportTemplatePath = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.EXTERNALIZABLE_HELP_URL_KEY);
        try {
            Map fieldValues = new HashMap<String, String>();
            fieldValues.put(KFSPropertyConstants.AGENCY_NUMBER, agency.getAgencyNumber());
            fieldValues.put(KFSPropertyConstants.ACTIVE, true);
            List<ContractsAndGrantsCGBAward> awards = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsCGBAward.class).getExternalizableBusinessObjectsList(ContractsAndGrantsCGBAward.class, fieldValues);
            Integer pageNumber = 1, totalPages;
            totalPages = (awards.size() / 30) + 1;
            PdfCopyFields copy = new PdfCopyFields(returnStream);

            // generate replacement list for FF425
            populateListByAgency(awards, reportingPeriod, year, agency);
            replacementList.put("totalPages", returnProperStringValue(totalPages + 1));
            KualiDecimal sumCashControl = KualiDecimal.ZERO;
            KualiDecimal sumCumExp = KualiDecimal.ZERO;
            while (pageNumber <= totalPages) {
                List<ContractsAndGrantsCGBAward> awardsList = new ArrayList<ContractsAndGrantsCGBAward>();
                for (int i = ((pageNumber - 1) * 30); i < (pageNumber * 30); i++) {
                    if (i < awards.size()) {
                        awardsList.add(awards.get(i));
                    }
                }
                // generate replacement list for FF425
                List<KualiDecimal> list = populateListByAgency(awardsList, reportingPeriod, year, agency);
                sumCashControl = sumCashControl.add(list.get(0));
                sumCumExp = sumCumExp.add(list.get(1));

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
            copy.addDocument(new PdfReader(renameFieldsIn(federalReportTemplatePath + FF_425_TEMPLATE_NM + ".pdf", replacementList)));
            // Close the PdfCopyFields object
            copy.close();
        }
        catch (Exception e) {
            throw e;
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
    private static byte[] renameFieldsIn(String template, Map<String, String> list) throws IOException, DocumentException {
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
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceReportService#sendEmailForListofInvoicesToAgency(java.util.Collection)
     */
    @Override
    public void sendEmailForListofInvoicesToAgency(Collection<ContractsGrantsInvoiceDocument> list) {
        for (ContractsGrantsInvoiceDocument invoiceDocument : list) {
            invoiceDocument.setMarkedForProcessing(ArConstants.INV_RPT_PRCS_IN_PROGRESS);
            SpringContext.getBean(DocumentService.class).updateDocument(invoiceDocument);
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceReportService#generateListOfInvoicesPdfToPrint(java.util.Collection)
     */
    @Override
    public byte[] generateListOfInvoicesPdfToPrint(Collection<ContractsGrantsInvoiceDocument> list) throws DocumentException, IOException {
        Date runDate = new Date();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        generateCombinedPdfForInvoices(list, baos);
        return baos.toByteArray();
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceReportService#generateListOfInvoicesEnvelopesPdfToPrint(java.util.Collection)
     */
    @Override
    public byte[] generateListOfInvoicesEnvelopesPdfToPrint(Collection<ContractsGrantsInvoiceDocument> list) throws DocumentException, IOException {
        Date runDate = new Date();
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
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(ArPropertyConstants.CustomerInvoiceDocumentFields.DOCUMENT_NUMBER, invoice.getDocumentNumber());
            List<InvoiceAgencyAddressDetail> agencyAddresses = (List<InvoiceAgencyAddressDetail>) SpringContext.getBean(BusinessObjectService.class).findMatching(InvoiceAgencyAddressDetail.class, map);
            for (InvoiceAgencyAddressDetail agencyAddress : agencyAddresses) {
                if (ArConstants.InvoiceIndicator.MAIL.equals(agencyAddress.getPreferredInvoiceIndicatorCode())) {
                    ContractsAndGrantsAgencyAddress address;
                    Map<String, Object> primaryKeys = new HashMap<String, Object>();
                    primaryKeys.put(KFSPropertyConstants.AGENCY_NUMBER, agencyAddress.getAgencyNumber());
                    primaryKeys.put("agencyAddressIdentifier", agencyAddress.getAgencyAddressIdentifier());
                    address = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsAgencyAddress.class).getExternalizableBusinessObject(ContractsAndGrantsAgencyAddress.class, primaryKeys);
                    Note note = SpringContext.getBean(NoteService.class).getNoteByNoteId(agencyAddress.getNoteId());
                    for (int i = 0; i < Integer.parseInt(address.getAgencyCopiesToPrint()); i++) {

                        if (ObjectUtils.isNotNull(note)) {
                            if (!pageAdded) {
                                copy.open();
                            }
                            pageAdded = true;
                            copy.addDocument(new PdfReader(note.getAttachment().getAttachmentContents()));
                        }
                    }
                }
            }
            invoice.setDateReportProcessed(new Date());
            SpringContext.getBean(DocumentService.class).updateDocument(invoice);
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
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(ArPropertyConstants.CustomerInvoiceDocumentFields.DOCUMENT_NUMBER, invoice.getDocumentNumber());
            List<InvoiceAgencyAddressDetail> agencyAddresses = (List<InvoiceAgencyAddressDetail>) SpringContext.getBean(BusinessObjectService.class).findMatching(InvoiceAgencyAddressDetail.class, map);
            for (InvoiceAgencyAddressDetail agencyAddress : agencyAddresses) {
                if (ArConstants.InvoiceIndicator.MAIL.equals(agencyAddress.getPreferredInvoiceIndicatorCode())) {
                    ContractsAndGrantsAgencyAddress address;
                    Map<String, Object> primaryKeys = new HashMap<String, Object>();
                    primaryKeys.put(KFSPropertyConstants.AGENCY_NUMBER, agencyAddress.getAgencyNumber());
                    primaryKeys.put("agencyAddressIdentifier", agencyAddress.getAgencyAddressIdentifier());
                    address = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsAgencyAddress.class).getExternalizableBusinessObject(ContractsAndGrantsAgencyAddress.class, primaryKeys);
                    for (int i = 0; i < Integer.parseInt(address.getAgencyPrintEnvelopesNumber()); i++) {
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
                        sendTo.add(new Paragraph(address.getAgencyAddressName(), titleFont));
                        if (StringUtils.isNotEmpty(address.getAgencyLine1StreetAddress())) {
                            sendTo.add(new Paragraph(address.getAgencyLine1StreetAddress(), titleFont));
                        }
                        if (StringUtils.isNotEmpty(address.getAgencyLine2StreetAddress())) {
                            sendTo.add(new Paragraph(address.getAgencyLine2StreetAddress(), titleFont));
                        }
                        String string = "";
                        if (StringUtils.isNotEmpty(address.getAgencyCityName())) {
                            string += address.getAgencyCityName();
                        }
                        if (StringUtils.isNotEmpty(address.getAgencyStateCode())) {
                            string += ", " + address.getAgencyStateCode();
                        }
                        if (StringUtils.isNotEmpty(address.getAgencyZipCode())) {
                            string += "-" + address.getAgencyZipCode();
                        }
                        if (StringUtils.isNotEmpty(string)) {
                            sendTo.add(new Paragraph(string, titleFont));
                        }
                        sendTo.setAlignment(Element.ALIGN_CENTER);
                        sendTo.add(new Paragraph(" "));

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
                        if (StringUtils.isNotEmpty(address.getAgencyCityName())) {
                            string += org.getOrganizationCityName();
                        }
                        if (StringUtils.isNotEmpty(address.getAgencyStateCode())) {
                            string += ", " + org.getOrganizationStateCode();
                        }
                        if (StringUtils.isNotEmpty(address.getAgencyZipCode())) {
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
        else { // in case the document is empty, no envelopes to print
            document.open();
            document.newPage();
            Paragraph sendTo = new Paragraph(" ");
            Paragraph sentBy = new Paragraph(" ");
            document.add(sentBy);
            document.add(sendTo);
            document.close();
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceReportService#generateCSVToExport(org.kuali.kfs.module.ar.document.ContractsGrantsLOCReviewDocument)
     */
    @Override
    public byte[] generateCSVToExport(ContractsGrantsLetterOfCreditReviewDocument LOCDocument) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(baos);
            writer.append("Award,");
            writer.append("Award Document Number,");
            writer.append("Account Description,");
            writer.append("Chart Code,");
            writer.append("Account/Chart of Account Number,");
            writer.append("Account Expiration Date,");
            writer.append("Award Budget Amount,");
            writer.append("Claim On Cash Balance,");
            writer.append("Amount To Draw,");
            writer.append("Funds Not Drawn");
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
            // generate whatever data you want

            writer.flush();
            writer.close();
            return baos.toByteArray();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.report.util;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.report.util.TransactionSummaryReportDataHolder.CashTransfersDataHolder;
import org.kuali.kfs.module.endow.report.util.TransactionSummaryReportDataHolder.ContributionsDataHolder;
import org.kuali.kfs.module.endow.report.util.TransactionSummaryReportDataHolder.ExpensesDataHolder;
import org.kuali.kfs.module.endow.report.util.TransactionSummaryReportDataHolder.SecurityTransfersDataHolder;
import org.kuali.rice.krad.util.ObjectUtils;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class TransactionSummaryReportPrint extends EndowmentReportPrintBase {
    final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TransactionSummaryReportPrint.class);

    /**
     * Generates the report in PDF using iText
     * 
     * @param reportRequestHeaderDataHolder
     * @param transactionStatementDataReportHolders
     * @return pdfStream
     */
    public ByteArrayOutputStream printTransactionSummaryReport(EndowmentReportHeaderDataHolder reportRequestHeaderDataHolder, List<TransactionSummaryReportDataHolder> transactionSummaryDataReportHolders, String listKemidsInHeader, String reportOption, String summaryTotalsOnly) {
        Document document = new Document();
        document.setPageSize(LETTER_PORTRAIT);
        document.addTitle("Endowment Transaction Summary");
        
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();

        try {            
            PdfWriter.getInstance(document, pdfStream);            
            document.open();

            // page
            HeaderFooter header = new HeaderFooter(new Phrase(new Date().toString() + "     Page: ", headerFont), true);
            header.setBorder(Rectangle.NO_BORDER);
            header.setAlignment(Element.ALIGN_RIGHT);
            header.setPageNumber(0);
            document.setHeader(header);
            
            // print the report header
            if (printReportHeaderPage(reportRequestHeaderDataHolder, document, listKemidsInHeader)) {                
                if (transactionSummaryDataReportHolders != null && transactionSummaryDataReportHolders.size() > 0) {
                    if ("Y".equalsIgnoreCase(summaryTotalsOnly)) {
                        printReportBodyBySummaryTotals(transactionSummaryDataReportHolders, document, reportOption);
                    }
                    else {
                        printReportBodyByAllTotals(transactionSummaryDataReportHolders, document, reportOption);
                    }
                } 
            } else {
                LOG.error("Transaction Summary Report Header Error");
            }

            document.close();

        } catch (Exception e) {
            LOG.error("PDF Error: " + e.getMessage());
            return null;
        }
        
        return pdfStream;
    }
    
    /**
     * 
     * Helper method to print body of the transaction summary report, listing only total field.
     * @param transactionSummaryDataReportHolders
     * @param document
     * @param reportOption
     * @return true if successful else return false
     */
    protected boolean printReportBodyBySummaryTotals(List<TransactionSummaryReportDataHolder> transactionSummaryDataReportHolders, Document document, String reportOption) {
        boolean success = true;
        
        if (EndowConstants.EndowmentReport.DETAIL.equalsIgnoreCase(reportOption)) {
            success &= printReportBodyForDetailReportOption(transactionSummaryDataReportHolders, document);
        }
        
        if (EndowConstants.EndowmentReport.TOTAL.equalsIgnoreCase(reportOption)) {
            success &= printReportBodyForSummaryReportOption(transactionSummaryDataReportHolders, document);
        }
        
        if (EndowConstants.EndowmentReport.BOTH.equalsIgnoreCase(reportOption)) {
            success &= printReportBodyForDetailReportOption(transactionSummaryDataReportHolders, document);
            success &= printReportBodyForSummaryReportOption(transactionSummaryDataReportHolders, document);
        }
            
        return success;
    }
    
    /**
     * Helper method to print body of the transaction summary report, listing all total fields.
     * @param transactionSummaryDataReportHolders
     * @param document
     * @param reportOption
     * @return true if successful else false
     */
    protected boolean printReportBodyByAllTotals(List<TransactionSummaryReportDataHolder> transactionSummaryDataReportHolders, Document document, String reportOption) {
        boolean sucess = true;
        
        if (reportOption.equalsIgnoreCase(EndowConstants.EndowmentReport.DETAIL)) {
            sucess &= printReportBodyByAllTotalsForDetailReportOption(transactionSummaryDataReportHolders, document);
        }
        if (reportOption.equalsIgnoreCase(EndowConstants.EndowmentReport.TOTAL)) {
            sucess &= printReportBodyByAllTotalsForTotalReportOption(transactionSummaryDataReportHolders, document);
        }
        if (reportOption.equalsIgnoreCase(EndowConstants.EndowmentReport.BOTH)) {
            sucess &= printReportBodyByAllTotalsForDetailReportOption(transactionSummaryDataReportHolders, document);
            sucess &= printReportBodyByAllTotalsForTotalReportOption(transactionSummaryDataReportHolders, document);
        }
        
        return true;
    }

    /**
     * Generates the Transaction Summary report showing all amounts fields.   
     * 
     * @param transactionSummaryReports
     * @param document
     * @return true if document created else return false
     */
    public boolean printReportBodyByAllTotalsForDetailReportOption(List<TransactionSummaryReportDataHolder> transactionSummaryReportDataHolders, Document document) {
            
        document.setPageCount(0);
        
        // for each kemid
        try {                               
            Font cellFont = regularFont;
            for (TransactionSummaryReportDataHolder transactionSummaryReport : transactionSummaryReportDataHolders) {
                // new page
                document.setPageSize(LETTER_LANDSCAPE);
                document.newPage();
                
                // header
                writeDocumentHeader(document, transactionSummaryReport);
                
                // report table column headers
                PdfPTable table = writeDocumentTitleHeadings(EndowConstants.EndowmentReport.DETAIL);
                
                if (ObjectUtils.isNull(table)) {
                    return false;
                }
                
                // write out Beginning Market value row values
                writeDetailLineRow(table, cellFont, "Beginning Market Value",  transactionSummaryReport.getIncomeBeginningMarketValue(), transactionSummaryReport.getPrincipalBeginningMarketValue(), transactionSummaryReport.getTotalBeginningMarketValue());
                
                // contributions rows
                writeContributionsRecordsForDetailReportOption(table, cellFont, transactionSummaryReport);
                
                // expenses rows....
                writeExpensesRecordsForDetailReportOption(table, cellFont, transactionSummaryReport);
                
                // cash transfer rows...
                writeCashTransfersRecordsForDetailReportOption(table, cellFont, transactionSummaryReport);
                
                // cash transfer rows...
                writeSecurityTransfersRecordsForDetailReportOption(table, cellFont, transactionSummaryReport);

                //write change in market value row....
                writeDetailLineRow(table, cellFont, "Change in Market Value",  transactionSummaryReport.getIncomeChangeInMarketValue(), transactionSummaryReport.getPrincipalChangeInMarketValue(), transactionSummaryReport.getTotalChangeInMarketValue());
                
                //write period end total market value record....
                writeDetailLineRow(table, cellFont, "Period End total Market Value (Include Cash)",  transactionSummaryReport.getIncomeEndingMarketValue(), transactionSummaryReport.getPrincipalEndingMarketValue(), transactionSummaryReport.getTotalEndingMarketValue());
                
                // write out estimate income row
                writeDetailsLineWithTotalAmountOnly(table, cellFont, "Next 12 Months Estimated Income", transactionSummaryReport.getNext12MonthsEstimatedIncome(), EndowConstants.EndowmentReport.DETAIL);
                
                //write out the remainder FY estimated row...
                writeDetailsLineWithTotalAmountOnly(table, cellFont, "Remainder of Fiscal Year Estimated Income", transactionSummaryReport.getRemainderOfFYEstimatedIncome(), EndowConstants.EndowmentReport.DETAIL);
                
                //write out the next FY estimated row...
                writeDetailsLineWithTotalAmountOnly(table, cellFont, "Next Fiscal Year Estimated Income", transactionSummaryReport.getNextFYEstimatedIncome(), EndowConstants.EndowmentReport.DETAIL);
                
                document.add(table);
                
                //print the footer...
                if (ObjectUtils.isNotNull(transactionSummaryReport.getFooter())) {
                    printFooter(transactionSummaryReport.getFooter(), document);
                }
            }
            
        } catch (Exception e) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Generates the Transaction Summary report showing only summary amount field.   
     * This report will only show the total amount field for each kemid
     * @param transactionSummaryReports
     * @param document
     * @return true if document created else return false
     */
    public boolean printReportBodyForDetailReportOption(List<TransactionSummaryReportDataHolder> transactionSummaryReportDataHolders, Document document) {

        document.setPageCount(0);
        
        try {                               
            Font cellFont = regularFont;
            for (TransactionSummaryReportDataHolder transactionSummaryReport : transactionSummaryReportDataHolders) {
                // new page
                document.setPageSize(LETTER_LANDSCAPE);
                document.newPage();
                
                // header
                writeDocumentHeader(document, transactionSummaryReport);
                
                // report table column headers
                PdfPTable table = writeDocumentTitleHeadings(EndowConstants.EndowmentReport.TOTAL);
                
                if (ObjectUtils.isNull(table)) {
                    return false;
                }
                
                // write out Beginning Market value row values
                writeDetailLineRow(table, cellFont, "Beginning Market Value", transactionSummaryReport.getTotalBeginningMarketValue());
                
                // contributions rows
                writeContributionsRecordsForSummaryReportOption(table, cellFont, transactionSummaryReport);
                
                // expenses rows....
                writeExpensesRecordsForSummaryReportOption(table, cellFont, transactionSummaryReport);
                
                //cash transfers rows..
                writeCashTransfersRecordsForSummaryReportOption(table, cellFont, transactionSummaryReport);
                
                // security transfer rows...
                writeSecurityTransfersRecordsForSummaryReportOption(table, cellFont, transactionSummaryReport);
                
                //write change in market value row....
                writeDetailLineRow(table, cellFont, "Change in Market Value",  transactionSummaryReport.getTotalChangeInMarketValue());
                
                //write period end total market value record....
                writeDetailLineRow(table, cellFont, "Period End total Market Value (Include Cash)", transactionSummaryReport.getTotalEndingMarketValue());
                
                // write out estimate income row
                writeDetailsLineWithTotalAmountOnly(table, cellFont, "Next 12 Months Estimated Income", transactionSummaryReport.getNext12MonthsEstimatedIncome(), EndowConstants.EndowmentReport.TOTAL);
                
                //write out the remainder FY estimated row...
                writeDetailsLineWithTotalAmountOnly(table, cellFont, "Remainder of Fiscal Year Estimated Income", transactionSummaryReport.getRemainderOfFYEstimatedIncome(), EndowConstants.EndowmentReport.TOTAL);
                
                //write out the next FY estimated row...
                writeDetailsLineWithTotalAmountOnly(table, cellFont, "Next Fiscal Year Estimated Income", transactionSummaryReport.getNextFYEstimatedIncome(), EndowConstants.EndowmentReport.TOTAL);
                
                document.add(table);
                
                //print the footer...
                if (ObjectUtils.isNotNull(transactionSummaryReport.getFooter())) {
                    printFooter(transactionSummaryReport.getFooter(), document);
                }
            }
            
        } catch (Exception e) {
            return false;
        }
        
        return true;
    }

    /**
     * Method to combine the kemid totals into one data record and print all fields.
     * @param transactionSummaryDataReportHolders
     * @param document
     * @return true if successful else return false
     */

    protected boolean printReportBodyByAllTotalsForTotalReportOption(List<TransactionSummaryReportDataHolder> transactionSummaryDataReportHolders, Document document) {
        List<TransactionSummaryReportDataHolder> summaryReportDataHolder = combineKemidTotals(transactionSummaryDataReportHolders);
        
        return printReportBodyByAllTotalsForDetailReportOption(summaryReportDataHolder, document);        
    }

    /**
     * Method to combine the kemid totals into one data record and print only summary field.
     * @param transactionSummaryDataReportHolders
     * @param document
     * @return true if successful else return false
     */

    protected boolean printReportBodyForSummaryReportOption(List<TransactionSummaryReportDataHolder> transactionSummaryDataReportHolders, Document document) {
        List<TransactionSummaryReportDataHolder> summaryReportDataHolder = combineKemidTotals(transactionSummaryDataReportHolders);
        
        return printReportBodyForDetailReportOption(summaryReportDataHolder, document);        
    }
    
    protected List<TransactionSummaryReportDataHolder> combineKemidTotals(List<TransactionSummaryReportDataHolder> transactionSummaryDataReportHolders) {
        List<TransactionSummaryReportDataHolder> summaryReportDataHolder = new ArrayList<TransactionSummaryReportDataHolder>();
        
        TransactionSummaryReportDataHolder transactionSummaryReportDataHolder = new TransactionSummaryReportDataHolder();
        
        for (TransactionSummaryReportDataHolder reportDataHolder : transactionSummaryDataReportHolders) {
            transactionSummaryReportDataHolder.setIncomeBeginningMarketValue(transactionSummaryReportDataHolder.getIncomeBeginningMarketValue().add(reportDataHolder.getIncomeBeginningMarketValue()));
            transactionSummaryReportDataHolder.setPrincipalBeginningMarketValue(transactionSummaryReportDataHolder.getPrincipalBeginningMarketValue().add(reportDataHolder.getPrincipalBeginningMarketValue()));
            transactionSummaryReportDataHolder.setIncomeChangeInMarketValue(transactionSummaryReportDataHolder.getIncomeChangeInMarketValue().add(reportDataHolder.getIncomeChangeInMarketValue()));
            transactionSummaryReportDataHolder.setPrincipalChangeInMarketValue(transactionSummaryReportDataHolder.getPrincipalChangeInMarketValue().add(reportDataHolder.getPrincipalChangeInMarketValue()));
            transactionSummaryReportDataHolder.setIncomeEndingMarketValue(transactionSummaryReportDataHolder.getIncomeEndingMarketValue().add(reportDataHolder.getIncomeEndingMarketValue()));
            transactionSummaryReportDataHolder.setPrincipalEndingMarketValue(transactionSummaryReportDataHolder.getPrincipalEndingMarketValue().add(reportDataHolder.getPrincipalEndingMarketValue()));
            transactionSummaryReportDataHolder.setNext12MonthsEstimatedIncome(transactionSummaryReportDataHolder.getNext12MonthsEstimatedIncome().add(reportDataHolder.getNext12MonthsEstimatedIncome()));
            transactionSummaryReportDataHolder.setRemainderOfFYEstimatedIncome(transactionSummaryReportDataHolder.getRemainderOfFYEstimatedIncome().add(reportDataHolder.getRemainderOfFYEstimatedIncome()));
            transactionSummaryReportDataHolder.setNextFYEstimatedIncome(transactionSummaryReportDataHolder.getNextFYEstimatedIncome().add(reportDataHolder.getNextFYEstimatedIncome()));
            transactionSummaryReportDataHolder.setInstitution(reportDataHolder.getInstitution());
            transactionSummaryReportDataHolder.setBeginningDate(reportDataHolder.getBeginningDate());
            transactionSummaryReportDataHolder.setEndingDate(reportDataHolder.getEndingDate());
        }
        
        transactionSummaryReportDataHolder.setKemid("All Kemids");
        transactionSummaryReportDataHolder.setKemidLongTitle("");
        
        getSummaryTotalsForContributions(transactionSummaryDataReportHolders, transactionSummaryReportDataHolder);
        getSummaryTotalsForExpenses(transactionSummaryDataReportHolders, transactionSummaryReportDataHolder);
        getSummaryTotalsForCashTransfers(transactionSummaryDataReportHolders, transactionSummaryReportDataHolder);
        getSummaryTotalsForSecurityTransfers(transactionSummaryDataReportHolders, transactionSummaryReportDataHolder);
        
        summaryReportDataHolder.add(transactionSummaryReportDataHolder);
        
        return summaryReportDataHolder;
    }
    
    /**
     * Method to summarize the expenses list records for the summary totals report.
     * @param transactionSummaryDataReportHolders
     * @param transactionSummaryReportDataHolder
     */
    protected void getSummaryTotalsForContributions(List<TransactionSummaryReportDataHolder> transactionSummaryDataReportHolders, TransactionSummaryReportDataHolder transactionSummaryReportDataHolder) {

        ExpensesDataHolder expensesDataHolder = transactionSummaryReportDataHolder.new ExpensesDataHolder();
        
        ExpensesDataHolder summaryExpensesData = transactionSummaryReportDataHolder.new ExpensesDataHolder();
        summaryExpensesData.setExpensesDescription("Summary Totals for Expenses");

        for (TransactionSummaryReportDataHolder reportDataHolder : transactionSummaryDataReportHolders) {
            List<ExpensesDataHolder> expensesDataHolders = reportDataHolder.getReportGroupsForExpenses();
            for (ExpensesDataHolder expenseData : expensesDataHolders) {
                summaryExpensesData.setIncomeExpenses(summaryExpensesData.getIncomeExpenses().add(expenseData.getIncomeExpenses()));
                summaryExpensesData.setPrincipalExpenses(summaryExpensesData.getPrincipalExpenses().add(expenseData.getPrincipalExpenses()));
            }
        }
        
        transactionSummaryReportDataHolder.getReportGroupsForExpenses().add(summaryExpensesData);
    }

    /**
     * Method to summarize the expenses list records for the summary totals report.
     * @param transactionSummaryDataReportHolders
     * @param transactionSummaryReportDataHolder
     */
    protected void getSummaryTotalsForExpenses(List<TransactionSummaryReportDataHolder> transactionSummaryDataReportHolders, TransactionSummaryReportDataHolder transactionSummaryReportDataHolder) {
        ContributionsDataHolder summaryContributionsData = transactionSummaryReportDataHolder.new ContributionsDataHolder();
        summaryContributionsData.setContributionsDescription("Summary Totals for Contibutions and Other Income");

        for (TransactionSummaryReportDataHolder reportDataHolder : transactionSummaryDataReportHolders) {
            List<ContributionsDataHolder> contributionDataHolders = reportDataHolder.getReportGroupsForContributions();
            for (ContributionsDataHolder contributionData : contributionDataHolders) {
                summaryContributionsData.setIncomeContributions(summaryContributionsData.getIncomeContributions().add(contributionData.getIncomeContributions()));
                summaryContributionsData.setPrincipalContributions(summaryContributionsData.getPrincipalContributions().add(contributionData.getPrincipalContributions()));
            }
        }
        
        transactionSummaryReportDataHolder.getReportGroupsForContributions().add(summaryContributionsData);
    }

    /**
     * Method to summarize the cash transfers list records for the summary totals report.
     * @param transactionSummaryDataReportHolders
     * @param transactionSummaryReportDataHolder
     */
    protected void getSummaryTotalsForCashTransfers(List<TransactionSummaryReportDataHolder> transactionSummaryDataReportHolders, TransactionSummaryReportDataHolder transactionSummaryReportDataHolder) {
        CashTransfersDataHolder summaryCashTransferData = transactionSummaryReportDataHolder.new CashTransfersDataHolder();
        summaryCashTransferData.setCashTransfersDescription("Summary Totals for Cash Transactions");

        for (TransactionSummaryReportDataHolder reportDataHolder : transactionSummaryDataReportHolders) {
            List<CashTransfersDataHolder> cashTransfersDataHolders = reportDataHolder.getReportGroupsForCashTransfers();
            for (CashTransfersDataHolder cashTransferData : cashTransfersDataHolders) {
                summaryCashTransferData.setIncomeCashTransfers(summaryCashTransferData.getIncomeCashTransfers().add(cashTransferData.getIncomeCashTransfers()));
                summaryCashTransferData.setPrincipalCashTransfers(summaryCashTransferData.getPrincipalCashTransfers().add(cashTransferData.getPrincipalCashTransfers()));
            }
        }
        
        transactionSummaryReportDataHolder.getReportGroupsForCashTransfers().add(summaryCashTransferData);
    }
    
    /**
     * Method to summarize the Security transfers list records for the summary totals report.
     * @param transactionSummaryDataReportHolders
     * @param transactionSummaryReportDataHolder
     */
    protected void getSummaryTotalsForSecurityTransfers(List<TransactionSummaryReportDataHolder> transactionSummaryDataReportHolders, TransactionSummaryReportDataHolder transactionSummaryReportDataHolder) {
        SecurityTransfersDataHolder summarySecurityTransferData = transactionSummaryReportDataHolder.new SecurityTransfersDataHolder();
        summarySecurityTransferData.setSecurityTransfersDescription("Summary Totals for Security Transactions");

        for (TransactionSummaryReportDataHolder reportDataHolder : transactionSummaryDataReportHolders) {
            List<SecurityTransfersDataHolder> securityTransfersDataHolders = reportDataHolder.getReportGroupsForSecurityTransfers();
            for (SecurityTransfersDataHolder securityTransferData : securityTransfersDataHolders) {
                summarySecurityTransferData.setIncomeSecurityTransfers(summarySecurityTransferData.getIncomeSecurityTransfers().add(securityTransferData.getIncomeSecurityTransfers()));
                summarySecurityTransferData.setPrincipalSecurityTransfers(summarySecurityTransferData.getPrincipalSecurityTransfers().add(securityTransferData.getPrincipalSecurityTransfers()));
            }
        }
        
        transactionSummaryReportDataHolder.getReportGroupsForSecurityTransfers().add(summarySecurityTransferData);
    }

    /**
     * Helper method to write the details line.
     * 
     * @param table
     * @param cellFont
     * @param description
     * @param incomeAmount
     * @param principalAmount
     * @param totalAmount
     */
    protected void writeDetailLineRow(PdfPTable table, Font cellFont, String description,  BigDecimal incomeAmount, BigDecimal principalAmount, BigDecimal totalAmount) {
        table.addCell("\t\t".concat(description));  
        table.addCell(createCell(formatAmount(incomeAmount), cellFont, Element.ALIGN_RIGHT, true));
        table.addCell(createCell(formatAmount(principalAmount), cellFont, Element.ALIGN_RIGHT, true));
        table.addCell(createCell(formatAmount(totalAmount), cellFont, Element.ALIGN_RIGHT, true));
    }

    /**
     * Helper method to write the details line.
     * 
     * @param table
     * @param cellFont
     * @param description
     * @param incomeAmount
     * @param principalAmount
     * @param totalAmount
     */
    protected void writeDetailLineRow(PdfPTable table, Font cellFont, String description, BigDecimal totalAmount) {
        table.addCell("\t\t".concat(description));  
        table.addCell(createCell(formatAmount(totalAmount), cellFont, Element.ALIGN_RIGHT, true));
    }
    
    /**
     * Helper method to go through the contributions list and write the lines..
     * @param table
     * @param cellFont
     * @param transactionSummaryReport
     */
    protected void writeContributionsRecordsForDetailReportOption(PdfPTable table, Font cellFont, TransactionSummaryReportDataHolder transactionSummaryReport) {
        String amount;
        BigDecimal totalIncomeAmounts = BigDecimal.ZERO;
        BigDecimal totalPrincipalAmounts = BigDecimal.ZERO;
        
        //write Contributions header....
        writeSubHeader(table, "Contibutions and Other Income", EndowConstants.EndowmentReport.DETAIL);
        
        //now write out the records....
        List<ContributionsDataHolder> contributionsData = transactionSummaryReport.getReportGroupsForContributions();
        
        if (contributionsData != null) {
            for (ContributionsDataHolder contribution : contributionsData) {
                table.addCell(createCell("\t\t\t\t\t\t\t".concat(contribution.getContributionsDescription()), cellFont, Element.ALIGN_LEFT, true));
                totalIncomeAmounts = totalIncomeAmounts.add(contribution.getIncomeContributions());
                amount = formatAmount(contribution.getIncomeContributions());
                table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
                totalPrincipalAmounts = totalPrincipalAmounts.add(contribution.getPrincipalContributions());
                amount = formatAmount(contribution.getPrincipalContributions());
                table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
                amount = formatAmount(contribution.getTotalContributions());
                table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));            
            }
        }
        
        //now write out the sub-total line....amount
        table.addCell("\t\t\t\t\t\t\t\tActivity Sub-Total");
        amount = formatAmount(totalIncomeAmounts);
        table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
        amount = formatAmount(totalPrincipalAmounts);
        table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
        amount = formatAmount(totalIncomeAmounts.add(totalPrincipalAmounts));
        table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));            
    }

    /**
     * Helper method to go through the contributions list and write the lines..For summary report
     * @param table
     * @param cellFont
     * @param transactionSummaryReport
     */
    protected void writeContributionsRecordsForSummaryReportOption(PdfPTable table, Font cellFont, TransactionSummaryReportDataHolder transactionSummaryReport) {
        String amount;
        BigDecimal totalIncomeAmounts = BigDecimal.ZERO;
        BigDecimal totalPrincipalAmounts = BigDecimal.ZERO;
        
        //write Contributions header....
        writeSubHeader(table, "Contibutions and Other Income", EndowConstants.EndowmentReport.TOTAL);
        
        //now write out the records....
        List<ContributionsDataHolder> contributionsData = transactionSummaryReport.getReportGroupsForContributions();
        
        if (contributionsData != null) {
            for (ContributionsDataHolder contribution : contributionsData) {
                table.addCell(createCell("\t\t\t\t\t\t\t".concat(contribution.getContributionsDescription()), cellFont, Element.ALIGN_LEFT, true));
                totalIncomeAmounts = totalIncomeAmounts.add(contribution.getIncomeContributions());
                totalPrincipalAmounts = totalPrincipalAmounts.add(contribution.getPrincipalContributions());
                amount = formatAmount(contribution.getTotalContributions());
                table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));            
            }
        }
        
        //now write out the sub-total line....amount
        table.addCell("\t\t\t\t\t\t\t\tActivity Sub-Total");
        amount = formatAmount(totalIncomeAmounts.add(totalPrincipalAmounts));
        table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));            
    }
    
    /**
     * Helper method to go through the expenses list and write the lines..
     * @param table
     * @param cellFont
     * @param transactionSummaryReport
     */
    protected void writeExpensesRecordsForDetailReportOption(PdfPTable table, Font cellFont, TransactionSummaryReportDataHolder transactionSummaryReport) {
        String amount;
        BigDecimal totalIncomeAmounts = BigDecimal.ZERO;
        BigDecimal totalPrincipalAmounts = BigDecimal.ZERO;
        
        //write Contributions header....
        writeSubHeader(table, "Expenses", EndowConstants.EndowmentReport.DETAIL);
        
        //now write out the records....
        List<ExpensesDataHolder> expensesData = transactionSummaryReport.getReportGroupsForExpenses();
        
        if (expensesData != null) {
            for (ExpensesDataHolder expenses : expensesData) {
                table.addCell(createCell("\t\t\t\t\t\t\t".concat(expenses.getExpensesDescription()), cellFont, Element.ALIGN_LEFT, true));
                totalIncomeAmounts = totalIncomeAmounts.add(expenses.getIncomeExpenses());
                amount = formatAmount(expenses.getIncomeExpenses());
                table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
                totalPrincipalAmounts = totalPrincipalAmounts.add(expenses.getPrincipalExpenses());
                amount = formatAmount(expenses.getPrincipalExpenses());
                table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
                amount = formatAmount(expenses.getTotalExpenses());
                table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));            
            }
        }
        
        //now write out the sub-total line....amount
        table.addCell("\t\t\t\t\t\t\t\tActivity Sub-Total");
        amount = formatAmount(totalIncomeAmounts);
        table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
        amount = formatAmount(totalPrincipalAmounts);
        table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
        amount = formatAmount(totalIncomeAmounts.add(totalPrincipalAmounts));
        table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));            
    }

    /**
     * Helper method to go through the expenses list and write the lines..
     * @param table
     * @param cellFont
     * @param transactionSummaryReport
     */
    protected void writeExpensesRecordsForSummaryReportOption(PdfPTable table, Font cellFont, TransactionSummaryReportDataHolder transactionSummaryReport) {
        String amount;
        BigDecimal totalIncomeAmounts = BigDecimal.ZERO;
        BigDecimal totalPrincipalAmounts = BigDecimal.ZERO;
        
        //write Contributions header....
        writeSubHeader(table, "Expenses", EndowConstants.EndowmentReport.TOTAL);
        
        //now write out the records....
        List<ExpensesDataHolder> expensesData = transactionSummaryReport.getReportGroupsForExpenses();
        
        if (expensesData != null) {
            for (ExpensesDataHolder expenses : expensesData) {
                table.addCell(createCell("\t\t\t\t\t\t\t".concat(expenses.getExpensesDescription()), cellFont, Element.ALIGN_LEFT, true));
                totalIncomeAmounts = totalIncomeAmounts.add(expenses.getIncomeExpenses());
                totalPrincipalAmounts = totalPrincipalAmounts.add(expenses.getPrincipalExpenses());
                amount = formatAmount(expenses.getTotalExpenses());
                table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));            
            }
        }
        
        //now write out the sub-total line....amount
        table.addCell("\t\t\t\t\t\t\t\tActivity Sub-Total");
        amount = formatAmount(totalIncomeAmounts.add(totalPrincipalAmounts));
        table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));            
    }

    /**
     * Helper method to go through the cash transfers list and write the lines..
     * @param table
     * @param cellFont
     * @param transactionSummaryReport
     */
    protected void writeCashTransfersRecordsForDetailReportOption(PdfPTable table, Font cellFont, TransactionSummaryReportDataHolder transactionSummaryReport) {
        String amount;
        BigDecimal totalIncomeAmounts = BigDecimal.ZERO;
        BigDecimal totalPrincipalAmounts = BigDecimal.ZERO;
        
        //write Contributions header....
        writeSubHeader(table, "Cash Transfers", EndowConstants.EndowmentReport.DETAIL);
        
        //now write out the records....
        List<CashTransfersDataHolder> cashTransfersData = transactionSummaryReport.getReportGroupsForCashTransfers();
        
        if (cashTransfersData != null) {
            for (CashTransfersDataHolder cashTransfer : cashTransfersData) {
                table.addCell(createCell("\t\t\t\t\t\t\t".concat(cashTransfer.getCashTransfersDescription()), cellFont, Element.ALIGN_LEFT, true));
                totalIncomeAmounts = totalIncomeAmounts.add(cashTransfer.getIncomeCashTransfers());
                amount = formatAmount(cashTransfer.getIncomeCashTransfers());
                table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
                totalPrincipalAmounts = totalPrincipalAmounts.add(cashTransfer.getPrincipalCashTransfers());
                amount = formatAmount(cashTransfer.getPrincipalCashTransfers());
                table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
                amount = formatAmount(cashTransfer.getTotalCashTransfers());
                table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));            
            }
        }
        
        //now write out the sub-total line....amount
        table.addCell("\t\t\t\t\t\t\t\tActivity Sub-Total");
        amount = formatAmount(totalIncomeAmounts);
        table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
        amount = formatAmount(totalPrincipalAmounts);
        table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
        amount = formatAmount(totalIncomeAmounts.add(totalPrincipalAmounts));
        table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));            
    }

    /**
     * Helper method to go through the expenses list and write the lines..
     * @param table
     * @param cellFont
     * @param transactionSummaryReport
     */
    protected void writeCashTransfersRecordsForSummaryReportOption(PdfPTable table, Font cellFont, TransactionSummaryReportDataHolder transactionSummaryReport) {
        String amount;
        BigDecimal totalIncomeAmounts = BigDecimal.ZERO;
        BigDecimal totalPrincipalAmounts = BigDecimal.ZERO;
        
        //write Contributions header....
        writeSubHeader(table, "Cash Transfers", EndowConstants.EndowmentReport.TOTAL);
        
        //now write out the records....
        List<CashTransfersDataHolder> cashTransfersData = transactionSummaryReport.getReportGroupsForCashTransfers();
        
        if (cashTransfersData != null) {
            for (CashTransfersDataHolder cashTransfer : cashTransfersData) {
                table.addCell(createCell("\t\t\t\t\t\t\t".concat(cashTransfer.getCashTransfersDescription()), cellFont, Element.ALIGN_LEFT, true));
                totalIncomeAmounts = totalIncomeAmounts.add(cashTransfer.getIncomeCashTransfers());
                totalPrincipalAmounts = totalPrincipalAmounts.add(cashTransfer.getPrincipalCashTransfers());
                amount = formatAmount(cashTransfer.getTotalCashTransfers());
                table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));            
            }
        }
        
        //now write out the sub-total line....amount
        table.addCell("\t\t\t\t\t\t\t\tActivity Sub-Total");
        amount = formatAmount(totalIncomeAmounts.add(totalPrincipalAmounts));
        table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));            
    }
    
    /**
     * Helper method to go through the security list and write the lines..
     * @param table
     * @param cellFont
     * @param transactionSummaryReport
     */
    protected void writeSecurityTransfersRecordsForSummaryReportOption(PdfPTable table, Font cellFont, TransactionSummaryReportDataHolder transactionSummaryReport) {
        String amount;
        BigDecimal totalIncomeAmounts = BigDecimal.ZERO;
        BigDecimal totalPrincipalAmounts = BigDecimal.ZERO;
        
        //write Contributions header....
        writeSubHeader(table, "Security Transfers", EndowConstants.EndowmentReport.TOTAL);
        
        //now write out the records....
        List<SecurityTransfersDataHolder> securityTransfersData = transactionSummaryReport.getReportGroupsForSecurityTransfers();
        
        if (securityTransfersData != null) {
            for (SecurityTransfersDataHolder securityTransfer : securityTransfersData) {
                table.addCell(createCell("\t\t\t\t\t\t\t".concat(securityTransfer.getSecurityTransfersDescription()), cellFont, Element.ALIGN_LEFT, true));
                totalIncomeAmounts = totalIncomeAmounts.add(securityTransfer.getIncomeSecurityTransfers());
                totalPrincipalAmounts = totalPrincipalAmounts.add(securityTransfer.getPrincipalSecurityTransfers());
                amount = formatAmount(securityTransfer.getTotalSecurityTransfers());
                table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));            
            }
        }
        
        //now write out the sub-total line....amount
        table.addCell("\t\t\t\t\t\t\t\tActivity Sub-Total");
        amount = formatAmount(totalIncomeAmounts.add(totalPrincipalAmounts));
        table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));            
    }
    
    /**
     * Helper method to go through the cash transfers list and write the lines..
     * @param table
     * @param cellFont
     * @param transactionSummaryReport
     */
    protected void writeSecurityTransfersRecordsForDetailReportOption(PdfPTable table, Font cellFont, TransactionSummaryReportDataHolder transactionSummaryReport) {
        String amount;
        BigDecimal totalIncomeAmounts = BigDecimal.ZERO;
        BigDecimal totalPrincipalAmounts = BigDecimal.ZERO;
        
        //write Contributions header....
        writeSubHeader(table, "Security Transfers", EndowConstants.EndowmentReport.DETAIL);
        
        //now write out the records....
        List<SecurityTransfersDataHolder> SecurityTransfersData = transactionSummaryReport.getReportGroupsForSecurityTransfers();
        
        if (SecurityTransfersData != null) {
            for (SecurityTransfersDataHolder securityTransfer : SecurityTransfersData) {
                table.addCell(createCell("\t\t\t\t\t\t\t".concat(securityTransfer.getSecurityTransfersDescription()), cellFont, Element.ALIGN_LEFT, true));
                totalIncomeAmounts = totalIncomeAmounts.add(securityTransfer.getIncomeSecurityTransfers());
                amount = formatAmount(securityTransfer.getIncomeSecurityTransfers());
                table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
                totalPrincipalAmounts = totalPrincipalAmounts.add(securityTransfer.getPrincipalSecurityTransfers());
                amount = formatAmount(securityTransfer.getPrincipalSecurityTransfers());
                table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
                amount = formatAmount(securityTransfer.getTotalSecurityTransfers());
                table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));            
            }
        }
        
        //now write out the sub-total line....amount
        table.addCell("\t\t\t\t\t\t\t\tActivity Sub-Total");
        amount = formatAmount(totalIncomeAmounts);
        table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
        amount = formatAmount(totalPrincipalAmounts);
        table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
        amount = formatAmount(totalIncomeAmounts.add(totalPrincipalAmounts));
        table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));            
    }
    
    /**
     * Helper method to write the document header
     * 
     * @param document
     * @param transactionSummaryReport
     */
    protected void writeDocumentHeader(Document document, TransactionSummaryReportDataHolder transactionSummaryReport) {
        // header
        StringBuffer title = new StringBuffer();
        title.append(transactionSummaryReport.getInstitution()).append("\n");
        title.append("SUMMARY OF ACTIVITY FROM ");
        title.append(transactionSummaryReport.getBeginningDate()).append(" TO ").append(transactionSummaryReport.getEndingDate()).append("\n");
        title.append(transactionSummaryReport.getKemid()).append("     ").append(transactionSummaryReport.getKemidLongTitle()).append("\n\n");
        try {
        Paragraph header = new Paragraph(title.toString());
        header.setAlignment(Element.ALIGN_CENTER);                
        document.add(header);
        } catch (DocumentException de) {
            LOG.info("writeDocumentHeader(): Unable to create the header for the report");  
        }
    }

    /**
     * Helper method to write a line containing the column headings for the report
     * 
     * @return table
     */
    protected PdfPTable writeDocumentTitleHeadings(String reportOption) {
        // report table
        int pdfPTableColumns;
        int[] relativeWidthsForDetails = {140, 25, 25, 25};
        int[] relativeWidthsForSummary = {140, 25};
        
        if (EndowConstants.EndowmentReport.DETAIL.equalsIgnoreCase(reportOption)) {
            pdfPTableColumns = 4;
        }
        else {
            pdfPTableColumns = 2;
        }
        
        try {
            PdfPTable table = new PdfPTable(pdfPTableColumns);
            table.setWidthPercentage(FULL_TABLE_WIDTH);
            table.setWidths((EndowConstants.EndowmentReport.DETAIL.equalsIgnoreCase(reportOption)) ? relativeWidthsForDetails : relativeWidthsForSummary);
            table.getDefaultCell().setPadding(5);
            
            // table titles
            table.addCell(new Phrase("", titleFont));
            
            if(EndowConstants.EndowmentReport.DETAIL.equalsIgnoreCase(reportOption)) {
                table.addCell(createCell("INCOME", titleFont, Element.ALIGN_CENTER, true));
                table.addCell(createCell("PRINCIPAL", titleFont, Element.ALIGN_CENTER, true));
            }
            
            table.addCell(createCell("TOTAL", titleFont, Element.ALIGN_CENTER, true));
            return table;
        }
        catch (DocumentException ex) {
            LOG.info("Unable to write column headers.");
            return null;
        }
    }

    /**
     * helper method to write out a sub-heading into the report.
     */
    protected void writeSubHeader(PdfPTable table, String subHeading, String reportOption) {
        table.addCell("\t\t".concat(subHeading));
        if (reportOption.equalsIgnoreCase(EndowConstants.EndowmentReport.DETAIL)) {
            table.addCell("");
            table.addCell("");
        }
        table.addCell("");
    }

    /**
     * helper method to write the details lines where only the last column exists for amounts..
     * @param description
     * @param amount
     */
    protected void writeDetailsLineWithTotalAmountOnly(PdfPTable table, Font cellFont, String description, BigDecimal amount, String reportOption) {
        table.addCell("\t\t".concat(description));
        if (reportOption.equalsIgnoreCase(EndowConstants.EndowmentReport.DETAIL)) {
            table.addCell("");
            table.addCell("");
        }
        table.addCell(createCell(formatAmount(amount), cellFont, Element.ALIGN_RIGHT, true));            
    }
}


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
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.kuali.kfs.module.endow.report.util.TransactionSummaryReportDataHolder.ContributionsDataHolder;
import org.kuali.kfs.module.endow.report.util.TransactionSummaryReportDataHolder.ExpensesDataHolder;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
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
    public ByteArrayOutputStream printTransactionSummaryReport(EndowmentReportHeaderDataHolder reportRequestHeaderDataHolder, List<TransactionSummaryReportDataHolder> transactionSummaryDataReportHolders, String listKemidsInHeader) {
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
            if (printReportHeaderPage(reportRequestHeaderDataHolder, document, listKemidsInHeader, false)) {                
                if (transactionSummaryDataReportHolders != null && transactionSummaryDataReportHolders.size() > 0) {            
                    printTransactionSummaryReportBody(transactionSummaryDataReportHolders, document);
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
     * Generates the Transaction Summary report    
     * 
     * @param transactionSummaryReports
     * @param document
     * @return true if document created else return false
     */
    public boolean printTransactionSummaryReportBody(List<TransactionSummaryReportDataHolder> transactionSummaryReportDataHolders, Document document) {
            
        document.setPageCount(0);
        
        // for each kemid
        try {                               
            Font cellFont = regularFont;
            for (TransactionSummaryReportDataHolder transactionSummaryReport : transactionSummaryReportDataHolders) {
                // new page
                document.setPageSize(LETTER_LANDSCAPE);
                document.newPage();
                
                // header
                StringBuffer title = new StringBuffer();
                title.append(transactionSummaryReport.getInstitution()).append("\n");
                title.append("SUMMARY OF ACTIVITY FROM ");
                title.append(transactionSummaryReport.getBeginningDate()).append(" TO ").append(transactionSummaryReport.getEndingDate()).append("\n");
                title.append(transactionSummaryReport.getKemid()).append("     ").append(transactionSummaryReport.getKemidLongTitle()).append("\n\n");
                Paragraph header = new Paragraph(title.toString());
                header.setAlignment(Element.ALIGN_CENTER);                
                document.add(header);

                // report table
                PdfPTable table = new PdfPTable(4);
                table.setWidthPercentage(FULL_TABLE_WIDTH);
                int[] relativeWidths = {140, 25, 25, 25};
                table.setWidths(relativeWidths);
                table.getDefaultCell().setPadding(5);
                
                // table titles
                table.addCell(new Phrase("", titleFont));
                table.addCell(createCell("INCOME", titleFont, Element.ALIGN_CENTER, true));
                table.addCell(createCell("PRINCIPAL", titleFont, Element.ALIGN_CENTER, true));
                table.addCell(createCell("TOTAL", titleFont, Element.ALIGN_CENTER, true));
                
                // write out Beginning Market value row values
                writeDetailLineRow(table, cellFont, "Beginning Market Value",  transactionSummaryReport.getIncomeBeginningMarketValue(), transactionSummaryReport.getPrincipalBeginningMarketValue(), transactionSummaryReport.getTotalBeginningMarketValue());
                
                // contributions rows
                writeContributionsRecords(table, cellFont, transactionSummaryReport);
                
                // expenses rows....
                writeExpensesRecords(table, cellFont, transactionSummaryReport);
                
                //write change in market value row....
                writeDetailLineRow(table, cellFont, "Change in Market Value",  transactionSummaryReport.getIncomeChangeInMarketValue(), transactionSummaryReport.getPrincipalChangeInMarketValue(), transactionSummaryReport.getTotalChangeInMarketValue());
                
                //write period end total market value record....
                writeDetailLineRow(table, cellFont, "Period End total Market Value (Include Cash)",  transactionSummaryReport.getIncomeEndingMarketValue(), transactionSummaryReport.getPrincipalEndingMarketValue(), transactionSummaryReport.getTotalEndingMarketValue());
                
                // write out estimate income row
                writeDetailsLineWithTotalAmountOnly(table, cellFont, "Next 12 Months Estimated Income", transactionSummaryReport.getNext12MonthsEstimatedIncome());
                
                //write out the remainder FY estimated row...
                writeDetailsLineWithTotalAmountOnly(table, cellFont, "Remainder of Fiscal Year Estimated Income", transactionSummaryReport.getRemainderOfFYEstimatedIncome());
                
                //write out the next FY estimated row...
                writeDetailsLineWithTotalAmountOnly(table, cellFont, "Next Fiscal Year Estimated Income", transactionSummaryReport.getNextFYEstimatedIncome());
                
                document.add(table);
            }
            
        } catch (Exception e) {
            return false;
        }
        
        return true;
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
     * Helper method to go through the contributions list and write the lines..
     * @param table
     * @param cellFont
     * @param transactionSummaryReport
     */
    protected void writeContributionsRecords(PdfPTable table, Font cellFont, TransactionSummaryReportDataHolder transactionSummaryReport) {
        String amount;
        BigDecimal totalIncomeAmounts = BigDecimal.ZERO;
        BigDecimal totalPrincipalAmounts = BigDecimal.ZERO;
        
        //write Contributions header....
        writeSubHeader(table, "Contibutions and Other Income");
        
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
     * Helper method to go through the expenses list and write the lines..
     * @param table
     * @param cellFont
     * @param transactionSummaryReport
     */
    protected void writeExpensesRecords(PdfPTable table, Font cellFont, TransactionSummaryReportDataHolder transactionSummaryReport) {
        String amount;
        BigDecimal totalIncomeAmounts = BigDecimal.ZERO;
        BigDecimal totalPrincipalAmounts = BigDecimal.ZERO;
        
        //write Contributions header....
        writeSubHeader(table, "Expenses");
        
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
     * helper method to write out a sub-heading into the report.
     */
    protected void writeSubHeader(PdfPTable table, String subHeading) {
        table.addCell("\t\t".concat(subHeading));
        table.addCell("");
        table.addCell("");
        table.addCell("");
    }
    
    /**
     * helper method to write the details lines where only the last column exists for amounts..
     * @param description
     * @param amount
     */
    protected void writeDetailsLineWithTotalAmountOnly(PdfPTable table, Font cellFont, String description, BigDecimal amount) {
        table.addCell("\t\t".concat(description));
        table.addCell("");
        table.addCell("");
        table.addCell(createCell(formatAmount(amount), cellFont, Element.ALIGN_RIGHT, true));            
    }
}


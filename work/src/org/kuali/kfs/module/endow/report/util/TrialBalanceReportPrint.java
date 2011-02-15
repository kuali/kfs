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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DateTimeService;

import com.lowagie.text.Chunk;
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

/**
 * Helper to print the report in PDF
 */
public class TrialBalanceReportPrint {

    private final String ZERO_FOR_REPORT = "0.00";
        
    /**
     * Generates the report in PDF using iText
     * 
     * @param reportRequestHeaderDataHolder
     * @param trialBalanceDataReportHolders
     * @param response
     * @return
     */
    public boolean printTrialBalanceReport(ReportRequestHeaderDataHolder reportRequestHeaderDataHolder, List<TrialBalanceReportDataHolder> trialBalanceDataReportHolders, HttpServletResponse response) {
        
        final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TrialBalanceReportPrint.class);
        
        response.setContentType("application/pdf");
        
        Document document = new Document();
        document.setPageSize(PageSize.LETTER);
        document.addTitle("Endowment Trial Balance");

        try {            
            if (trialBalanceDataReportHolders != null && trialBalanceDataReportHolders.size() > 1) {            
                PdfWriter.getInstance(document, response.getOutputStream());
                document.open();
    
                // page
                HeaderFooter header = new HeaderFooter(new Phrase(new Date().toString() + "     Page: ", EndowmentReportUtils.headerFont), true);
                header.setBorder(Rectangle.NO_BORDER);
                header.setAlignment(Element.ALIGN_RIGHT);
                header.setPageNumber(0);
                document.setHeader(header);
                
                // print the report header
                if (new EndowmentReportUtils().printReportHeaderPage(reportRequestHeaderDataHolder, document)) {
                    
                    // page break
                    Chunk chunk = new Chunk("");
                    chunk.setNewPage();
                    Paragraph newPage= new Paragraph (chunk);
                    document.add(newPage);
                    
                    // print Trial Balance report
                    if (printTrialBalanceReport(trialBalanceDataReportHolders, document)) {
                        LOG.error("Report Body Error");
                    }                    
                } else {
                    LOG.error("Report Header Error");
                }
            } else {
                Paragraph message = new Paragraph("No kemids found for report");
                document.add(message);
            }
            
            document.close();

        } catch (Exception e) {
            LOG.error("PDF Error: " + e.getMessage());
            return false;
        }
        
        return true;
    }
    
    /**
     * Generates the Trial Balance report
     * 
     * @param trialBalanceReports
     * @param document
     * @return
     */
    public boolean printTrialBalanceReport(List<TrialBalanceReportDataHolder> trialBalanceReports, Document document) {
                
        try {            
            // title
            Paragraph title = new Paragraph("KEMID Trial Balance");
            title.setAlignment(Element.ALIGN_CENTER);
            Date currentDate = SpringContext.getBean(KEMService.class).getCurrentDate();
            DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
            String asOfDate = dateTimeService.toDateString(currentDate);            
            title.add("\nAs of " + asOfDate + "\n\n");
            document.add(title);
               
            // report table
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(EndowmentReportUtils.TRIAL_BALANCE_TABLE_WIDTH);
            
            // table titles
            Font titleFont = EndowmentReportUtils.titleFont;
            table.addCell(new Phrase("KEMID", titleFont));
            table.addCell(new Phrase("KEMID\nName", titleFont));
            table.addCell(new Phrase("Income\nCash\nBalance", titleFont));
            table.addCell(new Phrase("Principal\nCash\nBalance", titleFont));
            table.addCell(new Phrase("KEMID Total\nMarket Value", titleFont));
            table.addCell(new Phrase("Available\nExpendable\nFunds", titleFont));
            table.addCell(new Phrase("FY\nRemainder\nEstimated\nIncome", titleFont));
            
            // table body
            Font cellFont = EndowmentReportUtils.regularFont;
            for (TrialBalanceReportDataHolder trialBalanceReport : trialBalanceReports) {
                
                if (trialBalanceReport.getKemid().equalsIgnoreCase("TOTALS")) {
                    cellFont = titleFont;
                }
                
                table.addCell(new Phrase(trialBalanceReport.getKemid(), cellFont));
                table.addCell(new Phrase(trialBalanceReport.getKemidName(), cellFont));
                if (trialBalanceReport.getInocmeCashBalance() != null) { 
                    String amount = formatAmount(trialBalanceReport.getInocmeCashBalance().bigDecimalValue());
                    table.addCell(new Phrase(amount, cellFont));
                } else { 
                    table.addCell(new Phrase(ZERO_FOR_REPORT, cellFont));
                }
                if (trialBalanceReport.getPrincipalcashBalance() != null) {
                    String amount = formatAmount(trialBalanceReport.getPrincipalcashBalance().bigDecimalValue());
                    table.addCell(new Phrase(amount, cellFont));
                } else {
                    table.addCell(new Phrase(ZERO_FOR_REPORT, cellFont));
                }
                if (trialBalanceReport.getKemidTotalMarketValue() != null) {
                    String amount = formatAmount(trialBalanceReport.getKemidTotalMarketValue());
                    table.addCell(new Phrase(amount, cellFont));
                } else {
                    table.addCell(new Phrase(ZERO_FOR_REPORT, cellFont));
                }
                if (trialBalanceReport.getAvailableExpendableFunds() != null) {
                    String amount = formatAmount(trialBalanceReport.getAvailableExpendableFunds());
                    table.addCell(new Phrase(amount, cellFont));
                } else {
                    table.addCell(new Phrase(ZERO_FOR_REPORT, cellFont));
                }
                if (trialBalanceReport.getFyRemainderEstimatedIncome() != null) {
                    String amount = formatAmount(trialBalanceReport.getFyRemainderEstimatedIncome());
                    table.addCell(new Phrase(amount, cellFont));
                } else {
                    table.addCell(new Phrase(ZERO_FOR_REPORT, cellFont));
                }
            }
            
            document.add(table);
        } catch (Exception e) {
            return false;
        }
        
        return true;
    }
       

    
    /** 
     * Format the dollar amount - 19,2 decimal
     * 
     * @param amount
     * @return
     */
    protected String formatAmount(BigDecimal amount) {        
        
        String amountString = "";;
        NumberFormat formatter = new DecimalFormat("#,###,###,###,###,###,##0.00");
        
        if (amount.doubleValue() < 0) {
            amount = amount.negate();
            amountString = "(" + formatter.format(amount.doubleValue()) + ")";
            
        } else {
            amountString = formatter.format(amount.doubleValue());
        }
        return amountString;         
    }
}

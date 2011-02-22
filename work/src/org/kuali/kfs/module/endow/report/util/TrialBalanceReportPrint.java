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
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DateTimeService;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Helper to print the report in PDF
 */
public class TrialBalanceReportPrint extends EndowmentReportPrintBase {

    private final String ZERO_FOR_REPORT = "0.00";
        
    /**
     * Generates the report in PDF using iText
     * 
     * @param reportRequestHeaderDataHolder
     * @param trialBalanceDataReportHolders
     * @param response
     * @return
     */
    public ByteArrayOutputStream printTrialBalanceReport(ReportRequestHeaderDataHolder reportRequestHeaderDataHolder, List<TrialBalanceReportDataHolder> trialBalanceDataReportHolders) {
        
        final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TrialBalanceReportPrint.class);
                
        Document document = new Document();
        //Document myDoc = new Document(PageSize.LETTER.rotate(), 0, 0, 0, 0);
        document.setPageSize(pageSize);
        document.addTitle("Endowment Trial Balance");
        
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        PdfWriter pdfWriter = null;

        try {            
            //PdfWriter.getInstance(document, response.getOutputStream());
            pdfWriter = PdfWriter.getInstance(document, pdfStream);
            document.open();

            // page
            HeaderFooter header = new HeaderFooter(new Phrase(new Date().toString() + "     Page: ", headerFont), true);
            header.setBorder(Rectangle.NO_BORDER);
            header.setAlignment(Element.ALIGN_RIGHT);
            header.setPageNumber(0);
            document.setHeader(header);
                
            // print the report header
            if (printReportHeaderPage(reportRequestHeaderDataHolder, document, true)) {
                    
                setPageBreak(document);
                document.resetPageCount();
                document.setPageCount(1);
                
                if (trialBalanceDataReportHolders != null && trialBalanceDataReportHolders.size() > 1) {        
                    // print Trial Balance report
                    if (printTrialBalanceReportBody(trialBalanceDataReportHolders, document)) {
                        LOG.error("Trial Balance Report Body Error");
                    } else {
                        Paragraph message = new Paragraph("No kemids found for Trial Balance report");
                        document.add(message);                        
                    }
                } else {
                    LOG.error("Trial Balance Report Header Error");
                }
            } 
        
            document.close();

        } catch (Exception e) {
            LOG.error("PDF Error: " + e.getMessage());
            return null;
        }
        
        return pdfStream;
    }
    
    /**
     * Generates the Trial Balance report
     * 
     * @param trialBalanceReports
     * @param document
     * @return
     */
    public boolean printTrialBalanceReportBody(List<TrialBalanceReportDataHolder> trialBalanceReports, Document document) {
                
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
            table.setWidthPercentage(TRIAL_BALANCE_TABLE_WIDTH);
            int[] relativeWidths = {12, 13, 15, 15, 15, 15, 15};
            table.setWidths(relativeWidths);
            table.getDefaultCell().setPadding(2);
            
            // table titles
            table.addCell(new Phrase("KEMID", titleFont));
            table.addCell(new Phrase("KEMID\nName", titleFont));
            table.addCell(new Phrase("Income\nCash\nBalance", titleFont));
            table.addCell(new Phrase("Principal\nCash\nBalance", titleFont));
            table.addCell(new Phrase("KEMID Total\nMarket Value", titleFont));
            table.addCell(new Phrase("Available\nExpendable\nFunds", titleFont));
            table.addCell(new Phrase("FY\nRemainder\nEstimated\nIncome", titleFont));
            
            // table body
            Font cellFont = regularFont;
            for (TrialBalanceReportDataHolder trialBalanceReport : trialBalanceReports) {
                
                // totals, which is the last row
                if (trialBalanceReport.getKemid().equalsIgnoreCase("TOTALS")) {
                    cellFont = titleFont;
                }
                
                table.addCell(new Phrase(trialBalanceReport.getKemid(), cellFont));
                table.addCell(new Phrase(trialBalanceReport.getKemidName(), cellFont));
                if (trialBalanceReport.getInocmeCashBalance() != null) { 
                    String amount = formatAmount(trialBalanceReport.getInocmeCashBalance().bigDecimalValue());
                    table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
                } else { 
                    table.addCell(createCell(ZERO_FOR_REPORT, cellFont, Element.ALIGN_RIGHT, true));
                }
                if (trialBalanceReport.getPrincipalcashBalance() != null) {
                    String amount = formatAmount(trialBalanceReport.getPrincipalcashBalance().bigDecimalValue());
                    table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
                } else {
                    table.addCell(createCell(ZERO_FOR_REPORT, cellFont, Element.ALIGN_RIGHT, true));
                }
                if (trialBalanceReport.getKemidTotalMarketValue() != null) {
                    String amount = formatAmount(trialBalanceReport.getKemidTotalMarketValue());
                    table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
                } else {
                    table.addCell(createCell(ZERO_FOR_REPORT, cellFont, Element.ALIGN_RIGHT, true));
                }
                if (trialBalanceReport.getAvailableExpendableFunds() != null) {
                    String amount = formatAmount(trialBalanceReport.getAvailableExpendableFunds());
                    table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
                } else {
                    table.addCell(createCell(ZERO_FOR_REPORT, cellFont, Element.ALIGN_RIGHT, true));
                }
                if (trialBalanceReport.getFyRemainderEstimatedIncome() != null) {
                    String amount = formatAmount(trialBalanceReport.getFyRemainderEstimatedIncome());
                    table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
                } else {
                    table.addCell(createCell(ZERO_FOR_REPORT, cellFont, Element.ALIGN_RIGHT, true));
                }
            }
            
            document.add(table);
        } catch (Exception e) {
            return false;
        }
        
        return true;
    }   

}

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

public class TransactionStatementReportPrint extends EndowmentReportPrintBase {

    private final String ZERO_FOR_REPORT = "0.00";
    
    /**
     * Generates the report in PDF using iText
     * 
     * @param reportRequestHeaderDataHolder
     * @param transactionStatementDataReportHolders
     * @return
     */
    public ByteArrayOutputStream printTransactionStatementReport(ReportRequestHeaderDataHolder reportRequestHeaderDataHolder, List<TransactionStatementReportDataHolder> transactionStatementDataReportHolders) {
        
        final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TransactionStatementReportPrint.class);
        
        Document document = new Document();
        //Document document = new Document(PageSize.LETTER.rotate(), 0, 0, 0, 0);
        document.setPageSize(pageSize);
        document.addTitle("Endowment Transaction Statement");
        
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
            if (printReportHeaderPage(reportRequestHeaderDataHolder, document, false)) {                
                if (transactionStatementDataReportHolders != null && transactionStatementDataReportHolders.size() > 0) {            
                    printTransactionStatementReportBody(transactionStatementDataReportHolders, document);
                } 
            } else {
                LOG.error("Transaction Statement Report Header Error");
            }

            document.close();

        } catch (Exception e) {
            LOG.error("PDF Error: " + e.getMessage());
            return null;
        }
        
        return pdfStream;
    }
    
    /**
     * Generates the Transaction Statement report    
     * 
     * @param transactionStatementReports
     * @param document
     * @return
     */
    public boolean printTransactionStatementReportBody(List<TransactionStatementReportDataHolder> transactionStatementReportDataHolders, Document document) {
            
        document.setPageCount(0);
        
        // for each kemid
        try {                               
            Font cellFont = regularFont;
            for (TransactionStatementReportDataHolder transactionStatementReport : transactionStatementReportDataHolders) {
                
                // new page
                document.newPage();
                
                // header
                StringBuffer title = new StringBuffer();
                title.append(transactionStatementReport.getInstitution()).append("\n");
                title.append("STATEMENT OF TRANSACTIONS FROM").append("\n");
                title.append(transactionStatementReport.getBeginningDate()).append(" TO ").append(transactionStatementReport.getEndingDate()).append("\n");
                title.append(transactionStatementReport.getKemid()).append("     ").append(transactionStatementReport.getKemidLongTitle()).append("\n\n\n");
                Paragraph header = new Paragraph(title.toString());
                header.setAlignment(Element.ALIGN_CENTER);                
                document.add(header);

                // report table
                PdfPTable table = new PdfPTable(4);
                table.setWidthPercentage(TRIAL_BALANCE_TABLE_WIDTH);
                int[] relativeWidths = {10, 40, 25, 25};
                table.setWidths(relativeWidths);
                table.getDefaultCell().setPadding(5);
                
                // table titles
                table.addCell(new Phrase("DATE", titleFont));
                table.addCell(new Phrase("DESCRIPTION", titleFont));
                table.addCell(createCell("INCOME\nAMOUNT", titleFont, Element.ALIGN_RIGHT, true));
                table.addCell(createCell("PRINCIPAL\nAMOUNT", titleFont, Element.ALIGN_RIGHT, true));
                
                // first row
                table.addCell(new Phrase(transactionStatementReport.getBeginningDate(), cellFont));
                table.addCell(new Phrase("Beginning Cash Balance", cellFont));                
                if (transactionStatementReport.getHistoryIncomeCash1() != null) {
                    String amount = formatAmount(transactionStatementReport.getHistoryIncomeCash1());
                    table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
                } else {
                    table.addCell(createCell(ZERO_FOR_REPORT, cellFont, Element.ALIGN_RIGHT, true));
                }
                if (transactionStatementReport.getHistoryPrincipalCash1() != null) {
                    String amount = formatAmount(transactionStatementReport.getHistoryPrincipalCash1());
                    table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
                } else {
                    table.addCell(createCell(ZERO_FOR_REPORT, cellFont, Element.ALIGN_RIGHT, true));
                }

                // second row
                table.addCell(new Phrase(transactionStatementReport.getPostedDate(), cellFont));
                table.addCell(new Phrase(transactionStatementReport.getDescription2(), cellFont));                
                if (transactionStatementReport.getIncomeAmount() != null) {
                    String amount = formatAmount(transactionStatementReport.getIncomeAmount());
                    table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
                } else {
                    table.addCell(createCell(ZERO_FOR_REPORT, cellFont, Element.ALIGN_RIGHT, true));
                }
                if (transactionStatementReport.getPrincipalAmount() != null) {
                    String amount = formatAmount(transactionStatementReport.getPrincipalAmount());
                    table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
                } else {
                    table.addCell(createCell(ZERO_FOR_REPORT, cellFont, Element.ALIGN_RIGHT, true));
                }

                // third row
                table.addCell(new Phrase(transactionStatementReport.getPostedDate(), cellFont));
                table.addCell(new Phrase(transactionStatementReport.getDescription3(), cellFont));                
                if (transactionStatementReport.getIncomeAmount() != null) {
                    String amount = formatAmount(transactionStatementReport.getIncomeAmount());
                    table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
                } else {
                    table.addCell(createCell(ZERO_FOR_REPORT, cellFont, Element.ALIGN_RIGHT, true));
                }
                if (transactionStatementReport.getPrincipalAmount() != null) {
                    String amount = formatAmount(transactionStatementReport.getPrincipalAmount());
                    table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
                } else {
                    table.addCell(createCell(ZERO_FOR_REPORT, cellFont, Element.ALIGN_RIGHT, true));
                }
                
                // fourth row
                table.addCell(new Phrase(transactionStatementReport.getPostedDate(), cellFont));
                table.addCell(new Phrase(transactionStatementReport.getDescription4(), cellFont));                
                if (transactionStatementReport.getIncomeAmount() != null) {
                    String amount = formatAmount(transactionStatementReport.getIncomeAmount());
                    table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
                } else {
                    table.addCell(createCell(ZERO_FOR_REPORT, cellFont, Element.ALIGN_RIGHT, true));
                }
                if (transactionStatementReport.getPrincipalAmount() != null) {
                    String amount = formatAmount(transactionStatementReport.getPrincipalAmount());
                    table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
                } else {
                    table.addCell(createCell(ZERO_FOR_REPORT, cellFont, Element.ALIGN_RIGHT, true));
                }
                
                // last row
                table.addCell(new Phrase(transactionStatementReport.getEndingDate(), cellFont));
                table.addCell(new Phrase("Ending Cash Balance", cellFont));                
                if (transactionStatementReport.getHistoryIncomeCash1() != null) {
                    String amount = formatAmount(transactionStatementReport.getHistoryIncomeCash2());
                    table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
                } else {
                    table.addCell(createCell(ZERO_FOR_REPORT, cellFont, Element.ALIGN_RIGHT, true));
                }
                if (transactionStatementReport.getHistoryPrincipalCash1() != null) {
                    String amount = formatAmount(transactionStatementReport.getHistoryPrincipalCash2());
                    table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
                } else {
                    table.addCell(createCell(ZERO_FOR_REPORT, cellFont, Element.ALIGN_RIGHT, true));
                }
                
                document.add(table);
            }
            
            
        } catch (Exception e) {
            return false;
        }
        
        return true;
    }

}


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
import java.util.List;

import org.kuali.kfs.module.endow.report.util.TransactionStatementReportDataHolder.TransactionArchiveInfo;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
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
    public ByteArrayOutputStream printTransactionStatementReport(EndowmentReportHeaderDataHolder reportRequestHeaderDataHolder, List<TransactionStatementReportDataHolder> transactionStatementDataReportHolders, String listKemidsInHeader) {
        
        final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TransactionStatementReportPrint.class);
        
        Document document = new Document();
        document.setPageSize(LETTER_PORTRAIT);
        document.addTitle("Endowment Transaction Statement");
        
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();

        try {            
            PdfWriter.getInstance(document, pdfStream);            
            document.open();

            // print the report header
            if (printReportHeaderPage(reportRequestHeaderDataHolder, document, listKemidsInHeader)) {
                // and then the body
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
                title.append(transactionStatementReport.getBeginningDate()).append(" to ").append(transactionStatementReport.getEndingDate()).append("\n");
                title.append(transactionStatementReport.getKemid()).append("     ").append(transactionStatementReport.getKemidLongTitle()).append("\n\n");
                Paragraph header = new Paragraph(title.toString());
                header.setAlignment(Element.ALIGN_CENTER);                
                document.add(header);

                // report table
                PdfPTable table = new PdfPTable(4);
                table.setWidthPercentage(FULL_TABLE_WIDTH);
                int[] relativeWidths = {10, 40, 25, 25};
                table.setWidths(relativeWidths);
                table.getDefaultCell().setPadding(5);
                
                // table titles
                table.addCell(new Phrase("DATE", titleFont));
                table.addCell(new Phrase("DESCRIPTION", titleFont));
                table.addCell(createCell("INCOME AMOUNT", titleFont, Element.ALIGN_RIGHT, Element.ALIGN_MIDDLE, true));
                table.addCell(createCell("PRINCIPAL AMOUNT", titleFont, Element.ALIGN_RIGHT, Element.ALIGN_MIDDLE, true));
                
                // beginning cash balance
                table.addCell(new Phrase(transactionStatementReport.getBeginningDate(), cellFont));
                table.addCell(new Phrase("Beginning Cash Balance", cellFont));                
                String amount = "";
                amount = formatAmount(transactionStatementReport.getBeginningIncomeCash());
                table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));                
                amount = formatAmount(transactionStatementReport.getBeginningPrincipalCash());
                table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
                
                // transactions
                List<TransactionArchiveInfo> TransactionArchiveInfoList = transactionStatementReport.getTransactionArchiveInfoList(); 
                for (TransactionArchiveInfo transactionArchiveInfo : TransactionArchiveInfoList) {
                    table.addCell(new Phrase(transactionArchiveInfo.getPostedDate(), cellFont));
                    table.addCell(new Phrase(getDescription(transactionArchiveInfo), cellFont));                
                    amount = formatAmount(transactionArchiveInfo.getTransactionIncomeCash());
                    table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
                    amount = formatAmount(transactionArchiveInfo.getTransactionPrincipalCash());
                    table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
                }
                
                // ending cash balance
                table.addCell(new Phrase(transactionStatementReport.getEndingDate(), cellFont));
                table.addCell(new Phrase("Ending Cash Balance", cellFont));                
                amount = formatAmount(transactionStatementReport.getEndingIncomeCash());
                table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
                amount = formatAmount(transactionStatementReport.getEndingPrincipalCash());
                table.addCell(createCell(amount, cellFont, Element.ALIGN_RIGHT, true));
                
                document.add(table);

                // footer
                printFooter(transactionStatementReport.getFooter(), document);
            }
            
        } catch (Exception e) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Gets the transaction info
     * 
     * @param transactionArchiveInfo
     * @return
     */
    protected String getDescription(TransactionArchiveInfo transactionArchiveInfo) {
        
        StringBuffer description = new StringBuffer();
        
        description.append(transactionArchiveInfo.getDocumentName()).append("\n");
        if (transactionArchiveInfo.getEtranCode() != null) {
            description.append(transactionArchiveInfo.getEtranCode())
                       .append(" - ")
                       .append(transactionArchiveInfo.getEtranCodeDesc()).append("\n");
        }

        description.append(transactionArchiveInfo.getTransactionDesc()).append("\n");
        
        if (transactionArchiveInfo.getTransactionSecurity() != null && !transactionArchiveInfo.getTransactionSecurity().isEmpty()) {
            description.append(transactionArchiveInfo.getTransactionSecurity()).append("\n");
        }
        if (transactionArchiveInfo.getTransactionSecurityUnits() != null && transactionArchiveInfo.getTransactionSecurityUnits() != BigDecimal.ZERO) {
            description.append(formatAmount(transactionArchiveInfo.getTransactionSecurityUnits(), FORMAT164))
                       .append(" at ")
                       .append(formatAmount(transactionArchiveInfo.getTransactionSecurityUnitValue(), FORMAT195))
                       .append(" per unit");
        }
     
        return description.toString();
    }

}


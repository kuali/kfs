/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source$
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
package org.kuali.module.gl.util;

import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.kuali.Constants;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.gl.bo.OriginEntry;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class GeneralLedgerPendingEntryReport {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GeneralLedgerPendingEntryReport.class);

    private PdfPTable dataTable;
    private Font headerFont;
    private Font textFont;
    private NumberFormat amountFormat;
    private NumberFormat countFormat;

    public void generateReport(Date runDate,String batchReportsDirectory,SimpleDateFormat sdf,Iterator entries) {
        LOG.debug("generateReport() started");

        String title = "PENDING LEDGER ENTRY TABLE";
        String filePrefix = "glpe_list";

        headerFont = FontFactory.getFont(FontFactory.COURIER, 8, Font.BOLD);
        textFont = FontFactory.getFont(FontFactory.COURIER, 8, Font.NORMAL);

        amountFormat = NumberFormat.getInstance();
        amountFormat.setGroupingUsed(true);
        amountFormat.setMaximumFractionDigits(2);
        amountFormat.setMinimumFractionDigits(2);
        countFormat = NumberFormat.getInstance();
        countFormat.setGroupingUsed(true);
        countFormat.setMaximumFractionDigits(0);
        countFormat.setMinimumFractionDigits(0);

        Document document = new Document(PageSize.A4.rotate());
        
        TransactionReport.PageHelper helper = new TransactionReport.PageHelper();
     
        helper.runDate = runDate;
        helper.headerFont = headerFont;
        helper.title = title;

        try {
            String filename = batchReportsDirectory + "/" + filePrefix + "_";

            filename = filename + sdf.format(runDate);
            filename = filename + ".pdf";
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            writer.setPageEvent(helper);

            document.open();

            float[] columnWidths = new float[] {6, 15, 5, 5, 15, 6, 16, 16, 16};

            dataTable = new PdfPTable(columnWidths);
            dataTable.setHeaderRows(1);
            dataTable.setWidthPercentage(100);

            String[] columnHeaders = new String[] {
                    "Doc Type", "Document Number", "Bal Type", "COA Code",
                    "Account Number", "Object Code", "Credit", "Debit", "Blank" };

            for(int x = 0; x < columnHeaders.length; x++) {
                PdfPCell cell = new PdfPCell(new Phrase(columnHeaders[x], headerFont));
                dataTable.addCell(cell);
            }
            
            String previousDocumentType = "-1";
            String previousDocumentNumber = "-1";

            KualiDecimal totalCredit = KualiDecimal.ZERO;
            KualiDecimal totalDebit = KualiDecimal.ZERO;
            KualiDecimal totalBlank = KualiDecimal.ZERO;
            int totalCount = 0;

            KualiDecimal totalDocumentTypeCredit = KualiDecimal.ZERO;
            KualiDecimal totalDocumentTypeDebit = KualiDecimal.ZERO;
            KualiDecimal totalDocumentTypeBlank = KualiDecimal.ZERO;
            int totalDocumentTypeCount = 0;

            KualiDecimal totalDocumentCredit = KualiDecimal.ZERO;
            KualiDecimal totalDocumentDebit = KualiDecimal.ZERO;
            KualiDecimal totalDocumentBlank = KualiDecimal.ZERO;

            OriginEntry lastEntry = null;

            boolean firstAccount = true;

            while ( entries.hasNext() ) {
                OriginEntry entry = (OriginEntry) entries.next();

                String docNumber = entry.getFinancialSystemOriginationCode() + "-" + entry.getDocumentNumber();

                if ( ! docNumber.equals(previousDocumentNumber) && ! "-1".equals(previousDocumentNumber) ) {
                    printTotal("Totals:",totalDocumentCredit,totalDocumentDebit,totalDocumentBlank);
                    totalDocumentCredit = KualiDecimal.ZERO;
                    totalDocumentDebit = KualiDecimal.ZERO;
                    totalDocumentBlank = KualiDecimal.ZERO;
                    firstAccount = true;
                }

                // Show doc type totals.
                if ( !entry.getFinancialDocumentTypeCode().equals(previousDocumentType) && !"-1".equals(previousDocumentType) ) {
                    printTotal("Totals for Document Type " + previousDocumentType + " Cnt: " + countFormat.format(totalDocumentTypeCount),totalDocumentTypeCredit,totalDocumentTypeDebit,totalDocumentTypeBlank);
                    totalDocumentTypeCredit = KualiDecimal.ZERO;
                    totalDocumentTypeDebit = KualiDecimal.ZERO;
                    totalDocumentTypeBlank = KualiDecimal.ZERO;
                    totalDocumentTypeCount = 0;
                }

                previousDocumentNumber = docNumber;
                previousDocumentType = entry.getFinancialDocumentTypeCode();

                totalDocumentTypeCount++;
                totalCount++;

                if ( firstAccount ) {
                    firstAccount = false;

                    PdfPCell column = new PdfPCell(new Phrase(entry.getFinancialDocumentTypeCode(), textFont));
                    column.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                    dataTable.addCell(column);

                    column = new PdfPCell(new Phrase(docNumber, textFont));
                    column.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                    dataTable.addCell(column);

                    column = new PdfPCell(new Phrase(entry.getFinancialBalanceTypeCode(), textFont));
                    column.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                    dataTable.addCell(column);
                } else {
                    PdfPCell column = new PdfPCell(new Phrase(" ", textFont));
                    column.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                    dataTable.addCell(column);

                    column = new PdfPCell(new Phrase(" ", textFont));
                    column.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                    dataTable.addCell(column);

                    column = new PdfPCell(new Phrase(" ", textFont));
                    column.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                    dataTable.addCell(column);
                }

                PdfPCell column = new PdfPCell(new Phrase(entry.getChartOfAccountsCode(), textFont));
                column.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                dataTable.addCell(column);

                column = new PdfPCell(new Phrase(entry.getAccountNumber(), textFont));
                column.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                dataTable.addCell(column);

                column = new PdfPCell(new Phrase(entry.getFinancialObjectCode(), textFont));
                column.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                dataTable.addCell(column);

                KualiDecimal amount = null;
                if(Constants.GL_DEBIT_CODE.equals(entry.getTransactionDebitCreditCode())) {
                    amount = entry.getTransactionLedgerEntryAmount();
                    totalDocumentDebit = totalDocumentDebit.add(amount);
                    totalDocumentTypeDebit = totalDocumentTypeDebit.add(amount);
                    totalDebit = totalDebit.add(amount);
                }
                column = new PdfPCell(new Phrase(null == amount ? " " : amountFormat.format(amount.doubleValue()), textFont));
                column.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                dataTable.addCell(column);

                amount = null;
                if ( Constants.GL_CREDIT_CODE.equals(entry.getTransactionDebitCreditCode()) ) {
                    amount = entry.getTransactionLedgerEntryAmount();
                    totalDocumentCredit = totalDocumentCredit.add(amount);
                    totalDocumentTypeCredit = totalDocumentTypeCredit.add(amount);
                    totalCredit = totalCredit.add(amount);
                }
                column = new PdfPCell(new Phrase(null == amount ? " " : amountFormat.format(amount.doubleValue()), textFont));
                column.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                dataTable.addCell(column);

                amount = null;
                if( Constants.GL_BUDGET_CODE.equals(entry.getTransactionDebitCreditCode()) ) {
                    amount = entry.getTransactionLedgerEntryAmount();
                    totalDocumentBlank = totalDocumentBlank.add(amount);
                    totalDocumentTypeBlank = totalDocumentTypeBlank.add(amount);
                    totalBlank = totalBlank.add(amount);
                }
                column = new PdfPCell(new Phrase(null == amount ? " " : amountFormat.format(amount.doubleValue()), textFont));
                column.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                dataTable.addCell(column);

                lastEntry = entry;
            }

            if ( totalCount > 0 ) {
                printTotal("Totals:",totalDocumentCredit,totalDocumentDebit,totalDocumentBlank);
            }

            if ( totalCount > 0 ) {
                printTotal("Totals for Document Type " + lastEntry.getFinancialDocumentTypeCode() + " Cnt: " + countFormat.format(totalDocumentTypeCount),totalDocumentTypeCredit,totalDocumentTypeDebit,totalDocumentTypeBlank);
            }

            printTotal("Grand Totals Cnt: " + countFormat.format(totalCount),totalCredit,totalDebit,totalBlank);

            document.add(dataTable);
        }
        catch (Exception de) {
            LOG.error("generatePendingEntryReport() Error creating PDF report", de);
            throw new RuntimeException("Report Generation Failed");
        }
        document.close();
    }

    private void printTotal(String title,KualiDecimal credit,KualiDecimal debit,KualiDecimal blank) {
        PdfPCell column = new PdfPCell(new Phrase(title, headerFont));
        column.setColspan(6);
        column.setPaddingTop(10.0F);
        column.setPaddingBottom(10.0F);
        column.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        dataTable.addCell(column);

        column = new PdfPCell(new Phrase(amountFormat.format(debit.doubleValue()), headerFont));
        column.setPaddingTop(10.0F);
        column.setPaddingBottom(10.0F);
        column.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        dataTable.addCell(column);

        column = new PdfPCell(new Phrase(amountFormat.format(credit.doubleValue()), headerFont));
        column.setPaddingTop(10.0F);
        column.setPaddingBottom(10.0F);
        column.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        dataTable.addCell(column);

        column = new PdfPCell(new Phrase(amountFormat.format(blank.doubleValue()), headerFont));
        column.setPaddingTop(10.0F);
        column.setPaddingBottom(10.0F);
        column.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        dataTable.addCell(column);            
    }
}

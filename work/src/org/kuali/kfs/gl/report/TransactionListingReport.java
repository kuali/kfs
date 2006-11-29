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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.kuali.Constants;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.gl.bo.Transaction;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

/**
 * This class prints out a transaction listing report. This is different from a transaction report in that this lists all the
 * transactions and a total amount. The transaction report shows the primary key from transactions and a list of messages for each
 * one.
 * 
 */
public class TransactionListingReport {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TransactionListingReport.class);

    class PageHelper extends PdfPageEventHelper {
        public Date runDate;
        public Font headerFont;
        public String title;

        public void onEndPage(PdfWriter writer, Document document) {
            try {
                Rectangle page = document.getPageSize();
                PdfPTable head = new PdfPTable(3);
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                PdfPCell cell = new PdfPCell(new Phrase(sdf.format(runDate), headerFont));
                cell.setBorder(Rectangle.NO_BORDER);
                head.addCell(cell);

                cell = new PdfPCell(new Phrase(title, headerFont));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                head.addCell(cell);

                cell = new PdfPCell(new Phrase("Page: " + new Integer(writer.getPageNumber()), headerFont));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                head.addCell(cell);

                head.setTotalWidth(page.width() - document.leftMargin() - document.rightMargin());
                head.writeSelectedRows(0, -1, document.leftMargin(), page.height() - document.topMargin() + head.getTotalHeight(), writer.getDirectContent());
            }
            catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        }
    }

    public TransactionListingReport() {
        super();
    }

    /**
     * This will generate a report on the transactions passed to it
     * 
     * @param transactions Transactions sorted properly
     * @param runDate
     * @param title
     * @param fileprefix
     * @param destinationDirectory
     */
    public void generateReport(Iterator<Transaction> transactions, Date runDate, String title, String fileprefix, String destinationDirectory) {
        LOG.debug("generateReport() started");

        Font headerFont = FontFactory.getFont(FontFactory.COURIER, 8, Font.BOLD);
        Font textFont = FontFactory.getFont(FontFactory.COURIER, 8, Font.NORMAL);

        Document document = new Document(PageSize.A4.rotate());

        PageHelper helper = new PageHelper();
        helper.runDate = runDate;
        helper.headerFont = headerFont;
        helper.title = title;

        try {
            String filename = destinationDirectory + "/" + fileprefix + "_";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            filename = filename + sdf.format(runDate);
            filename = filename + ".pdf";
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            writer.setPageEvent(helper);

            document.open();

            float[] widths = { 5, 9, 5, 5, 6, 5, 6, 5, 7, 25, 10, 10, 10 };
            PdfPTable transactionList = new PdfPTable(widths);
            transactionList.setHeaderRows(1);
            transactionList.setWidthPercentage(100);

            // Add headers
            PdfPCell cell = new PdfPCell(new Phrase("Fiscal Year", headerFont));
            transactionList.addCell(cell);
            cell = new PdfPCell(new Phrase("Account Number", headerFont));
            transactionList.addCell(cell);
            cell = new PdfPCell(new Phrase("Object Code", headerFont));
            transactionList.addCell(cell);
            cell = new PdfPCell(new Phrase("Object Type", headerFont));
            transactionList.addCell(cell);
            cell = new PdfPCell(new Phrase("Balance Type", headerFont));
            transactionList.addCell(cell);
            cell = new PdfPCell(new Phrase("Fiscal Period", headerFont));
            transactionList.addCell(cell);
            cell = new PdfPCell(new Phrase("Document Type", headerFont));
            transactionList.addCell(cell);
            cell = new PdfPCell(new Phrase("System Origin", headerFont));
            transactionList.addCell(cell);
            cell = new PdfPCell(new Phrase("Document Number", headerFont));
            transactionList.addCell(cell);
            cell = new PdfPCell(new Phrase("Description", headerFont));
            transactionList.addCell(cell);
            cell = new PdfPCell(new Phrase("Debit Amount", headerFont));
            transactionList.addCell(cell);
            cell = new PdfPCell(new Phrase("Credit Amount", headerFont));
            transactionList.addCell(cell);
            cell = new PdfPCell(new Phrase("Budget Amount", headerFont));
            transactionList.addCell(cell);

            int transactionCount = 0;
            KualiDecimal debitTotal = KualiDecimal.ZERO;
            KualiDecimal creditTotal = KualiDecimal.ZERO;
            KualiDecimal budgetTotal = KualiDecimal.ZERO;

            DecimalFormat nf = new DecimalFormat();
            nf.applyPattern("###,###,###,##0.00");

            if (transactions != null) {
                while (transactions.hasNext()) {
                    Transaction tran = (Transaction) transactions.next();

                    cell = new PdfPCell(new Phrase(tran.getUniversityFiscalYear() == null ? " " : tran.getUniversityFiscalYear().toString(), textFont));
                    transactionList.addCell(cell);
                    cell = new PdfPCell(new Phrase(tran.getChartOfAccountsCode() + "-" + tran.getAccountNumber(), textFont));
                    transactionList.addCell(cell);
                    cell = new PdfPCell(new Phrase(tran.getFinancialObjectCode(), textFont));
                    transactionList.addCell(cell);
                    cell = new PdfPCell(new Phrase(tran.getFinancialObjectTypeCode(), textFont));
                    transactionList.addCell(cell);
                    cell = new PdfPCell(new Phrase(tran.getFinancialBalanceTypeCode(), textFont));
                    transactionList.addCell(cell);
                    cell = new PdfPCell(new Phrase(tran.getUniversityFiscalPeriodCode(), textFont));
                    transactionList.addCell(cell);
                    cell = new PdfPCell(new Phrase(tran.getFinancialDocumentTypeCode(), textFont));
                    transactionList.addCell(cell);
                    cell = new PdfPCell(new Phrase(tran.getFinancialSystemOriginationCode(), textFont));
                    transactionList.addCell(cell);
                    cell = new PdfPCell(new Phrase(tran.getDocumentNumber(), textFont));
                    transactionList.addCell(cell);
                    cell = new PdfPCell(new Phrase(tran.getTransactionLedgerEntryDescription(), textFont));
                    transactionList.addCell(cell);

                    DecimalFormat decimalFormat = new DecimalFormat();

                    if (Constants.GL_DEBIT_CODE.equals(tran.getTransactionDebitCreditCode())) {
                        cell = new PdfPCell(new Phrase(nf.format(tran.getTransactionLedgerEntryAmount().doubleValue()), textFont));
                        debitTotal = debitTotal.add(tran.getTransactionLedgerEntryAmount());
                    }
                    else {
                        cell = new PdfPCell(new Phrase(nf.format(0), textFont));
                    }
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    transactionList.addCell(cell);

                    if (Constants.GL_CREDIT_CODE.equals(tran.getTransactionDebitCreditCode())) {
                        cell = new PdfPCell(new Phrase(nf.format(tran.getTransactionLedgerEntryAmount().doubleValue()), textFont));
                        creditTotal = creditTotal.add(tran.getTransactionLedgerEntryAmount());
                    }
                    else {
                        cell = new PdfPCell(new Phrase(nf.format(0), textFont));
                    }
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    transactionList.addCell(cell);
                    
                    if (!Constants.GL_CREDIT_CODE.equals(tran.getTransactionDebitCreditCode()) && !Constants.GL_DEBIT_CODE.equals(tran.getTransactionDebitCreditCode())){
                        cell = new PdfPCell(new Phrase(nf.format(tran.getTransactionLedgerEntryAmount().doubleValue()), textFont));
                        budgetTotal = budgetTotal.add(tran.getTransactionLedgerEntryAmount());
                    }
                    else {
                        cell = new PdfPCell(new Phrase(nf.format(0), textFont));
                    }
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    transactionList.addCell(cell);

                    transactionCount++;
                }
            }

            // Now add the total line
            cell = new PdfPCell(new Phrase("", textFont));
            transactionList.addCell(cell);
            cell = new PdfPCell(new Phrase("", textFont));
            transactionList.addCell(cell);
            
            DecimalFormat intf = new DecimalFormat();
            intf.applyPattern("###,###");
            cell = new PdfPCell(new Phrase(intf.format(transactionCount), headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            transactionList.addCell(cell);
            cell = new PdfPCell(new Phrase("", textFont));
            cell.setColspan(7);
            transactionList.addCell(cell);
            cell = new PdfPCell(new Phrase(nf.format(debitTotal.doubleValue()), headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            transactionList.addCell(cell);
            cell = new PdfPCell(new Phrase(nf.format(creditTotal.doubleValue()), headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            transactionList.addCell(cell);
            
            cell = new PdfPCell(new Phrase(nf.format(budgetTotal.doubleValue()), headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            transactionList.addCell(cell);

            document.add(transactionList);
        }
        catch (DocumentException de) {
            LOG.error("generateReport() Error creating PDF report", de);
            throw new RuntimeException("Report Generation Failed: " + de.getMessage());
        }
        catch (FileNotFoundException fnfe) {
            LOG.error("generateReport() Error writing PDF report", fnfe);
            throw new RuntimeException("Report Generation Failed: Error writing to file " + fnfe.getMessage());
        }
        finally {
            if ((document != null) && document.isOpen()) {
                document.close();
            }
        }
    }
}

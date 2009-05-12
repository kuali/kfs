/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.gl.report;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.report.BusinessObjectReportHelper;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.kns.util.KualiDecimal;

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
 */
public class TransactionListingReport {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TransactionListingReport.class);

    public TransactionListingReport() {
        super();
    }

    /**
     * This will generate a report on the transactions passed to it
     * 
     * @param transactions Transactions sorted properly
     * @param runDate date report is run
     * @param title title of report
     * @param fileprefix file prefix of file
     * @param destinationDirectory directory where file resides
     */
    public void generateReport(ReportWriterService reportWriterService, Iterator<? extends Transaction> transactions, Date runDate) {
        LOG.debug("generateReport() started");

        int transactionCount = 0;
        KualiDecimal debitTotal = KualiDecimal.ZERO;
        KualiDecimal creditTotal = KualiDecimal.ZERO;
        KualiDecimal budgetTotal = KualiDecimal.ZERO;

        DecimalFormat nf = new DecimalFormat();
        nf.applyPattern("###,###,###,##0.00");

        if (transactions != null) {
            while (transactions.hasNext()) {
                Transaction tran = (Transaction) transactions.next();

                reportWriterService.writeTableRow(tran);
                
                if (KFSConstants.GL_DEBIT_CODE.equals(tran.getTransactionDebitCreditCode())) {
                    debitTotal = debitTotal.add(tran.getTransactionLedgerEntryAmount());
                }
                if (KFSConstants.GL_CREDIT_CODE.equals(tran.getTransactionDebitCreditCode())) {
                    creditTotal = creditTotal.add(tran.getTransactionLedgerEntryAmount());
                }
                if (!KFSConstants.GL_CREDIT_CODE.equals(tran.getTransactionDebitCreditCode()) && !KFSConstants.GL_DEBIT_CODE.equals(tran.getTransactionDebitCreditCode())) {
                    budgetTotal = budgetTotal.add(tran.getTransactionLedgerEntryAmount());
                }
                transactionCount++;
            }
        }

            /*// Now add the total line
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

            document.add(transactionList);*/
    }
}

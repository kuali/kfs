/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.util.TransactionReport.PageHelper;

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

public class TransactionReportGenerator {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TransactionReportGenerator.class);
    public static final String PDF_FILE_EXTENSION = ".pdf";

    private Font headerFont = FontFactory.getFont(FontFactory.COURIER, 8, Font.BOLD);
    private Font textFont = FontFactory.getFont(FontFactory.COURIER, 8, Font.NORMAL);
    private Font totalFieldFont = FontFactory.getFont(FontFactory.COURIER, 8, Font.BOLD);

    /**
     * This method generates report based on the given summary
     * 
     * @param reportSummary the summary information to be reported
     * @param reportingDate the reporting date
     * @param title the report title
     * @param reportNamePrefix the prefix of the generated report file
     * @param destinationDirectory the directory where the report is located
     */
    public void generateSummaryReport(List reportSummary, Date reportingDate, String title, String reportNamePrefix, String destinationDirectory) {
        LOG.debug("generateSummaryReport() started");
        
        if(reportSummary != null){
            this.generatePDFReport(this.buildSummaryTable(reportSummary), reportingDate, title, reportNamePrefix, destinationDirectory);
        }
    }
    
    /**
     * This method generates report based on the given error information
     * 
     * @param reportErrors the error information to be reported
     * @param reportingDate the reporting date
     * @param title the report title
     * @param reportNamePrefix the prefix of the generated report file
     * @param destinationDirectory the directory where the report is located
     */
    public void generateErrorReport(Map reportErrors, Date reportingDate, String title, String reportNamePrefix, String destinationDirectory) {
        LOG.debug("generateErrorReport() started");
        
        if(reportErrors != null){
            this.generatePDFReport(this.buildErrorTable(reportErrors), reportingDate, title, reportNamePrefix, destinationDirectory);
        }
    }    

    // generate the PDF report with the given information
    private void generatePDFReport(PdfPTable pdfContents, Date reportingDate, String title, String reportNamePrefix, String destinationDirectory) {
        Document document = new Document(PageSize.A4.rotate());

        PDFPageHelper pageHelper = new PDFPageHelper();
        pageHelper.setRunDate(reportingDate);
        pageHelper.setHeaderFont(headerFont);
        pageHelper.setTitle(title);

        try {
            String filename = destinationDirectory + "/" + reportNamePrefix + "_";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            filename = filename + sdf.format(reportingDate);
            filename = filename + PDF_FILE_EXTENSION;

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            writer.setPageEvent(pageHelper);

            document.open();
            document.add(pdfContents);
        }
        catch (Exception de) {
            LOG.error("generateReport() Error creating PDF report", de);
            throw new RuntimeException("Report Generation Failed");
        }
        finally {
            this.closeDocument(document);
        }
    }
    
    // construct the summary table
    private PdfPTable buildSummaryTable(List reportSummary) {

        float[] cellWidths = { 80, 20 };
        PdfPTable summaryTable = new PdfPTable(cellWidths);
        summaryTable.setWidthPercentage(40);
        
        PdfPCell cell = new PdfPCell(new Phrase("S T A T I S T I C S", headerFont));
        cell.setColspan(2);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        summaryTable.addCell(cell);

        Collections.sort(reportSummary);
        for (Iterator iter = reportSummary.iterator(); iter.hasNext();) {
            Summary summary = (Summary) iter.next();
            this.addRow(summaryTable, summary, textFont);
        }
        return summaryTable;
    }

    // add a row with the given ledger entry into PDF table
    private void addRow(PdfPTable summaryTable, Summary summary, Font textFont) {
        
        PdfPCell cell = new PdfPCell(new Phrase(summary.getDescription(), textFont));
        cell.setBorder(Rectangle.NO_BORDER);
        summaryTable.addCell(cell);
    
        if ("".equals(summary.getDescription())) {
            cell = new PdfPCell(new Phrase("", textFont));
            cell.setBorder(Rectangle.NO_BORDER);
            summaryTable.addCell(cell);
        }
        else {
            DecimalFormat nf = new DecimalFormat("###,###,###,##0");
            cell = new PdfPCell(new Phrase(nf.format(summary.getCount()), textFont));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            summaryTable.addCell(cell);
        }
    }

    // construct the error table
    private PdfPTable buildErrorTable(Map reportErrors) {

        float[] cellWidths = { 4, 3, 6, 5, 5, 4, 5, 5, 4, 5, 5, 9, 4, 36 };
        PdfPTable errorTable = new PdfPTable(cellWidths);
        errorTable.setHeaderRows(2);
        errorTable.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell(new Phrase("W A R N I N G S", headerFont));
        cell.setColspan(14);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        errorTable.addCell(cell);
        
        if (reportErrors != null && reportErrors.size() > 0) {
            this.addHeader(errorTable, headerFont);
            for (Iterator errorIter = reportErrors.keySet().iterator(); errorIter.hasNext();) {
                Transaction transaction = (Transaction) errorIter.next();
                this.addRow(errorTable, reportErrors, transaction, textFont);
            }
        }
        else{
            cell = new PdfPCell(new Phrase("No errors occured!", headerFont));
            cell.setColspan(14);
            errorTable.addCell(cell);            
        }
        return errorTable;
    }

    // add a table header
    private void addHeader(PdfPTable errorTable, Font headerFont) {

        PdfPCell cell = new PdfPCell(new Phrase("Year", headerFont));
        errorTable.addCell(cell);
        cell = new PdfPCell(new Phrase("COA", headerFont));
        errorTable.addCell(cell);
        cell = new PdfPCell(new Phrase("Account", headerFont));
        errorTable.addCell(cell);
        cell = new PdfPCell(new Phrase("Sacct", headerFont));
        errorTable.addCell(cell);
        cell = new PdfPCell(new Phrase("Obj", headerFont));
        errorTable.addCell(cell);
        cell = new PdfPCell(new Phrase("SObj", headerFont));
        errorTable.addCell(cell);
        cell = new PdfPCell(new Phrase("BalTyp", headerFont));
        errorTable.addCell(cell);
        cell = new PdfPCell(new Phrase("ObjTyp", headerFont));
        errorTable.addCell(cell);
        cell = new PdfPCell(new Phrase("Prd", headerFont));
        errorTable.addCell(cell);
        cell = new PdfPCell(new Phrase("DocType", headerFont));
        errorTable.addCell(cell);
        cell = new PdfPCell(new Phrase("Origin", headerFont));
        errorTable.addCell(cell);
        cell = new PdfPCell(new Phrase("DocNbr", headerFont));
        errorTable.addCell(cell);
        cell = new PdfPCell(new Phrase("Seq", headerFont));
        errorTable.addCell(cell);
        cell = new PdfPCell(new Phrase("Warning", headerFont));
        errorTable.addCell(cell);
    }

    // add a row with the given ledger entry into PDF table
    private void addRow(PdfPTable errorTable, Map reportErrors, Transaction transaction, Font textFont) {
        PdfPCell cell = null;
        boolean first = true;

        List errors = (List) reportErrors.get(transaction);
        for (Iterator listIter = errors.iterator(); listIter.hasNext();) {
            Object m = listIter.next();
            String msg = m.toString();

            if (first) {
                first = false;

                String fiscalYear = transaction.getUniversityFiscalYear() == null ? "NULL" : transaction.getUniversityFiscalYear().toString();
                cell = new PdfPCell(new Phrase(fiscalYear, textFont));
                errorTable.addCell(cell);

                cell = new PdfPCell(new Phrase(transaction.getChartOfAccountsCode(), textFont));
                errorTable.addCell(cell);
                cell = new PdfPCell(new Phrase(transaction.getAccountNumber(), textFont));
                errorTable.addCell(cell);
                cell = new PdfPCell(new Phrase(transaction.getSubAccountNumber(), textFont));
                errorTable.addCell(cell);
                cell = new PdfPCell(new Phrase(transaction.getFinancialObjectCode(), textFont));
                errorTable.addCell(cell);
                cell = new PdfPCell(new Phrase(transaction.getFinancialSubObjectCode(), textFont));
                errorTable.addCell(cell);
                cell = new PdfPCell(new Phrase(transaction.getFinancialBalanceTypeCode(), textFont));
                errorTable.addCell(cell);
                cell = new PdfPCell(new Phrase(transaction.getFinancialObjectTypeCode(), textFont));
                errorTable.addCell(cell);
                cell = new PdfPCell(new Phrase(transaction.getUniversityFiscalPeriodCode(), textFont));
                errorTable.addCell(cell);
                cell = new PdfPCell(new Phrase(transaction.getFinancialDocumentTypeCode(), textFont));
                errorTable.addCell(cell);
                cell = new PdfPCell(new Phrase(transaction.getFinancialSystemOriginationCode(), textFont));
                errorTable.addCell(cell);
                cell = new PdfPCell(new Phrase(transaction.getFinancialDocumentNumber(), textFont));
                errorTable.addCell(cell);

                String squenceNumber = transaction.getUniversityFiscalYear() == null ? "NULL" : transaction.getTransactionLedgerEntrySequenceNumber().toString();
                cell = new PdfPCell(new Phrase(squenceNumber, textFont));
                errorTable.addCell(cell);
            }
            else {
                cell = new PdfPCell(new Phrase("", textFont));
                cell.setColspan(13);
                errorTable.addCell(cell);
            }
            cell = new PdfPCell(new Phrase(msg, textFont));
            errorTable.addCell(cell);
        }
    }

    // close the document and release the resource
    private void closeDocument(Document document) {
        try {
            if ((document != null) && document.isOpen()) {
                document.close();
            }
        }
        catch (Throwable t) {
            LOG.error("generateReport() Exception closing report", t);
        }
    }
}

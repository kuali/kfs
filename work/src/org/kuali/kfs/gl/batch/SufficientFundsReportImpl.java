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
package org.kuali.module.gl.batch.sufficientFunds.impl;

import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.gl.batch.sufficientFunds.SufficientFundsReport;
import org.kuali.module.gl.bo.SufficientFundRebuild;
import org.kuali.module.gl.util.Summary;

import com.lowagie.text.Document;
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
 * The default implementation of SufficientFundsReport
 */
public class SufficientFundsReportImpl extends PdfPageEventHelper implements SufficientFundsReport {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SufficientFundsReportImpl.class);
    private KualiConfigurationService configurationService;

    /**
     * Constructs a SufficientFundsReportImpl instance
     */
    public SufficientFundsReportImpl() {
        super();
    }

    /**
     * Generates the sufficient funds rebuild report and saves it to PDF
     * 
     * @param reportErrors a Map of errors encountered during the sufficient funds rebuild process
     * @param reportSummary a List of Strings that summarize the sufficient funds rebuild process 
     * @param runDate the date the sufficient funds rebuild process was run
     * @param mode the mode the sufficient funds rebuild process was run in
     * @see org.kuali.module.gl.batch.sufficientFunds.SufficientFundsReport#generateReport(java.util.Map, java.util.List, java.util.Date, int)
     */
    public void generateReport(Map reportErrors, List reportSummary, Date runDate, int mode) {
        LOG.debug("generateReport() started");

        String destinationDirectory = configurationService.getPropertyString(KFSConstants.REPORTS_DIRECTORY_KEY);

        String title = "Sufficient Funds Report ";
        String fileprefix = "sufficientFunds";

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

            // Sort what we get
            Collections.sort(reportSummary);

            float[] summaryWidths = { 90, 10 };
            PdfPTable summary = new PdfPTable(summaryWidths);
            summary.setWidthPercentage(40);
            PdfPCell cell = new PdfPCell(new Phrase("S T A T I S T I C S", headerFont));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            summary.addCell(cell);

            for (Iterator iter = reportSummary.iterator(); iter.hasNext();) {
                Summary s = (Summary) iter.next();

                cell = new PdfPCell(new Phrase(s.getDescription(), textFont));
                cell.setBorder(Rectangle.NO_BORDER);
                summary.addCell(cell);

                if ("".equals(s.getDescription())) {
                    cell = new PdfPCell(new Phrase("", textFont));
                    cell.setBorder(Rectangle.NO_BORDER);
                    summary.addCell(cell);
                }
                else {
                    DecimalFormat nf = new DecimalFormat("###,###,##0");
                    cell = new PdfPCell(new Phrase(nf.format(s.getCount()), textFont));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                    summary.addCell(cell);
                }
            }
            cell = new PdfPCell(new Phrase(""));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            summary.addCell(cell);

            document.add(summary);

            if (reportErrors != null && reportErrors.size() > 0) {
                float[] warningWidths = { 5, 12, 12, 53 };
                PdfPTable warnings = new PdfPTable(warningWidths);
                warnings.setHeaderRows(2);
                warnings.setWidthPercentage(100);
                cell = new PdfPCell(new Phrase("W A R N I N G S", headerFont));
                cell.setColspan(4);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                warnings.addCell(cell);

                // Add headers
                cell = new PdfPCell(new Phrase("COA", headerFont));
                warnings.addCell(cell);
                cell = new PdfPCell(new Phrase("Account/Object Code", headerFont));
                warnings.addCell(cell);
                cell = new PdfPCell(new Phrase("Account/Object Type", headerFont));
                warnings.addCell(cell);
                cell = new PdfPCell(new Phrase("Warning", headerFont));
                warnings.addCell(cell);

                for (Iterator errorIter = reportErrors.keySet().iterator(); errorIter.hasNext();) {
                    SufficientFundRebuild sfrb = (SufficientFundRebuild) errorIter.next();
                    boolean first = true;

                    List errors = (List) reportErrors.get(sfrb);
                    for (Iterator listIter = errors.iterator(); listIter.hasNext();) {
                        String msg = (String) listIter.next();

                        if (first) {
                            first = false;
                            cell = new PdfPCell(new Phrase(sfrb.getChartOfAccountsCode(), textFont));
                            warnings.addCell(cell);
                            cell = new PdfPCell(new Phrase(sfrb.getAccountNumberFinancialObjectCode(), textFont));
                            warnings.addCell(cell);
                            cell = new PdfPCell(new Phrase(sfrb.getAccountFinancialObjectTypeCode(), textFont));
                            warnings.addCell(cell);
                        }
                        else {
                            cell = new PdfPCell(new Phrase("", textFont));
                            cell.setColspan(3);
                            warnings.addCell(cell);
                        }
                        cell = new PdfPCell(new Phrase(msg, textFont));
                        warnings.addCell(cell);
                    }
                }
                document.add(warnings);
            }
        }
        catch (Exception de) {
            LOG.error("generateReport() Error creating PDF report", de);
            throw new RuntimeException("Report Generation Failed");
        }

        document.close();
    }

    /**
     * An inner class to help set up the PDF that is written
     */
    class PageHelper extends PdfPageEventHelper {
        public Date runDate;
        public Font headerFont;
        public String title;

        /**
         * Writes the footer on the last page
         * @see com.lowagie.text.pdf.PdfPageEventHelper#onEndPage(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
         */
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

    /**
     * Sets the configurationService attribute value.
     * 
     * @param configurationService The configurationService to set.
     */
    public void setConfigurationService(KualiConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}

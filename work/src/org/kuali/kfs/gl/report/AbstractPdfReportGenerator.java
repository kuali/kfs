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
package org.kuali.module.gl.util;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;

/**
 * This class is used to generate PDF reports for G/L related information
 */
public abstract class AbstractPdfReportGenerator {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AbstractPdfReportGenerator.class);
    public static final String PDF_FILE_EXTENSION = ".pdf";
    private static String REPORT_FILE_DATE_FORMAT = "yyyyMMdd_HHmmss";

    // generate the PDF report with the given information
    /**
     * This method generates a PDF report
     * 
     * @param reportingDate date report supposed to be for
     * @param title title of report
     * @param reportFileName file name used for saving report
     */
    public void generatePdfReport(Date reportingDate, String title, String reportFileName) {
        Document document = this.getDocument();

        PDFPageHelper pageHelper = new PDFPageHelper();
        pageHelper.setRunDate(reportingDate);
        pageHelper.setHeaderFont(this.getHeaderFont());
        pageHelper.setTitle(title);

        try {
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(reportFileName));
            pdfWriter.setPageEvent(pageHelper);

            document.open();
            document.add(this.getReportContents());
        }
        catch (Exception de) {
            LOG.error("generateReport() Error creating PDF report", de);
            throw new RuntimeException("Report Generation Failed");
        }
        finally {
            this.closeDocument(document);
        }
    }

    /**
     * This method returns an Element object which is used to display the actual content of a PDF report
     * 
     * @return Element represents report contents
     */
    public abstract Element getReportContents();

    /**
     * This method returns a reports file name
     * 
     * @param reportNamePrefix prefix of file name
     * @param destinationDirectory destination directory of file
     * @param reportingDate date report is for
     * @return
     */
    protected String generateReportFileName(String reportNamePrefix, String destinationDirectory, Date reportingDate) {
        String reportFilename = destinationDirectory + "/" + reportNamePrefix + "_";
        SimpleDateFormat dateFormat = new SimpleDateFormat(REPORT_FILE_DATE_FORMAT);
        reportFilename = reportFilename + dateFormat.format(reportingDate);
        reportFilename = reportFilename + PDF_FILE_EXTENSION;

        return reportFilename;
    }

    /**
     * Returns a new document
     * 
     * @return Document PDF document
     */
    protected Document getDocument() {
        return new Document(PageSize.A4.rotate());
    }

    /**
     * Returns font for header
     * 
     * @return Font font for header
     */
    protected Font getHeaderFont() {
        return FontFactory.getFont(FontFactory.COURIER, 8, Font.BOLD);
    }

    /**
     * Return font for content
     * 
     * @return
     */
    protected Font getTextFont() {
        return FontFactory.getFont(FontFactory.COURIER, 8, Font.BOLD);
    }

    /**
     * Close access to document
     * 
     * @param document document being closed
     */
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

/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.report;

import java.util.Date;
import java.util.List;

import org.kuali.module.gl.util.AbstractPdfReportGenerator;

import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

/**
 * This class can generate PDF report with the given transaction summary
 */
public class TransactionSummaryReport extends AbstractPdfReportGenerator {
    private Element reportContents;

    /**
     * generate the PDF report according to the given report information
     * 
     * @param reportSummary the given report contents
     * @param reportingDate the given reporting data
     * @param reportTitle the given report title
     * @param reportFilename the given report file name
     * @param reportsDirectory the given dirctory where the generated report will be stored
     */
    public void generateReport(List<String> reportSummary, Date reportingDate, String reportTitle, String reportFilename, String reportsDirectory) {
        setReportContents(reportSummary);
        String filename = this.generateReportFileName(reportFilename, reportsDirectory, reportingDate);
        this.generatePdfReport(reportingDate, reportTitle, filename);
    }

    /**
     * @see org.kuali.module.gl.util.AbstractPdfReportGenerator#getReportContents()
     */
    @Override
    public Element getReportContents() {
        return this.reportContents;
    }

    /**
     * setup the report contents with the given information
     * 
     * @param reportSummary the given report contents
     */
    public void setReportContents(List<String> reportSummary) {
        reportContents = this.buildSummaryTable(reportSummary);
    }

    // construct the summary table
    private PdfPTable buildSummaryTable(List<String> reportSummary) {

        float[] cellWidths = { 100 };
        PdfPTable summaryTable = new PdfPTable(cellWidths);
        summaryTable.setWidthPercentage(60);

        PdfPCell cell = new PdfPCell(new Phrase("S T A T I S T I C S", this.getHeaderFont()));
        cell.setColspan(1);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        summaryTable.addCell(cell);

        for (String itemline : reportSummary) {
            cell = new PdfPCell(new Phrase(itemline, this.getTextFont()));
            cell.setBorder(Rectangle.NO_BORDER);
            summaryTable.addCell(cell);
        }
        return summaryTable;
    }
}

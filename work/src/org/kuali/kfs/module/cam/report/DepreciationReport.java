/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.cams.report;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.util.GeneralLedgerPendingEntryReport;
import org.kuali.core.service.KualiConfigurationService;

import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

public class DepreciationReport {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GeneralLedgerPendingEntryReport.class);
    private int pageNumber=0;
    private int line=0;
    private int linesPerPage=30;
    private Document document;
    private PdfWriter writer;

    /**
     * 
     * This method...
     * @param reportLog
     * @param errorMsg
     */
    public void generateReport(List<String[]> reportLog, String errorMsg) {
        try {
            LOG.debug("createReport() started");
            this.document = new Document();        

            String destinationDirectory = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.REPORTS_DIRECTORY_KEY);

            String fileprefix = "CAMS";

            Date date = new Date();
            String filename = destinationDirectory + "/" + fileprefix + "_";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            filename = filename + sdf.format(date) + ".pdf"; 

            Font headerFont= FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL);

            String reportTitle = CamsConstants.CAMS;
            String subTitle    = CamsConstants.DEPRECIATION_REPORT_SUBTITLE;

            PageHelper helper = new PageHelper();
            helper.runDate      = date;
            helper.headerFont   = headerFont;
            helper.title        = subTitle;
            writer = PdfWriter.getInstance(this.document, new FileOutputStream(filename));            
            writer.setPageEvent(helper);

            this.document.open();

            //Generate body of document.
            this.generateReportLogBody(reportLog);
            this.generateReportErrorLog(errorMsg);

        } catch (Exception e) {
            throw new RuntimeException("DepreciationReport.generateReport(List<String[]> reportLog, List<String> errorLog) - Error on report generation: " + e.getMessage());
        } finally {
            if ((this.document != null) && this.document.isOpen()) {
                this.document.close();
            }
        }
    }

    /**
     * 
     * This method...
     * @param reportLog
     */
    private void generateReportLogBody(List<String[]> reportLog) {
        try {            
            Font font= FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL);
            int columnwidths[];             
            columnwidths = new int[] {40, 15};                  

            Table aTable = new Table(2,linesPerPage);
            int rowsWritten=0;
            for(String[] columns : reportLog) {
                //if (pageNumber ==0 || line >= aTable.size()) {
                if (pageNumber ==0 || line >= linesPerPage) {
                    if (pageNumber > 0) {
                        this.document.add(aTable);
                    }
                    int elementsLeft = reportLog.size() - rowsWritten;
                    int rowsNeeded = ( elementsLeft >= linesPerPage ? linesPerPage : elementsLeft );
                    this.document.newPage();

                    this.generateColumnHeaders();

                    aTable = new Table(2,rowsNeeded); //12 columns, 11 rows.

                    aTable.setAutoFillEmptyCells(true); 
                    aTable.setPadding(3);   
                    aTable.setWidths(columnwidths);             
                    aTable.setWidth(100);
                    aTable.setBorder(Rectangle.NO_BORDER);

                    line=0;
                    pageNumber++;
                }                
                rowsWritten++;

                Cell cell;
                cell = new Cell(new Phrase(columns[0],font));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                aTable.addCell(cell);

                cell = new Cell(new Phrase(columns[1],font));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                aTable.addCell(cell);
                line++;                
            }
            this.document.add(aTable);
        } catch (DocumentException de) {
            throw new RuntimeException("DepreciationReport.generateReportLogBody(List<String[]> reportLog) - error: " + de.getMessage());
        }    
    }

    /**
     * 
     * This method...
     * @param reportLog
     */
    private void generateReportErrorLog(String errorMsg) {
        try {            
            Font font= FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL);
            Paragraph p1    =   new Paragraph();

            int rowsWritten=0;
            //for(String error : reportLog) {
            if (!errorMsg.equals("")) {
                //if (pageNumber ==0 || line >= linesPerPage) {
                //    this.document.newPage();                    
                this.generateErrorColumnHeaders();

                //    line=0;
                //    pageNumber++;
                //}                
                p1 = new Paragraph(new Chunk(errorMsg,font));
                this.document.add(p1);
                line++;                
            }
        } catch (Exception de) {
            throw new RuntimeException("DepreciationReport.generateReportErrorLog(List<String> reportLog) - Report Generation Failed: " + de.getMessage());
        }    
    }

    /**
     * 
     */
    private void generateErrorColumnHeaders() throws DocumentException {
        try {
            int headerwidths[] = {60};

            Table aTable = new Table(1,1); //2 columns, 1 rows.

            aTable.setAutoFillEmptyCells(true); 
            aTable.setPadding(3);   
            aTable.setWidths(headerwidths);             
            aTable.setWidth(100);

            Cell cell;

            Font font= FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL);

            cell = new Cell(new Phrase("Error(s)",font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setGrayFill(0.9f);
            aTable.addCell(cell);

            this.document.add(aTable);

        } catch(Exception e) {
            throw new RuntimeException("DepreciationReport.generateErrorColumnHeaders() - Error: " + e.getMessage());
        }
    }

    /**
     * 
     * This method...
     */
    private void generateColumnHeaders() {
        try {
            int headerwidths[] = {40, 15};

            Table aTable = new Table(2,1); //2 columns, 1 rows.

            aTable.setAutoFillEmptyCells(true); 
            aTable.setPadding(3);   
            aTable.setWidths(headerwidths);             
            aTable.setWidth(100);

            Cell cell;

            Font font= FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL);

            cell = new Cell(new Phrase("Description",font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setGrayFill(0.9f);
            aTable.addCell(cell);

            cell = new Cell(new Phrase("Figures",font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setGrayFill(0.9f);
            aTable.addCell(cell);
            this.document.add(aTable);

        } catch(Exception e) {
            throw new RuntimeException("DepreciationReport.generateColumnHeaders() - Error: " + e.getMessage());            
        }
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

                head.writeSelectedRows(1, -1, document.leftMargin(), page.height() - document.topMargin() + head.getTotalHeight(), writer.getDirectContent());

            }
            catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        }        
    }
}
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
package org.kuali.module.pdp.service.impl;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.service.DateTimeService;
import org.kuali.module.gl.util.TransactionReport.PageHelper;
import org.kuali.module.pdp.bo.DailyReport;
import org.kuali.module.pdp.service.DailyReportService;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class DailyReportServiceImpl implements DailyReportService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DailyReportServiceImpl.class);

    private DateTimeService dateTimeService;
    private String directoryName;

    private Font headerFont;
    private Font textFont;

    public DailyReportServiceImpl() {
        headerFont = FontFactory.getFont(FontFactory.COURIER, 8, Font.BOLD);
        textFont = FontFactory.getFont(FontFactory.COURIER, 8, Font.NORMAL);
    }

    private List<DailyReport> getData() {
        LOG.debug("getData() started");

        List c = new ArrayList();
        c.add(new DailyReport("Bank One CK","Attachment","BL-TRMS-TRIU",new BigDecimal(299974),10,10));
        c.add(new DailyReport("Bank One CK","","BL-BURS-SIS",new BigDecimal(287762.18),136,136));
        c.add(new DailyReport("Bank One CK","","BL-TRMS-TRIU",new BigDecimal(38048.23),99,99));
        c.add(new DailyReport("Northern Trust CK","Special Handling","BL-FMOP-EPIC",new BigDecimal(5174),1,1));
        return c;
    }

    public void runReport() {
        LOG.debug("runReport() started");

        Collection<DailyReport> data = getData();
        Date today = dateTimeService.getCurrentDate();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        Document document = openPdfWriter(directoryName, "daily", dateTimeService.getCurrentDate(), "Summary of Payments Eligible For Format On " + sdf.format(today));

        try {
            float[] summaryWidths = { 15, 15, 15, 15, 15, 15 };
            PdfPTable dataTable = new PdfPTable(summaryWidths);
            dataTable.setWidthPercentage(100);
            dataTable.setHeaderRows(1);
            addHeader(dataTable);

            String lastBankName = "";
            DailyReport bankTotal = new DailyReport();
            DailyReport total = new DailyReport();
            total.setBankName("Total");

            for (Iterator iter = data.iterator(); iter.hasNext();) {
                DailyReport dr = (DailyReport)iter.next();
                if ( bankTotal.getBankName() == null ) {
                    bankTotal.setBankName(dr.getBankName());
                }

                if ( ! bankTotal.getBankName().equals(dr.getBankName()) ) {
                    addRow(dataTable,bankTotal,true);
                    bankTotal = new DailyReport();
                    bankTotal.setBankName(dr.getBankName());
                    bankTotal.addRow(dr);
                } else {
                    bankTotal.addRow(dr);
                }

                if ( ! lastBankName.equals(dr.getBankName()) ) {
                    addRow(dataTable,dr,false);
                } else {
                    addRow(dataTable,dr,false,false);
                }
                lastBankName = dr.getBankName();

                total.addRow(dr);
            }

            addRow(dataTable,bankTotal,true);
            addRow(dataTable,total,true);

            document.add(dataTable);
        } catch (DocumentException d) {
            
        }
        document.close();
    }

    private void addHeader(PdfPTable dataTable) {
        PdfPCell cell = new PdfPCell(new Phrase("Bank", headerFont));
        dataTable.addCell(cell);

        cell = new PdfPCell(new Phrase("Sort Order", headerFont));
        dataTable.addCell(cell);

        cell = new PdfPCell(new Phrase("Customer", headerFont));
        dataTable.addCell(cell);

        cell = new PdfPCell(new Phrase("Amount of Payments", headerFont));
        dataTable.addCell(cell);

        cell = new PdfPCell(new Phrase("# of Payment Records", headerFont));
        dataTable.addCell(cell);

        cell = new PdfPCell(new Phrase("# of Payees", headerFont));
        dataTable.addCell(cell);
    }

    private void addRow(PdfPTable dataTable,DailyReport dr,boolean bold) {
        addRow(dataTable,dr,bold,true);
    }

    private void addRow(PdfPTable dataTable,DailyReport dr,boolean bold,boolean printBankName) {
        DecimalFormat af = new DecimalFormat("###,###,##0.00");
        DecimalFormat nf = new DecimalFormat("###,##0");

        Font f = null;
        if ( bold ) {
            f = headerFont;

            for (int i = 0; i < 6; i++) {
                PdfPCell cell = new PdfPCell(new Phrase(" ", f));
                cell.setBorder(Rectangle.NO_BORDER);
                dataTable.addCell(cell);
            }
        } else {
            f = textFont;
        }

        PdfPCell cell = new PdfPCell(new Phrase(printBankName ? dr.getBankName() : "", f));
        cell.setBorder(Rectangle.NO_BORDER);
        dataTable.addCell(cell);

        cell = new PdfPCell(new Phrase(dr.getSortOrder(), f));
        cell.setBorder(Rectangle.NO_BORDER);
        dataTable.addCell(cell);

        cell = new PdfPCell(new Phrase(dr.getCustomer(), f));
        cell.setBorder(Rectangle.NO_BORDER);
        dataTable.addCell(cell);

        cell = new PdfPCell(new Phrase(af.format(dr.getAmount()), f));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        dataTable.addCell(cell);

        cell = new PdfPCell(new Phrase(nf.format(dr.getPayments()), f));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        dataTable.addCell(cell);

        cell = new PdfPCell(new Phrase(nf.format(dr.getPayees()), f));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        dataTable.addCell(cell);

        if ( bold ) {
            for (int i = 0; i < 6; i++) {
                PdfPCell cell2 = new PdfPCell(new Phrase(" ", f));
                cell2.setBorder(Rectangle.NO_BORDER);
                dataTable.addCell(cell2);
            }
        }
    }

    protected Document openPdfWriter(String destinationDirectory, String fileprefix, Date runDate, String title) {
        try {
            Document document = new Document(PageSize.A4.rotate());

            PageHelper helper = new PageHelper();
            helper.runDate = runDate;
            helper.headerFont = headerFont;
            helper.title = title;

            String filename = destinationDirectory + "/" + fileprefix + "_";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            filename = filename + sdf.format(runDate);
            filename = filename + ".pdf";
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            writer.setPageEvent(helper);

            document.open();

            return document;
        }
        catch (Exception e) {
            LOG.error("openPdfWriter() Exception caught trying to create new PDF document", e);
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            else {
                throw new RuntimeException(e);
            }
        }
    }

    public void setDirectoryName(String d) {
        directoryName = d;
    }

    public void setDateTimeService(DateTimeService dts) {
        dateTimeService = dts;
    }
}

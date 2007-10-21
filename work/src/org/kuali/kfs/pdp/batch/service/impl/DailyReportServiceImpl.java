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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.service.DateTimeService;
import org.kuali.module.gl.util.TransactionReport.PageHelper;
import org.kuali.module.pdp.bo.DailyReport;
import org.kuali.module.pdp.dao.PaymentDetailDao;
import org.kuali.module.pdp.service.DailyReportService;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Transactional
public class DailyReportServiceImpl implements DailyReportService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DailyReportServiceImpl.class);

    private PaymentDetailDao paymentDetailDao;
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

        return paymentDetailDao.getDailyReportData();
    }

    public void runReport() {
        LOG.debug("runReport() started");

        Collection<DailyReport> data = getData();
        Date today = dateTimeService.getCurrentDate();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        Document document = openPdfWriter(directoryName, "pdp_daily", dateTimeService.getCurrentDate(), "Summary of Payments Eligible For Format On " + sdf.format(today));

        try {
            float[] summaryWidths = { 20, 20, 20, 20, 20 };
            PdfPTable dataTable = new PdfPTable(summaryWidths);
            dataTable.setWidthPercentage(100);
            dataTable.setHeaderRows(1);
            addHeader(dataTable);

            boolean rows = false;
            String lastSortOrder = "";
            DailyReport sortTotal = new DailyReport();
            DailyReport total = new DailyReport();
            total.setSortOrder("Total");

            for (Iterator iter = data.iterator(); iter.hasNext();) {
                rows = true;
                DailyReport dr = (DailyReport)iter.next();
                if ( sortTotal.getSortOrder() == null ) {
                    sortTotal.setSortOrder(dr.getSortOrder());
                }

                if ( ! sortTotal.getSortOrder().equals(dr.getSortOrder()) ) {
                    sortTotal.setSortOrder("Total for " + sortTotal.getSortOrder());
                    addRow(dataTable,sortTotal,true);
                    sortTotal = new DailyReport();
                    sortTotal.setSortOrder(dr.getSortOrder());
                    sortTotal.addRow(dr);
                } else {
                    sortTotal.addRow(dr);
                }

                addRow(dataTable,dr,false);
                lastSortOrder = dr.getSortOrder();

                total.addRow(dr);
            }

            if ( rows ) {
                sortTotal.setSortOrder("Total for " + sortTotal.getSortOrder());
                addRow(dataTable,sortTotal,true);
            }
            addRow(dataTable,total,true);

            document.add(dataTable);
        } catch (DocumentException d) {
            
        }
        document.close();
    }

    private void addHeader(PdfPTable dataTable) {
        PdfPCell cell = new PdfPCell(new Phrase("Sort Order", headerFont));
        dataTable.addCell(cell);

        cell = new PdfPCell(new Phrase("Customer", headerFont));
        dataTable.addCell(cell);

        cell = new PdfPCell(new Phrase("Amount of Payments", headerFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        dataTable.addCell(cell);

        cell = new PdfPCell(new Phrase("# of Payment Records", headerFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        dataTable.addCell(cell);

        cell = new PdfPCell(new Phrase("# of Payees", headerFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        dataTable.addCell(cell);
    }

    private void addRow(PdfPTable dataTable,DailyReport dr,boolean bold) {
        DecimalFormat af = new DecimalFormat("###,###,##0.00");
        DecimalFormat nf = new DecimalFormat("###,##0");

        Font f = null;
        if ( bold ) {
            f = headerFont;

            for (int i = 0; i < 5; i++) {
                PdfPCell cell = new PdfPCell(new Phrase(" ", f));
                cell.setBorder(Rectangle.NO_BORDER);
                dataTable.addCell(cell);
            }
        } else {
            f = textFont;
        }

        PdfPCell cell = new PdfPCell(new Phrase(dr.getSortOrder(), f));
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
            for (int i = 0; i < 5; i++) {
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

    public void setPaymentDetailDao(PaymentDetailDao pdd) {
        paymentDetailDao = pdd;
    }
}

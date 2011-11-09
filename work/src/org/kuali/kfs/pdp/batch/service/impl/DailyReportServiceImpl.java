/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.pdp.batch.service.impl;

import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.gl.report.TransactionReport.PageHelper;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.batch.service.DailyReportService;
import org.kuali.kfs.pdp.businessobject.DailyReport;
import org.kuali.kfs.pdp.dataaccess.PaymentDetailDao;
import org.kuali.kfs.pdp.service.PaymentGroupService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
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
    private PaymentGroupService paymentGroupService;
    private ConfigurationService kualiConfigurationService;

    private Font headerFont;
    private Font textFont;

    public DailyReportServiceImpl() {
        headerFont = FontFactory.getFont(FontFactory.COURIER, 8, Font.BOLD);
        textFont = FontFactory.getFont(FontFactory.COURIER, 8, Font.NORMAL);
    }

    protected List<DailyReport> getData() {
        LOG.debug("getData() started");

        return paymentDetailDao.getDailyReportData(dateTimeService.getCurrentSqlDate());
    }

    public void runReport() {
        LOG.debug("runReport() started");

        Collection<DailyReport> data = getData();
        Date today = dateTimeService.getCurrentDate();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        String reportFilePrefix = this.kualiConfigurationService.getPropertyValueAsString(PdpKeyConstants.DAILY_REPORT_SERVICE_FILE_PREFIX);
        reportFilePrefix = MessageFormat.format(reportFilePrefix, new Object[] { null });

        String reportTitle = this.kualiConfigurationService.getPropertyValueAsString(PdpKeyConstants.DAILY_REPORT_SERVICE_REPORT_TITLE);
        reportTitle = MessageFormat.format(reportTitle, new Object[] { sdf.format(today) });

        Document document = openPdfWriter(directoryName, reportFilePrefix, dateTimeService.getCurrentDate(), reportTitle);

        try {
            float[] summaryWidths = { 20, 20, 20, 20, 20 };
            PdfPTable dataTable = new PdfPTable(summaryWidths);
            dataTable.setWidthPercentage(100);
            dataTable.setHeaderRows(1);
            addHeader(dataTable);

            boolean rows = false;
            DailyReport sortTotal = new DailyReport();
            DailyReport total = new DailyReport();
            DailyReport dr = new DailyReport();

            String totalForSubtitle = this.kualiConfigurationService.getPropertyValueAsString(PdpKeyConstants.DAILY_REPORT_SERVICE_TOTAL_FOR_SUBTITLE);

            String totalSubtitle = this.kualiConfigurationService.getPropertyValueAsString(PdpKeyConstants.DAILY_REPORT_SERVICE_TOTAL_SUBTITLE);
            totalSubtitle = MessageFormat.format(totalSubtitle, new Object[] { null });


            for (Iterator iter = data.iterator(); iter.hasNext();) {
                dr = (DailyReport) iter.next();

                if (!rows) {
                    rows = true;
                    sortTotal = new DailyReport(dr);
                    sortTotal.addRow(dr);
                    addRow(dataTable, dr, false, this.paymentGroupService.getSortGroupName(this.paymentGroupService.getSortGroupId(dr.getPaymentGroup())));
                }
                else if (this.paymentGroupService.getSortGroupId(sortTotal.getPaymentGroup()) != (this.paymentGroupService.getSortGroupId(dr.getPaymentGroup()))) {
                    String newTotalForSubtitle = MessageFormat.format(totalForSubtitle, new Object[] { this.paymentGroupService.getSortGroupName(this.paymentGroupService.getSortGroupId(sortTotal.getPaymentGroup())) });

                    addRow(dataTable, sortTotal, true, newTotalForSubtitle);
                    sortTotal = new DailyReport(dr);
                    sortTotal.addRow(dr);
                    addRow(dataTable, dr, false, this.paymentGroupService.getSortGroupName(this.paymentGroupService.getSortGroupId(dr.getPaymentGroup())));
                }
                else {
                    sortTotal.addRow(dr);
                    addRow(dataTable, dr, false, "");
                }

                total.addRow(dr);
            }

            if (rows) {
                String newTotalForSubtitle = MessageFormat.format(totalForSubtitle, new Object[] { this.paymentGroupService.getSortGroupName(this.paymentGroupService.getSortGroupId(sortTotal.getPaymentGroup())) });

                addRow(dataTable, sortTotal, true, newTotalForSubtitle);
            }
            addRow(dataTable, total, true, totalSubtitle);

            document.add(dataTable);
        }
        catch (DocumentException d) {
            throw new RuntimeException(d);
        }
        document.close();
    }

    protected void addHeader(PdfPTable dataTable) {
        String sortOrderSubtitle = this.kualiConfigurationService.getPropertyValueAsString(PdpKeyConstants.DAILY_REPORT_SERVICE_SORT_ORDER_SUBTITLE);
        sortOrderSubtitle = MessageFormat.format(sortOrderSubtitle, new Object[] { null });

        PdfPCell cell = new PdfPCell(new Phrase(sortOrderSubtitle, headerFont));
        dataTable.addCell(cell);

        String customerSubtitle = this.kualiConfigurationService.getPropertyValueAsString(PdpKeyConstants.DAILY_REPORT_SERVICE_CUSTOMER_SUBTITLE);
        customerSubtitle = MessageFormat.format(customerSubtitle, new Object[] { null });

        cell = new PdfPCell(new Phrase(customerSubtitle, headerFont));
        dataTable.addCell(cell);

        String amountOfPaymentsSubtitle = this.kualiConfigurationService.getPropertyValueAsString(PdpKeyConstants.DAILY_REPORT_SERVICE_AMOUNT_OF_PAYMENTS_SUBTITLE);
        amountOfPaymentsSubtitle = MessageFormat.format(amountOfPaymentsSubtitle, new Object[] { null });

        cell = new PdfPCell(new Phrase(amountOfPaymentsSubtitle, headerFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        dataTable.addCell(cell);

        String numberOfPaymentRecordsSubtitle = this.kualiConfigurationService.getPropertyValueAsString(PdpKeyConstants.DAILY_REPORT_SERVICE_NUMBER_OF_PAYMENT_RECORDS_SUBTITLE);
        numberOfPaymentRecordsSubtitle = MessageFormat.format(numberOfPaymentRecordsSubtitle, new Object[] { null });

        cell = new PdfPCell(new Phrase(numberOfPaymentRecordsSubtitle, headerFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        dataTable.addCell(cell);

        String numberOfPayeesSubtitle = this.kualiConfigurationService.getPropertyValueAsString(PdpKeyConstants.DAILY_REPORT_SERVICE_NUMBER_OF_PAYEES_SUBTITLE);
        numberOfPayeesSubtitle = MessageFormat.format(numberOfPayeesSubtitle, new Object[] { null });

        cell = new PdfPCell(new Phrase(numberOfPayeesSubtitle, headerFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        dataTable.addCell(cell);
    }

    protected void addRow(PdfPTable dataTable, DailyReport dr, boolean bold) {
        addRow(dataTable, dr, bold, this.paymentGroupService.getSortGroupName(this.paymentGroupService.getSortGroupId(dr.getPaymentGroup())));
    }

    protected void addRow(PdfPTable dataTable, DailyReport dr, boolean bold, String name) {
        DecimalFormat af = new DecimalFormat("###,###,##0.00");
        DecimalFormat nf = new DecimalFormat("###,##0");

        Font f = null;
        if (bold) {
            f = headerFont;

            for (int i = 0; i < 5; i++) {
                PdfPCell cell = new PdfPCell(new Phrase(" ", f));
                cell.setBorder(Rectangle.NO_BORDER);
                dataTable.addCell(cell);
            }
        }
        else {
            f = textFont;
        }

        PdfPCell cell = new PdfPCell(new Phrase(name, f));
        cell.setBorder(Rectangle.NO_BORDER);
        dataTable.addCell(cell);

        if (!bold) {
            cell = new PdfPCell(new Phrase(dr.getCustomer(), f));
        }
        else {
            cell = new PdfPCell(new Phrase("", f));
        }
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

        if (bold) {
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

    /**
     * @see org.kuali.kfs.pdp.batch.service.DailyReportService#setPaymentGroupService(org.kuali.kfs.pdp.service.PaymentGroupService)
     */
    public void setPaymentGroupService(PaymentGroupService paymentGroupService) {
        this.paymentGroupService = paymentGroupService;
    }

    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
}

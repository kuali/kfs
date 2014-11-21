/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.pdp.batch.service.impl;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.batch.service.DailyReportService;
import org.kuali.kfs.pdp.businessobject.DailyReport;
import org.kuali.kfs.pdp.dataaccess.PaymentDetailDao;
import org.kuali.kfs.pdp.service.PaymentGroupService;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DailyReportServiceImpl implements DailyReportService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DailyReportServiceImpl.class);

    protected PaymentDetailDao paymentDetailDao;
    protected DateTimeService dateTimeService;
    protected PaymentGroupService paymentGroupService;
    protected ConfigurationService kualiConfigurationService;
    protected ReportWriterService dailyReportReportWriterService;

    protected List<DailyReport> getData() {
        LOG.debug("getData() started");

        return paymentDetailDao.getDailyReportData(dateTimeService.getCurrentSqlDate());
    }

    @Override
    public void runReport() {
        LOG.debug("runReport() started");

        Collection<DailyReport> data = getData();
        Date today = dateTimeService.getCurrentDate();

        // Title and table header
        dailyReportReportWriterService.writeSubTitle(dateTimeService.toDateTimeStringForFilename(today));
        dailyReportReportWriterService.writeNewLines(1);
        dailyReportReportWriterService.writeTableHeader(DailyReport.class);

        // Objects for sort total and total
        DailyReport dailyReportSortTotal = new DailyReport();
        DailyReport dailyReportTotal = new DailyReport();
        dailyReportTotal.setSortOrder(this.kualiConfigurationService.getPropertyValueAsString(PdpKeyConstants.DAILY_REPORT_SERVICE_TOTAL_SUBTITLE));

        final String DAILY_REPORT_SORT_TOTAL_PROPERTY_STRING = this.kualiConfigurationService.getPropertyValueAsString(PdpKeyConstants.DAILY_REPORT_SERVICE_TOTAL_FOR_SUBTITLE);

        // first is the first entry (ever) we are writing. firstSortTotal is the first we are writing after sortTotal  was written
        boolean first = true;
        boolean firstSortTotal = true;

        for (DailyReport dailyReport : data) {
            if (!first
                    && this.paymentGroupService.getSortGroupId(dailyReportSortTotal.getPaymentGroup()) != this.paymentGroupService.getSortGroupId(dailyReport.getPaymentGroup())) {
                // Clause is for if the sort group changed which means we print a sortTotal. Unless this is the first element we print, then this doesn't apply

                firstSortTotal = true;

                // write sortTotal row (sub title)
                dailyReportReportWriterService.writeTableRow(dailyReportSortTotal);
                dailyReportReportWriterService.writeNewLines(1);

                // reset subtotal
                dailyReportSortTotal = new DailyReport();
            } else if (first) {
                first = false;
            }

            if (firstSortTotal) {
                // Set the sortOrder (aka "Title" of the row) since this is the firstSortTotal
                dailyReport.setSortOrder(this.paymentGroupService.getSortGroupName(this.paymentGroupService.getSortGroupId(dailyReport.getPaymentGroup())));
                firstSortTotal = false;
            }

            // We always write the row
            dailyReportReportWriterService.writeTableRow(dailyReport);

            // Do the summation
            dailyReportSortTotal.addRow(dailyReport);
            dailyReportTotal.addRow(dailyReport);

            // Set the sortTotal title if it hasn't been set yet. There are two scenarios this happens for: Only 1 element in list or we just reset dailyReportSortTotal
            if (StringUtils.isEmpty(dailyReportSortTotal.getSortOrder())) {
                String newTotalForSubtitle = MessageFormat.format(DAILY_REPORT_SORT_TOTAL_PROPERTY_STRING, new Object[] { this.paymentGroupService.getSortGroupName(this.paymentGroupService.getSortGroupId(dailyReportSortTotal.getPaymentGroup())) });
                dailyReportSortTotal.setSortOrder(newTotalForSubtitle);
            }
        }

        // At the end we still have one sortTotal to write. Unless there was no data
        if (!first) {
            // Final subtotal to write if there was one
            dailyReportReportWriterService.writeTableRow(dailyReportSortTotal);
            dailyReportReportWriterService.writeNewLines(1);
        }

        // Write the final total
        dailyReportReportWriterService.writeTableRowSeparationLine(dailyReportTotal);
        dailyReportReportWriterService.writeTableRow(dailyReportTotal);
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
    @Override
    public void setPaymentGroupService(PaymentGroupService paymentGroupService) {
        this.paymentGroupService = paymentGroupService;
    }

    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public ReportWriterService getDailyReportReportWriterService() {
        return dailyReportReportWriterService;
    }

    public void setDailyReportReportWriterService(ReportWriterService dailyReportReportWriterService) {
        this.dailyReportReportWriterService = dailyReportReportWriterService;
    }
}

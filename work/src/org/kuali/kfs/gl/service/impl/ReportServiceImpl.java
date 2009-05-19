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
package org.kuali.kfs.gl.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.gl.report.BalanceEncumbranceReport;
import org.kuali.kfs.gl.report.BalanceReport;
import org.kuali.kfs.gl.report.YearEndTransactionReport;
import org.kuali.kfs.gl.service.BalanceService;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.gl.service.OriginEntryService;
import org.kuali.kfs.gl.service.ReportService;
import org.kuali.kfs.gl.service.ReversalService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.PersistenceService;

import com.lowagie.text.Document;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

/**
 * The base implementation of ReportService
 */
public class ReportServiceImpl implements ReportService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReportServiceImpl.class);

    String reportsDirectory;
    private OriginEntryService originEntryService;
    private OriginEntryGroupService originEntryGroupService;
    private DateTimeService dateTimeService;
    private BalanceService balanceService;
    private OptionsService optionsService;
    private ReversalService reversalService;
    private KualiConfigurationService kualiConfigurationService;
    private PersistenceService persistenceService;

    /**
     * Constructs a ReportServiceImpl instance
     */
    public ReportServiceImpl() {
        super();
    }

    /**
     * initializes this service
     */
    public void init() {
        reportsDirectory = kualiConfigurationService.getPropertyString(KFSConstants.REPORTS_DIRECTORY_KEY);
    }

    /**
     * Generates the GL Summary report
     * 
     * @param runDate the run date of the poster service that should be reported
     * @param options the options of the fiscal year the poster was run
     * @param reportType the type of the report that should be generated
     * @see org.kuali.kfs.gl.service.ReportService#generateGlSummary(java.util.Date, int, java.util.List)
     */
    public void generateGlSummary(Date runDate, SystemOptions year, String reportType) {
        LOG.debug("generateGlSummary() started");

        List balanceTypeCodes = new ArrayList();
        if ("act".equals(reportType)) {
            balanceTypeCodes.add(year.getActualFinancialBalanceTypeCd());
        }
        else {
            balanceTypeCodes.add(year.getBudgetCheckingBalanceTypeCd());
            balanceTypeCodes.add(year.getBaseBudgetFinancialBalanceTypeCd());
            balanceTypeCodes.add(year.getMonthlyBudgetFinancialBalanceTypeCd());
        }

        List balances = balanceService.getGlSummary(year.getUniversityFiscalYear(), balanceTypeCodes);

        BalanceReport rept = new BalanceReport();
        rept.generateReport(runDate, balances, year.getUniversityFiscalYearName(), balanceTypeCodes, "glsummary_" + year.getUniversityFiscalYear() + "_" + reportType, reportsDirectory);
    }

    /**
     * Generates GL Encumbrance Summary report
     * 
     * @param runDate the run date of the poster service that should be reported
     * @param options the options of the fiscal year the poster was run
     * @param reportType the type of the report that should be generated
     * @see org.kuali.kfs.gl.service.ReportService#generateGlEncumbranceSummary(java.util.Date, int, java.util.List,
     *      java.lang.String)
     */
    public void generateGlEncumbranceSummary(Date runDate, SystemOptions year, String reportType) {
        LOG.debug("generateGlEncumbranceSummary() started");

        List balanceTypeCodes = new ArrayList();
        balanceTypeCodes.add(year.getExtrnlEncumFinBalanceTypCd());
        balanceTypeCodes.add(year.getIntrnlEncumFinBalanceTypCd());
        balanceTypeCodes.add(year.getPreencumbranceFinBalTypeCd());
        balanceTypeCodes.add(year.getCostShareEncumbranceBalanceTypeCd());

        List balances = balanceService.getGlSummary(year.getUniversityFiscalYear(), balanceTypeCodes);

        BalanceEncumbranceReport rept = new BalanceEncumbranceReport();
        rept.generateReport(runDate, balances, year.getUniversityFiscalYearName(), balanceTypeCodes, "glsummary_" + year.getUniversityFiscalYear() + "_" + reportType, reportsDirectory);
    }

    /**
     * Generates the Balance Forward Year-End job Report
     * 
     * @param reportSummary a List of summarized statistics to report
     * @param runDate the date of the balance forward run
     * @param openAccountOriginEntryGroup the origin entry group with balance forwarding origin entries with open accounts
     * @param closedAccountOriginEntryGroup the origin entry group with balance forwarding origin entries with closed accounts
     * @see org.kuali.kfs.gl.service.ReportService#generateBalanceForwardStatisticsReport(java.util.List, java.util.Date)
     */
    public void generateBalanceForwardStatisticsReport(List reportSummary, Date runDate, OriginEntryGroup openAccountOriginEntryGroup, OriginEntryGroup closedAccountOriginEntryGroup) {
        LOG.debug("generateBalanceForwardStatisticsReport() started");

        YearEndTransactionReport transactionReport = new YearEndTransactionReport(YearEndTransactionReport.YearEndReportType.FORWARD_BALANCES_REPORT);
        String title = "Balance Forward Report ";
        transactionReport.generateReport(new HashMap(), new HashMap(), reportSummary, runDate, title, "year_end_balance_forward", reportsDirectory, new Object[] { new Object[] { openAccountOriginEntryGroup, "Open Account Balance Forwards Statistics" }, new Object[] { closedAccountOriginEntryGroup, "Closed Account Balance Fowards Statistics" } });
    }

    /**
     * Generates the encumbrance foward year end job report
     * 
     * @param jobParameters the parameters that were used by the encumbrance forward job
     * @param reportSummary a List of summarized statistics to report
     * @param runDate the date of the encumbrance forward run
     * @param originEntryGroup the origin entry group that the job placed encumbrance forwarding origin entries into
     * @see org.kuali.kfs.gl.service.ReportService#generateEncumbranceClosingStatisticsReport(java.util.List, java.util.Date)
     */
    public void generateEncumbranceClosingStatisticsReport(Map jobParameters, List reportSummary, Date runDate, OriginEntryGroup originEntryGroup) {
        LOG.debug("generateEncumbranceForwardStatisticsReport() started");

        YearEndTransactionReport transactionReport = new YearEndTransactionReport(YearEndTransactionReport.YearEndReportType.FORWARD_ENCUMBERANCES_REPORT);
        String title = "Encumbrance Closing Report ";
        transactionReport.generateReport(jobParameters, new HashMap(), reportSummary, runDate, title, "year_end_encumbrance_closing", reportsDirectory, new Object[] { new Object[] { originEntryGroup, "Encumbrance Forwards Statistics" } });
    }

    /**
     * Generates the Nominal Activity Closing Report
     * 
     * @param jobParameters the parameters that were used by the nominal activity closing job
     * @param reportSummary a List of summarized statistics to report
     * @param runDate the date of the nominal activity closing job run
     * @param originEntryGroup the origin entry group that the job placed nominal activity closing origin entries into
     * @see org.kuali.kfs.gl.service.ReportService#generateNominalActivityClosingStatisticsReport(java.util.Map, java.util.List,
     *      java.util.Date)
     */
    public void generateNominalActivityClosingStatisticsReport(Map jobParameters, List reportSummary, Date runDate, OriginEntryGroup originEntryGroup) {
        LOG.debug("generateNominalActivityClosingStatisticsReport() started");

        YearEndTransactionReport transactionReport = new YearEndTransactionReport(YearEndTransactionReport.YearEndReportType.NOMINAL_ACTIVITY_CLOSE_REPORT);
        String title = "Nominal Activity Closing Report ";
        transactionReport.generateReport(jobParameters, null, reportSummary, runDate, title, "year_end_nominal_activity_closing", reportsDirectory, new Object[] { new Object[] { originEntryGroup, "Nominal Activity Closing Statistics" } });
    }

    /**
     * This method generates the statistics report of the organization reversion process.
     * 
     * @param jobParameters the parameters the org reversion process was run with
     * @param reportSummary a list of various counts the job went through
     * @param runDate the date the report was run
     * @param orgReversionOriginEntryGroup the origin entry group that contains the reversion origin entries
     * @see org.kuali.kfs.gl.service.ReportService#generateOrgReversionStatisticsReport(java.util.Map, java.util.List,
     *      java.util.Date, org.kuali.kfs.gl.businessobject.OriginEntryGroup)
     */
    public void generateOrgReversionStatisticsReport(Map jobParameters, List reportSummary, Date runDate, OriginEntryGroup orgReversionOriginEntryGroup) {
        LOG.debug("generateOrgReversionStatisticsReport() started");

        YearEndTransactionReport transactionReport = new YearEndTransactionReport(YearEndTransactionReport.YearEndReportType.ORGANIZATION_REVERSION_PROCESS_REPORT);
        String title = "Organization Reversion Process Report ";
        transactionReport.generateReport(jobParameters, null, reportSummary, runDate, title, "year_end_org_reversion_process", reportsDirectory, new Object[] { new Object[] { orgReversionOriginEntryGroup, "Organization Reversion Statistics" } });
    }

    /**
     * A class that helps format a PDF document
     */
    class SfPageHelper extends PdfPageEventHelper {
        public Date runDate;
        public Font headerFont;
        public String title;

        /**
         * Writes information to the last page in the document
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

    public void setOriginEntryService(OriginEntryService originEntryService) {
        this.originEntryService = originEntryService;
    }

    public void setBalanceService(BalanceService bs) {
        balanceService = bs;
    }

    public void setOptionsService(OptionsService os) {
        optionsService = os;
    }

    public void setDateTimeService(DateTimeService dts) {
        dateTimeService = dts;
    }

    public void setKualiConfigurationService(KualiConfigurationService kcs) {
        kualiConfigurationService = kcs;
    }

    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }

    public void setReversalService(ReversalService rs) {
        reversalService = rs;
    }

    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }
}

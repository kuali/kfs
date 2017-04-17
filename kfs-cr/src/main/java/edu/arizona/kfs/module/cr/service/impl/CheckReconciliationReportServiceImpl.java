package edu.arizona.kfs.module.cr.service.impl;

import java.io.ByteArrayOutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRParameter;

import org.apache.commons.io.IOUtils;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.service.ReportGenerationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kns.util.WebUtils;

import edu.arizona.kfs.module.cr.CrConstants;
import edu.arizona.kfs.module.cr.CrPropertyConstants;
import edu.arizona.kfs.module.cr.businessobject.CheckReconciliation;
import edu.arizona.kfs.module.cr.businessobject.CheckReconciliationReportLine;
import edu.arizona.kfs.module.cr.dataaccess.CheckReconciliationDao;
import edu.arizona.kfs.module.cr.service.CheckReconciliationReportService;
import edu.arizona.kfs.sys.KFSConstants;

/**
 * Check Reconciliation Report Service Implementation
 * 
 * Deprecation: Eclipse recognizes WebUtils as deprecated
 */
@SuppressWarnings("deprecation")
public class CheckReconciliationReportServiceImpl implements CheckReconciliationReportService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CheckReconciliationReportService.class);

    // Injected Objects

    private CheckReconciliationDao checkReconciliationDao;
    private DateTimeService dateTimeService;
    private ReportGenerationService reportGenerationService;

    // Spring Injection

    public void setCheckReconciliationDao(CheckReconciliationDao checkReconciliationDao) {
        this.checkReconciliationDao = checkReconciliationDao;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setReportGenerationService(ReportGenerationService reportGenerationService) {
        this.reportGenerationService = reportGenerationService;
    }

    // Public Service methods

    @Override
    public List<CheckReconciliationReportLine> generateReportSet(Date reportEndDate) {
        LOG.info("Generating Report Set.");

        Set<String> statusCodes = new HashSet<String>();
        statusCodes.add(CrConstants.CheckReconciliationStatusCodes.EXCP);
        List<CheckReconciliation> list = checkReconciliationDao.getAllCheckReconciliationForSearchCriteria(null, reportEndDate, statusCodes);

        List<CheckReconciliationReportLine> data = new ArrayList<CheckReconciliationReportLine>();
        for (CheckReconciliation cr : list) {
            data.add(new CheckReconciliationReportLine(cr));
        }

        LOG.info("Items found for report: " + data.size());
        return data;

    }

    @Override
    public void generateCsvReport(HttpServletResponse response, Date reportEndDate, Collection<CheckReconciliationReportLine> reportSet) throws Exception {
        LOG.info("Generating CSV Report.");
        prepareResponseHeadersForCsv(response);
        java.io.PrintWriter out = null;
        try {
            out = response.getWriter();

            boolean startReport = true;
            boolean newAccount = false;
            boolean newMonth = false;
            String currentAccount = KFSConstants.EMPTY_STRING;
            double accountTotal = 0.00;
            int accountCount = 0;
            String currentMonth = KFSConstants.EMPTY_STRING;
            double monthTotal = 0.00;
            int monthCount = 0;
            double overallTotal = 0.00;
            int overallCount = 0;

            String reportTitleLine = ",,As of " + CrConstants.MM_DD_YYYY.format(getReportAsOfDate(reportEndDate)) + ",,";
            String reportColumnHeader = "Check No,Payee Id,Payee Name,Account No,Date,Amount";

            for (CheckReconciliationReportLine reportLine : reportSet) {
                if (startReport) {
                    out.println(reportTitleLine);
                    out.println(reportColumnHeader);
                    currentAccount = reportLine.getBankAccountNumber();
                    currentMonth = reportLine.getCheckMonth();
                    startReport = false;
                }

                newMonth = !currentMonth.equals(reportLine.getCheckMonth());
                newAccount = !currentAccount.equals(reportLine.getBankAccountNumber());
                if (newMonth) {
                    out.println("Sub-total: " + currentMonth + KFSConstants.COMMA + KFSConstants.COMMA + "Outstanding: " + monthCount + KFSConstants.COMMA + KFSConstants.COMMA + KFSConstants.COMMA + CrConstants.REPORT_DECIMAL_FORMAT.format(monthTotal));
                    monthTotal = 0.00;
                    monthCount = 0;
                    currentMonth = reportLine.getCheckMonth();
                }
                if (newAccount) {
                    out.println("Bank: " + currentAccount + KFSConstants.COMMA + KFSConstants.COMMA + "Total Outstanding: " + accountCount + KFSConstants.COMMA + KFSConstants.COMMA + KFSConstants.COMMA + CrConstants.REPORT_DECIMAL_FORMAT.format(accountTotal));
                    out.println(",,,,");
                    monthTotal = 0.00;
                    accountTotal = 0.00;
                    currentMonth = reportLine.getCheckMonth();
                    monthCount = 0;
                    accountCount = 0;
                    currentAccount = reportLine.getBankAccountNumber();
                }
                if (newMonth || newAccount) {
                    out.println(reportColumnHeader);
                }

                out.println(reportLine.getCheckNumber() + KFSConstants.COMMA + reportLine.getPayeeId() + " - " + reportLine.getPayeeTypeCode() + KFSConstants.COMMA + "\"" + reportLine.getPayeeName() + "\"" + KFSConstants.COMMA + reportLine.getBankAccountNumber() + KFSConstants.COMMA + reportLine.getCheckDate() + KFSConstants.COMMA + "\"" + CrConstants.REPORT_DECIMAL_FORMAT.format(reportLine.getAmount().doubleValue()) + "\"");
                monthTotal += reportLine.getAmount();
                monthCount++;
                accountTotal += reportLine.getAmount();
                accountCount++;
                overallTotal += reportLine.getAmount();
                overallCount++;
            }

            out.println("Sub-total: " + currentMonth + KFSConstants.COMMA + KFSConstants.COMMA + "Outstanding: " + monthCount + KFSConstants.COMMA + KFSConstants.COMMA + KFSConstants.COMMA + CrConstants.REPORT_DECIMAL_FORMAT.format(monthTotal));
            out.println("Bank: " + currentAccount + KFSConstants.COMMA + KFSConstants.COMMA + "Total Outstanding: " + accountCount + KFSConstants.COMMA + KFSConstants.COMMA + KFSConstants.COMMA + CrConstants.REPORT_DECIMAL_FORMAT.format(accountTotal));

            LOG.info("CSV Report complete. " + reportSet.size() + " total records. Outstanding: " + overallCount + ", Total:" + CrConstants.REPORT_DECIMAL_FORMAT.format(overallTotal));
        } catch (Exception e) {
            LOG.error("Error generating CSV report: " + e.getMessage());
            throw e;
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    @Override
    public void generatePdfReport(HttpServletResponse response, Date reportEndDate, Collection<CheckReconciliationReportLine> reportSet) throws Exception {
        LOG.info("Generating PDF Report. " + reportSet.size() + " total records.");
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();

            ResourceBundle resourceBundle = ResourceBundle.getBundle(CrConstants.REPORT_MESSAGES_CLASSPATH, Locale.getDefault());
            String date = CrConstants.MM_DD_YYYY.format(getReportAsOfDate(reportEndDate));
            Map<String, Object> reportData = new HashMap<String, Object>();
            reportData.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundle);
            reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_DIR, KFSConstants.EMPTY_STRING);
            reportData.put(CrPropertyConstants.CheckReconciliationReport.END_DATE, date);

            reportGenerationService.generateReportToOutputStream(reportData, reportSet, CrConstants.REPORT_TEMPLATE_CLASSPATH, baos);
            WebUtils.saveMimeOutputStreamAsFile(response, KFSConstants.ReportGeneration.PDF_MIME_TYPE, baos, CrConstants.CR_REPORT_FILE_NAME + KFSConstants.ReportGeneration.PDF_FILE_EXTENSION);

            LOG.info("PDF Report complete. " + reportSet.size() + " total records.");
        } catch (Exception e) {
            LOG.error("Error generating PDF report: " + e.getMessage());
            throw e;
        } finally {
            IOUtils.closeQuietly(baos);
        }
    }

    // Private methods

    private java.util.Date getReportAsOfDate(Date reportEndDate) {
        if (reportEndDate != null) {
            return reportEndDate;
        } else {
            return dateTimeService.getCurrentDate();
        }
    }

    private void prepareResponseHeadersForCsv(HttpServletResponse response) {
        response.setContentType(KFSConstants.ReportGeneration.CSV_MIME_TYPE);
        response.setHeader(KFSConstants.HttpHeaderResponse.PRAGMA, "No-cache");
        response.setHeader(KFSConstants.HttpHeaderResponse.CACHE_CONTROL, "no-cache");
        response.setHeader(KFSConstants.HttpHeaderResponse.CONTENT_DIPOSITION, "attachment; filename=" + CrConstants.CR_REPORT_FILE_NAME + KFSConstants.ReportGeneration.CSV_FILE_EXTENSION);
        response.setHeader(KFSConstants.HttpHeaderResponse.EXPIRES, "0");
    }

}

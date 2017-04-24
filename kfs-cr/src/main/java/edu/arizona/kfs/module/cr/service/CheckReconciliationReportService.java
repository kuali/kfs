package edu.arizona.kfs.module.cr.service;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import edu.arizona.kfs.module.cr.businessobject.CheckReconciliationReportLine;

/**
 * Check Reconciliation Report Service
 */
public interface CheckReconciliationReportService {

    /**
     * Generates the data to be reported.
     */
    public List<CheckReconciliationReportLine> generateReportSet(Date reportEndDate);

    /**
     * Generates the CSV Report for the given data, and delivers it to the end user via the reponse object.
     */
    public void generateCsvReport(HttpServletResponse response, Date reportEndDate, Collection<CheckReconciliationReportLine> reportSet) throws Exception;

    /**
     * Generates the PDF Report for the given data, and delivers it to the end user via the reponse object.
     */
    public void generatePdfReport(HttpServletResponse response, Date reportEndDate, Collection<CheckReconciliationReportLine> reportSet) throws Exception;

}

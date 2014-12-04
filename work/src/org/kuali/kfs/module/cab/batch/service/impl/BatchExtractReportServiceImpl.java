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
package org.kuali.kfs.module.cab.batch.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import net.sf.jasperreports.engine.JRParameter;

import org.kuali.kfs.module.cab.batch.ExtractProcessLog;
import org.kuali.kfs.module.cab.batch.service.BatchExtractReportService;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.kfs.sys.service.ReportGenerationService;
import org.kuali.rice.core.api.datetime.DateTimeService;

public class BatchExtractReportServiceImpl implements BatchExtractReportService {
    protected ReportGenerationService reportGenerationService;
    protected ReportInfo cabBatchStatusReportInfo;
    protected ReportInfo cabBatchMismatchReportInfo;

    protected DateTimeService dateTimeService;

    /**
     * @see org.kuali.kfs.module.cab.batch.service.BatchExtractReportService#generateStatusReportPDF(org.kuali.kfs.module.cab.batch.ExtractProcessLog)
     */
    public File generateStatusReportPDF(ExtractProcessLog extractProcessLog) {
        String reportFileName = cabBatchStatusReportInfo.getReportFileName();
        String reportDirectoty = cabBatchStatusReportInfo.getReportsDirectory();
        String reportTemplateClassPath = cabBatchStatusReportInfo.getReportTemplateClassPath();
        String reportTemplateName = cabBatchStatusReportInfo.getReportTemplateName();
        ResourceBundle resourceBundle = cabBatchStatusReportInfo.getResourceBundle();
        String subReportTemplateClassPath = cabBatchStatusReportInfo.getSubReportTemplateClassPath();
        Map<String, String> subReports = cabBatchStatusReportInfo.getSubReports();
        Map<String, Object> reportData = new HashMap<String, Object>();
        reportData.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundle);
        reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_DIR, subReportTemplateClassPath);
        reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_TEMPLATE_NAME, subReports);
        String template = reportTemplateClassPath + reportTemplateName;
        String fullReportFileName = reportGenerationService.buildFullFileName(dateTimeService.getCurrentDate(), reportDirectoty, reportFileName, "");
        List<ExtractProcessLog> dataSource = new ArrayList<ExtractProcessLog>();
        dataSource.add(extractProcessLog);
        reportGenerationService.generateReportToPdfFile(reportData, dataSource, template, fullReportFileName);
        return new File(fullReportFileName + ".pdf");
    }

    /**
     * @see org.kuali.kfs.module.cab.batch.service.BatchExtractReportService#generateMismatchReportPDF(org.kuali.kfs.module.cab.batch.ExtractProcessLog)
     */
    public File generateMismatchReportPDF(ExtractProcessLog extractProcessLog) {
        String reportFileName = cabBatchMismatchReportInfo.getReportFileName();
        String reportDirectoty = cabBatchMismatchReportInfo.getReportsDirectory();
        String reportTemplateClassPath = cabBatchMismatchReportInfo.getReportTemplateClassPath();
        String reportTemplateName = cabBatchMismatchReportInfo.getReportTemplateName();
        ResourceBundle resourceBundle = cabBatchMismatchReportInfo.getResourceBundle();
        String subReportTemplateClassPath = cabBatchMismatchReportInfo.getSubReportTemplateClassPath();
        Map<String, String> subReports = cabBatchMismatchReportInfo.getSubReports();
        Map<String, Object> reportData = new HashMap<String, Object>();
        reportData.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundle);
        reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_DIR, subReportTemplateClassPath);
        reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_TEMPLATE_NAME, subReports);
        String template = reportTemplateClassPath + reportTemplateName;
        String fullReportFileName = reportGenerationService.buildFullFileName(dateTimeService.getCurrentDate(), reportDirectoty, reportFileName, "");
        List<ExtractProcessLog> dataSource = new ArrayList<ExtractProcessLog>();
        dataSource.add(extractProcessLog);
        reportGenerationService.generateReportToPdfFile(reportData, dataSource, template, fullReportFileName);
        return new File(fullReportFileName + ".pdf");
    }

    /**
     * Gets the reportGenerationService attribute.
     * 
     * @return Returns the reportGenerationService.
     */
    public ReportGenerationService getReportGenerationService() {
        return reportGenerationService;
    }

    /**
     * Sets the reportGenerationService attribute value.
     * 
     * @param reportGenerationService The reportGenerationService to set.
     */
    public void setReportGenerationService(ReportGenerationService reportGenerationService) {
        this.reportGenerationService = reportGenerationService;
    }

    /**
     * Gets the cabBatchStatusReportInfo attribute.
     * 
     * @return Returns the cabBatchStatusReportInfo.
     */
    public ReportInfo getCabBatchStatusReportInfo() {
        return cabBatchStatusReportInfo;
    }

    /**
     * Sets the cabBatchStatusReportInfo attribute value.
     * 
     * @param cabBatchStatusReportInfo The cabBatchStatusReportInfo to set.
     */
    public void setCabBatchStatusReportInfo(ReportInfo cabBatchStatusReportInfo) {
        this.cabBatchStatusReportInfo = cabBatchStatusReportInfo;
    }


    /**
     * Gets the dateTimeService attribute.
     * 
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Gets the cabBatchMismatchReportInfo attribute.
     * 
     * @return Returns the cabBatchMismatchReportInfo.
     */
    public ReportInfo getCabBatchMismatchReportInfo() {
        return cabBatchMismatchReportInfo;
    }

    /**
     * Sets the cabBatchMismatchReportInfo attribute value.
     * 
     * @param cabBatchMismatchReportInfo The cabBatchMismatchReportInfo to set.
     */
    public void setCabBatchMismatchReportInfo(ReportInfo cabBatchMismatchReportInfo) {
        this.cabBatchMismatchReportInfo = cabBatchMismatchReportInfo;
    }
}

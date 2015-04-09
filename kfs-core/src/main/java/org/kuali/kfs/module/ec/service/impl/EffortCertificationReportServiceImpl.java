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
package org.kuali.kfs.module.ec.service.impl;

import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;

import net.sf.jasperreports.engine.JRParameter;

import org.kuali.kfs.module.ec.service.EffortCertificationReportService;
import org.kuali.kfs.module.ec.util.ExtractProcessReportDataHolder;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.kfs.sys.service.ReportGenerationService;
import org.springframework.transaction.annotation.Transactional;

/**
 * To generate the working progress reports for the effort certification
 */
@Transactional
public class EffortCertificationReportServiceImpl implements EffortCertificationReportService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortCertificationReportServiceImpl.class);

    private ReportGenerationService reportGenerationService;
    private ReportInfo effortExtractProcessReportInfo;

    /**
     * @see org.kuali.kfs.module.ec.service.EffortCertificationReportService#generateReportForExtractProcess(org.kuali.kfs.module.ec.util.ExtractProcessReportDataHolder, java.util.Date)
     */
    public void generateReportForExtractProcess(ExtractProcessReportDataHolder reportDataHolder, Date runDate) {
        String reportFileName = effortExtractProcessReportInfo.getReportFileName();
        String reportDirectory = effortExtractProcessReportInfo.getReportsDirectory();
        String reportTemplateClassPath = effortExtractProcessReportInfo.getReportTemplateClassPath();
        String reportTemplateName = effortExtractProcessReportInfo.getReportTemplateName();
        ResourceBundle resourceBundle = effortExtractProcessReportInfo.getResourceBundle();
        String subReportTemplateClassPath = effortExtractProcessReportInfo.getSubReportTemplateClassPath();
        Map<String, String> subReports = effortExtractProcessReportInfo.getSubReports();

        Map<String, Object> reportData = reportDataHolder.getReportData();
        reportData.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundle);
        reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_DIR, subReportTemplateClassPath);
        reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_TEMPLATE_NAME, subReports);

        String template = reportTemplateClassPath + reportTemplateName;
        String fullReportFileName = reportGenerationService.buildFullFileName(runDate, reportDirectory, reportFileName, "");
        reportGenerationService.generateReportToPdfFile(reportData, template, fullReportFileName);
    }
    
    /**
     * Sets the effortExtractProcessReportInfo attribute value.
     * 
     * @param effortExtractProcessReportInfo The effortExtractProcessReportInfo to set.
     */
    public void setEffortExtractProcessReportInfo(ReportInfo effortExtractProcessReportInfo) {
        this.effortExtractProcessReportInfo = effortExtractProcessReportInfo;
    }

    /**
     * Sets the reportGenerationService attribute value.
     * 
     * @param reportGenerationService The reportGenerationService to set.
     */
    public void setReportGenerationService(ReportGenerationService reportGenerationService) {
        this.reportGenerationService = reportGenerationService;
    }
}

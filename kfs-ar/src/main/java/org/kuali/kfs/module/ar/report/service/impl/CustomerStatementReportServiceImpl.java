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
package org.kuali.kfs.module.ar.report.service.impl;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;

import net.sf.jasperreports.engine.JRParameter;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.report.service.CustomerStatementReportService;
import org.kuali.kfs.module.ar.report.util.CustomerStatementReportDataHolder;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.kfs.sys.service.ReportGenerationService;
import org.springframework.transaction.annotation.Transactional;

/**
 * To generate the working progress reports for the effort certification
 */
@Transactional
public class CustomerStatementReportServiceImpl implements CustomerStatementReportService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerStatementReportServiceImpl.class);

    private ReportGenerationService reportGenerationService;
    private ReportInfo customerStatementReportInfo;
    private ReportInfo customerDetailStatementReportInfo;

    /**
     * @see org.kuali.module.effort.service.EffortCertificationReportService#generateReportForExtractProcess(org.kuali.module.effort.util.ExtractProcessReportDataHolder, java.util.Date)
     */
    public File generateReport(CustomerStatementReportDataHolder reportDataHolder, Date runDate, String statementFormat) {
        String reportFileName = customerStatementReportInfo.getReportFileName();
        String reportDirectory = customerStatementReportInfo.getReportsDirectory();
        String reportTemplateClassPath = customerStatementReportInfo.getReportTemplateClassPath();
        ResourceBundle resourceBundle = customerStatementReportInfo.getResourceBundle();
        String subReportTemplateClassPath = customerStatementReportInfo.getSubReportTemplateClassPath();
        String reportTemplateName = "";
        Map<String, String> subReports = null;
        if (statementFormat.equalsIgnoreCase(ArConstants.STATEMENT_FORMAT_SUMMARY)) {
            reportTemplateName = customerStatementReportInfo.getReportTemplateName();
            subReports = customerStatementReportInfo.getSubReports();
        } else {
            reportTemplateName = customerDetailStatementReportInfo.getReportTemplateName();
            subReports = customerDetailStatementReportInfo.getSubReports();
        }

        Map<String, Object> reportData = reportDataHolder.getReportData();
        reportData.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundle);
        reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_DIR, subReportTemplateClassPath);
        reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_TEMPLATE_NAME, subReports);

        String template = reportTemplateClassPath + reportTemplateName;
        String fullReportFileName = reportGenerationService.buildFullFileName(runDate, reportDirectory, reportFileName, "");
        File report = new File(fullReportFileName+".pdf");
        reportGenerationService.generateReportToPdfFile(reportData, template, fullReportFileName);
        return report;
    }
    
    /**
     * Sets the effortExtractProcessReportInfo attribute value.
     * 
     * @param effortExtractProcessReportInfo The effortExtractProcessReportInfo to set.
     */
    public void setCustomerStatementReportInfo(ReportInfo customerStatementReportInfo) {
        this.customerStatementReportInfo = customerStatementReportInfo;
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
     * Sets the customerDetailStatementReportInfo attribute value
     * 
     * @param customerDetailStatementReportInfo 
     */
    public void setCustomerDetailStatementReportInfo(ReportInfo customerDetailStatementReportInfo) {
        this.customerDetailStatementReportInfo = customerDetailStatementReportInfo;
    }
    
}

/*
 * Copyright 2007-2008 The Kuali Foundation
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

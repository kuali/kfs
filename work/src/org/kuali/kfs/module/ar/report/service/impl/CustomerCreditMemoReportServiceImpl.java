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
import org.kuali.kfs.module.ar.report.service.CustomerCreditMemoReportService;
import org.kuali.kfs.module.ar.report.util.CustomerCreditMemoReportDataHolder;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.kfs.sys.service.ReportGenerationService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.springframework.transaction.annotation.Transactional;

/**
 * To generate the working progress reports for the effort certification
 */
@Transactional
public class CustomerCreditMemoReportServiceImpl implements CustomerCreditMemoReportService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerCreditMemoReportServiceImpl.class);

    private ReportGenerationService reportGenerationService;
    private ParameterService parameterService;
    private ReportInfo customerCreditMemoReportInfo;
    private ReportInfo customerCreditMemoReportInfoNoTax;

    /**
     * @see org.kuali.module.effort.service.EffortCertificationReportService#generateReportForExtractProcess(org.kuali.module.effort.util.ExtractProcessReportDataHolder, java.util.Date)
     */
    public File generateReport(CustomerCreditMemoReportDataHolder reportDataHolder, Date runDate) {
        String reportFileName;
        String reportDirectory;
        String reportTemplateClassPath;
        String reportTemplateName;
        ResourceBundle resourceBundle;
        String subReportTemplateClassPath;
        Map<String, String> subReports; 
        
        if (parameterService.getParameterValueAsBoolean("KFS-AR", "Document", ArConstants.ENABLE_SALES_TAX_IND)) {
            reportFileName = customerCreditMemoReportInfo.getReportFileName();
            reportDirectory = customerCreditMemoReportInfo.getReportsDirectory();
            reportTemplateClassPath = customerCreditMemoReportInfo.getReportTemplateClassPath();
            reportTemplateName = customerCreditMemoReportInfo.getReportTemplateName();
            resourceBundle = customerCreditMemoReportInfo.getResourceBundle();
            subReportTemplateClassPath = customerCreditMemoReportInfo.getSubReportTemplateClassPath();
            subReports = customerCreditMemoReportInfo.getSubReports();
        } else { // no sales tax
            reportFileName = customerCreditMemoReportInfoNoTax.getReportFileName();
            reportDirectory = customerCreditMemoReportInfoNoTax.getReportsDirectory();
            reportTemplateClassPath = customerCreditMemoReportInfoNoTax.getReportTemplateClassPath();
            reportTemplateName = customerCreditMemoReportInfoNoTax.getReportTemplateName();
            resourceBundle = customerCreditMemoReportInfoNoTax.getResourceBundle();
            subReportTemplateClassPath = customerCreditMemoReportInfoNoTax.getSubReportTemplateClassPath();
            subReports = customerCreditMemoReportInfoNoTax.getSubReports();
            
        }
        

        Map<String, Object> reportData = reportDataHolder.getReportData();
        reportData.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundle);
        reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_DIR, subReportTemplateClassPath);
        reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_TEMPLATE_NAME, subReports);

        String template = reportTemplateClassPath + reportTemplateName;
        String fullReportFileName = reportGenerationService.buildFullFileName(runDate, reportDirectory, reportFileName, "");
        reportGenerationService.generateReportToPdfFile(reportData, template, fullReportFileName);
        File report = new File(fullReportFileName+".pdf");
        return report;
    }
    
    /**
     * Sets the effortExtractProcessReportInfo attribute value.
     * 
     * @param effortExtractProcessReportInfo The effortExtractProcessReportInfo to set.
     */
    public void setCustomerCreditMemoReportInfo(ReportInfo customerCreditMemoReportInfo) {
        this.customerCreditMemoReportInfo = customerCreditMemoReportInfo;
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
     * Gets the customerCreditMemoReportInfoNoTax attribute. 
     * @return Returns the customerCreditMemoReportInfoNoTax.
     */
    public ReportInfo getCustomerCreditMemoReportInfoNoTax() {
        return customerCreditMemoReportInfoNoTax;
    }

    /**
     * Sets the customerCreditMemoReportInfoNoTax attribute value.
     * @param customerCreditMemoReportInfoNoTax The customerCreditMemoReportInfoNoTax to set.
     */
    public void setCustomerCreditMemoReportInfoNoTax(ReportInfo customerCreditMemoReportInfoNoTax) {
        this.customerCreditMemoReportInfoNoTax = customerCreditMemoReportInfoNoTax;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    
    
    
}

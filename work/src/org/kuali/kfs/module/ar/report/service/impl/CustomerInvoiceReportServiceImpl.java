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
import org.kuali.kfs.module.ar.report.service.CustomerInvoiceReportService;
import org.kuali.kfs.module.ar.report.util.CustomerInvoiceReportDataHolder;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.kfs.sys.service.ReportGenerationService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.springframework.transaction.annotation.Transactional;

/**
 * To generate the working progress reports for the effort certification
 */
@Transactional
public class CustomerInvoiceReportServiceImpl implements CustomerInvoiceReportService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerInvoiceReportServiceImpl.class);

    private ReportGenerationService reportGenerationService;
    private ParameterService parameterService;
    private ReportInfo customerInvoiceReportInfo;
    private ReportInfo customerInvoiceReportInfoNoTax;


    /**
     * @see org.kuali.module.effort.service.EffortCertificationReportService#generateReportForExtractProcess(org.kuali.module.effort.util.ExtractProcessReportDataHolder, java.util.Date)
     */
    public File generateReport(CustomerInvoiceReportDataHolder reportDataHolder, Date runDate) {

        String reportFileName;
        String reportDirectory;
        String reportTemplateClassPath;
        String reportTemplateName;
        ResourceBundle resourceBundle;
        String subReportTemplateClassPath;
        Map<String, String> subReports; 
        
        if (parameterService.getParameterValueAsBoolean("KFS-AR", "Document", ArConstants.ENABLE_SALES_TAX_IND)) {
            reportFileName = customerInvoiceReportInfo.getReportFileName();
            reportDirectory = customerInvoiceReportInfo.getReportsDirectory();
            reportTemplateClassPath = customerInvoiceReportInfo.getReportTemplateClassPath();
            reportTemplateName = customerInvoiceReportInfo.getReportTemplateName();
            resourceBundle = customerInvoiceReportInfo.getResourceBundle();
            subReportTemplateClassPath = customerInvoiceReportInfo.getSubReportTemplateClassPath();
            subReports = customerInvoiceReportInfo.getSubReports();
        } else { // no sales tax
            reportFileName = customerInvoiceReportInfoNoTax.getReportFileName();
            reportDirectory = customerInvoiceReportInfoNoTax.getReportsDirectory();
            reportTemplateClassPath = customerInvoiceReportInfoNoTax.getReportTemplateClassPath();
            reportTemplateName = customerInvoiceReportInfoNoTax.getReportTemplateName();
            resourceBundle = customerInvoiceReportInfoNoTax.getResourceBundle();
            subReportTemplateClassPath = customerInvoiceReportInfoNoTax.getSubReportTemplateClassPath();
            subReports = customerInvoiceReportInfoNoTax.getSubReports();
            
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
     * Sets the customerInvoiceReportInfo attribute value.
     * 
     * @param customerInvoiceReportInfo The customerInvoiceReportInfo to set.
     */
    public void setCustomerInvoiceReportInfo(ReportInfo customerInvoiceReportInfo) {
        this.customerInvoiceReportInfo = customerInvoiceReportInfo;
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
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Gets the customerInvoiceReportInfo attribute. 
     * @return Returns the customerInvoiceReportInfo.
     */
    public ReportInfo getCustomerInvoiceReportInfo() {
        return customerInvoiceReportInfo;
    }

    /**
     * Gets the customerInvoiceReportInfoNoTax attribute. 
     * @return Returns the customerInvoiceReportInfoNoTax.
     */
    public ReportInfo getCustomerInvoiceReportInfoNoTax() {
        return customerInvoiceReportInfoNoTax;
    }

    /**
     * Sets the customerInvoiceReportInfoNoTax attribute value.
     * @param customerInvoiceReportInfoNoTax The customerInvoiceReportInfoNoTax to set.
     */
    public void setCustomerInvoiceReportInfoNoTax(ReportInfo customerInvoiceReportInfoNoTax) {
        this.customerInvoiceReportInfoNoTax = customerInvoiceReportInfoNoTax;
    }
    
    
}

/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.gl.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import net.sf.jasperreports.engine.JRParameter;

import org.kuali.kfs.gl.dataaccess.TrialBalanceDao;
import org.kuali.kfs.gl.service.TrialBalanceService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.kfs.sys.service.ReportGenerationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is the OJB implementation of the Balance Service
 */
@Transactional
public class TrialBalanceServiceImpl implements TrialBalanceService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TrialBalanceServiceImpl.class);

    private String msuLogoPath;
    private String ebsLogoPath;

    protected TrialBalanceDao trialBalanceDao;
    protected ReportInfo glTrialBalanceReportInfo;
    protected ReportGenerationService reportGenerationService;
    protected DateTimeService dateTimeService;

    /**
     * @see edu.msu.ebsp.kfs.gl.service.TrialBalanceService#findTrialBalance(java.util.Map)
     */
    @Override
    public List findTrialBalance(String selectedFiscalYear, String chartCode) {
        if ("*".equals(chartCode)) {
            chartCode = "";
        }
        return trialBalanceDao.findBalanceByFields(selectedFiscalYear, chartCode);
    }

    /**
     * @see org.kuali.kfs.module.ec.service.EffortCertificationReportService#generateReportForExtractProcess(org.kuali.kfs.module.ec.util.ExtractProcessReportDataHolder,
     *      java.util.Date)
     */
    @Override
    public String generateReportForExtractProcess(Collection dataSource, String fiscalYear) {
        String reportFileName = glTrialBalanceReportInfo.getReportFileName();
        String reportDirectory = glTrialBalanceReportInfo.getReportsDirectory();
        String reportTemplateClassPath = glTrialBalanceReportInfo.getReportTemplateClassPath();
        String reportTemplateName = glTrialBalanceReportInfo.getReportTemplateName();
        ResourceBundle resourceBundle = glTrialBalanceReportInfo.getResourceBundle();
        String subReportTemplateClassPath = glTrialBalanceReportInfo.getSubReportTemplateClassPath();
        Map<String, String> subReports = glTrialBalanceReportInfo.getSubReports();

        Map<String, Object> reportData = new HashMap<String, Object>();
        reportData.put(KFSConstants.TRIAL_BAL_REPORT_YEAR, fiscalYear);
        reportData.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundle);
        reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_DIR, subReportTemplateClassPath);
        reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_TEMPLATE_NAME, subReports);

        String template = reportTemplateClassPath + reportTemplateName;
        String fullReportFileName = reportGenerationService.buildFullFileName(dateTimeService.getCurrentDate(), reportDirectory, reportFileName, "");
        reportGenerationService.generateReportToPdfFile(reportData, dataSource, template, fullReportFileName);

        return fullReportFileName + ".pdf";

    }

    /**
     * Gets the trialBalanceDao attribute.
     *
     * @return Returns the trialBalanceDao.
     */
    public TrialBalanceDao getTrialBalanceDao() {
        return trialBalanceDao;
    }

    /**
     * Sets the trialBalanceDao attribute value.
     *
     * @param trialBalanceDao The trialBalanceDao to set.
     */
    public void setTrialBalanceDao(TrialBalanceDao trialBalanceDao) {
        this.trialBalanceDao = trialBalanceDao;
    }

    /**
     * Gets the glTrialBalanceReportInfo attribute.
     *
     * @return Returns the glTrialBalanceReportInfo.
     */
    public ReportInfo getGlTrialBalanceReportInfo() {
        return glTrialBalanceReportInfo;
    }

    /**
     * Sets the glTrialBalanceReportInfo attribute value.
     *
     * @param glTrialBalanceReportInfo The glTrialBalanceReportInfo to set.
     */
    public void setGlTrialBalanceReportInfo(ReportInfo glTrialBalanceReportInfo) {
        this.glTrialBalanceReportInfo = glTrialBalanceReportInfo;
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

    public String getMsuLogoPath() {
        return msuLogoPath;
    }

    public void setMsuLogoPath(String msuLogoPath) {
        this.msuLogoPath = msuLogoPath;
    }

    public String getEbsLogoPath() {
        return ebsLogoPath;
    }

    public void setEbsLogoPath(String ebsLogoPath) {
        this.ebsLogoPath = ebsLogoPath;
    }


}

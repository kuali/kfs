/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.effort.service.impl;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import net.sf.jasperreports.engine.JRParameter;

import org.kuali.module.effort.report.EffortReportRegistry;
import org.kuali.module.effort.service.EffortCertificationReportService;
import org.kuali.module.effort.util.ExtractProcessReportDataHolder;
import org.kuali.module.effort.util.ReportGenerator;
import org.springframework.transaction.annotation.Transactional;

/**
 * To generate the working progress reports for the effort certification
 */
@Transactional
public class EffortCertificationReportServiceImpl implements EffortCertificationReportService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortCertificationReportServiceImpl.class);

    private String resourceBundleBaseName;

    /**
     * @see org.kuali.module.effort.service.EffortCertificationReportService#generate(org.kuali.module.effort.util.ExtractProcessReportDataHolder,
     *      org.kuali.module.effort.util.EffortReportRegistry, java.lang.String, java.util.Date)
     */
    public void generateReportForExtractProcess(ExtractProcessReportDataHolder reportDataHolder, EffortReportRegistry reportInfo, String reportsDirectory, Date runDate) {
        String template = "report/" + reportInfo.getReportTemplateName();
        String fullReportFileName = this.buildFullFileName(reportsDirectory, reportInfo.reportFilename(), runDate);

        Map<String, Object> reportData = reportDataHolder.getReportData();
        reportData.put(JRParameter.REPORT_RESOURCE_BUNDLE, this.getResourceBundle());

        LOG.info(reportDataHolder);
        // ReportGenerator.generateReportToPdfFile(reportData, template, fullReportFileName);
    }

    /**
     * build a full file name with the given information
     * 
     * @param directory the directory where the file would be located
     * @param fileName the file name
     * @param runDate the run date
     * @return a full file name built from the given information
     */
    private String buildFullFileName(String directory, String fileName, Date runDate) {
        String runtimeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(runDate);
        String fileNamePattern = "{0}/{1}_{2}";

        return MessageFormat.format(fileNamePattern, directory, fileName, runtimeStamp);
    }

    /**
     * get the resource bundle
     */
    private ResourceBundle getResourceBundle() {
        return ResourceBundle.getBundle(resourceBundleBaseName, Locale.getDefault());
    }

    /**
     * Sets the resourceBundleBaseName attribute value. The base name includes the full-qualified package name. If a resource bundle is
     * org/kuali/module/effort/report/messages.properties, the base name can be org/kuali/module/effort/report/messages.
     * 
     * @param resourceBundleBaseName The resourceBundleBaseName to set.
     */
    public void setResourceBundleBaseName(String resourceBundleBaseName) {
        this.resourceBundleBaseName = resourceBundleBaseName;
    }
}
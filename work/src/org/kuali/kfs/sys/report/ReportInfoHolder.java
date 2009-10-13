/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.report;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class ReportInfoHolder implements ReportInfo {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReportInfoHolder.class);

    private String reportFileName;
    private String reportsDirectory;
    private String reportTemplateClassPath;
    private String reportTemplateName;
    private String reportTitle;
    private String resourceBundleBaseName;
    private Map<String, String> subReports;
    private String subReportTemplateClassPath;

    /**
     * @see org.kuali.kfs.sys.report.ReportInfo#getReportFileName()
     */
    public String getReportFileName() {
        return reportFileName;
    }

    /**
     * @see org.kuali.kfs.sys.report.ReportInfo#getReportsDirectory()
     */
    public String getReportsDirectory() {
        return reportsDirectory;
    }

    /**
     * @see org.kuali.kfs.sys.report.ReportInfo#getReportTemplateClassPath()
     */
    public String getReportTemplateClassPath() {
        return reportTemplateClassPath;
    }

    /**
     * @see org.kuali.kfs.sys.report.ReportInfo#getReportTemplateName()
     */
    public String getReportTemplateName() {
        return reportTemplateName;
    }

    /**
     * @see org.kuali.kfs.sys.report.ReportInfo#getReportTitle()
     */
    public String getReportTitle() {
        return reportTitle;
    }

    /**
     * @see org.kuali.kfs.sys.report.ReportInfo#getResourceBundle()
     */
    public ResourceBundle getResourceBundle() {
        return ResourceBundle.getBundle(resourceBundleBaseName, Locale.getDefault());
    }

    /**
     * @see org.kuali.kfs.sys.report.ReportInfo#getResourceBundleBaseName()
     */
    public String getResourceBundleBaseName() {
        return resourceBundleBaseName;
    }

    /**
     * @see org.kuali.kfs.sys.report.ReportInfo#getSubReports()
     */
    public Map<String, String> getSubReports() {
        return subReports;
    }

    /**
     * @see org.kuali.kfs.sys.report.ReportInfo#getSubReportTemplateClassPath()
     */
    public String getSubReportTemplateClassPath() {
        return subReportTemplateClassPath;
    }

    /**
     * Sets the reportFilename attribute value.
     * 
     * @param reportFilename The reportFilename to set.
     */
    public void setReportFileName(String reportFileName) {
        this.reportFileName = reportFileName;
    }

    /**
     * Sets the reportsDirectory attribute value.
     * 
     * @param reportsDirectory The reportsDirectory to set.
     */
    public void setReportsDirectory(String reportsDirectory) {
        this.reportsDirectory = reportsDirectory;
    }

    /**
     * Sets the reportTemplateClassPath attribute value.
     * 
     * @param reportTemplateClassPath The reportTemplateClassPath to set.
     */
    public void setReportTemplateClassPath(String reportTemplateClassPath) {
        this.reportTemplateClassPath = reportTemplateClassPath;
    }

    /**
     * Sets the reportTemplateName attribute value.
     * 
     * @param reportTemplateName The reportTemplateName to set.
     */
    public void setReportTemplateName(String reportTemplateName) {
        this.reportTemplateName = reportTemplateName;
    }

    /**
     * Sets the reportTitle attribute value.
     * 
     * @param reportTitle The reportTitle to set.
     */
    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    /**
     * Sets the resourceBundleBaseName attribute value.
     * 
     * @param resourceBundleBaseName The resourceBundleBaseName to set.
     */
    public void setResourceBundleBaseName(String resourceBundleBaseName) {
        this.resourceBundleBaseName = resourceBundleBaseName;
    }

    /**
     * Sets the subReports attribute value.
     * 
     * @param subReports The subReports to set.
     */
    public void setSubReports(Map<String, String> subReports) {
        this.subReports = subReports;
    }

    /**
     * Sets the subReportTemplateClassPath attribute value.
     * 
     * @param subReportTemplateClassPath The subReportTemplateClassPath to set.
     */
    public void setSubReportTemplateClassPath(String subReportTemplateClassPath) {
        this.subReportTemplateClassPath = subReportTemplateClassPath;
    }
}

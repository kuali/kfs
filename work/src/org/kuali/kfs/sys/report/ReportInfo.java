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

import java.util.Map;
import java.util.ResourceBundle;

/**
 * Hold the basic information of a report, for example, the names and locations of the output report and the report template.  
 */
public interface ReportInfo {

    /**
     * Gets the reportTitle attribute.
     * 
     * @return Returns the reportTitle.
     */
    public String getReportTitle();

    /**
     * Gets the reportFileName attribute.
     * 
     * @return Returns the reportFileName.
     */
    public String getReportFileName();

    /**
     * Gets the resourceBundleBaseName attribute.
     * 
     * @return Returns the resourceBundleBaseName.
     */
    public String getResourceBundleBaseName();

    /**
     * Gets the reportTemplateName attribute.
     * 
     * @return Returns the reportTemplateName.
     */
    public String getReportTemplateName();

    /**
     * Gets the reportTemplateClassPath attribute.
     * 
     * @return Returns the reportTemplateClassPath.
     */
    public String getReportTemplateClassPath();

    /**
     * Gets the subReportTemplateClassPath attribute.
     * 
     * @return Returns the subReportTemplateClassPath.
     */
    public String getSubReportTemplateClassPath();

    /**
     * Gets the subReports attribute.
     * 
     * @return Returns the subReports.
     */
    public Map<String, String> getSubReports();

    /**
     * Gets the reportsDirectory attribute.
     * 
     * @return Returns the reportsDirectory.
     */
    public String getReportsDirectory();

    /**
     * get the resource bundle
     */
    public ResourceBundle getResourceBundle();

}

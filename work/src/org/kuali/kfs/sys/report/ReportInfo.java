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

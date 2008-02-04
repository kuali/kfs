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
package org.kuali.module.effort.report;

import java.io.File;
import java.io.IOException;

import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.effort.EffortConstants;
import org.springframework.core.io.ClassPathResource;

/**
 * A registry of the reports, which typically holds the key elements of a report: its file name and title.
 */
public enum EffortReportRegistry {
    EFFORT_EXTRACT_SUMMARY("effort_extract_summary", "Effort Certification Extract Process", "EffortExtractProcessReport");

    private String reportFilename;
    private String reportTitle;
    private String reportTemplateName;

    /**
     * Constructs a ReportRegistry.java.
     * 
     * @param reportFilename the report file name
     * @param reportTitle the report title
     */
    private EffortReportRegistry(String reportFilename, String reportTitle, String reportTemplateName) {
        this.reportFilename = reportFilename;
        this.reportTitle = reportTitle;
        this.reportTemplateName = reportTemplateName;
    }

    /**
     * @return report file name
     */
    public String reportFilename() {
        return this.reportFilename;
    }

    /**
     * @return report title
     */
    public String reportTitle() {
        return this.reportTitle;
    }
    
    /**
     * Gets the reportTemplateName attribute. 
     * @return Returns the reportTemplateName.
     */
    public String getReportTemplateName() {
        return reportTemplateName;
    }

    /**
     * get the directory where the reports can be stored
     */
    public static String getReportsDirectory() {
        return SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.REPORTS_DIRECTORY_KEY);
    }
}
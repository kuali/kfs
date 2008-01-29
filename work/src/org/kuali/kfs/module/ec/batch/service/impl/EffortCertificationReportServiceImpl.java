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

import org.kuali.module.effort.service.EffortCertificationReportService;
import org.kuali.module.effort.util.EffortReportRegistry;
import org.kuali.module.effort.util.ExtractProcessReportDataHolder;
import org.kuali.module.effort.util.ReportGenerator;

/**
 * To generate the working progress reports for the effort certification
 */
public class EffortCertificationReportServiceImpl implements EffortCertificationReportService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortCertificationReportServiceImpl.class);
    public static final String PDF_FILE_EXTENSION = "pdf";

    /**
     * @see org.kuali.module.effort.service.EffortCertificationReportService#generate(org.kuali.module.effort.util.ExtractProcessReportDataHolder,
     *      org.kuali.module.effort.util.EffortReportRegistry, java.lang.String, java.util.Date)
     */
    public void generateReportForExtractProcess(ExtractProcessReportDataHolder reportDataHolder, EffortReportRegistry reportInfo, String reportsDirectory, Date runDate) {
        String fileNamePrefix = reportInfo.reportFilename();
        String template = "report/" + reportInfo.getReportTemplateName();
        String fullReportFileName = this.buildFullFileName(reportsDirectory, fileNamePrefix, PDF_FILE_EXTENSION, runDate);

        LOG.info(reportDataHolder);

        //ReportGenerator.generateReportToPdfFile(reportDataHolder.getReportData(), template, fullReportFileName);
    }

    /**
     * build a full file name with the given information
     * 
     * @param directory the directory where the file would be located
     * @param fileNamePrefix the prefix of the file name
     * @param fileNameExtension the extension of the file name
     * @param runDate the run date
     * @return a full file name built from the given information
     */
    private String buildFullFileName(String directory, String fileNamePrefix, String fileNameExtension, Date runDate) {
        String sdf = new SimpleDateFormat("yyyyMMdd_HHmmss").format(runDate);
        String fileNamePattern = "{0}/{1}.{2}";

        return MessageFormat.format(fileNamePattern, directory, fileNamePrefix + sdf, fileNameExtension);
    }
}
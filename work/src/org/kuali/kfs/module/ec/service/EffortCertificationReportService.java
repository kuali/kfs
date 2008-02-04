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
package org.kuali.module.effort.service;

import java.util.Date;

import org.kuali.module.effort.report.EffortReportRegistry;
import org.kuali.module.effort.util.ExtractProcessReportDataHolder;

/** 
 * To generate the working progress reports for the effort certification  
 */
public interface EffortCertificationReportService {
    
    /**
     * generate report for effort certification extract process witht the given report data and information
     * @param reportDataHolder the given report data holder
     * @param reportInfo the primary elements of a report, such as report title and report file name 
     * @param reportsDirectory the directory in file system that is used to contain reports
     * @param runDate the datetime of the repor generation
     */
    void generateReportForExtractProcess(ExtractProcessReportDataHolder reportDataHolder, EffortReportRegistry reportInfo, String reportsDirectory, Date runDate);
}

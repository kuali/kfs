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
package org.kuali.kfs.module.cam.batch.service.impl;

import java.util.List;

import org.kuali.kfs.module.cam.batch.service.ReportService;
import org.kuali.kfs.module.cam.report.DepreciationReport;

public class ReportServiceImpl implements ReportService {
    /**
     * 
     * @see org.kuali.kfs.module.cam.batch.service.ReportService#generateDepreciationReport(java.util.List, java.lang.String)
     */
    public void generateDepreciationReport(List<String[]> reportLog, String errorMsg, String sDepreciationDate) {
        DepreciationReport dr = new DepreciationReport();
        dr.generateReport(reportLog, errorMsg, sDepreciationDate);
    }
}

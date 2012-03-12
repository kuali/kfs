/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.gl.report;

import org.kuali.kfs.sys.service.ReportWriterService;

public class PreScrubberReport {
    public void generateReport(PreScrubberReportData preScrubberReportData, ReportWriterService reportWriterService) {
        reportWriterService.writeFormattedMessageLine("Origin Entries In %,20d", preScrubberReportData.getInputRecords());
        reportWriterService.writeFormattedMessageLine("Origin Entries Out%,20d", preScrubberReportData.getOutputRecords());
        if (!preScrubberReportData.getAccountsWithNoCharts().isEmpty()) {
            reportWriterService.writeNewLines(2);
            reportWriterService.writeFormattedMessageLine("The following account numbers were not associated with any chart of accounts code:");
            for (String accountNumber : preScrubberReportData.getAccountsWithNoCharts()) {
                reportWriterService.writeFormattedMessageLine("%15s", accountNumber);
            }
        }
        if (!preScrubberReportData.getAccountsWithMultipleCharts().isEmpty()) {
            reportWriterService.writeNewLines(2);
            reportWriterService.writeFormattedMessageLine("The following account numbers were associated with multiple chart of accounts codes:");
            for (String accountNumber : preScrubberReportData.getAccountsWithMultipleCharts()) {
                reportWriterService.writeFormattedMessageLine("%15s", accountNumber);
            }
        }
    }
}

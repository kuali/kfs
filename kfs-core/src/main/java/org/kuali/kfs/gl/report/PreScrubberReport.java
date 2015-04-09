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

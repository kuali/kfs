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
package org.kuali.module.gl.service;

import org.kuali.module.gl.util.CollectorReportData;

/**
 * Services to help the Collector report 
 */
public interface CollectorReportService {
    /**
     * Send e-mails with the report data generated from a Collector run
     * 
     * @param collectorReportData the collector data to report on
     */
    public void sendEmails(CollectorReportData collectorReportData);

    /**
     * Generates reports on a Collector run
     * 
     * @param collectorReportData the collector data to report on
     */
    public void generateCollectorRunReports(CollectorReportData collectorReportData);
}

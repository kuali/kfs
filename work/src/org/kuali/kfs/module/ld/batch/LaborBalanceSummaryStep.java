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
package org.kuali.module.labor.batch;

import java.util.Date;

import org.kuali.kfs.batch.AbstractStep;
import org.kuali.module.labor.service.LaborBalanceSummaryReportService;

/**
 * This class defines the batch step for labor balance summary report generation
 */
public class LaborBalanceSummaryStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborBalanceSummaryStep.class);
    private LaborBalanceSummaryReportService laborBalanceSummaryReportService;

    /**
     * Invokes the method that generates the balance summary report.
     * 
     * @param jobName
     * @param jobRunDate
     * @return boolean
     * @see org.kuali.kfs.batch.Step#execute(String, Date)
     */
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        laborBalanceSummaryReportService.generateBalanceSummaryReports();
        return true;
    }

    /**
     * Sets the laborBalanceSummaryReportService attribute value.
     * 
     * @param laborBalanceSummaryReportService The laborBalanceSummaryReportService to set.
     */
    public void setLaborBalanceSummaryReportService(LaborBalanceSummaryReportService laborBalanceSummaryReportService) {
        this.laborBalanceSummaryReportService = laborBalanceSummaryReportService;
    }
}
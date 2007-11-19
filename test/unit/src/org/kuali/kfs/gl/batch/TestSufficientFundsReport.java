/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.gl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.kuali.module.gl.batch.sufficientFunds.SufficientFundsReport;

/**
 * A mock implementation of SufficientFundsReport that doesn't generate a report
 */
public class TestSufficientFundsReport implements SufficientFundsReport {
    public Map reportErrors;
    public List reportSummary;
    public Date runDate;
    public int mode;

    /**
     * This is a mock...so it doesn't generate a report at all, but sets the parameter values to 
     * be checked later.
     * @see org.kuali.module.gl.batch.sufficientFunds.SufficientFundsReport#generateReport(java.util.Map, java.util.List, java.util.Date, int)
     */
    public void generateReport(Map reportErrors, List reportSummary, Date runDate, int mode) {
        this.reportErrors = reportErrors;
        this.reportSummary = reportSummary;
        this.runDate = runDate;
        this.mode = mode;
    }
}

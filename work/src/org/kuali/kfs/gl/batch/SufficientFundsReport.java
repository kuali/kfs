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
package org.kuali.module.gl.batch.sufficientFunds;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * An interface to generate a sufficient funds report.
 */
public interface SufficientFundsReport {
    /**
     * Generates the sufficient funds report
     * 
     * @param reportErrors a Map of errors encountered during the sufficient funds rebuild process
     * @param reportSummary a List of Strings that summarize the sufficient funds rebuild process 
     * @param runDate the date the sufficient funds rebuild process was run
     * @param mode the mode the sufficient funds rebuild process was run in
     */
    public void generateReport(Map reportErrors, List reportSummary, Date runDate, int mode);
}

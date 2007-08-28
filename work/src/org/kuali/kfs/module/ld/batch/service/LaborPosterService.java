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
package org.kuali.module.labor.service;

import java.sql.Date;

/**
 * The interface gives clients entry point to consume poster services. It is designed for a batch process only. 
 */
public interface LaborPosterService {
    /**
     * Post the eligible entries into Ledger tables
     */
    public void postMainEntries();
    
    /**
     * Generate the summary reports for the actual, budget and encumbrance balances
     */
    public void generateBalanceSummaryReports();
    
    /**
     * generate a set of balance summary reports for actual, budget and encumbrance balances
     * 
     * @param runDate the data when the report generation starts
     */
    public void generateBalanceSummaryReports(Date runDate);
}
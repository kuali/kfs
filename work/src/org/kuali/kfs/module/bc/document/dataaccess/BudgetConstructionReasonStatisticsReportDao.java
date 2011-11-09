/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.bc.document.dataaccess;

import org.kuali.rice.core.api.util.type.KualiDecimal;

public interface BudgetConstructionReasonStatisticsReportDao {

    /**
     *  clears any previous report data for the current user out of the reason statistics report table
     * 
     * @param principalName--the user requesting the report
     */
    public void cleanReportsReasonStatisticsTable(String principalName);


    /**
     * 
     * report salary reason statistics for people at or above or at or below a given threshold percent
     * @param principalName--the user running this report
     * @param previousFiscalYear--the fiscal year preceding the one for which we are building a budget
     * @param reportIncreasesAtOrAboveTheThreshold--true if we report increases at or above the threshold, false otherwise
     * @param thresholdPercent--the threshold percent (fraction times 100) increase
     */
    public void updateReasonStatisticsReportsWithAThreshold(String principalName, Integer previousFiscalYear, boolean reportIncreasesAtOrAboveTheThreshold, KualiDecimal thresholdPercent);
    
    /**
     * 
     * report salary reason statistics for everyone
     * @param principalName--the user running this report
     * @param previousFiscalYear--the fiscal year preceding the one for which we are building a budget
     */
    public void updateReasonStatisticsReportsWithoutAThreshold(String principalName, Integer previousFiscalYear);


}


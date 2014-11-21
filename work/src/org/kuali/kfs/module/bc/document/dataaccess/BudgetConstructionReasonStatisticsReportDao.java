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


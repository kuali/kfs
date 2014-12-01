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

public interface BudgetConstructionSalarySummaryReportDao {



    /**
     * 
     * lists all salaries, or only those flagged with a reason for increase
     * @param principalName--the user requesting the report
     * @param listSalariesWithReasonCodes--true if only salaries associated with a reason for increase are to be listed, false if all salaries are to be listed
     */
    public void updateSalaryAndReasonSummaryReportsWithoutThreshold(String principalName, boolean listSalariesWithReasonCodes);
    
    /**
     * 
     * lists salaries with increases at or above a threshold, or with increases at or below a threshold
     * @param principalName--the user requesting the report
     * @param previousFiscalYear--the fiscal year preceding the one for which the budget is being built
     * @param reportGreaterThanOrEqualToThreshold--true if salaries increased at or above the threshold percentage are listed, false if salaries increased at or below the threshold percentage are listed
     * @param threshold--the threshold percentage (a fraction times 100)
     */
    public void updateSalaryAndReasonSummaryReportsWithThreshold(String principalName, Integer previousFiscalYear, boolean reportGreaterThanOrEqualToThreshold, KualiDecimal threshold);

}


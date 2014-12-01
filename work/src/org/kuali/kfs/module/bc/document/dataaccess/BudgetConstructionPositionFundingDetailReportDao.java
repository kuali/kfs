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

public interface BudgetConstructionPositionFundingDetailReportDao {

    /**
     *  cleans Position Funding Detail table.
     * 
     * @param principalName
     */
    public void cleanReportsPositionFundingDetailTable(String principalName);


    /**
     * 
     * populates the reporting table for PositionFunding so the user can run a report
     * @param principalName--the user making the request
     * @param applyAThreshold--true if the report will only list people with increases above (or below) a threshold, false otherwise
     * @param selectOnlyGreaterThanOrEqualToThreshold--true if people at or above the threshold are to be listed: false lists people at or below
     * @param thresholdPercent--percent (a fraction times 100) increase which marks the threshold 
     */
    public void updateReportsPositionFundingDetailTable(String principalName, boolean applyAThreshold, boolean selectOnlyGreaterThanOrEqualToThreshold, KualiDecimal thresholdPercent);

}


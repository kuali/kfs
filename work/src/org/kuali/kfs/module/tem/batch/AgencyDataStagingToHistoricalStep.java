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
package org.kuali.kfs.module.tem.batch;

import java.util.Date;

import org.kuali.kfs.module.tem.batch.service.AgencyDataImportService;
import org.kuali.kfs.sys.batch.AbstractStep;

public class AgencyDataStagingToHistoricalStep extends AbstractStep {

    private AgencyDataImportService agencyDataImportService;
    
    @Override
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        return agencyDataImportService.moveAgencyDataToHistoricalExpenseTable();

    }

    /**
     * Gets the agencyDataImportService attribute. 
     * @return Returns the agencyDataImportService.
     */
    public AgencyDataImportService getAgencyDataImportService() {
        return agencyDataImportService;
    }

    /**
     * Sets the agencyDataImportService attribute value.
     * @param agencyDataImportService The agencyDataImportService to set.
     */
    public void setAgencyDataImportService(AgencyDataImportService agencyDataImportService) {
        this.agencyDataImportService = agencyDataImportService;
    }

}

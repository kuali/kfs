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
package org.kuali.kfs.module.endow.batch;

import java.util.Date;

import org.kuali.kfs.module.endow.batch.service.UpdateHistoryCashService;
import org.kuali.kfs.sys.batch.AbstractStep;

/*
 * This process appends current cash balance records to the history table as of the end of each month.  
 * The records in the table will be used for reporting purposes and statement generation.  
 * As a part of the process, a record will be created for the month end date.
 */
public class UpdateHistoryCashStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreateAccrualTransactionsStep.class);

    private UpdateHistoryCashService updateHistoryCashService;

    /**
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String, java.util.Date)
     */
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        return updateHistoryCashService.updateHistoryCash();       
    }
        
    /**
     * Sets the updateHistoryCashService.
     * 
     * @param updateHistoryCashService
     */
    public void setUpdateHistoryCashService(UpdateHistoryCashService service) {
        this.updateHistoryCashService = service;
    }


}

/*
 * Copyright 2010 The Kuali Foundation.
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

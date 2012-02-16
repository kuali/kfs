/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.batch;

import java.util.Date;

import org.kuali.kfs.module.tem.batch.service.AgencyDataImportService;
import org.kuali.kfs.sys.batch.AbstractStep;

public class AgencyReconciliationMatchStep extends AbstractStep {

    private AgencyDataImportService agencyDataImportService;
    
    @Override
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        return agencyDataImportService.matchExpenses();

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

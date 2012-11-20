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

import org.kuali.kfs.module.tem.batch.service.TaxableRamificationNotificationService;
import org.kuali.kfs.sys.batch.AbstractStep;

public class TaxableRamificationNotificationStep extends AbstractStep {

    TaxableRamificationNotificationService taxableRamificationNotificationService;

    /**
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String, java.util.Date)
     */
    @Override
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        this.getTaxableRamificationNotificationService().sendTaxableRamificationReport();

        return true;
    }

    /**
     * Gets the taxableRamificationNotificationService attribute.
     * 
     * @return Returns the taxableRamificationNotificationService.
     */
    public TaxableRamificationNotificationService getTaxableRamificationNotificationService() {
        return taxableRamificationNotificationService;
    }

    /**
     * Sets the taxableRamificationNotificationService attribute value.
     * 
     * @param taxableRamificationNotificationService The taxableRamificationNotificationService to set.
     */
    public void setTaxableRamificationNotificationService(TaxableRamificationNotificationService taxableRamificationNotificationService) {
        this.taxableRamificationNotificationService = taxableRamificationNotificationService;
    }

}

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
package org.kuali.module.gl.batch;

import java.util.Date;

import org.kuali.kfs.batch.AbstractStep;
import org.kuali.module.gl.service.EnterpriseFeederService;

/**
 * This step executes the enterprise feeder
 */
public class EnterpriseFeedStep extends AbstractStep {

    private EnterpriseFeederService enterpriseFeederService;

    /**
     * Runs the enterprise feeder process
     * 
     * @param jobName the name of the job this step is being execute as part of
     * @param jobRunDate the time/date when the job was started
     * @return true if the step completed successfully, false if otherwise
     * @see org.kuali.kfs.batch.Step#execute(String, Date)
     */
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        enterpriseFeederService.feed(jobName, true);
        return true;
    }

    /**
     * Gets the enterpriseFeederService attribute.
     * 
     * @return Returns the enterpriseFeederService.
     * @see org.kuali.module.gl.service.EnterpriseFeederService
     */
    public EnterpriseFeederService getEnterpriseFeederService() {
        return enterpriseFeederService;
    }

    /**
     * Sets the enterpriseFeederService attribute value.
     * 
     * @param enterpriseFeederService The enterpriseFeederService to set.
     * @see org.kuali.module.gl.service.EnterpriseFeederService
     */
    public void setEnterpriseFeederService(EnterpriseFeederService enterpriseFeederService) {
        this.enterpriseFeederService = enterpriseFeederService;
    }

}

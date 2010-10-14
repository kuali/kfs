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
package org.kuali.kfs.module.endow.batch;

import java.util.Date;

import org.kuali.kfs.module.endow.batch.service.AvailableCashUpdateService;
import org.kuali.kfs.sys.batch.AbstractStep;

/**
 * Batch Step that executes the Available Cash Update Step Process.
 */
public class AvailableCashUpdateStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AvailableCashUpdateStep.class);

    protected AvailableCashUpdateService availableCashUpdateService;

    /**
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        availableCashUpdateService.summarizeAvailableSpendableFunds();
        
        return true;
    }

    /**
     * Sets the effortCertificationCreateService attribute value.
     * @param effortCertificationCreateService The effortCertificationCreateService to set.
     */
    public void setAvailableCashUpdateService(AvailableCashUpdateService availableCashUpdateService) {
        this.availableCashUpdateService = availableCashUpdateService;
    }
}

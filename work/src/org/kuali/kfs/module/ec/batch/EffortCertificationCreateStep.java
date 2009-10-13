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
package org.kuali.kfs.module.ec.batch;

import java.util.Date;

import org.kuali.kfs.module.ec.batch.service.EffortCertificationCreateService;
import org.kuali.kfs.sys.batch.AbstractStep;

/**
 * Batch Step that executes the Effort Certification Extract Process.
 */
public class EffortCertificationCreateStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortCertificationCreateStep.class);

    private EffortCertificationCreateService effortCertificationCreateService;

    /**
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        effortCertificationCreateService.create();
        
        return true;
    }

    /**
     * Sets the effortCertificationCreateService attribute value.
     * @param effortCertificationCreateService The effortCertificationCreateService to set.
     */
    public void setEffortCertificationCreateService(EffortCertificationCreateService effortCertificationCreateService) {
        this.effortCertificationCreateService = effortCertificationCreateService;
    }
}

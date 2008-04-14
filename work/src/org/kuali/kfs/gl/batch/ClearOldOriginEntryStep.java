/*
 * Copyright 2006-2007 The Kuali Foundation.
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
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.service.OriginEntryGroupService;

/**
 * A step which runs the process to remove old origin entry groups and associated origin entries
 */
public class ClearOldOriginEntryStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ClearOldOriginEntryStep.class);
    private OriginEntryGroupService originEntryGroupService;

    /**
     * Performs the process of clearing old origin entry groups and entries
     * 
     * @param the name of the job that this step is a part of
     * @param jobRunDate the time/date the job is run
     * @return that the job completed successfully
     * @see org.kuali.kfs.batch.Step#execute(String, Date)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        LOG.debug("performStep() started");
        String daysStr = getParameterService().getParameterValue(getClass(), GLConstants.RETAIN_DAYS);
        int days = Integer.parseInt(daysStr);
        originEntryGroupService.deleteOlderGroups(days);
        return true;
    }

    /**
     * Sets the originEntryService attribute, allowing injection of an implementation of the service
     * 
     * @param oes
     * @see org.kuali.module.gl.service.OriginEntryGroupService
     */
    public void setOriginEntryGroupService(OriginEntryGroupService oes) {
        originEntryGroupService = oes;
    }
}

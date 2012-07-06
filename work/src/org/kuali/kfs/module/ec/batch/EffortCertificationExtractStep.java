/*
 * Copyright 2007 The Kuali Foundation
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

import org.kuali.kfs.module.ec.EffortConstants.SystemParameters;
import org.kuali.kfs.module.ec.EffortKeyConstants;
import org.kuali.kfs.module.ec.batch.service.EffortCertificationExtractService;
import org.kuali.kfs.module.ec.util.EffortCertificationParameterFinder;
import org.kuali.kfs.sys.MessageBuilder;
import org.kuali.kfs.sys.batch.AbstractStep;

/**
 * Batch Step that executes the Effort Certification Extract Process.
 */
public class EffortCertificationExtractStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortCertificationExtractStep.class);
    
    private EffortCertificationExtractService effortCertificationExtractService;

    /**
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        if(EffortCertificationParameterFinder.getRunIndicator()) {
            effortCertificationExtractService.extract();
        }
        else {
            String key = EffortKeyConstants.ERROR_BATCH_JOB_NOT_SCHEDULED;
            String message = MessageBuilder.buildMessageWithPlaceHolder(key, 0, new Object[] { jobName, SystemParameters.RUN_IND } ).toString();            
            LOG.warn(message);
        }
        return true;
    }

    /**
     * Sets the effortCertificationExtractService attribute value.
     * @param effortCertificationExtractService The effortCertificationExtractService to set.
     */
    public void setEffortCertificationExtractService(EffortCertificationExtractService effortCertificationExtractService) {
        this.effortCertificationExtractService = effortCertificationExtractService;
    }
}

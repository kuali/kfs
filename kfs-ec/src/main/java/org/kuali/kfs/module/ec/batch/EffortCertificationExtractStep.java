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

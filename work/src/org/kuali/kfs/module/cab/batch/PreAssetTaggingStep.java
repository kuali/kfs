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
package org.kuali.module.capitalAssetBuilder.batch;

import java.util.Date;

import org.kuali.kfs.batch.AbstractStep;
import org.kuali.module.capitalAssetBuilder.service.impl.PreAssetTaggingService;

/**
 * Cams Batch Step.
 */
public class PreAssetTaggingStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PreAssetTaggingStep.class);

    private PreAssetTaggingService preAssetTaggingService;

    /**
     * Invokes service that copy flat file to Pre-Tag Temp Table
     * 
     * @param String jobName
     * @return boolean
     * @see org.kuali.kfs.batch.Step#execute()
     */
    public boolean execute(String jobName, Date jobRunDate) {
        try {
            preAssetTaggingService.copyPreAssetTaggingEntries();
        }
        catch (Exception e) {
            LOG.fatal(e);
            return false;
        }
        return true;
    }

    /**
     * Sets the laborNightlyOutService attribute value.
     * 
     * @param preAssetTaggingService the preAssetTaggingService to set.
     */
    public void setPreAssetTaggingService(PreAssetTaggingService preAssetTaggingService) {
        this.preAssetTaggingService = preAssetTaggingService;
    }

}


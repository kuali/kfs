/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.cam.batch;

import org.kuali.kfs.module.cam.batch.service.AssetDepreciationService;
import org.kuali.kfs.sys.batch.AbstractStep;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A step in AssetYearEndDepreciation.  Runs {@link AssetDepreciationService}.runYearEndDepreciation
 */
public class AssetYearEndDepreciationStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetYearEndDepreciationStep.class);
    private AssetDepreciationService assetDepreciationService;


    /**
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String, java.util.Date)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String date = sdf.format(new java.util.Date());
        assetDepreciationService.runYearEndDepreciation(new Integer(date));
        return true;
    }

    /**
     * Sets assetDepreciationService
     * @param assetDepreciationService
     */
    public void setAssetDepreciationService(AssetDepreciationService assetDepreciationService) {
        this.assetDepreciationService = assetDepreciationService;
    }
}
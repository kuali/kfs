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

import java.util.Date;

import org.kuali.kfs.module.cam.batch.service.AssetDepreciationService;
import org.kuali.kfs.sys.batch.AbstractStep;

public class AssetDepreciationStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetDepreciationStep.class);
    private AssetDepreciationService assetDepreciationService;

    /**
     * Executes the method that forwards the balance
     * 
     * @param String jobName
     * @return boolean
     * @see org.kuali.kfs.sys.batch.Step#execute()
     */
    public boolean execute(String jobName, Date jobRunDate)  {
        assetDepreciationService.runDepreciation();
        return true;
    }

    public void setAssetDepreciationService(AssetDepreciationService assetDepreciationService) {
        this.assetDepreciationService = assetDepreciationService;
    }
}

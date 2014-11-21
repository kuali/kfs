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

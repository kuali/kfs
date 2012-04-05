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
package org.kuali.kfs.module.cam.batch.service;

import java.util.Collection;

import org.kuali.kfs.module.cam.businessobject.AssetObjectCode;
import org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao;

public interface AssetDepreciationService {
    /**
     * This method runs depreciation process
     */
    public void runDepreciation();


    // CSU 6702 BEGIN
    /**
     * This method runs depreciation process
     */
    public void runYearEndDepreciation(Integer fiscalYearToDepreciate);
    // CSU 6702 END
    
    /**
     * Sets depreciation batch dao implementation
     * 
     * @param depreciationBatchDao
     */
    public void setDepreciationBatchDao(DepreciationBatchDao depreciationBatchDao);

    /**
     * This method retrieves a list of valid asset object codes for a particular fiscal year
     * 
     * @param fiscalYear
     * @return Collection<AssetObjectCode>
     */
    public Collection<AssetObjectCode> getAssetObjectCodes(Integer fiscalYear);

}

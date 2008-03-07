/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.cams.service.impl;

import java.util.List;

import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetDisposition;
import org.kuali.module.cams.bo.AssetLocation;
import org.kuali.module.cams.service.AssetDispositionService;
import org.kuali.module.cams.service.AssetLocationService;

public class AssetLocationServiceImpl implements AssetLocationService {

    /**
     * The method will set Off Campus Location from the assetLocations collection
     * @see org.kuali.module.cams.service.AssetLocationService#setOffCampusLocation(org.kuali.module.cams.bo.Asset)
     */
    public void setOffCampusLocation(Asset asset) {
        List<AssetLocation> assetLocations = asset.getAssetLocations();
        
        for (AssetLocation location :assetLocations) {
            if (CamsConstants.AssetLocationTypeCode.OFF_CAMPUS.equalsIgnoreCase(location.getAssetLocationTypeCode())){
                //location.refreshReferenceObject(referenceObjectName);
                asset.setOffCampusLocation(location);
                break;
            }
        }
    }
}

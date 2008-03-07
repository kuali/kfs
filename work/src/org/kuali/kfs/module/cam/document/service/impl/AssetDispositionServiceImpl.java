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
import org.kuali.module.cams.service.AssetDispositionService;

public class AssetDispositionServiceImpl implements AssetDispositionService {
    public void setAssetDispositionHistory(Asset asset) {
        AssetDisposition mergeHistroty = getMergeHistroty(asset);
        if (mergeHistroty != null) {
            asset.setAssetMergeHistory(mergeHistroty);
        }
        AssetDisposition separateHistroty = getSeparateHistroty(asset);
        if (separateHistroty != null) {
            asset.setAssetSeparateHistory(separateHistroty);
        }
    }


    private AssetDisposition getMergeHistroty(Asset asset) {
        List<AssetDisposition> assetDispositon = asset.getAssetDispositions();

        for (AssetDisposition disposition : assetDispositon) {
            if (CamsConstants.Discompositon.ASSET_DISCOMPOSTION_CODE_MERGE.equals(disposition.getDispositionCode().toUpperCase())) {
                return disposition;
            }
        }
        return null;
    }

    private AssetDisposition getSeparateHistroty(Asset asset) {
        List<AssetDisposition> assetDispositon = asset.getAssetDispositions();

        for (AssetDisposition disposition : assetDispositon) {
            if (CamsConstants.Discompositon.ASSET_DISCOMPOSTION_CODE_SEPARATE.equals(disposition.getDispositionCode().toUpperCase())) {
                return disposition;
            }
        }
        return null;
    }
}

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
package org.kuali.kfs.module.cam.fixture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobalDetail;

public enum AssetRetirementGlobalMaintainableFixture {
    RETIREMENT1(1);
;
    private int testDataPos;

    private static Properties properties;
    static {
        String propertiesFileName = "org/kuali/kfs/module/cam/document/service/asset_retirement.properties";
        properties = new Properties();
        try {
            properties.load(ClassLoader.getSystemResourceAsStream(propertiesFileName));
        }
        catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private AssetRetirementGlobalMaintainableFixture(int dataPos) {
        this.testDataPos = dataPos;
    }

    @SuppressWarnings("deprecation")
    public AssetRetirementGlobal newAssetRetirement() {
        String propertyKey = "assetRetirement.testData" + testDataPos;
        String deliminator = properties.getProperty("deliminator");
        String fieldNames = properties.getProperty("assetRetirement.fieldNames");
        AssetRetirementGlobal       assetRetirementGlobal       = CamsFixture.DATA_POPULATOR.buildTestDataObject(AssetRetirementGlobal.class, properties, propertyKey, fieldNames, deliminator);

        List<AssetRetirementGlobalDetail> assetRetirementGlobalDetail = newAssetRetirementDetail();
        assetRetirementGlobal.setAssetRetirementGlobalDetails(assetRetirementGlobalDetail);
        return assetRetirementGlobal;
    }

    @SuppressWarnings("deprecation")
    public List<AssetRetirementGlobalDetail> newAssetRetirementDetail() {
        String propertyKey;
        String deliminator = properties.getProperty("deliminator");
        String fieldNames = properties.getProperty("assetRetirementDetail.fieldNames");
        
        Integer dataRows    = new Integer(properties.getProperty("assetRetirementDetail.numOfData"));                
        
        List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails = new ArrayList<AssetRetirementGlobalDetail>();
        for(int i=1;i<=dataRows.intValue();i++) {
            propertyKey = "assetRetirementDetail.testData" + i;
            AssetRetirementGlobalDetail assetRetirementGlobalDetail = CamsFixture.DATA_POPULATOR.buildTestDataObject(AssetRetirementGlobalDetail.class, properties, propertyKey, fieldNames, deliminator);            
            assetRetirementGlobalDetails.add(assetRetirementGlobalDetail);
        }
        return assetRetirementGlobalDetails;
    }
}
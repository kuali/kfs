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
package org.kuali.kfs.module.cam.fixture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
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
    private List<AssetRetirementGlobalDetail> newAssetRetirementDetail() {
        List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails = new ArrayList<AssetRetirementGlobalDetail>();
        List<Asset> assets = newAssets();
        for (Asset asset : assets) {
            AssetRetirementGlobalDetail assetRetirementDetail = new AssetRetirementGlobalDetail();
            assetRetirementDetail.setAsset(asset);
            assetRetirementGlobalDetails.add(assetRetirementDetail);
        }
        return assetRetirementGlobalDetails;
    }
    
    @SuppressWarnings("deprecation")
    private List<Asset> newAssets() {
        List<Asset>  assets = new ArrayList<Asset>();
        String deliminator = properties.getProperty("deliminator");
        String fieldNames = properties.getProperty("asset.fieldNames");
        Integer dataRows    = new Integer(properties.getProperty("asset.numOfData"));  
        testDataPos=1;
        for (int i=1; i<= dataRows.intValue(); i++) {
            String propertyKey = "asset.testData" +i;
            Asset asset = CamsFixture.DATA_POPULATOR.buildTestDataObject(Asset.class, properties, propertyKey, fieldNames, deliminator);
            List<AssetPayment> assetPayments = newAssetPayments();
            asset.setAssetPayments(assetPayments);
            for (AssetPayment assetPayment : assetPayments) {
                assetPayment.setAsset(asset);
            }
            assets.add(asset);
            testDataPos += 2;
        }
        return assets;
    }
    
    private List<AssetPayment> newAssetPayments() {
        List<AssetPayment> assetPayments = new ArrayList<AssetPayment>();
        String deliminator = properties.getProperty("deliminator");
        String fieldNames = properties.getProperty("assetPayment.fieldNames");
        Integer dataRows = new Integer(properties.getProperty("assetPayment.numOfData"));
        for (int i=testDataPos;i<=dataRows.intValue() && i<testDataPos+2;i++) {
            String propertyKey = "assetPayment.testData" + i;
            AssetPayment assetPayment = CamsFixture.DATA_POPULATOR.buildTestDataObject(AssetPayment.class, properties, propertyKey, fieldNames, deliminator);
            assetPayments.add(assetPayment);
        }
        return assetPayments;
    }
}

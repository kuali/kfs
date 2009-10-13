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
import java.util.Properties;

import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.document.AssetTransferDocument;

public enum AssetTransferFixture {
    ACTIVE_CAPITAL_ASSET(1), RETIRED_ASSET(2), ACTIVE_NON_CAPITAL_ASSET(3), PAYMENT1_WITH_OFFSET(1), PAYMENT2_WITH_OFFSET(2), PAYMENT3_WITHOUT_OFFSET(3), PAYMENT4_WITHOUT_OFFSET(4), ASSET_TRANSFER(1);
    private int testDataPos;

    private static Properties properties;
    static {
        String propertiesFileName = "org/kuali/kfs/module/cam/document/service/asset_transfer_service.properties";
        properties = new Properties();
        try {
            properties.load(ClassLoader.getSystemResourceAsStream(propertiesFileName));
        }
        catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private AssetTransferFixture(int dataPos) {
        this.testDataPos = dataPos;
    }

    public Asset newAsset() {
        String propertyKey = "asset.testData" + testDataPos;
        String deliminator = properties.getProperty("deliminator");
        String fieldNames = properties.getProperty("asset.fieldNames");
        Asset asset = CamsFixture.DATA_POPULATOR.buildTestDataObject(Asset.class, properties, propertyKey, fieldNames, deliminator);
        return asset;
    }

    public AssetTransferDocument newAssetTransferDocument() {
        String propertyKey = "assetTransfer.testData" + testDataPos;
        String deliminator = properties.getProperty("deliminator");
        String fieldNames = properties.getProperty("assetTransfer.fieldNames");
        AssetTransferDocument assetTransferDocument = CamsFixture.DATA_POPULATOR.buildTestDataObject(AssetTransferDocument.class, properties, propertyKey, fieldNames, deliminator);
        return assetTransferDocument;
    }

    @SuppressWarnings("deprecation")
    public AssetPayment newAssetPayment() {
        String propertyKey = "assetPayment.testData" + testDataPos;
        String deliminator = properties.getProperty("deliminator");
        String fieldNames = properties.getProperty("assetPayment.fieldNames");
        AssetPayment assetPayment = CamsFixture.DATA_POPULATOR.buildTestDataObject(AssetPayment.class, properties, propertyKey, fieldNames, deliminator);
        return assetPayment;
    }
}

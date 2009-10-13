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

public enum AssetMaintainableFixture {
    ASSET1(1);

    private int testDataPos;

    private static Properties properties;
    static {
        String propertiesFileName = "org/kuali/kfs/module/cam/document/service/asset.properties";
        properties = new Properties();
        try {
            properties.load(ClassLoader.getSystemResourceAsStream(propertiesFileName));
        }
        catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private AssetMaintainableFixture(int dataPos) {
        this.testDataPos = dataPos;
    }

    @SuppressWarnings("deprecation")
    public Asset newAsset() {
        String propertyKey = "asset.testData" + testDataPos;
        String deliminator = properties.getProperty("deliminator");
        String fieldNames = properties.getProperty("asset.fieldNames");
        Asset asset= CamsFixture.DATA_POPULATOR.buildTestDataObject(Asset.class, properties, propertyKey, fieldNames, deliminator);
        return asset;
    }
}

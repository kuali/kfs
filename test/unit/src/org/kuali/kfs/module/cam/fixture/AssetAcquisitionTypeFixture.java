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

import org.kuali.kfs.module.cam.businessobject.AssetAcquisitionType;

public enum AssetAcquisitionTypeFixture {
    WITHOUT_INCOME_ASSET_OBJECT_CODE(1), WITH_INCOME_ASSET_OBJECT_CODE(2);
    private int testDataPos;

    private static Properties properties;
    static {
        String propertiesFileName = "org/kuali/kfs/module/cam/document/service/asset_acquisition_type_service.properties";
        properties = new Properties();
        try {
            properties.load(ClassLoader.getSystemResourceAsStream(propertiesFileName));
        }
        catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private AssetAcquisitionTypeFixture(int dataPos) {
        this.testDataPos = dataPos;
    }

    public AssetAcquisitionType newAssetAcquisitionType() {
        String propertyKey = "assetAcquisitionType.testData" + testDataPos;
        String deliminator = properties.getProperty("deliminator");
        String fieldNames = properties.getProperty("assetAcquisitionType.fieldNames");
        AssetAcquisitionType assetAcquisitionType = CamsFixture.DATA_POPULATOR.buildTestDataObject(AssetAcquisitionType.class, properties, propertyKey, fieldNames, deliminator);
        return assetAcquisitionType;
    }
}

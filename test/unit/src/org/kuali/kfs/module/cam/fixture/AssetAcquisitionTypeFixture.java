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

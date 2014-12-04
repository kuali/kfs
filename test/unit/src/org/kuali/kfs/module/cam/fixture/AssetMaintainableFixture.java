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

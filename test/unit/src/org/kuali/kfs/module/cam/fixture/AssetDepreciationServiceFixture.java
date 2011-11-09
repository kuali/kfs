/*
 * Copyright 2008-2009 The Kuali Foundation
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
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

// @Transactional

public enum AssetDepreciationServiceFixture {

    DATA();
    private BusinessObjectService businessObjectService;

    private int testDataPos;
    private static Properties properties;
    static {
        String propertiesFileName = "org/kuali/kfs/module/cam/document/service/depreciation_service.properties";
        properties = new Properties();
        try {
            properties.load(ClassLoader.getSystemResourceAsStream(propertiesFileName));
        }
        catch (IOException e) {
            throw new RuntimeException();
        }
    }

    static String TEST_RECORD = "testRecord";
    static String ASSET = "asset";
    static String ASSET_PAYMENT = "assetPayment";
    static String DEPRECIATION_DATE = "depreciationDate";
    static String FIELD_NAMES = "fieldNames";
    static String NUM_OF_REC = "numOfRecords";
    static String DELIMINATOR = "deliminator";
    static String RESULT = "result";

    private AssetDepreciationServiceFixture() {
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
    }

    @SuppressWarnings("deprecation")
    public List<Asset> getAssets() {
        Integer numOfRecords = new Integer(properties.getProperty(ASSET + "." + NUM_OF_REC));
        List<Asset> assets = new ArrayList<Asset>();

        String deliminator = properties.getProperty(DELIMINATOR);
        String fieldNames = properties.getProperty(ASSET + "." + FIELD_NAMES);

        for (int i = 1; i <= numOfRecords.intValue(); i++) {
            String propertyKey = ASSET + "." + TEST_RECORD + i;

            Asset asset = CamsFixture.DATA_POPULATOR.buildTestDataObject(Asset.class, properties, propertyKey, fieldNames, deliminator);
            assets.add(asset);
        }
        return assets;
    }

    @SuppressWarnings("deprecation")
    public String getDepreciationDate() {
        return properties.getProperty(DEPRECIATION_DATE);
    }

    @SuppressWarnings("deprecation")
    public List<AssetPayment> getAssetPaymentsFromPropertiesFile() {
        Integer numOfRecords = new Integer(properties.getProperty(ASSET_PAYMENT + "." + NUM_OF_REC));
        List<AssetPayment> assetPayments = new ArrayList<AssetPayment>();

        String deliminator = properties.getProperty(DELIMINATOR);
        String fieldNames = properties.getProperty(ASSET_PAYMENT + "." + FIELD_NAMES);

        for (int i = 1; i <= numOfRecords.intValue(); i++) {
            String propertyKey = ASSET_PAYMENT + "." + TEST_RECORD + i;

            AssetPayment assetPayment = CamsFixture.DATA_POPULATOR.buildTestDataObject(AssetPayment.class, properties, propertyKey, fieldNames, deliminator);
            assetPayments.add(assetPayment);
        }
        return assetPayments;
    }

    @SuppressWarnings("deprecation")
    public List<AssetPayment> getResultsFromPropertiesFile() {
        Integer numOfRecords = new Integer(properties.getProperty(RESULT + "." + NUM_OF_REC));
        List<AssetPayment> assetPayments = new ArrayList<AssetPayment>();

        String deliminator = properties.getProperty(DELIMINATOR);
        String fieldNames = properties.getProperty(RESULT + "." + FIELD_NAMES);

        for (int i = 1; i <= numOfRecords.intValue(); i++) {
            String propertyKey = RESULT + "." + TEST_RECORD + i;

            AssetPayment assetPayment = CamsFixture.DATA_POPULATOR.buildTestDataObject(AssetPayment.class, properties, propertyKey, fieldNames, deliminator);
            assetPayments.add(assetPayment);
        }
        return assetPayments;
    }

}

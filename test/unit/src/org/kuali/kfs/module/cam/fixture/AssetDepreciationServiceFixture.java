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

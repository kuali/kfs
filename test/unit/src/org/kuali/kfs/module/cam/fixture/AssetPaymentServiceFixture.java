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

import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;

public enum AssetPaymentServiceFixture {
    PAYMENT1(1);
    ;
    private int testDataPos;

    private static Properties properties;
    static {
        String propertiesFileName = "org/kuali/kfs/module/cam/document/service/asset_payment_service.properties";
        properties = new Properties();
        try {
            properties.load(ClassLoader.getSystemResourceAsStream(propertiesFileName));
        }
        catch (IOException e) {
            throw new RuntimeException();
        }
    }

    static String TESTDATA="testData";
    static String ASSET_PAYMENT_DETAIL="assetPaymentDetail";
    static String ASSET_PAYMENT="assetPayment";
    static String FIELD_NAME="fieldName";
    static String FIELD_NAMES="fieldNames";
    static String NUM_OF_DATA="numOfData";
    
    private AssetPaymentServiceFixture(int dataPos) {
        this.testDataPos = dataPos;
    }

    @SuppressWarnings("deprecation")
    public AssetPayment newAssetPayment() {
        String propertyKey = "assetPayment."+TESTDATA + testDataPos;
        String deliminator = properties.getProperty("deliminator");
        String fieldNames = properties.getProperty(ASSET_PAYMENT+"."+FIELD_NAMES);
        AssetPayment assetPayment = CamsFixture.DATA_POPULATOR.buildTestDataObject(AssetPayment.class, properties, propertyKey, fieldNames, deliminator);
        return assetPayment;
    }

    @SuppressWarnings("deprecation")
    public AssetPaymentAssetDetail newAssetPaymentAssetDetail() {
        String propertyKey = CamsPropertyConstants.AssetPaymentDocument.ASSET_PAYMENT_ASSET_DETAIL+"."+TESTDATA + testDataPos;
        String deliminator = properties.getProperty("deliminator");
        String fieldNames = properties.getProperty(CamsPropertyConstants.AssetPaymentDocument.ASSET_PAYMENT_ASSET_DETAIL+"."+FIELD_NAME);
        AssetPaymentAssetDetail assetPaymentAssetDetail = CamsFixture.DATA_POPULATOR.buildTestDataObject(AssetPaymentAssetDetail.class, properties, propertyKey, fieldNames, deliminator);
        return assetPaymentAssetDetail;
    }

    @SuppressWarnings("deprecation")
    public AssetPaymentDocument newAssetPaymentDocument() {
        AssetPaymentDocument assetPaymentDocument = new AssetPaymentDocument();        
        List<AssetPaymentDetail> assetPaymentDetails = new ArrayList<AssetPaymentDetail>();        
        List<AssetPaymentAssetDetail> assetPaymentAssetDetails = new ArrayList<AssetPaymentAssetDetail>();

        String fieldNames   = properties.getProperty(ASSET_PAYMENT_DETAIL+"."+FIELD_NAMES);        
        String deliminator  = properties.getProperty("deliminator");
        Integer dataRows    = new Integer(properties.getProperty(ASSET_PAYMENT_DETAIL+"."+NUM_OF_DATA));                
        String propertyKey="";

        for(int i=1;i<=dataRows.intValue();i++) {
            propertyKey = ASSET_PAYMENT_DETAIL+"."+TESTDATA + i;
            AssetPaymentDetail assetPaymentDetail = CamsFixture.DATA_POPULATOR.buildTestDataObject(AssetPaymentDetail.class, properties, propertyKey, fieldNames, deliminator);
            assetPaymentDetails.add(assetPaymentDetail);
        }

        fieldNames   = properties.getProperty(CamsPropertyConstants.AssetPaymentDocument.ASSET_PAYMENT_ASSET_DETAIL+"."+FIELD_NAMES);        
        deliminator  = properties.getProperty("deliminator");
        dataRows    = new Integer(properties.getProperty(CamsPropertyConstants.AssetPaymentDocument.ASSET_PAYMENT_ASSET_DETAIL+"."+NUM_OF_DATA));                
        propertyKey="";

        for(int i=1;i<=dataRows.intValue();i++) {
            propertyKey = CamsPropertyConstants.AssetPaymentDocument.ASSET_PAYMENT_ASSET_DETAIL+"."+TESTDATA + i;
            AssetPaymentAssetDetail assetPaymentAssetDetail = CamsFixture.DATA_POPULATOR.buildTestDataObject(AssetPaymentAssetDetail.class, properties, propertyKey, fieldNames, deliminator);
            assetPaymentAssetDetails.add(assetPaymentAssetDetail);
        }

        assetPaymentDocument.setSourceAccountingLines(assetPaymentDetails);
        assetPaymentDocument.setAssetPaymentAssetDetail(assetPaymentAssetDetails);
        return assetPaymentDocument;
    }
}

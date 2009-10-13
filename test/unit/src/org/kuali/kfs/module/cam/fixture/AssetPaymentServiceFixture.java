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

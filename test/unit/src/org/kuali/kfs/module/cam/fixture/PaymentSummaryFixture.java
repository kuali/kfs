/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.cams.fixture;

import java.io.IOException;
import java.util.Properties;

import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetPayment;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.test.util.TestDataPreparator;

public enum PaymentSummaryFixture {
    ASSET(1), PAYMENT1(1), PAYMENT2(2), PAYMENT3(3), PAYMENT4(4);
    private int testDataPos;

    private static Properties properties;
    static {
        String propertiesFileName = "org/kuali/module/cams/service/testdata/payment_summary_service.properties";
        properties = new Properties();
        try {
            properties.load(ClassLoader.getSystemResourceAsStream(propertiesFileName));
        }
        catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private PaymentSummaryFixture(int dataPos) {
        this.testDataPos = dataPos;
    }

    public Asset newAsset() {
        String propertyKey = "asset.testData" + testDataPos;
        String deliminator = properties.getProperty("deliminator");
        String fieldNames = properties.getProperty("asset.fieldNames");
        Asset asset = TestDataPreparator.buildTestDataObject(Asset.class, properties, propertyKey, fieldNames, deliminator);
        return asset;
    }

    @SuppressWarnings("deprecation")
    public AssetPayment newAssetPayment() {
        String propertyKey = "assetPayment.testData" + testDataPos;
        String deliminator = properties.getProperty("deliminator");
        String fieldNames = properties.getProperty("assetPayment.fieldNames");
        AssetPayment assetPayment = TestDataPreparator.buildTestDataObject(AssetPayment.class, properties, propertyKey, fieldNames, deliminator);

        // TODO this has to be re-implemented
        assetPayment.setFinancialObject(new ObjectCode() {
            public String getFinancialObjectSubTypeCode() {
                return "BF";
            }
        });
        return assetPayment;
    }
}

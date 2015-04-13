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
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.sys.TestDataPreparator;

public enum PaymentSummaryFixture {
    ASSET(1), PAYMENT1(1), PAYMENT2(2), PAYMENT3(3), PAYMENT4(4);
    private int testDataPos;

    private static Properties properties;
    static {
        String propertiesFileName = "org/kuali/kfs/module/cam/document/service/payment_summary_service.properties";
        properties = new Properties();
        try {
            properties.load(PaymentSummaryFixture.class.getClassLoader().getResourceAsStream(propertiesFileName));
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

    public AssetPayment newAssetPayment() {
        String propertyKey = "assetPayment.testData" + testDataPos;
        String deliminator = properties.getProperty("deliminator");
        String fieldNames = properties.getProperty("assetPayment.fieldNames");
        AssetPayment assetPayment = TestDataPreparator.buildTestDataObject(AssetPayment.class, properties, propertyKey, fieldNames, deliminator);

        assetPayment.setChartOfAccountsCode("BL");
        assetPayment.setFinancialObjectCode("7305");
        return assetPayment;
    }
}

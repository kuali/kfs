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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetPayment;
import org.kuali.module.cams.document.AssetTransferDocument;

import edu.iu.uis.eden.web.ConverterUtils;

public enum AssetTransferFixture {
    ACTIVE_ASSET(1), RETIRED_ASSET(2), PAYMENT1(1), PAYMENT2(2), ASSET_TRANSFER(1);
    private int testDataPos;

    private static Properties properties;
    static {
        String propertiesFileName = "org/kuali/module/cams/service/testdata/asset_transfer_service.properties";
        properties = new Properties();
        try {
            properties.load(ClassLoader.getSystemResourceAsStream(propertiesFileName));
        }
        catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private AssetTransferFixture(int dataPos) {
        this.testDataPos = dataPos;
    }

    public Asset newAsset() {
        String propertyKey = "asset.testData" + testDataPos;
        String deliminator = properties.getProperty("deliminator");
        String fieldNames = properties.getProperty("asset.fieldNames");
        Asset asset = buildTestDataObject(Asset.class, properties, propertyKey, fieldNames, deliminator);
        return asset;
    }

    public AssetTransferDocument newAssetTransferDocument() {
        String propertyKey = "assetTransfer.testData" + testDataPos;
        String deliminator = properties.getProperty("deliminator");
        String fieldNames = properties.getProperty("assetTransfer.fieldNames");
        AssetTransferDocument assetTransferDocument = buildTestDataObject(AssetTransferDocument.class, properties, propertyKey, fieldNames, deliminator);
        return assetTransferDocument;
    }

    @SuppressWarnings("deprecation")
    public AssetPayment newAssetPayment() {
        String propertyKey = "assetPayment.testData" + testDataPos;
        String deliminator = properties.getProperty("deliminator");
        String fieldNames = properties.getProperty("assetPayment.fieldNames");
        AssetPayment assetPayment = buildTestDataObject(AssetPayment.class, properties, propertyKey, fieldNames, deliminator);
        return assetPayment;
    }

    private <T> T buildTestDataObject(Class<? extends T> clazz, Properties properties, String propertyKey, String fieldNames, String delimiter) {
        T object;
        try {
            object = clazz.newInstance();
            String[] fields = fieldNames.split(delimiter, -1);
            String[] values = properties.getProperty(propertyKey).split(delimiter, -1);
            int pos = -1;
            for (String field : fields) {
                pos++;
                BeanUtilsBean instance = BeanUtilsBean.getInstance();
                ConvertUtilsBean convertUtils = instance.getConvertUtils();
                convertUtils.register(new Converter() {
                    public Object convert(Class type, Object value) {
                        return new KualiDecimal((String) value);
                    }
                }, KualiDecimal.class);
                BeanUtils.setProperty(object, field, values[pos]);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return object;
    }
}

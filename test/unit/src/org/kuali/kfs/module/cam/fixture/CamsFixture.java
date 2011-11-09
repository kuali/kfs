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

import java.util.Properties;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public enum CamsFixture {
    DATA_POPULATOR;
    private class KualiDecimalConverter implements Converter {
        @SuppressWarnings("unchecked")
        public Object convert(Class arg0, Object value) {
            return new KualiDecimal((String) value);
        }

    }

    private CamsFixture() {
        BeanUtilsBean instance = BeanUtilsBean.getInstance();
        ConvertUtilsBean convertUtils = instance.getConvertUtils();
        // Register Kuali Decimal Converter
        convertUtils.register(new KualiDecimalConverter(), KualiDecimal.class);
    }

    public <T> T buildTestDataObject(Class<? extends T> clazz, Properties properties, String propertyKey, String fieldNames, String delimiter) {
        T object;
        try {
            object = clazz.newInstance();
            String[] fields = fieldNames.split(delimiter, -1);
            String[] values = properties.getProperty(propertyKey).split(delimiter, -1);
            int pos = -1;
            for (String field : fields) {
                pos++;
                BeanUtils.setProperty(object, field, values[pos]);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return object;
    }
}

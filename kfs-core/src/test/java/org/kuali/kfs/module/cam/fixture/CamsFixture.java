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

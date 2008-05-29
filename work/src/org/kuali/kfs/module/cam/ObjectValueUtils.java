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
package org.kuali.module.cams.util;

import java.beans.PropertyDescriptor;

import org.apache.commons.beanutils.PropertyUtils;

public final class ObjectValueUtils {

    private ObjectValueUtils() {
    }

    public static void copySimpleProperties(Object origin, Object destination) {
        try {
            Object[] empty = new Object[] {};
            PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(origin.getClass());
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                if (propertyDescriptor.getReadMethod() != null && propertyDescriptor.getWriteMethod() != null) {
                    Object value = propertyDescriptor.getReadMethod().invoke(origin, empty);
                    if (value != null) {
                        propertyDescriptor.getWriteMethod().invoke(destination, value);
                    }
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Unexpected error while copying properties.", e);

        }
    }


}

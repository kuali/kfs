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
package org.kuali.kfs.module.cam.util;

import java.beans.PropertyDescriptor;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * This class is a utility which will do copying of attributes from a original object to destination object. Intention was to
 * provide a attribute value copying mechanism which is less expensive than ObjectUtils.deepCopy() method
 */
public final class ObjectValueUtils {

    private ObjectValueUtils() {
    }

    /**
     * This method uses simple getter/setter methods to copy object values from a original object to destination object
     * 
     * @param origin original object
     * @param destination destination object
     */
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

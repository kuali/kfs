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
package org.kuali.kfs.sys.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * A Map implementation which wraps a Java bean and can return values from that based on property Strings.  Unlike Apache Common's BeanMap, this Map can handle nested properties - though for the sake of that power,
 * the Map makes no effort to know its Set of keys at all - since to figure all of that out, it would need to recurse down and avoid property cycles, etc.  It does not know how many Entries it has.  Furthermore,
 * one cannot put any values into the Map and expect the underlying bean to have their own values changed.  It's really a read-only way to read nested properties from the underlying object via Map semantics.
 */
public class ReflectionMap implements Map<String, Object> {
    protected Object bean;
    protected PropertyUtilsBean propertyUtilsBean;

    public ReflectionMap(Object bean) {
        if (ObjectUtils.isNull(bean)) {
            throw new IllegalArgumentException("This cannot wrap a null object");
        }
        this.bean = bean;
        this.propertyUtilsBean = new PropertyUtilsBean(); // create our own beanUtilsBean to avoid struts injection of KNS classes
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("Cannot determine possible size of recursive structure");
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("Will not inspect every value within recursive structure");
    }

    @Override
    public boolean containsKey(Object key) {
        final Object value = get(key);
        return !ObjectUtils.isNull(value);
    }

    @Override
    public Object get(Object key) {
        final String keyAsString = (String)key;
        Object value;
        try {
            value = propertyUtilsBean.getProperty(this.bean, keyAsString);
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | IndexOutOfBoundsException ex) {
            // yep - we know we're swallowing the exception here.  However, we know that bean can't be null, so
            // to fit within regular map semantics, we're just going to return null for the missing key
            return null;
        }
        return value;
    }

    @Override
    public Object put(String key, Object value) {
        throw new UnsupportedOperationException("ReflectionMap is read-only");
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException("ReflectionMap is read-only");
    }

    @Override
    public void putAll(Map m) {
        throw new UnsupportedOperationException("ReflectionMap is read-only");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("ReflectionMap is read-only");
    }

    @Override
    public Set<String> keySet() {
        throw new UnsupportedOperationException("Cannot determine all keys within recursive structure");
    }

    @Override
    public Collection<Object> values() {
        throw new UnsupportedOperationException("Cannot determine all values within recursive structure");
    }

    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
        throw new UnsupportedOperationException("Cannot determine all entries within recursive structure");
    }
}

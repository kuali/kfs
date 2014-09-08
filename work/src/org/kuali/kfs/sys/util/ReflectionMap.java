/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.sys.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.rice.krad.util.ObjectUtils;

public class ReflectionMap implements Map<String, Object> {
    protected Object bean;

    public ReflectionMap(Object bean) {
        if (ObjectUtils.isNull(bean)) {
            throw new IllegalArgumentException("This cannot wrap a null object");
        }
        this.bean = bean;
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
            value = PropertyUtils.getProperty(this.bean, keyAsString);
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
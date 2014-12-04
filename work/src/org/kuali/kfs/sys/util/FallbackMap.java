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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A Map which wraps another Map, presumably one which is read-only (such as org.kuali.kfs.sys.util.ReflectionMap).  This Map holds a secondary Map which can be written to and read from; but if, in get, the key
 * is not present in the internally maintained Map, then the get falls back to the wrapped read-only Map to find the value
 */
public class FallbackMap<K, V> implements Map<K, V> {
    protected Map<K, V> frontMap;
    protected Map<K, V> backMap;

    public FallbackMap(Map<K,V> backMap) {
        frontMap = new HashMap<>();
        this.backMap = backMap;
    }

    @Override
    public int size() {
        return frontMap.size() + backMap.size();
    }

    @Override
    public boolean isEmpty() {
        return frontMap.isEmpty() && backMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if (frontMap.containsKey(key)) {
            return true;
        }
        return backMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        if (frontMap.containsValue(value)) {
            return true;
        }
        return backMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
        if (frontMap.containsKey(key)) {
            return frontMap.get(key);
        }
        return backMap.get(key);
    }

    @Override
    public V put(K key, V value) {
        return frontMap.put(key, value);
    }

    @Override
    public V remove(Object key) {
        if (frontMap.containsKey(key)) {
            return frontMap.remove(key);
        }
        return backMap.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        frontMap.putAll(m);
    }

    @Override
    public void clear() {
        frontMap.clear();
        backMap.clear();
    }

    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        keys.addAll(frontMap.keySet());
        keys.addAll(backMap.keySet());
        return keys;
    }

    @Override
    public Collection<V> values() {
        List<V> values = new ArrayList<>();
        values.addAll(frontMap.values());
        values.addAll(backMap.values());
        return values;
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        Set<java.util.Map.Entry<K, V>> entries = new HashSet<>();
        entries.addAll(frontMap.entrySet());
        entries.addAll(backMap.entrySet());
        return entries;
    }

}

/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.rice.kns.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.kuali.rice.kns.util.properties.PropertyTree;

/**
 * This class implements the Map interface for a Properties instance. Exports all properties from the given Properties instance as
 * constants, usable from jstl. Implements the Map interface (by delegating everything to the PropertyTree, which really implements
 * the Map methods directly) so that jstl can translate ${Constants.a} into a call to ConfigConstants.get( "a" ).
 * <p>
 * The contents of this Map cannot be changed once it has been initialized. Any calls to any of the Map methods made before the
 * propertyTree has been initialized (i.e. before setProperties has been called) will throw an IllegalStateException.
 * <p>
 * Jstl converts ${Constants.a.b.c} into get("a").get("b").get("c"), so the properties are stored in a PropertyTree, which converts
 * the initial set( "a.b.c", "value" ) into construction of the necessary tree structure to support get("a").get("b").get("c").
 * <p>
 * Implicitly relies on the assumption that the JSP will be calling toString() on the result of the final <code>get</code>, since
 * <code>get</code> can only return one type, and that type must be the complex one so that further dereferencing will be
 * possible.
 * 
 * 
 */

public abstract class JstlPropertyHolder implements Map {
    private PropertyTree propertyTree;

    /**
     * Default constructor
     */
    public JstlPropertyHolder() {
        propertyTree = null;
    }

    protected void setProperties(Map<String,String> properties) {
        propertyTree = new PropertyTree();
        propertyTree.setProperties(properties);
    }

    /**
     * Copies in the given propertyTree rather than building its own. Reasonably dangerous, since that tree might presumably be
     * modified, violating the readonlyness of this datastructure.
     * 
     * @param properties
     */
    protected void setPropertyTree(PropertyTree tree) {
        propertyTree = tree;
    }


    // delegated methods
    /**
     * @see org.kuali.rice.kns.util.properties.PropertyTree#get(java.lang.Object)
     */
    public Object get(Object key) {
        if (propertyTree == null) {
            throw new IllegalStateException("propertyTree has not been initialized");
        }
        return this.propertyTree.get(key);
    }

    /**
     * @see org.kuali.rice.kns.util.properties.PropertyTree#size()
     */
    public int size() {
        if (propertyTree == null) {
            throw new IllegalStateException("propertyTree has not been initialized");
        }
        return this.propertyTree.size();
    }

    /**
     * @see org.kuali.rice.kns.util.properties.PropertyTree#clear()
     */
    public void clear() {
        if (propertyTree == null) {
            throw new IllegalStateException("propertyTree has not been initialized");
        }
        this.propertyTree.clear();
    }

    /**
     * @see org.kuali.rice.kns.util.properties.PropertyTree#isEmpty()
     */
    public boolean isEmpty() {
        if (propertyTree == null) {
            throw new IllegalStateException("propertyTree has not been initialized");
        }
        return this.propertyTree.isEmpty();
    }

    /**
     * @see org.kuali.rice.kns.util.properties.PropertyTree#containsKey(java.lang.Object)
     */
    public boolean containsKey(Object key) {
        if (propertyTree == null) {
            throw new IllegalStateException("propertyTree has not been initialized");
        }
        return this.propertyTree.containsKey(key);
    }

    /**
     * @see org.kuali.rice.kns.util.properties.PropertyTree#containsValue(java.lang.Object)
     */
    public boolean containsValue(Object value) {
        if (propertyTree == null) {
            throw new IllegalStateException("propertyTree has not been initialized");
        }
        return this.propertyTree.containsValue(value);
    }

    /**
     * @see org.kuali.rice.kns.util.properties.PropertyTree#values()
     */
    public Collection values() {
        if (propertyTree == null) {
            throw new IllegalStateException("propertyTree has not been initialized");
        }
        return this.propertyTree.values();
    }

    /**
     * @see org.kuali.rice.kns.util.properties.PropertyTree#putAll(java.util.Map)
     */
    public void putAll(Map m) {
        if (propertyTree == null) {
            throw new IllegalStateException("propertyTree has not been initialized");
        }
        this.propertyTree.putAll(m);
    }

    /**
     * @see org.kuali.rice.kns.util.properties.PropertyTree#entrySet()
     */
    public Set entrySet() {
        if (propertyTree == null) {
            throw new IllegalStateException("propertyTree has not been initialized");
        }
        return this.propertyTree.entrySet();
    }

    /**
     * @see org.kuali.rice.kns.util.properties.PropertyTree#keySet()
     */
    public Set keySet() {
        if (propertyTree == null) {
            throw new IllegalStateException("propertyTree has not been initialized");
        }
        return this.propertyTree.keySet();
    }

    /**
     * @see org.kuali.rice.kns.util.properties.PropertyTree#remove(java.lang.Object)
     */
    public Object remove(Object key) {
        if (propertyTree == null) {
            throw new IllegalStateException("propertyTree has not been initialized");
        }
        return this.propertyTree.remove(key);
    }

    /**
     * @see org.kuali.rice.kns.util.properties.PropertyTree#put(java.lang.Object, java.lang.Object)
     */
    public Object put(Object key, Object value) {
        if (propertyTree == null) {
            throw new IllegalStateException("propertyTree has not been initialized");
        }
        return this.propertyTree.put(key, value);
    }
}

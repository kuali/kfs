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
package org.kuali.rice.kns.util.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * This class is a Recursive container for single- and multi-level key,value pairs. It relies on the assumption that the consumer
 * (presumably a JSP) will (implicitly) call toString at the end of the chain, which will return the String value of the chain's
 * endpoint.
 * 
 * It implements Map because that's how we fool jstl into converting "a.b.c" into get("a").get("b").get("c") instead of
 * getA().getB().getC()
 * 
 * Uses LinkedHashMap and LinkedHashSet because iteration order is now important.
 * 
 * 
 */
public class PropertyTree implements Map {
    private static Logger LOG = Logger.getLogger(PropertyTree.class);

    final boolean flat;
    final PropertyTree parent;
    String directValue;
    Map children;

    /**
     * Creates an empty instance with no parent
     */
    public PropertyTree() {
        this(false);
    }

    /**
     * Creates an empty instance with no parent. If flat is true, entrySet and size and the iterators will ignore entries in
     * subtrees.
     */
    public PropertyTree(boolean flat) {
        this.parent = null;
        this.children = new LinkedHashMap();
        this.flat = flat;
    }

    /**
     * Creates an empty instance with the given parent. If flat is true, entrySet and size and the iterators will ignore entries in
     * subtrees.
     */
    private PropertyTree(PropertyTree parent) {
        this.parent = parent;
        this.children = new LinkedHashMap();
        this.flat = parent.flat;
    }

    /**
     * Associates the given key with the given value. If the given key has multiple levels (consists of multiple strings separated
     * by '.'), the property value is stored such that it can be retrieved either directly, by calling get() and passing the entire
     * key; or indirectly, by decomposing the key into its separate levels and calling get() successively on the result of the
     * previous level's get. <br>
     * For example, given <br>
     * <code>
     * PropertyTree tree = new PropertyTree();
     * tree.set( "a.b.c", "something" );
     * </code> the following statements are
     * equivalent ways to retrieve the value: <br>
     * <code>
     * Object one = tree.get( "a.b.c" );
     * </code>
     * <code>
     * Object two = tree.get( "a" ).get( "b" ).get( "c" );
     * </code><br>
     * Note: since I can't have the get method return both a PropertyTree and a String, getting an actual String requires calling
     * toString on the PropertyTree returned by get.
     * 
     * @param key
     * @param value
     * @throws IllegalArgumentException if the key is null
     * @throws IllegalArgumentException if the value is null
     */
    public void setProperty(String key, String value) {
        validateKey(key);
        validateValue(value);

        if (parent == null) {
            LOG.debug("setting (k,v) (" + key + "," + value + ")");
        }

        if (StringUtils.contains(key, '.')) {
            String prefix = StringUtils.substringBefore(key, ".");
            String suffix = StringUtils.substringAfter(key, ".");

            PropertyTree node = getChild(prefix);
            node.setProperty(suffix, value);
        }
        else {
            PropertyTree node = getChild(key);
            node.setDirectValue(value);
        }
    }

    /**
     * Inserts all properties from the given Properties instance into this PropertyTree.
     * 
     * @param properties
     * @throws IllegalArgumentException if the Properties object is null
     * @throws IllegalArgumentException if a property's key is null
     * @throws IllegalArgumentException if a property's value is null
     */
    public void setProperties(Properties properties) {
        if (properties == null) {
            throw new IllegalArgumentException("invalid (null) Properties object");
        }

        for (Iterator i = properties.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();
            setProperty((String) e.getKey(), (String) e.getValue());
        }
    }

    public void setProperties(Map<String,String> properties) {
        if (properties == null) {
            throw new IllegalArgumentException("invalid (null) Properties object");
        }

        for (Iterator i = properties.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();
            setProperty((String) e.getKey(), (String) e.getValue());
        }
    }
    
    /**
     * Returns the PropertyTree object with the given key, or null if there is none.
     * 
     * @param key
     * @return
     * @throws IllegalArgumentException if the key is null
     */
    private PropertyTree getSubtree(String key) {
        validateKey(key);

        PropertyTree returnValue = null;
        if (StringUtils.contains(key, '.')) {
            String prefix = StringUtils.substringBefore(key, ".");
            String suffix = StringUtils.substringAfter(key, ".");

            PropertyTree child = (PropertyTree) this.children.get(prefix);
            if (child != null) {
                returnValue = child.getSubtree(suffix);
            }
        }
        else {
            returnValue = (PropertyTree) this.children.get(key);
        }

        return returnValue;
    }


    /**
     * @param key
     * @return the directValue of the PropertyTree associated with the given key, or null if there is none
     */
    public String getProperty(String key) {
        String propertyValue = null;

        PropertyTree subtree = getSubtree(key);
        if (subtree != null) {
            propertyValue = subtree.getDirectValue();
        }

        return propertyValue;
    }


    /**
     * @return an unmodifiable copy of the direct children of this PropertyTree
     */
    public Map getDirectChildren() {
        return Collections.unmodifiableMap(this.children);
    }


    /**
     * Returns the directValue of this PropertyTree, or null if there is none.
     * <p>
     * This is the hack that makes it possible for jstl to get what it needs when trying to retrive the value of a simple key or of
     * a complex (multi-part) key.
     */
    public String toString() {
        return getDirectValue();
    }

    /**
     * Sets the directValue of this PropertyTree to the given value.
     * 
     * @param value
     */
    private void setDirectValue(String value) {
        validateValue(value);

        this.directValue = value;
    }

    /**
     * @return directValue of this PropertyTree, or null if there is none
     */
    private String getDirectValue() {
        return this.directValue;
    }

    /**
     * @return true if the directValue of this PropertyTree is not null
     */
    private boolean hasDirectValue() {
        return (this.directValue != null);
    }

    /**
     * @return true if the this PropertyTree has children
     */
    private boolean hasChildren() {
        return (!this.children.isEmpty());
    }

    /**
     * Returns the PropertyTree associated with the given key. If none exists, creates a new PropertyTree associates it with the
     * given key, and returns it.
     * 
     * @param key
     * @return PropertyTree associated with the given key
     * @throws IllegalArgumentException if the given key is null
     */
    private PropertyTree getChild(String key) {
        validateKey(key);

        PropertyTree child = (PropertyTree) this.children.get(key);
        if (child == null) {
            child = new PropertyTree((PropertyTree)this);
            this.children.put(key, child);
        }

        return child;
    }

    /**
     * @param key
     * @throws IllegalArgumentException if the given key is not a String, or is null
     */
    private void validateKey(Object key) {
        if (!(key instanceof String)) {
            throw new IllegalArgumentException("invalid (non-String) key");
        }
        else if (key == null) {
            throw new IllegalArgumentException("invalid (null) key");
        }
    }

    /**
     * @param value
     * @throws IllegalArgumentException if the given value is not a String, or is null
     */
    private void validateValue(Object value) {
        if (!(value instanceof String)) {
            throw new IllegalArgumentException("invalid (non-String) value");
        }
        else if (value == null) {
            throw new IllegalArgumentException("invalid (null) value");
        }
    }


    // Map methods
    /**
     * Returns an unmodifiable Set containing all key,value pairs in this PropertyTree and its children.
     * 
     * @see java.util.Map#entrySet()
     */
    public Set entrySet() {
        return Collections.unmodifiableSet(collectEntries(null, this.flat).entrySet());
    }

    /**
     * Builds a HashMap containing all of the key,value pairs stored in this PropertyTree
     * 
     * @return
     */
    private Map collectEntries(String prefix, boolean flattenEntries) {
        LinkedHashMap entryMap = new LinkedHashMap();

        for (Iterator i = this.children.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();
            PropertyTree child = (PropertyTree) e.getValue();
            String childKey = (String) e.getKey();

            // handle children with values
            if (child.hasDirectValue()) {
                String entryKey = (prefix == null) ? childKey : prefix + "." + childKey;
                String entryValue = child.getDirectValue();

                entryMap.put(entryKey, entryValue);
            }

            // handle children with children
            if (!flattenEntries && child.hasChildren()) {
                String childPrefix = (prefix == null) ? childKey : prefix + "." + childKey;

                entryMap.putAll(child.collectEntries(childPrefix, flattenEntries));
            }
        }

        return entryMap;
    }

    /**
     * @return the number of keys contained, directly or indirectly, in this PropertyTree
     */
    public int size() {
        return entrySet().size();
    }

    /**
     * @see java.util.Map#isEmpty()
     */
    public boolean isEmpty() {
        return entrySet().isEmpty();
    }

    /**
     * Returns an unmodifiable Collection containing the values of all of the entries of this PropertyTree.
     * 
     * @see java.util.Map#values()
     */
    public Collection values() {
        ArrayList values = new ArrayList();

        Set entrySet = entrySet();
        for (Iterator i = entrySet.iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            values.add(e.getValue());
        }

        return Collections.unmodifiableList(values);
    }

    /**
     * Returns an unmodifiable Set containing the keys of all of the entries of this PropertyTree.
     * 
     * @see java.util.Map#keySet()
     */
    public Set keySet() {
        LinkedHashSet keys = new LinkedHashSet();

        Set entrySet = entrySet();
        for (Iterator i = entrySet.iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            keys.add(e.getKey());
        }

        return Collections.unmodifiableSet(keys);
    }

    /**
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    public boolean containsKey(Object key) {
        validateKey(key);

        boolean containsKey = false;

        Set entrySet = entrySet();
        for (Iterator i = entrySet.iterator(); !containsKey && i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            Object entryKey = e.getKey();
            containsKey = (entryKey != null) && entryKey.equals(key);
        }

        return containsKey;
    }

    /**
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    public boolean containsValue(Object value) {
        validateValue(value);

        boolean containsValue = false;

        Set entrySet = entrySet();
        for (Iterator i = entrySet.iterator(); !containsValue && i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            Object entryValue = e.getValue();
            containsValue = (entryValue != null) && entryValue.equals(value);
        }

        return containsValue;
    }

    /**
     * Traverses the tree structure until it finds the PropertyTree pointed to by the given key, and returns that PropertyTree
     * instance.
     * <p>
     * Only returns PropertyTree instances; if you want the String value pointed to by a given key, you must call toString() on the
     * returned PropertyTree (after verifying that it isn't null, of course).
     * 
     * @see java.util.Map#get(java.lang.Object)
     */
    public Object get(Object key) {
        validateKey(key);

        return getSubtree((String) key);
    }


    // unsupported operations
    /**
     * Unsupported, since you can't change the contents of a PropertyTree once it has been initialized.
     */
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported, since you can't change the contents of a PropertyTree once it has been initialized.
     */
    public void putAll(Map t) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported, since you can't change the contents of a PropertyTree once it has been initialized.
     */
    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported, since you can't change the contents of a PropertyTree once it has been initialized.
     */
    public Object put(Object key, Object value) {
        throw new UnsupportedOperationException();
    }
}

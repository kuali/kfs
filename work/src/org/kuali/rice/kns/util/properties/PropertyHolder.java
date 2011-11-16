/*
 * Copyright 2005-2007 The Kuali Foundation
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

import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.kuali.rice.krad.exception.DuplicateKeyException;

/**
 * This class is a Property container. It is able to load properties from various property-sources.
 * 
 * 
 */
public class PropertyHolder {
    private static Logger LOG = Logger.getLogger(PropertyHolder.class);
    
    Properties heldProperties;

    /**
     * Default constructor.
     */
    public PropertyHolder() {
        this.heldProperties = new Properties();
    }


    /**
     * @return true if this container currently has no properties
     */
    public boolean isEmpty() {
        return this.heldProperties.isEmpty();
    }

    /**
     * @param key
     * @return true if a property with the given key exists in this container
     * @throws IllegalArgumentException if the given key is null
     */
    public boolean containsKey(String key) {
        validateKey(key);

        return this.heldProperties.containsKey(key);
    }

    /**
     * @param key
     * @return the current value of the property with the given key, or null if no property exists with that key
     * @throws IllegalArgumentException if the given key is null
     */
    public String getProperty(String key) {
        validateKey(key);

        return this.heldProperties.getProperty(key);
    }


    /**
     * Associates the given value with the given key
     * 
     * @param key
     * @param value
     * @throws IllegalArgumentException if the given key is null
     * @throws IllegalArgumentException if the given value is null
     * @throws DuplicateKeyException if a property with the given key already exists
     */
    public void setProperty(String key, String value) {
	setProperty(null, key, value);	
    }
    
    /**
     * Associates the given value with the given key
     * 
     * @param source
     * @param key
     * @param value
     * @throws IllegalArgumentException if the given key is null
     * @throws IllegalArgumentException if the given value is null
     * @throws DuplicateKeyException if a property with the given key already exists
     */
    public void setProperty(PropertySource source, String key, String value) {
        validateKey(key);
        validateValue(value);

        if (containsKey(key)) {
            if (source != null && source instanceof FilePropertySource && ((FilePropertySource)source).isAllowOverrides()) {
                LOG.info("Duplicate Key: Override is enabled [key=" + key + ", new value=" + value + ", old value=" + this.heldProperties.getProperty(key) + "]");
            } else {
                throw new DuplicateKeyException("duplicate key '" + key + "'");
            }
        }
        this.heldProperties.setProperty(key, value);
    }

    /**
     * Removes the property with the given key from this container
     * 
     * @param key
     * @throws IllegalArgumentException if the given key is null
     */
    public void clearProperty(String key) {
        validateKey(key);

        this.heldProperties.remove(key);
    }


    /**
     * Copies all name,value pairs from the given PropertySource instance into this container.
     * 
     * @param source
     * @throws IllegalStateException if the source is invalid (improperly initialized)
     * @throws DuplicateKeyException the first time a given property has the same key as an existing property
     * @throws PropertiesException if unable to load properties from the given source
     */
    public void loadProperties(PropertySource source) {
        if (source == null) {
            throw new IllegalArgumentException("invalid (null) source");
        }

        Properties newProperties = source.loadProperties();

        for (Iterator i = newProperties.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            setProperty(source, key, newProperties.getProperty(key));
        }
    }

    /**
     * Removes all properties from this container.
     */
    public void clearProperties() {
        this.heldProperties.clear();
    }


    /**
     * @return iterator over the keys of all properties in this container
     */
    public Iterator getKeys() {
        return this.heldProperties.keySet().iterator();
    }


    /**
     * @param key
     * @throws IllegalArgumentException if the given key is null
     */
    private void validateKey(String key) {
        if (key == null) {
            throw new IllegalArgumentException("invalid (null) key");
        }
    }

    /**
     * @param value
     * @throws IllegalArgumentException if the given value is null
     */
    private void validateValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("invalid (null) value");
        }
    }


	public Properties getHeldProperties() {
		return heldProperties;
	}


	public void setHeldProperties(Properties heldProperties) {
		this.heldProperties = heldProperties;
	}
}

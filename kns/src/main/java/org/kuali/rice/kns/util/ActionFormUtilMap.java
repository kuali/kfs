/**
 * Copyright 2005-2014 The Kuali Foundation
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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.krad.keyvalues.KeyValuesFinder;
import org.kuali.rice.krad.keyvalues.PersistableBusinessObjectValuesFinder;
import org.kuali.rice.krad.util.KRADConstants;

import java.lang.reflect.Method;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Utility map for the action form to provide a way for calling functions through jstl.
 *
 *
 */
@SuppressWarnings("unchecked")
public class ActionFormUtilMap extends HashMap {
    private static final long serialVersionUID = 1L;
	private boolean cacheValueFinderResults;

    /**
     * This method parses from the key the actual method to run.
     *
     * @see java.util.Map#get(Object)
     */
    @Override
	public Object get(Object key) {
    	if (cacheValueFinderResults) {
    	    if (super.containsKey(key)) {
    		// doing a 2 step retrieval allows us to also cache the null key correctly
    		Object cachedObject = super.get(key);
    	    	return cachedObject;
    	    }
    	}
        String[] methodKey = StringUtils.split((String) key, KRADConstants.ACTION_FORM_UTIL_MAP_METHOD_PARM_DELIMITER);

        String methodToCall = methodKey[0];

        // handle method calls with more than one parameter
        Object[] methodParms = new Object[methodKey.length - 1];
        Class[] methodParmsPrototype = new Class[methodKey.length - 1];
        for (int i=1;i<methodKey.length;i++) {
            methodParms[i-1] = methodKey[i];
            methodParmsPrototype[i-1] = Object.class;
        }

        Method method = null;
        try {
            method = ActionFormUtilMap.class.getMethod(methodToCall, methodParmsPrototype);
        }
        catch (SecurityException e) {
            throw new RuntimeException("Unable to object handle on method given to ActionFormUtilMap: " + e.getMessage());
        }
        catch (NoSuchMethodException e1) {
            throw new RuntimeException("Unable to object handle on method given to ActionFormUtilMap: " + e1.getMessage());
        }

        Object methodValue = null;
        try {
            methodValue = method.invoke(this, methodParms);
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to invoke method " + methodToCall,e);
        }

        if (cacheValueFinderResults) {
            super.put(key, methodValue);
        }

        return methodValue;
    }

    /*
     * Will take in a class name parameter and attempt to create a KeyValueFinder instance, then call the finder to return a list of
     * KeyValue pairs. This is used by the htmlControlAttribute.tag to render select options from a given finder class specified in
     * the data dictionary.
     */
	public Object getOptionsMap(Object key) {
        List optionsList = new ArrayList();

        if (StringUtils.isBlank((String) key)) {
            return optionsList;
        }

        /*
         * the class name has . replaced with | in the jsp to prevent struts from treating each part of the class name as a property
         * substitute back here to get the correct name
         */
        key = StringUtils.replace((String) key, "|", ".");

        KeyValuesFinder finder;
        try {
            Class finderClass = Class.forName((String) key);
            finder = (KeyValuesFinder) finderClass.newInstance();
            optionsList = finder.getKeyValues();
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
        catch (InstantiationException e) {
            throw new RuntimeException(e.getMessage());
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        }

        return optionsList;
    }

    // Method added to keep backward compatibility for non-kimTypeId cases
    public Object getOptionsMap(Object key, Object boClass, Object keyAttribute, Object labelAttribute, Object includeKeyInLabel) {
    	return getOptionsMap(key, boClass, keyAttribute, labelAttribute, null, includeKeyInLabel );
    }

    /**
     * This method will take in a key parameter (values finder class name - in this case the generic
     * PersistableObjectValuesFinder) along with the related parameters required by this ValuesFinder,
     * and attempt to create a KeyValueFinder instance, then call the finder to return a list of
     * KeyValue pairs. This is used by the htmlControlAttribute.tag to render select options from
     * a given finder class specified in the data dictionary.
     *
     * @param key values finder class name
     * @param boClass BO class name
     * @param keyAttribute name of BO attribute for key
     * @param labelAttribute name of BO attribute for label
     * @param includeKeyInLabel whether to include the key in the label or not
     * @return list of KeyValue pairs
     */
	public Object getOptionsMap(Object key, Object boClass, Object keyAttribute, Object labelAttribute, Object includeBlankRow, Object includeKeyInLabel) {
        List optionsList = new ArrayList();

        if (StringUtils.isBlank((String) key)) {
            return optionsList;
        }

        /*
         * the class name has . replaced with | in the jsp to prevent struts from treating each part of the class name as a property
         * substitute back here to get the correct name
         */
        key = StringUtils.replace((String) key, "|", ".");

        KeyValuesFinder finder;
        try {
    		Class finderClass = Class.forName((String) key);
            finder = (KeyValuesFinder) finderClass.newInstance();
            if (finder instanceof PersistableBusinessObjectValuesFinder) {
                String businessObjectClassName = StringUtils.replace((String) boClass, "|", ".");
                Class businessObjectClass = Class.forName((String) businessObjectClassName);
                ((PersistableBusinessObjectValuesFinder) finder).setBusinessObjectClass(businessObjectClass);
                ((PersistableBusinessObjectValuesFinder) finder).setKeyAttributeName((String)keyAttribute);
                ((PersistableBusinessObjectValuesFinder) finder).setLabelAttributeName((String)labelAttribute);
                ((PersistableBusinessObjectValuesFinder) finder).setIncludeBlankRow(Boolean.parseBoolean((String)includeBlankRow));
                ((PersistableBusinessObjectValuesFinder) finder).setIncludeKeyInDescription(Boolean.parseBoolean((String)includeKeyInLabel));
            }

            optionsList = finder.getKeyValues();
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage(),e);
        }
        catch (InstantiationException e) {
            throw new RuntimeException(e.getMessage(),e);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(),e);
        }

        return optionsList;
    }

    /**
     * Encrypts a value passed from the ui.
     * @param value - clear text
     * @return String - encrypted text
     */
    public String encryptValue(Object value) {
        String encrypted = "";
        if (value != null) {
            encrypted = value.toString();
        }

        try {
            if(CoreApiServiceLocator.getEncryptionService().isEnabled()) {
                encrypted = CoreApiServiceLocator.getEncryptionService().encrypt(value);
            }
        }
        catch (GeneralSecurityException e) {
            throw new RuntimeException("Unable to encrypt value in action form: " + e.getMessage());
        }

        return encrypted;
    }

    public void setCacheValueFinderResults(boolean cacheValueFinderResults) {
        this.cacheValueFinderResults = cacheValueFinderResults;
    }


}

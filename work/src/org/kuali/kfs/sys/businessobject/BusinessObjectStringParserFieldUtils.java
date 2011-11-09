/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.sys.businessobject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;

/**
 * An abstract class which provides help in determining field lengths of business objects being parsed from Strings
 */
public abstract class BusinessObjectStringParserFieldUtils {
    protected org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(getClass());
    private Map<String, Integer> fieldLengthMap;
    private Map<String, Integer> fieldBeginningPositionMap;
    
    /**
     * @return a Map with attribute names as keys and lengths of how long those fields are specified to be in the DataDictionary as values
     */
    public Map<String, Integer> getFieldLengthMap() {
        if (fieldLengthMap == null) {
            initializeFieldLengthMap();
        }
        return fieldLengthMap;
    }
    
    /**
     * Calculates a map with the field length of all of the attributes of the class given by the
     * getBusinessObjectClass method
     */
    protected void initializeFieldLengthMap() {
        fieldLengthMap = new HashMap<String, Integer>();
        DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        List<AttributeDefinition> attributes = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(getBusinessObjectClass().getName()).getAttributes();

        for (AttributeDefinition attributeDefinition : attributes) {
            Integer fieldLength;
            fieldLength = dataDictionaryService.getAttributeMaxLength(getBusinessObjectClass(), attributeDefinition.getName());
            fieldLengthMap.put(attributeDefinition.getName(), fieldLength);
        }
    }
    
    /**
     * @return the class of the BusinessObject that this utility class will help parse from a String
     */
    public abstract Class<? extends BusinessObject> getBusinessObjectClass();

    /**
     * @return a Map with business object field names as keys and starting positions of each field in the String as values
     */
    public Map<String, Integer> getFieldBeginningPositionMap() {
        if (fieldBeginningPositionMap == null) {
            initializeFieldBeginningPositionMap();
        }
        return fieldBeginningPositionMap;
    }
    
    /**
     * Calculates the beginning positions of each field in the array returned by getOrderedProperties, based on
     * the length map calculated by getFieldLengthMap().
     */
    protected void initializeFieldBeginningPositionMap() {
        fieldBeginningPositionMap = new HashMap<String, Integer>();
        Map<String, Integer> lengthMap = getFieldLengthMap();
        
        int lengthTracker = 0;
   
        for (String property : getOrderedProperties()) {
            fieldBeginningPositionMap.put(property, new Integer(lengthTracker));
            if (LOG.isDebugEnabled()) {
                LOG.debug("Finding position for property: "+property+"; length = "+lengthMap.get(property));
            }
            lengthTracker += lengthMap.get(property).intValue();
        }
    }
    
    /**
     * @return an array of String names of fields in a business object in the order they will show up in the String to be parsed
     */
    public abstract String[] getOrderedProperties();
}

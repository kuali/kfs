/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.sys.businessobject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.service.DataDictionaryService;

/**
 * An abstract class which provides help in determining field lengths of business objects being parsed from Strings
 */
public abstract class BusinessObjectStringParserFieldUtils {
    /**
     * Calculates a map with the field length of all of the attributes of the class given by the
     * getBusinessObjectClass method
     * @return a Map with attribute names as keys and lengths as values
     */
    public Map<String, Integer> getFieldLengthMap() {
        Map<String, Integer> fieldLengthMap = new HashMap<String, Integer>();
        DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        List<AttributeDefinition> attributes = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(getBusinessObjectClass().getName()).getAttributes();

        for (AttributeDefinition attributeDefinition : attributes) {
            Integer fieldLength;
            fieldLength = dataDictionaryService.getAttributeMaxLength(OriginEntryFull.class, attributeDefinition.getName());
            fieldLengthMap.put(attributeDefinition.getName(), fieldLength);
        }
        return fieldLengthMap;
    }
    
    /**
     * @return the class of the BusinessObject that this utility class will help parse from a String
     */
    public abstract Class<? extends BusinessObject> getBusinessObjectClass();

    /**
     * Calculates the beginning positions of each field in the array returned by getOrderedProperties, based on
     * the length map calculated by getFieldLengthMap()
     * @return a Map with business object field names as keys and starting positions as values
     */
    public Map<String, Integer> getFieldBeginningPositionMap() {
        Map<String, Integer> positionMap = new HashMap<String, Integer>();
        Map<String, Integer> lengthMap = getFieldLengthMap();
        
        int lengthTracker = 0;
   
        for (String property : getOrderedProperties()) {
            positionMap.put(property, new Integer(lengthTracker));
            lengthTracker += lengthMap.get(property).intValue();
        }
        return positionMap;
    }
    
    /**
     * @return an array of String names of fields in a business object in the order they will show up in the String to be parsed
     */
    public abstract String[] getOrderedProperties();
}

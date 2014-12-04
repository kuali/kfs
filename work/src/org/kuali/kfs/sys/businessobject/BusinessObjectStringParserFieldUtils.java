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

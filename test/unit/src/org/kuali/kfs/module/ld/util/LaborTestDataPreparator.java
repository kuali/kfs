/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ld.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.TestDataPreparator;

public class LaborTestDataPreparator {
    /**
     * build a list of LaborOriginEntry objects from the given properties. The default fieldNames and deliminator are used.
     * 
     * @param properties the given properties that contain the data that can be used to populate LaborOriginEntry objects
     * @param propertyKeyPrefix the data with the given key prefix can be used to construct the return objects
     * @param fieldNames the field names of the data columns
     * @param deliminator the deliminator that is used to separate the field from each other
     * @param numberOfData the number of the data matching the search criteria
     * @return a list of LaborOriginEntry objects from the given properties
     */
    public static List<LaborOriginEntry> getLaborOriginEntryList(Properties properties, String propertyKeyPrefix, int numberOfInputData, OriginEntryGroup group) {
        String fieldNames = properties.getProperty(TestDataPreparator.DEFAULT_FIELD_NAMES);
        String deliminator = properties.getProperty(TestDataPreparator.DEFAULT_DELIMINATOR);
        return getLaborOriginEntryList(properties, propertyKeyPrefix, fieldNames, deliminator, numberOfInputData, group);
    }

    /**
     * build a list of LaborOriginEntry objects from the given properties
     * 
     * @param properties the given properties that contain the data that can be used to populate LaborOriginEntry objects
     * @param propertyKeyPrefix the data with the given key prefix can be used to construct the return objects
     * @param fieldNames the field names of the data columns
     * @param deliminator the deliminator that is used to separate the field from each other
     * @param numberOfData the number of the data matching the search criteria
     * @return a list of LaborOriginEntry objects from the given properties
     */
    public static List<LaborOriginEntry> getLaborOriginEntryList(Properties properties, String propertyKeyPrefix, String fieldNames, String deliminator, int numberOfInputData, OriginEntryGroup group) {
        List<LaborOriginEntry> inputDataList = new ArrayList<LaborOriginEntry>();
        for (int i = 1; i <= numberOfInputData; i++) {
            String propertyKey = propertyKeyPrefix + i;
            LaborOriginEntry inputData = new LaborOriginEntry();
            ObjectUtil.populateBusinessObject(inputData, properties, propertyKey, fieldNames, deliminator);
            inputData.setEntryGroupId(group.getId());
            inputDataList.add(inputData);
        }
        return inputDataList;
    }
}

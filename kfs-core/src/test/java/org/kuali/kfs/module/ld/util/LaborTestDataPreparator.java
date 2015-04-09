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

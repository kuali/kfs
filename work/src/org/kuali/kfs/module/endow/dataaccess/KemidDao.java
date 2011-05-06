/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.dataaccess;

import java.util.List;

import org.kuali.kfs.module.endow.businessobject.KEMID;

public interface KemidDao {
    
    /**
     * Gets KEMIDs by kemids
     * 
     * @param kemids
     * @param endowmentOption
     * @param currentDate
     * @return
     */
    public List<KEMID> getKemidRecordsByIds(List<String> kemids, String endowmentOption, String closedIndicator); 
    
    /**
     * Gets kemids by the given criteria
     * 
     * @param attributeName
     * @param values
     * @param endowmentOption
     * @return
     */
    public List<String> getKemidsByAttributeWithEndowmentOption(String attributeName, List<String> values, String endowmentOption, String closedIndicator);
    
    /**
     * Gets kemids by the given attribute value strings
     * 
     * @param attributeName
     * @param values
     * @return
     */
    public List<String> getKemidsByAttribute(String attributeName, List<String> values);  
    
    /**
     * Gets all the values of the attribute by the given value strings 
     * 
     * @param attributeName
     * @param values
     * @return
     */
    public List<String> getAttributeValues(String attributeName, List<String> values);
}

/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.dao;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.dao.OriginEntryDao;
import org.kuali.module.labor.bo.LaborOriginEntry;

public interface LaborOriginEntryDao extends OriginEntryDao{

        /**
     * Get origin entries that belong to the given groups
     * @param groups the given origin entry groups
     * @return origin entries that belong to the given groups
     */
    Iterator<LaborOriginEntry> getEntriesByGroups(Collection<OriginEntryGroup> groups);

    /**
     * Get the origin entries that belong to the given group in either the consolidation manner
     * @param group the given group
     * @return the origin entries that belong to the given group in either the consolidation manner
     */
    Iterator<Object[]> getConsolidatedEntriesByGroup(OriginEntryGroup group);

    /**
     * get the count of the origin entry collection in the given groups 
     * @param groups the given groups
     * @return the count of the origin entry collection in the given group
     */
    int getCountOfEntriesInGroups(Collection<OriginEntryGroup> groups);
    
    Collection<LaborOriginEntry> testingLaborGetAllEntries(); 
    
    Iterator<LaborOriginEntry> getLaborEntriesByGroup(OriginEntryGroup oeg, int sort);
    
    Collection getMatchingEntriesByCollection(Map searchCriteria); 
    
    
}
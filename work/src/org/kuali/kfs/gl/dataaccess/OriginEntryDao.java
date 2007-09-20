/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.module.gl.dao;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.gl.bo.OriginEntryFull;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;

/**
 * 
 */
public interface OriginEntryDao {
    public static final int SORT_DOCUMENT = 1;
    public static final int SORT_ACCOUNT = 2;
    public static final int SORT_REPORT = 3;
    public static final int SORT_LISTING_REPORT = 4; 

    /**
     * Get the total amount of transactions in a group
     */
    public KualiDecimal getGroupTotal(Integer groupId, boolean isCredit);

    /**
     * Get count of transactions in a group
     */
    public Integer getGroupCount(Integer groupId);

    /**
     * Get the counts of rows of all the origin entry groups
     * 
     * @return iterator of Object[] {[BigDecimal id,BigDecimal count]}
     */
    public Iterator getGroupCounts();

    /**
     * Delete an entry
     * 
     * @param oe Entry to delete
     */
    public void deleteEntry(OriginEntry oe);

    /**
     * Return an iterator to all documents in a group
     * 
     * @param oeg Group
     * @return Iterator of Object[] documents in the specified group
     */
    public Iterator getDocumentsByGroup(OriginEntryGroup oeg);

    /**
     * Return an iterator to all the entries in a group
     * 
     * @param oeg Group
     * @return Iterator of entries in the specified group
     */
    public <T> Iterator<T> getEntriesByGroup(OriginEntryGroup oeg, int sort);

    /**
     * Get bad balance entries
     * 
     * @param groups
     * @return
     */
    public Iterator<OriginEntryFull> getBadBalanceEntries(Collection groups);

    /**
     * Collection of entries that match criteria
     * 
     * @param searchCriteria Map of field, value pairs
     * @return collection of entries
     */
    public Collection<OriginEntryFull> getMatchingEntriesByCollection(Map searchCriteria);

    /**
     * Iterator of entries that match criteria
     * 
     * @param searchCriteria Map of field, value pairs
     * @return collection of entries
     */
    public Iterator getMatchingEntries(Map searchCriteria);

    /**
     * Delete entries that match criteria
     * 
     * @param searchCriteria Map of field, value pairs
     */
    public void deleteMatchingEntries(Map searchCriteria);

    /**
     * Delete all the groups in the list. This will delete the entries. The OriginEntryGroupDao has a method to delete the groups
     * 
     * @param groups Groups to be deleted
     */
    public void deleteGroups(Collection<OriginEntryGroup> groups);

    /**
     * Save origin entry
     * 
     * @param entry entry to save
     */
    public void saveOriginEntry(OriginEntry entry);


    public OriginEntryFull getExactMatchingEntry(Integer entryId);

    /**
     * get the summarized information of the entries that belong to the entry groups with the given group ids
     * 
     * @param groupIdList the ids of origin entry groups
     * @return a set of summarized information of the entries within the specified groups
     */
    public Iterator getSummaryByGroupId(Collection groupIdList);

    /**
     * This method should only be used in unit tests. It loads all the gl_origin_entry_t rows in memory into a collection. This
     * won't scale for production.
     * 
     * @return
     */
    public Collection testingGetAllEntries();
    
    /**
     * get the summarized information of poster input entries that belong to the entry groups with the given group id list
     * 
     * @param groups the origin entry groups
     * @return a set of summarized information of poster input entries within the specified groups
     */       
    public Iterator getPosterOutputSummaryByGroupId(Collection groups);
    
    /**
     * force current persistence broker to clear its cache
     */
    public void clearCache();
}

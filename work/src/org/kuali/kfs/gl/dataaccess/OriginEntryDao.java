/*
 * Copyright 2005-2006 The Kuali Foundation
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
package org.kuali.kfs.gl.dataaccess;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.gl.businessobject.OriginEntryInformation;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * 
 */
public interface OriginEntryDao {
    /**
     * Sort origin entries by document id
     */
    public static final int SORT_DOCUMENT = 1;
    /**
     * Sort origin entries by account number
     */
    public static final int SORT_ACCOUNT = 2;
    /**
     * Sort origin entries by standard report order (by document type code and system origination code)
     */
    public static final int SORT_REPORT = 3;
    /**
     * Sort origin entries by listing report order (by fiscal year, chart code, account number, etc.: the order you see them in in generated text files)
     */
    public static final int SORT_LISTING_REPORT = 4;

    /**
     * Get the total amount of transactions in a group
     * @param the id of the origin entry group to total
     * @param isCredit whether the total should be of credits or not
     * @return the sum of all queried origin entries
     */
    public KualiDecimal getGroupTotal(Integer groupId, boolean isCredit);

    /**
     * Counts the number of entries in a group
     * @param the id of an origin entry group
     * @return the count of the entries in that group
     */
    public Integer getGroupCount(Integer groupId);

    /**
     * Counts of rows of all the origin entry groups
     * 
     * @return iterator of Object[] {[BigDecimal id,BigDecimal count]}
     */
    public Iterator getGroupCounts();

    /**
     * Delete an entry
     * 
     * @param oe Entry to delete
     */
    public void deleteEntry(OriginEntryInformation oe);

    /**
     * Return an iterator to all document keys reference by origin entries in a given group
     * 
     * @param oeg Group the origin entry group to find entries in, by origin entry
     * @return Iterator of java.lang.Object[] with report data about all of the distinct document numbers/type code/origination code combinations of origin entries in the group
     */
    public Iterator getDocumentsByGroup(OriginEntryGroup oeg);

    /**
     * Return an iterator to all the entries in a group
     * 
     * @param oeg the origin entry group to get entries in
     * @param sort the Sort Order (one of the Sort Orders defined by the SORT_ constants defined in this class)
     * @return Iterator of entries in the specified group
     */
    public <T> Iterator<T> getEntriesByGroup(OriginEntryGroup oeg, int sort);

    /**
     * Get bad balance entries; bad because a) they have invalid balance types, and b) because they revert the balances back to their stone age selves
     * 
     * @param groups a Collection of groups to remove bad entries in
     * @return an Iterator of no good, won't use, bad balance entries
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
     * @param groups a Collection of Origin Entry Groups to delete entries in
     */
    public void deleteGroups(Collection<OriginEntryGroup> groups);

    /**
     * Finds an entry for the given entryId, or returns a newly created on
     * 
     * @param entryId an entry id to find an entry for
     * @return the entry for the given entry id, or a newly created entry
     */
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
     * @return a Collection with every single origin entry in the database
     */
    public Collection testingGetAllEntries();

    /**
     * get the summarized information of poster input entries that belong to the entry groups with the given group id list
     * 
     * @param groups the origin entry groups
     * @return a set of summarized information of poster input entries within the specified groups
     */
    public Iterator getPosterOutputSummaryByGroupId(Collection groups);

}

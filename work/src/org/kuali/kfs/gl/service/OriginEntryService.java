/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.gl.service;

import java.io.BufferedOutputStream;
import java.sql.Date;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.module.gl.bo.OriginEntryFull;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.util.LedgerEntryHolder;
import org.kuali.module.gl.util.OriginEntryStatistics;
import org.kuali.module.gl.util.PosterOutputSummaryEntry;

/**
 * An interface of methods to interact with Origin Entries
 */
public interface OriginEntryService {

    /**
     * Get statistics from a group
     * @param groupId the id of a group of origin entries
     * @return a collection of OriginEntryStatistics
     */
    public OriginEntryStatistics getStatistics(Integer groupId);

    /**
     * Copy a set of entries into a new group
     * 
     * @param date the date that the copied entries should list as their post date
     * @param sourceCode the source code of the origin entry group to create
     * @param valid whether the new group should be considered valid
     * @param process whether the new group should be ready to be processed
     * @param scrub whether the new group should be processed by the scrubber
     * @param entries a Collection of entries to copy
     * @return a new origin entry full of copied entries
     */
    public OriginEntryGroup copyEntries(Date date, String sourceCode, boolean valid, boolean process, boolean scrub, Collection<OriginEntryFull> entries);

    /**
     * Copy a set of entries into a new group. This method can use less space than the method that takes in a collection, because
     * iterators can be implemented to load data one chunk at a time, similar to how java ResultSets work.
     * 
     * @param date the date that the copied entries should list as their post date
     * @param sourceCode the source code of the origin entry group to create
     * @param valid whether the new group should be considered valid
     * @param process whether the new group should be ready to be processed
     * @param scrub whether the new group should be processed by the scrubber
     * @param entries a Iterator of entries to copy
     * @return a new origin entry full of copied entries
     */
    public OriginEntryGroup copyEntries(Date date, String sourceCode, boolean valid, boolean process, boolean scrub, Iterator<OriginEntryFull> entries);

    /**
     * Delete entry
     * 
     * @param oe Entry to delete
     */
    public void delete(OriginEntryFull oe);

    /**
     * This returns all of distinct primary key sets of documents that created origin entries that exist
     * in the given origin entry group.  It returns this information in OriginEntryFull objects
     * that just don't have any other information besides the document keys (doc number, doc type code,
     * and origination code) filled in.
     * 
     * @param oeg the group with the origin entries to get the documents of
     * @return Collection to qualifying documents
     */
    public Collection<OriginEntryFull> getDocumentsByGroup(OriginEntryGroup oeg);

    /**
     * Return all entries for a group sorted by account number for the error
     * 
     * @param oeg an origin entry group to get entries from
     * @return an Iterator of origin entries sorted by account number
     */
    public Iterator<OriginEntryFull> getEntriesByGroupAccountOrder(OriginEntryGroup oeg);

    /**
     * Return all entries for a group sorted for display on the pending entry report.
     * 
     * @param oeg a origin entry group to get entries from
     * @return an Iterator of origin entries sorted in the order needed for an origin entry report (fiscal year, chart, account, sub account, object, sub object)
     */
    public Iterator<OriginEntryFull> getEntriesByGroupReportOrder(OriginEntryGroup oeg);

    /**
     * Return all entries for a group sorted across the columns in report from left to right.
     * 
     * @param oeg an origin entry group to get entries from
     * @return an Iterator of origin entries sorted in the proper order
     */
    public Iterator<OriginEntryFull> getEntriesByGroupListingReportOrder(OriginEntryGroup oeg);

    /**
     * Return all entries for the groups where the balance type is empty
     * 
     * @param groups a Collection of groups to look through all the entries of
     * @return an Iterator of entries without balance types
     */
    public Iterator<OriginEntryFull> getBadBalanceEntries(Collection groups);

    /**
     * Return all the entries in a specific group
     * 
     * @param oeg Group used to select entries
     * @return Iterator to all the entires
     */
    public Iterator<OriginEntryFull> getEntriesByGroup(OriginEntryGroup oeg);

    /**
     * Return all the entries for a specific document in a specific group
     * 
     * @param oeg an origin entry group to find entries in
     * @param documentNumber the document number of entries to select
     * @param documentTypeCode the document type of entries to select
     * @param originCode the origination code of entries to select
     * @return iterator to all the qualifying entries
     */
    public Iterator<OriginEntryFull> getEntriesByDocument(OriginEntryGroup oeg, String documentNumber, String documentTypeCode, String originCode);

    /**
     * Take a generic transaction and save it as an origin entry in a specific group
     * 
     * @param tran transaction to save
     * @param group group to save the transaction
     */
    public void createEntry(Transaction tran, OriginEntryGroup group);

    /**
     * Save an origin entry
     * 
     * @param entry the entry to save
     */
    public void save(OriginEntryFull entry);

    /**
     * Export all origin entries in a group to a flat text file
     * 
     * @param filename Filename to save the text
     * @param groupId Group to save
     */
    public void exportFlatFile(String filename, Integer groupId);

    /**
     * Load a flat file of transations into the origin entry table (creating a new origin entry group in the process)
     * 
     * @param filename Filename with the text
     * @param groupSourceCode Source of the new group
     * @param valid Valid flag for new group
     * @param processed Process flag for new group
     * @param scrub Scrub flag for new group
     */
    public void loadFlatFile(String filename, String groupSourceCode, boolean valid, boolean processed, boolean scrub);

    /**
     * Write all of the origin entries in a group to an output stream
     * 
     * @param groupId the id of the origin entry group to get entries from
     * @param bw the output stream to dump the entries as text to
     */
    public void flatFile(Integer groupId, BufferedOutputStream bw);

    /**
     * writes out a list of origin entries to an output stream.
     * 
     * @param entries an Iterator of entries to save as text
     * @param bw the output stream to write origin entries to
     */
    public void flatFile(Iterator<OriginEntryFull> entries, BufferedOutputStream bw);

    /**
     * get the summarized information of the entries that belong to the entry groups with the given group id list
     * 
     * @param groupIdList the origin entry groups
     * @return a set of summarized information of the entries within the specified group
     */
    public LedgerEntryHolder getSummaryByGroupId(Collection groupIdList);

    /**
     * Finds all origin entries matching certain criteria; basically a catch-all origin entry search
     * 
     * @param searchCriteria the criteria to be used in forming a query
     * @return a Collection of qualifying origin entries
     */
    public Collection<OriginEntryFull> getMatchingEntriesByCollection(Map searchCriteria);

    /**
     * Retrieves a list of origin entries that are in a given group
     * 
     * @param groupId the id of the group to get all entries from
     * @return a List of Origin Entries
     */
    public List<OriginEntryFull> getEntriesByGroupId(Integer groupId);

    /**
     * Returns the entry with the given id
     * 
     * @param entryId the id of the entry to retrieve
     * @return the origin entry if found, or null otherwise
     */
    public OriginEntryFull getExactMatchingEntry(Integer entryId);

    /**
     * get the summarized information of poster input entries that belong to the entry groups with the given group id list
     * 
     * @param groupIdList the origin entry groups
     * @return a map of summarized information of poster input entries within the specified groups
     */
    public Map<String, PosterOutputSummaryEntry> getPosterOutputSummaryByGroupId(Collection groupIdList);

    /**
     * Get count of transactions in a group
     * @param groupId the group to get the count of entries from
     * @return a count of entries
     */
    public Integer getGroupCount(Integer groupId);
}

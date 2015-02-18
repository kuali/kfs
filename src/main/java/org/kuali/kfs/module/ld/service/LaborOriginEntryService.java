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
package org.kuali.kfs.module.ld.service;

import java.io.BufferedReader;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.businessobject.OriginEntryStatistics;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;

/**
 * Defines methods that must be implemented by classes providing a LaborOriginEntryServiceImpl.
 */
public interface LaborOriginEntryService {

    /**
     * Get statistics from a group
     */
    //public OriginEntryStatistics getStatistics(Integer groupId);
    public OriginEntryStatistics getStatistics(String fileName);

    /**
     * Copy a set of entries into a new group
     */
    //public OriginEntryGroup copyEntries(Date date, String sourceCode, boolean valid, boolean process, boolean scrub, Collection<LaborOriginEntry> entries);

    /**
     * Copy a set of entries into a new group
     */
    //public OriginEntryGroup copyEntries(Date date, String sourceCode, boolean valid, boolean process, boolean scrub, Iterator<LaborOriginEntry> entries);

    /**
     * Delete entry
     * 
     * @param oe Entry to delete
     */
    //public void delete(LaborOriginEntry oe);

    /**
     * Return all documents in a group
     * 
     * @param oeg Group used to select documents
     * @return Collection to all documents
     */
    //public Collection<LaborOriginEntry> getDocumentsByGroup(OriginEntryGroup oeg);

    /**
     * Return all entries for a group sorted by account number for the error
     * 
     * @param oeg
     * @return
     */
    //public Iterator<LaborOriginEntry> getEntriesByGroupAccountOrder(OriginEntryGroup oeg);

    /**
     * Return all entries for a group sorted for display on the pending entry report.
     * 
     * @param oeg
     * @return
     */
    //public Iterator<LaborOriginEntry> getEntriesByGroupReportOrder(OriginEntryGroup oeg);

    /**
     * Return all entries for a group sorted across the columns in report from left to right.
     * 
     * @param oeg
     * @return
     */
    //public Iterator<LaborOriginEntry> getEntriesByGroupListingReportOrder(OriginEntryGroup oeg);

    /**
     * Return all entries for the groups where the balance type is empty
     * 
     * @param groups
     * @return
     */
    //public Iterator<LaborOriginEntry> getBadBalanceEntries(Collection groups);

    /**
     * Return all the entries for a specific document in a specific group
     * 
     * @param oeg Group selection
     * @param documentNumber Document number selection
     * @param documentTypeCode Document type selection
     * @param originCode Origin Code selection
     * @return iterator to all the entries
     */
    //public Collection<LaborOriginEntry> getEntriesByDocument(OriginEntryGroup oeg, String documentNumber, String documentTypeCode, String originCode);

    /**
     * Take a generic transaction and save it as an origin entry in a specific group
     * 
     * @param tran transaction to save
     * @param group group to save the transaction
     */
    //public void createEntry(LaborTransaction laborTran, OriginEntryGroup group);

    /**
     * Save an laborOrigin entry
     * 
     * @param entry
     */
    //public void save(LaborOriginEntry entry);

    /**
     * Export all origin entries in a group to a flat text file
     * 
     * @param filename Filename to save the text
     * @param groupId Group to save
     */
    //public void exportFlatFile(String filename, Integer groupId);

    /**
     * Load a flat file of transations into the origin entry table
     * 
     * @param filename Filename with the text
     * @param groupSourceCode Source of the new group
     * @param valid Valid flag for new group
     * @param processed Process flag for new group
     * @param scrub Scrub flag for new group
     */
    //public void loadFlatFile(String filename, String groupSourceCode, boolean valid, boolean processed, boolean scrub);

    /**
     * Send data to an output stream
     * 
     * @param groupId
     * @param bw
     */
    //public void flatFile(Integer groupId, BufferedOutputStream bw);

    /**
     * Return all entries by searchCriteria
     * 
     * @param searchCriteria
     */
    //public Collection getMatchingEntriesByCollection(Map searchCriteria);

    /**
     * Return a matched entry with entryId
     * 
     * @param entryId
     */
    //public LaborOriginEntry getExactMatchingEntry(Integer entryId);


    /**
     * Get origin entries that belong to the given group
     * 
     * @param group the given origin entry group
     * @return origin entries that belong to the given group
     */
    //public Iterator<LaborOriginEntry> getEntriesByGroup(OriginEntryGroup group);

    /**
     * Get origin entries that belong to the given group
     * 
     * @param group the given origin entry group
     * @return origin entries that belong to the given group
     */
    //public Collection<LaborOriginEntry> getEntryCollectionByGroup(OriginEntryGroup group);

    /**
     * Get origin entries that belong to the given groups
     * 
     * @param groups the given origin entry groups
     * @return origin entries that belong to the given groups
     */
    //public Iterator<LaborOriginEntry> getEntriesByGroups(Collection<OriginEntryGroup> groups);

    /**
     * Get the origin entries that belong to the given group in either the consolidation manner or not
     * 
     * @param group the given group
     * @param isConsolidated the flag that indicates if return origin entries in either the consolidation manner or not
     * @return the origin entries that belong to the given group in either the consolidation manner or not
     */
    //public Iterator<LaborOriginEntry> getEntriesByGroup(OriginEntryGroup group, boolean isConsolidated);

    /**
     * Get the origin entries that belong to the given group in either the consolidation manner or not
     * 
     * @param group the given group
     * @param isConsolidated the flag that indicates if return origin entries in either the consolidation manner or not
     * @return the origin entries that belong to the given group in either the consolidation manner or not
     */
    //public Collection<LaborOriginEntry> getConsolidatedEntryCollectionByGroup(OriginEntryGroup group);

    /**
     * get the summarized information of the entries that belong to the given entry groups
     * 
     * @param groups the origin entry groups
     * @return a set of summarized information of the entries within the specified groups
     */
    //public LedgerEntryHolder getSummariedEntriesByGroups(Collection<OriginEntryGroup> groups);

    /**
     * get the summarized information of poster input entries that belong to the given entry groups
     * 
     * @param groups the origin entry groups
     * @return a map of summarized information of poster input entries within the specified groups
     */
    //public Map<String, PosterOutputSummaryEntry> getPosterOutputSummaryByGroups(Collection<OriginEntryGroup> groups);

    /**
     * get the count of the origin entry collection in the given groups
     * 
     * @param groups the given groups
     * @return the count of the origin entry collection in the given group
     */
    //public int getCountOfEntriesInGroups(Collection<OriginEntryGroup> groups);

    /**
     * get all entries with groupId
     * 
     * @param groupId
     */
    //public List<LaborOriginEntry> getEntriesByGroupId(Integer groupId);
    
    public Map getEntriesByGroupIdWithPath(String fileNameWithPath, List<LaborOriginEntry> originEntryList);

    public Map getEntriesByBufferedReader(BufferedReader inputBufferedReader, List<LaborOriginEntry> originEntryList);
    /**
     * get the summarized information of the entries that belong to the entry groups with the given group id list
     * 
     * @param groupIdList the origin entry groups
     * @return a set of summarized information of the entries within the specified group
     */
    //public LedgerEntryHolder getSummaryByGroupId(Collection groupIdList);

    /**
     * get the count of the origin entry collection in the given group
     * 
     * @param group the given group
     * @return the count of the origin entry collection in the given group
     */
    //public int getCountOfEntriesInSingleGroup(OriginEntryGroup group);
    
    /**
     * Get all the unscrubbed backup groups for Labor
     * 
     * @param backupDate the date all groups created on or before should be return to be backed up
     * @return a Collection of labor origin entry groups to backup
     */
    //public Collection getLaborBackupGroups(Date backupDate);
    
    /**
     * Counts the number of entries in a group
     * @param the id of an origin entry group
     * @return the count of the entries in that group
     */
    //public Integer getGroupCount(Integer groupId);
    
    public Integer getGroupCount(String fileName);
}

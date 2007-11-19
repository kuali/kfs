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
package org.kuali.module.gl.service.impl;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.bo.OriginEntryFull;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.OriginEntryDao;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.util.LedgerEntry;
import org.kuali.module.gl.util.LedgerEntryHolder;
import org.kuali.module.gl.util.OriginEntryStatistics;
import org.kuali.module.gl.util.PosterOutputSummaryEntry;
import org.springframework.transaction.annotation.Transactional;

/**
 * The base implementation of OriginEntryService
 */
@Transactional
public class OriginEntryServiceImpl implements OriginEntryService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OriginEntryServiceImpl.class);

    private static final String ENTRY_GROUP_ID = "entryGroupId";
    private static final String FINANCIAL_DOCUMENT_TYPE_CODE = "financialDocumentTypeCode";
    private static final String FINANCIAL_SYSTEM_ORIGINATION_CODE = "financialSystemOriginationCode";

    private OriginEntryDao originEntryDao;
    private OriginEntryGroupService originEntryGroupService;

    private DateTimeService dateTimeService;

    /**
     * Sets the originEntryDao attribute
     * @param originEntryDao the implementation of OriginEntryDao to set
     */
    public void setOriginEntryDao(OriginEntryDao originEntryDao) {
        this.originEntryDao = originEntryDao;
    }

    /**
     * Sets the originEntryGroupService attribute
     * @param originEntryGroupService the implementation of OriginEntryGroupService to set
     */
    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }

    /**
     * Constructs a new instance of OriginEntryServiceImpl
     */
    public OriginEntryServiceImpl() {
        super();
    }

    /**
     * Retrieves teh credit total, debit total, and row count for a given origin entry group.
     * @param groupId the id of the group to find statistics for
     * @return an OriginEntryStatistics object with the statistics for the group
     * @see org.kuali.module.gl.service.OriginEntryService#getStatistics(java.lang.Integer)
     */
    public OriginEntryStatistics getStatistics(Integer groupId) {
        LOG.debug("getStatistics() started");

        OriginEntryStatistics oes = new OriginEntryStatistics();

        oes.setCreditTotalAmount(originEntryDao.getGroupTotal(groupId, true));
        oes.setDebitTotalAmount(originEntryDao.getGroupTotal(groupId, false));
        oes.setRowCount(originEntryDao.getGroupCount(groupId));

        return oes;
    }

    /**
     * Creates a new origin entry group with the given parameters and saves copies all of the entries in the given Collection
     * of entries into that new group.
     * 
     * @param date the date that the copied entries should list as their post date
     * @param sourceCode the source code of the origin entry group to create
     * @param valid whether the new group should be considered valid
     * @param process whether the new group should be ready to be processed
     * @param scrub whether the new group should be processed by the scrubber
     * @param entries a Collection of entries to copy
     * @return a new origin entry full of copied entries
     * @see org.kuali.module.gl.service.OriginEntryService#copyEntries(java.util.Date, java.lang.String, boolean, boolean, boolean,
     *      java.util.Collection)
     */
    public OriginEntryGroup copyEntries(Date date, String sourceCode, boolean valid, boolean process, boolean scrub, Collection<OriginEntryFull> entries) {
        LOG.debug("copyEntries() started");

        OriginEntryGroup newOriginEntryGroup = originEntryGroupService.createGroup(date, sourceCode, valid, process, scrub);

        // Create new Entries with newOriginEntryGroup
        for (OriginEntryFull oe : entries) {
            oe.setEntryGroupId(newOriginEntryGroup.getId());
            createEntry(oe, newOriginEntryGroup);
        }

        return newOriginEntryGroup;
    }


    /**
     * Creates a new origin entry group with the given parameters and creates copies of all the origin entries in the
     * iterator, saving the copies in the new group
     * @param date the date that the copied entries should list as their post date
     * @param sourceCode the source code of the origin entry group to create
     * @param valid whether the new group should be considered valid
     * @param process whether the new group should be ready to be processed
     * @param scrub whether the new group should be processed by the scrubber
     * @param entries a Iterator of entries to copy
     * @see org.kuali.module.gl.service.OriginEntryService#copyEntries(java.sql.Date, java.lang.String, boolean, boolean, boolean,
     *      java.util.Iterator)
     */
    public OriginEntryGroup copyEntries(Date date, String sourceCode, boolean valid, boolean process, boolean scrub, Iterator<OriginEntryFull> entries) {
        LOG.debug("copyEntries() started");

        OriginEntryGroup newOriginEntryGroup = originEntryGroupService.createGroup(date, sourceCode, valid, process, scrub);

        // Create new Entries with newOriginEntryGroup
        while (entries.hasNext()) {
            OriginEntryFull oe = entries.next();
            oe.setEntryGroupId(newOriginEntryGroup.getId());
            createEntry(oe, newOriginEntryGroup);
        }

        return newOriginEntryGroup;
    }

    /**
     * Deletes an origin entry (full) from the database
     * @param oe the origin entry (full) to delete
     * @see org.kuali.module.gl.service.OriginEntryService#delete(org.kuali.module.gl.bo.OriginEntryFull)
     */
    public void delete(OriginEntryFull oe) {
        LOG.debug("deleteEntry() started");

        originEntryDao.deleteEntry(oe);
    }

    /**
     * This returns all of distinct primary key sets of documents that created origin entries that exist
     * in the given origin entry group.  It returns this information in OriginEntryFull objects
     * that just don't have any other information besides the document keys (doc number, doc type code,
     * and origination code) filled in.
     * @param oeg the group with the origin entries to get the documents of
     * @return Collection to qualifying documents
     * @see org.kuali.module.gl.service.OriginEntryService#getDocumentsByGroup(org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public Collection<OriginEntryFull> getDocumentsByGroup(OriginEntryGroup oeg) {
        LOG.debug("getDocumentsByGroup() started");

        Collection<OriginEntryFull> results = new ArrayList<OriginEntryFull>();
        Iterator i = originEntryDao.getDocumentsByGroup(oeg);
        while (i.hasNext()) {
            Object[] data = (Object[]) i.next();
            OriginEntryFull oe = new OriginEntryFull();
            oe.setDocumentNumber((String) data[0]);
            oe.setFinancialDocumentTypeCode((String) data[1]);
            oe.setFinancialSystemOriginationCode((String) data[2]);
            results.add(oe);
        }

        return results;
    }

    /**
     * Returns an iterator of all the origin entries in a given group.  Defers to the DAO.
     * @param originEntryGroup an origin entry group
     * @return an iterator of OriginEntryFull objects in that group
     * @see org.kuali.module.gl.service.OriginEntryService#getEntriesByGroup(org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public Iterator<OriginEntryFull> getEntriesByGroup(OriginEntryGroup originEntryGroup) {
        LOG.debug("getEntriesByGroup() started");

        return originEntryDao.getEntriesByGroup(originEntryGroup, OriginEntryDao.SORT_DOCUMENT);
    }

    /**
     * Return all entries for the groups where the balance type is empty. Defers to the DAO.
     * 
     * @param groups a Collection of groups to look through all the entries of
     * @return an Iterator of entries without balance types 
     * @see org.kuali.module.gl.service.OriginEntryService#getBadBalanceEntries(java.util.Collection)
     */
    public Iterator<OriginEntryFull> getBadBalanceEntries(Collection groups) {
        LOG.debug("getBadBalanceEntries() started");

        return originEntryDao.getBadBalanceEntries(groups);
    }

    /**
     * Return all entries for a group sorted by account number.  Defers to the DAO.
     * 
     * @param oeg an origin entry group to get entries from
     * @return an Iterator of origin entries sorted by account number
     * @see org.kuali.module.gl.service.OriginEntryService#getEntriesByGroupAccountOrder(org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public Iterator<OriginEntryFull> getEntriesByGroupAccountOrder(OriginEntryGroup oeg) {
        LOG.debug("getEntriesByGroupAccountOrder() started");

        return originEntryDao.getEntriesByGroup(oeg, OriginEntryDao.SORT_ACCOUNT);
    }

    /**
     * Return all entries for a group sorted for display on the pending entry report.  Defers to the DAO.
     * 
     * @param oeg a origin entry group to get entries from
     * @return an Iterator of origin entries sorted in the order needed for an origin entry report
     * @see org.kuali.module.gl.service.OriginEntryService#getEntriesByGroupReportOrder(org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public Iterator<OriginEntryFull> getEntriesByGroupReportOrder(OriginEntryGroup oeg) {
        LOG.debug("getEntriesByGroupAccountOrder() started");

        return originEntryDao.getEntriesByGroup(oeg, OriginEntryDao.SORT_REPORT);
    }

    /**
     * Return all entries for a group sorted across the columns in report from left to right.  Defers to the DAO.
     * 
     * @param oeg an origin entry group to get entries from
     * @return an Iterator of origin entries sorted in the proper order
     * @see org.kuali.module.gl.service.OriginEntryService#getEntriesByGroupListingReportOrder(org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public Iterator<OriginEntryFull> getEntriesByGroupListingReportOrder(OriginEntryGroup oeg) {
        LOG.debug("getEntriesByGroupAccountOrder() started");

        return originEntryDao.getEntriesByGroup(oeg, OriginEntryDao.SORT_LISTING_REPORT);
    }

    /**
     * Return all the entries for a specific document in a specific group.  Defers to the DAO.
     * 
     * @param oeg an origin entry group to find entries in
     * @param documentNumber the document number of entries to select
     * @param documentTypeCode the document type of entries to select
     * @param originCode the origination code of entries to select
     * @return iterator to all the qualifying entries
     * @see org.kuali.module.gl.service.OriginEntryService#getEntriesByDocument(org.kuali.module.gl.bo.OriginEntryGroup,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public Iterator<OriginEntryFull> getEntriesByDocument(OriginEntryGroup originEntryGroup, String documentNumber, String documentTypeCode, String originCode) {
        LOG.debug("getEntriesByGroup() started");

        Map criteria = new HashMap();
        criteria.put(ENTRY_GROUP_ID, originEntryGroup.getId());
        criteria.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);
        criteria.put(FINANCIAL_DOCUMENT_TYPE_CODE, documentTypeCode);
        criteria.put(FINANCIAL_SYSTEM_ORIGINATION_CODE, originCode);

        return originEntryDao.getMatchingEntries(criteria);
    }

    /**
     * Given a transaction, creates an origin entry out of the transaction and saves it in the given origin entry group
     * @param transaction the transaction to turn into an origin entry
     * @param originEntryGroup the group to save the new origin entry in
     * @see org.kuali.module.gl.service.OriginEntryService#createEntry(org.kuali.module.gl.bo.Transaction, org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public void createEntry(Transaction transaction, OriginEntryGroup originEntryGroup) {
        LOG.debug("createEntry() started");

        OriginEntryFull e = new OriginEntryFull(transaction);
        e.setGroup(originEntryGroup);

        originEntryDao.saveOriginEntry(e);

        // add 1 to the rows in the origin entry group, so we can unit test against that
        originEntryGroup.setRows(originEntryGroup.getRows().intValue() + 1);
    }

    /**
     * Saves the given origin entry full record
     * @param entry the origin entry to save
     * @see org.kuali.module.gl.service.OriginEntryService#save(org.kuali.module.gl.bo.OriginEntryFull)
     */
    public void save(OriginEntryFull entry) {
        LOG.debug("save() started");

        originEntryDao.saveOriginEntry(entry);
    }

    /**
     * Export all origin entries in a group to a flat text file
     * 
     * @param filename Filename to save the text
     * @param groupId Group to save
     * @see org.kuali.module.gl.service.OriginEntryService#exportFlatFile(java.lang.String, java.lang.Integer)
     */
    public void exportFlatFile(String filename, Integer groupId) {
        LOG.debug("exportFlatFile() started");

        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(filename));

            OriginEntryGroup oeg = new OriginEntryGroup();
            oeg.setId(groupId);
            Iterator i = getEntriesByGroup(oeg);
            while (i.hasNext()) {
                OriginEntryFull e = (OriginEntryFull) i.next();
                out.write(e.getLine() + "\n");
            }
        }
        catch (IOException e) {
            LOG.error("exportFlatFile() Error writing to file", e);
        }
        finally {
            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException ie) {
                    LOG.error("exportFlatFile() Error closing file", ie);
                }
            }
        }
    }

    /**
     * Creates a new origin entry group, reads all of the origin entries in the file and saves all of them in the group
     * 
     * @param filename Filename with the text
     * @param groupSourceCode Source of the new group
     * @param valid Valid flag for new group
     * @param processed Process flag for new group
     * @param scrub Scrub flag for new group
     * @see org.kuali.module.gl.service.OriginEntryService#loadFlatFile(java.lang.String, java.lang.String, boolean, boolean,
     *      boolean)
     */
    public void loadFlatFile(String filename, String groupSourceCode, boolean isValid, boolean isProcessed, boolean isScrub) {
        LOG.debug("loadFlatFile() started");

        java.sql.Date groupDate = new java.sql.Date(dateTimeService.getCurrentDate().getTime());
        OriginEntryGroup newGroup = originEntryGroupService.createGroup(groupDate, groupSourceCode, isValid, isProcessed, isScrub);

        BufferedReader input = null;
        try {
            input = new BufferedReader(new FileReader(filename));
            String line = null;
            while ((line = input.readLine()) != null) {
                OriginEntryFull entry = new OriginEntryFull(line);
                createEntry(entry, newGroup);
            }
        }
        catch (Exception ex) {
            LOG.error("performStep() Error reading file", ex);
            throw new IllegalArgumentException("Error reading file");
        }
        finally {
            try {
                if (input != null) {
                    input.close();
                }
            }
            catch (IOException ex) {
                LOG.error("loadFlatFile() error closing file.", ex);
            }
        }
    }

    /**
     * Given a collection of group ids, summarize the entries in each group.
     * @param groupIdList a Collection of the ids of origin entry groups to summarize
     * @return a LedgerEntryHolder with all of the summarized information
     * @see org.kuali.module.gl.service.OriginEntryService#getSummaryByGroupId(Collection)
     */
    public LedgerEntryHolder getSummaryByGroupId(Collection groupIdList) {
        LOG.debug("getSummaryByGroupId() started");

        LedgerEntryHolder ledgerEntryHolder = new LedgerEntryHolder();

        if (groupIdList.size() == 0) {
            return ledgerEntryHolder;
        }

        Iterator entrySummaryIterator = originEntryDao.getSummaryByGroupId(groupIdList);
        while (entrySummaryIterator.hasNext()) {
            Object[] entrySummary = (Object[]) entrySummaryIterator.next();
            LedgerEntry ledgerEntry = this.buildLedgerEntry(entrySummary);
            ledgerEntryHolder.insertLedgerEntry(ledgerEntry, true);
        }
        return ledgerEntryHolder;
    }

    /**
     * Creates or updates a ledger entry with the array of information from the given entry summary object
     * 
     * @param entrySummary a collection of java.lang.Objects, which is what OJB report queries return
     * @return a LedgerEntry holding the given report summarization data
     */
    public static LedgerEntry buildLedgerEntry(Object[] entrySummary) {
        // extract the data from an array and use them to populate a ledger entry
        Object oFiscalYear = entrySummary[0];
        Object oPeriodCode = entrySummary[1];
        Object oBalanceType = entrySummary[2];
        Object oOriginCode = entrySummary[3];
        Object oDebitCreditCode = entrySummary[4];
        Object oAmount = entrySummary[5];
        Object oCount = entrySummary[6];

        Integer fiscalYear = oFiscalYear != null ? new Integer(oFiscalYear.toString()) : null;
        String periodCode = oPeriodCode != null ? oPeriodCode.toString() : GLConstants.getSpaceUniversityFiscalPeriodCode();
        String balanceType = oBalanceType != null ? oBalanceType.toString() : GLConstants.getSpaceBalanceTypeCode();
        String originCode = oOriginCode != null ? oOriginCode.toString() : GLConstants.getSpaceFinancialSystemOriginationCode();
        String debitCreditCode = oDebitCreditCode != null ? oDebitCreditCode.toString() : GLConstants.getSpaceDebitCreditCode();
        KualiDecimal amount = oAmount != null ? new KualiDecimal(oAmount.toString()) : KualiDecimal.ZERO;
        int count = oCount != null ? Integer.parseInt(oCount.toString()) : 0;

        // construct a ledger entry with the information fetched from the given array
        LedgerEntry ledgerEntry = new LedgerEntry(fiscalYear, periodCode, balanceType, originCode);
        if (KFSConstants.GL_CREDIT_CODE.equals(debitCreditCode)) {
            ledgerEntry.setCreditAmount(amount);
            ledgerEntry.setCreditCount(count);
        }
        else if (KFSConstants.GL_DEBIT_CODE.equals(debitCreditCode)) {
            ledgerEntry.setDebitAmount(amount);
            ledgerEntry.setDebitCount(count);
        }
        else {
            ledgerEntry.setNoDCAmount(amount);
            ledgerEntry.setNoDCCount(count);
        }
        ledgerEntry.setRecordCount(count);

        return ledgerEntry;
    }

    /**
     * Write all of the origin entries in a group to an output stream
     * 
     * @param groupId the id of the origin entry group to get entries from
     * @param bw the output stream to dump the entries as text to
     * @see org.kuali.module.gl.service.OriginEntryService#flatFile(java.lang.Integer, java.io.BufferedOutputStream)
     */
    public void flatFile(Integer groupId, BufferedOutputStream bw) {
        LOG.debug("flatFile() started");
        OriginEntryGroup oeg = new OriginEntryGroup();
        oeg.setId(groupId);
        flatFile(getEntriesByGroup(oeg), bw);
    }

    /**
     * This method writes origin entries into a file format. This particular implementation will use the OriginEntryFull.getLine
     * method to generate the text for this file.
     * 
     * @param entries An iterator of OriginEntries
     * @param bw an opened, ready-for-output bufferedOutputStream.
     * @see org.kuali.module.gl.service.OriginEntryService#flatFile(java.util.Iterator, java.io.BufferedOutputStream)
     */
    public void flatFile(Iterator<OriginEntryFull> entries, BufferedOutputStream bw) {
        try {
            while (entries.hasNext()) {
                OriginEntryFull e = entries.next();
                bw.write((e.getLine() + "\n").getBytes());
            }
        }
        catch (IOException e) {
            LOG.error("flatFile() Error writing to file", e);
            throw new RuntimeException("Error writing to file: " + e.getMessage());
        }
    }

    /**
     * Finds all origin entries matching certain criteria
     * 
     * @param searchCriteria the criteria to be used in forming a query
     * @return a Collection of qualifying origin entries
     * @see org.kuali.module.gl.service.OriginEntryService#getMatchingEntriesByCollection(java.util.Map)
     */
    public Collection<OriginEntryFull> getMatchingEntriesByCollection(Map searchCriteria) {
        LOG.debug("getMatchingEntriesByCollection() started");

        return originEntryDao.getMatchingEntriesByCollection(searchCriteria);
    }

    /**
     * Given the id of an origin entry group, returns a list of all the origin entries in that group (actually uses
     * getMatchingEntriesByCollection() to accomplish this).
     * @param groupId the id of the group to get all entries from
     * @return a List of Origin Entries
     * @see org.kuali.module.gl.service.OriginEntryService#getMatchingEntriesByList(java.util.Map)
     */
    public List<OriginEntryFull> getEntriesByGroupId(Integer groupId) {
        if (groupId == null) {
            throw new IllegalArgumentException("Group ID is null");
        }
        Map<String, Object> searchCriteria = new HashMap<String, Object>();
        searchCriteria.put(ENTRY_GROUP_ID, groupId);
        Collection<OriginEntryFull> searchResultAsCollection = getMatchingEntriesByCollection(searchCriteria);
        if (searchResultAsCollection instanceof List) {
            return (List<OriginEntryFull>) searchResultAsCollection;
        }
        else {
            return new ArrayList<OriginEntryFull>(searchResultAsCollection);
        }
    }

    /**
     * Returns the origin entry with the given id.  Defers to the DAO.
     * @param entryId the unique id of an origin entry
     * @return the origin entry if found, or null otherwise
     * @see org.kuali.module.gl.service.OriginEntryService#getExactMatchingEntry(java.lang.Integer)
     */
    public OriginEntryFull getExactMatchingEntry(Integer entryId) {
        LOG.debug("getExactMatchingEntry() started");

        return originEntryDao.getExactMatchingEntry(entryId);
    }

    /**
     * get the summarized information of poster input entries that belong to the entry groups with the given group id list
     * 
     * @param groupIdList the origin entry groups
     * @return a map of summarized information of poster input entries within the specified groups
     * @see org.kuali.module.gl.service.OriginEntryService#getPosterOutputSummaryByGroupId(java.util.Collection)
     */
    public Map<String, PosterOutputSummaryEntry> getPosterOutputSummaryByGroupId(Collection groupIdList) {
        LOG.debug("getPosterOutputSummaryByGroupId() started");

        Map<String, PosterOutputSummaryEntry> output = new HashMap<String, PosterOutputSummaryEntry>();

        if (groupIdList.size() == 0) {
            return output;
        }

        Iterator entrySummaryIterator = originEntryDao.getPosterOutputSummaryByGroupId(groupIdList);
        while (entrySummaryIterator.hasNext()) {
            Object[] entrySummary = (Object[]) entrySummaryIterator.next();
            PosterOutputSummaryEntry posterOutputSummaryEntry = new PosterOutputSummaryEntry();
            int indexOfField = 0;

            Object tempEntry = entrySummary[indexOfField++];
            String entry = (tempEntry == null) ? "" : tempEntry.toString();
            posterOutputSummaryEntry.setBalanceTypeCode(entry);

            tempEntry = entrySummary[indexOfField++];
            entry = (tempEntry == null) ? null : tempEntry.toString();
            posterOutputSummaryEntry.setUniversityFiscalYear(new Integer(entry));

            tempEntry = entrySummary[indexOfField++];
            entry = (tempEntry == null) ? "" : tempEntry.toString();
            posterOutputSummaryEntry.setFiscalPeriodCode(entry);

            tempEntry = entrySummary[indexOfField++];
            entry = (tempEntry == null) ? "" : tempEntry.toString();
            posterOutputSummaryEntry.setFundGroup(entry);

            tempEntry = entrySummary[indexOfField++];
            String objectTypeCode = (tempEntry == null) ? "" : tempEntry.toString();
            posterOutputSummaryEntry.setObjectTypeCode(objectTypeCode);

            tempEntry = entrySummary[indexOfField++];
            String debitCreditCode = (tempEntry == null) ? KFSConstants.GL_BUDGET_CODE : tempEntry.toString();

            tempEntry = entrySummary[indexOfField];
            entry = (tempEntry == null) ? "0" : tempEntry.toString();
            KualiDecimal amount = new KualiDecimal(entry);

            posterOutputSummaryEntry.setAmount(debitCreditCode, objectTypeCode, amount);

            if (output.containsKey(posterOutputSummaryEntry.getKey())) {
                PosterOutputSummaryEntry pose = output.get(posterOutputSummaryEntry.getKey());
                pose.add(posterOutputSummaryEntry);
            }
            else {
                output.put(posterOutputSummaryEntry.getKey(), posterOutputSummaryEntry);
            }
        }
        return output;
    }

    /**
     * Returns the number of origin entries in a group.  Defers to the DAO.
     * Get count of transactions in a group
     * @param groupId the group to get the count of entries from
     * @return a count of entries
     * @see org.kuali.module.gl.service.OriginEntryService#getGroupCount(java.lang.Integer)
     */
    public Integer getGroupCount(Integer groupId) {
        return originEntryDao.getGroupCount(groupId);
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}

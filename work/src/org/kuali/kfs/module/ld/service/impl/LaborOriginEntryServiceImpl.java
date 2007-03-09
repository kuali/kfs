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
package org.kuali.module.labor.service.impl;

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
import java.util.Map;

import org.kuali.PropertyConstants;
import org.kuali.core.service.DateTimeService;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.OriginEntryDao;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.impl.OriginEntryServiceImpl;
import org.kuali.module.gl.util.LedgerEntry;
import org.kuali.module.gl.util.LedgerEntryHolder;
import org.kuali.module.gl.util.OriginEntryStatistics;
import org.kuali.module.gl.util.PosterOutputSummaryEntry;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.kuali.module.labor.dao.LaborOriginEntryDao;
import org.kuali.module.labor.service.LaborOriginEntryService;
import org.kuali.module.labor.util.LaborLedgerUnitOfWork;
import org.kuali.module.labor.util.ObjectUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements LaborOriginEntryService to provide the access to labor origin entries in data stores.
 */
@Transactional
public class LaborOriginEntryServiceImpl implements LaborOriginEntryService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OriginEntryServiceImpl.class);

    private LaborOriginEntryDao laborOriginEntryDao;
    private OriginEntryDao originEntryDao;
    private OriginEntryGroupService originEntryGroupService;
    private DateTimeService dateTimeService;

    public OriginEntryStatistics getStatistics(Integer groupId) {
        LOG.debug("getStatistics() started");

        OriginEntryStatistics oes = new OriginEntryStatistics();

        oes.setCreditTotalAmount(originEntryDao.getGroupTotal(groupId, true));
        oes.setDebitTotalAmount(originEntryDao.getGroupTotal(groupId, false));
        oes.setRowCount(originEntryDao.getGroupCount(groupId));

        return oes;
    }

    /**
     * @see org.kuali.module.gl.service.OriginEntryService#copyEntries(java.util.Date, java.lang.String, boolean, boolean, boolean,
     *      java.util.Collection)
     */
    public OriginEntryGroup copyEntries(Date date, String sourceCode, boolean valid, boolean process, boolean scrub, Collection<LaborOriginEntry> entries) {
        LOG.debug("copyEntries() started");

        OriginEntryGroup newOriginEntryGroup = originEntryGroupService.createGroup(date, sourceCode, valid, process, scrub);

        // Create new Entries with newOriginEntryGroup
        for (LaborOriginEntry oe : entries) {
            oe.setEntryGroupId(newOriginEntryGroup.getId());
            createEntry(oe, newOriginEntryGroup);
        }

        return newOriginEntryGroup;
    }

    /**
     * @see org.kuali.module.gl.service.OriginEntryService#delete(org.kuali.module.gl.bo.OriginEntry)
     */
    public void delete(LaborOriginEntry loe) {
        LOG.debug("deleteEntry() started");

        originEntryDao.deleteEntry(loe);
    }

    /**
     * @see org.kuali.module.gl.service.OriginEntryService#getDocumentsByGroup(org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public Collection<LaborOriginEntry> getDocumentsByGroup(OriginEntryGroup oeg) {
        LOG.debug("getDocumentsByGroup() started");

        Collection<LaborOriginEntry> results = new ArrayList<LaborOriginEntry>();
        Iterator i = originEntryDao.getDocumentsByGroup(oeg);
        while (i.hasNext()) {
            Object[] data = (Object[]) i.next();
            LaborOriginEntry oe = new LaborOriginEntry();
            oe.setDocumentNumber((String) data[0]);
            oe.setFinancialDocumentTypeCode((String) data[1]);
            oe.setFinancialSystemOriginationCode((String) data[2]);
            results.add(oe);
        }

        return results;
    }

    public Iterator<LaborOriginEntry> getBadBalanceEntries(Collection groups) {
        LOG.debug("getBadBalanceEntries() started");
        Iterator returnVal = originEntryDao.getBadBalanceEntries(groups);

        return returnVal;
    }

    public Iterator<LaborOriginEntry> getEntriesByGroupAccountOrder(OriginEntryGroup oeg) {
        LOG.debug("getEntriesByGroupAccountOrder() started");
        Iterator returnVal = originEntryDao.getEntriesByGroup(oeg, OriginEntryDao.SORT_ACCOUNT);

        return returnVal;
    }

    public Iterator<LaborOriginEntry> getEntriesByGroupReportOrder(OriginEntryGroup oeg) {
        LOG.debug("getEntriesByGroupAccountOrder() started");
        Iterator returnVal = originEntryDao.getEntriesByGroup(oeg, OriginEntryDao.SORT_REPORT);

        return returnVal;
    }

    public Iterator<LaborOriginEntry> getEntriesByGroupListingReportOrder(OriginEntryGroup oeg) {
        LOG.debug("getEntriesByGroupAccountOrder() started");

        Iterator returnVal = originEntryDao.getEntriesByGroup(oeg, OriginEntryDao.SORT_LISTING_REPORT);
        return returnVal;
    }

    /**
     * @see org.kuali.module.gl.service.OriginEntryService#getEntriesByDocument(org.kuali.module.gl.bo.OriginEntryGroup,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public Iterator<LaborOriginEntry> getEntriesByDocument(OriginEntryGroup originEntryGroup, String documentNumber, String documentTypeCode, String originCode) {
        LOG.debug("getEntriesByGroup() started");

        Map criteria = new HashMap();
        criteria.put(PropertyConstants.ENTRY_GROUP_ID, originEntryGroup.getId());
        criteria.put(PropertyConstants.DOCUMENT_NUMBER, documentNumber);
        criteria.put(PropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, documentTypeCode);
        criteria.put(PropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, originCode);

        return originEntryDao.getMatchingEntries(criteria);
    }

    /**
     * @see org.kuali.module.gl.service.OriginEntryService#createEntry(org.kuali.module.gl.bo.Transaction,
     *      org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public void createEntry(Transaction transaction, OriginEntryGroup originEntryGroup) {
        LOG.debug("createEntry() started");

        LaborOriginEntry e = new LaborOriginEntry(transaction);
        e.setGroup(originEntryGroup);

        originEntryDao.saveOriginEntry(e);
    }

    /**
     * @see org.kuali.module.gl.service.OriginEntryService#save(org.kuali.module.gl.bo.OriginEntry)
     */
    public void save(LaborOriginEntry entry) {
        LOG.debug("save() started");

        originEntryDao.saveOriginEntry(entry);
    }

    /**
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
                LaborOriginEntry e = (LaborOriginEntry) i.next();
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
                LaborOriginEntry entry = new LaborOriginEntry(line);
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

    public LedgerEntryHolder getSummaryByGroupId(Collection groupIdList) {
        LOG.debug("getSummaryByGroupId() started");

        LedgerEntryHolder ledgerEntryHolder = new LedgerEntryHolder();

        if (groupIdList.size() == 0) {
            return ledgerEntryHolder;
        }

        Iterator entrySummaryIterator = originEntryDao.getSummaryByGroupId(groupIdList);
        while (entrySummaryIterator.hasNext()) {
            Object[] entrySummary = (Object[]) entrySummaryIterator.next();
            LedgerEntry ledgerEntry = LedgerEntry.buildLedgerEntry(entrySummary);
            ledgerEntryHolder.insertLedgerEntry(ledgerEntry, true);
        }
        return ledgerEntryHolder;
    }

    /**
     * @see org.kuali.module.gl.service.OriginEntryService#flatFile(java.lang.Integer, java.io.BufferedOutputStream)
     */
    public void flatFile(Integer groupId, BufferedOutputStream bw) {
        LOG.debug("flatFile() started");

        try {
            OriginEntryGroup oeg = new OriginEntryGroup();
            oeg.setId(groupId);
            Iterator i = getEntriesByGroup(oeg);
            while (i.hasNext()) {
                LaborOriginEntry e = (LaborOriginEntry) i.next();
                bw.write((e.getLine() + "\n").getBytes());
            }
        }
        catch (IOException e) {
            LOG.error("flatFile() Error writing to file", e);
            throw new RuntimeException("Error writing to file: " + e.getMessage());
        }
    }

    /**
     * @see org.kuali.module.gl.service.OriginEntryService#getMatchingEntriesByCollection(java.util.Map)
     */
    public Collection getMatchingEntriesByCollection(Map searchCriteria) {
        LOG.debug("getMatchingEntriesByCollection() started");

        return originEntryDao.getMatchingEntriesByCollection(searchCriteria);
    }

    /**
     * @see org.kuali.module.gl.service.OriginEntryService#getExactMatchingEntry(java.lang.Integer)
     */
    public LaborOriginEntry getExactMatchingEntry(Integer entryId) {
        LOG.debug("getExactMatchingEntry() started");

        return (LaborOriginEntry) originEntryDao.getExactMatchingEntry(entryId);
    }

    /**
     * @see org.kuali.module.labor.service.LaborOriginEntryService#getEntriesByGroup(org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public Iterator<LaborOriginEntry> getEntriesByGroup(OriginEntryGroup group) {
        return laborOriginEntryDao.getEntriesByGroup(group);
    }

    /**
     * @see org.kuali.module.labor.service.LaborOriginEntryService#getEntriesByGroups(java.util.Collection)
     */
    public Iterator<LaborOriginEntry> getEntriesByGroups(Collection<OriginEntryGroup> groups) {
        return laborOriginEntryDao.getEntriesByGroups(groups);
    }

    /**
     * @see org.kuali.module.labor.service.LaborOriginEntryService#getEntriesByGroup(org.kuali.module.gl.bo.OriginEntryGroup,
     *      boolean)
     */
    public Iterator<LaborOriginEntry> getEntriesByGroup(OriginEntryGroup group, boolean isConsolidated) {
        if (!isConsolidated) {
            return this.getEntriesByGroup(group);
        }
        return this.getConsolidatedEntryCollectionByGroup(group).iterator();
    }

    /**
     * @see org.kuali.module.labor.service.LaborOriginEntryService#getConsolidatedEntryCollectionByGroup(org.kuali.module.gl.bo.OriginEntryGroup,
     *      boolean)
     */
    public Collection<LaborOriginEntry> getConsolidatedEntryCollectionByGroup(OriginEntryGroup group) {
        Collection<LaborOriginEntry> entryCollection = new ArrayList<LaborOriginEntry>();
        LaborLedgerUnitOfWork laborLedgerUnitOfWork = new LaborLedgerUnitOfWork();

        int count = 0;
        Iterator<Object[]> consolidatedEntries = laborOriginEntryDao.getConsolidatedEntriesByGroup(group);
        while (consolidatedEntries.hasNext()) {
            LaborOriginEntry laborOriginEntry = new LaborOriginEntry();
            Object[] oneEntry = consolidatedEntries.next();
            ObjectUtil.buildObject(laborOriginEntry, oneEntry, LaborConstants.consolidationAttributesOfOriginEntry());

            if (laborLedgerUnitOfWork.canContain(laborOriginEntry)) {
                laborLedgerUnitOfWork.addEntryIntoUnit(laborOriginEntry);
            }
            else {
                laborLedgerUnitOfWork.resetLaborLedgerUnitOfWork(laborOriginEntry);
                entryCollection.add(laborLedgerUnitOfWork.getWorkingEntry());
            }
        }
        return entryCollection;
    }

    /**
     * @see org.kuali.module.labor.service.LaborOriginEntryService#getSummariedEntriesByGroups(java.util.Collection)
     */
    public LedgerEntryHolder getSummariedEntriesByGroups(Collection<OriginEntryGroup> groups) {
        LedgerEntryHolder ledgerEntryHolder = new LedgerEntryHolder();

        if (groups.size() > 0) {
            Iterator entrySummaryIterator = laborOriginEntryDao.getSummaryByGroupId(groups);
            while (entrySummaryIterator.hasNext()) {
                Object[] entrySummary = (Object[]) entrySummaryIterator.next();
                ledgerEntryHolder.insertLedgerEntry(LedgerEntry.buildLedgerEntry(entrySummary), true);
            }
        }
        return ledgerEntryHolder;
    }

    /**
     * @see org.kuali.module.labor.service.LaborOriginEntryService#getPosterOutputSummaryByGroups(java.util.Collection)
     */
    public Map<String, PosterOutputSummaryEntry> getPosterOutputSummaryByGroups(Collection<OriginEntryGroup> groups) {
        Map<String, PosterOutputSummaryEntry> outputSummary = new HashMap<String, PosterOutputSummaryEntry>();

        if (groups.size() > 0) {
            Iterator entrySummaryIterator = laborOriginEntryDao.getPosterOutputSummaryByGroupId(groups);
            while (entrySummaryIterator.hasNext()) {
                Object[] entrySummary = (Object[]) entrySummaryIterator.next();
                PosterOutputSummaryEntry posterOutputSummaryEntry = PosterOutputSummaryEntry.buildPosterOutputSummaryEntry(entrySummary);

                if (outputSummary.containsKey(posterOutputSummaryEntry.getKey())) {
                    PosterOutputSummaryEntry tempEntry = outputSummary.get(posterOutputSummaryEntry.getKey());
                    tempEntry.add(posterOutputSummaryEntry);
                }
                else {
                    outputSummary.put(posterOutputSummaryEntry.getKey(), posterOutputSummaryEntry);
                }
            }
        }
        return outputSummary;
    }

    /**
     * @see org.kuali.module.labor.service.LaborOriginEntryService#getSizeOfEntriesInGroups(java.util.Collection)
     */
    public int getCountOfEntriesInGroups(Collection<OriginEntryGroup> groups) {
        return laborOriginEntryDao.getCountOfEntriesInGroups(groups);
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Sets the laborOriginEntryDao attribute value.
     * 
     * @param laborOriginEntryDao The laborOriginEntryDao to set.
     */
    public void setLaborOriginEntryDao(LaborOriginEntryDao laborOriginEntryDao) {
        this.laborOriginEntryDao = laborOriginEntryDao;
    }

    /**
     * Sets the originEntryDao attribute value.
     * 
     * @param originEntryDao The originEntryDao to set.
     */
    public void setOriginEntryDao(OriginEntryDao originEntryDao) {
        this.originEntryDao = originEntryDao;
    }

    /**
     * Sets the originEntryGroupService attribute value.
     * 
     * @param originEntryGroupService The originEntryGroupService to set.
     */
    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }
}
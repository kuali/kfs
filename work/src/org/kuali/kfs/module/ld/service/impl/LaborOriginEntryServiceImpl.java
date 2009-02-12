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
package org.kuali.kfs.module.ld.service.impl;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
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

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.businessobject.LedgerEntry;
import org.kuali.kfs.gl.businessobject.LedgerEntryHolder;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.gl.businessobject.OriginEntrySource;
import org.kuali.kfs.gl.businessobject.OriginEntryStatistics;
import org.kuali.kfs.gl.dataaccess.OriginEntryDao;
import org.kuali.kfs.gl.report.PosterOutputSummaryEntry;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.gl.service.impl.OriginEntryServiceImpl;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.module.ld.businessobject.LaborTransaction;
import org.kuali.kfs.module.ld.dataaccess.LaborOriginEntryDao;
import org.kuali.kfs.module.ld.service.LaborOriginEntryService;
import org.kuali.kfs.module.ld.util.LaborLedgerUnitOfWork;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.Guid;
import org.kuali.rice.kns.util.KualiDecimal;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of LaborOriginEntryService.
 */
@Transactional
public class LaborOriginEntryServiceImpl implements LaborOriginEntryService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OriginEntryServiceImpl.class);

    private LaborOriginEntryDao laborOriginEntryDao;
    private OriginEntryDao originEntryDao;
    private OriginEntryGroupService originEntryGroupService;
    private DateTimeService dateTimeService;
    
    private String batchFileDirectoryName;

    public OriginEntryStatistics getStatistics(Integer groupId) {
        LOG.debug("getStatistics() started");

        OriginEntryStatistics oes = new OriginEntryStatistics();

        oes.setCreditTotalAmount(laborOriginEntryDao.getGroupTotal(groupId, true));
        oes.setDebitTotalAmount(laborOriginEntryDao.getGroupTotal(groupId, false));
        oes.setRowCount(laborOriginEntryDao.getGroupCount(groupId));

        return oes;
    }
    
    public OriginEntryStatistics getStatistics(String fileName) {
        LOG.debug("getStatistics() started");
        OriginEntryStatistics oes = new OriginEntryStatistics();
        KualiDecimal totalCredit = KualiDecimal.ZERO;
        KualiDecimal totalDebit = KualiDecimal.ZERO;
        Integer rowCount = 0;
        FileReader INPUT_FILE = null;
        BufferedReader INPUT_FILE_br;
        try {
            INPUT_FILE = new FileReader(fileName);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Collection<LaborOriginEntry> entryCollection = new ArrayList();
        INPUT_FILE_br = new BufferedReader(INPUT_FILE);

        try {
            String currentLine = INPUT_FILE_br.readLine();
            while (currentLine != null) {
                KualiDecimal amount = KualiDecimal.ZERO;
                if (!currentLine.substring(109, 126).trim().equals(GeneralLedgerConstants.EMPTY_CODE)) {
                    try {
                        amount = new KualiDecimal(currentLine.substring(109, 126).trim());

                        // TODO: Shawn - ask to jeff (Row count should be all rows?)
                        rowCount++;
                    }
                    catch (NumberFormatException e) {
                    }
                }
                else {
                    amount = KualiDecimal.ZERO;
                }
                String debitOrCreditCode = currentLine.substring(126, 127);

                if (debitOrCreditCode.equals(KFSConstants.GL_CREDIT_CODE)) {
                    totalCredit.add(amount);
                }
                else if (debitOrCreditCode.equals(KFSConstants.GL_DEBIT_CODE)) {
                    totalDebit.add(amount);
                }
                currentLine = INPUT_FILE_br.readLine();
            }
            INPUT_FILE_br.close();
        }
        catch (IOException e) {
            // FIXME: do whatever should be done here
            throw new RuntimeException(e);
        }

        oes.setCreditTotalAmount(totalCredit);
        oes.setDebitTotalAmount(totalDebit);
        oes.setRowCount(rowCount);

        return oes;

    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborOriginEntryService#copyEntries(java.util.Date, java.lang.String, boolean, boolean,
     *      boolean, java.util.Collection)
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
     * @see org.kuali.kfs.module.ld.service.LaborOriginEntryService#copyEntries(java.sql.Date, java.lang.String, boolean, boolean,
     *      boolean, java.util.Iterator)
     */
    public OriginEntryGroup copyEntries(Date date, String sourceCode, boolean valid, boolean process, boolean scrub, Iterator<LaborOriginEntry> entries) {
        LOG.debug("copyEntries() started");

        OriginEntryGroup newOriginEntryGroup = originEntryGroupService.createGroup(date, sourceCode, valid, process, scrub);

        // Create new Entries with newOriginEntryGroup
        while (entries.hasNext()) {
            LaborOriginEntry oe = entries.next();
            oe.setEntryGroupId(newOriginEntryGroup.getId());
            createEntry(oe, newOriginEntryGroup);
        }

        return newOriginEntryGroup;
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborOriginEntryService#delete(org.kuali.kfs.module.ld.businessobject.LaborOriginEntry)
     */
    public void delete(LaborOriginEntry loe) {
        LOG.debug("deleteEntry() started");

        originEntryDao.deleteEntry(loe);
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborOriginEntryService#getDocumentsByGroup(org.kuali.module.gl.bo.originentrygroup)
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

    /**
     * @see org.kuali.kfs.module.ld.service.LaborOriginEntryService#getBadBalanceEntries(org.kuali.module.gl.bo.originentrygroup)
     */
    public Iterator<LaborOriginEntry> getBadBalanceEntries(Collection groups) {
        LOG.debug("getBadBalanceEntries() started");
        Iterator returnVal = laborOriginEntryDao.getBadBalanceEntries(groups);

        return returnVal;
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborOriginEntryService#getEntriesByGroupAccountOrder(org.kuali.module.gl.bo.originentrygroup)
     */
    public Iterator<LaborOriginEntry> getEntriesByGroupAccountOrder(OriginEntryGroup oeg) {
        LOG.debug("getEntriesByGroupAccountOrder() started");
        Iterator returnVal = laborOriginEntryDao.getEntriesByGroup(oeg, OriginEntryDao.SORT_ACCOUNT);

        return returnVal;
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborOriginEntryService#getEntriesByGroupReportOrder(org.kuali.module.gl.bo.originentrygroup)
     */
    public Iterator<LaborOriginEntry> getEntriesByGroupReportOrder(OriginEntryGroup oeg) {
        LOG.debug("getEntriesByGroupAccountOrder() started");
        Iterator returnVal = laborOriginEntryDao.getEntriesByGroup(oeg, OriginEntryDao.SORT_REPORT);

        return returnVal;
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborOriginEntryService#getEntriesByGroupListingReportOrder(org.kuali.module.gl.bo.originentrygroup)
     */
    public Iterator<LaborOriginEntry> getEntriesByGroupListingReportOrder(OriginEntryGroup oeg) {
        LOG.debug("getEntriesByGroupAccountOrder() started");

        Iterator returnVal = laborOriginEntryDao.getEntriesByGroup(oeg, OriginEntryDao.SORT_LISTING_REPORT);
        return returnVal;
    }

    /**
     * @see org.kuali.module.labor.service.LaborLaborOriginEntryService#getEntriesByDocument(org.kuali.module.labor.bo.LaborOriginEntryGroup,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public Collection<LaborOriginEntry> getEntriesByDocument(OriginEntryGroup originEntryGroup, String documentNumber, String documentTypeCode, String originCode) {
        LOG.debug("getEntriesByGroup() started");

        Map criteria = new HashMap();
        criteria.put(KFSPropertyConstants.ENTRY_GROUP_ID, originEntryGroup.getId());
        criteria.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);
        criteria.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, documentTypeCode);
        criteria.put(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, originCode);

        return laborOriginEntryDao.getMatchingEntriesByCollection(criteria);
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborOriginEntryService#createEntry(org.kuali.module.labor.bo.Transaction,
     *      org.kuali.module.gl.bo.originentrygroup)
     */
    public void createEntry(LaborTransaction laborTransaction, OriginEntryGroup originEntryGroup) {
        LOG.debug("createEntry() started");

        LaborOriginEntry e = new LaborOriginEntry(laborTransaction);
        e.setGroup(originEntryGroup);

        laborOriginEntryDao.saveOriginEntry(e);
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborOriginEntryService#save(org.kuali.kfs.module.ld.businessobject.LaborOriginEntry)
     */
    public void save(LaborOriginEntry entry) {
        LOG.debug("save() started");

        laborOriginEntryDao.saveOriginEntry(entry);
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborOriginEntryService#exportFlatFile(java.lang.String, java.lang.Integer)
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
     * @see org.kuali.kfs.module.ld.service.LaborOriginEntryService#loadFlatFile(java.lang.String, java.lang.String, boolean,
     *      boolean, boolean)
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

    /**
     * @see org.kuali.kfs.module.ld.service.LaborOriginEntryService#getMatchingEntriesByList(java.util.Map)
     */
    public List<LaborOriginEntry> getEntriesByGroupId(Integer groupId) {
        if (groupId == null) {
            throw new IllegalArgumentException("Group ID is null");
        }
        Map<String, Object> searchCriteria = new HashMap<String, Object>();
        searchCriteria.put("entryGroupId", groupId);
        Collection<LaborOriginEntry> searchResultAsCollection = getMatchingEntriesByCollection(searchCriteria);
        if (searchResultAsCollection instanceof List) {
            return (List<LaborOriginEntry>) searchResultAsCollection;
        }
        else {
            return new ArrayList<LaborOriginEntry>(searchResultAsCollection);
        }
    }
    
    public  Map getEntriesByGroupId(String fileName, List<LaborOriginEntry> originEntryList) {
        if (fileName == null) {
            throw new IllegalArgumentException("File Name is null");
        }
        
        String fullFileName = batchFileDirectoryName + File.separator + fileName;
        FileReader INPUT_GLE_FILE = null;
        BufferedReader INPUT_GLE_FILE_br;
        try {
            INPUT_GLE_FILE = new FileReader(fullFileName);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        INPUT_GLE_FILE_br = new BufferedReader(INPUT_GLE_FILE);
        
        boolean loadError = false;
        //returnErrorList is list of List<Message>
        Map returnErrorMap = getEntriesByBufferedReader(INPUT_GLE_FILE_br, originEntryList);

        try{
            INPUT_GLE_FILE_br.close();
            INPUT_GLE_FILE.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        
        return returnErrorMap;
    }
    
    
    public Map getEntriesByBufferedReader(BufferedReader inputBufferedReader, List<LaborOriginEntry> originEntryList) {
        String line;
        int lineNumber = 0;
        Map returnErrorMap = new HashMap();
        try {
            List<Message> tmperrors = new ArrayList();    
            while ((line = inputBufferedReader.readLine()) != null) {
                lineNumber++;
                LaborOriginEntry laborOriginEntry = new LaborOriginEntry();
                tmperrors = laborOriginEntry.setFromTextFileForBatch(line, lineNumber);
                laborOriginEntry.setEntryId(lineNumber);
                if (tmperrors.size() > 0){
                    returnErrorMap.put(new Integer(lineNumber), tmperrors);
                } else {
                    originEntryList.add(laborOriginEntry);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
            
        return returnErrorMap;

        
    }



    public LedgerEntryHolder getSummaryByGroupId(Collection groupIdList) {
        LOG.debug("getSummaryByGroupId() started");

        LedgerEntryHolder ledgerEntryHolder = new LedgerEntryHolder();

        if (groupIdList.size() == 0) {
            return ledgerEntryHolder;
        }

        Iterator entrySummaryIterator = laborOriginEntryDao.getSummaryByGroupId(groupIdList);
        while (entrySummaryIterator.hasNext()) {
            Object[] entrySummary = (Object[]) entrySummaryIterator.next();
            LedgerEntry ledgerEntry = LedgerEntry.buildLedgerEntry(entrySummary);
            ledgerEntryHolder.insertLedgerEntry(ledgerEntry, true);
        }
        return ledgerEntryHolder;
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborOriginEntryService#flatFile(java.lang.Integer, java.io.BufferedOutputStream)
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
     * @see org.kuali.kfs.module.ld.service.LaborOriginEntryService#getMatchingEntriesByCollection(java.util.Map)
     */
    public Collection getMatchingEntriesByCollection(Map searchCriteria) {
        LOG.debug("getMatchingEntriesByCollection() started");

        return laborOriginEntryDao.getMatchingEntriesByCollection(searchCriteria);
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborOriginEntryService#getExactMatchingEntry(java.lang.Integer)
     */
    public LaborOriginEntry getExactMatchingEntry(Integer entryId) {
        LOG.debug("getExactMatchingEntry() started");

        return (LaborOriginEntry) originEntryDao.getExactMatchingEntry(entryId);
    }

    /**
     * @see org.kuali.module.labor.service.LaborLaborOriginEntryService#getEntriesByGroup(org.kuali.kfs.gl.businessobject.OriginEntryGroup)
     */
    public Iterator<LaborOriginEntry> getEntriesByGroup(OriginEntryGroup group) {
        return laborOriginEntryDao.getLaborEntriesByGroup(group, LaborOriginEntryDao.SORT_DOCUMENT);
    }

    /**
     * @see org.kuali.module.labor.service.LaborLaborOriginEntryService#getEntriesByGroups(java.util.Collection)
     */
    public Iterator<LaborOriginEntry> getEntriesByGroups(Collection<OriginEntryGroup> groups) {
        return laborOriginEntryDao.getEntriesByGroups(groups);
    }

    /**
     * @see org.kuali.module.labor.service.LaborLaborOriginEntryService#getEntriesByGroup(org.kuali.kfs.gl.businessobject.OriginEntryGroup,
     *      boolean)
     */
    public Iterator<LaborOriginEntry> getEntriesByGroup(OriginEntryGroup group, boolean isConsolidated) {
        if (!isConsolidated) {
            return this.getEntriesByGroup(group);
        }
        return this.getConsolidatedEntryCollectionByGroup(group).iterator();
    }

    /**
     * @see org.kuali.module.labor.service.LaborLaborOriginEntryService#getConsolidatedEntryCollectionByGroup(org.kuali.kfs.gl.businessobject.OriginEntryGroup,
     *      boolean)
     */
    public Collection<LaborOriginEntry> getConsolidatedEntryCollectionByGroup(OriginEntryGroup group) {
        Collection<LaborOriginEntry> entryCollection = new ArrayList<LaborOriginEntry>();
        LaborLedgerUnitOfWork laborLedgerUnitOfWork = new LaborLedgerUnitOfWork();

        // the following iterator has been sorted
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
     * @see org.kuali.module.labor.service.LaborLaborOriginEntryService#getSummariedEntriesByGroups(java.util.Collection)
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
     * @see org.kuali.module.labor.service.LaborLaborOriginEntryService#getPosterOutputSummaryByGroups(java.util.Collection)
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
     * @see org.kuali.module.labor.service.LaborLaborOriginEntryService#getSizeOfEntriesInGroups(java.util.Collection)
     */
    public int getCountOfEntriesInGroups(Collection<OriginEntryGroup> groups) {
        return laborOriginEntryDao.getCountOfEntriesInGroups(groups);
    }

    /**
     * @see org.kuali.module.labor.service.LaborLaborOriginEntryService#getCountOfEntriesInSingleGroup(org.kuali.kfs.gl.businessobject.OriginEntryGroup)
     */
    public int getCountOfEntriesInSingleGroup(OriginEntryGroup group) {
        List<OriginEntryGroup> groups = new ArrayList<OriginEntryGroup>();
        groups.add(group);

        return this.getCountOfEntriesInGroups(groups);
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborOriginEntryService#getEntryCollectionByGroup(org.kuali.kfs.gl.businessobject.OriginEntryGroup)
     */
    public Collection<LaborOriginEntry> getEntryCollectionByGroup(OriginEntryGroup group) {
        return laborOriginEntryDao.getEntryCollectionByGroup(group);
    }

    /**
     * Returns all labor origin entry groups created on the given date to back them up
     * 
     * @param backupDate the date to find labor origin entry groups created on
     * @see org.kuali.kfs.module.ld.service.LaborOriginEntryService#getLaborBackupGroups(java.sql.Date)
     * @see org.kuali.kfs.module.ld.dataaccess.LaborOriginEntryDao#getLaborBackupGroups(java.sql.Date)
     */
    public Collection getLaborBackupGroups(Date backupDate) {
        LOG.debug("getBackupGroups() started");

        return laborOriginEntryDao.getLaborBackupGroups(backupDate);
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborOriginEntryService#createLaborBackupGroup()
     */
    public void createLaborBackupGroup() {
        LOG.debug("createBackupGroup() started");

        // Get the groups that need to be added
        Date today = dateTimeService.getCurrentSqlDate();
        Collection<OriginEntryGroup> originEntryGroups = laborOriginEntryDao.getLaborGroupsToBackup(today);

        // Create the new group
        OriginEntryGroup backupGroup = originEntryGroupService.createGroup(today, OriginEntrySource.LABOR_BACKUP, true, true, true);

        for (OriginEntryGroup group : originEntryGroups) {

            // Get only LaborOriginEntryGroup
            if (group.getSourceCode().startsWith("L")) { // TODO: the hard-coded constant is a problem
                Iterator<LaborOriginEntry> laborOriginEntries = laborOriginEntryDao.getLaborEntriesByGroup(group, 0);

                while (laborOriginEntries != null && laborOriginEntries.hasNext()) {
                    LaborOriginEntry entry = laborOriginEntries.next();

                    entry.setEntryId(null);
                    entry.setObjectId(new Guid().toString());
                    entry.setGroup(backupGroup);
                    laborOriginEntryDao.saveOriginEntry(entry);
                }

                group.setProcess(false);
                group.setScrub(false);
                originEntryGroupService.save(group);
            }
        }
    }
    
    public  Iterator<LaborOriginEntry> getEntriesIteratorByGroupIdWithoutErrorChecking(String fileName) {
        File file = new File(batchFileDirectoryName + File.separator + fileName);
        
        return new LaborOriginEntryFileIterator(file);
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborOriginEntryService#getGroupCount(java.lang.Integer)
     */
    public Integer getGroupCount(Integer groupId) {
        return laborOriginEntryDao.getGroupCount(groupId);
    }
    
    public Integer getGroupCount(String fileName){
        Iterator<LaborOriginEntry> fileIterator = getEntriesIteratorByGroupIdWithoutErrorChecking(fileName);
        int count = 0;
        
        while(fileIterator.hasNext()){
            count++;
            fileIterator.next();
        }
        return count;
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

    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }
}

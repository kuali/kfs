/*
 * Copyright 2005 The Kuali Foundation
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
package org.kuali.kfs.gl.service.impl;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.impl.OriginEntryFileIterator;
import org.kuali.kfs.gl.businessobject.LedgerEntryForReporting;
import org.kuali.kfs.gl.businessobject.LedgerEntryHolder;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.PosterOutputSummaryEntry;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.gl.service.OriginEntryService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
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

    private OriginEntryGroupService originEntryGroupService;

    private DateTimeService dateTimeService;
    private String batchFileDirectoryName;

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
    
    public void createEntry(OriginEntryFull originEntry, PrintStream ps) {
        LOG.debug("createEntry() with PrintStream started");   
        
        try {
            ps.printf("%s\n", originEntry.getLine());
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
        
    }

    /**
     * Given a collection of group ids, summarize the entries in each group.
     * @param groupIdList a Collection of the ids of origin entry groups to summarize
     * @return a LedgerEntryHolder with all of the summarized information
     * @see org.kuali.kfs.gl.service.OriginEntryService#getSummaryByGroupId(Collection)
     */
    
    //TODO:- This method used for report. I will delete this method after all reports are done.
    public LedgerEntryHolder getSummaryByGroupId(Collection groupIdList) {
        LOG.debug("getSummaryByGroupId() started");

        LedgerEntryHolder ledgerEntryHolder = new LedgerEntryHolder();

        if (groupIdList.size() == 0) {
            return ledgerEntryHolder;
        }

        return ledgerEntryHolder;
    }

    /**
     * Creates or updates a ledger entry with the array of information from the given entry summary object
     * 
     * @param entrySummary a collection of java.lang.Objects, which is what OJB report queries return
     * @return a LedgerEntry holding the given report summarization data
     */
    public static LedgerEntryForReporting buildLedgerEntry(Object[] entrySummary) {
        // extract the data from an array and use them to populate a ledger entry
        Object oFiscalYear = entrySummary[0];
        Object oPeriodCode = entrySummary[1];
        Object oBalanceType = entrySummary[2];
        Object oOriginCode = entrySummary[3];
        Object oDebitCreditCode = entrySummary[4];
        Object oAmount = entrySummary[5];
        Object oCount = entrySummary[6];

        Integer fiscalYear = oFiscalYear != null ? new Integer(oFiscalYear.toString()) : null;
        String periodCode = oPeriodCode != null ? oPeriodCode.toString() : GeneralLedgerConstants.getSpaceUniversityFiscalPeriodCode();
        String balanceType = oBalanceType != null ? oBalanceType.toString() : GeneralLedgerConstants.getSpaceBalanceTypeCode();
        String originCode = oOriginCode != null ? oOriginCode.toString() : GeneralLedgerConstants.getSpaceFinancialSystemOriginationCode();
        String debitCreditCode = oDebitCreditCode != null ? oDebitCreditCode.toString() : GeneralLedgerConstants.getSpaceDebitCreditCode();
        KualiDecimal amount = oAmount != null ? new KualiDecimal(oAmount.toString()) : KualiDecimal.ZERO;
        int count = oCount != null ? Integer.parseInt(oCount.toString()) : 0;

        // construct a ledger entry with the information fetched from the given array
        LedgerEntryForReporting ledgerEntry = new LedgerEntryForReporting(fiscalYear, periodCode, balanceType, originCode);
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
     * This method writes origin entries into a file format. This particular implementation will use the OriginEntryFull.getLine
     * method to generate the text for this file.
     * 
     * @param entries An iterator of OriginEntries
     * @param bw an opened, ready-for-output bufferedOutputStream.
     * @see org.kuali.kfs.gl.service.OriginEntryService#flatFile(java.util.Iterator, java.io.BufferedOutputStream)
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
    
    public  Map getEntriesByGroupIdWithPath(String fileNameWithPath, List<OriginEntryFull> originEntryList) {
        
        FileReader INPUT_GLE_FILE = null;
        BufferedReader INPUT_GLE_FILE_br;
        try {
            INPUT_GLE_FILE = new FileReader(fileNameWithPath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        INPUT_GLE_FILE_br = new BufferedReader(INPUT_GLE_FILE);
        
        boolean loadError = false;
        //returnErrorList is list of List<Message>
        Map returnMessageMap = getEntriesByBufferedReader(INPUT_GLE_FILE_br, originEntryList);

        try{
            INPUT_GLE_FILE_br.close();
            INPUT_GLE_FILE.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        return returnMessageMap;
    }
    
    public Map getEntriesByBufferedReader(BufferedReader inputBufferedReader, List<OriginEntryFull> originEntryList) {
        String line;
        int lineNumber = 0;
        Map returnMessageMap = new HashMap();
        try {
            List<Message> tmperrors = new ArrayList();    
            while ((line = inputBufferedReader.readLine()) != null) {
                lineNumber++;
                OriginEntryFull originEntry = new OriginEntryFull();
                tmperrors = originEntry.setFromTextFileForBatch(line, lineNumber);
                originEntry.setEntryId(lineNumber);
                if (tmperrors.size() > 0){
                    returnMessageMap.put(new Integer(lineNumber), tmperrors);
                } else {
                    originEntryList.add(originEntry);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
            
        return returnMessageMap;

        
    }

    /**
     * get the summarized information of poster input entries that belong to the entry groups with the given group id list
     * 
     * @param groupIdList the origin entry groups
     * @return a map of summarized information of poster input entries within the specified groups
     * @see org.kuali.kfs.gl.service.OriginEntryService#getPosterOutputSummaryByGroupId(java.util.Collection)
     */
    
    //TODO:- This method used for report. This method can be deleted after all reports are done.
    public Map<String, PosterOutputSummaryEntry> getPosterOutputSummaryByGroupId(Collection groupIdList) {
        LOG.debug("getPosterOutputSummaryByGroupId() started");

        Map<String, PosterOutputSummaryEntry> output = new HashMap<String, PosterOutputSummaryEntry>();

        if (groupIdList.size() == 0) {
            return output;
        }

        return output;
    }

    public Integer getGroupCount(String fileNameWithPath){
        File file = new File(fileNameWithPath);
        Iterator<OriginEntryFull> fileIterator = new OriginEntryFileIterator(file);
        int count = 0;
        
        while(fileIterator.hasNext()){
            count++;
            fileIterator.next();
        }
        return count;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }
}

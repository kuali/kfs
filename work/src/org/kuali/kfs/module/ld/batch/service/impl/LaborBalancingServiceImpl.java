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
package org.kuali.kfs.module.ld.batch.service.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.BalancingService;
import org.kuali.kfs.gl.batch.service.impl.BalancingServiceBaseImpl;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.businessobject.LedgerBalanceHistory;
import org.kuali.kfs.gl.businessobject.OriginEntry;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.batch.LaborBalancingStep;
import org.kuali.kfs.module.ld.businessobject.LaborBalanceHistory;
import org.kuali.kfs.module.ld.businessobject.LaborEntryHistory;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.sys.FileUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BalancingService for Labor balancing
 */
@Transactional
public class LaborBalancingServiceImpl extends BalancingServiceBaseImpl<LaborEntryHistory, LaborBalanceHistory> implements BalancingService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborBalancingServiceImpl.class);
    
    protected File laborPosterInputFile = null;
    protected File laborPosterErrorOutputFile = null;
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getPosterInputFile()
     */
    public File getPosterInputFile() {
        // avoid running scanning logic on file system
        if (laborPosterInputFile != null) {
            return laborPosterInputFile;
        }
        
        FilenameFilter filenameFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.startsWith(LaborConstants.BatchFileSystem.POSTER_INPUT_FILE) &&
                        name.endsWith(GeneralLedgerConstants.BatchFileSystem.EXTENSION));
            }
        };
        
        laborPosterInputFile = FileUtil.getNewestFile(new File(batchFileDirectoryName), filenameFilter);
        
        return laborPosterInputFile;
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getPosterErrorOutputFile()
     */
    public File getPosterErrorOutputFile() {
        // avoid running scanning logic on file system
        if (laborPosterErrorOutputFile != null) {
            return laborPosterErrorOutputFile;
        }
        
        FilenameFilter filenameFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.startsWith(LaborConstants.BatchFileSystem.POSTER_ERROR_OUTPUT_FILE) &&
                        name.endsWith(GeneralLedgerConstants.BatchFileSystem.EXTENSION));
            }
        };
        
        laborPosterErrorOutputFile = FileUtil.getNewestFile(new File(batchFileDirectoryName), filenameFilter);
        
        return laborPosterErrorOutputFile;
    }
    
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getPastFiscalYearsToConsider()
     */
    public int getPastFiscalYearsToConsider() {
        return Integer.parseInt(parameterService.getParameterValue(LaborBalancingStep.class, LaborConstants.Balancing.NUMBER_OF_PAST_FISCAL_YEARS_TO_INCLUDE));
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getComparisonFailuresToPrintPerReport()
     */
    public int getComparisonFailuresToPrintPerReport() {
        return Integer.parseInt(parameterService.getParameterValue(LaborBalancingStep.class, LaborConstants.Balancing.NUMBER_OF_COMPARISON_FAILURES_TO_PRINT_PER_REPORT));
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getShortTableLabel(java.lang.String)
     */
    public String getShortTableLabel(String businessObjectName) {
        Map<String, String> names = new HashMap<String, String>();
        names.put((Entry.class).getSimpleName(), kualiConfigurationService.getPropertyString(LaborKeyConstants.Balancing.REPORT_ENTRY_LABEL));
        names.put((LaborEntryHistory.class).getSimpleName(), kualiConfigurationService.getPropertyString(LaborKeyConstants.Balancing.REPORT_ENTRY_LABEL));
        names.put((Balance.class).getSimpleName(), kualiConfigurationService.getPropertyString(LaborKeyConstants.Balancing.REPORT_BALANCE_LABEL));
        names.put((LaborBalanceHistory.class).getSimpleName(), kualiConfigurationService.getPropertyString(LaborKeyConstants.Balancing.REPORT_BALANCE_LABEL));
        
        return names.get(businessObjectName) == null ? kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.REPORT_UNKNOWN_LABEL) : names.get(businessObjectName);
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getOriginEntry(java.lang.String, int)
     */
    public OriginEntry getOriginEntry(String inputLine, int lineNumber) {
        LaborOriginEntry originEntry = new LaborOriginEntry();
        originEntry.setFromTextFileForBatch(inputLine, lineNumber);
        
        return originEntry;
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#updateEntryHistory(org.kuali.kfs.gl.businessobject.OriginEntry)
     * @see org.kuali.kfs.module.ld.batch.service.impl.LaborPosterServiceImpl#postAsLedgerEntry(org.kuali.kfs.gl.businessobject.Transaction, int, java.util.Date)
     */
    public void updateEntryHistory(OriginEntry originEntry) {
        // TODO Retrieve and update 1 by 1? Is a HashMap or cache better so that storing only occurs once at the end?
        LaborOriginEntry laborOriginEntry = (LaborOriginEntry) originEntry;
        LaborEntryHistory ledgerEntryHistory = new LaborEntryHistory(laborOriginEntry);

        LaborEntryHistory retrievedLedgerEntryHistory = (LaborEntryHistory) businessObjectService.retrieve(ledgerEntryHistory);
        if(ObjectUtils.isNotNull(retrievedLedgerEntryHistory)) {
            ledgerEntryHistory = retrievedLedgerEntryHistory;
        }
        
        ledgerEntryHistory.addAmount(laborOriginEntry.getTransactionLedgerEntryAmount());
        
        businessObjectService.save(ledgerEntryHistory);
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#updateBalanceHistory(org.kuali.kfs.gl.businessobject.OriginEntry)
     * @see org.kuali.kfs.module.ld.batch.service.impl.LaborPosterServiceImpl#updateLedgerBalance(org.kuali.kfs.gl.businessobject.Transaction, int, java.util.Date)
     */
    public void updateBalanceHistory(OriginEntry originEntry) {
        // TODO Retrieve and update 1 by 1? Is a HashMap or cache better so that storing only occurs once at the end?
        LaborOriginEntry laborOriginEntry = (LaborOriginEntry) originEntry;
        LaborBalanceHistory ledgerBalanceHistory = new LaborBalanceHistory(laborOriginEntry);
        
        LaborBalanceHistory retrievedLedgerBalanceHistory = (LaborBalanceHistory) businessObjectService.retrieve(ledgerBalanceHistory);
        if(ObjectUtils.isNotNull(retrievedLedgerBalanceHistory)) {
            ledgerBalanceHistory = retrievedLedgerBalanceHistory;
        }
        
        // Make sure the amount update properly recognized debit / credit logic. This is modeled after LaborPosterServiceImpl#updateLedgerBalance
        String debitCreditCode = laborOriginEntry.getTransactionDebitCreditCode();
        KualiDecimal amount = laborOriginEntry.getTransactionLedgerEntryAmount();
        amount = debitCreditCode.equals(KFSConstants.GL_CREDIT_CODE) ? amount.negated() : amount;

        ledgerBalanceHistory.addAmount(laborOriginEntry.getUniversityFiscalPeriodCode(), amount);
        
        businessObjectService.save(ledgerBalanceHistory);  
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getBalance(org.kuali.kfs.gl.businessobject.LedgerBalanceHistory)
     */
    public Balance getBalance(LedgerBalanceHistory ledgerBalanceHistory) {
        LedgerBalance ledgerBalance = new LedgerBalance((LaborBalanceHistory) ledgerBalanceHistory);
        return (LedgerBalance) businessObjectService.retrieve(ledgerBalance);
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#clearPosterFileCache()
     */
    public void clearPosterFileCache() {
        this.laborPosterInputFile = null;
        this.laborPosterErrorOutputFile = null;
    }
}

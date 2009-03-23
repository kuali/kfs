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

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.BalancingService;
import org.kuali.kfs.gl.batch.service.impl.BalancingServiceBaseImpl;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.BalanceHistory;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.businessobject.EntryHistory;
import org.kuali.kfs.gl.businessobject.LedgerBalanceHistory;
import org.kuali.kfs.gl.businessobject.OriginEntry;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.businessobject.LaborBalanceHistory;
import org.kuali.kfs.module.ld.businessobject.LaborEntryHistory;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
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
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getReportFilename()
     */
    public String getReportFilename() {
        return LaborConstants.BatchFileSystem.BALANCING_REPORT_FILENAME_PREFIX;
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getReportTitle()
     */
    public String getReportTitle() {
        return kualiConfigurationService.getPropertyString(LaborKeyConstants.Balancing.REPORT_FILE_TITLE);
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getPosterInputFilename()
     */
    public String getPosterInputFilename() {
        return LaborConstants.BatchFileSystem.POSTER_INPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getPosterErrorOutputFilename()
     */
    public String getPosterErrorOutputFilename() {
        return LaborConstants.BatchFileSystem.POSTER_ERROR_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getShortTableLabel(java.lang.String)
     */
    public String getShortTableLabel(String businessObjectName) {
        Map<String, String> names = new HashMap<String, String>();
        names.put((Entry.class).getSimpleName(), kualiConfigurationService.getPropertyString(LaborKeyConstants.Balancing.REPORT_ENTRY_LABEL));
        names.put((EntryHistory.class).getSimpleName(), kualiConfigurationService.getPropertyString(LaborKeyConstants.Balancing.REPORT_ENTRY_LABEL));
        names.put((Balance.class).getSimpleName(), kualiConfigurationService.getPropertyString(LaborKeyConstants.Balancing.REPORT_BALANCE_LABEL));
        names.put((BalanceHistory.class).getSimpleName(), kualiConfigurationService.getPropertyString(LaborKeyConstants.Balancing.REPORT_BALANCE_LABEL));
        
        return names.get(businessObjectName) == null ? kualiConfigurationService.getPropertyString(KFSKeyConstants.Balancing.REPORT_UNKNOWN_LABEL) : names.get(businessObjectName);
    }
    
    /**
     * TODO should call a DoA executing the queries instead of printing a message
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getTableCreationInstructions(int)
     */
    public String getTableCreationInstructions(int startUniversityFiscalYear) {
        return 
              "INSERT INTO LD_LDGR_ENTR_HIST_T (UNIV_FISCAL_YR, FIN_COA_CD, FIN_OBJECT_CD, FIN_BALANCE_TYP_CD, UNIV_FISCAL_PRD_CD, TRN_DEBIT_CRDT_CD, OBJ_ID, VER_NBR, TRN_LDGR_ENTR_AMT, ROW_CNT)\n"
            + "SELECT UNIV_FISCAL_YR, FIN_COA_CD, FIN_OBJECT_CD, FIN_BALANCE_TYP_CD, UNIV_FISCAL_PRD_CD, TRN_DEBIT_CRDT_CD, OBJ_ID, 1, sum(TRN_LDGR_ENTR_AMT), count(*)\n"
            + "FROM LD_LDGR_ENTR_T\n"
            + "WHERE UNIV_FISCAL_YR >= " + startUniversityFiscalYear + "\n"
            + "GROUP BY UNIV_FISCAL_YR, FIN_COA_CD, FIN_OBJECT_CD, FIN_BALANCE_TYP_CD, UNIV_FISCAL_PRD_CD, TRN_DEBIT_CRDT_CD\n\n"
            
            + "INSERT INTO LD_LDGR_BAL_HIST_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, POSITION_NBR, EMPLID, OBJ_ID, VER_NBR, ACLN_ANNL_BAL_AMT, FIN_BEG_BAL_LN_AMT, CONTR_GR_BB_AC_AMT, MO1_ACCT_LN_AMT, MO2_ACCT_LN_AMT, MO3_ACCT_LN_AMT, MO4_ACCT_LN_AMT, MO5_ACCT_LN_AMT, MO6_ACCT_LN_AMT, MO7_ACCT_LN_AMT, MO8_ACCT_LN_AMT, MO9_ACCT_LN_AMT, MO10_ACCT_LN_AMT, MO11_ACCT_LN_AMT, MO12_ACCT_LN_AMT, MO13_ACCT_LN_AMT)\n"
            + "SELECT UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, POSITION_NBR, EMPLID, OBJ_ID, 1, ACLN_ANNL_BAL_AMT, FIN_BEG_BAL_LN_AMT, CONTR_GR_BB_AC_AMT, MO1_ACCT_LN_AMT, MO2_ACCT_LN_AMT, MO3_ACCT_LN_AMT, MO4_ACCT_LN_AMT, MO5_ACCT_LN_AMT, MO6_ACCT_LN_AMT, MO7_ACCT_LN_AMT, MO8_ACCT_LN_AMT, MO9_ACCT_LN_AMT, MO10_ACCT_LN_AMT, MO11_ACCT_LN_AMT, MO12_ACCT_LN_AMT, MO13_ACCT_LN_AMT\n"
            + "FROM LD_LDGR_BAL_T\n"
            + "WHERE UNIV_FISCAL_YR >= " + startUniversityFiscalYear + "\n\n"
            
            + "Comparison skipped since no historic data available.\n";
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
}

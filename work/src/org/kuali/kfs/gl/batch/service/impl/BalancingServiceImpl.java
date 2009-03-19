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
package org.kuali.kfs.gl.batch.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.TextReportHelper;
import org.kuali.kfs.gl.batch.service.BalancingService;
import org.kuali.kfs.gl.businessobject.AccountBalance;
import org.kuali.kfs.gl.businessobject.AccountBalanceHistory;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.BalanceHistory;
import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.gl.businessobject.EncumbranceHistory;
import org.kuali.kfs.gl.businessobject.EntryHistory;
import org.kuali.kfs.gl.businessobject.LedgerBalanceHistory;
import org.kuali.kfs.gl.businessobject.OriginEntry;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.dataaccess.AccountBalanceDao;
import org.kuali.kfs.gl.dataaccess.EncumbranceDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BalancingService of GL balancing
 */
@Transactional
public class BalancingServiceImpl extends BalancingServiceBaseImpl<BalanceHistory, EntryHistory> implements BalancingService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalancingServiceImpl.class);
    
    private static final String BALANCING_FILENAME = "balancing_";
    private static final String BALANCING_FILETITLE = "General Ledger Balancing Report";
    private static final String BALANCE_LABEL = "GLBL";
    private static final String ENTRY_LABEL = "GLEN";
    private static final String ACCOUNT_BALANCE_LABEL = "ACBL";
    private static final String ENCUMBRANCE_LABEL = "GLEC";
    private static final String ACCOUNT_BALANCE_BUSINESS_OBJECT_NAME = "AccountBalance";
    private static final String ENCUMBRANCE_BUSINESS_OBJECT_NAME = "Encumbrance";
    private static final String ACCOUNT_BALANCE_HISTORY_BUSINESS_OBJECT_NAME = "AccountBalanceHistory";
    private static final String ENCUMBRANCE_HISTORY_BUSINESS_OBJECT_NAME = "EncumbranceHistory";
    private static final String UNKNOWN_LABEL = "<Unknown Lable>";
    
    private AccountBalanceDao accountBalanceDao;
    private EncumbranceDao encumbranceDao;
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getReportFilename()
     */
    public String getReportFilename() {
        return BALANCING_FILENAME;
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getReportTitle()
     */
    public String getReportTitle() {
        return BALANCING_FILETITLE;
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getPosterInputFilename()
     */
    public String getPosterInputFilename() {
        return GeneralLedgerConstants.BatchFileSystem.POSTER_INPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getPosterErrorOutputFilename()
     */
    public String getPosterErrorOutputFilename() {
        return GeneralLedgerConstants.BatchFileSystem.POSTER_ERROR_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getBalanceLabel()
     */
    public String getBalanceLabel() {
        return BALANCE_LABEL;
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getEntryLabel()
     */
    public String getEntryLabel() {
        return ENTRY_LABEL;
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getTableCreationInstructions(int)
     */
    public String getTableCreationInstructions(int startUniversityFiscalYear) {
        return 
              "INSERT INTO GL_ENTRY_HIST_T (UNIV_FISCAL_YR, FIN_COA_CD, FIN_OBJECT_CD, FIN_BALANCE_TYP_CD, UNIV_FISCAL_PRD_CD, TRN_DEBIT_CRDT_CD, VER_NBR, TRN_LDGR_ENTR_AMT, ROW_CNT)\n"
            + "SELECT UNIV_FISCAL_YR, FIN_COA_CD, FIN_OBJECT_CD, FIN_BALANCE_TYP_CD, UNIV_FISCAL_PRD_CD, TRN_DEBIT_CRDT_CD, 1, sum(TRN_LDGR_ENTR_AMT), count(*)\n"
            + "FROM GL_ENTRY_T\n"
            + "WHERE UNIV_FISCAL_YR >= " + startUniversityFiscalYear + "\n"
            + "GROUP BY UNIV_FISCAL_YR, FIN_COA_CD, FIN_OBJECT_CD, FIN_BALANCE_TYP_CD, UNIV_FISCAL_PRD_CD, TRN_DEBIT_CRDT_CD\n\n"
            
            + "INSERT INTO GL_BALANCE_HIST_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, ACLN_ANNL_BAL_AMT, FIN_BEG_BAL_LN_AMT, CONTR_GR_BB_AC_AMT, MO1_ACCT_LN_AMT, MO2_ACCT_LN_AMT, MO3_ACCT_LN_AMT, MO4_ACCT_LN_AMT, MO5_ACCT_LN_AMT, MO6_ACCT_LN_AMT, MO7_ACCT_LN_AMT, MO8_ACCT_LN_AMT, MO9_ACCT_LN_AMT, MO10_ACCT_LN_AMT, MO11_ACCT_LN_AMT, MO12_ACCT_LN_AMT, MO13_ACCT_LN_AMT)\n"
            + "SELECT UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, ACLN_ANNL_BAL_AMT, FIN_BEG_BAL_LN_AMT, CONTR_GR_BB_AC_AMT, MO1_ACCT_LN_AMT, MO2_ACCT_LN_AMT, MO3_ACCT_LN_AMT, MO4_ACCT_LN_AMT, MO5_ACCT_LN_AMT, MO6_ACCT_LN_AMT, MO7_ACCT_LN_AMT, MO8_ACCT_LN_AMT, MO9_ACCT_LN_AMT, MO10_ACCT_LN_AMT, MO11_ACCT_LN_AMT, MO12_ACCT_LN_AMT, MO13_ACCT_LN_AMT\n"
            + "FROM GL_BALANCE_T\n"
            + "WHERE UNIV_FISCAL_YR >= " + startUniversityFiscalYear + "\n\n"
            
            + "INSERT INTO GL_ACCT_BALANCES_HIST_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT)\n"
            + "SELECT UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT\n"
            + "FROM GL_ACCT_BALANCES_T\n"
            + "WHERE UNIV_FISCAL_YR >= " + startUniversityFiscalYear + "\n\n"
            
            + "INSERT INTO GL_ENCUMBRANCE_HIST_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FDOC_TYP_CD, FS_ORIGIN_CD, FDOC_NBR, ACLN_ENCUM_AMT, ACLN_ENCUM_CLS_AMT)\n"
            + "SELECT UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FDOC_TYP_CD, FS_ORIGIN_CD, FDOC_NBR, ACLN_ENCUM_AMT, ACLN_ENCUM_CLS_AMT\n"
            + "FROM GL_ENCUMBRANCE_T\n"
            + "WHERE UNIV_FISCAL_YR >= " + startUniversityFiscalYear + "\n\n"
            
            + "Comparison skipped since no historic data available.\n";
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getOriginEntry(java.lang.String, int)
     */
    public OriginEntry getOriginEntry(String inputLine, int lineNumber) {
        // We need a OriginEntryFull because that's what updateBalanceHistory is looking for
        OriginEntryFull originEntry = new OriginEntryFull();
        originEntry.setFromTextFileForBatch(inputLine, lineNumber);
        
        return originEntry;
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#updateEntryHistory(org.kuali.kfs.gl.businessobject.OriginEntry)
     * @see org.kuali.kfs.gl.batch.service.impl.PostEntry#post(org.kuali.kfs.gl.businessobject.Transaction, int, java.util.Date)
     */
    public void updateEntryHistory(OriginEntry originEntry) {
        // TODO Retrieve and update 1 by 1? Is a HashMap or cache better so that storing only occurs once at the end?
        EntryHistory entryHistory = new EntryHistory(originEntry);

        EntryHistory retrievedEntryHistory = (EntryHistory) businessObjectService.retrieve(entryHistory);
        if(ObjectUtils.isNotNull(retrievedEntryHistory)) {
            entryHistory = retrievedEntryHistory;
        }
        
        entryHistory.addAmount(originEntry.getTransactionLedgerEntryAmount());
        
        businessObjectService.save(entryHistory);
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#updateBalanceHistory(org.kuali.kfs.gl.businessobject.OriginEntry)
     * @see org.kuali.kfs.gl.batch.service.impl.PostBalance#post(org.kuali.kfs.gl.businessobject.Transaction, int, java.util.Date)
     */
    public void updateBalanceHistory(OriginEntry originEntry) {
        // TODO Retrieve and update 1 by 1? Is a HashMap or cache better so that storing only occurs once at the end?
        OriginEntryFull originEntryFull = (OriginEntryFull) originEntry;
        BalanceHistory balanceHistory = new BalanceHistory(originEntryFull);
        
        BalanceHistory retrievedBalanceHistory = (BalanceHistory) businessObjectService.retrieve(balanceHistory);
        if(ObjectUtils.isNotNull(retrievedBalanceHistory)) {
            balanceHistory = retrievedBalanceHistory;
        }
        
        KualiDecimal amount = originEntryFull.getTransactionLedgerEntryAmount();

        // Make sure the amount update properly recognized debit / credit logic. This is modeled after PostBalance#post
        originEntryFull.refreshReferenceObject(KFSPropertyConstants.BALANCE_TYPE);
        originEntryFull.refreshReferenceObject(KFSPropertyConstants.OBJECT_TYPE);
        if (originEntryFull.getBalanceType().isFinancialOffsetGenerationIndicator()) {
            if (!originEntryFull.getTransactionDebitCreditCode().equals(originEntryFull.getObjectType().getFinObjectTypeDebitcreditCd())) {
                amount = amount.negated();
            }
        }

        balanceHistory.addAmount(originEntryFull.getUniversityFiscalPeriodCode(), amount);
        
        businessObjectService.save(balanceHistory);
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.BalancingService#getBalance(org.kuali.kfs.gl.businessobject.LedgerBalanceHistory)
     */
    public Balance getBalance(LedgerBalanceHistory ledgerBalanceHistory) {
        Balance balance = new Balance((BalanceHistory) ledgerBalanceHistory);
        return (Balance) businessObjectService.retrieve(balance);
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.impl.BalancingServiceBaseImpl#doesCustomHistoryExist(java.lang.Integer)
     */
    @Override
    protected boolean doesCustomHistoryExist(Integer fiscalYear) {
        return (this.getHistoryCount(fiscalYear, AccountBalanceHistory.class) > 0 &&
                this.getHistoryCount(fiscalYear, EncumbranceHistory.class) > 0);
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.impl.BalancingServiceBaseImpl#deleteCustomHistory(java.lang.Integer)
     */
    @Override
    protected void deleteCustomHistory(Integer fiscalYear, TextReportHelper textReportHelper) {
        deleteHistory(fiscalYear, AccountBalanceHistory.class);
        deleteHistory(fiscalYear, EncumbranceHistory.class);
        
        textReportHelper.printf((AccountBalanceHistory.class).getSimpleName() + " or " + (EncumbranceHistory.class).getSimpleName() + " for universityFiscalYear=" + fiscalYear + " found. That is out of range and will be deleted.\n");
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.impl.BalancingServiceBaseImpl#updateCustomHistory(org.kuali.kfs.gl.businessobject.OriginEntry)
     */
    @Override
    protected void updateCustomHistory(OriginEntry originEntry) {
        this.updateAccountBalanceHistory(originEntry);
        this.updateEncumbranceHistory(originEntry);
    }
    
    
    /**
     * Update the account balance history table
     * @param originEntry representing the update details
     * @see org.kuali.kfs.gl.batch.service.impl.PostAccountBalance#post(org.kuali.kfs.gl.businessobject.Transaction, int, java.util.Date)
     */
    private void updateAccountBalanceHistory(OriginEntry originEntry) {
        OriginEntryFull originEntryFull = (OriginEntryFull) originEntry;
        
        // As taken from PostAccountBalance#post: only post transactions where: balance type code is AC or CB or where object type isn't FB and
        // balance type code is EX, IE, PE and CE
        originEntryFull.refreshReferenceObject(KFSPropertyConstants.OPTION);
        if ((originEntryFull.getFinancialBalanceTypeCode().equals(originEntryFull.getOption().getActualFinancialBalanceTypeCd()) || originEntryFull.getFinancialBalanceTypeCode().equals(originEntryFull.getOption().getBudgetCheckingBalanceTypeCd())) || (originEntryFull.getFinancialBalanceTypeCode().equals(originEntryFull.getOption().getExtrnlEncumFinBalanceTypCd()) || originEntryFull.getFinancialBalanceTypeCode().equals(originEntryFull.getOption().getIntrnlEncumFinBalanceTypCd()) || originEntryFull.getFinancialBalanceTypeCode().equals(originEntryFull.getOption().getPreencumbranceFinBalTypeCd()) || originEntryFull.getFinancialBalanceTypeCode().equals(originEntryFull.getOption().getCostShareEncumbranceBalanceTypeCd())) && (!originEntryFull.getFinancialObjectTypeCode().equals(originEntryFull.getOption().getFinObjectTypeFundBalanceCd()))) {
            // TODO Retrieve and update 1 by 1? Is a HashMap or cache better so that storing only occurs once at the end?
            AccountBalanceHistory accountBalanceHistory = new AccountBalanceHistory(originEntry);
    
            AccountBalanceHistory retrievedAccountBalanceHistory = (AccountBalanceHistory) businessObjectService.retrieve(accountBalanceHistory);
            if(ObjectUtils.isNotNull(retrievedAccountBalanceHistory)) {
                accountBalanceHistory = retrievedAccountBalanceHistory;
            }
            
            // Following is a copy of PostAccountBalance.updateAccountBalanceReturn since the blancing process is to do this independently
            if (accountBalanceHistory.addAmount(originEntryFull)) {
                businessObjectService.save(accountBalanceHistory);
            }
        }
    }
    
    /**
     * Update the encumbrance history table
     * @param originEntry representing the update details
     * @see org.kuali.kfs.gl.batch.service.impl.PostEncumbrance#post(org.kuali.kfs.gl.businessobject.Transaction, int, java.util.Date)
     */
    private void updateEncumbranceHistory(OriginEntry originEntry) {
        OriginEntryFull originEntryFull = (OriginEntryFull) originEntry;
        
        // PostEncumbrance.verifyTransaction is not run because entries that fail that verification will be in the error poster file which entries
        // are already ignored before being passed to this method.
        
        // As taken from PostEncumbrance#post: If the encumbrance update code is space or N, or the object type code is FB we don't need to post an encumbrance
        originEntryFull.refreshReferenceObject(KFSPropertyConstants.OPTION);
        if ((StringUtils.isBlank(originEntryFull.getTransactionEncumbranceUpdateCode())) || " ".equals(originEntryFull.getTransactionEncumbranceUpdateCode()) || KFSConstants.ENCUMB_UPDT_NO_ENCUMBRANCE_CD.equals(originEntryFull.getTransactionEncumbranceUpdateCode()) || originEntryFull.getOption().getFinObjectTypeFundBalanceCd().equals(originEntryFull.getFinancialObjectTypeCode())) {
            return;
        }
        
        EncumbranceHistory encumbranceHistory = new EncumbranceHistory(originEntryFull);
        if (KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD.equals(originEntryFull.getTransactionEncumbranceUpdateCode())) {
            encumbranceHistory.setDocumentNumber(originEntryFull.getReferenceFinancialDocumentNumber());
            encumbranceHistory.setOriginCode(originEntryFull.getReferenceFinancialSystemOriginationCode());
            encumbranceHistory.setDocumentTypeCode(originEntryFull.getReferenceFinancialDocumentTypeCode());
        }
        // TODO Retrieve and update 1 by 1? Is a HashMap or cache better so that storing only occurs once at the end?
        EncumbranceHistory retrievedEncumbranceHistory = (EncumbranceHistory) businessObjectService.retrieve(encumbranceHistory);
        
        if(ObjectUtils.isNotNull(retrievedEncumbranceHistory)) {
            encumbranceHistory = retrievedEncumbranceHistory;
        }
        
        // Following is a copy & paste of PostEncumbrance.updateEncumbrance since the blancing process is to do this independently
        encumbranceHistory.addAmount(originEntryFull);
        
        businessObjectService.save(encumbranceHistory);
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.impl.BalancingServiceBaseImpl#customCompareHistory(org.kuali.kfs.gl.TextReportHelper)
     */
    @Override
    protected Map<String, Integer> customCompareHistory(TextReportHelper textReportHelper) {
        Integer countAccountBalanceComparisionFailure = this.accountBalanceCompareHistory(textReportHelper);
        Integer countEncumbranceComparisionFailure = this.encumbranceCompareHistory(textReportHelper);
        
        // Using LinkedHashMap because we want it ordered
        Map<String, Integer> countMap = new LinkedHashMap<String, Integer>();
        countMap.put((AccountBalanceHistory.class).getSimpleName(), countAccountBalanceComparisionFailure);
        countMap.put((EncumbranceHistory.class).getSimpleName(), countEncumbranceComparisionFailure);
        
        return countMap;
    }
    
    /**
     * Does comparision, error printing and returns failure count for account balances
     * @param textReportHelper handle on TextReportHelper for fancy printing
     * @return failure count
     */
    protected Integer accountBalanceCompareHistory(TextReportHelper textReportHelper) {
        Integer countComparisionFailures = 0;
        
        // TODO findAll might not be a good idea performance wise. Do some kind of LIMIT stepping?
        // Finding all history lines as starting point for comparision
        for (Iterator<AccountBalanceHistory> iterator = businessObjectService.findAll(AccountBalanceHistory.class).iterator(); iterator.hasNext();) {
            AccountBalanceHistory accountBalanceHistory = iterator.next();
            
            AccountBalance accountBalance = (AccountBalance) businessObjectService.retrieve(new AccountBalance(accountBalanceHistory));
            
            if (!accountBalanceHistory.compareAmounts(accountBalance)) {
                // Compare failed, properly log it if we havn't written more then TOTAL_COMPARISION_FAILURES_TO_PRINT yet
                if (countComparisionFailures == 0) {
                    textReportHelper.writeErrorHeader(accountBalanceHistory, true);
                }
                countComparisionFailures++;
                if (countComparisionFailures <= TOTAL_COMPARISION_FAILURES_TO_PRINT) {
                    textReportHelper.writeErrors(accountBalanceHistory, new Message("Failed " + accountBalanceHistory.getClass().getSimpleName() + " balancing", Message.TYPE_WARNING));
                }
            }
        }
        
        if (countComparisionFailures != 0) {
            textReportHelper.printf("\nTotal failure count for " + (AccountBalanceHistory.class).getSimpleName() + " is " + countComparisionFailures + ". Only up to " + TOTAL_COMPARISION_FAILURES_TO_PRINT + " failures printed.\n");
        }
        
        return countComparisionFailures;
    }
    
    /**
     * Does comparision, error printing and returns failure count for encumbrances
     * @param textReportHelper handle on TextReportHelper for fancy printing
     * @return failure count
     */
    protected Integer encumbranceCompareHistory(TextReportHelper textReportHelper) {
        Integer countComparisionFailures = 0;
        
        // TODO findAll might not be a good idea performance wise. Do some kind of LIMIT stepping?
        // Finding all history lines as starting point for comparision
        for (Iterator<EncumbranceHistory> iterator = businessObjectService.findAll(EncumbranceHistory.class).iterator(); iterator.hasNext();) {
            EncumbranceHistory encumbranceHistory = iterator.next();
            
            Encumbrance encumbrance = (Encumbrance) businessObjectService.retrieve(new Encumbrance(encumbranceHistory));
            
            if (!encumbranceHistory.compareAmounts(encumbrance)) {
                // Compare failed, properly log it if we havn't written more then TOTAL_COMPARISION_FAILURES_TO_PRINT yet
                if (countComparisionFailures == 0) {
                    textReportHelper.writeErrorHeader(encumbranceHistory, true);
                }
                countComparisionFailures++;
                if (countComparisionFailures <= TOTAL_COMPARISION_FAILURES_TO_PRINT) {
                    textReportHelper.writeErrors(encumbranceHistory, new Message("Failed " + encumbranceHistory.getClass().getSimpleName() + " balancing", Message.TYPE_WARNING));
                }
            }
        }
        
        if (countComparisionFailures != 0) {
            textReportHelper.printf("\nTotal failure count for " + (EncumbranceHistory.class).getSimpleName() + " is " + countComparisionFailures + ". Only up to " + TOTAL_COMPARISION_FAILURES_TO_PRINT + " failures printed.\n");
        }
        
        return countComparisionFailures;
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.impl.BalancingServiceBaseImpl#getCustomLabel(java.lang.String)
     */
    @Override
    protected String getCustomLabel(String businessObjectName) {
        Map<String, String> names = new HashMap<String, String>();
        names.put(ACCOUNT_BALANCE_BUSINESS_OBJECT_NAME, ACCOUNT_BALANCE_LABEL);
        names.put(ACCOUNT_BALANCE_HISTORY_BUSINESS_OBJECT_NAME, ACCOUNT_BALANCE_LABEL);
        names.put(ENCUMBRANCE_BUSINESS_OBJECT_NAME, ENCUMBRANCE_LABEL);
        names.put(ENCUMBRANCE_HISTORY_BUSINESS_OBJECT_NAME, ENCUMBRANCE_LABEL);
        
        return names.get(businessObjectName) == null ? UNKNOWN_LABEL : names.get(businessObjectName);
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.impl.BalancingServiceBaseImpl#customPrintRowCountHistory(org.kuali.kfs.gl.TextReportHelper)
     */
    @Override
    protected void customPrintRowCountHistory(Integer fiscalYear, TextReportHelper textReportHelper){
        // Note that fiscal year is passed as null for the History tables because for those we shouldn't have data prior to the fiscal year anyway (and
        // if we do it's a bug that should be discovered)
        textReportHelper.printf("                             %s ROW COUNT - CALC. %-25s %,9d\n", getCustomLabel((AccountBalanceHistory.class).getSimpleName()), "(" + (AccountBalanceHistory.class).getSimpleName() + ")", this.getHistoryCount(null, AccountBalanceHistory.class));
        textReportHelper.printf("                             %s ROW COUNT - PROD.                           %,9d\n", getCustomLabel((AccountBalance.class).getSimpleName()), accountBalanceDao.findCountGreaterOrEqualThan(fiscalYear));
        textReportHelper.printf("                             %s ROW COUNT - CALC. %-25s %,9d\n", getCustomLabel((EncumbranceHistory.class).getSimpleName()), "(" + (EncumbranceHistory.class).getSimpleName() + ")", this.getHistoryCount(null, EncumbranceHistory.class));
        textReportHelper.printf("                             %s ROW COUNT - PROD.                           %,9d\n", getCustomLabel((Encumbrance.class).getSimpleName()), encumbranceDao.findCountGreaterOrEqualThan(fiscalYear));
    }

    /**
     * Sets the AccountBalanceDao
     * 
     * @param accountBalanceDao The AccountBalanceDao to set.
     */
    public void setAccountBalanceDao(AccountBalanceDao accountBalanceDao) {
        this.accountBalanceDao = accountBalanceDao;
    }
    
    /**
     * Sets the EncumbranceDao
     * 
     * @param encumbranceDao The EncumbranceDao to set.
     */
    public void setEncumbranceDao(EncumbranceDao encumbranceDao) {
        this.encumbranceDao = encumbranceDao;
    }
}

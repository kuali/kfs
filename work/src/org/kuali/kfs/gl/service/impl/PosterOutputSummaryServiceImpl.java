/*
 * Copyright 2009 The Kuali Foundation
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

import java.util.Comparator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.gl.batch.service.AccountingCycleCachingService;
import org.kuali.kfs.gl.businessobject.OriginEntryInformation;
import org.kuali.kfs.gl.businessobject.PosterOutputSummaryAmountHolder;
import org.kuali.kfs.gl.businessobject.PosterOutputSummaryEntry;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.gl.service.PosterOutputSummaryService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * The default implementation of the PosterOutputSummaryService
 */
public class PosterOutputSummaryServiceImpl implements PosterOutputSummaryService {
    private AccountingCycleCachingService accountingCycleCachingService;

    /**
     * Default implementation
     * @see org.kuali.kfs.gl.service.PosterOutputSummaryService#addOriginEntryAmountToAmountHolder(org.kuali.kfs.gl.businessobject.OriginEntryInformation, org.kuali.kfs.gl.businessobject.PosterOutputSummaryAmountHolder)
     */
    public void addAmountToAmountHolder(OriginEntryInformation originEntry, PosterOutputSummaryAmountHolder amountHolder) {
        final String debitCreditCode = originEntry.getTransactionDebitCreditCode();
        final KualiDecimal amount = originEntry.getTransactionLedgerEntryAmount();
        final String objectTypeCode = originEntry.getFinancialObjectTypeCode();
        
        amountHolder.addAmount(debitCreditCode, objectTypeCode, amount);
    }

    /**
     * Default implementation
     * @see org.kuali.kfs.gl.service.PosterOutputSummaryService#addTransactionAmountToAmountHolder(org.kuali.kfs.gl.businessobject.Transaction, org.kuali.kfs.gl.businessobject.PosterOutputSummaryAmountHolder)
     */
    public void addAmountToAmountHolder(Transaction transaction, PosterOutputSummaryAmountHolder amountHolder) {
        final String debitCreditCode = transaction.getTransactionDebitCreditCode();
        final KualiDecimal amount = transaction.getTransactionLedgerEntryAmount();
        final String objectTypeCode = transaction.getFinancialObjectTypeCode();

        amountHolder.addAmount(debitCreditCode, objectTypeCode, amount);
    }

    /**
     * @see org.kuali.kfs.gl.service.PosterOutputSummaryService#getEntryComparator()
     */
    public Comparator<PosterOutputSummaryEntry> getEntryComparator() {
        return new Comparator<PosterOutputSummaryEntry>() {
            
            /**
             * Compares the first PosterOutputSummaryEntry given to the second, based on - in order - balance type code,
             * university fiscal year, fiscal period code, and finally fund group
             * @param vladimir the first PosterOutputSummaryEntry to compare
             * @param estragon the second PosterOutputSummaryEntry to compare
             * @return the standard result of a compare operation: 0 if there's equality, less than 0 if vladimir is "less" than estragon, and more than 0 if vladimir is "greater" than estragon
             */
            public int compare(PosterOutputSummaryEntry vladimir, PosterOutputSummaryEntry estragon) {
                if (shouldCompare(vladimir.getBalanceTypeCode(), estragon.getBalanceTypeCode())) {
                    return vladimir.getBalanceTypeCode().toUpperCase().compareTo(estragon.getBalanceTypeCode().toUpperCase());
                } else if (shouldCompare(vladimir.getUniversityFiscalYear(), estragon.getUniversityFiscalYear())) {
                    return vladimir.getUniversityFiscalYear().compareTo(estragon.getUniversityFiscalYear());
                } else if (shouldCompare(vladimir.getFiscalPeriodCode(), estragon.getFiscalPeriodCode())) {
                    return vladimir.getFiscalPeriodCode().toUpperCase().compareTo(estragon.getFiscalPeriodCode().toUpperCase());
                } else if (shouldCompare(vladimir.getFundGroup(), estragon.getFundGroup())) {
                    return vladimir.getFundGroup().toUpperCase().compareTo(estragon.getFundGroup().toUpperCase());
                } else {
                    return 0;
                }
            }
            
            /**
             * Determines if it's safe to compare two Strings
             * @param s1 the first String we may compare
             * @param s2 the second String we may compare
             * @return true if comparison of these two Strings would be meaningful
             */
            protected boolean shouldCompare(String s1, String s2) {
                return !StringUtils.isBlank(s1) && !StringUtils.isBlank(s2) && !s1.equalsIgnoreCase(s2);
            }
            
            /**
             * Determine if it's safe to compare two Integers
             * @param i1 the first Integer we may compare
             * @param i2 the second Integer we may compare
             * @return true if comparison of the two Integers would be meaningful
             */
            protected boolean shouldCompare(Integer i1, Integer i2) {
                return i1 != null && i2 != null && !i1.equals(i2);
            }
        };
    }

    /**
     * @see org.kuali.kfs.gl.service.PosterOutputSummaryService#getPosterOutputSummaryEntryMapKey(org.kuali.kfs.gl.businessobject.OriginEntryInformation)
     */
    protected String getPosterOutputSummaryEntryMapKey(OriginEntryInformation originEntry) {
        return buildKey(originEntry.getFinancialBalanceTypeCode(), originEntry.getUniversityFiscalYear(), originEntry.getUniversityFiscalPeriodCode(), originEntry.getChartOfAccountsCode(), originEntry.getAccountNumber());
    }

    /**
     * @see org.kuali.kfs.gl.service.PosterOutputSummaryService#getPosterOutputSummaryEntryMapKey(org.kuali.kfs.gl.businessobject.Transaction)
     */
    protected String getPosterOutputSummaryEntryMapKey(Transaction transaction) {
        return buildKey(transaction.getFinancialBalanceTypeCode(), transaction.getUniversityFiscalYear(), transaction.getUniversityFiscalPeriodCode(), transaction.getChartOfAccountsCode(), transaction.getAccountNumber());
    }

    /**
     * Builds a map key based on the given information
     * @param balanceTypeCode the balance type code to put in the key
     * @param universityFiscalYear the fiscal year to put in the key
     * @param fiscalPeriodCode the period code to put in the key
     * @param subFundGroupCode the sub fund group code to put in the key
     * @return a key build from the various attributes
     */
    protected String buildKey(String balanceTypeCode, Integer universityFiscalYear, String fiscalPeriodCode, String chartOfAccountsCode, String accountNumber) {
        return StringUtils.join(new String[] {balanceTypeCode, universityFiscalYear == null ? "" : universityFiscalYear.toString(), fiscalPeriodCode, getFundGroupCodeForAccount(chartOfAccountsCode, accountNumber)}, ':');
    }
    
    /**
     * Returns the sub fund group for the given origin entry
     * @param originEntry the origin entry to find the sub fund group for, from its account
     * @return the sub fund group code related to the account used by this origin entry
     */
    protected String getFundGroupCodeForAccount(String chartOfAccountsCode, String accountNumber) {
        final Account account = this.getAccountingCycleCachingService().getAccount(chartOfAccountsCode, accountNumber);
        if (account != null) {
            final SubFundGroup subFundGroup = this.getAccountingCycleCachingService().getSubFundGroup(account.getSubFundGroupCode());
            if (subFundGroup != null) {
                return subFundGroup.getFundGroupCode();
            }
        }
        return "";
    }

    /**
     * @see org.kuali.kfs.gl.service.PosterOutputSummaryService#summarizeOriginEntry(org.kuali.kfs.gl.businessobject.OriginEntryInformation, java.util.Map)
     */
    public void summarize(OriginEntryInformation originEntry, Map<String, PosterOutputSummaryEntry> entries) {
        final String key = getPosterOutputSummaryEntryMapKey(originEntry);
        PosterOutputSummaryEntry entry = entries.get(key);
        if (entry == null) {
            entry = new PosterOutputSummaryEntry(originEntry.getFinancialBalanceTypeCode(), originEntry.getUniversityFiscalYear(), originEntry.getUniversityFiscalPeriodCode(), getFundGroupCodeForAccount(originEntry.getChartOfAccountsCode(), originEntry.getAccountNumber()));
            entries.put(key, entry);
        }
        addAmountToAmountHolder(originEntry, entry);
    }

    /**
     * @see org.kuali.kfs.gl.service.PosterOutputSummaryService#summarizeTransaction(org.kuali.kfs.gl.businessobject.Transaction, java.util.Map)
     */
    public void summarize(Transaction transaction, Map<String, PosterOutputSummaryEntry> entries) {
        final String key = getPosterOutputSummaryEntryMapKey(transaction);
        PosterOutputSummaryEntry entry = entries.get(key);
        if (entry == null) {
            entry = new PosterOutputSummaryEntry(transaction.getFinancialBalanceTypeCode(), transaction.getUniversityFiscalYear(), transaction.getUniversityFiscalPeriodCode(), getFundGroupCodeForAccount(transaction.getChartOfAccountsCode(), transaction.getAccountNumber()));
            entries.put(key, entry);
        }
        addAmountToAmountHolder(transaction, entry);
    }

    /**
     * Gets the accountingCycleCachingService attribute. 
     * @return Returns the accountingCycleCachingService.
     */
    public AccountingCycleCachingService getAccountingCycleCachingService() {
        return accountingCycleCachingService;
    }

    /**
     * Sets the accountingCycleCachingService attribute value.
     * @param accountingCycleCachingService The accountingCycleCachingService to set.
     */
    public void setAccountingCycleCachingService(AccountingCycleCachingService accountingCycleCachingService) {
        this.accountingCycleCachingService = accountingCycleCachingService;
    }
}

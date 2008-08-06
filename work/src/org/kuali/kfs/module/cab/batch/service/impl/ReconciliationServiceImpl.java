/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.cab.batch.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.batch.service.ReconciliationService;
import org.kuali.kfs.module.cab.businessobject.AccountLineGroup;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.businessobject.GlAccountLineGroup;
import org.kuali.kfs.module.cab.businessobject.PendingGlAccountLineGroup;
import org.kuali.kfs.module.cab.businessobject.PurApAccountLineGroup;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;

/**
 * Default implementation of {@link ReconciliationService}
 */
public class ReconciliationServiceImpl implements ReconciliationService {
    protected BusinessObjectService businessObjectService;
    protected List<Entry> ignoredEntries = new ArrayList<Entry>();
    protected List<Entry> duplicateEntries = new ArrayList<Entry>();
    protected Set<GlAccountLineGroup> matchedGroups = new HashSet<GlAccountLineGroup>();
    protected Set<GlAccountLineGroup> misMatchedGroups = new HashSet<GlAccountLineGroup>();
    protected HashMap<GlAccountLineGroup, GlAccountLineGroup> glEntryGroupMap = new HashMap<GlAccountLineGroup, GlAccountLineGroup>();
    protected HashMap<PendingGlAccountLineGroup, PendingGlAccountLineGroup> pendingGlEntryGroupMap = new HashMap<PendingGlAccountLineGroup, PendingGlAccountLineGroup>();
    protected HashMap<PurApAccountLineGroup, PurApAccountLineGroup> purapAcctGroupMap = new HashMap<PurApAccountLineGroup, PurApAccountLineGroup>();

    /**
     * @see org.kuali.kfs.module.cab.batch.service.ReconciliationService#reconcile(java.util.Collection, java.util.Collection,
     *      java.util.Collection)
     */
    public void reconcile(Collection<Entry> glEntries, Collection<GeneralLedgerPendingEntry> pendingGlEntries, Collection<?> purapAcctEntries) {
        /**
         * FORMULA is amount value (GL_ENTRY_T + GL_PEND_ENTRY_T = AP_ACCT_LINE_HIST)
         */
        groupGLEntries(glEntries);
        groupPendingGLEntries(pendingGlEntries);
        groupPurapAccountEntries(purapAcctEntries);

        // identify the group matches
        reconcileGroups(glEntryGroupMap.keySet());

        if (!misMatchedGroups.isEmpty()) {
            // check for continuation account
            checkGroupByContinuationAccount();
            reconcileGroups(misMatchedGroups);
        }
    }

    /**
     * This method will run through all PO Accounting lines and Pending GL Lines for which a match was not found. Then check if
     * account number is expired and continuation account is available. If true then reassign the account group with this new
     * continuation account number.
     */
    protected void checkGroupByContinuationAccount() {
        for (PurApAccountLineGroup purapAcctLineGroup : purapAcctGroupMap.keySet()) {
            // if not matched
            if (!matchedGroups.contains(purapAcctLineGroup)) {
                Account account = findAccount(purapAcctLineGroup);
                // find the account and check expiration date and continuation
                String continuationAcctNum = null;
                if (account.isExpired() && (continuationAcctNum = account.getContinuationAccountNumber()) != null) {
                    purapAcctGroupMap.remove(purapAcctLineGroup);
                    purapAcctLineGroup.setAccountNumber(continuationAcctNum);
                    purapAcctGroupMap.put(purapAcctLineGroup, purapAcctLineGroup);
                }
            }
        }
        for (PendingGlAccountLineGroup pendingGlEntryGroup : pendingGlEntryGroupMap.keySet()) {
            // if not matched
            if (!matchedGroups.contains(pendingGlEntryGroup)) {
                Account account = findAccount(pendingGlEntryGroup);
                // find the account and check expiration date and continuation
                String continuationAcctNum = null;
                if (account.isExpired() && (continuationAcctNum = account.getContinuationAccountNumber()) != null) {
                    pendingGlEntryGroupMap.remove(pendingGlEntryGroup);
                    pendingGlEntryGroup.setAccountNumber(continuationAcctNum);
                    pendingGlEntryGroupMap.put(pendingGlEntryGroup, pendingGlEntryGroup);
                }
            }
        }
    }

    /**
     * Finds an account object using its primary key
     * 
     * @param acctLineGroup AcctLineGroup
     * @return Account
     */
    protected Account findAccount(AccountLineGroup acctLineGroup) {
        Map<String, String> keys = new HashMap<String, String>();
        keys.put("chartOfAccountsCode", acctLineGroup.getChartOfAccountsCode());
        keys.put("accountNumber", acctLineGroup.getAccountNumber());
        Account account = (Account) businessObjectService.findByPrimaryKey(Account.class, keys);
        return account;
    }

    /**
     * Identify and separate the matching groups and mismatched ones
     * 
     * @param glKeySet GL Account Line groups
     */
    protected void reconcileGroups(Set<GlAccountLineGroup> glKeySet) {
        for (GlAccountLineGroup glAccountLineGroup : glKeySet) {
            PendingGlAccountLineGroup pendingGlAccountLineGroup = this.pendingGlEntryGroupMap.get(glAccountLineGroup);
            PurApAccountLineGroup purapAccountLineGroup = purapAcctGroupMap.get(glAccountLineGroup);
            KualiDecimal pendingGlAmt = pendingGlAccountLineGroup != null ? pendingGlAccountLineGroup.getAmount() : KualiDecimal.ZERO;
            KualiDecimal glAmt = this.glEntryGroupMap.get(glAccountLineGroup).getAmount();
            KualiDecimal totalAmount = glAmt.add(pendingGlAmt);
            if (purapAccountLineGroup == null || !totalAmount.equals(purapAccountLineGroup.getAmount())) {
                misMatchedGroups.add(glAccountLineGroup);
            }
            else {
                glAccountLineGroup.setMatchedPurApAcctLines(purapAccountLineGroup.getSourceEntries());
                matchedGroups.add(glAccountLineGroup);
                misMatchedGroups.remove(glAccountLineGroup);
            }
        }
    }

    /**
     * Groups pending GL entries by fields by univ_fiscal_yr, fin_coa_cd, account_nbr, sub_acct_nbr, fin_object_cd, fin_sub_obj_cd,
     * univ_fiscal_prd_cd, fdoc_nbr, fdoc_ref_nbr
     * 
     * @param pendingGlEntries Pending GL Entries
     */
    protected void groupPendingGLEntries(Collection<GeneralLedgerPendingEntry> pendingGlEntries) {
        for (GeneralLedgerPendingEntry pendingGlEntry : pendingGlEntries) {
            if (pendingGlEntry.getTransactionLedgerEntryAmount() != null && !pendingGlEntry.getTransactionLedgerEntryAmount().isZero()) {
                // Step-2 Group by univ_fiscal_yr, fin_coa_cd, account_nbr, sub_acct_nbr, fin_object_cd, fin_sub_obj_cd,
                // univ_fiscal_prd_cd, fdoc_nbr, fdoc_ref_nbr
                PendingGlAccountLineGroup accountLineGroup = new PendingGlAccountLineGroup(pendingGlEntry);
                PendingGlAccountLineGroup targetAccountLineGroup = pendingGlEntryGroupMap.get(accountLineGroup);
                if (targetAccountLineGroup == null) {
                    pendingGlEntryGroupMap.put(accountLineGroup, accountLineGroup);
                }
                else {
                    // group GL entries
                    targetAccountLineGroup.combineEntry(pendingGlEntry);
                }
            }

        }
    }

    /**
     * Groups GL entries by fields by univ_fiscal_yr, fin_coa_cd, account_nbr, sub_acct_nbr, fin_object_cd, fin_sub_obj_cd,
     * univ_fiscal_prd_cd, fdoc_nbr, fdoc_ref_nbr
     * 
     * @param glEntries GL Entries
     */
    protected void groupGLEntries(Collection<Entry> glEntries) {
        for (Entry glEntry : glEntries) {
            // Step-1 Ignore zero or null amounts
            if (glEntry.getTransactionLedgerEntryAmount() == null || glEntry.getTransactionLedgerEntryAmount().isZero()) {
                this.ignoredEntries.add(glEntry);
            }
            else if (isDuplicateEntry(glEntry)) {
                // Ignore the duplicate entries
                this.duplicateEntries.add(glEntry);
            }
            else {
                // Step-2 Group by univ_fiscal_yr, fin_coa_cd, account_nbr, sub_acct_nbr, fin_object_cd, fin_sub_obj_cd,
                // univ_fiscal_prd_cd, fdoc_nbr, fdoc_ref_nbr
                GlAccountLineGroup accountLineGroup = new GlAccountLineGroup(glEntry);
                GlAccountLineGroup targetAccountLineGroup = glEntryGroupMap.get(accountLineGroup);
                if (targetAccountLineGroup == null) {
                    glEntryGroupMap.put(accountLineGroup, accountLineGroup);
                }
                else {
                    // group GL entries
                    targetAccountLineGroup.combineEntry(glEntry);
                }
            }

        }
    }

    /**
     * Groups Purap Account Line entries by fields by univ_fiscal_yr, fin_coa_cd, account_nbr, sub_acct_nbr, fin_object_cd,
     * fin_sub_obj_cd, univ_fiscal_prd_cd, fdoc_nbr, fdoc_ref_nbr
     * 
     * @param purapAcctEntries Purap account entries
     */
    protected void groupPurapAccountEntries(Collection<?> purapAcctEntries) {

    }

    /**
     * @see org.kuali.kfs.module.cab.batch.service.ReconciliationService#isDuplicateEntry(org.kuali.kfs.gl.businessobject.Entry)
     */
    public boolean isDuplicateEntry(Entry glEntry) {
        // find matching entry from CB_GL_ENTRY_T
        Map<String, Object> glKeys = new LinkedHashMap<String, Object>();
        glKeys.put(CabPropertyConstants.GeneralLedgerEntry.UNIVERSITY_FISCAL_YEAR, glEntry.getUniversityFiscalYear());
        glKeys.put(CabPropertyConstants.GeneralLedgerEntry.CHART_OF_ACCOUNTS_CODE, glEntry.getChartOfAccountsCode());
        glKeys.put(CabPropertyConstants.GeneralLedgerEntry.ACCOUNT_NUMBER, glEntry.getAccountNumber());
        glKeys.put(CabPropertyConstants.GeneralLedgerEntry.SUB_ACCOUNT_NUMBER, glEntry.getSubAccountNumber());
        glKeys.put(CabPropertyConstants.GeneralLedgerEntry.FINANCIAL_OBJECT_CODE, glEntry.getFinancialObjectCode());
        glKeys.put(CabPropertyConstants.GeneralLedgerEntry.FINANCIAL_SUB_OBJECT_CODE, glEntry.getFinancialSubObjectCode());
        glKeys.put(CabPropertyConstants.GeneralLedgerEntry.FINANCIAL_BALANCE_TYPE_CODE, glEntry.getFinancialBalanceTypeCode());
        glKeys.put(CabPropertyConstants.GeneralLedgerEntry.FINANCIAL_OBJECT_TYPE_CODE, glEntry.getFinancialObjectTypeCode());
        glKeys.put(CabPropertyConstants.GeneralLedgerEntry.UNIVERSITY_FISCAL_PERIOD_CODE, glEntry.getUniversityFiscalPeriodCode());
        glKeys.put(CabPropertyConstants.GeneralLedgerEntry.FINANCIAL_DOCUMENT_TYPE_CODE, glEntry.getFinancialDocumentTypeCode());
        glKeys.put(CabPropertyConstants.GeneralLedgerEntry.FINANCIAL_SYSTEM_ORIGINATION_CODE, glEntry.getFinancialSystemOriginationCode());
        glKeys.put(CabPropertyConstants.GeneralLedgerEntry.DOCUMENT_NUMBER, glEntry.getDocumentNumber());
        glKeys.put(CabPropertyConstants.GeneralLedgerEntry.TRANSACTION_LEDGER_ENTRY_SEQUENCE_NUMBER, glEntry.getTransactionLedgerEntrySequenceNumber());
        Collection<GeneralLedgerEntry> matchingEntries = businessObjectService.findMatching(GeneralLedgerEntry.class, glKeys);
        // if not found, return false
        if (matchingEntries == null || matchingEntries.size() == 0) {
            return false;
        }
        return true;
    }

    /**
     * Gets the businessObjectService attribute.
     * 
     * @return Returns the businessObjectService
     */

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute.
     * 
     * @param businessObjectService The businessObjectService to set.
     */

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the ignoredEntries attribute.
     * 
     * @return Returns the ignoredEntries
     */

    public List<Entry> getIgnoredEntries() {
        return ignoredEntries;
    }

    /**
     * Sets the ignoredEntries attribute.
     * 
     * @param ignoredEntries The ignoredEntries to set.
     */

    public void setIgnoredEntries(List<Entry> ignoredEntries) {
        this.ignoredEntries = ignoredEntries;
    }

    /**
     * Gets the duplicateEntries attribute.
     * 
     * @return Returns the duplicateEntries
     */

    public List<Entry> getDuplicateEntries() {
        return duplicateEntries;
    }

    /**
     * Sets the duplicateEntries attribute.
     * 
     * @param duplicateEntries The duplicateEntries to set.
     */

    public void setDuplicateEntries(List<Entry> duplicateEntries) {
        this.duplicateEntries = duplicateEntries;
    }

    /**
     * Gets the matchedGroups attribute.
     * 
     * @return Returns the matchedGroups
     */

    public Set<GlAccountLineGroup> getMatchedGroups() {
        return matchedGroups;
    }

    /**
     * Sets the matchedGroups attribute.
     * 
     * @param matchedGroups The matchedGroups to set.
     */

    public void setMatchedGroups(Set<GlAccountLineGroup> matchedGroups) {
        this.matchedGroups = matchedGroups;
    }

    /**
     * Gets the misMatchedGroups attribute.
     * 
     * @return Returns the misMatchedGroups
     */

    public Set<GlAccountLineGroup> getMisMatchedGroups() {
        return misMatchedGroups;
    }

    /**
     * Sets the misMatchedGroups attribute.
     * 
     * @param misMatchedGroups The misMatchedGroups to set.
     */

    public void setMisMatchedGroups(Set<GlAccountLineGroup> misMatchedGroups) {
        this.misMatchedGroups = misMatchedGroups;
    }


}

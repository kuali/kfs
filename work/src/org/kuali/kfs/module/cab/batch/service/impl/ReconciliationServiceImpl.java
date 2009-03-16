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

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.batch.service.ReconciliationService;
import org.kuali.kfs.module.cab.businessobject.AccountLineGroup;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.businessobject.GlAccountLineGroup;
import org.kuali.kfs.module.cab.businessobject.PurApAccountLineGroup;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLineBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link ReconciliationService}
 */
@Transactional
public class ReconciliationServiceImpl implements ReconciliationService {
    private static final Logger LOG = Logger.getLogger(ReconciliationServiceImpl.class);
    protected BusinessObjectService businessObjectService;
    protected List<Entry> ignoredEntries = new ArrayList<Entry>();
    protected List<Entry> duplicateEntries = new ArrayList<Entry>();
    protected Collection<GlAccountLineGroup> matchedGroups = new HashSet<GlAccountLineGroup>();
    protected Collection<GlAccountLineGroup> misMatchedGroups = new HashSet<GlAccountLineGroup>();
    protected HashMap<GlAccountLineGroup, GlAccountLineGroup> glEntryGroupMap = new HashMap<GlAccountLineGroup, GlAccountLineGroup>();
    protected HashMap<PurApAccountLineGroup, PurApAccountLineGroup> purapAcctGroupMap = new HashMap<PurApAccountLineGroup, PurApAccountLineGroup>();

    /**
     * @see org.kuali.kfs.module.cab.batch.service.ReconciliationService#reconcile(java.util.Collection, java.util.Collection,
     *      java.util.Collection)
     */
    public void reconcile(Collection<Entry> glEntries, Collection<PurApAccountingLineBase> purapAcctEntries) {
        /**
         * FORMULA is to equate amount value (GL_ENTRY_T + GL_PEND_ENTRY_T = AP_ACCT_LINE_HIST)
         */
        LOG.info("Reconcile started");
        groupGLEntries(glEntries);
        groupPurapAccountEntries(purapAcctEntries);
        reconcileGroups(glEntryGroupMap.values());

        // check for continuation account numbers
        if (!misMatchedGroups.isEmpty()) {
            LOG.info("Checking for continuation account");
            checkGroupByContinuationAccount();
            reconcileGroups(misMatchedGroups);
        }
        LOG.info("Reconcile finished");
    }

    /**
     * This method will run through all PO Accounting lines and Pending GL Lines for which a match was not found. Then check if
     * account number is expired and continuation account is available. If true then reassign the account group with this new
     * continuation account number.
     */
    protected void checkGroupByContinuationAccount() {
        // get the keys first to avoid concurrent modification issues
        List<PurApAccountLineGroup> purapGroups = new ArrayList<PurApAccountLineGroup>();
        purapGroups.addAll(purapAcctGroupMap.keySet());

        for (PurApAccountLineGroup purapAcctLineGroup : purapGroups) {
            // if not matched already, check and replace with continuation account
            if (!matchedGroups.contains(purapAcctLineGroup)) {
                Account account = findAccount(purapAcctLineGroup);
                // find the account and check expiration date and continuation
                String continuationAcctNum = null;
                if (account.isExpired() && (continuationAcctNum = account.getContinuationAccountNumber()) != null) {
                    LOG.debug("Continutation account found for " + account.getAccountNumber() + " is " + account.getContinuationAccountNumber());
                    purapAcctGroupMap.remove(purapAcctLineGroup);
                    purapAcctLineGroup.setAccountNumber(continuationAcctNum);
                    purapAcctGroupMap.put(purapAcctLineGroup, purapAcctLineGroup);
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
        keys.put(CabPropertyConstants.Account.CHART_OF_ACCOUNTS_CODE, acctLineGroup.getChartOfAccountsCode());
        keys.put(CabPropertyConstants.Account.ACCOUNT_NUMBER, acctLineGroup.getAccountNumber());
        Account account = (Account) businessObjectService.findByPrimaryKey(Account.class, keys);
        return account;
    }

    /**
     * Identify and separate the matching and mismatching account line groups
     * 
     * @param glKeySet GL Account Line groups
     */
    protected void reconcileGroups(Collection<GlAccountLineGroup> glKeySet) {
        for (GlAccountLineGroup glAccountLineGroup : glKeySet) {
            PurApAccountLineGroup purapAccountLineGroup = this.purapAcctGroupMap.get(glAccountLineGroup);
            KualiDecimal glAmt = this.glEntryGroupMap.get(glAccountLineGroup).getAmount();

            if (purapAccountLineGroup == null || !glAmt.equals(purapAccountLineGroup.getAmount())) {
                LOG.debug("GL account line " + glAccountLineGroup.toString() + " did not find a matching purchasing account line group");
                misMatchedGroups.add(glAccountLineGroup);
            }
            else if (!KualiDecimal.ZERO.equals(glAmt)) {
                LOG.debug("GL account line " + glAccountLineGroup.toString() + " found a matching Purchasing account line group ");
                glAccountLineGroup.setMatchedPurApAcctLines(purapAccountLineGroup.getSourceEntries());
                matchedGroups.add(glAccountLineGroup);
                misMatchedGroups.remove(glAccountLineGroup);
            }
            else if (KualiDecimal.ZERO.equals(glAmt)) {
                // if combined value is zero then ignore the entries
                List<Entry> sourceEntries = glAccountLineGroup.getSourceEntries();
                for (Entry entry : sourceEntries) {
                    this.ignoredEntries.add(entry);
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
    protected void groupPurapAccountEntries(Collection<PurApAccountingLineBase> purapAcctEntries) {
        for (PurApAccountingLineBase entry : purapAcctEntries) {
            if (entry.getAmount() != null && !entry.getAmount().isZero()) {
                // Step-2 Group by univ_fiscal_yr, fin_coa_cd, account_nbr, sub_acct_nbr, fin_object_cd, fin_sub_obj_cd,
                // univ_fiscal_prd_cd, fdoc_nbr, fdoc_ref_nbr
                PurApAccountLineGroup accountLineGroup = new PurApAccountLineGroup(entry);
                PurApAccountLineGroup targetAccountLineGroup = purapAcctGroupMap.get(accountLineGroup);
                if (targetAccountLineGroup == null) {
                    purapAcctGroupMap.put(accountLineGroup, accountLineGroup);
                }
                else {
                    // group GL entries
                    targetAccountLineGroup.combineEntry(entry);
                }
            }
        }
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
        if (matchingEntries == null || matchingEntries.isEmpty()) {
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

    public Collection<GlAccountLineGroup> getMatchedGroups() {
        return this.matchedGroups;
    }

    public Collection<GlAccountLineGroup> getMisMatchedGroups() {
        return this.misMatchedGroups;
    }


}

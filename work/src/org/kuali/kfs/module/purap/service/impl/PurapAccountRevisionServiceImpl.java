/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.purap.businessobject.AccountsPayableItemBase;
import org.kuali.kfs.module.purap.businessobject.CreditMemoAccountRevision;
import org.kuali.kfs.module.purap.businessobject.CreditMemoItem;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestAccount;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestAccountRevision;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLineBase;
import org.kuali.kfs.module.purap.service.PurapAccountRevisionService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.BusinessObjectService;

public class PurapAccountRevisionServiceImpl implements PurapAccountRevisionService {
    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;

    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountHistoryService#savePaymentRequestAccountHistories(java.util.List,
     *      java.lang.Integer, java.lang.String)
     */
    public void savePaymentRequestAccountRevisions(List<PaymentRequestItem> paymentRequestItems, Integer postingYear, String postingPeriodCode) {
        List<PaymentRequestAccountRevision> accountHistories = new ArrayList<PaymentRequestAccountRevision>();
        for (PaymentRequestItem item : paymentRequestItems) {
            Map<PurapAccountRevisionGroup, PurapAccountRevisionGroup> currentAcctLineGroups = buildAccountLineGroups(item, postingYear, postingPeriodCode);
            Map<PurapAccountRevisionGroup, PurapAccountRevisionGroup> historyAcctLineGroups = buildAccountHistoryGroups(item, postingYear, postingPeriodCode, PaymentRequestAccountRevision.class);
            HashSet<PurapAccountRevisionGroup> existList = new HashSet<PurapAccountRevisionGroup>();
            // handle existing account line changes
            for (PurapAccountRevisionGroup histGroup : historyAcctLineGroups.keySet()) {
                PurapAccountRevisionGroup currGroup = currentAcctLineGroups.get(histGroup);
                if (currGroup != null) {
                    // adjust the amount value
                    histGroup.setChangeAmount(currGroup.getAmount().subtract(histGroup.getAmount()));
                }
                else {
                    // negate the amount if acct line is deleted
                    histGroup.setChangeAmount(histGroup.getAmount().negated());
                }
                // build history record and save
                PaymentRequestAccountRevision history = (PaymentRequestAccountRevision) histGroup.buildRevisionRecord(PaymentRequestAccountRevision.class);
                history.setAccountRevisionTimestamp(dateTimeService.getCurrentTimestamp());
                accountHistories.add(history);
                existList.add(histGroup);
            }
            // handle new accounting lines
            for (PurapAccountRevisionGroup group : currentAcctLineGroups.keySet()) {
                if (!existList.contains(group)) {
                    // set change amount same as new amount
                    group.setChangeAmount(group.getAmount());
                    PaymentRequestAccountRevision history = (PaymentRequestAccountRevision) group.buildRevisionRecord(PaymentRequestAccountRevision.class);
                    history.setAccountRevisionTimestamp(dateTimeService.getCurrentTimestamp());
                    accountHistories.add(history);
                }
            }
        }
        businessObjectService.save(accountHistories);
    }

    public void cancelPaymentRequestAccountRevisions(List<PaymentRequestItem> paymentRequestItems, Integer postingYear, String postingPeriodCode) {
        List<PaymentRequestAccountRevision> accountHistories = new ArrayList<PaymentRequestAccountRevision>();
        for (PaymentRequestItem item : paymentRequestItems) {
            Map<PurapAccountRevisionGroup, PurapAccountRevisionGroup> historyAcctLineGroups = buildAccountHistoryGroups(item, postingYear, postingPeriodCode, PaymentRequestAccountRevision.class);
            // handle existing account line changes
            for (PurapAccountRevisionGroup histGroup : historyAcctLineGroups.keySet()) {
                // negate the amount
                histGroup.setChangeAmount(histGroup.getAmount().negated());
                // build history record and save
                PaymentRequestAccountRevision history = (PaymentRequestAccountRevision) histGroup.buildRevisionRecord(PaymentRequestAccountRevision.class);
                history.setAccountRevisionTimestamp(dateTimeService.getCurrentTimestamp());
                accountHistories.add(history);
            }
        }
        businessObjectService.save(accountHistories);
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PurapAccountHistoryService#saveCreditMemoAccountHistories(java.util.List,
     *      java.lang.Integer, java.lang.String)
     */
    public void saveCreditMemoAccountRevisions(List<CreditMemoItem> creditMemoItems, Integer postingYear, String postingPeriodCode) {
        List<CreditMemoAccountRevision> accountHistories = new ArrayList<CreditMemoAccountRevision>();
        for (CreditMemoItem item : creditMemoItems) {
            Map<PurapAccountRevisionGroup, PurapAccountRevisionGroup> currentAcctLineGroups = buildAccountLineGroups(item, postingYear, postingPeriodCode);
            Map<PurapAccountRevisionGroup, PurapAccountRevisionGroup> historyAcctLineGroups = buildAccountHistoryGroups(item, postingYear, postingPeriodCode, CreditMemoAccountRevision.class);
            HashSet<PurapAccountRevisionGroup> existList = new HashSet<PurapAccountRevisionGroup>();
            // first handle existing account line changes
            for (PurapAccountRevisionGroup histGroup : historyAcctLineGroups.keySet()) {
                PurapAccountRevisionGroup currGroup = currentAcctLineGroups.get(histGroup);
                if (currGroup != null) {
                    // adjust the amount
                    histGroup.setChangeAmount(currGroup.getAmount().subtract(histGroup.getAmount()));
                }
                else {
                    // negate the amount if line is deleted
                    histGroup.setChangeAmount(histGroup.getAmount().negated());
                }
                // build history record and save
                CreditMemoAccountRevision history = (CreditMemoAccountRevision) histGroup.buildRevisionRecord(CreditMemoAccountRevision.class);
                history.setAccountRevisionTimestamp(dateTimeService.getCurrentTimestamp());
                accountHistories.add(history);
                existList.add(histGroup);
            }
            // handle new account lines added
            for (PurapAccountRevisionGroup group : currentAcctLineGroups.keySet()) {
                if (!existList.contains(group)) {
                    // set same change amount same as new amount
                    group.setChangeAmount(group.getAmount());
                    CreditMemoAccountRevision history = (CreditMemoAccountRevision) group.buildRevisionRecord(CreditMemoAccountRevision.class);
                    history.setAccountRevisionTimestamp(dateTimeService.getCurrentTimestamp());
                    accountHistories.add(history);
                }
            }
        }
        businessObjectService.save(accountHistories);
    }

    public void cancelCreditMemoAccountRevisions(List<CreditMemoItem> creditMemoItems, Integer postingYear, String postingPeriodCode) {
        List<CreditMemoAccountRevision> accountHistories = new ArrayList<CreditMemoAccountRevision>();
        for (CreditMemoItem item : creditMemoItems) {
            Map<PurapAccountRevisionGroup, PurapAccountRevisionGroup> historyAcctLineGroups = buildAccountHistoryGroups(item, postingYear, postingPeriodCode, CreditMemoAccountRevision.class);
            // first handle existing account line changes
            for (PurapAccountRevisionGroup histGroup : historyAcctLineGroups.keySet()) {
                // negate the amount
                histGroup.setChangeAmount(histGroup.getAmount().negated());
                // build history record and save
                CreditMemoAccountRevision history = (CreditMemoAccountRevision) histGroup.buildRevisionRecord(CreditMemoAccountRevision.class);
                history.setAccountRevisionTimestamp(dateTimeService.getCurrentTimestamp());
                accountHistories.add(history);
            }
        }
        businessObjectService.save(accountHistories);
    }

    /**
     * Builds account history grouping data based on given list of purap account lines
     * 
     * @param item PurAp Item
     * @param postingYear Posting year
     * @param postingPeriodCode Posting period
     * @return
     */
    protected Map<PurapAccountRevisionGroup, PurapAccountRevisionGroup> buildAccountLineGroups(AccountsPayableItemBase item, Integer postingYear, String postingPeriodCode) {
        Map<PurapAccountRevisionGroup, PurapAccountRevisionGroup> accountLineGroups = new HashMap<PurapAccountRevisionGroup, PurapAccountRevisionGroup>();
        for (PurApAccountingLine account : item.getSourceAccountingLines()) {
            PurapAccountRevisionGroup lineGroup = new PurapAccountRevisionGroup((PurApAccountingLineBase) account);
            lineGroup.setPostingYear(postingYear);
            lineGroup.setPostingPeriodCode(postingPeriodCode);
            if ((accountLineGroups.get(lineGroup)) == null) {
                accountLineGroups.put(lineGroup, lineGroup);
            }
            else {
                accountLineGroups.get(lineGroup).combineEntry((PurApAccountingLineBase) account);
            }
        }
        return accountLineGroups;
    }

    /**
     * Builds account history group based on existing account history lines
     * 
     * @param item PurAp item
     * @param postingYear Posting year
     * @param postingPeriodCode Posting period code
     * @param clazz History class
     * @return Map of account history groups
     */
    protected Map<PurapAccountRevisionGroup, PurapAccountRevisionGroup> buildAccountHistoryGroups(AccountsPayableItemBase item, Integer postingYear, String postingPeriodCode, Class<? extends PurApAccountingLineBase> clazz) {
        Map<PurapAccountRevisionGroup, PurapAccountRevisionGroup> historyGroups = new HashMap<PurapAccountRevisionGroup, PurapAccountRevisionGroup>();
        // find the current sum value from history table and adjusts the amount
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put("itemIdentifier", item.getItemIdentifier());
        Collection<PurApAccountingLineBase> existingAccounts = (Collection<PurApAccountingLineBase>) businessObjectService.findMatching(clazz, fieldValues);
        if (existingAccounts != null && !existingAccounts.isEmpty()) {
            for (PurApAccountingLineBase existAcct : existingAccounts) {
                PurapAccountRevisionGroup historyGroup = new PurapAccountRevisionGroup(existAcct);
                historyGroup.setPostingYear(postingYear);
                historyGroup.setPostingPeriodCode(postingPeriodCode);
                if ((historyGroups.get(historyGroup)) == null) {
                    historyGroups.put(historyGroup, historyGroup);
                }
                else {
                    historyGroups.get(historyGroup).combineEntry((PaymentRequestAccount) existAcct);
                }
            }
        }
        return historyGroups;
    }

    /**
     * Gets the businessObjectService attribute.
     * 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the dateTimeService attribute.
     * 
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

}

/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.service.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.Constant;
import org.kuali.kfs.module.ld.businessobject.AccountStatusCurrentFunds;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.module.ld.businessobject.LedgerEntry;
import org.kuali.kfs.module.ld.service.LaborInquiryOptionsService;
import org.kuali.kfs.module.ld.service.LaborLedgerBalanceService;
import org.kuali.kfs.module.ld.service.LaborLedgerPendingEntryService;
import org.kuali.kfs.sys.KFSConstants.ParameterValues;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;

/**
 * The LaborInquiryOptionsService class is a service that will generate Pending Ledger and Consilidation options for balance
 * inquiries.
 */
public class LaborInquiryOptionsServiceImpl implements LaborInquiryOptionsService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborInquiryOptionsServiceImpl.class);

    private LaborLedgerPendingEntryService laborLedgerPendingEntryService;
    private LaborLedgerBalanceService laborLedgerBalanceService;

    /**
     * @see org.kuali.kfs.module.ld.service.LaborInquiryOptionsService#getConsolidationFieldName()
     */
    @Override
    public String getConsolidationFieldName() {
        return Constant.CONSOLIDATION_OPTION;
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborInquiryOptionsService#getConsolidationField(java.util.Collection)
     */
    @Override
    public Field getConsolidationField(Collection<Row> rows) {
        for (Row row : rows) {
            for (Field field : (row.getFields())) {
                if (field.getPropertyName().equals(getConsolidationFieldName())) {
                    return field;
                }
            }
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborInquiryOptionsService#getSelectedPendingEntryOption(java.util.Map)
     */
    @Override
    public String getSelectedPendingEntryOption(Map fieldValues) {
        // truncate the non-property filed
        String pendingEntryOption = (String) fieldValues.get(Constant.PENDING_ENTRY_OPTION);
        fieldValues.remove(Constant.PENDING_ENTRY_OPTION);

        return pendingEntryOption;
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborInquiryOptionsService#isCgBeginningBalanceOnlyExcluded(java.util.Map)
     */
    @Override
    public boolean isCgBeginningBalanceOnlyExcluded(Map fieldValues) {
        // truncate the non-property filed
        String cgBeginningBalanceExcludeOption = (String) fieldValues.get(Constant.EXCLUDE_CG_BEGINNING_BALANCE_ONLY_OPTION);
        fieldValues.remove(Constant.EXCLUDE_CG_BEGINNING_BALANCE_ONLY_OPTION);

        return ParameterValues.YES.equalsIgnoreCase(cgBeginningBalanceExcludeOption);
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborInquiryOptionsService#getConsolidationOption(java.util.Map)
     */
    @Override
    public String getConsolidationOption(Map fieldValues) {
        String consolidationOption = (String) fieldValues.get(getConsolidationFieldName());
        // truncate the non-property filed
        fieldValues.remove(getConsolidationFieldName());
        return consolidationOption;
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborInquiryOptionsService#isConsolidationSelected(java.util.Map, java.util.Collection)
     *
     * KRAD Conversion: Lookupable performs checking for a particular attribute and return true or false.
     * This method is called from BaseFundsLookupableHelperServiceImpl.java, CurrentFundsLookupableHelperServiceImpl.java,
     * LedgerBalanceLookupableHelperServiceImpl.java in ld module
     */
    @Override
    public boolean isConsolidationSelected(Map fieldValues, Collection<Row> rows) {
        boolean isConsolidationSelected = isConsolidationSelected(fieldValues);

        if (!isConsolidationSelected) {
            Field consolidationField = getConsolidationField(rows);
            consolidationField.setPropertyValue(Constant.DETAIL);
        }
        return isConsolidationSelected;
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborInquiryOptionsService#isConsolidationSelected(java.util.Map)
     */
    @Override
    public boolean isConsolidationSelected(Map fieldValues) {
        String consolidationOption = getConsolidationOption(fieldValues);

        // detail option would be used
        if (Constant.DETAIL.equals(consolidationOption)) {
            return false;
        }

        // if the subAccountNumber is specified, detail option could be used
        // if the subObjectCode is specified, detail option could be used
        // if the objectTypeCode is specified, detail option could be used
        if (isDetailDefaultFieldUsed(fieldValues, KFSPropertyConstants.SUB_ACCOUNT_NUMBER) || isDetailDefaultFieldUsed(fieldValues, KFSPropertyConstants.SUB_OBJECT_CODE) || isDetailDefaultFieldUsed(fieldValues, KFSPropertyConstants.OBJECT_TYPE_CODE)) {
            return false;
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborInquiryOptionsService#updateLedgerBalanceByPendingLedgerEntry(java.util.Collection,
     *      java.util.Map, java.lang.String, boolean)
     */
    @Override
    public void updateLedgerBalanceByPendingLedgerEntry(Collection<LedgerBalance> balanceCollection, Map fieldValues, String pendingEntryOption, boolean isConsolidated) {
        // determine if search results need to be updated by pending ledger entries
        if (Constant.ALL_PENDING_ENTRY.equals(pendingEntryOption)) {
            updateCollection(balanceCollection, fieldValues, false, isConsolidated, LedgerBalance.class);
        }
        else if (Constant.APPROVED_PENDING_ENTRY.equals(pendingEntryOption)) {
            updateCollection(balanceCollection, fieldValues, true, isConsolidated, LedgerBalance.class);
        }
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborInquiryOptionsService#updateCurrentFundsByPendingLedgerEntry(java.util.Collection,
     *      java.util.Map, java.lang.String, boolean)
     */
    @Override
    public void updateCurrentFundsByPendingLedgerEntry(Collection<AccountStatusCurrentFunds> balanceCollection, Map fieldValues, String pendingEntryOption, boolean isConsolidated) {
        // determine if search results need to be updated by pending ledger entries
        if (Constant.ALL_PENDING_ENTRY.equals(pendingEntryOption)) {
            updateCollection(balanceCollection, fieldValues, false, isConsolidated, AccountStatusCurrentFunds.class);
        }
        else if (Constant.APPROVED_PENDING_ENTRY.equals(pendingEntryOption)) {
            updateCollection(balanceCollection, fieldValues, true, isConsolidated, AccountStatusCurrentFunds.class);
        }
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborInquiryOptionsService#updateByPendingLedgerEntry(java.util.Collection,
     *      java.util.Map, java.lang.String, boolean)
     */
    @Override
    public void updateLedgerEntryByPendingLedgerEntry(Collection<LedgerEntry> entryCollection, Map fieldValues, String pendingEntryOption) {
        // determine if search results need to be updated by pending ledger entries
        if (Constant.ALL_PENDING_ENTRY.equals(pendingEntryOption)) {
            updateCollection(entryCollection, fieldValues, false, false, LedgerEntry.class);
        }
        else if (Constant.APPROVED_PENDING_ENTRY.equals(pendingEntryOption)) {
            updateCollection(entryCollection, fieldValues, true, false, LedgerEntry.class);
        }
    }

    /**
     * update a given collection entry with the pending entry obtained from the given field values and isApproved
     *
     * @param entryCollection the given entry collection
     * @param fieldValues the given field values
     * @param isApproved indicate if the resulting pending entry has been approved
     * @param isConsolidated indicate if the collection entries have been consolidated
     */
    protected void updateCollection(Collection entryCollection, Map fieldValues, boolean isApproved, boolean isConsolidated, Class clazz) {
        // go through the pending entries to update the balance collection
        Iterator<LaborLedgerPendingEntry> pendingEntryIterator = laborLedgerPendingEntryService.findPendingLedgerEntriesForLedgerBalance(fieldValues, isApproved);

        while (pendingEntryIterator.hasNext()) {
            LaborLedgerPendingEntry pendingEntry = pendingEntryIterator.next();

            // if consolidated, change the following fields into the default values for consolidation
            if (isConsolidated) {
                pendingEntry.setSubAccountNumber(Constant.CONSOLIDATED_SUB_ACCOUNT_NUMBER);
                pendingEntry.setFinancialSubObjectCode(Constant.CONSOLIDATED_SUB_OBJECT_CODE);
                pendingEntry.setFinancialObjectTypeCode(Constant.CONSOLIDATED_OBJECT_TYPE_CODE);
            }

            if (LedgerBalance.class.isAssignableFrom(clazz)) {
                try {
                    LedgerBalance ledgerBalance = laborLedgerBalanceService.findLedgerBalance(entryCollection, pendingEntry);
                    if (ledgerBalance == null) {

                        Object newLedgerBalance = clazz.newInstance();
                        ObjectUtil.buildObject(newLedgerBalance, pendingEntry);

                        ledgerBalance = (LedgerBalance) newLedgerBalance;
                        entryCollection.add(ledgerBalance);
                    }
                    laborLedgerBalanceService.updateLedgerBalance(ledgerBalance, pendingEntry);
                    ledgerBalance.getDummyBusinessObject().setConsolidationOption(isConsolidated ? Constant.CONSOLIDATION : Constant.DETAIL);
                    ledgerBalance.getDummyBusinessObject().setPendingEntryOption(isApproved ? Constant.APPROVED_PENDING_ENTRY : Constant.ALL_PENDING_ENTRY);
                }
                catch (Exception e) {
                    LOG.error("cannot create a new object of type: " + clazz.getName() + "/n" + e);
                }
            }
            else if (LedgerEntry.class.isAssignableFrom(clazz)) {
                LedgerEntry ledgerEntry = new LedgerEntry();
                ObjectUtil.buildObject(ledgerEntry, pendingEntry);

                ledgerEntry.getDummyBusinessObject().setConsolidationOption(isConsolidated ? Constant.CONSOLIDATION : Constant.DETAIL);
                ledgerEntry.getDummyBusinessObject().setPendingEntryOption(isApproved ? Constant.APPROVED_PENDING_ENTRY : Constant.ALL_PENDING_ENTRY);

                entryCollection.add(ledgerEntry);
            }
            else {
                LOG.warn("The class, " + clazz.getName() + ", is unregistered with the method.");
                return;
            }
        }
    }

    /**
     * Determines if any of the fields that require a detail view are used
     *
     * @param fieldValues
     * @param fieldName
     */
    protected boolean isDetailDefaultFieldUsed(Map fieldValues, String fieldName) {
        return StringUtils.isNotBlank((String) fieldValues.get(fieldName));
    }

    /**
     * Sets the laborLedgerBalanceService attribute value.
     *
     * @param laborLedgerBalanceService The laborLedgerBalanceService to set.
     */
    public void setLaborLedgerBalanceService(LaborLedgerBalanceService laborLedgerBalanceService) {
        this.laborLedgerBalanceService = laborLedgerBalanceService;
    }

    /**
     * Sets the laborLedgerPendingEntryService attribute value.
     *
     * @param laborLedgerPendingEntryService The laborLedgerPendingEntryService to set.
     */
    public void setLaborLedgerPendingEntryService(LaborLedgerPendingEntryService laborLedgerPendingEntryService) {
        this.laborLedgerPendingEntryService = laborLedgerPendingEntryService;
    }
}

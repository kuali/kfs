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
package org.kuali.module.labor.service.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.web.ui.Field;
import org.kuali.core.web.ui.Row;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.gl.web.Constant;
import org.kuali.module.labor.bo.LaborLedgerPendingEntry;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.service.LaborInquiryOptionsService;
import org.kuali.module.labor.service.LaborLedgerBalanceService;
import org.kuali.module.labor.service.LaborLedgerPendingEntryService;
import org.springframework.transaction.annotation.Transactional;

/**
 * The LaborInquiryOptionsService class is a service that will generate Pending Ledger and Consilidation options for balance
 * inquiries.
 */
@Transactional
public class LaborInquiryOptionsServiceImpl implements LaborInquiryOptionsService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborInquiryOptionsServiceImpl.class);
    private LaborLedgerPendingEntryService laborLedgerPendingEntryService;
    private LaborLedgerBalanceService laborLedgerBalanceService;

    /**
     * @see org.kuali.module.labor.service.LaborInquiryOptionsService#getConsolidationFieldName()
     */
    public String getConsolidationFieldName() {
        return Constant.CONSOLIDATION_OPTION;
    }

    /**
     * @see org.kuali.module.labor.service.LaborInquiryOptionsService#getConsolidationField(java.util.Collection)
     */
    public Field getConsolidationField(Collection<Row> rows) {
        for (Row row : rows) {
            for (Field field : ((Collection<Field>) row.getFields())) {
                if (field.getPropertyName().equals(getConsolidationFieldName())) {
                    return field;
                }
            }
        }
        return null;
    }

    /**
     * @see org.kuali.module.labor.service.LaborInquiryOptionsService#getConsolidationOption(java.util.Map)
     */
    public String getConsolidationOption(Map fieldValues) {
        String consolidationOption = (String) fieldValues.get(getConsolidationFieldName());
        // truncate the non-property filed
        fieldValues.remove(getConsolidationFieldName());
        return consolidationOption;
    }

    /**
     * @see org.kuali.module.labor.service.LaborInquiryOptionsService#isConsolidationSelected(java.util.Map, java.util.Collection)
     */
    public boolean isConsolidationSelected(Map fieldValues, Collection<Row> rows) {
        String consolidationOption = getConsolidationOption(fieldValues);
        Field consolidationField = getConsolidationField(rows);

        // detail option would be used
        if (Constant.DETAIL.equals(consolidationOption)) {
            return false;
        }

        // if the subAccountNumber is specified, detail option could be used
        // if the subObjectCode is specified, detail option could be used
        // if the objectTypeCode is specified, detail option could be used
        if (isDetailDefaultFieldUsed(fieldValues, KFSPropertyConstants.SUB_ACCOUNT_NUMBER) || isDetailDefaultFieldUsed(fieldValues, KFSPropertyConstants.SUB_OBJECT_CODE) || isDetailDefaultFieldUsed(fieldValues, KFSPropertyConstants.OBJECT_TYPE_CODE)) {
            consolidationField.setPropertyValue(Constant.DETAIL);
            return false;
        }
        return true;
    }

    /**
     * Determines if any of the fields that require a detail view are used
     * 
     * @param fieldValues
     * @param fieldName
     */
    private boolean isDetailDefaultFieldUsed(Map fieldValues, String fieldName) {
        return !(StringUtils.isBlank((String) fieldValues.get(fieldName)));
    }

    /**
     * @see org.kuali.module.labor.service.LaborInquiryOptionsService#updateByPendingLedgerEntry(java.util.Collection,
     *      java.util.Map, java.lang.String, boolean)
     */
    public void updateByPendingLedgerEntry(Collection entryCollection, Map fieldValues, String pendingEntryOption, boolean isConsolidated) {

        // determine if search results need to be updated by pending ledger entries
        if (Constant.ALL_PENDING_ENTRY.equals(pendingEntryOption)) {
            updateEntryCollection(entryCollection, fieldValues, false, isConsolidated);
        }
        else if (Constant.APPROVED_PENDING_ENTRY.equals(pendingEntryOption)) {
            updateEntryCollection(entryCollection, fieldValues, true, isConsolidated);
        }
    }

    /**
     * @see org.kuali.module.labor.service.LaborInquiryOptionsService#getSelectedPendingEntryOption(java.util.Map)
     */
    public String getSelectedPendingEntryOption(Map fieldValues) {
        // truncate the non-property filed
        String pendingEntryOption = (String) fieldValues.get(Constant.PENDING_ENTRY_OPTION);
        fieldValues.remove(Constant.PENDING_ENTRY_OPTION);

        return pendingEntryOption;
    }

    /**
     * @see org.kuali.module.labor.service.LaborInquiryOptionsService#updateEntryCollection(java.util.Collection, java.util.Map,
     *      boolean, boolean)
     */
    public void updateEntryCollection(Collection entryCollection, Map fieldValues, boolean isApproved, boolean isConsolidated) {
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

            LedgerBalance ledgerBalance = laborLedgerBalanceService.findLedgerBalance(entryCollection, pendingEntry);
            if (ledgerBalance == null) {
                laborLedgerBalanceService.addLedgerBalance(entryCollection, pendingEntry);
            }
            else {
                laborLedgerBalanceService.updateLedgerBalance(ledgerBalance, pendingEntry);
            }
        }
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

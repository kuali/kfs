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
package org.kuali.kfs.module.ld.service;

import java.util.Collection;
import java.util.Map;

import org.kuali.kfs.module.ld.businessobject.AccountStatusCurrentFunds;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.module.ld.businessobject.LedgerEntry;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;

/**
 * The LaborInquiryOptionsService interface provides hooks for Pending Ledger and Consilidation options for balance inquiries.
 */
public interface LaborInquiryOptionsService {

    /**
     * The expected name of the consolidation option field name
     *
     * @return String
     */
    public String getConsolidationFieldName();

    /**
     * Examine a collection of <code>{@link Row}</code> instances for the consolidation field
     *
     * @param rows
     * @return Field
     */
    public Field getConsolidationField(Collection<Row> rows);

    /**
     * Get the current state of the consolidation option
     *
     * @return String
     */
    public String getConsolidationOption(Map fieldValues);

    /**
     * This method tests if the user selects to see the details or consolidated results
     *
     * @param fieldValues the map containing the search fields and values
     * @param rows
     * @return true if consolidation is selected and subaccount is not specified
     */
    public boolean isConsolidationSelected(Map fieldValues, Collection<Row> rows);

    /**
     * This method tests if the user selects to see the details or consolidated results
     *
     * @param fieldValues the map containing the search fields and values
     * @return true if consolidation is selected and subaccount is not specified
     */
    public boolean isConsolidationSelected(Map fieldValues);

    /**
     * update a given balance collection with the pending entry obtained from the given field values and pending entry option
     *
     * @param balanceCollection the given ledger balance collection
     * @param fieldValues the given field values
     * @param pendingEntryOption the given pending entry option: all, approved or none
     * @param isConsolidated indicate if the collection balances have been consolidated
     * @see org.kuali.kfs.module.ld.businessobject.LedgerBalance
     */
    public void updateLedgerBalanceByPendingLedgerEntry(Collection<LedgerBalance> balanceCollection, Map fieldValues, String pendingEntryOption, boolean isConsolidated);

    /**
     * update a given balance collection with the pending entry obtained from the given field values and pending entry option
     *
     * @param balanceCollection the given ledger balance collection
     * @param fieldValues the given field values
     * @param pendingEntryOption the given pending entry option: all, approved or none
     * @param isConsolidated indicate if the collection balances have been consolidated
     * @see org.kuali.kfs.module.ld.businessobject.LedgerBalance
     */
    public void updateCurrentFundsByPendingLedgerEntry(Collection<AccountStatusCurrentFunds> balanceCollection, Map fieldValues, String pendingEntryOption, boolean isConsolidated);

    /**
     * update a given ledger entry collection with the pending entry obtained from the given field values and pending entry option
     *
     * @param entryCollection the given ledger entry collection
     * @param fieldValues the given field values
     * @param pendingEntryOption the given pending entry option: all, approved or none
     * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry
     */
    public void updateLedgerEntryByPendingLedgerEntry(Collection<LedgerEntry> entryCollection, Map fieldValues, String pendingEntryOption);

    /**
     * Get the Pending Entry Option selected
     *
     * @param fieldValues
     * @return String
     */
    public String getSelectedPendingEntryOption(Map fieldValues);

    /**
     * Returns true if the CG Beginning Balance Exclude Option is YES; false otherwise.
     *
     * @param fieldValues
     * @return String
     */
    public boolean isCgBeginningBalanceOnlyExcluded(Map fieldValues);

}

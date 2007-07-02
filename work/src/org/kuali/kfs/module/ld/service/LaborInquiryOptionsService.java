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
package org.kuali.module.labor.service;

import java.util.Collection;
import java.util.Map;

import org.kuali.core.web.ui.Field;
import org.kuali.core.web.ui.Row;

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

    public void updateByPendingLedgerEntry(Collection entryCollection, Map fieldValues, String pendingEntryOption, boolean isConsolidated, boolean isCostShareInclusive);

    /**
     * Get the Pending Entry Option selected 
     * 
     * @param fieldValues
     * @return String 
     */
    public String getSelectedPendingEntryOption(Map fieldValues);

    public void updateEntryCollection(Collection entryCollection, Map fieldValues, boolean isApproved, boolean isConsolidated, boolean isCostShareExcluded);
}

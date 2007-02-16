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
package org.kuali.kfs.document;

import java.util.List;

import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.module.gl.util.SufficientFundsItem;

/**
 * Defines methods that must be implements for a general ledger posting document.
 */
public interface GeneralLedgerPostingDocument extends LedgerPostingDocument{
    /**
     * This method retrieves the list of GLPEs for the document.
     * 
     * @return A list of pending entries.
     */
    List<GeneralLedgerPendingEntry> getGeneralLedgerPendingEntries();

    /**
     * This method retrieves a particular pending entry instance, automatically instantiating any missing intervening instances.
     * This behavior is coupled tightly with some underlying issues that the Struts PojoProcessor plugin has with how objects get
     * instantiated within lists. This behavior is required because otherwise when the PojoProcessor tries to automatically inject
     * values into the list, it will get an index out of bounds error if the instance at an index is being called and prior
     * instances at indices before that one are not being instantiated.
     * 
     * @param index
     * @return The GLPE instance at the passed in index.
     */
    GeneralLedgerPendingEntry getGeneralLedgerPendingEntry(int index);

    /**
     * This method sets the list of pending entries for this document.
     * 
     * @param generalLedgerPendingEntries
     */
    void setGeneralLedgerPendingEntries(List<GeneralLedgerPendingEntry> generalLedgerPendingEntries);

    /**
     * Returns whether the system has enabled flexible bank offsets. The CashManagementDocument displays the GLPE tag conditionally
     * based on this.
     * 
     * @return whether the system has enabled flexible bank offsets
     */
    boolean isBankCashOffsetEnabled();
    
    /**
     * This method will check sufficient funds for the document
     * 
     * @return a list of sufficientfundsitems that do not have sufficient funds. It returns an empty list if there is sufficient
     *         funds for the entire document
     */
    public List<SufficientFundsItem> checkSufficientFunds();

    /**
     * This method will return only PLEs that should be checked for SF.  Normally this will be all PLEs, but some docs (such as BA) have additional requirements.
     * 
     * @return a list of sufficientfundsitems that do not have sufficient funds. It returns an empty list if there is sufficient
     *         funds for the entire document
     */
    public List<GeneralLedgerPendingEntry> getPendingLedgerEntriesForSufficientFundsChecking();
}

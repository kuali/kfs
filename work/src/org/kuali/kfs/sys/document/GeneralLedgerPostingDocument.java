/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.sys.document;

import java.util.List;

import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.SufficientFundsItem;

/**
 * Defines methods that must be implements for a general ledger posting document.
 */
public interface GeneralLedgerPostingDocument extends LedgerPostingDocument {
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
     * This method will check sufficient funds for the document
     * 
     * @return a list of sufficientfundsitems that do not have sufficient funds. It returns an empty list if there is sufficient
     *         funds for the entire document
     */
    public List<SufficientFundsItem> checkSufficientFunds();

    /**
     * This method will return only PLEs that should be checked for SF. Normally this will be all PLEs, but some docs (such as BA)
     * have additional requirements.
     * 
     * @return a list of sufficientfundsitems that do not have sufficient funds. It returns an empty list if there is sufficient
     *         funds for the entire document
     */
    public List<GeneralLedgerPendingEntry> getPendingLedgerEntriesForSufficientFundsChecking();
}

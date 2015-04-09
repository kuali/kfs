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
package org.kuali.kfs.module.cab.document.web;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableActionHistory;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;

public class PurApLineSession {
    private List<PurchasingAccountsPayableActionHistory> actionsTakenHistory;
    private List<GeneralLedgerEntry> glEntryUpdateList;
    private List<PurchasingAccountsPayableItemAsset> processedItems;

    public PurApLineSession() {
        actionsTakenHistory = new ArrayList<PurchasingAccountsPayableActionHistory>();
        glEntryUpdateList = new ArrayList<GeneralLedgerEntry>();
        processedItems = new ArrayList<PurchasingAccountsPayableItemAsset>();
    }


    /**
     * Gets the processedDocuments attribute. 
     * @return Returns the processedDocuments.
     */
    public List<PurchasingAccountsPayableItemAsset> getProcessedItems() {
        return processedItems;
    }


    /**
     * Gets the glEntryList attribute. 
     * @return Returns the glEntryList.
     */
    public List<GeneralLedgerEntry> getGlEntryUpdateList() {
        return glEntryUpdateList;
    }


    /**
     * Sets the glEntryList attribute value.
     * @param glEntryList The glEntryList to set.
     */
    public void setGlEntryUpdateList(List<GeneralLedgerEntry> glEntryList) {
        this.glEntryUpdateList = glEntryList;
    }


    /**
     * Gets the actionsTakenHistory attribute.
     * 
     * @return Returns the actionsTakenHistory.
     */
    public List<PurchasingAccountsPayableActionHistory> getActionsTakenHistory() {
        return actionsTakenHistory;
    }

    /**
     * Sets the actionsTakenHistory attribute value.
     * 
     * @param actionsTakenHistory The actionsTakenHistory to set.
     */
    public void setActionsTakenHistory(List<PurchasingAccountsPayableActionHistory> actionsTakenHistory) {
        this.actionsTakenHistory = actionsTakenHistory;
    }
}

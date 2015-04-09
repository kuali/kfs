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
package org.kuali.kfs.module.ld.dataaccess;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.sys.dataaccess.GeneralLedgerPendingEntryDao;

/**
 * LaborLedgerPendingEntryDao DAO Interface.
 */
public interface LaborLedgerPendingEntryDao extends GeneralLedgerPendingEntryDao {

    /**
     * This method retrieves all pending ledger entries with the given search criteria
     * 
     * @param fieldValues the input fields and values
     * @param isApproved the flag that indicates whether the pending entries are approved or don't care
     * @param currentFYPeriod current FY period code
     * @param currentFY current fiscal year
     * @param encumbranceBalanceTypes a list of encumbrance balance types
     * @return all pending ledger entries that may belong to encumbrance table
     */
    public Iterator<LaborLedgerPendingEntry> findPendingLedgerEntriesForLedgerBalance(Map fieldValues, boolean isApproved, String currentFYPeriod, int currentFY, List<String> encumbranceBalanceTypes);

    /**
     * This method retrieves all pending ledger entries with the given search criteria
     * 
     * @param fieldValues the input fields and values
     * @param businessObject
     * @return all pending ledger entries that may belong to pendingentry table
     */
    public Collection<LaborLedgerPendingEntry> hasPendingLaborLedgerEntry(Map fieldValues, Object businessObject);
}

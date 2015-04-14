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
package org.kuali.kfs.module.ld.service;

import org.kuali.kfs.module.ld.businessobject.LaborGeneralLedgerEntry;

/**
 * This interface provides its clients with access to labor general leger entries in the backend data store.
 */
public interface LaborGeneralLedgerEntryService {

    /**
     * Save the given ledger entry or update it if it exsits
     * 
     * @param ledgerEntry the given ledger entry
     */
    void save(LaborGeneralLedgerEntry laborGeneralLedgerEntry);

    /**
     * The sequence number is one of the primary keys of ledger entry. The entries can be grouped by other keys. This method is used
     * to get the maximum sequence number in the group of entries.
     * 
     * @param ledgerEntry the given ledger entry
     * @return the maximum sequence number in a group of entries. If the group doesn't exist, return 0.
     */
    Integer getMaxSequenceNumber(LaborGeneralLedgerEntry laborGeneralLedgerEntry);
}

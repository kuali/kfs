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

import org.kuali.kfs.module.ld.businessobject.LaborGeneralLedgerEntry;

/**
 * This is the data access object for labor general ledger entry
 * 
 * @see org.kuali.kfs.module.ld.businessobject.LaborGeneralLedgerEntry
 */
public interface LaborGeneralLedgerEntryDao {

    /**
     * get the max squence number with the given Labor General edger Entry object
     * 
     * @param laborGeneralLedgerEntry the given labor General Ledger Entry
     * @return the max squence number with the infomation in the given Labor General edger Entry object
     */
    Integer getMaxSequenceNumber(LaborGeneralLedgerEntry laborGeneralLedgerEntry);
}

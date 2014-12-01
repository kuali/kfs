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
package org.kuali.kfs.gl.dataaccess;

import org.kuali.kfs.gl.businessobject.Transaction;

/**
 * A surprisingly small DAO interface that declares methods to help Entries interact with the database
 */
public interface EntryDao {

    /**
     * Get the max sequence number currently used for a transaction
     * 
     * @param t the transaction to check
     * @return the max sequence number
     */
    public int getMaxSequenceNumber(Transaction t);

    /**
     * Purge the entry table by chart/year
     * 
     * @param chart the chart of accounts code of entries to purge
     * @param year the university fiscal year of entries to purge
     */
    public void purgeYearByChart(String chart, int year);
}

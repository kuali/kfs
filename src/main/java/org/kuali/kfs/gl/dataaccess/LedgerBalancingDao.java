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


/**
 * DAO interface that declares common methods needed for the balancing process.
 */
public interface LedgerBalancingDao {
    /**
     * Populates LedgerEntryHistory with Entry (or LaborEntry) data based on the start fiscal year passed in
     * @param universityFiscalYear fiscal year the populate should start from
     * @return number of rows affected
     */
    public int populateLedgerEntryHistory(Integer universityFiscalYear);
    
    /**
     * Populates LedgerBalanceHistory with Balance (or LaborBalance) data based on the start fiscal year passed in
     * @param universityFiscalYear fiscal year the populate should start from
     * @return number of rows affected
     */
    public int populateLedgerBalanceHistory(Integer universityFiscalYear);
}

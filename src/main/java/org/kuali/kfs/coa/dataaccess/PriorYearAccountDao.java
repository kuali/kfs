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
package org.kuali.kfs.coa.dataaccess;

/**
 * Methods needed to copy prior year accounts from current year accounts; this population is best done directly through JDBC
 */
public interface PriorYearAccountDao {
    /**
     * This method purges all records in the Prior Year table in the DB base on the input table name .
     * 
     * @param priorYrAcctTableName prior year account table name 
     * @return Number of records that were purged.
     */
    public abstract int purgePriorYearAccounts(String priorYrAcctTableName);

    /**
     * This method copies all organization records from the current Account table to the Prior Year Account table.
     * 
     * @param priorYrAcctTableName prior year account table name 
     * @param acctTableName account table name
     * @return Number of records that were copied.
     */
    public abstract int copyCurrentAccountsToPriorYearTable(String priorYrAcctTableName, String acctTableName);
    
    /**
     * This method copies all organization records from the current Account table to the Prior Year Account table.
     * 
     * @param priorYrAcctTableName prior year account table name 
     * @param acctTableName account table name
     * @return Number of records that were copied.
     */
    public abstract int copyCurrentICRAccountsToPriorYearTable(String priorYrAcctTableName, String acctTableName);
}

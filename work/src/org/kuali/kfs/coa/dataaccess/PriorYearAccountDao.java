/*
 * Copyright 2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

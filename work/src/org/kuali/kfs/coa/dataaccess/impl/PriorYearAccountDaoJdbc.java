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
package org.kuali.module.chart.dao.jdbc;

import org.apache.ojb.broker.metadata.MetadataManager;
import org.kuali.core.dao.jdbc.PlatformAwareDaoBaseJdbc;
import org.kuali.core.dbplatform.RawSQL;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.PriorYearAccount;

/**
 * This class performs actions against the database through direct SQL command calls.
 */
@RawSQL
public class PriorYearAccountDaoJdbc extends PlatformAwareDaoBaseJdbc {

    /** Constant used to retrieve row counts for tables. Obj_Id value exists in all tables in DB. */
    private static final String OBJ_ID = "OBJ_ID";

    /**
     * This method purges all records in the Prior Year Account table in the DB.
     * 
     * @return Number of records that were purged.
     */
    @RawSQL
    public int purgePriorYearAccounts() {
        String priorYrAcctTableName = MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(PriorYearAccount.class).getFullTableName();

        // 1. Count how many rows are currently in the prior year acct table
        int count = getSimpleJdbcTemplate().queryForInt("SELECT COUNT(" + OBJ_ID + ") from " + priorYrAcctTableName);

        // 2. Purge all the rows from the prior year acct table
        getSimpleJdbcTemplate().update("DELETE from " + priorYrAcctTableName);

        return count;
    }

    /**
     * This method copies all organization records from the current Account table to the Prior Year Account table.
     * 
     * @return Number of records that were copied.
     */
    @RawSQL
    public int copyCurrentAccountsToPriorYearTable() {
        String priorYrAcctTableName = MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(PriorYearAccount.class).getFullTableName();
        String acctTableName = MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(Account.class).getFullTableName();

        // 1. Copy all the rows from the current org table to the prior year acct table
        getSimpleJdbcTemplate().update("INSERT into " + priorYrAcctTableName + " SELECT * from " + acctTableName);

        // 2. Count how many rows are currently in the prior year acct table
        return getSimpleJdbcTemplate().queryForInt("SELECT COUNT(" + OBJ_ID + ") from " + priorYrAcctTableName);
    }
}

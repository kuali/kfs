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
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.bo.PriorYearOrganization;
import org.kuali.module.chart.dao.PriorYearOrganizationDao;

/**
 * This class performs actions against the database through direct SQL command calls.
 */
@RawSQL
public class PriorYearOrganizationDaoJdbc extends PlatformAwareDaoBaseJdbc implements PriorYearOrganizationDao {

    /** Constant used to retrieve row counts for tables. Obj_Id value exists in all tables in DB. */
    private static final String OBJ_ID = "OBJ_ID";

    /**
     * This method purges all records in the Prior Year Organization table in the DB.
     * 
     * @return Number of records that were purged.
     * @see org.kuali.module.chart.dao.PriorYearOrganizationDao#purgePriorYearOrganizations()
     */
    @RawSQL
    public int purgePriorYearOrganizations() {
        String priorYrOrgTableName = MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(PriorYearOrganization.class).getFullTableName();

        // 1. Count how many rows are currently in the prior year org table
        int count = getSimpleJdbcTemplate().queryForInt("SELECT COUNT(" + OBJ_ID + ") from " + priorYrOrgTableName);

        // 2. Purge all the rows from the prior year org table
        getSimpleJdbcTemplate().update("DELETE from " + priorYrOrgTableName);

        return count;
    }

    /**
     * This method copies all organization records from the current Org table to the Prior Year Organization table.
     * 
     * @return Number of records that were copied.
     * @see org.kuali.module.chart.dao.PriorYearOrganizationDao#copyCurrentOrganizationsToPriorYearTable()
     */
    @RawSQL
    public int copyCurrentOrganizationsToPriorYearTable() {
        String priorYrOrgTableName = MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(PriorYearOrganization.class).getFullTableName();
        String orgTableName = MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(Org.class).getFullTableName();

        // 1. Copy all the rows from the current org table to the prior year org table
        getSimpleJdbcTemplate().update("INSERT into " + priorYrOrgTableName + " SELECT * from " + orgTableName);

        // 2. Count how many rows are currently in the prior year org table
        return getSimpleJdbcTemplate().queryForInt("SELECT COUNT(" + OBJ_ID + ") from " + priorYrOrgTableName);
    }

}

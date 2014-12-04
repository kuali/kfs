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
package org.kuali.kfs.coa.dataaccess.impl;

import org.apache.ojb.broker.metadata.MetadataManager;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.businessobject.PriorYearOrganization;
import org.kuali.kfs.coa.dataaccess.PriorYearOrganizationDao;
import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;

/**
 * This class performs actions against the database through direct SQL command calls.
 */
public class PriorYearOrganizationDaoJdbc extends PlatformAwareDaoBaseJdbc implements PriorYearOrganizationDao {

    /** Constant used to retrieve row counts for tables. Obj_Id value exists in all tables in DB. */
    private static final String OBJ_ID = "OBJ_ID";

    /**
     * This method purges all records in the Prior Year Organization table in the DB.
     * 
     * @return Number of records that were purged.
     * @see org.kuali.kfs.coa.dataaccess.PriorYearOrganizationDao#purgePriorYearOrganizations()
     */
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
     * @see org.kuali.kfs.coa.dataaccess.PriorYearOrganizationDao#copyCurrentOrganizationsToPriorYearTable()
     */
    public int copyCurrentOrganizationsToPriorYearTable() {
        String priorYrOrgTableName = MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(PriorYearOrganization.class).getFullTableName();
        String orgTableName = MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(Organization.class).getFullTableName();

        // 1. Copy all the rows from the current org table to the prior year org table
        getSimpleJdbcTemplate().update("INSERT into " + priorYrOrgTableName + " SELECT * from " + orgTableName);

        // 2. Count how many rows are currently in the prior year org table
        return getSimpleJdbcTemplate().queryForInt("SELECT COUNT(" + OBJ_ID + ") from " + priorYrOrgTableName);
    }

}

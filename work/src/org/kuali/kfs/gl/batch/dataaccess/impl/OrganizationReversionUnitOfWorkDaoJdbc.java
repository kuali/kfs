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
package org.kuali.kfs.gl.batch.dataaccess.impl;

import org.kuali.kfs.gl.batch.dataaccess.OrganizationReversionUnitOfWorkDao;
import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;

/**
 * A JDBC implementation of the OrganizationReversionUnitOfWorkDao...we had to use this because PersistenceService
 * truncated tables, which is something you can't do on tables with primary keys.
 */
public class OrganizationReversionUnitOfWorkDaoJdbc extends PlatformAwareDaoBaseJdbc implements OrganizationReversionUnitOfWorkDao {
    org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(getClass());

    /**
     * Deletes all existing records in gl_org_rvrsn_ctgry_amt_t and gl_org_rvrsn_unit_wrk_t
     * 
     * @see org.kuali.kfs.gl.batch.dataaccess.OrganizationReversionUnitOfWorkDao#destroyAllUnitOfWorkSummaries()
     */
    public void destroyAllUnitOfWorkSummaries() {
        LOG.info("Attempting to wipe out all unit of work summaries");
        getSimpleJdbcTemplate().update("delete from GL_ORG_RVRSN_CTGRY_AMT_T");
        getSimpleJdbcTemplate().update("delete from GL_ORG_RVRSN_UNIT_WRK_T");
        LOG.info("All unit of work summaries should be now removed");
    }

}

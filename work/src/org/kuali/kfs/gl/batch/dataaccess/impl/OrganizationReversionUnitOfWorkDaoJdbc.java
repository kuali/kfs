/*
 * Copyright 2007 The Kuali Foundation
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

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
package org.kuali.module.gl.dao.jdbc;

import org.kuali.core.dao.jdbc.PlatformAwareDaoBaseJdbc;
import org.kuali.module.gl.dao.OrgReversionUnitOfWorkDao;

/**
 * A JDBC implementation of the OrgReversionUnitOfWorkDao...we had to use this because PersistenceService
 * truncated tables, which is something you can't do on tables with primary keys.
 */
public class OrgReversionUnitOfWorkDaoJdbc extends PlatformAwareDaoBaseJdbc implements OrgReversionUnitOfWorkDao {

    /**
     * Deletes all existing records in gl_org_rvrsn_ctgry_amt_t and gl_org_rvrsn_unit_wrk_t
     * 
     * @see org.kuali.module.gl.dao.OrgReversionUnitOfWorkDao#destroyAllUnitOfWorkSummaries()
     */
    public void destroyAllUnitOfWorkSummaries() {
        getSimpleJdbcTemplate().update("delete from GL_ORG_RVRSN_CTGRY_AMT_T");
        getSimpleJdbcTemplate().update("delete from GL_ORG_RVRSN_UNIT_WRK_T");
    }

}

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
package org.kuali.kfs.module.ld.dataaccess.impl;

import org.kuali.kfs.module.ld.dataaccess.LaborClearGeneralLedgerEntryDao;
import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;

public class LaborClearGeneralLedgerEntryDaoJdbc extends PlatformAwareDaoBaseJdbc implements LaborClearGeneralLedgerEntryDao {
    org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(getClass());
    
    /**
     * @see org.kuali.kfs.module.ld.dataaccess.LaborClearGeneralLedgerEntryDao#deleteCopiedLaborGenerealLedgerEntries()
     */
    public void deleteCopiedLaborGenerealLedgerEntries() {
        LOG.info("clearing labor general ledger entries");
        getSimpleJdbcTemplate().update("delete from LD_LBR_GL_ENTRY_T");
    }

}

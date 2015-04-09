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

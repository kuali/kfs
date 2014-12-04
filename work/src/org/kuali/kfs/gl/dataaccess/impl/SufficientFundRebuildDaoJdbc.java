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
package org.kuali.kfs.gl.dataaccess.impl;

import org.kuali.kfs.gl.dataaccess.SufficientFundRebuildDao;
import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;

/**
 * This class...
 */
public class SufficientFundRebuildDaoJdbc extends PlatformAwareDaoBaseJdbc implements SufficientFundRebuildDao {

    /**
     * @see org.kuali.kfs.gl.dataaccess.SufficientFundRebuildDao#populateSufficientFundRebuild()
     */
    public void populateSufficientFundRebuild() {
       getSimpleJdbcTemplate().update("INSERT INTO GL_SF_REBUILD_T (FIN_COA_CD, ACCT_NBR_FOBJ_CD, acct_fobj_typ_cd, obj_id, ver_nbr) "+ 
               "(SELECT FIN_COA_CD, ACCOUNT_NBR, 'A', obj_id, 1 FROM CA_ACCOUNT_T WHERE ACCT_CLOSED_IND = 'N')");

    }

    /**
     * @see org.kuali.kfs.gl.dataaccess.SufficientFundRebuildDao#purgeSufficientFundRebuild()
     */
    public void purgeSufficientFundRebuild() {
        getSimpleJdbcTemplate().update("DELETE FROM GL_SF_REBUILD_T");

    }   
    
}

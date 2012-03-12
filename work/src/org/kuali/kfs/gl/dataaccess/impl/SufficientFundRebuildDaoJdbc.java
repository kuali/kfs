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

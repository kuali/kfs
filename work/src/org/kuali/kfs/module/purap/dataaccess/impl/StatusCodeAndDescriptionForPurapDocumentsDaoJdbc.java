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
package org.kuali.kfs.module.purap.dataaccess.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.purap.dataaccess.StatusCodeAndDescriptionForPurapDocumentsDao;
import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * A class to do the database queries needed to prepare documents status codes and descriptions.
 */
public class StatusCodeAndDescriptionForPurapDocumentsDaoJdbc extends PlatformAwareDaoBaseJdbc implements StatusCodeAndDescriptionForPurapDocumentsDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(StatusCodeAndDescriptionForPurapDocumentsDaoJdbc.class);
    
    /**
     * @see org.kuali.kfs.module.endow.batch.dataaccess.GLInterfaceBatchProcessDao#findDocumentTypes()
     */
    public Map<String, String> getRequisitionDocumentStatuses() {
        LOG.debug("getRequisitionDocumentStatuses() started");
        
        Map<String, String> requistionStatuses = new HashMap<String, String>();
        
        SqlRowSet statusesRowSet = getJdbcTemplate().queryForRowSet("SELECT * FROM PUR_REQS_STAT_T ORDER BY REQS_STAT_CD"); 

        while (statusesRowSet.next()) {
            requistionStatuses.put(statusesRowSet.getString("REQS_STAT_CD"), statusesRowSet.getString("REQS_STAT_DESC"));
        }
        
        LOG.debug("getRequisitionDocumentStatuses() exited");
        
        return requistionStatuses;
    }
    
    /**
     * @see org.kuali.kfs.module.purap.dataaccess.StatusCodeAndDescriptionForPurapDocumentsDao#saveMigratedApplicationDocumentStatuses(org.kuali.rice.kew.api.WorkflowDocument, java.lang.String)
     */
    public boolean updateAndSaveMigratedApplicationDocumentStatuses(String documentNumber, String applicationDocumentStatus, Date applicationDocumentStatusModifiedDate) {
        boolean success = true;
        
        String sql = "UPDATE KREW_DOC_HDR_T SET APP_DOC_STAT = '" + applicationDocumentStatus + "', APP_DOC_STAT_MDFN_DT = '" + applicationDocumentStatusModifiedDate + "' WHERE DOC_HDR_ID = '" + documentNumber + "'";
        
        getJdbcTemplate().execute(sql);
        
        return success;
    }
    
}   

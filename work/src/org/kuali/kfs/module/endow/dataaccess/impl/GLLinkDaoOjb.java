/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.dataaccess.impl;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.GLLink;
import org.kuali.kfs.module.endow.dataaccess.GLLinkDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.util.ObjectUtils;

public class GLLinkDaoOjb extends PlatformAwareDaoBaseOjb implements GLLinkDao {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GLLinkDaoOjb.class);
    
    /**
     * @see org.kuali.kfs.module.endow.dataaccess.GLLinkDao#getObjectCode(String, String)
     */
    public String getObjectCode(String chartCode, String endowmentTransactionCode) {
        GLLink gLLink = null;
        String objectCode = null;
        
        Criteria criteria = new Criteria();
        criteria.addEqualTo(EndowPropertyConstants.KEMID_ETRAN_GL_LNK_CHART_CD, chartCode);
        criteria.addEqualTo(EndowPropertyConstants.KEMID_ETRAN_GL_LNK_ETRAN_CD, endowmentTransactionCode);
        criteria.addEqualTo(EndowPropertyConstants.KEMID_ETRAN_GL_LNK_ROW_ACTIVE_IND, EndowConstants.YES);
        
        QueryByCriteria query = QueryFactory.newQuery(GLLink.class, criteria);
        gLLink = (GLLink) getPersistenceBrokerTemplate().getObjectByQuery(query);
        
        if (ObjectUtils.isNotNull(gLLink)) {
            objectCode = gLLink.getObject();
        }
        
        return objectCode;
    }
}

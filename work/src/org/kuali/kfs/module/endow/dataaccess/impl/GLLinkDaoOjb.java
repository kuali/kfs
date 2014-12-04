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

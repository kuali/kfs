/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.module.chart.dao.ojb;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.chart.bo.ObjLevel;
import org.kuali.module.chart.dao.ObjectLevelDao;

/**
 * This class is the OJB implementation of the ObjectLevelDao interface.
 */
public class ObjectLevelDaoOjb extends PlatformAwareDaoBaseOjb implements ObjectLevelDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ObjectLevelDaoOjb.class);

    /**
     * @see org.kuali.module.chart.dao.ObjectLevelDao#getByPrimaryId(java.lang.String, java.lang.String)
     */
    public ObjLevel getByPrimaryId(String chartOfAccountsCode, String objectLevelCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("financialObjectLevelCode", objectLevelCode);

        return (ObjLevel) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(ObjLevel.class, criteria));
    }

}

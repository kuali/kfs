/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.coa.dataaccess.impl;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.coa.businessobject.ObjectConsolidation;
import org.kuali.kfs.coa.dataaccess.ObjectConsDao;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

/**
 * This class is the OJB implementation of the ObjectLevelDao interface.
 */
public class ObjectConsDaoOjb extends PlatformAwareDaoBaseOjb implements ObjectConsDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ObjectConsDaoOjb.class);

    /**
     * @see org.kuali.kfs.coa.dataaccess.ObjectConsDao#getByPrimaryId(java.lang.String, java.lang.String)
     */
    public ObjectConsolidation getByPrimaryId(String chartOfAccountsCode, String objectConsCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("finConsolidationObjectCode", objectConsCode);

        return (ObjectConsolidation) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(ObjectConsolidation.class, criteria));
    }

}

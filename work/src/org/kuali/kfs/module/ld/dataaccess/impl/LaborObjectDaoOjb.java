/*
 * Copyright 2007-2008 The Kuali Foundation
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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.gl.OJBUtility;
import org.kuali.kfs.module.ld.businessobject.LaborObject;
import org.kuali.kfs.module.ld.dataaccess.LaborObjectDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * This is the data access object for Labor Object
 * 
 * @see org.kuali.kfs.module.ld.businessobject.LaborObject
 */
public class LaborObjectDaoOjb extends PlatformAwareDaoBaseOjb implements LaborObjectDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborObjectDaoOjb.class);

    /**
     * @see org.kuali.kfs.module.ld.dataaccess.LaborObjectDao#findAllLaborObjectInPositionGroups(java.util.Map, java.util.List)
     */
    public Collection<LaborObject> findAllLaborObjectInPositionGroups(Map<String, Object> fieldValues, List<String> positionGroupCodes) {
        LOG.debug("Start findAllLaborObjectInPositionGroups()");
        
        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new LaborObject());       
        criteria.addIn(KFSPropertyConstants.POSITION_OBJECT_GROUP_CODE, positionGroupCodes);
        
        QueryByCriteria query = QueryFactory.newQuery(LaborObject.class, criteria);
 
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }
}

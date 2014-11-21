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

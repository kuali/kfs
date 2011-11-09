/*
 * Copyright 2005 The Kuali Foundation
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

import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.coa.businessobject.OrganizationReversion;
import org.kuali.kfs.coa.businessobject.OrganizationReversionCategory;
import org.kuali.kfs.coa.dataaccess.OrganizationReversionDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * This class implements the {@link OrganizationReversionDao} data access methods using Ojb
 */
public class OrganizationReversionDaoOjb extends PlatformAwareDaoBaseOjb implements OrganizationReversionDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationReversionDaoOjb.class);


    /**
     * @see org.kuali.kfs.coa.dataaccess.OrganizationReversionDao#getCategories()
     */
    public List<OrganizationReversionCategory> getCategories() {
        LOG.debug("getCategories() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("active", true);
        QueryByCriteria q = QueryFactory.newQuery(OrganizationReversionCategory.class, criteria);
        q.addOrderByAscending("organizationReversionSortCode");

        return (List) getPersistenceBrokerTemplate().getCollectionByQuery(q);
    }
}

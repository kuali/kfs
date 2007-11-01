/*
 * Copyright 2005-2007 The Kuali Foundation.
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

import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.chart.bo.OrganizationReversion;
import org.kuali.module.chart.bo.OrganizationReversionCategory;
import org.kuali.module.chart.dao.OrganizationReversionDao;

/**
 * This class implements the {@link OrganizationReversionDao} data access methods using Ojb
 */
public class OrganizationReversionDaoOjb extends PlatformAwareDaoBaseOjb implements OrganizationReversionDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationReversionDaoOjb.class);

    /**
     * @see org.kuali.module.chart.dao.OrganizationReversionDao#getByPrimaryId(java.lang.Integer, java.lang.String,
     *      java.lang.String)
     */
    public OrganizationReversion getByPrimaryId(Integer universityFiscalYear, String financialChartOfAccountsCode, String organizationCode) {
        LOG.debug("getByPrimaryId() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("universityFiscalYear", universityFiscalYear);
        criteria.addEqualTo("chartOfAccountsCode", financialChartOfAccountsCode);
        criteria.addEqualTo("organizationCode", organizationCode);

        return (OrganizationReversion) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(OrganizationReversion.class, criteria));
    }

    /**
     * @see org.kuali.module.chart.dao.OrganizationReversionDao#getCategories()
     */
    public List<OrganizationReversionCategory> getCategories() {
        LOG.debug("getCategories() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("organizationReversionCategoryActiveIndicator", true);
        QueryByCriteria q = QueryFactory.newQuery(OrganizationReversionCategory.class, criteria);
        q.addOrderByAscending("organizationReversionSortCode");

        return (List) getPersistenceBrokerTemplate().getCollectionByQuery(q);
    }
}

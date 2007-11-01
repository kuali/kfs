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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.dao.ChartDao;

/**
 * This class is the OJB implementation of the ChartDao interface.
 */

public class ChartDaoOjb extends PlatformAwareDaoBaseOjb implements ChartDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ChartDaoOjb.class);

    /**
     * 
     * @see org.kuali.module.chart.dao.ChartDao#getAll()
     */
    public Collection getAll() {
        QueryByCriteria qbc = QueryFactory.newQuery(Chart.class, (Criteria) null);
        qbc.addOrderByAscending("chartOfAccountsCode");
        Collection charts = getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        List chartList = new ArrayList(charts);
        return chartList;
    }

    /**
     * 
     * @see org.kuali.module.chart.dao.ChartDao#getUniversityChart()
     */
    public Chart getUniversityChart() {
        Criteria criteria = new Criteria();
        criteria.addEqualToField("FIN_COA_CD", "RPTS_TO_FIN_COA_CD");
        return (Chart) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Chart.class, criteria)).iterator().next();
    }

    /**
     * 
     * @see org.kuali.module.chart.dao.ChartDao#getByPrimaryId(java.lang.String)
     */
    public Chart getByPrimaryId(String chartOfAccountsCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);

        return (Chart) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(Chart.class, criteria));
    }

    /**
     * fetch the charts that the user is manager for
     * 
     * @param kualiUser
     * @return a list of Charts that the user has responsibility for
     */
    public List getChartsThatUserIsResponsibleFor(UniversalUser universalUser) {
        List chartResponsibilities = new ArrayList();
        Criteria criteria = new Criteria();
        criteria.addEqualTo("finCoaManagerUniversalId", universalUser.getPersonUniversalIdentifier());
        Collection charts = getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Chart.class, criteria));
        for (Iterator iter = charts.iterator(); iter.hasNext();) {
            Chart chart = (Chart) iter.next();
            chartResponsibilities.add(chart);
        }
        return chartResponsibilities;
    }
}
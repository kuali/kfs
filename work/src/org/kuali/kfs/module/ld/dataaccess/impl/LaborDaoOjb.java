/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.dao.ojb;

import java.util.ArrayList;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.PropertyConstants;
import org.kuali.module.budget.bo.CalculatedSalaryFoundationTracker;
import org.kuali.module.labor.dao.LaborDao;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * This class is used to retrieve Calculated Salary Foundation Tracker information.
 */
public class LaborDaoOjb extends PersistenceBrokerDaoSupport implements LaborDao {

    /**
     * @see org.kuali.module.labor.dao.LaborDao#getCSFTrackerData(org.kuali.module.budget.bo.CalculatedSalaryFoundationTracker)
     */
    public List getCSFTrackerData(CalculatedSalaryFoundationTracker csf) {

        Criteria criteria = new Criteria();
        criteria.addLike(PropertyConstants.UNIVERSITY_FISCAL_YEAR, csf.getUniversityFiscalYear());
        if ((csf.getChartOfAccountsCode().length() == 0))
            criteria.addLike(PropertyConstants.CHART_OF_ACCOUNTS_CODE, "*");
        else
            criteria.addLike(PropertyConstants.CHART_OF_ACCOUNTS_CODE, csf.getChartOfAccountsCode());

        if ((csf.getAccountNumber().length() == 0))
            criteria.addLike(PropertyConstants.ACCOUNT_NUMBER, "*");
        else
            criteria.addLike(PropertyConstants.ACCOUNT_NUMBER, csf.getAccountNumber());

        if ((csf.getSubAccountNumber().length() == 0))
            criteria.addLike(PropertyConstants.SUB_ACCOUNT_NUMBER, "*");
        else
            criteria.addLike(PropertyConstants.SUB_ACCOUNT_NUMBER, csf.getSubAccountNumber());

        if ((csf.getFinancialObjectCode().length() == 0))
            criteria.addLike(PropertyConstants.FINANCIAL_OBJECT_CODE, "*");
        else
            criteria.addLike(PropertyConstants.FINANCIAL_OBJECT_CODE, csf.getFinancialObjectCode());

        QueryByCriteria query = QueryFactory.newQuery(CalculatedSalaryFoundationTracker.class, criteria);
        return new ArrayList(getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(CalculatedSalaryFoundationTracker.class, criteria)));
    }
}

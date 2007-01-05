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
package org.kuali.module.budget.dao.ojb;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.module.budget.bo.BudgetConstructionOrganizationReports;
import org.kuali.module.budget.dao.BudgetConstructionOrganizationReportsDao;
import org.kuali.module.chart.bo.Org;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

public class BudgetConstructionOrganizationReportsDaoOjb 
    extends PersistenceBrokerDaoSupport 
    implements BudgetConstructionOrganizationReportsDao {

    // TODO Auto-generated method stub
    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.budget.dao.BudgetConstructionOrganizationReportsDao#getByPrimaryId(java.lang.String, java.lang.String)
     */
    
    public BudgetConstructionOrganizationReports getByPrimaryId(String chartOfAccountsCode, String organizationCode) {

            Criteria criteria = new Criteria();
            criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
            criteria.addEqualTo("organizationCode", organizationCode);

            return (BudgetConstructionOrganizationReports) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(BudgetConstructionOrganizationReports.class, criteria));
        }
    }


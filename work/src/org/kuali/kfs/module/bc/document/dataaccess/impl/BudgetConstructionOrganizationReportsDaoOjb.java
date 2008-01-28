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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.util.TransactionalServiceUtils;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.budget.bo.BudgetConstructionOrganizationReports;
import org.kuali.module.budget.dao.BudgetConstructionOrganizationReportsDao;

public class BudgetConstructionOrganizationReportsDaoOjb extends PlatformAwareDaoBaseOjb implements BudgetConstructionOrganizationReportsDao {

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
        
        QueryByCriteria qbc = QueryFactory.newQuery(BudgetConstructionOrganizationReports.class, criteria);
        //String groupBy = "subFundGroupCode";
        
        /*qbc.addOrderByAscending("organizationChartOfAccountsCode");
        qbc.addOrderByAscending("organizationCode");
        qbc.addOrderByAscending("chartOfAccountsCode");
        qbc.addOrderByAscending("fundGroupCode");
        */
        //qbc.addOrderByAscending("subFundGroupCode");
        //qbc.addGroupBy(groupBy);
        /*qbc.addOrderByAscending("accountNumber");
        qbc.addOrderByAscending("subAccountNumber");
        qbc.addOrderByAscending("incomeExpenseCode");
        */
        
        //Changed from (BudgetConstructionOrganizationReports) getPersistenceBrokerTemplate().getCollectionByQuery(qbc); 
        //Since using primaryId, getObject should be better than collection. 
        return (BudgetConstructionOrganizationReports) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }
    
    public Collection getBySearchCriteria(Class cls, Map searchCriteria){
        
        Criteria criteria = new Criteria();
        for (Iterator iter = searchCriteria.keySet().iterator(); iter.hasNext();) {
            String element = (String) iter.next();
            criteria.addEqualTo(element, searchCriteria.get(element));
        }
        
        QueryByCriteria qbc = QueryFactory.newQuery(cls, criteria);
        
        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        
    }

    /**
     * @see org.kuali.module.budget.dao.BudgetConstructionOrganizationReportsDao#getActiveChildOrgs(java.lang.String,
     *      java.lang.String)
     */
    public List getActiveChildOrgs(String chartOfAccountsCode, String organizationCode) {

        List orgs = new ArrayList();

        Criteria cycleCheckCriteria = new Criteria();
        cycleCheckCriteria.addEqualToField("chartOfAccountsCode", "reportsToChartOfAccountsCode");
        cycleCheckCriteria.addEqualToField("organizationCode", "reportsToOrganizationCode");
        cycleCheckCriteria.setEmbraced(true);
        cycleCheckCriteria.setNegative(true);

        Criteria criteria = new Criteria();
        criteria.addEqualTo("reportsToChartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("reportsToOrganizationCode", organizationCode);
        criteria.addAndCriteria(cycleCheckCriteria);
        criteria.addEqualTo("organization.organizationActiveIndicator", Boolean.TRUE);

        orgs = (List) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(BudgetConstructionOrganizationReports.class, criteria));

        if (orgs.isEmpty() || orgs.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        return orgs;
    }

    /**
     * @see org.kuali.module.budget.dao.BudgetConstructionOrganizationReportsDao#isLeafOrg(java.lang.String, java.lang.String)
     */
    public boolean isLeafOrg(String chartOfAccountsCode, String organizationCode) {

        Criteria childExistsCriteria = new Criteria();
        childExistsCriteria.addEqualTo("reportsToChartOfAccountsCode", chartOfAccountsCode);
        childExistsCriteria.addEqualTo("reportsToOrganizationCode", organizationCode);
        childExistsCriteria.addEqualTo("organization.organizationActiveIndicator", Boolean.TRUE);

        QueryByCriteria childExistsQuery = QueryFactory.newQuery(BudgetConstructionOrganizationReports.class, childExistsCriteria);

        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("organizationCode", organizationCode);
        
        criteria.addExists(childExistsQuery);

        String[] queryAttr = { KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE };

        ReportQueryByCriteria query = new ReportQueryByCriteria(BudgetConstructionOrganizationReports.class, queryAttr, criteria, true);
        Iterator rowsReturned = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        if (rowsReturned.hasNext()) {
            TransactionalServiceUtils.exhaustIterator(rowsReturned);
            return false;
        }
        else {
            return true;
        }
    }
}

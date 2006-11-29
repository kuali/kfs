/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/gl/dataaccess/impl/EncumbranceDaoOjb.java,v $
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
package org.kuali.module.gl.dao.ojb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.module.gl.bo.Encumbrance;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.EncumbranceDao;
import org.kuali.module.gl.util.OJBUtility;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

public class EncumbranceDaoOjb extends PersistenceBrokerDaoSupport implements EncumbranceDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EncumbranceDaoOjb.class);

    public EncumbranceDaoOjb() {
        super();
    }

    /**
     * @see org.kuali.module.gl.dao.EncumbranceDao#getEncumbranceByTransaction(org.kuali.module.gl.bo.Transaction)
     */
    public Encumbrance getEncumbranceByTransaction(Transaction t) {
        LOG.debug("getEncumbranceByTransaction() started");

        Criteria crit = new Criteria();
        crit.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR, t.getUniversityFiscalYear());
        crit.addEqualTo(PropertyConstants.CHART_OF_ACCOUNTS_CODE, t.getChartOfAccountsCode());
        crit.addEqualTo(PropertyConstants.ACCOUNT_NUMBER, t.getAccountNumber());
        crit.addEqualTo(PropertyConstants.SUB_ACCOUNT_NUMBER, t.getSubAccountNumber());
        crit.addEqualTo(PropertyConstants.OBJECT_CODE, t.getFinancialObjectCode());
        crit.addEqualTo(PropertyConstants.SUB_OBJECT_CODE, t.getFinancialSubObjectCode());
        crit.addEqualTo(PropertyConstants.BALANCE_TYPE_CODE, t.getFinancialBalanceTypeCode());
        crit.addEqualTo(PropertyConstants.DOCUMENT_TYPE_CODE, t.getFinancialDocumentTypeCode());
        crit.addEqualTo(PropertyConstants.ORIGIN_CODE, t.getFinancialSystemOriginationCode());
        crit.addEqualTo(PropertyConstants.DOCUMENT_NUMBER, t.getDocumentNumber());

        QueryByCriteria qbc = QueryFactory.newQuery(Encumbrance.class, crit);
        return (Encumbrance) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

    /**
     * @see org.kuali.module.gl.dao.EncumbranceDao#getEncumbrancesToClose(java.lang.Integer)
     */
    public Iterator getEncumbrancesToClose(Integer fiscalYear) {

        Criteria criteria = new Criteria();
        criteria.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);

        QueryByCriteria query = new QueryByCriteria(Encumbrance.class, criteria);
        query.addOrderByAscending(PropertyConstants.CHART_OF_ACCOUNTS_CODE);
        query.addOrderByAscending(PropertyConstants.ACCOUNT_NUMBER);
        query.addOrderByAscending(PropertyConstants.SUB_ACCOUNT_NUMBER);
        query.addOrderByAscending(PropertyConstants.OBJECT_CODE);
        query.addOrderByAscending(PropertyConstants.SUB_OBJECT_CODE);
        query.addOrderByAscending(PropertyConstants.BALANCE_TYPE_CODE);

        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.gl.dao.EncumbranceDao#save(org.kuali.module.gl.bo.Encumbrance)
     */
    public void save(Encumbrance e) {
        LOG.debug("save() started");

        getPersistenceBrokerTemplate().store(e);
    }

    /**
     * @see org.kuali.module.gl.dao.EncumbranceDao#purgeYearByChart(java.lang.String, int)
     */
    public void purgeYearByChart(String chartOfAccountsCode, int year) {
        LOG.debug("purgeYearByChart() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(PropertyConstants.CHART, chartOfAccountsCode);
        criteria.addLessThan(PropertyConstants.UNIVERSITY_FISCAL_YEAR, new Integer(year));

        getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(Encumbrance.class, criteria));

        // This is required because if any deleted account balances are in the cache, deleteByQuery
        // doesn't
        // remove them from the cache so a future select will retrieve these deleted account
        // balances from
        // the cache and return them. Clearing the cache forces OJB to go to the database again.
        getPersistenceBrokerTemplate().clearCache();
    }

    /**
     * @see org.kuali.module.gl.dao.EncumbranceDao#getAllEncumbrances()
     */
    public Iterator getAllEncumbrances() {
        Criteria criteria = new Criteria();
        QueryByCriteria query = QueryFactory.newQuery(Encumbrance.class, criteria);
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.gl.dao.EncumbranceDao#getSummarizedEncumbrances(String, boolean)
     */
    public Iterator getSummarizedEncumbrances(String documentTypeCode, boolean included) {
        Criteria criteria = new Criteria();

        if (included) {
            criteria.addEqualTo(PropertyConstants.DOCUMENT_TYPE_CODE, documentTypeCode);
        }
        else {
            criteria.addNotEqualTo(PropertyConstants.DOCUMENT_TYPE_CODE, documentTypeCode);
        }

        ReportQueryByCriteria query = QueryFactory.newReportQuery(Encumbrance.class, criteria);

        // set the selection attributes
        List attributeList = buildAttributeList();
        String[] attributes = (String[]) attributeList.toArray(new String[attributeList.size()]);
        query.setAttributes(attributes);

        // add the group criteria into the selection statement
        List groupByList = buildGroupByList();
        String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);
        query.addGroupBy(groupBy);

        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.gl.dao.EncumbranceDao#findOpenEncumbrance(java.util.Map)
     */
    public Iterator findOpenEncumbrance(Map fieldValues) {
        LOG.debug("findOpenEncumbrance() started");

        Query query = this.getOpenEncumbranceQuery(fieldValues);
        OJBUtility.limitResultSize(query);
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.gl.dao.EncumbranceDao#getOpenEncumbranceRecordCount(java.util.Map)
     */
    public Integer getOpenEncumbranceRecordCount(Map fieldValues) {
        LOG.debug("getOpenEncumbranceRecordCount() started");

        Query query = this.getOpenEncumbranceQuery(fieldValues);
        return getPersistenceBrokerTemplate().getCount(query);
    }

    // build the query for encumbrance search
    private Query getOpenEncumbranceQuery(Map fieldValues) {
        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new Encumbrance());
        criteria.addIn(PropertyConstants.BALANCE_TYPE_CODE, Arrays.asList(Constants.ENCUMBRANCE_BALANCE_TYPE));
        return QueryFactory.newQuery(Encumbrance.class, criteria);
    }

    /**
     * This method builds the atrribute list used by balance searching
     */
    private List buildAttributeList() {
        List attributeList = this.buildGroupByList();

        attributeList.add("sum(" + PropertyConstants.ACCOUNT_LINE_ENCUMBRANCE_AMOUNT + ")");
        attributeList.add("sum(" + PropertyConstants.ACCOUNT_LINE_ENCUMBRANCE_CLOSED_AMOUNT + ")");

        return attributeList;
    }

    /**
     * This method builds group by attribute list used by balance searching
     */
    private List buildGroupByList() {
        List attributeList = new ArrayList();

        attributeList.add(PropertyConstants.UNIVERSITY_FISCAL_YEAR);
        attributeList.add(PropertyConstants.CHART_OF_ACCOUNTS_CODE);
        attributeList.add(PropertyConstants.ACCOUNT_NUMBER);
        attributeList.add(PropertyConstants.SUB_ACCOUNT_NUMBER);
        attributeList.add(PropertyConstants.OBJECT_CODE);
        attributeList.add(PropertyConstants.SUB_OBJECT_CODE);
        attributeList.add(PropertyConstants.BALANCE_TYPE_CODE);

        return attributeList;
    }
}
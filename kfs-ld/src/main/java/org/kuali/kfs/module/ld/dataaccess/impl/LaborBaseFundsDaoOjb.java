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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.gl.Constant;
import org.kuali.kfs.gl.OJBUtility;
import org.kuali.kfs.module.ld.businessobject.AccountStatusBaseFunds;
import org.kuali.kfs.module.ld.dataaccess.LaborBaseFundsDao;
import org.kuali.kfs.module.ld.util.ConsolidationUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * This is the data access object for account status base funds.
 * 
 * @see org.kuali.kfs.module.ld.businessobject.AccountStatusBaseFunds
 */
public class LaborBaseFundsDaoOjb extends PlatformAwareDaoBaseOjb implements LaborBaseFundsDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborBaseFundsDaoOjb.class);

    /**
     * @see org.kuali.kfs.module.ld.dataaccess.LaborBaseFundsDao#findLaborBaseFunds(java.util.Map, boolean)
     */
    public List<AccountStatusBaseFunds> findLaborBaseFunds(Map fieldValues, boolean isConsolidated) {
        LOG.debug("Start findLaborBaseFunds()");

        Iterator<Object[]> queryResults = this.findLaborBaseFundsRawData(fieldValues, isConsolidated);
        List<AccountStatusBaseFunds> BaseFundsCollection = new ArrayList<AccountStatusBaseFunds>();
        while (queryResults != null && queryResults.hasNext()) {
            BaseFundsCollection.add(this.marshalAccountStatusBaseFunds(queryResults.next()));
        }
        return BaseFundsCollection;
    }

    // get the labor base funds according to the given criteria
    protected Iterator<Object[]> findLaborBaseFundsRawData(Map fieldValues, boolean isConsolidated) {
        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new AccountStatusBaseFunds());
        criteria.addEqualTo(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_BASE_BUDGET);

        criteria.addEqualToField(KFSPropertyConstants.LABOR_OBJECT + "." + KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        criteria.addEqualToField(KFSPropertyConstants.LABOR_OBJECT + "." + KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        criteria.addEqualToField(KFSPropertyConstants.LABOR_OBJECT + "." + KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSPropertyConstants.FINANCIAL_OBJECT_CODE);

        // this statement is used to force OJB to join LABOR_OBJECT and GL_BALANCE tables
        criteria.addNotNull(KFSPropertyConstants.LABOR_OBJECT + "." + KFSPropertyConstants.FINANCIAL_OBJECT_FRINGE_OR_SALARY_CODE);

        ReportQueryByCriteria query = QueryFactory.newReportQuery(AccountStatusBaseFunds.class, criteria);

        List<String> groupByList = getGroupByList(isConsolidated);
        String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);
        query.addGroupBy(groupBy);

        List<String> getAttributeList = getAttributeListForBaseFunds(isConsolidated, false);
        String[] attributes = (String[]) getAttributeList.toArray(new String[getAttributeList.size()]);
        query.setAttributes(attributes);

        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    // marshal into AccountStatusBaseFunds from the query result
    protected AccountStatusBaseFunds marshalAccountStatusBaseFunds(Object[] queryResult) {
        AccountStatusBaseFunds baseFunds = new AccountStatusBaseFunds();
        List<String> keyFields = this.getAttributeListForBaseFunds(false, true);

        ObjectUtil.buildObject(baseFunds, queryResult, keyFields);
        return baseFunds;
    }

    // define a list of attributes that are used as the grouping criteria
    protected List<String> getGroupByList(boolean isConsolidated) {
        List<String> groupByList = new ArrayList<String>();
        groupByList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        groupByList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        groupByList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        groupByList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);

        if (!isConsolidated) {
            groupByList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
            groupByList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        }
        return groupByList;
    }

    // define the return attribute list
    protected List<String> getAttributeList(boolean isConsolidated) {
        List<String> attributeList = getGroupByList(isConsolidated);

        if (isConsolidated) {
            attributeList.add("'" + Constant.CONSOLIDATED_SUB_ACCOUNT_NUMBER + "'");
            attributeList.add("'" + Constant.CONSOLIDATED_SUB_OBJECT_CODE + "'");
        }
        return attributeList;
    }

    // define the return attribute list for base funds query
    protected List<String> getAttributeListForBaseFunds(boolean isConsolidated, boolean isAttributeNameNeeded) {
        List<String> attributeList = getAttributeList(isConsolidated);

        if (!isAttributeNameNeeded) {
            attributeList.add(ConsolidationUtil.sum(KFSPropertyConstants.ACCOUNTING_LINE_ANNUAL_BALANCE_AMOUNT));
            attributeList.add(ConsolidationUtil.sum(KFSPropertyConstants.FINANCIAL_BEGINNING_BALANCE_LINE_AMOUNT));
            attributeList.add(ConsolidationUtil.sum(KFSPropertyConstants.CONTRACTS_GRANTS_BEGINNING_BALANCE_AMOUNT));
        }
        else {
            attributeList.add(KFSPropertyConstants.ACCOUNTING_LINE_ANNUAL_BALANCE_AMOUNT);
            attributeList.add(KFSPropertyConstants.FINANCIAL_BEGINNING_BALANCE_LINE_AMOUNT);
            attributeList.add(KFSPropertyConstants.CONTRACTS_GRANTS_BEGINNING_BALANCE_AMOUNT);
        }
        return attributeList;
    }
}

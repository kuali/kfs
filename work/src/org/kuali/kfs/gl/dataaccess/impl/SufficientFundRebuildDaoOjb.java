/*
 * Copyright 2006 The Kuali Foundation.
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

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.gl.bo.SufficientFundRebuild;
import org.kuali.module.gl.dao.SufficientFundRebuildDao;

public class SufficientFundRebuildDaoOjb extends PlatformAwareDaoBaseOjb implements SufficientFundRebuildDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SufficientFundRebuildDaoOjb.class);

    public SufficientFundRebuildDaoOjb() {
        super();
    }

    public Collection getAll() {
        QueryByCriteria qbc = QueryFactory.newQuery(SufficientFundRebuild.class, (Criteria) null);
        qbc.addOrderByAscending(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        qbc.addOrderByAscending(KFSPropertyConstants.ACCOUNT_FINANCIAL_OBJECT_TYPE_CODE);
        qbc.addOrderByAscending(KFSPropertyConstants.ACCOUNT_NUMBER_FINANCIAL_OBJECT_CODE);

        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    public Collection getByType(String accountFinancialObjectTypeCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.ACCOUNT_FINANCIAL_OBJECT_TYPE_CODE, accountFinancialObjectTypeCode);

        QueryByCriteria qbc = QueryFactory.newQuery(SufficientFundRebuild.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    public SufficientFundRebuild getByAccount(String chartOfAccountsCode, String accountNumberFinancialObjectCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        criteria.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER_FINANCIAL_OBJECT_CODE, accountNumberFinancialObjectCode);

        QueryByCriteria qbc = QueryFactory.newQuery(SufficientFundRebuild.class, criteria);
        return (SufficientFundRebuild) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

    public SufficientFundRebuild get(String chartOfAccountsCode, String accountFinancialObjectTypeCode, String accountNumberFinancialObjectCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        criteria.addEqualTo(KFSPropertyConstants.ACCOUNT_FINANCIAL_OBJECT_TYPE_CODE, accountFinancialObjectTypeCode);
        criteria.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER_FINANCIAL_OBJECT_CODE, accountNumberFinancialObjectCode);

        QueryByCriteria qbc = QueryFactory.newQuery(SufficientFundRebuild.class, criteria);
        return (SufficientFundRebuild) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

    public void save(SufficientFundRebuild sfrb) {
        LOG.debug("save() started");
        getPersistenceBrokerTemplate().store(sfrb);
    }

    public void delete(SufficientFundRebuild sfrb) {
        getPersistenceBrokerTemplate().delete(sfrb);
    }

    /**
     * This method should only be used in unit tests. It loads all the gl_sf_rebuild_t rows in memory into a collection. This won't
     * sace for production.
     * 
     * @return
     */
    public Collection testingGetAllEntries() {
        LOG.debug("testingGetAllEntries() started");

        Criteria criteria = new Criteria();
        QueryByCriteria qbc = QueryFactory.newQuery(SufficientFundRebuild.class, criteria);
        qbc.addOrderByAscending(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        qbc.addOrderByAscending(KFSPropertyConstants.ACCOUNT_FINANCIAL_OBJECT_TYPE_CODE);
        qbc.addOrderByAscending(KFSPropertyConstants.ACCOUNT_NUMBER_FINANCIAL_OBJECT_CODE);
        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

}

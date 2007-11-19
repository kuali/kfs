/*
 * Copyright 2006-2007 The Kuali Foundation.
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

/**
 * An OJB implementation of the SufficientFundRebuildDao
 */
public class SufficientFundRebuildDaoOjb extends PlatformAwareDaoBaseOjb implements SufficientFundRebuildDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SufficientFundRebuildDaoOjb.class);

    /**
     * Constructs a SufficientFundRebuildDaoOjb instance
     */
    public SufficientFundRebuildDaoOjb() {
        super();
    }

    /**
     * Returns all sufficient fund rebuild balances in the database
     * 
     * @return a Collection with all sufficient fund rebuild balances
     * @see org.kuali.module.gl.dao.SufficientFundRebuildDao#getAll()
     */
    public Collection getAll() {
        QueryByCriteria qbc = QueryFactory.newQuery(SufficientFundRebuild.class, (Criteria) null);
        qbc.addOrderByAscending(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        qbc.addOrderByAscending(KFSPropertyConstants.ACCOUNT_FINANCIAL_OBJECT_TYPE_CODE);
        qbc.addOrderByAscending(KFSPropertyConstants.ACCOUNT_NUMBER_FINANCIAL_OBJECT_CODE);

        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    /**
     * Returns all sufficient fund rebuild balances with a given object type code
     * 
     * @param accountFinancialObjectTypeCode the object type code of sufficient fund balances to return
     * @return a Collection of qualifying sufficient fund balances
     * @see org.kuali.module.gl.dao.SufficientFundRebuildDao#getByType(java.lang.String)
     */
    public Collection getByType(String accountFinancialObjectTypeCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.ACCOUNT_FINANCIAL_OBJECT_TYPE_CODE, accountFinancialObjectTypeCode);

        QueryByCriteria qbc = QueryFactory.newQuery(SufficientFundRebuild.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    /**
     * Returns the sufficient fund rebuild balance by chart and account number/object code
     * 
     * @param chartOfAccountsCode the chart of the rebuild balance to return
     * @param accountNumberFinancialObjectCode the account number or object code of the rebuild balance to returnd
     * @return a qualifying sufficient fund rebuild record if found in the database, or null
     * @see org.kuali.module.gl.dao.SufficientFundRebuildDao#getByAccount(java.lang.String, java.lang.String)
     */
    public SufficientFundRebuild getByAccount(String chartOfAccountsCode, String accountNumberFinancialObjectCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        criteria.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER_FINANCIAL_OBJECT_CODE, accountNumberFinancialObjectCode);

        QueryByCriteria qbc = QueryFactory.newQuery(SufficientFundRebuild.class, criteria);
        return (SufficientFundRebuild) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

    /**
     * Returns the sufficient fund rebuild balance with the primary key given by the parameters 
     * 
     * @param chartOfAccountsCode the chart of the rebuild balance to return
     * @param accountFinancialObjectTypeCode the object type code of the rebuild balance to return
     * @param accountNumberFinancialObjectCode the account number or fiscal object of the rebuild balance to return
     * @return the qualifying rebuild balance, or null if not found in the database
     * @see org.kuali.module.gl.dao.SufficientFundRebuildDao#get(java.lang.String, java.lang.String, java.lang.String)
     */
    public SufficientFundRebuild get(String chartOfAccountsCode, String accountFinancialObjectTypeCode, String accountNumberFinancialObjectCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        criteria.addEqualTo(KFSPropertyConstants.ACCOUNT_FINANCIAL_OBJECT_TYPE_CODE, accountFinancialObjectTypeCode);
        criteria.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER_FINANCIAL_OBJECT_CODE, accountNumberFinancialObjectCode);

        QueryByCriteria qbc = QueryFactory.newQuery(SufficientFundRebuild.class, criteria);
        return (SufficientFundRebuild) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

    /**
     * Saves a sufficient fund rebuild record to the database
     * 
     * @param sfrb the sufficient fund rebuild balance to save
     * @see org.kuali.module.gl.dao.SufficientFundRebuildDao#save(org.kuali.module.gl.bo.SufficientFundRebuild)
     */
    public void save(SufficientFundRebuild sfrb) {
        LOG.debug("save() started");
        getPersistenceBrokerTemplate().store(sfrb);
    }

    /**
     * Deletes a sufficient fund rebuild record from the database
     * @param sfrb the sufficient fund rebuild balance to delete
     * @see org.kuali.module.gl.dao.SufficientFundRebuildDao#delete(org.kuali.module.gl.bo.SufficientFundRebuild)
     */
    public void delete(SufficientFundRebuild sfrb) {
        getPersistenceBrokerTemplate().delete(sfrb);
    }

    /**
     * This method should only be used in unit tests. It loads all the gl_sf_rebuild_t rows in memory into a collection. This won't
     * sace for production.
     * 
     * Note, though, we're warned not to use this method outside unit tests but running the getAll() method, which
     * is basically equivalent, is just fine.  So you can probably run this one too.
     * 
     * @return a Collection of all sufficient fund rebuild records currently in the database
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

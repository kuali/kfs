package org.kuali.module.gl.dao.ojb;

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.PropertyConstants;
import org.kuali.module.gl.bo.SufficientFundRebuild;
import org.kuali.module.gl.dao.SufficientFundRebuildDao;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

public class SufficientFundRebuildDaoOjb extends PersistenceBrokerDaoSupport implements SufficientFundRebuildDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SufficientFundRebuildDaoOjb.class);

    public SufficientFundRebuildDaoOjb() {
        super();
    }

    public Collection getAll() {
        QueryByCriteria qbc = QueryFactory.newQuery(SufficientFundRebuild.class, (Criteria) null);
        qbc.addOrderByAscending(PropertyConstants.CHART_OF_ACCOUNTS_CODE);
        qbc.addOrderByAscending(PropertyConstants.ACCOUNT_FINANCIAL_OBJECT_TYPE_CODE);
        qbc.addOrderByAscending(PropertyConstants.ACCOUNT_NUMBER_FINANCIAL_OBJECT_CODE);

        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    public Collection getByType(String accountFinancialObjectTypeCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PropertyConstants.ACCOUNT_FINANCIAL_OBJECT_TYPE_CODE, accountFinancialObjectTypeCode);

        QueryByCriteria qbc = QueryFactory.newQuery(SufficientFundRebuild.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    public SufficientFundRebuild getByAccount(String chartOfAccountsCode, String accountNumberFinancialObjectCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        criteria.addEqualTo(PropertyConstants.ACCOUNT_NUMBER_FINANCIAL_OBJECT_CODE, accountNumberFinancialObjectCode);

        QueryByCriteria qbc = QueryFactory.newQuery(SufficientFundRebuild.class, criteria);
        return (SufficientFundRebuild) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

    public SufficientFundRebuild get(String chartOfAccountsCode, String accountFinancialObjectTypeCode, String accountNumberFinancialObjectCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        criteria.addEqualTo(PropertyConstants.ACCOUNT_FINANCIAL_OBJECT_TYPE_CODE, accountFinancialObjectTypeCode);
        criteria.addEqualTo(PropertyConstants.ACCOUNT_NUMBER_FINANCIAL_OBJECT_CODE, accountNumberFinancialObjectCode);

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
        qbc.addOrderByAscending(PropertyConstants.CHART_OF_ACCOUNTS_CODE);
        qbc.addOrderByAscending(PropertyConstants.ACCOUNT_FINANCIAL_OBJECT_TYPE_CODE);
        qbc.addOrderByAscending(PropertyConstants.ACCOUNT_NUMBER_FINANCIAL_OBJECT_CODE);
        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

}

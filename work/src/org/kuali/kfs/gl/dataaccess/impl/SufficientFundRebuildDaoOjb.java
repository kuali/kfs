package org.kuali.module.gl.dao.ojb;

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.module.gl.bo.SufficientFundRebuild;
import org.kuali.module.gl.dao.SufficientFundRebuildDao;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

public class SufficientFundRebuildDaoOjb extends PersistenceBrokerDaoSupport implements SufficientFundRebuildDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SufficientFundRebuildDaoOjb.class);

    public SufficientFundRebuildDaoOjb() {
        super();
    }

    public Collection getAll() {
        QueryByCriteria qbc = QueryFactory.newQuery(SufficientFundRebuild.class, (Criteria)null);
        qbc.addOrderByAscending("chartOfAccountsCode");
        qbc.addOrderByAscending("accountFinancialObjectTypeCode");       
        qbc.addOrderByAscending("accountNumberFinancialObjectCode");        
     
        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    public SufficientFundRebuild get(String chartOfAccountsCode, String accountObjectIdentityCode, String AccountNumberObjectCode) {
        QueryByCriteria qbc = QueryFactory.newQuery(SufficientFundRebuild.class, (Criteria)null);
     //todo: add criteria!!!
        return (SufficientFundRebuild) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }    
    
    public void save(SufficientFundRebuild sfrb) {
        LOG.debug("save() started");
        getPersistenceBrokerTemplate().store(sfrb);
}

    public void delete(SufficientFundRebuild sfrb) {
        getPersistenceBrokerTemplate().delete(sfrb);
    }
    
}

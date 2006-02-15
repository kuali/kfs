package org.kuali.module.gl.dao.ojb;

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.module.gl.bo.SufficientFundRebuild;
import org.kuali.module.gl.dao.SufficientFundRebuildDao;
import org.springframework.orm.ojb.PersistenceBrokerTemplate;

public class SufficientFundRebuildDaoOjb extends PersistenceBrokerTemplate implements SufficientFundRebuildDao {

    public Collection getAll() {
        QueryByCriteria qbc = QueryFactory.newQuery(SufficientFundRebuild.class, (Criteria)null);
        qbc.addOrderByAscending("chartOfAccountsCode");
        qbc.addOrderByAscending("accountFinancialObjectTypeCode");       
        qbc.addOrderByAscending("accountNumberFinancialObjectCode");        
     
        return getCollectionByQuery(qbc);
    }    
    
    
}

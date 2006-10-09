/*
 * Created on Oct 14, 2005
 *
 */
package org.kuali.module.chart.dao.ojb;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.dao.ObjectTypeDao;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * 
 * 
 */
public class ObjectTypeDaoOjb extends PersistenceBrokerDaoSupport implements ObjectTypeDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ObjectTypeDaoOjb.class);

    /**
     * 
     */
    public ObjectTypeDaoOjb() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.chart.dao.ObjectTypeDao#getByPrimaryKey(java.lang.String)
     */
    public ObjectType getByPrimaryKey(String code) {
        LOG.debug("getByPrimaryKey() started");

        Criteria crit = new Criteria();
        crit.addEqualTo("code", code);

        QueryByCriteria qbc = QueryFactory.newQuery(ObjectType.class, crit);

        return (ObjectType) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

}

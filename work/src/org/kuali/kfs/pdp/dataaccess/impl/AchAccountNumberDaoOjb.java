/*
 * Created on Aug 19, 2004
 */
package org.kuali.module.pdp.dao.ojb;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.pdp.bo.AchAccountNumber;
import org.kuali.module.pdp.dao.AchAccountNumberDao;


/**
 * @author HSTAPLET
 */
public class AchAccountNumberDaoOjb extends PlatformAwareDaoBaseOjb implements AchAccountNumberDao {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AchAccountNumberDaoOjb.class);

  public AchAccountNumber get(Integer id) {
    LOG.debug("get(id) started");
    
    Criteria criteria = new Criteria();
    criteria.addEqualTo("id",id);
  
    return (AchAccountNumber)getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(AchAccountNumber.class,criteria));
  }

  public void delete(AchAccountNumber achAccountNumber) {
    LOG.debug("delete() enter method");
    getPersistenceBrokerTemplate().delete(achAccountNumber);
  }
}

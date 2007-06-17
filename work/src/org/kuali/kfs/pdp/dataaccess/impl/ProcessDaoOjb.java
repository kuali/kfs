/*
 * Created on Aug 19, 2004
 *
 */
package org.kuali.module.pdp.dao.ojb;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.UniversalUserService;
import org.kuali.module.pdp.bo.PaymentProcess;
import org.kuali.module.pdp.bo.PdpUser;
import org.kuali.module.pdp.bo.UserRequired;
import org.kuali.module.pdp.dao.ProcessDao;


/**
 * @author jsissom
 *
 */
public class ProcessDaoOjb extends PlatformAwareDaoBaseOjb implements ProcessDao {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcessDaoOjb.class);

  private UniversalUserService userService;

  public void setUniversalUserService(UniversalUserService u) {
    userService = u;
  }

  public ProcessDaoOjb() {
    super();
  }

  public PaymentProcess createProcess(String campusCd, PdpUser processUser) {
    LOG.debug("createProcess() started");

    Date d = new Date();
    Timestamp now = new Timestamp(d.getTime());

    PaymentProcess p = new PaymentProcess();
    p.setCampus(campusCd);
    p.setProcessUser(processUser);
    p.setProcessTimestamp(now);

    getPersistenceBrokerTemplate().store(p);

    return p;
  }

  public PaymentProcess get(Integer procId) {
    LOG.debug("get() started");

    Criteria c = new Criteria();
    c.addEqualTo("id",procId);

    PaymentProcess p = (PaymentProcess)getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(PaymentProcess.class,c));
    if ( p != null ) {
      updateProcessUser(p);
      return p;
    } else {
      return null;
    }
  }
  public List getMostCurrentProcesses(Integer number) {
    LOG.debug("get() started");

    Criteria c = new Criteria();
    GregorianCalendar gc = new GregorianCalendar();
    gc.setTime(new Date());
    gc.add(Calendar.MONTH,-4);
    c.addGreaterOrEqualThan("processTimestamp",new Timestamp(gc.getTimeInMillis()));
    QueryByCriteria qbc = new QueryByCriteria(PaymentProcess.class,c);
    qbc.setEndAtIndex(number.intValue());
    qbc.addOrderByDescending("processTimestamp");
    List l = (List)getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    updateProcessUser(l);

    return l;
  }
  
  private void updateProcessUser(List l) {
    for (Iterator iter = l.iterator(); iter.hasNext();) {
      updateProcessUser( (PaymentProcess)iter.next() );
    }
  }
  
  private void updateProcessUser(PaymentProcess b) {
      UserRequired ur = (UserRequired)b;
      try {
          ur.updateUser(userService);
      } catch (UserNotFoundException e) {
          b.setProcessUser(null);
      }
  }
}

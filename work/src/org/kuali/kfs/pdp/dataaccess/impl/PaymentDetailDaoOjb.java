/*
 * Created on Aug 11, 2004
 *
 */
package org.kuali.module.pdp.dao.ojb;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.UniversalUserService;
import org.kuali.module.pdp.bo.Batch;
import org.kuali.module.pdp.bo.CustomerProfile;
import org.kuali.module.pdp.bo.DisbursementNumberRange;
import org.kuali.module.pdp.bo.PaymentDetail;
import org.kuali.module.pdp.bo.PaymentGroupHistory;
import org.kuali.module.pdp.bo.PaymentProcess;
import org.kuali.module.pdp.bo.UserRequired;
import org.kuali.module.pdp.dao.PaymentDetailDao;
import org.kuali.module.pdp.service.ReferenceService;


/**
 * @author delyea
 *
 */
public class PaymentDetailDaoOjb extends PlatformAwareDaoBaseOjb implements PaymentDetailDao {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentDetailDaoOjb.class);
  private UniversalUserService userService;
  private ReferenceService referenceService;

  public PaymentDetailDaoOjb() {
    super();
  }

  public void setUniversalUserService(UniversalUserService us) {
    userService = us;
  }

  public void setReferenceService(ReferenceService ref) {
    referenceService = ref;
  }

  public PaymentDetail get(Integer id) {
    LOG.debug("get(id) started");
    List data = new ArrayList();

    Criteria criteria = new Criteria();
    criteria.addEqualTo("id", id);

    PaymentDetail cp = (PaymentDetail) getPersistenceBrokerTemplate().getObjectByQuery(
        new QueryByCriteria(PaymentDetail.class, criteria));

    if (cp.getPaymentGroup().getBatch() != null) {
      updateBatchUser(cp.getPaymentGroup().getBatch());
    }
    if (cp.getPaymentGroup().getProcess() != null) {
      updateProcessUser(cp.getPaymentGroup().getProcess());
    }
    if (cp.getPaymentGroup().getPaymentGroupHistory() != null) {
      updateChangeUser(cp.getPaymentGroup().getPaymentGroupHistory());
    }
    return cp;
  }
  
  public void save(PaymentDetail pd) {
    LOG.debug("save(paymentDetail) started... ID: " + pd.getId());

    getPersistenceBrokerTemplate().store(pd);
  }
  
  public PaymentDetail getDetailForEpic(String custPaymentDocNbr, String fdocTypeCode) {
    LOG.debug("getDetailForEpic(custPaymentDocNbr, fdocTypeCode) started");
    List data = new ArrayList();

    Criteria criteria = new Criteria();
    criteria.addEqualTo("custPaymentDocNbr", custPaymentDocNbr);
    criteria.addEqualTo("financialDocumentTypeCode", fdocTypeCode);
    criteria.addEqualTo("paymentGroup.batch.customerProfile.orgCode",CustomerProfile.EPIC_ORG_CODE);
    criteria.addEqualTo("paymentGroup.batch.customerProfile.subUnitCode",CustomerProfile.EPIC_SUB_UNIT_CODE);

    List paymentDetails = (List) getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(PaymentDetail.class, criteria));
    PaymentDetail cp = null;
    for (Iterator iter = paymentDetails.iterator(); iter.hasNext();) {
      PaymentDetail pd = (PaymentDetail) iter.next();
      if (cp == null) {
        cp = pd;
      } else {
        if ( (pd.getPaymentGroup().getBatch().getCustomerFileCreateTimestamp().compareTo(
            cp.getPaymentGroup().getBatch().getCustomerFileCreateTimestamp())) > 0 ) {
          cp = pd;
        }
      }
    }
    
    if (cp != null) {
      if (cp.getPaymentGroup().getBatch() != null) {
        updateBatchUser(cp.getPaymentGroup().getBatch());
      }
      if (cp.getPaymentGroup().getProcess() != null) {
        updateProcessUser(cp.getPaymentGroup().getProcess());
      }
      if (cp.getPaymentGroup().getPaymentGroupHistory() != null) {
        updateChangeUser(cp.getPaymentGroup().getPaymentGroupHistory());
      }
    }
    return cp;
  }
  
  private void updateChangeUser(List l) {
    for (Iterator iter = l.iterator(); iter.hasNext();) {
      updateChangeUser( (PaymentGroupHistory)iter.next() );
    }
  }
  
  private void updateChangeUser(PaymentGroupHistory b) {
      UserRequired ur = (UserRequired)b;
      try {
        ur.updateUser(userService);
      } catch (UserNotFoundException e) {
        b.setChangeUser(null);
      }
  }

  private void updateBatchUser(Batch b) {
      UserRequired ur = (UserRequired)b;
      try {
        ur.updateUser(userService);
      } catch (UserNotFoundException e) {
        b.setSubmiterUser(null);
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

  public List getDisbursementNumberRanges(String campus) {
    LOG.debug("getDisbursementNumberRanges() started");

    Date now = new Date();
    Timestamp nowTs = new Timestamp(now.getTime());

    Criteria criteria = new Criteria();
    criteria.addGreaterOrEqualThan("disbNbrExpirationDt",nowTs);
    criteria.addLessOrEqualThan("disbNbrEffectiveDt",nowTs);
    criteria.addEqualTo("physCampusProcCode",campus);

    QueryByCriteria qbc = new QueryByCriteria(DisbursementNumberRange.class,criteria);
    qbc.addOrderBy("bankId",true);

    List l = (List)getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    updateDnr(l);
    return l;
  }

  private void updateDnr(List l) {
    for (Iterator iter = l.iterator(); iter.hasNext();) {
      updateDnr( (DisbursementNumberRange)iter.next() );
    }
  }

  private void updateDnr(DisbursementNumberRange b) {
      UserRequired ur = (UserRequired)b;
      try {
        ur.updateUser(userService);
      } catch (UserNotFoundException e) {
        b.setLastUpdateUser(null);
      }
  }

  public void saveDisbursementNumberRange(DisbursementNumberRange range) {
    LOG.debug("saveDisbursementNumberRange() started");

    getPersistenceBrokerTemplate().store(range);
  }
}
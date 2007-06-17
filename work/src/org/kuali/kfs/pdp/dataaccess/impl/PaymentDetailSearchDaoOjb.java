/*
 * Created on Aug 7, 2004
 *
 */
package org.kuali.module.pdp.dao.ojb;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.UniversalUserService;
import org.kuali.module.pdp.bo.Batch;
import org.kuali.module.pdp.bo.PaymentDetail;
import org.kuali.module.pdp.bo.PaymentDetailSearch;
import org.kuali.module.pdp.bo.PaymentGroupHistory;
import org.kuali.module.pdp.bo.PaymentProcess;
import org.kuali.module.pdp.bo.UserRequired;
import org.kuali.module.pdp.dao.PaymentDetailSearchDao;
import org.kuali.module.pdp.utilities.GeneralUtilities;


/**
 * @author jsissom
 *
 */
public class PaymentDetailSearchDaoOjb extends PlatformAwareDaoBaseOjb implements PaymentDetailSearchDao {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentDetailSearchDaoOjb.class);
  private UniversalUserService userService;

  // TODO hard code
  private static String HELD_TAX_ALL = "HTXA";
  
  public PaymentDetailSearchDaoOjb() {
    super();
  }

  public void setUniversalUserService(UniversalUserService us) {
    userService = us;
  }

  public List getAllPaymentsForSearchCriteria(PaymentDetailSearch pds, int pdsl) {
    LOG.info("getAllPaymentsForSearchCriteria(PaymentDetailSearch) starting");
    Criteria criteria = new Criteria();
    
    if ((!(pds.getCustPaymentDocNbr() == null)) && (!(pds.getCustPaymentDocNbr().equals("")))) {
      criteria.addLike("custPaymentDocNbr","%" + pds.getCustPaymentDocNbr() + "%");
    }
    if ((!(pds.getInvoiceNbr() == null)) && (!(pds.getInvoiceNbr().equals("")))) {
      criteria.addLike("invoiceNbr","%" + pds.getInvoiceNbr() + "%");
    }
    if ((!(pds.getRequisitionNbr() == null)) && (!(pds.getRequisitionNbr().equals("")))) {
      criteria.addLike("requisitionNbr","%" + pds.getRequisitionNbr() + "%");
    }
    if ((!(pds.getPurchaseOrderNbr() == null)) && (!(pds.getPurchaseOrderNbr().equals("")))) {
      criteria.addLike("purchaseOrderNbr","%" + pds.getPurchaseOrderNbr() + "%");
    }
    if ((!(pds.getIuIdForCustomer() == null)) && (!(pds.getIuIdForCustomer().equals("")))) {
      criteria.addLike("paymentGroup.customerIuNbr","%" + pds.getIuIdForCustomer() + "%");
    }      
    if ((!(pds.getPayeeName() == null)) && (!(pds.getPayeeName().equals("")))) {
      criteria.addLike("paymentGroup.payeeName","%" + pds.getPayeeName().toUpperCase() + "%");
    }
    if ((!(pds.getPayeeId() == null)) && (!(pds.getPayeeId().equals("")))) {
      criteria.addEqualTo("paymentGroup.payeeId",pds.getPayeeId());
    }      
    if ((!(pds.getPayeeIdTypeCd() == null)) && (!(pds.getPayeeIdTypeCd().equals("")))) {
      criteria.addLike("paymentGroup.payeeIdTypeCd","%" + pds.getPayeeIdTypeCd() + "%");
    }      
    if (!(GeneralUtilities.isStringEmpty(pds.getPymtAttachment()))) {
      criteria.addLike("paymentGroup.pymtAttachment","%" + pds.getPymtAttachment() + "%");
    }
    if (!(GeneralUtilities.isStringEmpty(pds.getPymtSpecialHandling()))) {
      criteria.addLike("paymentGroup.pymtSpecialHandling","%" + pds.getPymtSpecialHandling() + "%");
    }
    if (!(GeneralUtilities.isStringEmpty(pds.getProcessImmediate()))) {
      criteria.addLike("paymentGroup.processImmediate","%" + pds.getProcessImmediate() + "%");
    }
    if ((!(pds.getDisbursementNbr() == null)) && (!(pds.getDisbursementNbr() == new Integer(0)))) {
      criteria.addEqualTo("paymentGroup.disbursementNbr",pds.getDisbursementNbr());
    }
    if ((!(pds.getProcessId() == null)) && (!(pds.getProcessId() == new Integer(0)))) {
      criteria.addEqualTo("paymentGroup.process.id",pds.getProcessId());
    }      
    if ((!(pds.getPaymentId() == null)) && (!(pds.getPaymentId() == new Integer(0)))) {
      criteria.addEqualTo("id",pds.getPaymentId());
    }      
    if ((!(pds.getNetPaymentAmount() == null)) && (!(pds.getNetPaymentAmount() == new BigDecimal(0.00)))) {
      criteria.addEqualTo("netPaymentAmount",pds.getNetPaymentAmount());
    }
    if (!(pds.getBeginDisbursementDate() == null)) {
      criteria.addGreaterOrEqualThan("paymentGroup.disbursementDate",pds.getBeginDisbursementDate());
    }      
    if (!(pds.getEndDisbursementDate() == null)) {
      GregorianCalendar gc = new GregorianCalendar();
      gc.setTime(pds.getEndDisbursementDate());
      gc.add(Calendar.DATE,1);
      criteria.addLessOrEqualThan("paymentGroup.disbursementDate",new Timestamp(gc.getTimeInMillis()));
    }
    if (!(pds.getBeginPaymentDate() == null)) {
      criteria.addGreaterOrEqualThan("paymentGroup.paymentDate",pds.getBeginPaymentDate());
    }      
    if (!(pds.getEndPaymentDate() == null)) {
      GregorianCalendar gc = new GregorianCalendar();
      gc.setTime(pds.getEndPaymentDate());
      gc.add(Calendar.DATE,1);
      criteria.addLessOrEqualThan("paymentGroup.paymentDate",new Timestamp(gc.getTimeInMillis()));
    }
    if ((!(pds.getPaymentStatusCode() == null)) && (!(pds.getPaymentStatusCode().equals("")))) {
      if (HELD_TAX_ALL.equals(pds.getPaymentStatusCode())){
        criteria.addLike("paymentGroup.paymentStatus.code","HTX%");
      } else {
        criteria.addEqualTo("paymentGroup.paymentStatus.code",pds.getPaymentStatusCode());
      }
    }      
    if ((!(pds.getDisbursementTypeCode() == null)) && (!(pds.getDisbursementTypeCode().equals("")))) {
      criteria.addEqualTo("paymentGroup.disbursementType.code",pds.getDisbursementTypeCode());
    }
    if ((!(pds.getChartCode() == null)) && (!(pds.getChartCode().equals("")))) {
      criteria.addLike("paymentGroup.batch.customerProfile.chartCode","%" + pds.getChartCode() + "%");
    }
    if ((!(pds.getOrgCode() == null)) && (!(pds.getOrgCode().equals("")))) {
      criteria.addLike("paymentGroup.batch.customerProfile.orgCode","%" + pds.getOrgCode() + "%");
    }      
    if ((!(pds.getSubUnitCode() == null)) && (!(pds.getSubUnitCode().equals("")))) {
      criteria.addLike("paymentGroup.batch.customerProfile.subUnitCode","%" + pds.getSubUnitCode() + "%");
    }

    QueryByCriteria qbc = new QueryByCriteria(PaymentDetail.class,criteria);
    qbc.addOrderBy("paymentGroup.payeeName",true);
    int total = pdsl + 1;
    qbc.setEndAtIndex(total);
    LOG.debug("getAllPaymentsForSearchCriteria() Query = " + qbc.toString());
    List l = (List)getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    updateUser(l);

    return l;

  }
  
  public List getAllPaymentsWithCancelReissueDisbNbr(PaymentDetailSearch pds) {
    LOG.info("getAllPaymentsWithCancelReissueDisbNbr(PaymentDetailSearch) starting");
    Criteria criteria = new Criteria();
    
//    if ((!(pds.getCustPaymentDocNbr() == null)) && (!(pds.getCustPaymentDocNbr().equals("")))) {
//      criteria.addLike("custPaymentDocNbr","%" + pds.getCustPaymentDocNbr() + "%");
//    }
//    if ((!(pds.getInvoiceNbr() == null)) && (!(pds.getInvoiceNbr().equals("")))) {
//      criteria.addLike("invoiceNbr","%" + pds.getInvoiceNbr() + "%");
//    }
//    if ((!(pds.getRequisitionNbr() == null)) && (!(pds.getRequisitionNbr().equals("")))) {
//      criteria.addLike("requisitionNbr","%" + pds.getRequisitionNbr() + "%");
//    }
//    if ((!(pds.getPurchaseOrderNbr() == null)) && (!(pds.getPurchaseOrderNbr().equals("")))) {
//      criteria.addLike("purchaseOrderNbr","%" + pds.getPurchaseOrderNbr() + "%");
//    }
    if ((!(pds.getIuIdForCustomer() == null)) && (!(pds.getIuIdForCustomer().equals("")))) {
      criteria.addLike("paymentGroup.customerIuNbr","%" + pds.getIuIdForCustomer() + "%");
    }      
    if ((!(pds.getPayeeName() == null)) && (!(pds.getPayeeName().equals("")))) {
      criteria.addLike("paymentGroup.payeeName","%" + pds.getPayeeName().toUpperCase() + "%");
    }
    if ((!(pds.getPayeeId() == null)) && (!(pds.getPayeeId().equals("")))) {
      criteria.addEqualTo("paymentGroup.payeeId",pds.getPayeeId());
    }      
    if ((!(pds.getPayeeIdTypeCd() == null)) && (!(pds.getPayeeIdTypeCd().equals("")))) {
      criteria.addLike("paymentGroup.payeeIdTypeCd","%" + pds.getPayeeIdTypeCd() + "%");
    }      
    if (!(GeneralUtilities.isStringEmpty(pds.getPymtAttachment()))) {
      criteria.addLike("paymentGroup.pymtAttachment","%" + pds.getPymtAttachment() + "%");
    }
    if (!(GeneralUtilities.isStringEmpty(pds.getPymtSpecialHandling()))) {
      criteria.addLike("origPmtSpecHandling","%" + pds.getPymtSpecialHandling() + "%");
    }
    if (!(GeneralUtilities.isStringEmpty(pds.getProcessImmediate()))) {
      criteria.addLike("origProcessImmediate","%" + pds.getProcessImmediate() + "%");
    }
    if ((!(pds.getDisbursementNbr() == null)) && (!(pds.getDisbursementNbr() == new Integer(0)))) {
      criteria.addEqualTo("origDisburseNbr",pds.getDisbursementNbr());
    }
    if ((!(pds.getProcessId() == null)) && (!(pds.getProcessId() == new Integer(0)))) {
      criteria.addEqualTo("paymentProcess.id",pds.getProcessId());
    }      
//    if ((!(pds.getPaymentId() == null)) && (!(pds.getPaymentId() == new Integer(0)))) {
//      criteria.addEqualTo("id",pds.getPaymentId());
//    }      
    if ((!(pds.getNetPaymentAmount() == null)) && (!(pds.getNetPaymentAmount() == new BigDecimal(0.00)))) {
      criteria.addEqualTo("paymentGroup.paymentDetails.netPaymentAmount",pds.getNetPaymentAmount());
    }
    if (!(pds.getBeginDisbursementDate() == null)) {
      criteria.addGreaterOrEqualThan("origDisburseDate",pds.getBeginDisbursementDate());
    }      
    if (!(pds.getEndDisbursementDate() == null)) {
      GregorianCalendar gc = new GregorianCalendar();
      gc.setTime(pds.getEndDisbursementDate());
      gc.add(Calendar.DATE,1);
      criteria.addLessOrEqualThan("origDisburseDate",new Timestamp(gc.getTimeInMillis()));
    }
    if (!(pds.getBeginPaymentDate() == null)) {
      criteria.addGreaterOrEqualThan("origPaymentDate",pds.getBeginPaymentDate());
    }      
    if (!(pds.getEndPaymentDate() == null)) {
      GregorianCalendar gc = new GregorianCalendar();
      gc.setTime(pds.getEndPaymentDate());
      gc.add(Calendar.DATE,1);
      criteria.addLessOrEqualThan("origPaymentDate",new Timestamp(gc.getTimeInMillis()));
    }
    if ((!(pds.getPaymentStatusCode() == null)) && (!(pds.getPaymentStatusCode().equals("")))) {
      criteria.addEqualTo("origPaymentStatus.code",pds.getPaymentStatusCode());
    }      
    if ((!(pds.getDisbursementTypeCode() == null)) && (!(pds.getDisbursementTypeCode().equals("")))) {
      criteria.addEqualTo("disbursementType.code",pds.getDisbursementTypeCode());
    }
    if ((!(pds.getChartCode() == null)) && (!(pds.getChartCode().equals("")))) {
      criteria.addLike("paymentGroup.batch.customerProfile.chartCode","%" + pds.getChartCode() + "%");
    }
    if ((!(pds.getOrgCode() == null)) && (!(pds.getOrgCode().equals("")))) {
      criteria.addLike("paymentGroup.batch.customerProfile.orgCode","%" + pds.getOrgCode() + "%");
    }      
    if ((!(pds.getSubUnitCode() == null)) && (!(pds.getSubUnitCode().equals("")))) {
      criteria.addLike("paymentGroup.batch.customerProfile.subUnitCode","%" + pds.getSubUnitCode() + "%");
    }
    QueryByCriteria qbc = new QueryByCriteria(PaymentGroupHistory.class,criteria);
    qbc.addOrderBy("paymentGroup.payeeName",true);
    LOG.debug("getAllPaymentsWithCancelReissueDisbNbr() Query = " + qbc.toString());
    List l = (List)getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    
    List finalResults = new ArrayList();
    for (Iterator iter = l.iterator(); iter.hasNext();) {
      PaymentGroupHistory pgh = (PaymentGroupHistory) iter.next();
      finalResults.addAll(pgh.getPaymentGroup().getPaymentDetails());
    }

    return finalResults;
  }
  private void updateUser(List l) {
    for (Iterator iter = l.iterator(); iter.hasNext();) {
      PaymentDetail p = (PaymentDetail)iter.next();
      if (p.getPaymentGroup().getBatch() != null) {
        updateBatchUser(p.getPaymentGroup().getBatch());
      }
      if (p.getPaymentGroup().getProcess() != null) {
        updateProcessUser(p.getPaymentGroup().getProcess());
      }
      if (p.getPaymentGroup().getPaymentGroupHistory() != null) {
        updateChangeUser(p.getPaymentGroup().getPaymentGroupHistory());
      }
    }
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
}

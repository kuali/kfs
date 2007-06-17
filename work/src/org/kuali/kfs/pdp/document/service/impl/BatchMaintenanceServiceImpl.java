/*
 * Created on Aug 12, 2004
 */
package org.kuali.module.pdp.service.impl;

import java.util.Iterator;
import java.util.List;

import org.kuali.module.pdp.bo.Code;
import org.kuali.module.pdp.bo.PaymentChange;
import org.kuali.module.pdp.bo.PaymentGroup;
import org.kuali.module.pdp.bo.PaymentGroupHistory;
import org.kuali.module.pdp.bo.PaymentStatus;
import org.kuali.module.pdp.bo.PdpUser;
import org.kuali.module.pdp.dao.BatchMaintenanceDao;
import org.kuali.module.pdp.dao.PaymentGroupDao;
import org.kuali.module.pdp.dao.PaymentGroupHistoryDao;
import org.kuali.module.pdp.exception.CancelPaymentException;
import org.kuali.module.pdp.exception.PdpException;
import org.kuali.module.pdp.service.BatchMaintenanceService;
import org.kuali.module.pdp.service.ReferenceService;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author HSTAPLET
 */
@Transactional
public class BatchMaintenanceServiceImpl implements BatchMaintenanceService {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchMaintenanceServiceImpl.class);

  // Payment Status Codes
  private static String HELD_CD = "HELD";
  private static String OPEN_CD = "OPEN";
  private static String CANCEL_PAYMENT_CD = "CPAY";
  
  // Payment Change Codes
  private static String CANCEL_BATCH_CHNG_CD = "CB";
  private static String HOLD_BATCH_CHNG_CD = "HB";
  private static String REMOVE_HOLD_BATCH_CHNG_CD = "RHB";

  private BatchMaintenanceDao batchMaintenanceDao;
  private PaymentGroupDao paymentGroupDao;
  private PaymentGroupHistoryDao paymentGroupHistoryDao;
  private ReferenceService referenceService;

  public void changeStatus(PaymentGroup paymentGroup, String newPaymentStatus, String changeStatus, String note, PdpUser user) {
    LOG.debug("changeStatus() enter method with new status of " + newPaymentStatus);
    PaymentGroupHistory paymentGroupHistory = new PaymentGroupHistory();
    Code cd = referenceService.getCode("PaymentChange", changeStatus);
    paymentGroupHistory.setPaymentChange((PaymentChange)cd);
    paymentGroupHistory.setOrigPaymentStatus(paymentGroup.getPaymentStatus());
    paymentGroupHistory.setChangeUser(user);
    paymentGroupHistory.setChangeNoteText(note);
    paymentGroupHistory.setPaymentGroup(paymentGroup);
    paymentGroupHistoryDao.save(paymentGroupHistory);

    Code code = referenceService.getCode("PaymentStatus", newPaymentStatus);
    paymentGroup.setPaymentStatus((PaymentStatus)code);
    paymentGroupDao.save(paymentGroup);
    LOG.debug("changeStatus() Status has been changed; exit method.");
  }
  
  /**
     * cancelPendingBatch()
     *   This method cancels a pending batch by canceling each payment in the
     *   batch if the following rules apply.
     * 
     *   - All payments must have a status of 'OPEN'.
     * 
     * @param paymentBatchId  (Integer) Primary key of the Pending Batch to be canceled.
     * @param note            (String)  Change note text entered by user.
     * @param user            (User)    Actor making change.
     */
    public void cancelPendingBatch(Integer paymentBatchId, String note, PdpUser user) throws PdpException {
      LOG.debug("cancelPendingBatch() Enter method to cancel batch with id = " + paymentBatchId);
      if (doBatchPaymentsHaveOpenOrHeldStatus(paymentBatchId)) {
        List paymentGroupList = paymentGroupDao.getByBatchId(paymentBatchId);
        if (paymentGroupList == null || paymentGroupList.isEmpty()) {
          LOG.debug("cancelPendingBatch() Pending payment groups not found for batchId; throw exception.");
          throw new CancelPaymentException("Pending payment groups not found for batchId.");
        }
        
        //cancel each payment
        // All actions must be performed on entire group not individual detail record
        for (Iterator iter = paymentGroupList.iterator(); iter.hasNext(); ) {
          PaymentGroup element = (PaymentGroup) iter.next();
          changeStatus(element, CANCEL_PAYMENT_CD, CANCEL_BATCH_CHNG_CD, note, user);
        }
        LOG.debug("cancelPendingBatch() All payment groups in batch have been canceled; exit method.");
      } else {
        LOG.debug("cancelPendingBatch() Not all payment groups are open; cannot cancel batch.");
        throw new CancelPaymentException("Not all payment groups are open; cannot cancel batch.");
      }
    }//end cancelPendingBatch()
    
    /**
     * holdPendingBatch()
     *   This method holds a pending batch by holding each payment in the
     *   batch if the following rules apply.
     * 
     *   - All payments must have a status of 'OPEN'.
     * 
     * @param paymentBatchId  (Integer) Primary key of the Pending Batch to be held.
     * @param note            (String)  Change note text entered by user.
     * @param user            (User)    Actor making change.
     */
    public void holdPendingBatch(Integer paymentBatchId, String note, PdpUser user) throws PdpException {
      LOG.debug("holdPendingBatch() Enter method to hold batch with id = " + paymentBatchId);
      if (doBatchPaymentsHaveOpenStatus(paymentBatchId)) {
        List paymentGroupList = paymentGroupDao.getByBatchId(paymentBatchId);
        if (paymentGroupList == null || paymentGroupList.isEmpty()) {
          LOG.debug("holdPendingBatch() Pending payment groups not found for batchId; throw exception.");
          throw new CancelPaymentException("Pending payment groups not found for batchId.");
        }
        
        //hold each payment
        // All actions must be performed on entire group not individual detail record
        for (Iterator iter = paymentGroupList.iterator(); iter.hasNext(); ) {
          PaymentGroup element = (PaymentGroup) iter.next();
          changeStatus(element, HELD_CD, HOLD_BATCH_CHNG_CD, note, user);
        }
        LOG.debug("holdPendingBatch() All payment groups in batch have been held; exit method.");
      } else {
        LOG.debug("holdPendingBatch() Not all payment groups are open; cannot hold batch.");
        throw new CancelPaymentException("Not all payment groups are open; cannot hold batch.");
      }
    }//end holdPendingBatch()

    /**
     * removeBatchHold()
     *   This method removes holds on batches of payments if the following rules apply.
     * 
     *   - All Payments' statuses in batch must be: "held".
     * 
     * @param paymentBatchId   (Integer) Primary key of the Pending Batch to be released from hold.
     * @param note             (String)  Change note text entered by user.
     * @param user             (User)    Actor making change.
     */
    public void removeBatchHold(Integer paymentBatchId, String note, PdpUser user)  throws PdpException {
      LOG.debug("removeBatchHold() Enter method to hold batch with id = " + paymentBatchId);
      if (doBatchPaymentsHaveHeldStatus(paymentBatchId)) {
        List paymentGroupList = paymentGroupDao.getByBatchId(paymentBatchId);
        if (paymentGroupList == null || paymentGroupList.isEmpty()) {
          LOG.debug("removeBatchHold() Pending payment groups not found for batchId; throw exception.");
          throw new CancelPaymentException("Pending payment groups not found for batchId.");
      }
      
        // hold each payment
        // All actions must be performed on entire group not individual detail record
        for (Iterator iter = paymentGroupList.iterator(); iter.hasNext(); ) {
          PaymentGroup element = (PaymentGroup) iter.next();
          changeStatus(element, OPEN_CD, REMOVE_HOLD_BATCH_CHNG_CD, note, user);
        }
        LOG.debug("holdPendingBatch() All payment groups in batch have been held; exit method.");
      } else {
        LOG.debug("holdPendingBatch() Not all payment groups are open; cannot hold batch.");
        throw new CancelPaymentException("Not all payment groups are open; cannot hold batch.");
      }

    }//end removeBatchHold()
    
    public boolean doBatchPaymentsHaveOpenStatus(Integer batchId){
      return batchMaintenanceDao.doBatchPaymentsHaveOpenStatus(batchId);
    }

    public boolean doBatchPaymentsHaveHeldStatus(Integer batchId) {
      return batchMaintenanceDao.doBatchPaymentsHaveHeldStatus(batchId);
    }

    public boolean doBatchPaymentsHaveOpenOrHeldStatus(Integer batchId) {
      LOG.debug("doBatchPaymentsHaveOpenOrHeldStatus() enter method.");
      
      if ((doBatchPaymentsHaveOpenStatus(batchId)) || (doBatchPaymentsHaveHeldStatus(batchId))) {
        LOG.debug("doBatchPaymentsHaveOpenOrHeldStatus() All payment groups have status 'HELD' or all payments have status 'OPEN'.");
        return true;
      } else {
        LOG.debug("doBatchPaymentsHaveOpenOrHeldStatus() Not all payment groups have status 'HELD' or not all payments have status 'OPEN'.");
        return false;
      }
    }
    /**
     * inject
     * @param dao
     */
    public void setPaymentGroupDao(PaymentGroupDao dao) {
      paymentGroupDao = dao;
    }

    /**
     * inject
     * @param dao
     */
    public void setPaymentGroupHistoryDao(PaymentGroupHistoryDao dao) {
      paymentGroupHistoryDao = dao;
    }

    /**
     * inject
     * @param service
     */
    public void setReferenceService(ReferenceService service) {
      referenceService = service;
    }

    /**
     * @param batchMaintenanceDao The batchMaintenanceDao to set.
     */
    public void setBatchMaintenanceDao(BatchMaintenanceDao batchMaintenanceDao) {
      this.batchMaintenanceDao = batchMaintenanceDao;
    }
}

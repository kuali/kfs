/*
 * Copyright 2007 The Kuali Foundation.
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
/*
 * Created on Aug 12, 2004
 */
package org.kuali.kfs.pdp.document.service.impl;

import java.util.List;

import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.businessobject.PaymentChange;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentGroupHistory;
import org.kuali.kfs.pdp.businessobject.PaymentStatus;
import org.kuali.kfs.pdp.dataaccess.BatchMaintenanceDao;
import org.kuali.kfs.pdp.dataaccess.PaymentGroupDao;
import org.kuali.kfs.pdp.dataaccess.PaymentGroupHistoryDao;
import org.kuali.kfs.pdp.document.service.BatchMaintenanceService;
import org.kuali.kfs.pdp.exception.PdpException;
import org.kuali.kfs.sys.service.KualiCodeService;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class provides methods for Batch maintenance.
 */
@Transactional
public class BatchMaintenanceServiceImpl implements BatchMaintenanceService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchMaintenanceServiceImpl.class);

    private BatchMaintenanceDao batchMaintenanceDao;
    private PaymentGroupDao paymentGroupDao;
    private PaymentGroupHistoryDao paymentGroupHistoryDao;
    private KualiCodeService kualiCodeService;

    public void changeStatus(PaymentGroup paymentGroup, String newPaymentStatus, String changeStatus, String note, UniversalUser user) {
        LOG.debug("changeStatus() enter method with new status of " + newPaymentStatus);

        PaymentGroupHistory paymentGroupHistory = new PaymentGroupHistory();
        PaymentChange paymentChange = (PaymentChange) kualiCodeService.getByCode(PaymentChange.class, changeStatus);

        paymentGroupHistory.setPaymentChange(paymentChange);
        paymentGroupHistory.setOrigPaymentStatus(paymentGroup.getPaymentStatus());
        paymentGroupHistory.setChangeUser(user);
        paymentGroupHistory.setChangeNoteText(note);
        paymentGroupHistory.setPaymentGroup(paymentGroup);

        paymentGroupHistoryDao.save(paymentGroupHistory);

        PaymentStatus paymentStatus = (PaymentStatus) kualiCodeService.getByCode(PaymentStatus.class, newPaymentStatus);

        paymentGroup.setPaymentStatus(paymentStatus);

        paymentGroupDao.save(paymentGroup);

        LOG.debug("changeStatus() Status has been changed; exit method.");
    }

    /**
     * cancelPendingBatch() This method cancels a pending batch by canceling each payment in the batch if the following rules apply. -
     * All payments must have a status of 'OPEN'.
     * 
     * @param paymentBatchId (Integer) Primary key of the Pending Batch to be canceled.
     * @param note (String) Change note text entered by user.
     * @param user (User) Actor making change.
     */
    public void cancelPendingBatch(Integer paymentBatchId, String note, UniversalUser user) throws PdpException {
        LOG.debug("cancelPendingBatch() Enter method to cancel batch with id = " + paymentBatchId);

        if (doBatchPaymentsHaveOpenOrHeldStatus(paymentBatchId)) {
            List<PaymentGroup> paymentGroupList = paymentGroupDao.getByBatchId(paymentBatchId);

            if (paymentGroupList == null || paymentGroupList.isEmpty()) {
                LOG.debug("cancelPendingBatch() Pending payment groups not found for batchId; throw exception.");
                throw new PdpException(PdpKeyConstants.BatchConstants.ErrorMessages.ERROR_PENDING_PAYMNET_GROUP_NOT_FOUND);
            }

            // cancel each payment
            // All actions must be performed on entire group not individual detail record
            for (PaymentGroup paymentGroup : paymentGroupList) {
                changeStatus(paymentGroup, PdpConstants.PaymentStatusCodes.CANCEL_PAYMENT, PdpConstants.PaymentChangeCodes.CANCEL_BATCH_CHNG_CD, note, user);
            }

            LOG.debug("cancelPendingBatch() All payment groups in batch have been canceled; exit method.");
        }
        else {
            LOG.debug("cancelPendingBatch() Not all payment groups are open; cannot cancel batch.");

            throw new PdpException(PdpKeyConstants.BatchConstants.ErrorMessages.ERROR_NOT_ALL_PAYMENT_GROUPS_OPEN_CANNOT_CANCEL);
        }
    }

    /**
     * holdPendingBatch() This method holds a pending batch by holding each payment in the batch if the following rules apply. - All
     * payments must have a status of 'OPEN'.
     * 
     * @param paymentBatchId (Integer) Primary key of the Pending Batch to be held.
     * @param note (String) Change note text entered by user.
     * @param user (User) Actor making change.
     */
    public void holdPendingBatch(Integer paymentBatchId, String note, UniversalUser user) throws PdpException {
        LOG.debug("holdPendingBatch() Enter method to hold batch with id = " + paymentBatchId);

        if (doBatchPaymentsHaveOpenStatus(paymentBatchId)) {
            List<PaymentGroup> paymentGroupList = paymentGroupDao.getByBatchId(paymentBatchId);

            if (paymentGroupList == null || paymentGroupList.isEmpty()) {
                LOG.debug("holdPendingBatch() Pending payment groups not found for batchId; throw exception.");
                throw new PdpException(PdpKeyConstants.BatchConstants.ErrorMessages.ERROR_PENDING_PAYMNET_GROUP_NOT_FOUND);
            }

            // hold each payment
            // All actions must be performed on entire group not individual detail record
            for (PaymentGroup paymentGroup : paymentGroupList) {
                changeStatus(paymentGroup, PdpConstants.PaymentStatusCodes.HELD_CD, PdpConstants.PaymentChangeCodes.HOLD_BATCH_CHNG_CD, note, user);
            }

            LOG.debug("holdPendingBatch() All payment groups in batch have been held; exit method.");
        }
        else {
            LOG.debug("holdPendingBatch() Not all payment groups are open; cannot hold batch.");

            throw new PdpException(PdpKeyConstants.BatchConstants.ErrorMessages.ERROR_NOT_ALL_PAYMENT_GROUPS_OPEN_CANNOT_HOLD);
        }
    }

    /**
     * removeBatchHold() This method removes holds on batches of payments if the following rules apply. - All Payments' statuses in
     * batch must be: "held".
     * 
     * @param paymentBatchId (Integer) Primary key of the Pending Batch to be released from hold.
     * @param note (String) Change note text entered by user.
     * @param user (User) Actor making change.
     */
    public void removeBatchHold(Integer paymentBatchId, String note, UniversalUser user) throws PdpException {
        LOG.debug("removeBatchHold() Enter method to remove hold batch with id = " + paymentBatchId);

        if (doBatchPaymentsHaveHeldStatus(paymentBatchId)) {
            List<PaymentGroup> paymentGroupList = paymentGroupDao.getByBatchId(paymentBatchId);

            if (paymentGroupList == null || paymentGroupList.isEmpty()) {
                LOG.debug("removeBatchHold() Pending payment groups not found for batchId; throw exception.");
                throw new PdpException(PdpKeyConstants.BatchConstants.ErrorMessages.ERROR_PENDING_PAYMNET_GROUP_NOT_FOUND);
            }

            // hold each payment
            // All actions must be performed on entire group not individual detail record
            for (PaymentGroup paymentGroup : paymentGroupList) {
                changeStatus(paymentGroup, PdpConstants.PaymentStatusCodes.OPEN, PdpConstants.PaymentChangeCodes.REMOVE_HOLD_BATCH_CHNG_CD, note, user);
            }

            LOG.debug("removeBatchHold() All payment groups in batch have been held; exit method.");
        }
        else {
            LOG.debug("removeBatchHold() Not all payment groups are open; cannot remove hold on batch.");

            throw new PdpException(PdpKeyConstants.BatchConstants.ErrorMessages.ERROR_NOT_ALL_PAYMENT_GROUPS_OPEN_CANNOT_REMOVE_HOLD);
        }

    }

    /**
     * @see org.kuali.kfs.pdp.document.service.BatchMaintenanceService#doBatchPaymentsHaveOpenStatus(java.lang.Integer)
     */
    public boolean doBatchPaymentsHaveOpenStatus(Integer batchId) {
        return batchMaintenanceDao.doBatchPaymentsHaveOpenStatus(batchId);
    }

    /**
     * @see org.kuali.kfs.pdp.document.service.BatchMaintenanceService#doBatchPaymentsHaveHeldStatus(java.lang.Integer)
     */
    public boolean doBatchPaymentsHaveHeldStatus(Integer batchId) {
        return batchMaintenanceDao.doBatchPaymentsHaveHeldStatus(batchId);
    }

    /**
     * @see org.kuali.kfs.pdp.document.service.BatchMaintenanceService#doBatchPaymentsHaveOpenOrHeldStatus(java.lang.Integer)
     */
    public boolean doBatchPaymentsHaveOpenOrHeldStatus(Integer batchId) {
        LOG.debug("doBatchPaymentsHaveOpenOrHeldStatus() enter method.");

        if ((doBatchPaymentsHaveOpenStatus(batchId)) || (doBatchPaymentsHaveHeldStatus(batchId))) {
            LOG.debug("doBatchPaymentsHaveOpenOrHeldStatus() All payment groups have status 'HELD' or all payments have status 'OPEN'.");

            return true;
        }
        else {
            LOG.debug("doBatchPaymentsHaveOpenOrHeldStatus() Not all payment groups have status 'HELD' or not all payments have status 'OPEN'.");

            return false;
        }
    }

    /**
     * This method sets paymentGroupDao
     * @param dao PaymentGroupDao
     */
    public void setPaymentGroupDao(PaymentGroupDao dao) {
        paymentGroupDao = dao;
    }

    /**
     * This method sets paymentGroupHistoryDao
     * @param dao PaymentGroupHistoryDao
     */
    public void setPaymentGroupHistoryDao(PaymentGroupHistoryDao dao) {
        paymentGroupHistoryDao = dao;
    }

    /**
     * This method sets the batchMaintenanceDao
     * @param batchMaintenanceDao BatchMaintenanceDao
     */
    public void setBatchMaintenanceDao(BatchMaintenanceDao batchMaintenanceDao) {
        this.batchMaintenanceDao = batchMaintenanceDao;
    }

    /**
     * This method gets the kualiCodeService.
     * @return KualiCodeService
     */
    public KualiCodeService getKualiCodeService() {
        return kualiCodeService;
    }

    /**
     * This method sets the kualiCodeService
     * @param kualiCodeService
     */
    public void setKualiCodeService(KualiCodeService kualiCodeService) {
        this.kualiCodeService = kualiCodeService;
    }
}

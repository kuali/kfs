/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
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
package org.kuali.kfs.pdp.service.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.AchAccountNumber;
import org.kuali.kfs.pdp.businessobject.PaymentChangeCode;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentGroupHistory;
import org.kuali.kfs.pdp.businessobject.PaymentNoteText;
import org.kuali.kfs.pdp.businessobject.PaymentStatus;
import org.kuali.kfs.pdp.dataaccess.PaymentDetailDao;
import org.kuali.kfs.pdp.dataaccess.PaymentGroupDao;
import org.kuali.kfs.pdp.service.PaymentGroupService;
import org.kuali.kfs.pdp.service.PaymentMaintenanceService;
import org.kuali.kfs.pdp.service.PdpAuthorizationService;
import org.kuali.kfs.pdp.service.PdpEmailService;
import org.kuali.kfs.pdp.service.PendingTransactionService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.KualiCode;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.MailService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @see org.kuali.kfs.pdp.service.PaymentMaintenanceService
 */
@Transactional
public class PaymentMaintenanceServiceImpl implements PaymentMaintenanceService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentMaintenanceServiceImpl.class);

    private PaymentGroupDao paymentGroupDao;
    private PaymentDetailDao paymentDetailDao;
    private PendingTransactionService glPendingTransactionService;
    private MailService mailService;
    private ParameterService parameterService;
    private BankService bankService;
    private BusinessObjectService businessObjectService;
    private PaymentGroupService paymentGroupService;
    private PdpEmailService emailService;
    private PdpAuthorizationService pdpAuthorizationService;

    /**
     * This method changes status for a payment group.
     *
     * @param paymentGroup the payment group
     * @param newPaymentStatus the new payment status
     * @param changeStatus the changed payment status
     * @param note a note for payment status change
     * @param user the user that changed the status
     */
    protected void changeStatus(PaymentGroup paymentGroup, String newPaymentStatus, String changeStatus, String note, Person user) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("changeStatus() enter method with new status of " + newPaymentStatus);
        }

        PaymentGroupHistory paymentGroupHistory = new PaymentGroupHistory();
        KualiCode cd = businessObjectService.findBySinglePrimaryKey(PaymentChangeCode.class, changeStatus);
        paymentGroupHistory.setPaymentChange((PaymentChangeCode) cd);
        paymentGroupHistory.setOrigPaymentStatus(paymentGroup.getPaymentStatus());
        paymentGroupHistory.setChangeUser(user);
        paymentGroupHistory.setChangeNoteText(note);
        paymentGroupHistory.setPaymentGroup(paymentGroup);
        paymentGroupHistory.setChangeTime(new Timestamp(new Date().getTime()));

        this.businessObjectService.save(paymentGroupHistory);

        KualiCode code = businessObjectService.findBySinglePrimaryKey(PaymentStatus.class, newPaymentStatus);
        paymentGroup.setPaymentStatus((PaymentStatus) code);
        this.businessObjectService.save(paymentGroup);
        LOG.debug("changeStatus() Status has been changed; exit method.");
    }

    /**
     * This method changes the state of a paymentGroup.
     *
     * @param paymentGroup the payment group to change the state for
     * @param newPaymentStatus the new payment status
     * @param changeStatus the status that is changed
     * @param note the note entered by the user
     * @param user the user that changed the
     * @param paymentGroupHistory
     */
    protected void changeStatus(PaymentGroup paymentGroup, String newPaymentStatus, String changeStatus, String note, Person user, PaymentGroupHistory paymentGroupHistory) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("changeStatus() enter method with new status of " + newPaymentStatus);
        }

        KualiCode cd = businessObjectService.findBySinglePrimaryKey(PaymentChangeCode.class, changeStatus);
        paymentGroupHistory.setPaymentChange((PaymentChangeCode) cd);
        paymentGroupHistory.setOrigPaymentStatus(paymentGroup.getPaymentStatus());
        paymentGroupHistory.setChangeUser(user);
        paymentGroupHistory.setChangeNoteText(note);
        paymentGroupHistory.setPaymentGroup(paymentGroup);
        paymentGroupHistory.setChangeTime(new Timestamp(new Date().getTime()));

        this.businessObjectService.save(paymentGroupHistory);

        KualiCode code = businessObjectService.findBySinglePrimaryKey(PaymentStatus.class, newPaymentStatus);
        if (paymentGroup.getPaymentStatus() != ((PaymentStatus) code)) {
            paymentGroup.setPaymentStatus((PaymentStatus) code);
        }
        this.businessObjectService.save(paymentGroup);

        LOG.debug("changeStatus() Status has been changed; exit method.");
    }

    /**
     * @see org.kuali.kfs.pdp.document.service.PaymentMaintenanceService#cancelPendingPayment(java.lang.Integer, java.lang.Integer,
     *      java.lang.String, org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public boolean cancelPendingPayment(Integer paymentGroupId, Integer paymentDetailId, String note, Person user) {
        // All actions must be performed on entire group not individual detail record
        if (LOG.isDebugEnabled()) {
            LOG.debug("cancelPendingPayment() Enter method to cancel pending payment with group id = " + paymentGroupId);
            LOG.debug("cancelPendingPayment() payment detail id being cancelled = " + paymentDetailId);
        }

        PaymentGroup paymentGroup = this.paymentGroupService.get(paymentGroupId);
        if (paymentGroup == null) {
            LOG.debug("cancelPendingPayment() Pending payment not found; throw exception.");
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_NOT_FOUND);
            return false;
        }

        String paymentStatus = paymentGroup.getPaymentStatus().getCode();

        if (!(PdpConstants.PaymentStatusCodes.CANCEL_PAYMENT.equals(paymentStatus))) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("cancelPendingPayment() Payment status is " + paymentStatus + "; continue with cancel.");
            }

            if ((PdpConstants.PaymentStatusCodes.HELD_TAX_EMPLOYEE_CD.equals(paymentStatus)) || (PdpConstants.PaymentStatusCodes.HELD_TAX_NRA_CD.equals(paymentStatus)) || (PdpConstants.PaymentStatusCodes.HELD_TAX_NRA_EMPL_CD.equals(paymentStatus))) {
                if (!pdpAuthorizationService.hasRemovePaymentTaxHoldPermission(user.getPrincipalId())) {
                    LOG.warn("cancelPendingPayment() Payment status is " + paymentStatus + "; user does not have rights to cancel. This should not happen unless user is URL spoofing.");
                    throw new RuntimeException("cancelPendingPayment() Payment status is " + paymentStatus + "; user does not have rights to cancel. This should not happen unless user is URL spoofing.");
                }

                changeStatus(paymentGroup, PdpConstants.PaymentStatusCodes.CANCEL_PAYMENT, PdpConstants.PaymentChangeCodes.CANCEL_PAYMENT_CHNG_CD, note, user);

                // set primary cancel indicator for EPIC to use
                Map primaryKeys = new HashMap();
                primaryKeys.put(PdpPropertyConstants.PaymentDetail.PAYMENT_ID, paymentDetailId);

                PaymentDetail pd = this.businessObjectService.findByPrimaryKey(PaymentDetail.class, primaryKeys);
                if (pd != null) {
                    pd.setPrimaryCancelledPayment(Boolean.TRUE);
                }
                this.businessObjectService.save(pd);
                this.emailService.sendCancelEmail(paymentGroup, note, user);

                LOG.debug("cancelPendingPayment() Pending payment cancelled and mail was sent; exit method.");
            }
            else if (PdpConstants.PaymentStatusCodes.OPEN.equals(paymentStatus) || PdpConstants.PaymentStatusCodes.HELD_CD.equals(paymentStatus)) {
                if (!pdpAuthorizationService.hasCancelPaymentPermission(user.getPrincipalId())) {
                    LOG.warn("cancelPendingPayment() Payment status is " + paymentStatus + "; user does not have rights to cancel. This should not happen unless user is URL spoofing.");
                    throw new RuntimeException("cancelPendingPayment() Payment status is " + paymentStatus + "; user does not have rights to cancel. This should not happen unless user is URL spoofing.");
                }

                changeStatus(paymentGroup, PdpConstants.PaymentStatusCodes.CANCEL_PAYMENT, PdpConstants.PaymentChangeCodes.CANCEL_PAYMENT_CHNG_CD, note, user);

                // set primary cancel indicator for EPIC to use
                Map primaryKeys = new HashMap();
                primaryKeys.put(PdpPropertyConstants.PaymentDetail.PAYMENT_ID, paymentDetailId);

                PaymentDetail pd = this.businessObjectService.findByPrimaryKey(PaymentDetail.class, primaryKeys);
                if (pd != null) {
                    pd.setPrimaryCancelledPayment(Boolean.TRUE);
                    PaymentNoteText payNoteText = new PaymentNoteText();
                    payNoteText.setCustomerNoteLineNbr(new KualiInteger(pd.getNotes().size() + 1));
                    payNoteText.setCustomerNoteText(note);
                    pd.addNote(payNoteText);
                }

                this.businessObjectService.save(pd);

                LOG.debug("cancelPendingPayment() Pending payment cancelled; exit method.");
            }
            else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("cancelPendingPayment() Payment status is " + paymentStatus + "; cannot cancel payment in this status");
                }

                GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_INVALID_STATUS_TO_CANCEL);
                return false;
            }
        }
        else {
            LOG.debug("cancelPendingPayment() Pending payment group has already been cancelled; exit method.");
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.pdp.document.service.PaymentMaintenanceService#holdPendingPayment(java.lang.Integer, java.lang.String,
     *      org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public boolean holdPendingPayment(Integer paymentGroupId, String note, Person user) {
        // All actions must be performed on entire group not individual detail record
        if (LOG.isDebugEnabled()) {
            LOG.debug("holdPendingPayment() Enter method to hold pending payment with id = " + paymentGroupId);
        }

        if (!pdpAuthorizationService.hasHoldPaymentPermission(user.getPrincipalId())) {
            LOG.warn("holdPendingPayment() User " + user.getPrincipalId() + " does not have rights to hold payments. This should not happen unless user is URL spoofing.");
            throw new RuntimeException("holdPendingPayment() User " + user.getPrincipalId() + " does not have rights to hold payments. This should not happen unless user is URL spoofing.");
        }

        PaymentGroup paymentGroup = this.paymentGroupService.get(paymentGroupId);
        if (paymentGroup == null) {
            LOG.debug("holdPendingPayment() Pending payment not found; throw exception.");
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_NOT_FOUND);
            return false;
        }

        String paymentStatus = paymentGroup.getPaymentStatus().getCode();

        if (!(PdpConstants.PaymentStatusCodes.HELD_CD.equals(paymentStatus))) {
            if (PdpConstants.PaymentStatusCodes.OPEN.equals(paymentStatus)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("holdPendingPayment() Payment status is " + paymentStatus + "; continue with hold.");
                }

                changeStatus(paymentGroup, PdpConstants.PaymentStatusCodes.HELD_CD, PdpConstants.PaymentChangeCodes.HOLD_CHNG_CD, note, user);

                LOG.debug("holdPendingPayment() Pending payment was put on hold; exit method.");
            }
            else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("holdPendingPayment() Payment status is " + paymentStatus + "; cannot hold payment in this status");
                }

                GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_INVALID_STATUS_TO_HOLD);
                return false;
            }
        }
        else {
            LOG.debug("holdPendingPayment() Pending payment group has already been held; exit method.");
        }
        return true;

    }

    /**
     * @see org.kuali.kfs.pdp.document.service.PaymentMaintenanceService#removeHoldPendingPayment(java.lang.Integer,
     *      java.lang.String, org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public boolean removeHoldPendingPayment(Integer paymentGroupId, String note, Person user) {
        // All actions must be performed on entire group not individual detail record
        if (LOG.isDebugEnabled()) {
            LOG.debug("removeHoldPendingPayment() Enter method to hold pending payment with id = " + paymentGroupId);
        }
        PaymentGroup paymentGroup = this.paymentGroupService.get(paymentGroupId);
        if (paymentGroup == null) {
            LOG.debug("removeHoldPendingPayment() Payment not found; throw exception.");

            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_NOT_FOUND);
            return false;
        }

        String paymentStatus = paymentGroup.getPaymentStatus().getCode();

        if (!(PdpConstants.PaymentStatusCodes.OPEN.equals(paymentStatus))) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("removeHoldPendingPayment() Payment status is " + paymentStatus + "; continue with hold removal.");
            }

            if ((PdpConstants.PaymentStatusCodes.HELD_TAX_EMPLOYEE_CD.equals(paymentStatus)) || (PdpConstants.PaymentStatusCodes.HELD_TAX_NRA_CD.equals(paymentStatus)) || (PdpConstants.PaymentStatusCodes.HELD_TAX_NRA_EMPL_CD.equals(paymentStatus))) {
                if (!pdpAuthorizationService.hasRemovePaymentTaxHoldPermission(user.getPrincipalId())) {
                    LOG.warn("removeHoldPendingPayment() User " + user.getPrincipalId() + " does not have rights to remove tax holds. This should not happen unless user is URL spoofing.");
                    throw new RuntimeException("removeHoldPendingPayment() User " + user.getPrincipalId() + " does not have rights to remove tax holds. This should not happen unless user is URL spoofing.");
                }

                changeStatus(paymentGroup, PdpConstants.PaymentStatusCodes.OPEN, PdpConstants.PaymentChangeCodes.REMOVE_HOLD_CHNG_CD, note, user);
                LOG.debug("removeHoldPendingPayment() Pending payment was taken off hold; exit method.");
            }
            else if (PdpConstants.PaymentStatusCodes.HELD_CD.equals(paymentStatus)) {
                if (!pdpAuthorizationService.hasHoldPaymentPermission(user.getPrincipalId())) {
                    LOG.warn("removeHoldPendingPayment() User " + user.getPrincipalId() + " does not have rights to hold payments. This should not happen unless user is URL spoofing.");
                    throw new RuntimeException("removeHoldPendingPayment() User " + user.getPrincipalId() + " does not have rights to hold payments. This should not happen unless user is URL spoofing.");
                }

                changeStatus(paymentGroup, PdpConstants.PaymentStatusCodes.OPEN, PdpConstants.PaymentChangeCodes.REMOVE_HOLD_CHNG_CD, note, user);

                LOG.debug("removeHoldPendingPayment() Pending payment was taken off hold; exit method.");
            }
            else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("removeHoldPendingPayment() Payment status is " + paymentStatus + "; cannot remove hold on payment in this status");
                }

                GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_INVALID_STATUS_TO_REMOVE_HOLD);
                return false;
            }
        }
        else {
            LOG.debug("removeHoldPendingPayment() Pending payment group has already been un-held; exit method.");
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.pdp.document.service.PaymentMaintenanceService#changeImmediateFlag(java.lang.Integer, java.lang.String,
     *      org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public void changeImmediateFlag(Integer paymentGroupId, String note, Person user) {
        // All actions must be performed on entire group not individual detail record
        if (LOG.isDebugEnabled()) {
            LOG.debug("changeImmediateFlag() Enter method to hold pending payment with id = " + paymentGroupId);
        }

        if (!pdpAuthorizationService.hasSetAsImmediatePayPermission(user.getPrincipalId())) {
            LOG.warn("changeImmediateFlag() User " + user.getPrincipalId() + " does not have rights to set payments as immediate. This should not happen unless user is URL spoofing.");
            throw new RuntimeException("changeImmediateFlag() User " + user.getPrincipalId() + " does not have rights to payments as immediate. This should not happen unless user is URL spoofing.");
        }

        PaymentGroupHistory paymentGroupHistory = new PaymentGroupHistory();
        PaymentGroup paymentGroup = this.paymentGroupService.get(paymentGroupId);

        paymentGroupHistory.setOrigProcessImmediate(paymentGroup.getProcessImmediate());

        if (paymentGroup.getProcessImmediate().equals(Boolean.TRUE)) {
            paymentGroup.setProcessImmediate(Boolean.FALSE);
        }
        else {
            paymentGroup.setProcessImmediate(Boolean.TRUE);
        }

        changeStatus(paymentGroup, paymentGroup.getPaymentStatus().getCode(), PdpConstants.PaymentChangeCodes.CHANGE_IMMEDIATE_CHNG_CD, note, user, paymentGroupHistory);

        LOG.debug("changeImmediateFlag() exit method.");
    }

    /**
     * @see org.kuali.kfs.pdp.document.service.PaymentMaintenanceService#cancelDisbursement(java.lang.Integer, java.lang.Integer,
     *      java.lang.String, org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public boolean cancelDisbursement(Integer paymentGroupId, Integer paymentDetailId, String note, Person user) {
        // All actions must be performed on entire group not individual detail record
        if (LOG.isDebugEnabled()) {
            LOG.debug("cancelDisbursement() Enter method to cancel disbursement with id = " + paymentGroupId);
        }

        if (!pdpAuthorizationService.hasCancelPaymentPermission(user.getPrincipalId())) {
            LOG.warn("cancelDisbursement() User " + user.getPrincipalId() + " does not have rights to cancel payments. This should not happen unless user is URL spoofing.");
            throw new RuntimeException("cancelDisbursement() User " + user.getPrincipalId() + " does not have rights to cancel payments. This should not happen unless user is URL spoofing.");
        }

        PaymentGroup paymentGroup = this.paymentGroupService.get(paymentGroupId);

        if (paymentGroup == null) {
            LOG.debug("cancelDisbursement() Disbursement not found; throw exception.");
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_DISBURSEMENT_NOT_FOUND);
            return false;
        }

        String paymentStatus = paymentGroup.getPaymentStatus().getCode();

        if (!(PdpConstants.PaymentStatusCodes.CANCEL_DISBURSEMENT.equals(paymentStatus))) {
            if (((PdpConstants.PaymentStatusCodes.EXTRACTED.equals(paymentStatus)) && (ObjectUtils.isNotNull(paymentGroup.getDisbursementDate()))) || (PdpConstants.PaymentStatusCodes.PENDING_ACH.equals(paymentStatus))) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("cancelDisbursement() Payment status is " + paymentStatus + "; continue with cancel.");
                }

                List<PaymentGroup> allDisbursementPaymentGroups = this.paymentGroupService.getByDisbursementNumber(paymentGroup.getDisbursementNbr().intValue());

                for (PaymentGroup element : allDisbursementPaymentGroups) {

                    PaymentGroupHistory pgh = new PaymentGroupHistory();

                    if (!element.getPaymentDetails().get(0).isDisbursementActionAllowed()) {
                        LOG.warn("cancelDisbursement() Payment does not allow disbursement action. This should not happen unless user is URL spoofing.");
                        throw new RuntimeException("cancelDisbursement() Payment does not allow disbursement action. This should not happen unless user is URL spoofing.");
                    }

                    if ((ObjectUtils.isNotNull(element.getDisbursementType())) && (element.getDisbursementType().getCode().equals(PdpConstants.DisbursementTypeCodes.CHECK))) {
                        pgh.setPmtCancelExtractStat(Boolean.FALSE);
                    }

                    changeStatus(element, PdpConstants.PaymentStatusCodes.CANCEL_DISBURSEMENT, PdpConstants.PaymentChangeCodes.CANCEL_DISBURSEMENT, note, user, pgh);

                    glPendingTransactionService.generateCancellationGeneralLedgerPendingEntry(element);

                    // set primary cancel indicator for EPIC to use
                    // these payment details will be canceled when running processPdpCancelAndPaidJOb
                    Map<String, KualiInteger> primaryKeys = new HashMap<String, KualiInteger>();
                    primaryKeys.put(PdpPropertyConstants.PaymentDetail.PAYMENT_ID, element.getId());

                    PaymentDetail pd = this.businessObjectService.findByPrimaryKey(PaymentDetail.class, primaryKeys);
                    if (pd != null) {
                        pd.setPrimaryCancelledPayment(Boolean.TRUE);
                    }

                    this.businessObjectService.save(pd);
                }

                LOG.debug("cancelDisbursement() Disbursement cancelled; exit method.");
            }
            else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("cancelDisbursement() Payment status is " + paymentStatus + " and disbursement date is " + paymentGroup.getDisbursementDate() + "; cannot cancel payment in this status");
                }

                GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_DISBURSEMENT_INVALID_TO_CANCEL);
                return false;
            }
        }
        else {
            LOG.debug("cancelDisbursement() Disbursement has already been cancelled; exit method.");
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.pdp.document.service.PaymentMaintenanceService#reissueDisbursement(java.lang.Integer,
     *      java.lang.String, org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public boolean reissueDisbursement(Integer paymentGroupId, String note, Person user) {
        // All actions must be performed on entire group not individual detail record
        if (LOG.isDebugEnabled()) {
            LOG.debug("reissueDisbursement() Enter method to reissue disbursement with id = " + paymentGroupId);
        }


        PaymentGroup paymentGroup = this.paymentGroupService.get(paymentGroupId);
        if (paymentGroup == null) {
            LOG.debug("reissueDisbursement() Disbursement not found; throw exception.");
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_DISBURSEMENT_NOT_FOUND);
            return false;
        }

        String paymentStatus = paymentGroup.getPaymentStatus().getCode();

        if (!(PdpConstants.PaymentStatusCodes.OPEN.equals(paymentStatus))) {
            if (((PdpConstants.PaymentStatusCodes.CANCEL_DISBURSEMENT.equals(paymentStatus)) && (ObjectUtils.isNotNull(paymentGroup.getDisbursementDate())))) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("reissueDisbursement() Payment status is " + paymentStatus + "; continue with reissue.");
                }

                List<PaymentGroup> allDisbursementPaymentGroups = this.paymentGroupService.getByDisbursementNumber(paymentGroup.getDisbursementNbr().intValue());

                for (PaymentGroup pg : allDisbursementPaymentGroups) {
                    PaymentGroupHistory pgh = new PaymentGroupHistory();

                    if (!pg.getPaymentDetails().get(0).isDisbursementActionAllowed()) {
                        LOG.warn("cancelDisbursement() Payment does not allow disbursement action. This should not happen unless user is URL spoofing.");
                        throw new RuntimeException("cancelDisbursement() Payment does not allow disbursement action. This should not happen unless user is URL spoofing.");
                    }


                    pgh.setOrigProcessImmediate(pg.getProcessImmediate());
                    pgh.setOrigPmtSpecHandling(pg.getPymtSpecialHandling());
                    pgh.setBank(pg.getBank());
                    pgh.setOrigPaymentDate(pg.getPaymentDate());
                    //put a check for null since disbursement date was not set in testMode / dev
                    if (ObjectUtils.isNotNull(pg.getDisbursementDate())) {
                        pgh.setOrigDisburseDate(new Timestamp(pg.getDisbursementDate().getTime()));
                    }
                    pgh.setOrigAchBankRouteNbr(pg.getAchBankRoutingNbr());
                    pgh.setOrigDisburseNbr(pg.getDisbursementNbr());
                    pgh.setOrigAdviceEmail(pg.getAdviceEmailAddress());
                    pgh.setDisbursementType(pg.getDisbursementType());
                    pgh.setProcess(pg.getProcess());

                   // glPendingTransactionService.generateReissueGeneralLedgerPendingEntry(pg);

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("cancelReissueDisbursement() Status is '" + paymentStatus + "; delete row from AchAccountNumber table.");
                    }

                    AchAccountNumber achAccountNumber = pg.getAchAccountNumber();

                    if (ObjectUtils.isNotNull(achAccountNumber)) {
                        this.businessObjectService.delete(achAccountNumber);
                        pg.setAchAccountNumber(null);
                    }

                    // if bank functionality is not enabled or the group bank is inactive clear bank code
                    if (!bankService.isBankSpecificationEnabled() || !pg.getBank().isActive()) {
                        pg.setBank(null);
                    }

                    pg.setDisbursementDate((java.sql.Date) null);
                    pg.setAchBankRoutingNbr(null);
                    pg.setAchAccountType(null);
                    pg.setPhysCampusProcessCd(null);
                    pg.setDisbursementNbr((KualiInteger) null);
                    pg.setAdviceEmailAddress(null);
                    pg.setDisbursementType(null);
                    pg.setProcess(null);
                    pg.setProcessImmediate(false);
                    changeStatus(pg, PdpConstants.PaymentStatusCodes.OPEN, PdpConstants.PaymentChangeCodes.REISSUE_DISBURSEMENT, note, user, pgh);
                }

                LOG.debug("reissueDisbursement() Disbursement reissued; exit method.");
            }
            else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("cancelReissueDisbursement() Payment status is " + paymentStatus + " and disbursement date is " + paymentGroup.getDisbursementDate() + "; cannot cancel payment");
                }

                GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_DISBURSEMENT_INVALID_TO_CANCEL_AND_REISSUE);
                return false;
            }
        }
        else {
            LOG.debug("cancelReissueDisbursement() Disbursement already cancelled and reissued; exit method.");
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.pdp.document.service.PaymentMaintenanceService#cancelReissueDisbursement(java.lang.Integer,
     *      java.lang.String, org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public boolean cancelReissueDisbursement(Integer paymentGroupId, String note, Person user) {
        // All actions must be performed on entire group not individual detail record
        if (LOG.isDebugEnabled()) {
            LOG.debug("cancelReissueDisbursement() Enter method to cancel disbursement with id = " + paymentGroupId);
        }

        if (!pdpAuthorizationService.hasCancelPaymentPermission(user.getPrincipalId())) {
            LOG.warn("cancelReissueDisbursement() User " + user.getPrincipalId() + " does not have rights to cancel payments. This should not happen unless user is URL spoofing.");
            throw new RuntimeException("cancelReissueDisbursement() User " + user.getPrincipalId() + " does not have rights to cancel payments. This should not happen unless user is URL spoofing.");
        }

        PaymentGroup paymentGroup = this.paymentGroupService.get(paymentGroupId);
        if (paymentGroup == null) {
            LOG.debug("cancelReissueDisbursement() Disbursement not found; throw exception.");
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_DISBURSEMENT_NOT_FOUND);
            return false;
        }

        String paymentStatus = paymentGroup.getPaymentStatus().getCode();

        if (!(PdpConstants.PaymentStatusCodes.OPEN.equals(paymentStatus))) {
            if (((PdpConstants.PaymentStatusCodes.EXTRACTED.equals(paymentStatus)) && (ObjectUtils.isNotNull(paymentGroup.getDisbursementDate()))) || (PdpConstants.PaymentStatusCodes.PENDING_ACH.equals(paymentStatus))) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("cancelReissueDisbursement() Payment status is " + paymentStatus + "; continue with cancel.");
                }

                List<PaymentGroup> allDisbursementPaymentGroups = this.paymentGroupService.getByDisbursementNumber(paymentGroup.getDisbursementNbr().intValue());

                for (PaymentGroup pg : allDisbursementPaymentGroups) {
                    PaymentGroupHistory pgh = new PaymentGroupHistory();

                    if (!pg.getPaymentDetails().get(0).isDisbursementActionAllowed()) {
                        LOG.warn("cancelDisbursement() Payment does not allow disbursement action. This should not happen unless user is URL spoofing.");
                        throw new RuntimeException("cancelDisbursement() Payment does not allow disbursement action. This should not happen unless user is URL spoofing.");
                    }

                    if ((ObjectUtils.isNotNull(pg.getDisbursementType())) && (pg.getDisbursementType().getCode().equals(PdpConstants.DisbursementTypeCodes.CHECK))) {
                        pgh.setPmtCancelExtractStat(Boolean.FALSE);
                    }

                    pgh.setOrigProcessImmediate(pg.getProcessImmediate());
                    pgh.setOrigPmtSpecHandling(pg.getPymtSpecialHandling());
                    pgh.setBank(pg.getBank());
                    pgh.setOrigPaymentDate(pg.getPaymentDate());
                    //put a check for null since disbursement date was not set in testMode / dev
                    if (ObjectUtils.isNotNull(pg.getDisbursementDate())) {
                        pgh.setOrigDisburseDate(new Timestamp(pg.getDisbursementDate().getTime()));
                    }
                    pgh.setOrigAchBankRouteNbr(pg.getAchBankRoutingNbr());
                    pgh.setOrigDisburseNbr(pg.getDisbursementNbr());
                    pgh.setOrigAdviceEmail(pg.getAdviceEmailAddress());
                    pgh.setDisbursementType(pg.getDisbursementType());
                    pgh.setProcess(pg.getProcess());

                    glPendingTransactionService.generateReissueGeneralLedgerPendingEntry(pg);

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("cancelReissueDisbursement() Status is '" + paymentStatus + "; delete row from AchAccountNumber table.");
                    }

                    AchAccountNumber achAccountNumber = pg.getAchAccountNumber();

                    if (ObjectUtils.isNotNull(achAccountNumber)) {
                        this.businessObjectService.delete(achAccountNumber);
                        pg.setAchAccountNumber(null);
                    }

                    // if bank functionality is not enabled or the group bank is inactive clear bank code
                    if (!bankService.isBankSpecificationEnabled() || !pg.getBank().isActive()) {
                        pg.setBank(null);
                    }

                    pg.setDisbursementDate((java.sql.Date) null);
                    pg.setAchBankRoutingNbr(null);
                    pg.setAchAccountType(null);
                    pg.setPhysCampusProcessCd(null);
                    pg.setDisbursementNbr((KualiInteger) null);
                    pg.setAdviceEmailAddress(null);
                    pg.setDisbursementType(null);
                    pg.setProcess(null);
                    pg.setProcessImmediate(false);

                    changeStatus(pg, PdpConstants.PaymentStatusCodes.OPEN, PdpConstants.PaymentChangeCodes.CANCEL_REISSUE_DISBURSEMENT, note, user, pgh);
                }

                LOG.debug("cancelReissueDisbursement() Disbursement cancelled and reissued; exit method.");
            }
            else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("cancelReissueDisbursement() Payment status is " + paymentStatus + " and disbursement date is " + paymentGroup.getDisbursementDate() + "; cannot cancel payment");
                }

                GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_DISBURSEMENT_INVALID_TO_CANCEL_AND_REISSUE);
                return false;
            }
        }
        else {
            LOG.debug("cancelReissueDisbursement() Disbursement already cancelled and reissued; exit method.");
        }
        return true;
    }

    /**
     * inject
     *
     * @param dao
     */
    public void setPaymentGroupDao(PaymentGroupDao dao) {
        paymentGroupDao = dao;
    }

    /**
     * inject
     *
     * @param dao
     */
    public void setPaymentDetailDao(PaymentDetailDao dao) {
        paymentDetailDao = dao;
    }

    /**
     * inject
     *
     * @param service
     */
    public void setGlPendingTransactionService(PendingTransactionService service) {
        glPendingTransactionService = service;
    }

    /**
     * inject
     *
     * @param service
     */
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the bankService attribute value.
     *
     * @param bankService The bankService to set.
     */
    public void setBankService(BankService bankService) {
        this.bankService = bankService;
    }

    /**
     * Sets the business object service
     *
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the payment group service
     *
     * @param paymentGroupService
     */
    public void setPaymentGroupService(PaymentGroupService paymentGroupService) {
        this.paymentGroupService = paymentGroupService;
    }

    public void setEmailService(PdpEmailService emailService) {
        this.emailService = emailService;
    }

    public void setPdpAuthorizationService(PdpAuthorizationService pdpAuthorizationService) {
        this.pdpAuthorizationService = pdpAuthorizationService;
    }
}

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
import org.kuali.kfs.pdp.service.EnvironmentService;
import org.kuali.kfs.pdp.service.PdpAuthorizationService;
import org.kuali.kfs.pdp.service.PaymentGroupService;
import org.kuali.kfs.pdp.service.PaymentMaintenanceService;
import org.kuali.kfs.pdp.service.PdpEmailService;
import org.kuali.kfs.pdp.service.PendingTransactionService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.sys.service.KualiCodeService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.bo.KualiCode;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.MailService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiInteger;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class...
 */
@Transactional
public class PaymentMaintenanceServiceImpl implements PaymentMaintenanceService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentMaintenanceServiceImpl.class);

    private PaymentGroupDao paymentGroupDao;
    private PaymentDetailDao paymentDetailDao;
    private PendingTransactionService glPendingTransactionService;
    private EnvironmentService environmentService;
    private MailService mailService;
    private ParameterService parameterService;
    private KualiCodeService kualiCodeService;
    private BankService bankService;
    private BusinessObjectService businessObjectService;
    private PaymentGroupService paymentGroupService;
    private PdpEmailService emailService;
    private PdpAuthorizationService pdpAuthorizationService;

    /**
     * This method changes status for a payment group.
     * @param paymentGroup the payment group
     * @param newPaymentStatus the new payment status
     * @param changeStatus the changed payment status
     * @param note a note for payment status change
     * @param user the user that changed the status
     */
    public void changeStatus(PaymentGroup paymentGroup, String newPaymentStatus, String changeStatus, String note, Person user) {
        LOG.debug("changeStatus() enter method with new status of " + newPaymentStatus);

        PaymentGroupHistory paymentGroupHistory = new PaymentGroupHistory();
        KualiCode cd = this.kualiCodeService.getByCode(PaymentChangeCode.class, changeStatus);
        paymentGroupHistory.setPaymentChange((PaymentChangeCode) cd);
        paymentGroupHistory.setOrigPaymentStatus(paymentGroup.getPaymentStatus());
        paymentGroupHistory.setChangeUser(user);
        paymentGroupHistory.setChangeNoteText(note);
        paymentGroupHistory.setPaymentGroup(paymentGroup);
        paymentGroupHistory.setChangeTime(new Timestamp(new Date().getTime()));

        this.businessObjectService.save(paymentGroupHistory);

        KualiCode code = this.kualiCodeService.getByCode(PaymentStatus.class, newPaymentStatus);
        paymentGroup.setPaymentStatus((PaymentStatus) code);
        this.businessObjectService.save(paymentGroup);
        LOG.debug("changeStatus() Status has been changed; exit method.");
    }

    /**
     * This method changes the state of a paymentGroup.
     * @param paymentGroup the payment group to change the state for
     * @param newPaymentStatus the new payment status
     * @param changeStatus the status that is changed
     * @param note the note entered by the user
     * @param user the user that changed the 
     * @param paymentGroupHistory
     */
    public void changeStatus(PaymentGroup paymentGroup, String newPaymentStatus, String changeStatus, String note, Person user, PaymentGroupHistory paymentGroupHistory) {
        LOG.debug("changeStatus() enter method with new status of " + newPaymentStatus);

        KualiCode cd = this.kualiCodeService.getByCode(PaymentChangeCode.class, changeStatus);
        paymentGroupHistory.setPaymentChange((PaymentChangeCode) cd);
        paymentGroupHistory.setOrigPaymentStatus(paymentGroup.getPaymentStatus());
        paymentGroupHistory.setChangeUser(user);
        paymentGroupHistory.setChangeNoteText(note);
        paymentGroupHistory.setPaymentGroup(paymentGroup);
        paymentGroupHistory.setChangeTime(new Timestamp(new Date().getTime()));

        this.businessObjectService.save(paymentGroupHistory);

        KualiCode code = this.kualiCodeService.getByCode(PaymentStatus.class, newPaymentStatus);
        if (paymentGroup.getPaymentStatus() != ((PaymentStatus) code)) {
            paymentGroup.setPaymentStatus((PaymentStatus) code);
        }
        this.businessObjectService.save(paymentGroup);

        LOG.debug("changeStatus() Status has been changed; exit method.");
    }

    /**
     * @see org.kuali.kfs.pdp.document.service.PaymentMaintenanceService#cancelPendingPayment(java.lang.Integer, java.lang.Integer, java.lang.String, org.kuali.rice.kim.bo.Person)
     */
    public boolean cancelPendingPayment(Integer paymentGroupId, Integer paymentDetailId, String note, Person user) {
        // All actions must be performed on entire group not individual detail record
        LOG.debug("cancelPendingPayment() Enter method to cancel pending payment with group id = " + paymentGroupId);
        LOG.debug("cancelPendingPayment() payment detail id being cancelled = " + paymentDetailId);

        PaymentGroup paymentGroup = this.paymentGroupService.get(paymentGroupId);
        if (paymentGroup == null) {
            LOG.debug("cancelPendingPayment() Pending payment not found; throw exception.");
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_NOT_FOUND);
            return false;
        }

        String paymentStatus = paymentGroup.getPaymentStatus().getCode();

        if (!(PdpConstants.PaymentStatusCodes.CANCEL_PAYMENT.equals(paymentStatus))) {
            LOG.debug("cancelPendingPayment() Payment status is " + paymentStatus + "; continue with cancel.");

            if ((PdpConstants.PaymentStatusCodes.HELD_TAX_EMPLOYEE_CD.equals(paymentStatus)) || (PdpConstants.PaymentStatusCodes.HELD_TAX_NRA_CD.equals(paymentStatus)) || (PdpConstants.PaymentStatusCodes.HELD_TAX_NRA_EMPL_CD.equals(paymentStatus))) {
                if (pdpAuthorizationService.hasRemovePaymentTaxHoldPermission(user.getPrincipalId()) /*|| KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, PdpConstants.Groups.SYSADMIN_GROUP)*/) {

                    changeStatus(paymentGroup, PdpConstants.PaymentStatusCodes.CANCEL_PAYMENT, PdpConstants.PaymentChangeCodes.CANCEL_PAYMENT_CHNG_CD, note, user);

                    // set primary cancel indicator for EPIC to use
                    Map primaryKeys = new HashMap();
                    primaryKeys.put(PdpPropertyConstants.PaymentDetail.PAYMENT_ID, paymentDetailId);

                    PaymentDetail pd = (PaymentDetail) this.businessObjectService.findByPrimaryKey(PaymentDetail.class, primaryKeys);
                    if (pd != null) {
                        pd.setPrimaryCancelledPayment(Boolean.TRUE);
                    }
                    this.businessObjectService.save(pd);
                    this.emailService.sendCancelEmail(paymentGroup, note, user);

                    LOG.debug("cancelPendingPayment() Pending payment cancelled and mail was sent; exit method.");
                }
                else {
                    LOG.debug("cancelPendingPayment() Payment status is " + paymentStatus + "; user does not have rights to cancel");

                    GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_INVALID_STATUS_TO_CANCEL);
                    return false;
                }
            }
            else if (PdpConstants.PaymentStatusCodes.OPEN.equals(paymentStatus) || PdpConstants.PaymentStatusCodes.HELD_CD.equals(paymentStatus)) {
                if (pdpAuthorizationService.hasCancelPaymentPermission(user.getPrincipalId())) {

                    changeStatus(paymentGroup, PdpConstants.PaymentStatusCodes.CANCEL_PAYMENT, PdpConstants.PaymentChangeCodes.CANCEL_PAYMENT_CHNG_CD, note, user);

                    // set primary cancel indicator for EPIC to use
                    Map primaryKeys = new HashMap();
                    primaryKeys.put(PdpPropertyConstants.PaymentDetail.PAYMENT_ID, paymentDetailId);

                    PaymentDetail pd = (PaymentDetail) this.businessObjectService.findByPrimaryKey(PaymentDetail.class, primaryKeys);
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
                    LOG.debug("cancelPendingPayment() Payment status is " + paymentStatus + "; user does not have rights to cancel");

                    GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_INVALID_STATUS_TO_CANCEL);
                    return false;
                }
            }
            else {
                LOG.debug("cancelPendingPayment() Payment status is " + paymentStatus + "; cannot cancel payment in this status");

                GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_INVALID_STATUS_TO_CANCEL);
                return false;
            }
        }
        else {
            LOG.debug("cancelPendingPayment() Pending payment group has already been cancelled; exit method.");
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.pdp.document.service.PaymentMaintenanceService#holdPendingPayment(java.lang.Integer, java.lang.String, org.kuali.rice.kim.bo.Person)
     */
    public boolean holdPendingPayment(Integer paymentGroupId, String note, Person user) {
        // All actions must be performed on entire group not individual detail record
        LOG.debug("holdPendingPayment() Enter method to hold pending payment with id = " + paymentGroupId);

        PaymentGroup paymentGroup = this.paymentGroupService.get(paymentGroupId);
        if (paymentGroup == null) {
            LOG.debug("holdPendingPayment() Pending payment not found; throw exception.");
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_NOT_FOUND);
            return false;
        }

        String paymentStatus = paymentGroup.getPaymentStatus().getCode();

        if (!(PdpConstants.PaymentStatusCodes.HELD_CD.equals(paymentStatus))) {
            if (PdpConstants.PaymentStatusCodes.OPEN.equals(paymentStatus)) {
                LOG.debug("holdPendingPayment() Payment status is " + paymentStatus + "; continue with hold.");

                changeStatus(paymentGroup, PdpConstants.PaymentStatusCodes.HELD_CD, PdpConstants.PaymentChangeCodes.HOLD_CHNG_CD, note, user);

                LOG.debug("holdPendingPayment() Pending payment was put on hold; exit method.");
            }
            else {
                LOG.debug("holdPendingPayment() Payment status is " + paymentStatus + "; cannot hold payment in this status");

                GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_INVALID_STATUS_TO_HOLD);
                return false;
            }
        }
        else {
            LOG.debug("holdPendingPayment() Pending payment group has already been held; exit method.");
        }
        return true;

    }

    /**
     * @see org.kuali.kfs.pdp.document.service.PaymentMaintenanceService#removeHoldPendingPayment(java.lang.Integer, java.lang.String, org.kuali.rice.kim.bo.Person)
     */
    public boolean removeHoldPendingPayment(Integer paymentGroupId, String note, Person user) {
        // All actions must be performed on entire group not individual detail record
        LOG.debug("removeHoldPendingPayment() Enter method to hold pending payment with id = " + paymentGroupId);
        PaymentGroup paymentGroup = this.paymentGroupService.get(paymentGroupId);
        if (paymentGroup == null) {
            LOG.debug("removeHoldPendingPayment() Payment not found; throw exception.");

            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_NOT_FOUND);
            return false;
        }

        String paymentStatus = paymentGroup.getPaymentStatus().getCode();

        if (!(PdpConstants.PaymentStatusCodes.OPEN.equals(paymentStatus))) {
            LOG.debug("removeHoldPendingPayment() Payment status is " + paymentStatus + "; continue with hold removal.");

            if ((PdpConstants.PaymentStatusCodes.HELD_TAX_EMPLOYEE_CD.equals(paymentStatus)) || (PdpConstants.PaymentStatusCodes.HELD_TAX_NRA_CD.equals(paymentStatus)) || (PdpConstants.PaymentStatusCodes.HELD_TAX_NRA_EMPL_CD.equals(paymentStatus))) {
                if (pdpAuthorizationService.hasRemovePaymentTaxHoldPermission(user.getPrincipalId()) /*|| KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, PdpConstants.Groups.SYSADMIN_GROUP)*/) {

                    changeStatus(paymentGroup, PdpConstants.PaymentStatusCodes.OPEN, PdpConstants.PaymentChangeCodes.REMOVE_HOLD_CHNG_CD, note, user);
                    LOG.debug("removeHoldPendingPayment() Pending payment was taken off hold; exit method.");
                }
                else {
                    LOG.debug("removeHoldPendingPayment() Payment status is " + paymentStatus + "; user does not have rights to cancel");

                    GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_INVALID_STATUS_TO_REMOVE_HOLD);
                    return false;
                }
            }
            else if (PdpConstants.PaymentStatusCodes.HELD_CD.equals(paymentStatus)) {
                if (pdpAuthorizationService.hasHoldPaymentPermission(user.getPrincipalId())) {

                    changeStatus(paymentGroup, PdpConstants.PaymentStatusCodes.OPEN, PdpConstants.PaymentChangeCodes.REMOVE_HOLD_CHNG_CD, note, user);

                    LOG.debug("removeHoldPendingPayment() Pending payment was taken off hold; exit method.");
                }
                else {
                    LOG.debug("removeHoldPendingPayment() Payment status is " + paymentStatus + "; user does not have rights to cancel");

                    GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_INVALID_STATUS_TO_REMOVE_HOLD);
                    return false;
                }
            }
            else {
                LOG.debug("removeHoldPendingPayment() Payment status is " + paymentStatus + "; cannot remove hold on payment in this status");

                GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_PAYMENT_INVALID_STATUS_TO_REMOVE_HOLD);
                return false;
            }
        }
        else {
            LOG.debug("removeHoldPendingPayment() Pending payment group has already been un-held; exit method.");
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.pdp.document.service.PaymentMaintenanceService#changeImmediateFlag(java.lang.Integer, java.lang.String, org.kuali.rice.kim.bo.Person)
     */
    public void changeImmediateFlag(Integer paymentGroupId, String note, Person user) {
        // All actions must be performed on entire group not individual detail record
        LOG.debug("changeImmediateFlag() Enter method to hold pending payment with id = " + paymentGroupId);
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
     * @see org.kuali.kfs.pdp.document.service.PaymentMaintenanceService#cancelDisbursement(java.lang.Integer, java.lang.Integer, java.lang.String, org.kuali.rice.kim.bo.Person)
     */
    public boolean cancelDisbursement(Integer paymentGroupId, Integer paymentDetailId, String note, Person user) {
        // All actions must be performed on entire group not individual detail record
        LOG.debug("cancelDisbursement() Enter method to cancel disbursement with id = " + paymentGroupId);

        PaymentGroup paymentGroup = this.paymentGroupService.get(paymentGroupId);

        if (paymentGroup == null) {
            LOG.debug("cancelDisbursement() Disbursement not found; throw exception.");
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_DISBURSEMENT_NOT_FOUND);
            return false;
        }

        String paymentStatus = paymentGroup.getPaymentStatus().getCode();

        if (!(PdpConstants.PaymentChangeCodes.CANCEL_DISBURSEMENT.equals(paymentStatus))) {
            if (((PdpConstants.PaymentStatusCodes.EXTRACTED.equals(paymentStatus)) && (ObjectUtils.isNotNull(paymentGroup.getDisbursementDate()) )) || (PdpConstants.PaymentStatusCodes.PENDING_ACH.equals(paymentStatus))) {
                LOG.debug("cancelDisbursement() Payment status is " + paymentStatus + "; continue with cancel.");

                List<PaymentGroup> allDisbursementPaymentGroups = this.paymentGroupService.getByDisbursementNumber(paymentGroup.getDisbursementNbr().intValue());

                for (PaymentGroup element : allDisbursementPaymentGroups) {

                    PaymentGroupHistory pgh = new PaymentGroupHistory();

                    if ((ObjectUtils.isNotNull(element.getDisbursementType()) ) && (element.getDisbursementType().getCode().equals(PdpConstants.DisbursementTypeCodes.CHECK))) {
                        pgh.setPmtCancelExtractStat(Boolean.FALSE);
                    }

                    changeStatus(element, PdpConstants.PaymentStatusCodes.CANCEL_DISBURSEMENT, PdpConstants.PaymentChangeCodes.CANCEL_DISBURSEMENT, note, user, pgh);

                    glPendingTransactionService.generateCancellationGeneralLedgerPendingEntry(element);
                }

                // set primary cancel indicator for EPIC to use
                Map primaryKeys = new HashMap();
                primaryKeys.put(PdpPropertyConstants.PaymentDetail.PAYMENT_ID, paymentDetailId);

                PaymentDetail pd = (PaymentDetail) this.businessObjectService.findByPrimaryKey(PaymentDetail.class, primaryKeys);
                if (pd != null) {
                    pd.setPrimaryCancelledPayment(Boolean.TRUE);
                }

                this.businessObjectService.save(pd);

                LOG.debug("cancelDisbursement() Disbursement cancelled; exit method.");
            }
            else {
                LOG.debug("cancelDisbursement() Payment status is " + paymentStatus + " and disbursement date is " + paymentGroup.getDisbursementDate() + "; cannot cancel payment in this status");

                GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_DISBURSEMENT_INVALID_TO_CANCEL);
                return false;
            }
        }
        else {
            LOG.debug("cancelDisbursement() Disbursement has already been cancelled; exit method.");
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.pdp.document.service.PaymentMaintenanceService#cancelReissueDisbursement(java.lang.Integer, java.lang.String, org.kuali.rice.kim.bo.Person)
     */
    public boolean cancelReissueDisbursement(Integer paymentGroupId, String note, Person user) {
        // All actions must be performed on entire group not individual detail record
        LOG.debug("cancelReissueDisbursement() Enter method to cancel disbursement with id = " + paymentGroupId);

        PaymentGroup paymentGroup = this.paymentGroupService.get(paymentGroupId);
        if (paymentGroup == null) {
            LOG.debug("cancelReissueDisbursement() Disbursement not found; throw exception.");
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_DISBURSEMENT_NOT_FOUND);
            return false;
        }

        String paymentStatus = paymentGroup.getPaymentStatus().getCode();

        if (!(PdpConstants.PaymentStatusCodes.OPEN.equals(paymentStatus))) {
            if (((PdpConstants.PaymentStatusCodes.EXTRACTED.equals(paymentStatus)) && (ObjectUtils.isNotNull(paymentGroup.getDisbursementDate()) )) || (PdpConstants.PaymentStatusCodes.PENDING_ACH.equals(paymentStatus))) {
                LOG.debug("cancelReissueDisbursement() Payment status is " + paymentStatus + "; continue with cancel.");

                List<PaymentGroup> allDisbursementPaymentGroups = this.paymentGroupService.getByDisbursementNumber(paymentGroup.getDisbursementNbr().intValue());

                for (PaymentGroup pg : allDisbursementPaymentGroups) {
                    PaymentGroupHistory pgh = new PaymentGroupHistory();

                    if ((ObjectUtils.isNotNull(pg.getDisbursementType()) ) && (pg.getDisbursementType().getCode().equals(PdpConstants.DisbursementTypeCodes.CHECK))) {
                        pgh.setPmtCancelExtractStat(Boolean.FALSE);
                    }

                    pgh.setOrigProcessImmediate(pg.getProcessImmediate());
                    pgh.setOrigPmtSpecHandling(pg.getPymtSpecialHandling());
                    pgh.setBank(pg.getBank());
                    pgh.setOrigPaymentDate(pg.getPaymentDate());
                    pgh.setOrigDisburseDate(pg.getDisbursementDate());
                    pgh.setOrigAchBankRouteNbr(pg.getAchBankRoutingNbr());
                    pgh.setOrigDisburseNbr(pg.getDisbursementNbr());
                    pgh.setOrigAdviceEmail(pg.getAdviceEmailAddress());
                    pgh.setDisbursementType(pg.getDisbursementType());
                    pgh.setProcess(pg.getProcess());

                    glPendingTransactionService.generateReissueGeneralLedgerPendingEntry(pg);

                    LOG.debug("cancelReissueDisbursement() Status is '" + paymentStatus + "; delete row from AchAccountNumber table.");

                    AchAccountNumber achAccountNumber = pg.getAchAccountNumber();

                    if (ObjectUtils.isNotNull(achAccountNumber) ) {
                        this.businessObjectService.delete(achAccountNumber);
                        pg.setAchAccountNumber(null);
                    }

                    // if bank functionality is not enabled or the group bank is inactive clear bank code
                    if (!bankService.isBankSpecificationEnabled() || !pg.getBank().isActive()) {
                        pg.setBank(null);
                    }

                    pg.setDisbursementDate(null);
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
                LOG.debug("cancelReissueDisbursement() Payment status is " + paymentStatus + " and disbursement date is " + paymentGroup.getDisbursementDate() + "; cannot cancel payment");

                GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.PaymentDetail.ErrorMessages.ERROR_DISBURSEMENT_INVALID_TO_CANCEL_AND_REISSUE);
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
    public void setEnvironmentService(EnvironmentService environmentService) {
        this.environmentService = environmentService;
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

    public void setKualiCodeService(KualiCodeService kualiCodeService) {
        this.kualiCodeService = kualiCodeService;
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

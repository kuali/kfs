/*
 * Created on Aug 12, 2004
 */
package org.kuali.module.pdp.service.impl;

import java.util.Iterator;
import java.util.List;

import org.kuali.core.mail.InvalidAddressException;
import org.kuali.core.mail.MailMessage;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.MailService;
import org.kuali.module.pdp.PdpConstants;
import org.kuali.module.pdp.bo.AchAccountNumber;
import org.kuali.module.pdp.bo.Code;
import org.kuali.module.pdp.bo.CustomerProfile;
import org.kuali.module.pdp.bo.PaymentChange;
import org.kuali.module.pdp.bo.PaymentDetail;
import org.kuali.module.pdp.bo.PaymentGroup;
import org.kuali.module.pdp.bo.PaymentGroupHistory;
import org.kuali.module.pdp.bo.PaymentStatus;
import org.kuali.module.pdp.bo.PdpUser;
import org.kuali.module.pdp.dao.AchAccountNumberDao;
import org.kuali.module.pdp.dao.PaymentDetailDao;
import org.kuali.module.pdp.dao.PaymentGroupDao;
import org.kuali.module.pdp.dao.PaymentGroupHistoryDao;
import org.kuali.module.pdp.exception.CancelPaymentException;
import org.kuali.module.pdp.exception.PdpException;
import org.kuali.module.pdp.service.EnvironmentService;
import org.kuali.module.pdp.service.GlPendingTransactionService;
import org.kuali.module.pdp.service.PaymentMaintenanceService;
import org.kuali.module.pdp.service.ReferenceService;
import org.kuali.module.pdp.service.SecurityRecord;
import org.kuali.module.pdp.utilities.GeneralUtilities;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author HSTAPLET
 */
@Transactional
public class PaymentMaintenanceServiceImpl implements PaymentMaintenanceService {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentMaintenanceServiceImpl.class);

  // Payment Status Codes
  private static String HELD_CD = "HELD";
  private static String OPEN_CD = "OPEN";
  private static String CANCEL_PAYMENT_CD = "CPAY";
  private static String CANCEL_DISBURSEMENT_CD = "CDIS";
  private static String EXTRACTED_CD = "EXTR";
  private static String PENDING_ACH_CD = "PACH";
  private static String HELD_TAX_EMPLOYEE_CD = "HTXE";
  private static String HELD_TAX_NRA_CD = "HTXN";
  private static String HELD_TAX_NRA_EMPL_CD = "HTXB";
  
  // Payment Change Codes
  private static String CANCEL_PAYMENT_CHNG_CD = "CP";
  private static String HOLD_CHNG_CD = "HP";
  private static String REMOVE_HOLD_CHNG_CD = "RHP";
  private static String CHANGE_IMMEDIATE_CHNG_CD = "IMP";
  private static String CANCEL_DISBURSEMENT_CHNG_CD = "CD";
  private static String CANCEL_REISSUE_CHNG_CD = "CRD";

  private PaymentGroupDao paymentGroupDao;
  private PaymentDetailDao paymentDetailDao;
  private AchAccountNumberDao achAccountNumberDao;
  private PaymentGroupHistoryDao paymentGroupHistoryDao;
  private ReferenceService referenceService;
  private GlPendingTransactionService glPendingTransactionService;
  private EnvironmentService environmentService;
  private MailService mailService;
  private KualiConfigurationService kualiConfigurationService;

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
  
  public void changeStatus(PaymentGroup paymentGroup, String newPaymentStatus, String changeStatus, String note, PdpUser user, PaymentGroupHistory paymentGroupHistory) {
    LOG.debug("changeStatus() enter method with new status of " + newPaymentStatus);
    Code cd = referenceService.getCode("PaymentChange", changeStatus);
    paymentGroupHistory.setPaymentChange((PaymentChange)cd);
    paymentGroupHistory.setOrigPaymentStatus(paymentGroup.getPaymentStatus());
    paymentGroupHistory.setChangeUser(user);
    paymentGroupHistory.setChangeNoteText(note);
    paymentGroupHistory.setPaymentGroup(paymentGroup);
    paymentGroupHistoryDao.save(paymentGroupHistory);

    Code code = referenceService.getCode("PaymentStatus", newPaymentStatus);
    if (paymentGroup.getPaymentStatus() != ((PaymentStatus)code)) {
      paymentGroup.setPaymentStatus((PaymentStatus)code);
    }
    paymentGroupDao.save(paymentGroup);

    LOG.debug("changeStatus() Status has been changed; exit method.");
  }
    /**
     * cancelPendingPayment()
     *   This method cancels the pending payment of the given payment id if the
     *   following rules apply.
     * 
     *   - Payment status must be: "open", "held", or "pending/ACH".
     * 
     * @param paymentGroupId   (Integer) Primary key of the PaymentGroup that the Payment Detail to be canceled belongs to.
     * @param paymentDetailId  (Integer) Primary key of the PaymentDetail that was actually cancelled.
     * @param note             (String)  Change note text entered by user.
     * @param user             (User)    Actor making change.
     */
    public void cancelPendingPayment(Integer paymentGroupId, Integer paymentDetailId, String note, PdpUser user, SecurityRecord sr) throws PdpException {
      // All actions must be performed on entire group not individual detail record
      LOG.debug("cancelPendingPayment() Enter method to cancel pending payment with group id = " + paymentGroupId);
      LOG.debug("cancelPendingPayment() payment detail id being cancelled = " + paymentDetailId);
      PaymentGroup paymentGroup = paymentGroupDao.get(paymentGroupId);
      if (paymentGroup == null) {
        LOG.debug("cancelPendingPayment() Pending payment not found; throw exception.");
        throw new CancelPaymentException("Pending payment not found.");
      }
      
      String paymentStatus = paymentGroup.getPaymentStatus().getCode();
      if (!(CANCEL_PAYMENT_CD.equals(paymentStatus))) {
        LOG.debug("cancelPendingPayment() Payment status is " + paymentStatus + "; continue with cancel.");
        if ((HELD_TAX_EMPLOYEE_CD.equals(paymentStatus)) || (HELD_TAX_NRA_CD.equals(paymentStatus)) || 
           (HELD_TAX_NRA_EMPL_CD.equals(paymentStatus))) {
          if (sr.isTaxHoldersRole() || sr.isSysAdminRole()){
            changeStatus(paymentGroup, CANCEL_PAYMENT_CD, CANCEL_PAYMENT_CHNG_CD, note, user);
            // set primary cancel indicator for EPIC to use
            PaymentDetail pd = paymentDetailDao.get(paymentDetailId);
            if (pd != null) {
              pd.setPrimaryCancelledPayment(Boolean.TRUE);
            }
            paymentDetailDao.save(pd);
            sendCancelEmail(paymentGroup,note,user);
            LOG.debug("cancelPendingPayment() Pending payment cancelled and mail was sent; exit method.");
          } else {
            LOG.debug("cancelPendingPayment() Payment status is " + paymentStatus + "; user does not have rights to cancel");
            throw new CancelPaymentException("Invalid status to cancel pending payment.");
          }
        } else if (OPEN_CD.equals(paymentStatus) || HELD_CD.equals(paymentStatus)) {
          if (sr.isCancelRole()){
            changeStatus(paymentGroup, CANCEL_PAYMENT_CD, CANCEL_PAYMENT_CHNG_CD, note, user);
            // set primary cancel indicator for EPIC to use
            PaymentDetail pd = paymentDetailDao.get(paymentDetailId);
            if (pd != null) {
              pd.setPrimaryCancelledPayment(Boolean.TRUE);
            }
            paymentDetailDao.save(pd);
            LOG.debug("cancelPendingPayment() Pending payment cancelled; exit method.");
          } else {
            LOG.debug("cancelPendingPayment() Payment status is " + paymentStatus + "; user does not have rights to cancel");
            throw new CancelPaymentException("Invalid status to cancel pending payment.");
          }
        } else {
          LOG.debug("cancelPendingPayment() Payment status is " + paymentStatus + "; cannot cancel payment in this status");
          throw new CancelPaymentException("Invalid status to cancel pending payment.");
        }
      } else {
        LOG.debug("cancelPendingPayment() Pending payment group has already been cancelled; exit method.");
      }
    }//end cancelPendingPayment()
    
    /**
     * holdPendingPayment()
     *   This method holds pending payment of the given payment id if the
     *   following rules apply.
     * 
     *   - Payment status must be: "open".
     * 
     * @param paymentGroupId   (Integer) Primary key of the PaymentGroup that the Payment Detail to be held belongs to.
     * @param note             (String)  Change note text entered by user.
     * @param user             (User)    Actor making change.
     */
    public void holdPendingPayment(Integer paymentGroupId, String note, PdpUser user) throws PdpException {
      // All actions must be performed on entire group not individual detail record
      LOG.debug("holdPendingPayment() Enter method to hold pending payment with id = " + paymentGroupId);
      PaymentGroup paymentGroup = paymentGroupDao.get(paymentGroupId);
      if (paymentGroup == null) {
        LOG.debug("holdPendingPayment() Pending payment not found; throw exception.");
        throw new CancelPaymentException("Pending payment not found.");
      }

      String paymentStatus = paymentGroup.getPaymentStatus().getCode();
      if (!(HELD_CD.equals(paymentStatus))) {
        if (OPEN_CD.equals(paymentStatus)) {
          LOG.debug("holdPendingPayment() Payment status is " + paymentStatus + "; continue with hold.");
          changeStatus(paymentGroup, HELD_CD, HOLD_CHNG_CD, note, user);
          LOG.debug("holdPendingPayment() Pending payment was put on hold; exit method.");
        } else {
          LOG.debug("holdPendingPayment() Payment status is " + paymentStatus + "; cannot hold payment in this status");
          throw new CancelPaymentException("Invalid status to hold pending payment.");
        }
      } else {
        LOG.debug("holdPendingPayment() Pending payment group has already been held; exit method.");
      }

    }//end holdPendingPayment()
    
    /**
     * removeHoldPendingPayment()
     *   This method removes holds on pending payments of the given payment id if the
     *   following rules apply.
     * 
     *   - Payment status must be: "held".
     * 
     * @param paymentGroupId   (Integer) Primary key of the PaymentGroup that the Payment Detail to be un-held belongs to.
     * @param note             (String)  Change note text entered by user.
     * @param user             (User)    Actor making change.
     * @param sr               (SecurityRecord)  User's rights
     */
    public void removeHoldPendingPayment(Integer paymentGroupId, String note, PdpUser user, SecurityRecord sr) throws PdpException {
      // All actions must be performed on entire group not individual detail record
      LOG.debug("removeHoldPendingPayment() Enter method to hold pending payment with id = " + paymentGroupId);
      PaymentGroup paymentGroup = paymentGroupDao.get(paymentGroupId);
      if (paymentGroup == null) {
        LOG.debug("removeHoldPendingPayment() Payment not found; throw exception.");
        throw new CancelPaymentException("Pending payment not found.");
      }

      String paymentStatus = paymentGroup.getPaymentStatus().getCode();
      if (!(OPEN_CD.equals(paymentStatus))) {
        LOG.debug("removeHoldPendingPayment() Payment status is " + paymentStatus + "; continue with hold removal.");
        if ((HELD_TAX_EMPLOYEE_CD.equals(paymentStatus)) || (HELD_TAX_NRA_CD.equals(paymentStatus)) || 
            (HELD_TAX_NRA_EMPL_CD.equals(paymentStatus))) {
          if (sr.isTaxHoldersRole() || sr.isSysAdminRole()){
            changeStatus(paymentGroup, OPEN_CD, REMOVE_HOLD_CHNG_CD, note, user);
            LOG.debug("removeHoldPendingPayment() Pending payment was taken off hold; exit method.");
          } else {
            LOG.debug("removeHoldPendingPayment() Payment status is " + paymentStatus + "; user does not have rights to cancel");
            throw new CancelPaymentException("Invalid status to hold pending payment.");
          }
        } else if (HELD_CD.equals(paymentStatus)) {
          if (sr.isHoldRole()){
            changeStatus(paymentGroup, OPEN_CD, REMOVE_HOLD_CHNG_CD, note, user);
            LOG.debug("removeHoldPendingPayment() Pending payment was taken off hold; exit method.");
          } else {
            LOG.debug("removeHoldPendingPayment() Payment status is " + paymentStatus + "; user does not have rights to cancel");
            throw new CancelPaymentException("Invalid status to hold pending payment.");
          }
        } else {
          LOG.debug("removeHoldPendingPayment() Payment status is " + paymentStatus + "; cannot remove hold on payment in this status");
          throw new CancelPaymentException("Invalid status to hold pending payment.");
        }
      } else {
        LOG.debug("removeHoldPendingPayment() Pending payment group has already been un-held; exit method.");
      }
    }//end removeHoldPendingPayment()
    
    public void changeImmediateFlag(Integer paymentGroupId, String note, PdpUser user) {
      // All actions must be performed on entire group not individual detail record
      LOG.debug("changeImmediateFlag() Enter method to hold pending payment with id = " + paymentGroupId);
      PaymentGroupHistory paymentGroupHistory = new PaymentGroupHistory();
      PaymentGroup paymentGroup = paymentGroupDao.get(paymentGroupId);

      paymentGroupHistory.setOrigProcessImmediate(paymentGroup.getProcessImmediate());
      
      if (paymentGroup.getProcessImmediate().equals(new Boolean("True"))) {
        paymentGroup.setProcessImmediate(new Boolean("False"));
      } else {
        paymentGroup.setProcessImmediate(new Boolean("True"));
      }

      changeStatus(paymentGroup,paymentGroup.getPaymentStatus().getCode(), CHANGE_IMMEDIATE_CHNG_CD, note,user,paymentGroupHistory);
      LOG.debug("changeImmediateFlag() exit method.");
    }
    
    /**
     * cancelDisbursement()
     *   This method cancels all disbursements with the same disbursment number as that 
     *   of the given payment id if the following rules apply.
     * 
     *   - Payment status must be: "extr".
     * 
     * @param paymentGroupId   (Integer) Primary key of the PaymentGroup that the Payment Detail to be cancelled belongs to.
     * @param paymentDetailId  (Integer) Primary key of the PaymentDetail that was actually cancelled.
     * @param note             (String)  Change note text entered by user.
     * @param user             (User)    Actor making change.
     */
    public void cancelDisbursement(Integer paymentGroupId, Integer paymentDetailId, String note, PdpUser user) throws PdpException {
      // All actions must be performed on entire group not individual detail record
      LOG.debug("cancelDisbursement() Enter method to cancel disbursement with id = " + paymentGroupId);
      PaymentGroup paymentGroup = paymentGroupDao.get(paymentGroupId);
      if (paymentGroup == null) {
        LOG.debug("cancelDisbursement() Disbursement not found; throw exception.");
        throw new CancelPaymentException("Disbursement not found.");
      }
      
      String paymentStatus = paymentGroup.getPaymentStatus().getCode();
      if (!(CANCEL_DISBURSEMENT_CD.equals(paymentStatus))) {
//        if ( ( (EXTRACTED_CD.equals(paymentStatus)) || 
//            (PENDING_ACH_CD.equals(paymentStatus)) ) && 
//          (paymentGroup.getDisbursementDate() != null) ) {
        if ( ( (EXTRACTED_CD.equals(paymentStatus)) && (paymentGroup.getDisbursementDate() != null) ) || 
             (PENDING_ACH_CD.equals(paymentStatus)) ) {
          LOG.debug("cancelDisbursement() Payment status is " + paymentStatus + "; continue with cancel.");
          List allDisbursementPaymentGroups = paymentGroupDao.getByDisbursementNumber(paymentGroup.getDisbursementNbr());
          
          for (Iterator iter = allDisbursementPaymentGroups.iterator(); iter.hasNext();) {
            PaymentGroup element = (PaymentGroup) iter.next();
            PaymentGroupHistory pgh = new PaymentGroupHistory();
            if ((element.getDisbursementType() != null) && 
                (element.getDisbursementType().getCode().equals("CHCK"))) {
              pgh.setPmtCancelExtractStat(new Boolean("False"));
            }
            changeStatus(element, CANCEL_DISBURSEMENT_CD, CANCEL_DISBURSEMENT_CHNG_CD, note, user, pgh);
            glPendingTransactionService.createCancellationTransaction(element);
          }
          // set primary cancel indicator for EPIC to use
          PaymentDetail pd = paymentDetailDao.get(paymentDetailId);
          if (pd != null) {
            pd.setPrimaryCancelledPayment(Boolean.TRUE);
          }
          paymentDetailDao.save(pd);
          
          LOG.debug("cancelDisbursement() Disbursement cancelled; exit method.");
        } else {
          LOG.debug("cancelDisbursement() Payment status is " + paymentStatus + " and disbursement date is " + paymentGroup.getDisbursementDate() + "; cannot cancel payment in this status");
          throw new CancelPaymentException("Invalid disbursement to cancel.");
        }
      } else {
        LOG.debug("cancelDisbursement() Disbursement has already been cancelled; exit method.");
      }
    }//end cancelDisbursement()
    
    /**
     * cancelReissueDisbursement()
     *   This method cancels and re-opens all disbursements with the same disbursment  
     *   number as that of the given payment id if the following rules apply.
     * 
     *   - Payment status must be: "extr".
     * 
     * @param paymentGroupId   (Integer) Primary key of the PaymentGroup that the Payment Detail to be canceled/reissued belongs to.
     * @param note             (String)  Change note text entered by user.
     * @param user             (User)    Actor making change.
     */
    public void cancelReissueDisbursement(Integer paymentGroupId, String note, PdpUser user) throws PdpException {
      // All actions must be performed on entire group not individual detail record
      LOG.debug("cancelReissueDisbursement() Enter method to cancel disbursement with id = " + paymentGroupId);
      PaymentGroup paymentGroup = paymentGroupDao.get(paymentGroupId);
      if (paymentGroup == null) {
        LOG.debug("cancelReissueDisbursement() Disbursement not found; throw exception.");
        throw new CancelPaymentException("Disbursement not found.");
      }
      
      String paymentStatus = paymentGroup.getPaymentStatus().getCode();
      if (!(OPEN_CD.equals(paymentStatus))) {
//        if (((EXTRACTED_CD.equals(paymentStatus)) || (PENDING_ACH_CD.equals(paymentStatus))) && (paymentGroup.getDisbursementDate() != null)) {
        if ( ((EXTRACTED_CD.equals(paymentStatus)) && (paymentGroup.getDisbursementDate() != null)) || 
             (PENDING_ACH_CD.equals(paymentStatus)) ) {
          LOG.debug("cancelReissueDisbursement() Payment status is " + paymentStatus + "; continue with cancel.");
          List allDisbursementPaymentGroups = paymentGroupDao.getByDisbursementNumber(paymentGroup.getDisbursementNbr());
          
          for (Iterator iter = allDisbursementPaymentGroups.iterator(); iter.hasNext();) {
            PaymentGroup pg = (PaymentGroup) iter.next();
            PaymentGroupHistory pgh = new PaymentGroupHistory();
  
            if ((pg.getDisbursementType() != null) && 
                (pg.getDisbursementType().getCode().equals("CHCK"))) {
              pgh.setPmtCancelExtractStat(new Boolean("False"));
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
            
            glPendingTransactionService.createCancelReissueTransaction(pg);
            LOG.debug("cancelReissueDisbursement() Status is '" + paymentStatus + "; delete row from AchAccountNumber table.");
            AchAccountNumber achAccountNumber = pg.getAchAccountNumber();
            if (achAccountNumber != null) {
              achAccountNumberDao.delete(achAccountNumber);
              pg.setAchAccountNumber(null);
            }
            
            pg.setBank(null);
            pg.setDisbursementDate(null);
            pg.setAchBankRoutingNbr(null);
            pg.setAchAccountType(null);
            pg.setPhysCampusProcessCd(null);
            pg.setDisbursementNbr(null);
            pg.setAdviceEmailAddress(null);
            pg.setDisbursementType(null);
            pg.setProcess(null);
            changeStatus(pg, OPEN_CD, CANCEL_REISSUE_CHNG_CD, note, user, pgh);
          }
          
          LOG.debug("cancelReissueDisbursement() Disbursement cancelled and reissued; exit method.");
        } else {
          LOG.debug("cancelReissueDisbursement() Payment status is " + paymentStatus + " and disbursement date is " + paymentGroup.getDisbursementDate() + "; cannot cancel payment");
          throw new CancelPaymentException("Invalid disbursement to cancel and reissue.");
        }
      } else {
        LOG.debug("cancelReissueDisbursement() Disbursement already cancelled and reissued; exit method.");
      }
    }//end cancelReissueDisbursement()
    
    public void sendCancelEmail(PaymentGroup paymentGroup, String note, PdpUser user) {
      LOG.debug("sendCancelEmail() starting");
      MailMessage message = new MailMessage();
      
      String env = environmentService.getEnvironment().toUpperCase();
      if ("PRD".equals(env)){
        message.setSubject("Cancelled Payments");
      } else {
        message.setSubject(env + "-PDP --- Cancelled Payment by Tax");
      }
      
      CustomerProfile cp = paymentGroup.getBatch().getCustomerProfile();
      String toAddresses = cp.getAdviceReturnEmailAddr();
      String toAddressList[] = toAddresses.split(",");
      
      if (toAddressList.length > 0) {
        for (int i = 0; i < toAddressList.length; i++) {
          if (toAddressList[i] != null) {
            message.addToAddress(toAddressList[i].trim());
          }
        }
      }
//      message.addToAddress(cp.getAdviceReturnEmailAddr());

      String ccAddresses = kualiConfigurationService.getApplicationParameterValue(PdpConstants.PDP_APPLICATION,PdpConstants.ApplicationParameterKeys.TAX_CANCEL_EMAIL_LIST);
      String ccAddressList[] = ccAddresses.split(",");

      if (ccAddressList.length > 0) {
        for (int i = 0; i < ccAddressList.length; i++) {
          if (ccAddressList[i] != null) {
            message.addCcAddress(ccAddressList[i].trim());
          }
        }
      }

      StringBuffer body = new StringBuffer();
      if (paymentGroup.getPaymentDetails().size() > 1){
        body.append("The following payments have been cancelled by the Financial Management Services Tax Department.  The payments were cancelled for the following reason:\n\n");
      } else {
        body.append("The following payment has been cancelled by the Financial Management Services Tax Department.  The payment was cancelled for the following reason:\n\n");
      }
      body.append(note + "\n\n");
      String taxEmail = kualiConfigurationService.getApplicationParameterValue(PdpConstants.PDP_APPLICATION,PdpConstants.ApplicationParameterKeys.TAX_GROUP_EMAIL_ADDRESS);
      if (GeneralUtilities.isStringEmpty(taxEmail)){
        body.append("Please contact the Financial Management Services Tax Department if you have questions regarding this cancellation.\n\n");
      } else {
        body.append("Please contact the Financial Management Services Tax Department at " + 
            taxEmail + " if you have questions regarding this cancellation.\n\n");
      }
      
      if (paymentGroup.getPaymentDetails().size() > 1){
        body.append("The following payment details were cancelled:\n\n");
      } else {
        body.append("The following payment detail was cancelled:\n\n");
      }
      for (Iterator iter = paymentGroup.getPaymentDetails().iterator(); iter.hasNext();) {
        PaymentDetail pd = (PaymentDetail) iter.next();
        body.append("Payee Name: " + paymentGroup.getPayeeName() + "\n");
        body.append("Net Payment Amount: " + pd.getNetPaymentAmount() + "\n");
        body.append("Source Document Number: " + pd.getCustPaymentDocNbr() + "\n");
        body.append("Invoice Number: " + pd.getInvoiceNbr() + "\n");
        body.append("Purchase Order Number: " + pd.getPurchaseOrderNbr() + "\n");
        body.append("Payment Detail ID: " + pd.getId() + "\n\n");
        
      }
      
      if (paymentGroup.getPaymentDetails().size() > 1){
        body.append("The cancelled payment details were sent to PDP as part of the following batch:\n\n");
      } else {
        body.append("The cancelled payment detail was sent to PDP as part of the following batch:\n\n");
      }
      body.append("Batch ID: " + paymentGroup.getBatch().getId() + "\n");
      body.append("Chart: " + cp.getChartCode() + "\n");
      body.append("Organization: " + cp.getOrgCode() + "\n");
      body.append("Sub Unit: " + cp.getSubUnitCode() + "\n");
      body.append("Creation Date: " + paymentGroup.getBatch().getCustomerFileCreateTimestamp() + "\n");
      body.append("Payment Count: " + paymentGroup.getBatch().getPaymentCount() + "\n");
      body.append("Payment Total Amount: " + paymentGroup.getBatch().getPaymentTotalAmount());

      message.setMessage(body.toString());
      try {
        mailService.sendMessage(message);
      } catch (InvalidAddressException e) {
        LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
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
    public void setPaymentDetailDao(PaymentDetailDao dao) {
      paymentDetailDao = dao;
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
     * inject
     * @param dao
     */
    public void setAchAccountNumberDao(AchAccountNumberDao dao) {
      achAccountNumberDao = dao;
    }
    
    /**
     * inject
     * @param service
     */
    public void setGlPendingTransactionService(GlPendingTransactionService service) {
      glPendingTransactionService = service;
    }
    
    /**
     * inject
     * @param service
     */
    public void setKualiConfigurationService(KualiConfigurationService kcs) {
        kualiConfigurationService = kcs;
    }

    /**
     * inject
     * @param service
     */
    public void setEnvironmentService(EnvironmentService environmentService) {
      this.environmentService = environmentService;
    }
    
    /**
     * inject
     * @param service
     */
    public void setMailService(MailService mailService) {
      this.mailService = mailService;
    }
}

/*
 * Created on Aug 24, 2004
 */
package org.kuali.module.pdp.action.paymentmaintenance;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.pdp.action.BaseAction;
import org.kuali.module.pdp.bo.Batch;
import org.kuali.module.pdp.bo.PaymentDetail;
import org.kuali.module.pdp.form.paymentmaintenance.PaymentMaintenanceForm;
import org.kuali.module.pdp.service.BatchMaintenanceService;
import org.kuali.module.pdp.service.PaymentMaintenanceService;
import org.kuali.module.pdp.service.SecurityRecord;
import org.kuali.module.pdp.utilities.GeneralUtilities;


/**
 * @author delyea
 *
 */
public class PaymentMaintenanceAction extends BaseAction {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentMaintenanceAction.class);
  private PaymentMaintenanceService paymentMaintenanceService;
  private BatchMaintenanceService batchMaintenanceService;

  public PaymentMaintenanceAction() {
      setPaymentMaintenanceService( (PaymentMaintenanceService)SpringServiceLocator.getService("pdpPaymentMaintenanceSerivce") );
      setBatchMaintenanceService( (BatchMaintenanceService)SpringServiceLocator.getService("pdpBatchMaintenanceService") );
  }

  public void setPaymentMaintenanceService(PaymentMaintenanceService p) {
    this.paymentMaintenanceService = p;
  }
  
  public void setBatchMaintenanceService(BatchMaintenanceService p) {
    this.batchMaintenanceService = p;
  }

  protected boolean isAuthorized(ActionMapping mapping, ActionForm form, HttpServletRequest request,
      HttpServletResponse response) {
    SecurityRecord sr = getSecurityRecord(request);
    return sr.isLimitedViewRole() || sr.isViewAllRole() || sr.isViewIdRole() || sr.isViewBankRole() || sr.isTaxHoldersRole();
  }

  protected ActionForward executeLogic(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    LOG.debug("executeLogic() started");

    PaymentDetail pd = (PaymentDetail)request.getSession().getAttribute("PaymentDetail");
    List batchSearchList = (List)request.getSession().getAttribute("batchIndivSearchResults");
    SecurityRecord sr = (SecurityRecord)request.getSession().getAttribute("SecurityRecord");
    PaymentMaintenanceForm pmf = (PaymentMaintenanceForm)form;
    String forward = "update";

    ActionErrors actionErrors = new ActionErrors();
    String buttonPressed = GeneralUtilities.whichButtonWasPressed(request);

    LOG.debug("executeLogic() PaymentMaintenanceAction ButtonPressed was " + buttonPressed);
    if (buttonPressed != null) {
      if (buttonPressed.startsWith("btnBatch")) {
        Batch b = (Batch)request.getSession().getAttribute("BatchDetail");
        if (b != null){
          // Action Originated from BatchDetail Screen
          if (buttonPressed.startsWith("btnBatchHold")){
            pmf.setAction("batchHold");
            if (batchMaintenanceService.doBatchPaymentsHaveOpenStatus(b.getId())) {
              pmf.setChangeId(b.getId());
            } else {
              actionErrors.add("errors", new ActionMessage("paymentMaintenanceAction.batchStatus.notAllOpen"));
            }
          } else if (buttonPressed.startsWith("btnBatchCancel")){
            pmf.setAction("batchCancel");
            if (batchMaintenanceService.doBatchPaymentsHaveOpenOrHeldStatus(b.getId())) {
              pmf.setChangeId(b.getId());
            } else {
              actionErrors.add("errors", new ActionMessage("paymentMaintenanceAction.batchStatus.notAllOpenOrHeld"));
            }
          } else if (buttonPressed.startsWith("btnBatchRemoveHold")){
            pmf.setAction("batchRemoveHold");
            if (batchMaintenanceService.doBatchPaymentsHaveHeldStatus(b.getId())) {
              pmf.setChangeId(b.getId());
            } else {
              actionErrors.add("errors", new ActionMessage("paymentMaintenanceAction.batchStatus.notAllHeld"));
            }
          } else {
            // Invalid Button was Pressed
            return mapping.findForward("pdp_system_error");
          }
          if (!actionErrors.isEmpty()) {
            forward = "batch";
          }
        } else {
          LOG.info("executeLogic() Batch object 'b' from session is null");
          forward = "pdp_session_timeout";
        }
      } else if ((pd == null) && (batchSearchList == null)) {
        LOG.info("executeLogic() PaymentDetail object 'pd' or 'batchSearchList' variable is null");
        forward = "pdp_session_timeout";
        
      } else if (buttonPressed.startsWith("btnUpdate")) {
        // Action Originated from Update page
        if (buttonPressed.startsWith("btnUpdateSave")) {
          String action = pmf.getAction();
          LOG.debug("executeLogic() string 'action' is " + action);
          
          // All Actions must be performed on Payment Group that Payment Detail is a member of
          if (action.equals("Hold")) {
            if (sr.isHoldRole()) {
              LOG.debug("executeLogic() running holdPendingPayment now");
              // ChangeID was previous set to PaymentGroupID 
              paymentMaintenanceService.holdPendingPayment(pmf.getChangeId(),pmf.getChangeText(),getUser(request));
              actionErrors.add("success", new ActionMessage("success.paymentUpdate.saved"));
            } else {
              actionErrors.add("error", new ActionMessage("paymentMaintenanceAction.paymentUpdate.notAuthorized"));
            }
          } else if (action.equals("RemoveHold")) {
              if (sr.isHoldRole() || sr.isTaxHoldersRole() || sr.isSysAdminRole()) {
                LOG.debug("executeLogic() running removeHoldPendingPayment now");
                // ChangeID was previous set to PaymentGroupID 
                paymentMaintenanceService.removeHoldPendingPayment(pmf.getChangeId(),pmf.getChangeText(),getUser(request), sr);
                actionErrors.add("success", new ActionMessage("success.paymentUpdate.saved"));
              } else {
                actionErrors.add("error", new ActionMessage("paymentMaintenanceAction.paymentUpdate.notAuthorized"));
              }
          } else if (action.equals("batchHold")) {
            if (sr.isHoldRole()) {
              LOG.debug("executeLogic() running holdPendingBatch now");
              // ChangeID was previous set to BatchID 
              batchMaintenanceService.holdPendingBatch(pmf.getChangeId(),pmf.getChangeText(),getUser(request));
              actionErrors.add("success", new ActionMessage("success.paymentUpdate.saved"));
            } else {
              actionErrors.add("error", new ActionMessage("paymentMaintenanceAction.paymentUpdate.notAuthorized"));
            }
          } else if (action.equals("batchRemoveHold")) {
            if (sr.isHoldRole()) {
              LOG.debug("executeLogic() running removeBatchHold now");
              // ChangeID was previous set to BatchID 
              batchMaintenanceService.removeBatchHold(pmf.getChangeId(),pmf.getChangeText(),getUser(request));
              actionErrors.add("success", new ActionMessage("success.paymentUpdate.saved"));
            } else {
              actionErrors.add("error", new ActionMessage("paymentMaintenanceAction.paymentUpdate.notAuthorized"));
            }
          } else if (action.equals("Cancel")) {
            if (sr.isCancelRole() || sr.isTaxHoldersRole() || sr.isSysAdminRole()) {
              LOG.debug("executeLogic() running cancelPendingPayment now");
              // ChangeID was previous set to PaymentGroupID 
              paymentMaintenanceService.cancelPendingPayment(pmf.getChangeId(),pmf.getPaymentDetailId(),pmf.getChangeText(),getUser(request), sr);
              actionErrors.add("success", new ActionMessage("success.paymentUpdate.saved"));
            } else {
              actionErrors.add("error", new ActionMessage("paymentMaintenanceAction.paymentUpdate.notAuthorized"));
            }
          } else if (action.equals("batchCancel")) {
            if (sr.isCancelRole()) {
              LOG.debug("executeLogic() running cancelPendingBatch now");
              // ChangeID was previous set to BatchID 
              batchMaintenanceService.cancelPendingBatch(pmf.getChangeId(),pmf.getChangeText(),getUser(request));
              actionErrors.add("success", new ActionMessage("success.paymentUpdate.saved"));
            } else {
              actionErrors.add("error", new ActionMessage("paymentMaintenanceAction.paymentUpdate.notAuthorized"));
            }
          } else if (action.equals("ChangeImmediate")) {
            if (sr.isProcessRole()) {
              LOG.debug("executeLogic() running changeImmediate now");
              // ChangeID was previous set to PaymentGroupID 
              paymentMaintenanceService.changeImmediateFlag(pmf.getChangeId(),pmf.getChangeText(),getUser(request));
              actionErrors.add("success", new ActionMessage("success.paymentUpdate.saved"));
            } else {
              actionErrors.add("error", new ActionMessage("paymentMaintenanceAction.paymentUpdate.notAuthorized"));
            }
          } else if (action.equals("DisbursementCancel")) {
            if (sr.isCancelRole()) {
              LOG.debug("executeLogic() running disbursementCancel now");
              // ChangeID was previous set to PaymentGroupID 
              paymentMaintenanceService.cancelDisbursement(pmf.getChangeId(), pmf.getPaymentDetailId(),pmf.getChangeText(),getUser(request));
              actionErrors.add("success", new ActionMessage("success.paymentUpdate.saved"));
            } else {
              actionErrors.add("error", new ActionMessage("paymentMaintenanceAction.paymentUpdate.notAuthorized"));
            }
          } else if (action.equals("ReIssueCancel")) {
            if (sr.isCancelRole()) {
              LOG.debug("executeLogic() running cancelReissueDisbursement now");
              // ChangeID was previous set to PaymentGroupID 
              paymentMaintenanceService.cancelReissueDisbursement(pmf.getChangeId(),pmf.getChangeText(),getUser(request));
              actionErrors.add("success", new ActionMessage("success.paymentUpdate.saved"));
            } else {
              actionErrors.add("error", new ActionMessage("paymentMaintenanceAction.paymentUpdate.notAuthorized"));
            }
         } 
          
          if (pmf.getAction() != null) {
            if (pmf.getAction().indexOf("batch") >= 0) {
              forward = "batch";
            } else if (pmf.getAction().indexOf("batch") < 0) {
              forward = "individual";
            } 
          }
        } else if (buttonPressed.startsWith("btnUpdateClear")) {
          pmf.setChangeText(null);
        } else if (buttonPressed.startsWith("btnUpdateCancel")) {
          if (pmf.getAction() != null) {
            LOG.debug("executeLogic() Form field holding Action is " + pmf.getAction());
            LOG.debug("executeLogic() Index is " + pmf.getAction().indexOf("batch"));
            if (pmf.getAction().indexOf("batch") >= 0) {
              forward = "batch";
            } else if (pmf.getAction().indexOf("batch") < 0) {
              forward = "detail";
//              forward = "individual";
            } 
          } 
        }
      } else {
        // Action Originated from PaymentDetail Screen
        // ChangeID is set to the Payment Group ID that the payment detail belongs to
        // Only Payment Groups have actions go against them... a payment detail may not be changed
        // Payment Detail ID is stored for any EPIC payments/disbursements that might be cancelled
        pmf.setChangeId(pd.getPaymentGroup().getId());
        pmf.setPaymentDetailId(pd.getId());
        if (buttonPressed.startsWith("btnCancel")){
          pmf.setAction("Cancel");
        } else if (buttonPressed.startsWith("btnHold")){
          pmf.setAction("Hold");
        } else if (buttonPressed.startsWith("btnRemoveHold")){
          pmf.setAction("RemoveHold");
        } else if (buttonPressed.startsWith("btnChangeImmediate")){
          pmf.setAction("ChangeImmediate");
        } else if (buttonPressed.startsWith("btnReIssueCancel")){
          pmf.setAction("ReIssueCancel");
        } else if (buttonPressed.startsWith("btnDisbursementCancel")) {
          pmf.setAction("DisbursementCancel");
        } else {
          // Invalid Button was Pressed
          return mapping.findForward("pdp_system_error");
        }
      }
    }       
  

    // If we had errors, save them
    if (!actionErrors.isEmpty()) {
      saveErrors(request, actionErrors);
    }

    request.setAttribute("PaymentMaintenanceForm", pmf);
    return mapping.findForward(forward);
  }

}

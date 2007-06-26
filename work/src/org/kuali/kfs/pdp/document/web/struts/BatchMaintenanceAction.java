/*
 * Created on Aug 24, 2004
 */
package org.kuali.module.pdp.action.paymentmaintenance;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.pdp.action.BaseAction;
import org.kuali.module.pdp.bo.Batch;
import org.kuali.module.pdp.form.paymentmaintenance.PaymentMaintenanceForm;
import org.kuali.module.pdp.service.BatchMaintenanceService;
import org.kuali.module.pdp.service.SecurityRecord;
import org.kuali.module.pdp.utilities.GeneralUtilities;


/**
 * @author delyea
 *
 */
public class BatchMaintenanceAction extends BaseAction {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchMaintenanceAction.class);
  private BatchMaintenanceService batchMaintenanceService;

  public BatchMaintenanceAction() {
      setBatchMaintenanceService( (BatchMaintenanceService)SpringServiceLocator.getService("pdpBatchMaintenanceService") );
  }

  public void setBatchMaintenanceService(BatchMaintenanceService p) {
    this.batchMaintenanceService = p;
  }

  protected boolean isAuthorized(ActionMapping mapping, ActionForm form, HttpServletRequest request,
      HttpServletResponse response) {
    SecurityRecord sr = getSecurityRecord(request);
    return sr.isLimitedViewRole() || sr.isViewAllRole() || sr.isViewIdRole() || sr.isViewBankRole();
  }

  protected ActionForward executeLogic(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    LOG.debug("executeLogic() started");

    HttpSession session = request.getSession();
    List batchSearchList = (List)session.getAttribute("batchIndivSearchResults");
    SecurityRecord sr = (SecurityRecord)session.getAttribute("SecurityRecord");
    Batch b = (Batch)session.getAttribute("BatchDetail");
    PaymentMaintenanceForm pmf = (PaymentMaintenanceForm)form;
    String forward = "update";

    ActionErrors actionErrors = new ActionErrors();
    String buttonPressed = GeneralUtilities.whichButtonWasPressed(request);

    LOG.debug("executeLogic() BatchMaintenanceAction ButtonPressed was " + buttonPressed);
    if (buttonPressed != null) {
      if (b == null) {
        LOG.info("executeLogic() Batch object 'b' is null");
        return mapping.findForward("pdp_session_timeout");
        
      } else if (buttonPressed.startsWith("btnBatch")) {
        // Action Originated from BatchDetail Screen
        if (buttonPressed.startsWith("btnBatchHold")){
          pmf.setAction("batchHold");
          if (batchMaintenanceService.doBatchPaymentsHaveOpenStatus(b.getId())) {
            pmf.setChangeId(b.getId());
          } else {
            actionErrors.add("errors", new ActionMessage("batchMaintenanceAction.batchStatus.notAllOpen"));
          }
        } else if (buttonPressed.startsWith("btnBatchCancel")){
          pmf.setAction("batchCancel");
          if (batchMaintenanceService.doBatchPaymentsHaveOpenOrHeldStatus(b.getId())) {
            pmf.setChangeId(b.getId());
          } else {
            actionErrors.add("errors", new ActionMessage("batchMaintenanceAction.batchStatus.notAllOpenOrHeld"));
          }
        } else if (buttonPressed.startsWith("btnBatchRemoveHold")){
          pmf.setAction("batchRemoveHold");
          if (batchMaintenanceService.doBatchPaymentsHaveHeldStatus(b.getId())) {
            pmf.setChangeId(b.getId());
          } else {
            actionErrors.add("errors", new ActionMessage("batchMaintenanceAction.batchStatus.notAllHeld"));
          }
        } else {
          // Invalid Button was Pressed
          return mapping.findForward("pdp_system_error");
        }
        if (!actionErrors.isEmpty()) {
          forward = "batch";
        }

      } else if (buttonPressed.startsWith("btnUpdate")) {
        // Action Originated from Update page
        if (buttonPressed.startsWith("btnUpdateClear")) {
          pmf.setChangeText(null);
          return mapping.findForward("update");
          
        } else if (buttonPressed.startsWith("btnUpdateSave")) {
          String action = pmf.getAction();
          LOG.debug("executeLogic() string 'action' is " + action);
          
          // All Actions must be performed on Payment Group that Payment Detail is a member of
          if (action.equals("batchHold")) {
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
          } else if (action.equals("batchCancel")) {
            if (sr.isCancelRole()) {
              LOG.debug("executeLogic() running cancelPendingBatch now");
              // ChangeID was previous set to BatchID 
              batchMaintenanceService.cancelPendingBatch(pmf.getChangeId(),pmf.getChangeText(),getUser(request));
              actionErrors.add("success", new ActionMessage("success.paymentUpdate.saved"));
            } else {
              actionErrors.add("error", new ActionMessage("paymentMaintenanceAction.paymentUpdate.notAuthorized"));
            }
         } 
        } 
        forward = "batch";
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

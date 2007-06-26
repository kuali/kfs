/*
 * Created on Sep 8, 2004
 *
 */
package org.kuali.module.pdp.action.disbursementnumbermaintenance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.pdp.action.BaseAction;
import org.kuali.module.pdp.bo.Bank;
import org.kuali.module.pdp.bo.DisbursementNumberRange;
import org.kuali.module.pdp.form.disbursementnumbermaintenance.DisbursementNumberMaintenanceForm;
import org.kuali.module.pdp.service.BankService;
import org.kuali.module.pdp.service.DisbursementNumberRangeService;
import org.kuali.module.pdp.service.SecurityRecord;


/**
 * @author delyea
 *
 */
public class DisbursementNumberMaintenanceSaveAction extends BaseAction {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementNumberMaintenanceSaveAction.class);

  private DisbursementNumberRangeService disbursementNumberRangeService;
  private BankService bankService;

  public DisbursementNumberMaintenanceSaveAction() {
      super();
      setDisbursementNumberRangeService( (DisbursementNumberRangeService)SpringServiceLocator.getService("pdpDisbursementNumberRangeService") );
      setBankService( (BankService)SpringServiceLocator.getService("pdpBankService") );
  }

  public void setDisbursementNumberRangeService(DisbursementNumberRangeService d) {
    disbursementNumberRangeService = d;
  }

  public void setBankService(BankService b) {
    bankService = b;
  }

  protected boolean isAuthorized(ActionMapping mapping, ActionForm form, HttpServletRequest request,
      HttpServletResponse response) {
    SecurityRecord sr = getSecurityRecord(request);
    return sr.isRangesRole();
  }

  protected ActionForward executeLogic(ActionMapping mapping, ActionForm form, HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    LOG.debug("executeLogic() started");
    String forward = "list";
    String btnPressed = whichButtonWasPressed(request);
    LOG.debug("executeLogic() Button Pressed was " + btnPressed);
    ActionErrors actionErrors = new ActionErrors();

    if ( "btnCancel".equals(btnPressed) ) {
      LOG.debug("executeLogic() Cancelled, returning to list");
    } else if ( "btnSave".equals(btnPressed) ) {
      LOG.debug("executeLogic() Saving");

      DisbursementNumberMaintenanceForm dnmf = (DisbursementNumberMaintenanceForm)form;
      DisbursementNumberRange dnr = dnmf.getDisbursementNumberRange();

      if ( (dnmf.getId() == null) || (dnmf.getId().intValue() == 0) ) {
        dnr.setId(null);
      } else {
        dnr.setId(dnmf.getId());
      }
      dnr.setBank( (Bank)bankService.get(dnmf.getBankId()) );
      dnr.setLastUpdateUser(getUser(request));
      

      try {
        disbursementNumberRangeService.save(dnr);
        actionErrors.add("success", new ActionMessage("success.disbursementNumberRange.saved"));
      } catch (Exception e) {
        actionErrors.add("error", new ActionMessage("disbursementNumberMaintenanceSaveAction.save.error"));
      }
      
    }
    
    if (!actionErrors.isEmpty()) { // If we had errors, save them.
      saveErrors(request, actionErrors);
    }
    return mapping.findForward(forward);
  }
}

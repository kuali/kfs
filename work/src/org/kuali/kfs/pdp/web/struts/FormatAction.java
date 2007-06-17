/*
 * Created on Aug 12, 2004
 *
 */
package org.kuali.module.pdp.action.format;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.pdp.action.BaseAction;
import org.kuali.module.pdp.form.format.FormatProcessForm;
import org.kuali.module.pdp.service.DisbursementRangeExhaustedException;
import org.kuali.module.pdp.service.FormatResult;
import org.kuali.module.pdp.service.FormatService;
import org.kuali.module.pdp.service.MissingDisbursementRangeException;
import org.kuali.module.pdp.service.SecurityRecord;


/**
 * @author jsissom
 *
 */
public class FormatAction extends BaseAction {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FormatAction.class);

  private FormatService formatService;

  public FormatAction() {
      super();
      setFormatService( (FormatService)SpringServiceLocator.getService("pdpFormatService") );
  }

  public void setFormatService(FormatService fs) {
    formatService = fs;
  }

  protected boolean isAuthorized(ActionMapping mapping, ActionForm form, HttpServletRequest request,
      HttpServletResponse response) {
    SecurityRecord sr = getSecurityRecord(request);
    return sr.isProcessRole();
  }

  protected ActionForward executeLogic(ActionMapping mapping, ActionForm form, HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    LOG.debug("executeLogic() started");

    FormatProcessForm fpf = (FormatProcessForm)form;

    if ( "btnCancel".equals(whichButtonWasPressed(request)) ) {
      // Clear the format
      formatService.clearUnfinishedFormat(fpf.getProcId());
      return mapping.findForward("cleared");
    }

    try {
      List results = formatService.performFormat(fpf.getProcId());
      Collections.sort(results);
      FormatResult total = new FormatResult();
      for (Iterator iter = results.iterator(); iter.hasNext();) {
        FormatResult element = (FormatResult)iter.next();
        total.setPayments(total.getPayments() + element.getPayments());
        total.setAmount(total.getAmount().add(element.getAmount()));
        
      }
      request.setAttribute("campusCd",fpf.getCampusCd());
      request.setAttribute("procId",fpf.getProcId());
      request.setAttribute("formatResultList",results);
      request.setAttribute("total",total);
      request.removeAttribute("FormatProcessForm");
      request.getSession().removeAttribute("FormatSelectionForm");
      request.getSession().removeAttribute("campus");
//      fpf.setProcId(null);
//      fpf.setCampusCd(null);
//      request.setAttribute("FormatProcessForm", fpf);
      return mapping.findForward("finished");
    } catch (DisbursementRangeExhaustedException e) {
      LOG.error("executeLogic() Disbursement Range Exhausted Exception", e);
      ActionErrors ae = new ActionErrors();
      ae.add("global",new ActionMessage("format.disb.exhausted"));
      saveErrors(request,ae);
      return mapping.findForward("pdp_error");
    } catch (MissingDisbursementRangeException e) {
      LOG.error("executeLogic() Missing Disbursment Number Range", e);
      ActionMessages ae = new ActionMessages();
      ae.add("global",new ActionMessage("format.disb.missing"));
      saveErrors(request,ae);
      return mapping.findForward("pdp_error");
    }
  }

}

/*
 * Created on Aug 30, 2004
 *
 */
package org.kuali.module.pdp.action.format;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.pdp.action.BaseAction;
import org.kuali.module.pdp.form.format.FormatProcessForm;
import org.kuali.module.pdp.form.format.FormatSelectionForm;
import org.kuali.module.pdp.service.FormatResult;
import org.kuali.module.pdp.service.FormatService;
import org.kuali.module.pdp.service.SecurityRecord;
import org.kuali.module.pdp.utilities.DateHandler;


/**
 * @author jsissom
 *
 */
public class FormatPrepareAction extends BaseAction {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FormatPrepareAction.class);

  private FormatService formatService;

  public FormatPrepareAction() {
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

    HttpSession session = request.getSession();

    if ( "btnClear".equals(whichButtonWasPressed(request)) ) {
      LOG.debug("executeLogic() Clear fields and return to selection");
      session.removeAttribute("customers");
      session.removeAttribute("ranges");
      session.removeAttribute("FormatSelectionForm");
      return mapping.findForward("restart");
    }

    String campus = (String)session.getAttribute("campus");
    if ( campus == null ) {
      return mapping.findForward("selection");
    }

    FormatSelectionForm fsf = (FormatSelectionForm)form;

    List customers = (List)session.getAttribute("customers");
    session.removeAttribute("customers");

    // Figure out which ones they have selected
    List selectedCustomers = new ArrayList();
    // TODO Session Check in Format?
    for (int i = 0; i < customers.size(); i++) {
      if ( "on".equals(fsf.getCustomerProfileId(i)) ) {
        selectedCustomers.add(customers.get(i));
      }
    }

    Date paymentDate = DateHandler.makeStringDate(fsf.getPaymentDate());


    // Get rid of un-needed session stuff
    session.removeAttribute("customers");
    session.removeAttribute("ranges");
    session.removeAttribute("FormatSelectionForm");

    List results = formatService.startFormatProcess(getUser(request),campus,selectedCustomers,paymentDate,"Y".equals(fsf.getImmediate()),fsf.getPaymentTypes());
    if ( results.size() == 0 ) {
      return mapping.findForward("no_payments");
    }
    
    // Get the first one to get the process ID out of it
    FormatResult fr = (FormatResult)results.get(0);

    FormatProcessForm fpf = new FormatProcessForm();
    fpf.setProcId(fr.getProcId());
    fpf.setCampusCd(campus);
    request.setAttribute("FormatProcessForm",fpf);

    int count = 0;
    BigDecimal amount = new BigDecimal(0);

    for (Iterator iter = results.iterator(); iter.hasNext();) {
      FormatResult element = (FormatResult)iter.next();

      count += element.getPayments();
      amount = amount.add(element.getAmount());
    }
    FormatResult total = new FormatResult();
    total.setAmount(amount);
    total.setPayments(count);

    request.setAttribute("results",results);
    request.setAttribute("total",total);

    return mapping.findForward("continue");
  }

}

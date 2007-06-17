/*
 * Created on Aug 12, 2004
 *
 */
package org.kuali.module.pdp.action.format;

import java.text.SimpleDateFormat;
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
import org.kuali.module.pdp.bo.CustomerProfile;
import org.kuali.module.pdp.bo.PdpUser;
import org.kuali.module.pdp.form.format.FormatSelectionForm;
import org.kuali.module.pdp.service.FormatSelection;
import org.kuali.module.pdp.service.FormatService;
import org.kuali.module.pdp.service.SecurityRecord;


/**
 * @author jsissom
 *
 */
public class FormatSelectionAction extends BaseAction {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FormatSelectionAction.class);
  private FormatService formatService;

  public FormatSelectionAction() {
      super();
      setFormatService( (FormatService)SpringServiceLocator.getService("pdpFormatService") );
  }

  public void setFormatService(FormatService fas) {
    formatService = fas;
  }

  protected boolean isAuthorized(ActionMapping mapping, ActionForm form, HttpServletRequest request,
      HttpServletResponse response) {
    SecurityRecord sr = getSecurityRecord(request);
    return sr.isProcessRole();
  }

  protected ActionForward executeLogic(ActionMapping mapping, ActionForm form, HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    LOG.debug("executeLogic() starting");

    HttpSession session = request.getSession();

    PdpUser user = getUser(request);
    FormatSelection fs = formatService.formatSelectionAction(user,request.getParameter("clear") != null);

    // Get the user's campus
    session.setAttribute("campus",user.getUniversalUser().getCampusCode());

    // Note, customers, ranges and FormatSelectionForm have to be in session so
    // validate works.  If they weren't in session, the page wouldn't have all the
    // data it needs to display after a validate failure.

    if ( fs.getStartDate() != null ) {
      LOG.debug("executeLogic() Format is already running for " + user.getUniversalUser().getCampusCode());

      session.removeAttribute("customers");
      session.removeAttribute("ranges");
      session.removeAttribute("FormatSelectionForm");

      // Format is already running, put up message
      request.setAttribute("formatStart",fs.getStartDate());
      return mapping.findForward("running");
    }

    // Get the data we need
    List customers = fs.getCustomerList();
    List ranges = fs.getRangeList();

    FormatSelectionForm fsf = new FormatSelectionForm();
    String[] cid = new String[customers.size()];
    fsf.setCustomerProfileId(cid);
    fsf.setPaymentTypes("A");

    if ( fs.getCampus().getFormatSelectCustomers().booleanValue() ) {
      int i = 0;
      for (Iterator iter = customers.iterator(); iter.hasNext();) {
        CustomerProfile element = (CustomerProfile)iter.next();
        if ( user.getUniversalUser().getCampusCode().equals(element.getDefaultPhysicalCampusProcessingCode()) ) {
          cid[i] = "on";
        }
        i++;
      }
    }

    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    Date today = new Date();
    fsf.setPaymentDate(sdf.format(today));

    // Save all the stuff in the session
    session.setAttribute("customers",customers);
    session.setAttribute("ranges",ranges);

    session.setAttribute("FormatSelectionForm",fsf);

    return mapping.findForward("selection");
  }
}

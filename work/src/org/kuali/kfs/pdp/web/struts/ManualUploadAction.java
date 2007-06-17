/*
 * Created on Jul 9, 2004
 *
 */
package org.kuali.module.pdp.action.upload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.module.pdp.action.BaseAction;
import org.kuali.module.pdp.service.SecurityRecord;


/**
 * @author jsissom
 *
 */
public class ManualUploadAction extends BaseAction {

  protected boolean isAuthorized(ActionMapping mapping, ActionForm form, HttpServletRequest request,
      HttpServletResponse response) {
    SecurityRecord sr = getSecurityRecord(request);
    return sr.isSubmitRole();
  }

  protected ActionForward executeLogic(ActionMapping mapping, ActionForm form, HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    return mapping.findForward("upload");
  }
}

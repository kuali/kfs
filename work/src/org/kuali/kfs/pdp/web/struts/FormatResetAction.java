/*
 * Created on Nov 2, 2004
 *
 */
package org.kuali.module.pdp.action.format;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.pdp.action.BaseAction;
import org.kuali.module.pdp.service.FormatService;
import org.kuali.module.pdp.service.SecurityRecord;


/**
 * @author delyea
 *
 */
public class FormatResetAction extends BaseAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FormatResetAction.class);
    private FormatService formatService;

    public FormatResetAction() {
        super();
        setFormatService( (FormatService)SpringServiceLocator.getService("pdpFormatService") );
    }

    public void setFormatService(FormatService fas) {
        formatService = fas;
    }

    protected boolean isAuthorized(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response) {
        SecurityRecord sr = getSecurityRecord(request);
        return sr.isProcessRole();
    }

    protected ActionForward executeLogic(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("executeLogic() started");

        Integer procId = new Integer(request.getParameter("processId"));
    
        if ((procId != new Integer(0)) && (procId != null)) {
            formatService.resetFormatPayments(procId);
        }
    
        return mapping.findForward("selection");
    }
}

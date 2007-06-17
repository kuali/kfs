/*
 * Created on Sep 17, 2004
 *
 */
package org.kuali.module.pdp.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author jsissom
 *
 */
public class ChannelAction extends BaseAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ChannelAction.class);

    protected ActionForward executeLogic(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
        LOG.debug("executeLogic() started");

        return mapping.findForward("channel");
    }
}

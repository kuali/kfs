/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.pdp.action.paymentdetail;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.pdp.action.BaseAction;
import org.kuali.module.pdp.bo.PaymentDetail;
import org.kuali.module.pdp.service.PaymentGroupService;
import org.kuali.module.pdp.utilities.GeneralUtilities;

public class EpicPaymentDetailListAction extends BaseAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EpicPaymentDetailListAction.class);
    private PaymentGroupService paymentGroupService;

    public EpicPaymentDetailListAction() {
        setPaymentGroupService(SpringContext.getBean(PaymentGroupService.class));
    }

    protected ActionForward executeLogic(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("executeLogic() started");

        PaymentDetail pd = (PaymentDetail) request.getSession().getAttribute("PaymentDetail");
        String listType = request.getParameter("listType");

        if (pd.equals(null)) {
            // Handle Session expiration
            return mapping.findForward("pdp_session_timeout");
        }
        else if (GeneralUtilities.isStringEmpty(listType)) {
            // Invalid call to Action
            return mapping.findForward("pdp_system_error");
        }
        else {
            if ((!("disbursement".equals(listType))) && (!("group".equals(listType)))) {
                // Invalid List Type
                return mapping.findForward("pdp_system_error");
            }
        }
        return mapping.findForward("display");
    }

    public void setPaymentGroupService(PaymentGroupService p) {
        paymentGroupService = p;
    }
}

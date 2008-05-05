/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.pdp.action.BaseAction;
import org.kuali.module.pdp.form.format.FormatProcessForm;
import org.kuali.module.pdp.service.DisbursementRangeExhaustedException;
import org.kuali.module.pdp.service.FormatResult;
import org.kuali.module.pdp.service.FormatService;
import org.kuali.module.pdp.service.MissingDisbursementRangeException;
import org.kuali.module.pdp.service.NoBankForCustomerException;
import org.kuali.module.pdp.service.SecurityRecord;

public class FormatAction extends BaseAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FormatAction.class);

    private FormatService formatService;

    public FormatAction() {
        super();
        setFormatService(SpringContext.getBean(FormatService.class));
    }

    public void setFormatService(FormatService fs) {
        formatService = fs;
    }

    protected boolean isAuthorized(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        SecurityRecord sr = getSecurityRecord(request);
        return sr.isProcessRole()||sr.isSysAdminRole();
    }

    protected ActionForward executeLogic(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("executeLogic() started");

        FormatProcessForm fpf = (FormatProcessForm) form;

        if ("btnCancel".equals(whichButtonWasPressed(request))) {
            // Clear the format
            formatService.clearUnfinishedFormat(fpf.getProcId());
            return mapping.findForward("cleared");
        }

        try {
            List results = formatService.performFormat(fpf.getProcId());
            Collections.sort(results);
            FormatResult total = new FormatResult();
            for (Iterator iter = results.iterator(); iter.hasNext();) {
                FormatResult element = (FormatResult) iter.next();
                total.setPayments(total.getPayments() + element.getPayments());
                total.setAmount(total.getAmount().add(element.getAmount()));
            }
            request.setAttribute("campusCd", fpf.getCampusCd());
            request.setAttribute("procId", fpf.getProcId());
            request.setAttribute("formatResultList", results);
            request.setAttribute("total", total);
            request.removeAttribute("FormatProcessForm");
            request.getSession().removeAttribute("FormatSelectionForm");
            request.getSession().removeAttribute("campus");
            return mapping.findForward("finished");
        }
        catch (NoBankForCustomerException nbfce) {
            LOG.error("executeLogic() No Bank For Customer Exception", nbfce);
            ActionErrors ae = new ActionErrors();
            ae.add("global", new ActionMessage("format.bank.missing", nbfce.getCustomerProfile()));
            saveErrors(request, ae);
            return mapping.findForward("pdp_message");
        }
        catch (DisbursementRangeExhaustedException e) {
            LOG.error("executeLogic() Disbursement Range Exhausted Exception", e);
            ActionErrors ae = new ActionErrors();
            ae.add("global", new ActionMessage("format.disb.exhausted"));
            saveErrors(request, ae);
            return mapping.findForward("pdp_message");
        }
        catch (MissingDisbursementRangeException e) {
            LOG.error("executeLogic() Missing Disbursment Number Range", e);
            ActionMessages ae = new ActionMessages();
            ae.add("global", new ActionMessage("format.disb.missing"));
            saveErrors(request, ae);
            return mapping.findForward("pdp_message");
        }
    }
}

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
/*
 * Created on Jan 14, 2005
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
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.pdp.action.BaseAction;
import org.kuali.module.pdp.form.format.FormatSummaryForm;
import org.kuali.module.pdp.service.FormatResult;
import org.kuali.module.pdp.service.FormatService;
import org.kuali.module.pdp.service.SecurityRecord;
import org.kuali.module.pdp.utilities.GeneralUtilities;


/**
 * @author delyea
 */
public class FormatSummaryAction extends BaseAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FormatAction.class);

    private FormatService formatService;

    public FormatSummaryAction() {
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

        String id = request.getParameter("id");
        FormatSummaryForm fsf = (FormatSummaryForm) form;

        ActionErrors actionErrors = new ActionErrors();

        if (GeneralUtilities.isStringEmpty(id)) {
            if (fsf != null) {
                if (fsf.getProcessId() != null) {
                    actionErrors.add(fsf.validate(mapping, request));
                    if (actionErrors.isEmpty()) {
                        id = fsf.getProcessId().toString();
                    }
                    else {
                        getRecentFormats(request);
                        saveErrors(request, actionErrors);
                        return mapping.findForward("selection");
                    }
                }
                else {
                    getRecentFormats(request);
                    return mapping.findForward("selection");
                }
            }
            else {
                getRecentFormats(request);
                return mapping.findForward("selection");
            }
        }
        else {
            if (!(GeneralUtilities.isStringAllNumbers(id))) {
                return mapping.findForward("pdp_error");
            }
        }

        List results = formatService.getFormatSummary(new Integer(id));
        Collections.sort(results);
        FormatResult total = new FormatResult();
        for (Iterator iter = results.iterator(); iter.hasNext();) {
            FormatResult element = (FormatResult) iter.next();
            total.setPayments(total.getPayments() + element.getPayments());
            total.setAmount(total.getAmount().add(element.getAmount()));

        }
        request.setAttribute("procId", id);
        request.setAttribute("formatResultList", results);
        request.setAttribute("total", total);
        return mapping.findForward("finished");


    }

    protected void getRecentFormats(HttpServletRequest request) {
        List recentFormats = formatService.getMostCurrentProcesses();
        request.setAttribute("recentFormats", recentFormats);
    }
}

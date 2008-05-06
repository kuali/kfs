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
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.pdp.action.BaseAction;
import org.kuali.module.pdp.bo.CustomerProfile;
import org.kuali.module.pdp.bo.PdpUser;
import org.kuali.module.pdp.form.format.FormatSelectionForm;
import org.kuali.module.pdp.service.FormatSelection;
import org.kuali.module.pdp.service.FormatService;
import org.kuali.module.pdp.service.SecurityRecord;


/**
 * @author jsissom
 */
public class FormatSelectionAction extends BaseAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FormatSelectionAction.class);
    private FormatService formatService;
    private ParameterService parameterService;

    public FormatSelectionAction() {
        super();
        setFormatService(SpringContext.getBean(FormatService.class));
        setParameterService(SpringContext.getBean(ParameterService.class));
    }

    protected boolean isAuthorized(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        SecurityRecord sr = getSecurityRecord(request);
        return sr.isProcessRole()||sr.isSysAdminRole();
    }

    protected ActionForward executeLogic(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("executeLogic() starting");

        HttpSession session = request.getSession();

        PdpUser user = getUser(request);
        FormatSelection fs = formatService.formatSelectionAction(user, request.getParameter("clear") != null);

        // Get the user's campus
        session.setAttribute("campus", user.getUniversalUser().getCampusCode());

        // Note, customers, ranges and FormatSelectionForm have to be in session so
        // validate works. If they weren't in session, the page wouldn't have all the
        // data it needs to display after a validate failure.

        if (fs.getStartDate() != null) {
            LOG.debug("executeLogic() Format is already running for " + user.getUniversalUser().getCampusCode());

            session.removeAttribute("customers");
            session.removeAttribute("ranges");
            session.removeAttribute("PdPFormatSelectionForm");

            // Format is already running, put up message
            request.setAttribute("formatStart", fs.getStartDate());
            return mapping.findForward("running");
        }

        // Get the data we need
        List customers = fs.getCustomerList();
        List ranges = fs.getRangeList();

        FormatSelectionForm fsf = new FormatSelectionForm();
        String[] cid = new String[customers.size()];
        fsf.setCustomerProfileId(cid);
        fsf.setPaymentTypes("A");

        int i = 0;
        for (Iterator iter = customers.iterator(); iter.hasNext();) {
            CustomerProfile element = (CustomerProfile) iter.next();
            if (fs.getCampus().equals(element.getDefaultPhysicalCampusProcessingCode())) {
                cid[i] = "on";
            }
            i++;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date today = new Date();
        fsf.setPaymentDate(sdf.format(today));

        // Save all the stuff in the session
        session.setAttribute("customers", customers);
        session.setAttribute("ranges", ranges);

        session.setAttribute("PdpFormatSelectionForm", fsf);

        return mapping.findForward("selection");
    }

    public void setFormatService(FormatService fas) {
        formatService = fas;
    }

    public void setParameterService(ParameterService ps) {
        parameterService = ps;
    }
}

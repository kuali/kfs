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
 * Created on Sep 7, 2004
 *
 */
package org.kuali.kfs.pdp.document.web.struts;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.pdp.web.struts.BaseAction;
import org.kuali.kfs.pdp.businessobject.DisbursementNumberRange;
import org.kuali.kfs.pdp.document.web.struts.DisbursementNumberMaintenanceForm;
import org.kuali.kfs.pdp.service.DisbursementNumberRangeService;
import org.kuali.kfs.pdp.businessobject.SecurityRecord;


/**
 * @author delyea
 */
public class DisbursementNumberMaintenanceAction extends BaseAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementNumberMaintenanceAction.class);

    private DisbursementNumberRangeService disbursementNumberRangeService;

    public DisbursementNumberMaintenanceAction() {
        super();
        setDisbursementNumberRangeService(SpringContext.getBean(DisbursementNumberRangeService.class));
    }

    public void setDisbursementNumberRangeService(DisbursementNumberRangeService d) {
        disbursementNumberRangeService = d;
    }

    protected boolean isAuthorized(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        SecurityRecord sr = getSecurityRecord(request);
        return sr.isRangesRole() || sr.isSysAdminRole();
    }

    protected ActionForward executeLogic(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("executeLogic() starting");
        String forward = "list";
        List disbursementRangeList = new ArrayList();
        String d = request.getParameter("dnrId");

        if (d != null) {
            int dnrId = -1;
            try {
                dnrId = Integer.parseInt(d);
            }
            catch (NumberFormatException e) {
                // Bad number - we don't need to do anything here
            }
            if (dnrId == 0) {
                // Add a new range
                DisbursementNumberMaintenanceForm dnmf = new DisbursementNumberMaintenanceForm();

                request.setAttribute("PdpDisbursementNumberMaintenanceForm", dnmf);

                return mapping.findForward("edit");
            }
            else if (dnrId == -1) {
                // No Id or invalid disbursement number range ID, go back to the list
                disbursementRangeList = disbursementNumberRangeService.getAll();
                request.setAttribute("disbursementRangeList", disbursementRangeList);
                return mapping.findForward("list");
            }
            else {
                // Load the disbursement number range to edit it
                DisbursementNumberRange dnr = disbursementNumberRangeService.get(new Integer(dnrId));
                DisbursementNumberMaintenanceForm dnmf = new DisbursementNumberMaintenanceForm(dnr);

                request.setAttribute("PdpDisbursementNumberMaintenanceForm", dnmf);
                return mapping.findForward("edit");
            }
        }
        else {
            disbursementRangeList = disbursementNumberRangeService.getAll();
            request.setAttribute("disbursementRangeList", disbursementRangeList);
            return mapping.findForward("list");
        }
    }
}

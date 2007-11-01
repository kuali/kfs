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
package org.kuali.module.pdp.action.disbursementnumbermaintenance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.pdp.action.BaseAction;
import org.kuali.module.pdp.bo.Bank;
import org.kuali.module.pdp.bo.DisbursementNumberRange;
import org.kuali.module.pdp.form.disbursementnumbermaintenance.DisbursementNumberMaintenanceForm;
import org.kuali.module.pdp.service.BankService;
import org.kuali.module.pdp.service.DisbursementNumberRangeService;
import org.kuali.module.pdp.service.SecurityRecord;

public class DisbursementNumberMaintenanceSaveAction extends BaseAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementNumberMaintenanceSaveAction.class);

    private DisbursementNumberRangeService disbursementNumberRangeService;
    private BankService bankService;

    public DisbursementNumberMaintenanceSaveAction() {
        super();
        setDisbursementNumberRangeService(SpringContext.getBean(DisbursementNumberRangeService.class));
        setBankService(SpringContext.getBean(BankService.class));
    }

    public void setDisbursementNumberRangeService(DisbursementNumberRangeService d) {
        disbursementNumberRangeService = d;
    }

    public void setBankService(BankService b) {
        bankService = b;
    }

    protected boolean isAuthorized(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        SecurityRecord sr = getSecurityRecord(request);
        return sr.isRangesRole() || sr.isSysAdminRole();
    }

    protected ActionForward executeLogic(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("executeLogic() started");

        String forward = "list";
        String btnPressed = whichButtonWasPressed(request);
        LOG.debug("executeLogic() Button Pressed was " + btnPressed);
        ActionErrors actionErrors = new ActionErrors();

        if ("btnCancel".equals(btnPressed)) {
            LOG.debug("executeLogic() Cancelled, returning to list");
        }
        else if ("btnSave".equals(btnPressed)) {
            LOG.debug("executeLogic() Saving");

            DisbursementNumberMaintenanceForm dnmf = (DisbursementNumberMaintenanceForm) form;
            ActionErrors ae = dnmf.validate(mapping, request);
            if (ae.size() > 0) {
                LOG.debug("executeLogic() Validation errors");

                saveErrors(request, ae);
                return mapping.getInputForward();
            }

            DisbursementNumberRange dnr = dnmf.getDisbursementNumberRange();

            if ((dnmf.getId() == null) || (dnmf.getId().intValue() == 0)) {
                dnr.setId(null);
            }
            else {
                dnr.setId(dnmf.getId());
            }
            dnr.setBank((Bank) bankService.get(dnmf.getBankId()));
            dnr.setLastUpdateUser(getUser(request));

            try {
                disbursementNumberRangeService.save(dnr);
                actionErrors.add("success", new ActionMessage("success.disbursementNumberRange.saved"));
            }
            catch (Exception e) {
                actionErrors.add("error", new ActionMessage("disbursementNumberMaintenanceSaveAction.save.error"));
            }
        }

        if (!actionErrors.isEmpty()) { // If we had errors, save them.
            saveErrors(request, actionErrors);
        }
        return mapping.findForward(forward);
    }
}

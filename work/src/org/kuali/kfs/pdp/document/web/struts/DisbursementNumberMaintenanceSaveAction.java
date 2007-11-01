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

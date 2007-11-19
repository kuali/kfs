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
 * Created on Sep 3, 2004
 *
 */
package org.kuali.module.pdp.action.bank;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.pdp.action.BaseAction;
import org.kuali.module.pdp.bo.Bank;
import org.kuali.module.pdp.bo.DisbursementType;
import org.kuali.module.pdp.form.bank.BankForm;
import org.kuali.module.pdp.service.BankService;
import org.kuali.module.pdp.service.ReferenceService;
import org.kuali.module.pdp.service.SecurityRecord;

public class BankSaveAction extends BaseAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BankSaveAction.class);

    private BankService bankService;
    private ReferenceService referenceService;

    public BankSaveAction() {
        super();
        setBankService(SpringContext.getBean(BankService.class));
        setReferenceService(SpringContext.getBean(ReferenceService.class));
    }

    protected boolean isAuthorized(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        SecurityRecord sr = getSecurityRecord(request);
        return sr.isSysAdminRole();
    }

    protected ActionForward executeLogic(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("executeLogic() started");

        String btnPressed = whichButtonWasPressed(request);

        if ("btnCancel".equals(btnPressed)) {
            LOG.debug("executeLogic() Cancelled, returning to list");
        } else if ("btnSave".equals(btnPressed)) {
            LOG.debug("executeLogic() Saving");

            BankForm bankForm = (BankForm) form;

            ActionErrors errors = bankForm.validate(mapping, request);
            if ( errors.size() > 0 ) {
                saveErrors(request, errors);
                return mapping.getInputForward();
            }

            Bank bank = new Bank();

            bank.setAccountNumber(bankForm.getAccountNumber());
            bank.setActive(bankForm.getActive());
            bank.setDescription(bankForm.getDescription());
            bank.setDisbursementType((DisbursementType) referenceService.getCode("DisbursementType", bankForm.getDisbursementTypeCode()));
            if ((bankForm.getId() == null) || (bankForm.getId().intValue() == 0)) {
                bank.setId(null);
            } else {
                bank.setId(bankForm.getId());
            }
            bank.setName(bankForm.getName());
            bank.setRoutingNumber(bankForm.getRoutingNumber());
            bank.setVersion(bankForm.getVersion());
            bank.setLastUpdateUser(getUser(request));

            bankService.save(bank);
        }
        return mapping.findForward("list");
    }

    public void setBankService(BankService b) {
        bankService = b;
    }

    public void setReferenceService(ReferenceService r) {
        referenceService = r;
    }
}

/*
 * Created on Sep 3, 2004
 *
 */
package org.kuali.module.pdp.action.bank;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.pdp.action.BaseAction;
import org.kuali.module.pdp.bo.Bank;
import org.kuali.module.pdp.form.bank.BankForm;
import org.kuali.module.pdp.service.BankService;
import org.kuali.module.pdp.service.SecurityRecord;


/**
 * @author jsissom
 *
 */
public class BankAction extends BaseAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BankAction.class);

    private BankService bankService;

    public BankAction() {
        super();
        setBankService((BankService)SpringServiceLocator.getService("pdpBankService"));
    }

    public void setBankService(BankService b) {
        bankService = b;
    }

    protected boolean isAuthorized(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response) {
        SecurityRecord sr = getSecurityRecord(request);
        return sr.isSysAdminRole();
    }

    protected ActionForward executeLogic(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
        // Go to the bank list screen if there is no bankId passed, otherwise, go to
        // the edit screen for a single bank

        String b = request.getParameter("bankId");
        if ( b != null ) {
            int bankId = -1;
            try {
                bankId = Integer.parseInt(b);
            } catch (NumberFormatException e) {
                // Bad number - we don't need to do anything here
            }
            if ( bankId == 0 ) {
                LOG.debug("executeLogic() add bank");

                // Add a new bank
                BankForm bf = new BankForm();
                bf.setActive(Boolean.TRUE);

                request.setAttribute("BankForm",bf);

                return mapping.findForward("edit");
            } else if ( bankId == -1 ) {
                // No bank Id or invalid bank ID, go back to the list
                return mapping.findForward("list");
            } else {
                LOG.debug("executeLogic() edit bank");

                // Load the bank to edit it
                Bank bank = bankService.get(bankId);
                BankForm bf = new BankForm(bank);

                request.setAttribute("PdpBankForm",bf);

                return mapping.findForward("edit");
            }
        } else {
            return mapping.findForward("list");
        }
    }
}

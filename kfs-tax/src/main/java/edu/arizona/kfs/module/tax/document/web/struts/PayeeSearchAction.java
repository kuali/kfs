package edu.arizona.kfs.module.tax.document.web.struts;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.util.GlobalVariables;

import edu.arizona.kfs.module.tax.TaxConstants;
import edu.arizona.kfs.module.tax.TaxPropertyConstants;
import edu.arizona.kfs.module.tax.businessobject.Payee;
import edu.arizona.kfs.module.tax.businessobject.Payer;
import edu.arizona.kfs.module.tax.service.TaxParameterHelperService;
import edu.arizona.kfs.module.tax.service.TaxPayeeService;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSKeyConstants;

@SuppressWarnings("deprecation")
public class PayeeSearchAction extends KualiAction {
    private static final Logger LOG = Logger.getLogger(PayeeSearchAction.class);

    public ActionForward searchPayees(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoleService roleService = KimApiServiceLocator.getRoleService();
        TaxPayeeService taxFormPayeeService = SpringContext.getBean(TaxPayeeService.class);
        TaxParameterHelperService taxParameterHelperService = SpringContext.getBean(TaxParameterHelperService.class);

        Role role = roleService.getRoleByNamespaceCodeAndName(TaxConstants.TAX_NAMESPACE_CODE, TaxConstants.TAX_USER_ROLE);
        // Protective guard for null condition which should never happen
        if (role == null) {
            LOG.error("System expected to find a role named " + TaxConstants.TAX_USER_ROLE + " under the " + TaxConstants.TAX_NAMESPACE_CODE + " namespace, but it was not found.");
            throw new IllegalStateException("System expected to find a role named " + TaxConstants.TAX_USER_ROLE + " under the " + TaxConstants.TAX_NAMESPACE_CODE + " namespace, but it was not found.");
        }

        boolean hasRole = roleService.principalHasRole(GlobalVariables.getUserSession().getPerson().getPrincipalId(), Collections.singletonList(role.getId()), null);

        if (hasRole) {
            PayeeSearchForm pform = (PayeeSearchForm) form;

            Payer p = taxParameterHelperService.getDefaultPayer();

            if (p == null) {
                GlobalVariables.getMessageMap().putError(TaxPropertyConstants.PayeeFields.TAX_YEAR, KFSKeyConstants.ERROR_CUSTOM, "No 1099 Payer Record Found");
            } else {

                List<Payee> payees = taxFormPayeeService.searchPayees(pform.getVendorName(), pform.getHeaderTaxNumber(), pform.getVendorNumber(), pform.getTaxYear());

                if ((payees != null) && !payees.isEmpty()) {
                    request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS, payees);
                    request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS_SIZE, payees.size());
                } else {
                    GlobalVariables.getMessageMap().putError(TaxPropertyConstants.PayeeFields.TAX_YEAR, KFSKeyConstants.ERROR_CUSTOM, "No 1099 Payees Found");
                }
            }
        } else {
            GlobalVariables.getMessageMap().putError(TaxPropertyConstants.PayeeFields.TAX_YEAR, KFSKeyConstants.ERROR_CUSTOM, "User Not Authorized");
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward returnToIndex(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_CLOSE);
    }

}

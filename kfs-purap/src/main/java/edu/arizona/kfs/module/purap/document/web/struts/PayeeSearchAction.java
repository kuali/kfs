package edu.arizona.kfs.module.purap.document.web.struts;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.kim.api.KimConstants;

import edu.arizona.kfs.tax.TaxConstants;
import edu.arizona.kfs.tax.TaxPropertyConstants;
import edu.arizona.kfs.tax.businessobject.Payee;
import edu.arizona.kfs.tax.businessobject.Payer;
import edu.arizona.kfs.tax.document.web.struts.PayeeSearchForm;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSKeyConstants;
import edu.arizona.kfs.module.purap.service.TaxReporting1099Service;

public class PayeeSearchAction extends KualiAction {
    private static final Logger LOG = Logger.getLogger(PayeeSearchAction.class);
    
    public ActionForward searchPayees(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	RoleService roleService = KimApiServiceLocator.getRoleService();
        
        Role role = roleService.getRoleByNamespaceCodeAndName(TaxConstants.NMSPC_CD, TaxConstants.TAX_USER_ROLE);
        // Protective guard for null condition which should never happen
        if (role == null) {
            LOG.error("System expected to find a role named " + TaxConstants.TAX_USER_ROLE + " under the " + TaxConstants.NMSPC_CD + " namespace, but it was not found.");
            throw new IllegalStateException("System expected to find a role named " + TaxConstants.TAX_USER_ROLE + " under the " + TaxConstants.NMSPC_CD + " namespace, but it was not found.");
        } 

        boolean hasRole  = roleService.principalHasRole(GlobalVariables.getUserSession().getPerson().getPrincipalId(), Collections.singletonList(role.getId()), null);
        
        if(hasRole) {
            PayeeSearchForm pform = (PayeeSearchForm)form;
            
            TaxReporting1099Service payeeServ = SpringContext.getBean(TaxReporting1099Service.class);
    
            Payer p = payeeServ.getDefaultPayer();
            
            if( p == null ) {
                GlobalVariables.getMessageMap().putError(TaxPropertyConstants.TAX_YEAR, KFSKeyConstants.ERROR_CUSTOM, "No 1099 Payer Record Found");
            } else {
                List <Payee> payees  = payeeServ.searchPayees(pform);
                
                if ((payees != null) && !payees.isEmpty()) {
                    request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS, payees);
                    request.setAttribute(KFSConstants.REQUEST_SEARCH_RESULTS_SIZE, payees.size());
                } else {
                    GlobalVariables.getMessageMap().putError(TaxPropertyConstants.TAX_YEAR, KFSKeyConstants.ERROR_CUSTOM, "No 1099 Payees Found");
                }
            }
        } else {
            GlobalVariables.getMessageMap().putError(TaxPropertyConstants.TAX_YEAR, KFSKeyConstants.ERROR_CUSTOM, "User Not Authorized");
        }
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    public ActionForward returnToIndex(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_CLOSE);
    }
    
    @Override
    protected void checkAuthorization( ActionForm form, String methodToCall) throws AuthorizationException {
        String principalId = GlobalVariables.getUserSession().getPrincipalId();
        Map<String, String> roleQualifier = new HashMap<String, String>(getRoleQualification(form, methodToCall));
        Map<String, String> permissionDetails = new HashMap<String, String>();
        permissionDetails.put(KRADConstants.NAMESPACE_CODE, TaxConstants.NMSPC_CD);
        permissionDetails.put(KRADConstants.ACTION_CLASS, this.getClass().getName());
        
        if (!KimApiServiceLocator.getPermissionService().isAuthorizedByTemplate(principalId,
                KRADConstants.KNS_NAMESPACE, KimConstants.PermissionTemplateNames.USE_SCREEN, permissionDetails, roleQualifier)) {
            throw new AuthorizationException(GlobalVariables.getUserSession().getPerson().getPrincipalName(), methodToCall, this.getClass().getSimpleName());
        }
    }
}

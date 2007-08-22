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
package org.kuali.module.budget.web.struts.action;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.authorization.AuthorizationType;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.exceptions.ModuleAuthorizationException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiModuleService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.TypedArrayList;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.BCConstants.OrgSelOpMode;
import org.kuali.module.budget.bo.BudgetConstructionOrganizationReports;
import org.kuali.module.budget.bo.BudgetConstructionPullup;
import org.kuali.module.budget.service.BudgetOrganizationTreeService;
import org.kuali.module.budget.service.PermissionService;
import org.kuali.module.budget.web.struts.form.OrganizationSelectionTreeForm;
import org.kuali.module.chart.bo.Org;

/**
 * This class...
 */
public class OrganizationSelectionTreeAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationSelectionTreeAction.class);

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.execute(mapping, form, request, response);
        
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;  


        return forward;
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#checkAuthorization(org.apache.struts.action.ActionForm, java.lang.String)
     */
    @Override
    protected void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {

        AuthorizationType adHocAuthorizationType = new AuthorizationType.AdHocRequest(this.getClass(), methodToCall);
        if ( !SpringContext.getBean(KualiModuleService.class).isAuthorized( GlobalVariables.getUserSession().getUniversalUser(), adHocAuthorizationType ) ) {
            LOG.error("User not authorized to use this action: " + this.getClass().getName() );
            throw new ModuleAuthorizationException( GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier(), adHocAuthorizationType, getKualiModuleService().getResponsibleModule(((KualiForm)form).getClass()) );
        }

        PermissionService permissionService = SpringContext.getBean(PermissionService.class);
        try {
            List<Org> pointOfViewOrgs = permissionService.getOrgReview(GlobalVariables.getUserSession().getNetworkId());
            if (pointOfViewOrgs.isEmpty()){
                GlobalVariables.getErrorMap().putError("pointOfViewOrg","error.budget.userNotOrgApprover");
            }
            
        }
        catch (Exception e){
            throw new AuthorizationException(GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier(), this.getClass().getName(), "Can't determine organization approver status.");
        }
    }

    /**
     * This method sets up the initial mode of the drill down screen based on a passed in calling mode attribute
     * This can be one of five modes - pullup, pushdown, reports, salset, account.  Each mode causes
     * a slightly different rendition of the controls presented to the user, but the basic point of view selection
     * and organization drill down functionality is the same in all five modes.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadExpansionScreen(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        OrganizationSelectionTreeForm orgSelTreeForm = (OrganizationSelectionTreeForm) form;

        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward returnToCaller(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        OrganizationSelectionTreeForm orgSelTreeForm = (OrganizationSelectionTreeForm) form;
        
        // depopulate any selection subtrees for the user
        String personUserIdentifier = GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier();
        SpringContext.getBean(BudgetOrganizationTreeService.class).cleanPullup(personUserIdentifier);

        
        // setup the return parms for the document and anchor
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, BCConstants.BC_SELECTION_REFRESH_METHOD);
        parameters.put(KFSConstants.DOC_FORM_KEY, orgSelTreeForm.getReturnFormKey());
        parameters.put(KFSConstants.ANCHOR, orgSelTreeForm.getReturnAnchor());
        parameters.put(KFSConstants.REFRESH_CALLER, BCConstants.ORG_SEL_TREE_REFRESH_CALLER);
        
        String lookupUrl = UrlFactory.parameterizeUrl("/" + BCConstants.BC_SELECTION_ACTION, parameters);
        
        
        return new ActionForward(lookupUrl, true);
    }

    /**
     * This method implements functionality behind the refresh button on the Organization Selection Tree screen.
     * This is also called when the value of the point of view select control changed and javascript is enabled
     * on the user's browser
     *   
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performBuildPointOfView(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;  

        // check for point of view change
        if (organizationSelectionTreeForm.getCurrentPointOfViewKeyCode() != null){
            if ((organizationSelectionTreeForm.getPreviousPointOfViewKeyCode() == null) || (!organizationSelectionTreeForm.getPreviousPointOfViewKeyCode().equalsIgnoreCase(organizationSelectionTreeForm.getCurrentPointOfViewKeyCode()) == true)){
                String[] flds = organizationSelectionTreeForm.getCurrentPointOfViewKeyCode().split("[-]");
                organizationSelectionTreeForm.setPreviousPointOfViewKeyCode(organizationSelectionTreeForm.getCurrentPointOfViewKeyCode());

                HashMap map = new HashMap();
                map.put("chartOfAccountsCode", flds[0]);
                map.put("organizationCode", flds[1]);
                organizationSelectionTreeForm.setPointOfViewOrg((BudgetConstructionOrganizationReports) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BudgetConstructionOrganizationReports.class, map));

                // build a new selection subtree
                String personUniversalIdentifier = GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier();
                SpringContext.getBean(BudgetOrganizationTreeService.class).buildPullup(personUniversalIdentifier, flds[0], flds[1]);

                // initialize the selection tool to the root
                map.put("personUniversalIdentifier", personUniversalIdentifier);
                organizationSelectionTreeForm.setSelectionSubTreeOrgs((List<BudgetConstructionPullup>) SpringContext.getBean(BusinessObjectService.class).findMatching(BudgetConstructionPullup.class, map));
                organizationSelectionTreeForm.populateSelectionSubTreeOrgs();
                organizationSelectionTreeForm.setPreviousBranchOrgs(new TypedArrayList(BudgetConstructionPullup.class));
            }
        } else {
            organizationSelectionTreeForm.setPointOfViewOrg(new BudgetConstructionOrganizationReports());
            organizationSelectionTreeForm.setSelectionSubTreeOrgs(new TypedArrayList(BudgetConstructionPullup.class));
            organizationSelectionTreeForm.setPreviousBranchOrgs(new TypedArrayList(BudgetConstructionPullup.class));
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    public ActionForward navigateDown (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        
        //TODO add not a leaf test and put up a info message without drilling
        //TODO add call to reset pullflags for the current level orgs to zero
        
        // push parent org onto the branch stack
        organizationSelectionTreeForm.getPreviousBranchOrgs().add(organizationSelectionTreeForm.getSelectionSubTreeOrgs().get(this.getSelectedLine(request)));

        // get the children
        HashMap map = new HashMap();
        String chartOfAccountsCode  = organizationSelectionTreeForm.getSelectionSubTreeOrgs().get(this.getSelectedLine(request)).getChartOfAccountsCode();
        String organizationCode = organizationSelectionTreeForm.getSelectionSubTreeOrgs().get(this.getSelectedLine(request)).getOrganizationCode();
        String personUniversalIdentifier = GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier();
        organizationSelectionTreeForm.setSelectionSubTreeOrgs((List<BudgetConstructionPullup>) SpringContext.getBean(BudgetOrganizationTreeService.class).getPullupChildOrgs(personUniversalIdentifier, chartOfAccountsCode, organizationCode));
        organizationSelectionTreeForm.populateSelectionSubTreeOrgs();
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward navigateUp (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        
        // pop the parent org off the branch stack
        String personUniversalIdentifier = GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier();
        int popIdx = this.getSelectedLine(request);
        BudgetConstructionPullup previousBranchOrg = organizationSelectionTreeForm.getPreviousBranchOrgs().remove(popIdx);
        if (popIdx == 0){
            organizationSelectionTreeForm.setPreviousBranchOrgs(new TypedArrayList(BudgetConstructionPullup.class));

            // reinitialize the selection tool to the root
            HashMap map = new HashMap();
            map.put("chartOfAccountsCode", previousBranchOrg.getChartOfAccountsCode());
            map.put("organizationCode", previousBranchOrg.getOrganizationCode());
            map.put("personUniversalIdentifier", personUniversalIdentifier);
            organizationSelectionTreeForm.setSelectionSubTreeOrgs((List<BudgetConstructionPullup>) SpringContext.getBean(BusinessObjectService.class).findMatching(BudgetConstructionPullup.class, map));
            organizationSelectionTreeForm.populateSelectionSubTreeOrgs();
            
        } else {
            organizationSelectionTreeForm.setPreviousBranchOrgs(organizationSelectionTreeForm.getPreviousBranchOrgs().subList(0,popIdx));

            // get the parent and parent siblings
            String chartOfAccountsCode  = previousBranchOrg.getReportsToChartOfAccountsCode();
            String organizationCode = previousBranchOrg.getReportsToOrganizationCode();
            organizationSelectionTreeForm.setSelectionSubTreeOrgs((List<BudgetConstructionPullup>) SpringContext.getBean(BudgetOrganizationTreeService.class).getPullupChildOrgs(personUniversalIdentifier, chartOfAccountsCode, organizationCode));
            organizationSelectionTreeForm.populateSelectionSubTreeOrgs();
        }
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
}

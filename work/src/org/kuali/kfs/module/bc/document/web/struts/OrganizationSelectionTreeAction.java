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
import java.util.Iterator;
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
import org.kuali.core.service.KualiConfigurationService;
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
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.BCConstants.OrgSelControlOption;
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

    /**
     * This method is called by the close button.  It removes the user's BudgetConstructionPullup table rows and returns the
     * user to the seleection screen action.
     *  
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
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
    
    /**
     * This method handles saving the BudgetConstructionPullup current row to the previous branches stack and displaying
     * the associated children.
     *  
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward navigateDown (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        String personUniversalIdentifier = GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier();
        
        // reset any set pullflags in the database before navigation
        SpringContext.getBean(BudgetOrganizationTreeService.class).resetPullFlag(personUniversalIdentifier);
        
        // push parent org onto the branch stack
        organizationSelectionTreeForm.getPreviousBranchOrgs().add(organizationSelectionTreeForm.getSelectionSubTreeOrgs().get(this.getSelectedLine(request)));

        // get the children
        String chartOfAccountsCode  = organizationSelectionTreeForm.getSelectionSubTreeOrgs().get(this.getSelectedLine(request)).getChartOfAccountsCode();
        String organizationCode = organizationSelectionTreeForm.getSelectionSubTreeOrgs().get(this.getSelectedLine(request)).getOrganizationCode();
        organizationSelectionTreeForm.setSelectionSubTreeOrgs((List<BudgetConstructionPullup>) SpringContext.getBean(BudgetOrganizationTreeService.class).getPullupChildOrgs(personUniversalIdentifier, chartOfAccountsCode, organizationCode));
        organizationSelectionTreeForm.populateSelectionSubTreeOrgs();
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method handles navigation back to a previous branch BudgetConstructionPullup row displaying
     * the associated parent and it's siblings
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward navigateUp (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        String personUniversalIdentifier = GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier();

        // reset any set pullflags in the database before navigation
        SpringContext.getBean(BudgetOrganizationTreeService.class).resetPullFlag(personUniversalIdentifier);
        
        // pop the parent org off the branch stack
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

    /**
     * This method sets the pullFlag for all displayed subtree organizations
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward selectAll (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        setPullFlag(organizationSelectionTreeForm.getSelectionSubTreeOrgs(), OrgSelControlOption.YES.getKey());
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method clears the pullFlag for all displayed subtree organizations
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward clearAll (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        setPullFlag(organizationSelectionTreeForm.getSelectionSubTreeOrgs(), OrgSelControlOption.NO.getKey());
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method sets the pullFlag for all displayed subtree organizations to the setting implied by the method name.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward selectPullOrgAll (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        setPullFlag(organizationSelectionTreeForm.getSelectionSubTreeOrgs(), OrgSelControlOption.ORG.getKey());
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method sets the pullFlag for all displayed subtree organizations to the setting implied by the method name.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward selectPullSubOrgAll (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        setPullFlag(organizationSelectionTreeForm.getSelectionSubTreeOrgs(), OrgSelControlOption.SUBORG.getKey());
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method sets the pullFlag for all displayed subtree organizations to the setting implied by the method name.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward selectPullBothAll (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        setPullFlag(organizationSelectionTreeForm.getSelectionSubTreeOrgs(), OrgSelControlOption.BOTH.getKey());
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method sets the pullFlag for all displayed subtree organizations to the setting implied by the method name.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward selectPushOrgLevAll (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        setPullFlag(organizationSelectionTreeForm.getSelectionSubTreeOrgs(), OrgSelControlOption.ORGLEV.getKey());
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method sets the pullFlag for all displayed subtree organizations to the setting implied by the method name.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward selectPushMgrLevAll (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        setPullFlag(organizationSelectionTreeForm.getSelectionSubTreeOrgs(), OrgSelControlOption.MGRLEV.getKey());
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method sets the pullFlag for all displayed subtree organizations to the setting implied by the method name.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward selectPushOrgMgrLevAll (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        setPullFlag(organizationSelectionTreeForm.getSelectionSubTreeOrgs(), OrgSelControlOption.ORGMGRLEV.getKey());
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method sets the pullFlag for all displayed subtree organizations to the setting implied by the method name.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward selectPushLevOneAll (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        setPullFlag(organizationSelectionTreeForm.getSelectionSubTreeOrgs(), OrgSelControlOption.LEVONE.getKey());
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method sets the pullFlag for all displayed subtree organizations to the setting implied by the method name.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward selectPushLevZeroAll (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        setPullFlag(organizationSelectionTreeForm.getSelectionSubTreeOrgs(), OrgSelControlOption.LEVZERO.getKey());
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method sets the pullFlags for each of the objects in the list to the pullFlagValue.
     * 
     * @param selOrgs
     * @param pullFlagValue
     */
    protected void setPullFlag (List <BudgetConstructionPullup> selOrgs, Integer pullFlagValue) {
        
        for (int i = 0; i < selOrgs.size(); i++) {
            selOrgs.get(i).setPullFlag(pullFlagValue);
        }
    }

    /**
     * This method checks that at lest one organization is selected and stores the selection settings.
     * If no organization is selected, an error message is displayed to the user.
     * 
     * @param selectionSubTreeOrgs
     * @return
     */
    protected boolean storedSelectedOrgs (List <BudgetConstructionPullup> selectionSubTreeOrgs) {

        boolean fndSel = false;
        BusinessObjectService boService = (BusinessObjectService) SpringContext.getBean(BusinessObjectService.class);
        
        // check to see if at least one pullflag is set and store the pullflag settings for currently displayed set of orgs
        // then call the position pick list tool, otherwise complain to the user
        Iterator selOrgsIter = selectionSubTreeOrgs.iterator();
        while (!fndSel && selOrgsIter.hasNext()){
            BudgetConstructionPullup selOrg = (BudgetConstructionPullup) selOrgsIter.next();
            if (selOrg.getPullFlag() > 0){
                fndSel = true;
                boService.save(selectionSubTreeOrgs);
            }
            
        }
        if (!fndSel){
            GlobalVariables.getErrorMap().putError("selectionSubTreeOrgs","error.budget.orgNotSelected");
        }
        return fndSel;
        
    }

    /**
     * This method checks the selection and calls the Position Pick list screen action.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performPositionPick (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        if (!storedSelectedOrgs(organizationSelectionTreeForm.getSelectionSubTreeOrgs())){
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        } else {

            //TODO this will eventually change to call the position picklist screen
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

    }

    /**
     * This method checks the selection and calls the Budgeted Incumbents Pick list screen action.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performIncumbentPick (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        if (!storedSelectedOrgs(organizationSelectionTreeForm.getSelectionSubTreeOrgs())){
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        } else {


            // build out base path for return location, using config service
            String basePath = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.APPLICATION_URL_KEY);

            // build out the actual form key that will be used to retrieve the form on refresh
            String callerDocFormKey = GlobalVariables.getUserSession().addObject(form);

            // now add required parameters
            Properties parameters = new Properties();
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);

            parameters.put(KFSConstants.DOC_FORM_KEY, callerDocFormKey);
            parameters.put(KFSConstants.BACK_LOCATION, basePath + mapping.getPath() + ".do");

            parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, "org.kuali.module.budget.bo.BudgetConstructionIntendedIncumbentSelect");
            parameters.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
            parameters.put("showInitialResults", "true");
            parameters.put("universityFiscalYear", organizationSelectionTreeForm.getUniversityFiscalYear().toString());

            parameters.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier());
            
//            String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + KFSConstants.GL_MODIFIED_INQUIRY_ACTION, parameters);
            String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + "budgetTempListLookup.do", parameters);
            
            return new ActionForward(lookupUrl, true);

            //TODO this will eventually change to call the budgeted incumbents picklist screen
//            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

    }

    /**
     * This method checks the selection and calls the Budget Documents pick list screen action.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performShowBudgetDocs (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        if (!storedSelectedOrgs(organizationSelectionTreeForm.getSelectionSubTreeOrgs())){
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        } else {

            //TODO this will eventually change to call the budget documents picklist screen using the showall mode
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

    }

    /**
     * This method checks the selection and performs the Pull up screen action.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performPullUp (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        if (!storedSelectedOrgs(organizationSelectionTreeForm.getSelectionSubTreeOrgs())){
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        } else {

            //TODO this will eventually change to perform pullup
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

    }

    /**
     * This method checks the selection and performs the show budget docs to be pulled up screen action.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performShowPullUpBudgetDocs (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        if (!storedSelectedOrgs(organizationSelectionTreeForm.getSelectionSubTreeOrgs())){
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        } else {

            //TODO this will eventually change to perform show budget docs to be pulled up
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

    }

    /**
     * This method checks the selection and performs the Push down screen action.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performPushDown (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        if (!storedSelectedOrgs(organizationSelectionTreeForm.getSelectionSubTreeOrgs())){
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        } else {

            //TODO this will eventually change to perform pushdown
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

    }

    /**
     * This method checks the selection and performs the show budget docs to be pushed down screen action.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performShowPushDownBudgetDocs (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        if (!storedSelectedOrgs(organizationSelectionTreeForm.getSelectionSubTreeOrgs())){
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        } else {

            //TODO this will eventually change to perform show budget docs to be pushed down
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

    }

    public ActionForward performReport (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        //TODO use this method as a template to create an action for a specific report button when running in REPORTS mode 
        
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        if (!storedSelectedOrgs(organizationSelectionTreeForm.getSelectionSubTreeOrgs())){
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        } else {

            //TODO this will eventually change to call the budgeted incumbents picklist screen
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

    }
}

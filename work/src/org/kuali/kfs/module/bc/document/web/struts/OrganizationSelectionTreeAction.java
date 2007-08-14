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
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.BCConstants.OrgSelOpMode;
import org.kuali.module.budget.bo.BudgetConstructionOrganizationReports;
import org.kuali.module.budget.web.struts.form.OrganizationSelectionTreeForm;

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
        // TODO Auto-generated method stub
        super.checkAuthorization(form, methodToCall);
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

        OrgSelOpMode opMode = OrgSelOpMode.valueOf(orgSelTreeForm.getOperatingMode());  
        switch (opMode){
            case SALSET:
                orgSelTreeForm.setOperatingModeTitle("Budget Salary Setting Organization Selection");
                break;
            case REPORTS:
                orgSelTreeForm.setOperatingModeTitle("BC Reports Organization Selection");
                break;
            case PULLUP:
                orgSelTreeForm.setOperatingModeTitle("BC Pull Up Organization Selection");
                break;
            case PUSHDOWN:
                orgSelTreeForm.setOperatingModeTitle("BC Push Down Organization Selection");
                break;
            default:
                // default to ACCOUNT operating mode
                orgSelTreeForm.setOperatingModeTitle("Budgeted Account List Search Organization Selection");
                break;
        }
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward returnToCaller(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        OrganizationSelectionTreeForm orgSelTreeForm = (OrganizationSelectionTreeForm) form; 
        
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
            }
        } else {
            organizationSelectionTreeForm.setPointOfViewOrg(new BudgetConstructionOrganizationReports());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
}

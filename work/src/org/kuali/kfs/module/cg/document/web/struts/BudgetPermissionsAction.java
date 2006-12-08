/*
 * Copyright 2006 The Kuali Foundation.
 * 
 * $Source$
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
package org.kuali.module.kra.budget.web.struts.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.authorization.DocumentActionFlags;
import org.kuali.core.bo.AdHocRoutePerson;
import org.kuali.core.bo.AdHocRouteWorkgroup;
import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.rule.event.AddAdHocRoutePersonEvent;
import org.kuali.core.rule.event.AddAdHocRouteWorkgroupEvent;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.kra.budget.bo.BudgetAdHocOrg;
import org.kuali.module.kra.budget.bo.BudgetAdHocPermission;
import org.kuali.module.kra.budget.bo.BudgetAdHocWorkgroup;
import org.kuali.module.kra.budget.document.BudgetDocument;
import org.kuali.module.kra.budget.web.struts.form.BudgetForm;

/**
 * This class handles Actions for Research Administration permissions page.
 * 
 * 
 */
public class BudgetPermissionsAction extends BudgetAction {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetPermissionsAction.class);
    
    @Override
    public ActionForward insertAdHocRoutePerson(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;
        BudgetDocument document = budgetForm.getBudgetDocument();

        // check authorization
        DocumentActionFlags flags = getDocumentActionFlags(document);
        if (!flags.getCanAdHocRoute()) {
            throw buildAuthorizationException("ad-hoc route", document);
        }
        
        AdHocRoutePerson adHocRoutePerson = (AdHocRoutePerson) budgetForm.getNewAdHocRoutePerson();

        // check business rules
        boolean rulePassed = SpringServiceLocator.getKualiRuleService().applyRules(new AddAdHocRoutePersonEvent(document, (AdHocRoutePerson) budgetForm.getNewAdHocRoutePerson()));
        
        if (rulePassed) {
            BudgetAdHocPermission newAdHocPermission = budgetForm.getNewAdHocPermission();
            UniversalUser user = SpringServiceLocator.getUniversalUserService().getUniversalUser(new AuthenticationUserId(adHocRoutePerson.getId()));
            newAdHocPermission.setPersonSystemIdentifier(user.getPersonUniversalIdentifier());
            user.setPersonUserIdentifier(StringUtils.upperCase(user.getPersonUserIdentifier()));
            newAdHocPermission.setUser(user);
            newAdHocPermission.setPersonAddedTimestamp(SpringServiceLocator.getDateTimeService().getCurrentTimestamp());
            newAdHocPermission.setAddedByPerson(SpringServiceLocator.getWebAuthenticationService().getNetworkId(request));
            budgetForm.getBudgetDocument().getBudget().getAdHocPermissions().add(newAdHocPermission);
            budgetForm.setNewAdHocPermission(new BudgetAdHocPermission());
            budgetForm.setNewAdHocRoutePerson(new AdHocRoutePerson());
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    @Override
    public ActionForward insertAdHocRouteWorkgroup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;
        BudgetDocument document = budgetForm.getBudgetDocument();

        // check authorization
        DocumentActionFlags flags = getDocumentActionFlags(document);
        if (!flags.getCanAdHocRoute()) {
            throw buildAuthorizationException("ad-hoc route", document);
        }
        
        AdHocRouteWorkgroup adHocRouteWorkgroup = (AdHocRouteWorkgroup) budgetForm.getNewAdHocRouteWorkgroup();

        // check business rules
        boolean rulePassed = SpringServiceLocator.getKualiRuleService().applyRules(new AddAdHocRouteWorkgroupEvent(document, (AdHocRouteWorkgroup) budgetForm.getNewAdHocRouteWorkgroup()));
        
        if (rulePassed) {
            BudgetAdHocWorkgroup newAdHocWorkgroup = new BudgetAdHocWorkgroup(adHocRouteWorkgroup.getId());
            newAdHocWorkgroup.setBudgetPermissionCode(budgetForm.getNewAdHocWorkgroupPermissionCode());
            newAdHocWorkgroup.setPersonAddedTimestamp(SpringServiceLocator.getDateTimeService().getCurrentTimestamp());
            newAdHocWorkgroup.setAddedByPerson(SpringServiceLocator.getWebAuthenticationService().getNetworkId(request));
            budgetForm.getBudgetDocument().getBudget().getAdHocWorkgroups().add(newAdHocWorkgroup);
            budgetForm.setNewAdHocRouteWorkgroup(new AdHocRouteWorkgroup());
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    /**
     * This method will remove the selected ad-hoc person from the list.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

        ((BudgetForm) form).getBudgetDocument().getBudget().getAdHocPermissions().remove(getLineToDelete(request));

        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    /**
     * This method will add a new ad-hoc org to the list.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward addOrg(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

        BudgetForm budgetForm = (BudgetForm) form;

        if (budgetForm.getNewAdHocOrg().getFiscalCampusCode() == null) {
            // Add page error.
            GlobalVariables.getErrorMap().putError("newAdHocOrg", KeyConstants.ERROR_NO_ORG_SELECTED, new String[] {});
        }
        else {
            BudgetAdHocOrg newAdHocOrg = budgetForm.getNewAdHocOrg();
            newAdHocOrg.setPersonAddedTimestamp(SpringServiceLocator.getDateTimeService().getCurrentTimestamp());
            newAdHocOrg.setAddedByPerson(SpringServiceLocator.getWebAuthenticationService().getNetworkId(request));
            budgetForm.getBudgetDocument().getBudget().getAdHocOrgs().add(newAdHocOrg);
            budgetForm.setNewAdHocOrg(new BudgetAdHocOrg());
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    } 

    /**
     * This method will remove the selected ad-hoc org from the list.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteOrg(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

        ((BudgetForm) form).getBudgetDocument().getBudget().getAdHocOrgs().remove(getLineToDelete(request));

        return mapping.findForward(Constants.MAPPING_BASIC);
    }


    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetForm budgetForm = (BudgetForm) form;

        List adHocPermissions = budgetForm.getBudgetDocument().getBudget().getAdHocPermissions();
        List adHocOrgs = budgetForm.getBudgetDocument().getBudget().getAdHocOrgs();
        List adHocWorkgroups = budgetForm.getBudgetDocument().getBudget().getAdHocWorkgroups();
        
        this.load(mapping, budgetForm, request, response);

        budgetForm.getBudgetDocument().getBudget().setAdHocPermissions(adHocPermissions);
        budgetForm.getBudgetDocument().getBudget().setAdHocOrgs(adHocOrgs);
        budgetForm.getBudgetDocument().getBudget().setAdHocWorkgroups(adHocWorkgroups);
        
        SpringServiceLocator.getDocumentService().updateDocument(budgetForm.getBudgetDocument());
        
        budgetForm.getBudgetDocument().populateDocumentForRouting();
        budgetForm.getBudgetDocument().getDocumentHeader().getWorkflowDocument().saveRoutingData();

        return super.save(mapping, budgetForm, request, response);
    }
    
    
    @Override
    public ActionForward reload(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        BudgetForm budgetForm = (BudgetForm) form;
        
        budgetForm.setNewAdHocPermission(new BudgetAdHocPermission());
        budgetForm.setNewAdHocOrg(new BudgetAdHocOrg());
        
        ActionForward forward = super.reload(mapping, budgetForm, request, response);
        
        SpringServiceLocator.getPersistenceService().retrieveReferenceObject(budgetForm.getBudgetDocument().getBudget(), "adHocPermissions");
        
        return forward;
    }
}

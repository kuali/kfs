/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.kra.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.core.bo.AdHocRoutePerson;
import org.kuali.core.bo.AdHocRouteWorkgroup;
import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.rule.event.AddAdHocRoutePersonEvent;
import org.kuali.core.rule.event.AddAdHocRouteWorkgroupEvent;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.struts.action.KualiDocumentActionBase;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.kra.KraKeyConstants;
import org.kuali.module.kra.bo.BudgetAdHocOrg;
import org.kuali.module.kra.bo.BudgetAdHocPermission;
import org.kuali.module.kra.bo.BudgetAdHocWorkgroup;
import org.kuali.module.kra.budget.document.BudgetDocument;
import org.kuali.module.kra.budget.web.struts.form.BudgetForm;
import org.kuali.module.kra.document.ResearchDocument;
import org.kuali.module.kra.web.struts.form.ResearchDocumentFormBase;

import edu.iu.uis.eden.clientapp.IDocHandler;

public abstract class ResearchDocumentActionBase extends KualiDocumentActionBase {

    public ResearchDocumentActionBase() {
        // TODO Auto-generated constructor stub
    }

    /**
     * This method will load the document, which was previously saved
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ResearchDocumentFormBase researchForm = (ResearchDocumentFormBase) form;
        researchForm.setDocId(researchForm.getDocument().getDocumentNumber());
        super.loadDocument(researchForm);

        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ActionForward forward = super.docHandler(mapping, form, request, response);
        ResearchDocumentFormBase researchForm = (ResearchDocumentFormBase) form;

        if (IDocHandler.INITIATE_COMMAND.equals(researchForm.getCommand())) {
            // do something?
            researchForm.getResearchDocument().initialize();
        }
        return forward;
    }
    
    @Override
    public ActionForward insertAdHocRoutePerson(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResearchDocumentFormBase researchForm = (ResearchDocumentFormBase) form;
        ResearchDocument researchDocument = (ResearchDocument) researchForm.getDocument();

        // check authorization
        DocumentActionFlags flags = getDocumentActionFlags(researchDocument);
        if (!flags.getCanAdHocRoute()) {
            throw buildAuthorizationException("ad-hoc route", researchDocument);
        }
        
        AdHocRoutePerson adHocRoutePerson = (AdHocRoutePerson) researchForm.getNewAdHocRoutePerson();

        // check business rules
        boolean rulePassed = SpringServiceLocator.getKualiRuleService().applyRules(new AddAdHocRoutePersonEvent(researchDocument, (AdHocRoutePerson) researchForm.getNewAdHocRoutePerson()));
        
        if (rulePassed) {
            BudgetAdHocPermission newAdHocPermission = researchForm.getNewAdHocPermission();
            UniversalUser user = SpringServiceLocator.getUniversalUserService().getUniversalUser(new AuthenticationUserId(adHocRoutePerson.getId()));
            newAdHocPermission.setPersonUniversalIdentifier(user.getPersonUniversalIdentifier());
            user.setPersonUserIdentifier(StringUtils.upperCase(user.getPersonUserIdentifier()));
            newAdHocPermission.setUser(user);
            newAdHocPermission.setPersonAddedTimestamp(SpringServiceLocator.getDateTimeService().getCurrentTimestamp());
            newAdHocPermission.setAddedByPerson(SpringServiceLocator.getWebAuthenticationService().getNetworkId(request));
            researchDocument.getAdHocPermissions().add(newAdHocPermission);
            researchForm.setNewAdHocPermission(new BudgetAdHocPermission());
            researchForm.setNewAdHocRoutePerson(new AdHocRoutePerson());
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    @Override
    public ActionForward insertAdHocRouteWorkgroup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResearchDocumentFormBase researchForm = (ResearchDocumentFormBase) form;
        ResearchDocument researchDocument = (ResearchDocument) researchForm.getDocument();

        // check authorization
        DocumentActionFlags flags = getDocumentActionFlags(researchDocument);
        if (!flags.getCanAdHocRoute()) {
            throw buildAuthorizationException("ad-hoc route", researchDocument);
        }
        
        AdHocRouteWorkgroup adHocRouteWorkgroup = (AdHocRouteWorkgroup) researchForm.getNewAdHocRouteWorkgroup();

        // check business rules
        boolean rulePassed = SpringServiceLocator.getKualiRuleService().applyRules(
                new AddAdHocRouteWorkgroupEvent(researchDocument, (AdHocRouteWorkgroup) researchForm.getNewAdHocRouteWorkgroup()));
        
        if (rulePassed) {
            BudgetAdHocWorkgroup newAdHocWorkgroup = new BudgetAdHocWorkgroup(adHocRouteWorkgroup.getId());
            newAdHocWorkgroup.setBudgetPermissionCode(researchForm.getNewAdHocWorkgroupPermissionCode());
            newAdHocWorkgroup.setPersonAddedTimestamp(SpringServiceLocator.getDateTimeService().getCurrentTimestamp());
            newAdHocWorkgroup.setAddedByPerson(SpringServiceLocator.getWebAuthenticationService().getNetworkId(request));
            researchDocument.getAdHocWorkgroups().add(newAdHocWorkgroup);
            researchForm.setNewAdHocRouteWorkgroup(new AdHocRouteWorkgroup());
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
        
        ResearchDocumentFormBase researchForm = (ResearchDocumentFormBase) form;
        ResearchDocument researchDocument = (ResearchDocument) researchForm.getDocument();
        researchDocument.getAdHocPermissions().remove(getLineToDelete(request));

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
    public ActionForward deleteWorkgroup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        
        ResearchDocumentFormBase researchForm = (ResearchDocumentFormBase) form;
        ResearchDocument researchDocument = (ResearchDocument) researchForm.getDocument();
        researchDocument.getAdHocWorkgroups().remove(getLineToDelete(request));

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

        ResearchDocumentFormBase researchForm = (ResearchDocumentFormBase) form;
        ResearchDocument researchDocument = (ResearchDocument) researchForm.getDocument();

        if (researchForm.getNewAdHocOrg().getFiscalCampusCode() == null) {
            // Add page error.
            GlobalVariables.getErrorMap().putError("newAdHocOrg", KraKeyConstants.ERROR_NO_ORG_SELECTED, new String[] {});
        }
        else {
            BudgetAdHocOrg newAdHocOrg = researchForm.getNewAdHocOrg();
            newAdHocOrg.setPersonAddedTimestamp(SpringServiceLocator.getDateTimeService().getCurrentTimestamp());
            newAdHocOrg.setAddedByPerson(SpringServiceLocator.getWebAuthenticationService().getNetworkId(request));
            researchDocument.getAdHocOrgs().add(newAdHocOrg);
            researchForm.setNewAdHocOrg(new BudgetAdHocOrg());
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

        ResearchDocumentFormBase researchForm = (ResearchDocumentFormBase) form;
        ResearchDocument researchDocument = (ResearchDocument) researchForm.getDocument();
        researchDocument.getAdHocOrgs().remove(getLineToDelete(request));

        return mapping.findForward(Constants.MAPPING_BASIC);
    }
}

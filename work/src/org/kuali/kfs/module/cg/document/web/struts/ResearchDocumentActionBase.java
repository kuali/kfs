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
package org.kuali.kfs.module.cg.document.web.struts;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.CGKeyConstants;
import org.kuali.kfs.module.cg.businessobject.AdhocOrg;
import org.kuali.kfs.module.cg.businessobject.AdhocPerson;
import org.kuali.kfs.module.cg.document.ResearchDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.AuthenticationService;
import org.kuali.rice.kns.bo.AdHocRoutePerson;
import org.kuali.rice.kns.rule.event.AddAdHocRoutePersonEvent;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase;
import org.kuali.rice.kns.web.struts.form.KualiForm;

public abstract class ResearchDocumentActionBase extends KualiDocumentActionBase {

    /**
     * Constructs a ResearchDocumentActionBase.java.
     */
    public ResearchDocumentActionBase() {
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

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    @Override
    /**
     * Overriding headerTab to customize how clearing tab state works on Budget. Specifically, additional attributes (selected task
     * and period) should be cleared any time header navigation occurs.
     */
    public ActionForward headerTab(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ((KualiForm) form).setTabStates(new HashMap());

        return super.headerTab(mapping, form, request, response);
    }


    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ActionForward forward = super.docHandler(mapping, form, request, response);
        ResearchDocumentFormBase researchForm = (ResearchDocumentFormBase) form;

        if (KEWConstants.INITIATE_COMMAND.equals(researchForm.getCommand())) {
            // do something?
            researchForm.getResearchDocument().initialize();
        }
        return forward;
    }


    public ActionForward notes(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        this.load(mapping, form, request, response);

        ResearchDocumentFormBase researchDocumentForm = (ResearchDocumentFormBase) form;

        if (researchDocumentForm.getDocument().getDocumentHeader().isBoNotesSupport() && (researchDocumentForm.getDocument().getDocumentHeader().getBoNotes() == null || researchDocumentForm.getDocument().getDocumentHeader().getBoNotes().isEmpty())) {
            researchDocumentForm.getDocument().refreshReferenceObject("documentHeader");
        }

        researchDocumentForm.setTabStates(new HashMap());

        return mapping.findForward("notes");
    }

    @Override
    public ActionForward insertAdHocRoutePerson(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResearchDocumentFormBase researchForm = (ResearchDocumentFormBase) form;
        ResearchDocument researchDocument = (ResearchDocument) researchForm.getDocument();

        // check authorization
        Map documentActions = researchForm.getDocumentActions();       
        if (!documentActions.containsKey(KNSConstants.KUALI_ACTION_CAN_EDIT)) {
            throw buildAuthorizationException("ad-hoc route", researchDocument);
        }

        AdHocRoutePerson adHocRoutePerson = (AdHocRoutePerson) researchForm.getNewAdHocRoutePerson();

        // check business rules
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AddAdHocRoutePersonEvent(researchDocument, (AdHocRoutePerson) researchForm.getNewAdHocRoutePerson()));

        if (rulePassed) {
            AdhocPerson newAdHocPermission = researchForm.getNewAdHocPerson();
            Person user = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).getPersonByPrincipalName(adHocRoutePerson.getId());
            newAdHocPermission.setPrincipalId(user.getPrincipalId());
            // TODO: FIXME
            //user.setPrincipalName(StringUtils.upperCase(user.getPrincipalName()));
            if (adHocRoutePerson.getActionRequested() == null || StringUtils.isBlank(adHocRoutePerson.getActionRequested())) {
                newAdHocPermission.setAdhocTypeCode(CGConstants.AD_HOC_PERMISSION);
            }
            else {
                newAdHocPermission.setActionRequested(adHocRoutePerson.getActionRequested());
                newAdHocPermission.setAdhocTypeCode(CGConstants.AD_HOC_APPROVER);
            }
            newAdHocPermission.setUser(user);
            newAdHocPermission.setPersonAddedTimestamp(SpringContext.getBean(DateTimeService.class).getCurrentTimestamp());
            newAdHocPermission.setAddedByPerson(SpringContext.getBean(AuthenticationService.class).getPrincipalName(request));
            researchDocument.getAdhocPersons().add(newAdHocPermission);
            researchForm.setNewAdHocPerson(new AdhocPerson());
            researchForm.setNewAdHocRoutePerson(new AdHocRoutePerson());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
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
    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ResearchDocumentFormBase researchForm = (ResearchDocumentFormBase) form;
        ResearchDocument researchDocument = (ResearchDocument) researchForm.getDocument();
        researchDocument.getAdhocPersons().remove(getLineToDelete(request));

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
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
    public ActionForward addOrg(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ResearchDocumentFormBase researchForm = (ResearchDocumentFormBase) form;
        ResearchDocument researchDocument = (ResearchDocument) researchForm.getDocument();

        if (researchForm.getNewAdHocOrg().getFiscalCampusCode() == null) {
            // Add page error.
            GlobalVariables.getErrorMap().putError("newAdHocOrg", CGKeyConstants.ERROR_NO_ORG_SELECTED, new String[] {});
        }
        else {
            AdhocOrg newAdHocOrg = researchForm.getNewAdHocOrg();
            if (newAdHocOrg.getActionRequested() == null) {
                newAdHocOrg.setAdhocTypeCode(CGConstants.AD_HOC_PERMISSION);
            }
            else {
                newAdHocOrg.setAdhocTypeCode(CGConstants.AD_HOC_APPROVER);
            }
            newAdHocOrg.setPersonAddedTimestamp(SpringContext.getBean(DateTimeService.class).getCurrentTimestamp());
            newAdHocOrg.setAddedByPerson(SpringContext.getBean(AuthenticationService.class).getPrincipalName(request));
            researchDocument.getAdhocOrgs().add(newAdHocOrg);
            researchForm.setNewAdHocOrg(new AdhocOrg());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
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
    public ActionForward deleteOrg(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ResearchDocumentFormBase researchForm = (ResearchDocumentFormBase) form;
        ResearchDocument researchDocument = (ResearchDocument) researchForm.getDocument();
        researchDocument.getAdhocOrgs().remove(getLineToDelete(request));

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
}


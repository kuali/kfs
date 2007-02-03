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
package org.kuali.module.kra.routingform.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.kra.KraKeyConstants;
import org.kuali.module.kra.routingform.bo.RoutingFormAdHocOrg;
import org.kuali.module.kra.routingform.bo.RoutingFormAdHocPerson;
import org.kuali.module.kra.routingform.bo.RoutingFormAdHocWorkgroup;
import org.kuali.module.kra.routingform.document.RoutingFormDocument;
import org.kuali.module.kra.routingform.web.struts.form.RoutingForm;

/**
 * This class handles Actions for the Routing Form Permissions page.
 * 
 * 
 */
public class RoutingFormPermissionsAction extends RoutingFormAction {
    
    public ActionForward insertAdHocPerson(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm) form;
        RoutingFormDocument document = routingForm.getRoutingFormDocument();

        RoutingFormAdHocPerson adHocPerson = (RoutingFormAdHocPerson) routingForm.getNewAdHocPerson();

        // check if person is valid
         boolean rulePassed = true; // SpringServiceLocator.getKualiRuleService().applyRules(new AddAdHocRoutePersonEvent(document, (AdHocRoutePerson) budgetForm.getNewAdHocRoutePerson()));
        
        if (rulePassed) {
            UniversalUser user = SpringServiceLocator.getUniversalUserService().getUniversalUser(new AuthenticationUserId(adHocPerson.getPersonSystemIdentifier()));
            adHocPerson.setUser(user);
            adHocPerson.setPersonAddedTimestamp(SpringServiceLocator.getDateTimeService().getCurrentTimestamp());
            adHocPerson.setAddedByPerson(SpringServiceLocator.getWebAuthenticationService().getNetworkId(request));
            routingForm.getRoutingFormDocument().getRoutingFormAdHocPeople().add(adHocPerson);
            routingForm.setNewAdHocPerson(new RoutingFormAdHocPerson());
        }

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
    public ActionForward insertAdHocOrg(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

        RoutingForm routingForm = (RoutingForm) form;

        if (routingForm.getNewAdHocOrg().getFiscalCampusCode() == null) {
            // Add page error.
            GlobalVariables.getErrorMap().putError("newAdHocOrg", KraKeyConstants.ERROR_NO_ORG_SELECTED, new String[] {});
        }
        else {
            RoutingFormAdHocOrg newAdHocOrg = routingForm.getNewAdHocOrg();
            newAdHocOrg.setPersonAddedTimestamp(SpringServiceLocator.getDateTimeService().getCurrentTimestamp());
            newAdHocOrg.setAddedByPerson(SpringServiceLocator.getWebAuthenticationService().getNetworkId(request));
            routingForm.getRoutingFormDocument().getRoutingFormAdHocOrgs().add(newAdHocOrg);
            routingForm.setNewAdHocOrg(new RoutingFormAdHocOrg());
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    public ActionForward insertAdHocWorkgroup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm) form;
        RoutingFormDocument document = routingForm.getRoutingFormDocument();

        RoutingFormAdHocWorkgroup adHocWorkgroup = (RoutingFormAdHocWorkgroup) routingForm.getNewAdHocWorkgroup();

        // check if person is valid
         boolean rulePassed = true; // SpringServiceLocator.getKualiRuleService().applyRules(new AddAdHocRoutePersonEvent(document, (AdHocRoutePerson) budgetForm.getNewAdHocRoutePerson()));
        
        if (rulePassed) {
            adHocWorkgroup.setPersonAddedTimestamp(SpringServiceLocator.getDateTimeService().getCurrentTimestamp());
            adHocWorkgroup.setAddedByPerson(SpringServiceLocator.getWebAuthenticationService().getNetworkId(request));
            routingForm.getRoutingFormDocument().getRoutingFormAdHocWorkgroups().add(adHocWorkgroup);
            routingForm.setNewAdHocWorkgroup(new RoutingFormAdHocWorkgroup());
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
    public ActionForward deleteAdHocPerson(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        ((RoutingForm) form).getRoutingFormDocument().getRoutingFormAdHocPeople().remove(getLineToDelete(request));
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
    public ActionForward deleteAdHocOrg(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        ((RoutingForm) form).getRoutingFormDocument().getRoutingFormAdHocOrgs().remove(getLineToDelete(request));
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
    public ActionForward deleteAdHocWorkgroup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        ((RoutingForm) form).getRoutingFormDocument().getRoutingFormAdHocWorkgroups().remove(getLineToDelete(request));
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.load(mapping, form, request, response);
        return super.save(mapping, form, request, response);
    }
}
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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.kra.budget.web.struts.form.BudgetOverviewFormHelper;
import org.kuali.module.kra.routingform.bo.RoutingFormBudget;
import org.kuali.module.kra.routingform.bo.RoutingFormPersonnel;
import org.kuali.module.kra.routingform.rules.event.RunRoutingFormAuditEvent;
import org.kuali.module.kra.routingform.web.struts.form.RoutingForm;
import org.kuali.module.kra.web.struts.action.ResearchDocumentActionBase;

public class RoutingFormAction extends ResearchDocumentActionBase {
    
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.execute(mapping, form, request, response);

        RoutingForm routingForm = (RoutingForm) form;

        if (routingForm.isAuditActivated()) {
            SpringServiceLocator.getKualiRuleService().applyRules(new RunRoutingFormAuditEvent(routingForm.getRoutingFormDocument()));
        }

        return forward;
    }
    
    public ActionForward mainpage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm) form;
        routingForm.setTabStates(new ArrayList());
        
        return mapping.findForward("mainpage");
    }
    
    public ActionForward personnel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm) form;
        
        routingForm.setTabStates(new ArrayList());
        
        // Make sure all the reference objects fields are filled. Since most pages don't care about them this is important.
        for(RoutingFormPersonnel routingFormPerson : routingForm.getRoutingFormDocument().getRoutingFormPersonnel()) {
            routingFormPerson.refresh();
        }
        
        return mapping.findForward("personnel");
    }
    
    public ActionForward researchrisks(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        RoutingForm routingForm = (RoutingForm) form;
        SpringServiceLocator.getPersistenceService().retrieveReferenceObject(routingForm.getRoutingFormDocument(), "routingFormResearchRisks");
        routingForm.setTabStates(new ArrayList());
        return mapping.findForward("researchrisks");
    }
    
    public ActionForward projectdetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        RoutingForm routingForm = (RoutingForm) form;
        SpringServiceLocator.getPersistenceService().retrieveReferenceObject(routingForm.getRoutingFormDocument(), "routingFormQuestions");
        routingForm.setTabStates(new ArrayList());
        return mapping.findForward("projectdetails");
    }
    
    public ActionForward agencyspecific(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward("agencyspecific");
    }
    
    public ActionForward sf424(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward("sf424");
    }

    public ActionForward link(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm)form;
        routingForm.setTabStates(new ArrayList());
        
        if (routingForm.getRoutingFormDocument().getRoutingFormBudgetNumber() != null) {
            new RoutingFormLinkAction().setupBudgetPeriodData(routingForm);
            RoutingFormBudget routingFormBudget = routingForm.getRoutingFormDocument().getRoutingFormBudget();

            for (BudgetOverviewFormHelper budgetOverviewFormHelper : routingForm.getPeriodBudgetOverviewFormHelpers()) {
                if (budgetOverviewFormHelper.getBudgetPeriod().getBudgetPeriodSequenceNumber().compareTo(routingFormBudget.getRoutingFormBudgetMinimumPeriodNumber()) >= 0 &&
                        budgetOverviewFormHelper.getBudgetPeriod().getBudgetPeriodSequenceNumber().compareTo(routingFormBudget.getRoutingFormBudgetMaximumPeriodNumber()) <= 0) {
                    budgetOverviewFormHelper.setSelected(true);
                }
            }
        }
        return mapping.findForward("link");
    }

    public ActionForward notes(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm) form;
        routingForm.setTabStates(new ArrayList());
        return mapping.findForward("notes");
    }

    public ActionForward output(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward("output");
    }

    public ActionForward template(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward("template");
    }

    public ActionForward auditmode(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.load(mapping, form, request, response);

        return mapping.findForward("auditmode");
    }

    public ActionForward permissions(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm) form;
        List referenceObjects = new ArrayList();
        referenceObjects.add("adhocPersons");
        referenceObjects.add("adhocOrgs");
        referenceObjects.add("adhocWorkgroups");
        SpringServiceLocator.getPersistenceService().retrieveReferenceObjects(routingForm.getRoutingFormDocument(), referenceObjects);
        return mapping.findForward("permissions");
    }

    public ActionForward approvals(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm) form;
        List referenceObjects = new ArrayList();
        referenceObjects.add("adhocPersons");
        referenceObjects.add("adhocOrgs");
        referenceObjects.add("adhocWorkgroups");
        SpringServiceLocator.getPersistenceService().retrieveReferenceObjects(routingForm.getRoutingFormDocument(), referenceObjects);
        return mapping.findForward("approvals");
    }
    
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm) form;

        // Check if user has permission to save
        routingForm.populateAuthorizationFields(SpringServiceLocator.getDocumentAuthorizationService().getDocumentAuthorizer(routingForm.getRoutingFormDocument()));
        if (!"TRUE".equals(routingForm.getEditingMode().get(AuthorizationConstants.EditMode.VIEW_ONLY))) {
            super.save(mapping, form, request, response);
        }
        
        if (routingForm.isAuditActivated()) {
            return mapping.findForward("auditmode");
        }
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
}
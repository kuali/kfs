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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.businessobject.RoutingFormBudget;
import org.kuali.kfs.module.cg.businessobject.RoutingFormPersonnel;
import org.kuali.kfs.module.cg.document.RoutingFormDocument;
import org.kuali.kfs.module.cg.document.validation.event.RunRoutingFormAuditEvent;
import org.kuali.kfs.module.cg.document.validation.impl.AuditCluster;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.PersistenceService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;

public class RoutingFormAction extends ResearchDocumentActionBase {

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.execute(mapping, form, request, response);

        RoutingForm routingForm = (RoutingForm) form;

        if (routingForm.isAuditActivated()) {
            SpringContext.getBean(KualiRuleService.class).applyRules(new RunRoutingFormAuditEvent(routingForm.getRoutingFormDocument()));
        }

        return forward;
    }

    public ActionForward mainpage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm) form;

        return mapping.findForward("mainpage");
    }

    public ActionForward personnel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm) form;

        // Make sure all the reference objects fields are filled. Since most pages don't care about them this is important.
        for (RoutingFormPersonnel routingFormPerson : routingForm.getRoutingFormDocument().getRoutingFormPersonnel()) {
            //KFSMI-798 - refreshNonUpdatableReferences() used instead of refresh(), 
            //RoutingFormPersonnel does not have any updatable references
            routingFormPerson.refreshNonUpdateableReferences();
        }

        return mapping.findForward("personnel");
    }

    public ActionForward researchrisks(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        RoutingForm routingForm = (RoutingForm) form;
        SpringContext.getBean(PersistenceService.class).retrieveReferenceObject(routingForm.getRoutingFormDocument(), "routingFormResearchRisks");

        return mapping.findForward("researchrisks");
    }

    public ActionForward projectdetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        RoutingForm routingForm = (RoutingForm) form;
        SpringContext.getBean(PersistenceService.class).retrieveReferenceObject(routingForm.getRoutingFormDocument(), "routingFormQuestions");

        return mapping.findForward("projectdetails");
    }

    public ActionForward agencyspecific(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward("agencyspecific");
    }

    public ActionForward sf424(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward("sf424");
    }

    public ActionForward link(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm) form;

        if (routingForm.getRoutingFormDocument().getRoutingFormBudgetNumber() != null) {
            new RoutingFormLinkAction().setupBudgetPeriodData(routingForm);
            RoutingFormBudget routingFormBudget = routingForm.getRoutingFormDocument().getRoutingFormBudget();

            for (BudgetOverviewFormHelper budgetOverviewFormHelper : routingForm.getPeriodBudgetOverviewFormHelpers()) {
                if (budgetOverviewFormHelper.getBudgetPeriod().getBudgetPeriodSequenceNumber().compareTo(routingFormBudget.getRoutingFormBudgetMinimumPeriodNumber()) >= 0 && budgetOverviewFormHelper.getBudgetPeriod().getBudgetPeriodSequenceNumber().compareTo(routingFormBudget.getRoutingFormBudgetMaximumPeriodNumber()) <= 0) {
                    budgetOverviewFormHelper.setSelected(true);
                }
            }
        }
        return mapping.findForward("link");
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
        SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(routingForm.getRoutingFormDocument(), referenceObjects);
        return mapping.findForward("permissions");
    }

    public ActionForward approvals(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.load(mapping, form, request, response);
        RoutingForm routingForm = (RoutingForm) form;

        activateAndCountAuditErrors(routingForm);

        routingForm.getRoutingFormDocument().populateDocumentForRouting();
        routingForm.getRoutingFormDocument().getDocumentHeader().getWorkflowDocument().saveRoutingData();

        return mapping.findForward("approvals");
    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm) form;

        Map documentActions = routingForm.getDocumentActions();       
        if (documentActions.containsKey(KNSConstants.KUALI_ACTION_CAN_EDIT)) {
            super.save(mapping, form, request, response);
        }

        if (routingForm.isAuditActivated()) {
            return mapping.findForward("auditmode");
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    protected void setApprovalsMessage(RoutingForm routingForm) {
        RoutingFormDocument routingFormDocument = routingForm.getRoutingFormDocument();

        Map documentActions = routingForm.getDocumentActions();       
        if (documentActions.containsKey(KNSConstants.KUALI_ACTION_CAN_ROUTE) || documentActions.containsKey(KNSConstants.KUALI_ACTION_CAN_APPROVE)) {
            Person user = GlobalVariables.getUserSession().getPerson();
            if (routingForm.getRoutingFormDocument().isUserProjectDirector(user.getPrincipalId())) {
                routingForm.setApprovalsMessage(SpringContext.getBean(ParameterService.class).getParameterValue(RoutingFormDocument.class, CGConstants.APPROVALS_PROJECT_DIRECTOR_WORDING));
            }
            else if (routingFormDocument.getDocumentHeader().getWorkflowDocument().userIsInitiator(user)) {
                routingForm.setApprovalsMessage(SpringContext.getBean(ParameterService.class).getParameterValue(RoutingFormDocument.class, CGConstants.APPROVALS_INITIATOR_WORDING));
            }
            else {
                routingForm.setApprovalsMessage(SpringContext.getBean(ParameterService.class).getParameterValue(RoutingFormDocument.class, CGConstants.APPROVALS_DEFAULT_WORDING));
            }
        }
    }

    protected void activateAndCountAuditErrors(RoutingForm routingForm) {
        boolean auditErrorsPassed = SpringContext.getBean(KualiRuleService.class).applyRules(new RunRoutingFormAuditEvent(routingForm.getRoutingFormDocument()));

        Map auditErrorMap = GlobalVariables.getAuditErrorMap();
        int auditCount = 0;
        for (Iterator iter = auditErrorMap.keySet().iterator(); iter.hasNext();) {
            AuditCluster auditCluster = (AuditCluster) auditErrorMap.get(iter.next());
            auditCount += auditCluster.getSize();
        }

        routingForm.setNumAuditErrors(auditCount);
        if (!auditErrorsPassed) {
            routingForm.setAuditActivated(true);
        }
        else {
            setApprovalsMessage(routingForm);
        }
    }
}


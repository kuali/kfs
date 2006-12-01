/*
 * Copyright 2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/document/web/struts/RoutingFormAction.java,v $
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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.struts.action.KualiDocumentActionBase;
import org.kuali.module.kra.budget.web.struts.form.BudgetForm;
import org.kuali.module.kra.routingform.web.struts.form.RoutingForm;

import edu.iu.uis.eden.clientapp.IDocHandler;

public class RoutingFormAction extends KualiDocumentActionBase {
    
    public ActionForward mainpage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward("mainpage");
    }
    
    public ActionForward personnel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward("personnel");
    }
    
    public ActionForward researchrisks(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        RoutingForm routingForm = (RoutingForm) form;
        routingForm.setDocId(routingForm.getDocument().getDocumentNumber());
        this.loadDocument(routingForm);
        
//      Setup research risks if this is the first entry into page.
        if (routingForm.getRoutingFormDocument().getRoutingFormResearchRisks().isEmpty()) {
            SpringServiceLocator.getRoutingFormResearchRiskService().setupResearchRisks(routingForm.getRoutingFormDocument());
        }
        
        routingForm.setTabStates(new ArrayList());
        return mapping.findForward("researchrisks");
    }
    
    public ActionForward projectdetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward("projectdetails");
    }
    
    public ActionForward agencyspecific(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward("agencyspecific");
    }
    
    public ActionForward sf424(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward("sf424");
    }

    public ActionForward link(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward("link");
    }

    public ActionForward notes(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward("notes");
    }

    public ActionForward output(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward("output");
    }

    public ActionForward template(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward("template");
    }

    public ActionForward auditmode(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward("auditmode");
    }

    public ActionForward permissions(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward("permissions");
    }

    public ActionForward approvals(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward("approvals");
    }
    
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm) form;

        // Check if user has permission to save
        routingForm.populateAuthorizationFields(SpringServiceLocator.getDocumentAuthorizationService().getDocumentAuthorizer(routingForm.getRoutingFormDocument()));
        if (!"TRUE".equals(routingForm.getEditingMode().get(AuthorizationConstants.EditMode.VIEW_ONLY))) {
            super.save(mapping, form, request, response);
        }

        // TODO RF Audit Mode
        /*
        if (routingForm.isAuditActivated()) {
            routingForm.newTabState(true, true);
            return mapping.findForward("auditmode");
        }
        */
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ActionForward forward = super.docHandler(mapping, form, request, response);
        RoutingForm routingForm = (RoutingForm) form;

        if (IDocHandler.INITIATE_COMMAND.equals(routingForm.getCommand())) {
            routingForm.getRoutingFormDocument().setDocumentNumber(routingForm.getRoutingFormDocument().getDocumentNumber());
        }
        return forward;
    }
}

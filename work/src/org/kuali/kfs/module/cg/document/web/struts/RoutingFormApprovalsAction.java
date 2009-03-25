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
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.document.service.RoutingFormProjectDetailsService;
import org.kuali.kfs.module.cg.document.service.RoutingFormResearchRiskService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.AdHocRouteWorkgroup;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

public class RoutingFormApprovalsAction extends RoutingFormAction {

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.execute(mapping, form, request, response);
        RoutingForm routingForm = (RoutingForm) form;
        activateAndCountAuditErrors(routingForm);
        return forward;
    }

    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm) form;

        cacheAndLoad(mapping, form, request, response);
        ActionForward forward = super.save(mapping, form, request, response);

        routingForm.getRoutingFormDocument().populateDocumentForRouting();
        routingForm.getRoutingFormDocument().getDocumentHeader().getWorkflowDocument().saveRoutingData();

        return forward;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#route(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm) form;

        if (routingForm.getNumAuditErrors() != 0) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        cacheAndLoad(mapping, form, request, response);

        ActionForward forward = super.route(mapping, form, request, response);
        return forward;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#approve(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward approve(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        cacheAndLoad(mapping, form, request, response);
        return super.approve(mapping, form, request, response); // when in Rome...
    }

    @Override
    public ActionForward disapprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        cacheAndLoad(mapping, form, request, response);
        return super.disapprove(mapping, form, request, response);
    }

    @Override
    public ActionForward insertAdHocRoutePerson(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        cacheAndLoad(mapping, form, request, response);
        return super.insertAdHocRoutePerson(mapping, form, request, response);
    }

    @Override
    public ActionForward insertAdHocRouteWorkgroup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        cacheAndLoad(mapping, form, request, response);
        return super.insertAdHocRouteWorkgroup(mapping, form, request, response);
    }

    @Override
    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        cacheAndLoad(mapping, form, request, response);
        return super.delete(mapping, form, request, response);
    }

    @Override
    public ActionForward addOrg(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        cacheAndLoad(mapping, form, request, response);
        return super.addOrg(mapping, form, request, response);
    }

    @Override
    public ActionForward deleteOrg(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        cacheAndLoad(mapping, form, request, response);
        return super.deleteOrg(mapping, form, request, response);
    }

    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.refresh(mapping, form, request, response);
        // Have to reload when coming back from the lookup, since we need the whole doc to run audit
        cacheAndLoad(mapping, form, request, response);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    private void cacheAndLoad(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm) form;

        List adhocPersons = routingForm.getRoutingFormDocument().getAdhocPersons();
        List adhocOrgs = routingForm.getRoutingFormDocument().getAdhocOrgs();

        this.load(mapping, routingForm, request, response);

        routingForm.getRoutingFormDocument().setAdhocPersons(adhocPersons);
        routingForm.getRoutingFormDocument().setAdhocOrgs(adhocOrgs);
    }
}

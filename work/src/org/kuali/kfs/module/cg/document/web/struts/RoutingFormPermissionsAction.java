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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.module.kra.routingform.web.struts.form.RoutingForm;

/**
 * This class handles Actions for the Routing Form Permissions page.
 * 
 * 
 */
public class RoutingFormPermissionsAction extends RoutingFormAction {
    
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        RoutingForm routingForm = (RoutingForm) form;

        List adhocPersons = routingForm.getRoutingFormDocument().getAdhocPersons();
        List adhocOrgs = routingForm.getRoutingFormDocument().getAdhocOrgs();
        List adhocWorkgroups = routingForm.getRoutingFormDocument().getAdhocWorkgroups();
        
        this.load(mapping, routingForm, request, response);

        routingForm.getRoutingFormDocument().setAdhocPersons(adhocPersons);
        routingForm.getRoutingFormDocument().setAdhocOrgs(adhocOrgs);
        routingForm.getRoutingFormDocument().setAdhocWorkgroups(adhocWorkgroups);
        
        ActionForward forward = super.save(mapping, routingForm, request, response);
        
        return forward;
    }
}
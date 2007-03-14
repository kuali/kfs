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
import org.kuali.Constants;
import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.kra.KraKeyConstants;
import org.kuali.module.kra.budget.web.struts.form.BudgetForm;
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
    
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        RoutingForm routingForm = (RoutingForm) form;

        List adHocPermissions = routingForm.getRoutingFormDocument().getAdHocPermissions();
        List adHocOrgs = routingForm.getRoutingFormDocument().getAdHocOrgs();
        List adHocWorkgroups = routingForm.getRoutingFormDocument().getAdHocWorkgroups();
        
        this.load(mapping, routingForm, request, response);

        routingForm.getRoutingFormDocument().setAdHocPermissions(adHocPermissions);
        routingForm.getRoutingFormDocument().setAdHocOrgs(adHocOrgs);
        routingForm.getRoutingFormDocument().setAdHocWorkgroups(adHocWorkgroups);
        
        ActionForward forward = super.save(mapping, routingForm, request, response);
        
        return forward;
    }
}
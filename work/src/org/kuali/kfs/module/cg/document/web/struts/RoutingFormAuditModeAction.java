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
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.kra.routingform.rules.event.RunRoutingFormAuditEvent;
import org.kuali.module.kra.routingform.web.struts.form.RoutingForm;

public class RoutingFormAuditModeAction extends RoutingFormAction {
    
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.load(mapping, form, request, response);
        return super.execute(mapping, form, request, response);
    }
    
    /**
     * Activate audit checks.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @throws Exception
     */
    public ActionForward activate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm) form;
        routingForm.setAuditActivated(true);
        
        SpringServiceLocator.getKualiRuleService().applyRules(new RunRoutingFormAuditEvent(routingForm.getRoutingFormDocument()));

        return mapping.findForward((KFSConstants.MAPPING_BASIC));
    }
    
    /**
     * Activate audit checks.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @throws Exception
     */
    public ActionForward deactivate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //super.load(mapping, form, request, response);

        RoutingForm routingForm = (RoutingForm) form;
        routingForm.setAuditActivated(false);

        return mapping.findForward((KFSConstants.MAPPING_BASIC));
    }
}

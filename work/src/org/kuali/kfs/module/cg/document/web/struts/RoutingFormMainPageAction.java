/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/document/web/struts/RoutingFormMainPageAction.java,v $
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
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.util.GlobalVariables;
import org.kuali.module.kra.routingform.bo.RoutingFormKeyword;
import org.kuali.module.kra.routingform.document.RoutingFormDocument;
import org.kuali.module.kra.routingform.web.struts.form.RoutingForm;

public class RoutingFormMainPageAction extends RoutingFormAction {
    
    public ActionForward insertRoutingFormKeyword(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        RoutingForm routingForm = (RoutingForm) form;
        RoutingFormKeyword rfKeyword = routingForm.getNewRoutingFormKeyword();

        if(!rfKeyword.isEmpty()) {
            routingForm.getRoutingFormDocument().addRoutingFormKeyword(rfKeyword);
            
            // use getters and setters on the form to reinitialize the properties on the form.                
            routingForm.setNewRoutingFormKeyword(new RoutingFormKeyword());
        } else {
            // Throw error
            GlobalVariables.getErrorMap().addToErrorPath(PropertyConstants.NEW_ROUTING_FORM_KEYWORD);
            GlobalVariables.getErrorMap().putError(PropertyConstants.ROUTING_FORM_SUBMISSION_DETAILS, KeyConstants.ERROR_CUSTOM, "Please enter a valid keyword.");
            GlobalVariables.getErrorMap().removeFromErrorPath(PropertyConstants.NEW_ROUTING_FORM_KEYWORD);
        }
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    public ActionForward deleteRoutingFormKeyword(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        RoutingForm routingForm = (RoutingForm) form;

        // Remove the item from the list.
        int lineToDelete = super.getLineToDelete(request);
        routingForm.getRoutingFormDocument().getRoutingFormKeywords().remove(lineToDelete);
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

}

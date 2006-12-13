/*
 * Copyright 2006 The Kuali Foundation.
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
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.kra.routingform.bo.RoutingFormKeyword;
import org.kuali.module.kra.routingform.bo.RoutingFormPersonal;
import org.kuali.module.kra.routingform.document.RoutingFormDocument;
import org.kuali.module.kra.routingform.web.struts.form.RoutingForm;

public class RoutingFormMainPageAction extends RoutingFormAction {
    
    public ActionForward addPersonLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        RoutingForm routingForm = (RoutingForm) form;

        routingForm.getRoutingFormDocument().addPerson(routingForm.getNewRoutingFormPersonal());
        routingForm.setNewRoutingFormPersonal(new RoutingFormPersonal());

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    public ActionForward deletePersonLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        RoutingForm routingForm = (RoutingForm) form;
        
        int lineToDelete = getLineToDelete(request);
        routingForm.getRoutingFormDocument().getRoutingFormPersonnel().remove(getLineToDelete(request));
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
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
            GlobalVariables.getErrorMap().putError(PropertyConstants.NEW_ROUTING_FORM_KEYWORD, KeyConstants.ERROR_ROUTING_FORM_MISSING_KEYWORD);
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

    
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm)form;
        
        List referenceObjects = new ArrayList();

        referenceObjects.add("routingFormSubcontractors");
        referenceObjects.add("routingFormOtherCostShares");
        referenceObjects.add("routingFormInstitutionCostShares");

        SpringServiceLocator.getPersistenceService().retrieveReferenceObjects(routingForm.getRoutingFormDocument(), referenceObjects);
        
        return super.save(mapping, form, request, response);
    }
}

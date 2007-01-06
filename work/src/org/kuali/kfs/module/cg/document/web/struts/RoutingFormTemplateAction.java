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
import org.kuali.module.kra.routingform.document.RoutingFormDocument;
import org.kuali.module.kra.routingform.web.struts.form.RoutingForm;

/**
 * This class handles Actions for the Budget Template page.
 * 
 * 
 */
public class RoutingFormTemplateAction extends RoutingFormAction {
    
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RoutingFormTemplateAction.class);

    /**
     * Template the current document and forward to new document parameters page.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @throws Exception
     */
    public ActionForward doTemplate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        // Make sure BudgetForm is fully populated
        super.load(mapping, form, request, response);

        RoutingForm routingForm = (RoutingForm) form;
        RoutingFormDocument routingFormDoc = routingForm.getRoutingFormDocument();

        // Check if delivery address to be copied over
        if (!routingForm.isTemplateAddress()) {
            routingFormDoc.getRoutingFormAgency().setAgencyAddressDescription("");
        }
        
//      Check if ad-hoc permissions to be copied over
        if (!routingForm.isTemplateAdHocPermissions()) {
            // Clear permissions
        }
        
//      Check if budget fringe rates to be copied over
        if (!routingForm.isTemplateAdHocApprovers()) {
            // Clear approvers
        }
        
        RoutingFormDocument copyDoc = (RoutingFormDocument) routingFormDoc.copy();
        
        routingForm.setDocument(copyDoc);
        routingForm.setDocId(copyDoc.getDocumentNumber());

        super.save(mapping, form, request, response);
        super.load(mapping, form, request, response);

        return super.mainpage(mapping, routingForm, request, response);
    }
    
    public ActionForward navigate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.load(mapping, form, request, response);
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
}

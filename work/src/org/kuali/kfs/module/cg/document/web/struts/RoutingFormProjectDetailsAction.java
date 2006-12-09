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

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.kra.routingform.document.RoutingFormDocument;
import org.kuali.module.kra.routingform.bo.RoutingFormInstitutionCostShare;
import org.kuali.module.kra.routingform.bo.RoutingFormOtherCostShare;
import org.kuali.module.kra.routingform.bo.RoutingFormSubcontractor;
import org.kuali.module.kra.routingform.web.struts.form.RoutingForm;

public class RoutingFormProjectDetailsAction extends RoutingFormAction {

    /**
     * Adds handling for amount updates.
     * 
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm) form;

        if (routingForm.hasDocumentId()) {
            RoutingFormDocument routingDoc = routingForm.getRoutingFormDocument();

            routingDoc.setTotalInstitutionCostShareAmount(calculateRoutingFormInstitutionCostShareTotal(routingDoc)); // recalc b/c changes to the amounts could have happened
            routingDoc.setTotalOtherCostShareAmount(calculateRoutingFormOtherCostShareTotal(routingDoc)); // recalc b/c changes to the amounts could have happened
            routingDoc.setTotalSubcontractorAmount(calculateRoutingFormSubcontractorTotal(routingDoc)); // recalc b/c changes to the amounts could have happened
        }

        // proceed as usual
        return super.execute(mapping, form, request, response);
    }

    public ActionForward insertRoutingFormInstitutionCostShare(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        RoutingForm routingForm = (RoutingForm) form;
        RoutingFormDocument routingFormDocument = routingForm.getRoutingFormDocument();
        RoutingFormInstitutionCostShare routingFormInstitutionCostShare = routingForm.getNewRoutingFormInstitutionCostShare();
        
        routingFormDocument.prepareNewRoutingFormInstitutionCostShare(routingFormInstitutionCostShare);
        routingFormDocument.addRoutingFormInstitutionCostShare(routingFormInstitutionCostShare);
        
        // use getters and setters on the form to reinitialize the properties on the form.                
        routingForm.setNewRoutingFormInstitutionCostShare(new RoutingFormInstitutionCostShare());
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    public ActionForward deleteRoutingFormInstitutionCostShare(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        RoutingForm routingForm = (RoutingForm) form;

        // Remove the item from the list.
        int lineToDelete = super.getLineToDelete(request);
        routingForm.getRoutingFormDocument().removeRoutingFormInstitutionCostShare(lineToDelete);        
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    public ActionForward insertRoutingFormOtherCostShare(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        RoutingForm routingForm = (RoutingForm) form;
        RoutingFormDocument routingFormDocument = routingForm.getRoutingFormDocument();
        RoutingFormOtherCostShare routingFormOtherCostShare = routingForm.getNewRoutingFormOtherCostShare();
        
        routingFormDocument.prepareNewRoutingFormOtherCostShare(routingFormOtherCostShare);
        routingFormDocument.addRoutingFormOtherCostShare(routingFormOtherCostShare);
        
        // use getters and setters on the form to reinitialize the properties on the form.                
        routingForm.setNewRoutingFormOtherCostShare(new RoutingFormOtherCostShare());
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    public ActionForward deleteRoutingFormOtherCostShare(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        RoutingForm routingForm = (RoutingForm) form;

        // Remove the item from the list.
        int lineToDelete = super.getLineToDelete(request);
        routingForm.getRoutingFormDocument().removeRoutingFormOtherCostShare(lineToDelete);        
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    public ActionForward insertRoutingFormSubcontractor(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        RoutingForm routingForm = (RoutingForm) form;
        RoutingFormDocument routingFormDocument = routingForm.getRoutingFormDocument();
        RoutingFormSubcontractor routingFormSubcontractor = routingForm.getNewRoutingFormSubcontractor();
        
        routingFormDocument.prepareNewRoutingFormSubcontractor(routingFormSubcontractor);
        routingFormDocument.addRoutingFormSubcontractor(routingFormSubcontractor);
        
        // use getters and setters on the form to reinitialize the properties on the form.                
        routingForm.setNewRoutingFormSubcontractor(new RoutingFormSubcontractor());
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    public ActionForward deleteRoutingFormSubcontractor(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        RoutingForm routingForm = (RoutingForm) form;

        // Remove the item from the list.
        int lineToDelete = super.getLineToDelete(request);
        routingForm.getRoutingFormDocument().removeRoutingFormSubcontractor(lineToDelete);        
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * Recalculates the routing form institution cost share total since user could have changed it during their update.
     * 
     * @param routingFormDocument
     */
    private KualiDecimal calculateRoutingFormInstitutionCostShareTotal(RoutingFormDocument routingFormDocument) {
        KualiDecimal total = KualiDecimal.ZERO;
        Iterator<RoutingFormInstitutionCostShare> institutionCostShares = routingFormDocument.getRoutingFormInstitutionCostShares().iterator();
        while (institutionCostShares.hasNext()) {
            RoutingFormInstitutionCostShare institutionCostShare = institutionCostShares.next();
            total = total.add(new KualiDecimal(institutionCostShare.getRoutingFormCostShareAmount()));
        }
        return total;
    }

    /**
     * Recalculates the routing form other cost share total since user could have changed it during their update.
     * 
     * @param routingFormDocument
     */
    private KualiDecimal calculateRoutingFormOtherCostShareTotal(RoutingFormDocument routingFormDocument) {
        KualiDecimal total = KualiDecimal.ZERO;
        Iterator<RoutingFormOtherCostShare> otherCostShares = routingFormDocument.getRoutingFormOtherCostShares().iterator();
        while (otherCostShares.hasNext()) {
            RoutingFormOtherCostShare otherCostShare = otherCostShares.next();
            total = total.add(new KualiDecimal(otherCostShare.getRoutingFormCostShareAmount()));
        }
        return total;
    }

    /**
     * Recalculates the routing form subcontractor total since user could have changed it during their update.
     * 
     * @param routingFormDocument
     */
    private KualiDecimal calculateRoutingFormSubcontractorTotal(RoutingFormDocument routingFormDocument) {
        KualiDecimal total = KualiDecimal.ZERO;
        Iterator<RoutingFormSubcontractor> subcontractors = routingFormDocument.getRoutingFormSubcontractors().iterator();
        while (subcontractors.hasNext()) {
            RoutingFormSubcontractor subcontractor = subcontractors.next();
            total = total.add(new KualiDecimal(subcontractor.getRoutingFormSubcontractorAmount()));
        }
        return total;
    }

}

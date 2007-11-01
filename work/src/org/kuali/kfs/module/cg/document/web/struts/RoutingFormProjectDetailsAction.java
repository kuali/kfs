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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.kra.routingform.bo.RoutingFormInstitutionCostShare;
import org.kuali.module.kra.routingform.bo.RoutingFormOrganization;
import org.kuali.module.kra.routingform.bo.RoutingFormOtherCostShare;
import org.kuali.module.kra.routingform.bo.RoutingFormQuestion;
import org.kuali.module.kra.routingform.bo.RoutingFormSubcontractor;
import org.kuali.module.kra.routingform.document.RoutingFormDocument;
import org.kuali.module.kra.routingform.web.struts.form.RoutingForm;

public class RoutingFormProjectDetailsAction extends RoutingFormAction {


    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm) form;

        RoutingFormDocument routingFormDocument = routingForm.getRoutingFormDocument();
        List<RoutingFormSubcontractor> routingFormSubcontractors = new ArrayList(routingFormDocument.getRoutingFormSubcontractors());
        List<RoutingFormInstitutionCostShare> routingFormInstitutionCostShareList = new ArrayList(routingFormDocument.getRoutingFormInstitutionCostShares());
        List<RoutingFormOtherCostShare> routingFormOtherCostShareList = new ArrayList(routingFormDocument.getRoutingFormOtherCostShares());
        List<RoutingFormQuestion> routingFormQuestions = new ArrayList(routingFormDocument.getRoutingFormQuestions());
        List<RoutingFormOrganization> routingFormOrganizations = new ArrayList(routingFormDocument.getRoutingFormOrganizations());

        Integer subcontractorNextSequenceNumber = routingFormDocument.getSubcontractorNextSequenceNumber();
        Integer institutionCostShareNextSequenceNumber = routingFormDocument.getInstitutionCostShareNextSequenceNumber();
        Integer otherCostShareNextSequenceNumber = routingFormDocument.getOtherCostShareNextSequenceNumber();

        super.load(mapping, form, request, response);

        routingForm.getRoutingFormDocument().setRoutingFormSubcontractors(routingFormSubcontractors);
        routingForm.getRoutingFormDocument().setRoutingFormInstitutionCostShares(routingFormInstitutionCostShareList);
        routingForm.getRoutingFormDocument().setRoutingFormOtherCostShares(routingFormOtherCostShareList);
        routingForm.getRoutingFormDocument().setRoutingFormQuestions(routingFormQuestions);

        routingForm.getRoutingFormDocument().setSubcontractorNextSequenceNumber(subcontractorNextSequenceNumber);
        routingForm.getRoutingFormDocument().setInstitutionCostShareNextSequenceNumber(institutionCostShareNextSequenceNumber);
        routingForm.getRoutingFormDocument().setOtherCostShareNextSequenceNumber(otherCostShareNextSequenceNumber);
        routingForm.getRoutingFormDocument().setRoutingFormOrganizations(routingFormOrganizations);

        return super.save(mapping, form, request, response);
    }

    public ActionForward insertRoutingFormInstitutionCostShare(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        RoutingForm routingForm = (RoutingForm) form;
        RoutingFormDocument routingFormDocument = routingForm.getRoutingFormDocument();
        RoutingFormInstitutionCostShare routingFormInstitutionCostShare = routingForm.getNewRoutingFormInstitutionCostShare();

        routingFormDocument.addRoutingFormInstitutionCostShare(routingFormInstitutionCostShare, false);

        routingForm.setNewRoutingFormInstitutionCostShare(new RoutingFormInstitutionCostShare());
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward deleteRoutingFormInstitutionCostShare(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        RoutingForm routingForm = (RoutingForm) form;

        // Remove the item from the list.
        int lineToDelete = super.getLineToDelete(request);
        routingForm.getRoutingFormDocument().getRoutingFormInstitutionCostShares().remove(lineToDelete);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward insertRoutingFormOtherCostShare(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        RoutingForm routingForm = (RoutingForm) form;
        RoutingFormDocument routingFormDocument = routingForm.getRoutingFormDocument();
        RoutingFormOtherCostShare routingFormOtherCostShare = routingForm.getNewRoutingFormOtherCostShare();

        routingFormDocument.addRoutingFormOtherCostShare(routingFormOtherCostShare);

        routingForm.setNewRoutingFormOtherCostShare(new RoutingFormOtherCostShare());
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward deleteRoutingFormOtherCostShare(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        RoutingForm routingForm = (RoutingForm) form;

        // Remove the item from the list.
        int lineToDelete = super.getLineToDelete(request);
        routingForm.getRoutingFormDocument().getRoutingFormOtherCostShares().remove(lineToDelete);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward insertRoutingFormSubcontractor(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        RoutingForm routingForm = (RoutingForm) form;
        RoutingFormDocument routingFormDocument = routingForm.getRoutingFormDocument();
        RoutingFormSubcontractor routingFormSubcontractor = routingForm.getNewRoutingFormSubcontractor();

        routingFormDocument.addRoutingFormSubcontractor(routingFormSubcontractor);

        routingForm.setNewRoutingFormSubcontractor(new RoutingFormSubcontractor());
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward deleteRoutingFormSubcontractor(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        RoutingForm routingForm = (RoutingForm) form;

        // Remove the item from the list.
        int lineToDelete = super.getLineToDelete(request);
        routingForm.getRoutingFormDocument().getRoutingFormSubcontractors().remove(lineToDelete);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward deleteRoutingFormOrganization(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        RoutingForm routingForm = (RoutingForm) form;

        // Remove the item from the list.
        int lineToDelete = super.getLineToDelete(request);
        routingForm.getRoutingFormDocument().getRoutingFormOrganizations().remove(lineToDelete);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward insertRoutingFormOrganization(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        RoutingForm routingForm = (RoutingForm) form;
        RoutingFormDocument routingFormDocument = routingForm.getRoutingFormDocument();
        RoutingFormOrganization routingFormOrganization = routingForm.getNewRoutingFormOrganization();

        routingFormDocument.addRoutingFormOrganization(routingFormOrganization);

        routingForm.setNewRoutingFormOrganization(new RoutingFormOrganization());
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

}
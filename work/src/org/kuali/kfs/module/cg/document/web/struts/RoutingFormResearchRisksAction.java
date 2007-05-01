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
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.kra.routingform.bo.RoutingFormResearchRisk;
import org.kuali.module.kra.routingform.web.struts.form.RoutingForm;

/**
 * This class handles Actions for the Research Risks page.
 * 
 * 
 */
public class RoutingFormResearchRisksAction extends RoutingFormAction {
    
    /**
     * Add a research risk study to the list.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward insertRoutingFormResearchRiskStudy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm) form;
        routingForm.getRoutingFormDocument().getRoutingFormResearchRisks().get(getSelectedLine(request)).addNewResearchRiskStudyToList();
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Delete a study from the list.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteRoutingFormResearchRiskStudy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        RoutingForm routingForm = (RoutingForm) form;

        // Remove the item from the list.
        int tabToDelete = super.getTabToToggle(request);
        int lineToDelete = super.getLineToDelete(request);
        routingForm.getRoutingFormDocument().getRoutingFormResearchRisks().get(tabToDelete).getResearchRiskStudies().remove(lineToDelete);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm) form;
        List<RoutingFormResearchRisk> researchRisks = new ArrayList(routingForm.getRoutingFormDocument().getRoutingFormResearchRisks());
        super.load(mapping, form, request, response);
        routingForm.getRoutingFormDocument().setRoutingFormResearchRisks(researchRisks);
        return super.save(mapping, form, request, response);
    }
}

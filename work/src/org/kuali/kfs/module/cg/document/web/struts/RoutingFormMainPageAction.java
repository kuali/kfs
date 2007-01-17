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
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.service.ChartUserService;
import org.kuali.module.kra.KraKeyConstants;
import org.kuali.module.kra.routingform.bo.RoutingFormKeyword;
import org.kuali.module.kra.routingform.bo.RoutingFormOrganizationCreditPercent;
import org.kuali.module.kra.routingform.bo.RoutingFormPersonnel;
import org.kuali.module.kra.routingform.document.RoutingFormDocument;
import org.kuali.module.kra.routingform.web.struts.form.RoutingForm;

public class RoutingFormMainPageAction extends RoutingFormAction {
    
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RoutingFormMainPageAction.class);
    
    public ActionForward addPersonLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        RoutingForm routingForm = (RoutingForm) form;

        routingForm.getRoutingFormDocument().addPerson(routingForm.getNewRoutingFormPersonnel());
        routingForm.setNewRoutingFormPersonnel(new RoutingFormPersonnel());

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    public ActionForward deletePersonLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        RoutingForm routingForm = (RoutingForm) form;
        
        int lineToDelete = getLineToDelete(request);
        routingForm.getRoutingFormDocument().getRoutingFormPersonnel().remove(getLineToDelete(request));
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    public ActionForward addOrganizationCreditPercentLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        RoutingForm routingForm = (RoutingForm) form;

        routingForm.getRoutingFormDocument().addOrganizationCreditPercent(routingForm.getNewRoutingFormOrganizationCreditPercent());
        routingForm.setNewRoutingFormOrganizationCreditPercent(new RoutingFormOrganizationCreditPercent());

        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    public ActionForward deleteOrganizationCreditPercentLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        RoutingForm routingForm = (RoutingForm) form;
        
        int lineToDelete = getLineToDelete(request);
        routingForm.getRoutingFormDocument().getRoutingFormOrganizationCreditPercents().remove(getLineToDelete(request));
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    /* TODO Keyword code isn't multi select yet. Following method probably has to change. */
    public ActionForward insertRoutingFormKeyword(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        RoutingForm routingForm = (RoutingForm) form;
        RoutingFormKeyword rfKeyword = routingForm.getNewRoutingFormKeyword();

        if(!rfKeyword.isEmpty()) {
            routingForm.getRoutingFormDocument().addRoutingFormKeyword(rfKeyword);
            
            // use getters and setters on the form to reinitialize the properties on the form.                
            routingForm.setNewRoutingFormKeyword(new RoutingFormKeyword());
        } else {
            /* TODO Following should be in rules class? */
            // Display error to user.
            GlobalVariables.getErrorMap().addToErrorPath("newRoutingFormKeyword");
            GlobalVariables.getErrorMap().putError("newRoutingFormKeyword", KraKeyConstants.ERROR_KEYWORD_MISSING);
            GlobalVariables.getErrorMap().removeFromErrorPath("newRoutingFormKeyword");
        }
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /* TODO Keyword code isn't multi select yet. Following method probably has to change. */
    public ActionForward deleteRoutingFormKeyword(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        RoutingForm routingForm = (RoutingForm) form;

        // Remove the item from the list.
        int lineToDelete = super.getLineToDelete(request);
        routingForm.getRoutingFormDocument().getRoutingFormKeywords().remove(lineToDelete);
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm) form;
        
        List referenceObjects = new ArrayList();

        referenceObjects.add("routingFormSubcontractors");
        referenceObjects.add("routingFormOtherCostShares");
        referenceObjects.add("routingFormInstitutionCostShares");
        referenceObjects.add("routingFormResearchRisks");
        referenceObjects.add("routingFormOrganizations");
        referenceObjects.add("routingFormQuestions");

        SpringServiceLocator.getPersistenceService().retrieveReferenceObjects(routingForm.getRoutingFormDocument(), referenceObjects);
        
        return super.save(mapping, form, request, response);
    }

    /**
     * Refresh method on Main Page does several things for lookups:
     * <ul>
     * <li>If TBN is selected it clears the according key field for the appropriate lookup.</li>
     * <li>If an item is selected it clears the TBN field for the appropriate lookup.</li>
     * <li>For personnel lookups it sets the chart / org to the appropriate line (or new) field.</li>
     * </ul>
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#refresh(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.refresh(mapping, form, request, response);
        RoutingForm routingForm = (RoutingForm) form;
        RoutingFormDocument routingFormDocument = routingForm.getRoutingFormDocument();

        if (request.getParameter(Constants.REFRESH_CALLER) != null) {
            String refreshCaller = request.getParameter(Constants.REFRESH_CALLER);
            // check to see if we are coming back from a lookup
            if (refreshCaller.equals(Constants.KUALI_LOOKUPABLE_IMPL)) {
                if (request.getParameter("document.routingFormAgency.agencyNumber") != null) {
                    // coming back from an Agency lookup - Agency selected
                    routingFormDocument.setRoutingFormAgencyToBeNamedIndicator(false);
                }
                else if ("true".equals(request.getParameter("document.routingFormAgencyToBeNamedIndicator"))) {
                    // coming back from Agency lookup - To Be Named selected
                    routingFormDocument.getRoutingFormAgency().setRoutingFormAgency(null);
                    routingFormDocument.getRoutingFormAgency().refresh();
                }
                else if (request.getParameter("document.agencyFederalPassThroughNumber") != null) {
                    // coming back from Agency Federal Pass Through lookup - Agency selected
                    routingFormDocument.setAgencyFederalPassThroughNotAvailableIndicator(false);
                }
                else if ("true".equals(request.getParameter("document.agencyFederalPassThroughNotAvailableIndicator"))) {
                    // coming back from Agency Federal Pass Through lookup - Name Later selected
                    routingFormDocument.setAgencyFederalPassThroughNumber(null);
                    routingFormDocument.refreshReferenceObject("federalPassThroughAgency");
                }
                else if (request.getParameter("newRoutingFormPersonnel.personSystemIdentifier") != null) {
                    RoutingFormPersonnel newRoutingFormPersonnel = routingForm.getNewRoutingFormPersonnel();

                    // coming back from new Person lookup - person selected. Unset TBN indicated and set chart / org.
                    setupPersonChartOrg(newRoutingFormPersonnel);
                    newRoutingFormPersonnel.setPersonToBeNamedIndicator(false);
                }
                else if ("true".equals(request.getParameter("newRoutingFormPersonnel.personToBeNamedIndicator"))) {
                    // coming back from new Person lookup - Name Later selected
                    routingForm.getNewRoutingFormPersonnel().setPersonSystemIdentifier(null);
                    routingForm.getNewRoutingFormPersonnel().refresh();
                } else {
                    // Must be related to personnel lookup, first find which item this relates to.
                    int personIndex = determinePersonnelIndex(request);
                    
                    // Next do the regular clearing of appropriate fields. If the above enumeration didn't find an item
                    // we print a warn message at the end of this if block.
                    if (request.getParameter("document.routingFormPerson[" + personIndex + "].personSystemIdentifier") != null) {
                        RoutingFormPersonnel routingFormPersonnel = routingForm.getNewRoutingFormPersonnel();
                        
                        // coming back from Person lookup - Person selected. Unset TBN indicated and set chart / org.
                        setupPersonChartOrg(routingFormPersonnel);
                        routingFormDocument.getRoutingFormPerson(personIndex).setPersonToBeNamedIndicator(false);
                    }
                    else if ("true".equals(request.getParameter("document.routingFormPerson[" + personIndex + "].personToBeNamedIndicator"))) {
                        // coming back from Person lookup - To Be Named selected
                        routingFormDocument.getRoutingFormPerson(personIndex).setPersonSystemIdentifier(null);
                        routingFormDocument.getRoutingFormPerson(personIndex).refresh();
                    } else {
                        LOG.warn("Personnel lookup TBN reset code wasn't able to find person: personIndexStr=" + personIndex);
                    }
                }
            }
        }
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * Sets the chart / org of a RoutingFormPersonnel person based upon the contained UniversalUserService's ChartUserService.
     * @param routingFormPersonnel
     */
    private void setupPersonChartOrg(RoutingFormPersonnel routingFormPersonnel) {
        // retrieve services and refresh UniversalUser objects (it's empty after returning from a kul:lookup)
        UniversalUserService universalUserService = SpringServiceLocator.getUniversalUserService();
        ChartUserService chartUserService = SpringServiceLocator.getChartUserService();
        UniversalUser user =  universalUserService.updateUniversalUserIfNecessary(routingFormPersonnel.getPersonSystemIdentifier(), routingFormPersonnel.getUser());
        
        // set chart / org for new person
        Map<String,String> chartMap = chartUserService.getDefaultOrgPair((ChartUser) user.getModuleUser("chart"));
        routingFormPersonnel.setChartOfAccountsCode(chartMap.get(PropertyConstants.CHART_OF_ACCOUNTS_CODE));
        routingFormPersonnel.setOrganizationCode(chartMap.get(PropertyConstants.ORGANIZATION_CODE));
    }

    /**
     * Checks what index document.routingFormPerson[?] refers to.
     * @param request
     * @return index of the personnel item referred to
     */
    private int determinePersonnelIndex(HttpServletRequest request) {
        int personIndex = -1;
        
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parametersName = (String) parameterNames.nextElement();
            String label = "document.routingFormPerson[";
            if (parametersName.startsWith(label)) {
                personIndex = Integer.parseInt(parametersName.substring(label.length(), parametersName.indexOf("]")));
                break;
            }
        }
        return personIndex;
    }
}
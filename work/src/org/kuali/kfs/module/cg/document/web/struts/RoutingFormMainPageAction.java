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
import java.util.Arrays;
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
import org.kuali.module.kra.routingform.bo.RoutingFormProjectType;
import org.kuali.module.kra.routingform.document.RoutingFormDocument;
import org.kuali.module.kra.routingform.rules.event.RunRoutingFormAuditEvent;
import org.kuali.module.kra.routingform.web.struts.form.RoutingForm;
import org.kuali.module.kra.web.struts.form.ResearchDocumentFormBase;

import edu.iu.uis.eden.clientapp.IDocHandler;

public class RoutingFormMainPageAction extends RoutingFormAction {
    
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RoutingFormMainPageAction.class);

    /**
     * Necessary to override so that RoutingFormMainPage has properly initialized data upon initiated documents.
     * @see org.kuali.module.kra.web.struts.action.ResearchDocumentActionBase#docHandler(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        SpringServiceLocator.getRoutingFormMainPageService().initializeRoutingFormMainPage((RoutingForm) form);
        
        return super.docHandler(mapping, form, request, response);
    }

    /**
     * When a person is added to the list.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward addPersonLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        RoutingForm routingForm = (RoutingForm) form;

        routingForm.getRoutingFormDocument().addPerson(routingForm.getNewRoutingFormPersonnel());
        routingForm.setNewRoutingFormPersonnel(new RoutingFormPersonnel());

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * When a person is deleted from the list.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward deletePersonLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        RoutingForm routingForm = (RoutingForm) form;
        
        int lineToDelete = getLineToDelete(request);
        routingForm.getRoutingFormDocument().getRoutingFormPersonnel().remove(getLineToDelete(request));
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    /**
     * When an org is added to the list.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward addOrganizationCreditPercentLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        RoutingForm routingForm = (RoutingForm) form;

        routingForm.getRoutingFormDocument().addOrganizationCreditPercent(routingForm.getNewRoutingFormOrganizationCreditPercent());
        routingForm.setNewRoutingFormOrganizationCreditPercent(new RoutingFormOrganizationCreditPercent());

        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    /**
     * When an org is deleted from the list.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
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

    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#route(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm) form;
        
        retrieveMainPageReferenceObjects(routingForm.getRoutingFormDocument());

        boolean auditErrorsPassed = SpringServiceLocator.getKualiRuleService().applyRules(new RunRoutingFormAuditEvent(routingForm.getRoutingFormDocument()));
        if (!auditErrorsPassed) {
            routingForm.setAuditActivated(true);
            return mapping.findForward(Constants.MAPPING_BASIC);
        }
        
        return super.route(mapping, form, request, response);
    }
    
    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#approve(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward approve(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm) form;
        
        retrieveMainPageReferenceObjects(routingForm.getRoutingFormDocument());

        return super.approve(mapping, form, request, response);
    }

    /**
     * @see org.kuali.module.kra.routingform.web.struts.action.RoutingFormAction#save(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm) form;
        RoutingFormDocument routingFormDocument = routingForm.getRoutingFormDocument();

        // Logic for SF424 question.
        ActionForward preRulesForward = preRulesCheck(mapping, form, request, response, "save");
        if (preRulesForward != null) {
            return preRulesForward;
        }
        
        retrieveMainPageReferenceObjects(routingFormDocument);
        handleProjectTypeCodesSelection(routingForm.getSelectedRoutingFormProjectTypes(), routingFormDocument.getDocumentNumber(), routingFormDocument.getRoutingFormProjectTypes());
        
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
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.refresh(mapping, form, request, response);
        RoutingForm routingForm = (RoutingForm) form;
        RoutingFormDocument routingFormDocument = routingForm.getRoutingFormDocument();

        String refreshCaller = request.getParameter(Constants.REFRESH_CALLER);
        // check to see if we are coming back from a lookup
        if (Constants.KUALI_LOOKUPABLE_IMPL.equals(refreshCaller)) {
            if (request.getParameter("document.routingFormAgency.agencyNumber") != null) {
                // coming back from an Agency lookup - Agency selected
                routingFormDocument.setRoutingFormAgencyToBeNamedIndicator(false);
            }
            else if ("true".equals(request.getParameter("document.routingFormAgencyToBeNamedIndicator"))) {
                // coming back from Agency lookup - To Be Named selected
                routingFormDocument.getRoutingFormAgency().getAgency().setFullName(null);
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
        } else if (Constants.KUALI_USER_LOOKUPABLE_IMPL.equals(refreshCaller)) {
            if (request.getParameter("newRoutingFormPersonnel.personSystemIdentifier") != null) {
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
                    RoutingFormPersonnel routingFormPersonnel = routingFormDocument.getRoutingFormPerson(personIndex);
                    
                    // coming back from Person lookup - Person selected. Unset TBN indicated and set chart / org.
                    setupPersonChartOrg(routingFormPersonnel);
                    routingFormPersonnel.setPersonToBeNamedIndicator(false);
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
    
    /**
     * Takes string array of selected project types and does two things:
     * <ul>
     * <li>Filters them correctly into RoutingFormDocument.getRoutingFormProjectType</li>
     * <li>Removes items from RoutingFormDocument.getRoutingFormProjectType that don't exist anymore. Unfortunatly HTML checkboxes don't tell us when they have been unchecked. Note that routingFormProjectTypes will need to be a DeletionAwareList in order for this to work.</li>
     * </ul>
     * This is done so that the documents' project types may still properly fill in hidden variables (version#) via JSP but at the
     * same time a user may multiselect/-deselect items via checkboxes. 
     * @param documentNumber
     * @param selectedRoutingFormProjectTypes
     * @param routingFormProjectTypes
     */
    private void handleProjectTypeCodesSelection(String[] selectedRoutingFormProjectTypes, String documentNumber, List<RoutingFormProjectType> routingFormProjectTypes) {
        for(int i = 0; i < selectedRoutingFormProjectTypes.length; i++) {
            if (!routingFormProjectTypes.contains(new RoutingFormProjectType(selectedRoutingFormProjectTypes[i]))) {
                RoutingFormProjectType routingFormProjectType = new RoutingFormProjectType();
                routingFormProjectType.setProjectTypeCode(selectedRoutingFormProjectTypes[i]);
                routingFormProjectType.setDocumentNumber(documentNumber);
                routingFormProjectTypes.add(routingFormProjectType);
            }
        }
        
        List selectedRoutingFormProjectTypesList = Arrays.asList(selectedRoutingFormProjectTypes);
        for(int i = routingFormProjectTypes.size()-1; i >= 0 ; i--) {
            RoutingFormProjectType routingFormProjectType = routingFormProjectTypes.get(i);
            if (!selectedRoutingFormProjectTypesList.contains(routingFormProjectType.getProjectTypeCode())) {
                routingFormProjectTypes.remove(i);
            }
        }
    }
    
    /**
     * Retrieves references objects for main page. Nothing special about this method, it's just consolidating code that's
     * called in multiple places.
     * @param routingForm
     */
    private void retrieveMainPageReferenceObjects(RoutingFormDocument routingFormDocument) {
        List referenceObjects = new ArrayList();

        referenceObjects.add("routingFormSubcontractors");
        referenceObjects.add("routingFormOtherCostShares");
        referenceObjects.add("routingFormInstitutionCostShares");
        referenceObjects.add("routingFormResearchRisks");
        referenceObjects.add("routingFormOrganizations");
        referenceObjects.add("routingFormQuestions");

        SpringServiceLocator.getPersistenceService().retrieveReferenceObjects(routingFormDocument, referenceObjects);
    }
}
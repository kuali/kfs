/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.web.struts;

import static org.kuali.kfs.module.tem.TemConstants.CERTIFICATION_STATEMENT_ATTRIBUTE;
import static org.kuali.kfs.module.tem.TemConstants.EMPLOYEE_TEST_ATTRIBUTE;
import static org.kuali.kfs.module.tem.TemConstants.KIM_PERSON_LOOKUPABLE;
import static org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;
import static org.kuali.kfs.module.tem.TemConstants.TEM_PROFILE_LOOKUPABLE;
import static org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationParameters.PARAM_DTL_TYPE;
import static org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationParameters.TRAVEL_ADVANCE_PAYMENT_ACCOUNT_NBR;
import static org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationParameters.TRAVEL_ADVANCE_PAYMENT_CHART_CODE;
import static org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationParameters.TRAVEL_ADVANCE_PAYMENT_OBJECT_CODE;
import static org.kuali.kfs.module.tem.TemPropertyConstants.NEW_EMERGENCY_CONTACT_LINE;
import static org.kuali.kfs.module.tem.TemPropertyConstants.NEW_TRAVEL_ADVANCE_LINE;
import static org.kuali.kfs.module.tem.TemPropertyConstants.TRVL_IDENTIFIER_PROPERTY;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_OBJECT_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.NEW_SOURCE_LINE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.purap.SingleConfirmationQuestion;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.businessobject.TravelerDetailEmergencyContact;
import org.kuali.kfs.module.tem.document.TravelAuthorizationAmendmentDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelDocumentBase;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.authorization.TravelAuthorizationAuthorizer;
import org.kuali.kfs.module.tem.document.validation.event.AddEmergencyContactLineEvent;
import org.kuali.kfs.module.tem.document.validation.event.AddTravelAdvanceLineEvent;
import org.kuali.kfs.module.tem.document.web.bean.TravelAuthorizationMvcWrapperBean;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.dao.DocumentDao;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.struts.form.BlankFormFile;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;

/**
 * Handles action events through the struts framework for the {@link TravelAuthorizationDocument}
 */
public class TravelAuthorizationAction extends TravelActionBase {
    
    public static Logger LOG = Logger.getLogger(TravelAuthorizationAction.class);
    
    public static final String DOCUMENT_ERROR_PREFIX = "document.";
    public static final String NEW_SOURCE_LINE_OBJECT_CODE = String.format("%s.%s", NEW_SOURCE_LINE, FINANCIAL_OBJECT_CODE);
    private DocumentDao documentDao;
    
    /**
     * @see org.kuali.kfs.module.tem.document.web.struts.TravelActionBase#docHandler(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ActionForward action = super.docHandler(mapping, form, request, response);
        final TravelAuthorizationForm authorization = (TravelAuthorizationForm) form;
        final TravelAuthorizationDocument document = authorization.getTravelAuthorizationDocument();
        
        final String identifierStr = request.getParameter(TRVL_IDENTIFIER_PROPERTY);

        //refresh and setup the custom primary destinations indicator
        if (document.getPrimaryDestinationId() != null && document.getPrimaryDestinationId().intValue() == TemConstants.CUSTOM_PRIMARY_DESTINATION_ID){
            document.getPrimaryDestination().setPrimaryDestinationName(document.getPrimaryDestinationName());
            document.getPrimaryDestination().setCounty(document.getPrimaryDestinationCounty());
            document.getPrimaryDestination().setCountryState(document.getPrimaryDestinationCountryState());
            document.setPrimaryDestinationIndicator(true);
        }
        return action;
    }
    
    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ActionForward retval = super.execute(mapping, form, request, response);
        TravelAuthorizationForm authForm = (TravelAuthorizationForm) form;
        TravelAuthorizationDocument travelAuthDocument = (TravelAuthorizationDocument) authForm.getDocument();

        if (travelAuthDocument.getTraveler() != null && travelAuthDocument.getTraveler().getPrincipalId() != null) {
            travelAuthDocument.getTraveler().setPrincipalName(getPersonService().getPerson(travelAuthDocument.getTraveler().getPrincipalId()).getPrincipalName());
        }
        
        setButtonPermissions(authForm);
        String perDiemPercentage = getParameterService().getParameterValue(PARAM_NAMESPACE, PARAM_DTL_TYPE, TravelAuthorizationParameters.FIRST_AND_LAST_DAY_PER_DIEM_PERCENTAGE);
        final String travelIdentifier = travelAuthDocument.getTravelDocumentIdentifier();
        
        authForm.setPerDiemPercentage(perDiemPercentage);

        if (authForm.getNewTravelAdvanceLine() == null) {
            String accountNumber = getParameterService().getParameterValue(PARAM_NAMESPACE, PARAM_DTL_TYPE, TRAVEL_ADVANCE_PAYMENT_ACCOUNT_NBR);
            String objectCode = getParameterService().getParameterValue(PARAM_NAMESPACE, PARAM_DTL_TYPE, TRAVEL_ADVANCE_PAYMENT_OBJECT_CODE);
            String chartCode = getParameterService().getParameterValue(PARAM_NAMESPACE, PARAM_DTL_TYPE, TRAVEL_ADVANCE_PAYMENT_CHART_CODE);
            TravelAdvance adv = new TravelAdvance();
            adv.setChartOfAccountsCode(chartCode);
            adv.setAccountNumber(accountNumber);
            adv.setFinancialObjectCode(objectCode);
            authForm.setNewTravelAdvanceLine(adv);
        }
        // try pulling the transpo modes from the form or the request
        String[] transpoModes = request.getParameterValues("selectedTransportationModes");
        if (transpoModes != null) {
            authForm.setSelectedTransportationModes(Arrays.asList(transpoModes));
        }
        else {
            authForm.setSelectedTransportationModes(travelAuthDocument.getTransportationModeCodes());
        }
        refreshTransportationModesAfterButtonAction(travelAuthDocument, request, authForm);
        
        if(ObjectUtils.isNotNull(travelAuthDocument.getActualExpenses())){
            travelAuthDocument.enableExpenseTypeSpecificFields(travelAuthDocument.getActualExpenses());
        }

        LOG.debug("Got "+ authForm.getRelatedDocuments().size()+ " related documents");

        if (!isReturnFromObjectCodeLookup(authForm, request)) {
            getTravelEncumbranceService().updateEncumbranceObjectCode(travelAuthDocument, authForm.getNewSourceLine());
        }

        getMessages(authForm);

        // update the list of related documents
        refreshRelatedDocuments(authForm);
        
        if (((TravelFormBase) form).getMethodToCall().equalsIgnoreCase("docHandler") && travelAuthDocument.getPrimaryDestinationId() != null){
            if (travelAuthDocument.getPrimaryDestinationId().intValue() == TemConstants.CUSTOM_PRIMARY_DESTINATION_ID){
                travelAuthDocument.getPrimaryDestination().setPrimaryDestinationName(travelAuthDocument.getPrimaryDestinationName());
                travelAuthDocument.getPrimaryDestination().setCounty(travelAuthDocument.getPrimaryDestinationCounty());
                travelAuthDocument.getPrimaryDestination().setCountryState(travelAuthDocument.getPrimaryDestinationCountryState());
            }
        }
        
        request.setAttribute(CERTIFICATION_STATEMENT_ATTRIBUTE, getCertificationStatement(travelAuthDocument));
        request.setAttribute(EMPLOYEE_TEST_ATTRIBUTE, isEmployee(travelAuthDocument.getTraveler()));
        
        // force recalculate    
        if(!getCalculateIgnoreList().contains(authForm.getMethodToCall())){
            recalculateTripDetailTotalOnly(mapping, form, request, response);
        }        

        setupTravelAdvances(authForm, request);
        
        return retval;
    }

    private void setupTravelAdvances(TravelAuthorizationForm form, HttpServletRequest request) {
        TravelAuthorizationDocument document = (TravelAuthorizationDocument)form.getTravelAuthorizationDocument();
        boolean waitingOnTraveler = document.getAppDocStatus().equals(TemConstants.TravelAuthorizationStatusCodeKeys.AWAIT_TRVLR);
        String initiator = document.getDocumentHeader().getWorkflowDocument().getRouteHeader().getInitiatorPrincipalId();
        String travelerID = "";
        boolean showPolicy = false;
        if (document.getTraveler() != null){
            travelerID = document.getTraveler().getPrincipalId();
            if (travelerID != null){
                //traveler must accept policy, if initiator is arranger, the traveler will have to accept later.
                showPolicy = initiator.equals(travelerID) || GlobalVariables.getUserSession().getPrincipalId().equals(travelerID);
            }
            else{ //Non-kim traveler, arranger accepts policy
                showPolicy = true;
            }
        }

        form.setWaitingOnTraveler(waitingOnTraveler);
        form.setShowPolicy(showPolicy);
    }

    /**
     * Determines if the request is a return from an Object Code lookup. In this case we want special treatment. We know the return
     * from a lookup by checking the refreshCaller attribute. We know it's for an object code when an object code for a
     * newSourceLine is present in the request parameter.
     * 
     * @param form to get refresh caller from
     * @param request to get the object code parameter from
     * @return boolean true if return from an object code lookup or false otherwise
     */
    protected boolean isReturnFromObjectCodeLookup(final TravelAuthorizationForm form, final HttpServletRequest request) {
        return (form.getRefreshCaller() != null && request.getParameter(NEW_SOURCE_LINE_OBJECT_CODE) != null);
    }

    private void getMessages(TravelAuthorizationForm authForm) {
        TravelAuthorizationDocument document = authForm.getTravelAuthorizationDocument();

        if (document.getAppDocStatus().equals(TravelAuthorizationStatusCodeKeys.REIMB_HELD)) {
            String name = SpringContext.getBean(PersonService.class).getPerson(GlobalVariables.getUserSession().getPrincipalId()).getName();
            GlobalVariables.getMessageList().add(TemKeyConstants.TA_MESSAGE_HOLD_DOCUMENT_TEXT, new String[] { name });
        }
        else if (document.getAppDocStatus().equals(TravelAuthorizationStatusCodeKeys.RETIRED_VERSION)) {
            GlobalVariables.getMessageList().add(TemKeyConstants.TA_MESSAGE_RETIRED_DOCUMENT_TEXT);
        }
        else if (document.getAppDocStatus().equals(TravelAuthorizationStatusCodeKeys.PEND_AMENDMENT)) {
            GlobalVariables.getMessageList().add(TemKeyConstants.TA_MESSAGE_AMEND_DOCUMENT_TEXT);
        }
    }

    /**
     * This method sets all the boolean properties on the form to determine what buttons can be displayed depending on what is going
     * on
     */
    protected void setButtonPermissions(TravelAuthorizationForm authForm) {
        canSave(authForm);
        canCopy(authForm);
        setCanCalculate(authForm);
        setCanAmend(authForm);
        setCanCancel(authForm);
        setCanClose(authForm);
        setCanHold(authForm);
        setCanRemoveHold(authForm);
        setCanReturnToFisicalOfficer(authForm);
        hideButtons(authForm);
    }

    protected void hideButtons(TravelAuthorizationForm authForm) {
        boolean can = false;
        
        TravelAuthorizationAuthorizer documentAuthorizer = getDocumentAuthorizer(authForm);
        can = documentAuthorizer.hideButtons(authForm.getTravelAuthorizationDocument(), GlobalVariables.getUserSession().getPerson());
        if (can){
            authForm.getDocumentActions().remove(KNSConstants.KUALI_ACTION_CAN_SAVE);
            authForm.getDocumentActions().remove(KNSConstants.KUALI_ACTION_CAN_CLOSE);
            authForm.getDocumentActions().remove(KNSConstants.KUALI_ACTION_CAN_SEND_ADHOC_REQUESTS);
            authForm.getDocumentActions().remove(KNSConstants.KUALI_ACTION_CAN_COPY);
            authForm.getDocumentActions().remove(KNSConstants.KUALI_ACTION_CAN_RELOAD);
        }
    }
    
    /**
     * 
     * @param authForm
     */
    protected void canCopy(TravelAuthorizationForm authForm) {
        TravelAuthorizationAuthorizer documentAuthorizer = getDocumentAuthorizer(authForm);
        boolean can = documentAuthorizer.canCopy(authForm.getTravelAuthorizationDocument(), GlobalVariables.getUserSession().getPerson());
        if(can){
            authForm.getDocumentActions().put(KNSConstants.KUALI_ACTION_CAN_COPY, true);
        }else{
            authForm.getDocumentActions().remove(KNSConstants.KUALI_ACTION_CAN_COPY);
        }     
    }
    
    protected void canSave(TravelAuthorizationForm authForm) {
        boolean can = !(isFinal(authForm) || isProcessed(authForm));       
        if (can) {
            TravelAuthorizationAuthorizer documentAuthorizer = getDocumentAuthorizer(authForm);
            can = documentAuthorizer.canSave(authForm.getTravelAuthorizationDocument(), GlobalVariables.getUserSession().getPerson());
        }
        
        if (!can){
            boolean isTravelManager = getTravelDocumentService().isTravelManager(GlobalVariables.getUserSession().getPerson());
            boolean isDelinquent = authForm.getTravelAuthorizationDocument().getDelinquentAction() != null;
            
            if (isTravelManager && isDelinquent){
                can = true;
            }
        }
        
        if (can) {
            authForm.getDocumentActions().put(KNSConstants.KUALI_ACTION_CAN_SAVE,true);
        }
        else{
            authForm.getDocumentActions().remove(KNSConstants.KUALI_ACTION_CAN_SAVE);
        }
    }

    /**
     * Determines whether or not they can remove a hold on a TA
     * 
     * @param authForm
     */
    protected void setCanRemoveHold(TravelAuthorizationForm authForm) {
        boolean can = isHeld(authForm) && (isFinal(authForm) || isProcessed(authForm));

        if (can) {
            TravelAuthorizationAuthorizer documentAuthorizer = getDocumentAuthorizer(authForm);
            can = documentAuthorizer.canRemoveHold(authForm.getTravelAuthorizationDocument(), GlobalVariables.getUserSession().getPerson());
        }
        
        authForm.setCanRemoveHold(can);
    }

    /**
     * Determines whether or not they can hold a TA
     * 
     * @param authForm
     */
    protected void setCanHold(TravelAuthorizationForm authForm) {
        // first check to see if the document is open and final
        boolean can = isOpen(authForm) && (isFinal(authForm) || isProcessed(authForm));

        if (can) {
            TravelAuthorizationAuthorizer documentAuthorizer = getDocumentAuthorizer(authForm);
            can = documentAuthorizer.canHold(authForm.getTravelAuthorizationDocument(), GlobalVariables.getUserSession().getPerson());
        }
        
        authForm.setCanHold(can);
    }

    /**
     * Determines whether or not someone can amend a travel authorization
     * 
     * @param authForm
     */
    protected void setCanAmend(TravelAuthorizationForm authForm) {
        boolean can = isOpen(authForm) && (isFinal(authForm) || isProcessed(authForm));
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        java.util.Date today = dateTimeService.getCurrentDate();

        if (authForm.getTravelAuthorizationDocument().getTripBegin() != null) {
            can &= today.before(authForm.getTravelAuthorizationDocument().getTripBegin());
        }
        
        if (can && authForm.getRelatedDocuments() == null) {
            //If there are TR's, disabled amend
            List<Document> trRelatedDocumentList = getTravelDocumentService().getDocumentsRelatedTo(authForm.getTravelDocument(), TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT);
            can = trRelatedDocumentList.isEmpty();
        }
        
        if (can) {
            // Find if they have the permissions
            TravelAuthorizationAuthorizer documentAuthorizer = getDocumentAuthorizer(authForm);
            can = documentAuthorizer.canAmend(authForm.getTravelAuthorizationDocument(), GlobalVariables.getUserSession().getPerson());
        }

        authForm.setCanAmend(can);
    }

    /**
     * Determines whether or not someone can calculate a travel authorization
     * 
     * @param authForm
     */
    protected void setCanCalculate(TravelAuthorizationForm authForm) {
        boolean can = !(isFinal(authForm) || isProcessed(authForm));
        
        if (can) {
            TravelAuthorizationAuthorizer documentAuthorizer = getDocumentAuthorizer(authForm);
            can = documentAuthorizer.canCalculate(authForm.getTravelAuthorizationDocument(), GlobalVariables.getUserSession().getPerson());
        }
        
        authForm.setCanCalculate(can);
    }

    /**
     * This method determines if the user can or cannot close a TA based on permissions and state
     * 
     * @return true if they can close  a TA
     */
    protected void setCanClose(TravelAuthorizationForm authForm) {
        // first check to see if the document is open and final
        boolean can = isOpen(authForm) && (isFinal(authForm) || isProcessed(authForm));

        if (can) {
            // Find if they have the permissions
            TravelAuthorizationAuthorizer documentAuthorizer = getDocumentAuthorizer(authForm);
            can = documentAuthorizer.canClose(authForm.getTravelAuthorizationDocument(), GlobalVariables.getUserSession().getPerson());
        }
        authForm.setCanCloseTA(can);
    }

    /**
     * This method determines if the user can or cannot cancel a TA based on permissions and state
     * 
     * @return true if they can cancel a TA
     */
    protected void setCanCancel(TravelAuthorizationForm authForm) {
        // first check to see if the document is open and final
        boolean can = isOpen(authForm) && (isFinal(authForm) || isProcessed(authForm));

        // next, verify that there are no reimbursements out there for this doc
        if (can) {
            List<TravelReimbursementDocument> reimbursements = getTravelDocumentService().find(TravelReimbursementDocument.class, authForm.getTravelAuthorizationDocument().getTravelDocumentIdentifier());
            if (!reimbursements.isEmpty()) {
                can = false;
            }
        }

        if (can) {
            // Find if they have the permissions
            TravelAuthorizationAuthorizer documentAuthorizer = getDocumentAuthorizer(authForm);
            can = documentAuthorizer.canCancel(authForm.getTravelAuthorizationDocument(), GlobalVariables.getUserSession().getPerson());
        }
        authForm.setCanCancelTA(can);
    }

    /**
     * is this document in an open for reimbursement workflow state?
     * 
     * @param authForm
     * @return
     */
    protected boolean isOpen(TravelAuthorizationForm authForm) {
        return authForm.getTravelAuthorizationDocument().getAppDocStatus().equals(TemConstants.TravelAuthorizationStatusCodeKeys.OPEN_REIMB);
    }

    /**
     * is this document on hold for reimbursement workflow state?
     * 
     * @param authForm
     * @return
     */
    protected boolean isHeld(TravelAuthorizationForm authForm) {
        return authForm.getTravelAuthorizationDocument().getAppDocStatus().equals(TemConstants.TravelAuthorizationStatusCodeKeys.REIMB_HELD);
    }

    /**
     * Overriding this so that we can populate the transportation modes before a user leaves the page
     * 
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#performLookup(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward performLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TravelAuthorizationForm authForm = (TravelAuthorizationForm) form;
        List<String> selectedTransportationModes = authForm.getTempSelectedTransporationModes();
        if (selectedTransportationModes != null) {
            authForm.getTravelAuthorizationDocument().setTransportationModeCodes(selectedTransportationModes);
        }
        else {
            authForm.getTravelAuthorizationDocument().setTransportationModeCodes(new ArrayList<String>());
        }

        return super.performLookup(mapping, form, request, response);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TravelAuthorizationForm authForm = (TravelAuthorizationForm) form;
        authForm.setSelectedTransportationModes(authForm.getTravelAuthorizationDocument().getTransportationModeCodes());
        ActionForward actionAfterTravelerLookup = this.refreshAfterTravelerLookup(mapping, authForm, request);
        if (actionAfterTravelerLookup != null) {
            return actionAfterTravelerLookup;
        }

        ActionForward actionAfterGroupTravelerLookup = this.refreshAfterGroupTravelerLookup(mapping, authForm, request);
        if (actionAfterGroupTravelerLookup != null) {
            return actionAfterGroupTravelerLookup;
        }

        ActionForward actionAfterPrimaryDestinationLookup = this.refreshAfterPrimaryDestinationLookup(mapping, authForm, request);
        if (actionAfterPrimaryDestinationLookup != null) {
            return actionAfterPrimaryDestinationLookup;
        }

        return super.refresh(mapping, form, request, response);
    }

    /**
     * 
     * @param travelReqDoc
     * @param request
     * @param authForm
     */
    protected void refreshTransportationModesAfterButtonAction(TravelAuthorizationDocument travelReqDoc, HttpServletRequest request, TravelAuthorizationForm authForm) {
        List<String> selectedTransportationModes = authForm.getTempSelectedTransporationModes();
        if (selectedTransportationModes != null) {
            LOG.debug("selected transportation modes are: "+ selectedTransportationModes.toString());
            travelReqDoc.setTransportationModeCodes(selectedTransportationModes);
        }
        else {
            LOG.debug("setting selected transportation modes to empty list");
            travelReqDoc.setTransportationModeCodes(new ArrayList<String>());
        }
    }

    /**
     * This method is called during a refresh from lookup, it checks to see if it is being called for Group Traveler or the initial
     * Traveler lookup
     * 
     * @param mapping
     * @param authForm
     * @param request
     * @return null, no special page to return to
     */
    protected ActionForward refreshAfterTravelerLookup(ActionMapping mapping, TravelAuthorizationForm authForm, HttpServletRequest request) {
        String refreshCaller = authForm.getRefreshCaller();

        LOG.debug("refresh call is: "+ refreshCaller);
        String groupTravelerId = (String) request.getParameter("newGroupTravelerLine.groupTravelerEmpId");
        TravelDocumentBase document = authForm.getTravelAuthorizationDocument();

        boolean isTravelerLookupable = StringUtils.equals(refreshCaller, TEM_PROFILE_LOOKUPABLE);

        // if a cancel occurred on address lookup we need to reset the payee id and type, rest of fields will still have correct
        // information
        if (refreshCaller == null) {
            authForm.setTravelerId(authForm.getTempTravelerId());
            return null;
        }

        // do not execute the further refreshing logic if the refresh caller is not a traveler profile lookupable
        if (!isTravelerLookupable) {
            return null;
        }

        LOG.debug("Looking up customer with number "+ document.getTraveler().getCustomerNumber());
        document.getTraveler().refreshReferenceObject(TemPropertyConstants.CUSTOMER);
        document.getTraveler().refreshReferenceObject(TemPropertyConstants.TRAVELER_TYPE);
        LOG.debug("Got "+ document.getTraveler().getCustomer());

        if (document.getTraveler().getPrincipalId() != null) {
            final String principalName = getPersonService().getPerson(document.getTraveler().getPrincipalId()).getPrincipalName();
            document.getTraveler().setPrincipalName(principalName);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method is called during a refresh from lookup, it checks to see if it is being called for Group Traveler or the initial
     * Traveler lookup
     * 
     * @param mapping
     * @param authForm
     * @param request
     * @return null, no special page to return to
     */
    private ActionForward refreshAfterGroupTravelerLookup(ActionMapping mapping, TravelAuthorizationForm authForm, HttpServletRequest request) {
        String refreshCaller = authForm.getRefreshCaller();

        LOG.debug("refresh call is: "+ refreshCaller);
        String groupTravelerId = (String) request.getParameter("newGroupTravelerLine.groupTravelerEmpId");
        TravelDocumentBase document = authForm.getTravelAuthorizationDocument();

        boolean isGroupTravelerLookupable = StringUtils.equals(refreshCaller, KIM_PERSON_LOOKUPABLE);

        // do not execute the further refreshing logic if the refresh caller is not a traveler profile lookupable
        if (!isGroupTravelerLookupable) {
            return null;
        }

        // We are dealing with either a group traveler or regular traveler lookup return
        if (isGroupTravelerLookupable && StringUtils.isNotBlank(groupTravelerId)) {
            Map<String, String> fieldsForLookup = new HashMap<String, String>();
            fieldsForLookup.put("principalId", groupTravelerId);

            Person person = (Person) getPersonService().findPeople(fieldsForLookup).get(0);
            authForm.getNewGroupTravelerLine().setName(person.getNameUnmasked());
        }

        return null;
    }

    /*private ActionForward refreshAfterPerDiemLookup(ActionMapping mapping, TravelAuthorizationForm authForm, HttpServletRequest request) {
        String refreshCaller = authForm.getRefreshCaller();

        TravelAuthorizationDocument document = authForm.getTravelAuthorizationDocument();

        boolean isPerDiemLookupable = PER_DIEM_LOOKUPABLE.equals(refreshCaller);

        // if a cancel occurred on address lookup we need to reset the payee id and type, rest of fields will still have correct
        // information
        if (refreshCaller == null) {
            // authForm.setTravelerId(authForm.getTempTravelerId());

            return null;
        }

        // do not execute the further refreshing logic if the refresh caller is not a per diem lookupable
        if (!isPerDiemLookupable) {
            return null;
        }
        Map<String, String> parameters = request.getParameterMap();
        Set<String> parameterKeys = parameters.keySet();
        for (String parameterKey : parameterKeys) {
            if (StringUtils.containsIgnoreCase(parameterKey, TemPropertyConstants.PER_DIEM_EXP)) {
                // its one of the numbered lines, lets
                int estimateLineNum = getLineNumberFromParameter(parameterKey);
                PerDiemExpense estimate = document.getPerDiemExpenses().get(estimateLineNum);
                estimate.refreshReferenceObject("perDiem");
                PerDiem perDiem = estimate.getPerDiem();

                // now copy info over to estimate
                estimate.setPrimaryDestination(perDiem.getPrimaryDestination());
                estimate.setMealsAndIncidentals(perDiem.getMealsAndIncidentals());
                estimate.setCountryState(perDiem.getCountryState());
                estimate.setCounty(perDiem.getCounty());

                break;
            }
        }

        // do not execute the further refreshing logic if a profile is not selected
        Integer perDiemId = document.getInitialPerDiemId();
        if (perDiemId == null) {
            return null;
        }

        if (isPerDiemLookupable) {
            PerDiem perDiem = new PerDiem();
            perDiem.setId(perDiemId);

            perDiem = (PerDiem) SpringContext.getBean(BusinessObjectService.class).retrieve(perDiem);

            document.setInitialPerDiem(perDiem);
            document.setInitialPrimaryDestination(perDiem.getPrimaryDestination());
            document.setTripType(perDiem.getTripType());
            document.setTripTypeCode(perDiem.getTripTypeCode().toUpperCase());


            return null;
        }

        return null;
    }*/
    
    
    /**
     * This method removes an other travel expense from this collection
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return the page to forward back to
     * @throws Exception
     */
    @Override
    public ActionForward deleteActualExpenseLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ActionForward retval = super.deleteActualExpenseLine(mapping, form, request, response);        
        //recalculate(mapping, form, request, response);
        
        return retval;
    }

    /**
     * This method adds a new emergency contact into the travel request document
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward addEmergencyContactLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TravelAuthorizationForm travelauthForm = (TravelAuthorizationForm) form;
        TravelAuthorizationDocument travelReqDoc = (TravelAuthorizationDocument) travelauthForm.getDocument();

        TravelerDetailEmergencyContact newEmergencyContactLine = travelauthForm.getNewEmergencyContactLine();
        boolean rulePassed = true;

        // check any business rules
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AddEmergencyContactLineEvent(NEW_EMERGENCY_CONTACT_LINE, travelauthForm.getDocument(), newEmergencyContactLine));

        if (rulePassed) {
            travelReqDoc.addEmergencyContactLine(newEmergencyContactLine);
            travelauthForm.setNewEmergencyContactLine(new TravelerDetailEmergencyContact());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method removes an emergency contact from this collection
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteEmergencyContactLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TravelAuthorizationForm travelauthForm = (TravelAuthorizationForm) form;
        TravelAuthorizationDocument travelReqDoc = (TravelAuthorizationDocument) travelauthForm.getDocument();

        int deleteIndex = getLineToDelete(request);
        travelReqDoc.getTraveler().getEmergencyContacts().remove(deleteIndex);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method adds a new travel advance into the travel authorization document
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward addTravelAdvanceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TravelAuthorizationForm travelauthForm = (TravelAuthorizationForm) form;
        TravelAuthorizationDocument travelReqDoc = (TravelAuthorizationDocument) travelauthForm.getDocument();

        TravelAdvance newTravelAdvanceLine = travelauthForm.getNewTravelAdvanceLine();
        boolean rulePassed = true;
        // check any business rules
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AddTravelAdvanceLineEvent(NEW_TRAVEL_ADVANCE_LINE, travelauthForm.getDocument(), newTravelAdvanceLine));
        // travelReqDoc.logErrors();
        if (rulePassed) {

            // add travel advance line
            travelauthForm.getNewTravelAdvanceLine().refreshNonUpdateableReferences();
            travelReqDoc.addTravelAdvanceLine(newTravelAdvanceLine);
            newTravelAdvanceLine.refreshReferenceObject("advancePaymentReason");
            travelauthForm.setNewTravelAdvanceLine(new TravelAdvance());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method removes a travel advance from this collection
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteTravelAdvanceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TravelAuthorizationForm travelAuthForm = (TravelAuthorizationForm) form;
        TravelAuthorizationDocument travelAuthDoc = (TravelAuthorizationDocument) travelAuthForm.getDocument();


        int deleteIndex = getLineToDelete(request);
        travelAuthDoc.getTravelAdvances().remove(deleteIndex);
        for (int i=deleteIndex;i<travelAuthDoc.getTravelAdvances().size();i++){
            int value = travelAuthDoc.getTravelAdvances().get(i).getFinancialDocumentLineNumber().intValue() - 1;
            travelAuthDoc.getTravelAdvances().get(i).setFinancialDocumentLineNumber(new Integer(value));
        }
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.web.struts.TravelActionBase#updatePerDiemExpenses(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward updatePerDiemExpenses(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TravelAuthorizationForm taForm = (TravelAuthorizationForm) form;
        TravelAuthorizationDocument document = taForm.getTravelAuthorizationDocument();
        
        ActionForward forward = super.updatePerDiemExpenses(mapping, form, request, response);
        taForm.getNewSourceLine().setAmount(this.getAccountingLineAmountToFillin(taForm));
        
        getTravelEncumbranceService().updateEncumbranceObjectCode(document, taForm.getNewSourceLine());
        
        return forward;
    }

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#save(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TravelAuthorizationForm authForm = (TravelAuthorizationForm) form;
        TravelAuthorizationDocument travelReqDoc = authForm.getTravelAuthorizationDocument();
        String tripTypeCode = travelReqDoc.getTripTypeCode();

        /**
         * KUALITEM-360 Need to call updateAppDocStatus to save the document status if the document is in initial state or if it
         * currently doesn't have a status code to prevent errors further down the process.
         **/
        if (ObjectUtils.isNull(travelReqDoc.getAppDocStatus()) || travelReqDoc.getDocumentHeader().getWorkflowDocument().stateIsInitiated()) {
            travelReqDoc.updateAppDocStatus(TravelAuthorizationStatusCodeKeys.IN_PROCESS);
        }

        LOG.debug("Got special circumstances "+ authForm.getTravelAuthorizationDocument().getSpecialCircumstances());
        LOG.debug("Save Called on "+ getClass().getSimpleName()+ " for "+ authForm.getDocument().getClass().getSimpleName());
        LOG.debug("Saving document traveler detail "+ travelReqDoc.getTravelerDetailId());

        String[] transpoModes = request.getParameterValues("selectedTransportationModes");
        if (transpoModes != null) {
            authForm.setSelectedTransportationModes(Arrays.asList(transpoModes));
        }
        List<String> selectedTransportationModes = authForm.getTempSelectedTransporationModes();
        if (selectedTransportationModes != null) {
            authForm.getTravelAuthorizationDocument().setTransportationModeCodes(selectedTransportationModes);
        }
        else {
            authForm.getTravelAuthorizationDocument().setTransportationModeCodes(new ArrayList<String>());
        }

        return super.save(mapping, form, request, response);
    }

    /**
     * Recalculates the Expenses Total Tab
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @throws Exception
     */
    public ActionForward recalculate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {        
        return recalculateTripDetailTotal(mapping, form, request, response);
    }
    
    // Custom button actions
    /**
     * For use with a specific set of methods of this class that create new purchase order-derived document types in response to
     * user actions, including <code>amendTa</code>. It employs the question framework to ask the user for a response before
     * creating and routing the new document. The response should consist of a note detailing a reason, and either yes or no. This
     * method can be better understood if it is noted that it will be gone through twice (via the question framework); when each
     * question is originally asked, and again when the yes/no response is processed, for confirmation.
     * 
     * @param mapping These are boiler-plate.
     * @param form "
     * @param request "
     * @param response "
     * @param questionType A string identifying the type of question being asked.
     * @param confirmType A string identifying which type of question is being confirmed.
     * @param documentType A string, the type of document to create
     * @param notePrefix A string to appear before the note in the BO Notes tab
     * @param messageType A string to appear on the PO once the question framework is done, describing the action taken
     * @param operation A string, the verb to insert in the original question describing the action to be taken
     * @return An ActionForward
     * @throws Exception
     */
    @Override
    protected ActionForward askQuestionsAndPerformDocumentAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String questionType, String confirmType, String documentType, String notePrefix, String messageType, String operation) throws Exception {
        LOG.debug("askQuestionsAndPerformDocumentAction started.");
        TravelAuthorizationForm taForm = (TravelAuthorizationForm) form;
        TravelDocumentBase taDoc = taForm.getTravelAuthorizationDocument();
        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        String reason = request.getParameter(KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME);
        String noteText = "";

        try {
            KualiConfigurationService kualiConfiguration = SpringContext.getBean(KualiConfigurationService.class);

            // Start in logic for confirming the proposed operation.
            if (ObjectUtils.isNull(question)) {
                String message = "";
                String key = kualiConfiguration.getPropertyString(TemKeyConstants.TA_QUESTION_DOCUMENT);
                message = StringUtils.replace(key, "{0}", operation);
                // Ask question if not already asked.
                return this.performQuestionWithInput(mapping, form, request, response, questionType, message, KFSConstants.CONFIRMATION_QUESTION, questionType, "");
            }
            else {
                Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
                if (question.equals(questionType) && buttonClicked.equals(ConfirmationQuestion.NO)) {

                    // If 'No' is the button clicked, just reload the doc
                    return returnToPreviousPage(mapping, taForm);
                }
                else if (question.equals(confirmType) && buttonClicked.equals(SingleConfirmationQuestion.OK)) {

                    // This is the case when the user clicks on "OK" in the end.
                    // After we inform the user that the close has been rerouted, we'll redirect to the portal page.
                    return mapping.findForward(KFSConstants.MAPPING_PORTAL);
                }
                else {
                    // Have to check length on value entered.
                    String introNoteMessage = notePrefix + KFSConstants.BLANK_SPACE;

                    // Build out full message.
                    noteText = introNoteMessage + reason;
                    int noteTextLength = noteText.length();

                    // Get note text max length from DD.
                    int noteTextMaxLength = SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(Note.class, KFSConstants.NOTE_TEXT_PROPERTY_NAME).intValue();

                    if (StringUtils.isBlank(reason) || (noteTextLength > noteTextMaxLength)) {
                        // Figure out exact number of characters that the user can enter.
                        int reasonLimit = noteTextMaxLength - noteTextLength;

                        if (ObjectUtils.isNull(reason)) {
                            // Prevent a NPE by setting the reason to a blank string.
                            reason = "";
                        }

                        return this.performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, questionType, kualiConfiguration.getPropertyString(TemKeyConstants.TA_QUESTION_DOCUMENT), KFSConstants.CONFIRMATION_QUESTION, questionType, "", reason, TemKeyConstants.ERROR_TA_REASON_REQUIRED, KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME, new Integer(reasonLimit).toString());
                    }
                }
            }
            // Below used as a place holder to allow code to specify actionForward to return if not a 'success question'
            ActionForward returnActionForward = null;
            String newStatus = null;
            if (documentType.equals(TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_AMEND_DOCUMENT)) {
                newStatus = TemConstants.TravelAuthorizationStatusCodeKeys.PEND_AMENDMENT;
                returnActionForward = mapping.findForward(KFSConstants.MAPPING_BASIC);
                noteText = noteText + " (Previous Document Id is " + taForm.getDocId() + ")";
            }
            else if (questionType.equals(TemConstants.HOLD_TA_QUESTION)) {
                newStatus = TemConstants.TravelAuthorizationStatusCodeKeys.REIMB_HELD;
            }
            else if (questionType.equals(TemConstants.REMOVE_HOLD_TA_QUESTION)) {
                newStatus = TemConstants.TravelAuthorizationStatusCodeKeys.OPEN_REIMB;
            }

            Note newNote = getDocumentService().createNoteFromDocument(taDoc, noteText);


            // newNote.setNoteText(noteText);
            // newNote.setNoteTypeCode(KFSConstants.NoteTypeEnum.DOCUMENT_HEADER_NOTE_TYPE.getCode());
            // getDocumentService().addNoteToDocument(taDoc, newNote);
            taForm.setNewNote(newNote);

            taForm.setAttachmentFile(new BlankFormFile());

            insertBONote(mapping, taForm, request, response);
            // save the new state on the document
            taDoc.updateAppDocStatus(newStatus);

            // send FYI for Hold, Remove Hold
            if (questionType.equals(TemConstants.REMOVE_HOLD_TA_QUESTION) || questionType.equals(TemConstants.HOLD_TA_QUESTION)) {
                getTravelDocumentService().addAdHocFYIRecipient(taDoc, taDoc.getDocumentHeader().getWorkflowDocument().getRouteHeader().getInitiatorPrincipalId());
            }

            SpringContext.getBean(DocumentService.class).saveDocument(taDoc);


            if (ObjectUtils.isNotNull(returnActionForward)) {
                return returnActionForward;
            }
            else {
                return this.performQuestionWithoutInput(mapping, form, request, response, confirmType, kualiConfiguration.getPropertyString(messageType), "temSingleConfirmationQuestion", questionType, "");
            }
        }
        catch (ValidationException ve) {
            throw ve;
        }
    }

    /**
     * Is invoked when the user pressed on the Amend button on a Travel Authorization page to amend the TA. It will display the
     * question page to the user to ask whether the user really wants to amend the TA and ask the user to enter a reason in the text
     * area. If the user has entered the reason, it will invoke a service method to do the processing for amending the TA, then
     * display a Single Confirmation page to inform the user that the <code>TravelAuthorizationAmendmentDocument</code> has been
     * routed.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     * @see org.kuali.kfs.module.tem.document.TravelAuthorizationAmendmentDocument
     */
    public ActionForward amendTa(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        LOG.debug("Amend TA started");
        final Inquisitive<TravelAuthorizationDocument, ActionForward> inq = getInquisitive(mapping, form, request, response);
        if (inq.wasQuestionAsked()) {
            if (request.getParameterMap().containsKey(KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME)){
                this.save(mapping, form, request, response);
            }
        }
        ActionForward forward = askQuestionsAndPerformDocumentAction(inq, TemConstants.AMENDMENT_TA_QUESTION);
        
        return forward;
    }

    /**
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward holdTa(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        LOG.debug("Hold TA started");
        final Inquisitive<TravelAuthorizationDocument, ActionForward> inq = getInquisitive(mapping, form, request, response);
        return askQuestionsAndPerformDocumentAction(inq, TemConstants.HOLD_TA_QUESTION);
    }

    /**
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward removeHoldTa(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        LOG.debug("Remove Hold TA started");
        final Inquisitive<TravelAuthorizationDocument, ActionForward> inq = getInquisitive(mapping, form, request, response);
        return askQuestionsAndPerformDocumentAction(inq, TemConstants.REMOVE_HOLD_TA_QUESTION);
    }

    /**
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward cancelTa(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        LOG.debug("Cancel TA started");

        TravelAuthorizationForm taForm = (TravelAuthorizationForm) form;
        TravelAuthorizationDocument taDocument = taForm.getTravelAuthorizationDocument();
        
        final Note newNote = getDocumentService().createNoteFromDocument(taDocument, TemConstants.TA_CANCELLED_MESSAGE);
        //newNote.setNoteTypeCode(KFSConstants.NoteTypeEnum.DOCUMENT_HEADER_NOTE_TYPE.getCode());
        getDocumentService().addNoteToDocument(taDocument, newNote);
        
        taDocument.updateAppDocStatus(TravelAuthorizationStatusCodeKeys.CANCELLED);
        getTravelEncumbranceService().liquidateEncumbranceForCancelTA(taDocument);
        SpringContext.getBean(DocumentService.class).saveDocument(taDocument);
        
        // send FYI for to initiator and traveler
        getTravelDocumentService().addAdHocFYIRecipient(taDocument, taDocument.getDocumentHeader().getWorkflowDocument().getRouteHeader().getInitiatorPrincipalId());
        getTravelDocumentService().addAdHocFYIRecipient(taDocument, taDocument.getTraveler().getPrincipalId());

        SpringContext.getBean(WorkflowDocumentService.class).acknowledge(taDocument.getDocumentHeader().getWorkflowDocument(), null, taDocument.getAdHocRoutePersons());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward closeTa(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        LOG.debug("Close TA started");
        String operation = "Close ";

        final Inquisitive<TravelAuthorizationDocument, ActionForward> inq = getInquisitive(mapping, form, request, response);

        return askQuestionsAndPerformDocumentAction(inq, TemConstants.CLOSE_TA_QUESTION);
    }

    /**
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    protected Inquisitive<TravelAuthorizationDocument, ActionForward> getInquisitive(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) {
        final Inquisitive<TravelAuthorizationDocument, ActionForward> inq = new StrutsInquisitor<TravelAuthorizationDocument, TravelAuthorizationForm, TravelAuthorizationAction>(mapping, (TravelAuthorizationForm) form, this, request, response);
        
        return inq;
    }

    /**
     * 
     * @param inq
     * @param questionId
     * @return
     * @throws Exception
     */
    protected ActionForward askQuestionsAndPerformDocumentAction(final Inquisitive<TravelAuthorizationDocument, ActionForward> inq, final String questionId) throws Exception {
        final QuestionHandler<TravelAuthorizationDocument> questionHandler = getQuestionHandler(questionId);

        if (inq.wasQuestionAsked()) {
            return questionHandler.handleResponse(inq);
        }
        
        return questionHandler.askQuestion(inq);
    }

    /**
     * This is a utility method used to prepare to and to return to a previous page, making sure that the buttons will be restored
     * in the process.
     * 
     * @param kualiDocumentFormBase The Form, considered as a KualiDocumentFormBase, as it typically is here.
     * @return An actionForward mapping back to the original page.
     */
    protected ActionForward returnToPreviousPage(ActionMapping mapping, KualiDocumentFormBase kualiDocumentFormBase) {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    KualiDecimal getAccountingLineAmountToFillin(TravelAuthorizationForm travelAuthForm) {
        KualiDecimal amount = new KualiDecimal(0);

        KualiDecimal encTotal = travelAuthForm.getTravelAuthorizationDocument().getEncumbranceTotal();
        KualiDecimal expenseTotal = travelAuthForm.getTravelAuthorizationDocument().getExpenseLimit();

        List<SourceAccountingLine> accountingLines = travelAuthForm.getTravelAuthorizationDocument().getSourceAccountingLines();

        KualiDecimal accountingTotal = new KualiDecimal(0);
        for (SourceAccountingLine accountingLine : accountingLines) {
            accountingTotal = accountingTotal.add(accountingLine.getAmount());
        }

        if (ObjectUtils.isNull(expenseTotal)) {
            amount = encTotal.subtract(accountingTotal);
        }
        else if (expenseTotal.isLessThan(encTotal)) {
            amount = expenseTotal.subtract(accountingTotal);
        }
        else {
            amount = encTotal.subtract(accountingTotal);
        }

        return amount;
    }

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#insertSourceLine(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward insertSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.insertSourceLine(mapping, form, request, response);
        TravelAuthorizationForm travelauthForm = (TravelAuthorizationForm) form;
        travelauthForm.getNewSourceLine().setAmount(this.getAccountingLineAmountToFillIn(travelauthForm));
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.web.struts.TravelActionBase#deleteSourceLine(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward deleteSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.deleteSourceLine(mapping, form, request, response);
        TravelAuthorizationForm travelauthForm = (TravelAuthorizationForm) form;
        travelauthForm.getNewSourceLine().setAmount(this.getAccountingLineAmountToFillIn(travelauthForm));
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.web.struts.TravelActionBase#cancel(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object buttonClicked = request.getParameter(KNSConstants.QUESTION_CLICKED_BUTTON);
        if (ConfirmationQuestion.YES.equals(buttonClicked)) {
            reverseAmendment((TravelAuthorizationForm) form);
        }
        
        return super.cancel(mapping, form, request, response);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.web.struts.TravelActionBase#close(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object buttonClicked = request.getParameter(KNSConstants.QUESTION_CLICKED_BUTTON);
        /*if (ConfirmationQuestion.NO.equals(buttonClicked)) {
            reverseAmendment((TravelAuthorizationForm) form);
        }*/
        return super.close(mapping, form, request, response);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.web.struts.TravelActionBase#route(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        recalculateTripDetailTotalOnly(mapping, form, request, response);
        TravelAuthorizationForm authForm = (TravelAuthorizationForm) form;
        TravelAuthorizationDocument document = authForm.getTravelAuthorizationDocument();
        String[] transpoModes = request.getParameterValues("selectedTransportationModes");
        if(transpoModes != null) {
            authForm.setSelectedTransportationModes(Arrays.asList(transpoModes));
        }
        List<String> selectedTransportationModes = authForm.getTempSelectedTransporationModes();
        if (selectedTransportationModes != null) {
            document.setTransportationModeCodes(selectedTransportationModes);
        }
        else {
            document.setTransportationModeCodes(new ArrayList<String>());
        }
        
        return super.route(mapping, form, request, response);       
    }

    /**
     * @see org.kuali.kfs.module.tem.document.web.struts.TravelActionBase#blanketApprove(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward blanketApprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        for (TravelAdvance advance : ((TravelAuthorizationForm)form).getTravelAuthorizationDocument().getTravelAdvances()){
            advance.setTravelAdvancePolicy(true);
        }
        
        return super.blanketApprove(mapping, form, request, response);
    }      

    /**
     * Update the TA status if the amendment is canceled or closed and not saved.
     * 
     * @param form
     */
    protected void reverseAmendment(TravelAuthorizationForm form) {
        if (form.getTravelAuthorizationDocument() instanceof TravelAuthorizationAmendmentDocument) {
            TravelDocument travelDocument = form.getTravelAuthorizationDocument();
            getTravelDocumentService().revertOriginalDocument(travelDocument, TravelAuthorizationStatusCodeKeys.OPEN_REIMB);
            /*String docID = form.getTravelAuthorizationDocument().getDocumentHeader().getDocumentNumber();
            try {
                TravelAuthorizationAmendmentDocument document = (TravelAuthorizationAmendmentDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docID);
                // If TAA doesn't exist yet, or is only in SAVED status, change TA back to OPEN status
                if (document == null || document.getDocumentHeader().getWorkflowDocument().stateIsSaved()) {
                    getTravelDocumentService().revertOriginalDocument(form, TravelAuthorizationStatusCodeKeys.OPEN_REIMB);
                }
                
            }
            catch (WorkflowException ex) {
                ex.printStackTrace();
            }*/
        }
    }


    /**
     * @see org.kuali.kfs.module.tem.document.web.bean.TravelReimbursementMvcWrapperBean
     * @see org.kuali.kfs.module.tem.document.web.bean.TravelMvcWrapperBean
     * @see org.kuali.kfs.module.tem.document.web.struts.TravelActionBase#getMvcWrapperInterface()
     * @see org.kuali.kfs.module.tem.document.web.struts.TravelActionBase#newMvcDelegate(ActionForm)
     * @see org.kuali.kfs.module.tem.document.web.struts.TravelStrutsObservable
     */
    @Override
    protected Class getMvcWrapperInterface() {
        return TravelAuthorizationMvcWrapperBean.class;
    }

}

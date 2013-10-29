/*
 * Copyright 2012 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.document.web.struts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.Event;
import org.kuali.kfs.module.ar.document.CollectionActivityDocument;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CollectionActivityDocumentService;
import org.kuali.kfs.module.ar.document.validation.event.AddCollectionActivityDocumentEvent;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentActionBase;
import org.kuali.kfs.sys.service.SegmentedLookupResultsService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.rules.rule.event.SaveDocumentEvent;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Action file for Collection Activity Document.
 */
public class CollectionActivityDocumentAction extends FinancialSystemTransactionalDocumentActionBase {

    protected DocumentService documentService;
    protected CollectionActivityDocumentService collectionActivityDocumentService;

    /**
     * Constructor for CollectionActivityDocumentAction class
     */
    public CollectionActivityDocumentAction() {
        super();
        documentService = SpringContext.getBean(DocumentService.class);
        collectionActivityDocumentService = SpringContext.getBean(CollectionActivityDocumentService.class);
    }

    /**
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     *      javax.servlet.ServletRequest, javax.servlet.ServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, ServletRequest request, ServletResponse response) throws Exception {
        return super.execute(mapping, form, request, response);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        CollectionActivityDocumentForm cform = (CollectionActivityDocumentForm) kualiDocumentFormBase;
        CollectionActivityDocument colActDoc = cform.getCollectionActivityDocument();

        if (ObjectUtils.isNotNull(cform.getSelectedInvoiceDocumentNumber()) && ObjectUtils.isNotNull(cform.getSelectedProposalNumber())) {
            colActDoc.setProposalNumber(new Long(cform.getSelectedProposalNumber()));
            SpringContext.getBean(CollectionActivityDocumentService.class).loadAwardInformationForCollectionActivityDocument(colActDoc);
            loadInvoices(cform, cform.getSelectedInvoiceDocumentNumber());
            colActDoc.setEventsFromCGInvoices();
        }
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);
        CollectionActivityDocumentForm cform = (CollectionActivityDocumentForm) kualiDocumentFormBase;
        CollectionActivityDocument colActDoc = cform.getCollectionActivityDocument();
        loadInvoices(cform, cform.getSelectedInvoiceDocumentNumber());
        colActDoc.setEventsFromCGInvoices();
    }

    /**
     * This method updates the customer invoice details when a new invoice is selected
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward goToInvoice(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CollectionActivityDocumentForm colActForm = (CollectionActivityDocumentForm) form;
        loadEvents(colActForm, colActForm.getSelectedInvoiceDocumentNumber());
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method updates customer invoice details when next invoice is selected
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward goToNextInvoice(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CollectionActivityDocumentForm colActForm = (CollectionActivityDocumentForm) form;
        loadEvents(colActForm, colActForm.getNextInvoiceDocumentNumber());
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method updates customer invoice details when previous invoice is selected
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward goToPreviousInvoice(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CollectionActivityDocumentForm colActForm = (CollectionActivityDocumentForm) form;
        loadEvents(colActForm, colActForm.getPreviousInvoiceDocumentNumber());
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Retrieve all invoices for the selected customer.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadInvoices(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CollectionActivityDocumentForm cform = (CollectionActivityDocumentForm) form;
        loadInvoices(cform, null);
        CollectionActivityDocument colActDoc = cform.getCollectionActivityDocument();
        colActDoc.setEventsFromCGInvoices();
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method loads the invoices for currently selected customer
     * 
     * @param applicationDocumentForm
     */
    @SuppressWarnings("unused")
    protected void loadInvoices(CollectionActivityDocumentForm colActForm, String selectedInvoiceNumber) {
        CollectionActivityDocument colActDoc = colActForm.getCollectionActivityDocument();
        String currentInvoiceNumber = selectedInvoiceNumber;
        Long proposalNumber = null;
        boolean validInvoice = true;

        String proposalNumber_string = colActForm.getSelectedProposalNumber();

        if (ObjectUtils.isNull(proposalNumber_string) && ObjectUtils.isNotNull(colActDoc.getProposalNumber())) {
            proposalNumber = colActDoc.getProposalNumber();
            colActForm.setSelectedProposalNumber(proposalNumber.toString());
        }
        else if (ObjectUtils.isNotNull(proposalNumber_string)) {
            proposalNumber = new Long(proposalNumber_string);
        }

        // reload invoices for the selected proposal number
        if (ObjectUtils.isNotNull(proposalNumber)) {
            Collection<ContractsGrantsInvoiceDocument> cgInvoices = new ArrayList<ContractsGrantsInvoiceDocument>();
            Collection<ContractsGrantsInvoiceDocument> newCGInvoices = new ArrayList<ContractsGrantsInvoiceDocument>();
            colActDoc.setInvoiceListByProposalNumber(proposalNumber);
        }

        loadEvents(colActForm, currentInvoiceNumber);

    }

    /**
     * This method loads the events for selected invoice number.
     * 
     * @param colActForm The form object of Collection Activity.
     * @param currentInvoiceNumber The invoice number of which, events are to be loaded.
     */
    public void loadEvents(CollectionActivityDocumentForm colActForm, String currentInvoiceNumber) {
        CollectionActivityDocument colActDoc = colActForm.getCollectionActivityDocument();

        // if no invoice number entered than get the first invoice
        if (colActForm.getSelectedProposalNumber() != null && StringUtils.isBlank(currentInvoiceNumber)) {
            if (ObjectUtils.isNull(colActDoc.getInvoices()) || colActDoc.getInvoices().isEmpty()) {
                currentInvoiceNumber = null;
            }
            else {
                currentInvoiceNumber = colActDoc.getInvoices().get(0).getDocumentNumber();
            }
        }

        // load information for the current selected invoice
        if (StringUtils.isNotBlank(currentInvoiceNumber)) {
            colActForm.setSelectedInvoiceDocumentNumber(currentInvoiceNumber);
            colActDoc.setSelectedInvoiceDocumentNumber(currentInvoiceNumber);
        }

    }

    /**
     * Get an error to display in the UI for a certain field.
     * 
     * @param propertyName
     * @param errorKey
     */
    protected void addFieldError(String errorPathToAdd, String propertyName, String errorKey) {
        GlobalVariables.getMessageMap().addToErrorPath(errorPathToAdd);
        GlobalVariables.getMessageMap().putError(propertyName, errorKey);
        GlobalVariables.getMessageMap().removeFromErrorPath(errorPathToAdd);
    }

    /**
     * Get an error to display at the global level, for the whole document.
     * 
     * @param errorKey
     */
    protected void addGlobalError(String errorKey) {
        GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(KRADConstants.DOCUMENT_ERRORS, errorKey, "document.hiddenFieldForErrors");
    }

    /**
     * This method adds a new event.
     * 
     * @param mapping action mapping
     * @param form action form
     * @param request
     * @param response
     * @return forward action
     * @throws Exception
     */
    public ActionForward addEvent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CollectionActivityDocumentForm colActDocForm = (CollectionActivityDocumentForm) form;
        CollectionActivityDocument colActDoc = colActDocForm.getCollectionActivityDocument();

        Event newEvent = colActDoc.getNewEvent();
        ConfigurationService kualiConfiguration = SpringContext.getBean(ConfigurationService.class);
        if (ObjectUtils.isNull(colActDocForm.getSelectedInvoiceApplication().getEvents())) {
            colActDocForm.getSelectedInvoiceApplication().setEvents(new ArrayList<Event>());
        }
        colActDoc.setProposalNumber(new Long(colActDocForm.getSelectedProposalNumber()));
        newEvent.setInvoiceNumber(colActDocForm.getSelectedInvoiceDocumentNumber());

        KualiRuleService ruleService = SpringContext.getBean(KualiRuleService.class);
        boolean rulePassed = true;

        // apply save rules for the doc
        rulePassed &= ruleService.applyRules(new SaveDocumentEvent(KFSConstants.DOCUMENT_HEADER_ERRORS, colActDoc));

        // apply rules for the new collection activity document detail
        rulePassed &= ruleService.applyRules(new AddCollectionActivityDocumentEvent(ArConstants.NEW_COLLECTION_ACTIVITY_EVENT_ERROR_PATH_PREFIX, colActDoc, newEvent));

        if (rulePassed) {
            collectionActivityDocumentService.addNewEvent(kualiConfiguration.getPropertyValueAsString(ArKeyConstants.CollectionActivityDocumentConstants.CREATED_BY_COLLECTION_ACTIVITY_DOC), colActDoc, newEvent);
            colActDoc.setNewEvent(new Event());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method adds a new event
     * 
     * @param mapping action mapping
     * @param form action form
     * @param request
     * @param response
     * @return forward action
     * @throws Exception
     */
    public ActionForward editEvent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        int index = getSelectedLine(request);
        CollectionActivityDocumentForm colActDocForm = (CollectionActivityDocumentForm) form;
        CollectionActivityDocument colActDoc = colActDocForm.getCollectionActivityDocument();
        ConfigurationService kualiConfiguration = SpringContext.getBean(ConfigurationService.class);

        Event selectedEvent = null;
        if (ObjectUtils.isNotNull(colActDocForm.getSelectedInvoiceApplication()) && ObjectUtils.isNotNull(colActDocForm.getSelectedInvoiceApplication().getEvents()) && !colActDocForm.getSelectedInvoiceApplication().getEvents().isEmpty()) {
            try {
                selectedEvent = colActDocForm.getSelectedInvoiceApplication().getEvents().get(index);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        KualiRuleService ruleService = SpringContext.getBean(KualiRuleService.class);
        boolean rulePassed = true;

        // apply save rules for the doc
        rulePassed &= ruleService.applyRules(new SaveDocumentEvent(KFSConstants.DOCUMENT_HEADER_ERRORS, colActDoc));

        // apply rules for the new collection activity document detail
        rulePassed &= ruleService.applyRules(new AddCollectionActivityDocumentEvent(ArConstants.NEW_COLLECTION_ACTIVITY_EVENT_ERROR_PATH_PREFIX, colActDoc, selectedEvent));

        if (rulePassed) {
            collectionActivityDocumentService.editEvent(kualiConfiguration.getPropertyValueAsString(ArKeyConstants.CollectionActivityDocumentConstants.CREATED_BY_COLLECTION_ACTIVITY_DOC), colActDoc, selectedEvent);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.refresh(mapping, form, request, response);
        CollectionActivityDocumentForm collectionActivityDocumentForm = (CollectionActivityDocumentForm) form;
        CollectionActivityDocument colActDoc = collectionActivityDocumentForm.getCollectionActivityDocument();
        Collection<PersistableBusinessObject> rawValues = null;
        Map<String, Set<String>> segmentedSelection = new HashMap<String, Set<String>>();

        // If multiple asset lookup was used to select the assets, then....
        if (StringUtils.equals(KFSConstants.MULTIPLE_VALUE, collectionActivityDocumentForm.getRefreshCaller())) {
            String lookupResultsSequenceNumber = collectionActivityDocumentForm.getLookupResultsSequenceNumber();
            Set<String> selectedIds = SpringContext.getBean(SegmentedLookupResultsService.class).retrieveSetOfSelectedObjectIds(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getPerson().getPrincipalId());
            if (ObjectUtils.isNotNull(selectedIds) && CollectionUtils.isNotEmpty(selectedIds)) {
                colActDoc.setSelectedInvoiceDocumentNumberList(StringUtils.join(selectedIds.toArray(), ","));
            }
        }
        if (StringUtils.equals(KFSConstants.AWARD_LOOKUP, collectionActivityDocumentForm.getRefreshCaller())) {
            if (ObjectUtils.isNotNull(colActDoc.getProposalNumber())) {
                ContractsAndGrantsBillingAward award = collectionActivityDocumentService.retrieveAwardByProposalNumber(colActDoc.getProposalNumber());
                if (ObjectUtils.isNotNull(award)) {
                    colActDoc.setAgencyNumber(award.getAgencyNumber());
                    colActDoc.setAgencyName(award.getAgency().getFullName());
                    if (ObjectUtils.isNotNull(award.getAgency().getCustomer())) {
                        colActDoc.setCustomerNumber(award.getAgency().getCustomer().getCustomerNumber());
                        colActDoc.setCustomerName(award.getAgency().getCustomer().getCustomerName());
                    }
                }
                else {
                    colActDoc.setAgencyNumber("Award not found");
                    colActDoc.setAgencyName("");
                    colActDoc.setCustomerNumber("");
                    colActDoc.setCustomerName("");
                }
            }
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward addGlobalEvent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        MessageMap errorMap = GlobalVariables.getMessageMap();
        CollectionActivityDocumentForm colActDocForm = (CollectionActivityDocumentForm) form;
        CollectionActivityDocument colActDoc = colActDocForm.getCollectionActivityDocument();
        if (ObjectUtils.isNull(colActDoc.getSelectedInvoiceDocumentNumberList())) {
            errorMap.putError("document." + ArPropertyConstants.EventFields.SELECTED_INVOICES, ArKeyConstants.CollectionActivityDocumentErrors.ERROR_COMPLETED_DATE_REQUIRED);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        List<String> selectedInvoiceList = new ArrayList(Arrays.asList(colActDoc.getSelectedInvoiceDocumentNumberList().split(",")));
        Event newGlobalEvent = colActDoc.getGlobalEvent();

        ConfigurationService kualiConfiguration = SpringContext.getBean(ConfigurationService.class);

        KualiRuleService ruleService = SpringContext.getBean(KualiRuleService.class);
        for (String invoiceNumber : selectedInvoiceList) {
            boolean rulePassed = true;
            newGlobalEvent.setInvoiceNumber(invoiceNumber);
            // apply save rules for the doc
            rulePassed &= ruleService.applyRules(new SaveDocumentEvent(KFSConstants.DOCUMENT_HEADER_ERRORS, colActDoc));

            // apply rules for the new collection activity document detail
            rulePassed &= ruleService.applyRules(new AddCollectionActivityDocumentEvent(ArConstants.NEW_COLLECTION_ACTIVITY_EVENT_ERROR_PATH_PREFIX, colActDoc, newGlobalEvent));

            if (rulePassed) {
                collectionActivityDocumentService.addNewEvent(kualiConfiguration.getPropertyValueAsString(ArKeyConstants.CollectionActivityDocumentConstants.CREATED_BY_COLLECTION_ACTIVITY_DOC), colActDoc, newGlobalEvent);
                colActDoc.setGlobalEvent(new Event());
                colActDoc.setSelectedInvoiceDocumentNumberList("");
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
}

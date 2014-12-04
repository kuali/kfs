/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.document.web.struts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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
import org.kuali.kfs.module.ar.businessobject.CollectionEvent;
import org.kuali.kfs.module.ar.document.CollectionActivityDocument;
import org.kuali.kfs.module.ar.document.service.CollectionActivityDocumentService;
import org.kuali.kfs.module.ar.document.validation.event.AddCollectionEventEvent;
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
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CollectionActivityDocumentAction.class);

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
        loadCollectionEvents(colActForm, colActForm.getSelectedInvoiceDocumentNumber());
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
        loadCollectionEvents(colActForm, colActForm.getNextInvoiceDocumentNumber());
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
        loadCollectionEvents(colActForm, colActForm.getPreviousInvoiceDocumentNumber());
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
            colActDoc.setInvoiceListByProposalNumber(proposalNumber);
        }

        loadCollectionEvents(colActForm, currentInvoiceNumber);

    }

    /**
     * This method loads the collection events for selected invoice number.
     *
     * @param colActForm The form object of Collection Activity.
     * @param currentInvoiceNumber The invoice number of which, events are to be loaded.
     */
    public void loadCollectionEvents(CollectionActivityDocumentForm colActForm, String currentInvoiceNumber) {
        CollectionActivityDocument colActDoc = colActForm.getCollectionActivityDocument();

        // if no invoice number entered than get the first invoice
        if (colActForm.getSelectedProposalNumber() != null && StringUtils.isBlank(currentInvoiceNumber)) {
            if (CollectionUtils.isEmpty(colActDoc.getInvoices())) {
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
     * This method adds a new collection event.
     *
     * @param mapping action mapping
     * @param form action form
     * @param request
     * @param response
     * @return forward action
     * @throws Exception
     */
    public ActionForward addCollectionEvent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CollectionActivityDocumentForm colActDocForm = (CollectionActivityDocumentForm) form;
        CollectionActivityDocument colActDoc = colActDocForm.getCollectionActivityDocument();

        CollectionEvent newCollectionEvent = colActDoc.getNewCollectionEvent();
        ConfigurationService kualiConfiguration = SpringContext.getBean(ConfigurationService.class);
        if (ObjectUtils.isNull(colActDocForm.getSelectedInvoiceApplication().getCollectionEvents())) {
            colActDocForm.getSelectedInvoiceApplication().setCollectionEvents(new ArrayList<CollectionEvent>());
        }
        colActDoc.setProposalNumber(new Long(colActDocForm.getSelectedProposalNumber()));
        newCollectionEvent.setInvoiceNumber(colActDocForm.getSelectedInvoiceDocumentNumber());

        KualiRuleService ruleService = SpringContext.getBean(KualiRuleService.class);
        boolean rulePassed = true;

        // apply save rules for the doc
        rulePassed &= ruleService.applyRules(new SaveDocumentEvent(KFSConstants.DOCUMENT_HEADER_ERRORS, colActDoc));

        // apply rules for the new collection activity document detail
        rulePassed &= ruleService.applyRules(new AddCollectionEventEvent(ArConstants.NEW_COLLECTION_EVENT_ERROR_PATH_PREFIX, colActDoc, newCollectionEvent));

        if (rulePassed) {
            collectionActivityDocumentService.addNewCollectionEvent(kualiConfiguration.getPropertyValueAsString(ArKeyConstants.CollectionActivityDocumentConstants.CREATED_BY_COLLECTION_ACTIVITY_DOC), colActDoc, newCollectionEvent);
            colActDoc.setNewCollectionEvent(new CollectionEvent());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method edits a collection event
     *
     * @param mapping action mapping
     * @param form action form
     * @param request
     * @param response
     * @return forward action
     * @throws Exception
     */
    public ActionForward editCollectionEvent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        int index = getSelectedLine(request);
        CollectionActivityDocumentForm colActDocForm = (CollectionActivityDocumentForm) form;
        CollectionActivityDocument colActDoc = colActDocForm.getCollectionActivityDocument();
        ConfigurationService kualiConfiguration = SpringContext.getBean(ConfigurationService.class);

        CollectionEvent selectedEvent = null;
        if (ObjectUtils.isNotNull(colActDocForm.getSelectedInvoiceApplication()) && ObjectUtils.isNotNull(colActDocForm.getSelectedInvoiceApplication().getCollectionEvents()) && !colActDocForm.getSelectedInvoiceApplication().getCollectionEvents().isEmpty()) {
            selectedEvent = colActDocForm.getSelectedInvoiceApplication().getCollectionEvents().get(index);
        }

        KualiRuleService ruleService = SpringContext.getBean(KualiRuleService.class);
        boolean rulePassed = true;

        // apply save rules for the doc
        rulePassed &= ruleService.applyRules(new SaveDocumentEvent(KFSConstants.DOCUMENT_HEADER_ERRORS, colActDoc));

        // apply rules for the new collection activity document detail
        rulePassed &= ruleService.applyRules(new AddCollectionEventEvent(ArConstants.NEW_COLLECTION_EVENT_ERROR_PATH_PREFIX, colActDoc, selectedEvent));

        if (rulePassed) {
            collectionActivityDocumentService.editCollectionEvent(kualiConfiguration.getPropertyValueAsString(ArKeyConstants.CollectionActivityDocumentConstants.CREATED_BY_COLLECTION_ACTIVITY_DOC), colActDoc, selectedEvent);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.refresh(mapping, form, request, response);
        CollectionActivityDocumentForm collectionActivityDocumentForm = (CollectionActivityDocumentForm) form;
        CollectionActivityDocument colActDoc = collectionActivityDocumentForm.getCollectionActivityDocument();
        Collection<PersistableBusinessObject> rawValues = null;

        // If multiple asset lookup was used to select the assets, then....
        if (StringUtils.equals(KFSConstants.MULTIPLE_VALUE, collectionActivityDocumentForm.getRefreshCaller())) {
            String lookupResultsSequenceNumber = collectionActivityDocumentForm.getLookupResultsSequenceNumber();
            Set<String> selectedIds = SpringContext.getBean(SegmentedLookupResultsService.class).retrieveSetOfSelectedObjectIds(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getPerson().getPrincipalId());
            if (ObjectUtils.isNotNull(selectedIds) && CollectionUtils.isNotEmpty(selectedIds)) {
                colActDoc.setSelectedInvoiceDocumentNumberList(StringUtils.join(selectedIds.toArray(), ","));
            }
        }
        if (StringUtils.equals(ArConstants.AWARD_LOOKUP_IMPL, collectionActivityDocumentForm.getRefreshCaller())) {
            if (ObjectUtils.isNotNull(colActDoc.getProposalNumber())) {
                ContractsAndGrantsBillingAward award = collectionActivityDocumentService.retrieveAwardByProposalNumber(colActDoc.getProposalNumber());
                if (ObjectUtils.isNotNull(award)) {
                    colActDoc.setAgencyNumber(award.getAgencyNumber());
                    colActDoc.setAgencyName(award.getAgency().getFullName());
                    if (ObjectUtils.isNotNull(award.getAgency().getCustomer())) {
                        colActDoc.setCustomerNumber(award.getAgency().getCustomer().getCustomerNumber());
                        colActDoc.setCustomerName(award.getAgency().getCustomer().getCustomerName());
                    }
                    loadInvoices(collectionActivityDocumentForm, null);
                    collectionActivityDocumentForm.getCollectionActivityDocument().setEventsFromCGInvoices();
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

    public ActionForward addGlobalCollectionEvent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        MessageMap errorMap = GlobalVariables.getMessageMap();
        CollectionActivityDocumentForm colActDocForm = (CollectionActivityDocumentForm) form;
        CollectionActivityDocument colActDoc = colActDocForm.getCollectionActivityDocument();
        if (ObjectUtils.isNull(colActDoc.getSelectedInvoiceDocumentNumberList())) {
            errorMap.putError("document." + ArPropertyConstants.CollectionEventFields.SELECTED_INVOICES, ArKeyConstants.CollectionActivityDocumentErrors.ERROR_INVOICE_REQUIRED);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        List<String> selectedInvoiceList = new ArrayList(Arrays.asList(colActDoc.getSelectedInvoiceDocumentNumberList().split(",")));

        ConfigurationService kualiConfiguration = SpringContext.getBean(ConfigurationService.class);

        KualiRuleService ruleService = SpringContext.getBean(KualiRuleService.class);
        for (String invoiceNumber : selectedInvoiceList) {
            boolean rulePassed = true;
            CollectionEvent newGlobalCollectionEvent = new CollectionEvent(colActDoc.getGlobalCollectionEvent());
            newGlobalCollectionEvent.setInvoiceNumber(invoiceNumber);
            // apply save rules for the doc
            rulePassed &= ruleService.applyRules(new SaveDocumentEvent(KFSConstants.DOCUMENT_HEADER_ERRORS, colActDoc));

            // apply rules for the new collection activity document detail
            rulePassed &= ruleService.applyRules(new AddCollectionEventEvent(ArConstants.NEW_COLLECTION_EVENT_ERROR_PATH_PREFIX, colActDoc, newGlobalCollectionEvent));
            if (rulePassed) {
                collectionActivityDocumentService.addNewCollectionEvent(kualiConfiguration.getPropertyValueAsString(ArKeyConstants.CollectionActivityDocumentConstants.CREATED_BY_COLLECTION_ACTIVITY_DOC), colActDoc, newGlobalCollectionEvent);
            }
        }
        colActDoc.setGlobalCollectionEvent(new CollectionEvent());
        colActDoc.setSelectedInvoiceDocumentNumberList("");
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
}

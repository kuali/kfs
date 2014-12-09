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

import java.util.Collection;
import java.util.HashMap;
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
<<<<<<< HEAD:work/src/org/kuali/kfs/module/ar/document/web/struts/CollectionActivityDocumentAction.java
import org.kuali.kfs.module.ar.businessobject.CollectionActivityInvoiceDetail;
import org.kuali.kfs.module.ar.document.CollectionActivityDocument;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CollectionActivityDocumentService;
=======
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsCollectionActivityInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.CollectionEvent;
import org.kuali.kfs.module.ar.document.ContractsGrantsCollectionActivityDocument;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsCollectionActivityDocumentService;
import org.kuali.kfs.module.ar.document.validation.event.AddCollectionEventEvent;
>>>>>>> KFSTP-1597: renames of collection activity stuff to contracts & grants collection activity:work/src/org/kuali/kfs/module/ar/document/web/struts/ContractsGrantsCollectionActivityDocumentAction.java
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentActionBase;
import org.kuali.kfs.sys.service.SegmentedLookupResultsService;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Action file for Collection Activity Document.
 */
public class ContractsGrantsCollectionActivityDocumentAction extends FinancialSystemTransactionalDocumentActionBase {

    protected DocumentService documentService;
    protected ContractsGrantsCollectionActivityDocumentService contractsGrantsCollectionActivityDocumentService;
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsCollectionActivityDocumentAction.class);

    /**
     * Constructor for ContractsGrantsCollectionActivityDocumentAction class
     */
    public ContractsGrantsCollectionActivityDocumentAction() {
        super();
        documentService = SpringContext.getBean(DocumentService.class);
        contractsGrantsCollectionActivityDocumentService = SpringContext.getBean(ContractsGrantsCollectionActivityDocumentService.class);
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
     * This method deletes an invoice from the list.
     *
     * @param mapping action mapping
     * @param form action form
     * @param request
     * @param response
     * @return action forward
     * @throws Exception
     */
    public ActionForward deleteInvoice(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContractsGrantsCollectionActivityDocumentForm colActDocForm = (ContractsGrantsCollectionActivityDocumentForm) form;
        ContractsGrantsCollectionActivityDocument colActDoc = colActDocForm.getCollectionActivityDocument();

        int indexOfLineToDelete = getLineToDelete(request);
        colActDoc.deleteInvoiceDetail(indexOfLineToDelete);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

<<<<<<< HEAD:work/src/org/kuali/kfs/module/ar/document/web/struts/CollectionActivityDocumentAction.java
=======
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

        ContractsGrantsCollectionActivityDocumentForm colActDocForm = (ContractsGrantsCollectionActivityDocumentForm) form;
        ContractsGrantsCollectionActivityDocument colActDoc = colActDocForm.getCollectionActivityDocument();

        CollectionEvent newCollectionEvent = colActDoc.getNewCollectionEvent();
        ConfigurationService kualiConfiguration = SpringContext.getBean(ConfigurationService.class);
//        if (ObjectUtils.isNull(colActDocForm.getSelectedInvoiceApplication().getCollectionEvents())) {
//            colActDocForm.getSelectedInvoiceApplication().setCollectionEvents(new ArrayList<CollectionEvent>());
//        }
        colActDoc.setProposalNumber(new Long(colActDocForm.getSelectedProposalNumber()));
        newCollectionEvent.setInvoiceNumber(colActDocForm.getSelectedInvoiceDocumentNumber());

        KualiRuleService ruleService = SpringContext.getBean(KualiRuleService.class);
        boolean rulePassed = true;

        // apply save rules for the doc
        rulePassed &= ruleService.applyRules(new SaveDocumentEvent(KFSConstants.DOCUMENT_HEADER_ERRORS, colActDoc));

        // apply rules for the new collection activity document detail
        rulePassed &= ruleService.applyRules(new AddCollectionEventEvent(ArConstants.NEW_COLLECTION_EVENT_ERROR_PATH_PREFIX, colActDoc, newCollectionEvent));

        if (rulePassed) {
            contractsGrantsCollectionActivityDocumentService.addNewCollectionEvent(kualiConfiguration.getPropertyValueAsString(ArKeyConstants.ContractsGrantsCollectionActivityDocumentConstants.CREATED_BY_DOC), colActDoc, newCollectionEvent);
            colActDoc.setNewCollectionEvent(new CollectionEvent());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

>>>>>>> KFSTP-1597: renames of collection activity stuff to contracts & grants collection activity:work/src/org/kuali/kfs/module/ar/document/web/struts/ContractsGrantsCollectionActivityDocumentAction.java
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.refresh(mapping, form, request, response);
        ContractsGrantsCollectionActivityDocumentForm contractsGrantsCollectionActivityDocumentForm = (ContractsGrantsCollectionActivityDocumentForm) form;
        ContractsGrantsCollectionActivityDocument colActDoc = contractsGrantsCollectionActivityDocumentForm.getCollectionActivityDocument();
        Collection<PersistableBusinessObject> rawValues = null;

        // If multiple asset lookup was used to select the assets, then....
        if (StringUtils.equals(KFSConstants.MULTIPLE_VALUE, contractsGrantsCollectionActivityDocumentForm.getRefreshCaller())) {
            String lookupResultsSequenceNumber = contractsGrantsCollectionActivityDocumentForm.getLookupResultsSequenceNumber();
            Set<String> selectedIds = SpringContext.getBean(SegmentedLookupResultsService.class).retrieveSetOfSelectedObjectIds(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getPerson().getPrincipalId());
            if (ObjectUtils.isNotNull(selectedIds) && CollectionUtils.isNotEmpty(selectedIds)) {
                for (String invoiceDocumentNumber: selectedIds) {
                    Map<String, String> criteria = new HashMap<String, String>();
                    criteria.put(KFSPropertyConstants.DOCUMENT_NUMBER, invoiceDocumentNumber);
                    ContractsGrantsInvoiceDocument cgInvoiceDocument = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(ContractsGrantsInvoiceDocument.class, criteria);

                    if (ObjectUtils.isNotNull(cgInvoiceDocument)) {
                        ContractsGrantsCollectionActivityInvoiceDetail invoiceDetail = new ContractsGrantsCollectionActivityInvoiceDetail();
                        invoiceDetail.setBillingPeriod(cgInvoiceDocument.getInvoiceGeneralDetail().getBillingPeriod());
                        invoiceDetail.setBillingDate(cgInvoiceDocument.getBillingDate());
                        invoiceDetail.setInvoiceNumber(cgInvoiceDocument.getDocumentNumber());
                        invoiceDetail.setDocumentNumber(colActDoc.getDocumentNumber());
                        if (!colActDoc.getInvoiceDetails().contains(invoiceDetail)) {
                            colActDoc.getInvoiceDetails().add(invoiceDetail);
                        }
                    }
                }
                colActDoc.setSelectedInvoiceDocumentNumberList(StringUtils.join(selectedIds.toArray(), ","));
            }
        }
        if (StringUtils.equals(ArConstants.AWARD_LOOKUP_IMPL, contractsGrantsCollectionActivityDocumentForm.getRefreshCaller())) {
            if (ObjectUtils.isNotNull(colActDoc.getProposalNumber())) {
                ContractsAndGrantsBillingAward award = contractsGrantsCollectionActivityDocumentService.retrieveAwardByProposalNumber(colActDoc.getProposalNumber());
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

<<<<<<< HEAD:work/src/org/kuali/kfs/module/ar/document/web/struts/CollectionActivityDocumentAction.java
=======
    public ActionForward addGlobalCollectionEvent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        MessageMap errorMap = GlobalVariables.getMessageMap();
        ContractsGrantsCollectionActivityDocumentForm colActDocForm = (ContractsGrantsCollectionActivityDocumentForm) form;
        ContractsGrantsCollectionActivityDocument colActDoc = colActDocForm.getCollectionActivityDocument();
        if (ObjectUtils.isNull(colActDoc.getSelectedInvoiceDocumentNumberList())) {
            errorMap.putError("document." + ArPropertyConstants.CollectionEventFields.SELECTED_INVOICES, ArKeyConstants.ContractsGrantsCollectionActivityDocumentErrors.ERROR_INVOICE_REQUIRED);
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
                contractsGrantsCollectionActivityDocumentService.addNewCollectionEvent(kualiConfiguration.getPropertyValueAsString(ArKeyConstants.ContractsGrantsCollectionActivityDocumentConstants.CREATED_BY_DOC), colActDoc, newGlobalCollectionEvent);
            }
        }
        colActDoc.setGlobalCollectionEvent(new CollectionEvent());
        colActDoc.setSelectedInvoiceDocumentNumberList("");
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
>>>>>>> KFSTP-1597: renames of collection activity stuff to contracts & grants collection activity:work/src/org/kuali/kfs/module/ar/document/web/struts/ContractsGrantsCollectionActivityDocumentAction.java
}

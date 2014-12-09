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
package org.kuali.kfs.module.ar.document.service.impl;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CollectionActivityInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.CollectionEvent;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.dataaccess.CollectionEventDao;
import org.kuali.kfs.module.ar.document.CollectionActivityDocument;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.service.CollectionActivityDocumentService;
import org.kuali.kfs.module.ar.document.service.InvoicePaidAppliedService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeIndexingQueue;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation class for Collection Activity Document.
 */
public class CollectionActivityDocumentServiceImpl implements CollectionActivityDocumentService {
    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CollectionActivityDocumentServiceImpl.class);

    protected DocumentService documentService;
    protected DateTimeService dateTimeService;
    protected BusinessObjectService businessObjectService;
    protected DocumentTypeService documentTypeService;
    protected CollectionEventDao collectionEventDao;
    protected InvoicePaidAppliedService invoicePaidAppliedService;
    protected KualiModuleService kualiModuleService;

    /**
     * Gets the documentService attribute.
     *
     * @return Returns the documentService.
     */
    @NonTransactional
    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * Sets the documentService attribute.
     *
     * @param documentService The documentService to set.
     */
    @NonTransactional
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    @NonTransactional
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * This method gets the business object service
     * @return the business object service
     */
    @NonTransactional
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * This method sets the business object service
     * @param businessObjectService
     */
    @NonTransactional
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    @NonTransactional
    public void setDocumentTypeService(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }

    /**
     * Sets the kualiModuleService attribute value.
     *
     * @param kualiModuleService The kualiModuleService to set.
     */
    @NonTransactional
    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CollectionActivityDocumentService#addNewEvent(java.lang.String,
     *      org.kuali.kfs.module.ar.document.CollectionActivityDocument, org.kuali.kfs.module.ar.businessobject.CollectionEvent)
     */
    @Override
    @Transactional
    public void createAndSaveCollectionEvents(CollectionActivityDocument colActDoc) {
        for (CollectionActivityInvoiceDetail invoiceDetail: colActDoc.getInvoiceDetails()) {
            CollectionEvent newCollectionEvent = new CollectionEvent();
            final Timestamp now = dateTimeService.getCurrentTimestamp();

            newCollectionEvent.setPostedDate(now);
            newCollectionEvent.setActivityCode(colActDoc.getActivityCode());
            newCollectionEvent.setActivityDate(colActDoc.getActivityDate());
            newCollectionEvent.setActivityText(colActDoc.getActivityText());
            newCollectionEvent.setFollowupDate(colActDoc.getFollowupDate());
            newCollectionEvent.setCompletedDate(colActDoc.getCompletedDate());
            newCollectionEvent.setDocumentNumber(colActDoc.getDocumentNumber());

            if (ObjectUtils.isNotNull(GlobalVariables.getUserSession()) && ObjectUtils.isNotNull(GlobalVariables.getUserSession().getPerson())) {
                Person authorUniversal = GlobalVariables.getUserSession().getPerson();
                newCollectionEvent.setUserPrincipalId(authorUniversal.getPrincipalId());
                newCollectionEvent.setUser(authorUniversal);
            }

            ContractsGrantsInvoiceDocument invoice = invoiceDetail.getInvoiceDocument();
            newCollectionEvent.setCollectionEventCode(invoice.getNextCollectionEventCode());
            newCollectionEvent.setInvoiceNumber(invoice.getDocumentNumber());
            businessObjectService.save(newCollectionEvent);
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CollectionActivityDocumentService#addNewEvent(java.lang.String,
     *      org.kuali.kfs.module.ar.document.CollectionActivityDocument, org.kuali.kfs.module.ar.businessobject.CollectionEvent)
     */
    @Override
    @Transactional
    public void addNewCollectionEvent(String description, CollectionActivityDocument colActDoc, CollectionEvent newCollectionEvent) throws WorkflowException {

        final Timestamp now = dateTimeService.getCurrentTimestamp();
        newCollectionEvent.setPostedDate(now);

        if (ObjectUtils.isNotNull(GlobalVariables.getUserSession()) && ObjectUtils.isNotNull(GlobalVariables.getUserSession().getPerson())) {
            Person authorUniversal = GlobalVariables.getUserSession().getPerson();
            newCollectionEvent.setUserPrincipalId(authorUniversal.getPrincipalId());
            newCollectionEvent.setUser(authorUniversal);
        }

        ContractsGrantsInvoiceDocument invoice = newCollectionEvent.getInvoiceDocument();
        newCollectionEvent.setDocumentNumber(colActDoc.getDocumentNumber());
        newCollectionEvent.setCollectionEventCode(invoice.getNextCollectionEventCode());

        colActDoc.getCollectionEvents().add(newCollectionEvent);

        colActDoc.prepareForSave();

        documentService.prepareWorkflowDocument(colActDoc);

        businessObjectService.save(newCollectionEvent);
        final DocumentAttributeIndexingQueue documentAttributeIndexingQueue = KewApiServiceLocator.getDocumentAttributeIndexingQueue();
        documentAttributeIndexingQueue.indexDocument(colActDoc.getDocumentNumber());

        DocumentType documentType = documentTypeService.getDocumentTypeByName(colActDoc.getFinancialDocumentTypeCode());
        DocumentAttributeIndexingQueue queue = KewApiServiceLocator.getDocumentAttributeIndexingQueue(documentType.getApplicationId());
        queue.indexDocument(colActDoc.getDocumentNumber());
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CollectionActivityDocumentService#editCollectionEvent(java.lang.String,
     *      org.kuali.kfs.module.ar.document.CollectionActivityDocument, org.kuali.kfs.module.ar.businessobject.CollectionEvent)
     */
    @Override
    @Transactional
    public void editCollectionEvent(String description, CollectionActivityDocument colActDoc, CollectionEvent event) throws WorkflowException {
        event.setDocumentNumber(colActDoc.getDocumentNumber());

        colActDoc.populateDocumentForRouting();
        colActDoc.prepareForSave();

        documentService.prepareWorkflowDocument(colActDoc);

        documentService.saveDocument(colActDoc);
        final DocumentAttributeIndexingQueue documentAttributeIndexingQueue = KewApiServiceLocator.getDocumentAttributeIndexingQueue();
        documentAttributeIndexingQueue.indexDocument(colActDoc.getDocumentNumber());

        businessObjectService.save(event);

        DocumentType documentType = documentTypeService.getDocumentTypeByName(colActDoc.getFinancialDocumentTypeCode());
        DocumentAttributeIndexingQueue queue = KewApiServiceLocator.getDocumentAttributeIndexingQueue(documentType.getApplicationId());
        queue.indexDocument(colActDoc.getDocumentNumber());

    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CollectionActivityDocumentService#editCollectionEvent(java.lang.String,
     *      org.kuali.kfs.module.ar.document.CollectionActivityDocument, org.kuali.kfs.module.ar.businessobject.CollectionEvent)
     */
    @Override
    @Transactional
    public void loadAwardInformationForCollectionActivityDocument(CollectionActivityDocument colActDoc) {

        if (ObjectUtils.isNotNull(colActDoc) && ObjectUtils.isNotNull(colActDoc.getProposalNumber())) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(ArPropertyConstants.ContractsGrantsInvoiceDocumentFields.PROPOSAL_NUMBER, colActDoc.getProposalNumber());

            List<ContractsGrantsInvoiceDocument> invoices = (List<ContractsGrantsInvoiceDocument>) businessObjectService.findMatching(ContractsGrantsInvoiceDocument.class, map);
            if (CollectionUtils.isNotEmpty(invoices)) {
                ContractsGrantsInvoiceDocument invoice = invoices.get(0);
                if (ObjectUtils.isNotNull(invoice.getInvoiceGeneralDetail().getAward()) && ObjectUtils.isNotNull(invoice.getInvoiceGeneralDetail().getAward().getAgency())) {
                    ContractsAndGrantsBillingAgency agency = invoice.getInvoiceGeneralDetail().getAward().getAgency();
                    colActDoc.setAgencyNumber(agency.getAgencyNumber());
                    colActDoc.setAgencyName(agency.getFullName());
                }
                colActDoc.setCustomerNumber(invoice.getCustomer().getCustomerNumber());
                colActDoc.setCustomerName(invoice.getCustomer().getCustomerName());
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CollectionActivityDocumentService#retrieveEvents(java.util.Map, java.lang.String)
     */
    @Override
    @Transactional
    public Collection<CollectionEvent> retrieveCollectionEvents(Map fieldValues, String documentNumberToExclude) {
        return collectionEventDao.getMatchingEventsByCollection(fieldValues, documentNumberToExclude);
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CollectionActivityDocumentService#retrieveAwardByProposalNumber(java.lang.Long)
     */
    @Override
    @Transactional
    public ContractsAndGrantsBillingAward retrieveAwardByProposalNumber(Long proposalNumber) {
        ContractsAndGrantsBillingAward award = null;
        if (ObjectUtils.isNotNull(proposalNumber)) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
            award = kualiModuleService.getResponsibleModuleService(ContractsAndGrantsBillingAward.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAward.class, map);
        }
        return award;
    }

    @Override
    @Transactional
    public java.sql.Date retrievePaymentDateByDocumentNumber(String documentNumber) {
        List<InvoicePaidApplied> invoicePaidApplieds = (List<InvoicePaidApplied>) getInvoicePaidAppliedService().getInvoicePaidAppliedsForInvoice(documentNumber);
        java.sql.Date paymentDate = null;
        if (invoicePaidApplieds != null && !invoicePaidApplieds.isEmpty()) {
            InvoicePaidApplied invPaidApp = invoicePaidApplieds.get(invoicePaidApplieds.size() - 1);
            PaymentApplicationDocument referenceFinancialDocument;
            try {
                referenceFinancialDocument = (PaymentApplicationDocument) documentService.getByDocumentHeaderId(invPaidApp.getDocumentNumber());
                paymentDate = referenceFinancialDocument.getFinancialSystemDocumentHeader().getDocumentFinalDate();
            }
            catch (WorkflowException ex) {
                LOG.error("Could not retrieve payment application document while calculating payment date: " + ex.getMessage());
                throw new RuntimeException("Could not retrieve payment application document while calculating payment date", ex);
            }
        }
        return paymentDate;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CollectionActivityDocumentService#retrievePaymentAmountByDocumentNumber(java.lang.String)
     */
    @Override
    @Transactional
    public KualiDecimal retrievePaymentAmountByDocumentNumber(String documentNumber) {
        KualiDecimal paymentAmount = KualiDecimal.ZERO;
        Collection<InvoicePaidApplied> invoicePaidApplieds = invoicePaidAppliedService.getInvoicePaidAppliedsForInvoice(documentNumber);
        if (invoicePaidApplieds != null && !invoicePaidApplieds.isEmpty()) {
            for (InvoicePaidApplied invPaidApp : invoicePaidApplieds) {
                paymentAmount = paymentAmount.add(invPaidApp.getInvoiceItemAppliedAmount());
            }
        }
        return paymentAmount;
    }

    @NonTransactional
    public CollectionEventDao getCollectionEventDao() {
        return collectionEventDao;
    }

    @NonTransactional
    public void setCollectionEventDao(CollectionEventDao collectionEventDao) {
        this.collectionEventDao = collectionEventDao;
    }

    @NonTransactional
    public InvoicePaidAppliedService getInvoicePaidAppliedService() {
        return invoicePaidAppliedService;
    }

    @NonTransactional
    public void setInvoicePaidAppliedService(InvoicePaidAppliedService invoicePaidAppliedService) {
        this.invoicePaidAppliedService = invoicePaidAppliedService;
    }
}

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
import org.kuali.kfs.module.ar.businessobject.Event;
import org.kuali.kfs.module.ar.dataaccess.EventDao;
import org.kuali.kfs.module.ar.document.CollectionActivityDocument;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CollectionActivityDocumentService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.api.KewApiConstants;
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

/**
 * Implementation class for Collection Activity Document.
 */
public class CollectionActivityDocumentServiceImpl implements CollectionActivityDocumentService {

    protected DocumentService documentService;
    protected DateTimeService dateTimeService;
    protected BusinessObjectService businessObjectService;
    protected DocumentTypeService documentTypeService;
    protected EventDao eventDao;
    protected KualiModuleService kualiModuleService;

    /**
     * Gets the documentService attribute.
     *
     * @return Returns the documentService.
     */
    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * Sets the documentService attribute.
     *
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * This method gets the business object service
     * @return the business object service
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * This method sets the business object service
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

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
     *      org.kuali.kfs.module.ar.document.CollectionActivityDocument, org.kuali.kfs.module.ar.businessobject.Event)
     */
    @Override
    public void addNewEvent(String description, CollectionActivityDocument colActDoc, Event newEvent) throws WorkflowException {

        final Timestamp now = dateTimeService.getCurrentTimestamp();
        newEvent.setPostedDate(now);

        if (ObjectUtils.isNotNull(GlobalVariables.getUserSession()) && ObjectUtils.isNotNull(GlobalVariables.getUserSession().getPerson())) {
            Person authorUniversal = GlobalVariables.getUserSession().getPerson();
            newEvent.setUserPrincipalId(authorUniversal.getPrincipalId());
            newEvent.setUser(authorUniversal);
        }

        newEvent.setEventRouteStatus(KewApiConstants.ROUTE_HEADER_SAVED_CD);
        newEvent.setDocumentNumber(colActDoc.getDocumentNumber());

        colActDoc.getEvents().add(newEvent);

        colActDoc.prepareForSave();

        documentService.prepareWorkflowDocument(colActDoc);

        businessObjectService.save(newEvent);
        final DocumentAttributeIndexingQueue documentAttributeIndexingQueue = KewApiServiceLocator.getDocumentAttributeIndexingQueue();
        documentAttributeIndexingQueue.indexDocument(colActDoc.getDocumentNumber());

      DocumentType documentType = documentTypeService.getDocumentTypeByName(colActDoc.getFinancialDocumentTypeCode());
      DocumentAttributeIndexingQueue queue = KewApiServiceLocator.getDocumentAttributeIndexingQueue(documentType.getApplicationId());
      queue.indexDocument(colActDoc.getDocumentNumber());
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CollectionActivityDocumentService#editEvent(java.lang.String,
     *      org.kuali.kfs.module.ar.document.CollectionActivityDocument, org.kuali.kfs.module.ar.businessobject.Event)
     */
    @Override
    public void editEvent(String description, CollectionActivityDocument colActDoc, Event event) throws WorkflowException {
        event.setEventRouteStatus(KewApiConstants.ROUTE_HEADER_SAVED_CD);
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
     * @see org.kuali.kfs.module.ar.document.service.CollectionActivityDocumentService#editEvent(java.lang.String,
     *      org.kuali.kfs.module.ar.document.CollectionActivityDocument, org.kuali.kfs.module.ar.businessobject.Event)
     */
    @Override
    public void loadAwardInformationForCollectionActivityDocument(CollectionActivityDocument colActDoc) {

        if (ObjectUtils.isNotNull(colActDoc) && ObjectUtils.isNotNull(colActDoc.getProposalNumber())) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(ArPropertyConstants.TicklersReportFields.PROPOSAL_NUMBER, colActDoc.getProposalNumber());

            List<ContractsGrantsInvoiceDocument> invoices = (List<ContractsGrantsInvoiceDocument>) businessObjectService.findMatching(ContractsGrantsInvoiceDocument.class, map);
            if (CollectionUtils.isNotEmpty(invoices)) {
                ContractsGrantsInvoiceDocument invoice = invoices.get(0);
                if (ObjectUtils.isNotNull(invoice.getAward()) && ObjectUtils.isNotNull(invoice.getAward().getAgency())) {
                    ContractsAndGrantsBillingAgency agency = invoice.getAward().getAgency();
                    colActDoc.setAgencyNumber(agency.getAgencyNumber());
                    colActDoc.setAgencyName(agency.getFullName());
                }
                colActDoc.setCustomerNumber(invoice.getCustomer().getCustomerNumber());
                colActDoc.setCustomerName(invoice.getCustomer().getCustomerName());
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CollectionActivityDocumentService#editEvent(java.lang.String,
     *      org.kuali.kfs.module.ar.document.CollectionActivityDocument, org.kuali.kfs.module.ar.businessobject.Event)
     */
    @Override
    public Collection<Event> retrieveEvents(Map fieldValues, boolean isSavedRouteStatus, String documentNumberToExclude) {
        return eventDao.getMatchingEventsByCollection(fieldValues, isSavedRouteStatus, documentNumberToExclude);
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CollectionActivityDocumentService#retrieveAwardByProposalNumber(java.lang.Long)
     */
    @Override
    public ContractsAndGrantsBillingAward retrieveAwardByProposalNumber(Long proposalNumber) {
        ContractsAndGrantsBillingAward award = null;
        if (ObjectUtils.isNotNull(proposalNumber)) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(ArPropertyConstants.TicklersReportFields.PROPOSAL_NUMBER, proposalNumber);
            award = kualiModuleService.getResponsibleModuleService(ContractsAndGrantsBillingAward.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAward.class, map);
        }
        return award;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CollectionActivityDocumentService#validateInvoiceForSavedEvents(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public boolean validateInvoiceForSavedEvents(String invoiceNumber, String documentNumber) {
        boolean resultInd = true;
        Map<String,String> fieldValues = new HashMap<String,String>();
        fieldValues.put(ArPropertyConstants.EventFields.INVOICE_NUMBER, invoiceNumber);

        List<Event> events = (List<Event>) this.retrieveEvents(fieldValues, true, documentNumber);
        if (CollectionUtils.isNotEmpty(events)) {
            resultInd = false;
        }
        return resultInd;
    }

    public EventDao getEventDao() {
        return eventDao;
    }

    public void setEventDao(EventDao eventDao) {
        this.eventDao = eventDao;
    }
}

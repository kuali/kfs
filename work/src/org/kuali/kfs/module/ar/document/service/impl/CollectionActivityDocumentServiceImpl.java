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

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.ojb.broker.query.Criteria;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.Event;
import org.kuali.kfs.module.ar.dataaccess.EventDao;
import org.kuali.kfs.module.ar.document.CollectionActivityDocument;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CollectionActivityDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeIndexingQueue;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.kfs.sys.context.SpringContext; import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.edl.impl.components.WorkflowDocumentActions;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Implementation class for Collection Activity Document.
 */
public class CollectionActivityDocumentServiceImpl implements CollectionActivityDocumentService {

    private DocumentService documentService;

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

    /**
     * @see org.kuali.kfs.module.ar.document.service.CollectionActivityDocumentService#addNewEvent(java.lang.String,
     *      org.kuali.kfs.module.ar.document.CollectionActivityDocument, org.kuali.kfs.module.ar.businessobject.Event)
     */
    @Override
    public void addNewEvent(String description, CollectionActivityDocument colActDoc, Event newEvent) throws WorkflowException {

        final Date now = SpringContext.getBean(DateTimeService.class).getCurrentDate();
        newEvent.setPostedDate(now);

        if (ObjectUtils.isNotNull(GlobalVariables.getUserSession()) && ObjectUtils.isNotNull(GlobalVariables.getUserSession().getPerson())) {
            Person authorUniversal = GlobalVariables.getUserSession().getPerson();
            newEvent.setUserPrincipalId(authorUniversal.getPrincipalId());
            newEvent.setUser(authorUniversal);
        }

        newEvent.setEventRouteStatus(KewApiConstants.ROUTE_HEADER_SAVED_CD);
        newEvent.setDocumentNumber(colActDoc.getDocumentNumber());

        colActDoc.getEvents().add(newEvent);

        colActDoc.populateDocumentForRouting();
        colActDoc.prepareForSave();

        documentService.prepareWorkflowDocument(colActDoc);

        documentService.saveDocument(colActDoc);
        SpringContext.getBean(BusinessObjectService.class).save(newEvent);
        final DocumentAttributeIndexingQueue documentAttributeIndexingQueue = KewApiServiceLocator.getDocumentAttributeIndexingQueue();
        documentAttributeIndexingQueue.indexDocument(colActDoc.getDocumentNumber());

//        final WorkflowDocumentActions workflowDocumentActions = SpringContext.getBean(WorkflowDocumentActions.class);
//        workflowDocumentActions.indexDocument(new Long(colActDoc.getDocumentNumber()));

//      DocumentTypeService documentTypeService = SpringContext.getBean(DocumentTypeService.class);
//      DocumentType documentType = documentTypeService.getDocumentTypeByName(colActDoc.getFinancialDocumentTypeCode());
//      DocumentAttributeIndexingQueue queue = KewApiServiceLocator.getDocumentAttributeIndexingQueue(documentType.getApplicationId());
//      queue.indexDocument(colActDoc.getDocumentNumber());
//        final SearchableAttributeProcessingService searchableAttributeProcessingService = SpringContext.getBean(SearchableAttributeProcessingService.class);
//        searchableAttributeProcessingService.indexDocument(new Long(colActDoc.getDocumentNumber()));
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
//        final WorkflowDocumentActions workflowDocumentActions = SpringContext.getBean(WorkflowDocumentActions.class);
//        workflowDocumentActions.indexDocument(new Long(colActDoc.getDocumentNumber()));
        final DocumentAttributeIndexingQueue documentAttributeIndexingQueue = KewApiServiceLocator.getDocumentAttributeIndexingQueue();
        documentAttributeIndexingQueue.indexDocument(colActDoc.getDocumentNumber());

        SpringContext.getBean(BusinessObjectService.class).save(event);

//        DocumentTypeService documentTypeService = SpringContext.getBean(DocumentTypeService.class);
//        DocumentType documentType = documentTypeService.getDocumentTypeByName(colActDoc.getFinancialDocumentTypeCode());
//        DocumentAttributeIndexingQueue queue = KewApiServiceLocator.getDocumentAttributeIndexingQueue(documentType.getApplicationId());
//        queue.indexDocument(colActDoc.getDocumentNumber());

//        final SearchableAttributeProcessingService searchableAttributeProcessingService = SpringContext.getBean(SearchableAttributeProcessingService.class);
//        searchableAttributeProcessingService.indexDocument(new Long(colActDoc.getDocumentNumber()));
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

            List<ContractsGrantsInvoiceDocument> invoices = (List<ContractsGrantsInvoiceDocument>) SpringContext.getBean(BusinessObjectService.class).findMatching(ContractsGrantsInvoiceDocument.class, map);
            if (ObjectUtils.isNotNull(invoices) && CollectionUtils.isNotEmpty(invoices)) {
                ContractsGrantsInvoiceDocument invoice = invoices.get(0);
                if (ObjectUtils.isNotNull(invoice.getAward()) && ObjectUtils.isNotNull(invoice.getAward().getAgency())) {
                    ContractsAndGrantsCGBAgency agency = invoice.getAward().getAgency();
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
    public Collection<Event> retrieveEventsByCriteria(Criteria criteria) {
        EventDao eventDao = SpringContext.getBean(EventDao.class);
        return eventDao.getEventsByCriteria(criteria);
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CollectionActivityDocumentService#retrieveAwardByProposalNumber(java.lang.Long)
     */
    @Override
    public ContractsAndGrantsCGBAward retrieveAwardByProposalNumber(Long proposalNumber) {
        ContractsAndGrantsCGBAward award = null;
        if (ObjectUtils.isNotNull(proposalNumber)) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(ArPropertyConstants.TicklersReportFields.PROPOSAL_NUMBER, proposalNumber);
            award = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsCGBAward.class).getExternalizableBusinessObject(ContractsAndGrantsCGBAward.class, map);
        }
        return award;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CollectionActivityDocumentService#validateInvoiceForSavedEvents(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public boolean validateInvoiceForSavedEvents(String invoiceNumber, String documentNumber) {
        boolean result = true;
        Criteria criteria = new Criteria();

        criteria.addEqualTo(ArPropertyConstants.EventFields.INVOICE_NUMBER, invoiceNumber);
        criteria.addEqualTo(ArPropertyConstants.EventFields.EVENT_ROUTE_STATUS, KewApiConstants.ROUTE_HEADER_SAVED_CD);
        criteria.addNotEqualTo(ArPropertyConstants.EventFields.DOCUMENT_NUMBER, documentNumber);
        List<Event> events = (List<Event>) this.retrieveEventsByCriteria(criteria);
        if (CollectionUtils.isNotEmpty(events)) {
            result = false;
        }
        return result;
    }
}

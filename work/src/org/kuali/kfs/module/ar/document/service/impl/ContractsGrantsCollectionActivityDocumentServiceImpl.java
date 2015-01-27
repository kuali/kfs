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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.businessobject.CollectionEvent;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsCollectionActivityInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.document.ContractsGrantsCollectionActivityDocument;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.dataaccess.ContractsGrantsInvoiceDocumentDao;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsCollectionActivityDocumentService;
import org.kuali.kfs.module.ar.document.service.InvoicePaidAppliedService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.NonTransactional;

/**
 * Implementation class for Collection Activity Document.
 */
@Transactional
public class ContractsGrantsCollectionActivityDocumentServiceImpl implements ContractsGrantsCollectionActivityDocumentService {
    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsCollectionActivityDocumentServiceImpl.class);

    protected ContractsGrantsInvoiceDocumentDao contractsGrantsInvoiceDocumentDao;
    protected DocumentService documentService;
    protected DateTimeService dateTimeService;
    protected BusinessObjectService businessObjectService;
    protected DocumentTypeService documentTypeService;
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
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsCollectionActivityDocumentService#addNewEvent(java.lang.String,
     *      org.kuali.kfs.module.ar.document.ContractsGrantsCollectionActivityDocument, org.kuali.kfs.module.ar.businessobject.CollectionEvent)
     */
    @Override
    @Transactional
    public void createAndSaveCollectionEvents(ContractsGrantsCollectionActivityDocument colActDoc) {
        for (ContractsGrantsCollectionActivityInvoiceDetail invoiceDetail: colActDoc.getInvoiceDetails()) {
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
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsCollectionActivityDocumentService#retrieveAwardByProposalNumber(java.lang.Long)
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

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsCollectionActivityDocumentService#retrievePaymentAmountByDocumentNumber(java.lang.String)
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

    /**
     * This method retrieves all collection activity eligible Contracts & Grants Invoices associated with the given proposal number. All
     * Contracts & Grants Invoices retrieved will meet the following criteria:
     * <ul>
     * <li>Must not be fully paid</li>
     * <li>Must not error correct another CINV</li>
     * <li>Must not be error corrected by another CINV</li>
     * <li>Must be final or processed</li>
     * </ul>
     *
     * @param proposalNumber
     * @return a Collection of collection activity eligible Contracts & Grants Invoices associated with the given proposal number
     */
    @Override
    public Collection<ContractsGrantsInvoiceDocument> retrieveCollectionActivityEligibleContractsGrantsInvoicesByProposalNumber(Long proposalNumber) {
        Collection<ContractsGrantsInvoiceDocument> cgInvoices = contractsGrantsInvoiceDocumentDao.getCollectionEligibleContractsGrantsInvoicesByProposalNumber(proposalNumber);
        if (CollectionUtils.isEmpty(cgInvoices)) {
            return cgInvoices;
        }
        Collection<ContractsGrantsInvoiceDocument> filteredInvoices = new ArrayList<ContractsGrantsInvoiceDocument>();
        for (ContractsGrantsInvoiceDocument invoice : cgInvoices) {
            if (!isFullyPaid(invoice)) {
                filteredInvoices.add(invoice);
            }
        }
        return filteredInvoices;
    }

    /**
     * Determines if a Contracts & Grants Invoice is fully paid or not
     * @param contractsGrantsInvoiceDocument the Contracts & Grants Invoice to check
     * @return true if the document is fully paid, false otherwise
     */
    protected boolean isFullyPaid(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        final KualiDecimal openAmount = contractsGrantsInvoiceDocument.getOpenAmount();
        return !ObjectUtils.isNull(openAmount) && openAmount.equals(KualiDecimal.ZERO);
    }

    @NonTransactional
    public InvoicePaidAppliedService getInvoicePaidAppliedService() {
        return invoicePaidAppliedService;
    }

    @NonTransactional
    public void setInvoicePaidAppliedService(InvoicePaidAppliedService invoicePaidAppliedService) {
        this.invoicePaidAppliedService = invoicePaidAppliedService;
    }

    public ContractsGrantsInvoiceDocumentDao getContractsGrantsInvoiceDocumentDao() {
        return contractsGrantsInvoiceDocumentDao;
    }

    public void setContractsGrantsInvoiceDocumentDao(ContractsGrantsInvoiceDocumentDao contractsGrantsInvoiceDocumentDao) {
        this.contractsGrantsInvoiceDocumentDao = contractsGrantsInvoiceDocumentDao;
    }
}

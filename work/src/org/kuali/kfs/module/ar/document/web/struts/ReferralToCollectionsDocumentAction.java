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
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.ReferralToCollectionsDocument;
import org.kuali.kfs.module.ar.document.service.CollectionActivityDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.lookup.LookupResultsService;
import org.kuali.rice.kns.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Action class for Referral To Collections Document.
 */
public class ReferralToCollectionsDocumentAction extends KualiTransactionalDocumentActionBase {
    private volatile static LookupResultsService lookupResultsService;
    private volatile static CollectionActivityDocumentService collectionActivityDocumentService;

    /**
     * Do initialization for a new Referral to Collections Document.
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        this.showInvoices((ReferralToCollectionsDocumentForm) kualiDocumentFormBase);
    }

    /**
     * This method is used to show invoices.
     *
     * @param referralToCollectionsDocumentForm
     */
    public void showInvoices(ReferralToCollectionsDocumentForm referralToCollectionsDocumentForm) {
        ReferralToCollectionsDocument referralToCollectionsDocument = referralToCollectionsDocumentForm.getReferralToCollectionsDocument();

        String lookupResultsSequenceNumber = referralToCollectionsDocumentForm.getLookupResultsSequenceNumber();
        if (StringUtils.isNotBlank(lookupResultsSequenceNumber)) {
            String personId = GlobalVariables.getUserSession().getPerson().getPrincipalId();
            setReferralToCollectionsDetailsFromLookupResultsSequenceNumber(referralToCollectionsDocument, lookupResultsSequenceNumber, personId);
        }
    }

    /**
     * Deletes the invoice from document list.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteInvoice(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ReferralToCollectionsDocumentForm referralToCollectionsDocumentForm = (ReferralToCollectionsDocumentForm) form;
        ReferralToCollectionsDocument referralToCollectionsDocument = referralToCollectionsDocumentForm.getReferralToCollectionsDocument();

        int indexOfLineToDelete = getLineToDelete(request);
        ReferralToCollectionsDetail referralToCollectionsDetail = referralToCollectionsDocument.getReferralToCollectionsDetail(indexOfLineToDelete);
        referralToCollectionsDocument.deleteReferralToCollectionsDetail(indexOfLineToDelete);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Sets the referraltoCollection document detail object collection in form.
     *
     * @param rcForm The ReferralToCollectionsDocumentForm object in which collection to set.
     * @param lookupResultsSequenceNumber The sequence number of result.
     * @param personId The id of logged in person.
     */
    protected void setReferralToCollectionsDetailsFromLookupResultsSequenceNumber(ReferralToCollectionsDocument rcDoc, String lookupResultsSequenceNumber, String personId) {
        Collection<ContractsGrantsInvoiceDocument> invoices = getCGInvoiceDocumentsFromLookupResultsSequenceNumber(lookupResultsSequenceNumber, personId);
        if (ObjectUtils.isNotNull(invoices) && CollectionUtils.isNotEmpty(invoices)) {
            ContractsGrantsInvoiceDocument selectedInvoice = invoices.iterator().next();
            ContractsAndGrantsBillingAward award = selectedInvoice.getAward();
            rcDoc.setAgencyNumber(award.getAgencyNumber());
            rcDoc.setAgencyFullName(award.getAgency().getFullName());
            rcDoc.setCustomerNumber(selectedInvoice.getCustomer() != null ? selectedInvoice.getCustomer().getCustomerNumber() : award.getAgency().getCustomerNumber());
            rcDoc.setCustomerTypeCode(award.getAgency().getCustomerTypeCode());
            Customer customer = SpringContext.getBean(CustomerService.class).getByPrimaryKey(rcDoc.getCustomerNumber());
            if (customer != null)
            {
                rcDoc.setCustomerName(customer.getCustomerName());
                rcDoc.setCustomerTypeCode(customer.getCustomerTypeCode());
            }
            rcDoc.setReferralToCollectionsDetails(getPopulatedReferralToCollectionsDetails(rcDoc, invoices));
        }
    }

    /**
     * Gets the invoice documents from sequence number.
     * @param lookupResultsSequenceNumber The sequence number of search result.
     * @param personId The principal id of the person who searched.
     * @return Returns the list of invoice documents.
     */
    protected Collection<ContractsGrantsInvoiceDocument> getCGInvoiceDocumentsFromLookupResultsSequenceNumber(String lookupResultsSequenceNumber, String personId) {
        Collection<ContractsGrantsInvoiceDocument> invoiceDocuments = new ArrayList<ContractsGrantsInvoiceDocument>();
        try {
            for (PersistableBusinessObject obj : getLookupResultsService().retrieveSelectedResultBOs(lookupResultsSequenceNumber, ContractsGrantsInvoiceDocument.class, personId)) {
                invoiceDocuments.add((ContractsGrantsInvoiceDocument) obj);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return invoiceDocuments;
    }

    /**
     * Gets the Referral To Collections Detail values from invoices list.
     *
     * @param rcDoc The ReferralToCollections object.
     * @param invoices The list of invoices to populate.
     * @return Returns the list of ReferralToCollectionsDetail objects.
     */
    protected List<ReferralToCollectionsDetail> getPopulatedReferralToCollectionsDetails(ReferralToCollectionsDocument rcDoc, Collection<ContractsGrantsInvoiceDocument> invoices) {
        List<ReferralToCollectionsDetail> populatedReferralToCollectionsDetails = new ArrayList<ReferralToCollectionsDetail>();

        if (CollectionUtils.isEmpty(invoices)) {
            return populatedReferralToCollectionsDetails;
        }

        for (ContractsGrantsInvoiceDocument invoice : invoices) {
            ReferralToCollectionsDetail rcDetail = new ReferralToCollectionsDetail();
            ContractsAndGrantsBillingAward award = invoice.getAward();
            ContractsAndGrantsBillingAgency agency = award.getAgency();

            // Get data from first award for agency data
            rcDoc.setReferralTypeCode(invoice.getReferralTypeCode());
            rcDetail.setDocumentNumber(rcDoc.getDocumentNumber());
            rcDetail.setAgencyNumber(agency.getAgencyNumber());
            rcDetail.setProposalNumber(award.getProposalNumber());
            rcDetail.setAge(invoice.getAge());
            rcDetail.setInvoiceNumber(invoice.getDocumentNumber());
            rcDetail.setProposalNumber(invoice.getProposalNumber());
            rcDetail.setChart(invoice.getBillByChartOfAccountCode());
            rcDetail.setInvoiceTotal(invoice.getSourceTotal());
            rcDetail.setBillingDate(invoice.getBillingDate());
            rcDetail.setFinalDispositionCode(invoice.getFinalDispositionCode());

            KualiDecimal paymentAmount = getCollectionActivityDocumentService().retrievePaymentAmountByDocumentNumber(invoice.getDocumentNumber());
            rcDetail.setInvoiceBalance(invoice.getSourceTotal().subtract(paymentAmount));

            List<InvoiceAccountDetail> invAccDets = invoice.getAccountDetails();
            if (CollectionUtils.isNotEmpty(invAccDets)) {
                rcDetail.setAccountNumber(invAccDets.get(0).getAccountNumber());
            }

            rcDetail.setInvoiceDocument(invoice);
            populatedReferralToCollectionsDetails.add(rcDetail);
        }

        return populatedReferralToCollectionsDetails;
    }

    public static LookupResultsService getLookupResultsService() {
        if (lookupResultsService == null) {
            lookupResultsService = SpringContext.getBean(LookupResultsService.class);
        }
        return lookupResultsService;
    }

    public static CollectionActivityDocumentService getCollectionActivityDocumentService() {
        if (collectionActivityDocumentService == null) {
            collectionActivityDocumentService = SpringContext.getBean(CollectionActivityDocumentService.class);
        }
        return collectionActivityDocumentService;
    }
}
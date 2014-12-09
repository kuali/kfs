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
package org.kuali.kfs.module.ar.report.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceGeneralDetail;
import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsDetail;
import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsLookupResult;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.ReferralToCollectionsDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsCollectionActivityDocumentService;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.module.ar.report.service.ReferralToCollectionsService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Default implementation of the ReferralToCollectionsService
 */
public class ReferralToCollectionsServiceImpl implements ReferralToCollectionsService {
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected CustomerService customerService;
    protected ContractsGrantsCollectionActivityDocumentService contractsGrantsCollectionActivityDocumentService;

    /**
     * @see org.kuali.kfs.module.ar.report.service.ReferralToCollectionsService#getPopulatedReferralToCollectionsLookupResults(java.util.Collection)
     */
    @Override
    public Collection<ReferralToCollectionsLookupResult> getPopulatedReferralToCollectionsLookupResults(Collection<ContractsGrantsInvoiceDocument> invoices) {
        Collection<ReferralToCollectionsLookupResult> populatedReferralToCollectionsLookupResults = new ArrayList<ReferralToCollectionsLookupResult>();

        if (ObjectUtils.isNull(invoices) || CollectionUtils.isEmpty(invoices)) {
            return populatedReferralToCollectionsLookupResults;
        }

        Iterator iter = getContractsGrantsInvoiceDocumentService().getInvoicesByAward(invoices).entrySet().iterator();
        ReferralToCollectionsLookupResult referralToCollectionsLookupResult = null;
        while (iter.hasNext()) {

            Map.Entry entry = (Map.Entry) iter.next();
            List<ContractsGrantsInvoiceDocument> list = (List<ContractsGrantsInvoiceDocument>) entry.getValue();

            if (CollectionUtils.isNotEmpty(list)){
                // Get data from first award for agency data
                ContractsGrantsInvoiceDocument invoice = list.get(0);
                ContractsAndGrantsBillingAward award = invoice.getInvoiceGeneralDetail().getAward();

                if (ObjectUtils.isNotNull(award)) {
                    ContractsAndGrantsBillingAgency agency = award.getAgency();
                    referralToCollectionsLookupResult = new ReferralToCollectionsLookupResult();
                    referralToCollectionsLookupResult.setAgencyNumber(agency.getAgencyNumber());
                    referralToCollectionsLookupResult.setCustomerNumber(ObjectUtils.isNotNull(invoice.getCustomer()) ? invoice.getCustomer().getCustomerNumber() : agency.getCustomerNumber());
                    referralToCollectionsLookupResult.setProposalNumber(award.getProposalNumber());
                    referralToCollectionsLookupResult.setAwardBeginningDate(award.getAwardBeginningDate());
                    referralToCollectionsLookupResult.setAwardEndingDate(award.getAwardEndingDate());
                    if (CollectionUtils.isNotEmpty(invoice.getAccountDetails())) {
                        referralToCollectionsLookupResult.setAccountNumber(invoice.getAccountDetails().get(0).getAccountNumber());
                    }
                    referralToCollectionsLookupResult.setAwardTotal(award.getAwardTotalAmount());
                    referralToCollectionsLookupResult.setInvoices(list);

                    populatedReferralToCollectionsLookupResults.add(referralToCollectionsLookupResult);
                }
            }
        }

        return populatedReferralToCollectionsLookupResults;
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.ReferralToCollectionsService#populateReferralToCollectionsDocumentWithInvoices(org.kuali.kfs.module.ar.document.ReferralToCollectionsDocument, java.util.Collection)
     */
    @Override
    public void populateReferralToCollectionsDocumentWithInvoices(ReferralToCollectionsDocument rcDoc, Collection<ContractsGrantsInvoiceDocument> invoices) {
        if (ObjectUtils.isNotNull(invoices) && CollectionUtils.isNotEmpty(invoices)) {
            ContractsGrantsInvoiceDocument selectedInvoice = invoices.iterator().next();
            ContractsAndGrantsBillingAward award = selectedInvoice.getInvoiceGeneralDetail().getAward();
            rcDoc.setAgencyNumber(award.getAgencyNumber());
            rcDoc.setAgencyFullName(award.getAgency().getFullName());
            rcDoc.setCustomerNumber(selectedInvoice.getCustomer() != null ? selectedInvoice.getCustomer().getCustomerNumber() : award.getAgency().getCustomerNumber());
            rcDoc.setCustomerTypeCode(award.getAgency().getCustomerTypeCode());
            rcDoc.setReferralTypeCode(selectedInvoice.getInvoiceGeneralDetail().getReferralTypeCode());
            Customer customer = getCustomerService().getByPrimaryKey(rcDoc.getCustomerNumber());
            if (customer != null)
            {
                rcDoc.setCustomerName(customer.getCustomerName());
                rcDoc.setCustomerTypeCode(customer.getCustomerTypeCode());
            }
            rcDoc.setReferralToCollectionsDetails(getPopulatedReferralToCollectionsDetails(rcDoc, invoices));
        }
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
            InvoiceGeneralDetail invoiceGeneralDetail = invoice.getInvoiceGeneralDetail();
            ContractsAndGrantsBillingAward award = invoiceGeneralDetail.getAward();
            ContractsAndGrantsBillingAgency agency = award.getAgency();

            // Get data from first award for agency data
            rcDetail.setDocumentNumber(rcDoc.getDocumentNumber());
            rcDetail.setAgencyNumber(agency.getAgencyNumber());
            rcDetail.setProposalNumber(award.getProposalNumber());
            rcDetail.setAge(invoice.getAge());
            rcDetail.setInvoiceNumber(invoice.getDocumentNumber());
            rcDetail.setProposalNumber(invoiceGeneralDetail.getProposalNumber());
            rcDetail.setChart(invoice.getBillByChartOfAccountCode());
            rcDetail.setInvoiceTotal(invoice.getSourceTotal());
            rcDetail.setBillingDate(invoice.getBillingDate());
            rcDetail.setFinalDispositionCode(invoiceGeneralDetail.getFinalDispositionCode());

            KualiDecimal paymentAmount = getContractsGrantsCollectionActivityDocumentService().retrievePaymentAmountByDocumentNumber(invoice.getDocumentNumber());
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

    public ContractsGrantsInvoiceDocumentService getContractsGrantsInvoiceDocumentService() {
        return contractsGrantsInvoiceDocumentService;
    }

    public void setContractsGrantsInvoiceDocumentService(ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService) {
        this.contractsGrantsInvoiceDocumentService = contractsGrantsInvoiceDocumentService;
    }

    public CustomerService getCustomerService() {
        return customerService;
    }

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    public ContractsGrantsCollectionActivityDocumentService getContractsGrantsCollectionActivityDocumentService() {
        return contractsGrantsCollectionActivityDocumentService;
    }

    public void setContractsGrantsCollectionActivityDocumentService(ContractsGrantsCollectionActivityDocumentService contractsGrantsCollectionActivityDocumentService) {
        this.contractsGrantsCollectionActivityDocumentService = contractsGrantsCollectionActivityDocumentService;
    }
}

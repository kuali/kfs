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
package org.kuali.kfs.module.ar.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsDetail;
import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsLookupResult;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.ReferralToCollectionsDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.lookup.LookupResultsService;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Utility class for Referral To Collections lookup.
 */
public class ReferralToCollectionsDocumentUtil {

    /**
     * This helper method returns a list of award lookup results based on the referral to collections lookup
     *
     * @param invoices The list of invoices.
     * @return Returns the list of ROC lookup result object.
     */
    public static Collection<ReferralToCollectionsLookupResult> getPopulatedReferralToCollectionsLookupResults(Collection<ContractsGrantsInvoiceDocument> invoices) {
        Collection<ReferralToCollectionsLookupResult> populatedReferralToCollectionsLookupResults = new ArrayList<ReferralToCollectionsLookupResult>();

        if (ObjectUtils.isNull(invoices) || CollectionUtils.isEmpty(invoices)) {
            return populatedReferralToCollectionsLookupResults;
        }

        Iterator iter = getInvoicesByAward(invoices).entrySet().iterator();
        ReferralToCollectionsLookupResult referralToCollectionsLookupResult = null;
        while (iter.hasNext()) {

            Map.Entry entry = (Map.Entry) iter.next();
            List<ContractsGrantsInvoiceDocument> list = (List<ContractsGrantsInvoiceDocument>) entry.getValue();

            if (CollectionUtils.isNotEmpty(list)){
                // Get data from first award for agency data
                ContractsGrantsInvoiceDocument invoice = list.get(0);
                ContractsAndGrantsBillingAward award = invoice.getAward();

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
     * Gets the Referral To Collections Detail values from invoices list.
     *
     * @param rcDoc The ReferralToCollections object.
     * @param invoices The list of invoices to populate.
     * @return Returns the list of ReferralToCollectionsDetail objects.
     */
    public static List<ReferralToCollectionsDetail> getPopulatedReferralToCollectionsDetails(ReferralToCollectionsDocument rcDoc, Collection<ContractsGrantsInvoiceDocument> invoices) {
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

            ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);

            KualiDecimal paymentAmount = contractsGrantsInvoiceDocumentService.retrievePaymentAmountByDocumentNumber(invoice.getDocumentNumber());
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

    /**
     * This helper method returns a map of a list of awards by agency
     *
     * @param invoices The list of invoices for which filtering to be done by award.
     * @return Returns the map of invoices based on key of proposal number.
     */
    public static Map<Long, List<ContractsGrantsInvoiceDocument>> getInvoicesByAward(Collection<ContractsGrantsInvoiceDocument> invoices) {
        // use a map to sort awards by agency
        Map<Long, List<ContractsGrantsInvoiceDocument>> invoicesByAward = new HashMap<Long, List<ContractsGrantsInvoiceDocument>>();
        for (ContractsGrantsInvoiceDocument invoice : invoices) {

            Long proposalNumber = invoice.getProposalNumber();
            if (invoicesByAward.containsKey(proposalNumber)) {
                invoicesByAward.get(proposalNumber).add(invoice);
            }
            else {
                List<ContractsGrantsInvoiceDocument> invoicesByProposalNumber = new ArrayList<ContractsGrantsInvoiceDocument>();
                invoicesByProposalNumber.add(invoice);
                invoicesByAward.put(proposalNumber, invoicesByProposalNumber);
            }

        }

        return invoicesByAward;
    }

    /**
     * Gets the invoice documents from sequence number.
     *
     * @param lookupResultsSequenceNumber The sequence number of search result.
     * @param personId The principal id of the person who searched.
     * @return Returns the list of invoice documents.
     */
    public static Collection<ContractsGrantsInvoiceDocument> getCGInvoiceDocumentsFromLookupResultsSequenceNumber(String lookupResultsSequenceNumber, String personId) {
        Collection<ContractsGrantsInvoiceDocument> invoiceDocuments = new ArrayList<ContractsGrantsInvoiceDocument>();
        try {
            for (PersistableBusinessObject obj : SpringContext.getBean(LookupResultsService.class).retrieveSelectedResultBOs(lookupResultsSequenceNumber, ContractsGrantsInvoiceDocument.class, personId)) {
                invoiceDocuments.add((ContractsGrantsInvoiceDocument) obj);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return invoiceDocuments;
    }

    /**
     * Gets the ReferralToCollections Lookup Result objects.
     *
     * @param lookupResultsSequenceNumber The sequence number of result.
     * @param personId The id of logged in person.
     * @return Returns the collection of awards.
     */
    public static Collection<ReferralToCollectionsLookupResult> getReferralToCollectionsResultsFromLookupResultsSequenceNumber(String lookupResultsSequenceNumber, String personId) {
        return ReferralToCollectionsDocumentUtil.getPopulatedReferralToCollectionsLookupResults(getCGInvoiceDocumentsFromLookupResultsSequenceNumber(lookupResultsSequenceNumber, personId));
    }

    /**
     * Sets the referraltoCollection document detail object collection in form.
     *
     * @param rcForm The ReferralToCollectionsDocumentForm object in which collection to set.
     * @param lookupResultsSequenceNumber The sequence number of result.
     * @param personId The id of logged in person.
     */
    public static void setReferralToCollectionsDetailsFromLookupResultsSequenceNumber(ReferralToCollectionsDocument rcDoc, String lookupResultsSequenceNumber, String personId) {
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
            rcDoc.setReferralToCollectionsDetails(ReferralToCollectionsDocumentUtil.getPopulatedReferralToCollectionsDetails(rcDoc, invoices));
        }
    }
}

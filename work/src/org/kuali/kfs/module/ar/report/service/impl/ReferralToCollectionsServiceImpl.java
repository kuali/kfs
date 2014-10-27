/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.report.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsLookupResult;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.report.service.ReferralToCollectionsService;
import org.kuali.rice.krad.util.ObjectUtils;

public class ReferralToCollectionsServiceImpl implements ReferralToCollectionsService {

    @Override
    public Collection<ReferralToCollectionsLookupResult> getPopulatedReferralToCollectionsLookupResults(Collection<ContractsGrantsInvoiceDocument> invoices) {
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
     * This helper method returns a map of a list of awards by agency
     *
     * @param invoices The list of invoices for which filtering to be done by award.
     * @return Returns the map of invoices based on key of proposal number.
     */
    protected Map<Long, List<ContractsGrantsInvoiceDocument>> getInvoicesByAward(Collection<ContractsGrantsInvoiceDocument> invoices) {
        // use a map to sort awards by agency
        Map<Long, List<ContractsGrantsInvoiceDocument>> invoicesByAward = new HashMap<Long, List<ContractsGrantsInvoiceDocument>>();
        for (ContractsGrantsInvoiceDocument invoice : invoices) {
            Long proposalNumber = invoice.getInvoiceGeneralDetail().getProposalNumber();
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
}
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsLookupResult;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.report.service.ReferralToCollectionsService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Default implementation of the ReferralToCollectionsService
 */
public class ReferralToCollectionsServiceImpl implements ReferralToCollectionsService {
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;

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

    public ContractsGrantsInvoiceDocumentService getContractsGrantsInvoiceDocumentService() {
        return contractsGrantsInvoiceDocumentService;
    }

    public void setContractsGrantsInvoiceDocumentService(ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService) {
        this.contractsGrantsInvoiceDocumentService = contractsGrantsInvoiceDocumentService;
    }
}
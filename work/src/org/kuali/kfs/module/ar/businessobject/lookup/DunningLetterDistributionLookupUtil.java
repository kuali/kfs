/*
 * Copyright 2008 The Kuali Foundation
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
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.businessobject.DunningLetterDistributionLookupResult;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.LookupResultsService;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Utility class for Lookup for Dunning Letter Distribution process
 */
public class DunningLetterDistributionLookupUtil {

    /**
     * This helper method returns a list of award lookup results based on the Dunning Letter Distribution lookup
     *
     * @param award
     * @return
     */
    public static Collection<DunningLetterDistributionLookupResult> getPopulatedDunningLetterDistributionLookupResults(Collection<ContractsGrantsInvoiceDocument> invoices) {
        Collection<DunningLetterDistributionLookupResult> populatedDunningLetterDistributionLookupResults = new ArrayList<DunningLetterDistributionLookupResult>();

        if (CollectionUtils.isEmpty(invoices)) {
            return populatedDunningLetterDistributionLookupResults;
        }

        Iterator iter = getInvoicesByAward(invoices).entrySet().iterator();
        DunningLetterDistributionLookupResult dunningLetterDistributionLookupResult = null;
        while (iter.hasNext()) {

            Map.Entry entry = (Map.Entry) iter.next();
            List<ContractsGrantsInvoiceDocument> list = (List<ContractsGrantsInvoiceDocument>) entry.getValue();

            if (CollectionUtils.isNotEmpty(list)){
                // Get data from first award for agency data
                ContractsGrantsInvoiceDocument document = list.get(0);
                ContractsAndGrantsBillingAward award = document.getAward();
                if (ObjectUtils.isNotNull(award) && !award.isStopWorkIndicator()) {
                    dunningLetterDistributionLookupResult = new DunningLetterDistributionLookupResult();
                    dunningLetterDistributionLookupResult.setProposalNumber(award.getProposalNumber());
                    dunningLetterDistributionLookupResult.setInvoiceDocumentNumber(document.getDocumentNumber());
                    dunningLetterDistributionLookupResult.setAgencyNumber(award.getAgencyNumber());
                    dunningLetterDistributionLookupResult.setCustomerNumber(document.getAccountsReceivableDocumentHeader().getCustomerNumber());
                    dunningLetterDistributionLookupResult.setAwardTotal(award.getAwardTotalAmount());
                    dunningLetterDistributionLookupResult.setCampaignID(award.getDunningCampaign());
                    if (CollectionUtils.isNotEmpty(document.getAccountDetails())) {
                        dunningLetterDistributionLookupResult.setAccountNumber(document.getAccountDetails().get(0).getAccountNumber());
                    }
                    dunningLetterDistributionLookupResult.setInvoices(list);

                    populatedDunningLetterDistributionLookupResults.add(dunningLetterDistributionLookupResult);
                }
            }
        }

        return populatedDunningLetterDistributionLookupResults;
    }


    /**
     * This helper method returns a map of a list of awards by agency
     *
     * @param awards
     * @return
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

    public static Collection<DunningLetterDistributionLookupResult> getDunningLetterDistributionLookupResultsFromLookupResultsSequenceNumber(String lookupResultsSequenceNumber, String personId) {
        return DunningLetterDistributionLookupUtil.getPopulatedDunningLetterDistributionLookupResults(getCGInvoiceDocumentsFromLookupResultsSequenceNumber(lookupResultsSequenceNumber, personId));
    }

}

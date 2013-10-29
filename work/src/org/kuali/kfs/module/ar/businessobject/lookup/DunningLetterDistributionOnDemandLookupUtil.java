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
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.DunningLetterDistributionOnDemandLookupResult;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.kns.lookup.LookupResultsService;
import java.util.ArrayList;

/**
 * Utility class for OnDemand Lookup for Dunning Letter Distribution process
 */
public class DunningLetterDistributionOnDemandLookupUtil {

    /**
     * This helper method returns a list of award lookup results based on the Dunning Letter Distribution on demand lookup
     *
     * @param award
     * @return
     */
    public static Collection<DunningLetterDistributionOnDemandLookupResult> getPopulatedDunningLetterDistributionOnDemandLookupResults(Collection<ContractsGrantsInvoiceDocument> invoices) {
        Collection<DunningLetterDistributionOnDemandLookupResult> populatedDunningLetterDistributionOnDemandLookupResults = new ArrayList<DunningLetterDistributionOnDemandLookupResult>();

        if (CollectionUtils.isEmpty(invoices)) {
            return populatedDunningLetterDistributionOnDemandLookupResults;
        }

        Iterator iter = getInvoicesByAward(invoices).entrySet().iterator();
        DunningLetterDistributionOnDemandLookupResult dunningLetterDistributionOnDemandLookupResult = null;
        while (iter.hasNext()) {

            Map.Entry entry = (Map.Entry) iter.next();
            List<ContractsGrantsInvoiceDocument> list = (List<ContractsGrantsInvoiceDocument>) entry.getValue();

            // Get data from first award for agency data
            ContractsAndGrantsBillingAward award = list.get(0).getAward();
            if (!award.isStopWorkIndicator()) {
                dunningLetterDistributionOnDemandLookupResult = new DunningLetterDistributionOnDemandLookupResult();
                dunningLetterDistributionOnDemandLookupResult.setProposalNumber(award.getProposalNumber());
                dunningLetterDistributionOnDemandLookupResult.setInvoiceDocumentNumber(list.get(0).getDocumentNumber());
                dunningLetterDistributionOnDemandLookupResult.setAgencyNumber(award.getAgencyNumber());
                dunningLetterDistributionOnDemandLookupResult.setCustomerNumber(list.get(0).getAccountsReceivableDocumentHeader().getCustomerNumber());
                dunningLetterDistributionOnDemandLookupResult.setAwardTotal(award.getAwardTotalAmount());
                dunningLetterDistributionOnDemandLookupResult.setCampaignID(award.getDunningCampaign());
                dunningLetterDistributionOnDemandLookupResult.setAccountNumber(list.get(0).getAccountDetails().get(0).getAccountNumber());
                dunningLetterDistributionOnDemandLookupResult.setInvoices(list);

                populatedDunningLetterDistributionOnDemandLookupResults.add(dunningLetterDistributionOnDemandLookupResult);
            }
        }

        return populatedDunningLetterDistributionOnDemandLookupResults;
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

    public static Collection<DunningLetterDistributionOnDemandLookupResult> getDunningLetterDistributionOnDemandLookupResultsFromLookupResultsSequenceNumber(String lookupResultsSequenceNumber, String personId) {
        return DunningLetterDistributionOnDemandLookupUtil.getPopulatedDunningLetterDistributionOnDemandLookupResults(getCGInvoiceDocumentsFromLookupResultsSequenceNumber(lookupResultsSequenceNumber, personId));
    }

}

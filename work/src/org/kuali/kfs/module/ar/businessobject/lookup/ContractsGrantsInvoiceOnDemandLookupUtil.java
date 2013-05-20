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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleRetrieveService;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceOnDemandLookupResult;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * Utility class for OnDemand Lookup for Contracts and Grants Invoices.
 */
public class ContractsGrantsInvoiceOnDemandLookupUtil {

    /**
     * This helper method returns a list of award lookup results based on the contracts greants on demand lookup
     * 
     * @param award
     * @return
     */
    public static Collection<ContractsGrantsInvoiceOnDemandLookupResult> getPopulatedContractsGrantsInvoiceOnDemandLookupResults(Collection<ContractsAndGrantsCGBAward> awards) {
        Collection<ContractsGrantsInvoiceOnDemandLookupResult> populatedContractsGrantsInvoiceOnDemandLookupResults = new ArrayList<ContractsGrantsInvoiceOnDemandLookupResult>();

        if (awards.size() == 0)
            return populatedContractsGrantsInvoiceOnDemandLookupResults;

        Iterator iter = getAwardByAgency(awards).entrySet().iterator();
        ContractsGrantsInvoiceOnDemandLookupResult contractsGrantsInvoiceOnDemandLookupResult = null;
        while (iter.hasNext()) {

            Map.Entry entry = (Map.Entry) iter.next();
            List<ContractsAndGrantsCGBAward> list = (List<ContractsAndGrantsCGBAward>) entry.getValue();

            // Get data from first award for agency data
            ContractsAndGrantsCGBAgency agency = ((ContractsAndGrantsCGBAward) list.get(0)).getAgency();
            contractsGrantsInvoiceOnDemandLookupResult = new ContractsGrantsInvoiceOnDemandLookupResult();
            contractsGrantsInvoiceOnDemandLookupResult.setAgencyNumber(agency.getAgencyNumber());
            contractsGrantsInvoiceOnDemandLookupResult.setAgencyReportingName(agency.getReportingName());
            contractsGrantsInvoiceOnDemandLookupResult.setAgencyFullName(agency.getFullName());
            contractsGrantsInvoiceOnDemandLookupResult.setCustomerNumber(agency.getCustomerNumber());
            contractsGrantsInvoiceOnDemandLookupResult.setAwards(list);

            populatedContractsGrantsInvoiceOnDemandLookupResults.add(contractsGrantsInvoiceOnDemandLookupResult);
        }

        return populatedContractsGrantsInvoiceOnDemandLookupResults;
    }


    /**
     * This helper method returns a map of a list of awards by agency
     * 
     * @param awards
     * @return
     */
    public static Map<String, List<ContractsAndGrantsCGBAward>> getAwardByAgency(Collection<ContractsAndGrantsCGBAward> awards) {
        // use a map to sort awards by agency
        Map<String, List<ContractsAndGrantsCGBAward>> awardsByAgency = new HashMap<String, List<ContractsAndGrantsCGBAward>>();
        for (ContractsAndGrantsCGBAward award : awards) {// To display awards only if their preferred Billing frequency is not LOC
                                                         // Billing
            if (StringUtils.isNotEmpty(award.getPreferredBillingFrequency()) && !award.getPreferredBillingFrequency().equalsIgnoreCase(ArPropertyConstants.LOC_BILLING_SCHEDULE_CODE)) {
                String agencyNumber = award.getAgencyNumber();
                if (awardsByAgency.containsKey(agencyNumber)) {
                    ((List<ContractsAndGrantsCGBAward>) awardsByAgency.get(agencyNumber)).add(award);
                }
                else {
                    List<ContractsAndGrantsCGBAward> awardsByAgencyNumber = new ArrayList<ContractsAndGrantsCGBAward>();
                    awardsByAgencyNumber.add(award);
                    awardsByAgency.put(agencyNumber, awardsByAgencyNumber);
                }
            }
        }

        return awardsByAgency;
    }

    public static Collection<ContractsAndGrantsCGBAward> getAwardsFromLookupResultsSequenceNumber(String lookupResultsSequenceNumber, String personId) {
        ContractsAndGrantsModuleRetrieveService moduleRetrieveService = SpringContext.getBean(ContractsAndGrantsModuleRetrieveService.class);

        Collection<ContractsAndGrantsCGBAward> awards = new ArrayList<ContractsAndGrantsCGBAward>();
        awards = moduleRetrieveService.getAwardsFromLookupResultsSequenceNumber(lookupResultsSequenceNumber, personId);

        return awards;
    }

    public static Collection<ContractsGrantsInvoiceOnDemandLookupResult> getContractsGrantsInvoiceOnDemandResultsFromLookupResultsSequenceNumber(String lookupResultsSequenceNumber, String personId) {
        return ContractsGrantsInvoiceOnDemandLookupUtil.getPopulatedContractsGrantsInvoiceOnDemandLookupResults(getAwardsFromLookupResultsSequenceNumber(lookupResultsSequenceNumber, personId));
    }

}

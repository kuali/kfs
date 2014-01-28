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
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceOnDemandLookupResult;
import org.kuali.kfs.module.ar.web.ui.ContractsGrantsInvoiceOnDemandResultRow;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.SegmentedLookupResultsService;
import org.kuali.rice.kns.lookup.LookupResultsService;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Utility class for OnDemand Lookup for Contracts and Grants Invoices.
 */
/**
 *
 */
public class ContractsGrantsInvoiceOnDemandLookupUtil {

    /**
     * This helper method returns a list of award lookup results based on the contracts grants on demand lookup
     *
     * @param award
     * @return
     */
    public static Collection<ContractsGrantsInvoiceOnDemandLookupResult> getPopulatedContractsGrantsInvoiceOnDemandLookupResults(Collection<ContractsAndGrantsBillingAward> awards) {
        Collection<ContractsGrantsInvoiceOnDemandLookupResult> populatedContractsGrantsInvoiceOnDemandLookupResults = new ArrayList<ContractsGrantsInvoiceOnDemandLookupResult>();

        if (awards.size() == 0) {
            return populatedContractsGrantsInvoiceOnDemandLookupResults;
        }

        Iterator iter = getAwardByAgency(awards).entrySet().iterator();
        ContractsGrantsInvoiceOnDemandLookupResult contractsGrantsInvoiceOnDemandLookupResult = null;
        while (iter.hasNext()) {

            Map.Entry entry = (Map.Entry) iter.next();
            List<ContractsAndGrantsBillingAward> list = (List<ContractsAndGrantsBillingAward>) entry.getValue();

            // Get data from first award for agency data
            if (CollectionUtils.isNotEmpty(list)){
                ContractsAndGrantsBillingAgency agency = list.get(0).getAgency();
                contractsGrantsInvoiceOnDemandLookupResult = new ContractsGrantsInvoiceOnDemandLookupResult();

                if (ObjectUtils.isNotNull(agency)){
                    contractsGrantsInvoiceOnDemandLookupResult.setAgencyNumber(agency.getAgencyNumber());
                    contractsGrantsInvoiceOnDemandLookupResult.setAgencyReportingName(agency.getReportingName());
                    contractsGrantsInvoiceOnDemandLookupResult.setAgencyFullName(agency.getFullName());
                    contractsGrantsInvoiceOnDemandLookupResult.setCustomerNumber(agency.getCustomerNumber());
                }

                contractsGrantsInvoiceOnDemandLookupResult.setAwards(list);
                populatedContractsGrantsInvoiceOnDemandLookupResults.add(contractsGrantsInvoiceOnDemandLookupResult);
            }
        }

        return populatedContractsGrantsInvoiceOnDemandLookupResults;
    }


    /**
     * This helper method returns a map of a list of awards by agency
     *
     * @param awards
     * @return
     */
    public static Map<String, List<ContractsAndGrantsBillingAward>> getAwardByAgency(Collection<ContractsAndGrantsBillingAward> awards) {
        // use a map to sort awards by agency
        Map<String, List<ContractsAndGrantsBillingAward>> awardsByAgency = new HashMap<String, List<ContractsAndGrantsBillingAward>>();
        for (ContractsAndGrantsBillingAward award : awards) {// To display awards only if their preferred Billing frequency is not LOC
                                                         // Billing
            if (StringUtils.isNotEmpty(award.getPreferredBillingFrequency()) && !award.getPreferredBillingFrequency().equalsIgnoreCase(ArPropertyConstants.LOC_BILLING_SCHEDULE_CODE)) {
                String agencyNumber = award.getAgencyNumber();
                if (awardsByAgency.containsKey(agencyNumber)) {
                    awardsByAgency.get(agencyNumber).add(award);
                }
                else {
                    List<ContractsAndGrantsBillingAward> awardsByAgencyNumber = new ArrayList<ContractsAndGrantsBillingAward>();
                    awardsByAgencyNumber.add(award);
                    awardsByAgency.put(agencyNumber, awardsByAgencyNumber);
                }
            }
        }

        return awardsByAgency;
    }

    /**
     * Get the Awards based on what the user selected in the lookup results.
     *
     * @param lookupResultsSequenceNumber sequence number used to retrieve the lookup results
     * @param personId person who performed the lookup
     * @return Collection of ContractsAndGrantsBillingAwards
     * @throws Exception if there was a problem getting the selected proposal numbers from the lookup results
     */
    public static Collection<ContractsAndGrantsBillingAward> getAwardsFromLookupResultsSequenceNumber(String lookupResultsSequenceNumber, String personId) throws Exception {
        KualiModuleService kualiModuleService = SpringContext.getBean(KualiModuleService.class);
        Collection<ContractsAndGrantsBillingAward> awards = new ArrayList<ContractsAndGrantsBillingAward>();

        List<String> selectedProposalNumbers = getSelectedProposalNumbersFromLookupResultsSequenceNumber(lookupResultsSequenceNumber, personId);
        ContractsAndGrantsBillingAward award = null;

        for (String selectedProposalNumber: selectedProposalNumbers) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(ArPropertyConstants.TicklersReportFields.PROPOSAL_NUMBER, selectedProposalNumber);
            award = kualiModuleService.getResponsibleModuleService(ContractsAndGrantsBillingAward.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAward.class, map);
            if (ObjectUtils.isNotNull(award)) {
                awards.add(award);
            }
        }

        return awards;
    }

    public static Collection<ContractsGrantsInvoiceOnDemandLookupResult> getContractsGrantsInvoiceOnDemandResultsFromLookupResultsSequenceNumber(String lookupResultsSequenceNumber, String personId) throws Exception {
        return ContractsGrantsInvoiceOnDemandLookupUtil.getPopulatedContractsGrantsInvoiceOnDemandLookupResults(getAwardsFromLookupResultsSequenceNumber(lookupResultsSequenceNumber, personId));
    }

    /**
     * Get the selected proposal numbers to be used as keys to retrieve the awards by retrieving the selected object ids
     * and then matching them against the results from the lookup.
     *
     * @param lookupResultsSequenceNumber sequence number used to retrieve the lookup results and selected object ids
     * @param personId person who performed the lookup
     * @return List of proposalNumber Strings that the user selected
     * @throws Exception if there was a problem getting the selected object ids or the lookup results
     */
    protected static List getSelectedProposalNumbersFromLookupResultsSequenceNumber(String lookupResultsSequenceNumber, String personId) throws Exception {
        List<String> selectedProposalNumbers = new ArrayList<String>();
        Set<String> selectedIds = SpringContext.getBean(SegmentedLookupResultsService.class).retrieveSetOfSelectedObjectIds(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getPerson().getPrincipalId());
        if (ObjectUtils.isNotNull(selectedIds) && CollectionUtils.isNotEmpty(selectedIds)) {
            List<ResultRow> results = SpringContext.getBean(LookupResultsService.class).retrieveResultsTable(lookupResultsSequenceNumber, personId);
            for (ResultRow result:results) {
                List<Column> columns = result.getColumns();
                if (result instanceof ContractsGrantsInvoiceOnDemandResultRow) {
                    for (ResultRow subResultRow : ((ContractsGrantsInvoiceOnDemandResultRow) result).getSubResultRows()) {
                        String objId = subResultRow.getObjectId();
                        if (selectedIds.contains(objId)) {
                            // This is somewhat brittle - it depends on the fact that the Proposal Number
                            // is the first column. If the format of the lookup results changes so that
                            // Proposal Number isn't the first column this will no longer work and will
                            // need to be changed.
                            selectedProposalNumbers.add(subResultRow.getColumns().get(0).getPropertyValue());
                        }
                    }
                }
            }
        }

        return selectedProposalNumbers;
    }

}

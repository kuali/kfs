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
package org.kuali.kfs.module.ar.service;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ar.businessobject.Bill;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.businessobject.Milestone;
import org.kuali.rice.core.api.util.type.KualiDecimal;


/**
 * Defines utility methods used by CGB.
 */
public interface ContractsGrantsBillingUtilityService {

    /**
     * Returns a proper String Value. Also returns proper value for currency (USD)
     *
     * @param string
     * @return
     */
    public String formatForCurrency(KualiDecimal amount);

    /**
     * Builds and resturns a full address string from a CustomerAddress
     *
     * @param address
     * @return
     */
    public String buildFullAddress(CustomerAddress address);

    /**
     * Places a value into a Map, but if that value is null, it places an empty String into the Map instead
     * @param map the Map to place the key into
     * @param key the key
     * @param value the value
     */
    public void putValueOrEmptyString(Map<String,String> map, String key, String value);

    /**
     * Retrieves all active bills associated with the given proposal number
     * @param proposalNumber the proposal number to lookup active bills for
     * @return a List of active bills, or an empty List of naught could be found
     */
    public List<Bill> getActiveBillsForProposalNumber(Long proposalNumber);

    /**
     * Retrieves all active milestones associated with the given proposal number
     * @param proposalNumber the proposal number to retrieve milestones for
     * @return a List of active milestones, or an empty List if BusinessObjectService couldn't turn any up
     */
    public List<Milestone> getActiveMilestonesForProposalNumber(Long proposalNumber);

}

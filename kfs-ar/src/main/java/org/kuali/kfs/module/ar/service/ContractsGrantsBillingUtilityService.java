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

/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.dataaccess;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;

public interface HoldingTaxLotDao {

    /**
     * Gets all tax lots with positive units that meet the given criteria on kemid, security id, registration code and
     * income/principal indicator
     * 
     * @param kemid
     * @param securityId
     * @param registrationCode
     * @param incomePrincipalIndicator
     * @return a collection of tax lots that meet the criteria
     */
    public Collection<HoldingTaxLot> getAllTaxLotsWithPositiveUnits(String kemid, String securityId, String registrationCode, String incomePrincipalIndicator);

    /**
     * Gets all tax lots with positive cost that meet the given criteria on kemid, security id, registration code and
     * income/principal indicator
     * 
     * @param kemid
     * @param securityId
     * @param registrationCode
     * @param incomePrincipalIndicator
     * @return a collection of tax lots that meet the criteria
     */
    public Collection<HoldingTaxLot> getAllTaxLotsWithPositiveCost(String kemid, String securityId, String registrationCode, String incomePrincipalIndicator);

    /**
     * Gets all the tax lots for the given security that have units greater than zero.
     * 
     * @param securityId
     * @return all tax lots that meet the criteria
     */
    public List<HoldingTaxLot> getTaxLotsPerSecurityIDWithUnitsGreaterThanZero(String securityId);

    /**
     * Gets all the tax lots for the given security that have accrued income greater than zero.
     * 
     * @param securityId
     * @return
     */
    public List<HoldingTaxLot> getTaxLotsWithAccruedIncomeGreaterThanZeroPerSecurity(String securityId);
}

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

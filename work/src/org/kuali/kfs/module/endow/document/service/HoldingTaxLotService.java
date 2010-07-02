/*
 * Copyright 2010 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.endow.document.service;

import java.util.List;

import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;

public interface HoldingTaxLotService {

    /**
     * Gets a holding tax lot based on primary keys: kemid, security id, registration code, lot number and IP indicator.
     * 
     * @param kemid
     * @param securityId
     * @param registrationCode
     * @param lotNumber
     * @param ipIndicator
     * @return the corresponding tax lot
     */
    public HoldingTaxLot getByPrimaryKey(String kemid, String securityId, String registrationCode, int lotNumber, String ipIndicator);

    /**
     * Gets the holding tax lot based on the following criteria: kemid, security id, registration code, and IP indicator.
     * 
     * @param kemid
     * @param securityId
     * @param registrationCode
     * @param ipIndicator
     * @return a list of tax lots that meet the criteria
     */
    public List<HoldingTaxLot> getAllTaxLots(String kemid, String securityId, String registrationCode, String ipIndicator);

    /**
     * Gets all tax lots with positive units based on the following criteria: kemid, security id, registration code, and IP
     * indicator.
     * 
     * @param kemid
     * @param securityId
     * @param registrationCode
     * @param ipIndicator
     * @return a list of tax lots that meet the criteria
     */
    public List<HoldingTaxLot> getAllTaxLotsWithPositiveUnits(String kemid, String securityId, String registrationCode, String ipIndicator);

    /**
     * Gets the holding tax lot based on the following criteria: kemid, security id, registration code, and IP indicator and orders
     * them ascending or descending based on the acquired date.
     * 
     * @param kemid
     * @param securityId
     * @param registrationCode
     * @param ipIndicator returned in the descending order
     * @return a list of tax lots that meet the criteria and in the right order
     */
    public List<HoldingTaxLot> getAllTaxLotsOrderByAcquiredDate(String kemid, String securityId, String registrationCode, String ipIndicator, boolean sortAscending);

}

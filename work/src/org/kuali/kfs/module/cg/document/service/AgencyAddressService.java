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
package org.kuali.kfs.module.cg.document.service;

import org.kuali.kfs.module.cg.businessobject.AgencyAddress;

/**
 * This class defines the services required for Agency Address.
 */
public interface AgencyAddressService {

    /**
     * This method returns an agency address by primary key
     * 
     * @param agencyNumber
     * @param agencyAddressIdentifier
     * @return
     */
    public AgencyAddress getByPrimaryKey(String agencyNumber, Integer agencyAddressIdentifier);

    /**
     * This method returns true if agency address exists
     * 
     * @param agencyNumber
     * @param agencyAddressIdentifier
     * @return
     */
    public boolean agencyAddressExists(String agencyNumber, Integer agencyAddressIdentifier);

    /**
     * This method gets the next address identifier
     * 
     * @return
     */
    public Integer getNextAgencyAddressIdentifier();

    /**
     * This method returns the AgencyAddress specified as the primary address for an Agency.
     * 
     * @param agencyNumber
     * @return
     */

    public AgencyAddress getPrimaryAddress(String agencyNumber);

    /**
     * This method returns true if agency address is active
     * 
     * @param agencyNumber
     * @param agencyAddressIdentifier
     * @return
     */
    public boolean agencyAddressActive(String agencyNumber, Integer agencyAddressIdentifier);
}

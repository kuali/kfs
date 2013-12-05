/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.fixture;

import org.kuali.kfs.module.cg.businessobject.AgencyAddress;

/**
 * Fixture class for Agency Address
 */
public enum AgencyAddressFixture {

    CG_AGENCY_ADD1("11505", new Long(26), "P"),
    CG_AGENCY_ADD2("11505", new Long(26), "A"),
    CG_AGENCY_ADD3("12851", new Long(26), "P");

    private String agencyNumber;
    private Long agencyAddressIdentifier;
    private String customerAddressTypeCode;

    private AgencyAddressFixture(String agencyNumber, Long agencyAddressIdentifier, String customerAddressTypeCode) {

        this.agencyNumber = agencyNumber;
        this.agencyAddressIdentifier = agencyAddressIdentifier;
        this.customerAddressTypeCode = customerAddressTypeCode;

    }

    public AgencyAddress createAgencyAddress() {
        AgencyAddress agencyAddress = new AgencyAddress();
        agencyAddress.setAgencyNumber(this.agencyNumber);
        agencyAddress.setAgencyAddressIdentifier(this.agencyAddressIdentifier);
        agencyAddress.setCustomerAddressTypeCode(this.customerAddressTypeCode);
        return agencyAddress;
    }
}

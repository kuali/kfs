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

import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.businessobject.Agency;

/**
 * Fixture class for Agency
 */

public enum AgencyFixture {

    CG_AGENCY1("11505", "USAIR FORCE", "U.S. AIR FORCE", "P", "C");

    private String agencyNumber;
    private String reportingName;
    private String fullName;
    private String agencyTypeCode;
    private String customerCreated;
    
    AgencyFixture(String agencyNumber, String reportingName, String fullName, String agencyTypeCode, String customerCreated) {

        this.agencyNumber = agencyNumber;
        this.reportingName = reportingName;
        this.fullName = fullName;
        this.agencyTypeCode = agencyTypeCode;
        this.customerCreated = customerCreated;
    }

    public Agency createAgency() {
        Agency agency = new Agency();
        agency.setAgencyNumber(this.agencyNumber);
        agency.setReportingName(this.reportingName);
        agency.setFullName(this.fullName);
        agency.setAgencyTypeCode(this.agencyTypeCode);
        agency.setCustomerCreated(this.customerCreated);
        return agency;
    }
}

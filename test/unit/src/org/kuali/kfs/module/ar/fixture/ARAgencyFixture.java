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
package org.kuali.kfs.module.ar.fixture;

import org.kuali.kfs.integration.cg.businessobject.Agency;

/**
 * Fixture class for CG Agency
 */


public enum ARAgencyFixture {

    CG_AGENCY1("11505", "US AIR FORCE", "U.S. AIR FORCE");

    private String agencyNumber;
    private String reportingName;
    private String fullName;

    private ARAgencyFixture(String agencyNumber, String reportingName, String fullName) {

        this.agencyNumber = agencyNumber;
        this.reportingName = reportingName;
        this.fullName = fullName;

    }

    public Agency createAgency() {
        Agency agency = new Agency();
        agency.setAgencyNumber(this.agencyNumber);
        agency.setReportingName(this.reportingName);
        agency.setFullName(this.fullName);

        return agency;
    }
}

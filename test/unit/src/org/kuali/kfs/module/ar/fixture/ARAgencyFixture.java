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
package org.kuali.kfs.module.ar.fixture;

import org.kuali.kfs.module.cg.businessobject.Agency;

/**
 * Fixture class for CG Agency
 */


public enum ARAgencyFixture {

    CG_AGENCY1("11505", "US AIR FORCE", "U.S. AIR FORCE", "3MC17500");

    private String agencyNumber;
    private String reportingName;
    private String fullName;
    private String customerNumber;

    private ARAgencyFixture(String agencyNumber, String reportingName, String fullName, String customerNumber) {

        this.agencyNumber = agencyNumber;
        this.reportingName = reportingName;
        this.fullName = fullName;
        this.customerNumber = customerNumber;

    }

    public Agency createAgency() {
        Agency agency = new Agency();
        agency.setAgencyNumber(this.agencyNumber);
        agency.setReportingName(this.reportingName);
        agency.setFullName(this.fullName);
        agency.setCustomerNumber(this.customerNumber);

        return agency;
    }
}

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
package org.kuali.kfs.vnd.fixture;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.fixture.VendorTestConstants.FaxNumbers;

public enum VendorRuleFaxNumberFixture {

    TWO_DEFAULT_FORMATS(FaxNumbers.defaultFormat, FaxNumbers.defaultFormat), TWO_SHORT_FAXES(FaxNumbers.shortFax, FaxNumbers.shortFax), ONE_DEFAULT_ONE_SHORT_FAX(FaxNumbers.defaultFormat, FaxNumbers.shortFax), ;

    public final String fax1;
    public final String fax2;

    VendorRuleFaxNumberFixture(String fax1, String fax2) {
        this.fax1 = fax1;
        this.fax2 = fax2;
    }

    /**
     * This method does the setup for the tests which examine the implementation of the requirement that the fax numbers in the
     * VendorAddress collection must be of a valid format
     * 
     * @param fax1 A fax number in String form
     * @param fax2 Another fax number in String form
     * @return A List<VendorAddress>, appropriately populated with fax numbers.
     */
    public List<VendorAddress> getAddresses() {
        List<VendorAddress> addrList = new ArrayList();
        VendorAddress addr1 = new VendorAddress();
        VendorAddress addr2 = new VendorAddress();
        addr1.setVendorFaxNumber(fax1);
        addr2.setVendorFaxNumber(fax2);
        addrList.add(addr1);
        addrList.add(addr2);
        return addrList;
    }

}
